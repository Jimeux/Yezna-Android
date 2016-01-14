package com.moobasoft.yezna.ui.presenters;

import com.moobasoft.yezna.R;
import com.moobasoft.yezna.rest.models.Question;
import com.moobasoft.yezna.rest.requests.AnswerRequest;
import com.moobasoft.yezna.rest.services.QuestionService;
import com.moobasoft.yezna.ui.RxSchedulers;
import com.moobasoft.yezna.ui.presenters.base.RxPresenter;

import java.util.List;

import retrofit.Result;
import rx.Observable;

public class PublicQuestionPresenter extends RxPresenter<PublicQuestionPresenter.View> {

    public interface View extends RxPresenter.RxView {
        void onQuestionsRetrieved(List<Question> questions);
    }

    private final QuestionService questionService;

    public PublicQuestionPresenter(QuestionService questionService, RxSchedulers subscriptions) {
        super(subscriptions);
        this.questionService = questionService;
    }

    public void loadSummaries(boolean refresh, int fromId) {
        Observable<Result<List<Question>>> shareable = questionService
                .index(getCacheHeader(refresh), fromId)
                .compose(rxSchedulers.applySchedulers())
                .share();

        subscriptions.add(shareable
                .filter(isSuccess())
                .map(result -> result.response().body())
                .subscribe(view::onQuestionsRetrieved));

        subscriptions.add(shareable
                .filter(not(isSuccess()))
                .subscribe(this::handleError));
    }

    public void answerQuestion(int questionId, boolean isYes) {
        Observable<Result<Question>> shareable = questionService
                .answer(questionId, new AnswerRequest(isYes))
                .compose(rxSchedulers.applySchedulers())
                .share();

        // Do nothing if isSuccess()

        subscriptions.add(shareable
                .filter(hasError(UNPROCESSABLE_ENTITY))
                .map(result -> R.string.answer_error)
                .subscribe(view::onError));

        subscriptions.add(shareable
                .filter(not(hasError(UNPROCESSABLE_ENTITY)))
                .filter(not(isSuccess()))
                .subscribe(this::handleError));
    }

}