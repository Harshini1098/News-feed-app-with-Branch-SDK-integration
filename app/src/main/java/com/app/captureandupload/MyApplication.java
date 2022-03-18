package com.app.captureandupload;

import android.app.Application;

import io.branch.referral.Branch;

public class MyApplication extends Application {

    public void onCreate() {
        super.onCreate();
        Branch.getAutoInstance(this);
        Branch.enableLogging();
    }
}
