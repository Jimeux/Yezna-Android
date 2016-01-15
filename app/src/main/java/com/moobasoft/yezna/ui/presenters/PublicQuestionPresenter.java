package com.moobasoft.yezna.ui.presenters;

import com.moobasoft.yezna.R;
import com.moobasoft.yezna.rest.models.Question;
import com.moobasoft.yezna.rest.requests.AnswerRequest;
import com.moobasoft.yezna.rest.services.QuestionService;
import com.moobasoft.yezna.ui.RxSchedulers;
import com.moobasoft.yezna.ui.presenters.base.RxPresenter;

import java.util.List;

import rx.Observable;
import rx.Subscription;

public class PublicQuestionPresenter extends RxPresenter<PublicQuestionPresenter.View> {

    public interface View extends RxPresenter.RxView {
        void onQuestionsRetrieved(List<Question> questions);
    }

    private final QuestionService questionService;

    private Observable<List<Question>> questionObservable;
    private Observable<Question> answerObservable;

    public PublicQuestionPresenter(QuestionService questionService, RxSchedulers subscriptions) {
        super(subscriptions);
        this.questionService = questionService;
    }

    @Override public void bindView(View view) {
        super.bindView(view);
        if (questionObservable != null)
            subscribeToQuestionObservable();
        if (answerObservable != null)
            subscribeToAnswerObservable();
    }

    public void loadSummaries(boolean refresh, int fromId) {
        questionObservable = questionService
                .index(getCacheHeader(refresh), fromId)
                .compose(rxSchedulers.applySchedulers())
                .cache();
        subscribeToQuestionObservable();
    }

    private void subscribeToQuestionObservable() {
        Subscription questionSubscription = questionObservable.subscribe(
                view::onQuestionsRetrieved,
                this::handleThrowable,
                () -> questionObservable = null);
        subscriptions.add(questionSubscription);
    }

    public void answerQuestion(int questionId, boolean isYes) {
        answerObservable = questionService
                .answer(questionId, new AnswerRequest(isYes))
                .compose(rxSchedulers.applySchedulers()) //TODO: Retry
                .cache();
        subscribeToAnswerObservable();
    }

    private void subscribeToAnswerObservable() {
        Subscription answerSubscription = answerObservable.subscribe(
                question -> {},
                this::handleAnswerOnError,
                () -> answerObservable = null);
        subscriptions.add(answerSubscription);
    }

    private void handleAnswerOnError(Throwable throwable) {
        if (hasErrorCode(throwable, UNPROCESSABLE_ENTITY))
            view.onError(R.string.answer_error);
        else
            handleThrowable(throwable);
    }

}