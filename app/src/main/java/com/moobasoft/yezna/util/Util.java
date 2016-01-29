package com.moobasoft.yezna.util;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class Util {

    private Util() {}

    public static void setImeVisibility(final boolean visible, final EditText editText) {
        Runnable mShowImeRunnable = new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) editText.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) imm.showSoftInput(editText, 0);
            }
            @Override
            public boolean equals(Object o) {
                return o instanceof Runnable &&
                        o.toString().equals(this.toString());
            }
            @Override
            public String toString() {
                return "ime runnable";
            }
        };

        if (visible) {
            editText.post(mShowImeRunnable);
        } else {
            editText.removeCallbacks(mShowImeRunnable);
            InputMethodManager imm = (InputMethodManager) editText.getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);

            if (imm != null) {
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        }
    }
}
