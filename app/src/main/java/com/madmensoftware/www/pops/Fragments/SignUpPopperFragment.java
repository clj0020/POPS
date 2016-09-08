package com.madmensoftware.www.pops.Fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.madmensoftware.www.pops.R;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SignUpPopperFragment extends Fragment implements View.OnClickListener {

    private static final String USER_ID = "user_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;

    private ProgressBar progressBar;

    private EditText mNameEditText;
    private EditText mAgeEditText;
    private EditText mZipCodeEditText;
    private EditText mOrganizationCode;
    private EditText mGoal;
    private EditText mParentCode;
    private Spinner mTransportationSpinner;
    private DiscreteSeekBar mRadiusSeekbar;
    private Button mBackButton;
    private Button mNextButton;
    private Button mDateButton;

    public String transportationType;
    private int mYear, mMonth, mDay;

    public Date goalDate;

    public String uid;

    private SignUpPopperCallbacks mCallbacks;

    /**
     * Required interface for hosting activities
     */
    public interface SignUpPopperCallbacks {
        void onPopperBackButton();
        void onPopperNextButton(String userId, String name, int age, int zip_code, String transportation, int radius, double goal, Date goalDate);
    }

    public SignUpPopperFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) return;
        if (activity instanceof SignUpPopperCallbacks) {
            mCallbacks = (SignUpPopperCallbacks) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SignUpPopperCallbacks) {
            mCallbacks = (SignUpPopperCallbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    public static SignUpPopperFragment newInstance(String userId) {
        SignUpPopperFragment fragment = new SignUpPopperFragment();
        Bundle args = new Bundle();
        args.putString(USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            uid = getArguments().getString(USER_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up_popper, container, false);


        mNameEditText = (EditText) view.findViewById(R.id.name);
        mAgeEditText = (EditText) view.findViewById(R.id.age);
        mZipCodeEditText = (EditText) view.findViewById(R.id.zip_code);
        mOrganizationCode = (EditText) view.findViewById(R.id.organization_code);
        mParentCode = (EditText) view.findViewById(R.id.parent_code);
        mTransportationSpinner = (Spinner) view.findViewById(R.id.transportation_spinner);
        mRadiusSeekbar = (DiscreteSeekBar) view.findViewById(R.id.radius_seekbar);
        mGoal = (EditText) view.findViewById(R.id.goal);
        mDateButton = (Button) view.findViewById(R.id.goal_due_date);
        mBackButton = (Button) view.findViewById(R.id.backBtn);
        mNextButton = (Button) view.findViewById(R.id.nextBtn);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        //Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.transportation_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTransportationSpinner.setAdapter(adapter);

        mTransportationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                transportationType = mTransportationSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        // Seekbar
        mRadiusSeekbar.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
            @Override
            public int transform(int value) {
                return value;
            }

            @Override
            public String transformToString(int value) {
                return value + " miles";
            }

            @Override
            public boolean useStringTransform() {
                return true;
            }
        });

        mDateButton.setOnClickListener(this);
        mBackButton.setOnClickListener(this);
        mNextButton.setOnClickListener(this);


        return view;
    }


    @Override
    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.backBtn:
                mCallbacks.onPopperBackButton();
                break;
            case R.id.nextBtn:
                String name = mNameEditText.getText().toString();
                int age = Integer.parseInt(mAgeEditText.getText().toString());
                int zip_code = Integer.parseInt(mZipCodeEditText.getText().toString());
                String transportation = transportationType;
                int radius = mRadiusSeekbar.getProgress();
                double goal = Double.parseDouble(mGoal.getText().toString());

                mCallbacks.onPopperNextButton(uid, name, age, zip_code, transportation, radius, goal, goalDate);
                break;
            case R.id.goal_due_date:

                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                final  SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                goalDate = new Date(year - 1900, monthOfYear, dayOfMonth);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();

                break;
            default:
                break;
        }

    }

}
