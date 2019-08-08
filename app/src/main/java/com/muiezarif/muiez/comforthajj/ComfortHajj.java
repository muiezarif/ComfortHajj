package com.muiezarif.muiez.comforthajj;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.ServerValue;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import es.dmoral.toasty.Toasty;

public class ComfortHajj extends Application {
    FirebaseUser user;
    DatabaseReference reference;
    LocationManager lm ;
    boolean gps_enabled;
    boolean network_enabled;
    public static String UPDATED_DEVICE_TOKEN="";

    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(this, getString(R.string.nav_header_title));

        user=FirebaseAuth.getInstance().getCurrentUser();

        lm = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        network_enabled = false;
        gps_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        if(!gps_enabled && !network_enabled) {
            Toasty.warning(getApplicationContext(),"Please enable GPS",Toast.LENGTH_LONG).show();
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mHandler,new IntentFilter("com.muiezarif.muiez.comforthajj.FCMMSG"));
        FirebaseMessaging.getInstance().subscribeToTopic("updates");
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("comforthajj", "ComfortHajj", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Comfort Hajj Notifications");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        Firebase.setAndroidContext(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Picasso.Builder builder=new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this,Integer.MAX_VALUE));
        Picasso built=builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
        if(user!=null){
            reference=FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    reference.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    private BroadcastReceiver mHandler=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String name=intent.getStringExtra("title");
            if (name.equals("Call")){
                String apikey=intent.getStringExtra("apiKey");
                String tousertoken=intent.getStringExtra("toUserToken");
                String sessionid=intent.getStringExtra("sessionID");
                String fromuserid=intent.getStringExtra("fromUserId");
                Intent intent1=new Intent(getApplicationContext(), CallActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                intent1.putExtra("api_key",apikey);
                intent1.putExtra("to_user_token",tousertoken);
                intent1.putExtra("session_id",sessionid);
                intent1.putExtra("from_user_id",fromuserid);
//                startActivity(intent1);

            }else{
                String msg=intent.getStringExtra("message");
                String userId=intent.getStringExtra("fromUserId");
                Toasty.info(getApplicationContext(),name+"\n"+msg, Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
