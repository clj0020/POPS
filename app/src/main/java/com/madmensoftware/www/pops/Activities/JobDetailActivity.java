package com.madmensoftware.www.pops.Activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.madmensoftware.www.pops.Fragments.JobDetailFragment;
import com.madmensoftware.www.pops.Models.Job;
import com.madmensoftware.www.pops.R;

import org.parceler.Parcels;

public class JobDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);

        //Job job = Parcels.unwrap(getIntent().getParcelableExtra("job"));
        String uid = getIntent().getStringExtra("job");

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.job_detail_fragment_container);

        if (fragment == null) {
            fragment = JobDetailFragment.newInstance(uid);
                fm.beginTransaction()
                        .add(R.id.job_detail_fragment_container, fragment)
                        .commit();
        }
        else {
            fragment = JobDetailFragment.newInstance(uid);
            fm.beginTransaction()
                    .replace(R.id.job_detail_fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(JobDetailActivity.this, MainActivity.class));
        finish();
    }

}
