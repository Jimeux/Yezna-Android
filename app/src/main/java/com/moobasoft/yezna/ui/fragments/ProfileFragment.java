package com.moobasoft.yezna.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.moobasoft.yezna.events.auth.LogOutEvent;
import com.moobasoft.yezna.events.auth.LoginEvent;
import com.moobasoft.yezna.rest.auth.CredentialStore;
import com.moobasoft.yezna.rest.models.User;
import com.moobasoft.yezna.ui.fragments.base.RxFragment;
import com.moobasoft.yezna.ui.presenters.ProfilePresenter;
import com.moobasoft.yezna.util.ImageUtil;

import java.io.ByteArrayOutputStream;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.Icicle;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class ProfileFragment extends RxFragment implements ProfilePresenter.View {

    @Inject ProfilePresenter presenter;
    @Inject CredentialStore credentialStore;

    @Bind(R.id.profile_avatar) ImageView avatar;
    @Bind(R.id.profile_username) TextView username;
    @Bind(R.id.profile_email) EditText email;

    @Icicle String imagePath;
    @Icicle String imageUrl;
    private RequestBody imageRb;

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
        User user = credentialStore.loadUser();
        if (!TextUtils.isEmpty(user.getAvatar()))
            Glide.with(this).load(user.getAvatar()).into(avatar);
        username.setText(user.getUsername());
        email.setText(user.getEmail());
    }

    @Override public void onDestroyView() {
        presenter.releaseView();
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override protected void subscribeToEvents() {
        eventSubscriptions.add(eventBus.listenFor(LogOutEvent.class)
                .subscribe(event -> {
                }));
    }

    @Override public void onError(int messageId) {
        super.onError(messageId);
    }

    @Override public void onRefresh() {
    }

    @Override public void onProfileUpdated(User user) {
        credentialStore.saveUser(user);
        eventBus.send(new LoginEvent());
        imageRb = null;
        imagePath = null;
        imageUrl = null;
        email.setText(user.getEmail());

        if (!TextUtils.isEmpty(user.getAvatar()))
            Glide.with(getActivity()).load(user.getAvatar()).into(avatar);

        Snackbar.make(avatar, getString(R.string.profile_update), Snackbar.LENGTH_SHORT).show();
    }

    @Override public void onProfileError(String error) {

    }

    @OnClick(R.id.update_profile_btn)
    public void clickUpdateBtn() {
        presenter.updateProfile(email.getText().toString(), null, imagePath, imageUrl, imageRb);
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
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, ImageUtil.REQUEST_SELECT_IMAGE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ImageUtil.REQUEST_CAPTURE_IMAGE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            if (bitmap != null) {
                //TODO: Resize
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
                imageRb = RequestBody.create(MediaType.parse("image/png"), stream.toByteArray());
            }
        }

        else if (requestCode == ImageUtil.REQUEST_SELECT_IMAGE && resultCode == Activity.RESULT_OK) {

            ImageUtil.ImageResult result = ImageUtil
                    .onImageSelected(getActivity().getApplicationContext(),
                            requestCode, resultCode, data, getActivity().findViewById(R.id.toolbar));

            if (result != null) {
                imagePath = result.imagePath;
                imageUrl = result.imageUrl;
            }
        }
    }
}