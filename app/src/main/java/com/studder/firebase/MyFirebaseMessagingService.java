package com.studder.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.studder.R;

import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String TAG = "MyFirebaseMessagingService";

    private NotificationManager mNotificationManager;

    public static final String ADMIN_CHANNEL_ID = "12345";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            setupChannels();
        }

        int notificationId = new Random().nextInt(60000);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID);

        notificationBuilder.setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("message"))
                .setAutoCancel(true)
                .setSound(defaultSoundUri);

        mNotificationManager.notify(notificationId, notificationBuilder.build());

        NotificationManager notif = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notif.notify(notificationId, notificationBuilder.build());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(){
        CharSequence adminChannelName = getString(R.string.login_register_activity_success);
        String adminChannelDescription = getString(R.string.login_register_activity_success);

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_LOW);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(adminChannel);
        }
    }


}
