package com.fireblaze.evento;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyMessagingService extends FirebaseMessagingService {
    public final static String TAG = MyMessagingService.class.getSimpleName();
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        //Get the notification object from the received message
        RemoteMessage.Notification notification = remoteMessage.getNotification();

        //Create a notification builder to show notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.analytics_icon)
                .setAutoCancel(true)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody());

        String UID = remoteMessage.getData().get(Constants.ORGANIZER_KEYWORD);
        Intent resultIntent = new Intent(this,EventListActivity.class);
        Bundle b = new Bundle();
        b.putString(EventListActivity.ID_KEYWORD,UID);
        resultIntent.putExtras(b);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.analytics_icon,"Visit page",resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,builder.build());
        Log.d(TAG, "onMessageReceived: "+ remoteMessage.toString());
    }
}
