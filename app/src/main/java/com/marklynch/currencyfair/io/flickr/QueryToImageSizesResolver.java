package com.marklynch.currencyfair.io.flickr;

import android.app.Application;

import com.marklynch.currencyfair.R;
import com.marklynch.currencyfair.io.flickr.response.FlickrGetSizesResponse;
import com.marklynch.currencyfair.io.flickr.response.FlickrSearchResponse;

import org.jetbrains.annotations.NotNull;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QueryToImageSizesResolver {

    private FlickrServer flickrServer;

    public QueryToImageSizesResolver(Application application) {
        flickrServer = new FlickrServer(application);
    }

    public interface QueryResultListener {
        void allImageSizesDownload(Vector<FlickrGetSizesResponse.ImageSizes> imageSizesList);
        void singleImageSizesDownloaded(FlickrGetSizesResponse.ImageSizes imageSizes);
        void onError(int errorMessage);
    }

    public void getPhotoUrlsFromSearchTerm(final String query, QueryResultListener listener, int page) {

        final AtomicInteger responseCounter = new AtomicInteger(0);
        final AtomicInteger totalImageCount = new AtomicInteger(0);
        final Vector<FlickrGetSizesResponse.ImageSizes> imageSizes = new Vector<>();

        Callback<FlickrGetSizesResponse> getSizesRequestCallback = new Callback<FlickrGetSizesResponse>() {
            @Override
            public void onResponse(@NotNull Call<FlickrGetSizesResponse> call, Response<FlickrGetSizesResponse> response) {

                if (!response.isSuccessful() || response.body() == null) {

                } else {
                    imageSizes.add(response.body().imageSizes);
                }

                //Notify listener if we have received all pages
                if (responseCounter.incrementAndGet() == totalImageCount.get()) {
                    if (imageSizes.size() == 0)
                        listener.onError(R.string.no_images_found);
                    else
                        listener.allImageSizesDownload(imageSizes);
                }
            }

            @Override
            public void onFailure(@NotNull Call<FlickrGetSizesResponse> call, @NotNull Throwable t) {
                //Post to livedata callback if we have getSize responses for whole page
                if (responseCounter.incrementAndGet() == totalImageCount.get()) {
                    if (imageSizes.size() == 0)
                        listener.onError(R.string.no_images_found);
                    else
                        listener.allImageSizesDownload(imageSizes);
                }
            }
        };

        Callback<FlickrSearchResponse> searchRequestCallback = new Callback<FlickrSearchResponse>() {
            @Override
            public void onResponse(@NotNull Call<FlickrSearchResponse> call, Response<FlickrSearchResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    listener.onError(R.string.error_loading_images);
                } else if (response.body().photos.photo.size() == 0) {
                    listener.onError(R.string.no_images_found);
                } else {
                    totalImageCount.set(response.body().photos.photo.size());
                    for (FlickrSearchResponse.Photo photo : response.body().photos.photo) {
                        flickrServer.getSizesRequest(photo, getSizesRequestCallback);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<FlickrSearchResponse> call, @NotNull Throwable t) {
                listener.onError(R.string.error_loading_images);
            }
        };

        flickrServer.searchRequest(query, page, searchRequestCallback);
    }
}
