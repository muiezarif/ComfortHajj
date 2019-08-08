package com.muiezarif.muiez.comforthajj.usersignedin;



import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.muiezarif.muiez.comforthajj.AudioPlayer;
import com.muiezarif.muiez.comforthajj.CallAdapter;
import com.muiezarif.muiez.comforthajj.R;
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
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;


import java.util.ArrayList;

import es.dmoral.toasty.Toasty;



/**
 * A simple {@link Fragment} subclass.
 */
public class CallFriendsListFragment extends Fragment {
    AdView adView;
    ListView connectedUsersList;
    DatabaseReference connectedUsersReference;
    DatabaseReference userInfoRefernce;
    DatabaseReference presenceReference;
    ArrayList<String> name=new ArrayList<>();
    ArrayList<String> status=new ArrayList<>();
    ArrayList<String> imgs=new ArrayList<>();
    ArrayList<String> key=new ArrayList<>();
    CallAdapter adapter;
    FirebaseUser user;
    AudioPlayer audioPlayer;
    InterstitialAd interstitialAd;

    public CallFriendsListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_call_friends_list, container, false);
        adView=v.findViewById(R.id.adView);
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
        audioPlayer=new AudioPlayer(getContext());
        connectedUsersList=v.findViewById(R.id.show_friend_list_call);
        user= FirebaseAuth.getInstance().getCurrentUser();
        presenceReference= FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        connectedUsersReference= FirebaseDatabase.getInstance().getReference().child("connected_users").child(user.getUid());
        userInfoRefernce=FirebaseDatabase.getInstance().getReference().child("users");
        adapter=new CallAdapter(getContext(),R.layout.call_friend_listview,name,status,imgs,key);
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

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    }




