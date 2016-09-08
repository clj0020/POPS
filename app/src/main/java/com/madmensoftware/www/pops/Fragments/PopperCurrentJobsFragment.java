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

    private static final String EXTRA_USER_ID = "com.madmensoftware.www.pops.userId";

    public PopperCurrentJobsFragment() {
        // Required empty public constructor
    }

    public static PopperCurrentJobsFragment newInstance(String userId) {
        PopperCurrentJobsFragment fragment = new PopperCurrentJobsFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_popper_current_jobs_list, container, false);
    }

}

