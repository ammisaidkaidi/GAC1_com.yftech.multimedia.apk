package com.yftech.libmvpmodule.mvp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yftech.libmvpmodule.mvp.BaseMvpPresenter;

/* JADX INFO: loaded from: classes4.dex */
public abstract class BaseMvpDialogFragment<T extends BaseMvpPresenter> extends BaseDialogFragment implements IMvpView {
    protected boolean isCreateView;
    protected Activity mActivity;
    protected T mCurrentPresenter;

    protected abstract T createPresenter();

    public T getPresenter() {
        return this.mCurrentPresenter;
    }

    protected abstract void initView(View view);

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (Activity) context;
    }

    protected abstract View onBindView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, Bundle bundle);

    @Override // androidx.fragment.app.Fragment
    @Nullable
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        this.mCurrentPresenter = (T) createPresenter();
        if (this.mCurrentPresenter != null) {
            this.mCurrentPresenter.attachView(this);
        }
        View viewOnBindView = onBindView(layoutInflater, viewGroup, bundle);
        this.isCreateView = true;
        initView(viewOnBindView);
        return viewOnBindView;
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        if (this.mCurrentPresenter != null) {
            this.mCurrentPresenter.detachView();
        }
        this.isCreateView = false;
        onUnbindView();
    }

    protected void onUnbindView() {
    }
}
