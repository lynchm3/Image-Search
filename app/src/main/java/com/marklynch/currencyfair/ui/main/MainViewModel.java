package com.marklynch.currencyfair.ui.main;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import com.marklynch.currencyfair.livedata.network.NetworkInfoLiveData;
import com.marklynch.currencyfair.livedata.weather.FlickrResponseLiveData;

public class MainViewModel extends AndroidViewModel {

    public FlickrResponseLiveData flickrResponseLiveDataLiveData;

    public MainViewModel(Application application) {
        super(application);

        //Weather
        flickrResponseLiveDataLiveData = new FlickrResponseLiveData(application);

        //Internet Connection
        NetworkInfoLiveData networkInfoLiveData;
    }

}
