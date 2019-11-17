package com.marklynch.currencyfair.ui.main;

public class ImageToDisplay {


    public final ImageInfo thumb;
    public final ImageInfo fullImage;

    public ImageToDisplay(ImageInfo thumb, ImageInfo fullImage) {
        this.thumb = thumb;
        this.fullImage = fullImage;
    }

    @Override
    public String toString() {
        return "ImageToDisplay{" +
                "thumb=" + thumb +
                ", fullImage=" + fullImage +
                '}';
    }

    public static class ImageInfo {

        public final String url;
        public final int width;
        public final int height;

        public ImageInfo(String url, int width, int height) {
            this.url = url;
            this.width = width;
            this.height = height;
        }

        @Override
        public String toString() {
            return "ImageInfo{" +
                    "url='" + url + '\'' +
                    ", width=" + width +
                    ", height=" + height +
                    '}';
        }
    }
}
