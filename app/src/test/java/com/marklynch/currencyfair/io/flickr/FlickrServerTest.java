package com.marklynch.currencyfair.io.flickr;

import com.marklynch.currencyfair.io.flickr.response.FlickrGetSizesResponse;
import com.marklynch.currencyfair.io.flickr.response.FlickrSearchResponse;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

public class FlickrServerTest {

    @Test
    public void testSearchRequest() throws InterruptedException, IOException {

        MockWebServer mockWebServer = new MockWebServer();
        mockWebServer.start();
        mockWebServer.enqueue(new MockResponse().setBody(searchResponseString));

        FlickrServer flickrServer = new FlickrServer(mockWebServer);

        final FlickrSearchResponse[] actualResponseBody = {null};
        final String[] actualRequestUrl = {null};
        CountDownLatch latch = new CountDownLatch(1);

        Callback<FlickrSearchResponse> searchRequestCallback = new Callback<FlickrSearchResponse>() {
            @Override
            public void onResponse(@NotNull Call<FlickrSearchResponse> call, Response<FlickrSearchResponse> response) {
                actualRequestUrl[0] = call.request().url().toString();
                actualResponseBody[0] = response.body();
                latch.countDown();
            }

            @Override
            public void onFailure(@NotNull Call<FlickrSearchResponse> call, @NotNull Throwable t) {
                latch.countDown();
            }
        };
        flickrServer.searchRequest("QUERY", 1, searchRequestCallback);

        latch.await(2, TimeUnit.SECONDS);

        if (latch.getCount() != 0)
            fail("onResponse was not called");

        String expectedUrl = mockWebServer.url("/") + "services/rest?method=flickr.photos.search&api_key=" + ApiKey.API_KEY + "&tags=QUERY&page=1&format=json&nojsoncallback=1&per_page=20";

        assertEquals("Search response not as expected", expectedUrl, actualRequestUrl[0]);
        assertEquals("Search response not as expected", generateExpectedSearchResponse(), actualResponseBody[0]);

        mockWebServer.close();
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
        photo1.secret = "6ed0d57a12";
        photo1.server = "65535";
        photos.photo.add(photo1);

        FlickrSearchResponse.Photo photo2 = new FlickrSearchResponse.Photo();
        photo2.id = "49074157138";
        photo2.secret = "989d35149c";
        photo2.server = "65535";
        photos.photo.add(photo2);

        FlickrSearchResponse.Photo photo3 = new FlickrSearchResponse.Photo();
        photo3.id = "49074096668";
        photo3.secret = "daf945567f";
        photo3.server = "65535";
        photos.photo.add(photo3);

        return searchResponseObject;
    }

    public final String searchResponseString = "{ \"photos\": { \"page\": 1, \"pages\": \"5028\", \"perpage\": 3, \"total\": \"502713\", \n" +
            "    \"photo\": [\n" +
            "      { \"id\": \"49074857286\", \"owner\": \"21611052@N02\", \"secret\": \"6ed0d57a12\", \"server\": \"65535\", \"farm\": 66, \"title\": \"Pizza anybody ?\", \"ispublic\": 1, \"isfriend\": 0, \"isfamily\": 0 },\n" +
            "      { \"id\": \"49074157138\", \"owner\": \"185084819@N02\", \"secret\": \"989d35149c\", \"server\": \"65535\", \"farm\": 66, \"title\": \"cash back\", \"ispublic\": 1, \"isfriend\": 0, \"isfamily\": 0 },\n" +
            "      { \"id\": \"49074096668\", \"owner\": \"151822520@N05\", \"secret\": \"daf945567f\", \"server\": \"65535\", \"farm\": 66, \"title\": \"hnb5\", \"ispublic\": 1, \"isfriend\": 0, \"isfamily\": 0 }\n" +
            "    ] }, \"stat\": \"ok\" }";

    @Test
    public void testSearchRequestBadJson() throws InterruptedException, IOException {

        String searchResponseString = "{ \"photos\": ERROR { \"page\": 1, \"pages\": \"5028\", \"perpage\": 3, \"total\": \"502713\", \n" +
                "    \"photo\": [] }, \"stat\": \"ok\" }";

        MockWebServer mockWebServer = new MockWebServer();
        mockWebServer.start();
        mockWebServer.enqueue(new MockResponse().setBody(searchResponseString));

        FlickrServer flickrServer = new FlickrServer(mockWebServer);

        CountDownLatch latch = new CountDownLatch(1);

        AtomicBoolean reachedOnFailure = new AtomicBoolean(false);

        Callback<FlickrSearchResponse> searchRequestCallback = new Callback<FlickrSearchResponse>() {
            @Override
            public void onResponse(@NotNull Call<FlickrSearchResponse> call, Response<FlickrSearchResponse> response) {
                latch.countDown();
            }

            @Override
            public void onFailure(@NotNull Call<FlickrSearchResponse> call, @NotNull Throwable t) {
                reachedOnFailure.set(true);
                latch.countDown();
            }
        };
        flickrServer.searchRequest("QUERY", 1, searchRequestCallback);

        latch.await(2, TimeUnit.SECONDS);

        if (!reachedOnFailure.get())
            fail("Search request didn't call back to onFailure, was meant to fail");

        mockWebServer.close();
    }

    @Test
    public void testGetSizesRequest() throws InterruptedException, IOException {

        MockWebServer mockWebServer = new MockWebServer();
        mockWebServer.start();
        mockWebServer.enqueue(new MockResponse().setBody(getSizesResponseString));

        FlickrServer flickrServer = new FlickrServer(mockWebServer);

        CountDownLatch latch = new CountDownLatch(1);

        final FlickrGetSizesResponse[] actualResponseBody = {null};
        final String[] actualRequestUrl = {null};

        Callback<FlickrGetSizesResponse> getSizesRequestCallback = new Callback<FlickrGetSizesResponse>() {
            @Override
            public void onResponse(@NotNull Call<FlickrGetSizesResponse> call, Response<FlickrGetSizesResponse> response) {
                actualRequestUrl[0] = call.request().url().toString();
                actualResponseBody[0] = response.body();
                latch.countDown();
            }

            @Override
            public void onFailure(@NotNull Call<FlickrGetSizesResponse> call, @NotNull Throwable t) {
                fail("Get imageSizes request called back to onFailure");
                latch.countDown();
            }
        };
        flickrServer.getSizesRequest(new FlickrSearchResponse.Photo(), getSizesRequestCallback);

        latch.await(2, TimeUnit.SECONDS);

        if (latch.getCount() != 0)
            fail("onResponse was not called");

        String expectedUrl = mockWebServer.url("/") + "services/rest?method=flickr.photos.getSizes&api_key=" + ApiKey.API_KEY + "&format=json&nojsoncallback=1";

        assertEquals("Search response not as expected", expectedUrl, actualRequestUrl[0]);
        assertEquals("Search response not as expected", generateExpectedGetSizesResponse(), actualResponseBody[0]);

        mockWebServer.close();
    }

    private FlickrGetSizesResponse generateExpectedGetSizesResponse() {
        FlickrGetSizesResponse getSizesResponseObject = new FlickrGetSizesResponse();
        getSizesResponseObject.stat = "ok";

        FlickrGetSizesResponse.ImageSizes imageSizes = new FlickrGetSizesResponse.ImageSizes();
        imageSizes.canblog = 0;
        imageSizes.canprint = 0;
        imageSizes.candownload = 0;
        imageSizes.imageSize = new ArrayList<>();
        getSizesResponseObject.imageSizes = imageSizes;

        FlickrGetSizesResponse.ImageSize size1 = new FlickrGetSizesResponse.ImageSize();
        size1.height = 150;
        size1.width = 150;
        size1.label = "Large Square";
        size1.source = "https://live.staticflickr.com/65535/49074857286_6ed0d57a12_q.jpg";
        imageSizes.imageSize.add(size1);

        FlickrGetSizesResponse.ImageSize size2 = new FlickrGetSizesResponse.ImageSize();
        size2.height = 809;
        size2.width = 1024;
        size2.label = "Large";
        size2.source = "https://live.staticflickr.com/65535/49074857286_6ed0d57a12_b.jpg";
        imageSizes.imageSize.add(size2);

        return getSizesResponseObject;
    }

    public final String getSizesResponseString = "{ \"sizes\": { \"canblog\": 0, \"canprint\": 0, \"candownload\": 0, \n" +
            "    \"size\": [\n" +
            "      { \"label\": \"Large Square\", \"width\": \"150\", \"height\": \"150\", \"source\": \"https:\\/\\/live.staticflickr.com\\/65535\\/49074857286_6ed0d57a12_q.jpg\", \"url\": \"https:\\/\\/www.flickr.com\\/photos\\/21611052@N02\\/49074857286\\/imageSizes\\/q\\/\", \"media\": \"photo\" },\n" +
            "      { \"label\": \"Large\", \"width\": \"1024\", \"height\": \"809\", \"source\": \"https:\\/\\/live.staticflickr.com\\/65535\\/49074857286_6ed0d57a12_b.jpg\", \"url\": \"https:\\/\\/www.flickr.com\\/photos\\/21611052@N02\\/49074857286\\/imageSizes\\/l\\/\", \"media\": \"photo\" }\n" +
            "    ] }, \"stat\": \"ok\" }";


}
