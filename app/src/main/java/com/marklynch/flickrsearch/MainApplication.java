package com.marklynch.flickrsearch;

import android.app.Application;

import com.marklynch.flickrsearch.logging.TimberDebugTree;

import timber.log.Timber;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new TimberDebugTree());
    }
}