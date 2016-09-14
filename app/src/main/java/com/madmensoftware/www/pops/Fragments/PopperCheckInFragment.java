package com.madmensoftware.www.pops.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.madmensoftware.www.pops.R;

import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class PopperCheckInFragment extends Fragment implements OnMapReadyCallback {

    private static final String EXTRA_USER_ID = "com.madmensoftware.www.pops.userId";

    private MapView mMapView;
    private Button mCheckInButton;


    public PopperCheckInFragment() {
        // Required empty public constructor
    }

    public static PopperCheckInFragment newInstance(String userId) {
        PopperCheckInFragment fragment = new PopperCheckInFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_popper_check_in, container, false);

        mCheckInButton = (Button) view.findViewById(R.id.popper_check_in_button);

        mMapView = (MapView) view.findViewById(R.id.popper_check_in_last_location);

        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);

        return view;
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

}
