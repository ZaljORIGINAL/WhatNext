package com.example.schedule;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedule.Adapters.DisciplineAdapter;
import com.example.schedule.Data.DataContract;
import com.example.schedule.Data.DisciplineDBHelper;
import com.example.schedule.Data.TimeDBHelper;
import com.example.schedule.Objects.Schedule;

import java.io.File;
import java.util.Calendar;

import static com.example.schedule.Data.DataContract.MyAppSettings;
import static com.example.schedule.Data.DataContract.MyAppSettings.PERMISSION_REQUEST_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements DisciplineAdapter.iOnItemClickListener
{
    //Настройки
    private SharedPreferences settings;

    //Object
    private Schedule schedule;

    //Расписане
    private Calendar calendar;

    //View
    private Button bDate;
    private RecyclerView disciplineList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bDate = findViewById(R.id.date);
        disciplineList = findViewById(R.id.disciplinesView);
        disciplineList.setLayoutManager(new LinearLayoutManager(this));

        calendar = Calendar.getInstance();
        updateDateButton();

        //Имя файла настроек
        settings = getSharedPreferences(DataContract.MyAppSettings.LAST_VIEWED_SCHEDULE, Context.MODE_PRIVATE);

        /*Проверка на последнее просматриваемое расписание.
         * Если пользователь уже работал с каким то расписание и не вышел из него, то оно и запустится*/
        if (settings.getString(DataContract.MyAppSettings.LAST_SCHEDULE, DataContract.MyAppSettings.NULL).equals(DataContract.MyAppSettings.NULL))
        {
            //Открываем активити выбора расписания
            Intent intent = new Intent(this, SelectScheduleActivity.class);
            startActivityForResult(intent, IntentHelper.SELECT_SCHEDULE);
        }else
        {
            //Считывается весь документ и сохраняется в объекте
            String path = this.getFilesDir().getPath() +
                    File.separator +
                    DataContract.MyFileManager.FILE_OF_SCHEDULE_DIRECTORY +
                    File.separator +
                    settings.getString(DataContract.MyAppSettings.LAST_SCHEDULE, DataContract.MyAppSettings.NULL) +
                    ".txt";
            //FIXME Возможно стоило сделать методы updateDisciplines и updateTimes методами класса Schedule
            schedule = DataContract.MyFileManager.readFileOfOptions(path);
            updateDisciplines();
            updateTimes();

            updateRecycleView();
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
        switch (item.getItemId())
        {
            case R.id.selectNewSchedules:
            {
                Intent intent = new Intent(this, SelectScheduleActivity.class);
                startActivityForResult(intent, IntentHelper.SELECT_SCHEDULE);

                SharedPreferences.Editor editor = settings.edit();
                editor.putString(DataContract.MyAppSettings.LAST_SCHEDULE, DataContract.MyAppSettings.NULL);
                editor.apply();
            }break;

            case R.id.edit:
            {
                Intent intent = new Intent(this, ScheduleBuilderActivity.class);

                intent.putExtra(IntentHelper.COMMAND, IntentHelper.EDIT_SCHEDULE);
                intent.putExtra(IntentHelper.SCHEDULE, schedule);
                startActivityForResult(intent, IntentHelper.EDIT_SCHEDULE);
            }break;

            case R.id.exportSchedule:
            {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                {
                    Log.i("Copy/Import/Export", "Внешняя память доступна");

                    //Проверка разрешения на доступ к хранилищу
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                        dialog.setTitle(R.string.Standard_Dialog_Report);

                        if(DataContract.MyFileManager.exportFiles(this, schedule.getNameOfFileSchedule()))
                        {
                            dialog.setMessage(R.string.Standard_isComplete);
                        }else
                        {
                            dialog.setMessage(R.string.Standard_Error);
                        }

                        dialog.setPositiveButton(R.string.Standard_dialog_positive_button,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
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
                }else
                {
                    Log.i("Copy/Import/Export", "Внешняя память не доступна. " + Environment.getExternalStorageState());
                    Toast.makeText(this, R.string.SelectScheduleActivity_Toast_storageIsNotAvailable, Toast.LENGTH_LONG).show();
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case IntentHelper.SELECT_SCHEDULE:
            {
                /*FIXME Возможна ошибка, пользователь может выйти выбрать новое расписание,
                *  но он этого не делает, а значит в data, ничего не передается*/
                String path = this.getFilesDir().getPath() +
                        File.separator +
                        DataContract.MyFileManager.FILE_OF_SCHEDULE_DIRECTORY +
                        File.separator +
                        data.getStringExtra(IntentHelper.NAME);
                schedule = DataContract.MyFileManager.readFileOfOptions(path);
                updateDisciplines();
                updateTimes();
                updateRecycleView();

                //Сохраняем открывшуюся расписание
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(DataContract.MyAppSettings.LAST_SCHEDULE, schedule.getNameOfFileSchedule());
                editor.apply();
            }break;

            case IntentHelper.EDIT_SCHEDULE:
            {
                switch (resultCode)
                {
                    case RESULT_OK:
                    {
                        updateRecycleView();
                    }break;

                    case IntentHelper.RESULT_DELETED:
                    {
                        Intent intent = new Intent(this, SelectScheduleActivity.class);
                        startActivityForResult(intent, IntentHelper.SELECT_SCHEDULE);

                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(DataContract.MyAppSettings.LAST_SCHEDULE, DataContract.MyAppSettings.NULL);
                        editor.apply();
                    }break;

                    case RESULT_CANCELED:
                    {

                    };

                    case IntentHelper.RESULT_ERROR:
                    {
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
    /*
    * TODO доделать обновления для параметров schedule:
    *   *Имя расписания
    *   *Тип расписания*/
    private void updateSchedule(){
        updateDisciplines();
        updateTimes();
    }

    private void updateDisciplines() {
        SQLiteDatabase db;
        DisciplineDBHelper disciplineDB;

        if (schedule.getType() == DataContract.MyAppSettings.SCHEDULE_TYPE_2)
        {
            if ((calendar.get(Calendar.DAY_OF_YEAR) + calendar.get(Calendar.DAY_OF_WEEK)) %2 == schedule.getParity()) {
                disciplineDB = new DisciplineDBHelper(this, schedule.getNameOfDB_1());
            }else {
                disciplineDB = new DisciplineDBHelper(this, schedule.getNameOfDB_2());
            }
        }else {
            disciplineDB = new DisciplineDBHelper(this, schedule.getNameOfDB_1());
        }
        db = disciplineDB.getReadableDatabase();
        schedule.setDisciplines(disciplineDB.getScheduleToday(db, calendar.get(Calendar.DAY_OF_WEEK)));

        db.close();
    }

    private void updateTimes(){
        SQLiteDatabase db;
        //Получаем время
        TimeDBHelper timeDB = new TimeDBHelper(this, schedule.getNameOfTimeDB());
        db = timeDB.getReadableDatabase();
        schedule.setTimes(timeDB.getTime(db));

        db.close();
    }

    private void updateRecycleView()
    {
        DisciplineAdapter adapter = new DisciplineAdapter(this, schedule.getDisciplines(), this);
        disciplineList.setAdapter(adapter);
    }

    /**Navigation buttons methods*/
    public void onNavigationButtonClicked(View view)
    {
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
        updateDisciplines();

        updateRecycleView();
    }

    public void onDateChangeButtonClicked(View view)
    {
        //Вывести диалоговое окно для выбора нужного дня и после представить
        new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
                    {
                        calendar.set(year, month, dayOfMonth);
                        updateDateButton();
                        updateRecycleView();
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void updateDateButton()
    {
        bDate.setText(DateUtils.formatDateTime(this, calendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE));
    }
}
