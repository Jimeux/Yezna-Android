package com.moobasoft.yezna.ui.activities.base;

import android.view.ViewGroup;
import android.widget.TextView;

import com.moobasoft.yezna.R;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

import static android.view.View.VISIBLE;

public abstract class RxActivity extends BaseActivity {

    @Bind(R.id.loading_view) ViewGroup loadingView;
    @Bind(R.id.empty_view)   ViewGroup emptyView;
    @Bind(R.id.error_view)   ViewGroup errorView;
    @Bind(R.id.error_msg)    TextView errorMessage;
    @Bind(R.id.empty_msg)    TextView emptyMessage;
    @Bind({R.id.loading_view, R.id.error_view, R.id.empty_view, R.id.content})
    List<ViewGroup> stateViews;

    protected void activateView(int id) {
        for (ViewGroup vg : stateViews)
            vg.setVisibility(android.view.View.GONE);
        try {
            findViewById(id).setVisibility(VISIBLE);
        } catch (NullPointerException e) {
            activateErrorView(getString(R.string.error_default));
        }
    }

    protected void activateEmptyView(String message) {
        activateView(R.id.empty_view);
        emptyMessage.setText(message);
    }

    protected void activateErrorView(String message) {
        activateView(R.id.error_view);
        errorMessage.setText(message);
    }

    protected void activateContentView() {
        activateView(R.id.content);
    }

    protected void activateLoadingView() {
        activateView(R.id.loading_view);
    }

    public abstract void onRefresh();

    @OnClick({R.id.empty_refresh_btn, R.id.error_refresh_btn})
    public void clickRefresh() { onRefresh(); }
}