/**
 * SelectScheduleActivity - activity. Для выбора расписания которое будет отображаться пользователю.
 *
 * Пользователь в праве выбрать предложенное расписание в элементе scheduleList или же создать новое кликнув fab.
 *
 * При клике на fab вызывается диологове окно в котором требуется задать наименование нового расписания*/
package com.zalj.schedule.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.zalj.schedule.Adapters.ChooseListAdapter;
import com.zalj.schedule.Adapters.SchedulesAdapter;
import com.zalj.schedule.Data.DataContract;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zalj.schedule.IntentHelper;
import com.zalj.schedule.R;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static com.zalj.schedule.Data.DataContract.MyAppSettings.PERMISSION_REQUEST_EXTERNAL_STORAGE;

public class SelectScheduleActivity extends AppCompatActivity implements SchedulesAdapter.iItemClickListener
{
    //Файлы
    private File file;
    private List<String> files;

    //View activity
    private RecyclerView scheduleList;
    private SchedulesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_schedule);

        FloatingActionButton fab = findViewById(R.id.addNewSchedule);
        fab.setOnClickListener(v -> createNewSchedule());

        try {
            String path = getFilesDir().getPath() +
                    File.separator +
                    DataContract.MyFileManager.FILE_OF_SCHEDULE_DIRECTORY;
            file = new File(path);
            String[] filesArr;
            if (!file.exists()) {
                file.mkdir();
            }
            filesArr = file.list();
            files = Arrays.asList(filesArr);
        }catch (Exception e) {
            Log.e("FILE", "Error in class SelectScheduleActivity");
        }

        scheduleList = findViewById(R.id.scheduleList);
        adapter = new SchedulesAdapter(this, files, this);
        scheduleList.setAdapter(adapter);
        scheduleList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select_schedule, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.importSchedule: {
                //Провверка на доступ к внешней памяти
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    Log.i("Copy/Import/Export", "Внешняя память доступна");

                    //Проверка разрешения на доступ к хранилищу
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Log.i("Copy/Import/Export", "Разрешение на внешнее хранилище получено");

                        //Получаем путь к папке Download во внешнейй памяти
                        findScheduleFile(this);
                    } else {
                        Log.i("Copy/Import/Export", "Разрешение на внешнее хранилище не получено, запрошиваю доступ");

                        //Запрашиваем разрешение у пользователя. Статья: https://habr.com/ru/post/278945/
                        ActivityCompat.requestPermissions(this,
                                new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                PERMISSION_REQUEST_EXTERNAL_STORAGE);
                    }
                }else {
                    Log.i("Copy/Import/Export", "Внешняя память не доступна. " + Environment.getExternalStorageState());
                    Toast.makeText(this, R.string.SelectScheduleActivity_Toast_storageIsNotAvailable, Toast.LENGTH_LONG).show();
                }
            } break;

            case R.id.settings:{
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivityForResult(intent, IntentHelper.OPEN_SETTINGS);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent();
        intent.putExtra(IntentHelper.NAME, files.get(position));
        setResult(IntentHelper.SELECT_SCHEDULE, intent);

        finish();
    }

    private void createNewSchedule() {
        Intent intent = new Intent(this, ScheduleBuilderActivity.class);

        intent.putExtra(IntentHelper.COMMAND, IntentHelper.CREATE_NEW_SCHEDULE);
        startActivityForResult(intent, IntentHelper.CREATE_NEW_SCHEDULE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IntentHelper.CREATE_NEW_SCHEDULE: {
                if (resultCode == RESULT_OK) {
                    updateList();
                }
            }break;

            case IntentHelper.SCHEDULE_OPTIONS: {
                if (resultCode == RESULT_OK || resultCode == IntentHelper.RESULT_DELETED) {
                    updateList();
                }
            }break;

            case IntentHelper.OPEN_SETTINGS:{

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_REQUEST_EXTERNAL_STORAGE:{
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("UpdateUp", "Разрешение на установку получено");
                }
            }
        }
    }

    private void updateList() {
        //Обнаволи массив имен
        files = Arrays.asList(file.list());
        //Сообщаем адаптеру что мы обновили данные
        adapter = new SchedulesAdapter(this, files, this);
        scheduleList.setAdapter(adapter);
    }

    private void findScheduleFile(final Context context) {
        Log.i("Copy/Import/Export", "Начало поиска файлов");

        String[] files = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS).getPath())
                .list();

        //Фильтр
        files = filterForRarFile(files);

        //Диалоговое окно для выбора требуемых файлов.
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.SelectScheduleActivity_Dialog_ChooseFile);

        if (files.length == 0) {
            dialog.setMessage(R.string.SelectScheduleActivity_Dialog_NotFound);
            dialog.setPositiveButton(R.string.Standard_dialog_positive_button,
                    (dialog1, which) -> {

                    });
        }else {
            View dialogView = View.inflate(this, R.layout.dialog_choose_files, null);
            dialog.setView(dialogView);
            RecyclerView fileList;
            fileList = dialogView.findViewById(R.id.filesLlist);
            fileList.setLayoutManager(new LinearLayoutManager(this));
            final ChooseListAdapter adapter = new ChooseListAdapter(files);
            fileList.setAdapter(adapter);

            final String[] finalFiles = files;
            dialog.setPositiveButton(R.string.Standard_dialog_positive_button,
                    (dialog12, which) -> {
                        boolean[] chose = adapter.getCheckBoxStatus();
                        //Обновить список выбранных файлов для иммпорта
                        int isTrue = 0;
                        for (int index = 0; index < chose.length; index++) {
                            if (chose[index]) {
                                chose[isTrue] = chose[index];
                                finalFiles[isTrue] = finalFiles[index];
                                isTrue++;
                            }
                        }

                        boolean[] complete = new boolean[finalFiles.length];

                        for (int index = 0; index < isTrue; index++) {
                            if (chose[index]) {
                                //Получаем ошибки и результаты импортирования
                                if (DataContract.MyFileManager.importFiles(getApplicationContext(),
                                        new File(
                                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                                ,finalFiles[index]))) {
                                    //Сообщить об успешном импортировании
                                    complete[index] = true;
                                }else {
                                    complete[index] = false;
                                    DataContract.MyFileManager.deleteDate(getApplicationContext(), finalFiles[index]);
                                }
                            }
                        }

                        //Демонстрируем отчет об ипорте
                        AlertDialog.Builder messageDialog = new AlertDialog.Builder(context);
                        messageDialog.setTitle(R.string.Standard_Dialog_Report);
                        StringBuilder report = new StringBuilder();
                        for (int index = 0; index < isTrue; index++) {
                            report
                                    .append(finalFiles[index])
                                    .append(": ");

                            if (complete[index]) {
                                report.append(DataContract.MyFileManager.REPORT_NO_PROBLEM);
                                updateList();
                            }else {
                                report.append(DataContract.MyFileManager.REPORT_ERROR);
                            }

                            report.append("\n");
                        }
                        messageDialog.setMessage(report.toString());

                        messageDialog.setPositiveButton(R.string.Standard_dialog_positive_button,
                                (dialog121, which1) -> { });

                        messageDialog.show();
                    });

            dialog.setNegativeButton(R.string.Standard_dialog_negative_button, (dialog13, which) -> { });
        }
        dialog.show();
    }
    private String[] filterForRarFile(String[] filesName){
        int isTrue = 0;
        for (int index = 0; index < filesName.length; index++)
        {
            if (filesName[index].indexOf("mSch") == 0 && filesName[index].contains(".zip"))
            {
                filesName[isTrue] = filesName[index];
                isTrue++;
            }
        }

        filesName = Arrays.copyOf(filesName, isTrue);
        return filesName;
    }
}
