package com.marklynch.currencyfair.ui.main;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.marklynch.currencyfair.livedata.flickr.FlickrService;
import com.marklynch.currencyfair.livedata.network.NetworkInfoLiveData;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends AndroidViewModel {

    public FlickrService flickrService;
    public MutableLiveData<List<String>> photoUrls = new MutableLiveData<>(new ArrayList<>());

    public MainViewModel(Application application) {
        super(application);

        photoUrls.setValue(new ArrayList<>());

        flickrService = new FlickrService(application);

        //Internet Connection
        NetworkInfoLiveData networkInfoLiveData;
    }

    public void retrieveSearchResults(String query, int page, boolean newSearch) {
        if (newSearch)
            photoUrls.setValue(new ArrayList<>());
        flickrService.getPhotoUrlsFromSearchTerm(query, photoUrls, page, getApplication());
    }

}
