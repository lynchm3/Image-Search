package com.marklynch.currencyfair.io.flickr;

import android.app.Application;

import com.bumptech.glide.Glide;
import com.marklynch.currencyfair.R;
import com.marklynch.currencyfair.io.flickr.response.FlickrGetSizesResponse;
import com.marklynch.currencyfair.io.flickr.response.FlickrSearchResponse;
import com.marklynch.currencyfair.ui.main.ImageToDisplay;
import com.marklynch.currencyfair.ui.main.ImagesToDisplay;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QueryToImagesResolver {

    private FlickrServer flickrServer;

    public QueryToImagesResolver(Application application) {
        flickrServer = new FlickrServer(application);
    }

    public interface QueryResultListener {
        void onNewImages(ImagesToDisplay newImages);
    }

    public void getPhotoUrlsFromSearchTerm(final String query, QueryResultListener listener, int page, Application application) {

        final AtomicInteger responseCounter = new AtomicInteger(0);
        final AtomicInteger totalImageCount = new AtomicInteger(0);
        final ImagesToDisplay imagesToDisplay = new ImagesToDisplay();

        Callback<FlickrGetSizesResponse> getSizesRequestCallback = new Callback<FlickrGetSizesResponse>() {
            @Override
            public void onResponse(@NotNull Call<FlickrGetSizesResponse> call, Response<FlickrGetSizesResponse> response) {

                ImageToDisplay imageToDisplay = getImageToDisplay(response.body());

                if (imageToDisplay != null && imageToDisplay.thumb.source != null) {
                    //Start download of thumb image
                    Glide.with(application).load(imageToDisplay.thumb.source).submit();
                    //Add image to our list
                    imagesToDisplay.images.add(imageToDisplay);
                }

                //Notify listener if we have received all pages
                if (responseCounter.incrementAndGet() % FlickrServer.PER_PAGE == 0) {
                    if (imagesToDisplay.images.size() == 0)
                        imagesToDisplay.errorMessage = R.string.error_loading_images;
                    listener.onNewImages(imagesToDisplay);
                }
            }

            @Override
            public void onFailure(@NotNull Call<FlickrGetSizesResponse> call, @NotNull Throwable t) {
                //Post to livedata callback if we have getSize responses for whole page
                if (responseCounter.addAndGet(1) == totalImageCount.get()) {
                    if (imagesToDisplay.images.size() == 0)
                        imagesToDisplay.errorMessage = R.string.error_loading_images;
                    listener.onNewImages(imagesToDisplay);
                }
            }
        };

        Callback<FlickrSearchResponse> searchRequestCallback = new Callback<FlickrSearchResponse>() {
            @Override
            public void onResponse(@NotNull Call<FlickrSearchResponse> call, Response<FlickrSearchResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    imagesToDisplay.errorMessage = R.string.error_loading_images;
                    listener.onNewImages(imagesToDisplay);
                } else if (response.body().photos.photo.size() == 0) {
                    imagesToDisplay.errorMessage = R.string.no_images_found;
                    listener.onNewImages(imagesToDisplay);
                } else {
                    totalImageCount.set(response.body().photos.photo.size());
                    for (FlickrSearchResponse.Photo photo : response.body().photos.photo) {
                        flickrServer.getSizesRequest(photo, getSizesRequestCallback);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<FlickrSearchResponse> call, @NotNull Throwable t) {
                imagesToDisplay.errorMessage = R.string.error_loading_images;
                listener.onNewImages(imagesToDisplay);
            }
        };

        flickrServer.searchRequest(query, page, searchRequestCallback);
    }

    private ImageToDisplay getImageToDisplay(FlickrGetSizesResponse flickrGetSizesResponse) {
        if (flickrGetSizesResponse == null || flickrGetSizesResponse.sizes == null || flickrGetSizesResponse.sizes.imageSize == null)
            return null;

        ImageToDisplay imageToDisplay = new ImageToDisplay();
        List<FlickrGetSizesResponse.ImageSize> sizesFromResponse = flickrGetSizesResponse.sizes.imageSize;
        for (FlickrGetSizesResponse.ImageSize imageSizeFromResponse : sizesFromResponse) {
            if ("Large Square".equals(imageSizeFromResponse.label)) {
                imageToDisplay.thumb = imageSizeFromResponse;
            } else if ("Large".equals(imageSizeFromResponse.label)) {
                imageToDisplay.large = imageSizeFromResponse;
            }
        }

        if (imageToDisplay.thumb == null)
            return null;

        if (imageToDisplay.large == null)
            imageToDisplay.large = imageToDisplay.thumb;

        return imageToDisplay;
    }
}
