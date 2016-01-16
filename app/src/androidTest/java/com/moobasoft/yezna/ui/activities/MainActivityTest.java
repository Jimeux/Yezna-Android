package com.moobasoft.yezna.ui.activities;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import com.moobasoft.yezna.MockServerTest;
import com.moobasoft.yezna.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import okhttp3.mockwebserver.MockResponse;

import static android.support.test.espresso.matcher.ViewMatchers.Visibility.GONE;
import static android.support.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE;


@RunWith(AndroidJUnit4.class)
@SmallTest
public class MainActivityTest extends MockServerTest {

    @Rule
    public ActivityTestRule<MainActivity> testRule =
            new ActivityTestRule<>(MainActivity.class, true, false);

    @Test
    public void getQuestions200() throws Exception {
        enqueueResponse(200, "index.json");
        testRule.launchActivity(new Intent());

        ensureHasVisibility(R.id.content, VISIBLE);
        ensureHasVisibility(R.id.error_view, GONE);
        ensureHasVisibility(R.id.loading_view, GONE);
        ensureHasVisibility(R.id.empty_view, GONE);
    }

    @Test
    public void getQuestionsEmpty() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200).setBody("[]"));

        testRule.launchActivity(new Intent());

        ensureHasVisibility(R.id.content, GONE);
        ensureHasVisibility(R.id.error_view, GONE);
        ensureHasVisibility(R.id.loading_view, GONE);
        ensureHasVisibility(R.id.empty_view, VISIBLE);
    }

    @Test
    public void getQuestions500() throws Exception {
        enqueueResponse(500);
        testRule.launchActivity(new Intent());

        ensureHasVisibility(R.id.error_view, VISIBLE);
        ensureHasVisibility(R.id.content, GONE);
        ensureHasVisibility(R.id.empty_view, GONE);
        ensureHasVisibility(R.id.loading_view, GONE);
    }

}