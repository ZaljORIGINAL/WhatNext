package com.zalj.schedule.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zalj.schedule.Data.DataContract;
import com.zalj.schedule.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

public class SchedulesAdapter extends RecyclerView.Adapter<SchedulesAdapter.MyHolder>
{
    private Context context;
    private File file;
    private List<String> names;
    private iItemClickListener iItemClickListener;


    public SchedulesAdapter(Context context, List<String> names, iItemClickListener iItemClickListener)
    {
        this.context = context;
        this.names = names;
        this.iItemClickListener = iItemClickListener;
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view;
        if (names.size() == 0)
        {
            view = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.element_of_list_no_info, parent, false);
        }else
        {
            view = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.element_of_list_schedule, parent, false);
        }
        return new MyHolder(view, iItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position)
    {
        if(names.size() == 0)
        {
            holder.builderNoInfo();
        }else
        {
            holder.builder(names.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if (names.size() == 0)
        {
            return 1;
        }else
        {
            return names.size();
        }
    }

    public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView
                tNoInfoText,
                tNameOfSchedule;
//                typeOfSchedule,
//                scheduleCreateDate;
/*        private Button
                    bDelete,
                    bEdit;*/

        private iItemClickListener iItemClickListener;

        public MyHolder(@NonNull View itemView, iItemClickListener iItemClickListener)
        {
            super(itemView);

            if (itemView.findViewById(R.id.tListHasNotInfo) == null)
            {
                tNameOfSchedule = (TextView)itemView.findViewById(R.id.nameOfSchedule);
                //          typeOfSchedule = (TextView)itemView.findViewById(R.id.typeOfSchedule);
                //          scheduleCreateDate = (TextView)itemView.findViewById(R.id.scheduleCreateDate);

                itemView.setOnClickListener(this);
            }else
            {
                tNoInfoText = (TextView)itemView.findViewById(R.id.tListHasNotInfo);
            }

            /*TODO Была идея добавить в представление эелемента кнопки "Удалить", "Изменить".
               Появились некторые проблемы: По логике, если нажать на "Изменить", то должно было
               открыться activity ScheduleBuilderActivity при помощи startActivityForResult, но
               данный вызов должен происходить из parent activity(то в котором создан объект адаптера
               Идея решения данной проблемы: Создать интерфейсы, такие же как и для клика по самому
               элементу, но эти должнны срабатыать по кнопкам*/
            /*bDelete = itemView.findViewById(R.id.delete);
            bDelete.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                            dialog.setTitle(context.getResources().getString(R.string.Delete));
                            dialog.setMessage(context.getResources().getString(R.string.QuestionOfDelete));
                            dialog.setPositiveButton(R.string.dialog_positive_button,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            DataContract.deleteDate(context, fileName);

                                            names = Arrays.asList(file.list());

                                            notifyDataSetChanged();
                                        }
                                    });
                            dialog.setNegativeButton(R.string.dialog_negative_button,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                            dialog.show();
                        }
                    }
            );
            bEdit = itemView.findViewById(R.id.edit);
            bEdit.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }
            );*/

            this.iItemClickListener = iItemClickListener;
        }

        public void builder(String name)
        {
//            fileName = name;

            StringBuffer path = new StringBuffer();
            path.append(context.getFilesDir().getPath())
                    .append(File.separator)
                    .append(DataContract.MyFileManager.FILE_OF_SCHEDULE_DIRECTORY)
                    .append(File.separator)
                    .append(name);

            try
            {
                BufferedReader reader = new BufferedReader(new FileReader(path.toString()));
                reader.readLine();
                tNameOfSchedule.setText(reader.readLine());
                String text = tNameOfSchedule.getText().toString();
                Log.i("Schedule NAME", tNameOfSchedule.getText().toString());
            }catch (Exception e)
            {
                Log.i("Schedule NAME", "ERROR");
            }
        }

        public void builderNoInfo()
        {
            tNoInfoText.setText(context.getString(R.string.activity_SelectScheduleActivity_text_ListHasNotInfo));
        }

        @Override
        public void onClick(View v)
        {
            iItemClickListener.onItemClick(getAdapterPosition());
        }
    }

    public interface iItemClickListener
    {
        void onItemClick(int position);
    }
}
