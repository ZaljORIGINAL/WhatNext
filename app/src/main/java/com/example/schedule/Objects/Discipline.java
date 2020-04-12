package com.example.schedule.Objects;

import android.os.Parcel;
import android.os.Parcelable;

public class Discipline implements Parcelable
{
    private int id;
    private byte position;
    private TimeSchedule time;
    private byte dayOfWeek;
    private String disciplineName;
    private byte type;
    private String building;
    private String auditorium;

    public static final byte
            LECTURE = 1,
            LABORATORY = 2;

    /**
     * Constructors
     * */
    public Discipline()
    {
    }

    //При чтении ДБ
    public Discipline(
            int id,
            int position,
            TimeSchedule time,
            int dayOfWeek,
            String disciplineName,
            int type,
            String building,
            String auditorium)
    {
        this.id = id;
        this.position = (byte) position;
        this.time = time;
        this.dayOfWeek = (byte) dayOfWeek;
        this.disciplineName = disciplineName;
        this.type = (byte) type;
        this.building = building;
        this.auditorium = auditorium;
    }

    //Create new
    public Discipline(
            int position,
            TimeSchedule time,
            int dayOfWeek,
            String disciplineName,
            int type,
            String building,
            String auditorium
    )
    {
        this.position = (byte) position;
        this.time = time;
        this.dayOfWeek = (byte) dayOfWeek;
        this.disciplineName = disciplineName;
        this.type = (byte) type;
        this.building = building;
        this.auditorium = auditorium;
    }

    protected Discipline(Parcel in) {
        id = in.readInt();
        position = in.readByte();
        time = in.readParcelable(TimeSchedule.class.getClassLoader());
        dayOfWeek = in.readByte();
        disciplineName = in.readString();
        type = in.readByte();
        building = in.readString();
        auditorium = in.readString();
    }

    public static final Creator<Discipline> CREATOR = new Creator<Discipline>() {
        @Override
        public Discipline createFromParcel(Parcel in) {
            return new Discipline(in);
        }

        @Override
        public Discipline[] newArray(int size) {
            return new Discipline[size];
        }
    };

    /**Get methods*/
    public int getId() {
        return id;
    }
    public int getPosition()
    {
        return position;
    }
    public String getNumber(){
        return String.valueOf(position);
    }
    public TimeSchedule getTime()
    {
        return time;
    }
    public int getTimeNumber(){return time.getNumber();}
    public int getStartHour()
    {
        return time.getStartHour();
    }
    public int getStartMinute()
    {
        return time.getStartMinute();
    }
    public int getFinishHour()
    {
        return time.getFinishHour();
    }
    public int getFinishMinute()
    {
        return time.getFinishMinute();
    }
    public String getDisciplineName()
    {
        return disciplineName;
    }
    public int getType(){return (int) type;}
    public int getDayOfWeek()
    {
        return dayOfWeek;
    }
    public String getBuilding()
    {
        return building;
    }
    public String getAuditorium()
    {
        return auditorium;
    }

    /**Set methods*/
    public void setId(int id) {
        this.id = id;
    }
    public void setTime(TimeSchedule time){this.time = time;}
//    public void setStartTime(int hour, int minute)
//    {
//        time.setStartTime(hour, minute);
//    }
//    public void setFinishTime(int hour, int minute)
//    {
//        time.setFinishTime(hour, minute);
//    }
    public void setDisciplineName(String name)
    {
        this.disciplineName = name;
    }
    public void setType(byte type){this.type = type;}
    public void setDayOfWeek(int dayOfWeek)
    {
        this.dayOfWeek = (byte) dayOfWeek;
    }
    public void setDayOfWeek(byte dayOfWeek)
    {
        this.dayOfWeek = dayOfWeek;
    }
    public void setPosition(int position)
    {
        this.position = (byte)position;
    }
    public void setDisceplines(TimeSchedule time){
        this.time = time;
        this.position = (byte) time.getNumber();
    }
    public void setBuilding(String building)
    {
        this.building = building;
    }
    public void setAuditorium(String auditorium)
    {
        this.auditorium = auditorium;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeByte(position);
        dest.writeParcelable(time, flags);
        dest.writeByte(dayOfWeek);
        dest.writeString(disciplineName);
        dest.writeByte(type);
        dest.writeString(building);
        dest.writeString(auditorium);
    }

    /**
     * Other methods
     * */
//    public String toString()
//    {
//        return String.valueOf(startHour + startMinute);
//    }
}
