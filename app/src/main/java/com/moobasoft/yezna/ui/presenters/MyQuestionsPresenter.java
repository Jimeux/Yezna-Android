package com.moobasoft.yezna.ui.presenters;

import com.moobasoft.yezna.rest.models.Question;
import com.moobasoft.yezna.rest.services.UserService;
import com.moobasoft.yezna.ui.RxSchedulers;
import com.moobasoft.yezna.ui.presenters.base.RxPresenter;

import java.util.List;

import rx.Subscription;

public class MyQuestionsPresenter extends RxPresenter<MyQuestionsPresenter.View> {

    public interface View extends RxPresenter.RxView {
        void onQuestionsRetrieved(List<Question> questions);
    }

    private final UserService userService;

    public MyQuestionsPresenter(UserService userService, RxSchedulers subscriptions) {
        super(subscriptions);
        this.userService = userService;
    }

    public void loadQuestions(boolean refresh, int page) {
        Subscription questionSubscription = userService
                .getQuestions(page)
                .compose(rxSchedulers.applySchedulers())
                .subscribe(view::onQuestionsRetrieved,
                        this::handleThrowable);
        subscriptions.add(questionSubscription);
    }

}