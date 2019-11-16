package com.marklynch.currencyfair.ui.main;

import com.marklynch.currencyfair.io.flickr.response.FlickrGetSizesResponse;

public class ImageToDisplay {
    public FlickrGetSizesResponse.ImageSize thumb;
    public FlickrGetSizesResponse.ImageSize large;

    @Override
    public String toString() {
        return "ImageToDisplay{" +
                "thumb=" + thumb +
                ", large=" + large +
                '}';
    }
}
