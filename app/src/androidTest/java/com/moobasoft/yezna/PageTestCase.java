package com.moobasoft.yezna;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;

import com.moobasoft.yezna.di.components.AppComponent;
import com.moobasoft.yezna.di.modules.AppModule;
import com.moobasoft.yezna.di.modules.EndpointModule;
import com.moobasoft.yezna.di.modules.RestModule;
import com.moobasoft.yezna.fixtures.AccessTokens;
import com.moobasoft.yezna.fixtures.Users;
import com.moobasoft.yezna.pages.MainPage;
import com.moobasoft.yezna.rest.auth.CredentialStore;
import com.moobasoft.yezna.ui.activities.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Component;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.QueueDispatcher;
import okhttp3.mockwebserver.RecordedRequest;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.moobasoft.yezna.RestServiceTestHelper.getStringFromFile;
import static org.hamcrest.Matchers.allOf;

public abstract class PageTestCase {

    @Singleton
    @Component(modules = {AppModule.class, RestModule.class, EndpointModule.class})
    public interface TestComponent extends AppComponent {
        void inject(PageTestCase pageTestCase);
    }

    @Inject CredentialStore credentialStore;

    @Rule
    public ActivityTestRule<MainActivity> testRule = new ActivityTestRule<>(MainActivity.class, true, false);

    protected App app;
    protected Context context;
    protected MockWebServer server;

    protected final Dispatcher dispatcher = new QueueDispatcher() {
        @Override
        public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
            if (!responseQueue.isEmpty()) {
                return super.dispatch(request);
            } else {
                if (request.getPath().matches("/oauth/token"))
                    return createResponseFromFile(200, "token.json");
                else if (request.getPath().matches("/api/questions.*"))
                    return createResponseFromFile(200, "index.json");
                else if (request.getPath().matches("/api/users/\\w+"))
                    return createResponseFromFile(200, "user.json");
                else
                    return createResponse(404);
            }
        }
    };

    protected MainPage getInitialPage() {
        testRule.launchActivity(new Intent());
        return new MainPage();
    }

    protected MainPage getInitialPageSignedIn() {
        credentialStore.saveToken(AccessTokens.accessToken1);
        credentialStore.saveUser(Users.user1);
        return getInitialPage();
    }

    @Before
    public void setUp() throws Exception {
        SharedPreferences preferences = InstrumentationRegistry.getTargetContext()
                .getSharedPreferences("credentials", Context.MODE_PRIVATE);
        preferences.edit().clear().commit();

        context = InstrumentationRegistry.getContext();
        app = (App) InstrumentationRegistry.getTargetContext().getApplicationContext();
        server = new MockWebServer();
        server.setDispatcher(dispatcher);
        server.start();

        EndpointModule endpointModule = new EndpointModule() {
            @Override public String provideEndpoint() {
                return server.url("/").toString();
            }
        };

        TestComponent component = DaggerPageTestCase_TestComponent.builder()
                .appModule(new AppModule(app))
                .endpointModule(endpointModule)
                .build();

        component.inject(this);
        app.setComponent(component);
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }

    /****************************************
     *** Convenience methods for Espresso ***
     ****************************************/

    public static void assertHasVisibility(int id, ViewMatchers.Visibility visibility) {
        onView(withId(id)).check(matches(withEffectiveVisibility(visibility)));
    }

    public static void assertSnackbarTextEquals(int stringResource) {
        onView(allOf(withId(android.support.design.R.id.snackbar_text), withText(stringResource)))
                .check(matches(isDisplayed()));
    }

    public static void assertSnackbarNotDisplayed() {
        onView(withId(android.support.design.R.id.snackbar_text))
                .check(doesNotExist());
    }

    /*********************************************
     *** Convenience methods for MockWebServer ***
     *********************************************/

    public void enqueueResponse(int code) {
        server.enqueue(buildResponse(code, null, null));
    }

    public void enqueueResponse(int code, String body) {
        server.enqueue(buildResponse(code, null, body));
    }

    public void enqueueResponseFromFile(int code, String filename) {
        server.enqueue(buildResponse(code, filename, null));
    }

    public MockResponse createResponse(int code) {
        return buildResponse(code, null, null);
    }

    public MockResponse createResponse(int code, String body) {
        return buildResponse(code, null, body);
    }

    public MockResponse createResponseFromFile(int code, String filename) {
        return buildResponse(code, filename, null);
    }

    public MockResponse buildResponse(int code, String filename, String body) {
        MockResponse mockResponse = new MockResponse();
        mockResponse.setResponseCode(code);

        if (filename != null) {
            try {
                mockResponse.setBody(getStringFromFile(context, filename));
            } catch (IOException e) {
                throw new IllegalArgumentException("Invalid filename.");
            }
        } else if (body != null) {
            mockResponse.setBody(body);
        }

        return mockResponse;
    }

}