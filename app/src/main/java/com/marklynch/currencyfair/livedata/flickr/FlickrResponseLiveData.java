package com.marklynch.currencyfair.livedata.flickr;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.marklynch.currencyfair.livedata.flickr.data.FlickrSearchResponse;
import com.marklynch.currencyfair.livedata.flickr.data.Photo;
import com.readystatesoftware.chuck.ChuckInterceptor;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import timber.log.Timber;

public class FlickrResponseLiveData extends MutableLiveData<List<Photo>> {

    private Retrofit retrofit;
    private FlickrSearchService apiService;

    private static final String METHOD_KEY = "method";
    private static final String SEARCH_METHOD_VALUE = "flickr.photos.search";
    private static final String API_KEY = "api_key";
    private static final String API_KEY_VALUE = "297af75d9d68977b69513409fc928ca8";
    private static final String TEXT_KEY = "text";
    private static final String PAGE_KEY = "page";
    private static final String FORMAT_KEY = "format";
    private static final String FORMAT_VALUE = "json";
    private static final String NO_JSON_CALLBACK_KEY = "nojsoncallback";
    private static final int NO_JSON_CALLBACK_VALUE = 1;
    private static final String PER_PAGE_KEY = "per_page";
    private static final int PER_PAGE_VALUE = 20;

    private static final String BASE_URL = "https://api.flickr.com";
    private static final String REST_API_METHOD = "/services/rest";
    private static final String baseSearchUrl = "https://api.flickr.com/services/rest/?method=flickr.photos.search";
    private static final String baseSizesUrl = "https://api.flickr.com/services/rest/?method=flickr.photos.getSizes";

    public FlickrResponseLiveData(Context context) {
        this.retrofit = getRetrofitInstance(BASE_URL, context);
        this.apiService = retrofit.create(FlickrSearchService.class);
    }

    public void retrieveSearchResults(String query) {

        Call<FlickrSearchResponse> call = apiService.search(SEARCH_METHOD_VALUE, API_KEY_VALUE, query, 1, FORMAT_VALUE, NO_JSON_CALLBACK_VALUE, PER_PAGE_VALUE);

        call.enqueue(new Callback<FlickrSearchResponse>() {
            @Override
            public void onResponse(Call<FlickrSearchResponse> call, Response<FlickrSearchResponse> response) {
                FlickrSearchResponse flickrSearchResponse = response.body();
                postValue(flickrSearchResponse.photos.photo);
            }

            @Override
            public void onFailure(Call<FlickrSearchResponse> call, Throwable throwable) {
                Timber.e(throwable);
            }
        });
    }

    private Retrofit getRetrofitInstance(String baseUrl, Context context) {

        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new ChuckInterceptor(context)).build();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }

    public interface FlickrSearchService {
        @GET(REST_API_METHOD)
        Call<FlickrSearchResponse> search(@Query(METHOD_KEY) String method,
                                          @Query(API_KEY) String apiKey,
                                          @Query(TEXT_KEY) String text,
                                          @Query(PAGE_KEY) int page,
                                          @Query(FORMAT_KEY) String format,
                                          @Query(NO_JSON_CALLBACK_KEY) int noJsonCallback,
                                          @Query(PER_PAGE_KEY) int perPage);
    }
}