package com.moobasoft.yezna;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.ViewMatchers;

import com.moobasoft.yezna.di.components.DaggerAppComponent;
import com.moobasoft.yezna.di.modules.AppModule;
import com.moobasoft.yezna.di.modules.EndpointModule;

import org.junit.After;
import org.junit.Before;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.moobasoft.yezna.RestServiceTestHelper.getStringFromFile;

public abstract class MockServerTest {
    protected App app;
    protected Context context;
    protected MockWebServer server;

    @Before
    public void setUp() throws Exception {
        context = InstrumentationRegistry.getContext();
        app = (App) InstrumentationRegistry.getTargetContext().getApplicationContext();
        server = new MockWebServer();
        server.start();

        EndpointModule endpointModule = new EndpointModule() {
            @Override public String provideEndpoint() { // Do NOT add @Provides or @MyScope
                return server.url("/").toString();
            }
        };

        app.setComponent(DaggerAppComponent.builder()
                .appModule(new AppModule(app))
                .endpointModule(endpointModule)
                .build());
    }

    @After
    public void tearDown() throws Exception{
        server.shutdown();
    }

    public static void ensureHasVisibility(int id, ViewMatchers.Visibility visibility) {
        onView(withId(id)).check(matches(withEffectiveVisibility(visibility)));
    }

    public void enqueueResponse(int code) throws Exception {
        enqueueResponse(code, null);
    }

    public void enqueueResponse(int code, String filename) throws Exception {
        MockResponse mockResponse = new MockResponse();
        mockResponse.setResponseCode(code);
        if (filename != null)
            mockResponse.setBody(getStringFromFile(context, filename));
        server.enqueue(mockResponse);
    }
}
