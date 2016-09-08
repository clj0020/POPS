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

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpNeighborFragment extends Fragment implements View.OnClickListener {

    private static final String USER_ID = "user_id";

    private ProgressBar progressBar;

    private EditText mNameEditText;
    private EditText mAddressEditText;
    private EditText mZipCodeEditText;
    private EditText mOrganizationCode;
    private Button mBackButton;
    private Button mNextButton;
    public String uid;

    private SignUpNeighborCallbacks mCallbacks;

    /**
     * Required interface for hosting activities
     */
    public interface SignUpNeighborCallbacks {
        void onNeighborSubmit(String name, String address, int zip_code, int organization_code);
    }

    public SignUpNeighborFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) return;
        if (activity instanceof SignUpNeighborCallbacks) {
            mCallbacks = (SignUpNeighborCallbacks) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SignUpNeighborCallbacks) {
            mCallbacks = (SignUpNeighborCallbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    public static SignUpNeighborFragment newInstance(String userId) {
        SignUpNeighborFragment fragment = new SignUpNeighborFragment();
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
        View view = inflater.inflate(R.layout.fragment_sign_up_neighbor, container, false);


        mNameEditText = (EditText) view.findViewById(R.id.neighbor_name);
        mAddressEditText = (EditText) view.findViewById(R.id.neighbor_address);
        mZipCodeEditText = (EditText) view.findViewById(R.id.neighbor_zip_code);
        mOrganizationCode = (EditText) view.findViewById(R.id.neighbor_organization_code);
        mBackButton = (Button) view.findViewById(R.id.backBtn);
        mNextButton = (Button) view.findViewById(R.id.nextBtn);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        mBackButton.setOnClickListener(this);
        mNextButton.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.backBtn:
                break;
            case R.id.nextBtn:
                String name = mNameEditText.getText().toString();
                String address = mAddressEditText.getText().toString();
                int zip_code = Integer.parseInt(mZipCodeEditText.getText().toString());
                int organizationCode = Integer.parseInt(mOrganizationCode.getText().toString());

                mCallbacks.onNeighborSubmit(name, address, zip_code, organizationCode);
                break;
            default:
                break;
        }

    }

}