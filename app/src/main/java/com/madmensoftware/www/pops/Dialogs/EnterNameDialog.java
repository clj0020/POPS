package com.madmensoftware.www.pops.Dialogs;

import android.app.Activity;
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
 * Created by carsonjones on 10/24/16.
 */

public class EnterNameDialog extends DialogFragment {

    EditText mNameEditText;
    Button mOkButton;

    private EnterNameDialog.EnterNameDialogCallbacks mCallbacks;

    public interface EnterNameDialogCallbacks {
        void onNameEntered(String name, User popper);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) return;
        if (activity instanceof EnterNameDialog.EnterNameDialogCallbacks) {
            mCallbacks = (EnterNameDialog.EnterNameDialogCallbacks) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof EnterNameDialog.EnterNameDialogCallbacks) {
            mCallbacks = (EnterNameDialog.EnterNameDialogCallbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    public EnterNameDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static EnterNameDialog newInstance(String title, User popper) {
        EnterNameDialog frag = new EnterNameDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putParcelable("User", Parcels.wrap(popper));
        frag.setArguments(args);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_pick_radius, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get field from view
        mOkButton = (Button) view.findViewById(R.id.enter_name_ok_button);
        mNameEditText = (EditText) view.findViewById(R.id.enter_name_edit_text);

        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "What is your name?");

        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmpty(mNameEditText)) {
                    Toast.makeText(getActivity(), "Please fill out your name!", Toast.LENGTH_LONG).show();
                }
                else {
                    User popper = Parcels.unwrap(getArguments().getParcelable("User"));
                    mCallbacks.onNameEntered(mNameEditText.getText().toString(), popper);
                }
            }
        });

        getDialog().setTitle(title);
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

}