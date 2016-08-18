package com.example.yanbraslavski.bitcoingraph;

import com.example.yanbraslavski.bitcoingraph.main.MainActivity;
import com.example.yanbraslavski.bitcoingraph.main.MainPresenter;
import com.example.yanbraslavski.bitcoingraph.utils.AppUtils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.espresso.IdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.ProgressBar;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.registerIdlingResources;
import static android.support.test.espresso.Espresso.unregisterIdlingResources;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;

/**
 * This test makes sure that our activity presents all required elements
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule(MainActivity.class);

    @Test
    public void testAllElementsAppear() {
        onView(withId(R.id.graph_view)).check(matches(isCompletelyDisplayed()));
        if (AppUtils.isPortrait(mActivityRule.getActivity())) {
            onView(withId(R.id.graph_title_text_view)).check(matches(isCompletelyDisplayed()));
        }
    }

    @Test
    public void testPresenterDataIsLoaded() {
        //register presenter as an idling resource
        IdlingResource idlingResource = createPresenterIdlingResource((MainPresenter) mActivityRule.getActivity().getMainPresenter());
        registerIdlingResources(idlingResource);

        //make sure the graph is there
        onView(withId(R.id.graph_view)).check(matches(isCompletelyDisplayed()));

        //make sure the title is correct
        if (AppUtils.isPortrait(mActivityRule.getActivity())) {
            onView(allOf(withId(R.id.graph_title_text_view), withText("Market Price (USD)")));
        }

        unregisterIdlingResources(idlingResource);
    }

    /**
     * Since presenter loads it's data asynchronously using RxJava
     * we need to mark it as an idling resource as long as it loads the data
     *
     * NOTE : there are several solutions for rxJava with espresso , I cannot confirm
     * that they are reliable or work with up to date espresso and RxJava libraries....
     * Therefore , I will not use them in this project.
     */
    private IdlingResource createPresenterIdlingResource(final MainPresenter mainPresenter) {
        return new IdlingResource() {
            private ResourceCallback _resourceCallback;

            @Override
            public String getName() {
                return ProgressBar.class.getSimpleName();
            }

            @Override
            public boolean isIdleNow() {
                final boolean isIdle = mainPresenter.getCachedDisplayModel().isPresent();
                if (isIdle && _resourceCallback != null)
                    _resourceCallback.onTransitionToIdle();
                return isIdle;
            }

            @Override
            public void registerIdleTransitionCallback(ResourceCallback callback) {
                _resourceCallback = callback;
            }
        };
    }

}
