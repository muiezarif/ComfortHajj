package com.muiezarif.muiez.comforthajj;

import android.Manifest;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GPSService extends Service {
    LocationListener listener;
    LocationManager manager;
    DatabaseReference reference;
    FirebaseUser user;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public ComponentName startForegroundService(Intent service) {
        return super.startForegroundService(service);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        onTaskRemoved(intent);
        reference= FirebaseDatabase.getInstance().getReference().child("locations");
        user= FirebaseAuth.getInstance().getCurrentUser();
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Intent i=new Intent("location_update");
                i.putExtra("Lat",location.getLatitude());
                i.putExtra("Lng",location.getLongitude());
                sendBroadcast(i);
                reference.child(user.getUid()).child("lat").setValue(String.valueOf(location.getLatitude()));
                reference.child(user.getUid()).child("lng").setValue(String.valueOf(location.getLongitude()));


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };
        manager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, listener);

        return START_STICKY;
    }

//    @Override
//    public void onCreate() {
//        reference= FirebaseDatabase.getInstance().getReference().child("locations");
//        user= FirebaseAuth.getInstance().getCurrentUser();
//
//
//    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent i=new Intent(getApplicationContext(),this.getClass());
        i.setPackage(getPackageName());
        startService(i);
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (manager!=null){
            manager.removeUpdates(listener);
        }
    }
}
