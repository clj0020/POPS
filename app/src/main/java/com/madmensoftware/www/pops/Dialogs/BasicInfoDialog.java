package com.madmensoftware.www.pops.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.madmensoftware.www.pops.Models.User;
import com.madmensoftware.www.pops.R;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.parceler.Parcels;

/**
 * Created by carsonjones on 10/20/16.
 */

public class BasicInfoDialog extends DialogFragment {

//    DiscreteSeekBar mRadiusSeekbar;
    EditText mNameEditText;
    EditText mAgeEditText;
    Button mOkButton;

    private BasicInfoDialogCallbacks mCallbacks;

    public interface BasicInfoDialogCallbacks {
//        void onRadiusEntered(int radius, User popper);
        void onInfoEntered(String name, int age, User popper);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) return;
        if (activity instanceof BasicInfoDialogCallbacks ) {
            mCallbacks = (BasicInfoDialogCallbacks ) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BasicInfoDialogCallbacks ) {
            mCallbacks = (BasicInfoDialogCallbacks ) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    public BasicInfoDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static BasicInfoDialog newInstance(String title, User popper) {
        BasicInfoDialog frag = new BasicInfoDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putParcelable("User", Parcels.wrap(popper));
        frag.setArguments(args);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_basic_details, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get field from view
        mOkButton = (Button) view.findViewById(R.id.pick_radius_ok_button);
        //mRadiusSeekbar = (DiscreteSeekBar) view.findViewById(R.id.pick_radius_seekbar);
        mNameEditText = (EditText) view.findViewById(R.id.popper_basic_details_name);
        mAgeEditText = (EditText) view.findViewById(R.id.popper_basic_details_age);


//        mRadiusSeekbar.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
//            @Override
//            public int transform(int value) {
//                return value;
//            }
//
//            @Override
//            public String transformToString(int value) {
//                return value + " miles";
//            }
//
//            @Override
//            public boolean useStringTransform() {
//                return true;
//            }
//        });


        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "We need a little information from you before you can begin.");

        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User popper = Parcels.unwrap(getArguments().getParcelable("User"));
              //  mCallbacks.onRadiusEntered(mRadiusSeekbar.getProgress(), popper);

                int age = Integer.parseInt(mAgeEditText.getText().toString());

                if(isEmpty(mNameEditText) || isEmpty(mAgeEditText)) {
                    Toast.makeText(getActivity(), "Please enter all fields!", Toast.LENGTH_LONG).show();
                }
                else if (age > 25) {
                    Toast.makeText(getActivity(), "You exceed the age limit to be a popper.", Toast.LENGTH_LONG).show();
                }
                else if (age <= 0) {
                    Toast.makeText(getActivity(), "You do not meet the minimum age requirement.", Toast.LENGTH_LONG).show();
                }
                else {
                    mCallbacks.onInfoEntered(mNameEditText.getText().toString(), age, popper);
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

}
