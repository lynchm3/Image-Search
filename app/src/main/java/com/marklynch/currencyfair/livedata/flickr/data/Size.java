package com.marklynch.currencyfair.livedata.flickr.data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Size {
    @JsonProperty("label")
    public String label;
    @JsonProperty("width")
    public String width;
    @JsonProperty("height")
    public String height;
    @JsonProperty("source")
    public String source;
    @JsonProperty("url")
    public String url;
    @JsonProperty("media")
    public String media;

    @Override
    public String toString() {
        return "Size{" +
                "label='" + label + '\'' +
                ", width='" + width + '\'' +
                ", height='" + height + '\'' +
                ", source='" + source + '\'' +
                ", url='" + url + '\'' +
                ", media='" + media + '\'' +
                '}';
    }
}