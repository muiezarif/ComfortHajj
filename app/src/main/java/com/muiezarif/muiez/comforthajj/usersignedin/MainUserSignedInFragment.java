package com.muiezarif.muiez.comforthajj.usersignedin;


import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.muiezarif.muiez.comforthajj.AllUsersActivity;
import com.muiezarif.muiez.comforthajj.MyAdapter;
import com.muiezarif.muiez.comforthajj.R;
import com.muiezarif.muiez.comforthajj.SearchAdapter;
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
public class MainUserSignedInFragment extends Fragment {
    EditText searchUser;
    ImageButton search;
    ListView searchUserList;
    DatabaseReference reference;
    DatabaseReference presenceReference;
    FirebaseUser user;
    ArrayList<String> name=new ArrayList<>();
    ArrayList<String> status=new ArrayList<>();
    ArrayList<String> imgs=new ArrayList<>();
    ArrayList<String> key=new ArrayList<>();
    SearchAdapter adapter;
    TextView hint;
    String username;
    AdView adView;
    InterstitialAd interstitialAd;


    public MainUserSignedInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_main_user_signed_in, container, false);
        adView=v.findViewById(R.id.adViewfindfriends);
        AdRequest request=new AdRequest.Builder().build();
        adView.loadAd(request);
        interstitialAd=new InterstitialAd(getContext());
        interstitialAd.setAdUnitId("ca-app-pub-7701765309854052/7867968342");
        interstitialAd.loadAd(new AdRequest.Builder().build());
        searchUser=v.findViewById(R.id.search_user_text);
        search=v.findViewById(R.id.btn_search_user);
        searchUserList=v.findViewById(R.id.search_user_list);
        reference= FirebaseDatabase.getInstance().getReference().child("users");
        user= FirebaseAuth.getInstance().getCurrentUser();
        presenceReference=FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        adapter=new SearchAdapter(getContext(),R.layout.users_single_layout,name,status,imgs,key);
        searchUserList.setAdapter(adapter);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interstitialAd.isLoaded()){
                    interstitialAd.show();
                }
                try {
                    int i = 0;
                    while (searchUserList.getAdapter().getCount() >= 0) {
                        name.remove(i);
                        imgs.remove(i);
                        status.remove(i);
                        key.remove(i);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


                username = searchUser.getText().toString().replace(" ","");

                if (!username.isEmpty()){
                    reference.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            if (dataSnapshot.hasChild("name")) {
                                if (dataSnapshot.child("name").getValue(String.class).equals(username) || dataSnapshot.child("name").getValue(String.class).toLowerCase().equals(username.toLowerCase()) || dataSnapshot.child("name").getValue(String.class).toUpperCase().equals(username.toUpperCase())) {
                                    String userKey = dataSnapshot.getKey();
                                    String username = dataSnapshot.child("name").getValue(String.class);
                                    String userstatus = dataSnapshot.child("profile_status").getValue(String.class);
                                    String userimg = dataSnapshot.child("thumb_image").getValue(String.class);
                                    name.add(username);
                                    status.add(userstatus);
                                    imgs.add(userimg);
                                    key.add(userKey);
                                    adapter.notifyDataSetChanged();
                                    int size = adapter.getCount();
                                    int pos = adapter.getPosition(username);
                                }
                            }
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
                }else {
                    Toasty.warning(getContext(), "Enter name to search", Toast.LENGTH_SHORT).show();
                }
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
