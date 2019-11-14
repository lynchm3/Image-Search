package com.marklynch.currencyfair.livedata.flickr.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Photo {
    @JsonProperty("id")
    public String id;

    @Override
    public String toString() {
        return "Photo{" +
                "id='" + id + '\'' +
                '}';
    }
}