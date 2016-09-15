package com.madmensoftware.www.pops.Fragments;


import android.content.Intent;
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
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.madmensoftware.www.pops.Activities.LoginActivity;
import com.madmensoftware.www.pops.Activities.NeighborActivity;
import com.madmensoftware.www.pops.Adapters.JobAdapter;
import com.madmensoftware.www.pops.Adapters.JobViewHolder;
import com.madmensoftware.www.pops.Adapters.NeighborNotificationAdapter;
import com.madmensoftware.www.pops.Adapters.NeighborNotificationViewHolder;
import com.madmensoftware.www.pops.Adapters.ParentJobAdapter;
import com.madmensoftware.www.pops.Adapters.ParentJobViewHolder;
import com.madmensoftware.www.pops.Adapters.ParentNotificationAdapter;
import com.madmensoftware.www.pops.Adapters.ParentNotificationViewHolder;
import com.madmensoftware.www.pops.Adapters.PopperJobsViewPagerAdapter;
import com.madmensoftware.www.pops.Models.Job;
import com.madmensoftware.www.pops.Models.Notification;
import com.madmensoftware.www.pops.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NeighborNotificationFragment extends Fragment {

    private static final String EXTRA_USER_ID = "com.madmensoftware.www.pops.userId";

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;




    //    private Query jobQuery;
//
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView jobRecyclerview;

    private NeighborNotificationAdapter mJobAdapter;
    private DatabaseReference mRef;
    private DatabaseReference mJobRef;

    private TabLayout tabLayout;
    private ViewPager viewPager;


    // Firebase instance variables
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Job, NeighborNotificationViewHolder>
            mFirebaseAdapter;



    public NeighborNotificationFragment() {
        // Required empty public constructor
    }

    public static NeighborNotificationFragment newInstance(String userId) {
        NeighborNotificationFragment fragment = new NeighborNotificationFragment();
        Log.i("Jobs", "Attached the jobs fragment");
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_neighbor_jobs, container, false);

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();



        jobRecyclerview = (RecyclerView) view.findViewById(R.id.neighbor_jobs_recycler_view);
        jobRecyclerview.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        mRef = FirebaseDatabase.getInstance().getReference();
        Query jobQuery = mRef.child("notification").orderByChild("neighborUid").equalTo(auth.getCurrentUser().getUid());
        mJobAdapter = new NeighborNotificationAdapter(Notification.class, R.layout.job_list_row, NeighborNotificationViewHolder.class, jobQuery, getContext());
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