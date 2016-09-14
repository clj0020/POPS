package com.madmensoftware.www.pops.Activities;

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

        Job job = Parcels.unwrap(getIntent().getParcelableExtra("job"));

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.job_detail_fragment_container);

        if (fragment == null) {
            fragment = JobDetailFragment.newInstance(job);
                fm.beginTransaction()
                        .add(R.id.job_detail_fragment_container, fragment)
                        .commit();
        }
    }

}
