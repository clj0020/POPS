package com.madmensoftware.www.pops.Adapters;

import android.content.Context;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.madmensoftware.www.pops.Models.Job;
import com.madmensoftware.www.pops.Models.Notification;

/**
 * Created by carson on 11/14/2016.
 */

public class NeighborNotificationsAdapter extends FirebaseRecyclerAdapter<Notification, NeighborNotificationsViewHolder> {

    private Context context;

    public NeighborNotificationsAdapter(Class<Notification> modelClass, int modelLayout, Class<NeighborNotificationsViewHolder> viewHolderClass, Query ref, Context context) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.context = context;
    }

    @Override
    protected void populateViewHolder(NeighborNotificationsViewHolder viewHolder, Notification model, int position) {
        viewHolder.notificationTitle.setText(model.getTitle());

//        viewHolder.jobPosterName.setText(model.getPosterName());
//        viewHolder.jobDescription.setText(model.getDescription());

//        if(model.getStatus().equals("active")) {
//            viewHolder.jobBudget.setText(model.getBudget() + "");
//        }
//        else {
//            viewHolder.jobBudget.setText(model.getStatus());
//        }
    }
}