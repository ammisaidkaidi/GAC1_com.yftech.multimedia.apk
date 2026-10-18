package com.yftech.libmvpmodule.mvp;

import com.yftech.libmvpmodule.mvp.IMvpView;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes4.dex */
public class BaseMvpPresenter<T extends IMvpView> {
    private WeakReference<T> mReference;

    void attachView(T t) {
        this.mReference = new WeakReference<>(t);
    }

    void detachView() {
        if (this.mReference != null) {
            this.mReference.clear();
            this.mReference = null;
        }
    }

    public T getView() {
        return this.mReference.get();
    }

    public boolean isViewAttach() {
        return (this.mReference == null || this.mReference.get() == null) ? false : true;
    }
}
