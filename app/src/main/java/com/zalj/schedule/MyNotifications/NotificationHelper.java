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
    //Notification class
    private static final int TIME_TO_GO = 0;
    private static final int BEFORE_START_OF_DISCIPLINE = 1;
    private static final int START_OF_DISCIPLINE = 2;
    private static final int BEFORE_FINISH_OF_DISCIPLINE = 3;
    private static final int FINISH_OF_DISCIPLINE = 4;
    private static final int FINISH_OF_DAY = 5;

    //Сhannels
    public static final String CHANEL_ID_DISCIPLINE = "NOTIFICATION_CHANEL_ID_DISCIPLINE";
    public static final String CHANEL_ID_UPDATE = "NOTIFICATION_CHANEL_ID_UPDATE";

    //Notification id
    public static final int NOTIFICATION_ID_DISCIPLINE = 10;
    public static final int NOTIFICATION_ID_UPDATE = 11;

    //Vibration
    public static final int SHORT_VIBRATE = 0;
    public static final int MEDIUM_VIBRATE = 1;
    public static final int LONG_VIBRATE = 2;
    public static final int MAX_VIBRATE = 3;

    //Activity to show
    /**Использовать если не требуется открыть активити по нажатию на уведомление*/
    public static final int DO_NOT_SHOW = 0;
    /**Использовать если требуется открыть активити с расписание на день*/
    public static final int SCHEDULE_TO_DAY = 1;
    /**Использовать если требуется открыть активити для обновления*/
    public static final int NEW_VERSION_DOWNLOAD = 2;
}
