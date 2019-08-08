package com.muiezarif.muiez.comforthajj;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.firebase.client.ServerValue;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.muiezarif.muiez.comforthajj.usersignedin.UserSignedIn;

public class MainActivity extends AppCompatActivity {
    ImageView hajj;
    FirebaseUser user;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar();
        hajj=findViewById(R.id.hajj);
        Animation animation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.splash_anim);
        hajj.setAnimation(animation);
        user= FirebaseAuth.getInstance().getCurrentUser();

        if (user!=null){
            finish();
            reference= FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
            reference.child("online").setValue("true");
            startActivity(new Intent(MainActivity.this, UserSignedIn.class));
        }
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                finish();
                Intent intent=new Intent(MainActivity.this,SignIn.class);
                startActivity(intent);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (user!=null) {
            reference.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (user!=null) {
            reference.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }
}
