package com.moobasoft.yezna.ui.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Transition;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.moobasoft.yezna.R;
import com.moobasoft.yezna.events.ask.QuestionCreatedEvent;
import com.moobasoft.yezna.rest.models.Question;
import com.moobasoft.yezna.ui.activities.base.BaseActivity;
import com.moobasoft.yezna.ui.presenters.AskQuestionPresenter;
import com.moobasoft.yezna.util.ImageUtil;
import com.moobasoft.yezna.util.Util;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class AskQuestionActivity extends BaseActivity implements AskQuestionPresenter.View {

    @Inject AskQuestionPresenter presenter;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.question_et) EditText questionEt;
    @Bind(R.id.ask_btn) TextView askBtn;
    @Bind(R.id.public_checkbox) CheckBox publicCb;
    @Bind(R.id.time_limit) Spinner timeLimitSpinner;
    @Bind(R.id.processing_view) ViewGroup processingView;

    private String imagePath;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_question);
        ButterKnife.bind(this);
        getComponent().inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        presenter.bindView(this);
        initialiseInputField();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition transition = new Explode(); //.setInterpolator(new DecelerateInterpolator());
            getWindow().setEnterTransition(transition);
        }
    }

    @Override protected void onDestroy() {
        questionEt.clearFocus();
        presenter.releaseView();
        super.onDestroy();
    }

    private void initialiseInputField() {
        questionEt.setOnFocusChangeListener(
                (v, hasFocus) -> Util.setImeVisibility(hasFocus, questionEt));
        questionEt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId != EditorInfo.IME_ACTION_DONE)
                return false;
            else if (questionEt.getText().toString().length() < Question.MIN_LENGTH) {
                onAskError(getString(R.string.blank_question_error, Question.MIN_LENGTH));
                return false;
            } else {
                questionEt.clearFocus();
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    @Override public void onAskSuccess(Question question) {
        setProcessing(false);
        //showNotification(getString(R.string.ask_success));
        questionEt.setText(""); //TODO: Reset other inputs
        eventBus.sendDelayed(new QuestionCreatedEvent(question), 300);
        //finish();
        ActivityCompat.finishAfterTransition(this);
    }

    @Override public void promptForLogin() {
        Toast.makeText(this, "Sort this out!", Toast.LENGTH_SHORT).show();
    }

    @Override public void onError(int messageId) {
        setProcessing(false);
        // if (showAsToast)
        Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show();
        //else
        // showNotification(error);
    }

    @Override public void onAskError(String questionError) {
        setProcessing(false);
        questionEt.setError(questionError);
        questionEt.requestFocus();
    }

    private void setProcessing(boolean processing) {
        processingView.setVisibility(processing ? VISIBLE : GONE);
        askBtn.setVisibility(processing ? GONE : VISIBLE);
    }


    @OnClick(R.id.ask_btn) public void clickAskButton() {
        setProcessing(true);
        String questionString = questionEt.getText().toString();
        boolean isPublic = publicCb.isChecked();
        int timeLimit = timeLimitSpinner.getSelectedItemPosition();

        presenter.askQuestion(questionString, isPublic, timeLimit, imagePath);
    }

    @OnClick(R.id.image_btn)
    public void imageButtonClicked() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, ImageUtil.SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageUtil.ImageResult result = ImageUtil
                .onImageSelected(this.getApplicationContext(), requestCode, resultCode, data, toolbar);

        imagePath = result.imagePath;
        imageUrl = result.imageUrl;
    }

}