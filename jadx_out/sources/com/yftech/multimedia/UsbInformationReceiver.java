package com.yftech.multimedia;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.media.common.event.USBStateForPhotoAndVideoEvent;
import org.greenrobot.eventbus.EventBus;

/* JADX INFO: loaded from: classes4.dex */
public class UsbInformationReceiver extends BroadcastReceiver {
    public static final String ACTION_USB_STATUS_CHANGE_IN_PHOTO_AND_VIDEO = "ACTION_USB_STATUS_CHANGE_IN_PHOTO_AND_VIDEO";
    public static final String TAG = "com.yftech.multimedia.UsbInformationReceiver";

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (((action.hashCode() == -1314300526 && action.equals(ACTION_USB_STATUS_CHANGE_IN_PHOTO_AND_VIDEO)) ? (byte) 0 : (byte) -1) != 0) {
            return;
        }
        EventBus.getDefault().post(new USBStateForPhotoAndVideoEvent(intent.getIntExtra("usbIndex", -1), intent.getBooleanExtra("isUsbExists", false)));
    }
}
