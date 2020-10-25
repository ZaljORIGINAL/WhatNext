package com.zalj.schedule.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zalj.schedule.Objects.DayOfWeek;
import com.zalj.schedule.R;

import java.util.ArrayList;

public class DayOfWeekAdapter extends RecyclerView.Adapter<DayOfWeekAdapter.MyHolder>
{
    private ArrayList<DayOfWeek> dayOfWeekArrayList;
    private iOnItemClickListener iOnItemClickListener;
    private String[] dayOfWeekArray;

    public DayOfWeekAdapter(Context context, ArrayList<DayOfWeek> dayOfWeekArrayList, iOnItemClickListener iOnItemClickListener)
    {
        this.dayOfWeekArrayList = dayOfWeekArrayList;
        this.iOnItemClickListener = iOnItemClickListener;
        Resources resources = context.getResources();
        dayOfWeekArray = resources.getStringArray(R.array.day_of_week_array);
    }
    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_of_list_day_of_week, parent, false);
        return new MyHolder(view, iOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position)
    {
        holder.builder(position);
    }

    @Override
    public int getItemCount() {
        return dayOfWeekArrayList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder  implements View.OnClickListener
    {
        private LinearLayout panel;
        private TextView dayOfWeek, count;

        private iOnItemClickListener clickListener;

        public MyHolder(@NonNull View itemView, iOnItemClickListener iOnItemClickListener)
        {
            super(itemView);

            panel = (LinearLayout)itemView.findViewById(R.id.panel);
            dayOfWeek = (TextView)itemView.findViewById(R.id.dayOfWeek);
            count = (TextView)itemView.findViewById(R.id.countOfDisciplines);

            this.clickListener = iOnItemClickListener;
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v)
        {
            Log.d("CLICK", "Click В АДАПТОРЕ");
            iOnItemClickListener.onItemClick(getAdapterPosition());
        }

        public void builder(int position)
        {
            DayOfWeek day = dayOfWeekArrayList.get(position);

            if (day.getCount() == 0)
            {
                panel.setBackgroundResource(R.color.noInfo);
            }else
            {
                panel.setBackgroundResource(R.color.haveInfo);
            }

            dayOfWeek.setText(dayOfWeekArray[position]);
            count.setText(String.valueOf(day.getCount()));
        }
    }

    public interface iOnItemClickListener
    {
        void onItemClick(int position);
    }
}
