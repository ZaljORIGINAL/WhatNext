package com.zalj.schedule.Objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DayOfWeek implements Parcelable
{
    private byte dayOfWeek; //Для понимания какой это день недели
    private ArrayList<Discipline> disciplines;

    public DayOfWeek()
    {
    }

    public DayOfWeek(byte dayOfWeek, ArrayList<Discipline> disciplines)
    {
        this.dayOfWeek = dayOfWeek;
        this.disciplines = disciplines;
    }

    protected DayOfWeek(Parcel in) {
        dayOfWeek = in.readByte();
        disciplines = in.createTypedArrayList(Discipline.CREATOR);
    }

    public static final Creator<DayOfWeek> CREATOR = new Creator<DayOfWeek>() {
        @Override
        public DayOfWeek createFromParcel(Parcel in) {
            return new DayOfWeek(in);
        }

        @Override
        public DayOfWeek[] newArray(int size) {
            return new DayOfWeek[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(dayOfWeek);
        dest.writeTypedList(disciplines);
    }

    /**
     * Get methods
     * */
    public int getDayOfWeek(){
        return dayOfWeek;
    }
    public ArrayList<Discipline> getDisciplines(){
        return disciplines;
    }
    public int getCount(){return disciplines.size();}
    public Discipline getDiscipline(int position){
        return disciplines.get(position);
    }

    /**
     * Set methods
     * */
    public void setDayOfWeek(byte dayOfWeek){
        this.dayOfWeek = dayOfWeek;
    }
    public void setDisciplines(ArrayList<Discipline> discipline) {
        this.disciplines = discipline;
    }
    public void setDiscipline(int position, Discipline discipline){
        this.disciplines.set(position, discipline);
    }

    /**
     * Other methods*/
    public void addDiscipline(Discipline discipline) {
        this.disciplines.add(discipline);
    }
    public void updateDiscipline(Discipline discipline, int position) {
        this.disciplines.set(position, discipline);
    }
    public void removeDiscipline(int position){
        disciplines.remove(position);
    }
    public void sortDisciplines(){
        Collections.sort(disciplines, new Comparator<Discipline>()
        {
            @Override
            public int compare(Discipline o1, Discipline o2) {
                return o1.getNumber().compareTo(o2.getNumber());
            }
        });
    }
}
