package com.example.schedule;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MyNotification
{
    private String
            title,
            massage,
            channelId;

    private int _ID;

    //Constructor
    public MyNotification(
            String title,
            String massage,
            int _ID,
            String channelId)
    {
        this.title = title;
        this.massage = massage;
        this._ID = _ID;
        this.channelId = channelId;
    }

    //Set notification
    public void createNotification(Context context)
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle(title)
                .setContentText(massage)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVibrate(new long[]
                        {
                                1000, 700, 1000, 700, 1000
                        })
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat nm = NotificationManagerCompat.from(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel nc = new NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_DEFAULT);
            nc.setDescription(channelId);
            nm.createNotificationChannel(nc);
        }
        nm.notify(_ID, builder.build());
    }

    public void change()
    {

    }
}
