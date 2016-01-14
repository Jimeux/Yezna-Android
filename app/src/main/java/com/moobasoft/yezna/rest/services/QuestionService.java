package com.moobasoft.yezna.rest.services;

import com.moobasoft.yezna.rest.models.Question;
import com.moobasoft.yezna.rest.requests.AnswerRequest;

import java.util.List;

import retrofit.Result;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

import static com.moobasoft.yezna.rest.Rest.CACHE_CONTROL_HEADER;

public interface QuestionService {

    @GET("/api/questions/{id}")
    Observable<Result<Question>> show(@Header(CACHE_CONTROL_HEADER) String cacheControl,
                                      @Path("id") int id);

    @GET("/api/questions")
    Observable<Result<List<Question>>> index(@Header(CACHE_CONTROL_HEADER) String cacheControl,
                                             @Query("from_id") int fromId);

    @POST("/api/questions/{id}/answers")
    Observable<Result<Question>> answer(@Path("id") int questionId,
                                @Body AnswerRequest answerRequest);
}