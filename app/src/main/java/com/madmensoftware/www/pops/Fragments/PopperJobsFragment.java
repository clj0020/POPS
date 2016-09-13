package com.madmensoftware.www.pops.Fragments;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.madmensoftware.www.pops.Adapters.PopperJobsViewPagerAdapter;
import com.madmensoftware.www.pops.R;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class PopperJobsFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PopperJobsViewPagerAdapter adapter;
    

    private static final String EXTRA_USER_ID = "com.madmensoftware.www.pops.userId";

    public PopperJobsFragment() {
        // Required empty public constructor
    }

    public static PopperJobsFragment newInstance(String userId) {
        PopperJobsFragment fragment = new PopperJobsFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_popper_jobs, container, false);
        setHasOptionsMenu(true);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.popper_job_viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        Log.v("Layout","Tabs");
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.popper_job_tabs);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FFFFFF"));
        tabLayout.setTabTextColors(Color.parseColor("#707070"), Color.parseColor("#FFFFFF"));
        assert viewPager != null;
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(PopperCurrentJobsFragment.newInstance(), "Current Jobs");
        adapter.addFragment(PopperPastJobsFragment.newInstance(), "Past Jobs");
        viewPager.setAdapter(adapter);
    }


    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

}
