package com.muiezarif.muiez.comforthajj;

import android.Manifest;
import android.app.Service;
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

public class BackgroundLocationService extends Service {
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        manager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return Integer.parseInt(null);
        }
        reference= FirebaseDatabase.getInstance().getReference().child("locations");
        user= FirebaseAuth.getInstance().getCurrentUser();
        listener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
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
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, listener);




        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (manager!=null){

            manager.removeUpdates(listener);
        }
    }
}
