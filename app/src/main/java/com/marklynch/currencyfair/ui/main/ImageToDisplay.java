package com.marklynch.currencyfair.ui.main;

public class ImageToDisplay {


    public ImageInfo thumb;
    public ImageInfo fullImage;

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

        public String url;
        public int width;
        public int height;

        public ImageInfo(String url, int width, int height) {
            this.url = url;
            this.width = width;
            this.height = height;
        }
    }
}
