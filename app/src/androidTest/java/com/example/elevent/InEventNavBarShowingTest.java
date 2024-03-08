package com.example.elevent;


import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class InEventNavBarShowingTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void inEventNavBarShowingTest() {
        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.nav_bar_allevents), withContentDescription("All Events"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.activity_main_navigation_bar),
                                        0),
                                0),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        DataInteraction linearLayout = onData(anything())
                .inAdapterView(allOf(withId(R.id.list_view),
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                1)))
                .atPosition(0);
        linearLayout.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.notification_text), withText("Most Recent Notification Here"),
                        withParent(allOf(withId(R.id.eventViewAttendee),
                                withParent(withId(R.id.activity_main_framelayout)))),
                        isDisplayed()));
        textView.check(matches(withText("Most Recent Notification Here")));

        ViewInteraction frameLayout = onView(
                allOf(withId(R.id.nav_bar_allevents), withContentDescription("All Events"),
                        withParent(withParent(withId(R.id.activity_main_navigation_bar))),
                        isDisplayed()));
        frameLayout.check(matches(isDisplayed()));

        ViewInteraction frameLayout2 = onView(
                allOf(withId(R.id.nav_bar_myevents), withContentDescription("My Events"),
                        withParent(withParent(withId(R.id.activity_main_navigation_bar))),
                        isDisplayed()));
        frameLayout2.check(matches(isDisplayed()));

        ViewInteraction frameLayout3 = onView(
                allOf(withId(R.id.nav_bar_scanner), withContentDescription("Scanner"),
                        withParent(withParent(withId(R.id.activity_main_navigation_bar))),
                        isDisplayed()));
        frameLayout3.check(matches(isDisplayed()));

        ViewInteraction frameLayout4 = onView(
                allOf(withId(R.id.nav_bar_profile), withContentDescription("Profile"),
                        withParent(withParent(withId(R.id.activity_main_navigation_bar))),
                        isDisplayed()));
        frameLayout4.check(matches(isDisplayed()));

        ViewInteraction frameLayout5 = onView(
                allOf(withId(R.id.nav_bar_profile), withContentDescription("Profile"),
                        withParent(withParent(withId(R.id.activity_main_navigation_bar))),
                        isDisplayed()));
        frameLayout5.check(matches(isDisplayed()));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
