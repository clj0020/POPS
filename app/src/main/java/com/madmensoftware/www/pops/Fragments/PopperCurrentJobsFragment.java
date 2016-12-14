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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.madmensoftware.www.pops.Adapters.NeighborJobAdapter;
import com.madmensoftware.www.pops.Adapters.NeighborJobViewHolder;
import com.madmensoftware.www.pops.Adapters.PopperCurrentJobsAdapter;
import com.madmensoftware.www.pops.Adapters.PopperJobAdapter;
import com.madmensoftware.www.pops.Adapters.PopperJobViewHolder;
import com.madmensoftware.www.pops.Adapters.PopperJobsViewPagerAdapter;
import com.madmensoftware.www.pops.Models.Job;
import com.madmensoftware.www.pops.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class PopperCurrentJobsFragment extends Fragment {

    @BindView(R.id.popper_current_jobs_recycler_view) RecyclerView jobRecyclerview;

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private DatabaseReference mRef;
    private DatabaseReference mJobRef;
    private DatabaseReference mFirebaseDatabaseReference;

    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter<Job, PopperJobViewHolder> mFirebaseAdapter;
    private PopperJobAdapter mJobAdapter;
    private PopperCurrentJobsAdapter mAdapter;

    public PopperCurrentJobsFragment() {
        // Required empty public constructor
    }

    public static PopperCurrentJobsFragment newInstance() {
        PopperCurrentJobsFragment fragment = new PopperCurrentJobsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_popper_current_jobs_list, container, false);
        ButterKnife.bind(this, view);

        auth = FirebaseAuth.getInstance();

        jobRecyclerview.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity());

        mRef = FirebaseDatabase.getInstance().getReference();

        jobRecyclerview.setLayoutManager(linearLayoutManager);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Query jobQuery = mRef.child("jobs").orderByChild("popperUid").equalTo(auth.getCurrentUser().getUid());
        mJobAdapter = new PopperJobAdapter(Job.class, R.layout.job_list_row, PopperJobViewHolder.class, jobQuery, getContext());

        mJobAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mJobAdapter.getItemCount();
                int lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 || (positionStart >= (friendlyMessageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    jobRecyclerview.scrollToPosition(positionStart);
                }
            }
        });

        jobRecyclerview.setAdapter(mJobAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();

        mJobAdapter.cleanup();
    }

}

