package com.marklynch.currencyfair.ui.main;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import com.marklynch.currencyfair.livedata.network.NetworkInfoLiveData;
import com.marklynch.currencyfair.livedata.flickr.FlickrService;

import java.io.IOException;

public class MainViewModel extends AndroidViewModel {

    public FlickrService flickrService;

    public MainViewModel(Application application) {
        super(application);

        //Weather
        flickrService = new FlickrService(application);

        //Internet Connection
        NetworkInfoLiveData networkInfoLiveData;
    }

    public void retrieveSearchResults(String query) throws IOException {
        flickrService.getPhotosForSearchTerm(query);
    }

}
