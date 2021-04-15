package com.example.websocketsample.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.websocketsample.MainActivity;
import com.example.websocketsample.R;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseNotificationService extends FirebaseMessagingService {
    private static final String TAG = "FCM";
    private static final String CHANNEL = String.valueOf(R.string.default_notification_channel_id);

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        buildNotification(remoteMessage);
    }

    public void buildNotification(RemoteMessage remoteMessage) {
        Log.d(TAG, remoteMessage.getFrom());

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent
                = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL,
                    "app",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL);
        builder.setContentTitle(remoteMessage.getNotification().getTitle());
        builder.setContentText(remoteMessage.getNotification().getBody());
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        builder.setContentIntent(pendingIntent);
        builder.setDefaults(Notification.DEFAULT_ALL);

        notificationManager.notify(0, builder.build());
    }
}
