package com.muiezarif.muiez.comforthajj;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opentok.android.AudioDeviceManager;
import com.opentok.android.BaseAudioDevice;
import com.opentok.android.Connection;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;
import com.opentok.android.SubscriberKit;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class CallActivity extends AppCompatActivity implements Session.SessionListener, PublisherKit.PublisherListener, Session.ConnectionListener, Session.ReconnectionListener, SubscriberKit.SubscriberListener {
    TextView tvState,tvName;
    ImageButton btnEnd,btnAccept;
    CircleImageView userImage;
    DatabaseReference callNotifyRef;
    DatabaseReference onCallReference;
    String calledUserKey;
    FirebaseUser user;
    //Communication status
    boolean isConnected = false;
    String currentUserToken,receiverUserToken,receiverApiKey,receiverSessionId,apiKey,sessionId,fromUserId;
    Session mSession,mReceiverSession;
    Publisher mPublisher;
    Subscriber mSubscriber;
    DatabaseReference currentImageReference;
    DatabaseReference toImageReference;
    DatabaseReference userRejectedCallReference;
    AudioPlayer player;





    private BroadcastReceiver mHandler=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (!(intent.getStringExtra("fromUserToken").equals(null))){
                String name=intent.getStringExtra("title");
                currentUserToken=intent.getStringExtra("fromUserToken");
                apiKey= intent.getStringExtra("apiKey");
                sessionId=intent.getStringExtra("sessionID");
                mSession = new Session.Builder(getApplicationContext(), apiKey, sessionId).build();
                mSession.setSessionListener(CallActivity.this);
                mSession.setConnectionListener(CallActivity.this);
                mSession.setReconnectionListener(CallActivity.this);
                mSession.connect(currentUserToken);
            }


        }
    };
    private BroadcastReceiver mHandler2=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (!(intent.getStringExtra("fromUserId").equals(null))){
                player.stopRingtone();
                player.stopProgressTone();
                finish();
            }


        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        if (ContextCompat.checkSelfPermission(CallActivity.this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(CallActivity.this, Manifest.permission.PROCESS_OUTGOING_CALLS) != PackageManager.PERMISSION_GRANTED) {
                Toasty.warning(CallActivity.this, "Please grant permission for making calls", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            Toasty.warning(CallActivity.this, "Please grant permission for microphone", Toast.LENGTH_SHORT).show();
            finish();
            return;
            // Permission is not granted
        }
        player=new AudioPlayer(this);
        tvState=findViewById(R.id.tvState);
        tvName=findViewById(R.id.tvName);
        btnEnd=findViewById(R.id.btn_call_end);
        btnAccept=findViewById(R.id.btn_accept_call);
        userImage=findViewById(R.id.call_user_img);
        currentImageReference=FirebaseDatabase.getInstance().getReference().child("users");
        toImageReference=FirebaseDatabase.getInstance().getReference().child("users");
        onCallReference=FirebaseDatabase.getInstance().getReference().child("users");
        userRejectedCallReference=FirebaseDatabase.getInstance().getReference().child("call_reject_notification");
        LocalBroadcastManager.getInstance(this).registerReceiver(mHandler,new IntentFilter("com.muiezarif.muiez.comforthajj.FCMMSGG"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mHandler2,new IntentFilter("com.muiezarif.muiez.comforthajj.FCMMSGREJECT"));
        user= FirebaseAuth.getInstance().getCurrentUser();
        callNotifyRef= FirebaseDatabase.getInstance().getReference().child("call_notifications");

        if (getIntent().hasExtra("key")) {
            calledUserKey = getIntent().getExtras().get("key").toString();
            onCallReference.child(user.getUid()).child("onCall").setValue(calledUserKey);
            onCallReference.child(calledUserKey).child("onCall").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.getValue().equals("false")){
                        player.stopProgressTone();
                        Toasty.info(CallActivity.this,"User is on another call",Toast.LENGTH_SHORT).show();
                        finish();
                    }else if (dataSnapshot.getValue().equals("false")){
                        callNotifyRef.child(calledUserKey).push().child("from").setValue(user.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                onCallReference.child(calledUserKey).child("onCall").setValue(user.getUid());
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            tvState.setText("Ringing");
            player.playProgressTone();
            toImageReference.child(calledUserKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("image")) {
                        try {
                            final String image = dataSnapshot.child("image").getValue().toString();
                            tvName.setText(dataSnapshot.child("name").getValue().toString());
                            if (!image.equals("default")) {
                                Picasso.get().load(image).placeholder(R.drawable.userprofile)
                                        .networkPolicy(NetworkPolicy.OFFLINE).into(userImage, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Picasso.get().load(image).placeholder(R.drawable.userprofile).into(userImage);
                                    }
                                });
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            btnAccept.setVisibility(View.INVISIBLE);
            btnEnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        isConnected=false;
                        player.stopProgressTone();
                        mSession.disconnect();
                        mReceiverSession.disconnect();
                        mSession.unsubscribe(mSubscriber);
                        mReceiverSession.unsubscribe(mSubscriber);
                        mSession.unpublish(mPublisher);
                        mReceiverSession.unpublish(mPublisher);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    userRejectedCallReference.child(calledUserKey).push().child("from").setValue(user.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            onCallReference.child(calledUserKey).child("onCall").setValue("false");
                        }
                    });

                    finish();



                }
            });




        }
        if (getIntent().hasExtra("to_user_token")){
            player.playRingtone();
            tvState.setText("Incoming Call");
            receiverApiKey=getIntent().getExtras().get("api_key").toString();
            receiverSessionId=getIntent().getExtras().get("session_id").toString();
            receiverUserToken=getIntent().getExtras().get("to_user_token").toString();
            fromUserId=getIntent().getExtras().get("from_user_id").toString();
            onCallReference.child(user.getUid()).child("onCall").setValue(fromUserId);
            onCallReference.child(fromUserId).child("onCall").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if ((dataSnapshot.getValue().equals("false"))||(!dataSnapshot.getValue().equals(user.getUid()))){
                        Toasty.info(CallActivity.this,"User ended this call",Toast.LENGTH_SHORT).show();
                        player.stopRingtone();
                        onCallReference.child(user.getUid()).child("onCall").setValue("false");
                        finish();
                    }else if (dataSnapshot.getValue().equals(user.getUid())){

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            currentImageReference.child(fromUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("image")) {
                        try {
                            final String image = dataSnapshot.child("image").getValue().toString();
                            tvName.setText(dataSnapshot.child("name").getValue().toString());
                            if (!image.equals("default")) {
                                Picasso.get().load(image).placeholder(R.drawable.userprofile)
                                        .networkPolicy(NetworkPolicy.OFFLINE).into(userImage, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Picasso.get().load(image).placeholder(R.drawable.userprofile).into(userImage);
                                    }
                                });
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isConnected=true;
                    player.stopRingtone();
                    mReceiverSession=new Session.Builder(getApplicationContext(),receiverApiKey,receiverSessionId).build();
                    mReceiverSession.setSessionListener(CallActivity.this);
                    mReceiverSession.setConnectionListener(CallActivity.this);
                    mReceiverSession.setReconnectionListener(CallActivity.this);
                    mReceiverSession.connect(receiverUserToken);
                    btnAccept.setVisibility(View.INVISIBLE);
                }
            });
            btnEnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        isConnected=false;
                        player.stopRingtone();
                        mReceiverSession.disconnect();
                        mSession.disconnect();
                        mSession.unpublish(mPublisher);
                        mReceiverSession.unpublish(mPublisher);
                        mSession.unsubscribe(mSubscriber);
                        mReceiverSession.unsubscribe(mSubscriber);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    userRejectedCallReference.child(fromUserId).push().child("from").setValue(user.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
                    finish();
                }
            });

        }

    }

    @Override
    protected void onResume() {
        super.onResume();


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mHandler);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mHandler2);
        if (onCallReference!=null) {
            onCallReference.child(user.getUid()).child("onCall").setValue("false");
        }
        try {
            if (mSession!=null) {
                mSession.disconnect();
                mSession.unsubscribe(mSubscriber);
                mSession.unpublish(mPublisher);
            }
            if (mReceiverSession!=null) {
                mReceiverSession.disconnect();
                mReceiverSession.unsubscribe(mSubscriber);
                mReceiverSession.unpublish(mPublisher);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    @Override
    public void onConnected(Session session) {
        AudioDeviceManager.getAudioDevice().setOutputMode(
                BaseAudioDevice.OutputMode.Handset);
        mPublisher = new Publisher.Builder(this).audioTrack(true).videoTrack(false).build();
        mPublisher.setPublisherListener(this);
        if (currentUserToken!=null){
            mSession.publish(mPublisher);
            onCallReference.child(user.getUid()).child("onCall").setValue(calledUserKey);
        }

        if (getIntent().hasExtra("to_user_token")){
            mReceiverSession.publish(mPublisher);
            onCallReference.child(user.getUid()).child("onCall").setValue(fromUserId);
        }



    }

    @Override
    public void onDisconnected(Session session) {
        isConnected=false;
        onCallReference.child(user.getUid()).child("onCall").setValue("false");
        finish();
    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {
        if (mSubscriber == null) {
            mSubscriber = new Subscriber.Builder(this, stream).build();
            mSubscriber.setSubscriberListener(CallActivity.this);
            if (currentUserToken!=null) {
                player.stopProgressTone();
                isConnected=true;
                tvState.setText("Connected");
                mSession.subscribe(mSubscriber);
            }
            if (getIntent().hasExtra("to_user_token")){
                player.stopRingtone();
                isConnected=true;
                tvState.setText("Connected");
                mReceiverSession.subscribe(mSubscriber);
            }

        }

    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        if (mSubscriber != null) {
            mSubscriber = null;
            isConnected=false;
            onCallReference.child(user.getUid()).child("onCall").setValue("false");
            finish();
        }
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {
        onCallReference.child(user.getUid()).child("onCall").setValue("false");
        finish();
    }


    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {
        isConnected=false;
        onCallReference.child(user.getUid()).child("onCall").setValue("false");
        finish();

    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {
        onCallReference.child(user.getUid()).child("onCall").setValue("false");
    }
    @Override
    public void onConnectionCreated(Session session, Connection connection)
    {
        // New client connected to the session
        mPublisher = new Publisher.Builder(this).videoTrack(false).build();
        mPublisher.setPublisherListener(this);
        if (currentUserToken!=null) {
            isConnected=true;
            mSession.publish(mPublisher);
            onCallReference.child(user.getUid()).child("onCall").setValue(calledUserKey);
        }
        if (getIntent().hasExtra("to_user_token")){
            isConnected=true;
            mReceiverSession.publish(mPublisher);
            onCallReference.child(user.getUid()).child("onCall").setValue(fromUserId);
        }

    }

    @Override
    public void onConnectionDestroyed(Session session, Connection connection)
    {
        // A client disconnected from the session
        isConnected=false;
        onCallReference.child(user.getUid()).child("onCall").setValue("false");
        finish();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onReconnecting(Session session) {

    }

    @Override
    public void onReconnected(Session session) {

    }

    @Override
    public void onConnected(SubscriberKit subscriberKit) {

    }

    @Override
    public void onDisconnected(SubscriberKit subscriberKit) {
        onCallReference.child(user.getUid()).child("onCall").setValue("false");
        finish();
    }

    @Override
    public void onError(SubscriberKit subscriberKit, OpentokError opentokError) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onBackPressed() {
        player.stopProgressTone();
        player.stopRingtone();
        if (currentUserToken!=null){
            userRejectedCallReference.child(calledUserKey).push().child("from").setValue(user.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            });
        }
        if (getIntent().hasExtra("to_user_token")){
            userRejectedCallReference.child(fromUserId).push().child("from").setValue(user.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            });
        }
        onCallReference.child(calledUserKey).child("onCall").setValue("false");
        finish();
        super.onBackPressed();
    }

}
