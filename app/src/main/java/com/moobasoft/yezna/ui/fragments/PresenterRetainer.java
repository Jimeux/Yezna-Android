package com.moobasoft.yezna.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.moobasoft.yezna.ui.presenters.base.Presenter;

import java.lang.ref.WeakReference;

public class PresenterRetainer<T extends Presenter> extends Fragment {

    private WeakReference<T> presenter;

    public void put(T presenter) {
        this.presenter = new WeakReference<>(presenter);
    }

    public T get() {
        return presenter.get();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
}