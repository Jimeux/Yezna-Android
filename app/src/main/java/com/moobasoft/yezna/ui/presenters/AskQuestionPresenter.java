package com.moobasoft.yezna.ui.presenters;

import android.text.TextUtils;

import com.moobasoft.yezna.R;
import com.moobasoft.yezna.rest.models.Question;
import com.moobasoft.yezna.rest.requests.QuestionRequest;
import com.moobasoft.yezna.rest.services.QuestionService;
import com.moobasoft.yezna.ui.RxSchedulers;
import com.moobasoft.yezna.ui.presenters.base.RxPresenter;

import rx.Subscription;

public class AskQuestionPresenter extends RxPresenter<AskQuestionPresenter.View> {

    public interface View extends RxPresenter.RxView  {
        void onAskSuccess(Question question);
        void onAskError(String questionError);
    }

    private final QuestionService questionService;

    public AskQuestionPresenter(RxSchedulers schedulers,
                                QuestionService questionService) {
        super(schedulers);
        this.questionService = questionService;
    }

    public void askQuestion(String question, boolean isPublic, int timeLimit) {
        if (TextUtils.isEmpty(question) || question.length() < Question.MIN_LENGTH) {
            view.onError(R.string.blank_question_error);
            return;
        }

        Subscription createSubscription = questionService
                .create(new QuestionRequest(question, isPublic, timeLimit))
                .compose(rxSchedulers.applySchedulers())
                .subscribe(view::onAskSuccess, this::handleError);

        subscriptions.add(createSubscription);
    }

    private void handleError(Throwable throwable) {
        if (hasErrorCode(throwable, UNPROCESSABLE_ENTITY))
            view.onAskError(""); //TODO: QuestionError converter
        else
            handleThrowable(throwable);
    }
}