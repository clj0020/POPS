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

public class SignUpSecondPageFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_TYPE = "type";

    private EditText inputEmail, inputPassword;
    private ProgressBar progressBar;

    private Button backBtn;
    private Button nextBtn;

    private SignUpSecondPageCallbacks mCallbacks;

    /**
     * Required interface for hosting activities
     */
    public interface SignUpSecondPageCallbacks {
        void onBackButton();
        void onNextButton(String email, String password, String type);
    }


    public String type;

    public SignUpSecondPageFragment() {
        // Required empty public constructor
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) return;
        if (activity instanceof SignUpSecondPageCallbacks) {
            mCallbacks = (SignUpSecondPageCallbacks) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SignUpSecondPageCallbacks) {
            mCallbacks = (SignUpSecondPageCallbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }


    public static SignUpSecondPageFragment newInstance(String type) {
        SignUpSecondPageFragment fragment = new SignUpSecondPageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getString(ARG_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sign_up_second_page, container, false);

//        btnSignIn = (Button) view.findViewById(R.id.sign_in_button);

        backBtn = (Button) view.findViewById(R.id.backBtn);
        nextBtn = (Button) view.findViewById(R.id.nextBtn);

        backBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);


        inputEmail = (EditText) view.findViewById(R.id.email);
        inputPassword = (EditText) view.findViewById(R.id.password);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);


//        btnSignUp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                String email = inputEmail.getText().toString().trim();
//                String password = inputPassword.getText().toString().trim();
//
//
//            }
//        });


        return view;
    }


    @Override
    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.backBtn:
                mCallbacks.onBackButton();
                break;
            case R.id.nextBtn:
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                mCallbacks.onNextButton(email, password, type);

                break;
            default:
                break;
        }

    }

}
