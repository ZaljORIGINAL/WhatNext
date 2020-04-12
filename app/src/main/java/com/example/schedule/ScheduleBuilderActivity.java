package com.example.schedule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.schedule.Adapters.FragmentAdapter;
import com.example.schedule.Data.DataContract;
import com.example.schedule.Data.DisciplineDBHelper;
import com.example.schedule.Data.MyAppSettings;
import com.example.schedule.Data.TimeDBHelper;
import com.example.schedule.Objects.Discipline;
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
    public static Week
            topWeek,
            loverWeek;
    public static ArrayList<TimeSchedule> times;
    public static ArrayList<String> namesOfDisciplines;
    public Intent parentIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creating_new_schedule);

        parentIntent = getIntent();

        Calendar calendar = Calendar.getInstance();

        if (parentIntent.getIntExtra(IntentHelper.COMMAND, 0) == IntentHelper.EDIT_SCHEDULE)
        {
            schedule = parentIntent.getParcelableExtra(IntentHelper.SCHEDULE);

            SQLiteDatabase db;

            TimeDBHelper timeDB = new TimeDBHelper(this, schedule.getNameOfTimeDB());
            db = timeDB.getReadableDatabase();
            times = timeDB.getTime(db);

            Week week;
            DisciplineDBHelper disciplineDB;

            topWeek = new Week((byte) 0);
            disciplineDB = new DisciplineDBHelper(this, schedule.getNameOfDB_1());
            db = disciplineDB.getReadableDatabase();
            for (int indexOfDay = 0; indexOfDay < 7; indexOfDay++)
            {
                topWeek.setDisciplinesOfDay(disciplineDB.getScheduleToday(db, indexOfDay, times), indexOfDay);
            }

            loverWeek = new Week((byte) 1);
            try {
                disciplineDB = new DisciplineDBHelper(this, schedule.getNameOfDB_2());
                db = disciplineDB.getReadableDatabase();
                for (int indexOfDay = 0; indexOfDay < 7; indexOfDay++)
                {
                    loverWeek.setDisciplinesOfDay(disciplineDB.getScheduleToday(db, indexOfDay, times), indexOfDay);
                }
            }catch (Exception e)
            {
                loverWeek = new Week((byte) 1);
            }

            namesOfDisciplines = new ArrayList<>();

        }else
        {
            schedule = new Schedule(String.valueOf(calendar.getTimeInMillis()));
            topWeek = new Week((byte) 0);
            loverWeek = new Week((byte) 1);
            times = new ArrayList<>();
            namesOfDisciplines = new ArrayList<>();
        }

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
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.saveSchedule:
            {
                //Сохранение всего расписания
                if (checkScheduleFields())
                {
                    saveSchedule();

                    setResult(RESULT_OK);
                    finish();
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkScheduleFields()
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.Error);

        boolean result = true;

        if (schedule.getNameOfSchedule().isEmpty())
        {
            result = false;
            dialog.setMessage(getResources().getString(R.string.ScheduleCreatingActivity_Error_fieldOfNameIsClear));
            dialog.setPositiveButton(R.string.dialog_positive_button, null);
            dialog.show();
        }else if (schedule.getType() == 1)
        {
            if (schedule.getParity() == -1)
            {
                result = false;
                dialog.setMessage(getResources().getString(R.string.ScheduleCreatingActivity_Error_topWeekIsNotSeted));
                dialog.setPositiveButton(R.string.dialog_positive_button, null);
                dialog.show();
            }
        }

        return result;
    }

    //Сохранить расписание
    private void saveSchedule()
    {
        if (parentIntent.getIntExtra(IntentHelper.COMMAND, 0) == IntentHelper.EDIT_SCHEDULE)
        {
            DataContract.deleteDate(this, schedule);
        }

        //Сохранение основных параметров
        StringBuffer path = new StringBuffer();
        path.append(this.getFilesDir().getPath())
                .append(File.separator)
                .append(DataContract.FILE_OF_SCHEDULE_DIRECTORY)
                .append(File.separator)
                .append(schedule.getNameOfFileSchedule())
                .append(".txt");
        MyAppSettings.createFileOfOptions(path.toString(), schedule);

        SQLiteDatabase db;

        if (parentIntent.getIntExtra(IntentHelper.COMMAND, 0) == IntentHelper.EDIT_SCHEDULE)
        {

        }
        //Сохранение TimeSchedule
        TimeDBHelper timeDB = new TimeDBHelper(this, schedule.getNameOfTimeDB());
        db = timeDB.getReadableDatabase();
        for (byte i = 0; i < times.size(); i++)
        {
            timeDB.addTime(times.get(i));
        }

        Week week;

        //Сохранение TopWeek
        DisciplineDBHelper disciplineDB = new DisciplineDBHelper(this, schedule.getNameOfDB_1());
        db = disciplineDB.getReadableDatabase();
        insertDisciplines(topWeek, disciplineDB);

        //Сохранение LoverWeek
        if (schedule.getType() == 1)
        {
            disciplineDB = new DisciplineDBHelper(this, schedule.getNameOfDB_2());
            db = disciplineDB.getReadableDatabase();
            insertDisciplines(loverWeek, disciplineDB);
        }
    }

    private void insertDisciplines(Week week, DisciplineDBHelper disciplineDB)
    {
        byte i;
        for (i = 0; i < 7; i++)
        {
            ArrayList<Discipline> disciplines = week.getDisciplines(i);
            for (int b = 0; b < disciplines.size(); b++)
            {
                disciplineDB.addDiscipline(disciplines.get(b), i);
            }
        }
    }
}
