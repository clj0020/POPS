package com.madmensoftware.www.pops.Fragments;


import android.Manifest;
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
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.madmensoftware.www.pops.R;

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
public class PopperMapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;


    private GoogleMap mGoogleMap;
    private MapView mapView;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private LocationManager locationManager;
    public Criteria criteria;
    public String bestProvider;
    public double latitude;
    public double longitude;
    private LocationListener listener;
    public LocationRequest mLocationRequest;
    public Marker mCurrLocationMarker;

    private Double mLatitudeText;
    private Double mLongitudeText;

    View mView;


    public static PopperMapFragment newInstance(String userId) {
        PopperMapFragment fragment = new PopperMapFragment();

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mGoogleApiClient.connect();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }
        };
        MapsInitializer.initialize(getContext());

    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_popper_map, container, false);

        checkLocationPermission();

        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) mView.findViewById(map);
        if (mapView != null) {
            // Initialise the MapView
            mapView.onCreate(null);
            mapView.onResume();
            // Set the map ready callback to receive the GoogleMap object

            mapView.getMapAsync(this);

        }

    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(500000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if(mGoogleApiClient.isConnected() ) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        }
    }



    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getActivity());

        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location)
    {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

        Circle circle = mGoogleMap.addCircle(new CircleOptions()
                .center(new LatLng(-33.87365, 151.20689))
                .radius(10000)
                .strokeColor(Color.RED)
                .fillColor(Color.CYAN));
        circle.setCenter(latLng);

        addMarkersToMap();

        //move map camera
        //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(16));

        //stop location updates
//        if (mGoogleApiClient != null) {
//            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
//        }
    }

    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission was granted.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_LONG).show();
                }
            }

            // other 'case' lines to check for other permissions this app might request.
            //You can add here other case statements according to your requirement.
        }
    }

    // convert address to lng lat and add markers to map
    public void addMarkersToMap() {
        double[] latitude = {32.6085256, 32.6105717,32.6105385, 32.735118};
        double[] longitude = {-85.4746521, -85.4726629, -85.4724567,-85.576729};
        String[] title = {"LandScaping", "Cleaning", "Arson","Clean Garage"};
        String[] discription = {"Mow The Lawn", "Clean out my Garage", "Burn Down Neighbors House","With a broom and shovel"};
        List<LatLng> points = filterMap(latitude,longitude);
        if (latitude.length > 0 && longitude.length > 0) {
            for (int i = 0; i < latitude.length; i++) {
                try {

                    if(points.distanceTo(curr)){
                    System.out.println("latitude = " + latitude[i] + " longitude = " + longitude[i]);
                    mGoogleMap.addMarker(new MarkerOptions()
                            .position(points.get(i))
                            .title(title[i])
                            .snippet(discription[i])
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                            .alpha(0.7f));
                    }

                  
                } catch (Exception e) {
                    e.printStackTrace();
                } // end catch
            }
        }
    } //end addMarkersToMap

    public List<LatLng> filterMap(double[] x , double[] y){

        List<LatLng> points=new ArrayList<LatLng>();

        if(x.length == 0) {
            Toast.makeText(getActivity(), "No Jobs Near You.", Toast.LENGTH_LONG).show();
        }
        else {
            for(int i = 0; i < x.length; i++){
                Toast.makeText(getActivity(), "filtering", Toast.LENGTH_LONG).show();
                if (InRadius(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 5, x[i],y[i])) {
                    points.add(new LatLng(x[i],y[i]));
                }
            }
        }
        return points;

    }

    //32.735118, -85.576729
    public boolean InRadius(double centerX, double centerY,int  radius, double x, double y)
    {
        boolean check = true;
        double c1 = toRadians(centerX);
        double c2 = toRadians(centerY);
        double l1 = toRadians(x);
        double l2 = toRadians(y);

        Location location1 = new Location();


        double dist = acos(sin(c1) * sin(c2) * cos(c1) * cos(c2) * cos(l1-l2));

        if(dist < 0)
        {
            dist = dist + Math.PI;
        }
        long solution = Math.round(dist * 6378100);

        if(solution < (long) radius){
            check = true;
        }
        else{
            check = false;
        }
        Log.v("within", "index=" + dist);

        return check;
    }
}