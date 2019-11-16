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
    private static final int PER_PAGE = 20;

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

    private void searchRequest(String query, int page, Callback<FlickrSearchResponse> callback) {
        flickrService.search(API_KEY_VALUE, query, page, FORMAT_JSON, NO_JSON_CALLBACK, PER_PAGE).enqueue(callback);
    }

    private void getSizesRequest(FlickrSearchResponse.Photo photo, Callback<FlickrGetSizesResponse> callback) {
        flickrService.getSizes(API_KEY_VALUE, photo.id, FORMAT_JSON, NO_JSON_CALLBACK).enqueue(callback);
    }

    public void getPhotoUrlsFromSearchTerm(final String query, final MutableLiveData<ImagesToDisplay> liveDataCallback, int page, Application application) {


        final int[] count = {0};
        final ImagesToDisplay imagesToDisplay = liveDataCallback.getValue();

        Callback<FlickrGetSizesResponse> getSizesRequestCallback = new Callback<FlickrGetSizesResponse>() {
            @Override
            public void onResponse(@NotNull Call<FlickrGetSizesResponse> call, Response<FlickrGetSizesResponse> response) {

                ImageToDisplay imageToDisplay = getImageToDisplay(response.body());

                //Queue up download of thumb image
                if (imageToDisplay != null && imageToDisplay.thumb.source != null)
                    Glide.with(application).load(imageToDisplay.thumb.source).submit();

                //Add image to our list, post to livedata callback if we have getSize responses for whole page
                synchronized (liveDataCallback) {
                    if (imageToDisplay != null && imageToDisplay.thumb.source != null)
                        imagesToDisplay.images.add(imageToDisplay);

                    count[0]++;
                    if (count[0] % PER_PAGE == 0) {
                        liveDataCallback.postValue(imagesToDisplay);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<FlickrGetSizesResponse> call, @NotNull Throwable t) {
                //Post to livedata callback if we have getSize responses for whole page
                synchronized (liveDataCallback) {
                    count[0]++;
                    if (count[0] % PER_PAGE == 0) {
                        liveDataCallback.postValue(imagesToDisplay);
                    }
                }
            }
        };

        Callback<FlickrSearchResponse> searchRequestCallback = new Callback<FlickrSearchResponse>() {
            @Override
            public void onResponse(@NotNull Call<FlickrSearchResponse> call, Response<FlickrSearchResponse> response) {
                for (FlickrSearchResponse.Photo photo : response.body().photos.photo) {
                    getSizesRequest(photo, getSizesRequestCallback);
                }
            }

            @Override
            public void onFailure(@NotNull Call<FlickrSearchResponse> call, @NotNull Throwable t) {
            }
        };

        searchRequest(query, page, searchRequestCallback);
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