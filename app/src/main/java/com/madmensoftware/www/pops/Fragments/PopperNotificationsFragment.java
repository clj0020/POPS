package com.madmensoftware.www.pops.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.madmensoftware.www.pops.R;

import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class PopperNotificationsFragment extends Fragment {

    private static final String EXTRA_USER_ID = "com.madmensoftware.www.pops.userId";

    public PopperNotificationsFragment() {
        // Required empty public constructor
    }

    public static PopperNotificationsFragment newInstance() {
        PopperNotificationsFragment fragment = new PopperNotificationsFragment();
        Log.i("Popper:", " PopperNotificationsFragment created");
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_popper_notifications, container, false);
        return view;
    }

}