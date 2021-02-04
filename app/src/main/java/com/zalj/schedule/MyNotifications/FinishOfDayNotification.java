package com.zalj.schedule.MyNotifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.zalj.schedule.Activity.MainActivity;
import com.zalj.schedule.Objects.Discipline;
import com.zalj.schedule.R;

import java.util.Calendar;

public class FinishOfDayNotification extends DisciplineNotification{

    public FinishOfDayNotification(Context context, Discipline discipline, DisciplineNotificationManager.Options options) {
        super(context, discipline, options);
    }

    /**
     * Формула установки ID для уведомлений:
     * (discipline.getPosition() + 1) * 10 + TYPE_OF_NOTIFICATION
     * Под значение TYPE_OF_NOTIFICATION подставляется идентификатор типа уведомления.
     * Идентификаторы:
     *      TIME_TO_GO = 0,
     *      BEFORE_START_OF_DISCIPLINE = 1,
     *      START_OF_DISCIPLINE = 2,
     *      BEFORE_FINISH_OF_DISCIPLINE = 3,
     *      FINISH_OF_DISCIPLINE/FINISH_OF_DAY = 4,*/
    @Override
    public void setNotificationId() {
        this.notificationId = (discipline.getPosition() + 1) * 10 + 4;
    }

    @Override
    public String getMessage() {
        StringBuilder message = new StringBuilder();
        message
                .append(R.string.Notification_Discipline_FinishOfDay);

        return message.toString();
    }

    @Override
    protected long getTriggerTime() {
        Calendar triggerTime = Calendar.getInstance();
        triggerTime.set(Calendar.HOUR_OF_DAY, discipline.getFinishHour());
        triggerTime.set(Calendar.MINUTE, discipline.getFinishMinute());
        triggerTime.set(Calendar.SECOND, 0);

        return triggerTime.getTimeInMillis();
    }
}
