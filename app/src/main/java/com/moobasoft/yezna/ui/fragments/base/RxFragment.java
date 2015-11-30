package com.moobasoft.yezna.ui.fragments.base;

import android.app.Fragment;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moobasoft.yezna.App;
import com.moobasoft.yezna.R;
import com.moobasoft.yezna.di.components.DaggerMainComponent;
import com.moobasoft.yezna.di.components.MainComponent;
import com.moobasoft.yezna.di.modules.MainModule;
import com.moobasoft.yezna.ui.activities.base.BaseActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

import static android.view.View.VISIBLE;

public abstract class RxFragment extends Fragment {
    @Bind(R.id.content)      protected ViewGroup contentView;
    @Bind(R.id.loading_view) protected ViewGroup loadingView;
    @Bind(R.id.empty_view)   protected ViewGroup emptyView;
    @Bind(R.id.error_view)   protected ViewGroup errorView;
    @Bind(R.id.error_msg)    protected TextView errorMessage;
    @Bind(R.id.empty_msg)    protected TextView emptyMessage;
    @Bind({R.id.loading_view, R.id.error_view, R.id.empty_view, R.id.content})
    protected List<ViewGroup> stateViews;

    public void onError(int messageId) {
        String message = getString(messageId);
        if (message == null)
            message = getString(R.string.error_default);
        activateErrorView(message);
    }

    public void promptForLogin() {
        ((BaseActivity) getActivity()).promptForLogin(null);
    }

    protected MainComponent getComponent() {
        return DaggerMainComponent.builder()
                .mainModule(new MainModule())
                .appComponent(((App) getActivity().getApplication()).getAppComponent())
                .build();
    }

    protected void activateView(View view) {
        for (ViewGroup vg : stateViews)
            vg.setVisibility(View.GONE);

        if (view == null)
            activateErrorView(getString(R.string.error_default));
        else
            view.setVisibility(VISIBLE);
    }

    protected void activateEmptyView(String message) {
        activateView(emptyView);
        emptyMessage.setText(message);
    }

    protected void activateErrorView(String message) {
        if (loadingView.getVisibility() == VISIBLE || errorView.getVisibility() == VISIBLE) {
            errorMessage.setText(message);
            activateView(errorView);
        } else
           Snackbar.make(getActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }

    protected void activateContentView() {
        activateView(contentView);
    }
    
    protected void activateLoadingView() {
        activateView(loadingView);
    }
    
    public abstract void onRefresh();

    @OnClick({R.id.empty_refresh_btn, R.id.error_refresh_btn})
    public void clickRefresh() {
        onRefresh();
    }

}