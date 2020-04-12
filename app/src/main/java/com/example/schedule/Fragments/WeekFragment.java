package com.example.schedule.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedule.Adapters.DayOfWeekAdapter;
import com.example.schedule.Objects.DayOfWeek;
import com.example.schedule.Objects.Week;
import com.example.schedule.R;
import com.example.schedule.ScheduleBuilderActivity;
import com.example.schedule.ScheduleOfDayActivity;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class WeekFragment extends Fragment implements DayOfWeekAdapter.iOnItemClickListener
{
    private int number;
    private TextView week;
    private RecyclerView disciplinesList;

    private DayOfWeekAdapter adapter;

    private Context context;

    private static final String KAY_DAYS = "DAYS";

    public static WeekFragment newInstance(int number)
    {
        WeekFragment fragment = new WeekFragment();
        Bundle value = new Bundle();
        value.putInt("number", number);
        //value.putParcelable("week", week.getDaysOfWeek());
        fragment.setArguments(value);

        return fragment;
    }

    /*FIXME Происходит какой то пиздец с неделями. Есть идея это вот ArrayList<Week> weeks
    *  заменить на 2 объекта Week. А то пиздец*/
    public WeekFragment()
    {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        try {
            number = getArguments().getInt("number");
        }catch (Exception e){

        }

    }

    @Override
    public View onCreateView( LayoutInflater inflater,
                              ViewGroup container,
                              Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_schedule_of_week, container, false);

        week = view.findViewById(R.id.addQuestion);
        if (number == 0) {
            week.setText(this.getString(R.string.fragment_name_topWeek));
        }else {
            week.setText(this.getString(R.string.fragment_name_lowerWeek));
        }

        disciplinesList = (RecyclerView)view.findViewById(R.id.weekList);
        disciplinesList.setLayoutManager(new LinearLayoutManager(container.getContext()));
        if (number == 0)
        {
            adapter = new DayOfWeekAdapter(context ,ScheduleBuilderActivity.topWeek.getDaysOfWeek(), this);
        }else
        {
            adapter = new DayOfWeekAdapter(context ,ScheduleBuilderActivity.loverWeek.getDaysOfWeek(), this);
        }
        disciplinesList.setAdapter(adapter);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        Bundle value = new Bundle();
//        value.putParcelableArrayList(KAY_DAYS, days);
    }

    @Override
    public void onItemClick(int position)
    {
        Log.d("CLICK", "Click(в фрагменте): " + position);
        Intent intent = new Intent(getContext(), ScheduleOfDayActivity.class);
        if (number == 0)
        {
            intent.putExtra("DAY", ScheduleBuilderActivity.topWeek.getDisciplines((byte) position));
        }else
        {
            intent.putExtra("DAY", ScheduleBuilderActivity.loverWeek.getDisciplines((byte) position));
        }
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (resultCode)
        {
            case RESULT_OK:
                DayOfWeek dayOfWeek = data.getParcelableExtra("DAY");

                if (number == 0)
                {
                    ScheduleBuilderActivity.topWeek.setDisciplinesOfDay(dayOfWeek.getDisciplines(), dayOfWeek.getDayOfWeek());
                }else
                {
                    ScheduleBuilderActivity.loverWeek.setDisciplinesOfDay(dayOfWeek.getDisciplines(), dayOfWeek.getDayOfWeek());
                }
                adapter.notifyDataSetChanged();
        }
    }
}
