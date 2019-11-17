package com.marklynch.currencyfair.ui.main;

import android.app.Activity;
import android.content.res.Resources;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.intent.rule.IntentsTestRule;

import com.marklynch.currencyfair.MainActivity;
import com.marklynch.currencyfair.R;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static com.marklynch.currencyfair.EspressoWrapper.assertKeyboardShown;
import static com.marklynch.currencyfair.EspressoWrapper.assertViewVisible;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MainActivityTest {

    @Rule
    public IntentsTestRule activityTestRule = new IntentsTestRule(MainActivity.class, false, false);

    public Resources resources = getInstrumentation().getTargetContext().getResources();

    @Test
    public void testInitialState() {
        activityTestRule.launchActivity(null);
        initialStateChecks();
        activityTestRule.finishActivity();
    }

    @Test
    public void testSearch() throws InterruptedException {
        Activity activity = activityTestRule.launchActivity(null);

        initialStateChecks();

        onView(isAssignableFrom(EditText.class)).perform(typeText("Puppy"), pressImeActionButton());

        RelativeLayout searchProgressBar = activity.findViewById(R.id.search_progress_bar);
        while (searchProgressBar.getVisibility() == View.VISIBLE) {
            Thread.sleep(1_000);
        }

        RecyclerView recyclerView = activity.findViewById(R.id.recycler_view);

        activity.runOnUiThread(() -> recyclerView.scrollBy(0, 10_000));

        Thread.sleep(1_000);

        RelativeLayout infiniteScrollProgressBar = activity.findViewById(R.id.infinite_scroll_progress_bar);
        while (infiniteScrollProgressBar.getVisibility() == View.VISIBLE) {
            Thread.sleep(1_000);
        }

        activityTestRule.finishActivity();
    }

    public void initialStateChecks() {
        SearchView searchView = activityTestRule.getActivity().findViewById(R.id.search_view);
        assertEquals("", searchView.getQuery().toString());
        assertEquals(resources.getString(R.string.query_hint), searchView.getQueryHint().toString());
        assertTrue(searchView.hasFocus());
        assertViewVisible(R.id.search_view);
        assertKeyboardShown(activityTestRule.getActivity());
    }
}