package com.example.schedule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.schedule.Adapters.FragmentAdapter;
import com.example.schedule.Data.DataContract;
import com.example.schedule.Data.DisciplineDBHelper;
import com.example.schedule.Data.TimeDBHelper;
import com.example.schedule.MyNotifications.MyDisciplineNotificationManager;
import com.example.schedule.Objects.DayOfWeek;
import com.example.schedule.Objects.Schedule;
import com.example.schedule.Objects.TimeSchedule;
import com.example.schedule.Objects.Week;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public class ScheduleBuilderActivity extends AppCompatActivity
{
    //Данные
    public static Schedule schedule;
    public static MyDisciplineNotificationManager.Options options;
    public static Week
            topWeek,
            loverWeek;
    public static ArrayList<TimeSchedule> times;
    public static ArrayList<String> namesOfDisciplines;
    public static ArrayList<String> auditoryOfDisciplines;
    public static ArrayList<String> buildingOfDisciplines;
    public static Intent parentIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creating_new_schedule);

        parentIntent = getIntent();

        Calendar calendar = Calendar.getInstance();

        if (parentIntent.getIntExtra(IntentHelper.COMMAND, 0) == IntentHelper.EDIT_SCHEDULE) {

            schedule = parentIntent.getParcelableExtra(IntentHelper.SCHEDULE);

            options = new MyDisciplineNotificationManager.Options(
                            this,
                            schedule.getNameOfFileSchedule());

            SQLiteDatabase db;

            TimeDBHelper timeDB = new TimeDBHelper(this, schedule.getNameOfTimeDB());
            db = timeDB.getReadableDatabase();
            times = timeDB.getTime(db);

            DisciplineDBHelper disciplineDB;

            topWeek = new Week((byte) 0);
            disciplineDB = new DisciplineDBHelper(this, schedule.getNameOfDB_1());
            db = disciplineDB.getReadableDatabase();
            for (int index = 0; index < 7; index++) {
                DayOfWeek day = topWeek.getDayOfWeek(index);
                day.setDisciplines(disciplineDB.getScheduleToday(db, day.getDayOfWeek(), times));
                topWeek.setDayOfWeek(index, day);
            }

            loverWeek = new Week((byte) 1);
            try {
                disciplineDB = new DisciplineDBHelper(this, schedule.getNameOfDB_2());
                db = disciplineDB.getReadableDatabase();
                for (int index = 0; index < 7; index++) {
                    DayOfWeek day = loverWeek.getDayOfWeek(index);
                    day.setDisciplines(disciplineDB.getScheduleToday(db, day.getDayOfWeek(), times));
                    loverWeek.setDayOfWeek(index, day);
                }
            }catch (Exception e) {
                loverWeek = new Week((byte) 1);
            }
        }else {
            schedule = new Schedule(String.valueOf(calendar.getTimeInMillis()));
            options = new MyDisciplineNotificationManager.Options(
                    this,
                    DataContract.MyFileManager.NO_INFO);
            topWeek = new Week((byte) 0);
            loverWeek = new Week((byte) 1);
            times = new ArrayList<>();
        }

        namesOfDisciplines = new ArrayList<>();
        auditoryOfDisciplines = new ArrayList<>();
        buildingOfDisciplines = new ArrayList<>();

        //Представления
        ViewPager pager = findViewById(R.id.scheduleCreatingViewPager);
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(), this);
        pager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_create_schedule, menu);

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saveSchedule: {
                //Сохранение всего расписания
                if (checkScheduleFields()) {
                    saveSchedule();

                    setResult(RESULT_OK);
                    finish();
                }
            }break;

            case R.id.deleteSchedule: {
                deleteSchedule();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkScheduleFields() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.Standard_Error);

        boolean result = true;

        if (schedule.getNameOfSchedule().isEmpty()) {
            result = false;
            dialog.setMessage(getResources().getString(R.string.ScheduleCreatingActivity_Error_fieldOfNameIsClear));
            dialog.setPositiveButton(R.string.Standard_dialog_positive_button, null);
            dialog.show();
        }else if (schedule.getType() == DataContract.MyAppSettings.SCHEDULE_TYPE_2) {
            if (schedule.getParity() == -1) {
                result = false;
                dialog.setMessage(getResources().getString(R.string.ScheduleCreatingActivity_Error_topWeekIsNotSelected));
                dialog.setPositiveButton(R.string.Standard_dialog_positive_button, null);
                dialog.show();
            }
        }

        return result;
    }

    //Сохранить расписание
    private void saveSchedule()
    {
        if (parentIntent.getIntExtra(IntentHelper.COMMAND, 0) == IntentHelper.EDIT_SCHEDULE) {
            DataContract.MyFileManager.deleteDate(this, schedule.getNameOfFileSchedule());
        }

        //Сохранение основных параметров
        StringBuffer path = new StringBuffer();
        path.append(this.getFilesDir().getPath())
                .append(File.separator)
                .append(DataContract.MyFileManager.FILE_OF_SCHEDULE_DIRECTORY)
                .append(File.separator)
                .append(schedule.getNameOfFileSchedule())
                .append(".txt");
        DataContract.MyFileManager.createFileOfOptions(path.toString(), schedule);

        //Сохранение настроек уведомлений
        if (!options.save()){
            options.setPathToFile(this, schedule.getNameOfFileSchedule());
            options.save();
        }

        SQLiteDatabase db;

        //Сохранение TimeSchedule
        TimeDBHelper timeDB = new TimeDBHelper(this, schedule.getNameOfTimeDB());
        db = timeDB.getReadableDatabase();
        for (byte i = 0; i < times.size(); i++) {
            timeDB.addTime(times.get(i));
        }
        db.close();

        //Сохранение TopWeek
        DisciplineDBHelper disciplineDB = new DisciplineDBHelper(this, schedule.getNameOfDB_1());
        db = disciplineDB.getReadableDatabase();
        insertDisciplines(topWeek, disciplineDB);
        db.close();

        //Сохранение LoverWeek
        if (schedule.getType() == DataContract.MyAppSettings.SCHEDULE_TYPE_2) {
            disciplineDB = new DisciplineDBHelper(this, schedule.getNameOfDB_2());
            db = disciplineDB.getReadableDatabase();
            insertDisciplines(loverWeek, disciplineDB);
            db.close();
        }
    }
    private void insertDisciplines(Week week, DisciplineDBHelper disciplineDB) {
        int index;
        for (index = 0; index < 7; index++) {
            DayOfWeek day = week.getDayOfWeek(index);
            for (int b = 0; b < day.getDisciplines().size(); b++) {
                disciplineDB.addDiscipline(day.getDisciplines().get(b), day.getDayOfWeek());
            }
        }
    }

    //Удаление расписания
    private void deleteSchedule() {
        if (parentIntent.getIntExtra(IntentHelper.COMMAND, 0) != IntentHelper.EDIT_SCHEDULE) {
            setResult(IntentHelper.RESULT_ERROR);
            finish();
        }else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(this.getResources().getString(R.string.Standard_Delete));
            dialog.setMessage(this.getResources().getString(R.string.Standard_QuestionOfDelete));
            dialog.setPositiveButton(R.string.Standard_dialog_positive_button,
                    (dialog1, which) -> {
                        setResult(IntentHelper.RESULT_DELETED);
                        finish();
                    });
            dialog.setNegativeButton(R.string.Standard_dialog_negative_button,
                    (dialog12, which) -> {
                    });
            dialog.show();
        }
    }
}
