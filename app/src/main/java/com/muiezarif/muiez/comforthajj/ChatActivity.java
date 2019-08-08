package com.muiezarif.muiez.comforthajj;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;



import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class ChatActivity extends AppCompatActivity {
    private String chatUser;
    private String username;
    private TextView titleView;
    private TextView lastSeenView;
    private CircleImageView userImage;
    DatabaseReference presenceReference;
    EditText sendMessage;
    ImageButton send;
    DatabaseReference rootReference;
    DatabaseReference msgNotifyReference;
    FirebaseUser user;
    private RecyclerView messagesList;
    private final List<Messages> userMessagesList=new ArrayList<>();
    private LinearLayoutManager manager;
    private MessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatUser=getIntent().getStringExtra("key");
        username=getIntent().getStringExtra("name");
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rootReference= FirebaseDatabase.getInstance().getReference();
        msgNotifyReference=FirebaseDatabase.getInstance().getReference().child("message_notifications");
        user= FirebaseAuth.getInstance().getCurrentUser();
        presenceReference=FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        LayoutInflater inflater= (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView=inflater.inflate(R.layout.chat_custom_bar,null);
        getSupportActionBar().setCustomView(actionBarView);
        adapter=new MessageAdapter(userMessagesList);
        inIt();
        manager=new LinearLayoutManager(ChatActivity.this);
        messagesList.setHasFixedSize(true);
//        manager.setReverseLayout(true);
//        manager.setStackFromEnd(true);
        messagesList.setLayoutManager(manager);
        messagesList.setAdapter(adapter);
//        messagesList.scrollToPosition(messagesList.getAdapter().getItemCount()-1);
        loadMessages();
        titleView.setText(username);
        rootReference.child("users").child(chatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String online = dataSnapshot.child("online").getValue().toString();
                    if (online.equals("true")){
                        lastSeenView.setText("online");
                    } else {
                    GetTimeAgo getTimeAgo = new GetTimeAgo();
                    long lastTime = Long.valueOf(online);
                    String lastSeenTime = getTimeAgo.getTimeAgo(lastTime, getApplicationContext());
                    lastSeenView.setText(lastSeenTime);
                }
                final String image=dataSnapshot.child("thumb_image").getValue().toString();
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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        rootReference.child("chat").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(chatUser)){
                    Map chatAddMap=new HashMap();
                    chatAddMap.put("seen",false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);
                    Map chatUserMap=new HashMap();
                    chatUserMap.put("chat/"+user.getUid()+"/"+chatUser,chatAddMap);
                    chatUserMap.put("chat/"+chatUser+"/"+user.getUid(),chatAddMap);
                    rootReference.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError!=null){
                                Log.d("CHAT_LOG",databaseError.getMessage().toString());
                                Toasty.error(ChatActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
                msgnotify();
                sendMessage.setText("");
            }
        });
    }
    private void msgnotify(){
        msgNotifyReference.child(chatUser).push().child("from").setValue(user.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }

    private void sendMessage() {
        String message=sendMessage.getText().toString();
        if(!TextUtils.isEmpty(message)){
            String currentUserRef="messages/"+user.getUid()+"/"+chatUser;
            String chatUserRef="messages/"+chatUser+"/"+user.getUid();
            DatabaseReference user_message_push=rootReference.child("messages").child(user.getUid()).child(chatUser).push();
            String pushId=user_message_push.getKey();
            Map messageMap=new HashMap();
            messageMap.put("message",message);
            messageMap.put("seen",false);
            messageMap.put("type","text");
            messageMap.put("time",ServerValue.TIMESTAMP);
            messageMap.put("from",user.getUid());
            Map messageUserMap=new HashMap();
            messageUserMap.put(currentUserRef+"/"+pushId,messageMap);
            messageUserMap.put(chatUserRef+"/"+pushId,messageMap);
            rootReference.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError!=null){
                        Log.d("CHAT_LOG",databaseError.getMessage().toString());
                        Toasty.error(ChatActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    public void loadMessages(){
        rootReference.child("messages").child(user.getUid()).child(chatUser).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages messages=dataSnapshot.getValue(Messages.class);
                userMessagesList.add(messages);
                adapter.notifyDataSetChanged();
                messagesList.smoothScrollToPosition(messagesList.getAdapter().getItemCount());
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

    public void inIt(){
        titleView=findViewById(R.id.custom_display_name);
        lastSeenView=findViewById(R.id.custom_last_seen);
        userImage=findViewById(R.id.custom_bar_image);
        send=findViewById(R.id.img_btn_send);
        sendMessage=findViewById(R.id.send_message);
        messagesList=findViewById(R.id.messages_list);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if (id==android.R.id.home){
            this.finish();
        }
        return true;
    }
    @Override
    public void onStart() {
        super.onStart();
        if(user!=null) {
            presenceReference.child("online").setValue("true");
        }
    }

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
            presenceReference.child("online").setValue(com.firebase.client.ServerValue.TIMESTAMP);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(user!=null) {
            presenceReference.child("online").setValue(com.firebase.client.ServerValue.TIMESTAMP);
        }
    }
}
