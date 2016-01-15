package com.moobasoft.yezna.rest.auth;

import android.support.annotation.Nullable;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

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
    public Request authenticate(Route route, Response response) throws IOException {
        if (response.request().url().encodedPath().contains("oauth") ||
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
}