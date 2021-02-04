package com.zalj.schedule.MyNotifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.zalj.schedule.Activity.SettingsActivity;
import com.zalj.schedule.R;
import com.zalj.schedule.VersionControl.Version;


public class UpdateNotification extends MyNotification{
    private Version version;

    public UpdateNotification(Context context, Version version){
        super(context);
        this.notificationId = NotificationHelper.NOTIFICATION_ID_UPDATE;
        this.version = version;
    }

    @Override
    public String getTitle() {
        return context.getString(R.string.Notification_Update_Title);
    }

    @Override
    public String getMessage(){
        StringBuilder string = new StringBuilder();

        string.append(context.getString(R.string.Notification_Update_Params_Version) + " ").append(version.getVersionName() + "\n");
        return string.toString();
    }

    @Override
    public String getChanelId() {
        return NotificationHelper.CHANEL_ID_UPDATE;
    }

    @Override
    public String getChanelName() {
        return context.getString(R.string.Notification_chanelName_Update);
    }

    @Override
    public PendingIntent getActivityToShow() {
        Intent intent = new Intent(context, SettingsActivity.class);
        return PendingIntent.getActivity(context, 0, intent, 0);
    }
}
