package com.madmensoftware.www.pops.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.madmensoftware.www.pops.Models.User;
import com.madmensoftware.www.pops.R;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.parceler.Parcels;

import butterknife.BindView;

/**
 * Created by carsonjones on 10/20/16.
 */

public class PickRadiusDialog extends DialogFragment {

    DiscreteSeekBar mRadiusSeekbar;
    Button mOkButton;

    private PickRadiusDialogCallbacks mCallbacks;

    public interface PickRadiusDialogCallbacks {
        void onRadiusEntered(int radius, User popper);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) return;
        if (activity instanceof PickRadiusDialogCallbacks) {
            mCallbacks = (PickRadiusDialogCallbacks) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PickRadiusDialogCallbacks) {
            mCallbacks = (PickRadiusDialogCallbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    public PickRadiusDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static PickRadiusDialog newInstance(String title, User popper) {
        PickRadiusDialog frag = new PickRadiusDialog();
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
        mOkButton = (Button) view.findViewById(R.id.pick_radius_ok_button);
        mRadiusSeekbar = (DiscreteSeekBar) view.findViewById(R.id.pick_radius_seekbar);


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


        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Pick the radius that you want to find jobs in.");

        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User popper = Parcels.unwrap(getArguments().getParcelable("User"));
                mCallbacks.onRadiusEntered(mRadiusSeekbar.getProgress(), popper);
            }
        });

        getDialog().setTitle(title);

    }

}
