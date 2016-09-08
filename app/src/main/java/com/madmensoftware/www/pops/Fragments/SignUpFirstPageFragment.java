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

import com.madmensoftware.www.pops.R;


public class SignUpFirstPageFragment extends Fragment implements View.OnClickListener {


    private Button mPopperBtn;
    private Button mParentBtn;
    private Button mNeighborBtn;


    private SignUpFirstPageCallbacks mCallbacks;


    /**
     * Required interface for hosting activities
     */
    public interface SignUpFirstPageCallbacks {
        void onTypeSelected(String type);
    }


    public SignUpFirstPageFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) return;
        if (activity instanceof SignUpFirstPageCallbacks) {
            mCallbacks = (SignUpFirstPageCallbacks) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SignUpFirstPageCallbacks) {
            mCallbacks = (SignUpFirstPageCallbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    public static SignUpFirstPageFragment newInstance() {
        SignUpFirstPageFragment fragment = new SignUpFirstPageFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up_first_page, container, false);

        mPopperBtn = (Button) view.findViewById(R.id.popperBtn);
        mParentBtn = (Button) view.findViewById(R.id.parentBtn);
        mNeighborBtn = (Button) view.findViewById(R.id.neighborBtn);

        mPopperBtn.setOnClickListener(this);
        mParentBtn.setOnClickListener(this);
        mNeighborBtn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.popperBtn:
                mCallbacks.onTypeSelected("Popper");
                break;
            case R.id.parentBtn:
                mCallbacks.onTypeSelected("Parent");
                break;
            case R.id.neighborBtn:
                mCallbacks.onTypeSelected("Neighbor");
                break;
            default:
                break;
        }

    }


}
