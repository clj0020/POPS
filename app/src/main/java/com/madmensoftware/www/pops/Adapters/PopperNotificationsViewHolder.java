package com.madmensoftware.www.pops.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.madmensoftware.www.pops.Activities.JobDetailActivity;
import com.madmensoftware.www.pops.Models.Notification;
import com.madmensoftware.www.pops.R;
import com.orhanobut.logger.Logger;

import org.parceler.Parcels;

import java.util.ArrayList;

/**
 * Created by carson on 11/14/2016.
 */

public class PopperNotificationsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView notificationTitle;

    View mView;
    Context mContext;

    public PopperNotificationsViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
        notificationTitle = (TextView) itemView.findViewById(R.id.notification_list_title);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        final ArrayList<Notification> notifications = new ArrayList<>();
        Query notificationQuery = FirebaseDatabase.getInstance().getReference().child("notifications").orderByChild("recieverUid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        notificationQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Notification notification = snapshot.getValue(Notification.class);
                    notifications.add(notification);
                    Logger.i("PopperNotificationViewHolder: notification added to notifications. UID is " + notification.getUid());
                }

                int itemPosition = getLayoutPosition();

                Logger.i("NeighborNotificationViewHolder: There are " + notifications.size() + " notifications.");
                Logger.i("NeighborNotificationViewHolder: itemPosition is " + itemPosition);



                if (notifications.get(itemPosition).getType() == "Job") {
                    Intent intent = new Intent(mContext, JobDetailActivity.class);
                    intent.putExtra("position", itemPosition + "");
                    intent.putExtra("job", Parcels.wrap(notifications.get(itemPosition).getJob()));
                    mContext.startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}

