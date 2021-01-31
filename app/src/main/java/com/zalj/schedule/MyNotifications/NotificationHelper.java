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

public class NotificationHelper {

    //Vibration
    public static final int SHORT_VIBRATE = 0;
    public static final int MEDIUM_VIBRATE = 1;
    public static final int LONG_VIBRATE = 2;
    public static final int MAX_VIBRATE = 3;

    public static void createNotificationChanel(
            Context context,
            String chanelId,
            String chanelNameDiscipline){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationManager manager = context.getSystemService(NotificationManager.class);

            NotificationChannel nc =
                    new NotificationChannel(
                            chanelId,
                            chanelNameDiscipline,
                            NotificationManager.IMPORTANCE_DEFAULT);

            manager.createNotificationChannel(nc);
        }
    }

    public static Uri getSound(){

        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    }

    public static long[] getVibrate(int type){
        switch (type){
            case SHORT_VIBRATE:
                return new long[]{0,400};

            case MEDIUM_VIBRATE:
                return new long[]{0,800};

            case LONG_VIBRATE:
                return new long[]{0,800, 400, 800};

            case MAX_VIBRATE:
                return new long[]{0,1200, 400, 1200};

            default:
                return new long[]{0,800, 400, 800};
        }
    }

    public static PendingIntent getActivityToStart(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        return pendingIntent;
    }
}
