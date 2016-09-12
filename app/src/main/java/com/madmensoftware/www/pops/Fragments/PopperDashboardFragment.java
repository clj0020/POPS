package com.madmensoftware.www.pops.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madmensoftware.www.pops.Models.User;
import com.madmensoftware.www.pops.R;

import java.util.Date;


public class PopperDashboardFragment extends Fragment {
    private static final String USER_ID = "user_id";

    private DatabaseReference mDatabase;

    public String userId;


    private TextView mNameTextView;
    private TextView mAgeTextView;
    private TextView mOrganizationTextView;
    private TextView mGoalTextView;
    private TextView mEarnedTextView;
    private TextView mGoalDateTextView;
    private ImageButton mSettingsButton;

    private PopperDashCallbacks mCallbacks;

    /**
     * Required interface for hosting activities
     */
    public interface PopperDashCallbacks {
        void onSignOutButton();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) return;
        if (activity instanceof PopperDashCallbacks) {
            mCallbacks = (PopperDashCallbacks) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PopperDashCallbacks) {
            mCallbacks = (PopperDashCallbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    public PopperDashboardFragment() {
        // Required empty public constructor
    }

    public static PopperDashboardFragment newInstance(String userId) {
        PopperDashboardFragment fragment = new PopperDashboardFragment();
        Bundle args = new Bundle();
        args.putSerializable(USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = getArguments().getString(USER_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_popper_dashboard, container, false);

        mNameTextView = (TextView) view.findViewById(R.id.popper_dash_name);
        mAgeTextView = (TextView) view.findViewById(R.id.popper_dash_age);
        mOrganizationTextView = (TextView) view.findViewById(R.id.popper_dash_organization);
        mGoalTextView = (TextView) view.findViewById(R.id.popper_dash_goal);
        mEarnedTextView = (TextView) view.findViewById(R.id.popper_dash_earned);
        mGoalDateTextView = (TextView) view.findViewById(R.id.popper_dash_goal_date);

        mSettingsButton = (ImageButton) view.findViewById(R.id.popper_menu_btn);

        mSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(getActivity(), mSettingsButton);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popper_popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.popper_menu_settings:
                                break;
                            case R.id.popper_menu_logout:
                                mCallbacks.onSignOutButton();
                                break;
                            default:
                                break;
                        }

                        return true;
                    }
                });

                popup.show();//showing popup menu
            }
        });

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users/" + userId);

        // Read from the database
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                User mUser = dataSnapshot.getValue(User.class);

                mNameTextView.setText(mUser.getName());
                mAgeTextView.setText(mUser.getAge() + " years old");
                mGoalTextView.setText("$" + mUser.getGoal());
                mEarnedTextView.setText("$" + mUser.getEarned());
                mGoalDateTextView.setText(mUser.getDaysUntilGoalDate() + " days");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });

        return view;
    }


//    @Override
//    public void onClick(View v) {
//
//        switch (v.getId()) {
//            case R.id.popper_dash_sign_out_btn:
//                mCallbacks.onSignOutButton();
//                Log.i("Sign Out", "Sign out button clicked");
//                break;
//            case R.id.popper_dash_goal:
//                break;
//            default:
//                break;
//        }
//    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
