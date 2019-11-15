package com.marklynch.currencyfair.livedata.flickr;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.marklynch.currencyfair.livedata.flickr.data.FlickrGetSizesResponse;
import com.marklynch.currencyfair.livedata.flickr.data.FlickrSearchResponse;
import com.marklynch.currencyfair.livedata.flickr.data.Photo;
import com.marklynch.currencyfair.livedata.flickr.data.Size;
import com.readystatesoftware.chuck.ChuckInterceptor;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

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

    public void getPhotoUrlsFromSearchTerm(String query, MutableLiveData<List<String>> liveData) throws IOException {

        Callback<FlickrGetSizesResponse> getSizesRequestCallback = new Callback<FlickrGetSizesResponse>() {
            @Override
            public void onResponse(Call<FlickrGetSizesResponse> call, Response<FlickrGetSizesResponse> response) {
                String newUrl = getUrlAtPreferredSize(response.body());
                List<String> urls = liveData.getValue();
                if (!urls.contains(newUrl)) {
                    urls.add(newUrl);
                    liveData.postValue(urls);
                }
            }

            @Override public void onFailure(Call<FlickrGetSizesResponse> call, Throwable t) {}
        };

        Callback<FlickrSearchResponse> searchRequestCallback = new Callback<FlickrSearchResponse>() {
            @Override
            public void onResponse(Call<FlickrSearchResponse> call, Response<FlickrSearchResponse> response) {
                for (Photo photo : response.body().photos.photo) {
                    getSizesRequest(photo, getSizesRequestCallback);
                }
            }

            @Override public void onFailure(Call<FlickrSearchResponse> call, Throwable t) {}
        };

        searchRequest(query,searchRequestCallback);
    }

    private void searchRequest(String query, Callback<FlickrSearchResponse> callback){
        apiService.search(SEARCH_METHOD_VALUE, API_KEY_VALUE, query, 1, FORMAT_JSON, NO_JSON_CALLBACK, PER_PAGE_VALUE).enqueue(callback);
    }

    private void getSizesRequest(Photo photo, Callback<FlickrGetSizesResponse> callback){
        apiService.getSizes(GET_SIZES_METHOD_VALUE, API_KEY_VALUE, photo.id, FORMAT_JSON, NO_JSON_CALLBACK).enqueue(callback);
    }

    private final String[] preferredSizes = {
            "Large Square"
    };

    public String getUrlAtPreferredSize(FlickrGetSizesResponse flickrGetSizesResponse) {
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

    private Retrofit getRetrofitInstance(String baseUrl, Context context) {

        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new ChuckInterceptor(context)).build();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
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