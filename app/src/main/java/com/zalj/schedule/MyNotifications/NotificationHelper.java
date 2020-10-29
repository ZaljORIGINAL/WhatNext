package com.zalj.schedule.MyNotifications;

import android.media.RingtoneManager;
import android.net.Uri;

public class NotificationHelper {

    //Vibration
    public static final int SHORT_VIBRATE = 0;
    public static final int MEDIUM_VIBRATE = 1;
    public static final int LONG_VIBRATE = 2;
    public static final int MAX_VIBRATE = 3;

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
}
