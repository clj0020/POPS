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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.madmensoftware.www.pops.Adapters.JobAdapter;
import com.madmensoftware.www.pops.Adapters.JobViewHolder;
import com.madmensoftware.www.pops.Adapters.PopperJobsViewPagerAdapter;
import com.madmensoftware.www.pops.Models.Job;
import com.madmensoftware.www.pops.R;

import org.parceler.Parcels;

import static android.R.id.button2;

/**
 * A simple {@link Fragment} subclass.
 */
public class JobDetailFragment extends Fragment implements OnMapReadyCallback {

    private Job mJob;

    private TextView mJobNeighborName;
    private TextView mJobTitle;
    private TextView mJobCategory;
    private TextView mJobDescription;
    private TextView mJobBudget;

    MapView mMapView;

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;



    //    private Query jobQuery;
//
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView jobRecyclerview;

    private JobAdapter mJobAdapter;
    private DatabaseReference mRef;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PopperJobsViewPagerAdapter adapter;


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

    public void ButtonOnClick()
    {

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

        mJobNeighborName = (TextView) view.findViewById(R.id.job_detail_neighbor_name);
        mJobTitle = (TextView) view.findViewById(R.id.job_detail_title);
        mJobCategory = (TextView) view.findViewById(R.id.job_detail_category);
        mJobDescription = (TextView) view.findViewById(R.id.job_detail_description);
        mJobBudget = (TextView) view.findViewById(R.id.job_detail_budget);
        mMapView = (MapView) view.findViewById(R.id.mapView);

        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        auth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();
        //Query jobQuery = mRef.child("jobs").orderByChild("posterUid").orderByChild("popperUid").equalTo(auth.getCurrentUser().getUid());

        Button AcceptButton = (Button) view.findViewById(R.id.Accept);
        AcceptButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Toast.makeText(getActivity(), "Accept Clicked", Toast.LENGTH_LONG).show();
                mDatabase.child("jobs").child("-KRYMZCx2wAHN0f7q79e").child("popperUid").setValue("ybzUIvmFTNUCdzX67dw6nPdpgtD2");
                mDatabase.child("jobs").child("-KR_YrgH7_UH1jAU3-TX").child("status").setValue("Accepted");



            }
        });
        return view;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        // create marker
        MarkerOptions marker = new MarkerOptions().position(
                new LatLng(mJob.getLatitude(), mJob.getLongitude())).title("Job Location");
        // Changing marker icon
        marker.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

        // adding marker
        map.addMarker(marker);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(mJob.getLatitude(), mJob.getLongitude())).zoom(12).build();
        map.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }



}
