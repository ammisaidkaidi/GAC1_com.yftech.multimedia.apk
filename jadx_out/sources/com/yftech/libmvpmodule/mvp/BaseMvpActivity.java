package com.yftech.libmvpmodule.mvp;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.yftech.libmvpmodule.mvp.BaseMvpPresenter;
import com.yftech.libmvpmodule.utils.Constants;

/* JADX INFO: loaded from: classes4.dex */
public abstract class BaseMvpActivity<T extends BaseMvpPresenter> extends AppCompatActivity implements IMvpView {
    private final String TAG = BaseMvpActivity.class.getSimpleName();
    public boolean isDisplaying = true;
    protected T mCurrentPresenter;
    public View main;

    protected abstract T createPresenter();

    public T getPresenter() {
        return this.mCurrentPresenter;
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        this.mCurrentPresenter = (T) createPresenter();
        if (this.mCurrentPresenter != null) {
            this.mCurrentPresenter.attachView(this);
        }
        if (Constants.displayHeight == 0 || Constants.displayWidth == 0) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            Constants.displayWidth = displayMetrics.widthPixels;
            Constants.displayHeight = displayMetrics.heightPixels;
        }
        this.main = findViewById(16908290);
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        if (this.mCurrentPresenter != null) {
            this.mCurrentPresenter.detachView();
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        Log.d(this.TAG, "onResume");
        this.isDisplaying = true;
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onStop() {
        super.onStop();
        Log.d(this.TAG, "onStop");
        this.isDisplaying = false;
    }
}
