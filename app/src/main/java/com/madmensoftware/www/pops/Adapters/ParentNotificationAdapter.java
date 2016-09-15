package com.madmensoftware.www.pops.Adapters;

import android.content.Context;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.madmensoftware.www.pops.Models.Job;
import com.madmensoftware.www.pops.Models.Notification;

/**
 * Created by carsonjones on 9/10/16.
 */
public class ParentNotificationAdapter extends FirebaseRecyclerAdapter<Job, ParentNotificationViewHolder> {

    private Context context;

    public ParentNotificationAdapter(Class<Job> modelClass, int modelLayout, Class<ParentNotificationViewHolder> viewHolderClass, Query ref, Context context) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.context = context;
    }

    @Override
    protected void populateViewHolder(ParentNotificationViewHolder viewHolder, Job model, int position) {
        viewHolder.jobTitle.setText(model.getTitle());
        viewHolder.jobDescription.setText(model.getDescription());
        viewHolder.jobBudget.setText(model.getStatus());
    }
}