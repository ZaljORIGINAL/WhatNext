package com.zalj.schedule.MyNotifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import com.zalj.schedule.Activity.MainActivity;
import com.zalj.schedule.R;


public abstract class MyNotification {
    protected Context context;
    protected int notificationId;

    public MyNotification(
            Context context){
        this.context = context;
        setNotificationId();
    }

    public abstract String getTitle();

    public abstract String getMessage();

    public abstract String getChanelId();

    public abstract String getChanelName();

    public Context getContext() {
        return context;
    }

    public int getIcon(){
        return R.drawable.ic_launcher_foreground;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public Uri getSound(){
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    }

    //TODO Сделать что бы считывало из настроек приложения
    public long[] getVibrate(int type){
        switch (type){
            case NotificationHelper.SHORT_VIBRATE:
                return new long[]{0,400};

            case NotificationHelper.MEDIUM_VIBRATE:
                return new long[]{0,800};

            case NotificationHelper.LONG_VIBRATE:
                return new long[]{0,800, 400, 800};

            case NotificationHelper.MAX_VIBRATE:
                return new long[]{0,1200, 400, 1200};

            default:
                return new long[]{0,800, 400, 800};
        }
    }

    /**
     *         Intent intent = new Intent(context, MainActivity.class);
     *         return PendingIntent.getActivity(context, 0, intent, 0);*/
    public PendingIntent getActivityToShow(){
        Intent intent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(context, 0, intent, 0);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setNotificationId() {
        this.notificationId = -5;
    }

    public void createNotificationChanel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationManager manager = context.getSystemService(NotificationManager.class);

            NotificationChannel nc =
                    new NotificationChannel(
                            getChanelId(),
                            getChanelName(),
                            NotificationManager.IMPORTANCE_DEFAULT);

            manager.createNotificationChannel(nc);
        }
    }

}
