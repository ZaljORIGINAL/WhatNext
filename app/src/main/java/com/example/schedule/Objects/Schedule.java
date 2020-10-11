package com.example.schedule.Objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.schedule.Data.DataContract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Schedule implements Parcelable
{
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
    public Schedule()
    {
    }

    public Schedule(
            String nameOfFileSchedule
    )
    {
        if (nameOfFileSchedule.contains("."))
        {
            this.nameOfFileSchedule = nameOfFileSchedule.substring(0, nameOfFileSchedule.indexOf('.'));
        }else
        {
            this.nameOfFileSchedule = nameOfFileSchedule;
        }

        type = DataContract.MyAppSettings.SCHEDULE_TYPE_1;
        parity = -1;

        times = new ArrayList<>();
        disciplines = new ArrayList<>();
    }

    protected Schedule(Parcel in) {
        nameOfFileSchedule = in.readString();
        nameOfSchedule = in.readString();
        type = in.readByte();
        parity = in.readByte();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nameOfFileSchedule);
        dest.writeString(nameOfSchedule);
        dest.writeByte(type);
        dest.writeByte(parity);
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

    private void sort()
    {
        Collections.sort(disciplines, new Comparator<Discipline>()
        {
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
