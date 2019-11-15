package com.marklynch.currencyfair.ui.main;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.marklynch.currencyfair.livedata.flickr.FlickrService;
import com.marklynch.currencyfair.livedata.network.NetworkInfoLiveData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

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

    public void retrieveSearchResults(String query) throws IOException {
        Timber.d("viewModel.retrieveSearchResults");
        photoUrls.setValue(new ArrayList<>());
        flickrService.getPhotoUrlsFromSearchTerm(query, photoUrls);
    }

}
