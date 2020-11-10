package com.zalj.schedule.Objects;

import android.content.Context;
import android.os.Environment;
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
    private final static long serialVersionUID = 0;

    private String nameOfFileSchedule;  /*Фаил в котором хранятся пареметры Расписаня, в качестве имени указывается дата создания*/
    private String pathToScheduleParams;/*Путь к файлу хранящий основные параметры расписани. Путь может обращаться ко внутренней памяти или к внешней*/
    private String nameOfSchedule;      /*Наименование расписания*/
    private byte type;                  /*Тип расписания (двойное/одинарное)*/
    private byte parity;                /*Четность расписания, для отображения одного вида расписания в зависимости от недели*/

    //Заполняется после
    private ArrayList<TimeSchedule> times;
    private ArrayList<Discipline> disciplines;  /*Расписание на актуальный день*/

    public ScheduleBuilder(){};

    public static ScheduleBuilder getInternalSchedule(Context context, String nameOfFileSchedule){
        StringBuilder path = new StringBuilder();
        path
                .append(context.getFilesDir())
                .append(File.separator)
                .append(DataContract.MyFileManager.FILE_OF_SCHEDULE_DIRECTORY)
                .append(File.separator)
                .append(nameOfFileSchedule);

        ScheduleBuilder scheduleBuilder = new ScheduleBuilder(nameOfFileSchedule, path.toString());
        scheduleBuilder.read();

        return scheduleBuilder;
    }

    public static ScheduleBuilder getExternalSchedule(Context context, String nameOfFileSchedule){
        StringBuilder path = new StringBuilder();
        path
                .append(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
                .append(File.separator)
                .append("mSch" + nameOfFileSchedule)
                .append(File.separator)
                .append(DataContract.MyFileManager.MIGRATE_OPTIONS_DIRECTORY)
                .append(File.separator)
                .append(nameOfFileSchedule);

        ScheduleBuilder scheduleBuilder = new ScheduleBuilder(nameOfFileSchedule, path.toString());
        scheduleBuilder.read();

        return scheduleBuilder;
    }

    private ScheduleBuilder(String nameOfFileSchedule,String pathToScheduleParams){
        this.nameOfFileSchedule = nameOfFileSchedule;
        this.pathToScheduleParams = pathToScheduleParams;
    }

    public ScheduleBuilder(Context context, Schedule schedule){
        this.nameOfFileSchedule = schedule.getNameOfFileSchedule();
        this.nameOfSchedule = schedule.getNameOfSchedule();
        this.parity = (byte) schedule.getParity();
        this.type = (byte) schedule.getType();

        StringBuilder path = new StringBuilder();
        path
                .append(context.getFilesDir())
                .append(File.separator)
                .append(DataContract.MyFileManager.FILE_OF_SCHEDULE_DIRECTORY)
                .append(File.separator)
                .append(nameOfFileSchedule);

        this.pathToScheduleParams = path.toString();
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

    public boolean read(){
        if (nameOfFileSchedule != null && pathToScheduleParams != null){
            try {
                ObjectInputStream deserialize = new ObjectInputStream(
                        new FileInputStream(pathToScheduleParams)
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

    public boolean save(){
        if (nameOfFileSchedule != null){
            File file = new File(pathToScheduleParams);
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

    private String getOptionsFilePath(Context context) {
        return new File(
                context.getFilesDir(),
                DataContract.MyFileManager.FILE_OF_SCHEDULE_DIRECTORY
                        + File.separator
                        + nameOfFileSchedule).getPath();
    }
}
