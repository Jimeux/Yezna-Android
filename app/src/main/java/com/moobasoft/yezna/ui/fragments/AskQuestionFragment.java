package com.moobasoft.yezna.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.moobasoft.yezna.R;
import com.moobasoft.yezna.events.ask.QuestionCreatedEvent;
import com.moobasoft.yezna.rest.models.Question;
import com.moobasoft.yezna.ui.fragments.base.RxFragment;
import com.moobasoft.yezna.ui.presenters.AskQuestionPresenter;
import com.moobasoft.yezna.util.Util;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class AskQuestionFragment extends RxFragment implements AskQuestionPresenter.View {

    @Inject AskQuestionPresenter presenter;

    @Bind(R.id.question_et)     EditText questionEt;
    @Bind(R.id.ask_btn)         Button   askBtn;
    @Bind(R.id.public_checkbox) CheckBox publicCb;
    @Bind(R.id.time_limit) Spinner timeLimitSpinner;
    @Bind(R.id.processing_view) ViewGroup processingView;

    private String imagePath;
    private String imageUrl;

    public AskQuestionFragment() {}

    @OnClick(R.id.ask_btn) public void clickAskButton() {
        setProcessing(true);
        String questionString = questionEt.getText().toString();
        boolean isPublic      = publicCb.isChecked();
        int timeLimit         = timeLimitSpinner.getSelectedItemPosition();

        presenter.askQuestion(questionString, isPublic, timeLimit, imagePath);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View view = inflater.inflate(R.layout.fragment_ask_question, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);
        getComponent().inject(this);
        presenter.bindView(this);
        initialiseInputField();
    }

    private void initialiseInputField() {
        questionEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        questionEt.setSingleLine(true);
        questionEt.setLines(3);
        questionEt.setHorizontallyScrolling(false);
        questionEt.setImeOptions(EditorInfo.IME_ACTION_DONE);
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

    @Override public void onDestroyView() {
        questionEt.clearFocus();
        ButterKnife.unbind(this);
        presenter.releaseView();
        super.onDestroyView();
    }

    @Override public void onAskSuccess(Question question) {
        setProcessing(false);
        //showNotification(getString(R.string.ask_success));
        questionEt.setText(""); //TODO: Reset other inputs
        eventBus.send(new QuestionCreatedEvent(question));
    }

    @Override public void onError(int messageId) {
        setProcessing(false);
       // if (showAsToast)
            Toast.makeText(getActivity(), getString(messageId), Toast.LENGTH_SHORT).show();
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

    @Override protected void subscribeToEvents() {
    }

    @Override public void onRefresh() {
    }




    public static final int SELECT_PICTURE = 531367;

    @OnClick(R.id.image_btn)
    public void imageButtonClicked() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == SELECT_PICTURE && resultCode == Activity.RESULT_OK && null != data) {
                String picturePath = getImagePath(data);

                if (picturePath != null) {
                    if (picturePath.contains("http"))
                        imageUrl = picturePath;
                    else
                        imagePath = picturePath;
                }
            } else {
                Snackbar.make(contentView, getString(R.string.no_image_selected), Snackbar.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Snackbar.make(contentView, e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    private String getImagePath(Intent data) {
        Uri uri = data.getData();
        String[] projection = { MediaStore.Images.Media.DATA };

        Cursor cursor = getActivity().getContentResolver()
                .query(uri, projection, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();   //TODO: Null checks
            int columnIndex = cursor.getColumnIndex(projection[0]);
            String picturePath = cursor.getString(columnIndex); // returns null
            cursor.close();
            return picturePath;
        }
        return null;
    }

}