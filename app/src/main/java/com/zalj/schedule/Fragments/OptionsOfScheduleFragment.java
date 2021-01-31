package com.zalj.schedule.Fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zalj.schedule.Data.DataContract;
import com.zalj.schedule.IntentHelper;
import com.zalj.schedule.R;
import com.zalj.schedule.Activity.ScheduleBuilderActivity;

import java.util.Calendar;

import static com.zalj.schedule.Activity.ScheduleBuilderActivity.schedule;

public class OptionsOfScheduleFragment extends Fragment
{
    /**View elements*/
    private EditText name;
    private Switch isDouble;
    private TextView info;
    private Button selectWeek;

    private Context context;
    private Calendar calendar;

    public static final String
            KAY_NUMBER = "NUMBER",
            KAY_PARITY = "PARITY";

    public static OptionsOfScheduleFragment newInstance(int number)
    {
        OptionsOfScheduleFragment fragment = new OptionsOfScheduleFragment();
        Bundle value = new Bundle();
        value.putInt(KAY_NUMBER, number);
        fragment.setArguments(value);

        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calendar = Calendar.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_main_settings_of_schedule, container, false);

        //Инициализация элементов
        name = view.findViewById(R.id.setNameOfScheduleFragment);
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                schedule.setNameOfSchedule(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        isDouble = view.findViewById(R.id.setDouble);
        isDouble.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    if (isChecked) {
                        info.setVisibility(View.VISIBLE);
                        selectWeek.setVisibility(View.VISIBLE);
                        schedule.setType(DataContract.MyAppSettings.SCHEDULE_TYPE_2);
                        selectWeek.setText(R.string.fragment_button_setWeek);
                    }else {
                        info.setVisibility(View.GONE);
                        selectWeek.setVisibility(View.GONE);
                        schedule.setType(DataContract.MyAppSettings.SCHEDULE_TYPE_1);
                        schedule.setParity(-1);
                    }
                });
        info = view.findViewById(R.id.infoAboutOfDouble);

        selectWeek = view.findViewById(R.id.selectWeek);
        selectWeek.setOnClickListener(
                (v) -> {
                        new DatePickerDialog(
                            context,
                            (view1, year, month, dayOfMonth) -> {
                                    calendar.set(year, month, dayOfMonth);
                                    schedule.setParity((calendar.get(Calendar.WEEK_OF_YEAR) % 2));
                                    selectWeek.setText(
                                            String.valueOf(
                                                    DateUtils.formatDateTime(
                                                            context,
                                                            calendar.getTimeInMillis(),
                                                            DateUtils.FORMAT_SHOW_DATE)));
                                    },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                                ).show();
                    });

        //В случе изменении расписания заполненим поля данными
        if (ScheduleBuilderActivity.parentIntent.getIntExtra(
                IntentHelper.COMMAND, 0) == IntentHelper.EDIT_SCHEDULE){
            //Устаанавливаем время
            name.setText(schedule.getNameOfSchedule());
            //Установка типа расписания
            if (schedule.getType() == DataContract.MyAppSettings.SCHEDULE_TYPE_2){
                isDouble.setChecked(true);
                //Устанавливаем следующий понедельник верхней недели
                if ((calendar.get(Calendar.WEEK_OF_YEAR) % 2) != schedule.getParity())
                    calendar.add(Calendar.DATE, 7);

                selectWeek.setText(
                        String.valueOf(
                                DateUtils.formatDateTime(
                                        context,
                                        calendar.getTimeInMillis(),
                                        DateUtils.FORMAT_SHOW_DATE)));
            } else{
                isDouble.setChecked(false);
            }
        }

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        Bundle value = new Bundle();
        value.putString(KAY_PARITY, selectWeek.getText().toString());

        setArguments(value);
    }
}
