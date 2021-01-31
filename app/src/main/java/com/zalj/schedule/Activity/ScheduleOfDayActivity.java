package com.zalj.schedule.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import com.zalj.schedule.Adapters.DisciplineAdapter;
import com.zalj.schedule.Objects.DayOfWeek;
import com.zalj.schedule.Objects.Discipline;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zalj.schedule.R;

import java.util.ArrayList;
import java.util.Calendar;

public class ScheduleOfDayActivity extends AppCompatActivity implements DisciplineAdapter.iOnItemClickListener
{
    /**View*/
    private RecyclerView list;
    private FloatingActionButton fab;

    private DayOfWeek day;
    private Discipline disciplineBuilder;

    /**Adapters*/
    private DisciplineAdapter adapter;
    private ArrayAdapter<String> adapterPosition;
    private ArrayAdapter<String> adapterNames;

    private Intent intent;
    private int maxPositionSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_added_elements);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        intent = getIntent();

        list = (RecyclerView)findViewById(R.id.elementsList);
        list.setLayoutManager(new LinearLayoutManager(this));
        day = intent.getParcelableExtra("DAY");

        updateTitleOfActivity(this, actionBar, day.getDayOfWeek());
        /*Для упрощения установки номера пары требуется провести анализ.
        * Если день пуст, то в positionSpinner первым выбранным элемментом будет указана
        * первая пара. Если же день не пуст, то выискивается самая поздняя пара в спинере как
        * выбранная будет указана "поздняя пара" + 1. Тем самым мы исключаем возможность
        * установления подряд несскольких пар в одно и то же время, но требуется ввести проверку
        * на количество возможных пар в ДЕНЬ. Если мы достигли максимума, то в positionSpinner
        * как выбранным будет указана как последняя. Так же при созднаии новой пары в
        * positionSpinner для следующей пары будет указана +1 к позиции*/
        if (day != null && day.getCount() != 0){
            ArrayList<Discipline> disciplines = day.getDisciplines();

            //Получаем номер дисциплины у последнего элемента в массиве. Надеясь на то,
            // что он является последней парой за день
            maxPositionSelected = disciplines.get(disciplines.size() - 1).getPosition();
            if (maxPositionSelected < ScheduleBuilderActivity.times.size() - 1)
                maxPositionSelected++;
        }else {
            maxPositionSelected = 0;
        }

        adapter = new DisciplineAdapter(this, day.getDisciplines(), this);
        list.setAdapter(adapter);

        fab = (FloatingActionButton)findViewById(R.id.addElement);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDiscipline();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                intent.putExtra("DAY", day);
                setResult(RESULT_OK, intent);
                finish();
                return true;

                default:
                    return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(int position)
    {
        disciplineBuilder = day.getDiscipline(position);

        setDisciplineOptions(disciplineBuilder, position);
    }

    @Override
    public void onBackPressed() {
        intent.putExtra("DAY", day);

        setResult(RESULT_OK, intent);
        finish();
    }

    private void addDiscipline()
    {
        disciplineBuilder = new Discipline();

        setDisciplineOptions(disciplineBuilder, -1);
    }

    private String[] getPosition(int count)
    {
        String[] strings = new String[count];
        for (byte index = 0; index < count; index++)
        {
            strings[index] = String.valueOf(index + 1) + " " + getResources().getString(R.string.fragment_text_Discipline);
        }

        return strings;
    }

    private void updateNameOfDisciplineAdapter()
    {
        adapterNames = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                ScheduleBuilderActivity.namesOfDisciplines);
    }

    private void updateDisciplineAdapter()
    {
        adapter = new DisciplineAdapter(this, day.getDisciplines(), this);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void setDisciplineOptions(final Discipline discipline, final int position)
    {
        //Показать диалог для редактирования
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.activity_ScheduleOfDay_dialog_title);
        View view = getLayoutInflater().inflate(R.layout.dialog_discipline_options, null);
        dialog.setView(view);

        /*View elements*/
        final Spinner positionSpinner,
                setTypeSpinner;
        final AutoCompleteTextView nameAC, buildingAC, auditoryAC;

        positionSpinner = view.findViewById(R.id.setPosition);
        adapterPosition = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                getPosition(ScheduleBuilderActivity.times.size()));
        adapterPosition.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        positionSpinner.setAdapter(adapterPosition);
        if (position != -1){
            positionSpinner.setSelection(discipline.getPosition());
        }else {
            positionSpinner.setSelection(maxPositionSelected);
        }

        nameAC = view.findViewById(R.id.setName);
        adapterNames = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                ScheduleBuilderActivity.namesOfDisciplines);
        nameAC.setAdapter(adapterNames);
        try {
            nameAC.setText(disciplineBuilder.getDisciplineName());
        }catch (Exception e)
        {
        }

        setTypeSpinner = view.findViewById(R.id.setType);
        //Уже имеет добавить прослушиваетль
        setTypeSpinner.setSelection(discipline.getType());

        buildingAC = view.findViewById(R.id.setBuilding);
        adapterNames = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                ScheduleBuilderActivity.buildingOfDisciplines);
        buildingAC.setAdapter(adapterNames);
        try {
            buildingAC.setText(disciplineBuilder.getBuilding());
        }catch (Exception e)
        {
        }

        auditoryAC = view.findViewById(R.id.setAuditory);
        adapterNames = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                ScheduleBuilderActivity.auditoryOfDisciplines);
        auditoryAC.setAdapter(adapterNames);
        try {
            auditoryAC.setText(disciplineBuilder.getAuditorium());
        }catch (Exception e)
        {
        }

        dialog.setPositiveButton(R.string.Standard_dialog_positive_button, (dialog1, which) -> {
            //Сохранение предмета
            if (nameAC.getText().toString().isEmpty())
            {
                //Сообщение об ошибка
                AlertDialog.Builder errorDialog = new AlertDialog.Builder(getApplicationContext());
                errorDialog.setTitle(R.string.Standard_Error);
                errorDialog.setMessage(getResources().getString(R.string.ScheduleCreatingActivity_Error_fieldOfNameIsClear));
                errorDialog.setPositiveButton(R.string.Standard_dialog_positive_button, null);
                errorDialog.show();
            }else
            {
                //Установка порядкого номера дисциплины
                int positionScheduleOfDay;
                positionScheduleOfDay = positionSpinner.getSelectedItemPosition();
                disciplineBuilder.setPosition(positionScheduleOfDay);
                //Устанвока времени
                disciplineBuilder.setTime(
                        ScheduleBuilderActivity.times.get(positionScheduleOfDay));

                if (maxPositionSelected <= positionScheduleOfDay){
                    if (positionScheduleOfDay == ScheduleBuilderActivity.times.size() - 1){
                        maxPositionSelected = positionScheduleOfDay;
                    }else {
                        maxPositionSelected = positionScheduleOfDay + 1;
                    }
                }

                //Установка имени дисциплины
                disciplineBuilder.setDisciplineName(nameAC.getText().toString());
                if (!ScheduleBuilderActivity.namesOfDisciplines.contains(nameAC.getText().toString()))
                {
                    ScheduleBuilderActivity.namesOfDisciplines.add(nameAC.getText().toString());
                    updateNameOfDisciplineAdapter();
                }
                //Установка здания
                disciplineBuilder.setBuilding(buildingAC.getText().toString());
                if (!ScheduleBuilderActivity.buildingOfDisciplines.contains(buildingAC.getText().toString()))
                {
                    ScheduleBuilderActivity.buildingOfDisciplines.add(buildingAC.getText().toString());
                    updateNameOfDisciplineAdapter();
                }
                //Установка номера аудитории
                disciplineBuilder.setAuditorium(auditoryAC.getText().toString());
                if (!ScheduleBuilderActivity.auditoryOfDisciplines.contains(auditoryAC.getText().toString()))
                {
                    ScheduleBuilderActivity.auditoryOfDisciplines.add(auditoryAC.getText().toString());
                    updateNameOfDisciplineAdapter();
                }
                //Установка типа дисциплины
                disciplineBuilder.setType(setTypeSpinner.getSelectedItemPosition());

                if (position != -1)
                {
                    day.updateDiscipline(disciplineBuilder, position);
                }else
                {
                    day.addDiscipline(disciplineBuilder);
                }

                day.sortDisciplines();

                positionSpinner.setSelection(maxPositionSelected);

                updateDisciplineAdapter();
            }
        });

        dialog.setNegativeButton(R.string.Standard_dialog_negative_button, (dialog12, which) -> {
        });

        if (position != -1)
        {
            dialog.setNeutralButton(R.string.Standard_Delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    day.removeDiscipline(position);
                    updateDisciplineAdapter();
                }
            });
        }

        dialog.show();
    }

    private void updateTitleOfActivity(Context context, ActionBar actionBar, int dayOfWeek){
        String[] daysOfWeek = context.getResources().getStringArray(R.array.day_of_week_array);

        switch (dayOfWeek){
            case Calendar.MONDAY:
                actionBar.setTitle(daysOfWeek[0]);
                break;

            case Calendar.TUESDAY:
                actionBar.setTitle(daysOfWeek[1]);
                break;

            case Calendar.WEDNESDAY:
                actionBar.setTitle(daysOfWeek[2]);
                break;

            case Calendar.THURSDAY:
                actionBar.setTitle(daysOfWeek[3]);
                break;

            case Calendar.FRIDAY:
                actionBar.setTitle(daysOfWeek[4]);
                break;

            case Calendar.SATURDAY:
                actionBar.setTitle(daysOfWeek[5]);
                break;

            case Calendar.SUNDAY:
                actionBar.setTitle(daysOfWeek[6]);
                break;
        }
    }
}
