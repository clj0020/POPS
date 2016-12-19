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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

import fr.ganfra.materialspinner.MaterialSpinner;

/**
 * Created by carson on 12/14/2016.
 */

public class PopperSignUpInfoDialog extends DialogFragment {

    EditText mFirstNameEditText;
    EditText mLastNameEditText;
    NiceSpinner mDateOfBirthMonthEditText;
    NiceSpinner mDateOfBirthDayEditText;
    NiceSpinner mDateOfBirthYearEditText;
    EditText mEmergencyContactFirstNameEditText;
    EditText mEmergencyContactLastNameEditText;
    EditText mEmergencyContactEmailAddressEditText;
    NiceSpinner mOrganizationSpinner;
    Button mSubmitButton;

    private String mOrganizationName;


    //String[] organizationList = {"Pick your high school", "Opelika", "Auburn", "Russel County", "Smiths Station", "Loachapoka"};

    private PopperSignUpInfoDialog.PopperSignUpInfoDialogCallbacks mCallbacks;

    public interface PopperSignUpInfoDialogCallbacks {
        //        void onRadiusEntered(int radius, User popper);
        void onPopperInfoEntered(String firstName, String lastName, long birthDay, String organization, String parentFirstName, String parentLastName, String parentEmail, User popper);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) return;
        if (activity instanceof BasicInfoDialog.BasicInfoDialogCallbacks) {
            mCallbacks = (PopperSignUpInfoDialog.PopperSignUpInfoDialogCallbacks) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement PopperSignUpInfoDialogCallbacks");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BasicInfoDialog.BasicInfoDialogCallbacks) {
            mCallbacks = (PopperSignUpInfoDialog.PopperSignUpInfoDialogCallbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement PopperSignUpInfoDialogCallbacks");
        }
    }

    public PopperSignUpInfoDialog() {

    }

    public static PopperSignUpInfoDialog newInstance(String title, User popper) {
        PopperSignUpInfoDialog frag = new PopperSignUpInfoDialog();
        Bundle args = new Bundle();
        args.putString("We need to know a little bit more about you.", title);
        args.putParcelable("User", Parcels.wrap(popper));
        frag.setArguments(args);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_popper_sign_up, container);

        //getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get field from view
        mSubmitButton = (Button) view.findViewById(R.id.dialog_popper_sign_up_submit_button);
        //mRadiusSeekbar = (DiscreteSeekBar) view.findViewById(R.id.pick_radius_seekbar);
        mFirstNameEditText = (EditText) view.findViewById(R.id.dialog_popper_sign_up_first_name);
        mLastNameEditText = (EditText) view.findViewById(R.id.dialog_popper_sign_up_last_name);

        mDateOfBirthDayEditText = (NiceSpinner) view.findViewById(R.id.dialog_popper_sign_up_date_of_birth_day);
        final List<String> dayList = new LinkedList<>(Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13",
                "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"));
        mDateOfBirthDayEditText.attachDataSource(dayList);
        mDateOfBirthDayEditText.setTextColor(Color.BLACK);
        mDateOfBirthDayEditText.setTextSize(11);

        mDateOfBirthMonthEditText = (NiceSpinner) view.findViewById(R.id.dialog_popper_sign_up_date_of_birth_month);
        final List<String> monthList = new LinkedList<>(Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept", "Oct", "Nov", "Dec"));
        mDateOfBirthMonthEditText.attachDataSource(monthList);
        mDateOfBirthMonthEditText.setTextColor(Color.BLACK);
        mDateOfBirthMonthEditText.setTextSize(11);

        mDateOfBirthYearEditText = (NiceSpinner) view.findViewById(R.id.dialog_popper_sign_up_date_of_birth_year);
        final List<String> yearList = new LinkedList<>(Arrays.asList("2003", "2002", "2001", "2000", "1999", "1998"));
        mDateOfBirthYearEditText.attachDataSource(yearList);
        mDateOfBirthYearEditText.setTextColor(Color.BLACK);
        mDateOfBirthYearEditText.setTextSize(11);

        mEmergencyContactFirstNameEditText = (EditText) view.findViewById(R.id.dialog_popper_sign_up_emergency_contact_first_name);
        mEmergencyContactLastNameEditText = (EditText) view.findViewById(R.id.dialog_popper_sign_up_emergency_contact_last_name);
        mEmergencyContactEmailAddressEditText = (EditText) view.findViewById(R.id.dialog_popper_sign_up_emergency_contact_email);


        mOrganizationSpinner = (NiceSpinner) view.findViewById(R.id.dialog_popper_sign_up_organization_spinner);
        final List<String> organizationList = new LinkedList<>(Arrays.asList("Opelika", "Auburn", "Russel County", "Smiths Station", "Loachapoka"));
        mOrganizationSpinner.attachDataSource(organizationList);
        mOrganizationSpinner.setTextColor(Color.BLACK);

        String title = getArguments().getString("title", "We need a little information from you before you can begin.");

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User popper = Parcels.unwrap(getArguments().getParcelable("User"));

                mOrganizationName = organizationList.get(mOrganizationSpinner.getSelectedIndex());

                int year = getYear(yearList.get(mDateOfBirthYearEditText.getSelectedIndex()));
                int month = getMonth(monthList.get(mDateOfBirthMonthEditText.getSelectedIndex()));
                int day = getDay(dayList.get(mDateOfBirthDayEditText.getSelectedIndex()));
                Date dateOfBirth = getDate(year, month, day);

                if(isEmpty(mFirstNameEditText) || isEmpty(mLastNameEditText) || isEmpty(mEmergencyContactFirstNameEditText)
                        || isEmpty(mEmergencyContactLastNameEditText) || isEmpty(mEmergencyContactEmailAddressEditText)) {
                    Toast.makeText(getActivity(), "Please enter all fields!", Toast.LENGTH_LONG).show();
                }
                else if (!isValidEmail(mEmergencyContactEmailAddressEditText.getText())) {
                    Toast.makeText(getActivity(), "Emergency contact email is invalid!", Toast.LENGTH_LONG).show();
                }
                else {
                    mCallbacks.onPopperInfoEntered(mFirstNameEditText.getText().toString(), mLastNameEditText.getText().toString(),
                            dateOfBirth.getTime(), mOrganizationName, mEmergencyContactFirstNameEditText.getText().toString(),
                            mEmergencyContactLastNameEditText.getText().toString(), mEmergencyContactEmailAddressEditText.getText().toString(), popper);
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

    public int getYear(String year) {
        return Integer.parseInt(year);
    }

    public int getDay(String day) {
        return Integer.parseInt(day);
    }

    public int getMonth(String monthString) {
        int month = 0;

        switch(monthString) {
            case "Jan":
                month = 0;
                break;
            case "Feb":
                month = 1;
                break;
            case "Mar":
                month = 2;
                break;
            case "Apr":
                month = 3;
                break;
            case "May":
                month = 4;
                break;
            case "June":
                month = 5;
                break;
            case "July":
                month = 6;
                break;
            case "Aug":
                month = 7;
                break;
            case "Sep":
                month = 8;
                break;
            case "Oct":
                month = 9;
                break;
            case "Nov":
                month = 10;
                break;
            case "Dec":
                month = 11;
                break;
        }

        return month;
    }

    public static Date getDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    static class CustomArrayAdapter<T> extends ArrayAdapter<T>
    {
        public CustomArrayAdapter(Context ctx, T [] objects)
        {
            super(ctx, android.R.layout.simple_spinner_item, objects);
        }

        //other constructors

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent)
        {
            View view = super.getView(position, convertView, parent);

            //we know that simple_spinner_item has android.R.id.text1 TextView:

        /* if(isDroidX) {*/
            TextView text = (TextView)view.findViewById(android.R.id.text1);
            text.setTextColor(Color.BLACK);//choose your color :)
        /*}*/

            return view;

        }
    }

}