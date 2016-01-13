package com.moobasoft.yezna.ui.presenters;

import com.moobasoft.yezna.R;
import com.moobasoft.yezna.rest.models.Question;
import com.moobasoft.yezna.rest.requests.AnswerRequest;
import com.moobasoft.yezna.rest.services.QuestionService;
import com.moobasoft.yezna.ui.RxSubscriber;
import com.moobasoft.yezna.ui.presenters.base.RxPresenter;

import java.util.List;

import retrofit.Response;
import retrofit.Result;

public class PublicQuestionPresenter extends RxPresenter<PublicQuestionPresenter.View> {

    public interface View extends RxPresenter.RxView {
        void onPostsRetrieved(List<Question> questions);
    }

    private final QuestionService questionService;

    public PublicQuestionPresenter(QuestionService questionService, RxSubscriber subscriptions) {
        super(subscriptions);
        this.questionService = questionService;
    }

    public void loadSummaries(boolean refresh, int fromId) {
        subscriptions.add(
                questionService.index(getCacheHeader(refresh), fromId),
                this::handleOnNext,
                this::handleError);
    }

    public void answerQuestion(int questionId, boolean isYes) {
        subscriptions.add(questionService.answer(questionId, new AnswerRequest(isYes)),
                question -> {},
                this::handleAnswerError);
    }

    private void handleAnswerError(Throwable throwable) {
        if (throwable.getMessage() != null && throwable.getMessage().contains("422"))
            view.onError(R.string.answer_error);
        else super.handleError(throwable);
    }

    private void handleOnNext(Result<List<Question>> result) {
        if (view == null) return;

        if (result.isError()) {
            handleError(result.error());
        } else {
            Response<List<Question>> response = result.response();

            if (response.isSuccess())
                view.onPostsRetrieved(response.body());
            else
                defaultResponses(response.code());
        }
    }

}