package com.madmensoftware.www.pops.Fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madmensoftware.www.pops.Models.User;
import com.madmensoftware.www.pops.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ParentImportantInfoFragment extends Fragment {

    @BindView(R.id.parent_settings_code) TextView mAccessCode;
    @BindView(R.id.parent_settings_safe_word) TextView mSafeWord;
    @BindView(R.id.parent_important_info_close_btn) Button mCloseButton;

    private ParentImportantInfoCallbacks mCallbacks;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    public interface ParentImportantInfoCallbacks {
        void onImportantInfoClose();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) return;
        if (activity instanceof ParentImportantInfoFragment.ParentImportantInfoCallbacks) {
            mCallbacks = (ParentImportantInfoFragment.ParentImportantInfoCallbacks) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ParentImportantInfoFragment.ParentImportantInfoCallbacks) {
            mCallbacks = (ParentImportantInfoFragment.ParentImportantInfoCallbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onImportantInfoClose");
        }
    }

    public ParentImportantInfoFragment() {
        // Required empty public constructor
    }

    public static ParentImportantInfoFragment newInstance() {
        ParentImportantInfoFragment fragment = new ParentImportantInfoFragment();
        Log.i("Parent:", " ParentImporantInfoFragment created.");
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_parent_important_info, container, false);
        ButterKnife.bind(this, view);

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallbacks.onImportantInfoClose();
            }
        });

        mDatabase.child("users").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User parent = dataSnapshot.getValue(User.class);
                mAccessCode.setText(parent.getAccessCode() + "");
                mSafeWord.setText(parent.getSafeWord());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return view;
    }
}
