package com.muiezarif.muiez.comforthajj.usersignedin;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


import com.muiezarif.muiez.comforthajj.CallActivity;
import com.muiezarif.muiez.comforthajj.Compass;

import com.muiezarif.muiez.comforthajj.HotelMap;
import com.muiezarif.muiez.comforthajj.MapsActivity;
import com.muiezarif.muiez.comforthajj.SettingsActivity;

import com.muiezarif.muiez.comforthajj.R;
import com.muiezarif.muiez.comforthajj.SignIn;
import com.firebase.client.ServerValue;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.karan.churi.PermissionManager.PermissionManager;
//import com.nabinbhandari.android.permissions.PermissionHandler;
//import com.nabinbhandari.android.permissions.Permissions;
import com.muiezarif.muiez.comforthajj.TasbeehCounter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class UserSignedIn extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseAuth auth;
    DatabaseReference reference;
    DatabaseReference locationReference;
    FirebaseUser user;
    TextView navUsername,navUserEmail;
    CircleImageView navUserImage;
    LocationManager lm;
    Location location;
    private View headerView;
    PermissionManager manager;
    String Lat,Lng;
    String tokenId;
    DatabaseReference onCallReference;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCallReference=FirebaseDatabase.getInstance().getReference().child("users");
        user=FirebaseAuth.getInstance().getCurrentUser();
        LocalBroadcastManager.getInstance(this).registerReceiver(mHandler,new IntentFilter("com.muiezarif.muiez.comforthajj.FCMMSG"));
        if (android.os.Build.VERSION.SDK_INT >= 23) {   //Android M Or Over
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1234);
            }
        }
        onCallReference.child(user.getUid()).child("onCall").setValue("false");


        lm= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        setContentView(R.layout.activity_user_signed_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");
        manager=new PermissionManager() {};
        manager.checkAndRequestPermissions(UserSignedIn.this);
        reference= FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                if (task.isSuccessful()){
                    tokenId=task.getResult().getToken().toString();
                    reference.child("tokenId").setValue(tokenId);
                }else{
                    Toasty.warning(UserSignedIn.this, "Cant get tokenID of user", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //------//

        if (ActivityCompat.checkSelfPermission(UserSignedIn.this, Manifest.permission.ACCESS_FINE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(UserSignedIn.this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(UserSignedIn.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            Toasty.warning(UserSignedIn.this, "Please give permission to get your location!", Toast.LENGTH_SHORT).show();
        }else{
            location=lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location!=null){
                Lat=String.valueOf(location.getLatitude());
                Lng=String.valueOf(location.getLongitude());
            }else{
                Toasty.warning(UserSignedIn.this, "Can't update your current location", Toast.LENGTH_SHORT).show();
            }
        }

        locationReference=FirebaseDatabase.getInstance().getReference().child("locations");
        locationReference.child(user.getUid()).child("lat").setValue(Lat);
        locationReference.child(user.getUid()).child("lng").setValue(Lng);
        //-----//
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //-----//

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerView=navigationView.getHeaderView(0);
        inIt();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String navName=dataSnapshot.child("name").getValue().toString();
                final String navImage=dataSnapshot.child("thumb_image").getValue().toString();
                String navEmail=dataSnapshot.child("email").getValue().toString();
               navUsername.setText(navName);
               navUserEmail.setText(navEmail);
                Picasso.get().load(navImage).placeholder(R.drawable.userprofile)
                        .networkPolicy(NetworkPolicy.OFFLINE).into(navUserImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(navImage).placeholder(R.drawable.userprofile).into(navUserImage);

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        //-----//
        MainUserSignedInFragment mainUserSignedInFragment=new MainUserSignedInFragment();
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        transaction.add(R.id.userSignedInFrameLayout,mainUserSignedInFragment).commit();
        auth=FirebaseAuth.getInstance();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_signed_in, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_logout){
            reference.child("online").setValue(ServerValue.TIMESTAMP);
            auth.signOut();
            reference.child("device_token").setValue("");
            finish();
            Intent intent=new Intent(UserSignedIn.this, SignIn.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
         if(id == R.id.nav_main) {
             MainUserSignedInFragment mainUserSignedInFragment=new MainUserSignedInFragment();
             FragmentManager manager=getSupportFragmentManager();
             FragmentTransaction transaction=manager.beginTransaction();
             transaction.replace(R.id.userSignedInFrameLayout,mainUserSignedInFragment).commit();

        }else if (id == R.id.nav_map) {
             startActivity(new Intent(this, MapsActivity.class));
        } else if (id == R.id.nav_hotel) {
             startActivity(new Intent(this, HotelMap.class));
        } else if (id == R.id.nav_Message) {
            FragmentMessage message=new FragmentMessage();
            FragmentManager manager=getSupportFragmentManager();
            FragmentTransaction transaction=manager.beginTransaction();
            transaction.replace(R.id.userSignedInFrameLayout,message).commit();
        }else if(id == R.id.nav_linkRequests){
            FragmentLinkUserRequests requests=new FragmentLinkUserRequests();
            FragmentManager manager=getSupportFragmentManager();
            FragmentTransaction transaction=manager.beginTransaction();
            transaction.replace(R.id.userSignedInFrameLayout,requests).commit();
         }else if(id == R.id.nav_profile){
             FragmentProfile profile=new FragmentProfile();
             FragmentManager manager=getSupportFragmentManager();
             FragmentTransaction transaction=manager.beginTransaction();
             transaction.replace(R.id.userSignedInFrameLayout,profile).commit();
         }else if(id== R.id.nav_all_users){
             EmergencyFragment emergencyFragment=new EmergencyFragment();
             FragmentManager manager=getSupportFragmentManager();
             FragmentTransaction transaction=manager.beginTransaction();
             transaction.replace(R.id.userSignedInFrameLayout,emergencyFragment).commit();
         }else if(id == R.id.nav_connected_users){
             ConnectedUsersFragment connectedUsersFragment=new ConnectedUsersFragment();
             FragmentManager manager=getSupportFragmentManager();
             FragmentTransaction transaction=manager.beginTransaction();
             transaction.replace(R.id.userSignedInFrameLayout,connectedUsersFragment).commit();
         }else if(id==R.id.nav_hajjGuide){
             FragmentHajjGuide fragmentHajjGuide=new FragmentHajjGuide();
             FragmentManager manager=getSupportFragmentManager();
             FragmentTransaction transaction=manager.beginTransaction();
             transaction.replace(R.id.userSignedInFrameLayout,fragmentHajjGuide).commit();
         }else if(id==R.id.nav_tasbeeh_counter){
//             FragmentTasbeehCounter fragmentTasbeehCounter=new FragmentTasbeehCounter();
//             FragmentManager manager=getSupportFragmentManager();
//             FragmentTransaction transaction=manager.beginTransaction();
//             transaction.replace(R.id.userSignedInFrameLayout,fragmentTasbeehCounter).commit();
             startActivity(new Intent(UserSignedIn.this, TasbeehCounter.class));
         }else if(id==R.id.nav_qibla_compass){
             startActivity(new Intent(UserSignedIn.this, Compass.class));
         }else if (id==R.id.nav_settings){
             startActivity(new Intent(UserSignedIn.this, SettingsActivity.class));
         }
//         else if (id==R.id.nav_call_friends){
//             CallFriendsListFragment callFriendsListFragment=new CallFriendsListFragment();
//             FragmentManager manager=getSupportFragmentManager();
//             FragmentTransaction transaction=manager.beginTransaction();
//             transaction.replace(R.id.userSignedInFrameLayout,callFriendsListFragment).commit();
//         }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void inIt(){
        navUsername=headerView.findViewById(R.id.nav_user_name);
        navUserEmail=headerView.findViewById(R.id.nav_user_email);
        navUserImage=headerView.findViewById(R.id.nav_user_img);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

//        manager.checkResult(requestCode,permissions,grantResults);
//        ArrayList<String> deniedPermission=manager.getStatus().get(0).denied;
//        if (deniedPermission.isEmpty()){
//            Toasty.success(UserSignedIn.this, "Permission granted", Toast.LENGTH_SHORT).show();
//        }else{
//            Toasty.warning(UserSignedIn.this, "Permission denied! Please grant this permission.", Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1234) {
            if (android.os.Build.VERSION.SDK_INT >= 23) {   //Android M Or Over
                if (!Settings.canDrawOverlays(this)) {
                    // ADD UI FOR USER TO KNOW THAT UI for SYSTEM_ALERT_WINDOW permission was not granted earlier...
                }
            }
        }
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        if(user!=null) {
//            reference.child("online").setValue("true");
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
        if(user!=null) {
            reference.child("online").setValue("true");
        }
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (user!=null) {
//            reference.child("online").setValue(ServerValue.TIMESTAMP);
//            LocalBroadcastManager.getInstance(this).unregisterReceiver(mHandler);
//        }
//    }

    @Override
    protected void onStop() {
        super.onStop();
        if (user!=null) {
            reference.child("online").setValue(ServerValue.TIMESTAMP);
        }
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
                Intent intent1=new Intent(UserSignedIn.this, CallActivity.class);
                intent1.putExtra("api_key",apikey);
                intent1.putExtra("to_user_token",tousertoken);
                intent1.putExtra("session_id",sessionid);
                intent1.putExtra("from_user_id",fromuserid);
                startActivity(intent1);

            }else{
                String msg=intent.getStringExtra("message");
                String userId=intent.getStringExtra("fromUserId");
                Toasty.info(getApplicationContext(),name+"\n"+msg, Toast.LENGTH_LONG).show();
            }

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mHandler);

    }


}
