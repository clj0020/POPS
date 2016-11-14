package com.madmensoftware.www.pops;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import android.content.Context;
import android.os.StrictMode;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.GINGERBREAD;

/**
 * Created by carsonjones on 9/15/16.
 */
public class POPSApplication extends Application {

    @Override public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
//        enabledStrictMode();
    }



//
//    private void enabledStrictMode() {
//        if (SDK_INT >= GINGERBREAD) {
//            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder() //
//                    .detectAll() //
//                    .penaltyLog() //
//                    .penaltyDeath() //
//                    .build());
//        }
//    }
}