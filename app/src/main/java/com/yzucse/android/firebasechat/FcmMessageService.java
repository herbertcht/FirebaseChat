package com.yzucse.android.firebasechat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FcmMessageService extends FirebaseMessagingService {
    public static String TAG = "FCM-MessageService";
    NotificationChannel myChannel;
    String notificationTitle;
    String notificationBody;
    String notificationTag;  //record from who

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceived:" + remoteMessage + ", from:" + remoteMessage.getFrom() + ", data=" + remoteMessage.getData());

        notificationTitle = remoteMessage.getData().get("title");
        notificationBody = remoteMessage.getData().get("body");
        notificationTag = remoteMessage.getNotification().getTag();

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification title: " + notificationTitle);
            Log.d(TAG, "Message Notification Body: " + notificationBody);
        }

        changeFCMtoNotification(notificationTitle, notificationBody); //Display notification in notification bar
    }


    private void changeFCMtoNotification(String notifytitle, String notifybody) {
        Intent intent = new Intent(this, MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                //.setLargeIcon(Url2Bitmap(globalData.getmUser().getPhotoUrl()))
                .setContentTitle(notifytitle)
                //.setAutoCancel(true)
                .setContentText(notifybody)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notifybody))
                .setSound(defaultSoundUri)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            notificationBuilder.setChannelId("1111");
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            myChannel = new NotificationChannel("1111"
                    , "Notify Test"
                    , NotificationManager.IMPORTANCE_HIGH);
            myChannel.setShowBadge(true);
            myChannel.enableLights(true);
            notificationManager.createNotificationChannel(myChannel);
        }
        notificationManager.notify(notificationTag, 0 /* ID of notification */, notificationBuilder.build());
    }

    private Bitmap Url2Bitmap(String url) {
        try {
            URL u = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}