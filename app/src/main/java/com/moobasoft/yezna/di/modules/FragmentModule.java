package com.moobasoft.yezna.di.modules;

import com.moobasoft.yezna.di.scopes.PerActivity;
import com.moobasoft.yezna.rest.services.QuestionService;
import com.moobasoft.yezna.rest.services.UserService;
import com.moobasoft.yezna.ui.RxSchedulers;
import com.moobasoft.yezna.ui.presenters.MyQuestionsPresenter;
import com.moobasoft.yezna.ui.presenters.ProfilePresenter;
import com.moobasoft.yezna.ui.presenters.PublicQuestionPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class FragmentModule {

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
    public ProfilePresenter profilePresenter(RxSchedulers schedulers,
                                             UserService userService) {
        return new ProfilePresenter(schedulers, userService);
    }

}