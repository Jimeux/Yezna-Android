package com.moobasoft.yezna.di.modules;

import com.moobasoft.yezna.di.scopes.PerActivity;
import com.moobasoft.yezna.rest.auth.CredentialStore;
import com.moobasoft.yezna.rest.services.QuestionService;
import com.moobasoft.yezna.rest.services.UserService;
import com.moobasoft.yezna.ui.RxSchedulers;
import com.moobasoft.yezna.ui.presenters.AskQuestionPresenter;
import com.moobasoft.yezna.ui.presenters.ConnectPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {

    @PerActivity @Provides
    public ConnectPresenter connectPresenter(RxSchedulers schedulers,
                                             UserService userService,
                                             CredentialStore credentialStore) {
        return new ConnectPresenter(schedulers, userService, credentialStore);
    }

    @PerActivity @Provides
    public AskQuestionPresenter askQuestionPresenter(RxSchedulers schedulers,
                                             QuestionService questionService) {
        return new AskQuestionPresenter(schedulers, questionService);
    }

}