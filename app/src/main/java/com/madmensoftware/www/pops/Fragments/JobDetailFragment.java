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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.madmensoftware.www.pops.Activities.JobDetailActivity;
import com.madmensoftware.www.pops.Activities.NeighborActivity;
import com.madmensoftware.www.pops.Activities.NeighborPaymentOverviewActivity;
import com.madmensoftware.www.pops.Activities.PopperActivity;
import com.madmensoftware.www.pops.Adapters.NeighborJobAdapter;
import com.madmensoftware.www.pops.Adapters.NeighborJobViewHolder;
import com.madmensoftware.www.pops.Adapters.PopperJobsViewPagerAdapter;
import com.madmensoftware.www.pops.Helpers.TinyDB;
import com.madmensoftware.www.pops.Models.Job;
import com.madmensoftware.www.pops.Models.Notification;
import com.madmensoftware.www.pops.Models.User;
import com.madmensoftware.www.pops.POPSApplication;
import com.madmensoftware.www.pops.R;
import com.orhanobut.logger.Logger;
import com.squareup.leakcanary.RefWatcher;

import org.fabiomsr.moneytextview.MoneyTextView;
import org.parceler.Parcels;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class JobDetailFragment extends Fragment implements OnMapReadyCallback {

    private Notification notification;

    @BindView(R.id.job_detail_neighbor_name) TextView mJobNeighborName;
    @BindView(R.id.job_detail_title) TextView mJobTitle;
    @BindView(R.id.job_detail_category) TextView mJobCategory;
    @BindView(R.id.job_detail_description) TextView mJobDescription;
    @BindView(R.id.job_detail_budget) MoneyTextView mJobBudget;
    @BindView(R.id.job_detail_status) TextView mJobStatus;
    @BindView(R.id.job_detail_duration) TextView mJobDuration;
    @BindView(R.id.job_detail_start_time_text_view) TextView mJobStartTime;
    @BindView(R.id.job_detail_completion_time_text_view) TextView mJobCompletionTime;

    @BindView(R.id.job_detail_neighbor_request_container) RelativeLayout mNeighborRequestContainer;
    @BindView(R.id.job_detail_neighbor_accept_request_btn) Button mNeighborAcceptRequestButton;
    @BindView(R.id.job_detail_neighbor_decline_request_btn) Button mNeighborDeclineRequestButton;
    @BindView(R.id.job_detail_neighbor_request_job_start_container) RelativeLayout mNeighborRequestJobStartContainer;
    @BindView(R.id.job_detail_neighbor_accept_job_start_btn) Button mNeighborAcceptJobStartRequestButton;
    @BindView(R.id.job_detail_neighbor_reject_job_start_btn) Button mNeighborRejectJobStartButton;
    @BindView(R.id.job_detail_neighbor_request_job_start_textview) TextView mNeighborRequestJobStartTextView;
    @BindView(R.id.job_detail_neighbor_in_progress_container) RelativeLayout mNeighborInProgressContainer;
    @BindView(R.id.job_detail_neighbor_in_progress_finish_btn) Button mNeighborInProgressFinishButton;
    @BindView(R.id.job_detail_neighbor_pay_container) RelativeLayout mNeighborPayContainer;
    @BindView(R.id.job_detail_neighbor_pay_btn) Button mNeighborPayButton;


    @BindView(R.id.job_detail_parent_request_container) RelativeLayout mParentRequestContainer;
    @BindView(R.id.job_detail_parent_accept_request_btn) Button mParentAcceptRequestButton;
    @BindView(R.id.job_detail_parent_decline_request_btn) Button mParentDeclineRequestButton;

    @BindView(R.id.job_detail_date_section) RelativeLayout mJobScheduledDateContainer;
    @BindView(R.id.job_detail_date) TextView mJobScheduledDate;


    @BindView(R.id.job_detail_popper_request_container) RelativeLayout mPopperRequestContainer;
    @BindView(R.id.job_detail_popper_request_btn) Button mPopperRequestJobButton;
    @BindView(R.id.job_detail_popper_job_start_container) RelativeLayout mPopperJobStartContainer;
    @BindView(R.id.job_detail_popper_job_start_btn) Button mPopperJobStartButton;
    @BindView(R.id.job_detail_popper_job_cancel_btn) Button mPopperJobCancelButton;
    @BindView(R.id.job_detail_popper_in_progress_container) RelativeLayout mPopperInProgressContainer;
    @BindView(R.id.job_detail_popper_in_progress_finish_btn) Button mPopperInProgressFinishButton;
    @BindView(R.id.job_detail_times_container) LinearLayout mJobDetailTimesContainer;
    @BindView(R.id.job_detail_popper_complete_container) RelativeLayout mPopperJobCompleteContainer;
    @BindView(R.id.job_detail_popper_complete_textview) TextView mPopperJobCompleteTextView;

    @BindView(R.id.job_detail_poppers_section) RelativeLayout mPoppersSection;
    @BindView(R.id.job_detail_popper_name) TextView mPopperNameTextView;


    @BindView(R.id.mapView) MapView mMapView;

    private Job mJob;
    private User mUser;
    private String mJobUid;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private DatabaseReference mRef;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Job, NeighborJobViewHolder> mFirebaseAdapter;
    private GeoFire geoFire;
    private GoogleMap mGoogleMap;
    private String mUid;
    private String mType;
    private String type;
    private String parentUid;
    private ValueEventListener mJobListener;
    private ValueEventListener mUserListener;
    private DatabaseReference mJobDatabaseReference;
    private DatabaseReference mUserDatabaseReference;

//    public static JobDetailFragment newInstance(Job job) {
//        JobDetailFragment jobDetailFragment= new JobDetailFragment();
//        Bundle args = new Bundle();
//        args.putParcelable("job", Parcels.wrap(job));
//        jobDetailFragment.setArguments(args);
//        return jobDetailFragment;
//    }


    public static JobDetailFragment newInstance(String jobUid) {
        JobDetailFragment jobDetailFragment= new JobDetailFragment();
        Bundle args = new Bundle();
        args.putString("job", jobUid);
        jobDetailFragment.setArguments(args);
        return jobDetailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //mJob = Parcels.unwrap(getArguments().getParcelable("job"));
        mJobUid = getArguments().getString("job");

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mJobDatabaseReference = FirebaseDatabase.getInstance().getReference().child("jobs").child(mJobUid);
        mUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(auth.getCurrentUser().getUid());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_detail, container, false);
        ButterKnife.bind(this, view);
        Logger.d("onCreateView");

        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately
        mMapView.getMapAsync(this);

        Logger.d("onCreateView: fbType=" + mType);
        Logger.d("onCreate: uid = " + mUid);
        Logger.d("onCreate: type = " + type);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    mUser = dataSnapshot.getValue(User.class);
                    TinyDB tinyDB = new TinyDB(getActivity());
                    tinyDB.putObject("User", mUser);
                    mJobDatabaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mJob = dataSnapshot.getValue(Job.class);

                            if(mJob == null) {
                                Toast.makeText(getActivity(), "Error Loading Job... Please try again.", Toast.LENGTH_LONG).show();
                            }
                            else {
                                mJobNeighborName.setText(mJob.getPosterName());
                                mJobTitle.setText(mJob.getTitle());
                                mJobCategory.setText(mJob.getCategory());
                                mJobDescription.setText(mJob.getDescription());

                                mJobBudget.setAmount(Float.valueOf(String.valueOf(mJob.getBudget())));

                                mJobDuration.setText(String.valueOf(mJob.getDuration()) + " hours");
                                mJobStatus.setText(mJob.getStatus());

                                if (mJob.getStatus().equals("open") || mJob.getStatus().equals("waitingForParentApproval")
                                        || mJob.getStatus().equals("pending") || mJob.getStatus().equals("active")
                                        || mJob.getStatus().equals("waitingForNeighborToStart") || mJob.getStatus().equals("neighborRejectedJobStart")) {
                                    mJobScheduledDateContainer.setVisibility(View.VISIBLE);
                                    mJobScheduledDate.setText(formatDateAndTime(mJob.getScheduledTime()));
                                }
                                else {
                                    mJobScheduledDateContainer.setVisibility(View.GONE);
                                }
                                if (mJob.getStatus().equals("active") || mJob.getStatus().equals("waitingForNeighborToStart")
                                        || mJob.getStatus().equals("neighborRejectedJobStart")) {
                                    mPoppersSection.setVisibility(View.VISIBLE);
                                    mPopperNameTextView.setText(mJob.getPopperName());
                                }

                                mType = mUser.getType();

                                switch (mType){
                                    case "Popper":{

                                        if(mJob.getStatus().equals("open")){
                                            mPopperRequestContainer.setVisibility(View.VISIBLE);
                                            mPopperJobStartContainer.setVisibility(View.GONE);
                                        }
                                        if(mJob.getStatus().equals("active")) {
                                            mPopperRequestContainer.setVisibility(View.GONE);
                                            Logger.d("Popper: Job Status is active");
                                            Logger.d("Popper: Job Status is " + mJob.getStatus());
                                            Logger.d("Popper: mUid is " + mUid);

                                            mPopperJobStartContainer.setVisibility(View.VISIBLE);
                                        }
                                        if (mJob.getStatus().equals("neighborRejectedJobStart")) {
                                            mPopperRequestContainer.setVisibility(View.GONE);

                                            mPopperJobStartContainer.setVisibility(View.VISIBLE);
                                        }

                                        if (mJob.getStatus().equals("inProgress")) {
                                            mPopperRequestContainer.setVisibility(View.GONE);
                                            mPopperJobStartContainer.setVisibility(View.GONE);
                                            //  mPopperInProgressContainer.setVisibility(View.VISIBLE);

                                        }
                                        if (mJob.getStatus().equals("complete")) {
                                            mPopperJobCompleteContainer.setVisibility(View.VISIBLE);
                                            mPopperRequestContainer.setVisibility(View.GONE);
                                            mPopperJobStartContainer.setVisibility(View.GONE);

                                            mJobDetailTimesContainer.setVisibility(View.VISIBLE);
                                            mJobStartTime.setText(formatDateAndTime(mJob.getStartTime()));
                                            mJobCompletionTime.setText(formatDateAndTime(mJob.getCompletionTime()));
                                        }


                                        //parentUid = user.getParentUid();

                                        mPopperRequestJobButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                Logger.d("onclick: fbType=" + mType);
                                                Logger.d("onclick: fbType=" + mType);

                                                TinyDB tinyDb = new TinyDB(getActivity());
                                                User popper = (User) tinyDb.getObject("User", User.class);

                                                sendNotificationToUser(popper.getUid(), mJob.getPosterUid(), popper.getName() + " has requested your job " + mJob.getTitle());


//                                            sendNotificationToUser(mJob,
//                                                    mJob.getPosterUid(),
//                                                    user.getUid(),
//                                                    mJob.getPosterUid(),
//                                                    user.getName() + " has requested your job titled " + mJob.getTitle() + ".",
//                                                    "Tap to view",
//                                                    "Job");

//                                            notification = new Notification();
//                                            notification.setTitle(user.getName() + " has requested your job titled " + mJob.getTitle() + ".");
//                                            notification.setDescription("Tap to view");
//                                            notification.setJobUid(mJob.getUid());
//                                            notification.setPopperUid(user.getUid());
//                                            notification.setParentUid(popper.getParentUid());
//                                            notification.setNeighborUid(mJob.getPosterUid());
//
//                                            notification.setRecieverUid(mJob.getPosterUid());
//                                            notification.setType("Job");
//                                            notification.setJob(mJob);
//
//                                            String notID = mDatabase.child("notifications").push().getKey();
//                                            notification.setUid(notID);
//
//                                            mDatabase.child("notifications").child(notID).setValue(notification);

                                                mJob.setStatus("waitingForParentApproval");
                                                mJob.setPopperUid(auth.getCurrentUser().getUid());
                                                mJob.setPopperName(popper.getName());
                                                mJob.setParentUid(popper.getParentUid());

                                                Notification notification = new Notification();
                                                notification.setRecieverUid(mJob.getParentUid());
                                                notification.setTitle(mJob.getPopperName() + " has requested a job.");
                                                notification.setDescription("Click here to accept " + mJob.getPopperName() + "'s request for the job: " + mJob.getTitle());
                                                notification.setJob(mJob);

                                                String notificationUid = mDatabase.child("notifications").push().getKey();
                                                mDatabase.child("notifications").child(notificationUid).setValue(notification);

                                                mDatabase.child("jobs").child(mJob.getUid()).setValue(mJob);

                                                mDatabase.child("job-posters").child(mJob.getPosterUid()).child(mJob.getUid()).setValue(mJob);
                                                mDatabase.child("job-posters").child(mJob.getPosterUid()).child(mJob.getUid()).child("statusCurrent").setValue("active");

                                                mDatabase.child("job-parents").child(popper.getParentUid()).child(mJob.getUid()).setValue(mJob);
                                                mDatabase.child("job-parents").child(popper.getParentUid()).child(mJob.getUid()).child("statusCurrent").setValue("active");

                                                mDatabase.child("job-poppers").child(mJob.getPopperUid()).child(mJob.getUid()).setValue(mJob);
                                                mDatabase.child("job-poppers").child(mJob.getPopperUid()).child(mJob.getUid()).child("statusCurrent").setValue("active");

                                                mPopperRequestContainer.setVisibility(View.GONE);
                                                Toast.makeText(getActivity(), "Requested the Job!", Toast.LENGTH_LONG).show();
                                            }
                                        });


                                        mPopperJobStartButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                TinyDB tinyDb = new TinyDB(getActivity());
                                                User popper = (User) tinyDb.getObject("User", User.class);

                                                Date currentDate = Calendar.getInstance().getTime();
                                                long currentTime = currentDate.getTime();

                                                mJob.setStatus("waitingForNeighborToStart");
                                                mJob.setClockInTime(currentTime);

                                                Notification notification = new Notification();
                                                notification.setRecieverUid(mJob.getPosterUid());
                                                notification.setTitle(mJob.getPopperName() + " has requested to start the job titled: " + mJob.getTitle());
                                                notification.setDescription("Tap to view and Accept or Decline the Start of Your Job.");
                                                notification.setJobUid(mJob.getUid());
                                                notification.setJob(mJob);

                                                Notification notification2 = new Notification();
                                                notification2.setRecieverUid(mJob.getParentUid());
                                                notification2.setTitle(mJob.getPopperName() + " has requested to start the job titled: " + mJob.getTitle());
                                                notification2.setDescription("You might need to request a check in.");
                                                notification2.setJobUid(mJob.getUid());
                                                notification2.setJob(mJob);

                                                String notificationUid = mDatabase.child("notifications").push().getKey();
                                                mDatabase.child("notifications").child(notificationUid).setValue(notification);

                                                String notificationUid2 = mDatabase.child("notifications").push().getKey();
                                                mDatabase.child("notifications").child(notificationUid2).setValue(notification2);

                                                mDatabase.child("jobs").child(mJob.getUid()).setValue(mJob);

                                                mDatabase.child("job-poppers").child(mJob.getPopperUid()).child(mJob.getUid()).setValue(mJob);
                                                mDatabase.child("job-poppers").child(mJob.getPopperUid()).child(mJob.getUid()).child("statusCurrent").setValue("active");
                                                mDatabase.child("job-posters").child(mJob.getPosterUid()).child(mJob.getUid()).setValue(mJob);
                                                mDatabase.child("job-posters").child(mJob.getPosterUid()).child(mJob.getUid()).child("statusCurrent").setValue("active");
                                                mDatabase.child("job-parents").child(popper.getParentUid()).child(mJob.getUid()).setValue(mJob);
                                                mDatabase.child("job-parents").child(popper.getParentUid()).child(mJob.getUid()).child("statusCurrent").setValue("active");


                                                mPopperJobStartContainer.setVisibility(View.GONE);
                                            }
                                        });

                                        mPopperInProgressFinishButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Date currentDate = Calendar.getInstance().getTime();
                                                long currentTime = currentDate.getTime();

                                                mJob.setStatus("complete");
                                                mJob.setCompletionTime(currentTime);

                                                mDatabase.child("jobs").child(mJob.getUid()).setValue(mJob);

                                                mDatabase.child("job-poppers").child(mJob.getPopperUid()).child(mJob.getUid()).setValue(mJob);
                                                mDatabase.child("job-poppers").child(mJob.getPopperUid()).child(mJob.getUid()).child("statusCurrent").setValue("active");
                                                mDatabase.child("job-posters").child(mJob.getPosterUid()).child(mJob.getUid()).setValue(mJob);
                                                mDatabase.child("job-posters").child(mJob.getPosterUid()).child(mJob.getUid()).child("statusCurrent").setValue("active");
                                                mDatabase.child("job-parents").child(mJob.getParentUid()).child(mJob.getUid()).setValue(mJob);
                                                mDatabase.child("job-parents").child(mJob.getParentUid()).child(mJob.getUid()).child("statusCurrent").setValue("active");

                                            }
                                        });

                                        break;
                                    }

                                    case "Neighbor":{
                                            if (mUser.getIsParent() && mUser.getChildUid().equals(mJob.getPopperUid())) {
                                                if (mJob.getStatus().equals("waitingForParentApproval")) {
                                                    mParentRequestContainer.setVisibility(View.VISIBLE);
                                                    mNeighborRequestContainer.setVisibility(View.GONE);
                                                    mNeighborRequestJobStartContainer.setVisibility(View.GONE);
                                                    mNeighborInProgressContainer.setVisibility(View.GONE);
                                                } else {
                                                    mParentRequestContainer.setVisibility(View.GONE);
                                                    mNeighborRequestContainer.setVisibility(View.GONE);
                                                    mNeighborRequestJobStartContainer.setVisibility(View.GONE);
                                                    mNeighborInProgressContainer.setVisibility(View.GONE);
                                                }

                                                mParentAcceptRequestButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        mJob.setStatus("pending");

                                                        Notification notification = new Notification();
                                                        notification.setRecieverUid(mJob.getPopperUid());
                                                        notification.setTitle("Your parent has accepted your job request for the job:" + mJob.getTitle());
                                                        notification.setDescription("Now you just need to get the approval of the neighbor.");
                                                        notification.setJob(mJob);

                                                        Notification notification2 = new Notification();
                                                        notification2.setRecieverUid(mJob.getPosterUid());
                                                        notification2.setTitle(mJob.getPopperName() + " has requested a job.");
                                                        notification2.setDescription("Click here to accept " + mJob.getPopperName() + "'s request for the job: " + mJob.getTitle());
                                                        notification2.setJob(mJob);

                                                        String notificationUid = mDatabase.child("notifications").push().getKey();
                                                        mDatabase.child("notifications").child(notificationUid).setValue(notification);

                                                        String notificationUid2 = mDatabase.child("notifications").push().getKey();
                                                        mDatabase.child("notifications").child(notificationUid2).setValue(notification2);

                                                        mDatabase.child("jobs").child(mJob.getUid()).setValue(mJob);

                                                        mDatabase.child("job-poppers").child(mJob.getPopperUid()).child(mJob.getUid()).setValue(mJob);
                                                        mDatabase.child("job-poppers").child(mJob.getPopperUid()).child(mJob.getUid()).child("statusCurrent").setValue("active");
                                                        mDatabase.child("job-posters").child(mJob.getPosterUid()).child(mJob.getUid()).setValue(mJob);
                                                        mDatabase.child("job-posters").child(mJob.getPosterUid()).child(mJob.getUid()).child("statusCurrent").setValue("active");
                                                        mDatabase.child("job-parents").child(mJob.getParentUid()).child(mJob.getUid()).setValue(mJob);
                                                        mDatabase.child("job-parents").child(mJob.getParentUid()).child(mJob.getUid()).child("statusCurrent").setValue("active");

                                                        mParentRequestContainer.setVisibility(View.GONE);
                                                    }
                                                });

                                                mParentDeclineRequestButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        mDatabase.child("job-parents").child(mJob.getParentUid()).child(mJob.getUid()).setValue(null);
                                                        mJob.setParentUid(null);

                                                        mDatabase.child("job-poppers").child(mJob.getPopperUid()).child(mJob.getUid()).setValue(null);
                                                        mJob.setPopperUid(null);
                                                        mJob.setPopperName(null);

                                                        mJob.setStatus("open");

                                                        mDatabase.child("jobs").child(mJob.getUid()).setValue(mJob);

                                                        mDatabase.child("job-posters").child(mJob.getPosterUid()).child(mJob.getUid()).setValue(mJob);
                                                        mDatabase.child("job-posters").child(mJob.getPosterUid()).child(mJob.getUid()).child("statusCurrent").setValue("active");

                                                        mParentRequestContainer.setVisibility(View.GONE);
                                                    }
                                                });
                                            }
                                            else {

                                                if (mJob.getStatus().equals("pending")) {
                                                    mNeighborRequestContainer.setVisibility(View.VISIBLE);
                                                    mNeighborRequestJobStartContainer.setVisibility(View.GONE);
                                                    mNeighborInProgressContainer.setVisibility(View.GONE);

                                                    Logger.d(" Neighbor: jobStatus is pending");
                                                    Logger.d(" Neighbor: job is " + mJob.getStatus());
                                                } else if (mJob.getStatus().equals("waitingForNeighborToStart")) {
                                                    mNeighborRequestContainer.setVisibility(View.GONE);
                                                    mNeighborRequestJobStartContainer.setVisibility(View.VISIBLE);
                                                    mNeighborInProgressContainer.setVisibility(View.GONE);
                                                } else if (mJob.getStatus().equals("inProgress")) {
                                                    mNeighborRequestContainer.setVisibility(View.GONE);
                                                    mNeighborRequestJobStartContainer.setVisibility(View.GONE);
                                                    mNeighborInProgressContainer.setVisibility(View.VISIBLE);
                                                } else if (mJob.getStatus().equals("complete")) {
                                                    mNeighborPayContainer.setVisibility(View.VISIBLE);
                                                    mJobDetailTimesContainer.setVisibility(View.VISIBLE);
                                                    mJobStartTime.setText(formatDateAndTime(mJob.getStartTime()));
                                                    mJobCompletionTime.setText(formatDateAndTime(mJob.getCompletionTime()));
                                                } else if (mJob.getStatus().equals("paid")) {
                                                    mNeighborRequestContainer.setVisibility(View.GONE);
                                                    mNeighborRequestJobStartContainer.setVisibility(View.GONE);
                                                    mNeighborInProgressContainer.setVisibility(View.GONE);
                                                    mNeighborPayContainer.setVisibility(View.GONE);
                                                } else {
                                                    mNeighborRequestContainer.setVisibility(View.GONE);
                                                    mNeighborInProgressContainer.setVisibility(View.GONE);
                                                    mNeighborRequestJobStartContainer.setVisibility(View.GONE);
                                                }

                                                mNeighborAcceptRequestButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        mJob.setStatus("active");

                                                        Notification notification = new Notification();
                                                        notification.setRecieverUid(mJob.getPopperUid());
                                                        notification.setTitle("Congratulations!! " + mJob.getPosterName() + " has accepted your job request for the job titled " + mJob.getTitle());
                                                        notification.setDescription("Tap to view");
                                                        notification.setJobUid(mJob.getUid());
                                                        notification.setJob(mJob);

                                                        Notification notification2 = new Notification();
                                                        notification2.setRecieverUid(mJob.getParentUid());
                                                        notification2.setTitle(mJob.getPosterName() + " has accepted" + mJob.getPopperName() + "'s job request for the job titled " + mJob.getTitle() + "!");
                                                        notification2.setDescription("Tap to view");
                                                        notification2.setJobUid(mJob.getUid());
                                                        notification2.setJob(mJob);


                                                        String notificationUid = mDatabase.child("notifications").push().getKey();
                                                        mDatabase.child("notifications").child(notificationUid).setValue(notification);

                                                        String notificationUid2 = mDatabase.child("notifications").push().getKey();
                                                        mDatabase.child("notifications").child(notificationUid2).setValue(notification2);


                                                        mDatabase.child("jobs").child(mJob.getUid()).setValue(mJob);

                                                        mDatabase.child("job-poppers").child(mJob.getPopperUid()).child(mJob.getUid()).setValue(mJob);
                                                        mDatabase.child("job-poppers").child(mJob.getPopperUid()).child(mJob.getUid()).child("statusCurrent").setValue("active");
                                                        mDatabase.child("job-posters").child(mJob.getPosterUid()).child(mJob.getUid()).setValue(mJob);
                                                        mDatabase.child("job-posters").child(mJob.getPosterUid()).child(mJob.getUid()).child("statusCurrent").setValue("active");
                                                        mDatabase.child("job-parents").child(mJob.getParentUid()).child(mJob.getUid()).setValue(mJob);
                                                        mDatabase.child("job-parents").child(mJob.getParentUid()).child(mJob.getUid()).child("statusCurrent").setValue("active");

                                                        mNeighborRequestContainer.setVisibility(View.GONE);
                                                        Toast.makeText(getActivity(), "You have accepted the job request!", Toast.LENGTH_LONG).show();
                                                    }
                                                });

                                                mNeighborDeclineRequestButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        mDatabase.child("job-poppers").child(mJob.getPopperUid()).child(mJob.getUid()).setValue(mJob);
                                                        mJob.setPopperName(null);
                                                        mJob.setPopperUid(null);

                                                        mDatabase.child("job-parents").child(mJob.getParentUid()).child(mJob.getUid()).setValue(null);
                                                        mJob.setParentUid(null);

                                                        mJob.setStatus("open");

                                                        mDatabase.child("jobs").child(mJob.getUid()).setValue(mJob);

                                                        mDatabase.child("job-posters").child(mJob.getPosterUid()).child(mJob.getUid()).setValue(mJob);
                                                        mDatabase.child("job-posters").child(mJob.getPosterUid()).child(mJob.getUid()).child("statusCurrent").setValue("active");

                                                        mNeighborRequestContainer.setVisibility(View.GONE);
                                                        Toast.makeText(getActivity(), "You have declined the job request.", Toast.LENGTH_LONG).show();
                                                    }
                                                });

                                                mNeighborAcceptJobStartRequestButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        Date currentDate = Calendar.getInstance().getTime();
                                                        long currentTime = currentDate.getTime();

                                                        mJob.setStatus("inProgress");
                                                        mJob.setStartTime(currentTime);

                                                        Notification notification = new Notification();
                                                        notification.setRecieverUid(mJob.getPopperUid());
                                                        notification.setTitle("Your job has started. When you are finished with your job, ask " + mJob.getPosterName() + " to press the complete button on the job.");
                                                        notification.setDescription("Tap to view");
                                                        notification.setJobUid(mJob.getUid());
                                                        notification.setJob(mJob);

                                                        Notification notification2 = new Notification();
                                                        notification2.setRecieverUid(mJob.getParentUid());
                                                        notification2.setTitle(mJob.getPopperName() + " has started the job:" + mJob.getTitle() + ". Don't forget to request a check in!");
                                                        notification2.setDescription("Tap to view");
                                                        notification2.setJobUid(mJob.getUid());
                                                        notification2.setJob(mJob);

                                                        Notification notification3 = new Notification();
                                                        notification3.setRecieverUid(mJob.getPosterUid());
                                                        notification3.setTitle(mJob.getPopperName() + " has started the job:" + mJob.getTitle() + " . When the popper is done, press the complete button on the job page.");
                                                        notification3.setDescription("Tap to view");
                                                        notification3.setJobUid(mJob.getUid());
                                                        notification3.setJob(mJob);

                                                        String notificationUid = mDatabase.child("notifications").push().getKey();
                                                        mDatabase.child("notifications").child(notificationUid).setValue(notification);

                                                        String notificationUid2 = mDatabase.child("notifications").push().getKey();
                                                        mDatabase.child("notifications").child(notificationUid2).setValue(notification2);

                                                        String notificationUid3 = mDatabase.child("notifications").push().getKey();
                                                        mDatabase.child("notifications").child(notificationUid3).setValue(notification3);

                                                        mDatabase.child("jobs").child(mJob.getUid()).setValue(mJob);

                                                        mDatabase.child("job-poppers").child(mJob.getPopperUid()).child(mJob.getUid()).setValue(mJob);
                                                        mDatabase.child("job-poppers").child(mJob.getPopperUid()).child(mJob.getUid()).child("statusCurrent").setValue("active");
                                                        mDatabase.child("job-posters").child(mJob.getPosterUid()).child(mJob.getUid()).setValue(mJob);
                                                        mDatabase.child("job-posters").child(mJob.getPosterUid()).child(mJob.getUid()).child("statusCurrent").setValue("active");
                                                        mDatabase.child("job-parents").child(mJob.getParentUid()).child(mJob.getUid()).setValue(mJob);
                                                        mDatabase.child("job-parents").child(mJob.getParentUid()).child(mJob.getUid()).child("statusCurrent").setValue("active");

                                                        Toast.makeText(getActivity(), "The job is starting at " + currentTime, Toast.LENGTH_LONG).show();

                                                        Logger.i("Start Time" + formatDateAndTime(currentTime));
                                                        mNeighborRequestJobStartContainer.setVisibility(View.GONE);
                                                    }
                                                });

                                                mNeighborRejectJobStartButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        mJob.setStatus("neighborRejectedJobStart");
                                                        mJob.setClockInTime(0);

                                                        Notification notification = new Notification();
                                                        notification.setRecieverUid(mJob.getPopperUid());
                                                        notification.setTitle(mJob.getPosterName() + " has rejected the start of the job:" + mJob.getTitle());
                                                        notification.setDescription("Tap to view");
                                                        notification.setJobUid(mJob.getUid());
                                                        notification.setJob(mJob);

                                                        Notification notification2 = new Notification();
                                                        notification2.setRecieverUid(mJob.getParentUid());
                                                        notification2.setTitle(mJob.getPosterName() + " has rejected the start of the job:" + mJob.getTitle());
                                                        notification2.setDescription("Tap to view");
                                                        notification2.setJobUid(mJob.getUid());
                                                        notification2.setJob(mJob);

                                                        Notification notification3 = new Notification();
                                                        notification3.setRecieverUid(mJob.getPosterUid());
                                                        notification3.setTitle("You have rejected the start of the job:" + mJob.getTitle());
                                                        notification3.setDescription("Tap to view");
                                                        notification3.setJobUid(mJob.getUid());
                                                        notification3.setJob(mJob);

                                                        String notificationUid = mDatabase.child("notifications").push().getKey();
                                                        mDatabase.child("notifications").child(notificationUid).setValue(notification);

                                                        String notificationUid2 = mDatabase.child("notifications").push().getKey();
                                                        mDatabase.child("notifications").child(notificationUid2).setValue(notification2);

                                                        String notificationUid3 = mDatabase.child("notifications").push().getKey();
                                                        mDatabase.child("notifications").child(notificationUid3).setValue(notification3);

                                                        mDatabase.child("jobs").child(mJob.getUid()).setValue(mJob);

                                                        mDatabase.child("job-poppers").child(mJob.getPopperUid()).child(mJob.getUid()).setValue(mJob);
                                                        mDatabase.child("job-poppers").child(mJob.getPopperUid()).child(mJob.getUid()).child("statusCurrent").setValue("active");
                                                        mDatabase.child("job-posters").child(mJob.getPosterUid()).child(mJob.getUid()).setValue(mJob);
                                                        mDatabase.child("job-posters").child(mJob.getPosterUid()).child(mJob.getUid()).child("statusCurrent").setValue("active");
                                                        mDatabase.child("job-parents").child(mJob.getParentUid()).child(mJob.getUid()).setValue(mJob);
                                                        mDatabase.child("job-parents").child(mJob.getParentUid()).child(mJob.getUid()).child("statusCurrent").setValue("active");

                                                        Toast.makeText(getActivity(), "You have rejected " + mJob.getPopperName() + "'s request to start the job.", Toast.LENGTH_LONG).show();

                                                        mNeighborRequestJobStartContainer.setVisibility(View.GONE);
                                                    }
                                                });


                                                mNeighborInProgressFinishButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Date currentDate = Calendar.getInstance().getTime();
                                                        long currentTime = currentDate.getTime();

                                                        mJob.setStatus("complete");
                                                        mJob.setCompletionTime(currentTime);

                                                        mJob.setTotalTime(totalTimeDifference(mJob.getStartTime(), mJob.getCompletionTime()));
                                                        mJob.setSubtotal(mJob.getBudget() * mJob.getTotalTime());
                                                        mJob.setTransactionFee(mJob.getSubtotal() * 0.1);
                                                        mJob.setTotal(mJob.getSubtotal() + mJob.getTransactionFee());

                                                        Notification notification = new Notification();
                                                        notification.setRecieverUid(mJob.getPopperUid());
                                                        notification.setTitle("You have completed the job:" + mJob.getTitle() + ". You are now awaiting payment from " + mJob.getPosterName() + ".");
                                                        notification.setDescription("Tap to view");
                                                        notification.setJobUid(mJob.getUid());
                                                        notification.setJob(mJob);

                                                        Notification notification2 = new Notification();
                                                        notification2.setRecieverUid(mJob.getParentUid());
                                                        notification2.setTitle(mJob.getPopperName() + " has completed the job:" + mJob.getTitle() + ". They are now awaiting payment from " + mJob.getPosterName() + ".");
                                                        notification2.setDescription("Tap to view");
                                                        notification2.setJobUid(mJob.getUid());
                                                        notification2.setJob(mJob);

                                                        Notification notification3 = new Notification();
                                                        notification3.setRecieverUid(mJob.getPosterUid());
                                                        notification3.setTitle("You now need to pay " + mJob.getTotal() +  " for the job " + mJob.getTitle() + ". Please go to the job page to see an overview of your purchase.");
                                                        notification3.setDescription("Tap to view");
                                                        notification3.setJobUid(mJob.getUid());
                                                        notification3.setJob(mJob);

                                                        String notificationUid = mDatabase.child("notifications").push().getKey();
                                                        mDatabase.child("notifications").child(notificationUid).setValue(notification);

                                                        String notificationUid2 = mDatabase.child("notifications").push().getKey();
                                                        mDatabase.child("notifications").child(notificationUid2).setValue(notification2);

                                                        String notificationUid3 = mDatabase.child("notifications").push().getKey();
                                                        mDatabase.child("notifications").child(notificationUid3).setValue(notification3);

                                                        mDatabase.child("jobs").child(mJob.getUid()).setValue(mJob);

                                                        mDatabase.child("job-poppers").child(mJob.getPopperUid()).child(mJob.getUid()).setValue(mJob);
                                                        mDatabase.child("job-poppers").child(mJob.getPopperUid()).child(mJob.getUid()).child("statusCurrent").setValue("active");
                                                        mDatabase.child("job-posters").child(mJob.getPosterUid()).child(mJob.getUid()).setValue(mJob);
                                                        mDatabase.child("job-posters").child(mJob.getPosterUid()).child(mJob.getUid()).child("statusCurrent").setValue("active");
                                                        mDatabase.child("job-parents").child(mJob.getParentUid()).child(mJob.getUid()).setValue(mJob);
                                                        mDatabase.child("job-parents").child(mJob.getParentUid()).child(mJob.getUid()).child("statusCurrent").setValue("active");

                                                        Logger.i("Completion Time" + formatDateAndTime(currentTime));

                                                        mNeighborInProgressContainer.setVisibility(View.GONE);
                                                    }
                                                });

                                                mNeighborPayButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        TinyDB tinyDb = new TinyDB(getActivity());
                                                        tinyDb.putString("job-uid", mJob.getUid());

                                                        Intent intent = new Intent(getActivity(), NeighborPaymentOverviewActivity.class);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);
                                                    }

                                                });
                                            }

                                        break;
                                    }
//                    case "Parent":{
//                        if(mJob.getStatus().equals("pending")){
//                            AcceptButton.setText("Accept Request");
//                            RejectButton.setVisibility(View.VISIBLE);
//                            AcceptButton.setVisibility(View.VISIBLE);
//
//                            Logger.d(" Parent: jobStatus is pending");
//                            Logger.d(" Parent: job is " + mJob.getStatus());
//                        }
//                        if(mJob.getStatus().equals("active")) {
//                            mDatabase.child("jobs").child(mJob.getUid()).child("parentUid").setValue(mUid);
//                            mDatabase.child("jobs").child(mJob.getUid()).child("cachpar").setValue(" ");
//
//                            Logger.d(" Parent: jobStatus is active");
//                            Logger.d(" Parent: job is " + mJob.getStatus());
//                        }
//
//                        // Parent approves of popper request
//                        AcceptButton.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
//                                TinyDB tinyDb = new TinyDB(getActivity());
//                                User parent = (User) tinyDb.getObject("User", User.class);
//
//                                Logger.d(" Parent: acceptButtonClicked Job Status is " + mJob.getStatus());
//
//                                mDatabase.child("notifications").child(mJob.getNotification()).child("parentUid").setValue("");
//
//                                notification = new Notification();
//                                notification.setTitle("Parent Accepted Job Request");
//                                notification.setDescription("Tap to view");
//                                notification.setJobUid(mJob.getUid());
//                                notification.setNeighborUid(mJob.getPosterUid());
//                                notification.setPopperUid(parent.getChildUid());
//
//                                Logger.d(" Parent: acceptButtonClicked User is " + parent.getUid());
//                                Logger.d(" Parent: acceptButtonClicked Current User is " + auth.getCurrentUser().getUid());
//
//                                Toast.makeText(getActivity(), "Job Has Been Requested", Toast.LENGTH_LONG).show();
//
//                                mDatabase.child("jobs").child(mJob.getUid()).child("status").setValue("pending");
//                                mDatabase.child("jobs").child(mJob.getUid()).child("cashPostNot").setValue(mJob.getPosterUid());
//
//                                String notificationID = mDatabase.child("notification").push().getKey();
//                                notification.setUid(notificationID);
//
//                                mDatabase.child("jobs").child(mJob.getUid()).child("notification").setValue(notificationID);
//                                mDatabase.child("jobs").child(mJob.getUid()).child("cachepop").setValue(parent.getChildUid());
//                                mDatabase.child("notification").child(notificationID).setValue(notification);
//
//                                AcceptButton.setVisibility(View.INVISIBLE);
//                                RejectButton.setVisibility(View.INVISIBLE);
//                            }
//                        });
//                        RejectButton.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
//                                TinyDB tinyDb = new TinyDB(getActivity());
//                                User parent = (User) tinyDb.getObject("User", User.class);
//
//                                mDatabase.child("notifications").child(mJob.getNotification()).setValue("");
//
//                                notification = new Notification();
//                                notification.setTitle("Job Request Rejected");
//                                notification.setDescription("Find Different Jobs");
//                                notification.setPopperUid(parent.getChildUid());
//                                notification.setParentUid("");
//
//                                Toast.makeText(getActivity(), "Reject Clicked", Toast.LENGTH_LONG).show();
//
//                                mDatabase.child("jobs").child(mJob.getUid()).child("status").setValue("open");
//                                mDatabase.child("jobs").child(mJob.getUid()).child("notification").setValue("");
//                                mDatabase.child("jobs").child(mJob.getUid()).child("parentUid").setValue("");
//
//                                Logger.d(" Parent: rejectButtonClicked User is " + user.getUid());
//                                Logger.d(" Parent: rejectButtonClicked Current User is " + auth.getCurrentUser().getUid());
//
//                                String notID = mDatabase.child("notification").push().getKey();
//                                notification.setUid(notID);
//                                mDatabase.child("notification").child(notID).setValue(notification);
//
//                                mJob.setNotification(notID);
//                                mJob.setPopperCache(parent.getChildUid());
//                                mDatabase.child("jobs").child(mJob.getUid()).setValue(mJob);
//
//                            }
//                        });
//                        break;
//
//                    }
                                    default:{
                                        //do something
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };



        mUserDatabaseReference.addValueEventListener(userListener);


        mUserListener = userListener;
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mUserListener != null) {
            mUserDatabaseReference.removeEventListener(mUserListener);
        }


        TinyDB tinyDB = new TinyDB(getActivity());
        tinyDB.putObject("User", mUser);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleMap = map;
        geoFire = new GeoFire(mDatabase.child("jobs_location"));
        geoFire.getLocation(mJobUid, new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {
                if (location != null) {
                    MarkerOptions marker = new MarkerOptions().position(
                            new LatLng(location.latitude, location.longitude)).title("Job Location");
                    // Changing marker icon
                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

                    // adding marker
                    mGoogleMap.addMarker(marker);
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(location.latitude, location.longitude)).zoom(20).build();
                    mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
                else {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
               // System.err.println("There was an error getting the GeoFire location: " + databaseError);
            }
        });

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.i("onDestroy()");
    }

    public static void sendNotificationToUser(final String recieverUid, final String senderUid, final String message) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        Map notification = new HashMap<>();
        notification.put("recieverUid", recieverUid);
        notification.put("senderUid", senderUid);
        notification.put("message", message);

        ref.child("notifications").push().setValue(notification);

        ref.child("notifications").child(recieverUid + senderUid).setValue(notification);
//        String uid = ref.child("notifications").push().getKey();
//        notification.put("uid", uid);

       // ref.child("notifications").child(uid).setValue(notification);
    }

    public String formatDateAndTime(long time) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a MM-dd-yyyy"); // Set your date format
        String currentData = sdf.format(date); // Get Date String according to date format

        return currentData;
    }

    public long totalTimeDifference(long startTime, long completionTime) {

        long difference = completionTime - startTime;

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;

        long elapsedHours = difference / hoursInMilli;

        return elapsedHours;
    }

}
