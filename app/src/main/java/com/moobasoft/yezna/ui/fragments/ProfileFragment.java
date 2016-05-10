package com.moobasoft.yezna.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ProfileFragment extends RxFragment implements ProfilePresenter.View {

    @Inject ProfilePresenter presenter;
    @Inject CredentialStore credentialStore;

    @Bind(R.id.profile_avatar) ImageView avatar;
    @Bind(R.id.profile_username) TextView username;
    @Bind(R.id.profile_password) TextView password;
    @Bind(R.id.profile_email) EditText email;

    //@Icicle String avatarPath;
    //@Icicle String avatarUrl;
    private RequestBody avatarRb;

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

        avatarRb = null;
        avatar.setImageBitmap(null);
        loadUserData();
        Snackbar.make(avatar, getString(R.string.profile_update), Snackbar.LENGTH_SHORT).show();
    }

    @Override public void onProfileError(String error) {

    }

    @OnClick(R.id.update_profile_btn)
    public void clickUpdateBtn() {
        presenter.updateProfile(email.getText().toString(), password.getText().toString(), avatarRb);
    }

    @OnClick(R.id.profile_avatar)
    public void imageButtonClicked() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, ImageUtil.REQUEST_SELECT_IMAGE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ImageUtil.REQUEST_SELECT_IMAGE && resultCode == Activity.RESULT_OK &&
                data != null && data.getData() != null) {

            Uri uri = data.getData();

            bitmapObservable(uri)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(bitmap -> avatar.setImageBitmap(bitmap));
        }
    }

    @NonNull private Observable<Bitmap> bitmapObservable(final Uri uri) {
        return Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override public void call(Subscriber<? super Bitmap> subscriber) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                    bitmap = rotateAndScaleBitmap(uri, bitmap);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                    avatarRb = RequestBody.create(MediaType.parse("image/jpeg"), stream.toByteArray());
                    stream.close();

                    subscriber.onNext(bitmap);
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public Bitmap rotateAndScaleBitmap(Uri contentUri, Bitmap image) {
        String [] projection =
                {MediaStore.Images.Media.DATA, MediaStore.Images.Media.ORIENTATION};
        Cursor cursor = getActivity().getContentResolver().query(
                contentUri, projection, null, null, null);

        int rotation = 0;
        if (cursor != null && cursor.moveToFirst()) {
            rotation = cursor.getInt(1);
            cursor.close();
        }

        int width = image.getWidth();
        int height = image.getHeight();
        int maxWidth = 600;
        int maxHeight = 600;

        if (width > height && width > maxWidth) {
            // landscape
            int ratio = width / maxWidth;
            width = maxWidth;
            height = height / ratio;
        } else if (height > width && height > maxHeight) {
            // portrait
            int ratio = height / maxHeight;
            height = maxHeight;
            width = width / ratio;
        } else {
            // square
            height = maxHeight;
            width = maxWidth;
        }


        // calculate the scale - in this case = 0.4f
        float scaleWidth = ((float) width) / image.getWidth();
        float scaleHeight = ((float) height) / image.getHeight();

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        matrix.postRotate(rotation);
        return Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
    }

}