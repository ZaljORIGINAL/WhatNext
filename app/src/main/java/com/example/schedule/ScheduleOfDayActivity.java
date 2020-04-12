package com.example.schedule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.schedule.Adapters.DisciplineAdapter;
import com.example.schedule.Objects.DayOfWeek;
import com.example.schedule.Objects.Discipline;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_added_elements);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        list = (RecyclerView)findViewById(R.id.elementsList);
        list.setLayoutManager(new LinearLayoutManager(this));
        day = getIntent().getParcelableExtra("DAY");
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
                Intent intent = new Intent();
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
        Intent intent = new Intent();
        intent.putExtra("DAY", day);

        setResult(RESULT_OK, intent);
        finish();
    }

    private void addDiscipline()
    {
        //Показать диалог для удаления
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

    private void updateAdapter()
    {
        adapterNames = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                ScheduleBuilderActivity.namesOfDisciplines);
    }

    private void setDisciplineOptions(Discipline discipline, final int position)
    {
        //Показать диалог для редактирования
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.activity_ScheduleOfDay_dialog_title);
        View view = getLayoutInflater().inflate(R.layout.dialog_discipline_options, null);
        dialog.setView(view);

        /*View elements*/
        final Spinner setPositionSpinner,
                setTypeSpinner;
        final AutoCompleteTextView setNameAC;
        final EditText setBuilding,
                setAuditory;
        Button delete;

        setPositionSpinner = view.findViewById(R.id.setPosition);
        adapterPosition = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                getPosition(ScheduleBuilderActivity.times.size()));
        adapterPosition.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setPositionSpinner.setAdapter(adapterPosition);
        setPositionSpinner.setSelection(disciplineBuilder.getPosition());
        setPositionSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        disciplineBuilder.setPosition(position);
                        disciplineBuilder.setTime(ScheduleBuilderActivity.times.get(position));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );

        setNameAC = view.findViewById(R.id.setName);
        //Сделать адаптер, получить инфу из ScheduleCreatingActivity.discipline + (последний пункт "добавить новый предмет"
        adapterNames = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                ScheduleBuilderActivity.namesOfDisciplines);
        setNameAC.setAdapter(adapterNames);
        try {
            setNameAC.setText(disciplineBuilder.getDisciplineName());
        }catch (Exception e)
        {
        }

        setTypeSpinner = view.findViewById(R.id.setType);
        //Уже имеет добавить прослушиваетль
        setTypeSpinner.setSelection(discipline.getType());
        setTypeSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        switch (position)
                        {
                            case Discipline.LECTURE:
                                disciplineBuilder.setType(Discipline.LECTURE);
                                break;

                            case Discipline.LABORATORY:
                                disciplineBuilder.setType(Discipline.LABORATORY);
                                break;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

        /*FIXME Сделать так же как и с названиями*/
        setBuilding =  view.findViewById(R.id.setBuilding);
        try {
            setBuilding.setText(disciplineBuilder.getBuilding());
        }catch (Exception e){
        }

        /*FIXME Сделать так же как и с названиями*/
        setAuditory = view.findViewById(R.id.setAuditory);
        try {
            setAuditory.setText(disciplineBuilder.getAuditorium());
        }catch (Exception e){
        }

        dialog.setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Сохранение предмета
                if (setNameAC.getText().toString().isEmpty())
                {
                    //Сообщение об ошибка
                    AlertDialog.Builder errorDialog = new AlertDialog.Builder(getApplicationContext());
                    errorDialog.setTitle(R.string.Error);
                    errorDialog.setMessage(getResources().getString(R.string.ScheduleCreatingActivity_Error_fieldOfNameIsClear));
                    errorDialog.setPositiveButton(R.string.dialog_positive_button, null);
                    errorDialog.show();
                }else
                {
                    disciplineBuilder.setDisciplineName(setNameAC.getText().toString());
                    if (!ScheduleBuilderActivity.namesOfDisciplines.contains(setNameAC.getText().toString()))
                    {
                        ScheduleBuilderActivity.namesOfDisciplines.add(setNameAC.getText().toString());
                        updateAdapter();
                    }
                    disciplineBuilder.setBuilding(setBuilding.getText().toString());
                    disciplineBuilder.setAuditorium(setAuditory.getText().toString());

                    if (position != -1)
                    {
                        day.updateDiscipline(disciplineBuilder, position);
                    }else
                    {
                        day.addDiscipline(disciplineBuilder);
                    }

                    day.sortDisciplines();

                    adapter.notifyDataSetChanged();
                }
            }
        });

        dialog.setNegativeButton(R.string.dialog_negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        if (position != -1)
        {
            dialog.setNeutralButton(R.string.Delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    day.removeDiscipline(position);
                    adapter.notifyDataSetChanged();
                }
            });
        }

        dialog.show();
    }
}
