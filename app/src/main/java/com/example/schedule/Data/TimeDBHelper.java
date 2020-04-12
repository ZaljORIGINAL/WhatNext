package com.example.schedule.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.schedule.Objects.TimeSchedule;
import com.example.schedule.Data.DataContract.TimeDB;

import java.util.ArrayList;

public class TimeDBHelper extends SQLiteOpenHelper
{
    private SQLiteDatabase db;

    public TimeDBHelper(Context context, String name)
    {
        super(context, name, null, 1);
    }

    //FIXME Как мы выяснили, если база данных уже имеется то метод onCreate не вызывается, сделать так что бы если бузу данных имеется, то все равно инециализировать db;
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        this.db = db;

        StringBuffer command = new StringBuffer();
        command.append(DataContract.DEFAULT_STRING_CREATE_TABLE).append(getDatabaseName()).append(" ( ")
                .append(TimeDB._ID).append(DataContract.DEFAULT_STRING_ID).append(" , ")
                .append(TimeDB.NUMBER).append(" ").append(DataContract.VALUE_TYPE_INTEGER).append(" , ")
                .append(TimeDB.START_HOUR).append(" ").append(DataContract.VALUE_TYPE_INTEGER).append(" , ")
                .append(TimeDB.START_MINUTE).append(" ").append(DataContract.VALUE_TYPE_INTEGER).append(" , ")
                .append(TimeDB.FINISH_HOUR).append(" ").append(DataContract.VALUE_TYPE_INTEGER).append(" , ")
                .append(TimeDB.FINISH_MINUTE).append(" ").append(DataContract.VALUE_TYPE_INTEGER).append(" );");

        db.execSQL(command.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

    public void addTime(TimeSchedule time)
    {
        ContentValues cv = new ContentValues();
        cv.put(TimeDB.NUMBER, time.getNumber());
        cv.put(TimeDB.START_HOUR, time.getStartHour());
        cv.put(TimeDB.START_MINUTE, time.getStartMinute());
        cv.put(TimeDB.FINISH_HOUR, time.getFinishHour());
        cv.put(TimeDB.FINISH_MINUTE, time.getFinishMinute());

        db.insert(getDatabaseName(), null, cv);
    }

    public ArrayList getTime(SQLiteDatabase db)
    {
        this.db = db;
        ArrayList<TimeSchedule> times = new ArrayList<>();

        String[] columns = new String[]
                {
                        TimeDB._ID,
                        TimeDB.NUMBER,
                        TimeDB.START_HOUR,
                        TimeDB.START_MINUTE,
                        TimeDB.FINISH_HOUR,
                        TimeDB.FINISH_MINUTE
                };

        Cursor cursor = db.query(
                getDatabaseName(),
                columns,
                null,
                null,
                null,
                null,
                null
        );

        try {
            int
                    idColumnIndex = cursor.getColumnIndex(TimeDB._ID),
                    numberColumnIndex = cursor.getColumnIndex(TimeDB.NUMBER),
                    startHourColumnIndex = cursor.getColumnIndex(TimeDB.START_HOUR),
                    startMinuteColumnIndex = cursor.getColumnIndex(TimeDB.START_MINUTE),
                    finishHourColumnIndex = cursor.getColumnIndex(TimeDB.FINISH_HOUR),
                    finishMinuteColumnIndex = cursor.getColumnIndex(TimeDB.FINISH_MINUTE);

            while (cursor.moveToNext())
            {
                byte
                        currentID = (byte) cursor.getInt(idColumnIndex),
                        currentNumber = (byte) cursor.getInt(numberColumnIndex),
                        currentStartHour = (byte) cursor.getInt(startHourColumnIndex),
                        currentStartMinute = (byte) cursor.getInt(startMinuteColumnIndex),
                        currentFinishHour = (byte) cursor.getInt(finishHourColumnIndex),
                        currentFinishMinute = (byte) cursor.getInt(finishMinuteColumnIndex);

                times.add(new TimeSchedule
                        (
                                currentID,
                                currentNumber,
                                currentStartHour,
                                currentStartMinute,
                                currentFinishHour,
                                currentFinishMinute
                        ));
            }
        }finally {
            cursor.close();
        }

        return times;
    }
}
