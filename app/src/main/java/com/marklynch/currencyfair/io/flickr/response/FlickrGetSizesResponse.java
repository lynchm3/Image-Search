package com.marklynch.currencyfair.io.flickr.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FlickrGetSizesResponse {
    @JsonProperty("sizes")
    public ImageSizes imageSizes;
    @JsonProperty("stat")
    public String stat;

    @Override
    public String toString() {
        return "FlickrGetSizesResponse{" +
                "imageSizes=" + imageSizes +
                ", stat='" + stat + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlickrGetSizesResponse that = (FlickrGetSizesResponse) o;
        return Objects.equals(imageSizes, that.imageSizes) &&
                Objects.equals(stat, that.stat);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ImageSizes {
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
            return "ImageSizes{" +
                    "canblog=" + canblog +
                    ", canprint=" + canprint +
                    ", candownload=" + candownload +
                    ", imageSize=" + imageSize +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ImageSizes imageSizes = (ImageSizes) o;
            return canblog == imageSizes.canblog &&
                    canprint == imageSizes.canprint &&
                    candownload == imageSizes.candownload &&
                    Objects.equals(imageSize, imageSizes.imageSize);
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ImageSize imageSize = (ImageSize) o;
            return width == imageSize.width &&
                    height == imageSize.height &&
                    Objects.equals(label, imageSize.label) &&
                    Objects.equals(source, imageSize.source);
        }
    }
}