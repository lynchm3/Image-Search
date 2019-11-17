package com.marklynch.currencyfair.ui.main;

import android.content.Context;
import android.content.res.Resources;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.widget.SearchView;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;

import com.marklynch.currencyfair.MainActivity;
import com.marklynch.currencyfair.R;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MainActivityTest {

    @Rule
    public IntentsTestRule activityTestRule = new IntentsTestRule(MainActivity.class, false, false);

    public Resources resources = getInstrumentation().getTargetContext().getResources();

    @Test
    public void checkInitialState() {
        activityTestRule.launchActivity(null);
        SearchView searchView = activityTestRule.getActivity().findViewById(R.id.search_view);
        assertEquals("", searchView.getQuery().toString());
        assertEquals(resources.getString(R.string.query_hint), searchView.getQueryHint().toString());
        assertTrue(searchView.hasFocus());
        assertViewVisible(R.id.search_view);
        assertKeyboardShown();
        activityTestRule.finishActivity();
    }

    public void assertViewVisible(int viewId) {
        onView(withId(viewId)).check(
                ViewAssertions.matches(
                        withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)
                )
        );
    }

    public void assertKeyboardShown() {
        InputMethodManager inputMethodManager = (InputMethodManager) activityTestRule.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        assertTrue(inputMethodManager.isAcceptingText());
    }
}
