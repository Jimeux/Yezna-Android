package com.moobasoft.yezna.rest.models;

public class AccessToken {

    private int expiresIn;
    private String accessToken;
    private String refreshToken;
    private User user;

    public AccessToken() {}

    public AccessToken(String accessToken, String refreshToken, int expiresIn, User user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.user = user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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