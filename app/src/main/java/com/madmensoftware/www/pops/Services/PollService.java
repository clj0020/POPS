package com.madmensoftware.www.pops.Services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;

import com.madmensoftware.www.pops.Activities.MainActivity;
import com.madmensoftware.www.pops.R;
import com.orhanobut.logger.Logger;

/**
 * Created by carson on 11/29/2016.
 */

public class PollService extends IntentService {
    private static final String TAG = "PollService";

    // TODO: Change Poll Interval, currently every 60 seconds which is way too often.
    private static final int POLL_INTERVAL = 1000 * 60; // Poll every 60 seconds

    public static Intent newIntent(Context context) {
        return new Intent(context, PollService.class);
    }

    public PollService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!isNetworkAvailableAndConnected()) {
            return;
        }

        // Send a notification
        Resources resources = getResources();
        Intent intent2 = MainActivity.newIntent(this);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent2, 0);

        // TODO: UnHardCode the Notification Content
        Notification notification = new NotificationCompat.Builder(this)
                .setTicker("POPS: You have a new notification from POPS.")
                .setSmallIcon(R.drawable.pops_logo)
                .setContentTitle("POPS: You have a new notification from POPS.")
                .setContentText("Check your notification now.")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

//        Notification notification = new NotificationCompat.Builder(this)
//                .setTicker(resources.getString(R.string.notification_title))
//                .setSmallIcon(android.R.drawable.ic_menu_agenda)
//                .setContentTitle(resources.getString(R.string.notification_title))
//                .setContentText(resources.getString(R.string.notification_text))
//                .setContentIntent(pendingIntent)
//                .setAutoCancel(true)
//                .build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(0, notification);

                                                                                              Logger.i("Recieved an intent: " + intent);
    }

    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent intent = PollService.newIntent(context);

        // Create a pending intent which basically says "I wish to start PollService".
        // Takes three parameters:
        // 1. Context with which to send the intent
        // 2. a request code that you can use to distinguish this PendingIntent from others
        // 3. the intent object to send
        // 4. a set of flags that we could use to tweak how the PendingIntent is created.
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        // AlarmManager is a system service that can send Intents for you in the background.
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);


        if (isOn) {
            // To set the alarm
            // Takes four parameters
            // 1. A constant to describe the time basis for the alarm
            // 2. the time at which to start the alarm
            // 3. the time interval at which to repeat the alarm
            // 4. a PendingIntent to fire when the alarm goes off
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(), POLL_INTERVAL, pendingIntent);
        }
        else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    // Tells you if the service alarm is already running
    public static boolean isServiceAlarmOn(Context context) {
        Intent intent = PollService.newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE);
        return pendingIntent != null;
    }

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        boolean isNetworkAvailable = connectivityManager.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable && connectivityManager.getActiveNetworkInfo().isConnected();

        return isNetworkConnected;
    }
}
