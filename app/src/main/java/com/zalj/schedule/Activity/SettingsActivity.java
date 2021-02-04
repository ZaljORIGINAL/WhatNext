package com.zalj.schedule.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zalj.schedule.BuildConfig;
import com.zalj.schedule.MyNotifications.NotificationHelper;
import com.zalj.schedule.MyNotifications.UpdateNotification;
import com.zalj.schedule.R;
import com.zalj.schedule.VersionControl.CallBack;
import com.zalj.schedule.VersionControl.Exceptions.NoMemoryException;
import com.zalj.schedule.VersionControl.VersionManager;

public class SettingsActivity extends AppCompatActivity {
    private VersionManager versionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Build activity
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        TextView appVersion = findViewById(R.id.AppVersion);
        appVersion.setText(
                getApplicationContext().getText(R.string.SettingsActivity_AppDescription_version) +
                        BuildConfig.VERSION_NAME);

        versionManager = VersionManager.getInstance();
        Button installVersion = findViewById(R.id.InstallVersion);
        installVersion.setOnClickListener(
                v -> {
                    try {
                        versionManager.updateApp(getApplicationContext());
                    }catch (NoMemoryException exception){
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getApplicationContext());
                        dialog.setTitle(R.string.Standard_Error);
                        dialog.setMessage(R.string.SettingsActivity_AppControl_install);
                        dialog.setPositiveButton(
                                R.string.Standard_dialog_positive_button,
                                (dialog1, which) -> {});
                    }
                }
        );
        versionManager.checkVersion(
                (isActual, version) -> {
                    installVersion.setEnabled(isActual);
                    //TODO вывести уведомление
                    UpdateNotification notification = new UpdateNotification(
                            getApplicationContext(),
                            version);

                    Notification notificationBuilder =
                            new NotificationCompat.Builder(
                                    getApplicationContext(),
                                    notification.getChanelId())
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

                    NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
                    manager.notify(notification.getNotificationId(), notificationBuilder);
                });

        Button vkCommunity = findViewById(R.id.linkToVK);
        vkCommunity.setOnClickListener(
                v -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/public187121726"));
                    startActivity(intent);
                });

        Button developerBlog = findViewById(R.id.linkToBlog);
        developerBlog.setOnClickListener(
                v -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
                    startActivity(intent);
                }
        );
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}