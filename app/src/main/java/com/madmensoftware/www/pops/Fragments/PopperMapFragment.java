package com.madmensoftware.www.pops.Fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;
import com.madmensoftware.www.pops.Activities.AddUserDetails;
import com.madmensoftware.www.pops.Activities.NeighborActivity;
import com.madmensoftware.www.pops.Activities.ParentActivity;
import com.madmensoftware.www.pops.Activities.PopperActivity;
import com.madmensoftware.www.pops.Adapters.MapWrapperLayout;
import com.madmensoftware.www.pops.Adapters.OnInfoWindowElemTouchListener;
import com.madmensoftware.www.pops.Helpers.AndroidPermissions;
import com.madmensoftware.www.pops.Helpers.GPSTracker;
import com.madmensoftware.www.pops.Helpers.TinyDB;
import com.madmensoftware.www.pops.Models.Job;
import com.madmensoftware.www.pops.Models.User;
import com.madmensoftware.www.pops.R;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import static com.madmensoftware.www.pops.R.id.dark;
import static com.madmensoftware.www.pops.R.id.map;
import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;

/**
 * A simple {@link Fragment} subclass.
 */
public class PopperMapFragment extends Fragment implements GPSTracker.UpdateLocationListener, OnMapReadyCallback {
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private GeoFire geoFire;
    private GeoLocation currentGeoLocation;

    private GoogleMap mGoogleMap;
    private GPSTracker gpsTracker;
    private MapView mapView;
    private Bundle mBundle;

    private ViewGroup infoWindow;
    private TextView infoTitle;
    private TextView infoSnippet;
    private Button infoButton;
    private OnInfoWindowElemTouchListener infoButtonListener;
    MapWrapperLayout mapWrapperLayout;

    private User mUser;

    public static PopperMapFragment newInstance(String userId) {
        PopperMapFragment fragment = new PopperMapFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_popper_map, container, false);

        mapView = (MapView) view.findViewById(R.id.popper_map);
        mapWrapperLayout = (MapWrapperLayout)view.findViewById(R.id.popper_map_wrapper);

        // We want to reuse the info window for all the markers,
        // so let's create only one class member instance
        infoWindow = (ViewGroup) inflater.inflate(R.layout.map_marker_layout, null);
        infoTitle = (TextView)infoWindow.findViewById(R.id.title);
        infoSnippet = (TextView)infoWindow.findViewById(R.id.snippet);
        infoButton = (Button)infoWindow.findViewById(R.id.button);

        infoButtonListener = new OnInfoWindowElemTouchListener(infoButton, getResources().getDrawable(android.R.drawable.btn_default), getResources().getDrawable(android.R.drawable.btn_default)) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                // Here we can perform some action triggered after clicking the button
                Toast.makeText(getActivity(), marker.getTitle() + "'s button clicked!", Toast.LENGTH_SHORT).show();
            }
        };

        infoButton.setOnTouchListener(infoButtonListener);


        gpsTracker = new GPSTracker(getActivity());
        gpsTracker.setLocationListener(this);

        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.onCreate(mBundle);
        mapView.getMapAsync(this);

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        TinyDB tinyDb = new TinyDB(getActivity());
        mUser = (User) tinyDb.getObject("User", User.class);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        // MapWrapperLayout initialization
        // 39 - default marker height
        // 20 - offset between the default InfoWindow bottom edge and it's content bottom edge
        mapWrapperLayout.init(mGoogleMap, getPixelsFromDp(getActivity(), 39 + 20));

        mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Setting up the infoWindow with current's marker info
                infoTitle.setText(marker.getTitle());
                infoSnippet.setText(marker.getSnippet());
                infoButtonListener.setMarker(marker);

                // We must call this to set the current marker and infoWindow references
                // to the MapWrapperLayout
                mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                return infoWindow;
            }
        });

        if (gpsTracker.checkLocationState()) {
            gpsTracker.startLocationUpdates();
            setMap(googleMap);
            LatLng latLang = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
            //First time if you don't have latitude and longitude user default address
            if (gpsTracker.getLatitude() == 0) {
                mGoogleMap.setMyLocationEnabled(true);
                latLang = new LatLng(17.3700, 78.4800);
                gpsTracker.setDefaultAddress("Hyderabad, Telangana");
            }

            if (mGoogleMap != null) {
                mGoogleMap.setMyLocationEnabled(true);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLang, 20);
                mGoogleMap.animateCamera(cameraUpdate);
            }

        }
        else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings_home_fragment_layout
            gpsTracker.showSettingsAlert();
        }

    }

    private void updateLocation() {
        if (gpsTracker != null) {
            gpsTracker.onResume();
            if (Build.VERSION.SDK_INT >= 23) {
                if (!AndroidPermissions.getInstance().checkLocationPermission(getActivity())) {
                    AndroidPermissions.getInstance().displayLocationPermissionAlert(getActivity());
                }
            }
            gpsTracker.startLocationUpdates();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            LatLng latLng;
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            updateMapWithLocationFirstTime(latLng);
        }
    }

    private void setMap(GoogleMap mGoogleMap) {
        if (mGoogleMap != null) {
            mGoogleMap.clear();
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
            mGoogleMap.getUiSettings().setTiltGesturesEnabled(true);
            mGoogleMap.getUiSettings().setCompassEnabled(false);
            mGoogleMap.getUiSettings().setRotateGesturesEnabled(true);
            mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        }
    }

    //first time when fragment opened we will call this and update map with current location and marker
    private void updateMapWithLocationFirstTime(LatLng latLang) {
        if (gpsTracker.getLatitude() != 0) {
            if (mGoogleMap != null) {
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLang, 20);
                mGoogleMap.animateCamera(cameraUpdate);

                Log.i("GeoFire CurrLocation", "Lat: " + gpsTracker.getLatitude() + "Long: " + gpsTracker.getLongitude());

                GeoLocation currentLocation = new GeoLocation(gpsTracker.getLatitude(), gpsTracker.getLongitude());
                Log.i("GeoFire CurrLocation", "Lat: " + currentLocation.latitude + "Long: " + currentLocation.longitude);

                geoFire = new GeoFire(mDatabase.child("jobs_location"));
                GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(gpsTracker.getLatitude(), gpsTracker.getLongitude()), mUser.getRadius());
                geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                    @Override
                    public void onKeyEntered(String key, GeoLocation location) {
                        Log.i("updateMapWithLocation", "GeoFireEntered:  Lat: " + location.latitude + " Long: " + location.longitude + " Key:" + key);

                        final double latitude = location.latitude;
                        final double longitude = location.longitude;

                        mDatabase.child("jobs").child(key).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Job job = dataSnapshot.getValue(Job.class);


                                Marker jobMarker = mGoogleMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(latitude, longitude))
                                        .snippet(job.getDescription())
                                        .title(job.getTitle()));

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }

                    @Override
                    public void onKeyExited(String key) {
                        Log.i("updateMapWithLocation", "GeoFireExitted: Key is no longer in the search area." + key);
                    }

                    @Override
                    public void onKeyMoved(String key, GeoLocation location) {
                        Log.i("updateMapWithLocation", "GeoFireMovedTo: Lat: " + location.latitude + "Long: " + location.longitude + "Key: " + key);

                        final double latitude = location.latitude;
                        final double longitude = location.longitude;

                        mDatabase.child("jobs").child(key).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Job job = dataSnapshot.getValue(Job.class);
                                Marker jobMarker = mGoogleMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(latitude, longitude))
                                        .snippet(job.getDescription())
                                        .title(job.getTitle()));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onGeoQueryReady() {
                        Log.i("updateMapWithLocation", "GeoFireReady: All initial data has been loaded and events have been fired!");
                    }

                    @Override
                    public void onGeoQueryError(DatabaseError error) {
                        Log.i("updateMapWithLocation", "GeoFireError: " + error + "");
                    }
                });
            }
        }
        else {
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

    public void addJobToMap(Job job) {
        LatLng jobLocation = new LatLng(job.getLatitude(), job.getLongitude());

        mGoogleMap.addMarker(new MarkerOptions()
                .position(jobLocation)
                .title(job.getTitle())
                .snippet(job.getDescription())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .alpha(0.7f));
    }

    // convert address to lng lat and add markers to map
    public void addMarkersToMap() {
        List<LatLng> jobLocations = new ArrayList<LatLng>();
        jobLocations.add(new LatLng(32.6085256, -85.4746521));
        jobLocations.add(new LatLng(32.6105717, -85.4726629));
        jobLocations.add(new LatLng(32.6105385, -85.4746521));
        jobLocations.add(new LatLng(32.735118, -85.576729));
        jobLocations.add(new LatLng(72.6085256, -25.4746521));

        String[] title = {"LandScaping", "Cleaning", "Arson", "Clean Garage"};
        String[] discription = {"Mow The Lawn", "Clean out my Garage", "Burn Down Neighbors House", "With a broom and shovel"};

        List<LatLng> nearbyJobs = getNearbyJobs(jobLocations, mUser.getRadius());

        if(nearbyJobs.size() == 0) {
            Toast.makeText(getActivity(), "No Jobs Near You.", Toast.LENGTH_LONG).show();
        }
        else {
            for (int i = 0; i < nearbyJobs.size(); i++) {
                mGoogleMap.addMarker(new MarkerOptions()
                    .position(nearbyJobs.get(i))
                    .title(title[i])
                    .snippet(discription[i])
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .alpha(0.7f));
            }
        }
    } //end addMarkersToMap

    public List<LatLng> getNearbyJobs(List<LatLng> jobLocations, int radius){
        List<LatLng> nearbyJobs = new ArrayList<LatLng>();
        for(int i = 0; i < jobLocations.size(); i++){
            double distanceBetween = SphericalUtil.computeDistanceBetween(jobLocations.get(i), new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()));
            if (distanceBetween < convertRadiusToMeters(radius)) {
                nearbyJobs.add(jobLocations.get(i));
            }
        }
        return nearbyJobs;
    }

    public double convertRadiusToMeters(int miles) {
        return miles * 1609.344;
    }

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
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
        if (mapView != null) {
            mapView.onResume();
        }
        updateLocation();
    }

    @Override
    public void onPause() {
        super.onPause();
        gpsTracker.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        if (mapView != null) {
            mapView.onDestroy();
        }
        super.onDestroy();
    }



}