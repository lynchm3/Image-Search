package com.marklynch.currencyfair.ui.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.marklynch.currencyfair.R;
import com.marklynch.currencyfair.io.flickr.response.FlickrGetSizesResponse;

public class ImagesFragment extends Fragment implements ImagesAdapter.ImageZoomer {

    private FragmentScrollListener fragmentScrollListener;
    private ImagesLiveDataProvider imagesLiveDataProvider;

    private Animator currentAnimator;
    private int shortAnimationDuration;

    private RelativeLayout largeImageLoadingLayout;
    private RelativeLayout expandedImageMask;

    ImageView expandedImageView;

    public static ImagesFragment newInstance() {
        return new ImagesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.images_fragment, container, false);

        //Expanded image view
        expandedImageView = rootView.findViewById(R.id.expanded_image);
        expandedImageMask = rootView.findViewById(R.id.expanded_image_mask);

        //Progress bars
        largeImageLoadingLayout = rootView.findViewById(R.id.expanded_image_progress_bar);

        //Animation
        shortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);


        //Recycler view
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        ImagesAdapter recyclerViewAdapter = new ImagesAdapter(getActivity(), this);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                fragmentScrollListener.onScrolled(recyclerView, dx, dy);
            }
        });

        imagesLiveDataProvider.getImagesLiveData().observe(this,
                imagesToDisplay ->
                {
                    recyclerViewAdapter.setImagesToDisplay(imagesToDisplay);
                });


        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            fragmentScrollListener = (FragmentScrollListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement FragmentScrollListener");
        }
        try {
            imagesLiveDataProvider = (ImagesLiveDataProvider) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement ImagesLiveDataProvider");
        }
    }

    public interface FragmentScrollListener {
        void onScrolled(RecyclerView recyclerView, int dx, int dy);
    }

    public interface ImagesLiveDataProvider {
        LiveData<ImagesToDisplay> getImagesLiveData();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void zoomImageFromThumb(final ImageView thumbView, ImageToDisplay imageToDisplay) {
        largeImageLoadingLayout.setVisibility(View.VISIBLE);
        expandedImageMask.setVisibility(View.VISIBLE);
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }

//        expandedImageView.setImageBitmap(thumbView.getDrawable());

        //CALCULATE WHERE AND WHAT SIZE TO DRAW THE THUMB
        //Do it simple first assume portrait phone
        //Assume portrait photo OK

        RelativeLayout expandedImageHolder = getView().findViewById(R.id.expanded_image_holder);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        FlickrGetSizesResponse.ImageSize large = imageToDisplay.large;
        if (large.width > large.height) {
            float ratio = (float) large.height / (float) large.width;
            float widthToDrawAt = screenWidth * ratio;
            expandedImageHolder.getLayoutParams().width = (int) widthToDrawAt;
        }

        RequestOptions options = new RequestOptions().placeholder(thumbView.getDrawable());

        Glide.with(getActivity())
                .load(imageToDisplay.large.source)
                .listener(
                        new RequestListener() {
                            @Override
                            public boolean onLoadFailed(GlideException e, Object model, Target target, boolean isFirstResource) {
                                largeImageLoadingLayout.setVisibility(View.GONE);
                                expandedImageHolder.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                                largeImageLoadingLayout.setVisibility(View.GONE);
                                expandedImageHolder.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                                return false;
                            }
                        }).apply(options).into(expandedImageView);


        // Load the high-resolution "zoomed-in" image.
//        final ImageView expandedImageView = (ImageView) findViewById(
//                R.id.expanded_image);
//        expandedImageView.setImageResource(R.drawable.ic_launcher_foreground);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        getView().findViewById(R.id.frame)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView,
                        View.SCALE_Y, startScale, 1f));
        set.setDuration(shortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                currentAnimator = null;
            }
        });
        set.start();
        currentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandedImageHolder.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                expandedImageMask.setVisibility(View.GONE);
                largeImageLoadingLayout.setVisibility(View.GONE);
                if (currentAnimator != null) {
                    currentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(shortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        currentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        currentAnimator = null;
                    }
                });
                set.start();
                currentAnimator = set;
            }
        });
    }

//    @Override
//    public void onBackPressed() {
//        if (expandedImageView.getVisibility() == View.VISIBLE)
//            expandedImageView.callOnClick();
//        else
//            super.onBackPressed();
//    }

}
