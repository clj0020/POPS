package com.madmensoftware.www.pops.Fragments;


import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.madmensoftware.www.pops.Helpers.AndroidPermissions;
import com.madmensoftware.www.pops.Helpers.GPSTracker;
import com.madmensoftware.www.pops.Helpers.TinyDB;
import com.madmensoftware.www.pops.Models.Job;
import com.madmensoftware.www.pops.R;
import com.orhanobut.logger.Logger;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ParentCheckInFragment extends Fragment implements GPSTracker.UpdateLocationListener, OnMapReadyCallback {

    @BindView(R.id.popper_check_in_button) Button mCheckInButton;

    private GPSTracker gpsTracker;
    private MapView mMapView;
    private GeoFire geofire;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    private ParentCheckInCallbacks mCallbacks;

    public interface ParentCheckInCallbacks {
        void onCheckIn(double latitude, double longitude);
    }


    public ParentCheckInFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) return;
        if (activity instanceof ParentCheckInCallbacks) {
            mCallbacks = (ParentCheckInCallbacks) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ParentCheckInCallbacks) {
            mCallbacks = (ParentCheckInCallbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    public static ParentCheckInFragment newInstance() {
        ParentCheckInFragment fragment = new ParentCheckInFragment();
        Logger.d(" ParentCheckInFragment created");
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parent_check_in, container, false);
        ButterKnife.bind(this, view);

        Logger.d("onCreateView");

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        gpsTracker = new GPSTracker(getActivity());
        gpsTracker.changeUpdateInterval(400000);
        gpsTracker.setLocationListener(this);

        mCheckInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid = auth.getCurrentUser().getUid();

                gpsTracker.updateGPSCoordinates();
                double latitude = gpsTracker.getLatitude();
                double longitude = gpsTracker.getLongitude();

                mCallbacks.onCheckIn(latitude, longitude);

                Log.i("ParentCheckIn", "CheckInBtn: UserUID=" + uid);
                //Log.i("PopperCheckIn", "CheckInBtn: CurrentTime=" + currentTime);
                Log.i("ParentCheckIn", "CheckInBtn: CurrentLocation:  Latitude=" + gpsTracker.getLatitude() + " Longitude=" + gpsTracker.getLongitude());
            }
        });


        mMapView = (MapView) view.findViewById(R.id.popper_check_in_last_location);

        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            LatLng latLng;
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            Logger.d("onLocationChanged");
            Logger.d("latLng:" + latLng);
        }
    }

    private void updateLocation() {
        if (gpsTracker != null) {
            gpsTracker.onResume();
            if (Build.VERSION.SDK_INT >= 23) {
                if (!AndroidPermissions.getInstance().checkLocationPermission(getActivity())) {
                    AndroidPermissions.getInstance().displayLocationPermissionAlert(getActivity());

                    Logger.d("Permission not currently granted.");
                }
            }
            Logger.d("gpsTracker onResume");
            gpsTracker.startLocationUpdates();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case AndroidPermissions.REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateLocation();
                } else {
                    AndroidPermissions.getInstance().displayAlert(getActivity(), AndroidPermissions.REQUEST_LOCATION);
                }
                break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap map) {
        // create marker
        MarkerOptions marker = new MarkerOptions().position(
                new LatLng(32.610362, -85.472567)).title("Job Location");
        // Changing marker icon
        marker.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

        // adding marker
        map.addMarker(marker);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(32.610362, -85.472567)).zoom(12).build();
        map.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }

    @Override
    public void onStart() {
        super.onStart();
        gpsTracker.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        gpsTracker.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.onResume();
        }
        updateLocation();
    }

    @Override
    public void onPause() {
        super.onPause();
        gpsTracker.onPause();
        if (mMapView != null) {
            mMapView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        if (mMapView != null) {
            mMapView.onDestroy();
        }
        super.onDestroy();
    }

}
