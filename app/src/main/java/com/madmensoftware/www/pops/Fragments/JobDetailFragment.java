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
import android.widget.Button;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madmensoftware.www.pops.Adapters.NeighborJobAdapter;
import com.madmensoftware.www.pops.Adapters.NeighborJobViewHolder;
import com.madmensoftware.www.pops.Adapters.PopperJobsViewPagerAdapter;
import com.madmensoftware.www.pops.Helpers.TinyDB;
import com.madmensoftware.www.pops.Models.Job;
import com.madmensoftware.www.pops.Models.Notification;
import com.madmensoftware.www.pops.Models.User;
import com.madmensoftware.www.pops.R;
import com.orhanobut.logger.Logger;

import org.parceler.Parcels;

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
    @BindView(R.id.job_detail_budget) TextView mJobBudget;
    @BindView(R.id.Accept) Button AcceptButton;
    @BindView(R.id.Reject) Button RejectButton;
    @BindView(R.id.mapView) MapView mMapView;

    private Job mJob;
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

    public static JobDetailFragment newInstance(Job job) {
        JobDetailFragment jobDetailFragment= new JobDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable("job", Parcels.wrap(job));
        jobDetailFragment.setArguments(args);
        return jobDetailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mJob = Parcels.unwrap(getArguments().getParcelable("job"));

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_detail, container, false);
        ButterKnife.bind(this, view);

        Logger.d("onCreateView");

        RejectButton.setVisibility(View.INVISIBLE);
        AcceptButton.setVisibility(View.INVISIBLE);

        if(mJob == null){
            Toast.makeText(getActivity(), "Error Loading Job... Please try again.", Toast.LENGTH_LONG).show();
        }
        else{
            mJobNeighborName.setText(mJob.getPosterName());
            mJobTitle.setText(mJob.getTitle());
            mJobCategory.setText(mJob.getCategory());
            mJobDescription.setText(mJob.getDescription());
            mJobBudget.setText(String.valueOf(mJob.getBudget()));
        }

        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        mMapView.getMapAsync(this);

        auth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();

        TinyDB tinyDb = new TinyDB(getActivity());
        User user = (User) tinyDb.getObject("User", User.class);

        Logger.d("userType" + user.getType());

        mUid = auth.getCurrentUser().getUid();

        Logger.d("Outside onDataChange: jobStatus: " + mJob.getStatus());

        mDatabase.child("users/" + mUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);

                Logger.d("onDataChangedCalled Job Status is " + mJob.getStatus());

                mType = user.getType();
                type = mType;

                Logger.d("onCreateView: fbType=" + mType);

                switch (mType){
                    case "Popper":{
                        Logger.d("inSwitch: fbType=" + mType);

                        if(mJob.getStatus().equals("open")){
                            AcceptButton.setVisibility(View.VISIBLE);
                        }
                        if(mJob.getStatus().equals("active")) {

                            Logger.d("Popper: Job Status is active");
                            Logger.d("Popper: Job Status is " + mJob.getStatus());
                            Logger.d("Popper: mUid is " + mUid);
                        }
                        //parentUid = user.getParentUid();
                        AcceptButton.setText("Request Job");
                        AcceptButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Logger.d("onclick: fbType=" + mType);
                                Logger.d("onclick: fbType=" + mType);

                                TinyDB tinyDb = new TinyDB(getActivity());
                                User popper = (User) tinyDb.getObject("User", User.class);

                                notification = new Notification();
                                notification.setTitle(user.getName() + " has requested your job titled " + mJob.getTitle() + ".");
                                notification.setDescription("Tap to view");
                                notification.setJobUid(mJob.getUid());
                                notification.setPopperUid(user.getUid());
                                notification.setParentUid(popper.getParentUid());
                                notification.setNeighborUid(mJob.getPosterUid());

                                notification.setRecieverUid(mJob.getPosterUid());
                                notification.setType("Job");
                                notification.setJob(mJob);

                                String notID = mDatabase.child("notifications").push().getKey();
                                notification.setUid(notID);

                                mDatabase.child("notifications").child(notID).setValue(notification);

                                mDatabase.child("jobs").child(mJob.getUid()).child("status").setValue("pending");
                                mDatabase.child("jobs").child(mJob.getUid()).child("popperCache").setValue(popper.getUid());

                                //mDatabase.child("jobs").child(mJob.getUid()).child("popperUid").setValue(popper.getUid());

                                AcceptButton.setVisibility(View.INVISIBLE);

                                Toast.makeText(getActivity(), "Requested the Job!", Toast.LENGTH_LONG).show();

                                Logger.d(" Popper: acceptButtonClicked User is " + popper.getType());
                                Logger.d(" Popper: acceptButtonClicked Current User is " + auth.getCurrentUser().getUid());
                                Logger.d(" Popper: acceptButtonClicked Job Title is " + mJob.getTitle());
                                Logger.d(" Popper: acceptButtonClicked Job Status is " + mJob.getStatus());
                            }
                        });
                        break;
                    }

                    case "Neighbor":{
                        if(mJob.getStatus().equals("pending")){
                            AcceptButton.setText("Accept Request");
                            RejectButton.setVisibility(View.VISIBLE);
                            AcceptButton.setVisibility(View.VISIBLE);

                            Logger.d(" Neighbor: jobStatus is pending");
                            Logger.d(" Neighbor: job is " + mJob.getStatus());
                        }
                        AcceptButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Logger.d(" Neighbor: acceptButtonClicked Job Status is " + mJob.getStatus());

                                notification = new Notification();
                                notification.setTitle(mJob.getPosterName() + " accepted your job request for the job titled " + mJob.getTitle());
                                notification.setDescription("Tap to view");
                                notification.setJobUid(mJob.getUid());
                                notification.setNeighborUid(user.getUid());
                                notification.setPopperUid(mJob.getPopperUid());
                                notification.setParentUid(mJob.getParentUid());

                                notification.setRecieverUid(mJob.getPopperUid());
                                notification.setType("Job");

                                String notID = mDatabase.child("notifications").push().getKey();
                                notification.setUid(notID);

                                mDatabase.child("notifications").child(notID).setValue(notification);

                                mDatabase.child("jobs").child(mJob.getUid()).child("status").setValue("active");
                                mDatabase.child("jobs").child(mJob.getUid()).child("popperCache").setValue("");
                                mDatabase.child("jobs").child(mJob.getUid()).child("popperUid").setValue(mJob.getPopperCache());
                                mDatabase.child("jobs").child(mJob.getUid()).child("notifications").setValue(notID);

                                Logger.d(" Neighbor: acceptButtonClicked UID is " + mUid);
                            }
                        });
                        RejectButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                notification = new Notification();
                                notification.setTitle("Job Request Rejected by " + user.getName());
                                notification.setDescription("Please contact parent or guardian");

                                notification.setPopperUid(mJob.getPopperUid());
                                notification.setParentUid(mJob.getParentUid());
                                notification.setNeighborUid(user.getUid());

                                notification.setType("Job");
                                notification.setJob(mJob);
                                notification.setRecieverUid(mJob.getPopperUid());

                                String notID = mDatabase.child("notifications").push().getKey();
                                notification.setUid(notID);

                                mDatabase.child("notifications").child(notID).setValue(notification);

                                mDatabase.child("jobs").child(mJob.getUid()).child("status").setValue("open");
                                mDatabase.child("jobs").child(mJob.getUid()).child("popperCache").setValue("");
                                mDatabase.child("jobs").child(mJob.getUid()).child("notification").setValue(notID);

                            }
                        });
                        break;
                    }
                    case "Parent":{
                        if(mJob.getStatus().equals("pending")){
                            AcceptButton.setText("Accept Request");
                            RejectButton.setVisibility(View.VISIBLE);
                            AcceptButton.setVisibility(View.VISIBLE);

                            Logger.d(" Parent: jobStatus is pending");
                            Logger.d(" Parent: job is " + mJob.getStatus());
                        }
                        if(mJob.getStatus().equals("active")) {
                            mDatabase.child("jobs").child(mJob.getUid()).child("parentUid").setValue(mUid);
                            mDatabase.child("jobs").child(mJob.getUid()).child("cachpar").setValue(" ");

                            Logger.d(" Parent: jobStatus is active");
                            Logger.d(" Parent: job is " + mJob.getStatus());
                        }

                        // Parent approves of popper request
                        AcceptButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                TinyDB tinyDb = new TinyDB(getActivity());
                                User parent = (User) tinyDb.getObject("User", User.class);

                                Logger.d(" Parent: acceptButtonClicked Job Status is " + mJob.getStatus());

                                mDatabase.child("notifications").child(mJob.getNotification()).child("parentUid").setValue("");

                                notification = new Notification();
                                notification.setTitle("Parent Accepted Job Request");
                                notification.setDescription("Tap to view");
                                notification.setJobUid(mJob.getUid());
                                notification.setNeighborUid(mJob.getPosterUid());
                                notification.setPopperUid(parent.getChildUid());

                                Logger.d(" Parent: acceptButtonClicked User is " + parent.getUid());
                                Logger.d(" Parent: acceptButtonClicked Current User is " + auth.getCurrentUser().getUid());

                                Toast.makeText(getActivity(), "Job Has Been Requested", Toast.LENGTH_LONG).show();

                                mDatabase.child("jobs").child(mJob.getUid()).child("status").setValue("pending");
                                mDatabase.child("jobs").child(mJob.getUid()).child("cashPostNot").setValue(mJob.getPosterUid());

                                String notificationID = mDatabase.child("notification").push().getKey();
                                notification.setUid(notificationID);

                                mDatabase.child("jobs").child(mJob.getUid()).child("notification").setValue(notificationID);
                                mDatabase.child("jobs").child(mJob.getUid()).child("cachepop").setValue(parent.getChildUid());
                                mDatabase.child("notification").child(notificationID).setValue(notification);

                                AcceptButton.setVisibility(View.INVISIBLE);
                                RejectButton.setVisibility(View.INVISIBLE);
                            }
                        });
                        RejectButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                TinyDB tinyDb = new TinyDB(getActivity());
                                User parent = (User) tinyDb.getObject("User", User.class);

                                mDatabase.child("notifications").child(mJob.getNotification()).setValue("");

                                notification = new Notification();
                                notification.setTitle("Job Request Rejected");
                                notification.setDescription("Find Different Jobs");
                                notification.setPopperUid(parent.getChildUid());
                                notification.setParentUid("");

                                Toast.makeText(getActivity(), "Reject Clicked", Toast.LENGTH_LONG).show();

                                mDatabase.child("jobs").child(mJob.getUid()).child("status").setValue("open");
                                mDatabase.child("jobs").child(mJob.getUid()).child("notification").setValue("");
                                mDatabase.child("jobs").child(mJob.getUid()).child("parentUid").setValue("");

                                Logger.d(" Parent: rejectButtonClicked User is " + user.getUid());
                                Logger.d(" Parent: rejectButtonClicked Current User is " + auth.getCurrentUser().getUid());

                                String notID = mDatabase.child("notification").push().getKey();
                                notification.setUid(notID);
                                mDatabase.child("notification").child(notID).setValue(notification);

                                mJob.setNotification(notID);
                                mJob.setPopperCache(parent.getChildUid());
                                mDatabase.child("jobs").child(mJob.getUid()).setValue(mJob);

                            }
                        });
                        break;

                    }
                    default:{
                        //do something
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Logger.d("onCreateView: fbType=" + mType);

        Logger.d("onCreate: uid = " + mUid);
        Logger.d("onCreate: type = " + type);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleMap = map;
        geoFire = new GeoFire(mDatabase.child("jobs_location"));
        geoFire.getLocation(mJob.getUid(), new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {
                if (location != null) {
                    MarkerOptions marker = new MarkerOptions().position(
                            new LatLng(location.latitude, location.longitude)).title("Job Location");
                    // Changing marker icon
                    marker.icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

                    // adding marker
                    mGoogleMap.addMarker(marker);
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(location.latitude, location.longitude)).zoom(12).build();
                    mGoogleMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition));
                    System.out.println(String.format("The location for key %s is [%f,%f]", key, location.latitude, location.longitude));
                } else {
                    System.out.println(String.format("There is no location for key %s in GeoFire", key));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("There was an error getting the GeoFire location: " + databaseError);
            }
        });

    }



}
