package com.marklynch.currencyfair;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.marklynch.currencyfair.ui.main.ImagesFragment;
import com.marklynch.currencyfair.ui.main.ImagesToDisplay;
import com.marklynch.currencyfair.ui.main.MainViewModel;

public class MainActivity extends AppCompatActivity implements ImagesFragment.FragmentScrollListener, ImagesFragment.ImagesLiveDataProvider {

    private boolean hidingSearchView = false;

    private boolean loading = false;
    private int currentPage = 1;
    private String currentSearchQuery = null;

    private RelativeLayout searchProgressBar;
    private RelativeLayout infiniteScrollProgressBar;
    private SearchView searchView;

    private MainViewModel viewModel;

    Toolbar toolbar;

    private enum SCROLL_DIRECTION {NONE, UP, DOWN}
    private static final int SCROLL_REMAINING_WHEN_LOAD_NEXT_PAGE = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        viewModel = new MainViewModel(getApplication());

        //Allow drawing under status bar and navigation bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        //Progress bars
        searchProgressBar = findViewById(R.id.search_progress_bar);
        infiniteScrollProgressBar = findViewById(R.id.infinite_scroll_progress_bar);

        //Set colors for navigation and status bars
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            findViewById(R.id.navigation_bar).setBackgroundResource(R.color.black_translucent);
        } else {
            findViewById(R.id.status_bar).setBackgroundResource(R.color.black_translucent);
            findViewById(R.id.navigation_bar).setBackgroundResource(R.color.black_translucent);
        }

        //Toolbar
         toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        //SearchView
        searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                hideKeyboard();
                currentPage = 1;
                currentSearchQuery = query;
                viewModel.retrieveSearchResults(currentSearchQuery, currentPage, true);
                loading = true;
                searchProgressBar.setVisibility(View.VISIBLE);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                return true;
            }
        });
        searchView.setIconifiedByDefault(false);

        //Images Fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, ImagesFragment.newInstance())
                    .commitNow();

        }

        // Observer photos livedata
        viewModel.imageToDisplayLiveData.observe(this,
                imagesToDisplay ->
                {
                    loading = false;
                    searchProgressBar.setVisibility(View.GONE);
                    infiniteScrollProgressBar.setVisibility(View.GONE);

                    if (imagesToDisplay.errorMessage != null)
                        Toast.makeText(this, imagesToDisplay.errorMessage, Toast.LENGTH_SHORT).show();
                });

        searchView.requestFocus();
    }

    public void hideSearchView() {
        toolbar.animate().translationY(-200);
        hidingSearchView = true;
    }

    public void showSearchView() {
        toolbar.animate().translationY(0);
        hidingSearchView = false;
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

        SCROLL_DIRECTION scrollDirection = SCROLL_DIRECTION.NONE;

        if (dy > 0) scrollDirection = SCROLL_DIRECTION.DOWN;
        else if (dy < 0) scrollDirection = SCROLL_DIRECTION.UP;

        //Show/Hide searchView
        if (scrollDirection == SCROLL_DIRECTION.DOWN && !hidingSearchView) {
            hideSearchView();
        } else if (scrollDirection == SCROLL_DIRECTION.UP && hidingSearchView) {
            showSearchView();
        }

        //Load ahead if getting to end of images
        if (scrollDirection == SCROLL_DIRECTION.DOWN && !loading) {
            int maxScroll = recyclerView.computeVerticalScrollRange();
            int currentScroll = recyclerView.computeVerticalScrollOffset() + recyclerView.computeVerticalScrollExtent();
            if (maxScroll - currentScroll < SCROLL_REMAINING_WHEN_LOAD_NEXT_PAGE) {
                loadNextPage();
            }
        }
    }

    public void loadNextPage() {
        loading = true;
        infiniteScrollProgressBar.setVisibility(View.VISIBLE);
        viewModel.retrieveSearchResults(currentSearchQuery, ++currentPage, false);
    }

    @Override
    public LiveData<ImagesToDisplay> getImagesLiveData() {
        return viewModel.imageToDisplayLiveData;
    }
}
