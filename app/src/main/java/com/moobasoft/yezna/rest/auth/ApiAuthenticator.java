package com.moobasoft.yezna.rest.auth;

import android.support.annotation.Nullable;

import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.Proxy;

public class ApiAuthenticator implements Authenticator {

    private final CredentialStore credentialStore;

    public ApiAuthenticator(CredentialStore credentialStore) {
        this.credentialStore = credentialStore;
    }

    /**
     * Called whenever a request is challenged for authentication.
     *
     * 1. The current access token is deleted and an attempt is
     *    made to get a new one using the refresh token.
     * 2. If a token is retrieved, the request is retried, otherwise
     *    the user is asked to login.
     */
    @Override
    public Request authenticate(Proxy proxy, Response response) throws IOException {
        if (response.request().url().getPath().contains("oauth") ||
            !response.message().trim().equalsIgnoreCase("Unauthorized"))
            return null;

        String authToken = attemptToRefreshToken();

        if (authToken != null) {
            return response.request().newBuilder()
                    .header("Authorization", "Bearer " + authToken)
                    .build();
        } else {
            credentialStore.delete(); // All tokens are invalid
            return null;
        }
    }

    @Nullable
    private String attemptToRefreshToken() {
        credentialStore.deleteAccessToken();
        /*try {
            return userService.refreshAccessToken(
                    accessToken.getRefreshToken(), REFRESH_GRANT);
        } catch (NullPointerException | RetrofitError error) {
            return null;
        }*/
        return null;
    }

    @Override
    public Request authenticateProxy(Proxy proxy, Response response) throws IOException {
        return null;
    }
}