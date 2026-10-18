package com.yftech.libmusic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.media.common.event.USBStateEvent;
import com.yftech.libmusic.event.BluetoothStateEvent;
import com.yftech.libmusic.event.CarLifeStatusEvent;
import com.yftech.libmusic.event.CarPlayStateEvent;
import org.greenrobot.eventbus.EventBus;

/* JADX INFO: loaded from: classes4.dex */
public class MusicSourceInformationReceiver extends BroadcastReceiver {
    public static final String ACTION_BLUETOOTH_STATUS_CHANGE = "ACTION_BLUETOOTH_STATUS_CHANGE";
    public static final String ACTION_CARLIFE_STATUS_CHANGE = "ACTION_CARLIFE_STATUS_CHANGE";
    public static final String ACTION_CARPLAY_STATUS_CHANGE = "ACTION_CARPLAY_STATUS_CHANGE";
    public static final String ACTION_USB_STATUS_CHANGE = "ACTION_USB_STATUS_CHANGE";
    public static final String TAG = "com.yftech.libmusic.MusicSourceInformationReceiver";

    /* JADX WARN: Code duplicated, block: B:25:0x0049  */
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case "ACTION_USB_STATUS_CHANGE":
                    EventBus.getDefault().post(new USBStateEvent(intent.getIntExtra("usbIndex", -1), intent.getBooleanExtra("isUsbExists", false)));
                    break;
                case "ACTION_BLUETOOTH_STATUS_CHANGE":
                    EventBus.getDefault().post(new BluetoothStateEvent(intent.getIntExtra("state", 0), intent.getStringExtra("deviceName")));
                    break;
                case "ACTION_CARLIFE_STATUS_CHANGE":
                    EventBus.getDefault().post(new CarLifeStatusEvent(intent.getBooleanExtra("mIsCarLifeConnect", false)));
                    break;
                case "ACTION_CARPLAY_STATUS_CHANGE":
                    EventBus.getDefault().post(new CarPlayStateEvent(intent.getIntExtra("carplay_state", 0)));
                    break;
            }
        }
    }
}
