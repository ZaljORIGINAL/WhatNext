/**
 * SelectScheduleActivity - activity. Для выбора расписания которое будет отображаться пользователю.
 *
 * Пользователь в праве выбрать предложенное расписание в элементе scheduleList или же создать новое кликнув fab.
 *
 * При клике на fab вызывается диологове окно в котором требуется задать наименование нового расписания*/
package com.example.schedule;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.schedule.Adapters.SchedulesAdapter;
import com.example.schedule.Data.DataContract;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class SelectScheduleActivity extends AppCompatActivity implements SchedulesAdapter.iItemClickListener
{
    //Файлы
    private File file;
    private List<String> files;

    //View activity
    private RecyclerView scheduleList;
    private SchedulesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_schedulee);

        FloatingActionButton fab = findViewById(R.id.addNewSchedule);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                createNewSchedule();
            }
        });

        try {
            String path = this.getFilesDir().getPath() +
                    File.separator +
                    DataContract.FILE_OF_SCHEDULE_DIRECTORY;
            file = new File(path);
            String[] filesArr;
            if (!file.exists())
            {
                file.mkdir();
            }
            filesArr = file.list();
            files = Arrays.asList(filesArr);
        }catch (Exception e)
        {
            Log.e("FILE", "Error in class SelectScheduleActivity");
        }

        scheduleList = findViewById(R.id.scheduleList);
        adapter = new SchedulesAdapter(this, this);
        scheduleList.setAdapter(adapter);
        scheduleList.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    public void onItemClick(int position)
    {
        Intent intent = new Intent();
        intent.putExtra(IntentHelper.NAME, files.get(position));
        setResult(IntentHelper.SELECT_SCHEDULE, intent);

        finish();
    }

    private void createNewSchedule()
    {
        //Schedule
        Calendar calendar = Calendar.getInstance();
        Intent intent = new Intent(this, ScheduleBuilderActivity.class);

        intent.putExtra(IntentHelper.COMMAND, IntentHelper.CREATE_NEW_SCHEDULE);
        startActivityForResult(intent, IntentHelper.CREATE_NEW_SCHEDULE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IntentHelper.CREATE_NEW_SCHEDULE:
            {
                if (resultCode == RESULT_OK)
                {
                    updateList();
                }
            }break;

            case IntentHelper.SCHEDULE_OPTIONS:
            {
                if (resultCode == RESULT_OK || resultCode == IntentHelper.RESULT_DELETE)
                {
                    updateList();
                }
            }
        }
    }

    private void updateList()
    {
        //Обнаволи массив имен
        files = Arrays.asList(file.list());
        //Сообщаем адаптеру что мы обновили данные
        adapter = new SchedulesAdapter(this, this);
        scheduleList.setAdapter(adapter);
    }
}
