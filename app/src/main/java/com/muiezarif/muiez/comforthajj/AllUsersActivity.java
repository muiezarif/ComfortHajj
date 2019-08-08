package com.muiezarif.muiez.comforthajj;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AllUsersActivity extends AppCompatActivity {
    ListView user_list;
    DatabaseReference reference;
    ArrayList<String> name=new ArrayList<>();
    ArrayList<String> status=new ArrayList<>();
    ArrayList<String> imgs=new ArrayList<>();
    ArrayList<String> key=new ArrayList<>();
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        inIt();
        reference= FirebaseDatabase.getInstance().getReference().child("users");
        adapter=new MyAdapter(AllUsersActivity.this,R.layout.single_user_layout,name,status,imgs,key);
        user_list.setAdapter(adapter);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String userKey=dataSnapshot.getKey();
                String username=dataSnapshot.child("name").getValue(String.class);
                String userstatus=dataSnapshot.child("profile_status").getValue(String.class);
                String userimg=dataSnapshot.child("thumb_image").getValue(String.class);
                name.add(username);
                status.add(userstatus);
                imgs.add(userimg);
                key.add(userKey);
                adapter.notifyDataSetChanged();
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

    }


    public void inIt(){
        user_list=findViewById(R.id.user_list);
    }
}
