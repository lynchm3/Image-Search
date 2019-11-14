package com.marklynch.currencyfair;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.marklynch.currencyfair.ui.main.ImagesAdapter;
import com.marklynch.currencyfair.ui.main.MainViewModel;

import java.io.IOException;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;
    private SwipeRefreshLayout swipeRefreshLayout;

    private static int actionBarHeightPixels;//56dp

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
                Timber.d("onQueryTextSubmit");
                retrieveSearchResults(query);
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
        ImagesAdapter recyclerViewAdapter = new ImagesAdapter(this);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        //Fragment
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.container, MainFragment.newInstance())
//                    .commitNow();
//        }

        //Swipe refresh
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setProgressViewOffset (true, 0, 192);

        // Observer photos livedata
        viewModel.photoUrls.observe(this,
                photoUrls ->
                {
                    swipeRefreshLayout.setRefreshing(false);
                    recyclerViewAdapter.setPhotoUrls(photoUrls);
                });
    }

    public void retrieveSearchResults(String query) {
        try {
            Timber.d("activity.retrieveSearchResults");
            viewModel.retrieveSearchResults(query);
            hideKeyboard();
            swipeRefreshLayout.setRefreshing(true);
        } catch (IOException e) {
            Timber.e(e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;

//        // Inflate the options menu from XML
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.main_menu, menu);
//
//        //Sackoverflow
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                searchRequest(query);
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String query) {
//                searchRequest(query);
//                return true;
//            }
//        });
//        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
//
//        return true;
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
