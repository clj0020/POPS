package com.madmensoftware.www.pops.Fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.madmensoftware.www.pops.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpParentFragment extends Fragment implements View.OnClickListener {

    private static final String USER_ID = "user_id";

    private ProgressBar progressBar;

    private EditText mNameEditText;
    private EditText mPhoneEditText;
    private Button mBackButton;
    private Button mNextButton;
    public String uid;

    private SignUpParentCallbacks mCallbacks;

    /**
     * Required interface for hosting activities
     */
    public interface SignUpParentCallbacks {
        void onParentSubmit(String name, int phone);
    }

    public SignUpParentFragment() {
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

    public static SignUpParentFragment newInstance(String userId) {
        SignUpParentFragment fragment = new SignUpParentFragment();
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
        View view = inflater.inflate(R.layout.fragment_sign_up_parent, container, false);


        mNameEditText = (EditText) view.findViewById(R.id.parent_name);
        mPhoneEditText = (EditText) view.findViewById(R.id.parent_phone);

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
                int phone = Integer.parseInt(mPhoneEditText.getText().toString());

                mCallbacks.onParentSubmit(name, phone);
                break;
            default:
                break;
        }

    }
}
