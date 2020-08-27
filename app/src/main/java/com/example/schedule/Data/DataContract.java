package com.example.schedule.Data;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.provider.BaseColumns;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.schedule.Objects.Schedule;
import com.example.schedule.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class DataContract
{
    public final static String UPPER_SCHEDULE = "top";
    public final static String LOWER_SCHEDULE = "lower";
    public final static String TIME_DB = "time";

    final static String
        VALUE_TYPE_INTEGER = " INTEGER ",
        VALUE_TYPE_TEXT = " TEXT ";

    final static String
        DEFAULT_STRING_CREATE_TABLE = " CREATE TABLE ",
        DEFAULT_STRING_ID = " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT ";

    //База данных не зависит от типа расписания(одиночное/множественное).
    static class DisciplinesDB implements BaseColumns
    {
        static final String
                _ID = BaseColumns._ID,
                DAY_OF_WEEK = "day_of_week",
                POSITION = "position",
                TIME = "time",
                DISCIPLINE_NAME = "disciplineName",
                TYPE = "type",
                BUILDING = "building",
                AUDITORIUM = "auditorium";
    }

    static class TimeDB implements BaseColumns
    {
        static final String
                _ID = BaseColumns._ID,
                NUMBER = "number",
                START_HOUR = "startHour",
                START_MINUTE = "startMinute",
                FINISH_HOUR = "finishHour",
                FINISH_MINUTE = "finishMinute";
    }

    public static class MyFileManager
    {
        //Directory
        public final static String DATA_DIRECTORY = "database";
        public final static String FILE_OF_SCHEDULE_DIRECTORY = "Schedules";
        public final static String MIGRATE_OPTIONS_DIRECTORY = "Options";
        public final static String MIGRATE_DATABASE_DIRECTORY = "Database";

        //Report
        public final static String REPORT_NO_PROBLEM = "is complete";
        public final static String REPORT_ERROR = "ERROR";

        public static void createFileOfOptions(String path, Schedule schedule)
        {
            try
            {
                File file = new File(path);
                FileWriter writer = new FileWriter(file.getPath(), false);
                writer.append(String.valueOf(schedule.getType())).append("\n")
                        .append(schedule.getNameOfSchedule()).append("\n")
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
                //Первая строка содержит ИМЯ расписания: Читаем
                schedule.setType(reader.readLine());
                //Вторая строка содержит ТИП расписания: Читаем
                schedule.setNameOfSchedule(reader.readLine());
                //Третья строкаа сожержит параметры четности
                schedule.setParity(reader.readLine());
            }catch (FileNotFoundException e)
            {
                Log.i("ERROR","ERROR: File is not founded");
            }catch (Exception e)
            {
                Log.i("ERROR", "Что то явно пошло не по плану");
            }

            return schedule;
        }

        public static void deleteDate(Context context, String name)
        {
            String path;
            File file;
            int typeOfSchedule;

            path = context.getFilesDir().getPath() +
                    File.separator +
                    FILE_OF_SCHEDULE_DIRECTORY +
                    File.separator +
                    name + ".txt" +
                    ".txt";
            file = new File(path);
            if (file.exists())
            {
                file.delete();
                Log.i("Delete DATA", "Deleted: file options of user schedule");
            }

            try {
                context.deleteDatabase("time" + name);
                Log.i("Delete DATA", "Deleted: TimeDB");
            }catch (Exception e)
            {
                Log.i("Delete DATA", "TimeDB not exist");
            }

            try {
                context.deleteDatabase("top" + name);
                Log.i("Delete DATA", "Deleted: DB_1");
            }catch (Exception e)
            {
                Log.i("Delete DATA", "DB_1 not exist");
            }


            try {
                context.deleteDatabase("lower" + name);
                Log.i("Delete DATA", "Deleted: DB_2");
            }catch (Exception e)
            {
                Log.i("Delete DATA", "DB_2 not exist");
            }
        }

        public static boolean importFiles(Context context, String name)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            {

                boolean[] dbExist = new boolean[]
                        {
                                false,//Отвечает за DB1
                                false,//Отвечает за DB2
                                false //Отвечает за TimeDB
                        };
                int typeOfSchedule;

                //Путь к коренным директориям
                StringBuilder
                        pathToExternalStorage = new StringBuilder(),
                        pathToInternalStorage = new StringBuilder();
                String[] nameOfFile;
                File fileOfExternal;
                //Путь к внутренней директории
                File fileOfInternal;

                //Путь к паку
                pathToExternalStorage
                        .append(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath())
                        .append(File.separator)
                        .append(name);

                //Перед импортом основных параметров получаем информацию о типе распиания
                fileOfExternal = new File(pathToExternalStorage.toString(), MIGRATE_OPTIONS_DIRECTORY);
                nameOfFile = fileOfExternal.list();
                if (nameOfFile != null)
                {
                    fileOfExternal = new File(fileOfExternal, nameOfFile[0]);

                    try (BufferedReader reader = new BufferedReader(new FileReader(fileOfExternal)))
                    {
                        typeOfSchedule = Integer.parseInt(reader.readLine());
                        if (!(typeOfSchedule == MyAppSettings.SCHEDULE_TYPE_1 || typeOfSchedule == MyAppSettings.SCHEDULE_TYPE_2))
                        {
                            //Ошибка, в файле неправильная запист типа расписания
                            return false;
                        }
                    }catch (Exception e)
                    {
                        //Ошибка, чтение файла не удалось
                        return false;
                    }

                    //Перенос основного фала параметров
                    pathToInternalStorage
                            .append(context.getFilesDir().getPath());
                    fileOfInternal = new File(pathToInternalStorage.toString(), FILE_OF_SCHEDULE_DIRECTORY);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    {
                        try
                        {
                            Files.move(fileOfExternal.toPath(), fileOfInternal.toPath().resolve(fileOfExternal.getName()), StandardCopyOption.REPLACE_EXISTING);
                        } catch (Exception e) {
                            //Ошибка, неудалось переместить фаил
                            return false;
                        }
        /*                if (fileOfExternal.renameTo(fileOfInternal))
                        {
                            Log.i("Import", "File moved: " + nameOfFile[0]);
                        }else
                        {
                            Log.i("Import", "File not moved: " + nameOfFile[0]);
                            return context.getString(R.string.FileManager_Error_mainParamsOfScheduleNotMoved);
                        }*/
                    }else
                    {
                        if (!copyFile(fileOfExternal, fileOfInternal))
                            return false;
                    }
                }else
                {
                    //Ошибка, фаил параметров не обнаружен
                    return false;
                }

                //Обновление корневых путей
                pathToExternalStorage
                        .append(MIGRATE_DATABASE_DIRECTORY);
                pathToInternalStorage = new StringBuilder();
                pathToInternalStorage
                        .append(context.getDataDir().getPath());

                fileOfExternal = new File(pathToExternalStorage.toString());
                fileOfInternal = new File(pathToInternalStorage.toString());

                //Проверка на наличие и отсутсвие DB
                nameOfFile = fileOfExternal.list();
                if (nameOfFile != null)
                {
                    //Перенос DB
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    {
                        for (int index = 0; index < nameOfFile.length; index++)
                        {
                            fileOfExternal = new File(pathToExternalStorage.toString(), nameOfFile[index]);

                            if (nameOfFile[index].indexOf("lower") == 0) {
                                dbExist[0] = true;
                                try
                                {
                                    Files.move(fileOfExternal.toPath(), fileOfInternal.toPath().resolve(fileOfExternal.getName()));
                                }catch (Exception e)
                                {
                                    return false;
                                }
                            } else if (nameOfFile[index].indexOf("top") == 0) {
                                dbExist[1] = true;
                                try
                                {
                                    Files.move(fileOfExternal.toPath(), fileOfInternal.toPath().resolve(fileOfExternal.getName()));
                                }catch (Exception e)
                                {
                                    return false;
                                }
                            } else if (nameOfFile[index].indexOf("time") == 0) {
                                dbExist[2] = true;
                                try
                                {
                                    Files.move(fileOfExternal.toPath(), fileOfInternal.toPath().resolve(fileOfExternal.getName()));
                                }catch (Exception e)
                                {
                                    return false;
                                }
                            }
                        }
                    }else
                    {
                        for (int index = 0; index < nameOfFile.length; index++)
                        {
                            fileOfExternal = new File(pathToExternalStorage.toString(), nameOfFile[index]);

                            if (nameOfFile[index].indexOf("lower") == 0)
                            {
                                dbExist[0] = true;
                                if (!copyFile(fileOfExternal, fileOfInternal))
                                    return false;
                            } else if (nameOfFile[index].indexOf("top") == 0)
                            {
                                dbExist[1] = true;
                                if (!copyFile(fileOfExternal, fileOfInternal))
                                    return false;
                            } else if (nameOfFile[index].indexOf("time") == 0)
                            {
                                dbExist[2] = true;
                                if (!copyFile(fileOfExternal, fileOfInternal))
                                    return false;
                            }
                        }
                    }

                    //Окнчательная проверка
                    if (typeOfSchedule == MyAppSettings.SCHEDULE_TYPE_2)
                    {
                        //Ошибка, фаил второго расписания не обноружен
                        return dbExist[1] && dbExist[0];
                    }
                }else
                {
                    //Ошибка, файлы DB небыли обнаружены
                    return false;
                }

                return true;
            }else
            {
                //Ошибка версия Android устарела.
                return false;
            }
        }

        public static boolean exportFiles(Context context, String nameOfOptionsFile)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            {
                //Путь к коренным директориям
                StringBuilder
                        pathToExternalStorage = new StringBuilder(),
                        pathToInternalStorage = new StringBuilder();
                String[] nameOfFile;
                File fileOfExternal;
                File fileOfInternal;

                //Создание пакета
                pathToExternalStorage
                        .append(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath())
                        .append(File.separator)
                        .append("mSch")
                        .append(nameOfOptionsFile);
                fileOfExternal = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "mSch" + nameOfOptionsFile);
                fileOfExternal.mkdirs();

                //Обновление коренных путей
                pathToInternalStorage
                        .append(context.getFilesDir())
                        .append(File.separator)
                        .append(FILE_OF_SCHEDULE_DIRECTORY);
                pathToExternalStorage
                        .append(File.separator)
                        .append(MIGRATE_OPTIONS_DIRECTORY);

                //создание папки для фала с параметрами расписания
                fileOfExternal = new File(pathToExternalStorage.toString());
                fileOfExternal.mkdirs();

                //Получаем список файлов в директории
                fileOfInternal = new File(pathToInternalStorage.toString());
                nameOfFile = fileOfInternal.list();

                if (nameOfFile != null)
                {
                    //Фаил с параметрами расписания
                    fileOfInternal = new File(pathToInternalStorage.toString(), nameOfFile[0]);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    {
                        try
                        {
                            Files.copy(fileOfInternal.toPath(), fileOfExternal.toPath().resolve(fileOfInternal.getName()), StandardCopyOption.REPLACE_EXISTING);
                        }catch (Exception e)
                        {
                        /*fileOfInternal = new File(pathToInternalStorage.toString() + File.separator + FILE_OF_SCHEDULE_DIRECTORY);
                        Log.i("ERROR", "DataContract.FileManager.exportFiles: Неудалось скопировать фаил " + fileOfInternal.getPath());*/

                            //Ошибка,
                            return false;
                        }
                    }else
                    {
                        if (!copyFile(fileOfInternal, fileOfExternal))
                            return false;
                    }
                }else
                {
                    //Ошибка,
                    return false;
                }

                //Обновление коренных файлов
                pathToInternalStorage = new StringBuilder();
                pathToInternalStorage
                        .append(context.getDataDir().getPath())
                        .append(File.separator)
                        .append("databases");
                pathToExternalStorage = new StringBuilder();
                pathToExternalStorage
                        .append(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath())
                        .append(File.separator)
                        .append("mSch")
                        .append(nameOfOptionsFile)
                        .append(File.separator)
                        .append(MIGRATE_DATABASE_DIRECTORY);

                //создание папки для DB
                fileOfExternal = new File(pathToExternalStorage.toString());
                fileOfExternal.mkdirs();

                fileOfInternal = new File(pathToInternalStorage.toString());
                nameOfFile = fileOfInternal.list();

                if (nameOfFile != null) {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        {
                            for (String s : nameOfFile) {
                                fileOfInternal = new File(pathToInternalStorage.toString(), s);
                                Files.copy(fileOfInternal.toPath(), fileOfExternal.toPath().resolve(fileOfInternal.getName()), StandardCopyOption.REPLACE_EXISTING);
                            }
                        } else
                        {
                            for (String s : nameOfFile)
                            {
                                fileOfInternal = new File(pathToInternalStorage.toString(), s);
                                copyFile(fileOfInternal, fileOfExternal);
                            }
                        }
                    } catch (Exception e)
                    {
                        //Ошибка,
                        return false;
                    }
                }else
                {
                    //Ошибка,
                    return false;
                }

                return true;
            }else
            {
                //Ошибка,
                return false;
            }
        }

        private static boolean copyFile(File original, File directory)
        {
            try
            {
                if (!original.exists()) {
                    return false;
                }

                directory = new File(directory.getPath(), original.getName());
                if (!directory.exists()) {
                    directory.createNewFile();
                }

                FileChannel source;
                FileChannel destination;
                source = new FileInputStream(original).getChannel();
                destination = new FileOutputStream(directory).getChannel();
                if (destination != null && source != null) {
                    destination.transferFrom(source, 0, source.size());
                }
                if (source != null) {
                    source.close();
                }
                if (destination != null) {
                    destination.close();
                }

                return true;
            }catch (Exception e)
            {
                return false;
            }
        }
    }

    public static class MyAppSettings
    {
        //Name file of settings
        public static final String LAST_VIEWED_SCHEDULE = "LAST_ACCESS";

        //Field
        public static final String LAST_SCHEDULE = "LAST_SCHEDULE"; //File name it is create time.

        //Schedule params
        public static final int NULL_INFO = -1;
        public static final int YES = 1;
        public static final int NO = 0;
        public static final int SCHEDULE_TYPE_1 = 7;
        public static final int SCHEDULE_TYPE_2 = 14;
        public static final int parity = 0;
        public static final String NULL = "NULL";

        //Permission
        public static final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 1;
    }
}
