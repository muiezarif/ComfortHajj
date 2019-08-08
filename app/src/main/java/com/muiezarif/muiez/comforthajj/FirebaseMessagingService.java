package com.muiezarif.muiez.comforthajj;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.renderscript.RenderScript;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.muiezarif.muiez.comforthajj.usersignedin.UserSignedIn;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.RemoteMessage;


public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    String notificationTitle;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        try {
            FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
                reference.child("device_token").setValue(s).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData().size()>0){
            String notificationid=remoteMessage.getData().get("id");
            if (notificationid!=null){
                if (notificationid.equals("Call")){
                    String fromUserId = remoteMessage.getData().get("from_user_id");
                    String api_key = remoteMessage.getData().get("api_key");
                    String to_user_token = remoteMessage.getData().get("to_user_token");
                    String sessionid = remoteMessage.getData().get("session_id");
                    Intent intent=new Intent(getApplicationContext(),CallActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    intent.putExtra("to_user_token",to_user_token);
                    intent.putExtra("from_user_id",fromUserId);
                    intent.putExtra("api_key",api_key);
                    intent.putExtra("session_id",sessionid);
                    startActivity(intent);
                }
            }else{
                notificationTitle=remoteMessage.getNotification().getTitle();
            }

        }


            if (notificationTitle!=null) {
                if (notificationTitle.equals("Rejected")) {
                    String from_user_id = remoteMessage.getData().get("from_user_id");
                    Intent foregroundIntent = new Intent("com.muiezarif.muiez.comforthajj.FCMMSGREJECT");
                    foregroundIntent.putExtra("title", notificationTitle);
                    foregroundIntent.putExtra("fromUserId", notificationTitle);
                    LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
                    broadcastManager.sendBroadcast(foregroundIntent);
                } else if (notificationTitle.equals("calling")) {
                    String api_key = remoteMessage.getData().get("apikey");
                    String from_user_token = remoteMessage.getData().get("fromusertoken");
                    String sessionid = remoteMessage.getData().get("sessionid");
                    remoteMessage.getData().size();
                    Intent foregroundIntent = new Intent("com.muiezarif.muiez.comforthajj.FCMMSGG");
                    foregroundIntent.putExtra("title", notificationTitle);
                    foregroundIntent.putExtra("apiKey", api_key);
                    foregroundIntent.putExtra("fromUserToken", from_user_token);
                    foregroundIntent.putExtra("sessionID", sessionid);
                    LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
                    broadcastManager.sendBroadcast(foregroundIntent);
                } else if (notificationTitle.equals("Call")) {
                    String notificationMessage = remoteMessage.getNotification().getBody();
                    String click_action = remoteMessage.getNotification().getClickAction();
                    String fromUserId = remoteMessage.getData().get("from_user_id");
                    String api_key = remoteMessage.getData().get("api_key");
                    String to_user_token = remoteMessage.getData().get("to_user_token");
                    String sessionid = remoteMessage.getData().get("session_id");
                    remoteMessage.getData().size();
                    Intent foregroundIntent = new Intent("com.muiezarif.muiez.comforthajj.FCMMSG");
                    foregroundIntent.putExtra("title", notificationTitle);
                    foregroundIntent.putExtra("message", notificationMessage);
                    foregroundIntent.putExtra("fromUserId", fromUserId);
                    foregroundIntent.putExtra("apiKey", api_key);
                    foregroundIntent.putExtra("toUserToken", to_user_token);
                    foregroundIntent.putExtra("sessionID", sessionid);
                    LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
                    broadcastManager.sendBroadcast(foregroundIntent);
                    RemoteViews collapse = new RemoteViews(getPackageName(), R.layout.custom_tost_view);
                    collapse.setTextViewText(R.id.toast_title, notificationTitle);
                    collapse.setTextViewText(R.id.toast_msg, notificationMessage);
                    Intent intent = new Intent(click_action);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("fromuserid", fromUserId);
                    intent.putExtra("apikey", api_key);
                    intent.putExtra("tousertoken", to_user_token);
                    intent.putExtra("sessionid", sessionid);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                            .setSmallIcon(R.drawable.hajj)
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setContentTitle(notificationTitle)
                            .setContentText(notificationMessage)
                            .setVibrate(new long[]{1000, 1000, 1000})
                            .setLights(Color.RED, 3000, 3000)
                            .setContentIntent(pendingIntent);
                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    int notificatidId = (int) System.currentTimeMillis();
                    manager.notify(notificatidId, builder.build());

            }else {
                    String notificationMessage = remoteMessage.getNotification().getBody();
                    String click_action = remoteMessage.getNotification().getClickAction();
                    String fromUserId = remoteMessage.getData().get("from_user_id");
                    remoteMessage.getData().size();
                    Intent foregroundIntent = new Intent("com.muiezarif.muiez.comforthajj.FCMMSG");
                    foregroundIntent.putExtra("title", notificationTitle);
                    foregroundIntent.putExtra("message", notificationMessage);
                    foregroundIntent.putExtra("fromUserId", fromUserId);
                    LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
                    broadcastManager.sendBroadcast(foregroundIntent);
                    RemoteViews collapse = new RemoteViews(getPackageName(), R.layout.custom_tost_view);
                    collapse.setTextViewText(R.id.toast_title, notificationTitle);
                    collapse.setTextViewText(R.id.toast_msg, notificationMessage);
                    Intent intent = new Intent(click_action);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("key", fromUserId);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                            .setSmallIcon(R.drawable.hajj)
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setContentTitle(notificationTitle)
                            .setContentText(notificationMessage)
                            .setVibrate(new long[]{1000, 1000})
                            .setLights(Color.BLUE, 3000, 3000)
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            .setContentIntent(pendingIntent);

                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    int notificatidId = (int) System.currentTimeMillis();
                    manager.notify(notificatidId, builder.build());
                }
            }



        }
    }


