package com.moobasoft.yezna.specs;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import com.moobasoft.yezna.PageTestCase;
import com.moobasoft.yezna.R;
import com.moobasoft.yezna.pages.PublicQuestionsPage;

import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
@SmallTest
public class PublicQuestionsPageTest extends PageTestCase {

    @Test
    public void click_yesButton_prompts_for_login() throws Exception {
        getInitialPage();
        PublicQuestionsPage page = new PublicQuestionsPage();

        page.clickYesButton();
        assertSnackbarTextEquals(R.string.sign_in_to_answer);
    }

    @Test
    public void click_NoButton_prompts_for_login() throws Exception {
        getInitialPage();
        PublicQuestionsPage page = new PublicQuestionsPage();

        page.clickNoButton();
        assertSnackbarTextEquals(R.string.sign_in_to_answer);
    }

    @Test
    public void click_NoButton_() throws Exception {
        getInitialPage();
        PublicQuestionsPage page = new PublicQuestionsPage();

        page.clickNoButton();
        assertSnackbarTextEquals(R.string.sign_in_to_answer);
    }

}