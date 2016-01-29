package com.moobasoft.yezna.rest.services;

import com.moobasoft.yezna.rest.models.AccessToken;
import com.moobasoft.yezna.rest.models.Question;
import com.moobasoft.yezna.rest.models.User;
import com.moobasoft.yezna.rest.requests.RegistrationRequest;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface UserService {

    @FormUrlEncoded
    @POST("/oauth/token")
    Observable<AccessToken> getAccessToken(
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
    Observable<AccessToken> register(@Body RegistrationRequest user);

    @GET("/api/users/{username}")
    Observable<User> getUser(@Path("username") String username);

    @GET("/api/users/questions")
    Observable<List<Question>> getQuestions(@Query("page") int page);

    @Multipart
    @PATCH("/api/users/")
    Observable<User> updateProfile(@Part("user[username]") RequestBody username,
                                   @Part("user[email]") RequestBody email,
                                   @Part("user[password]") RequestBody password,
                                   @Part("user[avatar]\"; filename=\"filename\" ") RequestBody avatar);

    @FormUrlEncoded
    @POST("/api/users/avatar")
    Observable<User> saveAvatarUrl(@Field("avatar") String url);

    @POST("/oauth/revoke")
    Observable<Response> logOut();

}