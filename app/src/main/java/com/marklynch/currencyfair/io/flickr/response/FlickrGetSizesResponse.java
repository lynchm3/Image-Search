package com.marklynch.currencyfair.io.flickr.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

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

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Sizes {
        @JsonProperty("canblog")
        public int canblog;
        @JsonProperty("canprint")
        public int canprint;
        @JsonProperty("candownload")
        public int candownload;
        @JsonProperty("size")
        public List<ImageSize> imageSize = null;

        @Override
        public String toString() {
            return "Sizes{" +
                    "canblog=" + canblog +
                    ", canprint=" + canprint +
                    ", candownload=" + candownload +
                    ", imageSize=" + imageSize +
                    '}';
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ImageSize {
        @JsonProperty("label")
        public String label;
        @JsonProperty("source")
        public String source;
        @JsonProperty("width")
        public int width;
        @JsonProperty("height")
        public int height;

        @Override
        public String toString() {
            return "ImageSize{" +
                    "label='" + label + '\'' +
                    ", source='" + source + '\'' +
                    ", width='" + width + '\'' +
                    ", height='" + height + '\'' +
                    '}';
        }
    }
}