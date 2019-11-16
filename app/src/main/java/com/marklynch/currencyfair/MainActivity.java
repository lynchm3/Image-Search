package com.marklynch.currencyfair;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.marklynch.currencyfair.io.flickr.response.FlickrGetSizesResponse;
import com.marklynch.currencyfair.ui.main.ImageToDisplay;
import com.marklynch.currencyfair.ui.main.ImagesAdapter;
import com.marklynch.currencyfair.ui.main.MainViewModel;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements ImagesAdapter.ImageZoomer {

    private MainViewModel viewModel;
//    private SwipeRefreshLayout swipeRefreshLayout;

    private static int actionBarHeightPixels;//56dp

    private boolean loading = false;
    private int currentPage = 1;
    private String currentSearchQuery = null;

    private Animator currentAnimator;
    private int shortAnimationDuration;

    private RelativeLayout searchLoading;
    private RelativeLayout scrollLoadingLayout;
    private RelativeLayout largeImageLoadingLayout;
    private RelativeLayout expandedImageMask;
    private boolean hidingSearchView = false;


    ImageView expandedImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        viewModel = new MainViewModel(getApplication());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        //Set colors for navigation and status bars
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }


        expandedImageView = findViewById(
                R.id.expanded_image);

        //Loading animations
        searchLoading = findViewById(R.id.search_loading);
        scrollLoadingLayout = findViewById(R.id.scroll_loading);
        largeImageLoadingLayout = findViewById(R.id.expanded_image_loading);
        expandedImageMask = findViewById(R.id.expanded_image_mask);

        //Animation
        shortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        //SearchView
        SearchView searchView = findViewById(R.id.search_view);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                hideKeyboard();
                currentPage = 1;
                currentSearchQuery = query;
                viewModel.retrieveSearchResults(currentSearchQuery, currentPage, true);
                loading = true;
                searchLoading.setVisibility(View.VISIBLE);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                return true;
            }
        });
        searchView.setIconifiedByDefault(false);

        //Recycler view recycler_view
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        ImagesAdapter recyclerViewAdapter = new ImagesAdapter(this, this);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                //SHow/Hide searchView
                if (!hidingSearchView && dy > 10) {
                    searchView.animate().translationY(-200);
                    hidingSearchView = true;
                } else if (hidingSearchView && dy < -10) {
                    searchView.animate().translationY(0);
                    hidingSearchView = false;
                }


                //Load ahead if getting to end of images
                int maxScroll = recyclerView.computeVerticalScrollRange();
                int currentScroll = recyclerView.computeVerticalScrollOffset() + recyclerView.computeVerticalScrollExtent();
                if (loading == false && maxScroll - currentScroll < 2000 && currentSearchQuery != null) {
                    Timber.d("INFINITE");
                    Timber.d("INFINITE maxScroll = " + maxScroll);
                    Timber.d("INFINITE currentScroll = " + currentScroll);
                    Timber.d("INFINITE maxScroll - currentScroll = " + (maxScroll - currentScroll));
                    loading = true;
                    scrollLoadingLayout.setVisibility(View.VISIBLE);
                    viewModel.retrieveSearchResults(currentSearchQuery, ++currentPage, false);
                }
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                }
            }
        });

        //Fragment
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.container, MainFragment.newInstance())
//                    .commitNow();
//        }

        //Swipe refresh
//        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
//        swipeRefreshLayout.setEnabled(false);
//        swipeRefreshLayout.setProgressViewOffset(true, 0, 256);

        // Observer photos livedata
        viewModel.photoUrls.observe(this,
                photoUrls ->
                {
                    Timber.d("OBSERVE");
                    loading = false;
                    searchLoading.setVisibility(View.GONE);
                    scrollLoadingLayout.setVisibility(View.GONE);
                    recyclerViewAdapter.setImagesToDisplay(photoUrls);
                });

        searchView.requestFocus();
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    private static RequestOptions options = new RequestOptions().centerInside();

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

        RelativeLayout expandedImageHolder = findViewById(R.id.expanded_image_holder);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        FlickrGetSizesResponse.ImageSize large = imageToDisplay.large;
        if (large.width > large.height) {
            float ratio = (float) large.height / (float) large.width;
            float widthToDrawAt = screenWidth * ratio;
            expandedImageHolder.getLayoutParams().width = (int) widthToDrawAt;
        }

        RequestOptions options = new RequestOptions().placeholder(thumbView.getDrawable());

        Glide.with(getApplication())
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
        findViewById(R.id.frame)
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

    @Override
    public void onBackPressed() {
        if (expandedImageView.getVisibility() == View.VISIBLE)
            expandedImageView.callOnClick();
        else
            super.onBackPressed();
    }
}
