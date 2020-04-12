package com.example.schedule.Objects;

import android.os.Parcel;
import android.os.Parcelable;

public class TimeSchedule implements Parcelable
{
    private byte id;
    private byte number;
    private byte startHour, startMinute;
    private byte finishHour, finishMinute;

    public TimeSchedule()
    {
    }

    public TimeSchedule(int number)
    {
        this.number = (byte) number;
        startHour = -1;
        startMinute = -1;
        finishHour = -1;
        finishMinute = -1;
    }

    public TimeSchedule(int id, int number, int startHour, int startMinute, int finishHour, int finishMinute)
    {
        this.id = (byte) id;
        this.number = (byte) number;
        this.startHour = (byte) startHour;
        this.startMinute = (byte) startMinute;
        this.finishHour = (byte) finishHour;
        this.finishMinute = (byte) finishMinute;
    }

    protected TimeSchedule(Parcel in) {
        id = in.readByte();
        number = in.readByte();
        startHour = in.readByte();
        startMinute = in.readByte();
        finishHour = in.readByte();
        finishMinute = in.readByte();
    }

    public static final Creator<TimeSchedule> CREATOR = new Creator<TimeSchedule>() {
        @Override
        public TimeSchedule createFromParcel(Parcel in) {
            return new TimeSchedule(in);
        }

        @Override
        public TimeSchedule[] newArray(int size) {
            return new TimeSchedule[size];
        }
    };

    /**
     * Set methods
     * */
    public void setStartTime(int startHour, int startMinute)
    {
        this.startHour = (byte) startHour;
        this.startMinute = (byte) startMinute;
    }
    public void setFinishTime(int finishHour, int finishMinute)
    {
        this.finishHour = (byte) finishHour;
        this.finishMinute = (byte) finishMinute;
    }

    /**
     * Get methods
     * */
    public int getID()
    {
        return id;
    }
    public int getNumber(){
        return number;
    }
    public int getStartHour()
    {
        return startHour;
    }
    public int getStartMinute()
    {
        return startMinute;
    }
    public int getFinishHour()
    {
        return finishHour;
    }
    public int getFinishMinute()
    {
        return finishMinute;
    }

    public boolean getStatus() {
        if (startHour == -1)
        {
            return false;
        }

        if (startMinute == -1)
        {
            return false;
        }

        if (finishHour == -1)
        {
            return false;
        }

        if (finishMinute == -1)
        {
            return false;
        }

        return true;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(id);
        dest.writeByte(number);
        dest.writeByte(startHour);
        dest.writeByte(startMinute);
        dest.writeByte(finishHour);
        dest.writeByte(finishMinute);
    }
}
