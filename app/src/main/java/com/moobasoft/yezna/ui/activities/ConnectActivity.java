package com.moobasoft.yezna.ui.activities;

import android.animation.Animator;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.moobasoft.yezna.R;
import com.moobasoft.yezna.events.auth.LoginEvent;
import com.moobasoft.yezna.ui.activities.base.BaseActivity;
import com.moobasoft.yezna.ui.fragments.PresenterRetainer;
import com.moobasoft.yezna.ui.presenters.ConnectPresenter;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ConnectActivity extends BaseActivity implements ConnectPresenter.View {

    public static final String PROCESSING_KEY = "processing_key";
    public static final String REGISTER_MODE_KEY = "register_mode_key";
    public static final String RETAINER_TAG = "connect_retainer_tag";

    /**
     * Track if a network request is currently in progress
     */
    private boolean processing;
    /**
     * Track if the register or login form should be displayed
     */
    private boolean isRegisterMode;

    @Inject ConnectPresenter presenter;

    @Bind(R.id.main_connect_view) ViewGroup mainLayout;
    @Bind(R.id.btn_close) ImageView closeBtn;
    @Bind(R.id.btn_primary) TextView primaryBtn;
    @Bind(R.id.btn_secondary) TextView secondaryBtn;
    @Bind(R.id.email_label) View emailLabel;
    @Bind(R.id.email_et) EditText emailEt;
    @Bind(R.id.username_et) EditText usernameEt;
    @Bind(R.id.password_et) EditText passwordEt;
    @Bind({R.id.username_et, R.id.email_et, R.id.password_et})
    List<EditText> inputFields;

    @Override protected void onCreate(final Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_connect);
        ButterKnife.bind(this);
        getComponent().inject(this);
        initialisePresenter();
        isRegisterMode = getIntent().getBooleanExtra(REGISTER_MODE_KEY, true);
        initialiseForm();

        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override public void onGlobalLayout() {
                if (state == null)
                    mainLayout.post(() -> reveal());
                else
                    mainLayout.setVisibility(View.VISIBLE);
                // for SDK >= 16
                mainLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void reveal() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int cx = mainLayout.getWidth() ;
            int cy = mainLayout.getHeight();
            float finalRadius = (float) Math.hypot(cx, cy);

            Animator anim = ViewAnimationUtils
                    .createCircularReveal(mainLayout, cx, cy, 0, finalRadius);
            mainLayout.setVisibility(View.VISIBLE);
            anim.addListener(new Animator.AnimatorListener() {
                @Override public void onAnimationStart(Animator animation) {
                    mainLayout.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                }

                @Override public void onAnimationEnd(Animator animation) {
                    mainLayout.setLayerType(View.LAYER_TYPE_NONE, null);
                }

                @Override public void onAnimationCancel(Animator animation) {
                }

                @Override public void onAnimationRepeat(Animator animation) {
                }
            });
            anim.start();
        }

        if (mainLayout.getVisibility() != View.VISIBLE)
            mainLayout.setVisibility(View.VISIBLE);
    }

    @Override protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        if (state != null) {
            processing = state.getBoolean(PROCESSING_KEY);
            setProcessing(processing);
        }
    }

    @Override protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putBoolean(PROCESSING_KEY, processing);
        getIntent().putExtra(REGISTER_MODE_KEY, isRegisterMode);
    }

    @Override protected void onDestroy() {
        presenter.releaseView();
        super.onDestroy();
    }

    private void initialisePresenter() {
        Fragment retainer = getFragmentManager().findFragmentByTag(RETAINER_TAG);
        if (retainer == null) {
            PresenterRetainer<ConnectPresenter> presenterRetainer = new PresenterRetainer<>();
            presenterRetainer.put(presenter);
            getFragmentManager()
                    .beginTransaction()
                    .add(presenterRetainer, RETAINER_TAG)
                    .commit();
        } else {
            presenter = (ConnectPresenter) ((PresenterRetainer) retainer).get();
        }
        presenter.bindView(this);
    }

    private void initialiseForm() {
        if (isRegisterMode) {
            emailLabel.setVisibility(VISIBLE);
            primaryBtn.setText(getString(R.string.register));
            secondaryBtn.setText(getString(R.string.login_prompt));
        } else {
            emailLabel.setVisibility(GONE);
            primaryBtn.setText(getString(R.string.login));
            secondaryBtn.setText(getString(R.string.register_prompt));
        }
    }

    private void clearErrors() {
        emailEt.setError(null);
        usernameEt.setError(null);
        passwordEt.setError(null);
    }

    @Override public void onLogin() {
        Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
        eventBus.send(new LoginEvent());
        finish();
    }

    @Override public void onLoginError() {
        setProcessing(false);
        usernameEt.setError(getString(R.string.login_error));
        usernameEt.requestFocus();
    }

    @Override public void onError(int error) {
        setProcessing(false);
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override public void onRegistrationError(@Nullable String username,
                                              @Nullable String email,
                                              @Nullable String password) {
        setProcessing(false);
        usernameEt.setError(username);
        emailEt.setError(email);
        passwordEt.setError(password);

        for (EditText input : inputFields) {
            if (input.getError() != null) {
                input.requestFocus();
                break;
            }
        }
    }

    @Override public void promptForLogin() {
    }

    private void setProcessing(boolean processing) {
        this.processing = processing;
        this.setFinishOnTouchOutside(!processing);
        secondaryBtn.setEnabled(!processing);
        closeBtn.setEnabled(!processing);

        for (EditText e : inputFields)
            e.setEnabled(!processing);

        if (processing) {
            primaryBtn.setText(getString(R.string.loading));
            primaryBtn.setBackgroundColor(getResources().getColor(R.color.red300));
        } else {
            int stringId = isRegisterMode ? R.string.register : R.string.login;
            primaryBtn.setText(getString(stringId));
            primaryBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    @OnClick(R.id.login_page)
    public void clickBackdrop() {
        if (!processing) finish();
    }

    @OnClick(R.id.btn_primary)
    public void clickPrimaryButton() {
        clearErrors();
        setProcessing(true);

        //TODO: validation
        if (isRegisterMode) {
            presenter.register(emailEt.getText().toString(),
                    usernameEt.getText().toString(),
                    passwordEt.getText().toString());
        } else {
            presenter.login(usernameEt.getText().toString(),
                    passwordEt.getText().toString());
        }
    }

    @OnClick(R.id.btn_secondary)
    public void clickSecondaryButton() {
        isRegisterMode = !isRegisterMode;
        clearErrors();
        initialiseForm();
    }

    @OnClick(R.id.btn_close)
    public void clickCloseBtn() {
        finish();
    }
}