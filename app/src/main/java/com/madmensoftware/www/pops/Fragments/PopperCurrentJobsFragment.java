package com.madmensoftware.www.pops.Fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.madmensoftware.www.pops.Adapters.JobAdapter;
import com.madmensoftware.www.pops.Adapters.JobViewHolder;
import com.madmensoftware.www.pops.Adapters.PopperCurrentJobsAdapter;
import com.madmensoftware.www.pops.Adapters.PopperJobsViewPagerAdapter;
import com.madmensoftware.www.pops.Adapters.SimpleSectionedRecyclerViewAdapter;
import com.madmensoftware.www.pops.Models.Job;
import com.madmensoftware.www.pops.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PopperCurrentJobsFragment extends Fragment {

    private static final String EXTRA_USER_ID = "com.madmensoftware.www.pops.userId";

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    //    private Query jobQuery;
//
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView jobRecyclerview;

    private JobAdapter mJobAdapter;
    private DatabaseReference mRef;
    private DatabaseReference mJobRef;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PopperJobsViewPagerAdapter adapter;


    // Firebase instance variables
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Job, JobViewHolder>
            mFirebaseAdapter;


    private RecyclerView mRecyclerView;
    private PopperCurrentJobsAdapter mAdapter;

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
        View view = inflater.inflate(R.layout.fragment_popper_current_jobs_list, container, false);
        auth = FirebaseAuth.getInstance();



        jobRecyclerview = (RecyclerView) view.findViewById(R.id.popper_current_jobs_recycler_view);
        jobRecyclerview.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        mRef = FirebaseDatabase.getInstance().getReference();
        Query jobQuery = mRef.child("jobs").orderByChild("popperUid").equalTo(auth.getCurrentUser().getUid());
        mJobAdapter = new JobAdapter(Job.class, R.layout.job_list_row, JobViewHolder.class, jobQuery, getContext());
        mJobAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mJobAdapter.getItemCount();
                int lastVisiblePosition =
                        linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    jobRecyclerview.scrollToPosition(positionStart);
                }
            }
        });

        jobRecyclerview.setLayoutManager(linearLayoutManager);
        jobRecyclerview.setAdapter(mJobAdapter);

        return view;
    }

}

