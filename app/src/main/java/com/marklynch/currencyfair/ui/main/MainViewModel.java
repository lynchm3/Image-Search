package com.marklynch.currencyfair.ui.main;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.marklynch.currencyfair.R;
import com.marklynch.currencyfair.io.flickr.QueryToImageSizesResolver;
import com.marklynch.currencyfair.io.flickr.response.FlickrGetSizesResponse;

import java.util.List;
import java.util.Vector;

public class MainViewModel extends AndroidViewModel  {

    public MutableLiveData<ImagesToDisplay> imageToDisplayLiveData = new MutableLiveData<>(new ImagesToDisplay());
    private QueryToImageSizesResolver queryToImageSizesResolver;
    private FlickrImageSizesToDisplayImagesAdapter flickrImageSizesToDisplayImagesAdapter;

    public MainViewModel(Application application) {
        super(application);
        flickrImageSizesToDisplayImagesAdapter = new FlickrImageSizesToDisplayImagesAdapter();
        imageToDisplayLiveData.setValue(new ImagesToDisplay());
        queryToImageSizesResolver = new QueryToImageSizesResolver(application);
    }

    public void retrieveSearchResults(String query, int page, boolean newSearch) {
        if (newSearch)
            imageToDisplayLiveData.setValue(new ImagesToDisplay());
        queryToImageSizesResolver.getPhotoUrlsFromSearchTerm(query, flickrImageSizesToDisplayImagesAdapter, page);
    }

    public class FlickrImageSizesToDisplayImagesAdapter implements QueryToImageSizesResolver.QueryResultListener
    {
        private static final String THUMB_SIZE = "Large Square";
        private static final String FULL_SIZE = "Large";

        @Override
        public void allImageSizesDownload(Vector<FlickrGetSizesResponse.ImageSizes> imageSizesList) {
            ImagesToDisplay currentImages = imageToDisplayLiveData.getValue();

            ImagesToDisplay concatenatedImages = new ImagesToDisplay();
            concatenatedImages.images.addAll(currentImages.images);
            int newImagesCount = 0;
            for (FlickrGetSizesResponse.ImageSizes imageSizes : imageSizesList) {
                ImageToDisplay imageToDisplay = getImageToDisplay(imageSizes);
                if (imageToDisplay != null) {
                    concatenatedImages.images.add(imageToDisplay);
                    newImagesCount++;
                }
            }

            if (newImagesCount == 0)
                concatenatedImages.errorMessage = getApplication().getString(R.string.no_images_found);
            imageToDisplayLiveData.setValue(concatenatedImages);
        }

        @Override
        public void singleImageSizesDownloaded(FlickrGetSizesResponse.ImageSizes imageSizes) {
            preloadThumb(imageSizes);
        }

        @Override
        public void onError(int errorMessage) {
            ImagesToDisplay currentImages = imageToDisplayLiveData.getValue();
            currentImages.errorMessage = getApplication().getString(errorMessage);
            imageToDisplayLiveData.setValue(currentImages);
        }


        private ImageToDisplay getImageToDisplay(FlickrGetSizesResponse.ImageSizes imageSizes) {
            ImageToDisplay imageToDisplay = null;
            ImageToDisplay.ImageInfo thumb = null;
            ImageToDisplay.ImageInfo fullImage = null;
            List<FlickrGetSizesResponse.ImageSize> sizesFromResponse = imageSizes.imageSize;
            for (FlickrGetSizesResponse.ImageSize imageSizeFromResponse : sizesFromResponse) {
                if (THUMB_SIZE.equals(imageSizeFromResponse.label)) {
                    thumb = new ImageToDisplay.ImageInfo(imageSizeFromResponse.source,
                            imageSizeFromResponse.width,
                            imageSizeFromResponse.height);
                } else if (FULL_SIZE.equals(imageSizeFromResponse.label)) {
                    fullImage = new ImageToDisplay.ImageInfo(imageSizeFromResponse.source,
                            imageSizeFromResponse.width,
                            imageSizeFromResponse.height);
                }
            }

            if (thumb == null)
                return null;

            if (fullImage == null)
                fullImage = thumb;

            imageToDisplay = new ImageToDisplay(thumb,fullImage);

            return imageToDisplay;
        }

        private void preloadThumb(FlickrGetSizesResponse.ImageSizes imageSizes) {
            List<FlickrGetSizesResponse.ImageSize> sizesFromResponse = imageSizes.imageSize;
            for (FlickrGetSizesResponse.ImageSize imageSizeFromResponse : sizesFromResponse) {
                if (THUMB_SIZE.equals(imageSizeFromResponse.label)) {
                    Glide.with(getApplication().getApplicationContext()).load(imageSizeFromResponse.source).submit();
                    return;
                }
            }
        }
    }
}