package com.madmensoftware.www.pops.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.madmensoftware.www.pops.Helpers.GPSTracker;
import com.madmensoftware.www.pops.Models.Job;
import com.madmensoftware.www.pops.Models.User;
import com.madmensoftware.www.pops.R;

import org.parceler.Parcels;

public class AddJobActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = AddJobActivity.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CODE_LOCATION = 1;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private GeoFire geofire;

    private User user;
    private String uid;
    private String category;


    private EditText mJobTitleEditText, mJobDescriptionEditText, mJobDurationEditText, mJobBudgetEditText;
    private Spinner mJobCategorySpinner;
    private Button mAddJobButton;
    private ImageButton mGetCurrentLocation;

    private GoogleApiClient mGoogleApiClient;
    private double longitude;
    private double latitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_job);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .build();
        }

        Bundle b = this.getIntent().getExtras();
        if (b != null) {
            user = Parcels.unwrap(b.getParcelable("User"));
        }


        mDatabase = FirebaseDatabase.getInstance().getReference();

        mJobTitleEditText = (EditText) findViewById(R.id.add_job_title);
        mJobDescriptionEditText = (EditText) findViewById(R.id.add_job_description);
        mJobDurationEditText = (EditText) findViewById(R.id.add_job_duration);
        mJobBudgetEditText = (EditText) findViewById(R.id.add_job_budget);
        mJobCategorySpinner = (Spinner) findViewById(R.id.add_job_category);
        mAddJobButton = (Button) findViewById(R.id.add_job_submit_btn);

        //Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.job_category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mJobCategorySpinner.setAdapter(adapter);
        mJobCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                category = mJobCategorySpinner.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });


        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setHint("Address of Job");

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Place: " + place.getName());
                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });


        auth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Log.i("Auth", "User is null");
                    startActivity(new Intent(AddJobActivity.this, LoginActivity.class));
                    finish();
                }
                else {
                    uid = user.getUid();
                }
            }
        };

        mAddJobButton.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
        auth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
        if (mAuthListener != null) {
            auth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.add_job_submit_btn:
                if (isEmpty(mJobBudgetEditText) || isEmpty(mJobDescriptionEditText) || isEmpty(mJobDurationEditText) || isEmpty(mJobBudgetEditText)) {
                    Toast.makeText(AddJobActivity.this, "Please fill out all fields.", Toast.LENGTH_LONG).show();
                }
                else {
                    String title = mJobTitleEditText.getText().toString();
                    String description = mJobDescriptionEditText.getText().toString();
                    int duration = Integer.parseInt(mJobDurationEditText.getText().toString());
                    double budget = Double.parseDouble(mJobBudgetEditText.getText().toString());

                    Job job = new Job();
                    job.setPosterUid(uid);
                    job.setPosterName(user.getName());
                    job.setTitle(title);
                    job.setDescription(description);
                    job.setDuration(duration);
                    job.setBudget(budget);
                    job.setStatus("Pending");
                    job.setCategory(category);

                    String jobId = mDatabase.child("jobs").push().getKey();
                    mDatabase.child("jobs").child(jobId).setValue(job);

                    geofire = new GeoFire(mDatabase.child("jobs_location"));
                    geofire.setLocation(jobId, new GeoLocation(latitude, longitude));

                    Toast.makeText(AddJobActivity.this, "Job Added Successfully", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(AddJobActivity.this, NeighborActivity.class));
                }

                break;
            default:
                break;
        }
    }

    private boolean isEmpty(EditText myeditText) {
        return myeditText.getText().toString().trim().length() == 0;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}


}
