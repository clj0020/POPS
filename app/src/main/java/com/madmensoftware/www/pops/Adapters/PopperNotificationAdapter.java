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
public class PopperNotificationAdapter extends FirebaseRecyclerAdapter<Notification, PopperNotificationViewHolder> {

    private Context context;

    public PopperNotificationAdapter(Class<Notification> modelClass, int modelLayout, Class<PopperNotificationViewHolder> viewHolderClass, Query ref, Context context) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.context = context;
    }

    @Override
    protected void populateViewHolder(PopperNotificationViewHolder viewHolder, Notification model, int position) {
        viewHolder.jobTitle.setText(model.getName());
        viewHolder.jobDescription.setText(model.getDescription());
    }
}