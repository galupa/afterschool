package com.jemput.rangga.jemputan.fragments;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 4/28/2017.
 */

public class DashboardAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList();

    private final List<String> mFragmentTitleNames = new ArrayList();

    public DashboardAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleNames.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleNames.get(position);
    }
}
