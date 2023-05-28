package com.example.projectandroid;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        String title = remoteMessage.getNotification().getTitle();
        String detail = remoteMessage.getNotification().getBody();
        final String CHANNEL_ID = "notify_header";

        NotificationChannel channel = new NotificationChannel(
            CHANNEL_ID, "HEAD_NOTIFICATION", NotificationManager.IMPORTANCE_HIGH
        );
        getSystemService(NotificationManager.class).createNotificationChannel(channel);

        Notification.Builder builder = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(detail)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true);

        NotificationManagerCompat.from(this).notify(1, builder.build());

        super.onMessageReceived(remoteMessage);
    }

    @Override
    public void onMessageSent(@NonNull String s) {
        super.onMessageSent(s);
    }
}
