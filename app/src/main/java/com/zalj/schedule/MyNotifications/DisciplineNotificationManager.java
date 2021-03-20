package com.zalj.schedule.MyNotifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.util.Log;

import com.zalj.schedule.Data.DataContract;
import com.zalj.schedule.IntentHelper;
import com.zalj.schedule.MyNotifications.NotificationTypes.BeforeFinishNotification;
import com.zalj.schedule.MyNotifications.NotificationTypes.BeforeStartNotification;
import com.zalj.schedule.MyNotifications.NotificationTypes.FinishNotification;
import com.zalj.schedule.MyNotifications.NotificationTypes.FinishOfDayNotification;
import com.zalj.schedule.MyNotifications.NotificationTypes.StartNotification;
import com.zalj.schedule.MyNotifications.NotificationTypes.TimeToGoNotification;
import com.zalj.schedule.Objects.Discipline;
import com.zalj.schedule.Objects.Schedule;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class DisciplineNotificationManager {
    private static DisciplineNotificationManager manager;

    public static DisciplineNotificationManager getInstance(){
        if (manager == null){
            DisciplineNotificationManager manager = new DisciplineNotificationManager();
            DisciplineNotificationManager.manager = manager;
            return manager;
        }else {
            return manager;
        }
    }

    public DisciplineNotificationManager(){
    }

    public static void updateAllAlarm(Context context, Schedule schedule){
        DisciplineNotificationManager.Options options;
        options = new Options(context, schedule.getNameOfFileSchedule());
        ArrayList<Discipline> disciplines = schedule.getDisciplines();

        //Устанавливаем будильники для первой паре отдельно, по той причине, что для того что бы
        //оказаться на ней, надой выйти из дома.
        if (disciplines.size() != 0){

            //Устанавливаем уведомления для подготовки для выхода
            if (options.getTimeToGo()){
                Discipline discipline = disciplines.get(0);

                TimeToGoNotification notification =
                        new TimeToGoNotification(context, discipline, options);
                notification.setAlarm();
            }

            //Устанавливаем будильники для оставшихся дисциплин
            for (int index = 0; index < disciplines.size(); index++){
                Discipline discipline;
                discipline = disciplines.get(index);

                if (options.getBeforeStartOfDiscipline()){
                    BeforeStartNotification notification =
                            new BeforeStartNotification(context, discipline, options);
                    notification.setAlarm();
                }

                if (options.getStartOfDiscipline()){
                    StartNotification notification =
                            new StartNotification(context, discipline, options);
                    notification.setAlarm();
                }

                if (options.getBeforeFinishOfDiscipline()){
                    BeforeFinishNotification notification =
                            new BeforeFinishNotification(context, discipline, options);
                    notification.setAlarm();
                }

                if (options.getFinishOfDiscipline()){
                    FinishNotification notification =
                            new FinishNotification(context, discipline, options);
                    notification.setAlarm();
                }
            }

            //Устанавливаем уведомления для подготовки для выхода
            if (options.getFinishOfDay()){
                Discipline discipline = disciplines.get(disciplines.size() - 1);

                FinishOfDayNotification notification =
                        new FinishOfDayNotification(context, discipline, options);
                notification.setAlarm();
            }
        }

        setAlarmToUpdateDisciplineOfNextDay(context, schedule);
    }

    public static void deleteAllAlarm(Context context, Schedule schedule){
        DisciplineNotificationManager.Options options;
        options = new Options(context, schedule.getNameOfFileSchedule());
        ArrayList<Discipline> disciplines = schedule.getDisciplines();

        if (disciplines.size() != 0){
            if (options.getTimeToGo()){
                Discipline discipline = disciplines.get(0);

                TimeToGoNotification notification =
                        new TimeToGoNotification(context, discipline, options);
                notification.deleteAlarm();
            }

            //Устанавливаем будильники для оставшихся дисциплин
            for (int index = 0; index < disciplines.size(); index++){
                Discipline discipline;
                discipline = disciplines.get(index);

                if (options.getBeforeStartOfDiscipline()){
                    BeforeStartNotification notification =
                            new BeforeStartNotification(context, discipline, options);
                    notification.deleteAlarm();
                }

                if (options.getStartOfDiscipline()){
                    StartNotification notification =
                            new StartNotification(context, discipline, options);
                    notification.deleteAlarm();
                }

                if (options.getBeforeFinishOfDiscipline()){
                    BeforeFinishNotification notification =
                            new BeforeFinishNotification(context, discipline, options);
                    notification.deleteAlarm();
                }

                if (options.getFinishOfDiscipline()){
                    FinishNotification notification =
                            new FinishNotification(context, discipline, options);
                    notification.deleteAlarm();
                }
            }

            //Устанавливаем уведомления для подготовки для выхода
            if (options.getFinishOfDay()){
                Discipline discipline = disciplines.get(disciplines.size() - 1);

                FinishOfDayNotification notification =
                        new FinishOfDayNotification(context, discipline, options);
                notification.deleteAlarm();
            }
        }

        deleteAlarmToUpdateDisciplineOfNextDay(context);
    }

    public static void deleteOptionsFile(Context context, String name){
        Options.delete(context, name);
    }

    private static void setAlarmToUpdateDisciplineOfNextDay(Context context, Schedule schedule){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 2);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(AlarmReceiver.UPDATE_ALARM);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.putExtra(IntentHelper.COMMAND, IntentHelper.COMMAND_NOTIFICATION_UpdateAppData);
        intent.putExtra(IntentHelper.SCHEDULE_NAME, schedule.getNameOfFileSchedule());

        Log.i("Notification", "Будильники обновятся в: " + DateUtils.formatDateTime(context, calendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE) + " ; "  + DateUtils.formatDateTime(context, calendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME));
        PendingIntent pIntent  =
                PendingIntent.getBroadcast(
                        context,
                        -1,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
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

                    DisciplineNotificationManager.Options object =
                            (DisciplineNotificationManager.Options) deserialize.readObject();

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
