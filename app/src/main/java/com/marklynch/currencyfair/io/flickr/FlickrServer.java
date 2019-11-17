package com.marklynch.currencyfair.io.flickr;

import android.content.Context;

import com.marklynch.currencyfair.io.flickr.response.FlickrGetSizesResponse;
import com.marklynch.currencyfair.io.flickr.response.FlickrSearchResponse;
import com.readystatesoftware.chuck.ChuckInterceptor;

import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static com.marklynch.currencyfair.io.flickr.ApiKey.API_KEY;

public class FlickrServer {

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
    private static final String FORMAT_JSON = "json";
    private static final int NO_JSON_CALLBACK = 1;
    static final int PER_PAGE = 20;

    FlickrServer(Context context) {
        this.flickrService = getRetrofitInstance(context).create(FlickrService.class);
    }

    FlickrServer(MockWebServer mockWebServer) {
        this.flickrService = getRetrofitInstance(mockWebServer).create(FlickrService.class);
    }

    private Retrofit getRetrofitInstance(Context context) {
        return new Retrofit.Builder()
                .baseUrl(FlickrServer.BASE_URL)
                .client(new OkHttpClient.Builder().addInterceptor(new ChuckInterceptor(context)).build())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }

    private Retrofit getRetrofitInstance(MockWebServer mockWebServer) {
        return new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .client(new OkHttpClient.Builder().build())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }

    public void searchRequest(String query, int page, Callback<FlickrSearchResponse> callback) {
        flickrService.search(API_KEY, query, page, FORMAT_JSON, NO_JSON_CALLBACK, PER_PAGE).enqueue(callback);
    }

    public void getSizesRequest(FlickrSearchResponse.Photo photo, Callback<FlickrGetSizesResponse> callback) {
        flickrService.getSizes(API_KEY, photo.id, FORMAT_JSON, NO_JSON_CALLBACK).enqueue(callback);
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
}