package com.zalj.schedule.MyNotifications;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;

import com.zalj.schedule.Activity.MainActivity;
import com.zalj.schedule.IntentHelper;
import com.zalj.schedule.Objects.Discipline;
import com.zalj.schedule.R;

import java.io.Serializable;

import static android.content.Context.ALARM_SERVICE;

public abstract class DisciplineNotification extends MyNotification implements AlarmController, Serializable {
    protected DisciplineNotificationManager.Options options;
    protected Discipline discipline;

    public DisciplineNotification(Context context, Discipline discipline, DisciplineNotificationManager.Options options){
        super(context);
        this.discipline = discipline;
        this.options = options;
        setNotificationId();
    }

    @Override
    public String getTitle() {
        String[] types = context.getResources().getStringArray(R.array.type_of_discipline);
        return "(" + types[discipline.getType()] + ") " + discipline.getDisciplineName();
    }

    @Override
    public String getMessage() {
        return context.getString(R.string.Notification_Title);
    }

    @Override
    public String getChanelId() {
        return NotificationHelper.CHANEL_ID_DISCIPLINE;
    }

    @Override
    public String getChanelName() {
        return context.getString(R.string.Notification_chanelName_Discipline);
    }

    protected String getOtherDescription(){
        StringBuilder string = new StringBuilder();

        //Class room
        if (!(TextUtils.isEmpty(discipline.getAuditorium()) || discipline.getAuditorium().equals(""))){
            string
                    .append(context.getString(R.string.activity_ScheduleOfDay_dialog_auditory))
                    .append(": ")
                    .append(discipline.getAuditorium())
                    .append("\n");
        }

        //Building
        if (!(TextUtils.isEmpty(discipline.getBuilding()) || discipline.getBuilding().equals(""))){
            string
                    .append(context.getString(R.string.activity_ScheduleOfDay_dialog_building))
                    .append(": ")
                    .append(discipline.getBuilding());
        }

        return string.toString();
    }

    protected abstract long getTriggerTime();

    @Override
    public void setAlarm() {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        //Идентификатор действия
        intent.setAction(AlarmReceiver.SHOW_NOTIFICATION);
        //Устанавливаем команду
        intent.putExtra(
                IntentHelper.COMMAND,
                IntentHelper.COMMAND_NOTIFICATION_SetAlarm);
        //Передача уведомления
        intent.putExtra(IntentHelper.NOTIFICATION, this);

        PendingIntent pIntent  =
                PendingIntent.getBroadcast(
                        context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        manager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                getTriggerTime(),
                pIntent);
    }

    @Override
    public void deleteAlarm() {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        PendingIntent pIntent  =
                PendingIntent.getBroadcast(
                        context,
                        getNotificationId(),
                        new Intent(),
                        PendingIntent.FLAG_CANCEL_CURRENT);

        alarmManager.cancel(pIntent);
    }

    public void setNotificationId(){
        this.notificationId = -10;
    }
}
