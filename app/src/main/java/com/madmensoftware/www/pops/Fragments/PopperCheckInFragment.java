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
public class PopperCheckInFragment extends Fragment {

    private static final String EXTRA_USER_ID = "com.madmensoftware.www.pops.userId";

    public PopperCheckInFragment() {
        // Required empty public constructor
    }

    public static PopperCheckInFragment newInstance(String userId) {
        PopperCheckInFragment fragment = new PopperCheckInFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_popper_check_in, container, false);
    }

}
