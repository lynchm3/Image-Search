package com.marklynch.currencyfair;

import android.app.Application;

import com.marklynch.currencyfair.logging.TimberDebugTree;

import timber.log.Timber;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new TimberDebugTree());
    }
}