package com.moobasoft.yezna.di.components;

import com.moobasoft.yezna.di.modules.ActivityModule;
import com.moobasoft.yezna.di.modules.MainModule;
import com.moobasoft.yezna.di.scopes.PerActivity;
import com.moobasoft.yezna.ui.activities.AskQuestionActivity;
import com.moobasoft.yezna.ui.activities.ConnectActivity;
import com.moobasoft.yezna.ui.activities.MainActivity;

import dagger.Component;

@PerActivity
@Component(
        dependencies = {
                AppComponent.class
        },
        modules = {
                MainModule.class,
                ActivityModule.class
        }
)
public interface ActivityComponent {

    void inject(MainActivity mainIndexActivity);
    void inject(AskQuestionActivity askQuestionActivity);
    void inject(ConnectActivity connectActivity);

}