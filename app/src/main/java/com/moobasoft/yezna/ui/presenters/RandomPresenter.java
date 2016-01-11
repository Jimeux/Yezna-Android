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

public class RandomPresenter extends RxPresenter<RandomPresenter.View> {

    public interface View extends RxPresenter.RxView {
        void onPostsRetrieved(List<Question> questions);
    }

    private final QuestionService questionService;

    public RandomPresenter(QuestionService questionService, RxSubscriber subscriptions) {
        super(subscriptions);
        this.questionService = questionService;
    }

    public void loadSummaries(boolean refresh, int page) {
        subscriptions.add(
                questionService.index(getCacheHeader(refresh), page),
                this::handleOnNext,
                this::handleError);
    }

    public void answerQuestion(int questionId, boolean isYes) {
        subscriptions.add(questionService.answer(questionId, new AnswerRequest(isYes)),
                question -> {},
                this::fuckYou);
    }

    private void fuckYou(Throwable throwable) {
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