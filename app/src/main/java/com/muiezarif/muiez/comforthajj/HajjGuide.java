package com.muiezarif.muiez.comforthajj;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.client.ServerValue;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HajjGuide extends AppCompatActivity {
    ListView guideList;
    String lang;
    Intent intent;
    GuideAdapter itemsAdapter;
    DatabaseReference presenceReference;
    FirebaseUser user;
    ArrayList<String> engGuide=new ArrayList<String>();
    ArrayList<String> urduGuide=new ArrayList<String>();
    String[] engg={"State of Ihram","Mecca","Tawaf","Sa'ey","Departure to mina","Mount Arafat","Muzdalifah","Ramy al-Jamarat","Eid al-Adha"};
    String[] urduu={"احرم کی حالت","مکہ","توفف","سعی","مینا کی روانگی","عرفات پہاڑ","مظفرفا","رامی الجمیر","عید الاہھا"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hajj_guide);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        inIt();
        user= FirebaseAuth.getInstance().getCurrentUser();
        guideList=findViewById(R.id.guideList);
        presenceReference= FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        intent=getIntent();
        lang=intent.getStringExtra("Guide");
        if (lang.equals("urdu")){
                itemsAdapter = new GuideAdapter(getBaseContext(), R.layout.guidelayout, urduGuide);
            guideList.setAdapter(itemsAdapter);
        }else if (lang.equals("english")){
                itemsAdapter = new GuideAdapter(getBaseContext(), R.layout.guidelayout, engGuide);
            guideList.setAdapter(itemsAdapter);
        }
    }
    public void inIt(){
        List<String> list= Arrays.asList(engg);
        engGuide.addAll(list);
        List<String> list2=Arrays.asList(urduu);
        urduGuide.addAll(list2);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if (id==android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(user!=null) {
            presenceReference.child("online").setValue("true");
        }
    }

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

    @Override
    public void onPause() {
        super.onPause();
        if(user!=null) {
            presenceReference.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }
}
