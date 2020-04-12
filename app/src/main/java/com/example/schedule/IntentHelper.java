package com.example.schedule;

public class IntentHelper
{
    //DEFAULT
    public static final int RESULT_DELETE = -2;
    public static final String SCHEDULE = "SCHEDULE";

    //SelectScheduleActivity -> MainActivity
    public static final String NAME = "NAME";

    //MainActivity -> SelectScheduleActivity
    public static final String COMMAND = "COMMAND";
    //(requestCode)MainActivity -> SelectScheduleActivity
    public static final int SELECT_SCHEDULE = 100;
    public static final int EDIT_SCHEDULE = 101;

    //(requestCode)SelectScheduleActivity -> ScheduleCreatingActivity
    public static final int CREATE_NEW_SCHEDULE = 20;
    public static final int SCHEDULE_OPTIONS = 21;
    //(resultCode)SelectScheduleActivity -> ScheduleOptions
}
