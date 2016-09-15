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
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.madmensoftware.www.pops.Adapters.JobAdapter;
import com.madmensoftware.www.pops.Adapters.JobViewHolder;
import com.madmensoftware.www.pops.Adapters.PopperJobsViewPagerAdapter;
import com.madmensoftware.www.pops.Helpers.TinyDB;
import com.madmensoftware.www.pops.Models.Job;
import com.madmensoftware.www.pops.Models.Notification;
import com.madmensoftware.www.pops.Models.User;
import com.madmensoftware.www.pops.R;

import org.parceler.Parcels;

import static android.R.id.button2;

/**
 * A simple {@link Fragment} subclass.
 */
public class JobDetailFragment extends Fragment implements OnMapReadyCallback {

    private Notification notification;

    private Job mJob;

    private TextView mJobNeighborName;
    private TextView mJobTitle;
    private TextView mJobCategory;
    private TextView mJobDescription;
    private TextView mJobBudget;
    private Button AcceptButton;
    private Button RejectButton;

    MapView mMapView;

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    private String mUid;
    private String mType;
    private String type;
    private String parentUid;

    private String ButtonText;

    //    private Query jobQuery;
//
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView jobRecyclerview;

    private JobAdapter mJobAdapter;
    private DatabaseReference mRef;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PopperJobsViewPagerAdapter adapter;

    private GeoFire geoFire;
    private GoogleMap mGoogleMap;


    // Firebase instance variables
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Job, JobViewHolder>
            mFirebaseAdapter;



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
        AcceptButton = (Button) view.findViewById(R.id.Accept);
        RejectButton = (Button) view.findViewById(R.id.Reject);
        RejectButton.setVisibility(View.INVISIBLE);

        mJobNeighborName = (TextView) view.findViewById(R.id.job_detail_neighbor_name);
        mJobTitle = (TextView) view.findViewById(R.id.job_detail_title);
        mJobCategory = (TextView) view.findViewById(R.id.job_detail_category);
        mJobDescription = (TextView) view.findViewById(R.id.job_detail_description);
        mJobBudget = (TextView) view.findViewById(R.id.job_detail_budget);
        mMapView = (MapView) view.findViewById(R.id.mapView);

        mJobNeighborName.setText(mJob.getPosterName());
        mJobTitle.setText(mJob.getTitle());
        mJobCategory.setText(mJob.getCategory());
        mJobDescription.setText(mJob.getDescription());
        mJobBudget.setText(String.valueOf(mJob.getBudget()));




        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately


        auth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();
        //Query jobQuery = mRef.child("jobs").orderByChild("posterUid").orderByChild("popperUid").equalTo(auth.getCurrentUser().getUid());

        TinyDB tinyDb = new TinyDB(getActivity());


          mUid = auth.getCurrentUser().getUid();

        mDatabase.child("users/" + mUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);

                mType = user.getType();
                type = mType;
                Log.i("JOBDETAILS", "onCreate: fbType=" + mType);

                switch (mType){
                    case "Popper":{
                        Log.i("switch", "inSwitch: fbType=" + mType);

                        AcceptButton.setText("Request Job");
                        type = "Popper";
                        parentUid = user.getParentUid();
                        AcceptButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.i("switch", "onclick: fbType=" + mType);
                                Log.i("switch", "onclick: fbType=" + mType);

                                notification = new Notification();
                                notification.setName("New Job Request");
                                notification.setDescription("Tap to view");
                                notification.setJobUid(mJob.getUid());
                                notification.setNeighborUid(mJob.getPosterUid());
                                notification.setPopperUid(mUid);
                                notification.setParentUid(user.getParentUid());
                                Toast.makeText(getActivity(), "Accept Clicked", Toast.LENGTH_LONG).show();
                                mDatabase.child("jobs").child(mJob.getUid()).child("status").setValue("Negotiating");
                                mDatabase.child("jobs").child(mJob.getUid()).child("popperUid").setValue(mUid);
                                String notID = mDatabase.child("notification").push().getKey();
                                notification.setUid(notID);
                                mDatabase.child("notification").child(notID).setValue(notification);

                            }
                        });
                        break;
                    }
                    case "Parent":{
                        AcceptButton.setText("Accept Request");
                        RejectButton.setVisibility(View.VISIBLE);
                        type = "Parent";
                        AcceptButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                notification = new Notification();
                                notification.setName("New Job Request");
                                notification.setDescription("Tap to view");
                                notification.setJobUid(mJob.getUid());
                                notification.setNeighborUid(mJob.getPosterUid());
                                notification.setPopperUid(user.getChildUid());
                                notification.setParentUid(mUid);
                                Toast.makeText(getActivity(), "Accept Clicked", Toast.LENGTH_LONG).show();
                                mDatabase.child("jobs").child(mJob.getUid()).child("status").setValue("Negotiating");
                                mDatabase.child("jobs").child(mJob.getUid()).child("parentUid").setValue(mUid);
                                String notID = mDatabase.child("notification").push().getKey();
                                notification.setUid(notID);
                                mDatabase.child("notification").child(notID).setValue(notification);

                            }
                        });
                        RejectButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                notification = new Notification();
                                notification.setName("Job Request Rejected");
                                notification.setDescription("Please contact parent or guardian");
                                notification.setPopperUid(user.getChildUid());
                                notification.setParentUid(mUid);
                                Toast.makeText(getActivity(), "Reject Clicked", Toast.LENGTH_LONG).show();
                                mDatabase.child("jobs").child(mJob.getUid()).child("status").setValue("Open");
                                mDatabase.child("jobs").child(mJob.getUid()).child("parentUid").setValue("no popper");
                                String notID = mDatabase.child("notification").push().getKey();
                                notification.setUid(notID);
                                mDatabase.child("notification").child(notID).setValue(notification);
                            }
                        });
                        break;

                    }
                    case "Neighbor":{
                        AcceptButton.setText("Accept Request");
                        RejectButton.setVisibility(View.VISIBLE);
                        type = "Neighbor";
                        AcceptButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                notification = new Notification();
                                notification.setName("Accepted Job Request");
                                notification.setDescription("Tap to view");
                                notification.setJobUid(mJob.getUid());
                                notification.setNeighborUid(mJob.getPosterUid());
                                notification.setPopperUid(user.getChildUid());
                                notification.setParentUid(mJob.getParentUid());
                                Toast.makeText(getActivity(), "Accept Clicked", Toast.LENGTH_LONG).show();
                                mDatabase.child("jobs").child(mJob.getUid()).child("status").setValue("Active");
                                String notID = mDatabase.child("notification").push().getKey();
                                notification.setUid(notID);
                                mDatabase.child("notification").child(notID).setValue(notification);

                            }
                        });
                        RejectButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                notification = new Notification();
                                notification.setName("Job Request Rejected");
                                notification.setDescription("Please contact parent or guardian");
                                notification.setPopperUid(user.getChildUid());
                                notification.setParentUid(mJob.getParentUid());
                                Toast.makeText(getActivity(), "Reject Clicked", Toast.LENGTH_LONG).show();
                                mDatabase.child("jobs").child(mJob.getUid()).child("status").setValue("Open");
                                mDatabase.child("jobs").child(mJob.getUid()).child("parentUid").setValue("no parent");
                                mDatabase.child("jobs").child(mJob.getUid()).child("popperUid").setValue("no popper");

                                String notID = mDatabase.child("notification").push().getKey();
                                notification.setUid(notID);
                                mDatabase.child("notification").child(notID).setValue(notification);
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

        Log.i("JOBDETAILS", "onCreate: fbType=" + mType);

        Log.i("JOBDETAIL", "onCreate: uid = " + mUid);
         Log.d("JOBDETAIL", "onCreate: type = " + type);


        return view;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        // create marker
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
