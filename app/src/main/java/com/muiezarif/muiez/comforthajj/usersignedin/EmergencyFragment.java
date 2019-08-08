package com.muiezarif.muiez.comforthajj.usersignedin;


import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

//import com.muiezarif.muiez.comforthajj.HotelMap;
import com.muiezarif.muiez.comforthajj.R;
import com.firebase.client.ServerValue;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import es.dmoral.toasty.Toasty;

import static org.webrtc.ContextUtils.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 */
public class EmergencyFragment extends Fragment {
    Button emg;
    DatabaseReference reference;
    DatabaseReference userInfoReference;
    FirebaseUser user;
    String number,Lat,Lng;
    DatabaseReference locationReference;
    DatabaseReference friendReference;
    DatabaseReference emgNotifyReference;
    LocationManager locationManager;
    DatabaseReference presenceReference;
    Location location;
    InterstitialAd interstitialAd;
    AdView adView;



    public EmergencyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_emergency, container, false);
        adView=v.findViewById(R.id.adViewemergency);
        AdRequest request=new AdRequest.Builder().build();
        adView.loadAd(request);
        interstitialAd=new InterstitialAd(getContext());
        interstitialAd.setAdUnitId("ca-app-pub-7701765309854052/7867968342");
        interstitialAd.loadAd(new AdRequest.Builder().build());
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("comforthajj", "ComfortHajj", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Comfort Hajj Notifications");
            NotificationManager manager = getActivity().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        emg=v.findViewById(R.id.btnEmg);
        user= FirebaseAuth.getInstance().getCurrentUser();
        presenceReference=FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        emgNotifyReference=FirebaseDatabase.getInstance().getReference().child("emergency_notifications");
        userInfoReference=FirebaseDatabase.getInstance().getReference().child("users");
        friendReference=FirebaseDatabase.getInstance().getReference().child("connected_users").child(user.getUid());
        reference= FirebaseDatabase.getInstance().getReference().child("emergency_contact").child(user.getUid());
        emg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vv) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    Toasty.warning(getContext(), "Please grant permission to send message through this app for emergency purpose or else the emergency message to the emergency contact wont be delivered :)", Toast.LENGTH_LONG).show();

                }
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toasty.warning(getContext(),"Please grant permission to access your location",Toast.LENGTH_SHORT).show();
                }
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("num")){
                            number = dataSnapshot.child("num").getValue().toString();
                        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                        } else {
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                Lat = String.valueOf(location.getLatitude());
                                Lng = String.valueOf(location.getLongitude());
                                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    Toasty.warning(getContext(),"Please grant permission to access your location",Toast.LENGTH_SHORT).show();
                                }else {
                                    Toasty.success(getContext(), "Your Current location is updated on ComfortHajj MAP", Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                        locationReference = FirebaseDatabase.getInstance().getReference().child("locations");
                        locationReference.child(user.getUid()).child("lat").setValue(Lat);
                        locationReference.child(user.getUid()).child("lng").setValue(Lng);
//                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ) {
//                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS}, 2);
//                        }else {
//                            SmsManager smsManager = SmsManager.getDefault();
//                            smsManager.sendTextMessage(number, null, "I NEED HELP URGENT!!! You can get my recent Location on comfortHajj", null, null);
//                        }
                        }else{
                            Toasty.warning(getContext(),"Please enter emergency number in the settings page",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                friendReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> friends=dataSnapshot.getChildren();
                        for (DataSnapshot child:friends){
                           String friendId= child.getKey().toString();
                            emgNotifyReference.child(friendId).push().child("from").setValue(user.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });

                        }
                        Toasty.success(getContext(),"Help is on the way!",Toast.LENGTH_LONG).show();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });
        try {
            interstitialAd.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        if(user!=null) {
//            presenceReference.child("online").setValue("true");
//        }
//    }

    @Override
    public void onResume() {
        super.onResume();
        if(user!=null) {
            presenceReference.child("online").setValue("true");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(user!=null) {
            presenceReference.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        if(user!=null) {
//            presenceReference.child("online").setValue(ServerValue.TIMESTAMP);
//        }
//    }

}
