package com.example.schedule.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedule.Objects.Discipline;
import com.example.schedule.R;

import java.util.ArrayList;

public class DisciplineAdapter extends RecyclerView.Adapter<DisciplineAdapter.MyHolder>
{
    private Context context;
    private ArrayList<Discipline> disciplines;
    private String[] type;
    private iOnItemClickListener onClick;

    public DisciplineAdapter(Context context, ArrayList<Discipline> disciplines, iOnItemClickListener onClick)
    {
        this.context = context;
        this.disciplines = disciplines;
        this.onClick = onClick;
        type = context.getResources().getStringArray(R.array.type_of_discipline);
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.element_of_list_discipline, parent, false);
        return new MyHolder(view, onClick);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position)
    {
        holder.holderBuilder(disciplines.get(position));
    }

    @Override
    public int getItemCount() {
        return disciplines.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView
                position,
                startTime,
                finishTime,
                nameOfDiscipline,
                building,
                auditorium;

        private iOnItemClickListener onItemClick;

        public MyHolder(@NonNull View itemView, iOnItemClickListener onItemClick)
        {
            super(itemView);

            position = (TextView)itemView.findViewById(R.id.position);
            startTime = (TextView)itemView.findViewById(R.id.startTime);
            finishTime = (TextView)itemView.findViewById(R.id.finishTime);
            nameOfDiscipline = (TextView)itemView.findViewById(R.id.nameOfDiscipline);
            building = (TextView)itemView.findViewById(R.id.building);
            auditorium = (TextView)itemView.findViewById(R.id.auditorium);

            this.onItemClick = onItemClick;
            itemView.setOnClickListener(this);
        }

        public void holderBuilder(Discipline discipline)
        {
            //Очередность пары
            position.setText(String.valueOf(discipline.getPosition() + 1));
            //Время начала
            startTime.setText(String.format("%2d:%02d", discipline.getStartHour(), discipline.getStartMinute()));
            //Время конца
            finishTime.setText(String.format("%2d:%02d", discipline.getFinishHour(), discipline.getFinishMinute()));
            //Наименоввание предмета
            nameOfDiscipline.setText("(" + type[discipline.getType()] + ") " + discipline.getDisciplineName());
            //Здание в котором проходит занятие
            building.setText(discipline.getBuilding());
            //Аудитория в которой проходит занятие
            auditorium.setText(discipline.getAuditorium());
        }

        @Override
        public void onClick(View v) {
            this.onItemClick.onItemClick(getAdapterPosition());
        }
    }

    public interface iOnItemClickListener
    {
        void onItemClick(int position);
    }
}
