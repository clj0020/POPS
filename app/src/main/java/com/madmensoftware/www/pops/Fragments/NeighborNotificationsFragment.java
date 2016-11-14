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
import com.madmensoftware.www.pops.Adapters.NeighborNotificationsAdapter;
import com.madmensoftware.www.pops.Adapters.NeighborNotificationsViewHolder;
import com.madmensoftware.www.pops.Adapters.PopperJobsViewPagerAdapter;
import com.madmensoftware.www.pops.Models.Job;
import com.madmensoftware.www.pops.Models.Notification;
import com.madmensoftware.www.pops.R;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class NeighborNotificationsFragment extends Fragment {

    private static final String EXTRA_USER_ID = "com.madmensoftware.www.pops.userId";

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private DatabaseReference mRef;
    private DatabaseReference mJobRef;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Notification, NeighborNotificationsViewHolder> mFirebaseAdapter;
    private LinearLayoutManager linearLayoutManager;
    private NeighborNotificationsAdapter mNeighborNotificationsAdapter;

    @BindView(R.id.neighbor_notifications_recycler_view) RecyclerView notificationsRecyclerview;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PopperJobsViewPagerAdapter adapter;

    public NeighborNotificationsFragment() {
        // Required empty public constructor
    }

    public static NeighborNotificationsFragment newInstance(String userId) {
        NeighborNotificationsFragment fragment = new NeighborNotificationsFragment();
        Logger.i("Attached the notifications fragment.");
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_neighbor_notifications, container, false);
        ButterKnife.bind(this, view);

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        notificationsRecyclerview.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity());

        mRef = FirebaseDatabase.getInstance().getReference();
        Query notificationQuery = mRef.child("notifications").orderByChild("recieverUid").equalTo(auth.getCurrentUser().getUid());

        mNeighborNotificationsAdapter = new NeighborNotificationsAdapter(Notification.class, R.layout.notification_list_row, NeighborNotificationsViewHolder.class, notificationQuery, getContext());
        mNeighborNotificationsAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mNeighborNotificationsAdapter.getItemCount();
                int lastVisiblePosition =
                        linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    notificationsRecyclerview.scrollToPosition(positionStart);
                }
            }
        });

        notificationsRecyclerview.setLayoutManager(linearLayoutManager);
        notificationsRecyclerview.setAdapter(mNeighborNotificationsAdapter);

        return view;
    }

}
