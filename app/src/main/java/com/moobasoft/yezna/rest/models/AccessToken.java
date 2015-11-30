package com.moobasoft.yezna.rest.models;

public class AccessToken {

    private int expiresIn;
    private String accessToken;
    private String refreshToken;

    public AccessToken() {}

    public AccessToken(String accessToken, String refreshToken, int expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof AccessToken &&
                ((AccessToken) o).getAccessToken().equals(accessToken);
    }

    @Override
    public String toString() {
        return "Access: " + accessToken + "\nRefresh: " + refreshToken + "\nExpires:" + expiresIn;
    }
}