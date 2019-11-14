package com.marklynch.currencyfair.livedata.flickr;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.marklynch.currencyfair.livedata.flickr.data.FlickrGetSizesResponse;
import com.marklynch.currencyfair.livedata.flickr.data.FlickrSearchResponse;
import com.marklynch.currencyfair.livedata.flickr.data.Photo;
import com.marklynch.currencyfair.livedata.flickr.data.Size;
import com.readystatesoftware.chuck.ChuckInterceptor;

import org.reactivestreams.Subscriber;

import java.io.IOException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import timber.log.Timber;

public class FlickrService {

    private Retrofit retrofit;
    private FlickrSearchService apiService;

    private static final String METHOD_KEY = "method";
    private static final String SEARCH_METHOD_VALUE = "flickr.photos.search";
    private static final String GET_SIZES_METHOD_VALUE = "flickr.photos.getSizes";
    private static final String API_KEY = "api_key";
    private static final String API_KEY_VALUE = "297af75d9d68977b69513409fc928ca8";
    private static final String TAGS_KEY = "tags";
    private static final String PAGE_KEY = "page";
    private static final String FORMAT_KEY = "format";
    private static final String FORMAT_JSON = "json";
    private static final String NO_JSON_CALLBACK_KEY = "nojsoncallback";
    private static final int NO_JSON_CALLBACK = 1;
    private static final String PER_PAGE_KEY = "per_page";
    private static final int PER_PAGE_VALUE = 20;
    private static final String PHOTO_ID_KEY = "photo_id";

    private static final String BASE_URL = "https://api.flickr.com";
    private static final String REST_API = "/services/rest";
    private static final String baseSearchUrl = "https://api.flickr.com/services/rest/?method=flickr.photos.search";
    private static final String baseSizesUrl = "https://api.flickr.com/services/rest/?method=flickr.photos.getSizes";

    public FlickrService(Context context) {
        this.retrofit = getRetrofitInstance(BASE_URL, context);
        this.apiService = retrofit.create(FlickrSearchService.class);
    }

    public void getPhotoUrlsFromSearchTerm(String query, MutableLiveData<List<String>> callback) throws IOException {
        //START THREAD
        Timber.d("getPhotoUrlsFromSearchTerm");
        getPhotoUrls(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Timber.d("onSubscribe");
                    }

                    @Override
                    public void onNext(String url) {
                        List<String> urls = callback.getValue();
                        if (!urls.contains(url)) {
                            urls.add(url);
                            callback.postValue(urls);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private final String[] preferredSizes = {
            "Large Square"
    };

    public Observable<String> getPhotoUrls(String query) {
        return Observable.create(s ->
        {
            Timber.d("inside the thread");
            List<Photo> photos = searchRequest(query);
            for (Photo photo : photos) {
                FlickrGetSizesResponse flickrGetSizesResponse = getSizesRequest(photo);
                String url = getUrlArPreferredSize(flickrGetSizesResponse);
                if (url != null)
                    s.onNext(url);
            }
            s.onComplete();
        });
    }

    public String getUrlArPreferredSize(FlickrGetSizesResponse flickrGetSizesResponse) {
        if (flickrGetSizesResponse == null || flickrGetSizesResponse.sizes == null || flickrGetSizesResponse.sizes.size == null)
            return null;

        List<Size> sizesFromResponse = flickrGetSizesResponse.sizes.size;

        for (String preferredSize : preferredSizes) {
            for (Size sizeFromResponse : sizesFromResponse) {
                if (preferredSize.equals(sizeFromResponse.label)) {
                    return sizeFromResponse.source;
                }
            }
        }
        return null;
    }

    public List<Photo> searchRequest(String query) throws IOException {
        Call<FlickrSearchResponse> call = apiService.search(SEARCH_METHOD_VALUE, API_KEY_VALUE, query, 1, FORMAT_JSON, NO_JSON_CALLBACK, PER_PAGE_VALUE);
        Response<FlickrSearchResponse> response = call.execute();
        return response.body().photos.photo;
    }

    public FlickrGetSizesResponse getSizesRequest(Photo photo) throws IOException {
        Call<FlickrGetSizesResponse> call = apiService.getSizes(GET_SIZES_METHOD_VALUE, API_KEY_VALUE, photo.id, FORMAT_JSON, NO_JSON_CALLBACK);
        Response<FlickrGetSizesResponse> response = call.execute();

        return response.body();
    }

    private Retrofit getRetrofitInstance(String baseUrl, Context context) {

        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new ChuckInterceptor(context)).build();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }

    public interface FlickrSearchService {
        @GET(REST_API)
        Call<FlickrSearchResponse> search(@Query(METHOD_KEY) String method,
                                          @Query(API_KEY) String apiKey,
                                          @Query(TAGS_KEY) String text,
                                          @Query(PAGE_KEY) int page,
                                          @Query(FORMAT_KEY) String format,
                                          @Query(NO_JSON_CALLBACK_KEY) int noJsonCallback,
                                          @Query(PER_PAGE_KEY) int perPage);

        @GET(REST_API)
        Call<FlickrGetSizesResponse> getSizes(@Query(METHOD_KEY) String method,
                                              @Query(API_KEY) String apiKey,
                                              @Query(PHOTO_ID_KEY) String photoId,
                                              @Query(FORMAT_KEY) String format,
                                              @Query(NO_JSON_CALLBACK_KEY) int noJsonCallback);
    }
}