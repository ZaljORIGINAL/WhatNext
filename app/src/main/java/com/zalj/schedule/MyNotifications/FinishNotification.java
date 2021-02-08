package com.zalj.schedule.MyNotifications;

import android.content.Context;

import com.zalj.schedule.Objects.Discipline;
import com.zalj.schedule.R;

import java.util.Calendar;

public class FinishNotification extends DisciplineNotification{
    public FinishNotification(Context context, Discipline discipline, DisciplineNotificationManager.Options options) {
        super(context, discipline, options);
    }

    @Override
    public String getMessage() {
        StringBuilder message = new StringBuilder();
        message
                .append(context.getString(R.string.Notification_Discipline_FinishOfDiscipline))
                .append("\n");
        message.append(getOtherDescription());

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

    /**
     * Формула установки ID для уведомлений:
     * (discipline.getPosition() + 1) * 10 + TYPE_OF_NOTIFICATION
     * Под значение TYPE_OF_NOTIFICATION подставляется идентификатор типа уведомления.
     * Идентификаторы:
     *      TIME_TO_GO = 0,
     *      BEFORE_START_OF_DISCIPLINE = 1,
     *      START_OF_DISCIPLINE = 2,
     *      BEFORE_FINISH_OF_DISCIPLINE = 3,
     *      FINISH_OF_DISCIPLINE = 4,
     *      FINISH_OF_DAY = 4*/
    @Override
    public void setNotificationId() {
        this.notificationId = (discipline.getPosition() + 1) * 10 + NotificationHelper.FINISH_OF_DISCIPLINE;
    }
}
