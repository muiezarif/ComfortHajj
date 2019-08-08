package com.muiezarif.muiez.comforthajj;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class Compass extends AppCompatActivity implements SensorEventListener, LocationListener {
    AdView adView;
    ImageView compass, arrow;
    SensorManager sensorManager;
    Button updateLocation;
    Sensor sensor;
    Location userLocation;
    Location l;
    LocationManager locationManager;
    float bearTo;
    float currentDegree = 0f;
    float currentDegreeNeedle = 0f;
    boolean isGPSEnabled;
    boolean isNetworkEnabled;
    InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        adView=findViewById(R.id.adViewcompass);
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
        compass = findViewById(R.id.img_compass);
        arrow = findViewById(R.id.needle);
        updateLocation = findViewById(R.id.update_location_btn);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        updateLocation.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                if (interstitialAd.isLoaded()){
                    interstitialAd.show();
                }
                l = getLastKnownLocation();
                if (l != null) {
                    userLocation = new Location("service Provider");
                    userLocation.setAltitude(l.getAltitude());
                    userLocation.setLongitude(l.getLongitude());
                    userLocation.setLatitude(l.getLatitude());
                }
            }
        });

        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isGPSEnabled) {
            l = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (l != null) {
                userLocation = new Location("service Provider");
                userLocation.setAltitude(l.getAltitude());
                userLocation.setLongitude(l.getLongitude());
                userLocation.setLatitude(l.getLatitude());
            }
        } else if (isNetworkEnabled) {
            l = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (l != null) {
                userLocation = new Location("service Provider");
                userLocation.setAltitude(l.getAltitude());
                userLocation.setLongitude(l.getLongitude());
                userLocation.setLatitude(l.getLatitude());
            }
        } else {
            Toasty.info(Compass.this, "Cant get your location", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        } else {
            Toasty.warning(getApplicationContext(), "We are really sorry your device does not support it.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float degree = Math.round(event.values[0]);
        float head = Math.round(event.values[0]);
        float bearTo = 0f;
        Location destinationLoc = new Location("service Provider");
        destinationLoc.setLatitude(21.422487);
        destinationLoc.setLongitude(39.826206);
        if (userLocation != null) {
            bearTo = userLocation.bearingTo(destinationLoc);
            GeomagneticField geoField = new GeomagneticField(Double.valueOf(userLocation.getLatitude()).floatValue(), Double
                    .valueOf(userLocation.getLongitude()).floatValue(),
                    Double.valueOf(userLocation.getAltitude()).floatValue(),
                    System.currentTimeMillis());
            head -= geoField.getDeclination();
            if (bearTo < 0) {
                bearTo = bearTo + 360;
                //bearTo = -100 + 360  = 260;
            }
            float direction = bearTo - head;
            if (direction < 0) {
                direction = direction + 360;
            }
            RotateAnimation raQibla = new RotateAnimation(currentDegreeNeedle, direction, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            raQibla.setDuration(210);
            raQibla.setFillAfter(true);

            arrow.startAnimation(raQibla);

            currentDegreeNeedle = direction;
            RotateAnimation ra = new RotateAnimation(currentDegree, -degree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

// how long the animation will take place
            ra.setDuration(210);


// set the animation after the end of the reservation status
            ra.setFillAfter(true);

// Start the animation
            compass.startAnimation(ra);

            currentDegree = -degree;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onLocationChanged(Location location) {
        l = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private Location getLastKnownLocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
            }
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

}
