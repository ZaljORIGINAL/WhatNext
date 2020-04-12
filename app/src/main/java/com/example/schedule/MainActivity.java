package com.example.schedule;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.example.schedule.Adapters.DisciplineAdapter;
import com.example.schedule.Data.DataContract;
import com.example.schedule.Data.DisciplineDBHelper;
import com.example.schedule.Data.MyAppSettings;
import com.example.schedule.Data.TimeDBHelper;
import com.example.schedule.Objects.Schedule;

import java.io.File;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements DisciplineAdapter.iOnItemClickListener
{
    //Настройки
    private SharedPreferences settings;

    //Object
    private Schedule today;

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
        settings = getSharedPreferences(MyAppSettings.LAST_VIEWED_SCHEDULE, Context.MODE_PRIVATE);

        /*Проверка на последнее просматриваемое расписание.
         * Если пользователь уже работал с каким то расписание и не вышел из него, то оно и запустится*/
        if (settings.getString(MyAppSettings.LAST_SCHEDULE, MyAppSettings.NULL).equals(MyAppSettings.NULL))
        {
            //Открываем активити выбора расписания
            Intent intent = new Intent(this, SelectScheduleActivity.class);
            startActivityForResult(intent, IntentHelper.SELECT_SCHEDULE);
        }else
        {
            //Считывается весь документ и сохраняется в объекте
            String path = this.getFilesDir().getPath() +
                    File.separator +
                    DataContract.FILE_OF_SCHEDULE_DIRECTORY +
                    File.separator +
                    settings.getString(MyAppSettings.LAST_SCHEDULE, MyAppSettings.NULL) +
                    ".txt";
            today = MyAppSettings.readFileOfOptions(path);

            getScheduleToToday(today);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
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
                editor.putString(MyAppSettings.LAST_SCHEDULE, MyAppSettings.NULL);
                editor.apply();
            }break;

            case R.id.edit:
            {
                Intent intent = new Intent(this, ScheduleBuilderActivity.class);

                intent.putExtra(IntentHelper.COMMAND, IntentHelper.EDIT_SCHEDULE);
                intent.putExtra(IntentHelper.SCHEDULE, today);
                startActivityForResult(intent, IntentHelper.EDIT_SCHEDULE);
            }break;

            case R.id.delete:
            {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle(this.getResources().getString(R.string.Delete));
                dialog.setMessage(this.getResources().getString(R.string.QuestionOfDelete));
                dialog.setPositiveButton(R.string.dialog_positive_button,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DataContract.deleteDate(getApplicationContext(), today);

                                Intent intent = new Intent(getApplicationContext(), SelectScheduleActivity.class);
                                startActivityForResult(intent, IntentHelper.SELECT_SCHEDULE);

                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString(MyAppSettings.LAST_SCHEDULE, MyAppSettings.NULL);
                                editor.apply();
                            }
                        });
                dialog.setNegativeButton(R.string.dialog_negative_button,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                dialog.show();
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
                        DataContract.FILE_OF_SCHEDULE_DIRECTORY +
                        File.separator +
                        data.getStringExtra(IntentHelper.NAME);
                today = MyAppSettings.readFileOfOptions(path);

                //Получение данных из ДБ
                getScheduleToToday(today);

                //Сохраняем то расписание что открыывали
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(MyAppSettings.LAST_SCHEDULE, today.getNameOfFileSchedule());
                editor.apply();
            }break;

            /*FIXME Хрен поми что происходит при той или иной ситуации, нужно проверять*/
            case IntentHelper.EDIT_SCHEDULE:
            {
                switch (resultCode)
                {
                    case RESULT_OK:
                    {
                        updateRecycleView();
                    }break;

                    case IntentHelper.RESULT_DELETE:
                    {
                        Intent intent = new Intent(this, SelectScheduleActivity.class);
                        startActivityForResult(intent, IntentHelper.SELECT_SCHEDULE);

                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(MyAppSettings.LAST_SCHEDULE, MyAppSettings.NULL);
                        editor.apply();
                    }break;

                    case RESULT_CANCELED:
                    {

                    };
                }
            }
        }

    }

    /**
     * Слушатели кнопок
     * */
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
        //Надо отсоритировать. Расписание может быть в разнобой
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

    //TODO Далее сделать возможным запись заметок по данному предмету
    @Override
    public void onItemClick(int position) {

    }

    //Первичное чтеные баззы данных(получение времени и самого расписания на актуальный день)
    private void getScheduleToToday(Schedule schedule)
    {
        SQLiteDatabase db;
        //Получаем время
        TimeDBHelper timeDB = new TimeDBHelper(this, schedule.getNameOfTimeDB());
        db = timeDB.getReadableDatabase();
        schedule.setTimes(timeDB.getTime(db));

        //Получаем расписание предметов
        updateRecycleView();
    }
    private void getDisciplines()
    {
        SQLiteDatabase db;
        DisciplineDBHelper disciplineDB;

        if (today.getType() == 1)//Проверка на тип расписание (одинарное - 0, двойное - 1)
        {
            if ((calendar.get(Calendar.DAY_OF_YEAR) + calendar.get(Calendar.DAY_OF_WEEK)) %2 == today.getParity()) {
                disciplineDB = new DisciplineDBHelper(this, today.getNameOfDB_1());
            }else {
                disciplineDB = new DisciplineDBHelper(this, today.getNameOfDB_2());
            }
        }else {
            disciplineDB = new DisciplineDBHelper(this, today.getNameOfDB_1());
        }
        db = disciplineDB.getReadableDatabase();
        today.setDisciplines(disciplineDB.getScheduleToday(db, calendar.get(Calendar.DAY_OF_WEEK) - 2,
                today.getTimes()));
    }

    /**Data update methods*/
    private void updateRecycleView()
    {
        getDisciplines();
        DisciplineAdapter adapter = new DisciplineAdapter(this, today.getDisciplines(), this);
        disciplineList.setAdapter(adapter);
    }
    private void updateDateButton()
    {
        bDate.setText(DateUtils.formatDateTime(this, calendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE));
    }
}
