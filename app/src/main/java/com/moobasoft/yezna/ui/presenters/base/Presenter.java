package com.moobasoft.yezna.ui.presenters.base;

public class Presenter<V> {

    protected V view;

    public void bindView(V view) {
        this.view = view;
    }

    public void releaseView() {
        this.view = null;
    }
}