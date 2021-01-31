package com.zalj.schedule.Adapters;

import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.zalj.schedule.Fragments.OptionsOfDisciplineNotificationFragment;
import com.zalj.schedule.Fragments.OptionsOfScheduleFragment;
import com.zalj.schedule.Fragments.TimeFragment;
import com.zalj.schedule.R;
import com.zalj.schedule.Fragments.WeekFragment;

import static com.zalj.schedule.Activity.ScheduleBuilderActivity.loverWeek;
import static com.zalj.schedule.Activity.ScheduleBuilderActivity.topWeek;

public class FragmentAdapter extends FragmentPagerAdapter
{
    private Resources resources;
    private Fragment main;

    public FragmentAdapter(FragmentManager fm, Context context) {
        super(fm);

        resources = context.getResources();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                //Main settings
                main = OptionsOfScheduleFragment.newInstance(position);
                return main;
            case 1:
                //Time list
                return TimeFragment.newInstance(position);

            case 2:
                //Options Of Notification
                return OptionsOfDisciplineNotificationFragment.newInstance();

            case 3:
                return WeekFragment.newInstance(topWeek.getNumber());

            case 4:
                return WeekFragment.newInstance(loverWeek.getNumber());
                default:
                    return WeekFragment.newInstance(position - 2);
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position)
        {
            case 0:
                //Main settings
                return resources.getString(R.string.fragment_name_mainSettingsOfFragment);

            case 1:
                //Times
                return resources.getString(R.string.fragment_name_timeOfSchedule);

            case 2:
                //Options Of Notification
                return resources.getString(R.string.fragment_name_optoinsOfDisciplineeNotification);

            case 3:
                //Top week
                return resources.getString(R.string.fragment_name_topWeek);

            case 4:
                //Lower week
                return resources.getString(R.string.fragment_name_lowerWeek);

                default:
                    return new String("ERROR");
        }
    }

    @Override
    public int getCount() {
        return 5;
    }

    //TODO Поручить адаптеру собрать всю информацию с фрагментов, информацию каждого фрагмента
    // упаковать по объектам и вернуть их в одном пакетет. Это позволит нам избавить от статических классов.
}
