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
import android.widget.Toast;

import com.madmensoftware.www.pops.R;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddDetailsNeighborFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.neighbor_name) EditText mNameEditText;
    @BindView(R.id.neighbor_address) EditText mAddressEditText;
    @BindView(R.id.neighbor_zip_code) EditText mZipCodeEditText;
    @BindView(R.id.neighbor_organization_code) EditText mOrganizationCode;
    @BindView(R.id.nextBtn) Button mNextButton;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    private SignUpNeighborCallbacks mCallbacks;

    /**
     * Required interface for hosting activities
     */
    public interface SignUpNeighborCallbacks {
        void onNeighborSubmit(String name, String address, int zip_code, int organization_code);
    }

    public AddDetailsNeighborFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) return;
        if (activity instanceof SignUpNeighborCallbacks) {
            mCallbacks = (SignUpNeighborCallbacks) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SignUpNeighborCallbacks) {
            mCallbacks = (SignUpNeighborCallbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    public static AddDetailsNeighborFragment newInstance() {
        AddDetailsNeighborFragment fragment = new AddDetailsNeighborFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_details_neighbor, container, false);
        ButterKnife.bind(this, view);

        Logger.d("onCreateView");

        mNextButton.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.nextBtn:
                if (isEmpty(mNameEditText) || isEmpty(mAddressEditText) || isEmpty(mZipCodeEditText) || isEmpty(mOrganizationCode)) {
                    Toast.makeText(getActivity(), "Please fill in all fields.", Toast.LENGTH_LONG).show();
                }
                else if (mZipCodeEditText.getText().toString().length() != 5) {
                    Toast.makeText(getActivity(), "Zip code must be 5 digits.", Toast.LENGTH_LONG).show();
                }
                else if (mOrganizationCode.getText().toString().length() != 4) {
                    Toast.makeText(getActivity(), "Organization code must be 4 digits.", Toast.LENGTH_LONG).show();
                }
                else {
                    String name = mNameEditText.getText().toString();
                    String address = mAddressEditText.getText().toString();
                    int zip_code = Integer.parseInt(mZipCodeEditText.getText().toString());
                    int organizationCode = Integer.parseInt(mOrganizationCode.getText().toString());

                    mCallbacks.onNeighborSubmit(name, address, zip_code, organizationCode);
                }

                break;
            default:
                break;
        }

    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

}