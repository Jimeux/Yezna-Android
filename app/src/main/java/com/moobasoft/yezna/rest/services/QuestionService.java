package com.moobasoft.yezna.rest.services;

import com.moobasoft.yezna.rest.models.Question;
import com.moobasoft.yezna.rest.requests.AnswerRequest;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

import static com.moobasoft.yezna.rest.Rest.CACHE_CONTROL_HEADER;

public interface QuestionService {

    @GET("/api/questions/{id}")
    Observable<Question> show(@Header(CACHE_CONTROL_HEADER) String cacheControl,
                                      @Path("id") int id);

    @GET("/api/questions")
    Observable<List<Question>> index(@Header(CACHE_CONTROL_HEADER) String cacheControl,
                                             @Query("from_id") int fromId);

    @POST("/api/questions/{id}/answers")
    Observable<Question> answer(@Path("id") int questionId,
                                @Body AnswerRequest answerRequest);
}