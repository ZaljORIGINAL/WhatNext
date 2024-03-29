package com.zalj.schedule.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zalj.schedule.R;
import com.zalj.schedule.Activity.ScheduleBuilderActivity;

public class OptionsOfDisciplineNotificationFragment extends Fragment {
    /**View elements*/
    private CheckBox[] checkBoxes; /**id: 0 - beforeStart, 1 - start, 2 - beforeFinish, 3 - finish, 4 - timeToGo, 5 - finishOfDay*/
    private EditText[] minutes; /**id: 0 - beforeStart, 1 - beforeFinish, 2 - timeToGo*/
    private Context context;

    public static OptionsOfDisciplineNotificationFragment newInstance(){
        OptionsOfDisciplineNotificationFragment fragment =
                new OptionsOfDisciplineNotificationFragment();

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

        checkBoxes = new CheckBox[6];
        minutes = new EditText[3];
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_options_of_discipline_notification,
                container,
                false);

        //Инициализируем
        /*Блок по timeToGo*/
        checkBoxes[4] = view.findViewById(R.id.checkboxBeforeStartTimeToGo);
        checkBoxes[4].setChecked(ScheduleBuilderActivity.options.getTimeToGo());
        checkBoxes[4].setOnCheckedChangeListener(((buttonView, isChecked) -> {
            //TODO Решение на время.
            if (checkToAccept()){
                if (isChecked){
                    String time;
                    if (ScheduleBuilderActivity.options.getTimeToGoMin() == -1)
                        time = "10";
                    else
                        time = String.valueOf(ScheduleBuilderActivity.options.getTimeToGoMin());

                    minutes[2].setText(time);
                }else {
                    minutes[2].setText("");
                }

                minutes[2].setEnabled(isChecked);
            }else {
                checkBoxes[4].setChecked(false);
            }
        }));

        minutes[2] = view.findViewById(R.id.editTextTimeToGo);
        if (ScheduleBuilderActivity.options.getTimeToGo()){
            minutes[2].setText(
                    String.valueOf(ScheduleBuilderActivity.options.getTimeToGoMin()));
            minutes[2].setEnabled(true);
        }
        minutes[2].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (minutes[2].getText().toString().equals("") || TextUtils.isEmpty(minutes[2].getText().toString())){
                    ScheduleBuilderActivity.options.setTimeToGo(-1);
                }else {
                    ScheduleBuilderActivity.options.setTimeToGo(
                            Integer.parseInt(minutes[2].getText().toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /*Блок по beforeStart*/
        checkBoxes[0] = view.findViewById(R.id.checkBoxBeforeStart);
        checkBoxes[0].setChecked(ScheduleBuilderActivity.options.getBeforeStartOfDiscipline());
        checkBoxes[0].setOnCheckedChangeListener((buttonView, isChecked) -> {
            //TODO Решение на время.
            if (checkToAccept()){
                if (isChecked){
                    String time;
                    if (ScheduleBuilderActivity.options.getBeforeStartMin() == -1)
                        time = "10";
                    else
                        time = String.valueOf(ScheduleBuilderActivity.options.getBeforeStartMin());

                    minutes[0].setText(time);
                }else {
                    minutes[0].setText("");
                }

                minutes[0].setEnabled(isChecked);
            }else {
                checkBoxes[0].setChecked(false);
            }
        });

        minutes[0] = view.findViewById(R.id.editTextBeforeStart);
        if (ScheduleBuilderActivity.options.getBeforeStartOfDiscipline()){
            minutes[0].setText(
                    String.valueOf(ScheduleBuilderActivity.options.getBeforeStartMin()));
            minutes[0].setEnabled(true);
        }
        minutes[0].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (minutes[0].getText().toString().equals("") || TextUtils.isEmpty(minutes[0].getText().toString())){
                    ScheduleBuilderActivity.options.setBeforeStartOfDiscipline(-1);
                }else {
                    ScheduleBuilderActivity.options.setBeforeStartOfDiscipline(
                            Integer.parseInt(minutes[0].getText().toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /*Блок по start*/
        checkBoxes[1] = view.findViewById(R.id.checkBoxStart);
        checkBoxes[1].setChecked(ScheduleBuilderActivity.options.getStartOfDiscipline());
        checkBoxes[1].setOnCheckedChangeListener((buttonView, isChecked) -> {
            //TODO Решение на время.
            if (checkToAccept()){
                ScheduleBuilderActivity.options.setStartOfDiscipline(isChecked);
            }else {
                checkBoxes[1].setChecked(false);
            }
        });

        /*Блок по beforeFinish*/
        checkBoxes[2] = view.findViewById(R.id.checkBoxBeforeFinish);
        checkBoxes[2].setChecked(ScheduleBuilderActivity.options.getBeforeFinishOfDiscipline());
        checkBoxes[2].setOnCheckedChangeListener((buttonView, isChecked) -> {
            //TODO Решение на время.
            if (checkToAccept()) {
                if (isChecked){
                    String time;
                    if (ScheduleBuilderActivity.options.getBeforeFinishMin() == -1)
                        time = "10";
                    else
                        time = String.valueOf(ScheduleBuilderActivity.options.getBeforeFinishMin());

                    minutes[1].setText(time);
                } else
                    minutes[1].setText("");

                minutes[1].setEnabled(isChecked);
            }else {
                checkBoxes[2].setChecked(false);
            }
        });

        minutes[1] = view.findViewById(R.id.editTextBeforeFinish);
        if (ScheduleBuilderActivity.options.getBeforeFinishOfDiscipline()){
            minutes[1].setText(String.valueOf(ScheduleBuilderActivity.options.getBeforeFinishMin()));
            minutes[1].setEnabled(true);
        }
        minutes[1].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (minutes[1].getText().toString().equals("") || TextUtils.isEmpty(minutes[1].getText().toString())){
                    ScheduleBuilderActivity.options.setBeforeFinishOfDiscipline(-1);
                }else {
                    ScheduleBuilderActivity.options.setBeforeFinishOfDiscipline(
                            Integer.parseInt(minutes[1].getText().toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /*Блок по finish*/
        checkBoxes[3] = view.findViewById(R.id.checkBoxFinish);
        checkBoxes[3].setChecked(ScheduleBuilderActivity.options.getFinishOfDiscipline());
        checkBoxes[3].setOnCheckedChangeListener((buttonView, isChecked) -> {
            //TODO Решение на время.
            if (checkToAccept()){
                ScheduleBuilderActivity.options.setFinishOfDiscipline(isChecked);
            }else {
                checkBoxes[3].setChecked(false);
            }
        });

        /*Блок по 5 finishOfDay*/
        checkBoxes[5] = view.findViewById(R.id.checkBoxFinishOfDay);
        checkBoxes[5].setChecked(ScheduleBuilderActivity.options.getFinishOfDay());
        checkBoxes[5].setOnCheckedChangeListener((buttonView, isChecked) -> {
            //TODO Решение на время.
            if (checkToAccept()){
                ScheduleBuilderActivity.options.setFinishOfDay(isChecked);
            }else {
                checkBoxes[5].setChecked(false);
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    //TODO Решение на время.
    private boolean checkToAccept(){
        if (!ScheduleBuilderActivity.options.getAccept()){
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle(R.string.Standard_QuestionOfAction);
            dialog.setMessage(R.string.vremennoeReshenie_notification);

            dialog.setPositiveButton(R.string.Standard_dialog_positive_button,
                    (dialog1, which) -> {
                              ScheduleBuilderActivity.options.setAccept(true);
                    });

            dialog.setNegativeButton(R.string.Standard_dialog_negative_button,
                    ((dialog1, which) -> {
                    }));
            dialog.show();
        }

        return ScheduleBuilderActivity.options.getAccept();
    }
}
