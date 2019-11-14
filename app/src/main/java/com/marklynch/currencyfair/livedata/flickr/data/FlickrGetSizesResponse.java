package com.marklynch.currencyfair.livedata.flickr.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FlickrGetSizesResponse {
    @JsonProperty("sizes")
    public Sizes sizes;
    @JsonProperty("stat")
    public String stat;

    @Override
    public String toString() {
        return "FlickrGetSizesResponse{" +
                "sizes=" + sizes +
                ", stat='" + stat + '\'' +
                '}';
    }
}