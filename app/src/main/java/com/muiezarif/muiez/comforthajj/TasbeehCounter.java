package com.muiezarif.muiez.comforthajj;

import android.content.Context;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.ServerValue;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.muiezarif.muiez.comforthajj.R;

public class TasbeehCounter extends AppCompatActivity  {
    AdView adView;
    private int mCounter=0;
    Button btn_count,btn_clear;
    TextView counter;
    FirebaseUser user;
    DatabaseReference presenceReference;
    AudioManager manager;
    InterstitialAd interstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasbeeh_counter);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        adView=findViewById(R.id.adViewtasbeeh);
        AdRequest request=new AdRequest.Builder().build();
        adView.loadAd(request);
        interstitialAd=new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-7701765309854052/7867968342");
        interstitialAd.loadAd(new AdRequest.Builder().build());
        try {
            interstitialAd.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        manager= (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        btn_count=findViewById(R.id.btn_counter);
        btn_clear=findViewById(R.id.btn_clearcounter);
        user= FirebaseAuth.getInstance().getCurrentUser();
        presenceReference= FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        counter=findViewById(R.id.tasbeehcounter);

        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCounter=0;
                counter.setText(Integer.toString(mCounter));
            }
        });
        btn_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCounter++;
                counter.setText(Integer.toString(mCounter));
            }
        });
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if (id==android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                mCounter++;
                counter.setText(Integer.toString(mCounter));
                manager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_RAISE,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                mCounter++;
                counter.setText(Integer.toString(mCounter));
                manager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_LOWER,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                return true;
            default:
            return super.onKeyDown(keyCode, event);
        }
    }
}
