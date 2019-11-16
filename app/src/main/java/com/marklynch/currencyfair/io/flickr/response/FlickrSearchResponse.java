package com.marklynch.currencyfair.io.flickr.response;

//{"photos":
//        {"page":1,"pages":2236,"perpage":100,"total":"223547","photo":
//        [{"id":"49060340363","owner":"185465448@N07","secret":"7d8928c90a","server":"65535","farm":66,"title":"1256","ispublic":1,"isfriend":0,"isfamily":0}]},"stat":"ok"}
//


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FlickrSearchResponse {
    @JsonProperty("photos")
    public Photos photos;
    @JsonProperty("stat")
    public String stat;

    @Override
    public String toString() {
        return "FlickrSearchResponse{" +
                "photos=" + photos +
                ", stat='" + stat + '\'' +
                '}';
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Photos {
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Photo {
        @JsonProperty("id")
        public String id;

        @Override
        public String toString() {
            return "Photo{" +
                    "id='" + id + '\'' +
                    '}';
        }
    }
}