package com.example.chat.Notifications;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;
import androidx.core.graphics.drawable.IconCompat;

import com.example.chat.Activity.GroupMessageActivity;
import com.example.chat.Activity.MainActivity;
import com.example.chat.Activity.MessengersActivity;
import com.example.chat.Model.NotificationChannal;
import com.example.chat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    private static String KEY_TEXT_REPLY = "key_text_reply";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser.getUid() != null) {
            updateToken(s);
        }
    }

    private void updateToken(String newtoken) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(newtoken);
        if (firebaseUser.getUid() != null) {
            databaseReference.child(firebaseUser.getUid()).setValue(token);
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String sented = remoteMessage.getData().get("sented");
        String user = remoteMessage.getData().get("user");

        SharedPreferences preferences = getSharedPreferences("PREPS", MODE_PRIVATE);
        String currentUser = preferences.getString("currentuser", "none");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null && sented.equals(firebaseUser.getUid())) {
            if (!currentUser.equals(user)) {
                sendNotification(remoteMessage);
            }
        }
    }

    private void sendNotification(RemoteMessage remoteMessage) {

        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String Pl = remoteMessage.getData().get("PL");
        String sented = remoteMessage.getData().get("sented");

        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));

        switch (Pl) {
            case "0":
                Intent intent = new Intent(this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("IdUser", user);
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);
                Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NotificationChannal.CHANNEL_ID)
                        .setSmallIcon(Integer.parseInt(icon))
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.addfriend))
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(body))
                        .setSound(defaultSound)
                        .setContentIntent(pendingIntent);
                NotificationManager noti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                int i = 0;
                if (j > 0) {
                    i = j;
                }
                noti.notify(i, builder.build());
                break;
            case "1":
                Intent intent1 = new Intent(this, MessengersActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putString("IdUser", user);
                intent1.putExtras(bundle1);
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent1 = PendingIntent.getActivity(this, j, intent1, PendingIntent.FLAG_ONE_SHOT);
                Uri defaultSound1 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder builder1 = new NotificationCompat.Builder(this, NotificationChannal.CHANNEL_ID)
                        .setSmallIcon(Integer.parseInt(icon))
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.messenger))
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(body))
                        .setSound(defaultSound1)
                        .setContentIntent(pendingIntent1);
                NotificationManager noti1 = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                int i1 = 0;
                if (j > 0) {
                    i1 = j;
                }
                noti1.notify(i1, builder1.build());
                break;
        }
    }
}
