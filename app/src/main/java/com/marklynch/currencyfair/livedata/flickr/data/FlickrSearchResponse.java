package com.marklynch.currencyfair.livedata.flickr.data;

//{"photos":
//        {"page":1,"pages":2236,"perpage":100,"total":"223547","photo":
//        [{"id":"49060340363","owner":"185465448@N07","secret":"7d8928c90a","server":"65535","farm":66,"title":"1256","ispublic":1,"isfriend":0,"isfamily":0}]},"stat":"ok"}
//


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FlickrSearchResponse {
    @JsonProperty("photos")
    public Photos photos;
    @JsonProperty("stat")
    public String stat;
}