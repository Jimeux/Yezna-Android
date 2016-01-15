package com.moobasoft.yezna.di.modules;

import com.moobasoft.yezna.di.scopes.PerActivity;
import com.moobasoft.yezna.rest.auth.CredentialStore;
import com.moobasoft.yezna.rest.services.QuestionService;
import com.moobasoft.yezna.rest.services.UserService;
import com.moobasoft.yezna.ui.RxSchedulers;
import com.moobasoft.yezna.ui.presenters.AskQuestionPresenter;
import com.moobasoft.yezna.ui.presenters.ConnectPresenter;
import com.moobasoft.yezna.ui.presenters.PublicQuestionPresenter;
import com.moobasoft.yezna.ui.presenters.MyQuestionsPresenter;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Module
public class MainModule {

    @Provides
    public RxSchedulers rxSubscriber() {
        // TODO: provide these schedulers
        return new RxSchedulers(Schedulers.io(), AndroidSchedulers.mainThread());
    }

    @PerActivity @Provides
    public PublicQuestionPresenter publicQuestionsPresenter(RxSchedulers schedulers,
                                                            QuestionService questionService) {
        return new PublicQuestionPresenter(questionService, schedulers);
    }

    @PerActivity @Provides
    public MyQuestionsPresenter myQuestionsPresenter(RxSchedulers schedulers,
                                                     UserService userService) {
        return new MyQuestionsPresenter(userService, schedulers);
    }

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

    @PerActivity @Provides
    public QuestionService questionService(Retrofit retrofit) {
        return retrofit.create(QuestionService.class);
    }

    @PerActivity @Provides
    public UserService userService(Retrofit retrofit) {
        return retrofit.create(UserService.class);
    }

}