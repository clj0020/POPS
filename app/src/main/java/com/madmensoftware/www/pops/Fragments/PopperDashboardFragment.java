package com.madmensoftware.www.pops.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
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
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madmensoftware.www.pops.Activities.AddPaymentInformationActivity;
import com.madmensoftware.www.pops.Activities.NeighborActivity;
import com.madmensoftware.www.pops.Activities.PopperActivity;
import com.madmensoftware.www.pops.Activities.PopperPaymentInfoActivity;
import com.madmensoftware.www.pops.Helpers.TinyDB;
import com.madmensoftware.www.pops.Models.User;
import com.madmensoftware.www.pops.R;
import com.orhanobut.logger.Logger;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Transfer;

import org.fabiomsr.moneytextview.MoneyTextView;
import org.parceler.Parcels;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PopperDashboardFragment extends Fragment {

    @BindView(R.id.popper_dash_name) TextView mNameTextView;
    @BindView(R.id.popper_dash_age) TextView mAgeTextView;
    @BindView(R.id.popper_dash_organization) TextView mOrganizationNameTextView;
    @BindView(R.id.popper_dash_goal) TextView mGoalTextView;
    @BindView(R.id.popper_dash_earned) TextView mEarnedTextView;
    @BindView(R.id.popper_dash_goal_date) TextView mGoalDateTextView;
    @BindView(R.id.popper_menu_btn) ImageButton mSettingsButton;
    @BindView(R.id.popper_dash_goal_progress_bar) ProgressBar mGoalProgressBar;
    //@BindView(R.id.popper_dash_skill_rating_communication) RatingBar mSkillCommunicationStars;
    @BindView(R.id.popper_dash_stats_container) RelativeLayout mStatsContainer;
    @BindView(R.id.popper_dash_no_goal_container) RelativeLayout mNoGoalContainer;
    @BindView(R.id.bankText) MoneyTextView mBankText;
    @BindView(R.id.cash_out_button) Button mCashOutButton;
    @BindView(R.id.add_goal_button) Button mGoalButton;


    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    public String userId;

    private PopperDashCallbacks mCallbacks;

    private ValueEventListener mUserValueEventListener;
    private DatabaseReference mUserDatabaseReference;

    private User mUser;

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

    public static PopperDashboardFragment newInstance() {
        PopperDashboardFragment fragment = new PopperDashboardFragment();
        Log.i("Popper:", " PopperDashboardFragment created");
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        mUserDatabaseReference = FirebaseDatabase.getInstance().getReference("users/" + auth.getCurrentUser().getUid());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_popper_dashboard, container, false);
        ButterKnife.bind(this, view);

//        LayerDrawable stars = (LayerDrawable) mSkillCommunicationStars.getProgressDrawable();
//
//        stars.getDrawable(2).setColorFilter(Color.parseColor("#0076B2"), PorterDuff.Mode.SRC_ATOP); // for filled stars
//        stars.getDrawable(1).setColorFilter(Color.parseColor("#D3D3D3"), PorterDuff.Mode.SRC_ATOP); // for half filled stars
//        stars.getDrawable(0).setColorFilter(Color.parseColor("#D3D3D3"), PorterDuff.Mode.SRC_ATOP); // for empty stars

        mGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                alertDialogBuilder.setMessage("Ability to add goals in BETA version.");
                alertDialogBuilder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                            }
                        });


                android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

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

        mCashOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getUser().getStripeAccountId() == null) {
                    Logger.d(getUser().getName());
                    Intent intent = new Intent(getActivity(), PopperPaymentInfoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("User", Parcels.wrap(getUser()));
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else {
// Build a dialog that tells the user that they were successful.
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("You may now cash out. Would you like to?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Map<String, Object> accountMap = new HashMap<String, Object>();
                            accountMap.put("popper", getUser());
                            new PopperCashOutTask().execute(accountMap);
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();                }

            }
        });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        ValueEventListener userValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                mUser = dataSnapshot.getValue(User.class);

                setUser(mUser);

                //BANK
                mBankText.setAmount(Float.valueOf(String.valueOf(mUser.getBankStatement())));


                mNameTextView.setText(mUser.getName());
                if (mUser.getAge() == 0) {
                    // Do not have their age
                }
                else {
                    mAgeTextView.setText(mUser.getAge() + " years old");
                }
                //mOrganizationNameTextView.setText(mUser.getOrganizationName());
                if (mUser.getGoal() == 0) {
                    // They have not added a Goal yet
                    mNoGoalContainer.setVisibility(View.VISIBLE);
                    mStatsContainer.setVisibility(View.INVISIBLE);
                }
                else {
                    mNoGoalContainer.setVisibility(View.INVISIBLE);
                    mStatsContainer.setVisibility(View.VISIBLE);

                    mGoalTextView.setText(convertDoubleToDollar(mUser.getGoal()));
                    mEarnedTextView.setText(convertDoubleToDollar(mUser.getEarned()));
                    mGoalDateTextView.setText(mUser.getDaysUntilGoalDate() + " days");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Logger.e(databaseError.getMessage());
            }
        };

        mUserDatabaseReference.addValueEventListener(userValueEventListener);

        mUserValueEventListener = userValueEventListener;
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mUserValueEventListener != null) {
            mUserDatabaseReference.removeEventListener(mUserValueEventListener);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public String convertDoubleToDollar(double dbl) {
        String dollar = "";
        NumberFormat nf = new DecimalFormat("$#,###.00");
        return nf.format(dbl);
    }

    private class PopperCashOutTask extends AsyncTask<Map<String, Object>, Integer, String> {
        // Do the long-running work in here
        protected String doInBackground(Map<String, Object>... params) {

            String result = "";
            Map<String, Object> accountMap = params[0];
            User popper = (User) accountMap.get("popper");

//            Customer customer = new Customer();
////
////            try {
////                customer = Customer.retrieve(user.getStripeCustomerId());
////            } catch (AuthenticationException e) {
////                e.printStackTrace();
////            } catch (InvalidRequestException e) {
////                e.printStackTrace();
////            } catch (APIConnectionException e) {
////                e.printStackTrace();
////            } catch (CardException e) {
////                e.printStackTrace();
////            } catch (APIException e) {
////                e.printStackTrace();
////            }


            // TODO: Change to production api key
            com.stripe.Stripe.apiKey = "sk_test_I9UFP4mZBd3kq6W7w9zDenGq ";
            //RequestOptions requestOptions = RequestOptions.builder().setStripeAccount("ca_9COvHdfuphlKUmpxzueVbYA3jnewkr0N").build();
            try {
                Map<String, Object> transferParams = new HashMap<String, Object>();

                int total = (int) Math.round(popper.getBankStatement() * 100);
                transferParams.put("amount", total); // Amount in cents
                transferParams.put("currency", "usd");
                transferParams.put("destination", popper.getStripeAccountId());
                //transferParams.put("method", "instant");


                Transfer.create(transferParams);

                result = "100";

            } catch (AuthenticationException e) {
                result = e.getMessage();
            } catch (InvalidRequestException e) {
                result = e.getMessage();
            } catch (APIConnectionException e) {
                result = e.getMessage();
            } catch (CardException e) {
                result = e.getMessage();
            } catch (APIException e) {
                result = e.getMessage();
            }

            return result;
        }

        // This is called each time you call publishProgress()
        protected void onProgressUpdate(Integer... progress) {

        }

        // This is called when doInBackground() is finished
        protected void onPostExecute(String result) {
            cashOutFinished(result);
            super.onPostExecute(result);
        }
    }

    public void cashOutFinished(String result) {
        if (result.equals("100")) {
            getUser().setBankStatemnt(0);

            mUserDatabaseReference.child("bankStatement").setValue(0);

            Toast.makeText(getActivity(), "Success!!", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getActivity(), "Oh no! Something went wrong. Try again!", Toast.LENGTH_LONG).show();
        }
    }


    public User getUser() {
        return this.mUser;
    }

    public void setUser(User user) {
        this.mUser = user;
    }
}
