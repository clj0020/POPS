package com.madmensoftware.www.pops.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.madmensoftware.www.pops.Fragments.NeighborJobsFragment;
import com.madmensoftware.www.pops.Fragments.NeighborNotificationsFragment;
import com.madmensoftware.www.pops.Fragments.ParentCheckInFragment;
import com.madmensoftware.www.pops.Helpers.NonSwipeableViewPager;
import com.madmensoftware.www.pops.Helpers.TinyDB;
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
import com.stripe.model.Customer;

import org.parceler.Parcels;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by carsonjones on 8/30/16.
 */
public class NeighborActivity extends AppCompatActivity implements ParentCheckInFragment.ParentCheckInCallbacks {
        public final static String EXTRA_USER_ID = "com.madmensoftware.www.pops.userId";
        private static final String PUBLISHIBLE_TEST_KEY = "pk_test_9SdGQF1ZibEEnbJ3vYmBaAFj";
        private static final String SECRET_TEST_KEY = "sk_test_I9UFP4mZBd3kq6W7w9zDenGq";
    
        @BindView(R.id.neighbor_tabs) TabLayout tabLayout;
        @BindView(R.id.neighbor_viewpager) NonSwipeableViewPager viewPager;
        @BindView(R.id.neighbor_toolbar) Toolbar myToolbar;  
    
        private FirebaseAuth.AuthStateListener authListener;
        private FirebaseAuth auth;
        private DatabaseReference mDatabase;
        public FirebaseUser mFirebaseUser;
        private CallbackManager mCallbackManager;
        private User user;
        private GeoFire geofire;
    public String uid;
        private int[] tabIcons = {
                R.mipmap.ic_jobs,
                R.mipmap.ic_notifications,
                R.mipmap.ic_check_in

        };

        @Override
        public void onStart() {
            super.onStart();
            auth.addAuthStateListener(authListener);
        }

        @Override
        public void onStop() {
            super.onStop();
            if (authListener != null) {
                auth.removeAuthStateListener(authListener);
            }
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            FacebookSdk.sdkInitialize(getApplicationContext());
            setContentView(R.layout.activity_neighbor);
            ButterKnife.bind(this);
            mCallbackManager = CallbackManager.Factory.create();
            
            setSupportActionBar(myToolbar);
            
            mDatabase = FirebaseDatabase.getInstance().getReference();

            Bundle b = this.getIntent().getExtras();
            if (b != null) {
                user = Parcels.unwrap(b.getParcelable("User"));
            }

            auth = FirebaseAuth.getInstance();
            authListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser == null) {
                        Logger.d("Auth", "User is null");
                        startActivity(new Intent(NeighborActivity.this, LoginActivity.class));
                        finish();
                    }
                    else {
                        mFirebaseUser = firebaseUser;
                        uid = mFirebaseUser.getUid();

                        setupViewPager(viewPager, uid, user);
                        tabLayout.setupWithViewPager(viewPager);
                        setupTabIcons(user);

                        // Write a message to the database
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        final DatabaseReference ref = database.getReference("users/" + uid);
                        // Read from the database
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getValue() != null) {
                                    User mUser = dataSnapshot.getValue(User.class);
                                    ref.child("paymentAdded").setValue(true);
                                    user = mUser;
                                }
                                else {
                                    auth.signOut();
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError error) {

                            }
                        });
                    }
                }
            };

            FirebaseMessaging.getInstance().subscribeToTopic("user_"+ auth.getCurrentUser().getUid());
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.neighbor_toolbar, menu);
            return true;
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_settings:
                    // User chose the "Settings" item, show the app settings UI...
                    return true;
                case R.id.action_log_out:
                    auth.signOut();
                    LoginManager.getInstance().logOut();
                    return true;
                case R.id.action_add_job:
                    TinyDB tinyDB = new TinyDB(getApplicationContext());
                    User user = (User) tinyDB.getObject("User", User.class);
                    Logger.d("paymentAdded", "paymentAdded is " + user.getPaymentAdded());
                    //Logger.d("paymentAdded", "paymentAdded is " + mFirebaseUser.getPaymentAdded());

                    if(!user.getPaymentAdded()) {
                        CharSequence accept = "Add";
                        CharSequence reject = "Later";
                        CharSequence message = "To post jobs you have to finish signing up. Add your Payment information to continue.";

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                        alertDialogBuilder.setMessage(message);
                        alertDialogBuilder.setPositiveButton(accept,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        startActivity(new Intent(NeighborActivity.this, AddPaymentInformationActivity.class));
                                    }
                                });

                        alertDialogBuilder.setNegativeButton(reject, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();

                    }
                    else {
                        Intent neighborIntent = new Intent(NeighborActivity.this, AddJobActivity.class);
                        Bundle neighborBundle = new Bundle();
                        neighborBundle.putParcelable("User", Parcels.wrap(user));
                        neighborIntent.putExtras(neighborBundle);
                        startActivity(neighborIntent);
                        return true;
                    }
                case R.id.action_test_purchase:
                    tinyDB = new TinyDB(getApplicationContext());
                    User neighbor = (User) tinyDB.getObject("User", User.class);

                    mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                User neighbor = dataSnapshot.getValue(User.class);
                                new CreateChargeTask().execute(neighbor);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                        }

                    });
                    return true;
                default:
                    // If we got here, the user's action was not recognized.
                    // Invoke the superclass to handle it.
                    return super.onOptionsItemSelected(item);
            }
        }

        private void setupTabIcons(User user) {
            if(user.getParentUid() == null) {
                tabLayout.getTabAt(0).setIcon(tabIcons[0]);
                tabLayout.getTabAt(1).setIcon(tabIcons[1]);
            }
            else {
                tabLayout.getTabAt(2).setIcon(tabIcons[2]);
            }

        }

        private void setupViewPager(ViewPager viewPager, String uid, User user) {
            ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
            adapter.addFragment(NeighborJobsFragment.newInstance(uid), "Jobs");
            adapter.addFragment(NeighborNotificationsFragment.newInstance(uid), "Notifications");

            if(user.getParentUid() != null) {
                adapter.addFragment(ParentCheckInFragment.newInstance(), "Check In");
            }
            viewPager.setAdapter(adapter);
        }

        class ViewPagerAdapter extends FragmentPagerAdapter {
            private final List<Fragment> mFragmentList = new ArrayList<>();
            private final List<String> mFragmentTitleList = new ArrayList<>();

            public ViewPagerAdapter(FragmentManager manager) {
                super(manager);
            }

            @Override
            public Fragment getItem(int position) {
                return mFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return mFragmentList.size();
            }

            public void addFragment(Fragment fragment, String title) {
                mFragmentList.add(fragment);
                mFragmentTitleList.add(title);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mFragmentTitleList.get(position);
            }
        }

        private class CreateChargeTask extends AsyncTask<User, Integer, String> {
        // Do the long-running work in here
        protected String doInBackground(User... params) {
            User user = params[0];
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

            Stripe.apiKey = "sk_test_I9UFP4mZBd3kq6W7w9zDenGq";
            // Create a charge: this will charge the user's card
            try {
                Map<String, Object> chargeParams = new HashMap<String, Object>();
                chargeParams.put("amount", 1000); // Amount in cents
                chargeParams.put("currency", "usd");
                chargeParams.put("customer", user.getStripeCustomerId());
                chargeParams.put("description", "Example charge");
                chargeParams.put("destination", "acct_18u1uFHfcGmL46Ma");
                chargeParams.put("application_fee", 250);

                Charge charge = Charge.create(chargeParams);
            } catch (CardException e) {
                // The card has been declined
            } catch (APIException e) {
                e.printStackTrace();
            } catch (InvalidRequestException e) {
                e.printStackTrace();
            } catch (APIConnectionException e) {
                e.printStackTrace();
            } catch (AuthenticationException e) {
                e.printStackTrace();
            }

            return "";
        }

        // This is called each time you call publishProgress()
        protected void onProgressUpdate(Integer... progress) {

        }

        // This is called when doInBackground() is finished
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }
    @Override
    public void onCheckIn(double latitude, double longitude) {
        String uid = auth.getCurrentUser().getUid();
        Date currentDate = Calendar.getInstance().getTime();
        long currentTime = currentDate.getTime();

        Logger.d(currentTime);

        String checkInId = mDatabase.child("users").child(uid).child("check-in").push().getKey();
        mDatabase.child("users").child(uid).child("check-in").child(checkInId).setValue(currentTime);

        geofire = new GeoFire(mDatabase.child("check_in_location"));
        geofire.setLocation(checkInId, new GeoLocation(latitude, longitude));

        Toast.makeText(this, "Successfully Checked In!", Toast.LENGTH_LONG).show();
    }
}
