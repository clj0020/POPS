package com.madmensoftware.www.pops.Fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.madmensoftware.www.pops.Adapters.JobAdapter;
import com.madmensoftware.www.pops.Adapters.JobViewHolder;
import com.madmensoftware.www.pops.Adapters.ParentNotificationAdapter;
import com.madmensoftware.www.pops.Adapters.ParentNotificationViewHolder;
import com.madmensoftware.www.pops.Models.Job;
import com.madmensoftware.www.pops.Models.Notification;
import com.madmensoftware.www.pops.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ParentNotificationFragment extends Fragment {

    private static final String EXTRA_USER_ID = "com.madmensoftware.www.pops.userId";

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    private LinearLayoutManager linearLayoutManager;
    private RecyclerView parentNotificationRecyclerview;

    private JobAdapter mNotificationAdapter;
    private DatabaseReference mRef;
    private DatabaseReference mJobRef;

    private TabLayout tabLayout;
    private ViewPager viewPager;


    // Firebase instance variables
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Job, ParentNotificationViewHolder>
            mFirebaseAdapter;



    public ParentNotificationFragment() {
        // Required empty public constructor
    }

    public static ParentNotificationFragment newInstance(String userId) {
        ParentNotificationFragment fragment = new ParentNotificationFragment();
        Log.i("Jobs", "Attached the jobs fragment");
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parent_notification, container, false);

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        parentNotificationRecyclerview = (RecyclerView) view.findViewById(R.id.parent_notification_recycler_view);
        parentNotificationRecyclerview.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        mRef = FirebaseDatabase.getInstance().getReference();
        Query jobQuery = mRef.child("jobs").orderByChild("cachpar").equalTo(auth.getCurrentUser().getUid());
        mNotificationAdapter = new JobAdapter(Job.class, R.layout.job_list_row, JobViewHolder.class, jobQuery, getContext());
        mNotificationAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mNotificationAdapter.getItemCount();
                int lastVisiblePosition =
                        linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    parentNotificationRecyclerview.scrollToPosition(positionStart);
                }
            }
        });

        parentNotificationRecyclerview.setLayoutManager(linearLayoutManager);
        parentNotificationRecyclerview.setAdapter(mNotificationAdapter);

        return view;
    }
}
