package com.zalj.schedule;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zalj.schedule.Adapters.DisciplineAdapter;
import com.zalj.schedule.Data.DataContract;
import com.zalj.schedule.MyNotifications.MyDisciplineNotificationManager;
import com.zalj.schedule.Objects.Schedule;

import java.io.File;
import java.util.Calendar;

import static com.zalj.schedule.Data.DataContract.MyAppSettings.PERMISSION_REQUEST_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements DisciplineAdapter.iOnItemClickListener
{
    //Настройки
    private SharedPreferences settings;

    //Object
    private Schedule schedule;
    private MyDisciplineNotificationManager notificationManager;

    //Расписане
    private Calendar calendar;

    //View
    private Button bDate;
    private RecyclerView disciplineList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bDate = findViewById(R.id.date);
        disciplineList = findViewById(R.id.disciplinesView);
        disciplineList.setLayoutManager(new LinearLayoutManager(this));

        calendar = Calendar.getInstance();

        //Имя файла настроек
        settings = getSharedPreferences(
                DataContract.MyAppSettings.LAST_VIEWED_SCHEDULE,
                Context.MODE_PRIVATE);

        /**Проверка на последнее просматриваемое расписание.
         * Если пользователь уже работал с каким то расписание и не вышел из него, то оно и запустится*/
        if (settings.getString(
                DataContract.MyAppSettings.LAST_SCHEDULE,
                DataContract.MyAppSettings.NULL)
                .equals(DataContract.MyAppSettings.NULL)) {
            //Открываем активити выбора расписания
            Intent intent = new Intent(this, SelectScheduleActivity.class);
            startActivityForResult(intent, IntentHelper.SELECT_SCHEDULE);
        }else {
            //Считывается весь документ и сохраняется в объекте
            String path = this.getFilesDir().getPath() +
                    File.separator +
                    DataContract.MyFileManager.FILE_OF_SCHEDULE_DIRECTORY +
                    File.separator +
                    settings.getString(DataContract.MyAppSettings.LAST_SCHEDULE, DataContract.MyAppSettings.NULL) +
                    ".txt";

            schedule = DataContract.MyFileManager.readFileOfOptions(path);

            updateDateButton();
            updateRecycleView();

            //Считываются настройки уведомлений по расписанию
            notificationManager = MyDisciplineNotificationManager
                    .getInstance(getApplicationContext(), schedule);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
        {
            MenuItem exportSchedule = menu.findItem(R.id.exportSchedule);
            exportSchedule.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.selectNewSchedules: {
                Intent intent = new Intent(this, SelectScheduleActivity.class);
                startActivityForResult(intent, IntentHelper.SELECT_SCHEDULE);

                notificationManager.deleteAllAlarm();
                notificationManager.setNull();
                schedule = null;

                SharedPreferences.Editor editor = settings.edit();
                editor.putString(DataContract.MyAppSettings.LAST_SCHEDULE, DataContract.MyAppSettings.NULL);
                editor.apply();
            }break;

            case R.id.edit: {
                Intent intent = new Intent(this, ScheduleBuilderActivity.class);

                intent.putExtra(IntentHelper.COMMAND, IntentHelper.EDIT_SCHEDULE);
                intent.putExtra(IntentHelper.SCHEDULE, schedule);
                startActivityForResult(intent, IntentHelper.EDIT_SCHEDULE);
            }break;

            case R.id.exportSchedule: {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    Log.i("Copy/Import/Export", "Внешняя память доступна");

                    //Проверка разрешения на доступ к хранилищу
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                        dialog.setTitle(R.string.Standard_Dialog_Report);

                        if(DataContract.MyFileManager.exportFiles(this, schedule.getNameOfFileSchedule())) {
                            dialog.setMessage(R.string.Standard_isComplete);
                        }else {
                            dialog.setMessage(R.string.Standard_Error);
                        }

                        dialog.setPositiveButton(R.string.Standard_dialog_positive_button,
                                (dialog1, which) -> {
                                });

                        dialog.show();
                    } else
                    {
                        Log.i("Copy/Import/Export", "Разрешение на внешнее хранилище не получено, запрошиваю доступ");

                        //Запрашиваем разрешение у пользователя. Статья: https://habr.com/ru/post/278945/
                        ActivityCompat.requestPermissions(this,
                                new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                PERMISSION_REQUEST_EXTERNAL_STORAGE);
                    }
                }else {
                    Log.i("Copy/Import/Export", "Внешняя память не доступна. " + Environment.getExternalStorageState());
                    Toast.makeText(this, R.string.SelectScheduleActivity_Toast_storageIsNotAvailable, Toast.LENGTH_LONG).show();
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case IntentHelper.SELECT_SCHEDULE: {

                String path = this.getFilesDir().getPath() +
                        File.separator +
                        DataContract.MyFileManager.FILE_OF_SCHEDULE_DIRECTORY +
                        File.separator +
                        data.getStringExtra(IntentHelper.NAME);
                schedule = DataContract.MyFileManager.readFileOfOptions(path);

                updateRecycleView();
                notificationManager = MyDisciplineNotificationManager.getInstance(this, schedule);

                //Сохраняем открывшуюся расписание
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(DataContract.MyAppSettings.LAST_SCHEDULE, schedule.getNameOfFileSchedule());
                editor.apply();
            }break;

            case IntentHelper.EDIT_SCHEDULE: {
                switch (resultCode) {
                    case RESULT_OK: {
                        updateRecycleView();

                        notificationManager.updateAllAlarm();
                    }break;

                    case IntentHelper.RESULT_DELETED: {

                        DataContract.MyFileManager.deleteDate(this, schedule.getNameOfFileSchedule());
                        notificationManager.deleteAllAlarm();
                        notificationManager.deleteOptionsFile(this);
                        notificationManager.setNull();
                        schedule = null;

                        Intent intent = new Intent(this, SelectScheduleActivity.class);
                        startActivityForResult(intent, IntentHelper.SELECT_SCHEDULE);

                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(DataContract.MyAppSettings.LAST_SCHEDULE, DataContract.MyAppSettings.NULL);
                        editor.apply();
                    }break;

                    case RESULT_CANCELED: {

                    };

                    case IntentHelper.RESULT_ERROR: {
                        Toast.makeText(this, this.getText(R.string.toast_message_scheduleIsNotCreated), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    //TODO Далее сделать возможным запись заметок по данному предмету
    @Override
    public void onItemClick(int position) {

    }

    /**
     * schedule update methods
     * */
    private void updateSchedule(){
        schedule.updateTimes(this);
        schedule.updateDiscipline(this, calendar);
    }

    private void updateRecycleView() {
        updateSchedule();
        DisciplineAdapter adapter = new DisciplineAdapter(this, schedule.getDisciplines(), this);
        disciplineList.setAdapter(adapter);
    }

    /**Navigation buttons methods*/
    public void onNavigationButtonClicked(View view) {
        //Перейти на сследующий или предидущий день
        switch (view.getId())
        {
            case R.id.after:
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                break;

            case R.id.before:
                calendar.add(Calendar.DAY_OF_YEAR, -1);
                break;
        }

        updateDateButton();

        updateRecycleView();
    }

    public void onDateChangeButtonClicked(View view) {
        //Вывести диалоговое окно для выбора нужного дня и после представить
        new DatePickerDialog(
                this,
                (view1, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    updateDateButton();

                    updateRecycleView();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void updateDateButton() {
        StringBuilder string = new StringBuilder();

        string.append(DateUtils.formatDateTime(this, calendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE));

        if (schedule.getType() == DataContract.MyAppSettings.SCHEDULE_TYPE_2){

            string.append(" (");
            if (calendar.get(Calendar.WEEK_OF_YEAR) % 2 == schedule.getParity())
                string.append(getString(R.string.fragment_name_topWeek));
            else
                string.append(getString(R.string.fragment_name_lowerWeek));

            string.append(")");
        }

        bDate.setText(string.toString());
    }
}
