package com.zalj.schedule.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zalj.schedule.Objects.Discipline;
import com.zalj.schedule.Objects.TimeSchedule;

import java.util.ArrayList;

import static com.zalj.schedule.Data.DataContract.DisciplinesDB;


public class DisciplineDBHelper extends SQLiteOpenHelper
{
    private SQLiteDatabase db;

    public DisciplineDBHelper(Context context, String tableName)
    {
        super(context, tableName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        this.db = db;

        StringBuffer command = new StringBuffer();
        command.append(DataContract.DEFAULT_STRING_CREATE_TABLE).append(getDatabaseName()).append(" ( ")
                .append(DisciplinesDB._ID).append(" ").append(DataContract.DEFAULT_STRING_ID).append(" , ")
                .append(DisciplinesDB.DAY_OF_WEEK).append(" ").append(DataContract.VALUE_TYPE_INTEGER).append(" , ")
                .append(DisciplinesDB.POSITION).append(" ").append(DataContract.VALUE_TYPE_INTEGER).append(" , ")
                .append(DisciplinesDB.TIME).append(" ").append(DataContract.VALUE_TYPE_INTEGER).append(" , ")
                .append(DisciplinesDB.DISCIPLINE_NAME).append(" ").append(DataContract.VALUE_TYPE_TEXT).append(" , ")
                .append(DisciplinesDB.TYPE).append(" ").append(DataContract.VALUE_TYPE_TEXT).append(" , ")
                .append(DisciplinesDB.BUILDING).append(" ").append(DataContract.VALUE_TYPE_TEXT).append(" , ")
                .append(DisciplinesDB.AUDITORIUM).append(" ").append(DataContract.VALUE_TYPE_TEXT).append( " );");

        db.execSQL(command.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

    //Вставить новый предмет
    public void addDiscipline(Discipline discipline)
    {
        ContentValues value = new ContentValues();

        value.put(DisciplinesDB.DAY_OF_WEEK, discipline.getDayOfWeek());
        value.put(DisciplinesDB.POSITION, discipline.getPosition());
        value.put(DisciplinesDB.TIME, discipline.getTimeNumber());
        value.put(DisciplinesDB.DISCIPLINE_NAME, discipline.getDisciplineName());
        value.put(DisciplinesDB.TYPE, discipline.getType());
        value.put(DisciplinesDB.BUILDING, discipline.getBuilding());
        value.put(DisciplinesDB.AUDITORIUM, discipline.getAuditorium());

        db.insert(getDatabaseName(), null, value);
    }

    public void addDiscipline(Discipline discipline, int dayOfWeek)
    {
        ContentValues value = new ContentValues();

        value.put(DisciplinesDB.DAY_OF_WEEK, dayOfWeek);
        value.put(DisciplinesDB.POSITION, discipline.getPosition());
        value.put(DisciplinesDB.TIME, discipline.getTimeNumber());
        value.put(DisciplinesDB.DISCIPLINE_NAME, discipline.getDisciplineName());
        value.put(DisciplinesDB.TYPE, discipline.getType());
        value.put(DisciplinesDB.BUILDING, discipline.getBuilding());
        value.put(DisciplinesDB.AUDITORIUM, discipline.getAuditorium());

        db.insert(getDatabaseName(), null, value);
    }

    public void deleteDiscipline(int id)
    {
        db.delete(getDatabaseName(), DisciplinesDB._ID + "=?", new String[]{String.valueOf(id)});
    }

    public void change(int id, ContentValues values)
    {
        db.update(getDatabaseName(), values, DisciplinesDB._ID + "=?", new String[]{String.valueOf(id)});
    }

    public ArrayList<Discipline> getScheduleToday(SQLiteDatabase db, int dayOfWeek)
    {
        this.db = db;
        ArrayList<Discipline> disciplines = new ArrayList<>();

        String[] columns = new String[]
                {
                    DisciplinesDB._ID,
                        DisciplinesDB.DAY_OF_WEEK,
                        DisciplinesDB.POSITION,
                        DisciplinesDB.TIME,
                        DisciplinesDB.DISCIPLINE_NAME,
                        DisciplinesDB.TYPE,
                        DisciplinesDB.BUILDING,
                        DisciplinesDB.AUDITORIUM
                };



        Cursor cursor = db.query(
                getDatabaseName(),
                columns,
                DisciplinesDB.DAY_OF_WEEK + "=?",
                new String[]{String.valueOf(dayOfWeek)},
                null,
                null,
                null,
                null);




        try {
            int idColumnIndex = cursor.getColumnIndex(DisciplinesDB._ID),
                    dayOfWeekColumnIndex = cursor.getColumnIndex(DisciplinesDB.DAY_OF_WEEK),
                    positionColumnIndex = cursor.getColumnIndex(DisciplinesDB.POSITION),
                    timeColumnIndex = cursor.getColumnIndex(DisciplinesDB.TIME),
                    disciplineNameColumnIndex = cursor.getColumnIndex(DisciplinesDB.DISCIPLINE_NAME),
                    typeColumnIndex = cursor.getColumnIndex(DisciplinesDB.TYPE),
                    buildingColumnIndex = cursor.getColumnIndex(DisciplinesDB.BUILDING),
                    auditoriumColumnIndex = cursor.getColumnIndex(DisciplinesDB.AUDITORIUM);

            while (cursor.moveToNext())
            {
                int currentID = cursor.getInt(idColumnIndex),
                        currentDayOfWeek = cursor.getInt(dayOfWeekColumnIndex),
                        currentPosition = cursor.getInt(positionColumnIndex),
                        currentTime = cursor.getInt(timeColumnIndex),
                        currentType = cursor.getInt(typeColumnIndex);

                String currentDisciplineName = cursor.getString(disciplineNameColumnIndex),
                        currentBuilding = cursor.getString(buildingColumnIndex),
                        currentAuditorium = cursor.getString(auditoriumColumnIndex);

                disciplines.add(new Discipline(
                        currentID,
                        currentPosition,
                        null,
                        currentDayOfWeek,
                        currentDisciplineName,
                        currentType,
                        currentBuilding,
                        currentAuditorium));
            }
        }finally {
            cursor.close();
        }

        return disciplines;
    }

    public ArrayList<Discipline> getScheduleToday(SQLiteDatabase db, int dayOfWeek, ArrayList<TimeSchedule> times)
    {
        this.db = db;
        ArrayList<Discipline> disciplines = new ArrayList<>();

        String[] columns = new String[]
                {
                        DisciplinesDB._ID,
                        DisciplinesDB.DAY_OF_WEEK,
                        DisciplinesDB.POSITION,
                        DisciplinesDB.TIME,
                        DisciplinesDB.DISCIPLINE_NAME,
                        DisciplinesDB.TYPE,
                        DisciplinesDB.BUILDING,
                        DisciplinesDB.AUDITORIUM
                };



        Cursor cursor = db.query(
                getDatabaseName(),
                columns,
                DisciplinesDB.DAY_OF_WEEK + "=?",
                new String[]{String.valueOf(dayOfWeek)},
                null,
                null,
                null,
                null);




        try {
            int idColumnIndex = cursor.getColumnIndex(DisciplinesDB._ID),
                    dayOfWeekColumnIndex = cursor.getColumnIndex(DisciplinesDB.DAY_OF_WEEK),
                    positionColumnIndex = cursor.getColumnIndex(DisciplinesDB.POSITION),
                    timeColumnIndex = cursor.getColumnIndex(DisciplinesDB.TIME),
                    disciplineNameColumnIndex = cursor.getColumnIndex(DisciplinesDB.DISCIPLINE_NAME),
                    typeColumnIndex = cursor.getColumnIndex(DisciplinesDB.TYPE),
                    buildingColumnIndex = cursor.getColumnIndex(DisciplinesDB.BUILDING),
                    auditoriumColumnIndex = cursor.getColumnIndex(DisciplinesDB.AUDITORIUM);

            while (cursor.moveToNext())
            {
                int currentID = cursor.getInt(idColumnIndex),
                        currentDayOfWeek = cursor.getInt(dayOfWeekColumnIndex),
                        currentPosition = cursor.getInt(positionColumnIndex),
                        currentTime = cursor.getInt(timeColumnIndex),
                        currentType = cursor.getInt(typeColumnIndex);

                String currentDisciplineName = cursor.getString(disciplineNameColumnIndex),
                        currentBuilding = cursor.getString(buildingColumnIndex),
                        currentAuditorium = cursor.getString(auditoriumColumnIndex);

                disciplines.add(new Discipline(
                        currentID,
                        currentPosition,
                        times.get(currentTime),
                        currentDayOfWeek,
                        currentDisciplineName,
                        currentType,
                        currentBuilding,
                        currentAuditorium));
            }
        }finally {
            cursor.close();
        }

        return disciplines;
    }
}
