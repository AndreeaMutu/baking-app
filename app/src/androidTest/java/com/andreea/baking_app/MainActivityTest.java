package com.andreea.baking_app;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;

import com.andreea.baking_app.ui.MainActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.equalTo;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    private static final int BROWNIES_POSITION = 1;
    private static final String BROWNIES_TITLE = "Brownies";
    private CountingIdlingResource mainActivityIdlingResource;
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void registerIdlingResource() {
        mainActivityIdlingResource = mActivityRule.getActivity().getRecipesIdlingResource();
        IdlingRegistry.getInstance().register(mainActivityIdlingResource);
    }

    @After
    public void unRegisterIdlingResource() {
        if (mainActivityIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(mainActivityIdlingResource);
        }
    }

    @Test
    public void testItemAtPosition1IsBrownies() {
        onView(withId(R.id.recipe_list))
                .perform(actionOnItemAtPosition(BROWNIES_POSITION, scrollTo()))
                .check(matches(hasDescendant(withText(BROWNIES_TITLE))));
    }

    @Test
    public void testItemAtPosition1ClickOpensBrowniesDetails() {
        onView(withId(R.id.recipe_list))
                .perform(actionOnItemAtPosition(BROWNIES_POSITION, click()));
        onView(withId(R.id.toolbar)).check(matches(withToolbarTitle(equalTo(BROWNIES_TITLE))));
    }

    /**
     * From http://blog.sqisland.com/2015/05/espresso-match-toolbar-title.html
     */
    private static Matcher<Object> withToolbarTitle(final Matcher<CharSequence> textMatcher) {
        return new BoundedMatcher<Object, Toolbar>(Toolbar.class) {
            @Override
            public boolean matchesSafely(Toolbar toolbar) {
                return textMatcher.matches(toolbar.getTitle());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Toolbar title ");
                textMatcher.describeTo(description);
            }
        };
    }
}
