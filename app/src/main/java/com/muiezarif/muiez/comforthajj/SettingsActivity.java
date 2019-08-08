package com.muiezarif.muiez.comforthajj;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.ServerValue;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import es.dmoral.toasty.Toasty;
import okhttp3.internal.Util;

public class SettingsActivity extends AppCompatActivity {
    Button addEmergency,startService,stopService;
    EditText emergencyNumber;
    DatabaseReference reference;
    DatabaseReference emergencyReference;
    DatabaseReference presenceReference;
    DatabaseReference locationReference;
    FirebaseUser user;
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        startService=findViewById(R.id.startService);
        stopService=findViewById(R.id.stopService);
        stopService=findViewById(R.id.stopService);
        locationReference=FirebaseDatabase.getInstance().getReference().child("locations");
        addEmergency=findViewById(R.id.btnAddEmergencyContact);
        user= FirebaseAuth.getInstance().getCurrentUser();
        emergencyNumber=findViewById(R.id.etEmergencyNumber);
        presenceReference=FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        emergencyReference= FirebaseDatabase.getInstance().getReference().child("emergency_contact");
        addEmergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emergencyReference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.hasChild("num")) {
                            emergencyReference.child(user.getUid()).child("num").setValue(emergencyNumber.getText().toString());
                            Toasty.success(SettingsActivity.this, "Emergency Number Added").show();
                        }else if (dataSnapshot.hasChild("num")){
                            emergencyReference.child(user.getUid()).child("num").setValue(emergencyNumber.getText().toString());
                            Toasty.success(SettingsActivity.this, "Emergency Number Changed").show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });
        startService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toasty.warning(getApplicationContext(),"Please grant permission to access your location",Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    Intent i = new Intent(getApplicationContext(), GPSService.class);
                    startService(i);
                    Toasty.info(SettingsActivity.this, "Your location will always update while you are using app", Toast.LENGTH_SHORT).show();
//                registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
                }
            }
        });
        stopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),GPSService.class);
                stopService(i);
                Toasty.info(SettingsActivity.this,"Location update stopped",Toast.LENGTH_SHORT).show();
//                if (broadcastReceiver!=null){
//                    unregisterReceiver(broadcastReceiver);
//                }
            }
        });


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
        Log.i("activity","start");
    }

    @Override
    public void onResume() {
        super.onResume();
        if(user!=null) {
            presenceReference.child("online").setValue("true");
        }
//        if (broadcastReceiver==null){
//            broadcastReceiver=new BroadcastReceiver() {
//                @Override
//                public void onReceive(Context context, Intent intent) {
//                    locationReference.child(user.getUid()).child("lat").setValue(intent.getExtras().get("Lat"));
//                    locationReference.child(user.getUid()).child("lng").setValue(intent.getExtras().get("Lng"));
//                }
//            };
//        }
        Log.i("activity","resume");


    }

    @Override
    public void onStop() {
        super.onStop();
        if(user!=null) {
            presenceReference.child("online").setValue(ServerValue.TIMESTAMP);
        }

        Log.i("activity","Stopped");
    }

    @Override
    public void onPause() {
        super.onPause();
        if(user!=null) {
            presenceReference.child("online").setValue(ServerValue.TIMESTAMP);
        }
        Log.i("activity","Paused");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (broadcastReceiver!=null){
//            registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
//            unregisterReceiver(broadcastReceiver);
//        }
        Log.i("activityDestroy","Destroyed");
    }
}
