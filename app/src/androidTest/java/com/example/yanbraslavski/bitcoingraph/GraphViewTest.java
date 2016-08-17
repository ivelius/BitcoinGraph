package com.example.yanbraslavski.bitcoingraph;

import com.example.yanbraslavski.bitcoingraph.main.MainActivity;
import com.example.yanbraslavski.bitcoingraph.utils.AppUtils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;

/**
 * This test makes sure that our activity presents all required elements
 */
@RunWith(AndroidJUnit4.class)
public class GraphViewTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule(MainActivity.class);

    @Test
    public void testAllElementsAppear() {
        onView(ViewMatchers.withId(R.id.graph_view)).check(matches(isCompletelyDisplayed()));
        if (AppUtils.isPortrait(mActivityRule.getActivity())) {
            onView(ViewMatchers.withId(R.id.graph_title_text_view)).check(matches(isCompletelyDisplayed()));
        }
    }

}
