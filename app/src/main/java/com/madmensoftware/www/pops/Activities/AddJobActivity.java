package com.madmensoftware.www.pops.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.madmensoftware.www.pops.Models.Job;
import com.madmensoftware.www.pops.Models.User;
import com.madmensoftware.www.pops.R;

public class AddJobActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    public String uid;
    public String category;

    private EditText mJobTitleEditText, mJobDescriptionEditText, mJobDurationEditText, mJobBudgetEditText;
    private Spinner mJobCategorySpinner;
    private Button mAddJobButton;

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            auth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_job);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mJobTitleEditText = (EditText) findViewById(R.id.add_job_title);
        mJobDescriptionEditText = (EditText) findViewById(R.id.add_job_description);
        mJobDurationEditText = (EditText) findViewById(R.id.add_job_duration);
        mJobBudgetEditText = (EditText) findViewById(R.id.add_job_budget);
        mJobCategorySpinner = (Spinner) findViewById(R.id.add_job_category);
        mAddJobButton = (Button) findViewById(R.id.add_job_submit_btn);

        //Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.job_category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mJobCategorySpinner.setAdapter(adapter);

        mJobCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                category = mJobCategorySpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        auth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Log.i("Auth", "User is null");
                    startActivity(new Intent(AddJobActivity.this, LoginActivity.class));
                    finish();
                }
                else {
                    uid = user.getUid();
                }
            }
        };

        mAddJobButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.add_job_submit_btn:

                if (isEmpty(mJobBudgetEditText) || isEmpty(mJobDescriptionEditText) || isEmpty(mJobDurationEditText) || isEmpty(mJobBudgetEditText)) {
                    Toast.makeText(AddJobActivity.this, "Please fill out all fields.", Toast.LENGTH_LONG).show();
                }
                else {

                    String title = mJobTitleEditText.getText().toString();
                    String description = mJobDescriptionEditText.getText().toString();
                    int duration = Integer.parseInt(mJobDurationEditText.getText().toString());
                    double budget = Double.parseDouble(mJobBudgetEditText.getText().toString());

                    Job job = new Job();
                    job.setPosterUid(uid);
                    job.setTitle(title);
                    job.setDescription(description);
                    job.setDuration(duration);
                    job.setBudget(budget);
                    job.setStatus("Pending");
                    job.setCategory(category);

                    writeNewJob(job);

                    Toast.makeText(AddJobActivity.this, "Job Added Successfully", Toast.LENGTH_LONG).show();

                    startActivity(new Intent(AddJobActivity.this, NeighborActivity.class));
                }


                break;
            default:
                break;
        }

    }

    private void writeNewJob(Job mJob) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.child("jobs").child(firebaseUser.getUid()).setValue(mJob);
    }


    private boolean isEmpty(EditText myeditText) {
        return myeditText.getText().toString().trim().length() == 0;
    }

}
