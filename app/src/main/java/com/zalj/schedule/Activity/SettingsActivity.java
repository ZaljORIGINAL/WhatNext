package com.zalj.schedule.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.zalj.schedule.BuildConfig;
import com.zalj.schedule.Data.DataContract;
import com.zalj.schedule.MyNotifications.NotificationHelper;
import com.zalj.schedule.MyNotifications.UpdateNotification;
import com.zalj.schedule.R;
import com.zalj.schedule.VersionControl.CallBack;
import com.zalj.schedule.VersionControl.Exceptions.NoMemoryException;
import com.zalj.schedule.VersionControl.VersionManager;

import static com.zalj.schedule.Data.DataContract.MyAppSettings.PERMISSION_REQUEST_EXTERNAL_STORAGE;
import static com.zalj.schedule.Data.DataContract.MyAppSettings.PERMISSION_REQUEST_INSTALL_PACKAGES;

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

        versionManager = VersionManager.getInstance(this);
        Button installVersion = findViewById(R.id.InstallVersion);
        installVersion.setOnClickListener(
                v -> {
                    boolean[] permissionGranted = new boolean[2];

                    //Провверка на доступ к внешней памяти
                    if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        Log.i("UpdateUp", "Доступ к внешнему хранилищу разрешен");
                        permissionGranted[0] = true;
                    }else {
                        Log.i("UpdateUp", "Запросить доступ к внешнему хранилищу");
                        ActivityCompat.requestPermissions(this,
                                new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                PERMISSION_REQUEST_EXTERNAL_STORAGE);
                        Log.i("UpdateUp", "Доступ к внешнему хранилищу недоступен");
                    }


                    //Проверка на разрешение установки apk файлов
                    if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.REQUEST_INSTALL_PACKAGES) == PackageManager.PERMISSION_GRANTED){
                        Log.i("UpdateUp", "Разрешение на установку программ получено");
                        permissionGranted[1] = true;
                    }else {
                        Log.i("UpdateUp", "Запросить доступ на установку APK");
                        ActivityCompat.requestPermissions(this,
                                new String[] {Manifest.permission.REQUEST_INSTALL_PACKAGES},
                                PERMISSION_REQUEST_INSTALL_PACKAGES);
                        Log.i("UpdateUp", "Разрешение на установку программ отсутсвует");
                    }

                    if (checkPermissions(permissionGranted)) {
                            try {
                                //Сообщаем что началось обнавление
                                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                                dialog.setTitle("Обновление");
                                dialog.setMessage("Идет процесс установки");
                                dialog.setPositiveButton("ОК",
                                        (dialog12, which) -> {

                                        });
                                dialog.show();

                                versionManager.updateApp(this);
                            } catch (NoMemoryException exception) {
                                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                                dialog.setTitle(R.string.Standard_Error);
                                dialog.setMessage(R.string.SettingsActivity_AppControl_install);
                                dialog.setPositiveButton(
                                        R.string.Standard_dialog_positive_button,
                                        (dialog1, which) -> {
                                        });
                                dialog.show();
                            }
                        } else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                        dialog.setTitle(R.string.Standard_Error);
                        dialog.setTitle(R.string.Standard_NoPermissionGranted);
                        dialog.setPositiveButton(R.string.Standard_dialog_positive_button,
                                (dialog13, which) -> {});
                        dialog.show();
                        }
                    });

        //Проверим есть ли уедомление. Если есто то кнопка для начала установки станет активной
        //и будет выведено уведомление
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

    private boolean checkPermissions(boolean[] permissionGranted) {
        if (!permissionGranted[0]) {
            Log.i("UpdateUp", "Запросить доступ к внешнему хранилищу");
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_EXTERNAL_STORAGE);

            return false;
        }

        if (!permissionGranted[1]) {
            Log.i("UpdateUp", "Запросить доступ на установку APK");
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.REQUEST_INSTALL_PACKAGES},
                    PERMISSION_REQUEST_INSTALL_PACKAGES);

            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case PERMISSION_REQUEST_EXTERNAL_STORAGE:{
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("UpdateUp", "Разрешение на доступ к внешнему хранилищу получено");
                }
            }break;

            case PERMISSION_REQUEST_INSTALL_PACKAGES:{
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("UpdateUp", "Разрешение на установку получено");
                }
            }
        }
    }
}