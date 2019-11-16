package com.marklynch.currencyfair.ui.main;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.marklynch.currencyfair.io.flickr.FlickrInterface;
import com.marklynch.currencyfair.io.flickr.QueryToImagesResolver;
import com.marklynch.currencyfair.io.flickr.response.FlickrGetSizesResponse;
import com.marklynch.currencyfair.io.flickr.response.FlickrSearchResponse;
import com.marklynch.currencyfair.network.NetworkInfoLiveData;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends AndroidViewModel implements QueryToImagesResolver.QueryResultListener {

    public MutableLiveData<ImagesToDisplay> imageToDisplayLiveData = new MutableLiveData<>(new ImagesToDisplay());

    public QueryToImagesResolver queryToImagesResolver;

    public MainViewModel(Application application) {
        super(application);

        imageToDisplayLiveData.setValue(new ImagesToDisplay());

        queryToImagesResolver = new QueryToImagesResolver(application);

        NetworkInfoLiveData networkInfoLiveData;
    }

    public void retrieveSearchResults(String query, int page, boolean newSearch) {
        if (newSearch)
            imageToDisplayLiveData.setValue(new ImagesToDisplay());
        queryToImagesResolver.getPhotoUrlsFromSearchTerm(query, this, page, getApplication());
    }

    @Override
    public void onNewImages(ImagesToDisplay newImages) {

        ImagesToDisplay currentImages = imageToDisplayLiveData.getValue();
        currentImages.images.addAll(newImages.images);

        ImagesToDisplay concatenatedImages = new ImagesToDisplay();
        concatenatedImages.errorMessage = newImages.errorMessage;
        concatenatedImages.images.addAll(currentImages.images);
        concatenatedImages.images.addAll(newImages.images);

        imageToDisplayLiveData.postValue(concatenatedImages);
    }
}
