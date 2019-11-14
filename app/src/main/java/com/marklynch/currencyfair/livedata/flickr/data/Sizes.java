package com.marklynch.currencyfair.livedata.flickr.data;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Sizes {
    @JsonProperty("canblog")
    public int canblog;
    @JsonProperty("canprint")
    public int canprint;
    @JsonProperty("candownload")
    public int candownload;
    @JsonProperty("size")
    public List<Size> size = null;

    @Override
    public String toString() {
        return "Sizes{" +
                "canblog=" + canblog +
                ", canprint=" + canprint +
                ", candownload=" + candownload +
                ", size=" + size +
                '}';
    }
}