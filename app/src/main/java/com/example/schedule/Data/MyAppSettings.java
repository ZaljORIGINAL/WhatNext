package com.example.schedule.Data;

import android.content.SharedPreferences;
import android.util.Log;

import com.example.schedule.Objects.Schedule;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

public class MyAppSettings
{
    //Name file of settings
    public static final String LAST_VIEWED_SCHEDULE = "LAST_ACCESS";

    //Field
    public static final String LAST_SCHEDULE = "LAST_SCHEDULE"; //File name it is create time.

    //Value
    public static final int NULL_INFO = -1;
    public static final int YES = 1;
    public static final int NO = 0;
    public static final int SCHEDULE_TYPE_1 = 7;
    public static final int SCHEDULE_TYPE_2 = 14;
    public static final int parity = 0;
    public static final String NULL = "NULL";

    public static void createFileOfOptions(String path, Schedule schedule)
    {
        try
        {
            File file = new File(path);
            FileWriter writer = new FileWriter(file.getPath(), false);
            writer.append(schedule.getNameOfSchedule()).append("\n")
                    .append(String.valueOf(schedule.getType())).append("\n")
                    .append(String.valueOf(schedule.getParity()));

            writer.close();
        }catch (FileNotFoundException e)
        {
            Log.d("SAVE","ERROR: MAIN FILE NOTE CREATED");
        }catch (Exception e)
        {
            Log.d("ERROR", "Что то явно пошло не по плану");
        }
    }

    public static Schedule readFileOfOptions(String path)
    {
        File file  = new File(path);
        Schedule schedule = new Schedule(file.getName());
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            //Первая строка содержит ИМЯ расписания: Читем
            schedule.setNameOfSchedule(reader.readLine());
            //Вторая строка содержит ТИП расписания: Читаем
            schedule.setType(reader.readLine());
            //Третья строкаа сожержит параметры четности
            schedule.setParity(reader.readLine());
        }catch (FileNotFoundException e)
        {
            Log.i("SAVE","ERROR: File is not founded");
        }catch (Exception e)
        {
            Log.i("ERROR", "Что то явно пошло не по плану");
        }

        return schedule;
    }
}
