package com.madmensoftware.www.pops.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.madmensoftware.www.pops.R;

import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class PopperPastJobsFragment extends Fragment {

    public PopperPastJobsFragment() {
        // Required empty public constructor
    }

    public static PopperPastJobsFragment newInstance() {
        PopperPastJobsFragment fragment = new PopperPastJobsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_popper_past_jobs, container, false);

        return view;
    }

}
