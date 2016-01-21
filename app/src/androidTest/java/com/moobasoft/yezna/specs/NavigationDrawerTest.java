package com.moobasoft.yezna.specs;

import android.support.test.espresso.ViewAssertion;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import com.moobasoft.yezna.PageTestCase;
import com.moobasoft.yezna.R;
import com.moobasoft.yezna.fixtures.Users;
import com.moobasoft.yezna.pages.MainPage;

import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;


@RunWith(AndroidJUnit4.class)
@SmallTest
public class NavigationDrawerTest extends PageTestCase {

    @Test
    public void opens_and_closes() throws Exception {
        MainPage mainPage = getInitialPage();

        onView(withId(R.id.drawer_header)).check(matches(not(isDisplayed())));
        mainPage.openDrawer();
        onView(withId(R.id.drawer_header)).check(matches(isDisplayed()));
        mainPage.closeDrawer();
        onView(withId(R.id.drawer_header)).check(matches(not(isDisplayed())));
    }

    @Test
    public void closes_on_select() throws Exception {
        getInitialPageSignedIn().openDrawer();
        onView(withId(R.id.drawer_header)).check(matches(isDisplayed()));
        onView(withText(R.string.public_questions)).perform(click());
        onView(withId(R.id.drawer_header)).check(matches(not(isDisplayed())));
    }

    @Test
    public void shows_defaultHeader_when_signedOut() throws Exception {
        getInitialPage().openDrawer();
        onView(withId(R.id.signed_in_username))
                .check(matches(withText(R.string.not_signed_in)));
        //TODO: Test avatar ImageView
    }

    @Test
    public void shows_userHeader_when_signedIn() throws Exception {
        getInitialPageSignedIn().openDrawer();
        String expected = testRule.getActivity()
                .getString(R.string.signed_in_as, Users.user1.getUsername());
        onView(withId(R.id.signed_in_username)).check(matches(withText(expected)));
        //TODO: Test avatar ImageView
    }

    @Test
    public void userMenu_hidden_when_signedOut() throws Exception {
        getInitialPage().openDrawer();
        authMenuMatches(matches(isDisplayed()));
        userMenuMatches(doesNotExist());
    }

    @Test
    public void userMenu_shown_when_signedIn() throws Exception {
        getInitialPageSignedIn().openDrawer();
        userMenuMatches(matches(isDisplayed()));
        authMenuMatches(doesNotExist());
    }

    @Test
    public void userMenu_hidden_after_signingOut() throws Exception {
        MainPage mainPage = getInitialPageSignedIn().openDrawer();

        userMenuMatches(matches(isDisplayed()));
        authMenuMatches(doesNotExist());

        mainPage.logOut().openDrawer();

        authMenuMatches(matches(isDisplayed()));
        userMenuMatches(doesNotExist());
    }

    @Test
    public void displaysMessage_and_redirects_onSignOut() throws Exception {
        getInitialPageSignedIn().navigateToAskQuestionPage()
                .logOut();

        assertSnackbarTextEquals(R.string.logout_success);
        onView(withId(R.id.public_questions_page)).check(matches(isDisplayed()));
    }

    private static void userMenuMatches(ViewAssertion assertion) {
        onView(withText(R.string.public_questions)).check(assertion);
        onView(withText(R.string.ask_question)).check(assertion);
        onView(withText(R.string.my_questions)).check(assertion);
        onView(withText(R.string.profile)).check(assertion);
        onView(withText(R.string.logout)).check(assertion);
    }

    private static void authMenuMatches(ViewAssertion assertion) {
        onView(withText(R.string.login)).check(assertion);
        onView(withText(R.string.register)).check(assertion);
    }

}