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
import androidx.annotation.Nullable;
import com.media.common.util.AppConstants;
import com.media.common.util.LogUtil;
import com.yftech.car.media.radio.dab.CarDabRadioManager;
import com.yftech.car.media.radio.dab.entity.DabScanStatus;
import com.yftech.car.phonelink.CarPhoneLinkManager;
import com.yftech.libmvpmodule.mvp.BaseMvpActivity;
import com.yftech.libmvpmodule.mvp.BaseMvpPresenter;

/* JADX INFO: loaded from: classes4.dex */
public abstract class DabPlayerActivity<T extends BaseMvpPresenter> extends BaseMvpActivity {
    private static final String ACTION_CARPLAY_EVENT_SHOW_UI = "com.autochips.carplay.event.show_ui";
    private static final String KEY_CARPLAY_EVENT_SHOW_UI = "ui_show";
    private DabPlayerActivity<T>.CarplayShowUIReceiver mCarplayShowUIReceiver;
    private IntentFilter mIntentFilter;
    private boolean isAVMDisplaying = false;
    private final int RC_CARPLAY = UidHealthStats.MEASUREMENT_MOBILE_RX_MS;
    private final int RC_HICAR = UidHealthStats.MEASUREMENT_MOBILE_TX_MS;
    private boolean isCarPlayCalled = false;
    private boolean isCarplayDisconnected = false;
    private String mConnectedLinkStatus = "";
    protected final Handler mHandler = new Handler(Looper.getMainLooper()) { // from class: com.yftech.libradio.dabui.DabPlayerActivity.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what != 10025) {
                if (message.what == 10026) {
                    boolean zBooleanValue = ((Boolean) message.obj).booleanValue();
                    LogUtil.d("RC_CARPLAY->CarPlay isShow:" + zBooleanValue + ", isAVMDisplaying=" + DabPlayerActivity.this.isAVMDisplaying);
                    if (zBooleanValue || DabPlayerActivity.this.isAVMDisplaying || CarDabRadioManager.getInstance().isPlaying()) {
                        return;
                    }
                    CarDabRadioManager.getInstance().play();
                    return;
                }
                return;
            }
            boolean zBooleanValue2 = ((Boolean) message.obj).booleanValue();
            LogUtil.d("RC_CARPLAY->CarPlay isShow:" + zBooleanValue2 + ", isAVMDisplaying=" + DabPlayerActivity.this.isAVMDisplaying + ", isCarPlayCalled=" + DabPlayerActivity.this.isCarPlayCalled + ", isCarplayDisconnected:" + DabPlayerActivity.this.isCarplayDisconnected);
            if (!zBooleanValue2 && !DabPlayerActivity.this.isAVMDisplaying && !DabPlayerActivity.this.isCarPlayCalled && !DabPlayerActivity.this.isCarplayDisconnected && !CarDabRadioManager.getInstance().isPlaying()) {
                CarDabRadioManager.getInstance().play();
            }
            if (DabPlayerActivity.this.isCarPlayCalled && !zBooleanValue2) {
                DabPlayerActivity.this.isCarPlayCalled = false;
            }
            DabPlayerActivity.this.isCarplayDisconnected = false;
        }
    };
    private CarPhoneLinkManager.CarPhoneLinkCallback mCarPhoneLinkCallback = new CarPhoneLinkManager.CarPhoneLinkCallback() { // from class: com.yftech.libradio.dabui.DabPlayerActivity.2
        public void onCallStatusChanged(int i) {
            super.onCallStatusChanged(i);
            LogUtil.d("onCallStatusChanged() status:" + i);
            if (i == 1 || i == 2) {
                DabPlayerActivity.this.isCarPlayCalled = true;
            }
        }

        public void onConnectedStatusChanged(String str) {
            super.onConnectedStatusChanged(str);
            LogUtil.d("onConnectedStatusChanged() status:" + str);
            if ("carplay".equals(DabPlayerActivity.this.mConnectedLinkStatus) && "none".equals(str)) {
                DabPlayerActivity.this.isCarplayDisconnected = true;
            }
            DabPlayerActivity.this.mConnectedLinkStatus = str;
        }
    };
    private BroadcastReceiver receiver = new BroadcastReceiver() { // from class: com.yftech.libradio.dabui.DabPlayerActivity.3
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("com.yftech.avmservice.SHOW_STATE".equals(intent.getAction())) {
                boolean booleanExtra = intent.getBooleanExtra("isShow", false);
                LogUtil.d("com.yftech.avmservice.SHOW_STATE:isShow=" + booleanExtra);
                DabPlayerActivity.this.isAVMDisplaying = booleanExtra;
            }
        }
    };

    private class CarplayShowUIReceiver extends BroadcastReceiver {
        private CarplayShowUIReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (DabPlayerActivity.ACTION_CARPLAY_EVENT_SHOW_UI.equals(action)) {
                boolean booleanExtra = intent.getBooleanExtra(DabPlayerActivity.KEY_CARPLAY_EVENT_SHOW_UI, true);
                LogUtil.i("onReceive() ACTION_CARPLAY_EVENT_SHOW_UI isShow:" + booleanExtra);
                Message messageObtainMessage = DabPlayerActivity.this.mHandler.obtainMessage(UidHealthStats.MEASUREMENT_MOBILE_RX_MS);
                messageObtainMessage.obj = Boolean.valueOf(booleanExtra);
                DabPlayerActivity.this.mHandler.sendMessageDelayed(messageObtainMessage, 500L);
                return;
            }
            if (AppConstants.ACTION_HICAR_SHOW_STATE.equals(action)) {
                boolean booleanExtra2 = intent.getBooleanExtra("isShow", false);
                LogUtil.d("com.yftech.hicar.show:isShow=" + booleanExtra2);
                Message messageObtainMessage2 = DabPlayerActivity.this.mHandler.obtainMessage(UidHealthStats.MEASUREMENT_MOBILE_TX_MS);
                messageObtainMessage2.obj = Boolean.valueOf(booleanExtra2);
                DabPlayerActivity.this.mHandler.sendMessageDelayed(messageObtainMessage2, 500L);
            }
        }
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseMvpActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        LogUtil.d("onCreate()");
        this.mIntentFilter = new IntentFilter();
        this.mIntentFilter.addAction(ACTION_CARPLAY_EVENT_SHOW_UI);
        this.mIntentFilter.addAction("com.yftech.avmservice.SHOW_STATE");
        this.mIntentFilter.addAction(AppConstants.ACTION_HICAR_SHOW_STATE);
        this.mCarplayShowUIReceiver = new CarplayShowUIReceiver();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onStart() {
        super.onStart();
        this.mConnectedLinkStatus = CarPhoneLinkManager.getInstance().getConnectedLinkStatus();
        CarPhoneLinkManager.getInstance().registerCarPhoneLinkCallback(this.mCarPhoneLinkCallback, this.mHandler);
        registerReceiver(this.mCarplayShowUIReceiver, this.mIntentFilter);
        registerReceiver(this.receiver, this.mIntentFilter);
        boolean zIsPlaying = CarDabRadioManager.getInstance().isPlaying();
        DabScanStatus scanStatus = CarDabRadioManager.getInstance().getScanStatus();
        boolean z = scanStatus != null && scanStatus.isRuning();
        LogUtil.d("onStart() isPlaying:" + zIsPlaying + " connectedLinkStatus:" + this.mConnectedLinkStatus + " isScanning:" + z);
        if (zIsPlaying || z) {
            return;
        }
        CarDabRadioManager.getInstance().play();
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseMvpActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onStop() {
        super.onStop();
        LogUtil.d("onStop()");
        this.isCarPlayCalled = false;
        this.isCarplayDisconnected = false;
        this.mHandler.removeCallbacksAndMessages(null);
        CarPhoneLinkManager.getInstance().unregisterCarPhoneLinkCallback(this.mCarPhoneLinkCallback);
        unregisterReceiver(this.mCarplayShowUIReceiver);
        unregisterReceiver(this.receiver);
    }
}
