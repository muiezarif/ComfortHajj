package com.muiezarif.muiez.comforthajj.usersignedin;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.muiezarif.muiez.comforthajj.ChangeImageActivity;
import com.muiezarif.muiez.comforthajj.ChangeStatusActivity;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

import static android.support.v4.provider.FontsContractCompat.FontRequestCallback.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentProfile extends Fragment {
    CircleImageView imageView;
    TextView displayName,status;
    Button changeImage,changeStatus;
    DatabaseReference reference;
    DatabaseReference presenceReference;
    FirebaseUser user;
    private static final int GALLERY_PICK=1;
    StorageReference storageReference;
    AdView adView;
    InterstitialAd interstitialAd;


    public FragmentProfile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_fragment_profile, container, false);
        adView=v.findViewById(R.id.adViewprofile);
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
        imageView=v.findViewById(R.id.profile_image);
        displayName=v.findViewById(R.id.profile_name);
        status=v.findViewById(R.id.profile_status);
        changeImage=v.findViewById(R.id.btn_profile_img_change);
        changeStatus=v.findViewById(R.id.btn_profile_status_change);
        user=FirebaseAuth.getInstance().getCurrentUser();
        presenceReference=FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        reference= FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    String name=dataSnapshot.child("name").getValue().toString();
                    final String image=dataSnapshot.child("image").getValue().toString();
                    String thumb_image=dataSnapshot.child("thumb_image").getValue().toString();
                    String profileStatus=dataSnapshot.child("profile_status").getValue().toString();
                    status.setText(profileStatus);
                    displayName.setText(name);
                    if (!image.equals("default")) {
                        Picasso.get().load(image).placeholder(R.drawable.userprofile)
                                .networkPolicy(NetworkPolicy.OFFLINE).into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(image).placeholder(R.drawable.userprofile).into(imageView);
                            }
                        });
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        changeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), ChangeStatusActivity.class);
                startActivity(intent);
            }
        });
        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ChangeImageActivity.class));
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
