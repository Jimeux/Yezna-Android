package com.moobasoft.yezna.ui.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.moobasoft.yezna.ui.presenters.base.Presenter;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class PresenterRetainer extends Fragment {

    private static final String TAG_RETAINER = "retainer_tag";

    private final Map<UUID, Presenter> presenterMap = new WeakHashMap<>();

    public interface PresenterHost {
        void putPresenter(@NonNull UUID key, @NonNull Presenter presenter);
        Presenter getPresenter(@NonNull UUID key);
    }

    public static void put(@NonNull FragmentManager fragmentManager,
                           @NonNull UUID key,
                           @NonNull Presenter presenter) {
        getRetainer(fragmentManager).put(key, presenter);
    }

    public static Presenter get(@NonNull FragmentManager fragmentManager,
                                @NonNull UUID key) {
        return getRetainer(fragmentManager).get(key);
    }

    public static PresenterRetainer getRetainer(@NonNull FragmentManager fragmentManager) {
        PresenterRetainer retainer = (PresenterRetainer)
                fragmentManager.findFragmentByTag(TAG_RETAINER);
        if (retainer == null) {
            retainer = new PresenterRetainer();
            fragmentManager.beginTransaction()
                    .add(retainer, TAG_RETAINER).commit();
        }
        return retainer;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * Retain instance state to persist map across config changes.
         */
        setRetainInstance(true);
    }

    private void put(@NonNull final UUID key, @NonNull final Presenter presenter) {
        presenterMap.put(key, presenter);
    }

    @Nullable
    private Presenter get(@NonNull final UUID key) {
        return presenterMap.get(key);
    }
}