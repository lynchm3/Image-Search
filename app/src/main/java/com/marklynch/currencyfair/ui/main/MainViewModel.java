package com.marklynch.currencyfair.ui.main;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.marklynch.currencyfair.livedata.flickr.FlickrService;
import com.marklynch.currencyfair.livedata.network.NetworkInfoLiveData;

import java.io.IOException;
import java.util.List;

public class MainViewModel extends AndroidViewModel {

    public FlickrService flickrService;
    public MutableLiveData<List<String>> photoUrls = new MutableLiveData<>();

    public MainViewModel(Application application) {
        super(application);

        flickrService = new FlickrService(application);

        //Internet Connection
        NetworkInfoLiveData networkInfoLiveData;
    }

    public void retrieveSearchResults(String query) throws IOException {
        flickrService.getPhotoUrlsFromSearchTerm(query, photoUrls);
    }

}
