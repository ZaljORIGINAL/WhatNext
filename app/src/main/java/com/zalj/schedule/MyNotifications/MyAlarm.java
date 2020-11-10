package com.zalj.schedule.MyNotifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.zalj.schedule.Data.DataContract;
import com.zalj.schedule.IntentHelper;
import com.zalj.schedule.Objects.Schedule;
import com.zalj.schedule.Objects.ScheduleBuilder;
import com.zalj.schedule.R;

import java.util.Calendar;

public class MyAlarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int idCase = intent.getIntExtra(
                IntentHelper.COMMAND,
                IntentHelper.COMMAND_NOTIFICATION_UpdateAlarmToDay);

        Log.i("Notification", "СРАБОТАЛ БУДИЛЬНИК. idCase = " + idCase);
        switch (idCase){
            case IntentHelper.COMMAND_NOTIFICATION_SetAlarm:{
                showNotification(context ,intent);
            }break;

            case IntentHelper.COMMAND_NOTIFICATION_UpdateAlarmToDay:{
                updateAlarmsToNextDay(context, intent);
            }break;
        }
    }

    private void showNotification(Context context, Intent intent){
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        String title;
        String message;
        String chanelId;
        String chanelNameDiscipline;
        int notificationId;

        title = intent.getStringExtra(IntentHelper.NOTIFICATION_TITLE);
        message = intent.getStringExtra(IntentHelper.NOTIFICATION_MESSAGE);
        chanelId = intent.getStringExtra(IntentHelper.CHANEL_ID);
        chanelNameDiscipline = intent.getStringExtra(IntentHelper.CHANEL_NAME);
        notificationId = intent.getIntExtra(IntentHelper.NOTIFICATION_ID, 0);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, chanelId);
        notification
                .setSmallIcon(R.drawable.discipline_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(NotificationHelper.getSound())
                .setVibrate(NotificationHelper.getVibrate(NotificationHelper.LONG_VIBRATE))
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel nc =
                    new NotificationChannel(
                            chanelId,
                            chanelNameDiscipline,
                            NotificationManager.IMPORTANCE_DEFAULT);

            notificationManager.createNotificationChannel(nc);
        }

        notificationManager.notify(notificationId, notification.build());
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

        MyDisciplineNotificationManager.updateAllAlarm(context, schedule);
        Log.i("Notification", "Уведомления обнавлены!");
    }
}
