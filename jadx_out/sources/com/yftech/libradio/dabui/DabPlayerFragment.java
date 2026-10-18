package com.yftech.libradio.dabui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.health.UidHealthStats;
import android.util.Log;
import androidx.annotation.Nullable;
import com.media.common.util.AppConstants;
import com.media.common.util.LogUtil;
import com.yftech.car.media.radio.dab.CarDabRadioManager;
import com.yftech.car.phonelink.CarPhoneLinkManager;
import com.yftech.libmvpmodule.mvp.BaseMvpFragment;
import com.yftech.libradio.presenter.DabMainPresenter;

/* JADX INFO: loaded from: classes4.dex */
public abstract class DabPlayerFragment extends BaseMvpFragment<DabMainPresenter> {
    private static final String ACTION_CARPLAY_EVENT_SHOW_UI = "com.autochips.carplay.event.show_ui";
    private static final String KEY_CARPLAY_EVENT_SHOW_UI = "ui_show";
    private CarplayShowUIReceiver mCarplayShowUIReceiver;
    private IntentFilter mIntentFilter;
    private boolean isAVMDisplaying = false;
    private final int RC_CARPLAY = UidHealthStats.MEASUREMENT_MOBILE_RX_MS;
    private final int RC_HICAR = UidHealthStats.MEASUREMENT_MOBILE_TX_MS;
    private boolean isCarPlayCalled = false;
    private boolean isCarplayDisconnected = false;
    private String mConnectedLinkStatus = "";
    protected final Handler mHandler = new Handler(Looper.getMainLooper()) { // from class: com.yftech.libradio.dabui.DabPlayerFragment.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what != 10025) {
                if (message.what == 10026) {
                    boolean zBooleanValue = ((Boolean) message.obj).booleanValue();
                    LogUtil.d("RC_CARPLAY->CarPlay isShow:" + zBooleanValue + ", isAVMDisplaying=" + DabPlayerFragment.this.isAVMDisplaying);
                    if (zBooleanValue || DabPlayerFragment.this.isAVMDisplaying || CarDabRadioManager.getInstance().isPlaying() || DabPlayerFragment.this.isHidden() || !DabPlayerFragment.this.hasWindowFocusEx()) {
                        return;
                    }
                    CarDabRadioManager.getInstance().play();
                    return;
                }
                return;
            }
            boolean zBooleanValue2 = ((Boolean) message.obj).booleanValue();
            LogUtil.d("RC_CARPLAY->CarPlay isShow:" + zBooleanValue2 + ", isAVMDisplaying=" + DabPlayerFragment.this.isAVMDisplaying + ", isCarPlayCalled=" + DabPlayerFragment.this.isCarPlayCalled + ", isCarplayDisconnected:" + DabPlayerFragment.this.isCarplayDisconnected);
            if (!zBooleanValue2 && !DabPlayerFragment.this.isAVMDisplaying && !DabPlayerFragment.this.isCarPlayCalled && !DabPlayerFragment.this.isCarplayDisconnected && !CarDabRadioManager.getInstance().isPlaying() && !DabPlayerFragment.this.isHidden() && DabPlayerFragment.this.hasWindowFocusEx()) {
                CarDabRadioManager.getInstance().play();
            }
            if (DabPlayerFragment.this.isCarPlayCalled && !zBooleanValue2) {
                DabPlayerFragment.this.isCarPlayCalled = false;
            }
            DabPlayerFragment.this.isCarplayDisconnected = false;
        }
    };
    private CarPhoneLinkManager.CarPhoneLinkCallback mCarPhoneLinkCallback = new CarPhoneLinkManager.CarPhoneLinkCallback() { // from class: com.yftech.libradio.dabui.DabPlayerFragment.2
        public void onCallStatusChanged(int i) {
            super.onCallStatusChanged(i);
            LogUtil.d("onCallStatusChanged() status:" + i);
            if (i == 1 || i == 2) {
                DabPlayerFragment.this.isCarPlayCalled = true;
            }
        }

        public void onConnectedStatusChanged(String str) {
            super.onConnectedStatusChanged(str);
            LogUtil.d("onConnectedStatusChanged() status:" + str);
            if ("carplay".equals(DabPlayerFragment.this.mConnectedLinkStatus) && "none".equals(str)) {
                DabPlayerFragment.this.isCarplayDisconnected = true;
            }
            DabPlayerFragment.this.mConnectedLinkStatus = str;
        }
    };
    private BroadcastReceiver receiver = new BroadcastReceiver() { // from class: com.yftech.libradio.dabui.DabPlayerFragment.3
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("com.yftech.avmservice.SHOW_STATE".equals(intent.getAction())) {
                boolean booleanExtra = intent.getBooleanExtra("isShow", false);
                LogUtil.d("com.yftech.avmservice.SHOW_STATE:isShow=" + booleanExtra);
                DabPlayerFragment.this.isAVMDisplaying = booleanExtra;
            }
        }
    };

    private class CarplayShowUIReceiver extends BroadcastReceiver {
        private CarplayShowUIReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (DabPlayerFragment.ACTION_CARPLAY_EVENT_SHOW_UI.equals(action)) {
                boolean booleanExtra = intent.getBooleanExtra(DabPlayerFragment.KEY_CARPLAY_EVENT_SHOW_UI, true);
                LogUtil.i("onReceive() ACTION_CARPLAY_EVENT_SHOW_UI isShow:" + booleanExtra);
                Message messageObtainMessage = DabPlayerFragment.this.mHandler.obtainMessage(UidHealthStats.MEASUREMENT_MOBILE_RX_MS);
                messageObtainMessage.obj = Boolean.valueOf(booleanExtra);
                DabPlayerFragment.this.mHandler.sendMessageDelayed(messageObtainMessage, 500L);
                return;
            }
            if (AppConstants.ACTION_HICAR_SHOW_STATE.equals(action)) {
                boolean booleanExtra2 = intent.getBooleanExtra("isShow", false);
                LogUtil.d("com.yftech.hicar.show:isShow=" + booleanExtra2);
                Message messageObtainMessage2 = DabPlayerFragment.this.mHandler.obtainMessage(UidHealthStats.MEASUREMENT_MOBILE_TX_MS);
                messageObtainMessage2.obj = Boolean.valueOf(booleanExtra2);
                DabPlayerFragment.this.mHandler.sendMessageDelayed(messageObtainMessage2, 500L);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean hasWindowFocusEx() {
        boolean zIsUserVisible = isUserVisible();
        boolean zHasWindowFocus = getActivity() != null ? getActivity().hasWindowFocus() : false;
        LogUtil.d("hasWindowFocusEx=" + zHasWindowFocus);
        return zHasWindowFocus && zIsUserVisible;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.yftech.libmvpmodule.mvp.BaseMvpFragment
    public DabMainPresenter createPresenter() {
        return new DabMainPresenter(getActivity());
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseMvpFragment, androidx.fragment.app.Fragment
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        LogUtil.d("onCreate()");
        this.mIntentFilter = new IntentFilter();
        this.mIntentFilter.addAction(ACTION_CARPLAY_EVENT_SHOW_UI);
        this.mIntentFilter.addAction("com.yftech.avmservice.SHOW_STATE");
        this.mIntentFilter.addAction(AppConstants.ACTION_HICAR_SHOW_STATE);
        this.mCarplayShowUIReceiver = new CarplayShowUIReceiver();
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseMvpFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        Log.i("TAG", "onPause: " + getClass().getSimpleName());
        this.isCarPlayCalled = false;
        this.isCarplayDisconnected = false;
        this.mHandler.removeCallbacksAndMessages(null);
        CarPhoneLinkManager.getInstance().unregisterCarPhoneLinkCallback(this.mCarPhoneLinkCallback);
        getContext().unregisterReceiver(this.mCarplayShowUIReceiver);
        getContext().unregisterReceiver(this.receiver);
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseMvpFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mConnectedLinkStatus = CarPhoneLinkManager.getInstance().getConnectedLinkStatus();
        CarPhoneLinkManager.getInstance().registerCarPhoneLinkCallback(this.mCarPhoneLinkCallback, this.mHandler);
        getContext().registerReceiver(this.mCarplayShowUIReceiver, this.mIntentFilter);
        getContext().registerReceiver(this.receiver, this.mIntentFilter);
    }
}
