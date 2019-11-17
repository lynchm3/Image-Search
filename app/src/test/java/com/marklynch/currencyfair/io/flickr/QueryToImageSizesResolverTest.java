package com.marklynch.currencyfair.io.flickr;

import com.marklynch.currencyfair.io.flickr.response.FlickrGetSizesResponse;
import com.marklynch.currencyfair.io.flickr.response.FlickrSearchResponse;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;

public class QueryToImageSizesResolverTest {


    Vector<FlickrGetSizesResponse.ImageSizes> actualImageSizesList;
    FlickrGetSizesResponse.ImageSizes actualImageSizes;

    @Test
    public void getPhotoUrlsFromSearchTermTest() throws InterruptedException, IOException {

        MockWebServer mockWebServer = new MockWebServer();
        mockWebServer.start();
        mockWebServer.enqueue(new MockResponse().setBody(searchResponseString));
        mockWebServer.enqueue(new MockResponse().setBody(getSizesResponseString));
        mockWebServer.enqueue(new MockResponse().setBody(getSizesResponseString));
        mockWebServer.enqueue(new MockResponse().setBody(getSizesResponseString));

        CountDownLatch singleImageslatch = new CountDownLatch(3);
        CountDownLatch allImageslatch = new CountDownLatch(1);

        QueryToImageSizesResolver queryToImageSizesResolver = new QueryToImageSizesResolver(mockWebServer);


        QueryToImageSizesResolver.QueryResultListener queryResultListener = new QueryToImageSizesResolver.QueryResultListener()
        {
            @Override
            public void allImageSizesDownload(Vector<FlickrGetSizesResponse.ImageSizes> imageSizesList) {
                actualImageSizesList = imageSizesList;
                allImageslatch.countDown();
            }

            @Override
            public void singleImageSizesDownloaded(FlickrGetSizesResponse.ImageSizes imageSizes) {
                actualImageSizes = imageSizes;
                singleImageslatch.countDown();
            }

            @Override
            public void onError(int errorMessage) {
                fail("onError was reached");
            }
        };

        queryToImageSizesResolver.getPhotoUrlsFromSearchTerm("QUERY", queryResultListener, 1);

        singleImageslatch.await(2, TimeUnit.SECONDS);
        allImageslatch.await(2, TimeUnit.SECONDS);

        if(singleImageslatch.getCount() != 0)
            fail("singleImageSizesDownloaded() was not called enough times");

        if(singleImageslatch.getCount() != 0)
            fail("allImageSizesDownload() was not called");

        assertEquals(generateExpectedGetSizesResponseList(), actualImageSizesList);
        assertEquals(generateExpectedGetSizesResponse(), actualImageSizes);
    }

    private FlickrGetSizesResponse.ImageSizes generateExpectedGetSizesResponse() {
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

        return getSizesResponseObject.imageSizes;
    }

    private Vector<FlickrGetSizesResponse.ImageSizes> generateExpectedGetSizesResponseList() {

        Vector<FlickrGetSizesResponse.ImageSizes> responseList = new Vector<>();
        responseList.add(generateExpectedGetSizesResponse());
        responseList.add(generateExpectedGetSizesResponse());
        responseList.add(generateExpectedGetSizesResponse());

        return responseList;
    }

    public String searchResponseString = "{ \"photos\": { \"page\": 1, \"pages\": \"5028\", \"perpage\": 3, \"total\": \"502713\", \n" +
            "    \"photo\": [\n" +
            "      { \"id\": \"49074857286\", \"owner\": \"21611052@N02\", \"secret\": \"6ed0d57a12\", \"server\": \"65535\", \"farm\": 66, \"title\": \"Pizza anybody ?\", \"ispublic\": 1, \"isfriend\": 0, \"isfamily\": 0 },\n" +
            "      { \"id\": \"49074157138\", \"owner\": \"185084819@N02\", \"secret\": \"989d35149c\", \"server\": \"65535\", \"farm\": 66, \"title\": \"cash back\", \"ispublic\": 1, \"isfriend\": 0, \"isfamily\": 0 },\n" +
            "      { \"id\": \"49074096668\", \"owner\": \"151822520@N05\", \"secret\": \"daf945567f\", \"server\": \"65535\", \"farm\": 66, \"title\": \"hnb5\", \"ispublic\": 1, \"isfriend\": 0, \"isfamily\": 0 }\n" +
            "    ] }, \"stat\": \"ok\" }";

    public String getSizesResponseString = "{ \"sizes\": { \"canblog\": 0, \"canprint\": 0, \"candownload\": 0, \n" +
            "    \"size\": [\n" +
            "      { \"label\": \"Large Square\", \"width\": \"150\", \"height\": \"150\", \"source\": \"https:\\/\\/live.staticflickr.com\\/65535\\/49074857286_6ed0d57a12_q.jpg\", \"url\": \"https:\\/\\/www.flickr.com\\/photos\\/21611052@N02\\/49074857286\\/imageSizes\\/q\\/\", \"media\": \"photo\" },\n" +
            "      { \"label\": \"Large\", \"width\": \"1024\", \"height\": \"809\", \"source\": \"https:\\/\\/live.staticflickr.com\\/65535\\/49074857286_6ed0d57a12_b.jpg\", \"url\": \"https:\\/\\/www.flickr.com\\/photos\\/21611052@N02\\/49074857286\\/imageSizes\\/l\\/\", \"media\": \"photo\" }\n" +
            "    ] }, \"stat\": \"ok\" }";

}
