package com.marklynch.currencyfair.ui.main;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.marklynch.currencyfair.io.flickr.FlickrInterface;
import com.marklynch.currencyfair.network.NetworkInfoLiveData;

public class MainViewModel extends AndroidViewModel {

    public FlickrInterface flickrInterface;
    public MutableLiveData<ImagesToDisplay> imageToDisplayLiveData = new MutableLiveData<>(new ImagesToDisplay());

    public MainViewModel(Application application) {
        super(application);

        imageToDisplayLiveData.setValue(new ImagesToDisplay());

        flickrInterface = new FlickrInterface(application);

        //Internet Connection
        NetworkInfoLiveData networkInfoLiveData;
    }

    public void retrieveSearchResults(String query, int page, boolean newSearch) {
        if (newSearch)
            imageToDisplayLiveData.setValue(new ImagesToDisplay());
        flickrInterface.getPhotoUrlsFromSearchTerm(query, imageToDisplayLiveData, page, getApplication());
    }

}
