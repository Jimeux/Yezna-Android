package com.moobasoft.yezna.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moobasoft.yezna.R;
import com.moobasoft.yezna.events.auth.LoginEvent;
import com.moobasoft.yezna.rest.auth.CredentialStore;
import com.moobasoft.yezna.rest.models.User;
import com.moobasoft.yezna.ui.fragments.base.RxFragment;
import com.moobasoft.yezna.ui.presenters.ProfilePresenter;
import com.moobasoft.yezna.util.ImageUtil;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.Icicle;
import okhttp3.RequestBody;

public class ProfileFragment extends RxFragment implements ProfilePresenter.View {

    @Inject ProfilePresenter presenter;
    @Inject CredentialStore credentialStore;

    @Bind(R.id.profile_avatar) ImageView avatar;
    @Bind(R.id.profile_username) TextView username;
    @Bind(R.id.profile_password) TextView password;
    @Bind(R.id.profile_email) EditText email;

    @Icicle String avatarPath;
    @Icicle String avatarUrl;
    private RequestBody avatarRequestBody;

    public ProfileFragment() {
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override public void onActivityCreated(@Nullable Bundle state) {
        super.onActivityCreated(state);
        getComponent().inject(this);
        presenter.bindView(this);
        if (state == null)
            loadUserData();
    }

    private void loadUserData() {
        User user = credentialStore.loadUser();
        if (!TextUtils.isEmpty(user.getAvatar()))
            Glide.with(this).load(user.getAvatar()).into(avatar);
        username.setText(user.getUsername());
        email.setText(user.getEmail());
        password.setText("");
    }

    @Override public void onDestroyView() {
        presenter.releaseView();
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override protected void subscribeToEvents() {
        eventSubscriptions.add(eventBus.listenFor(LoginEvent.class)
                .subscribe(event -> loadUserData()));
    }

    @Override public void onError(int messageId) {
        super.onError(messageId);
    }

    @Override public void onRefresh() {
    }

    @Override public void onProfileUpdated(User user) {
        credentialStore.saveUser(user);
         eventBus.send(new LoginEvent()); //TODO: Make new event?
        loadUserData();

        avatarRequestBody = null;
        avatarPath = null;
        avatarUrl = null;

        Snackbar.make(avatar, getString(R.string.profile_update), Snackbar.LENGTH_SHORT).show();
    }

    @Override public void onProfileError(String error) {

    }

    @OnClick(R.id.update_profile_btn)
    public void clickUpdateBtn() {
        presenter.updateProfile(email.getText().toString(), password.getText().toString(), avatarPath, avatarUrl, avatarRequestBody);
    }

    @OnClick(R.id.capture_image_btn)
    public void cameraButtonClicked() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, ImageUtil.REQUEST_CAPTURE_IMAGE);
        }
    }

    @OnClick(R.id.select_image_btn)
    public void imageButtonClicked() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, ImageUtil.REQUEST_SELECT_IMAGE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ImageUtil.REQUEST_CAPTURE_IMAGE && resultCode == Activity.RESULT_OK) {
            avatarRequestBody = ImageUtil.getBitmapFromData(data, avatar);
        } else if (requestCode == ImageUtil.REQUEST_SELECT_IMAGE && resultCode == Activity.RESULT_OK) {

            ImageUtil.ImageResult result = ImageUtil
                    .onImageSelected(getActivity().getApplicationContext(),
                            requestCode, resultCode, data, getActivity().findViewById(R.id.toolbar));

            if (result != null) {
                avatarPath = result.avatarPath;
                avatarUrl = result.avatarUrl;

                if (avatarPath != null)
                    Glide.with(this).load(avatarPath).into(avatar);
                else if (avatarUrl != null)
                    Glide.with(this).load(avatarUrl).into(avatar);
            }
        }
    }
}