package com.marklynch.flickrsearch;

import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.widget.SearchView;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.ViewMatchers;

import org.hamcrest.Matcher;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertTrue;

public class EspressoWrapper {

    public static void assertViewVisible(int viewId) {
        onView(withId(viewId)).check(
                matches(
                        withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)
                )
        );
    }

    public static void assertKeyboardShown(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        assertTrue(inputMethodManager.isAcceptingText());
    }

    public static void clickViewWithId(int viewId) {
        onView(withId(viewId)).perform(click());
    }

    public static void clickViewWithText(String viewText) {
        onView(withText(viewText)).perform(click());
    }

    public static void typeTextInEditText(int viewId, String text) {
        onView(withId(viewId)).perform(typeText(text));
    }

    public static void clickIMEActionButtonForEditText(int viewId) {
        onView(withId(viewId))
                .perform(pressImeActionButton());
    }

    public static void typeTheLetterA()
    {
        onView(isRoot()).perform(pressKey(KeyEvent.KEYCODE_A));
    }

    public static void typeTextInSearchVIew(int viewId, String text) {
        onView(withId(viewId))
                .perform(typeSearchViewText(text));
    }

    public static ViewAction typeSearchViewText(final String text) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return allOf(isDisplayed(), isAssignableFrom(SearchView.class));
            }

            @Override
            public String getDescription() {
                return "Change view text";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((SearchView) view).setQuery(text, false);
            }
        };
    }
}
