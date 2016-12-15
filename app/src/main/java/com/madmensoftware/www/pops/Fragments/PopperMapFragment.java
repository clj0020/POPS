package com.madmensoftware.www.pops.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madmensoftware.www.pops.Activities.JobDetailActivity;
import com.madmensoftware.www.pops.Adapters.MapWrapperLayout;
import com.madmensoftware.www.pops.Adapters.OnInfoWindowElemTouchListener;
import com.madmensoftware.www.pops.Helpers.AndroidPermissions;
import com.madmensoftware.www.pops.Helpers.GPSTracker;
import com.madmensoftware.www.pops.Helpers.TinyDB;
import com.madmensoftware.www.pops.Models.Job;
import com.madmensoftware.www.pops.Models.User;
import com.madmensoftware.www.pops.R;
import com.orhanobut.logger.Logger;
import org.parceler.Parcels;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class PopperMapFragment extends Fragment implements GPSTracker.UpdateLocationListener, OnMapReadyCallback, GoogleMap.OnCameraChangeListener, GeoQueryEventListener {
    
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private GeoFire geoFire;
    private GeoQuery geoQuery;

    private GoogleMap mGoogleMap;
    private GPSTracker gpsTracker;
    private Map<String,Marker> markers;
    private OnInfoWindowElemTouchListener infoButtonListener;

    private User mUser;
    private Bundle mBundle;
    private double mRadius = 10.00;


    @BindView(R.id.popper_map) MapView mapView;
    @BindView(R.id.popper_map_wrapper) MapWrapperLayout mapWrapperLayout;
    
    private ViewGroup infoWindow;
    private TextView infoTitle;
    private TextView infoSnippet;
    private Button infoButton;

    private PopperMapFragmentCallbacks mCallbacks;

    /**
     * Required interface for hosting activities
     */
    public interface PopperMapFragmentCallbacks {

    }

    public static PopperMapFragment newInstance() {
        PopperMapFragment fragment = new PopperMapFragment();
        Logger.d("Popper:", " PopperCheckInFragment created");
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) return;
        if (activity instanceof PopperMapFragment.PopperMapFragmentCallbacks) {
            mCallbacks = (PopperMapFragment.PopperMapFragmentCallbacks) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PopperMapFragment.PopperMapFragmentCallbacks) {
            mCallbacks = (PopperMapFragment.PopperMapFragmentCallbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onStart() {
        gpsTracker.onStart();
        this.geoQuery.addGeoQueryEventListener(this);
        Logger.d("PopperMap", "onStart: gpsTracker onStart called.");
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        gpsTracker.onStop();
        this.geoQuery.removeAllListeners();

        for (Marker marker: this.markers.values()) {
            marker.remove();
        }
        this.markers.clear();

        Logger.d("PopperMap", "onStop: gpsTracker onStop called.");

    }

    @Override
    public void onResume() {
        if (mapView != null) {
            mapView.onResume();
            Logger.d("PopperMap", "onResume: MapView is not null. MapView onResume called");
        }
        updateLocation();
        Logger.d("PopperMap", "onResume: updateLocation.");
        super.onResume();
    }

    @Override
    public void onPause() {
        gpsTracker.onPause();
        if (mapView != null) {
            mapView.onPause();
            Logger.d("PopperMap", "onPause: MapView is not null. MapView onPause called");
        }
        Logger.d("PopperMap", "onPause: gpsTracker onPause called");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mapView != null) {
            mapView.onDestroy();
            Logger.d("PopperMap", "onDestroy: MapView is not null. MapView onDestroy called");
        }
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_popper_map, container, false);
        ButterKnife.bind(this, view);
        
        this.markers = new HashMap<String, Marker>();

        // We want to reuse the info window for all the markers,
        // so let's create only one class member instance
        infoWindow = (ViewGroup) inflater.inflate(R.layout.map_marker_layout, null);
        infoTitle = (TextView)infoWindow.findViewById(R.id.map_marker_title);
        infoSnippet = (TextView)infoWindow.findViewById(R.id.map_marker_snippet);
        infoButton = (Button) infoWindow.findViewById(R.id.map_marker_button);

        infoButtonListener = new OnInfoWindowElemTouchListener(infoButton, getResources().getDrawable(android.R.drawable.btn_default), getResources().getDrawable(android.R.drawable.btn_default)) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                Job job = (Job) marker.getTag();
                Intent intent = new Intent(getActivity(), JobDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("job", job.getUid());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        };
        infoButton.setOnTouchListener(infoButtonListener);
        
        gpsTracker = new GPSTracker(getActivity());
        gpsTracker.setLocationListener(this);

        try {
            MapsInitializer.initialize(getActivity());
        } 
        catch (Exception e) {
            e.printStackTrace();
        }

        mapView.onCreate(mBundle);
        mapView.getMapAsync(this);
//        mGoogleMap.setOnCameraChangeListener(this);

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        TinyDB tinyDb = new TinyDB(getActivity());
        mUser = (User) tinyDb.getObject("User", User.class);

        //Logger.i("mUser", mUser.getUid());

        geoFire = new GeoFire(mDatabase.child("jobs_location"));

//        geoQuery = geoFire.queryAtLocation(new GeoLocation(gpsTracker.getLatitude(), gpsTracker.getLongitude()), mUser.getRadius());
//        Hard coded the radius for now
        // TODO: Make the popper map query change based on a slider's value in the filter pop up
        geoQuery = geoFire.queryAtLocation(new GeoLocation(gpsTracker.getLatitude(), gpsTracker.getLongitude()), 10);

       // sendNotificationToUser(auth.getCurrentUser().getUid(), "Testing the notifications.");

        return view;
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

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
                try {
                    mGoogleMap.setMyLocationEnabled(true);
                    setMap(googleMap);
                    Logger.d("PopperMap", "onMapReady: getLatitude is 0. Set my location enabled.");
                }
                catch (SecurityException e) {

                }
                latLang = new LatLng(17.3700, 78.4800);
                gpsTracker.setDefaultAddress("Hyderabad, Telangana");
            }

            if (mGoogleMap != null) {
                try {
                    mGoogleMap.setMyLocationEnabled(true);
                    setMap(googleMap);
                    Logger.d("PopperMap", "onMapReady: Google Map is not null. Set my location enabled.");
                }
                catch (SecurityException e) {

                }
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLang, 20);
                mGoogleMap.animateCamera(cameraUpdate);
            }

        }
        else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings_home_fragment_layout
            gpsTracker.showSettingsAlert();
            Logger.d("PopperMap", "onMapReady: Can't get location, gps or network not enabled.");
        }

    }

    private void updateLocation() {
        if (gpsTracker != null) {
            gpsTracker.onResume();
            if (Build.VERSION.SDK_INT >= 23) {
                if (!AndroidPermissions.getInstance().checkLocationPermission(getActivity())) {
                    AndroidPermissions.getInstance().displayLocationPermissionAlert(getActivity());
                    Logger.d("PopperMap", "updateLocation: gpsTracker onResume called, location permissions are not enabled. Display location permission alert.");
                }
            }
            gpsTracker.startLocationUpdates();
            mapView.getMapAsync(this);
            Logger.d("PopperMap", "updateLocation: gpsTracker onResume called, startLocationUpdates.");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            LatLng latLng;
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            updateMapWithLocationFirstTime(latLng);
            Logger.d("PopperMap", "onLocationChanged: location is not null. Update map with location first time.");
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

            Logger.d("PopperMap", "setMap: Map is not null. Clear map and set Ui settings");

        }
    }
    
    private void updateMapWithLocationFirstTime(LatLng latLang) {
        Logger.d("PopperMap", "updateMapWithLocationFirstTime called.");

        if (gpsTracker.getLatitude() != 0) {
            if (mGoogleMap != null) {
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLang, 20);
                mGoogleMap.animateCamera(cameraUpdate);

                Logger.d("GeoFire CurrLocation Lat: " + gpsTracker.getLatitude() + "Long: " + gpsTracker.getLongitude());

                GeoLocation currentLocation = new GeoLocation(gpsTracker.getLatitude(), gpsTracker.getLongitude());
                Logger.d("GeoFire CurrLocation Lat: " + currentLocation.latitude + "Long: " + currentLocation.longitude);

                geoFire = new GeoFire(mDatabase.child("jobs_location"));
//                GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(gpsTracker.getLatitude(), gpsTracker.getLongitude()), mUser.getRadius());
                GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(gpsTracker.getLatitude(), gpsTracker.getLongitude()), 10);
                geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                    @Override
                    public void onKeyEntered(String key, GeoLocation location) {
                        Logger.d("updateMapWithLocation GeoFireEntered:  Lat: " + location.latitude + " Long: " + location.longitude + " Key:" + key);
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
                                jobMarker.setTag(job);

                                markers.put(job.getUid(), jobMarker);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }

                    @Override
                    public void onKeyExited(String key) {
                        Logger.d("updateMapWithLocation GeoFireExitted: Key is no longer in the search area." + key);


                        mDatabase.child("jobs").child(key).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Job job = dataSnapshot.getValue(Job.class);

                                Marker marker = markers.get(job.getUid());
                                if (marker != null) {
                                    marker.remove();
                                    markers.remove(job.getUid());
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onKeyMoved(String key, GeoLocation location) {
                        Logger.d("updateMapWithLocation GeoFireMovedTo: Lat: " + location.latitude + "Long: " + location.longitude + "Key: " + key);

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
                                jobMarker.setTag(job);

                                Marker marker = markers.get(job.getUid());
                                if (marker != null) {
                                    animateMarkerTo(jobMarker, latitude, longitude);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onGeoQueryReady() {
                        Logger.d("updateMapWithLocation GeoFireReady: All initial data has been loaded and events have been fired!");
                    }

                    @Override
                    public void onGeoQueryError(DatabaseError error) {
                        Logger.d("updateMapWithLocation GeoFireError: " + error + "");
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
                    try {
                        mGoogleMap.setMyLocationEnabled(true);
                        updateLocation();
                        Logger.d("PopperMap", "onRequestPermissionsResult: Permission Granted.");
                    }
                    catch (SecurityException e) {
                        Log.e("PopperMap", "onRequestPermissionsResult: SecurityException:" + e.getMessage());
                    }

                } else {
                    AndroidPermissions.getInstance().displayAlert(getActivity(), AndroidPermissions.REQUEST_LOCATION);
                    Logger.d("PopperMap", "onRequestPermissionsResult: Alert Displayed.");
                }
                break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }
    
    public double convertRadiusToMeters(int miles) {
        return miles * 1609.344;
    }

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }

    private void animateMarkerTo(final Marker marker, final double lat, final double lng) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long DURATION_MS = 3000;
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final LatLng startPosition = marker.getPosition();
        handler.post(new Runnable() {
            @Override
            public void run() {
                float elapsed = SystemClock.uptimeMillis() - start;
                float t = elapsed/DURATION_MS;
                float v = interpolator.getInterpolation(t);

                double currentLat = (lat - startPosition.latitude) * v + startPosition.latitude;
                double currentLng = (lng - startPosition.longitude) * v + startPosition.longitude;
                marker.setPosition(new LatLng(currentLat, currentLng));

                // if animation is not finished yet, repeat
                if (t < 1) {
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        // Update the search criteria for this geoQuery and the circle on the map
        LatLng center = cameraPosition.target;
        this.geoQuery.setCenter(new GeoLocation(center.latitude, center.longitude));
        // radius in km
//        this.geoQuery.setRadius(mUser.getRadius()/1000);
        this.geoQuery.setRadius(10/1000);
    }

    @Override
    public void onKeyEntered(String key, GeoLocation location) {
        Logger.d("updateMapWithLocation GeoFireEntered:  Lat: " + location.latitude + " Long: " + location.longitude + " Key:" + key);
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
                jobMarker.setTag(job);

                markers.put(job.getUid(), jobMarker);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onKeyExited(String key) {
        Logger.d("updateMapWithLocation GeoFireExitted: Key is no longer in the search area." + key);


        mDatabase.child("jobs").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Job job = dataSnapshot.getValue(Job.class);

                Marker marker = markers.get(job.getUid());
                if (marker != null) {
                    marker.remove();
                    markers.remove(job.getUid());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onKeyMoved(String key, GeoLocation location) {
        Logger.d("updateMapWithLocation GeoFireMovedTo: Lat: " + location.latitude + "Long: " + location.longitude + "Key: " + key);

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
                jobMarker.setTag(job);

                Marker marker = markers.get(job.getUid());
                if (marker != null) {
                    animateMarkerTo(jobMarker, latitude, longitude);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onGeoQueryReady() {
        Logger.d("updateMapWithLocation GeoFireReady: All initial data has been loaded and events have been fired!");
    }

    @Override
    public void onGeoQueryError(DatabaseError error) {
        Logger.d("updateMapWithLocation GeoFireError: " + error + "");
    }




}