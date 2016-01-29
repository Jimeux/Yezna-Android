package com.moobasoft.yezna.di.modules;

import com.moobasoft.yezna.di.scopes.PerActivity;
import com.moobasoft.yezna.rest.services.QuestionService;
import com.moobasoft.yezna.rest.services.UserService;
import com.moobasoft.yezna.ui.RxSchedulers;

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
    public QuestionService questionService(Retrofit retrofit) {
        return retrofit.create(QuestionService.class);
    }

    @PerActivity @Provides
    public UserService userService(Retrofit retrofit) {
        return retrofit.create(UserService.class);
    }

}