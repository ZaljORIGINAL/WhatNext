package com.zalj.schedule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.zalj.schedule.Adapters.FragmentAdapter;
import com.zalj.schedule.Data.DataContract;
import com.zalj.schedule.Data.DisciplineDBHelper;
import com.zalj.schedule.Data.TimeDBHelper;
import com.zalj.schedule.MyNotifications.MyDisciplineNotificationManager;
import com.zalj.schedule.Objects.DayOfWeek;
import com.zalj.schedule.Objects.Schedule;
import com.zalj.schedule.Objects.ScheduleBuilder;
import com.zalj.schedule.Objects.TimeSchedule;
import com.zalj.schedule.Objects.Week;

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

        ActionBar actionBar = getSupportActionBar();

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

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

        if (parentIntent.getIntExtra(IntentHelper.COMMAND, 0) == IntentHelper.CREATE_NEW_SCHEDULE){
            MenuItem item = menu.findItem(R.id.deleteSchedule);
            item.setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;

            case R.id.saveSchedule: {
                //Сохранение всего расписания
                if (checkScheduleFields()) {
                    saveSchedule();

                    Intent intent = new Intent();
                    intent.putExtra(IntentHelper.SCHEDULE_NAME, schedule.getNameOfFileSchedule());

                    setResult(RESULT_OK, intent);
                    finish();
                }else {
                    //TODO Cообщить, что не все данные вбиты
                }
            }break;

            case R.id.deleteSchedule: {
                deleteSchedule();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkScheduleFields() {
        StringBuilder message = new StringBuilder();

        boolean result = true;

        message
                .append(getString(R.string.ScheduleBuilderActivity_Dialog_dataCheck_message_warning))
                .append("\n");

        if (schedule.getNameOfSchedule().isEmpty()) {
            result = false;

            message
                    .append(getString(R.string.ScheduleBuilderActivity_Dialog_dataCheck_message_name))
                    .append("\n");
/*            dialog.setMessage(getResources().getString(R.string.ScheduleCreatingActivity_Error_fieldOfNameIsClear));
            dialog.setPositiveButton(R.string.Standard_dialog_positive_button, null);
            dialog.show();*/
        }

        if (schedule.getType() == DataContract.MyAppSettings.SCHEDULE_TYPE_2) {
            if (schedule.getParity() == -1) {
                result = false;

                message
                        .append(getString(R.string.ScheduleBuilderActivity_Dialog_dataCheck_message_date))
                        .append("\n");
/*                dialog.setMessage(getResources().getString(R.string.ScheduleCreatingActivity_Error_topWeekIsNotSelected));
                dialog.setPositiveButton(R.string.Standard_dialog_positive_button, null);
                dialog.show();*/
            }
        }

        if (times.size() == 0){
            result = false;

            message
                    .append(getString(R.string.ScheduleBuilderActivity_Dialog_dataCheck_message_times))
                    .append("\n");
        }

        if (!result){
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.ScheduleBuilderActivity_Dialog_dataCheck_title);
            dialog.setMessage(message.toString());
            dialog.setPositiveButton(R.string.Standard_dialog_positive_button,
                    (dialog1, which) -> {

                    });
            dialog.show();
        }

        return result;
    }

    //Сохранить расписание
    private void saveSchedule()
    {
        if (parentIntent.getIntExtra(IntentHelper.COMMAND, 0) == IntentHelper.EDIT_SCHEDULE) {
            DataContract.MyFileManager.deleteDate(this, schedule.getNameOfFileSchedule());
        }

        //Сохранение параметров расписания
        ScheduleBuilder scheduleBuilder = new ScheduleBuilder(getApplicationContext() ,schedule);
        scheduleBuilder.save();

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
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(this.getResources().getString(R.string.Standard_Delete));
        dialog.setMessage(this.getResources().getString(R.string.Standard_QuestionOfAction));
        dialog.setPositiveButton(R.string.Standard_dialog_positive_button,
                (dialog1, which) -> {
                    schedule.delete(getApplicationContext());

                    setResult(IntentHelper.RESULT_DELETED);
                    finish();
        });
        dialog.setNegativeButton(R.string.Standard_dialog_negative_button,
                (dialog12, which) -> { });
        dialog.show();
    }
}
