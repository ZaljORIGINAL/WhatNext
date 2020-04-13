/**
 * Values "kay":
 *  @NAME - name of schedule
 *  @CHANGE - switch status
 *  @PARITY - parity*/

package com.example.schedule.Fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.schedule.R;
import java.util.Calendar;

import static com.example.schedule.ScheduleBuilderActivity.schedule;

public class ScheduleOptionsFragment extends Fragment
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

    public static ScheduleOptionsFragment newInstance(int number)
    {
        ScheduleOptionsFragment fragment = new ScheduleOptionsFragment();
        Bundle value = new Bundle();
        value.putInt(KAY_NUMBER, number);
        fragment.setArguments(value);

        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        calendar = Calendar.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View view =  inflater.inflate(R.layout.fragment_main_settings_of_schedule, container, false);

        //Инийиализация элементов
        name = view.findViewById(R.id.setNameOfScheduleFragment);
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    schedule.setNameOfSchedule(s.toString());
                }catch (Exception e)
                {

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        isDouble = (Switch)view.findViewById(R.id.setDouble);
        isDouble.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                    {
                        if (isChecked)
                        {
                            info.setVisibility(View.VISIBLE);
                            selectWeek.setVisibility(View.VISIBLE);
                            schedule.setType(1);
                            selectWeek.setText(R.string.fragment_button_setWeek);
                        }else
                        {
                            info.setVisibility(View.GONE);
                            selectWeek.setVisibility(View.GONE);
                            schedule.setType(0);
                        }
                    }
                }
        );
        info = (TextView) view.findViewById(R.id.infoAboutOfDouble);


        selectWeek = (Button) view.findViewById(R.id.selectWeek);
        selectWeek.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        new DatePickerDialog(
                                context,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
                                    {
                                        calendar.set(year, month, dayOfMonth);
                                        schedule.setParity((calendar.get(Calendar.DAY_OF_YEAR) + calendar.get(Calendar.DAY_OF_MONTH)) % 2);
                                        selectWeek.setText(String.valueOf(DateUtils.formatDateTime(context, calendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE)));
                                    }
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                                ).show();
                    }
                }
        );

        //Заполнение данными
        try {
            name.setText(schedule.getNameOfSchedule());
        }catch (Exception e)
        {
        }

        if (schedule.getType() == 1)
            isDouble.setChecked(true);
        else
            isDouble.setChecked(false);
        selectWeek.setText(getArguments().getString(KAY_PARITY));

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
