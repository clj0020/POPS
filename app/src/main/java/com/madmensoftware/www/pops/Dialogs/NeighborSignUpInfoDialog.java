package com.madmensoftware.www.pops.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.madmensoftware.www.pops.Helpers.NiceSpinner;
import com.madmensoftware.www.pops.Models.User;
import com.madmensoftware.www.pops.R;

import org.parceler.Parcels;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by carson on 12/15/2016.
 */

public class NeighborSignUpInfoDialog extends DialogFragment {

    EditText mFirstNameEditText;
    EditText mLastNameEditText;
    EditText mAddressEditText;
    EditText mAddress2EditText;
    EditText mCityEditText;
    NiceSpinner mStateSpinner;
    EditText mZipCodeEditText;
    CheckBox m21OrOlderCheckBox;
    Button mSubmitButton;


    //String[] organizationList = {"Pick your high school", "Opelika", "Auburn", "Russel County", "Smiths Station", "Loachapoka"};

    private NeighborSignUpInfoDialogCallbacks mCallbacks;

    public interface NeighborSignUpInfoDialogCallbacks {
        //        void onRadiusEntered(int radius, User popper);
        void onNeighborInfoEntered(String firstName, String lastName, String address, String city, String state, int zipcode, User neighbor);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) return;
        if (activity instanceof BasicInfoDialog.BasicInfoDialogCallbacks) {
            mCallbacks = (NeighborSignUpInfoDialog.NeighborSignUpInfoDialogCallbacks) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement NeighborSignUpInfoDialogCallbacks");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BasicInfoDialog.BasicInfoDialogCallbacks) {
            mCallbacks = (NeighborSignUpInfoDialog.NeighborSignUpInfoDialogCallbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement NeighborSignUpInfoDialogCallbacks");
        }
    }

    public NeighborSignUpInfoDialog() {

    }

    public static NeighborSignUpInfoDialog newInstance(String title, User popper) {
        NeighborSignUpInfoDialog frag = new NeighborSignUpInfoDialog();
        Bundle args = new Bundle();
        args.putString("We need to know a little bit more about you.", title);
        args.putParcelable("User", Parcels.wrap(popper));
        frag.setArguments(args);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_neighbor_sign_up, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get field from view
        mSubmitButton = (Button) view.findViewById(R.id.dialog_neighbor_sign_up_submit_button);
        mFirstNameEditText = (EditText) view.findViewById(R.id.dialog_neighbor_sign_up_first_name);
        mLastNameEditText = (EditText) view.findViewById(R.id.dialog_neighbor_sign_up_last_name);
        mAddressEditText = (EditText) view.findViewById(R.id.dialog_neighbor_sign_up_address);
        mAddress2EditText = (EditText) view.findViewById(R.id.dialog_neighbor_sign_up_address_2);
        mCityEditText = (EditText) view.findViewById(R.id.dialog_neighbor_sign_up_city);
        mZipCodeEditText = (EditText) view.findViewById(R.id.dialog_neighbor_sign_up_zip_code);

        mStateSpinner = (NiceSpinner) view.findViewById(R.id.dialog_neighbor_sign_up_state);
        final List<String> stateList = new LinkedList<>(Arrays.asList("AK","AL","AR","AZ","CA","CO","CT","DC","DE","FL",
                "GA","GU","HI","IA","ID", "IL","IN","KS","KY","LA","MA","MD","ME","MH","MI","MN","MO","MS","MT","NC","ND",
                "NE","NH","NJ","NM","NV","NY", "OH","OK","OR","PA","PR","PW","RI","SC","SD","TN","TX","UT","VA","VI","VT",
                "WA","WI","WV","WY"));
        mStateSpinner.attachDataSource(stateList);
        mStateSpinner.setTextColor(Color.BLACK);
        m21OrOlderCheckBox = (CheckBox) view.findViewById(R.id.dialog_neighbor_sign_up_21_or_older_check_box);

        String title = getArguments().getString("title", "We need a little information from you before you can begin.");

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User neighbor = Parcels.unwrap(getArguments().getParcelable("User"));
                String state = stateList.get(mStateSpinner.getSelectedIndex());

                if(isEmpty(mFirstNameEditText) || isEmpty(mLastNameEditText) || isEmpty(mAddressEditText) || isEmpty(mCityEditText) || isEmpty(mZipCodeEditText)) {
                    Toast.makeText(getActivity(), "Please enter all fields!", Toast.LENGTH_LONG).show();
                }
                else if (mZipCodeEditText.getText().toString().length() != 5) {
                    Toast.makeText(getActivity(), "Zip code must be 5 digits", Toast.LENGTH_LONG).show();
                }
                else if (!m21OrOlderCheckBox.isChecked()) {
                    Toast.makeText(getActivity(), "You must be 21 years or older to be a neighbor.", Toast.LENGTH_LONG).show();
                }
                else {
                    mCallbacks.onNeighborInfoEntered(mFirstNameEditText.getText().toString(), mLastNameEditText.getText().toString(), mAddressEditText.getText().toString(),
                            mCityEditText.getText().toString(), state, Integer.parseInt(mZipCodeEditText.getText().toString()), neighbor);
                }
            }
        });

        getDialog().setTitle(title);

    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(null);
            setStyle(STYLE_NO_FRAME, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        }
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

}
