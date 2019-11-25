package com.marklynch.flickrsearch.ui.main;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.marklynch.flickrsearch.R;
import com.marklynch.flickrsearch.io.flickr.QueryToImageSizesResolver;
import com.marklynch.flickrsearch.io.flickr.response.FlickrGetSizesResponse;
import com.marklynch.flickrsearch.io.flickr.response.FlickrSearchResponse;

import java.util.List;
import java.util.Vector;

public class MainViewModel extends AndroidViewModel {

    public final MutableLiveData<ImagesToDisplay> imagesToDisplayLiveData = new MutableLiveData<>(new ImagesToDisplay());
    private final QueryToImageSizesResolver queryToImageSizesResolver;
    private final FlickrImageSizesToDisplayImagesAdapter flickrImageSizesToDisplayImagesAdapter;

    public MainViewModel(Application application) {
        super(application);
        flickrImageSizesToDisplayImagesAdapter = new FlickrImageSizesToDisplayImagesAdapter();
        imagesToDisplayLiveData.setValue(new ImagesToDisplay());
        queryToImageSizesResolver = new QueryToImageSizesResolver(application);
    }

    public void retrieveSearchResults(String query, int page, boolean newSearch) {
        if (newSearch)
            imagesToDisplayLiveData.setValue(new ImagesToDisplay());
        queryToImageSizesResolver.getPhotoUrlsFromSearchTerm(query, flickrImageSizesToDisplayImagesAdapter, page);
    }

    public class FlickrImageSizesToDisplayImagesAdapter implements QueryToImageSizesResolver.QueryResultListener {
        private static final String THUMB_SIZE = "Large Square";
        private static final String FULL_SIZE = "Large";

        @Override
        public void allImageSizesDownloaded(Vector<FlickrGetSizesResponse.ImageSizes> imageSizesList) {
            ImagesToDisplay currentImages = imagesToDisplayLiveData.getValue();

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
            imagesToDisplayLiveData.setValue(concatenatedImages);
        }

        @Override
        public void singleImageSizesDownloaded(FlickrGetSizesResponse.ImageSizes imageSizes) {
            preloadThumb(imageSizes);
        }

        @Override
        public void onError(int errorMessage) {
            ImagesToDisplay currentImages = imagesToDisplayLiveData.getValue();
            currentImages.errorMessage = getApplication().getString(errorMessage);
            imagesToDisplayLiveData.setValue(currentImages);
        }

        @Override
        public void searchDownloadedExperimental(FlickrSearchResponse flickrSearchResponse) {
            ImagesToDisplay currentImages = imagesToDisplayLiveData.getValue();
            ImagesToDisplay concatenatedImages = new ImagesToDisplay();
            concatenatedImages.images.addAll(currentImages.images);
            for (FlickrSearchResponse.Photo photo : flickrSearchResponse.photos.photo) {
                concatenatedImages.images.add(new ImageToDisplay(
                        new ImageToDisplay.ImageInfo("https://live.staticflickr.com/" + photo.server + '/' + photo.id + '_' + photo.secret + '_' + "q.png",
                                1,
                                1),
                        new ImageToDisplay.ImageInfo("https://live.staticflickr.com/" + photo.server + '/' + photo.id + '_' + photo.secret + '_' + "b.png",
                                1,
                                1)));
            }
            imagesToDisplayLiveData.setValue(concatenatedImages);
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

            imageToDisplay = new ImageToDisplay(thumb, fullImage);

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