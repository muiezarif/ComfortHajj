package com.muiezarif.muiez.comforthajj;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMarkerClickListener {
    AdView adView;
    private GoogleMap mMap;
    GoogleApiClient client;
    LocationRequest request;
    Marker currentUserMarker;
    Marker updateMarkerPolyline;
    ArrayList<Marker> markerArrayList;
    Location lastLocation;
    DatabaseReference reference;
    DatabaseReference connectedUserReference;
    DatabaseReference infoReference;
    FirebaseUser user;
    HashMap<String,Marker> hashMap;
    LatLng updateLatLng;
    LatLng latLng;
    Route cameraTrackRoute;
    boolean cameraTrack=false;
    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};
    ImageButton clear;
    ImageButton zoom,btnTrack,btnShowName;
    EditText showNamesMarker;
//    String serverKey="AIzaSyBpqon2SGE25oHTjTlBGwabkSh_gB5jOa8";
    LocationManager lm ;
    boolean gps_enabled;
    boolean network_enabled;
    InterstitialAd interstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        adView=findViewById(R.id.adViewmaps);
        AdRequest request=new AdRequest.Builder().build();
        adView.loadAd(request);
        interstitialAd=new InterstitialAd(getApplicationContext());
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
        markerArrayList=new ArrayList<Marker>();
        clear=findViewById(R.id.btn_clear);
        showNamesMarker=findViewById(R.id.et_show_names);
        btnShowName=findViewById(R.id.btn_show_name);
        zoom=findViewById(R.id.btn_goto_current_location);
        btnTrack=findViewById(R.id.btn_enable_camera_track);
        polylines=new ArrayList<>();
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        connectedUserReference=FirebaseDatabase.getInstance().getReference().child("connected_users").child(user.getUid());
        infoReference=FirebaseDatabase.getInstance().getReference().child("users");
        hashMap=new HashMap<>();
        btnShowName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interstitialAd=new InterstitialAd(getApplicationContext());
                interstitialAd.setAdUnitId("ca-app-pub-7701765309854052/7867968342");
                interstitialAd.loadAd(new AdRequest.Builder().build());
                if (interstitialAd.isLoaded()){
                    interstitialAd.show();
                }
                if (showNamesMarker.getText().toString().isEmpty()){
                    Toasty.warning(MapsActivity.this,"Please enter name to search friends",Toast.LENGTH_LONG).show();
                }
                    showMarkerNames(showNamesMarker.getText().toString().replace(" ", ""));

            }
        });

        btnTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    Toasty.warning(MapsActivity.this, "Please grant permission to get your location!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!gps_enabled && !network_enabled) {
                    Toasty.warning(getApplicationContext(),"Please enable GPS and Please refresh this page after turning on GPS :)",Toast.LENGTH_LONG).show();
                    return;
                }
                if (cameraTrack==false){
                    cameraTrack=true;
                    Toasty.info(MapsActivity.this,"Camera tracking is enabled",Toast.LENGTH_SHORT).show();
                }else if(cameraTrack==true){
                    cameraTrack=false;
                    Toasty.info(MapsActivity.this,"Camera tracking is disabled",Toast.LENGTH_SHORT).show();
                }
            }
        });
        zoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    Toasty.warning(MapsActivity.this, "Please grant permission to get your location!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!gps_enabled && !network_enabled) {
                    Toasty.warning(getApplicationContext(),"Please enable GPS and refresh this page after turning on GPS :)",Toast.LENGTH_LONG).show();
                    return;
                }
                if (latLng==null) {
                    Toasty.info(MapsActivity.this,"Let the location load",Toast.LENGTH_SHORT).show();
                }else{
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomBy(10));
                }
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                erasePolylines();
            }
        });
        connectedUserReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final String userKey=dataSnapshot.getKey();
                reference.child("locations").child(userKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("lat") && dataSnapshot.hasChild("lng")){
                             final String lat = dataSnapshot.child("lat").getValue().toString();
                             final String lng = dataSnapshot.child("lng").getValue().toString();
                            infoReference.child(userKey).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String name = dataSnapshot.child("name").getValue(String.class);
                                    updateLatLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                                    MarkerOptions markerOptions = new MarkerOptions();
                                    markerOptions.position(updateLatLng);
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                                    markerOptions.title(name);
                                    Marker myMarker = hashMap.get(name);
                                    if (myMarker != null) {
                                        myMarker.setPosition(updateLatLng);
                                        markerArrayList.add(myMarker);
                                        myMarker.showInfoWindow();
                                    } else {
                                        Marker marker = mMap.addMarker(markerOptions);
                                        hashMap.put(marker.getTitle(), marker);
                                        markerArrayList.add(marker);
                                        marker.showInfoWindow();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });


                        }else{
                            Toasty.warning(MapsActivity.this,"Some users location is not updated yet.",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
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
            Toasty.warning(MapsActivity.this, "Please grant permission to get your location!", Toast.LENGTH_LONG).show();
            return;
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);

        mMap.setOnMarkerClickListener(this);

    }
    protected synchronized void buildGoogleApiClient() {
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
            Toasty.warning(MapsActivity.this, "Grant Location permission to this app", Toast.LENGTH_LONG).show();
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toasty.error(MapsActivity.this, connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(final Location location) {
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

        }
        if (!polylines.isEmpty()){
            onMarkerClick(updateMarkerPolyline);
        }

    }

    private void erasePolylines(){
        for (Polyline line:polylines){
            line.remove();
        }
        polylines.clear();
    }


    @Override
    public boolean onMarkerClick(final Marker marker) {

        erasePolylines();
        updateMarkerPolyline=marker;
        final LatLng start=new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
        final LatLng end=new LatLng(marker.getPosition().latitude,marker.getPosition().longitude);
        GoogleDirection.withServerKey(getString(R.string.google_server_key))
                .from(start)
                .to(end)
                .language(Language.ENGLISH)
                .transportMode(TransportMode.WALKING)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if(direction.isOK()) {
                            // TODO
                            com.akexorcist.googledirection.model.Route route=direction.getRouteList().get(0);
                            Leg leg=route.getLegList().get(0);
                            ArrayList<LatLng> pointList=leg.getDirectionPoint();
                            polylines.add(mMap.addPolyline(DirectionConverter.createPolyline(getBaseContext(),pointList,5, Color.BLACK)));
                            //setCameraWithCoordinationBounds(route);
                            LatLng markerLoc=new LatLng(marker.getPosition().latitude,marker.getPosition().longitude);


                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        // TODO
                        Toasty.error(MapsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                });
        return false;
    }


    private void setCameraWithCoordinationBounds(com.akexorcist.googledirection.model.Route route) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    public void showMarkerNames(String name){
        for (int i=0;i<markerArrayList.size();i++){
            markerArrayList.get(i).getTitle().replace(" ","");
            if (markerArrayList.get(i).getTitle().toString().equals(name)||markerArrayList.get(i).getTitle().toLowerCase().toString().equals(name.toLowerCase())||markerArrayList.get(i).getTitle().toUpperCase().toString().equals(name.toUpperCase())) {
                markerArrayList.get(i).showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(markerArrayList.get(i).getPosition()));

            }
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("PAUSE","paused");
        onLocationChanged(lastLocation);

    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.e("STOP","stopped");
        onLocationChanged(lastLocation);

    }


}
