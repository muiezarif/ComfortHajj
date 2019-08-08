package com.muiezarif.muiez.comforthajj;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Query;
import com.firebase.client.ServerValue;
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
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class SingleUserProfileActivity extends AppCompatActivity {
    CircleImageView userImage;
    Button link,cancelLink;
    TextView userName,userCurrentStatus;
    DatabaseReference reference;
    DatabaseReference connectRequestReference;
    DatabaseReference connectMessageReference;
    DatabaseReference connectedReference;
    DatabaseReference notificationReference;
    DatabaseReference presenceReference;
    FirebaseUser user;
    String currentState;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_user_profile);

        LocalBroadcastManager.getInstance(this).registerReceiver(mHandler,new IntentFilter("com.muiezarif.muiez.comforthajj.FCMMSG"));

        inIt();
        currentState="not_friends";
        Intent intent=getIntent();
        if (intent.getExtras()!=null) {
            key = getIntent().getStringExtra("key");
        }
        reference= FirebaseDatabase.getInstance().getReference().child("users").child(key);
        connectRequestReference=FirebaseDatabase.getInstance().getReference().child("connect_request");
        connectMessageReference=FirebaseDatabase.getInstance().getReference().child("messages");
        user= FirebaseAuth.getInstance().getCurrentUser();
        presenceReference=FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        connectedReference=FirebaseDatabase.getInstance().getReference().child("connected_users");
        notificationReference=FirebaseDatabase.getInstance().getReference().child("notifications");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String displayName=dataSnapshot.child("name").getValue().toString();
                String displayStatus=dataSnapshot.child("profile_status").getValue().toString();
                String displayImage=dataSnapshot.child("image").getValue().toString();
                userName.setText(displayName.toUpperCase());
                userCurrentStatus.setText(displayStatus.toLowerCase());
                cancelLink.setVisibility(View.INVISIBLE);
                cancelLink.setEnabled(false);
                Picasso.get().load(displayImage).placeholder(R.drawable.userprofile).into(userImage);
                connectRequestReference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(key)){
                            String req_type=dataSnapshot.child(key).child("request_type").getValue().toString();
                            if (req_type.equals("received")){
                                currentState="req_received";
                                link.setText("Accept Connection Request");
                                cancelLink.setVisibility(View.VISIBLE);
                                cancelLink.setEnabled(true);
                            }else if(req_type.equals("sent")){
                                currentState="req_sent";
                                link.setText("Cancel Connection Request");
                                cancelLink.setVisibility(View.INVISIBLE);
                                cancelLink.setEnabled(false);
                            }
                        }else{

                            connectedReference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(key)){
                                        currentState="friends";
                                        link.setText("Cancel Connection");
                                        cancelLink.setVisibility(View.INVISIBLE);
                                        cancelLink.setEnabled(false);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                link.setEnabled(false);
                if (currentState.equals("not_friends")){
                    connectRequestReference.child(user.getUid()).child(key).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                connectRequestReference.child(key)
                                        .child(user.getUid())
                                        .child("request_type")
                                        .setValue("received")
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        HashMap<String ,String > data=new HashMap<>();
                                        data.put("from",user.getUid());
                                        data.put("type","request");
                                        currentState="req_sent";
                                        link.setText("Cancel Connection Request");
                                        cancelLink.setVisibility(View.INVISIBLE);
                                        cancelLink.setEnabled(false);
                                        notificationReference.child(key).push().setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toasty.success(SingleUserProfileActivity.this, "Request sent", Toast.LENGTH_SHORT).show();
                                                }else{

                                                }
                                            }
                                        });
                                        Toasty.success(SingleUserProfileActivity.this, "Request sent", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else{
                                Toasty.error(SingleUserProfileActivity.this, "Error sending request.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else if(currentState.equals("req_sent")){
                    connectRequestReference.child(user.getUid()).child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            connectRequestReference.child(key).child(user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    link.setEnabled(true);
                                    currentState="req_cancelled";
                                    link.setText("Connect");
                                    cancelLink.setVisibility(View.INVISIBLE);
                                    cancelLink.setEnabled(false);
                                    Toasty.success(SingleUserProfileActivity.this, "Request Cancelled", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }else if(currentState.equals("req_received")){
                    final String currentDate= DateFormat.getDateTimeInstance().format(new Date());
                    connectedReference.child(user.getUid()).child(key).child("date").setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            connectedReference.child(key).child(user.getUid()).child("date").setValue(currentDate)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    connectRequestReference.child(user.getUid()).child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            connectRequestReference.child(key).child(user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    link.setEnabled(true);
                                                    currentState="friends";
                                                    link.setText("Cancel Connection");
                                                    cancelLink.setVisibility(View.INVISIBLE);
                                                    cancelLink.setEnabled(false);
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });
                }else if(currentState.equals("friends")){
                    connectedReference.child(user.getUid()).child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            connectedReference.child(key).child(user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    link.setEnabled(true);
                                    currentState="not_friends";
                                    link.setText("Connect");
                                    cancelLink.setVisibility(View.INVISIBLE);
                                    cancelLink.setEnabled(false);
                                    connectMessageReference.child(key).child(user.getUid()).removeValue();
                                    connectMessageReference.child(user.getUid()).child(key).removeValue();
                                    Toasty.success(SingleUserProfileActivity.this, "Cancelled connection", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    });
                }
                link.setEnabled(true);
            }
        });
        cancelLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectRequestReference.child(user.getUid()).child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        connectRequestReference.child(key).child(user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                link.setEnabled(true);
                                currentState="req_cancelled";
                                link.setText("Connect");
                                cancelLink.setVisibility(View.INVISIBLE);
                                cancelLink.setEnabled(false);
                                Toasty.success(SingleUserProfileActivity.this, "Request Cancelled", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
    }
    public void inIt(){
        userImage=findViewById(R.id.single_profile_image);
        link=findViewById(R.id.send_link_request);
        userName=findViewById(R.id.display_name_single_user);
        userCurrentStatus=findViewById(R.id.user_status_current);
        cancelLink=findViewById(R.id.cancel_link_request);
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        if(user!=null) {
//            presenceReference.child("online").setValue("true");
//        }
//    }

    @Override
    public void onResume() {
        super.onResume();
        if(user!=null) {
            presenceReference.child("online").setValue("true");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(user!=null) {
            presenceReference.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        if(user!=null) {
//            presenceReference.child("online").setValue(ServerValue.TIMESTAMP);
//            LocalBroadcastManager.getInstance(this).unregisterReceiver(mHandler);
//        }
//    }
    private BroadcastReceiver mHandler=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String name=intent.getStringExtra("title");
            String msg=intent.getStringExtra("message");
            String userId=intent.getStringExtra("fromUserId");
            Toasty.info(SingleUserProfileActivity.this,name+"\n"+msg,Toast.LENGTH_LONG).show();
        }
    };
}
