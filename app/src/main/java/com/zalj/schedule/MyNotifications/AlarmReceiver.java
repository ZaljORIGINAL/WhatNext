package com.zalj.schedule.MyNotifications;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.zalj.schedule.Data.DataContract;
import com.zalj.schedule.IntentHelper;
import com.zalj.schedule.Objects.Schedule;
import com.zalj.schedule.Objects.ScheduleBuilder;
import com.zalj.schedule.R;
import com.zalj.schedule.VersionControl.CallBack;
import com.zalj.schedule.VersionControl.Version;
import com.zalj.schedule.VersionControl.VersionManager;
import com.zalj.schedule.VersionControl.Exceptions.VersionNotReceivedException;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String SHOW_NOTIFICATION = "com.zalj.schedule.NOTIFICATION_DISCIPLINE";
    public static final String UPDATE_ALARM = "com.zalj.schedule.UPDATE_ALARM";

    @Override
    public void onReceive(Context context, Intent intent) {
        int idCase = intent.getIntExtra(
                IntentHelper.COMMAND,
                IntentHelper.COMMAND_NOTIFICATION_UpdateAppData);

        Log.i("Notification", "СРАБОТАЛ БУДИЛЬНИК. idCase = " + idCase);
        switch (idCase){
            case IntentHelper.COMMAND_NOTIFICATION_SetAlarm:{
                MyNotification notification = intent.getParcelableExtra(IntentHelper.NOTIFICATION);
                showNotification(context, notification);
            }break;

            case IntentHelper.COMMAND_NOTIFICATION_UpdateAppData:{
                updateAlarmsToNextDay(context, intent);
                checkNewVersionApp(context);
            }break;
        }
    }

    private void showNotification(Context context, MyNotification notification){
        Notification notificationBuilder = new NotificationCompat.Builder(context, notification.getChanelId())
                .setSmallIcon(notification.getIcon())
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getMessage())
                .setStyle(new NotificationCompat.BigTextStyle())
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setSound(notification.getSound())
                .setVibrate(notification.getVibrate(NotificationHelper.LONG_VIBRATE))
                .setAutoCancel(true)
                .setContentIntent(notification.getActivityToShow())
                .build();

        notification.createNotificationChanel();

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(notification.getNotificationId(), notificationBuilder);
    }

    private void updateAlarmsToNextDay(Context context, Intent intent){
        Schedule schedule;

        Log.i("Notification", "Обновление расписаний на следующий день");
        String name = intent.getStringExtra(IntentHelper.SCHEDULE_NAME);

        if (name == null){
            SharedPreferences settings = context.getSharedPreferences(
                    DataContract.MyAppSettings.LAST_VIEWED_SCHEDULE,
                    Context.MODE_PRIVATE);

            name = settings.getString(
                    DataContract.MyAppSettings.LAST_SCHEDULE,
                    DataContract.MyAppSettings.NULL);
        }

        ScheduleBuilder scheduleBuilder = ScheduleBuilder.getInternalSchedule(context, name);
        schedule = scheduleBuilder.build();
        schedule.updateTimes(context);
        schedule.updateDiscipline(context, Calendar.getInstance());

        DisciplineNotificationManager.updateAllAlarm(context, schedule);
        Log.i("Notification", "Уведомления обнавлены!");
    }

    private void checkNewVersionApp(Context context){
        VersionManager versionManager = VersionManager.getInstance(context);
        versionManager.checkVersion((isActual, version) -> {
            try {
                version = versionManager.getVersion();
                UpdateNotification notification = new UpdateNotification(context, version);

                showNotification(context, notification);
            } catch (VersionNotReceivedException exception){

            }
        });
    }
}
