package com.example.schedule.Data;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.provider.BaseColumns;
import android.util.Log;

import com.example.schedule.Objects.Schedule;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.xml.namespace.QName;

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

    public static class MyFileManager {
        //Directory
        public final static String NO_INFO = "NO_INFO";
        public final static String DATA_DIRECTORY = "databases";
        public final static String FILE_OF_SCHEDULE_DIRECTORY = "Schedules";
        public final static String FILE_OF_OPTIONS_OF_DISCIPLINE_NOTIFICATION = "OptionsOfDisciplineNotification";
        public final static String MIGRATE_OPTIONS_DIRECTORY = "Options";
        public final static String MIGRATE_DATABASE_DIRECTORY = "Database";

        //Report
        public final static String REPORT_NO_PROBLEM = "is complete";
        public final static String REPORT_ERROR = "ERROR";

        //TODO Можно провести сериализацию класса Schedule. Что упростить получение параметров с расписания
        public static void createFileOfOptions(String path, Schedule schedule) {
            try {
                File file = new File(path);
                FileWriter writer = new FileWriter(file.getPath(), false);
                writer.append(String.valueOf(schedule.getType())).append("\n")
                        .append(schedule.getNameOfSchedule()).append("\n")
                        .append(String.valueOf(schedule.getParity()));

                writer.close();
            } catch (FileNotFoundException e) {
                Log.d("SAVE", "ERROR: MAIN FILE NOTE CREATED");
            } catch (Exception e) {
                Log.d("ERROR", "Что то явно пошло не по плану");
            }
        }

        //TODO Можно провести сериализацию класса Schedule. Что упростить получение параметров с расписания
        public static Schedule readFileOfOptions(String path) {
            File file = new File(path);
            Schedule schedule = new Schedule(file.getName());
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                //Первая строка содержит ИМЯ расписания: Читаем
                schedule.setType(reader.readLine());
                //Вторая строка содержит ТИП расписания: Читаем
                schedule.setNameOfSchedule(reader.readLine());
                //Третья строкаа сожержит параметры четности
                schedule.setParity(reader.readLine());
            } catch (FileNotFoundException e) {
                Log.i("ERROR", "ERROR: File is not founded");
            } catch (Exception e) {
                Log.i("ERROR", "Что то явно пошло не по плану");
            }

            return schedule;
        }

        /*TODO Требуется дописать удаление файла настроек уведемолений расписания
        *  Так как мы планируем сериализововать классы Schedule и OptionsOfNotificationsDiscipline
        *  имеет смысл дописать методы удалиний именно у этих классов и просто обратиться к ним*/
        public static void deleteDate(Context context, String name) {
            String path;
            File file;

            path = context.getFilesDir().getPath() +
                    File.separator +
                    FILE_OF_SCHEDULE_DIRECTORY +
                    File.separator +
                    name + ".txt";
            file = new File(path);
            if (file.exists()) {
                file.delete();
                Log.i("Delete DATA", "Deleted: file options of user schedule");
            }

            try {
                context.deleteDatabase("time" + name);
                Log.i("Delete DATA", "Deleted: TimeDB");
            } catch (Exception e) {
                Log.i("Delete DATA", "TimeDB not exist");
            }

            try {
                context.deleteDatabase("top" + name);
                Log.i("Delete DATA", "Deleted: DB_1");
            } catch (Exception e) {
                Log.i("Delete DATA", "DB_1 not exist");
            }


            try {
                context.deleteDatabase("lower" + name);
                Log.i("Delete DATA", "Deleted: DB_2");
            } catch (Exception e) {
                Log.i("Delete DATA", "DB_2 not exist");
            }
        }

        public static void deleteDir(File dir) {
            if (dir.isDirectory()) {
                File files[] = dir.listFiles();

                if (files.length != 0) {
                    for (File file : files) {
                        deleteDir(file);
                    }
                }
            }

            dir.delete();
        }

        /**
         * Метод importFiles(Context context,File fileToImport) неработате самостоятельно
         * Логические шаги его работы:
         * 1. Распаковка полученно файла(fileToImport)
         * 2. Проверка на наличие всех файлов согласно файлу основных параметров расписания (checkingForFiles(File dir))
         * 3. Распакованный пак переместить во внутренную память приложения (moveScheduleToInternalMemory(Context context, File dir))
         * 4. Удалить распакованные файлы из внейшней памяти (deleteDir(File dir))
         */
        public static boolean importFiles(Context context, File fileToImport) {
            //Плучим имя пакета
            String packageName;
            packageName = fileToImport.getName().substring(0, fileToImport.getName().indexOf("."));

            //Разархивируем полученный фаил
            if (unzipFile(fileToImport)) {
                File packageToImport = new File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        packageName);

                //Проверить на целостность
                if (checkingForFiles(packageToImport)
                ) {
                    if (moveScheduleToInternalMemory(context, packageToImport)) {
                        deleteDir(packageToImport);
                        return !packageToImport.exists();
                    }
                }
            }

            //TODO Если испорт неудался, то требуется удалить все рабочие и удачно перемещенные файлы
            return false;
        }

        private static boolean checkingForFiles(File dir) {
            boolean[] dbExist = new boolean[]
                    {
                            false,//Отвечает за DB1
                            false,//Отвечает за DB2
                            false //Отвечает за TimeDB
                    };
            int typeOfSchedule;

            String optionsFile = getExternalPathToFileOfSchedulesOptions(dir);
            if (optionsFile.equals("NULL"))
                return false;

            File scheduleOptionsFile = new File(optionsFile);
            try (BufferedReader reader = new BufferedReader(new FileReader(scheduleOptionsFile))) {
                typeOfSchedule = Integer.parseInt(reader.readLine());
            } catch (Exception e) {
                //Ошибка, чтение файла не удалось
                return false;
            }

            String[] databaseFilesNames = getExternalDatabaseList(dir);
            for (int index = 0; index < databaseFilesNames.length; index++) {
                if (databaseFilesNames[index].indexOf("lower") == 0) {
                    dbExist[0] = true;
                } else if (databaseFilesNames[index].indexOf("top") == 0) {
                    dbExist[1] = true;
                } else if (databaseFilesNames[index].indexOf("time") == 0) {
                    dbExist[2] = true;
                }
            }

            if (typeOfSchedule == MyAppSettings.SCHEDULE_TYPE_1) {
                if (dbExist[0] && dbExist[2])
                    return true;
            } else {
                if (dbExist[0] && dbExist[1] && dbExist[2])
                    return true;
            }

            return false;
        }

        private static String getExternalPathToFileOfSchedulesOptions(File dir) {
            StringBuilder path = new StringBuilder();

            path
                    .append(dir.getPath())
                    .append(File.separator)
                    .append(MIGRATE_OPTIONS_DIRECTORY)
                    .append(File.separator);

            String[] files = new File(path.toString()).list();

            if (files.length == 1) {
                path
                        .append(files[0]);

                return path.toString();
            } else {
                return "NULL";
            }
        }

        private static String[] getExternalDatabaseList(File dir) {
            StringBuilder path = new StringBuilder();

            path
                    .append(dir.getPath())
                    .append(File.separator)
                    .append(MIGRATE_DATABASE_DIRECTORY);

            return new File(path.toString()).list();
        }

        private static boolean moveScheduleToInternalMemory(Context context, File dir) {
            /*TODO МЫ ОСТАНОВИЛИСЬ ТУТ. ТЕПЕРЬ НАДО ПЕРЕПИСАТЬ ПЕРЕНОС ФАЙЛОВ
             *  1. Перенести параметры
             *  2. Перенести базы данных*/
            File[] files = dir.listFiles();

            if (!importFileOfScheduleOptions(context, files[0])) {
                return false;
            }
            if (!importFileOfDatabase(context, files[1])) {
                return false;
            }

            return true;
        }

        private static boolean importFileOfScheduleOptions(Context context, File dir) {
            File[] files = dir.listFiles();
            if (!copyFile(files[0], getInternalDirOfOptionsOfSchedule(context))) {
                return false;
            }

            return true;
        }

        private static File getInternalDirOfOptionsOfSchedule(Context context) {
            StringBuilder path = new StringBuilder();
            path
                    .append(context.getFilesDir())
                    .append(File.separator)
                    .append(MyFileManager.FILE_OF_SCHEDULE_DIRECTORY);

            return new File(path.toString());
        }

        private static boolean importFileOfDatabase(Context context, File dir) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                File[] files = dir.listFiles();
                File databaseDir = new File(context.getDataDir(), "databases");

                for (int index = 0; index < files.length; index++) {
                    if (!copyFile(files[index], databaseDir))
                        return false;
                }
            }

            return true;
        }

        /**
         * Метод exportFiles(Context context, String name) неработате самостоятельно
         * Логические шаги его работы:
         * 1. Создать пак на внешнем накопителе. (В паке должны быть дерриктория для
         * файла параметров расписания и дирректория для базданных) (createExternalDirToZip(Context context, String nameOfDir))
         * 2. Переместить в пак рабочие файлы (moveScheduleToExternalMemory(Context context, File[] dir, String name))
         * 3. Преобразовать все в архив (zipFile(File file))
         * 4. Удалить рабочий пак (deleteDir(File dir))
         */
        public static boolean exportFiles(Context context, String name) {
            File dirToZip = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "mSch" + name
            );

            File[] directories = createPackageToExport(dirToZip);

            if (moveScheduleToExternalMemory(context, directories, name)) {
                if (zipFile(dirToZip)) {
                    deleteDir(dirToZip);
                    if (!dirToZip.exists())
                        return true;
                }
            }

            return false;
        }

        private static File[] createPackageToExport(File dirToExport) {
            File[] directories = new File[2];

            //Создание директории для файла основных параметров расписания
            directories[0] = new File(dirToExport, MIGRATE_OPTIONS_DIRECTORY);
            directories[0].mkdirs();

            //Создание директории для баз данных
            directories[1] = new File(dirToExport, MIGRATE_DATABASE_DIRECTORY);
            directories[1].mkdirs();

            return directories;
        }

        private static boolean moveScheduleToExternalMemory(Context context, File[] dir, String name) {
            //Копируем основные параметры расписания
            if (!exportFileOfScheduleOptions(context, dir[0], name))
                return false;

            //Копируем базы данных
            if (!exportFileOfDatabase(context, dir[1], name))
                return false;

            return true;
        }

        private static boolean exportFileOfScheduleOptions(Context context, File dir, String name) {
            StringBuilder path = new StringBuilder();
            path
                    .append(context.getFilesDir())
                    .append(File.separator)
                    .append(FILE_OF_SCHEDULE_DIRECTORY)
                    .append(File.separator)
                    .append(name).append(".txt");

            if (!copyFile(new File(path.toString()), dir)) {
                return false;
            }

            return true;
        }

        private static boolean exportFileOfDatabase(Context context, File dir, String name) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                StringBuilder path = new StringBuilder();
                path
                        .append(context.getDataDir().getPath())
                        .append(File.separator)
                        .append("databases");


                //Перенос оасписания верхней недели
                copyFile(new File(path.toString(), UPPER_SCHEDULE + name), dir);

                //Перенос расписания нижней недели
                copyFile(new File(path.toString(), LOWER_SCHEDULE + name), dir);

                //Перенос расписания времени
                copyFile(new File(path.toString(), TIME_DB + name), dir);

                return true;
            }

            return false;
        }

        private static boolean copyFile(File original, File directory) {
            try {
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
            } catch (Exception e) {
                return false;
            }
        }

        public static boolean zipFile(File file) {
            try {
                ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(file.getPath() + ".zip"));

                addDirectoryToZip(zipOut, file);
                zipOut.close();
                return true;
            } catch (Exception e) {
                Log.i("EXPORT ERROR", "DataContract: (метод zipFile) неудлось провести архивацию");
                return false;
            }
        }

        private static void addDirectoryToZip(ZipOutputStream zipOut, File file) throws Exception {
            File[] files = file.listFiles();

            for (int index = 0; index < files.length; index++) {
                if (files[index].isDirectory()) {
                    addDirectoryToZip(zipOut, files[index]);
                    continue;
                }

                FileInputStream fileStream = new FileInputStream(files[index]);
                zipOut.putNextEntry(
                        new ZipEntry(files[index].getPath()));

                //FIXME Возможна ошибка, по причине выхода за граници массива(объем может измеряться в переменной long, а не int
                byte[] buffer = new byte[fileStream.available()]; //проверить момент, какой размерности получится массив
                zipOut.write(buffer, 0, fileStream.read(buffer));

                zipOut.closeEntry();
                fileStream.close();
            }
        }

        public static boolean unzipFile(File file) {
            try {
                //Архив
                ZipFile zipFile = new ZipFile(file);

                Enumeration<?> entries = zipFile.entries();

                while (entries.hasMoreElements()) {
                    ZipEntry entry = (ZipEntry) entries.nextElement();
                    String entryName = entry.getName();

                    File exportFile = new File(entry.getName());
                    if (!exportFile.getParentFile().exists()) {
                        exportFile.getParentFile().mkdirs();
                    }

                    exportFile.createNewFile();

                    InputStream inputStream = zipFile.getInputStream(entry);
                    FileOutputStream outputStream = new FileOutputStream(exportFile);

                    //FIXME Возможна ошибка, по причине выхода за граници массива(объем может измеряться в переменной long, а не int
                    byte[] buffer = new byte[inputStream.available()]; //проверить момент, какой размерности получится массив
                    inputStream.read(buffer, 0, buffer.length);
                    outputStream.write(buffer, 0, buffer.length);

                    inputStream.close();
                    outputStream.close();
                }
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    public static class MyAppSettings{
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
