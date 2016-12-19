package com.madmensoftware.www.pops.Fragments;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.madmensoftware.www.pops.Helpers.NiceSpinner;
import com.madmensoftware.www.pops.R;
import com.orhanobut.logger.Logger;

import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class PopperPaymentInfoFragment extends Fragment {

    private static final String USER_ID = "user_id";

    @BindView(R.id.popper_payment_info_address) EditText mAddressEditText;
    @BindView(R.id.popper_payment_info_city) EditText mCityEditText;
    @BindView(R.id.popper_payment_info_zip_code) EditText mZipCodeEditText;
    @BindView(R.id.popper_payment_info_last_four_of_social) EditText mLastFourOfSocialEditText;
    @BindView(R.id.popper_payment_info_state) NiceSpinner mStateSpinner;
    @BindView(R.id.popper_payment_info_phone) EditText mPhoneEditText;
    @BindView(R.id.nextBtn) Button mNextButton;

    public String uid;
    private int mYear, mMonth, mDay;
    private int dobYear, dobMonth, dobDay;

    private PopperPaymentInfoCallbacks mCallbacks;

    /**
     * Required interface for hosting activities
     */
    public interface PopperPaymentInfoCallbacks {
        void onPopperPaymentInfoSubmit(String address, String city, String state, int zipcode, String lastFourSSN, int phone);
    }

    public PopperPaymentInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) return;
        if (activity instanceof PopperPaymentInfoFragment.PopperPaymentInfoCallbacks) {
            mCallbacks = (PopperPaymentInfoFragment.PopperPaymentInfoCallbacks) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement PopperPaymentInfoCallbacks");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PopperPaymentInfoFragment.PopperPaymentInfoCallbacks) {
            mCallbacks = (PopperPaymentInfoFragment.PopperPaymentInfoCallbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement PopperPaymentInfoCallbacks");
        }
    }

    public static PopperPaymentInfoFragment newInstance() {
        PopperPaymentInfoFragment fragment = new PopperPaymentInfoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_popper_payment_info, container, false);
        ButterKnife.bind(this, view);

        Logger.d("onCreateView");


        final List<String> stateList = new LinkedList<>(Arrays.asList("AK","AL","AR","AZ","CA","CO","CT","DC","DE","FL",
                "GA","GU","HI","IA","ID", "IL","IN","KS","KY","LA","MA","MD","ME","MH","MI","MN","MO","MS","MT","NC","ND",
                "NE","NH","NJ","NM","NV","NY", "OH","OK","OR","PA","PR","PW","RI","SC","SD","TN","TX","UT","VA","VI","VT",
                "WA","WI","WV","WY"));
        mStateSpinner.attachDataSource(stateList);
        mStateSpinner.setTextColor(Color.BLACK);

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String state = stateList.get(mStateSpinner.getSelectedIndex());

                if(isEmpty(mAddressEditText) || isEmpty(mCityEditText) || isEmpty(mZipCodeEditText)
                        || isEmpty(mLastFourOfSocialEditText) || isEmpty(mPhoneEditText)) {
                    Toast.makeText(getActivity(), "Please fill out all fields.", Toast.LENGTH_LONG).show();
                }
                else if (mLastFourOfSocialEditText.getText().toString().length() != 9) {
                    Toast.makeText(getActivity(), "Last four of social invalid.", Toast.LENGTH_LONG).show();
                }
                else if (mZipCodeEditText.getText().toString().length() != 5) {
                    Toast.makeText(getActivity(), "Zip code invalid.", Toast.LENGTH_LONG).show();
                }
                else {
                    String address = mAddressEditText.getText().toString();
                    String city = mCityEditText.getText().toString();
                    int zipcode = Integer.parseInt(mZipCodeEditText.getText().toString());
                    String lastFourSSN = mLastFourOfSocialEditText.getText().toString();
                    int phone = Integer.parseInt(mPhoneEditText.getText().toString());

                    mCallbacks.onPopperPaymentInfoSubmit(address, city, state, zipcode, lastFourSSN, phone);
                }
            }
        });

        return view;
    }


    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
}
