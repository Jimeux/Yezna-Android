package com.moobasoft.yezna.rest.auth;

import android.content.SharedPreferences;
import android.text.TextUtils;

import com.moobasoft.yezna.rest.models.AccessToken;
import com.moobasoft.yezna.rest.models.User;

public class CredentialStore {

    private final SharedPreferences prefs;

    private static final String ACCESS_TOKEN  = "access_token";
    private static final String EXPIRES_IN    = "expires_in";
    private static final String REFRESH_TOKEN = "refresh_token";
    private static final String SCOPE         = "scope";

    private static final String USER_ID       = "user_id";
    private static final String USERNAME      = "username";
    private static final String AVATAR        = "avatar";

    public CredentialStore(SharedPreferences prefs) {
        this.prefs = prefs;
    }

    public boolean isLoggedIn() {
        return !TextUtils.isEmpty(getAccessToken());
    }

    public AccessToken loadAccessToken() {
        AccessToken accessToken = new AccessToken();
        accessToken.setAccessToken(prefs.getString(ACCESS_TOKEN, null));
        accessToken.setRefreshToken(prefs.getString(REFRESH_TOKEN, null));
        accessToken.setExpiresIn(prefs.getInt(EXPIRES_IN, -1));

        return accessToken;
    }

    public String getAccessToken() {
        return prefs.getString(ACCESS_TOKEN, null);
    }

    public void saveToken(AccessToken accessToken) {
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(ACCESS_TOKEN,  accessToken.getAccessToken());
        editor.putString(REFRESH_TOKEN, accessToken.getRefreshToken());
        editor.putInt(   EXPIRES_IN,    accessToken.getExpiresIn());
        editor.apply();
    }

    public User loadUser() {
        User user = new User();
        user.setId(prefs.getInt(USER_ID, -1));
        user.setUsername(prefs.getString(USERNAME, null));
        user.setAvatar(prefs.getString(AVATAR, null));

        return (user.getId() == -1) ? null : user;
    }

    public void saveUser(User user) {
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(   USER_ID,  user.getId());
        editor.putString(USERNAME, user.getUsername());
        editor.putString(AVATAR,   user.getAvatar());
        editor.apply();
    }

    public void deleteAccessToken() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(ACCESS_TOKEN);
        editor.apply();
    }

    public void delete() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(ACCESS_TOKEN);
        editor.remove(EXPIRES_IN);
        editor.remove(REFRESH_TOKEN);
        editor.remove(SCOPE);

        editor.remove(USER_ID);
        editor.remove(USERNAME);
        editor.remove(AVATAR);

        editor.apply();
    }

}