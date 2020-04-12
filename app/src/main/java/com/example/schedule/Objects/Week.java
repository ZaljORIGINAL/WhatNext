package com.example.schedule.Objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Week implements Parcelable
{
    private byte number;
    private ArrayList<DayOfWeek> daysOfWeek;

    public Week(byte number)
    {
        this.number = number;
        daysOfWeek = new ArrayList<>();

        for (byte index = 0; index < 7; index++)
        {
            DayOfWeek day = new DayOfWeek(index, new ArrayList<Discipline>());
            daysOfWeek.add(day);
        }
    }

    public Week(int number, ArrayList<DayOfWeek> daysOfWeek){
        this.number = (byte) number;
        this.daysOfWeek = daysOfWeek;
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
    public ArrayList<Discipline> getDisciplines(byte dayOfWeek)
    {
        return daysOfWeek.get(dayOfWeek).getDisciplines();
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
    public void setDisciplinesOfDay(ArrayList<Discipline> disciplines, int index)
    {
        daysOfWeek.set(index ,new DayOfWeek((byte) index, disciplines));
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
