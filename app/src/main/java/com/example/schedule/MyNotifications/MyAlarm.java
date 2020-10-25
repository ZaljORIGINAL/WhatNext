package com.example.schedule.MyNotifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.schedule.IntentHelper;
import com.example.schedule.Objects.Schedule;
import com.example.schedule.R;

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

        //TODO Сделать маленькую иконку.
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, chanelId);
        notification
                .setSmallIcon(R.drawable.discipline_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVibrate(new long[]
                        {
                                0, 400, 200, 400
                        })
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
        MyDisciplineNotificationManager disciplineNotificationManager;

        schedule = intent.getParcelableExtra(IntentHelper.SCHEDULE);
        disciplineNotificationManager = MyDisciplineNotificationManager.getInstance(context, schedule);

        disciplineNotificationManager.updateAllAlarm();
        Log.i("Notification", "Уведомления обнавлены!");
    }
}
