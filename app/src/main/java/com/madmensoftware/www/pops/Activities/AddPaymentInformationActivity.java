package com.madmensoftware.www.pops.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.madmensoftware.www.pops.Fragments.NeighborCreditCardFormFragment;
import com.madmensoftware.www.pops.Fragments.ParentImportantInfoFragment;
import com.madmensoftware.www.pops.Helpers.TinyDB;
import com.madmensoftware.www.pops.Models.Job;
import com.madmensoftware.www.pops.Models.User;
import com.madmensoftware.www.pops.R;
import com.orhanobut.logger.Logger;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Customer;

import java.util.HashMap;
import java.util.Map;

public class AddPaymentInformationActivity extends AppCompatActivity implements NeighborCreditCardFormFragment.NeighborCreditCardFormCallbacks {

    private static final String PUBLISHIBLE_TEST_KEY = "pk_test_9SdGQF1ZibEEnbJ3vYmBaAFj";
    private static final String SECRET_TEST_KEY = "sk_test_I9UFP4mZBd3kq6W7w9zDenGq";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment_information);


        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.add_payment_information_fragment_container);

        if (fragment == null) {
            fragment = NeighborCreditCardFormFragment.newInstance();
            fm.beginTransaction()
                    .add(R.id.add_payment_information_fragment_container, fragment)
                    .commit();
        }
        else {
            fragment = NeighborCreditCardFormFragment.newInstance();
            fm.beginTransaction()
                    .replace(R.id.add_payment_information_fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onCreditCardFormSubmit(String cardNumber, String expirationMonth, String expirationYear, String ccv, String postalCode) {
        int expMonth = Integer.parseInt(expirationMonth);
        int expYear = Integer.parseInt(expirationYear);

        Card card = new Card(
                cardNumber,
                expMonth,
                expYear,
                ccv
        );

        String token;
        try {
            Stripe stripe = new Stripe(PUBLISHIBLE_TEST_KEY);

            boolean validation = card.validateCard();
            if (validation) {
                stripe.createToken(
                        card,
                        new TokenCallback() {
                            public void onSuccess(Token token) {
                                TinyDB tinyDB = new TinyDB(getApplicationContext());
                                User neighbor = (User) tinyDB.getObject("User", User.class);
                                new AddPaymentInformationActivity.CreateCustomerTask().execute(token);
                            }
                            public void onError(Exception error) {
                            }
                        });
            }
            else if (!card.validateNumber()) {
                Toast.makeText(this, "The card number that you entered is invalid", Toast.LENGTH_LONG).show();
            }
            else if (!card.validateExpiryDate()) {
                Toast.makeText(this, "The expiration date that you entered is invalid", Toast.LENGTH_LONG).show();
            }
            else if (!card.validateCVC()) {
                Toast.makeText(this, "The CVC code that you entered is invalid", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "The card details that you entered is invalid", Toast.LENGTH_LONG).show();
            }

        } catch (AuthenticationException e) {
            e.printStackTrace();
        }


    }

    public void processFinish(Customer customer) {
        TinyDB tinyDB = new TinyDB(getApplicationContext());
        User user = (User) tinyDB.getObject("User", User.class);
        Logger.d("AddUserDetails", "processFinish user is " + user.getName());

        user.setStripeCustomerId(customer.getId());
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        if (tinyDB.getBoolean("add_job")) {
            Job job = (Job) tinyDB.getObject("job", Job.class);

            //DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            String jobId = mDatabase.child("jobs").push().getKey();
            job.setUid(jobId);
            mDatabase.child("jobs").child(jobId).setValue(job);

            GeoFire geofire = new GeoFire(mDatabase.child("jobs_location"));
            geofire.setLocation(jobId, new GeoLocation(job.getLatitude(), job.getLongitude()));


            user.setPaymentAdded(true);

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            mDatabase.child("users").child(firebaseUser.getUid()).setValue(user);
            mDatabase.child("users").child(firebaseUser.getUid()).child("paymentAdded").setValue(true);
            //mUser.setPaymentAdded(true);
            //mDatabase.child("users").child(user.getUid()).child("paymentAdded").setValue(true);



            //mDatabase.child("users").child(firebaseUser.getUid()).child("hasPaymentInfo").setValue(true);

            tinyDB.putObject("User", user);

            Toast.makeText(AddPaymentInformationActivity.this, "Job Added Successfully", Toast.LENGTH_LONG).show();
            startActivity(new Intent(AddPaymentInformationActivity.this, NeighborActivity.class));
        }
        else {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid()).child("paymentAdded").setValue(true);
            FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid()).setValue(user);

            tinyDB.putObject("User", user);
            startActivity(new Intent(AddPaymentInformationActivity.this, MainActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AddPaymentInformationActivity.this, MainActivity.class));
        finish();
    }

    private class CreateCustomerTask extends AsyncTask<Token, Integer, Customer> {
        // Do the long-running work in here
        protected Customer doInBackground(Token... params) {

            Token token = params[0];
            String tokenId = token.getId();
            com.stripe.Stripe.apiKey = SECRET_TEST_KEY;
            // Create a Customer
            Map<String, Object> customerParams = new HashMap<String, Object>();
            customerParams.put("source", tokenId);
            customerParams.put("description", "Example");

            Customer customer = new Customer();

            try {
                customer = Customer.create(customerParams);

            } catch (AuthenticationException e) {
                e.printStackTrace();
            } catch (InvalidRequestException e) {
                e.printStackTrace();
            } catch (APIConnectionException e) {
                e.printStackTrace();
            } catch (CardException e) {
                e.printStackTrace();
            } catch (APIException e) {
                e.printStackTrace();
            }

            return customer;
        }

        // This is called each time you call publishProgress()
        protected void onProgressUpdate(Integer... progress) {

        }

        // This is called when doInBackground() is finished
        protected void onPostExecute(Customer result) {
            processFinish(result);
            super.onPostExecute(result);
        }
    }
}
