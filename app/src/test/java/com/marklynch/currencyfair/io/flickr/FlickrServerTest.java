package com.marklynch.currencyfair.io.flickr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklynch.currencyfair.io.flickr.response.FlickrSearchResponse;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FlickrServerTest {

    @Test
    public void testSearchRequest() throws InterruptedException, IOException {

        MockWebServer mockWebServer = new MockWebServer();
        mockWebServer.start();
        mockWebServer.enqueue(new MockResponse().setBody(searchResponseString));

        FlickrServer flickrServer = new FlickrServer(mockWebServer);

        FlickrSearchResponse expected = generateExpectedSearchResponse();
        final FlickrSearchResponse[] actual = {null};
        CountDownLatch latch = new CountDownLatch(1);

        Callback<FlickrSearchResponse> searchRequestCallback = new Callback<FlickrSearchResponse>() {
            @Override
            public void onResponse(@NotNull Call<FlickrSearchResponse> call, Response<FlickrSearchResponse> response) {
                actual[0] = response.body();
                latch.countDown();
            }

            @Override
            public void onFailure(@NotNull Call<FlickrSearchResponse> call, @NotNull Throwable t) {
                fail("Search request called back to onFailure");
                latch.countDown();
            }
        };
        flickrServer.searchRequest("QUERY", 1, searchRequestCallback);

        latch.await(2, TimeUnit.SECONDS);

        assertEquals("Search response not as expected", expected,actual[0]);

    }

    private FlickrSearchResponse generateExpectedSearchResponse() {
        FlickrSearchResponse searchResponseObject = new FlickrSearchResponse();
        searchResponseObject.stat = "ok";

        FlickrSearchResponse.Photos photos = new FlickrSearchResponse.Photos();
        photos.page = 1;
        photos.pages = 5028;
        photos.perpage = 3;
        photos.total = 502713;
        photos.photo = new ArrayList<>();
        searchResponseObject.photos = photos;

        FlickrSearchResponse.Photo photo1 = new FlickrSearchResponse.Photo();
        photo1.id = "49074857286";
        photos.photo.add(photo1);

        FlickrSearchResponse.Photo photo2 = new FlickrSearchResponse.Photo();
        photo2.id = "49074157138";
        photos.photo.add(photo2);

        FlickrSearchResponse.Photo photo3 = new FlickrSearchResponse.Photo();
        photo3.id = "49074096668";
        photos.photo.add(photo3);

        return searchResponseObject;
    }

    @Test
    public void testGetSizesRequest() {


    }

    public String searchResponseString = "{ \"photos\": { \"page\": 1, \"pages\": \"5028\", \"perpage\": 3, \"total\": \"502713\", \n" +
            "    \"photo\": [\n" +
            "      { \"id\": \"49074857286\", \"owner\": \"21611052@N02\", \"secret\": \"6ed0d57a12\", \"server\": \"65535\", \"farm\": 66, \"title\": \"Pizza anybody ?\", \"ispublic\": 1, \"isfriend\": 0, \"isfamily\": 0 },\n" +
            "      { \"id\": \"49074157138\", \"owner\": \"185084819@N02\", \"secret\": \"989d35149c\", \"server\": \"65535\", \"farm\": 66, \"title\": \"cash back\", \"ispublic\": 1, \"isfriend\": 0, \"isfamily\": 0 },\n" +
            "      { \"id\": \"49074096668\", \"owner\": \"151822520@N05\", \"secret\": \"daf945567f\", \"server\": \"65535\", \"farm\": 66, \"title\": \"hnb5\", \"ispublic\": 1, \"isfriend\": 0, \"isfamily\": 0 }\n" +
            "    ] }, \"stat\": \"ok\" }";


}
