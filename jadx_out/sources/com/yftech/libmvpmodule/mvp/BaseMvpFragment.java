package com.yftech.libmvpmodule.mvp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.yftech.libmvpmodule.mvp.BaseMvpPresenter;
import com.yftech.libmvpmodule.utils.SystemUtil;
import com.yftech.vehicle.internal.adapter.ICarInfo;
import com.yftech.vehicle.internal.adapter.ICarPower;
import com.yftech.vehicle.sdk.CarPowerManager;
import java.util.Objects;

/* JADX INFO: loaded from: classes4.dex */
public abstract class BaseMvpFragment<T extends BaseMvpPresenter> extends Fragment implements IMvpView {
    protected int index;
    protected boolean isCreateView;
    protected Activity mActivity;
    protected T mCurrentPresenter;
    private OnDestotyListener onDestotyListener;
    private String powerMode;
    private View rootView;
    public boolean isFullScreen = false;
    public boolean isSplitMode = false;
    private final int RC_POWER_ON = 10;
    private final int RC_POWER_OFF = 20;
    private Handler baseHandler = new Handler(Looper.getMainLooper()) { // from class: com.yftech.libmvpmodule.mvp.BaseMvpFragment.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 10) {
                BaseMvpFragment.this.setResumeByPowerOn(true);
                BaseMvpFragment.this.onResume();
                BaseMvpFragment.this.setResumeByPowerOn(false);
            } else if (message.what == 20) {
                BaseMvpFragment.this.onStop();
            }
        }
    };
    private CarPowerManager.CarPowerListener carPowerListener = new CarPowerManager.CarPowerListener() { // from class: com.yftech.libmvpmodule.mvp.BaseMvpFragment.2
        @Override // com.yftech.vehicle.sdk.CarPowerManager.CarPowerListener
        public void onPowerModeChanged(ICarPower.PowerMode powerMode) {
            Log.d("BaseMvpFragment", "onPowerModeChanged:mode=" + powerMode.toString() + ", powerMode=" + BaseMvpFragment.this.powerMode);
            if (BaseMvpFragment.this.getActivity() instanceof BaseMvpActivity) {
                BaseMvpActivity baseMvpActivity = (BaseMvpActivity) BaseMvpFragment.this.getActivity();
                Log.d("BaseMvpFragment", "onResume:isDisplaying=" + baseMvpActivity.isDisplaying);
                if (!baseMvpActivity.isDisplaying) {
                    return;
                }
            }
            if (powerMode.toString().equals(BaseMvpFragment.this.powerMode)) {
                return;
            }
            BaseMvpFragment.this.powerMode = powerMode.toString();
            if (ICarInfo.PowerMode.valueOf(powerMode.value) == ICarInfo.PowerMode.OFF) {
                Log.d("BaseMvpFragment", "onStop");
                BaseMvpFragment.this.baseHandler.sendEmptyMessage(20);
                return;
            }
            boolean zIsVisible = BaseMvpFragment.this.isVisible();
            Log.d(getClass().getSimpleName(), "onResume:visible=" + BaseMvpFragment.this.isUserVisible());
            if (zIsVisible) {
                BaseMvpFragment.this.baseHandler.sendEmptyMessage(10);
            }
        }
    };
    private boolean mIsResumeByPowerOn = false;

    public interface OnDestotyListener {
        void onDestoryCallback();
    }

    protected abstract T createPresenter();

    public T getPresenter() {
        return this.mCurrentPresenter;
    }

    protected void initListener() {
    }

    protected abstract void initView(View view);

    protected boolean isResumeByPowerOn() {
        return this.mIsResumeByPowerOn;
    }

    protected boolean isUserVisible() {
        try {
            boolean zIsVisible = isVisible();
            boolean zIsResumed = isResumed();
            boolean zIsShown = this.rootView != null ? this.rootView.isShown() : false;
            Log.d(getClass().getSimpleName(), "isUserVisible:visible=" + zIsVisible + ",isResume:" + zIsResumed + ",rootViewShown:" + zIsShown);
            return zIsVisible && zIsResumed && zIsShown;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mActivity = (Activity) context;
    }

    protected abstract View onBindView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, Bundle bundle);

    @Override // androidx.fragment.app.Fragment
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        CarPowerManager.get().registerListener(this.carPowerListener);
    }

    @Override // androidx.fragment.app.Fragment
    @Nullable
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        this.mCurrentPresenter = (T) createPresenter();
        if (this.mCurrentPresenter != null) {
            this.mCurrentPresenter.attachView(this);
        }
        this.rootView = onBindView(layoutInflater, viewGroup, bundle);
        this.isCreateView = true;
        initView(this.rootView);
        initListener();
        return this.rootView;
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        if (this.onDestotyListener != null) {
            this.onDestotyListener.onDestoryCallback();
        }
        CarPowerManager.get().unregisterListener(this.carPowerListener);
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        if (this.mCurrentPresenter != null) {
            this.mCurrentPresenter.detachView();
        }
        this.isCreateView = false;
        onUnbindView();
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        Log.i("TAG", "onPause: " + getClass().getSimpleName());
    }

    public void onRefreshData() {
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        Log.i(getClass().getSimpleName(), "onResume:isFullScreen=" + this.isFullScreen + ", isSplitMode=" + this.isSplitMode);
        if (getActivity() != null) {
            SystemUtil.setSysFullScreen((Activity) Objects.requireNonNull(getActivity()), this.isFullScreen);
        }
        if (this.rootView instanceof ViewGroup) {
            ((ViewGroup) this.rootView).setMotionEventSplittingEnabled(this.isSplitMode);
        }
    }

    protected void onUnbindView() {
    }

    protected void setOnDestotyListener(OnDestotyListener onDestotyListener) {
        this.onDestotyListener = onDestotyListener;
    }

    protected void setResumeByPowerOn(boolean z) {
        Log.d("BaseMvpFragment", "setResumeByPowerOn " + z);
        this.mIsResumeByPowerOn = z;
    }
}
