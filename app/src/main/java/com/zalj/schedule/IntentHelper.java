package com.zalj.schedule;

public class IntentHelper
{
    //DEFAULT
    /**Применять в качестве удаления любой информации*/
    public static final int RESULT_DELETED = -2;
    /**Применять в качестве ответа при ошибке*/
    public static final int RESULT_ERROR = -222;
    /**Применять при передачи экземпляра класса Schedule*/
    public static final String SCHEDULE = "SCHEDULE";
    /**Применять при передачи имения класса Schedule*/
    public static final String SCHEDULE_NAME = "SCHEDULE_NAME";

    //SelectScheduleActivity -> MainActivity
    public static final String NAME = "NAME";

    //MainActivity -> SelectScheduleActivity
    public static final String COMMAND = "COMMAND"; //Применяется для отправки команд.
        public static final int COMMAND_NOTIFICATION_UpdateAppData = 10; //Установки новых будильников на следующий день
        public static final int COMMAND_NOTIFICATION_UpdateAlarm = 11; //Обновить существующий будильник
        public static final int COMMAND_NOTIFICATION_SetAlarm = 12; //Установить новый будильник
    //(requestCode)MainActivity -> SelectScheduleActivity
    public static final int SELECT_SCHEDULE = 100;
    public static final int EDIT_SCHEDULE = 101;
    public static final int OPEN_SETTINGS = 102;

    //(requestCode)SelectScheduleActivity -> ScheduleCreatingActivity
    public static final int CREATE_NEW_SCHEDULE = 20;
    public static final int SCHEDULE_OPTIONS = 21;
    //(resultCode)SelectScheduleActivity -> ScheduleOptions

    //Intent. Notification
    public static final String NOTIFICATION = "NOTIFICATION";
    public static final String OPTIONS_OF_DISCIPLINE_NOTIFICATION = "OPTIONS_OF_DISCIPLINE_NOTIFICATION";
    public static final String CHANEL_ID = "CHANEL_ID";
    public static final String CHANEL_NAME = "CHANEL_NAME";
    public static final String NOTIFICATION_ID = "ID_OF_NOTIFICATION";
    public static final String NOTIFICATION_TITLE = "TITLE_OF_NOTIFICATION";
    public static final String NOTIFICATION_MESSAGE = "MESSAGE_OF_NOTIFICATION";
    public static final String ID_OF_SMALL_ICON_OF_NOTIFICATION = "ID_OF_SMALL_ICON_OF_NOTIFICATION";
    public static final String ID_OF_ACTIVITY_TO_SHOW = "ID_OF_ACTIVITY_TO_SHOW";

}
