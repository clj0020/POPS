package com.madmensoftware.www.pops.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.madmensoftware.www.pops.Models.Job;
import com.madmensoftware.www.pops.R;

import org.parceler.Parcels;

/**
 * A simple {@link Fragment} subclass.
 */
public class JobDetailFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback {

    private Job mJob;

    private TextView mJobNeighborName;
    private TextView mJobTitle;
    private TextView mJobCategory;
    private TextView mJobDescription;
    private TextView mJobBudget;

    MapView mMapView;


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

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);
        mJobNeighborName.setText(mJob.getPosterName());
        mJobTitle.setText(mJob.getTitle());
        mJobCategory.setText(mJob.getCategory());
        mJobDescription.setText(mJob.getDescription());
        mJobBudget.setText("$" + mJob.getBudget());

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

    @Override
    public void onClick(View v) {

    }

}
