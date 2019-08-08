package com.muiezarif.muiez.comforthajj.usersignedin;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.muiezarif.muiez.comforthajj.AllUsersActivity;
import com.muiezarif.muiez.comforthajj.MyAdapter;
import com.muiezarif.muiez.comforthajj.R;
import com.firebase.client.Query;
import com.firebase.client.ServerValue;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectedUsersFragment extends Fragment {
    ListView connectedUsersList;
    DatabaseReference connectedUsersReference;
    DatabaseReference userInfoRefernce;
    DatabaseReference presenceReference;
    ArrayList<String> name=new ArrayList<>();
    ArrayList<String> status=new ArrayList<>();
    ArrayList<String> imgs=new ArrayList<>();
    ArrayList<String> key=new ArrayList<>();
    MyAdapter adapter;
    FirebaseUser user;
    InterstitialAd interstitialAd;
    AdView adView;

    public ConnectedUsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_connected_users, container, false);
        adView=v.findViewById(R.id.adViewconnectedusers);
        AdRequest request=new AdRequest.Builder().build();
        adView.loadAd(request);
        interstitialAd=new InterstitialAd(getContext());
        interstitialAd.setAdUnitId("ca-app-pub-7701765309854052/7867968342");
        interstitialAd.loadAd(new AdRequest.Builder().build());
        try {
            interstitialAd.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        connectedUsersList=v.findViewById(R.id.connected_users_list);
        user=FirebaseAuth.getInstance().getCurrentUser();
        presenceReference=FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        connectedUsersReference= FirebaseDatabase.getInstance().getReference().child("connected_users").child(user.getUid());
        userInfoRefernce=FirebaseDatabase.getInstance().getReference().child("users");
        adapter=new MyAdapter(getContext(),R.layout.single_user_layout,name,status,imgs,key);
        connectedUsersList.setAdapter(adapter);
        connectedUsersReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String userKey=dataSnapshot.getKey();
                final String userConnectedDate=dataSnapshot.child("date").getValue(String.class);
                userInfoRefernce.child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String userKey=dataSnapshot.getKey();
                        String username=dataSnapshot.child("name").getValue(String.class);
                        String userstatus=dataSnapshot.child("profile_status").getValue(String.class);
                        String userimg=dataSnapshot.child("thumb_image").getValue(String.class);
                        name.add(username);
                        status.add(userConnectedDate);
                        imgs.add(userimg);
                        key.add(userKey);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });



            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
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
