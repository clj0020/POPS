package com.madmensoftware.www.pops.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.fitness.data.Value;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madmensoftware.www.pops.Helpers.TinyDB;
import com.madmensoftware.www.pops.Models.Job;
import com.madmensoftware.www.pops.Models.User;
import com.madmensoftware.www.pops.R;
import com.orhanobut.logger.Logger;
import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Charge;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NeighborPaymentOverviewActivity extends AppCompatActivity {

    @BindView(R.id.neighbor_payment_overview_start_time) TextView mNeighborPaymentStartTimeTextView;
    @BindView(R.id.neighbor_payment_overview_completion_time) TextView mNeighborPaymentCompletionTimeTextView;
    @BindView(R.id.neighbor_payment_overview_total_time_taken) TextView mNeighborPaymentTotalTimeTextView;
    @BindView(R.id.neighbor_payment_overview_price_rate) TextView mNeighborPaymentPriceRateTextView;
    @BindView(R.id.neighbor_payment_overview_subtotal) TextView mNeighborPaymentSubtotalTextView;
    @BindView(R.id.neighbor_payment_overview_transaction_fee) TextView mNeighborPaymentTransactionFeeTextView;
    @BindView(R.id.neighbor_payment_overview_total) TextView mNeighborPaymentTotalTextView;
    @BindView(R.id.neighbor_payment_overview_tip_edittext) EditText mNeighborPaymentTipEditText;
    @BindView(R.id.neighbor_payment_overview_pay_button) Button mNeighborPayButton;

    private Job mJob;
    private String mJobUid;

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    private ValueEventListener mJobValueEventListener;
    private DatabaseReference mJobDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_neighbor_payment_overview);
        ButterKnife.bind(this);

        TinyDB tinyDB = new TinyDB(getApplicationContext());
        mJobUid = tinyDB.getString("job-uid");


        mDatabase = FirebaseDatabase.getInstance().getReference();

        mJobDatabaseReference = mDatabase.child("jobs").child(mJobUid);
    }

    @Override
    public void onStart() {
        super.onStart();
        Logger.i("onStart Neighborpaymentoverview");

        ValueEventListener jobValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mJob = dataSnapshot.getValue(Job.class);


                if (mJob.getStartTime() != 0 && mJob.getCompletionTime() != 0 && mJob.getBudget() != 0) {
                    mNeighborPaymentStartTimeTextView.setText(formatDateAndTime(mJob.getStartTime()));
                    mNeighborPaymentCompletionTimeTextView.setText(formatDateAndTime(mJob.getCompletionTime()));

                    mNeighborPaymentTotalTimeTextView.setText(mJob.getTotalTime() + " hours");

                    NumberFormat formatter = NumberFormat.getCurrencyInstance();
                    mNeighborPaymentPriceRateTextView.setText(formatter.format(mJob.getBudget()));
                    mNeighborPaymentSubtotalTextView.setText(formatter.format(mJob.getSubtotal()));
                    mNeighborPaymentTransactionFeeTextView.setText(formatter.format(mJob.getTransactionFee()));
                    mNeighborPaymentTotalTextView.setText(formatter.format(mJob.getTotal()));
                }


                mNeighborPayButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO: Set neighbor pay button to enabled if payment doesn't work.
                        mNeighborPayButton.setEnabled(false);
                        mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()) {
                                    User neighbor = dataSnapshot.getValue(User.class);

                                    HashMap<String, Object> hashMap = new HashMap<String, Object>();
                                    hashMap.put("neighbor", neighbor);
                                    hashMap.put("job", mJob);
                                    new NeighborPaymentOverviewActivity.CreateChargeTask().execute(neighbor, mJob);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                Logger.e(error.getMessage());
                            }

                        });

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Logger.e(databaseError.getMessage());
            }
        };

        mJobDatabaseReference.addValueEventListener(jobValueEventListener);

        mJobValueEventListener = jobValueEventListener;
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mJobValueEventListener != null) {
            mJobDatabaseReference.removeEventListener(mJobValueEventListener);
        }
    }

    public String formatDateAndTime(long time) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm MM-dd-yyyy"); // Set your date format
        String currentData = sdf.format(date); // Get Date String according to date format

        return currentData;
    }

    // Called after the payment is processed.
    public void paymentFinished(String result) {

        // If the result starts with 100 then success.
        if (result.substring(0, 3).equals("100")) {
            Logger.i("Payment Successful!");
            Logger.i("Job UID is " + result.substring(3));

            // Result should contain the job uid after 100
            String jobUid = result.substring(3);
            final String popperUid = mJob.getPopperUid();
            final double total = mJob.getTotal();



            // Set the status of the job to paid
            mDatabase.child("jobs").child(jobUid).child("status").setValue("paid");

            mDatabase.child("job-poppers").child(mJob.getPopperUid()).child(mJob.getUid()).setValue(mJob);
            mDatabase.child("job-posters").child(mJob.getPosterUid()).child(mJob.getUid()).setValue(mJob);

            mDatabase.child("users").child(mJob.getPopperUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        User popper = dataSnapshot.getValue(User.class);
                        popper.setBankStatemnt(mJob.getTotal());
                        mDatabase.child("users").child(popper.getUid()).child("bankStatement").setValue(popper.getBankStatement());
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Logger.e(error.getMessage());
                }

            });
           /* mDatabase.child(popperUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User mUser = dataSnapshot.getValue(User.class);
                    mUser.setBankStatemnt(total);

                    mDatabase.child("users").child(popperUid).child("bankStatement").setValue(mUser.getBankStatement());
                    Logger.d("mUser bank " + mUser.getBankStatement());

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            } );*/


            // Build a dialog that tells the user that they were successful.
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Your payment was successful!");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Go to the Neighbor activity once they click ok
                    startActivity(new Intent(NeighborPaymentOverviewActivity.this, NeighborActivity.class));
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        else {
            Logger.i("Payment Unsuccessful - " + result);

            // Build a dialog that tells the user that they were successful.
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Payment Unsuccessful - " + result);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Go to the Neighbor activity once they click ok
                    //startActivity(new Intent(NeighborPaymentOverviewActivity.this, NeighborActivity.class));
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    private class CreateChargeTask extends AsyncTask<Object, Integer, String> {
        // Do the long-running work in here
        protected String doInBackground(Object... params) {
            User user = (User) params[0];
            Job job = (Job) params[1];

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
            Stripe.apiKey = "sk_test_I9UFP4mZBd3kq6W7w9zDenGq";
            // Create a charge: this will charge the user's card
            try {
                Map<String, Object> chargeParams = new HashMap<String, Object>();
                int total = (int) Math.round(job.getTotal() * 100);
                //int applicationFee = (int) Math.round(job.getTransactionFee() * 100);
                chargeParams.put("amount", total); // Amount in cents
                chargeParams.put("currency", "usd");
                chargeParams.put("customer", user.getStripeCustomerId());
                chargeParams.put("description", "Charge for POPS Job #" + job.getUid());
                //chargeParams.put("destination", "acct_18u1uFHfcGmL46Ma");
                //chargeParams.put("application_fee", applicationFee);

                Charge charge = Charge.create(chargeParams);

                return "100" + job.getUid();
            } catch (CardException e) {
                // The card has been declined
                return "Error - " + e.getMessage();
            } catch (APIException e) {
                e.printStackTrace();
                return "Error - " + e.getMessage();
            } catch (InvalidRequestException e) {
                e.printStackTrace();
                return "Error - " + e.getMessage();
            } catch (APIConnectionException e) {
                e.printStackTrace();
                return "Error - " + e.getMessage();
            } catch (AuthenticationException e) {
                e.printStackTrace();
                return "Error - " + e.getMessage();
            }
        }

        // This is called each time you call publishProgress()
        protected void onProgressUpdate(Integer... progress) {

        }

        // This is called when doInBackground() is finished
        protected void onPostExecute(String result) {
            paymentFinished(result);
            super.onPostExecute(result);
        }
    }
}
