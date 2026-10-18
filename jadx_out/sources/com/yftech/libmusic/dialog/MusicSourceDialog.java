package com.yftech.libmusic.dialog;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;
import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.android.internal.logging.nano.MetricsProto;
import com.google.android.material.badge.BadgeDrawable;
import com.media.common.ui.widget.TipsDialog;
import com.media.common.util.AppConstants;
import com.media.common.util.LogUtil;
import com.media.common.util.ToastUtils;
import com.yftech.car.bluetooth.CarBluetoothManager;
import com.yftech.car.bluetooth.callback.CarBluetoothMusicCallback;
import com.yftech.car.media.data.CarMediaDataManager;
import com.yftech.libmusic.R;
import com.yftech.libmusic.dialog.adapter.MusicSourceAdapter;
import com.yftech.libmusic.event.MusicSourceChangedEvent;
import com.yftech.libmusic.utils.CarLifeMusicManager;
import com.yftech.libmusic.utils.CarPlayMusicManager;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/* JADX INFO: loaded from: classes4.dex */
public class MusicSourceDialog extends DialogFragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "music_source";
    private List<DelegateAdapter.Adapter> adapters;
    private DelegateAdapter delegateAdapter;
    private MusicSourceAdapter musicSourceAdapter;
    private String music_source;
    private OnMusicSourceSelectedListener onMusicSourceSelectedListener;
    private boolean dismissOutOfView = false;
    private TipsDialog usbTipsDialog = null;
    private final int RC_DISMISS = 10;
    private final int DISMISS_INTERVAL = 15000;
    private final Handler mHandler = new Handler(Looper.getMainLooper()) { // from class: com.yftech.libmusic.dialog.MusicSourceDialog.1
        @Override // android.os.Handler
        public void handleMessage(@NonNull Message message) {
            if (message.what == 10) {
                MusicSourceDialog.this.dismissOutOfView = false;
                MusicSourceDialog.this.dismissAllowingStateLoss();
            }
        }
    };
    private CarMediaDataManager.OnDataChangeCallback callback = new CarMediaDataManager.OnDataChangeCallback() { // from class: com.yftech.libmusic.dialog.MusicSourceDialog.2
        public void onScanStatusChange(int i, boolean z) {
            LogUtil.d("onScanStatusChange:usbIndex" + i + ", isScanner=" + z);
            if (z) {
                return;
            }
            boolean z2 = CarMediaDataManager.getInstance().getMusicFileExists() != 0;
            if (MusicSourceDialog.this.musicSourceAdapter != null) {
                if (MusicSourceDialog.this.musicSourceAdapter.getEnable(i == 0 ? AppConstants.MUSIC_SOURCE_OF_USB1 : AppConstants.MUSIC_SOURCE_OF_USB2) == 2) {
                    return;
                }
                MusicSourceDialog.this.musicSourceAdapter.setEnable(i == 0 ? AppConstants.MUSIC_SOURCE_OF_USB1 : AppConstants.MUSIC_SOURCE_OF_USB2, z2 ? 0 : 3);
            }
        }

        public void onUsbStatusChange(int i, boolean z) throws Throwable {
            LogUtil.d("onUsbStatusChange:usbIndex=" + i + ", isUsbExists=" + z);
            if (z) {
                if (MusicSourceDialog.this.musicSourceAdapter != null) {
                    MusicSourceDialog.this.musicSourceAdapter.setEnable(i == 0 ? AppConstants.MUSIC_SOURCE_OF_USB1 : AppConstants.MUSIC_SOURCE_OF_USB2, 1);
                }
            } else if (MusicSourceDialog.this.musicSourceAdapter != null) {
                if (MusicSourceDialog.this.usbTipsDialog != null && MusicSourceDialog.this.usbTipsDialog.isVisible()) {
                    MusicSourceDialog.this.usbTipsDialog.dismissAllowingStateLoss();
                    MusicSourceDialog.this.usbTipsDialog = null;
                }
                String typeBySelection = MusicSourceDialog.this.musicSourceAdapter.getTypeBySelection();
                if (!TextUtils.isEmpty(typeBySelection)) {
                    if (typeBySelection.equals(i == 0 ? AppConstants.MUSIC_SOURCE_OF_USB1 : AppConstants.MUSIC_SOURCE_OF_USB2)) {
                        MusicSourceDialog.this.musicSourceAdapter.setSelection(-1);
                    }
                }
                MusicSourceDialog.this.musicSourceAdapter.setEnable(i == 0 ? AppConstants.MUSIC_SOURCE_OF_USB1 : AppConstants.MUSIC_SOURCE_OF_USB2, 2);
            }
        }
    };
    private CarBluetoothMusicCallback bluetoothMusicCallback = new CarBluetoothMusicCallback() { // from class: com.yftech.libmusic.dialog.MusicSourceDialog.3
        public void onBluetoothAvrcpConnectStateChanged(BluetoothDevice bluetoothDevice, int i) throws Throwable {
            if (i == 2) {
                MusicSourceDialog.this.musicSourceAdapter.setBlueToothEnable(true);
                return;
            }
            MusicSourceDialog.this.musicSourceAdapter.setBlueToothEnable(false);
            String typeBySelection = MusicSourceDialog.this.musicSourceAdapter.getTypeBySelection();
            if (TextUtils.isEmpty(typeBySelection) || !typeBySelection.equals(AppConstants.MUSIC_SOURCE_OF_BLUETOOTH)) {
                return;
            }
            MusicSourceDialog.this.musicSourceAdapter.setSelection(-1);
        }
    };
    private CarLifeMusicManager.OnCarLifeDataChangedCallback carLifeDataChangedCallback = new CarLifeMusicManager.OnCarLifeDataChangedCallback() { // from class: com.yftech.libmusic.dialog.MusicSourceDialog.4
        @Override // com.yftech.libmusic.utils.CarLifeMusicManager.OnCarLifeDataChangedCallback
        public void onConnectionStatusChanged(boolean z) throws Throwable {
            if (MusicSourceDialog.this.musicSourceAdapter != null) {
                int i = z ? 0 : 2;
                if (!z) {
                    String typeBySelection = MusicSourceDialog.this.musicSourceAdapter.getTypeBySelection();
                    if (!TextUtils.isEmpty(typeBySelection) && typeBySelection.equals(AppConstants.MUSIC_SOURCE_OF_CARLIFE)) {
                        MusicSourceDialog.this.musicSourceAdapter.setSelection(-1);
                    }
                }
                MusicSourceDialog.this.musicSourceAdapter.setEnable(AppConstants.MUSIC_SOURCE_OF_CARLIFE, i);
            }
        }

        @Override // com.yftech.libmusic.utils.CarLifeMusicManager.OnCarLifeDataChangedCallback
        public void onMusicDurationChanged(int i) {
        }

        @Override // com.yftech.libmusic.utils.CarLifeMusicManager.OnCarLifeDataChangedCallback
        public void onMusicInformationChanged(CarLifeMusicManager.OnCarLifeDataChangedCallback.MusicInfo musicInfo) {
        }

        @Override // com.yftech.libmusic.utils.CarLifeMusicManager.OnCarLifeDataChangedCallback
        public void onMusicPlayStatusChanged(int i) {
        }

        @Override // com.yftech.libmusic.utils.CarLifeMusicManager.OnCarLifeDataChangedCallback
        public void onMusicProgressChanged(int i) {
        }
    };
    private final CarPlayMusicManager.CarPlayMusicListener carplayCallback = new CarPlayMusicManager.CarPlayMusicListener() { // from class: com.yftech.libmusic.dialog.MusicSourceDialog.5
        @Override // com.yftech.libmusic.utils.CarPlayMusicManager.CarPlayMusicListener
        public void onCarplayConnectChanged(int i) throws Throwable {
            int i2;
            if (MusicSourceDialog.this.musicSourceAdapter != null) {
                switch (i) {
                    case 1:
                        i2 = 1;
                        break;
                    case 2:
                        i2 = 0;
                        break;
                    default:
                        i2 = 2;
                        break;
                }
                if (i != 2) {
                    String typeBySelection = MusicSourceDialog.this.musicSourceAdapter.getTypeBySelection();
                    if (!TextUtils.isEmpty(typeBySelection) && typeBySelection.equals(AppConstants.MUSIC_SOURCE_OF_CARPLAY)) {
                        MusicSourceDialog.this.musicSourceAdapter.setSelection(-1);
                    }
                }
                MusicSourceDialog.this.musicSourceAdapter.setEnable(AppConstants.MUSIC_SOURCE_OF_CARPLAY, i2);
            }
        }

        @Override // com.yftech.libmusic.utils.CarPlayMusicManager.CarPlayMusicListener
        public void onCarplayMusicID3Changed(String str, String str2) {
        }

        @Override // com.yftech.libmusic.utils.CarPlayMusicManager.CarPlayMusicListener
        public void onCarplayPlayingChanged(boolean z) {
        }

        @Override // com.yftech.libmusic.utils.CarPlayMusicManager.CarPlayMusicListener
        public void onCarplayProgressChanged(int i) {
        }

        @Override // com.yftech.libmusic.utils.CarPlayMusicManager.CarPlayMusicListener
        public void onCarplayTotalDurationChanged(int i) {
        }

        @Override // com.yftech.libmusic.utils.CarPlayMusicManager.CarPlayMusicListener
        public void onResourceStateChanged(boolean z) {
        }
    };

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public @interface MusicSourceType {
    }

    public interface OnMusicSourceSelectedListener {
        void onSelected(String str, int i, String str2);
    }

    private void initData() throws Throwable {
        int iFindPositionBySource;
        this.musicSourceAdapter = new MusicSourceAdapter((Context) Objects.requireNonNull(getContext()));
        if (!TextUtils.isEmpty(this.music_source) && (iFindPositionBySource = this.musicSourceAdapter.findPositionBySource(this.music_source)) >= 0) {
            this.musicSourceAdapter.setSelection(iFindPositionBySource);
        }
        this.musicSourceAdapter.setBlueToothEnable(CarBluetoothManager.getInstance().isConnected(12));
        this.musicSourceAdapter.setOnItemClickListener(new MusicSourceAdapter.OnItemClickListener() { // from class: com.yftech.libmusic.dialog.MusicSourceDialog.7
            @Override // com.yftech.libmusic.dialog.adapter.MusicSourceAdapter.OnItemClickListener
            public void onItemClickListener(int i, int i2, String str, String str2) throws Throwable {
                String string;
                LogUtil.d("onItemClickListener ,enable==" + i2 + ",type==" + str2);
                if (MusicSourceDialog.this.mHandler.hasMessages(10)) {
                    MusicSourceDialog.this.mHandler.removeMessages(10);
                }
                MusicSourceDialog.this.mHandler.sendEmptyMessageDelayed(10, 15000L);
                if (i2 != 0) {
                    if (!AppConstants.MUSIC_SOURCE_OF_USB1.equals(str2) && !AppConstants.MUSIC_SOURCE_OF_USB2.equals(str2)) {
                        if (AppConstants.MUSIC_SOURCE_OF_CARLIFE.equals(str2) || AppConstants.MUSIC_SOURCE_OF_CARPLAY.equals(str2)) {
                            TipsDialog.newInstance(String.format(Locale.getDefault(), MusicSourceDialog.this.getString(R.string.music_source_not_connected_tips), str)).show(MusicSourceDialog.this.getChildFragmentManager(), TipsDialog.class.getSimpleName());
                            return;
                        }
                        return;
                    }
                    if (i2 != 1) {
                        string = i2 != 3 ? MusicSourceDialog.this.getString(R.string.music_source_not_found_tips) : MusicSourceDialog.this.getString(R.string.music_source_usb_music_file_not_found_tips);
                    } else {
                        string = MusicSourceDialog.this.getString(R.string.music_source_loading_tips);
                    }
                    if (MusicSourceDialog.this.usbTipsDialog != null) {
                        MusicSourceDialog.this.usbTipsDialog.dismissAllowingStateLoss();
                        MusicSourceDialog.this.usbTipsDialog = null;
                    }
                    MusicSourceDialog.this.usbTipsDialog = TipsDialog.newInstance(string);
                    MusicSourceDialog.this.usbTipsDialog.show(MusicSourceDialog.this.getChildFragmentManager(), TipsDialog.class.getSimpleName());
                    return;
                }
                if (TextUtils.isEmpty(str2) || !str2.equals(AppConstants.MUSIC_SOURCE_OF_BLUETOOTH) || MusicSourceDialog.this.musicSourceAdapter.isBlueToothEnable()) {
                    if (MusicSourceDialog.this.onMusicSourceSelectedListener != null && MusicSourceDialog.this.musicSourceAdapter.getSelection() != i) {
                        MusicSourceDialog.this.onMusicSourceSelectedListener.onSelected(str, i2, str2);
                    }
                    MusicSourceDialog.this.musicSourceAdapter.setSelection(i);
                    MusicSourceDialog.this.dismissOutOfView = false;
                    MusicSourceDialog.this.dismissAllowingStateLoss();
                    return;
                }
                try {
                    Intent intent = new Intent();
                    intent.setAction("com.systemui.bt.show");
                    MusicSourceDialog.this.getContext().sendBroadcast(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.showFailMessage(MusicSourceDialog.this.getString(R.string.not_find_bt_setting_entry), 1);
                }
                MusicSourceDialog.this.dismissAllowingStateLoss();
            }
        });
        this.adapters.add(this.musicSourceAdapter);
        this.delegateAdapter.setAdapters(this.adapters);
    }

    private void initW(View view) {
        ((ImageView) view.findViewById(R.id.iv_close)).setOnClickListener(this);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() { // from class: com.yftech.libmusic.dialog.MusicSourceDialog.6
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(@NonNull RecyclerView recyclerView2, int i, int i2) {
                if (MusicSourceDialog.this.mHandler.hasMessages(10)) {
                    MusicSourceDialog.this.mHandler.removeMessages(10);
                }
                MusicSourceDialog.this.mHandler.sendEmptyMessageDelayed(10, 15000L);
            }
        });
        VirtualLayoutManager virtualLayoutManager = new VirtualLayoutManager(getContext());
        recyclerView.setLayoutManager(virtualLayoutManager);
        RecyclerView.RecycledViewPool recycledViewPool = new RecyclerView.RecycledViewPool();
        recyclerView.setRecycledViewPool(recycledViewPool);
        recycledViewPool.setMaxRecycledViews(0, 20);
        this.delegateAdapter = new DelegateAdapter(virtualLayoutManager, true);
        recyclerView.setAdapter(this.delegateAdapter);
        this.adapters = new LinkedList();
    }

    public static MusicSourceDialog newInstance(String str) {
        MusicSourceDialog musicSourceDialog = new MusicSourceDialog();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM1, str);
        musicSourceDialog.setArguments(bundle);
        return musicSourceDialog;
    }

    public void dismissCustom() {
        this.dismissOutOfView = false;
        dismissAllowingStateLoss();
    }

    @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnCancelListener
    public void onCancel(@NonNull DialogInterface dialogInterface) {
        super.onCancel(dialogInterface);
        this.dismissOutOfView = true;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view.getId() == R.id.iv_close) {
            this.dismissOutOfView = false;
            dismiss();
        }
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() != null) {
            this.music_source = getArguments().getString(ARG_PARAM1);
        }
        CarMediaDataManager.getInstance().registerCarDataControlCallback(this.callback);
        CarBluetoothManager.getInstance().registerBluetoothMusicCallback(this.bluetoothMusicCallback, (Handler) null);
        CarLifeMusicManager.getInstance().registerMusicControlCallback(this.carLifeDataChangedCallback);
        CarPlayMusicManager.getInstance(getActivity()).registerCarPlayMusicListener(this.carplayCallback);
        EventBus.getDefault().register(this);
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) throws Throwable {
        View viewInflate = layoutInflater.inflate(R.layout.dialog_music_source, viewGroup, false);
        initW(viewInflate);
        initData();
        return viewInflate;
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        this.mHandler.removeMessages(10);
        CarMediaDataManager.getInstance().unregisterCarDataControlCallback(this.callback);
        CarBluetoothManager.getInstance().unregisterBluetoothMusicCallback(this.bluetoothMusicCallback);
        CarLifeMusicManager.getInstance().unregisterMusicControlCallback(this.carLifeDataChangedCallback);
        CarPlayMusicManager.getInstance(getActivity()).unregisterCarPlayMusicListener(this.carplayCallback);
        EventBus.getDefault().unregister(this);
    }

    @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnDismissListener
    public void onDismiss(@NonNull DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        this.dismissOutOfView = false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMusicSourceChanged(MusicSourceChangedEvent musicSourceChangedEvent) throws Throwable {
        int iFindPositionBySource;
        this.music_source = musicSourceChangedEvent.music_source;
        if (this.musicSourceAdapter == null || TextUtils.isEmpty(this.music_source) || (iFindPositionBySource = this.musicSourceAdapter.findPositionBySource(this.music_source)) < 0) {
            return;
        }
        this.musicSourceAdapter.setSelection(iFindPositionBySource);
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        if (this.mHandler.hasMessages(10)) {
            this.mHandler.removeMessages(10);
        }
        this.mHandler.sendEmptyMessageDelayed(10, 15000L);
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onStart() {
        Window window;
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog == null || (window = dialog.getWindow()) == null) {
            return;
        }
        window.setBackgroundDrawableResource(17170445);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.dimAmount = 0.0f;
        if ("ar".equals(Locale.getDefault().getLanguage())) {
            attributes.gravity = BadgeDrawable.TOP_END;
            attributes.windowAnimations = R.style.DialogLeftAnimation;
        } else {
            attributes.gravity = BadgeDrawable.TOP_START;
            attributes.windowAnimations = R.style.DialogRightAnimation;
        }
        attributes.width = MetricsProto.MetricsEvent.DIALOG_DREAM_START_DELAY;
        attributes.height = MetricsProto.MetricsEvent.ACTION_PERMISSION_DENIED_PROCESS_OUTGOING_CALLS;
        attributes.y = 34;
        attributes.x = 180;
        window.setAttributes(attributes);
    }

    public void setOnMusicSourceSelectedListener(OnMusicSourceSelectedListener onMusicSourceSelectedListener) {
        this.onMusicSourceSelectedListener = onMusicSourceSelectedListener;
    }
}
