package com.example.schedule.Data;

import android.content.Context;
import android.provider.BaseColumns;

import com.example.schedule.Objects.Schedule;

import java.io.File;

public class DataContract
{
    public final static String DATA_DIRECTORY = "database";
    public final static String FILE_OF_SCHEDULE_DIRECTORY = "scheduleFiles";
    public final static String UPPER_SCHEDULE = "top";
    public final static String LOWER_SCHEDULE = "lower";
    public final static String TIME_DB = "time";

    final static String
        VALUE_TYPE_INTEGER = " INTEGER ",
        VALUE_TYPE_TEXT = " TEXT ";

    final static String
        DEFAULT_STRING_CREATE_TABLE = " CREATE TABLE ",
        DEFAULT_STRING_ID = " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT ";

    //База данных не зависит от типа расписания(одиночное/множественное).
    static class DisciplinesDB implements BaseColumns
    {
        static final String
                _ID = BaseColumns._ID,
                DAY_OF_WEEK = "day_of_week",
                POSITION = "position",
                TIME = "time",
                DISCIPLINE_NAME = "disciplineName",
                TYPE = "type",
                BUILDING = "building",
                AUDITORIUM = "auditorium";
    }

    static class TimeDB implements BaseColumns
    {
        static final String
                _ID = BaseColumns._ID,
                NUMBER = "number",
                START_HOUR = "startHour",
                START_MINUTE = "startMinute",
                FINISH_HOUR = "finishHour",
                FINISH_MINUTE = "finishMinute";
    }

    public static void deleteDate(Context context, Schedule schedule)
    {
        String path = context.getFilesDir().getPath() +
                File.separator +
                FILE_OF_SCHEDULE_DIRECTORY +
                File.separator +
                schedule.getNameOfFileSchedule() +
                ".txt";
        File file = new File(path);
        file.delete();

        context.deleteDatabase(schedule.getNameOfTimeDB());
        context.deleteDatabase(schedule.getNameOfDB_1());
        try {
            context.deleteDatabase(schedule.getNameOfDB_2());
        }catch (Exception e)
        {
        }
    }
}
