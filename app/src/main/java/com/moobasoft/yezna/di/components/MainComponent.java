package com.moobasoft.yezna.di.components;

import com.moobasoft.yezna.ui.activities.ConnectActivity;
import com.moobasoft.yezna.ui.activities.MainActivity;
import com.moobasoft.yezna.di.modules.MainModule;
import com.moobasoft.yezna.di.scopes.PerActivity;
import com.moobasoft.yezna.ui.fragments.SummaryFragment;

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
    void inject(ConnectActivity connectActivity);
    void inject(SummaryFragment summaryFragment);

}