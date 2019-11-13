package com.marklynch.currencyfair.ui.main;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import com.marklynch.currencyfair.livedata.network.NetworkInfoLiveData;
import com.marklynch.currencyfair.livedata.flickr.FlickrResponseLiveData;

public class MainViewModel extends AndroidViewModel {

    public FlickrResponseLiveData flickrResponseLiveDataLiveData;

    public MainViewModel(Application application) {
        super(application);

        //Weather
        flickrResponseLiveDataLiveData = new FlickrResponseLiveData(application);

        //Internet Connection
        NetworkInfoLiveData networkInfoLiveData;
    }

    public void retrieveSearchResults(String query)
    {
        flickrResponseLiveDataLiveData.retrieveSearchResults(query);
    }

}
