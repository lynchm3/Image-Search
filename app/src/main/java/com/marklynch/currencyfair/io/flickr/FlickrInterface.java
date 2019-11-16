package com.marklynch.currencyfair.io.flickr;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.marklynch.currencyfair.io.flickr.response.FlickrGetSizesResponse;
import com.marklynch.currencyfair.io.flickr.response.FlickrSearchResponse;
import com.marklynch.currencyfair.ui.main.ImageToDisplay;
import com.marklynch.currencyfair.ui.main.ImagesToDisplay;
import com.readystatesoftware.chuck.ChuckInterceptor;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class FlickrInterface {

    private interface Fields {
        String METHOD = "method";
        String API_KEY = "api_key";
        String TAGS = "tags";
        String PAGE = "page";
        String FORMAT = "format";
        String PER_PAGE = "per_page";
        String PHOTO_ID = "photo_id";
        String NO_JSON_CALLBACK = "nojsoncallback";
    }

    private interface Methods {
        String SEARCH = "flickr.photos.search";
        String GET_SIZES = "flickr.photos.getSizes";
    }

    private FlickrService flickrService;
    private static final String BASE_URL = "https://api.flickr.com";
    private static final String REST_API = "/services/rest";
    private static final String API_KEY_VALUE = "297af75d9d68977b69513409fc928ca8";
    private static final String FORMAT_JSON = "json";
    private static final int NO_JSON_CALLBACK = 1;
    public static final int PER_PAGE = 20;

    public FlickrInterface(Context context) {
        this.flickrService = getRetrofitInstance(BASE_URL, context).create(FlickrService.class);
    }

    private Retrofit getRetrofitInstance(String baseUrl, Context context) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(new OkHttpClient.Builder().addInterceptor(new ChuckInterceptor(context)).build())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }

    public interface FlickrService {
        @GET(REST_API + "?" + Fields.METHOD + "=" + Methods.SEARCH)
        Call<FlickrSearchResponse> search(@Query(Fields.API_KEY) String apiKey,
                                          @Query(Fields.TAGS) String text,
                                          @Query(Fields.PAGE) int page,
                                          @Query(Fields.FORMAT) String format,
                                          @Query(Fields.NO_JSON_CALLBACK) int noJsonCallback,
                                          @Query(Fields.PER_PAGE) int perPage);

        @GET(REST_API + "?" + Fields.METHOD + "=" + Methods.GET_SIZES)
        Call<FlickrGetSizesResponse> getSizes(@Query(Fields.API_KEY) String apiKey,
                                              @Query(Fields.PHOTO_ID) String photoId,
                                              @Query(Fields.FORMAT) String format,
                                              @Query(Fields.NO_JSON_CALLBACK) int noJsonCallback);
    }

    public void searchRequest(String query, int page, Callback<FlickrSearchResponse> callback) {
        flickrService.search(API_KEY_VALUE, query, page, FORMAT_JSON, NO_JSON_CALLBACK, PER_PAGE).enqueue(callback);
    }

    public void getSizesRequest(FlickrSearchResponse.Photo photo, Callback<FlickrGetSizesResponse> callback) {
        flickrService.getSizes(API_KEY_VALUE, photo.id, FORMAT_JSON, NO_JSON_CALLBACK).enqueue(callback);
    }
}