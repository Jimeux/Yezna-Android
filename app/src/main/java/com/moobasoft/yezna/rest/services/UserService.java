package com.moobasoft.yezna.rest.services;

import com.moobasoft.yezna.rest.models.AccessToken;
import com.moobasoft.yezna.rest.models.User;
import com.moobasoft.yezna.rest.requests.RegistrationRequest;
import com.squareup.okhttp.Response;

import retrofit.Result;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;

public interface UserService {

    @FormUrlEncoded
    @POST("/oauth/token")
    Observable<Result<AccessToken>> getAccessToken(
            @Field("username") String username,
            @Field("password") String password,
            @Field("grant_type") String grantType);

    /** This method is called synchronously */
    @FormUrlEncoded
    @POST("/oauth/token")
    AccessToken refreshAccessToken(
            @Field("refresh_token") String refreshToken,
            @Field("grant_type") String grantType);

    @POST("/api/users")
    Observable<Result<AccessToken>> register(@Body RegistrationRequest user);

    @GET("/api/users/{username}")
    Observable<User> getUser(@Path("username") String username);

    /*@Multipart
    @POST("/api/users/avatar")
    Observable<User> uploadAvatar(@Part("avatar") TypedFile avatar);*/

    @FormUrlEncoded
    @POST("/api/users/avatar")
    Observable<User> saveAvatarUrl(@Field("avatar") String url);

    @POST("/oauth/revoke")
    Observable<Response> logOut();

}