package com.zalj.schedule.Objects;

import android.content.Context;
import android.util.Log;

import com.zalj.schedule.Data.DataContract;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class ScheduleBuilder implements Serializable {
    private String nameOfFileSchedule;  /*Фаил в котором хранятся пареметры Расписаня, в качестве имени указывается дата создания*/
    private String nameOfSchedule;      /*Наименование расписания*/
    private byte type;                  /*Тип расписания (двойное/одинарное)*/
    private byte parity;                /*Четность расписания, для отображения одного вида расписания в зависимости от недели*/

    //Заполняется после
    private ArrayList<TimeSchedule> times;
    private ArrayList<Discipline> disciplines;  /*Расписание на актуальный день*/

    public ScheduleBuilder(){};

    public ScheduleBuilder(String nameOfFileSchedule){
        this.nameOfFileSchedule = nameOfFileSchedule;
    }

    public ScheduleBuilder(String nameOfFileSchedule, String nameOfSchedule, byte type, byte parity){
        this.nameOfFileSchedule = nameOfFileSchedule;
        this.nameOfSchedule = nameOfSchedule;
        this.type = type;
        this.parity = parity;
    }

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
    public void setParity(String parity) {
        this.parity = Byte.parseByte(parity);
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

    public Schedule build(){
        Schedule schedule = new Schedule(
                nameOfFileSchedule,
                nameOfSchedule,
                type,
                parity
        );

        return schedule;
    }

    public boolean read(Context context){
        if (nameOfFileSchedule != null){
            String path = getOptionsFilePath(context);

            try {
                ObjectInputStream deserialize = new ObjectInputStream(
                        new FileInputStream(path)
                );

                ScheduleBuilder object = (ScheduleBuilder) deserialize.readObject();

                deserialize.close();

                this.nameOfFileSchedule = object.nameOfFileSchedule;
                this.nameOfSchedule = object.nameOfSchedule;
                this.type = object.type;
                this.parity = object.parity;
            }catch (Exception e){
                Log.i("Notification", e.getMessage());
                return false;
            }

            return true;
        }

        return false;
    }

    public boolean save(Context context){
        if (nameOfFileSchedule != null){
            String path = getOptionsFilePath(context);

            File file = new File(path);
            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();

            try {
                ObjectOutputStream serialize = new ObjectOutputStream(
                        new FileOutputStream(file)
                );

                serialize.writeObject(
                        new ScheduleBuilder(
                                this.nameOfFileSchedule,
                                this.nameOfSchedule,
                                this.type,
                                this.parity)
                );

                serialize.close();
            }catch (Exception e){
                return false;
            }

            return true;
        }

        return false;
    }

    public boolean save(Schedule schedule, Context context){
        this.nameOfFileSchedule = schedule.getNameOfFileSchedule();
        this.nameOfSchedule = schedule.getNameOfSchedule();
        this.type = (byte) schedule.getType();
        this.parity = (byte) schedule.getParity();

        return save(context);
    }

    private String getOptionsFilePath(Context context){
        return new File(
                context.getFilesDir(),
                DataContract.MyFileManager.FILE_OF_SCHEDULE_DIRECTORY
                        + File.separator
                        + nameOfFileSchedule).getPath();
    }
}
