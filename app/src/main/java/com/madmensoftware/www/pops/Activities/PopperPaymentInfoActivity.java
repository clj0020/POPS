package com.madmensoftware.www.pops.Activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madmensoftware.www.pops.Fragments.AddDetailsParentFragment;
import com.madmensoftware.www.pops.Fragments.ParentCreditCardFormFragment;
import com.madmensoftware.www.pops.Fragments.ParentImportantInfoFragment;
import com.madmensoftware.www.pops.Fragments.PopperCreditCardFormFragment;
import com.madmensoftware.www.pops.Fragments.PopperPaymentInfoFragment;
import com.madmensoftware.www.pops.Helpers.IPAddressUtils;
import com.madmensoftware.www.pops.Helpers.TinyDB;
import com.madmensoftware.www.pops.Models.Job;
import com.madmensoftware.www.pops.Models.User;
import com.madmensoftware.www.pops.R;
import com.orhanobut.logger.Logger;
import com.stripe.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Account;
import com.stripe.model.Charge;
import com.stripe.model.ExternalAccount;
import com.stripe.model.Transfer;
import com.stripe.net.RequestOptions;

import org.parceler.Parcels;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by carson on 12/16/2016.
 */

public class PopperPaymentInfoActivity extends AppCompatActivity implements PopperPaymentInfoFragment.PopperPaymentInfoCallbacks, PopperCreditCardFormFragment.PopperCreditCardFormCallbacks {

    private User mUser;

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    private static final String PUBLISHIBLE_TEST_KEY = "pk_test_9SdGQF1ZibEEnbJ3vYmBaAFj";
    private static final String SECRET_TEST_KEY = "sk_test_I9UFP4mZBd3kq6W7w9zDenGq";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popper_payment_info);
        ButterKnife.bind(this);

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            mUser = Parcels.unwrap(bundle.getParcelable("User"));
            Logger.d(mUser.getName());
        }
        else {
            Logger.d("Bundle is Null");
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();

        auth = FirebaseAuth.getInstance();

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.popper_payment_info_fragment_container);

        if (fragment == null) {
            fragment = PopperPaymentInfoFragment.newInstance();
            fm.beginTransaction()
                    .add(R.id.popper_payment_info_fragment_container, fragment)
                    .commit();
        }
        else {
            fragment = PopperPaymentInfoFragment.newInstance();
            fm.beginTransaction()
                    .replace(R.id.popper_payment_info_fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

//        ValueEventListener jobValueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                mJob = dataSnapshot.getValue(Job.class);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Logger.e(databaseError.getMessage());
//            }
//        };
//
//        mJobDatabaseReference.addValueEventListener(jobValueEventListener);
//
//        mJobValueEventListener = jobValueEventListener;
    }

    @Override
    public void onStop() {
        super.onStop();

//        if (mJobValueEventListener != null) {
//            mJobDatabaseReference.removeEventListener(mJobValueEventListener);
//        }
    }

    @Override
    public void onPopperPaymentInfoSubmit(String address, String city, String state, int zipcode, String SSN, int phone) {

        mUser.setAddress(address);
        mUser.setCity(city);
        mUser.setState(state);
        mUser.setZipCode(zipcode);
        mUser.setPhone(phone);

        mDatabase.child("users").child(auth.getCurrentUser().getUid()).setValue(mUser);

        Logger.i("Popper Email: " + auth.getCurrentUser().getEmail());

        Map<String, Object> accountParams = new HashMap<String, Object>();
        accountParams.put("managed", true);
        accountParams.put("country", "US");
        accountParams.put("email", auth.getCurrentUser().getEmail());

        Map<String, Object> legalEntityParams = new HashMap<String, Object>();
        Map<String, Object> dobParams = new HashMap<String, Object>();
        Map<String, Object> addressParams = new HashMap<String, Object>();
        Map<String, Object> termsOfServiceParams = new HashMap<String, Object>();

        long birthDay = mUser.getBirthDay();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(birthDay);

        addressParams.put("city", city);
        addressParams.put("country", "US");
        addressParams.put("line1", address);
        addressParams.put("line2", null);
        addressParams.put("postal_code", zipcode);
        addressParams.put("state", state);

        dobParams.put("day", cal.get(Calendar.DAY_OF_MONTH));
        dobParams.put("month", cal.get(Calendar.MONTH) + 1);
        dobParams.put("year", cal.get(Calendar.YEAR));


        long currentTimeInMillis = Calendar.getInstance().getTime().getTime() / 1000;
        int currentTimeInSeconds = (int) Math.floor(System.currentTimeMillis() / 1000);

        Logger.d(System.currentTimeMillis());
        Logger.d(currentTimeInSeconds);


        termsOfServiceParams.put("date", currentTimeInSeconds);
        termsOfServiceParams.put("ip", IPAddressUtils.getIPAddress(true));

        legalEntityParams.put("dob", dobParams);
        legalEntityParams.put("first_name", mUser.getFirstName());
        legalEntityParams.put("last_name", mUser.getLastName());
        legalEntityParams.put("phone_number", phone);
        legalEntityParams.put("personal_id_number", SSN);
        legalEntityParams.put("type", "individual");
        legalEntityParams.put("address", addressParams);
        accountParams.put("legal_entity", legalEntityParams);
        accountParams.put("tos_acceptance", termsOfServiceParams);

        new CreateAccountTask().execute(accountParams);
;
    }

    @Override
    public void onPopperCreditCardFormSubmit(String cardNumber, String expirationMonth, String expirationYear, String ccv, String postalCode) {
        int expMonth = Integer.parseInt(expirationMonth);
        int expYear = Integer.parseInt(expirationYear);


        Card card = new Card(
                cardNumber,
                expMonth,
                expYear,
                ccv
        );

        card.setCurrency("usd");

        try {
            com.stripe.android.Stripe stripe = new com.stripe.android.Stripe(PUBLISHIBLE_TEST_KEY);

            boolean validation = card.validateCard();
            if (validation) {
                stripe.createToken(
                        card,
                        new TokenCallback() {
                            public void onSuccess(Token token) {

                                TinyDB tinyDB = new TinyDB(getApplicationContext());
                                User popper = (User) tinyDB.getObject("User", User.class);

                                Map<String, Object> accountMap = new HashMap<String, Object>();
                                accountMap.put("popper", popper);
                                accountMap.put("token", token);
                                new UpdatePopperAccountTask().execute(accountMap);
                            }
                            public void onError(Exception error) {
                                Logger.e(error.getMessage());
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

    public void accountCreationProcessFinish(String accountId, String publishableAPIKey, String secretAPIKey) {

        Logger.d("AccountID" + accountId);
        Logger.d("AccountPublishable" + publishableAPIKey);
        Logger.d("AccountSecret" + secretAPIKey);

        mUser.setStripeAccountId(accountId);
        mUser.setStripeApiSecretKey(secretAPIKey);
        mUser.setStripeApiPublishableKey(publishableAPIKey);


        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.child("users").child(firebaseUser.getUid()).setValue(mUser);

        TinyDB tinyDB = new TinyDB(getApplicationContext());
        tinyDB.putObject("User", mUser);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.popper_payment_info_fragment_container);

        if (fragment == null) {
            fragment = PopperCreditCardFormFragment.newInstance();
            fm.beginTransaction()
                    .add(R.id.popper_payment_info_fragment_container, fragment)
                    .commit();
        }
        else {
            fragment = PopperCreditCardFormFragment.newInstance();
            fm.beginTransaction()
                    .replace(R.id.popper_payment_info_fragment_container, fragment)
                    .commit();
        }
    }

    public void accountUpdateProcessFinish(ExternalAccount account) {

        TinyDB tinyDB = new TinyDB(this);
        final User popper = (User) tinyDB.getObject("User", User.class);

        // Build a dialog that tells the user that they were successful.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You may now cash out. Would you like to?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Map<String, Object> accountMap = new HashMap<String, Object>();
                accountMap.put("popper", popper);
                new PopperCashOutTask().execute(accountMap);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startActivity(new Intent(PopperPaymentInfoActivity.this, PopperActivity.class));
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void cashOutFinished(String result) {

        mUser.setBankStatemnt(0);

        mDatabase.child("users").child(auth.getCurrentUser().getUid()).setValue(mUser);

        Toast.makeText(getApplicationContext(), "Success!!", Toast.LENGTH_LONG).show();

        startActivity(new Intent(PopperPaymentInfoActivity.this, PopperActivity.class));
    }

    private class CreateAccountTask extends AsyncTask<Map<String, Object>, Integer, Account> {
        // Do the long-running work in here
        protected Account doInBackground(Map<String, Object>... params) {
            Map<String, Object> accountParams = params[0];

            com.stripe.Stripe.apiKey = SECRET_TEST_KEY;

            Account account = new Account();

            try {
                account = Account.create(accountParams);
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
            Logger.d(account);
            Logger.d(account.getId());
            return account;
        }

        // This is called each time you call publishProgress()
        protected void onProgressUpdate(Integer... progress) {

        }

        // This is called when doInBackground() is finished
        protected void onPostExecute(Account result) {
            Logger.d(result.getId());
            accountCreationProcessFinish(result.getId(), result.getKeys().getPublishable(), result.getKeys().getSecret());
            super.onPostExecute(result);
        }
    }

    private class UpdatePopperAccountTask extends AsyncTask<Map<String, Object>, Integer, ExternalAccount> {
        // Do the long-running work in here
        protected ExternalAccount doInBackground(Map<String, Object>... params) {
            Map<String, Object> accountMap = params[0];
            User popper = (User) accountMap.get("popper");
            Token cardToken = (Token) accountMap.get("token");

            Logger.d(popper.getName());
            Logger.d(cardToken.getCard());
            Logger.d(popper.getStripeAccountId());

            com.stripe.Stripe.apiKey = SECRET_TEST_KEY;



            ExternalAccount externalAccount = new ExternalAccount();

            Logger.d("AddUserDetails: ", "updateParentAccountTask doInBackground accountSecretAPIKey is " + popper.getStripeApiSecretKey());
            Logger.d("AddUserDetails: ", "updateParentAccountTask doInBackground accountPublishableAPIKey is " + popper.getStripeApiPublishableKey());
            Logger.d("AddUserDetails: ", "updateParentAccountTask doInBackground accountID is " + popper.getStripeAccountId());

            try {
                Account account = Account.retrieve(popper.getStripeAccountId(), null);

                Map<String, Object> externalAccountParams = new HashMap<String, Object>();
                externalAccountParams.put("external_account", cardToken.getId());
                externalAccountParams.put("default_for_currency", true);
                externalAccount = account.getExternalAccounts().create(externalAccountParams);

                Logger.d("AddUserDetails: ", "updateParentAccountTask doInBackground externalAccountID is " + externalAccount.getId());

            } catch (AuthenticationException e) {
                Logger.d("AddUserDetails: ", "updateParentAccountTask doInBackground AuthenticationException:" + e.getMessage());
            } catch (InvalidRequestException e) {
                Logger.d("AddUserDetails: " + "updateParentAccountTask doInBackground InvalidRequestException:" + e.getMessage());
            } catch (APIConnectionException e) {
                Logger.d("AddUserDetails: ", "updateParentAccountTask doInBackground APIConnectionException:" + e.getMessage());
            } catch (CardException e) {
                Logger.d("AddUserDetails: ", "updateParentAccountTask doInBackground CardException:" + e.getMessage());
            } catch (APIException e) {
                Logger.d("AddUserDetails: ", "updateParentAccountTask doInBackground APIException:" + e.getMessage());
            }

            return externalAccount;
        }

        // This is called each time you call publishProgress()
        protected void onProgressUpdate(Integer... progress) {

        }

        // This is called when doInBackground() is finished
        protected void onPostExecute(ExternalAccount result) {
            accountUpdateProcessFinish(result);
            super.onPostExecute(result);
        }
    }

    private class PopperCashOutTask extends AsyncTask<Map<String, Object>, Integer, String> {
        // Do the long-running work in here
        protected String doInBackground(Map<String, Object>... params) {

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
//            RequestOptions requestOptions = RequestOptions.builder().setStripeAccount("ca_9COvHdfuphlKUmpxzueVbYA3jnewkr0N").build();
            try {
                Map<String, Object> transferParams = new HashMap<String, Object>();

                int total = (int) Math.round(popper.getBankStatement() * 100);
                transferParams.put("amount", total); // Amount in cents
                transferParams.put("currency", "usd");
                transferParams.put("destination", popper.getStripeAccountId());

                Transfer.create(transferParams);
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

            return "";
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

    public User getUser() {
        return this.mUser;
    }

    public void setUser(User user) {
        this.mUser = user;
    }

}
