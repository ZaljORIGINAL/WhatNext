package com.zalj.schedule.Objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Calendar;

public class Week implements Parcelable
{
    private byte number;
    private ArrayList<DayOfWeek> daysOfWeek;

    public Week(byte number)
    {
        this.number = number;
        daysOfWeek = new ArrayList<>();


        daysOfWeek.add(new DayOfWeek((byte) Calendar.MONDAY, new ArrayList<Discipline>()));
        daysOfWeek.add(new DayOfWeek((byte) Calendar.TUESDAY, new ArrayList<Discipline>()));
        daysOfWeek.add(new DayOfWeek((byte) Calendar.WEDNESDAY, new ArrayList<Discipline>()));
        daysOfWeek.add(new DayOfWeek((byte) Calendar.THURSDAY, new ArrayList<Discipline>()));
        daysOfWeek.add(new DayOfWeek((byte) Calendar.FRIDAY, new ArrayList<Discipline>()));
        daysOfWeek.add(new DayOfWeek((byte) Calendar.SATURDAY, new ArrayList<Discipline>()));
        daysOfWeek.add(new DayOfWeek((byte) Calendar.SUNDAY, new ArrayList<Discipline>()));

    }

    protected Week(Parcel in) {
        number = in.readByte();
        daysOfWeek = in.createTypedArrayList(DayOfWeek.CREATOR);
    }

    public static final Creator<Week> CREATOR = new Creator<Week>() {
        @Override
        public Week createFromParcel(Parcel in) {
            return new Week(in);
        }

        @Override
        public Week[] newArray(int size) {
            return new Week[size];
        }
    };

    /**
     * Get methods
     * */
    public int getNumber(){
        return (int) number;
    }
    public ArrayList<DayOfWeek> getDaysOfWeek(){
        return daysOfWeek;
    }
    public ArrayList<Discipline> getDisciplines(int dayOfWeek) {
        return daysOfWeek.get(dayOfWeek).getDisciplines();
    }
    public DayOfWeek getDayOfWeek(int dayOfWeek) {
        return daysOfWeek.get(dayOfWeek);
    }
    /**
     * Set methods
     * */
    public void setNumber(int number) {
        this.number = (byte)number;
    }
    public void setDaysOfWeek(ArrayList<DayOfWeek> daysOfWeek){
        this.daysOfWeek = daysOfWeek;
    }
    public void setDayOfWeek(int index, DayOfWeek dayOfWeek)
    {
        daysOfWeek.set(index, dayOfWeek);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(number);
        dest.writeTypedList(daysOfWeek);
    }
}
