package com.moobasoft.yezna.ui.presenters;

import com.moobasoft.yezna.rest.models.Question;
import com.moobasoft.yezna.rest.services.QuestionService;
import com.moobasoft.yezna.ui.RxSubscriber;
import com.moobasoft.yezna.ui.presenters.base.RxPresenter;

import java.util.List;

import retrofit.Response;
import retrofit.Result;
import rx.Subscription;
import rx.subjects.BehaviorSubject;

public class SummaryPresenter extends RxPresenter<SummaryPresenter.View> {

    private Subscription subscription;

    public interface View extends RxPresenter.RxView {
        void onPostsRetrieved(List<Question> questions);
    }

    private final QuestionService questionService;
    private BehaviorSubject<Result<List<Question>>> cacheSubject;

    public boolean requestInProgress() {
        if (cacheSubject != null) {
            subscription = cacheSubject.subscribe(
                    this::handleOnNext, this::handleError);
            return true;
        }
        return false;
    }

    @Override
    public void releaseView() {
        super.releaseView();
        if (subscription != null)
            subscription.unsubscribe();
    }

    public SummaryPresenter(QuestionService questionService, RxSubscriber subscriptions) {
        super(subscriptions);
        this.questionService = questionService;
    }

    public void loadSummaries(boolean refresh, int page) {
        if (cacheSubject == null) {
            cacheSubject = BehaviorSubject.create();
            questionService.index(getCacheHeader(refresh), page)
                    .compose(subscriptions.applySchedulers())
                    .subscribe(cacheSubject::onNext, this::handleError);
        }

        subscription = cacheSubject.subscribe(
                this::handleOnNext, this::handleError);
    }

    private void handleOnNext(Result<List<Question>> result) {
        if (view == null) return;

        cacheSubject.onCompleted();
        cacheSubject = null;

        if (result.isError())
            handleError(result.error());
        else {
            Response<List<Question>> response = result.response();

            if (response.isSuccess())
                view.onPostsRetrieved(response.body());
            else
                defaultResponses(response.code());
        }
    }

}