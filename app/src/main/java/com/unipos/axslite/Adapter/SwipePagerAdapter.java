package com.unipos.axslite.Adapter;

import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.unipos.axslite.Fragments.MapsFragment;
import com.unipos.axslite.Fragments.SummaryFragment;
import com.unipos.axslite.Fragments.ToDoTaskListFragment;
import com.unipos.axslite.UpdateDebug.ManualStopFragment;
import com.unipos.axslite.UpdateDebug.MenuFragment;
import com.unipos.axslite.UpdateDebug.ReturnParcelFragment;
import com.unipos.axslite.UpdateDebug.SelfDispatchActivity;

public class SwipePagerAdapter extends FragmentStatePagerAdapter {

    public SwipePagerAdapter(FragmentManager fm){
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (position){
            case 0:
                fragment = new MenuFragment();
                break;
//            case 2:
//                fragment = new ReturnParcelFragment();
//                break;
            default:
                fragment = new MenuFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 1;
    }

   /* @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title;
        switch (position){
            case 0:
                title = ToDoTaskListFragment.TO_DO_TASK_LIST_FRAGMENT_TITLE;
                break;
            case 1:
                title = MapsFragment.MAP_FRAGMENT_TITLE;
                break;
            case 2:
                title = SummaryFragment.SUMMARY_FRAGMENT_TITLE;
                break;
            default:
                title = ToDoTaskListFragment.TO_DO_TASK_LIST_FRAGMENT_TITLE;
        }
        return title;
    }*/
}
