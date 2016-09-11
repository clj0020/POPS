package com.madmensoftware.www.pops.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.madmensoftware.www.pops.Fragments.JobDetailFragment;
import com.madmensoftware.www.pops.Models.Job;

import java.util.ArrayList;

/**
 * Created by carsonjones on 9/10/16.
 */
public class JobDetailPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Job> mJobs;

    public JobDetailPagerAdapter(FragmentManager fm, ArrayList<Job> jobs) {
        super(fm);
        mJobs = jobs;
    }

    @Override
    public Fragment getItem(int position) {
        return JobDetailFragment.newInstance(mJobs.get(position));
    }

    @Override
    public int getCount() {
        return mJobs.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mJobs.get(position).getTitle();
    }
}
