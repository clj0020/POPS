package com.madmensoftware.www.pops.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.madmensoftware.www.pops.Fragments.NeighborJobsFragment;
import com.madmensoftware.www.pops.Fragments.NeighborNotificationsFragment;
import com.madmensoftware.www.pops.Helpers.NonSwipeableViewPager;
import com.madmensoftware.www.pops.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carsonjones on 8/30/16.
 */
public class NeighborActivity extends AppCompatActivity {
        public final static String EXTRA_USER_ID = "com.madmensoftware.www.pops.userId";

        private FirebaseAuth.AuthStateListener authListener;
        private FirebaseAuth auth;

        private TabLayout tabLayout;
        private NonSwipeableViewPager viewPager;

        private int[] tabIcons = {
                R.mipmap.ic_jobs,
                R.mipmap.ic_notifications
        };

        public FirebaseUser mUser;
        public String uid;

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
            setContentView(R.layout.activity_neighbor);

            Toolbar myToolbar = (Toolbar) findViewById(R.id.neighbor_toolbar);
            setSupportActionBar(myToolbar);

            viewPager = (NonSwipeableViewPager) findViewById(R.id.neighbor_viewpager);
            tabLayout = (TabLayout) findViewById(R.id.neighbor_tabs);

            auth = FirebaseAuth.getInstance();

            authListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user == null) {
                        Log.i("Auth", "User is null");
                        startActivity(new Intent(NeighborActivity.this, LoginActivity.class));
                        finish();
                    }
                    else {
                        mUser = user;
                        uid = mUser.getUid();

                        setupViewPager(viewPager, uid);
                        tabLayout.setupWithViewPager(viewPager);
                        setupTabIcons();
                    }
                }
            };

        }


        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.neighbor_toolbar, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_settings:
                    // User chose the "Settings" item, show the app settings UI...
                    return true;
                case R.id.action_log_out:
                    auth.signOut();
                    return true;
                case R.id.action_add_job:
                    startActivity(new Intent(NeighborActivity.this, AddJobActivity.class));
                default:
                    // If we got here, the user's action was not recognized.
                    // Invoke the superclass to handle it.
                    return super.onOptionsItemSelected(item);
        }
    }

        private void setupTabIcons() {
            tabLayout.getTabAt(0).setIcon(tabIcons[0]);
            tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        }

        private void setupViewPager(ViewPager viewPager, String uid) {
            ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
            adapter.addFragment(NeighborJobsFragment.newInstance(uid), "Jobs");
            adapter.addFragment(NeighborNotificationsFragment.newInstance(uid), "Notifications");

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
}
