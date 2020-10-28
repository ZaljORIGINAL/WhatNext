package com.zalj.schedule.MyNotifications;

import android.media.RingtoneManager;
import android.net.Uri;

public class NotificationHelper {

    public static Uri getSound(){

        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    }

    public static long[] getVibrate(){
        return new long[]{400,800,400,800};
    }
}
