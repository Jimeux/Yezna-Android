package com.moobasoft.yezna.specs;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import com.moobasoft.yezna.PageTestCase;
import com.moobasoft.yezna.R;

import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.Visibility.GONE;
import static android.support.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


@RunWith(AndroidJUnit4.class)
@SmallTest
public class MainPageTest extends PageTestCase {

    @Test
    public void menuClicks_show_loginPrompt_when_signedOut() throws Exception {
        getInitialPage();

        onView(withId(R.id.action_ask_question)).perform(click());
        assertSnackbarTextEquals(R.string.error_unauthorized);
        onView(withId(R.id.action_my_questions)).perform(click());
        assertSnackbarTextEquals(R.string.error_unauthorized);
    }

    @Test
    public void click_askQuestion_MenuItem_opens_page() throws Exception {
        getInitialPageSignedIn();

        onView(withId(R.id.action_ask_question)).perform(click());
        assertSnackbarNotDisplayed();
        onView(withId(R.id.ask_question_page)).check(matches(isDisplayed()));
    }

    @Test
    public void click_myQuestions_MenuItem_opens_page() throws Exception {
        getInitialPageSignedIn();

        onView(withId(R.id.action_my_questions)).perform(click());
        assertSnackbarNotDisplayed();
        onView(withId(R.id.my_questions_page)).check(matches(isDisplayed()));
    }

    @Test
    public void contentView_displayed_onQuestionSuccess() throws Exception {
        getInitialPage();

        assertHasVisibility(R.id.content, VISIBLE);
        assertHasVisibility(R.id.error_view, GONE);
        assertHasVisibility(R.id.loading_view, GONE);
        assertHasVisibility(R.id.empty_view, GONE);
    }

    @Test
    public void emptyView_displayed_onEmptyQuestionArray() throws Exception {
        enqueueResponse(200, "[]");
        getInitialPage();

        assertHasVisibility(R.id.content, GONE);
        assertHasVisibility(R.id.error_view, GONE);
        assertHasVisibility(R.id.loading_view, GONE);
        assertHasVisibility(R.id.empty_view, VISIBLE);
    }

    @Test
    public void errorView_displayed_onQuestionError() throws Exception {
        enqueueResponse(500);
        getInitialPage();

        assertHasVisibility(R.id.error_view, VISIBLE);
        assertHasVisibility(R.id.content, GONE);
        assertHasVisibility(R.id.empty_view, GONE);
        assertHasVisibility(R.id.loading_view, GONE);
    }

}