package com.zalj.schedule.MyNotifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;

import com.zalj.schedule.Data.DataContract;
import com.zalj.schedule.IntentHelper;
import com.zalj.schedule.Objects.Discipline;
import com.zalj.schedule.Objects.Schedule;
import com.zalj.schedule.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class MyDisciplineNotificationManager {
    private static final int TIME_TO_GO = 0;
    private static final int BEFORE_START_OF_DISCIPLINE = 1;
    private static final int START_OF_DISCIPLINE = 2;
    private static final int BEFORE_FINISH_OF_DISCIPLINE = 3;
    private static final int FINISH_OF_DISCIPLINE = 4;
    private static final int FINISH_OF_DAY = 5;

    public static final String CHANEL_ID_DISCIPLINE = "NOTIFICATION_CHANEL_ID_DISCIPLINE";
    public static final int NOTIFICATION_ID_DISCIPLINE = 10;

    public static void updateAllAlarm(Context context, Schedule schedule){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        MyDisciplineNotificationManager.Options options;
        options = new Options(context, schedule.getNameOfFileSchedule());

        ArrayList<Discipline> disciplines = schedule.getDisciplines();

        //Устанавливаем будильники для первой паре отдельно, по той причине, что для того что бы
        //оказаться на ней, надой выйти из дома.
        if (disciplines.size() != 0){
            Discipline discipline = disciplines.get(0);

            if (options.getTimeToGo()){
                setAlarmToTimeToGo(context, discipline, options);
                Log.i("Notification", "Уведомление: Время для выхода...");
            }

            //Устанавливаем будильники для оставшихся дисциплин
            for (int index = 0; index < disciplines.size(); index++){
                discipline = disciplines.get(index);

                if (options.getBeforeStartOfDiscipline()){
                    setAlarmToBeforeStartOfDiscipline(context, discipline, options);
                    Log.i("Notification", "Уведомление: До начала осталось...");
                }

                if (options.getStartOfDiscipline()){
                    setAlarmToStartOfDiscipline(context, discipline, options);
                    Log.i("Notification", "Уведомление: Пара началась...");
                }

                if (options.getBeforeFinishOfDiscipline()){
                    setAlarmBeforeFinishOfDiscipline(context, discipline, options);
                    Log.i("Notification", "Уведомление: До конца осталось...");
                }

                if (options.getFinishOfDiscipline()){
                    setAlarmFinishOfDiscipline(context, discipline, options);
                    Log.i("Notification", "Уведомление: Пара закончилась...");
                }

                if (options.getFinishOfDay()){
                    setAlarmFinishOfDay(context, discipline, options);
                    Log.i("Notification", "Уведомление: Конец учебного дня...");
                }
            }

            //TODO оповестить что учебый день окончен
        }

        setAlarmToUpdateDisciplineOfNextDay(context, schedule, alarmManager);
    }

    public static void deleteAllAlarm(Context context, Schedule schedule){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        MyDisciplineNotificationManager.Options options;
        options = new Options(context, schedule.getNameOfFileSchedule());

        ArrayList<Discipline> disciplines = schedule.getDisciplines();
        if (disciplines.size() != 0){
            Discipline discipline;
            if (options.getTimeToGo()){
                discipline = disciplines.get(0);
                deleteAlarmToTimeToGo(context, discipline);
            }

            for (int index = 0; index < disciplines.size(); index++){
                discipline = disciplines.get(index);

                if (options.getBeforeStartOfDiscipline()){
                    deleteAlarmToBeforeStartOfDiscipline(context, discipline);
                    Log.i("Notification", "Будильник -До начала осталось- удален");
                }

                if (options.getStartOfDiscipline()){
                    deleteAlarmToStartOfDiscipline(context, discipline);
                    Log.i("Notification", "Будильник -Пара началась- удален");
                }

                if (options.getBeforeFinishOfDiscipline()){
                    deleteAlarmToBeforeFinishOfDiscipline(context, discipline);
                    Log.i("Notification", "Будильник -До конца осталось- удален");
                }

                if (options.getFinishOfDiscipline()){
                    deleteAlarmToFinishOfDiscipline(context, discipline);
                    Log.i("Notification", "Будильник -Пара закончилась- удален");
                }

                if (options.getFinishOfDay()){
                    deleteAlarmToFinishOfDay(context, discipline);
                    Log.i("Notification", "Будильник -Конец учебного дня- удален");
                }
            }
        }

        deleteAlarmToUpdateDisciplineOfNextDay(context);
    }

    public static void deleteOptionsFile(Context context, String name){
        Options.delete(context, name);
    }

    private static void setAlarmToTimeToGo(Context context,Discipline discipline, Options options){
        StringBuilder message = new StringBuilder();
        String[] typeOfDiscipline = context.getResources().getStringArray(R.array.type_of_discipline);
        Calendar calendar = Calendar.getInstance();
        int id;


        message
                .append(context.getString(R.string.Notification_Discipline_TimeToGo))
                .append(" ")
                .append(options.getTimeToGoMin())
                .append(" ")
                .append(context.getString(R.string.Notification_Discipline_Minute))
                .append("\n");

        calendar.set(Calendar.HOUR_OF_DAY, discipline.getStartHour());
        calendar.set(Calendar.MINUTE, discipline.getStartMinute());
        calendar.add(Calendar.MINUTE, -options.getTimeToGoMin());
        calendar.set(Calendar.SECOND, 0);

        id = (discipline.getPosition() + 1) * 10 + TIME_TO_GO;

        setAlarm(
                context,
                id,
                "(" + typeOfDiscipline[discipline.getType()] + ") " + discipline.getDisciplineName(),
                message.toString(),
                calendar);
    }

    private static void setAlarmToBeforeStartOfDiscipline(Context context, Discipline discipline, Options options){
        StringBuilder message = new StringBuilder();
        String[] typeOfDiscipline = context.getResources().getStringArray(R.array.type_of_discipline);
        Calendar calendar = Calendar.getInstance();
        int id;

        message
                .append(context.getString(R.string.Notification_Discipline_BeforeStartOfDiscipline))
                .append(" ")
                .append(options.getBeforeStartMin())
                .append(" ")
                .append(context.getString(R.string.Notification_Discipline_Minute))
                .append("\n");

        calendar.set(Calendar.HOUR_OF_DAY, discipline.getStartHour());
        calendar.set(Calendar.MINUTE, discipline.getStartMinute());
        calendar.add(Calendar.MINUTE, -options.getBeforeStartMin());
        calendar.set(Calendar.SECOND, 0);

        id = (discipline.getPosition() + 1) * 10 + BEFORE_START_OF_DISCIPLINE;

        //Class room
        if (!(TextUtils.isEmpty(discipline.getAuditorium()) || discipline.getAuditorium().equals(""))){
            message
                    .append(context.getString(R.string.activity_ScheduleOfDay_dialog_auditory))
                    .append(": ")
                    .append(discipline.getAuditorium())
                    .append("\n");
        }

        //Building
        if (!(TextUtils.isEmpty(discipline.getBuilding()) || discipline.getBuilding().equals(""))){
            message
                    .append(context.getString(R.string.activity_ScheduleOfDay_dialog_building))
                    .append(": ")
                    .append(discipline.getBuilding());
        }

        setAlarm(
                context,
                id,
                "(" + typeOfDiscipline[discipline.getType()] + ") " + discipline.getDisciplineName(),
                message.toString(),
                calendar);
    }

    private static void setAlarmToStartOfDiscipline(Context context, Discipline discipline, Options options){
        StringBuilder message = new StringBuilder();
        String[] typeOfDiscipline = context.getResources().getStringArray(R.array.type_of_discipline);
        Calendar calendar = Calendar.getInstance();
        int id;

        message
                .append(context.getString(
                        R.string.Notification_Discipline_StartedOfDiscipline))
                .append("\n");

        calendar.set(Calendar.HOUR_OF_DAY, discipline.getStartHour());
        calendar.set(Calendar.MINUTE, discipline.getStartMinute());
        calendar.set(Calendar.SECOND, 0);

        id = (discipline.getPosition() + 1) * 10 + START_OF_DISCIPLINE;

        //Class room
        if (!(TextUtils.isEmpty(discipline.getAuditorium()) || discipline.getAuditorium().equals(""))){
            message
                    .append(context.getString(R.string.activity_ScheduleOfDay_dialog_auditory))
                    .append(": ")
                    .append(discipline.getAuditorium())
                    .append("\n");
        }

        //Building
        if (!(TextUtils.isEmpty(discipline.getBuilding()) || discipline.getBuilding().equals(""))){
            message
                    .append(context.getString(R.string.activity_ScheduleOfDay_dialog_building))
                    .append(": ")
                    .append(discipline.getBuilding());
        }

        setAlarm(
                context,
                id,
                "(" + typeOfDiscipline[discipline.getType()] + ") " + discipline.getDisciplineName(),
                message.toString(),
                calendar);
    }

    private static void setAlarmBeforeFinishOfDiscipline(Context context, Discipline discipline, Options options){
        StringBuilder message = new StringBuilder();
        String[] typeOfDiscipline = context.getResources().getStringArray(R.array.type_of_discipline);
        Calendar calendar = Calendar.getInstance();
        int id;

        message
                .append(context.getString(
                        R.string.Notification_Discipline_BeforeFinishOfDiscipline))
                .append(" ")
                .append(options.getBeforeFinishMin())
                .append(" ")
                .append(context.getString(R.string.Notification_Discipline_Minute))
                .append("\n");

        calendar.set(Calendar.HOUR_OF_DAY, discipline.getFinishHour());
        calendar.set(Calendar.MINUTE, discipline.getFinishMinute());
        calendar.add(Calendar.MINUTE, -options.getBeforeFinishMin());
        calendar.set(Calendar.SECOND, 0);

        id = (discipline.getPosition() + 1) * 10 + BEFORE_FINISH_OF_DISCIPLINE;

        //Class room
        if (!(TextUtils.isEmpty(discipline.getAuditorium()) || discipline.getAuditorium().equals(""))){
            message
                    .append(context.getString(R.string.activity_ScheduleOfDay_dialog_auditory))
                    .append(": ")
                    .append(discipline.getAuditorium())
                    .append("\n");
        }

        //Building
        if (!(TextUtils.isEmpty(discipline.getBuilding()) || discipline.getBuilding().equals(""))){
            message
                    .append(context.getString(R.string.activity_ScheduleOfDay_dialog_building))
                    .append(": ")
                    .append(discipline.getBuilding());
        }

        setAlarm(
                context,
                id,
                "(" + typeOfDiscipline[discipline.getType()] + ") " + discipline.getDisciplineName(),
                message.toString(),
                calendar);
    }

    private static void setAlarmFinishOfDiscipline(Context context, Discipline discipline, Options options){
        StringBuilder message = new StringBuilder();
        String[] typeOfDiscipline = context.getResources().getStringArray(R.array.type_of_discipline);
        Calendar calendar = Calendar.getInstance();
        int id;

        message
                .append(context.getString(
                        R.string.Notification_Discipline_FinishOfDiscipline))
                .append("\n");

        calendar.set(Calendar.HOUR_OF_DAY, discipline.getFinishHour());
        calendar.set(Calendar.MINUTE, discipline.getFinishMinute());
        calendar.set(Calendar.SECOND, 0);


        id = (discipline.getPosition() + 1) * 10 + FINISH_OF_DISCIPLINE;

        //Class room
        if (!(TextUtils.isEmpty(discipline.getAuditorium()) || discipline.getAuditorium().equals(""))){
            message
                    .append(context.getString(R.string.activity_ScheduleOfDay_dialog_auditory))
                    .append(": ")
                    .append(discipline.getAuditorium())
                    .append("\n");
        }

        //Building
        if (!(TextUtils.isEmpty(discipline.getBuilding()) || discipline.getBuilding().equals(""))){
            message
                    .append(context.getString(R.string.activity_ScheduleOfDay_dialog_building))
                    .append(": ")
                    .append(discipline.getBuilding());
        }

        setAlarm(
                context,
                id,
                "(" + typeOfDiscipline[discipline.getType()] + ") " + discipline.getDisciplineName(),
                message.toString(),
                calendar);
    }

    private static void setAlarmFinishOfDay(Context context, Discipline discipline, Options options){
        StringBuilder message = new StringBuilder();
        String[] typeOfDiscipline = context.getResources().getStringArray(R.array.type_of_discipline);
        Calendar calendar = Calendar.getInstance();
        int id;

        message
                .append(context.getString(
                        R.string.Notification_Discipline_FinishOfDiscipline))
                .append(context.getString(
                        R.string.Notification_Discipline_FinishOfDay))
                .append("\n");

        calendar.set(Calendar.HOUR_OF_DAY, discipline.getFinishHour());
        calendar.set(Calendar.MINUTE, discipline.getFinishMinute());
        calendar.set(Calendar.SECOND, 0);


        id = (discipline.getPosition() + 1) * 10 + FINISH_OF_DAY;

        //Class room
        if (!(TextUtils.isEmpty(discipline.getAuditorium()) || discipline.getAuditorium().equals(""))){
            message
                    .append(context.getString(R.string.activity_ScheduleOfDay_dialog_auditory))
                    .append(": ")
                    .append(discipline.getAuditorium())
                    .append("\n");
        }

        //Building
        if (!(TextUtils.isEmpty(discipline.getBuilding()) || discipline.getBuilding().equals(""))){
            message
                    .append(context.getString(R.string.activity_ScheduleOfDay_dialog_building))
                    .append(": ")
                    .append(discipline.getBuilding());
        }

        setAlarm(
                context,
                id,
                "(" + typeOfDiscipline[discipline.getType()] + ") " + discipline.getDisciplineName(),
                message.toString(),
                calendar);
    }

    private static void setAlarm(Context context, int id, String title, String message, Calendar calendar){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(AlarmReceiver.SHOW_NOTIFICATION);

        //Устанавливаем команду
        intent.putExtra(
                IntentHelper.COMMAND,
                IntentHelper.COMMAND_NOTIFICATION_SetAlarm);

        //ChanelId
        intent.putExtra(
                IntentHelper.CHANEL_ID,
                CHANEL_ID_DISCIPLINE);

        //ChanelName
        intent.putExtra(
                IntentHelper.CHANEL_NAME,
                context.getString(R.string.Notification_chanelName_Discipline));

        //ID
        intent.putExtra(
                IntentHelper.NOTIFICATION_ID,
                NOTIFICATION_ID_DISCIPLINE);

        //Title
        intent.putExtra(
                IntentHelper.NOTIFICATION_TITLE,
                title);

        //Message
        intent.putExtra(
                IntentHelper.NOTIFICATION_MESSAGE,
                message);

        //Упаковываем посылку
        PendingIntent pIntent  =
                PendingIntent.getBroadcast(
                        context,
                        id,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        //Установка будильника
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
    }

    private static void deleteAlarmToTimeToGo(Context context, Discipline discipline){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        int id;
        id = (discipline.getPosition() + 1) * 10 + TIME_TO_GO;

        PendingIntent pIntent  =
                PendingIntent.getBroadcast(
                        context,
                        id,
                        new Intent(),
                        PendingIntent.FLAG_CANCEL_CURRENT);

        alarmManager.cancel(pIntent);
    }

    private static void deleteAlarmToBeforeStartOfDiscipline(Context context, Discipline discipline){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        int id;
        id = (discipline.getPosition() + 1) * 10 + BEFORE_START_OF_DISCIPLINE;

        PendingIntent pIntent  =
                PendingIntent.getBroadcast(
                        context,
                        id,
                        new Intent(),
                        PendingIntent.FLAG_CANCEL_CURRENT);

        alarmManager.cancel(pIntent);
    }

    private static void deleteAlarmToStartOfDiscipline(Context context, Discipline discipline){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        int id;
        id = (discipline.getPosition() + 1) * 10 + START_OF_DISCIPLINE;

        PendingIntent pIntent  =
                PendingIntent.getBroadcast(
                        context,
                        id,
                        new Intent(),
                        PendingIntent.FLAG_CANCEL_CURRENT);

        alarmManager.cancel(pIntent);
    }

    private static void deleteAlarmToBeforeFinishOfDiscipline(Context context, Discipline discipline){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        int id;
        id = (discipline.getPosition() + 1) * 10 + BEFORE_FINISH_OF_DISCIPLINE;

        PendingIntent pIntent  =
                PendingIntent.getBroadcast(
                        context,
                        id,
                        new Intent(),
                        PendingIntent.FLAG_CANCEL_CURRENT);

        alarmManager.cancel(pIntent);
    }

    private static void deleteAlarmToFinishOfDiscipline(Context context, Discipline discipline){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        int id;
        id = (discipline.getPosition() + 1) * 10 + FINISH_OF_DISCIPLINE;

        PendingIntent pIntent  =
                PendingIntent.getBroadcast(
                        context,
                        id,
                        new Intent(),
                        PendingIntent.FLAG_CANCEL_CURRENT);

        alarmManager.cancel(pIntent);
    }
    
    private static void deleteAlarmToFinishOfDay(Context context, Discipline discipline){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        int id;
        id = (discipline.getPosition() + 1) * 10 + FINISH_OF_DAY;

        PendingIntent pIntent  =
                PendingIntent.getBroadcast(
                        context,
                        id,
                        new Intent(),
                        PendingIntent.FLAG_CANCEL_CURRENT);

        alarmManager.cancel(pIntent);
    }

    private static void setAlarmToUpdateDisciplineOfNextDay(Context context, Schedule schedule, AlarmManager alarmManager){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 2);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(AlarmReceiver.UPDATE_ALARM);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.putExtra(IntentHelper.COMMAND, IntentHelper.COMMAND_NOTIFICATION_UpdateAlarmToDay);
        intent.putExtra(IntentHelper.SCHEDULE_NAME, schedule.getNameOfFileSchedule());

        Log.i("Notification", "Будильники обновятся в: " + DateUtils.formatDateTime(context, calendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE) + " ; "  + DateUtils.formatDateTime(context, calendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME));
        PendingIntent pIntent  =
                PendingIntent.getBroadcast(
                        context,
                        -1,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
        /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
        else
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);*/
        Log.i("Notification", "Будильник на обновление уведомлений установлен");
    }

    private static void deleteAlarmToUpdateDisciplineOfNextDay(Context context){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 4);
        calendar.set(Calendar.MINUTE, 0);

        PendingIntent pIntent  =
                PendingIntent.getBroadcast(
                        context,
                        -1,
                        new Intent(),
                        PendingIntent.FLAG_CANCEL_CURRENT);

        alarmManager.cancel(pIntent);
    }

    public static class Options implements Serializable {

        //TODO Решение на время.
        /**Предупреждение для пользователя о том, что данная особенность приложения не надежна.*/
        private boolean accept;

        private int timeToGo;
        private int beforeStartOfDiscipline;
        private boolean startOfDiscipline;
        private int beforeFinishOfDiscipline;
        private boolean finishOfDiscipline;
        private boolean finishOfDay;
        private String pathToFile;

        public Options(Context context, String name){

            if (!name.equals(DataContract.MyFileManager.NO_INFO))
            {
                this.pathToFile = getOptionsFilePath(context, name);
                File file = new File(pathToFile);

                try {
                    if (file.exists()){
                        read();
                    }else {
                        setDefaultParams();
                        save();
                    }

                }catch (Exception e){
                }
            }else{
                setDefaultParams();
            }
        }

        public Options(){
            setDefaultParams();
        }

        private Options(
                int timeToGo,
                int beforeStartOfDiscipline,
                boolean startOfDiscipline,
                int beforeFinishOfDiscipline,
                boolean finishOfDiscipline,
                boolean finishOfDay
        ){
            this.timeToGo = timeToGo;
            this.beforeStartOfDiscipline = beforeStartOfDiscipline;
            this.startOfDiscipline = startOfDiscipline;
            this.beforeFinishOfDiscipline = beforeFinishOfDiscipline;
            this.finishOfDiscipline = finishOfDiscipline;
            this.finishOfDay = finishOfDay;
        }

        /**Get methods*/
        public boolean getAccept(){
            return accept;
        }

        public boolean getBeforeStartOfDiscipline(){
            return beforeStartOfDiscipline != -1;
        }
        public int getBeforeStartMin(){
            return beforeStartOfDiscipline;
        }

        public boolean getTimeToGo(){
            return timeToGo != -1;
        }
        public int getTimeToGoMin(){
            return timeToGo;
        }

        public boolean getStartOfDiscipline(){
            return startOfDiscipline;
        }

        public boolean getBeforeFinishOfDiscipline(){
            return beforeFinishOfDiscipline != -1;
        }
        public int getBeforeFinishMin(){
            return beforeFinishOfDiscipline;
        }

        public boolean getFinishOfDiscipline(){
            return finishOfDiscipline;
        }

        public boolean getFinishOfDay(){
            return finishOfDay;
        }

        public String getPathToFile(){
            return pathToFile;
        }

        /**Set methods*/
        public void setAccept(boolean accept){
            this.accept = accept;
        }

        public void setBeforeStartOfDiscipline(int beforeStartOfDiscipline){
            this.beforeStartOfDiscipline = beforeStartOfDiscipline;
        }

        public void setTimeToGo(int timeToGo){
            this.timeToGo = timeToGo;
        }

        public void setStartOfDiscipline(boolean startOfDiscipline) {
            this.startOfDiscipline = startOfDiscipline;
        }

        public void setBeforeFinishOfDiscipline(int beforeFinishOfDiscipline) {
            this.beforeFinishOfDiscipline = beforeFinishOfDiscipline;
        }

        public void setFinishOfDiscipline(boolean finishOfDiscipline) {
            this.finishOfDiscipline = finishOfDiscipline;
        }

        public void setFinishOfDay(boolean finishOfDay){
            this.finishOfDay = finishOfDay;
        }

        public void setPathToFile(Context context ,String name){
            this.pathToFile = getOptionsFilePath(context, name);
        }

        public boolean read(){
            if (pathToFile != null){
                try {
                    ObjectInputStream deserialize = new ObjectInputStream(
                            new FileInputStream(pathToFile)
                    );

                    MyDisciplineNotificationManager.Options object =
                            (MyDisciplineNotificationManager.Options) deserialize.readObject();

                    deserialize.close();

                    this.timeToGo = object.getTimeToGoMin();
                    this.beforeStartOfDiscipline = object.getBeforeStartMin();
                    this.startOfDiscipline = object.getStartOfDiscipline();
                    this.beforeFinishOfDiscipline = object.getBeforeFinishMin();
                    this.finishOfDiscipline = object.getFinishOfDiscipline();
                    this.finishOfDay = object.getFinishOfDay();
                }catch (Exception e){
                    return false;
                }

                return true;
            }

            return false;
        }

        public boolean save(){
            if (pathToFile != null){
                File file = new File(pathToFile);
                if (!file.getParentFile().exists())
                    file.getParentFile().mkdirs();

                try {
                    ObjectOutputStream serialize = new ObjectOutputStream(
                            new FileOutputStream(file)
                    );

                    serialize.writeObject(
                            new Options(
                                    this.timeToGo,
                                    this.beforeStartOfDiscipline,
                                    this.startOfDiscipline,
                                    this.beforeFinishOfDiscipline,
                                    this.finishOfDiscipline,
                                    this.finishOfDay)
                    );

                    serialize.close();
                }catch (Exception e){
                    return false;
                }

                return true;
            }

            return false;
        }

        public static boolean delete(Context context, String nameOfFile){
            File file = new File(getOptionsFilePath(context, nameOfFile));

            return file.delete();
        }

        private static String getOptionsFilePath(Context context, String name){
            return new File(
                    context.getFilesDir(),
                    DataContract.MyFileManager.FILE_OF_OPTIONS_OF_DISCIPLINE_NOTIFICATION
                            + File.separator
                            + "N_options" + name).getPath();
        }

        private void setDefaultParams(){
            this.accept = false;
            this.beforeStartOfDiscipline = -1;
            this.timeToGo = -1;
            this.startOfDiscipline = false;
            this.beforeFinishOfDiscipline = -1;
            this.finishOfDiscipline = false;
            this.finishOfDay = false;
        }
    }
}
