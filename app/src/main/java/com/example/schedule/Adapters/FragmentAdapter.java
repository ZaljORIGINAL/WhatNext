package com.example.schedule.Adapters;

import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.schedule.Fragments.ScheduleOptionsFragment;
import com.example.schedule.Fragments.TimeFragment;
import com.example.schedule.IntentHelper;
import com.example.schedule.Objects.Week;
import com.example.schedule.R;
import com.example.schedule.Fragments.WeekFragment;

import static com.example.schedule.ScheduleBuilderActivity.loverWeek;
import static com.example.schedule.ScheduleBuilderActivity.topWeek;

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
                main = ScheduleOptionsFragment.newInstance(position);
                return main;
            case 1:
                //Time list
                return TimeFragment.newInstance(position);

            case 2:
                return WeekFragment.newInstance(topWeek.getNumber());

            case 3:
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
                //Top week
                return resources.getString(R.string.fragment_name_topWeek);

            case 3:
                //Lower week
                return resources.getString(R.string.fragment_name_lowerWeek);

                default:
                    return new String("ERROR");
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
