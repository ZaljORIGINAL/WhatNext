package com.example.schedule.Fragments;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedule.Adapters.SchedulesAdapter;
import com.example.schedule.Adapters.TimesAdapter;
import com.example.schedule.Objects.TimeSchedule;
import com.example.schedule.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.schedule.ScheduleBuilderActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class TimeFragment extends Fragment implements SchedulesAdapter.iItemClickListener
{
    /**View elements*/
    private RecyclerView timeList;
    private FloatingActionButton fab;

    private ArrayList<TimeSchedule> times;
    private TimesAdapter adapter;
    private Calendar calendar;

    private Context context;

    public static final String KAY_TIMES = "TIMES";

    public static TimeFragment newInstance(int number)
    {
        TimeFragment fragment = new TimeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("NUMBER", number);
        fragment.setArguments(bundle);

        return fragment;
    }

    public TimeFragment()
    {
//        times = new ArrayList<>();
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
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_added_elements, container, false);

        timeList = (RecyclerView) view.findViewById(R.id.elementsList);
        timeList.setLayoutManager(new LinearLayoutManager(context));
//        if (getArguments().getParcelableArrayList(KAY_TIMES) != null)
//        {
//            times = getArguments().getParcelableArrayList(KAY_TIMES);
//        }
        adapter = new TimesAdapter(context,ScheduleBuilderActivity.times /*times*/, this);
        timeList.setAdapter(adapter);

        fab = (FloatingActionButton) view.findViewById(R.id.addElement);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                createTime();
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        Bundle value = new Bundle();
//        value.putParcelableArrayList(KAY_TIMES, times);
    }

    @Override
    public void onItemClick(int position) {
        TimeSchedule timeSchedule = ScheduleBuilderActivity.times.get(position);

        setTimeOptions(timeSchedule, position);
    }

    private void createTime() {
        TimeSchedule timeSchedule = new TimeSchedule(ScheduleBuilderActivity.times.size() /*times.size()*/);

        setTimeOptions(timeSchedule, -1);
    }

    private void setTimeOptions(TimeSchedule timeSchedule, final int position)
    {
        final TimeSchedule time = timeSchedule;

        /**Dialog view*/
        TextView text;

        final Button
                setStartTimeButton,
                setFinishTimeButton;

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(R.string.fragment_dialog_newTime);
        View view = getLayoutInflater().inflate(R.layout.dialog_time_of_shedule_options, null);
        dialog.setView(view);

        text = (TextView)view.findViewById(R.id.setNumber);
        text.setText(timeSchedule.getNumber() + 1 + " " + getResources().getString(R.string.fragment_text_Discipline));

        setStartTimeButton = (Button) view.findViewById(R.id.setStartTime);
        if(position != -1)
        {
            calendar.set(Calendar.HOUR_OF_DAY, time.getStartHour());
            calendar.set(Calendar.MINUTE, time.getStartMinute());
            setStartTimeButton.setText(DateUtils.formatDateTime(getContext(), calendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME));
        }else
        {
            setStartTimeButton.setText(R.string.fragment_text_startTime);
        }
        setStartTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                new TimePickerDialog(
                        context,
                        new TimePickerDialog.OnTimeSetListener()
                        {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute)
                            {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                time.setStartTime(hourOfDay, minute);
                                setStartTimeButton.setText(DateUtils.formatDateTime(getContext(), calendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME));
                            }
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                ).show();
            }
        });

        setFinishTimeButton = (Button) view.findViewById(R.id.setFinishTime);
        if (position != -1)
        {
            calendar.set(Calendar.HOUR_OF_DAY, time.getFinishHour());
            calendar.set(Calendar.MINUTE, time.getFinishMinute());
            setFinishTimeButton.setText(DateUtils.formatDateTime(getContext(), calendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME));
        }else
        {
            setFinishTimeButton.setText(R.string.fragment_text_finishTime);
        }
        setFinishTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                new TimePickerDialog(
                        context,
                        new TimePickerDialog.OnTimeSetListener()
                        {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute)
                            {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                time.setFinishTime(hourOfDay, minute);
                                setFinishTimeButton.setText(DateUtils.formatDateTime(getContext(), calendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME));

                            }
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                ).show();
            }
        });

        dialog.setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (position == -1)
                {
//                    times.add(time);
                    ScheduleBuilderActivity.times.add(time);
                }else
                {
//                    times.set(position, time);
                    ScheduleBuilderActivity.times.set(time.getNumber(), time);
                }
                adapter.notifyDataSetChanged();
            }
        });

        dialog.setNegativeButton(R.string.dialog_negative_button, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        dialog.show();
    }
}
