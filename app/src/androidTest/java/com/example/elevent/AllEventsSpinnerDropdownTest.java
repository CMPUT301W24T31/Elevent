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
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class AllEventsSpinnerDropdownTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void allEventsSpinnerDropdownTest() {
        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.nav_bar_allevents), withContentDescription("All Events"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.activity_main_navigation_bar),
                                        0),
                                0),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction appCompatSpinner = onView(
                allOf(withId(R.id.event_filter_spinner),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.FrameLayout")),
                                        0),
                                0),
                        isDisplayed()));
        appCompatSpinner.perform(click());

        ViewInteraction checkedTextView = onView(
                allOf(withId(android.R.id.text1), withText("all"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class))),
                        isDisplayed()));
        checkedTextView.check(matches(isDisplayed()));

        ViewInteraction checkedTextView2 = onView(
                allOf(withId(android.R.id.text1), withText("signed-up"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class))),
                        isDisplayed()));
        checkedTextView2.check(matches(isDisplayed()));

        ViewInteraction checkedTextView3 = onView(
                allOf(withId(android.R.id.text1), withText("signed-up"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class))),
                        isDisplayed()));
        checkedTextView3.check(matches(isDisplayed()));

        DataInteraction appCompatCheckedTextView = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(1);
        appCompatCheckedTextView.perform(click());

        ViewInteraction appCompatSpinner2 = onView(
                allOf(withId(R.id.event_filter_spinner),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.FrameLayout")),
                                        0),
                                0),
                        isDisplayed()));
        appCompatSpinner2.perform(click());

        DataInteraction appCompatCheckedTextView2 = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(0);
        appCompatCheckedTextView2.perform(click());

        ViewInteraction appCompatSpinner3 = onView(
                allOf(withId(R.id.event_filter_spinner),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.FrameLayout")),
                                        0),
                                0),
                        isDisplayed()));
        appCompatSpinner3.perform(click());

        DataInteraction appCompatCheckedTextView3 = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(1);
        appCompatCheckedTextView3.perform(click());

        ViewInteraction appCompatSpinner4 = onView(
                allOf(withId(R.id.event_filter_spinner),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.FrameLayout")),
                                        0),
                                0),
                        isDisplayed()));
        appCompatSpinner4.perform(click());

        ViewInteraction checkedTextView4 = onView(
                allOf(withId(android.R.id.text1), withText("all"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class))),
                        isDisplayed()));
        checkedTextView4.check(matches(isDisplayed()));

        ViewInteraction checkedTextView5 = onView(
                allOf(withId(android.R.id.text1), withText("signed-up"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class))),
                        isDisplayed()));
        checkedTextView5.check(matches(isDisplayed()));
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
