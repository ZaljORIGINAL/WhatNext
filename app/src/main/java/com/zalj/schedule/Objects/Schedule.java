package com.zalj.schedule.Objects;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

import com.zalj.schedule.Data.DataContract;
import com.zalj.schedule.Data.DisciplineDBHelper;
import com.zalj.schedule.Data.TimeDBHelper;
import com.zalj.schedule.MyNotifications.DisciplineNotificationManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class Schedule implements Parcelable {
    private String nameOfFileSchedule;  /*Фаил в котором хранятся пареметры Расписаня, в качестве имени указывается дата создания*/
    private String nameOfSchedule;      /*Наименование расписания*/
    private byte type;                  /*Тип расписания (двойное/одинарное)*/
    private byte parity;                /*Четность расписания, для отображения одного вида расписания в зависимости от недели*/

    //Заполняется после
    private ArrayList<TimeSchedule> times;
    private ArrayList<Discipline> disciplines;  /*Расписание на актуальный день*/

    /**
     * Constructors
     * */
    public Schedule() {
    }

    public Schedule(
            String nameOfFileSchedule
    ) {
        if (nameOfFileSchedule.contains(".")) {
            this.nameOfFileSchedule = nameOfFileSchedule.substring(0, nameOfFileSchedule.indexOf('.'));
        }else {
            this.nameOfFileSchedule = nameOfFileSchedule;
        }

        nameOfSchedule = "";
        type = DataContract.MyAppSettings.SCHEDULE_TYPE_1;
        parity = -1;

        times = new ArrayList<>();
        disciplines = new ArrayList<>();
    }

    public Schedule(String nameOfFileSchedule, String nameOfSchedule, byte type, byte parity){
        this.nameOfFileSchedule = nameOfFileSchedule;
        this.nameOfSchedule = nameOfSchedule;
        this.type = type;
        this.parity = parity;
    }

    protected Schedule(Parcel in) {
        nameOfFileSchedule = in.readString();
        nameOfSchedule = in.readString();
        type = in.readByte();
        parity = in.readByte();
        times = in.readArrayList(TimeSchedule.class.getClassLoader());
        disciplines = in.readArrayList(Discipline.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nameOfFileSchedule);
        dest.writeString(nameOfSchedule);
        dest.writeByte(type);
        dest.writeByte(parity);
        dest.writeList(times);
        dest.writeList(disciplines);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Schedule> CREATOR = new Creator<Schedule>() {
        @Override
        public Schedule createFromParcel(Parcel in) {
            return new Schedule(in);
        }

        @Override
        public Schedule[] newArray(int size) {
            return new Schedule[size];
        }
    };

    /**Set methods*/
    public void setNameOfFileSchedule(String nameOfFileSchedule) {
        this.nameOfFileSchedule = nameOfFileSchedule;
    }
    public void setNameOfSchedule(String nameOfSchedule) {
        this.nameOfSchedule = nameOfSchedule;
    }
    public void setType(int type) {
        this.type = (byte) type;
    }
    public void setType(String type)
    {
        this.type = Byte.parseByte(type);
    }
    public void setParity(int parity) {
        this.parity = (byte) parity;
    }
    public void setParity(String parity)
    {
        this.parity = Byte.parseByte(parity);
    }
    public void setTimes(ArrayList<TimeSchedule> times){
        if (times.size() != 0) {
            this.times = times;
        }else {
            ArrayList<TimeSchedule> supportTimes = new ArrayList<>();

            for (int index = 0; index < 5; index++) {
                TimeSchedule myTime = new TimeSchedule(
                        index,
                        0,
                        0,
                        0,
                        0,
                        0);

                supportTimes.add(myTime);
            }

            this.times = supportTimes;
        }
        changeTimes();
    }
    public void setDisciplines(ArrayList<Discipline> disciplines) {
        this.disciplines = disciplines;
        changeDisciplines();
        sort();
    }

    /**Get methods*/
    public String getNameOfFileSchedule() {
        return nameOfFileSchedule;
    }
    public String getNameOfSchedule() {
        return nameOfSchedule;
    }
    public int getType(){
        return (int) type;
    }
    public int getParity(){
        return (int) parity;
    }
    public String getNameOfTimeDB()
    {
        return DataContract.TIME_DB + nameOfFileSchedule;
    }
    public String getNameOfDB_1() {
        return DataContract.UPPER_SCHEDULE + nameOfFileSchedule;
    }
    public String getNameOfDB_2() {
        return DataContract.LOWER_SCHEDULE + nameOfFileSchedule;
    }
    public ArrayList<TimeSchedule> getTimes()
    {
        return times;
    }
    public ArrayList<Discipline> getDisciplines()
    {
        return disciplines;
    }

    /**
     * Other methods
     * */
    public void addNewDiscipline(Discipline discipline)
    {
        disciplines.add(discipline);
    }

    public void changeDisciplineParams(Discipline discipline, int index) {
        disciplines.add(index, discipline);
    }

    public void updateDiscipline(Context context, Calendar calendar){
        SQLiteDatabase db;
        DisciplineDBHelper disciplineDB;

        if (this.type == DataContract.MyAppSettings.SCHEDULE_TYPE_2) {
            if (calendar.get(Calendar.WEEK_OF_YEAR) % 2 == this.parity) {
                disciplineDB = new DisciplineDBHelper(context, getNameOfDB_1());
            }else {
                disciplineDB = new DisciplineDBHelper(context, getNameOfDB_2());
            }
        }else {
            disciplineDB = new DisciplineDBHelper(context, getNameOfDB_1());
        }
        db = disciplineDB.getReadableDatabase();

        setDisciplines(disciplineDB.getScheduleToday(db, calendar.get(Calendar.DAY_OF_WEEK)));

        db.close();
    }

    public void updateTimes(Context context){
        SQLiteDatabase db;
        //Получаем время
        TimeDBHelper timeDB = new TimeDBHelper(context, getNameOfTimeDB());
        db = timeDB.getReadableDatabase();

        setTimes(timeDB.getTime(db));

        db.close();
    }

    public void delete(Context context){
        DataContract.MyFileManager.deleteDate(context, getNameOfFileSchedule());
        DisciplineNotificationManager.Options.delete(context, getNameOfFileSchedule());
    }

    private String getOptionsFilePath(Context context){
        return new File(
                context.getFilesDir(),
                DataContract.MyFileManager.FILE_OF_SCHEDULE_DIRECTORY
                        + File.separator
                        + nameOfFileSchedule).getPath();
    }

    private void sort() {
        Collections.sort(disciplines, new Comparator<Discipline>() {
            @Override
            public int compare(Discipline o1, Discipline o2) {
                return o1.getNumber().compareTo(o2.getNumber());
            }
        });

    }

    private void changeDisciplines(){
        if (times != null && times.size() != 0){
            for (int index = 0; index < disciplines.size(); index++){
                Discipline discipline;

                discipline = disciplines.get(index);
                discipline.setTime(times.get(discipline.getPosition()));
            }
        }
    }
    private void changeTimes(){
        if (disciplines != null && disciplines.size() != 0){
            for (int index = 0; index < disciplines.size(); index++){
                Discipline discipline;

                discipline = disciplines.get(index);
                discipline.setTime(times.get(discipline.getPosition()));
            }
        }
    }
}
