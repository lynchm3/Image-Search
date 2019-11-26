package com.marklynch.flickrsearch.io.flickr;

import android.app.Application;

import com.marklynch.flickrsearch.R;
import com.marklynch.flickrsearch.io.flickr.response.FlickrGetSizesResponse;
import com.marklynch.flickrsearch.io.flickr.response.FlickrSearchResponse;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class QueryToImageSizesResolver {

    private final FlickrServer flickrServer;

    public QueryToImageSizesResolver(Application application) {
        flickrServer = new FlickrServer(application);
    }

    public QueryToImageSizesResolver(MockWebServer mockWebServer) {
        flickrServer = new FlickrServer(mockWebServer);
    }

    public interface QueryResultListener {
        void allImageSizesDownloaded(Vector<FlickrGetSizesResponse.ImageSizes> imageSizesList);

        void singleImageSizesDownloaded(FlickrGetSizesResponse.ImageSizes imageSizes);

        void onError(int errorMessage);

        void searchDownloadedExperimental(FlickrSearchResponse flickrSearchResponse);
    }

    public void getPhotoUrlsFromSearchTerm(final String query, QueryResultListener listener, int page) {

        final AtomicInteger responseCounter = new AtomicInteger(0);
        final AtomicInteger totalImageCount = new AtomicInteger(0);
        final Vector<FlickrGetSizesResponse.ImageSizes> imageSizes = new Vector<>();

        Callback<FlickrGetSizesResponse> getSizesRequestCallback = new Callback<FlickrGetSizesResponse>() {
            @Override
            public void onResponse(@NotNull Call<FlickrGetSizesResponse> call, Response<FlickrGetSizesResponse> response) {

                if (!response.isSuccessful() || response.body() == null || response.body().imageSizes == null) {

                } else {
                    listener.singleImageSizesDownloaded(response.body().imageSizes);
                    imageSizes.add(response.body().imageSizes);
                }

                //Notify listener if we have received all pages
                if (responseCounter.incrementAndGet() == totalImageCount.get()) {
                    if (imageSizes.size() == 0)
                        listener.onError(R.string.no_images_found);
                    else
                        listener.allImageSizesDownloaded(imageSizes);
                }
            }

            @Override
            public void onFailure(@NotNull Call<FlickrGetSizesResponse> call, @NotNull Throwable t) {
                //Post to livedata callback if we have getSize responses for whole page
                if (responseCounter.incrementAndGet() == totalImageCount.get()) {
                    if (imageSizes.size() == 0)
                        listener.onError(R.string.no_images_found);
                    else
                        listener.allImageSizesDownloaded(imageSizes);
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
                        flickrServer.getSizesRequestAsync(photo, getSizesRequestCallback);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<FlickrSearchResponse> call, @NotNull Throwable t) {
                listener.onError(R.string.error_loading_images);
            }
        };

        flickrServer.searchRequestAsync(query, page, searchRequestCallback);
    }

    public void getPhotoUrlsFromSearchTermZipMethod(final String query, int page, QueryResultListener listener) {
        Timber.d("getPhotoUrlsFromSearchTermZipMethod()");

        flickrServer.searchRequestObservable(query, page)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribeWith(

                new Observer<FlickrSearchResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Timber.d("getPhotoUrlsFromSearchTermZipMethod() onSubscribe");
                    }

                    @Override
                    public void onNext(FlickrSearchResponse flickrSearchResponse) {
                        Timber.d("getPhotoUrlsFromSearchTermZipMethod() onNext flickrSearchResponse = " + flickrSearchResponse);
                        ArrayList<Observable<FlickrGetSizesResponse>> getSizesObservables = new ArrayList<>();
                        for (FlickrSearchResponse.Photo photo : flickrSearchResponse.photos.photo) {
                            getSizesObservables.add(flickrServer.getSizesRequestSync(photo.id));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.d("getPhotoUrlsFromSearchTermZipMethod() onError");
                        e.printStackTrace();

                    }

                    @Override
                    public void onComplete() {
                        Timber.d("getPhotoUrlsFromSearchTermZipMethod() onComplete");

                    }
                });


    }


//    public Single<FlickrSearchResponse> getFlickrSearchResponseObservable(final String query, final int page) {
//        return Single.create(emitter -> {
//            try {
//                emitter.onSuccess(flickrServer.searchRequestObservable(query, page).body());//ON SUCCESS TO SUBSCRIBER
//            } catch (Exception e) {
//                emitter.onError(e);//ON ERROR TO SUBSCRIBER
//            }
//        });
//    }


//    public Single<FlickrGetSizesResponse> getFlickrGetSizesResponseObservable(final String photoId) {
//        return Single.create(emitter -> {
//            try {
//                emitter.onSuccess(flickrServer.getSizesRequestSync(photoId).body());//ON SUCCESS TO SUBSCRIBER
//            } catch (Exception e) {
//                emitter.onError(e);//ON ERROR TO SUBSCRIBER
//            }
//        });
//    }

//    public void getPhotoUrlsFromSearchTermExperimental(final String query, QueryResultListener listener, int page) {
//
//        final AtomicInteger responseCounter = new AtomicInteger(0);
//        final AtomicInteger totalImageCount = new AtomicInteger(0);
//        final Vector<FlickrGetSizesResponse.ImageSizes> imageSizes = new Vector<>();
//        Callback<FlickrSearchResponse> searchRequestCallback = new Callback<FlickrSearchResponse>() {
//            @Override
//            public void onResponse(@NotNull Call<FlickrSearchResponse> call, Response<FlickrSearchResponse> response) {
//                if (!response.isSuccessful() || response.body() == null) {
//                    listener.onError(R.string.error_loading_images);
//                } else if (response.body().photos.photo.size() == 0) {
//                    listener.onError(R.string.no_images_found);
//                } else {
//                    totalImageCount.set(response.body().photos.photo.size());
//                    ArrayList<String> imageURLs = new ArrayList<>();
//                    listener.searchDownloadedExperimental(response.body());
//                }
//            }
//
//            @Override
//            public void onFailure(@NotNull Call<FlickrSearchResponse> call, @NotNull Throwable t) {
//                listener.onError(R.string.error_loading_images);
//            }
//        };
//
//        flickrServer.searchRequestAsync(query, page, searchRequestCallback);
//    }
}
