package com.example.schedule.MyNotifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;

import com.example.schedule.Data.DataContract;
import com.example.schedule.IntentHelper;
import com.example.schedule.Objects.Discipline;
import com.example.schedule.Objects.Schedule;
import com.example.schedule.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**Применяется паттерн Singleton по той причине, что должен существоать лишь один контроллер
 * уведомлений (касательно уведомлений по расписанию).
 * Работа класса:
 * 1. Инстанцирование, полчение объекта класса
 *      1.1 По той причне, что мы работаем с классом Singleton, перед его созданием мы должны
 *      проверить существует ли уже объект данного класса instance = null?
 *      (instanceMyDisciplineNotificationManager(Context context, ArrayList<TimeSchedule> times))
 *      1.2 Если не существует, то создаем объект через private конструктор и устанавливаем его
 *      в instance и возвращаем его. В случае существования объекта возвращаем instance;
 * 2.
 * */
public class MyDisciplineNotificationManager {
    //Для реализации патттерна Singleton
    private static MyDisciplineNotificationManager instance;
    private final int BEFORE_START = 0;
    private final int TIME_TO_GO = 4;
    private final int START = 1;
    private final int BEFORE_FINISH = 2;
    private final int FINISH = 3;

    //Рабочие поля класса
    private Context context;
    private static AlarmManager alarmManager;
    private Schedule schedule;

    public static final String CHANEL_ID_DISCIPLINE = "NOTIFICATION_CHANEL_ID_DISCIPLINE";
    public static final int NOTIFICATION_ID_DISCIPLINE = 10;

    private MyDisciplineNotificationManager(){}

    //Блок по созданию объекта
    public static MyDisciplineNotificationManager getInstance(
            Context appContext,
            Schedule schedule){
        if (instance == null)
            instance = new MyDisciplineNotificationManager(appContext, schedule);

        return instance;
    }

    private MyDisciplineNotificationManager(Context appContext, Schedule schedule){
        this.context = appContext;
        alarmManager = (AlarmManager)appContext.getSystemService(ALARM_SERVICE);
        this.schedule = schedule;

        /**
         * 1. Получить из schedule список дисциплин
         * 2. Перебирая каждую дисциплину согласно настройкам приложения создавать временные точки
         * 3. Обновить уведомления на следующий деньпри достижении которых будет демонстрироваться
         * уведомление*/
        updateAllAlarm();
    }

    public void setNull(){
        instance = null;
    }

    public void updateAllAlarm(){
        MyDisciplineNotificationManager.Options options;
        options = new Options(context, schedule.getNameOfFileSchedule());

        ArrayList<Discipline> disciplines = schedule.getDisciplines();

        for (int index = 0; index < disciplines.size(); index++){
            Discipline discipline = disciplines.get(index);

            if (options.getBeforeStart()){
                setAlarm(BEFORE_START, discipline, options);
                Log.i("Notification", "Уведомление: До начала осталось...");
            }

            if (options.getTimeToGo()){
                setAlarm(TIME_TO_GO, discipline, options);
                Log.i("Notification", "Уведомление: Время для выхода...");
            }

            if (options.getStart()){
                setAlarm(START, discipline, options);
                Log.i("Notification", "Уведомление: Пара началась...");
            }

            if (options.getBeforeFinish()){
                setAlarm(BEFORE_FINISH, discipline, options);
                Log.i("Notification", "Уведомление: До конца осталось...");
            }

            if (options.getFinish()){
                setAlarm(FINISH, discipline, options);
                Log.i("Notification", "Уведомление: Пара закончилась...");
            }
        }

        setAlarmToUpdateDisciplineOfNextDay();
    }

    public void deleteAllAlarm(){
        MyDisciplineNotificationManager.Options options;
        options = new Options(context, schedule.getNameOfFileSchedule());

        ArrayList<Discipline> disciplines = schedule.getDisciplines();

        for (int index = 0; index < disciplines.size(); index++){
            Discipline discipline = disciplines.get(index);

            if (options.getBeforeStart()){
                deleteAlarm(BEFORE_START, discipline, options);
                Log.i("Notification", "Уведомление: До начала осталось...");
            }

            if (options.getStart()){
                deleteAlarm(START, discipline, options);
                Log.i("Notification", "Уведомление: Пара началась...");
            }

            if (options.getBeforeFinish()){
                deleteAlarm(BEFORE_FINISH, discipline, options);
                Log.i("Notification", "Уведомление: До конца осталось...");
            }

            if (options.getFinish()){
                deleteAlarm(FINISH, discipline, options);
                Log.i("Notification", "Уведомление: Пара закончилась...");
            }
        }

        deleteAlarmToUpdateDisciplineOfNextDay();
    }

    public void deleteOptionsFile(Context context){
        Options.delete(context, schedule.getNameOfFileSchedule());
    }

    //TODO При нажатии открыть расписание на день.
    //TODO Если закончилась последняя пара, то сообщить дальше пар нет.
    private void setAlarm(int type, Discipline discipline, MyDisciplineNotificationManager.Options options){
        String[] typeOfDiscipline = context.getResources().getStringArray(R.array.type_of_discipline);
        StringBuilder message = new StringBuilder();

        int id;

        Calendar calendar = Calendar.getInstance();

        switch (type){
            case BEFORE_START:{
                message
                        .append(context.getString(R.string.Notification_Discipline_BeforeStart))
                        .append(" ")
                        .append(options.getBeforeStartMin())
                        .append(" ")
                        .append(context.getString(R.string.Notification_Discipline_Minute))
                        .append("\n");

                calendar.set(Calendar.HOUR_OF_DAY, discipline.getStartHour());
                calendar.set(Calendar.MINUTE, discipline.getStartMinute());
                calendar.add(Calendar.MINUTE, -options.getBeforeStartMin());
                calendar.set(Calendar.SECOND, 0);

                id = (discipline.getPosition() + 1) * 10 + 1;
            }break;

            case TIME_TO_GO:{
                message
                        .append(context.getString(R.string.Notification_Discipline_TimeToGo))
                        .append(" ")
                        .append(options.getBeforeStartMin())
                        .append(" ")
                        .append(context.getString(R.string.Notification_Discipline_Minute))
                        .append("\n");

                calendar.set(Calendar.HOUR_OF_DAY, discipline.getStartHour());
                calendar.set(Calendar.MINUTE, discipline.getStartMinute());
                calendar.add(Calendar.MINUTE, -options.getTimeToGoMin());
                calendar.set(Calendar.SECOND, 0);

                id = (discipline.getPosition() + 1) * 10 + 5;
            }break;

            case START:{
                message
                        .append(context.getString(
                                R.string.Notification_Discipline_Started))
                        .append("\n");

                calendar.set(Calendar.HOUR_OF_DAY, discipline.getStartHour());
                calendar.set(Calendar.MINUTE, discipline.getStartMinute());
                calendar.set(Calendar.SECOND, 0);


                id = (discipline.getPosition() + 1) * 10 + 2;
            }break;

            case BEFORE_FINISH:{
                message
                        .append(context.getString(
                                R.string.Notification_Discipline_BeforeFinish))
                        .append(" ")
                        .append(options.getBeforeFinishMin())
                        .append(" ")
                        .append(context.getString(R.string.Notification_Discipline_Minute))
                        .append("\n");

                calendar.set(Calendar.HOUR_OF_DAY, discipline.getFinishHour());
                calendar.set(Calendar.MINUTE, discipline.getFinishMinute());
                calendar.add(Calendar.MINUTE, -options.getBeforeFinishMin());
                calendar.set(Calendar.SECOND, 0);

                id = (discipline.getPosition() + 1) * 10 + 3;
            }break;

            case FINISH:{
                message
                        .append(context.getString(
                                R.string.Notification_Discipline_Finish))
                        .append("\n");

                calendar.set(Calendar.HOUR_OF_DAY, discipline.getFinishHour());
                calendar.set(Calendar.MINUTE, discipline.getFinishMinute());
                calendar.set(Calendar.SECOND, 0);


                id = (discipline.getPosition() + 1) * 10 + 4;
            }break;

            default:
                id = 0;
        }

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

        Intent intent = new Intent(context, MyAlarm.class);

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
                "(" + typeOfDiscipline[discipline.getType()] + ") " + discipline.getDisciplineName());

        //Message
        intent.putExtra(
                IntentHelper.NOTIFICATION_MESSAGE,
                message.toString());

        //Упаковываем посылку
        PendingIntent pIntent  =
                PendingIntent.getBroadcast(
                        context,
                        id,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        //Установка будильника
        Log.i("Notification", "Уведомление произойдет в:" + DateUtils.formatDateTime(context, calendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE) + " ; "  + DateUtils.formatDateTime(context, calendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME));
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
    }

    private void deleteAlarm(int type, Discipline discipline, MyDisciplineNotificationManager.Options options){
        Calendar calendar = Calendar.getInstance();
        int id = 0;

        switch (type) {
            case BEFORE_START: {
                calendar.set(Calendar.HOUR_OF_DAY, discipline.getStartHour());
                calendar.set(Calendar.MINUTE, discipline.getStartMinute());
                calendar.add(Calendar.MINUTE, -options.getBeforeStartMin());
                calendar.set(Calendar.SECOND, 0);

                id = (discipline.getPosition() + 1) * 10 + 1;
            }
            break;

            case START: {
                calendar.set(Calendar.HOUR_OF_DAY, discipline.getStartHour());
                calendar.set(Calendar.MINUTE, discipline.getStartMinute());
                calendar.set(Calendar.SECOND, 0);


                id = (discipline.getPosition() + 1) * 10 + 2;
            }
            break;

            case BEFORE_FINISH: {
                calendar.set(Calendar.HOUR_OF_DAY, discipline.getFinishHour());
                calendar.set(Calendar.MINUTE, discipline.getFinishMinute());
                calendar.add(Calendar.MINUTE, -options.getBeforeFinishMin());
                calendar.set(Calendar.SECOND, 0);

                id = (discipline.getPosition() + 1) * 10 + 3;
            }

            case FINISH: {
                calendar.set(Calendar.HOUR_OF_DAY, discipline.getFinishHour());
                calendar.set(Calendar.MINUTE, discipline.getFinishMinute());
                calendar.set(Calendar.SECOND, 0);


                id = (discipline.getPosition() + 1) * 10 + 4;
            }
            break;
        }

            PendingIntent pIntent  =
                    PendingIntent.getBroadcast(
                            context,
                            id,
                            new Intent(),
                            PendingIntent.FLAG_CANCEL_CURRENT);

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
    }

    private void setAlarmToUpdateDisciplineOfNextDay(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 4);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Intent intent = new Intent(context, MyAlarm.class);
        intent.putExtra(IntentHelper.COMMAND, IntentHelper.COMMAND_NOTIFICATION_UpdateAlarmToDay);
        intent.putExtra(IntentHelper.SCHEDULE, schedule);

        Log.i("Notification", "Будильники обновятся в: " + calendar.get(Calendar.DAY_OF_MONTH) + ":" + calendar.get(Calendar.MONTH) + "; " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
        PendingIntent pIntent  =
                PendingIntent.getBroadcast(
                        context,
                        -1,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
        Log.i("Notification", "Будильник на обновление уведомлений установлен");
    }

    private void deleteAlarmToUpdateDisciplineOfNextDay(){
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

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
    }

    public static class Options implements Serializable {

        private int beforeStart;
        private int timeToGo;
        private boolean start;
        private int beforeFinish;
        private boolean finish;
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
                int beforeStart,
                int timeToGo,
                boolean start,
                int beforeFinish,
                boolean finish
        ){
            this.beforeStart = beforeStart;
            this.timeToGo = timeToGo;
            this.start = start;
            this.beforeFinish = beforeFinish;
            this.finish = finish;
        }

        /**Get methods*/
        public boolean getBeforeStart(){
            return beforeStart != -1;
        }
        public int getBeforeStartMin(){
            return beforeStart;
        }

        public boolean getTimeToGo(){
            return timeToGo != -1;
        }
        public int getTimeToGoMin(){
            return timeToGo;
        }

        public boolean getStart(){
            return start;
        }

        public boolean getBeforeFinish(){
            return beforeFinish != -1;
        }
        public int getBeforeFinishMin(){
            return beforeFinish;
        }

        public boolean getFinish(){
            return finish;
        }

        public String getPathToFile(){
            return pathToFile;
        }

        /**Set methods*/
        public void setBeforeStart(int beforeStart){
            this.beforeStart = beforeStart;
        }

        public void setTimeToGo(int timeToGo){
            this.timeToGo = timeToGo;
        }

        public void setStart(boolean start) {
            this.start = start;
        }

        public void setBeforeFinish(int beforeFinish) {
            this.beforeFinish = beforeFinish;
        }

        public void setFinish(boolean finish) {
            this.finish = finish;
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

                    this.beforeStart = object.getBeforeStartMin();
                    this.timeToGo = object.getTimeToGoMin();
                    this.start = object.getStart();
                    this.beforeFinish = object.getBeforeFinishMin();
                    this.finish = object.getFinish();
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
                            new FileOutputStream(pathToFile)
                    );

                    serialize.writeObject(
                            new Options(
                                    this.beforeStart,
                                    this.timeToGo,
                                    this.start,
                                    this.beforeFinish,
                                    this.finish)
                    );

                    serialize.close();
                }catch (Exception e){
                    return false;
                }

                return true;
            }

            return false;
        }

        public static boolean delete(Context context, String name){
            File file = new File(getOptionsFilePath(context, name));

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
            this.beforeStart = -1;
            this.timeToGo = -1;
            this.start = false;
            this.beforeFinish = -1;
            this.finish = false;
        }
    }
}
