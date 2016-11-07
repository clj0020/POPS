package com.madmensoftware.www.pops.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Calendar;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
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
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
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
import com.orhanobut.logger.Logger;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddJobActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = AddJobActivity.class.getSimpleName();

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private GeoFire geofire;
    private GoogleApiClient mGoogleApiClient;
    private double longitude;
    private double latitude;

    private User user;
    private String uid;
    private String category;

    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    @BindView(R.id.add_job_title) EditText mJobTitleEditText;
    @BindView(R.id.add_job_description) EditText mJobDescriptionEditText;
    @BindView(R.id.add_job_duration) EditText mJobDurationEditText;
    @BindView(R.id.add_job_budget) EditText mJobBudgetEditText;
    @BindView(R.id.add_job_location_btn) Button mAddJobLocationButton;
    @BindView(R.id.add_job_category) Spinner mJobCategorySpinner;
    @BindView(R.id.add_job_submit_btn) Button mAddJobButton;
    @BindView(R.id.add_job_date_btn) Button btnDatePicker;
    @BindView(R.id.add_job_time_btn) Button btnTimePicker;
    @BindView(R.id.add_job_date) EditText mJobDate;
    @BindView(R.id.add_job_time) EditText mJobTime;

    private int mYear, mMonth, mDay, mHour, mMinute;
    private int mJobYear, mJobDay, mJobMonth, mJobHour, mJobMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_job);

        ButterKnife.bind(this);

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

        mJobBudgetEditText.addTextChangedListener(new CurrencyTextWatcher(mJobBudgetEditText, "#,###"));

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


//        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
//                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
//        autocompleteFragment.setHint("Address of Job");
//
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//                Log.i(TAG, "Place: " + place.getName());
//                latitude = place.getLatLng().latitude;
//                longitude = place.getLatLng().longitude;
//            }
//
//            @Override
//            public void onError(Status status) {
//                Log.i(TAG, "An error occurred: " + status);
//            }
//        });


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

        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);
        mAddJobButton.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
        auth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onPause() {
        Logger.d("AddJobActivityPaused");
        super.onPause();

    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
        if (mAuthListener != null) {
            auth.removeAuthStateListener(mAuthListener);
        }

        Logger.d("AddJobActivityStopped");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place: " + place.getName());
                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.add_job_location_btn:
                try {
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }


            case R.id.add_job_submit_btn:
                    if (isEmpty(mJobDescriptionEditText) || isEmpty(mJobTitleEditText) || isEmpty(mJobDurationEditText) || isEmpty(mJobBudgetEditText)) {
                        Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_LONG).show();
                    }
                    else {
                        String title = mJobTitleEditText.getText().toString();
                        String description = mJobDescriptionEditText.getText().toString();
                        double budget = convertDollarToDouble(mJobBudgetEditText.getText().toString());
                        int duration = Integer.parseInt(mJobDurationEditText.getText().toString());
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(mJobYear, mJobMonth, mJobDay,
                                mJobHour, mJobMinute, 0);
                        long startTime = calendar.getTimeInMillis();

                        if (startTime == 0) {
                            Toast.makeText(this, "Please pick a date and a time.", Toast.LENGTH_LONG).show();
                        }
                        else if (latitude == 0 || longitude == 0) {
                            Toast.makeText(this, "Please choose a location for the job.", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Job job = new Job();
                            job.setPosterUid(uid);
                            job.setPosterName(user.getName());
                            job.setTitle(title);
                            job.setStartTime(startTime);
                            job.setDescription(description);
                            job.setDuration(duration);
                            job.setBudget(budget);
                            job.setStatus("open");
                            job.setCategory(category);
                            job.setLatitude(latitude);
                            job.setLongitude(longitude);

                            String jobId = mDatabase.child("jobs").push().getKey();
                            job.setUid(jobId);
                            mDatabase.child("jobs").child(jobId).setValue(job);

                            geofire = new GeoFire(mDatabase.child("jobs_location"));
                            geofire.setLocation(jobId, new GeoLocation(latitude, longitude));

                            Toast.makeText(AddJobActivity.this, "Job Added Successfully", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(AddJobActivity.this, NeighborActivity.class));
                        }
                    }


                break;
            case R.id.add_job_date_btn:
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                mJobDate.setText((monthOfYear + 1) + "-" + dayOfMonth + "-" + year);
                                mJobYear = year;
                                mJobMonth = monthOfYear + 1;
                                mJobDay = dayOfMonth;

                                btnDatePicker.setText((monthOfYear + 1) + "-" + dayOfMonth + "-" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                break;
            case R.id.add_job_time_btn:
                // Get Current Time
                final Calendar cal = Calendar.getInstance();
                mHour = cal.get(Calendar.HOUR_OF_DAY);
                mMinute = cal.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                mJobHour = hourOfDay;
                                mJobMinute = minute;

                                btnTimePicker.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    public double convertDollarToDouble(String dollar) {
        double dbl = 0;
        NumberFormat nf = new DecimalFormat("$#,###.00");

        try {
            dbl = nf.parse(dollar).doubleValue();
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        return dbl;
    }

    public String convertDoubleToDollar(double dbl) {
        String dollar = "";
        NumberFormat nf = new DecimalFormat("$#,###.00");
        return nf.format(dbl);
    }



    public class CurrencyTextWatcher implements TextWatcher {

        private final DecimalFormat df;
        private final DecimalFormat dfnd;
        private final EditText et;
        private boolean hasFractionalPart;
        private int trailingZeroCount;

        public CurrencyTextWatcher(EditText editText, String pattern) {
            df = new DecimalFormat(pattern);
            df.setDecimalSeparatorAlwaysShown(true);
            dfnd = new DecimalFormat("#,###.00");
            this.et = editText;
            hasFractionalPart = false;
        }

        @Override
        public void afterTextChanged(Editable s) {
            et.removeTextChangedListener(this);

            if (s != null && !s.toString().isEmpty()) {
                try {
                    int inilen, endlen;
                    inilen = et.getText().length();
                    String v = s.toString().replace(String.valueOf(df.getDecimalFormatSymbols().getGroupingSeparator()), "").replace("$","");
                    Number n = df.parse(v);
                    int cp = et.getSelectionStart();
                    if (hasFractionalPart) {
                        StringBuilder trailingZeros = new StringBuilder();
                        while (trailingZeroCount-- > 0)
                            trailingZeros.append('0');
                        et.setText(df.format(n) + trailingZeros.toString());
                    } else {
                        et.setText(dfnd.format(n));
                    }
                    et.setText("$".concat(et.getText().toString()));
                    endlen = et.getText().length();
                    int sel = (cp + (endlen - inilen));
                    if (sel > 0 && sel < et.getText().length()) {
                        et.setSelection(sel);
                    } else if (trailingZeroCount > -1) {
                        et.setSelection(et.getText().length() - 3);
                    } else {
                        et.setSelection(et.getText().length());
                    }
                } catch (NumberFormatException | ParseException e) {
                    e.printStackTrace();
                }
            }

            et.addTextChangedListener(this);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int index = s.toString().indexOf(String.valueOf(df.getDecimalFormatSymbols().getDecimalSeparator()));
            trailingZeroCount = 0;
            if (index > -1) {
                for (index++; index < s.length(); index++) {
                    if (s.charAt(index) == '0')
                        trailingZeroCount++;
                    else {
                        trailingZeroCount = 0;
                    }
                }
                hasFractionalPart = true;
            } else {
                hasFractionalPart = false;
            }
        }
    }
}
