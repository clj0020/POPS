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
import com.madmensoftware.www.pops.Adapters.NeighborJobAdapter;
import com.madmensoftware.www.pops.Adapters.NeighborJobViewHolder;
import com.madmensoftware.www.pops.Adapters.ParentChildJobAdapter;
import com.madmensoftware.www.pops.Adapters.ParentChildJobViewHolder;
import com.madmensoftware.www.pops.Adapters.PopperJobsViewPagerAdapter;
import com.madmensoftware.www.pops.Models.Job;
import com.madmensoftware.www.pops.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by carson on 12/18/2016.
 */

public class ParentChildJobsFragment extends Fragment {

    private static final String EXTRA_USER_ID = "com.madmensoftware.www.pops.userId";

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private DatabaseReference mRef;
    private DatabaseReference mJobRef;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Job, ParentChildJobViewHolder> mFirebaseAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ParentChildJobAdapter mParentChildJobAdapter;

    @BindView(R.id.parent_child_jobs_recycler_view) RecyclerView jobRecyclerview;


    public ParentChildJobsFragment() {
        // Required empty public constructor
    }

    public static ParentChildJobsFragment newInstance(String userId) {
        ParentChildJobsFragment fragment = new ParentChildJobsFragment();
        Log.i("Jobs", "Attached the jobs fragment");
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parent_child_jobs, container, false);
        ButterKnife.bind(this, view);

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        jobRecyclerview.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity());

        mRef = FirebaseDatabase.getInstance().getReference();

        jobRecyclerview.setLayoutManager(linearLayoutManager);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Query jobQuery = mRef.child("job-parents").child(auth.getCurrentUser().getUid()).orderByChild("statusCurrent").equalTo("active");
        mParentChildJobAdapter = new ParentChildJobAdapter(Job.class, R.layout.job_list_row, ParentChildJobViewHolder.class, jobQuery, getContext());

        mParentChildJobAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mParentChildJobAdapter.getItemCount();
                int lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 || (positionStart >= (friendlyMessageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    jobRecyclerview.scrollToPosition(positionStart);
                }
            }
        });

        jobRecyclerview.setAdapter(mParentChildJobAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();

        mParentChildJobAdapter.cleanup();
    }



}

