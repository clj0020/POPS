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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.madmensoftware.www.pops.R;
import com.orhanobut.logger.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/*** A simple {@link Fragment} subclass.
 */
public class AddDetailsParentFragment extends Fragment implements View.OnClickListener {

    private static final String USER_ID = "user_id";

    @BindView(R.id.progress_bar) ProgressBar progressBar;
    @BindView(R.id.parent_first_name) EditText mFirstNameEditText;
    @BindView(R.id.parent_last_name) EditText mLastNameEditText;
    @BindView(R.id.parent_last_four_of_social) EditText mLastFourSocialEditText;
    @BindView(R.id.parent_phone) EditText mPhoneEditText;
    @BindView(R.id.parent_dob_date_picker_btn) Button mDateOfBirthButton;
    @BindView(R.id.nextBtn) Button mNextButton;

    public String uid;
    private int mYear, mMonth, mDay;
    private int dobYear, dobMonth, dobDay;

    private SignUpParentCallbacks mCallbacks;

    /**
     * Required interface for hosting activities
     */
    public interface SignUpParentCallbacks {
        void onParentSubmit(String firstName, String lastName, int lastFourSSN, int dobYear, int dobMonth, int dobDay, int phone);
    }

    public AddDetailsParentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) return;
        if (activity instanceof SignUpParentCallbacks) {
            mCallbacks = (SignUpParentCallbacks) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SignUpParentCallbacks) {
            mCallbacks = (SignUpParentCallbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    public static AddDetailsParentFragment newInstance() {
        AddDetailsParentFragment fragment = new AddDetailsParentFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_details_parent, container, false);
        ButterKnife.bind(this, view);

        Logger.d("onCreateView");

        mDateOfBirthButton.setOnClickListener(this);
        mNextButton.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.nextBtn:

                if(isEmpty(mFirstNameEditText) || isEmpty(mPhoneEditText)) {
                    Toast.makeText(getActivity(), "Please fill out all fields.", Toast.LENGTH_LONG).show();
                }
                else {
                    String firstName = mFirstNameEditText.getText().toString();
                    String lastName = mLastNameEditText.getText().toString();
                    int lastFourSSN = Integer.parseInt(mLastFourSocialEditText.getText().toString());
                    int phone = Integer.parseInt(mPhoneEditText.getText().toString());

                    mCallbacks.onParentSubmit(firstName, lastName, lastFourSSN, dobYear, dobMonth, dobDay, phone);
                }
                break;
            case R.id.parent_dob_date_picker_btn:
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                dobYear = year;
                                dobMonth = monthOfYear + 1;
                                dobDay = dayOfMonth;
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                break;
            default:
                break;
        }

    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
}
