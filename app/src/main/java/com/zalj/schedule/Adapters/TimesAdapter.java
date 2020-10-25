package com.zalj.schedule.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zalj.schedule.Objects.TimeSchedule;
import com.zalj.schedule.R;

import java.util.ArrayList;
import java.util.Calendar;

public class TimesAdapter extends RecyclerView.Adapter<TimesAdapter.MyHolder>
{
    private Context context;
    private ArrayList<TimeSchedule> times;
    private SchedulesAdapter.iItemClickListener iItemClickListener;
    private Resources resources;

    public TimesAdapter(Context context, ArrayList<TimeSchedule> times, SchedulesAdapter.iItemClickListener iItemClickListener)
    {
        this.context = context;
        this.times = times;
        this.resources = context.getResources();

        this.iItemClickListener = iItemClickListener;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = (View) LayoutInflater.from(context).inflate(R.layout.element_of_list_time, parent, false);
        return new MyHolder(view, iItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position)
    {
        holder.builder(times.get(position));
    }

    @Override
    public int getItemCount()
    {
        return times.size();
    }

    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView
                number,
                startTime,
                finishTime;

        private SchedulesAdapter.iItemClickListener iItemClickListener;

        public MyHolder(@NonNull View itemView, SchedulesAdapter.iItemClickListener iItemClickListener)
        {
            super(itemView);

            number = itemView.findViewById(R.id.numberOfDiscipline);
            startTime = itemView.findViewById(R.id.startTimeSchedule);
            finishTime = itemView.findViewById(R.id.finishTimeSchedule);

            this.iItemClickListener = iItemClickListener;
            itemView.setOnClickListener(this);
        }

        public void builder(TimeSchedule time)
        {
            Calendar calendar = Calendar.getInstance();

            StringBuffer string = new StringBuffer();
            string.append(time.getNumber() + 1)
                    .append(" ")
                    .append(resources.getString(R.string.fragment_text_Discipline));
            number.setText(string.toString());

            calendar.set(Calendar.HOUR_OF_DAY, time.getStartHour());
            calendar.set(Calendar.MINUTE, time.getStartMinute());
            startTime.setText(DateUtils.formatDateTime(context, calendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME));

            calendar.set(Calendar.HOUR_OF_DAY, time.getFinishHour());
            calendar.set(Calendar.MINUTE, time.getFinishMinute());
            finishTime.setText(DateUtils.formatDateTime(context, calendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME));
        }

        @Override
        public void onClick(View v) {
            iItemClickListener.onItemClick(getAdapterPosition());
        }
    }

}
