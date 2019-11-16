package com.marklynch.currencyfair.ui.main;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.marklynch.currencyfair.io.flickr.FlickrInterface;
import com.marklynch.currencyfair.network.NetworkInfoLiveData;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends AndroidViewModel {

    public FlickrInterface flickrInterface;
    public MutableLiveData<List<ImageToDisplay>> photoUrls = new MutableLiveData<>(new ArrayList<>());

    public MainViewModel(Application application) {
        super(application);

        photoUrls.setValue(new ArrayList<>());

        flickrInterface = new FlickrInterface(application);

        //Internet Connection
        NetworkInfoLiveData networkInfoLiveData;
    }

    public void retrieveSearchResults(String query, int page, boolean newSearch) {
        if (newSearch)
            photoUrls.setValue(new ArrayList<>());
        flickrInterface.getPhotoUrlsFromSearchTerm(query, photoUrls, page, getApplication());
    }

}
