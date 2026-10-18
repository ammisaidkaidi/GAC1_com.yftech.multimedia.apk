package com.yftech.multimedia;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.media.common.util.AppConstants;
import com.media.common.util.LogUtil;
import com.media.common.util.MyFragmentManger;
import com.yftech.car.media.data.CarMediaDataManager;
import com.yftech.libgallery.event.PictureDeleteResultEvent;
import com.yftech.libmvpmodule.mvp.BaseMvpActivity;
import com.yftech.libmvpmodule.mvp.BaseMvpPresenter;
import com.yftech.multimedia.ui.MainFragment;
import com.yftech.multimedia.ui.SubjectFragment;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.greenrobot.eventbus.EventBus;

/* JADX INFO: loaded from: classes4.dex */
public class MainActivity extends BaseMvpActivity {
    private MainFragment mainFragment;
    private FrameLayout main_frame;
    private final UsbInformationReceiver receiver = new UsbInformationReceiver();
    private BroadcastReceiver testReceiver = new BroadcastReceiver() { // from class: com.yftech.multimedia.MainActivity.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Intent intent2;
            if ("MainActivity".equals(intent.getAction())) {
                int intExtra = intent.getIntExtra("value", -1);
                if (intExtra == 0) {
                    intent2 = new Intent("com.yftech.intent.action.start.radio.fm");
                } else if (intExtra == 1) {
                    intent2 = new Intent("com.yftech.intent.action.start.radio.am");
                } else if (intExtra == 2) {
                    intent2 = new Intent("com.yftech.intent.action.start.music.usb");
                    intent2.putExtra("music_source", AppConstants.MUSIC_SOURCE_OF_USB1);
                    intent2.putExtra("isAutoPlay", true);
                } else if (intExtra == 3) {
                    intent2 = new Intent("com.yftech.intent.action.start.music.usb");
                    intent2.putExtra("music_source", AppConstants.MUSIC_SOURCE_OF_USB2);
                    intent2.putExtra("isAutoPlay", true);
                } else {
                    intent2 = null;
                }
                if (intent2 != null) {
                    intent2.addFlags(268435456);
                    ActivityCompat.startActivity(MainActivity.this.getApplicationContext(), intent2, null);
                }
            }
        }
    };
    private final int RC_PHOTO_DELETE_RESULT = 1000;
    private Handler mHandler = new Handler(Looper.myLooper()) { // from class: com.yftech.multimedia.MainActivity.2
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 1000) {
                if (message.arg1 == 0) {
                    LogUtil.d("onDeleteSuccess");
                    EventBus.getDefault().post(new PictureDeleteResultEvent(true));
                } else {
                    LogUtil.d("onDeleteError");
                    EventBus.getDefault().post(new PictureDeleteResultEvent(false));
                }
            }
        }
    };
    private final CarMediaDataManager.OnDataChangeCallback dataChangeCallback = new CarMediaDataManager.OnDataChangeCallback() { // from class: com.yftech.multimedia.MainActivity.3
        public void onDeleteError(int i, int i2) {
            Message messageObtainMessage = MainActivity.this.mHandler.obtainMessage(1000);
            messageObtainMessage.arg1 = 1;
            MainActivity.this.mHandler.sendMessage(messageObtainMessage);
        }

        public void onDeleteSuccess(int i) {
            Message messageObtainMessage = MainActivity.this.mHandler.obtainMessage(1000);
            messageObtainMessage.arg1 = 0;
            MainActivity.this.mHandler.sendMessage(messageObtainMessage);
        }

        public void onMusicDataChange(int i) {
        }

        public void onPhotoDataChange(int i) {
        }

        public void onScanStatusChange(int i, boolean z) {
        }

        public void onTextDataChange(int i) {
            super.onTextDataChange(i);
        }

        public void onUsbStatusChange(int i, boolean z) throws Throwable {
            LogUtil.e("onUsbStatusChange:" + i + ", isUsbExists" + z);
            if (i >= 1) {
                return;
            }
            Intent intent = new Intent();
            intent.setAction(UsbInformationReceiver.ACTION_USB_STATUS_CHANGE_IN_PHOTO_AND_VIDEO);
            intent.putExtra("usbIndex", i);
            intent.putExtra("isUsbExists", z);
            LocalBroadcastManager.getInstance(MainActivity.this.getApplicationContext()).sendBroadcast(intent);
        }

        public void onVideoDataChange(int i) {
        }
    };

    private boolean checkNoneAnim(Intent intent) {
        if (intent == null) {
            LogUtil.d(" parseIntent is fail intent is null");
            return false;
        }
        boolean booleanExtra = intent.getBooleanExtra("isNoneAnim", false);
        LogUtil.d("checkNoneAnim flag =" + booleanExtra);
        return booleanExtra;
    }

    private void initW() {
        this.main_frame = (FrameLayout) findViewById(R.id.main_frame);
        ArrayList arrayList = new ArrayList();
        CarMediaDataManager carMediaDataManager = CarMediaDataManager.getInstance();
        List existUdiskList = carMediaDataManager.getExistUdiskList();
        if (existUdiskList != null) {
            for (int i = 0; i < existUdiskList.size(); i++) {
                int usbIndex = carMediaDataManager.getUsbIndex((String) existUdiskList.get(i));
                if (usbIndex < 1) {
                    arrayList.add(Integer.valueOf(usbIndex));
                }
            }
        }
        this.mainFragment = MainFragment.newInstance(arrayList);
        pushFragment(this.mainFragment);
        setSplitMotionAvailable(false);
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UsbInformationReceiver.ACTION_USB_STATUS_CHANGE_IN_PHOTO_AND_VIDEO);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(this.receiver, intentFilter);
    }

    private void registerTestReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("MainActivity");
        registerReceiver(this.testReceiver, intentFilter);
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseMvpActivity
    protected BaseMvpPresenter createPresenter() {
        return null;
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onActivityResult(int i, int i2, @Nullable Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (this.mainFragment != null) {
            this.mainFragment.onActivityResult(i, i2, intent);
        }
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        Stack<Fragment> fragmentStack = MyFragmentManger.getInstance().getFragmentStack(this.mainFragment.getTabIndex());
        Fragment fragmentPeek = (fragmentStack == null || fragmentStack.isEmpty()) ? null : fragmentStack.peek();
        if (this.mainFragment == null || fragmentStack == null || fragmentStack.isEmpty() || (fragmentPeek instanceof SubjectFragment)) {
            super.onBackPressed();
        } else {
            this.mainFragment.onBackPress();
        }
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseMvpActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        if (checkNoneAnim(getIntent())) {
            overridePendingTransition(0, 0);
        }
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        initW();
        CarMediaDataManager.getInstance().registerCarDataControlCallback(this.dataChangeCallback);
        registerReceiver();
        registerTestReceiver();
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseMvpActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d("Main onDestroy");
        if (this.mainFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(this.mainFragment).commitNowAllowingStateLoss();
        }
        CarMediaDataManager.getInstance().unregisterCarDataControlCallback(this.dataChangeCallback);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(this.receiver);
        this.mHandler.removeCallbacksAndMessages(null);
        unregisterReceiver(this.testReceiver);
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable("android:support:fragments", null);
    }

    public void pushFragment(@NonNull Fragment fragment) {
        FragmentTransaction fragmentTransactionBeginTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransactionBeginTransaction.add(R.id.main_frame, fragment, fragment.getClass().getName());
        fragmentTransactionBeginTransaction.commit();
    }

    public void pushFragment(@NonNull Fragment fragment, boolean z) {
        FragmentTransaction fragmentTransactionBeginTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransactionBeginTransaction.add(R.id.main_frame, fragment, fragment.getClass().getName());
        if (z) {
            fragmentTransactionBeginTransaction.addToBackStack(fragment.getClass().getName());
        }
        fragmentTransactionBeginTransaction.commit();
    }

    public void setSplitMotionAvailable(boolean z) {
        this.main_frame.setMotionEventSplittingEnabled(z);
    }
}
