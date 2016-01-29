package com.moobasoft.yezna.di.components;

import com.moobasoft.yezna.ui.activities.AskQuestionActivity;
import com.moobasoft.yezna.ui.activities.ConnectActivity;
import com.moobasoft.yezna.ui.activities.MainActivity;
import com.moobasoft.yezna.di.modules.MainModule;
import com.moobasoft.yezna.di.scopes.PerActivity;
import com.moobasoft.yezna.ui.fragments.AskQuestionFragment;
import com.moobasoft.yezna.ui.fragments.PublicQuestionsFragment;
import com.moobasoft.yezna.ui.fragments.MyQuestionsFragment;

import dagger.Component;

@PerActivity
@Component(
        dependencies = {
                AppComponent.class
        },
        modules = {
                MainModule.class
        }
)
public interface MainComponent {

    void inject(MainActivity mainIndexActivity);
    void inject(AskQuestionActivity askQuestionActivity);
    void inject(ConnectActivity connectActivity);
    void inject(MyQuestionsFragment myQuestionsFragment);
    void inject(PublicQuestionsFragment publicQuestionsFragment);

    void inject(AskQuestionFragment askQuestionFragment);

}