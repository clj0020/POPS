package com.madmensoftware.www.pops.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.madmensoftware.www.pops.Fragments.AddDetailsNeighborFragment;
import com.madmensoftware.www.pops.Fragments.AddDetailsParentFragment;
import com.madmensoftware.www.pops.Fragments.AddDetailsPopperFragment;
import com.madmensoftware.www.pops.Fragments.NeighborCreditCardFormFragment;
import com.madmensoftware.www.pops.Fragments.ParentCreditCardFormFragment;
import com.madmensoftware.www.pops.Fragments.ParentImportantInfoFragment;
import com.madmensoftware.www.pops.Helpers.TinyDB;
import com.madmensoftware.www.pops.Models.User;
import com.madmensoftware.www.pops.R;

import com.orhanobut.logger.Logger;
import com.stripe.android.*;
import com.stripe.android.Stripe;
import com.stripe.model.Account;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Customer;
import com.stripe.model.ExternalAccount;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AddUserDetails extends AppCompatActivity implements AddDetailsPopperFragment.SignUpPopperCallbacks,
        AddDetailsNeighborFragment.SignUpNeighborCallbacks, AddDetailsParentFragment.SignUpParentCallbacks,
        ParentImportantInfoFragment.ParentImportantInfoCallbacks, NeighborCreditCardFormFragment.NeighborCreditCardFormCallbacks,
        ParentCreditCardFormFragment.ParentCreditCardFormCallbacks {

    private static final String TAG = "AddUserDetails:";
    private static final String PUBLISHIBLE_TEST_KEY = "pk_test_9SdGQF1ZibEEnbJ3vYmBaAFj";
    private static final String SECRET_TEST_KEY = "sk_test_I9UFP4mZBd3kq6W7w9zDenGq";
    
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_details);

        auth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser == null) {
                    startActivity(new Intent(AddUserDetails.this, LoginActivity.class));
                    finish();
                }
                else {
                }
            }
        };

        mDatabase = FirebaseDatabase.getInstance().getReference();

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.user_details_fragment_container);

        TinyDB tinyDb = new TinyDB(this);
        String type = tinyDb.getString("userType");

        switch (type) {
            case "Popper":
                if (fragment == null) {
                    fragment = AddDetailsPopperFragment.newInstance();
                    fm.beginTransaction()
                            .add(R.id.user_details_fragment_container, fragment)
                            .commit();
                }
                break;
            case "Parent":
                if (fragment == null) {
                    fragment = AddDetailsParentFragment.newInstance();
                    fm.beginTransaction()
                            .add(R.id.user_details_fragment_container, fragment)
                            .commit();
                }
                break;
            case "Neighbor":
                if (fragment == null) {
                    fragment = AddDetailsNeighborFragment.newInstance();
                    fm.beginTransaction()
                            .add(R.id.user_details_fragment_container, fragment)
                            .commit();
                }
                break;
        }
    }

    @Override
    public void onPopperSubmit(String name, int age, int zip_code, String transportation, int radius, double goal, long goalDateLong) {
//    public void onPopperSubmit(String name, int age, int zip_code, String transportation, int radius, double goal, long goalDateLong, int parentCode, int organizationCode) {
        FirebaseUser firebasePopper = FirebaseAuth.getInstance().getCurrentUser();
        String email = firebasePopper.getEmail();

        final User popper = new User();

        popper.setName(name);
        popper.setEmail(email);
        popper.setAge(age);
        popper.setZipCode(zip_code);
        popper.setTransportationType(transportation);
        popper.setRadius(radius);
        popper.setGoal(goal);
        popper.setGoalDate(goalDateLong);
        popper.setEarned(0);
        popper.setType("Popper");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.child("users").child(firebaseUser.getUid()).setValue(popper);

        TinyDB tinyDB = new TinyDB(getApplicationContext());
        tinyDB.putObject("User", popper);


        Intent intent = new Intent(AddUserDetails.this, MainActivity.class);
        startActivity(intent);

    }

    @Override
    public void onNeighborSubmit(String name, String address, int zip_code, int organizationCode) {
        FirebaseUser firebaseNeighbor = FirebaseAuth.getInstance().getCurrentUser();
        String email = firebaseNeighbor.getEmail();

        User neighbor = new User();
        neighbor.setName(name);
        neighbor.setEmail(email);
        neighbor.setAddress(address);
        neighbor.setZipCode(zip_code);
        neighbor.setOrganizationCode(organizationCode);
        neighbor.setType("Neighbor");

        TinyDB tinyDB = new TinyDB(this);
        tinyDB.putObject("User", neighbor);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.user_details_fragment_container);

        if (fragment == null) {
            fragment = NeighborCreditCardFormFragment.newInstance();
            fm.beginTransaction()
                    .add(R.id.user_details_fragment_container, fragment)
                    .commit();
        }
        else {
            fragment = NeighborCreditCardFormFragment.newInstance();
            fm.beginTransaction()
                    .replace(R.id.user_details_fragment_container, fragment)
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
                            new CreateCustomerTask().execute(token);

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

    @Override
    public void onParentCreditCardFormSubmit(String cardNumber, String expirationMonth, String expirationYear, String ccv, String postalCode) {
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
            Stripe stripe = new Stripe(PUBLISHIBLE_TEST_KEY);

            boolean validation = card.validateCard();
            if (validation) {
                stripe.createToken(
                        card,
                        new TokenCallback() {
                            public void onSuccess(Token token) {

                                TinyDB tinyDB = new TinyDB(getApplicationContext());
                                User parent = (User) tinyDB.getObject("User", User.class);
                                Map<String, Object> accountMap = new HashMap<String, Object>();
                                accountMap.put("parent", parent);
                                accountMap.put("token", token);
                                new UpdateParentAccountTask().execute(accountMap);
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
    
    @Override
    public void onParentSubmit(String firstName, String lastName, int lastFourSSN, int dobYear, int dobMonth, int dobDay, int phone) {
        FirebaseUser firebaseParent = FirebaseAuth.getInstance().getCurrentUser();
        String email = firebaseParent.getEmail();

        User parent = new User();

        parent.setName(firstName + " " + lastName);
        parent.setPhone(phone);
        parent.setEmail(email);

        Map<String, Object> accountParams = new HashMap<String, Object>();
        accountParams.put("managed", true);
        accountParams.put("country", "US");
        accountParams.put("email", email);

        Map<String, Object> legalEntityParams = new HashMap<String, Object>();
        Map<String, Object> dobParams = new HashMap<String, Object>();
        dobParams.put("day", dobDay);
        dobParams.put("month", dobMonth);
        dobParams.put("year", dobYear);

        legalEntityParams.put("dob", dobParams);
        legalEntityParams.put("first_name", firstName);
        legalEntityParams.put("last_name", lastName);
        legalEntityParams.put("phone_number", phone);
        legalEntityParams.put("ssn_last_4", lastFourSSN);
        legalEntityParams.put("type", "individual");
        accountParams.put("legal_entity", legalEntityParams);

        new CreateAccountTask().execute(accountParams);

        TinyDB tinyDB = new TinyDB(getApplicationContext());
        tinyDB.putObject("User", parent);
    }

    @Override
    public void onImportantInfoClose() {
        Intent intent = new Intent(AddUserDetails.this, MainActivity.class);
        startActivity(intent);
    }

    public int generateUniqueCode() {
        final int randomPIN = (int)(Math.random()*9000)+1000;

        Query parentCodeQuery = mDatabase.child("parent-codes").equalTo(randomPIN);
        parentCodeQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        if (postSnapshot.getValue() != null) {
                            generateUniqueCode();
                        }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return randomPIN;
    }

    public String generateRandomSafeWord() {
        String[] safeWords = {"FISH", "CHIP", "TACO"};

        Random rand = new Random();
        int randomNum = rand.nextInt((2 - 0) + 1) + 0;
        return safeWords[randomNum];
    }

    public void processFinish(Customer customer) {
        TinyDB tinyDB = new TinyDB(getApplicationContext());
        User user = (User) tinyDB.getObject("User", User.class);
        Logger.d("AddUserDetails", "processFinish user is " + user.getName());

        user.setStripeCustomerId(customer.getId());

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.child("users").child(firebaseUser.getUid()).setValue(user);

        tinyDB.putObject("User", user);


        switch(user.getType()) {
            case "Parent":
                FragmentManager fm = getSupportFragmentManager();
                Fragment fragment = fm.findFragmentById(R.id.user_details_fragment_container);

                if (fragment == null) {
                    fragment = ParentImportantInfoFragment.newInstance();
                    fm.beginTransaction()
                            .add(R.id.user_details_fragment_container, fragment)
                            .commit();
                }
                else {
                    fragment = ParentImportantInfoFragment.newInstance();
                    fm.beginTransaction()
                            .replace(R.id.user_details_fragment_container, fragment)
                            .commit();
                }
                break;
            case "Neighbor":
                Intent intent = new Intent(AddUserDetails.this, MainActivity.class);
                startActivity(intent);
                break;
        }


    }

    public void accountCreationProcessFinish(Account account) {
        int parentCode = generateUniqueCode();
        String safeWord = generateRandomSafeWord();

        Logger.d("AddUserDetails", "accountCreationProcessFinish accountId" + account.getId());
        Logger.d("AddUserDetails", "accountCreationProcessFinish secretAccountKey" + account.getKeys().getSecret());
        Logger.d("AddUserDetails", "accountCreationProcessFinish publishableAccountKey" + account.getKeys().getPublishable());

        TinyDB tinyDB = new TinyDB(this);
        User parent = (User) tinyDB.getObject("User", User.class);
        parent.setStripeAccountId(account.getId());
        parent.setStripeApiSecretKey(account.getKeys().getSecret());
        parent.setStripeApiPublishableKey(account.getKeys().getPublishable());
        parent.setAccessCode(parentCode);
        parent.setSafeWord(safeWord);
        parent.setType("Parent");

        Logger.d("UserDetails", "ParentAccessCode" + parent.getAccessCode());
        Logger.d("UserDetails", "ParentSafeWord" + parent.getSafeWord());

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.child("users").child(firebaseUser.getUid()).setValue(parent);
        mDatabase.child("parent-codes").child(parentCode + "").setValue(firebaseUser.getUid());
        mDatabase.child("safe-words").child(safeWord).setValue(firebaseUser.getUid());

        tinyDB.putObject("User", parent);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.user_details_fragment_container);

        if (fragment == null) {
            fragment = ParentCreditCardFormFragment.newInstance();
            fm.beginTransaction()
                    .add(R.id.user_details_fragment_container, fragment)
                    .commit();
        }
        else {
            fragment = ParentCreditCardFormFragment.newInstance();
            fm.beginTransaction()
                    .replace(R.id.user_details_fragment_container, fragment)
                    .commit();
        }
    }

    public void accountUpdateProcessFinish(ExternalAccount account) {

        TinyDB tinyDB = new TinyDB(this);
        User parent = (User) tinyDB.getObject("User", User.class);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.user_details_fragment_container);

        if (fragment == null) {
            fragment = ParentImportantInfoFragment.newInstance();
            fm.beginTransaction()
                    .add(R.id.user_details_fragment_container, fragment)
                    .commit();
        }
        else {
            fragment = ParentImportantInfoFragment.newInstance();
            fm.beginTransaction()
                    .replace(R.id.user_details_fragment_container, fragment)
                    .commit();
        }
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
            return account;
        }

        // This is called each time you call publishProgress()
        protected void onProgressUpdate(Integer... progress) {

        }

        // This is called when doInBackground() is finished
        protected void onPostExecute(Account result) {
            accountCreationProcessFinish(result);
            super.onPostExecute(result);
        }
    }

    private class UpdateParentAccountTask extends AsyncTask<Map<String, Object>, Integer, ExternalAccount> {
        // Do the long-running work in here
        protected ExternalAccount doInBackground(Map<String, Object>... params) {
            Map<String, Object> accountMap = params[0];
            User parent = (User) accountMap.get("parent");
            Token cardToken = (Token) accountMap.get("token");

            com.stripe.Stripe.apiKey = parent.getStripeApiSecretKey();

            ExternalAccount externalAccount = new ExternalAccount();

            Logger.d("AddUserDetails: ", "updateParentAccountTask doInBackground accountSecretAPIKey is " + parent.getStripeApiSecretKey());
            Logger.d("AddUserDetails: ", "updateParentAccountTask doInBackground accountPublishableAPIKey is " + parent.getStripeApiPublishableKey());
            Logger.d("AddUserDetails: ", "updateParentAccountTask doInBackground accountID is " + parent.getStripeAccountId());

            try {
                Account account = Account.retrieve(parent.getStripeAccountId(), null);

                Map<String, Object> externalAccountParams = new HashMap<String, Object>();
                externalAccountParams.put("external_account", cardToken.getId());
                externalAccountParams.put("default_for_currency", true);
                externalAccount = account.getExternalAccounts().create(externalAccountParams);

                Logger.d("AddUserDetails: ", "updateParentAccountTask doInBackground externalAccountID is " + externalAccount.getId());

            } catch (AuthenticationException e) {
                Logger.d("AddUserDetails: ", "updateParentAccountTask doInBackground AuthenticationException:" + e.getMessage());
            } catch (InvalidRequestException e) {
                Logger.d("AddUserDetails: ", "updateParentAccountTask doInBackground InvalidRequestException:" + e.getMessage());
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
}
