package com.zalj.schedule.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zalj.schedule.R;

public class ChooseListAdapter extends RecyclerView.Adapter<ChooseListAdapter.MyHolder>
{
    private String[] list;
    private boolean[] chose;
    public ChooseListAdapter(String[] list)
    {
        this.list = list;
        this.chose = new boolean[list.length];
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_of_list_files, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position)
    {
        holder.holderBuilder(position);
    }

    @Override
    public int getItemCount() {
        return list.length;
    }

    public boolean[] getCheckBoxStatus()
    {
        return chose;
    }

    public class MyHolder extends RecyclerView.ViewHolder
    {
        private CheckBox file;
        private int index;

        public MyHolder(@NonNull View itemView)
        {
            super(itemView);

            file = itemView.findViewById(R.id.fileName);
            file.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    if (isChecked)
                    {
                        chose[index] = true;
                    }else
                    {
                        chose[index] = false;
                    }
                }
            });
        }

        public void holderBuilder(int index)
        {
            this.index = index;

            file.setText(list[index]);
        }
    }
}
