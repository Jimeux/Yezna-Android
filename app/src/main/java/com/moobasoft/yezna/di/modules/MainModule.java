package com.moobasoft.yezna.di.modules;

import com.moobasoft.yezna.di.scopes.PerActivity;
import com.moobasoft.yezna.rest.auth.CredentialStore;
import com.moobasoft.yezna.rest.services.QuestionService;
import com.moobasoft.yezna.rest.services.UserService;
import com.moobasoft.yezna.ui.RxSubscriber;
import com.moobasoft.yezna.ui.presenters.ConnectPresenter;
import com.moobasoft.yezna.ui.presenters.PublicQuestionPresenter;
import com.moobasoft.yezna.ui.presenters.SummaryPresenter;

import dagger.Module;
import dagger.Provides;
import retrofit.Retrofit;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Module
public class MainModule {

    @Provides
    public RxSubscriber rxSubscriber() {
        // TODO: provide these schedulers
        return new RxSubscriber(Schedulers.io(), AndroidSchedulers.mainThread());
    }

    @PerActivity
    @Provides
    public PublicQuestionPresenter randomPresenter(RxSubscriber subscriptionManager,
                                           QuestionService questionService) {
        return new PublicQuestionPresenter(questionService, subscriptionManager);
    }

    @PerActivity
    @Provides
    public SummaryPresenter summaryPresenter(RxSubscriber subscriptionManager,
                                           QuestionService questionService) {
        return new SummaryPresenter(questionService, subscriptionManager);
    }

    @PerActivity
    @Provides
    public ConnectPresenter loginPresenter(RxSubscriber subscriptionManager,
                                           UserService userService,
                                           CredentialStore credentialStore) {
        return new ConnectPresenter(subscriptionManager, userService, credentialStore);
    }

    @PerActivity
    @Provides
    public QuestionService questionService(Retrofit retrofit) {
        return retrofit.create(QuestionService.class);
    }

    @PerActivity
    @Provides
    public UserService userService(Retrofit retrofit) {
        return retrofit.create(UserService.class);
    }

}