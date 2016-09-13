package com.madmensoftware.www.pops.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.madmensoftware.www.pops.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PopperCurrentJobsFragment extends Fragment {

    public PopperCurrentJobsFragment() {
        // Required empty public constructor
    }

    public static PopperCurrentJobsFragment newInstance() {
        PopperCurrentJobsFragment fragment = new PopperCurrentJobsFragment();
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_popper_current_jobs_list, container, false);
    }

}

