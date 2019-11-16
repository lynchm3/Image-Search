package com.marklynch.currencyfair.ui.main;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.marklynch.currencyfair.io.flickr.QueryToImagesResolver;

public class MainViewModel extends AndroidViewModel implements QueryToImagesResolver.QueryResultListener {

    public MutableLiveData<ImagesToDisplay> imageToDisplayLiveData = new MutableLiveData<>(new ImagesToDisplay());
    private QueryToImagesResolver queryToImagesResolver;

    public MainViewModel(Application application) {
        super(application);

        imageToDisplayLiveData.setValue(new ImagesToDisplay());

        queryToImagesResolver = new QueryToImagesResolver(application);
    }

    public void retrieveSearchResults(String query, int page, boolean newSearch) {
        if (newSearch)
            imageToDisplayLiveData.setValue(new ImagesToDisplay());
        queryToImagesResolver.getPhotoUrlsFromSearchTerm(query, this, page, getApplication());
    }

    @Override
    public void onNewImages(ImagesToDisplay newImages) {

        ImagesToDisplay currentImages = imageToDisplayLiveData.getValue();

        ImagesToDisplay concatenatedImages = new ImagesToDisplay();
        concatenatedImages.errorMessage = newImages.errorMessage;
        concatenatedImages.images.addAll(currentImages.images);
        concatenatedImages.images.addAll(newImages.images);

        imageToDisplayLiveData.setValue(concatenatedImages);
    }
}