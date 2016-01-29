package com.moobasoft.yezna.fixtures;

import com.moobasoft.yezna.rest.models.AccessToken;
import com.moobasoft.yezna.rest.models.User;

public class AccessTokens {

    public static final AccessToken accessToken1 =
            new AccessToken("12345token", "12345refresh", 54321, new User());

}
