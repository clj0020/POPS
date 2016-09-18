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

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpSecondPageFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_TYPE = "type";

    @BindView(R.id.email) EditText inputEmail;
    @BindView(R.id.password) EditText inputPassword;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.backBtn) Button backBtn;
    @BindView(R.id.nextBtn) Button submitBtn;

    private SignUpSecondPageCallbacks mCallbacks;
    public String type;

    public interface SignUpSecondPageCallbacks {
        void onBackButton();
        void onSubmitButton(String email, String password, String type);
    }

    public SignUpSecondPageFragment() {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up_second_page, container, false);
        ButterKnife.bind(this, view);

        backBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);

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
                mCallbacks.onSubmitButton(email, password, type);

                break;
            default:
                break;
        }

    }
}
