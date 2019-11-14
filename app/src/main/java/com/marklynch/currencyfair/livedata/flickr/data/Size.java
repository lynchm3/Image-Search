package com.marklynch.currencyfair.livedata.flickr.data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Size {
    @JsonProperty("label")
    public String label;
    @JsonProperty("source")
    public String source;

    @Override
    public String toString() {
        return "Size{" +
                "label='" + label + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}