package com.muiezarif.muiez.comforthajj;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.Language;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.muiezarif.muiez.comforthajj.R;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import es.dmoral.toasty.Toasty;

public class HotelMap extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    GoogleApiClient client;
    LocationRequest request;
    Location lastLocation;
    LatLng latLng;
    LatLng hotelLatLng;
    Marker currentUserMarker;
    Marker hotelMarker;
    String hotelLat,hotelLng;
    DatabaseReference reference;
    DatabaseReference hotelReference;
    FirebaseUser user;
    boolean cameraTrack=false;
    ImageButton setHotel,hotelDirection;
    ImageButton clearDirections;
    private List<Polyline> polylines;
//    String serverKey="AIzaSyBpqon2SGE25oHTjTlBGwabkSh_gB5jOa8";
    HashMap<String,Marker> hashMap;
    boolean hotelTrack=false;
    LocationManager lm ;
    boolean gps_enabled;
    boolean network_enabled;
    LatLng temp;
    AdView adView;
    InterstitialAd interstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.hotelmap);
        mapFragment.getMapAsync(this);
        adView=findViewById(R.id.adViewhotelmap);
        AdRequest request=new AdRequest.Builder().build();
        adView.loadAd(request);
        interstitialAd=new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-7701765309854052/7867968342");
        interstitialAd.loadAd(new AdRequest.Builder().build());
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
            Toasty.warning(getApplicationContext(),"Please enable GPS and Please refresh this page after turning on GPS :)",Toast.LENGTH_LONG).show();
        }
        hashMap=new HashMap<>();
        clearDirections = findViewById(R.id.btn_clear_hotel_direction);
        setHotel = findViewById(R.id.btn_set_hotel);
        hotelDirection = findViewById(R.id.btn_hotel_direction);
        polylines = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference();
        hotelReference = FirebaseDatabase.getInstance().getReference().child("hotel_locations");
        user = FirebaseAuth.getInstance().getCurrentUser();
        setHotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    Toasty.warning(HotelMap.this, "Please grant permission to get your location!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!gps_enabled && !network_enabled) {
                    Toasty.warning(getApplicationContext(),"Please enable GPS and Please refresh this page after turning on GPS :)",Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    temp=new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                } catch (Exception e) {
                    e.printStackTrace();
                }


                new FancyGifDialog.Builder(HotelMap.this)
                        .setTitle("Set Hotel Location")
                        .setMessage("Are you sure about setting your hotel location to current location?")
                        .setPositiveBtnText("YES")
                        .setPositiveBtnBackground("#FF4081")
                        .setGifResource(R.drawable.img_hotel)//Pass your Gif here
                        .isCancellable(true)
                        .OnPositiveClicked(new FancyGifDialogListener() {
                            @Override
                            public void OnClick() {
                                if (temp == null) {
                                    Toasty.info(HotelMap.this, "Let the location load.", Toast.LENGTH_SHORT).show();
                                } else {
                                    reference.child("hotel_locations").child(user.getUid()).child("lat").setValue(temp.latitude).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });
                                    reference.child("hotel_locations").child(user.getUid()).child("lng").setValue(temp.longitude).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                        }
                                    });
                                    if (hotelMarker != null) {
                                        hotelMarker.remove();
                                        Toasty.info(HotelMap.this, "Current location is updated as hotel", Toast.LENGTH_SHORT).show();
                                    } else {

                                        MarkerOptions markerOptions = new MarkerOptions();
                                        markerOptions.position(temp);
                                        markerOptions.title("My Hotel");
                                        hotelMarker = mMap.addMarker(markerOptions);
                                        hotelTrack = true;
                                        Toasty.info(HotelMap.this, "Current location is set as hotel", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        })
                        .build();
            }
        });
        hotelDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interstitialAd=new InterstitialAd(getApplicationContext());
                interstitialAd.setAdUnitId("ca-app-pub-7701765309854052/7867968342");
                interstitialAd.loadAd(new AdRequest.Builder().build());
                if (interstitialAd.isLoaded()){
                    interstitialAd.show();
                }
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    Toasty.warning(HotelMap.this, "Please grant permission to get your location!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!gps_enabled && !network_enabled) {
                    Toasty.warning(getApplicationContext(), "Please enable GPS and Please refresh this page after turning on GPS :)", Toast.LENGTH_LONG).show();
                    return;
                }
                erasePolylines();
                if (lastLocation == null) {
                    Toasty.info(HotelMap.this,"Let the location load.",Toast.LENGTH_SHORT).show();
                }else{
                reference.child("hotel_locations").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        erasePolylines();
                        if (dataSnapshot.hasChild("lat") && dataSnapshot.hasChild("lng")) {
                            hotelLat = dataSnapshot.child("lat").getValue().toString();
                            hotelLng = dataSnapshot.child("lng").getValue().toString();
                            final LatLng start = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                            final LatLng end = new LatLng(Double.parseDouble(hotelLat), Double.parseDouble(hotelLng));
                            GoogleDirection.withServerKey(getString(R.string.google_server_key))
                                    .from(start)
                                    .to(end)
                                    .language(Language.ENGLISH)
                                    .transportMode(TransportMode.WALKING)
                                    .execute(new DirectionCallback() {
                                        @Override
                                        public void onDirectionSuccess(Direction direction, String rawBody) {
                                            if (direction.isOK()) {
                                                Route route = direction.getRouteList().get(0);
                                                Leg leg = route.getLegList().get(0);
                                                ArrayList<LatLng> pointList = leg.getDirectionPoint();
                                                polylines.add(mMap.addPolyline(DirectionConverter.createPolyline(getBaseContext(), pointList, 5, Color.BLACK)));
                                                setCameraWithCoordinationBounds(route);
                                            }
                                        }

                                        @Override
                                        public void onDirectionFailure(Throwable t) {
                                            Toasty.error(HotelMap.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toasty.info(HotelMap.this, "Please set Hotel location First", Toast.LENGTH_SHORT).show();
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                }
            }
        });
        clearDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                erasePolylines();
            }
        });
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("hotel_locations")){
                    if (dataSnapshot.child("hotel_locations").hasChild(user.getUid())) {
                        getHotelLocation();
                    }

                }else{
                    Toasty.info(HotelMap.this, "Select location", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            Toasty.warning(HotelMap.this, "Please grant permission to get your location!", Toast.LENGTH_SHORT).show();
            return;
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);

    }
    private synchronized void buildGoogleApiClient() {
        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        client.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        request = new LocationRequest();
        request.setInterval(3100);
        request.setFastestInterval(1100);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);

    }

    @Override
    public void onConnectionSuspended(int i) {
        Toasty.warning(HotelMap.this, "Connection has been suspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toasty.error(HotelMap.this, connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation=location;
        if (location==null){

        }else {
            latLng=new LatLng(location.getLatitude(),location.getLongitude());
            if (currentUserMarker!=null){
                currentUserMarker.remove();
            }
            reference.child("locations").child(user.getUid()).child("lat").setValue(String.valueOf(location.getLatitude()));
            reference.child("locations").child(user.getUid()).child("lng").setValue(String.valueOf(location.getLongitude()));
            MarkerOptions markerOptions=new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("you");
            currentUserMarker=mMap.addMarker(markerOptions);
            if (cameraTrack==true){
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            }
            if (!polylines.isEmpty()){
                erasePolylines();
                final LatLng start = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                final LatLng end = new LatLng(Double.parseDouble(hotelLat), Double.parseDouble(hotelLng));
                GoogleDirection.withServerKey(getString(R.string.google_server_key))
                        .from(start)
                        .to(end)
                        .language(Language.ENGLISH)
                        .transportMode(TransportMode.WALKING)
                        .execute(new DirectionCallback() {
                            @Override
                            public void onDirectionSuccess(Direction direction, String rawBody) {
                                if(direction.isOK()) {
                                    Route route = direction.getRouteList().get(0);
                                    Leg leg = route.getLegList().get(0);
                                    ArrayList<LatLng> pointList = leg.getDirectionPoint();
                                    polylines.add(mMap.addPolyline(DirectionConverter.createPolyline(getBaseContext(), pointList, 5, Color.BLACK)));
                                }
                            }

                            @Override
                            public void onDirectionFailure(Throwable t) {
                                Toasty.error(HotelMap.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }

        }
    }
    private void setCameraWithCoordinationBounds(com.akexorcist.googledirection.model.Route route) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }
    private void erasePolylines(){
        for (Polyline line:polylines){
            line.remove();
        }
        polylines.clear();
    }
    private void getHotelLocation(){
        hotelReference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("lat") && dataSnapshot.hasChild("lng")) {


                       String lat = dataSnapshot.child("lat").getValue().toString();
                       String lng = dataSnapshot.child("lng").getValue().toString();

                    if (!lat.isEmpty() && !lng.isEmpty()) {
                        hotelLatLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                        if (hotelMarker != null) {
                            hotelMarker.remove();
                        }
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(hotelLatLng);
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                            markerOptions.title("My Hotel");
                            hotelMarker = mMap.addMarker(markerOptions);
                            hotelMarker.showInfoWindow();

                    }

                }else{
                    Toasty.info(HotelMap.this, "Select Hotel Location", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}
