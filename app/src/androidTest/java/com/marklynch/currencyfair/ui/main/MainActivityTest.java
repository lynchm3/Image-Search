package com.marklynch.currencyfair.ui.main;

import android.content.res.Resources;

import androidx.test.espresso.intent.rule.IntentsTestRule;

import com.marklynch.currencyfair.MainActivity;

import org.junit.Rule;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

public class MainActivityTest {

    @Rule
    IntentsTestRule activityTestRule = new IntentsTestRule(MainActivity.class, false, false);

    private Resources resources = getInstrumentation().getTargetContext().getResources();
}
