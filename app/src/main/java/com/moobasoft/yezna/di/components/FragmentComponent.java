package com.moobasoft.yezna.di.components;

import com.moobasoft.yezna.di.modules.FragmentModule;
import com.moobasoft.yezna.di.modules.MainModule;
import com.moobasoft.yezna.di.scopes.PerActivity;
import com.moobasoft.yezna.ui.fragments.MyQuestionsFragment;
import com.moobasoft.yezna.ui.fragments.ProfileFragment;
import com.moobasoft.yezna.ui.fragments.PublicQuestionsFragment;

import dagger.Component;

@PerActivity
@Component(
        dependencies = {
                AppComponent.class
        },
        modules = {
                MainModule.class,
                FragmentModule.class
        }
)
public interface FragmentComponent {

    void inject(MyQuestionsFragment myQuestionsFragment);
    void inject(PublicQuestionsFragment publicQuestionsFragment);
    void inject(ProfileFragment profileFragment);

}