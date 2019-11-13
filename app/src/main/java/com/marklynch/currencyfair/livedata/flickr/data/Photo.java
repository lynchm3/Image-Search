package com.marklynch.currencyfair.livedata.flickr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Photo {
    @JsonProperty("id")
    public String id;
}