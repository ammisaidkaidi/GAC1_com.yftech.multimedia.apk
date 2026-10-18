package com.yftech.libmvpmodule.mvp;

import androidx.fragment.app.DialogFragment;

/* JADX INFO: loaded from: classes4.dex */
public class BaseDialogFragment extends DialogFragment {
    private OnDestotyListener onDestotyListener;

    public interface OnDestotyListener {
        void onDestoryCallback();
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroy() {
        if (this.onDestotyListener != null) {
            this.onDestotyListener.onDestoryCallback();
        }
        super.onDestroy();
    }

    protected void setOnDestotyListener(OnDestotyListener onDestotyListener) {
        this.onDestotyListener = onDestotyListener;
    }
}
