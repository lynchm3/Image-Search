package com.marklynch.currencyfair.livedata.flickr.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Photos {
    @JsonProperty("page")
    public int page;
    @JsonProperty("pages")
    public int pages;
    @JsonProperty("perpage")
    public int perpage;
    @JsonProperty("total")
    public String total;
    @JsonProperty("photo")
    public List<Photo> photo = null;

    @Override
    public String toString() {
        return "Photos{" +
                "page=" + page +
                ", pages=" + pages +
                ", perpage=" + perpage +
                ", total='" + total + '\'' +
                ", photo=" + photo +
                '}';
    }
}