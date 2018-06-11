package com.studder.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.studder.ChatActivity;
import com.studder.NavigationActivity;
import com.studder.R;
import com.studder.database.schema.UserMatchTable;
import com.studder.database.schema.UserTable;
import com.studder.model.Media;

import java.util.Random;

import static android.util.Base64.*;

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

        if(remoteMessage.getData().get("type").equals("message")){
            /*byte[] bitmapBytes = decode(remoteMessage.getData().get("image"), DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            bmp = bmp.createScaledBitmap(bmp, 100, 100, false);*/

            Intent chatActivityIntent = new Intent(getApplicationContext(), ChatActivity.class);
            chatActivityIntent.putExtra(UserMatchTable.Cols._ID, remoteMessage.getData().get("matchId"));
            chatActivityIntent.putExtra(UserTable.Cols._ID, remoteMessage.getData().get("userId"));

            PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),0,chatActivityIntent,0);

            notificationBuilder.setSmallIcon(R.drawable.ic_notifications_black_24dp)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getData().get("message"))
                    .addAction(-1,"Reply",pendingIntent)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri);

            mNotificationManager.notify(notificationId, notificationBuilder.build());

            NotificationManager notif = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notif.notify(notificationId, notificationBuilder.build());
        } else if(remoteMessage.getData().get("type").equals("match")){
            Intent navigationActivityIntent = new Intent(getApplicationContext(), NavigationActivity.class);
            navigationActivityIntent.putExtra(UserMatchTable.Cols._ID, remoteMessage.getData().get("matchId"));
            navigationActivityIntent.putExtra(UserTable.Cols._ID, remoteMessage.getData().get("userId"));

            PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),0,navigationActivityIntent,0);

            notificationBuilder.setSmallIcon(R.drawable.ic_notifications_black_24dp)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                    .addAction(-1,"See match",pendingIntent)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri);

            mNotificationManager.notify(notificationId, notificationBuilder.build());

            NotificationManager notif = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notif.notify(notificationId, notificationBuilder.build());
        } else {
            notificationBuilder.setSmallIcon(R.drawable.ic_notifications_black_24dp)
                    .setContentTitle(remoteMessage.getData().get("title"))
                    .setContentText(remoteMessage.getData().get("message"))
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri);

            mNotificationManager.notify(notificationId, notificationBuilder.build());

            NotificationManager notif = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notif.notify(notificationId, notificationBuilder.build());
        }
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
