package com.madmensoftware.www.pops.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.madmensoftware.www.pops.Adapters.PopperJobsViewPagerAdapter;
import com.madmensoftware.www.pops.R;

import java.util.ArrayList;
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
        return inflater.inflate(R.layout.fragment_popper_jobs, container, false);
    }


}
