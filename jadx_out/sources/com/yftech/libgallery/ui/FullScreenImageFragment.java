package com.yftech.libgallery.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import android.util.TimedRemoteCaller;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.imagepipeline.image.ImageInfo;
import com.media.common.ui.widget.MyActionBar;
import com.media.common.ui.widget.zoomable.DoubleTapGestureListener;
import com.media.common.ui.widget.zoomable.ZoomableDraweeView;
import com.media.common.util.AudioSettingUtil;
import com.media.common.util.LogUtil;
import com.media.common.util.MyFragmentManger;
import com.media.common.util.SharedPrefsUtil;
import com.media.common.util.ToastUtils;
import com.yftech.car.bluetooth.CarBluetoothManager;
import com.yftech.car.bluetooth.callback.CarBluetoothPhoneCallback;
import com.yftech.car.bluetooth.model.PhoneCall;
import com.yftech.car.input.CarInputManager;
import com.yftech.car.input.CarKey;
import com.yftech.car.media.data.CarMediaDataManager;
import com.yftech.car.media.data.entity.MediaFile;
import com.yftech.car.screen.CarScreenManager;
import com.yftech.libgallery.R;
import com.yftech.libgallery.presenter.FullScreenImagePresenter;
import com.yftech.libgallery.utils.MyPhotoDataManager;
import com.yftech.libgallery.view.IFullScreenImageFragmentView;
import com.yftech.libmusic.utils.CarPlayMusicManager;
import com.yftech.libmvpmodule.mvp.BaseMvpActivity;
import com.yftech.libmvpmodule.mvp.BaseMvpFragment;
import com.yftech.libmvpmodule.utils.Constants;
import com.yftech.vehicle.sdk.HostConfig;
import java.io.File;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;

/* JADX INFO: loaded from: classes4.dex */
public class FullScreenImageFragment extends BaseMvpFragment<FullScreenImagePresenter> implements IFullScreenImageFragmentView, View.OnClickListener {
    private static final String ARG_PARAM1 = "imagePath";
    private static final String ARG_PARAM2 = "index";
    private MyActionBar actionBar;
    private FrameLayout control_bar;
    private long currentMillis;
    private ZoomableDraweeView dv_image;
    private String imagePath;
    private int index;
    private ImageView iv_next;
    private ImageView iv_previous;
    private ImageView iv_slide;
    private List<MediaFile> paths;
    private int position;
    private int rotation;
    private boolean isPlaying = true;
    private boolean isLoading = true;
    private boolean isReturnByUser = false;
    private final int[] rotation_selection = {0, 270, 180, 90};
    private int rotation_index = 0;
    private int forward = 0;
    private final int RC_CONTROL_BAR = 10;
    private final int RC_IMAGE_SWITCH = 99;
    private final int INTERVAL = 5000;
    private final int IMAGE_INTERVAL = 5000;
    private final int RC_INPUT = 20;
    private final int RC_BLUETOOTH = 30;
    private boolean isPhoneDisconnected = true;
    private boolean isOpen = true;
    private boolean isRegisterCallback = false;
    private final Handler mHandler = new Handler(Looper.getMainLooper()) { // from class: com.yftech.libgallery.ui.FullScreenImageFragment.1
        @Override // android.os.Handler
        public void handleMessage(@NonNull Message message) {
            if (message.what == 10) {
                FullScreenImageFragment.this.setControlBarVisible(8);
                return;
            }
            if (message.what == 99) {
                if (FullScreenImageFragment.this.paths == null || FullScreenImageFragment.this.paths.isEmpty()) {
                    return;
                }
                StringBuilder sb = new StringBuilder();
                sb.append("RC_IMAGE_SWITCH:has message=");
                sb.append(hasMessages(99));
                sb.append(", isOverTime=");
                sb.append(FullScreenImageFragment.this.currentMillis + TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS < System.currentTimeMillis());
                LogUtil.d(sb.toString());
                FullScreenImageFragment.this.forward = 1;
                FullScreenImageFragment.this.position = (FullScreenImageFragment.this.position + 1) % FullScreenImageFragment.this.paths.size();
                FullScreenImageFragment.this.displayImage();
                FullScreenImageFragment.this.currentMillis = System.currentTimeMillis();
                if (FullScreenImageFragment.this.mHandler.hasMessages(99)) {
                    FullScreenImageFragment.this.mHandler.removeMessages(99);
                }
                FullScreenImageFragment.this.mHandler.sendEmptyMessageDelayed(99, TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
                return;
            }
            if (message.what == 20) {
                int i = message.arg1;
                if (i == 1) {
                    FullScreenImageFragment.this.iv_next.callOnClick();
                    return;
                } else {
                    if (i == 2) {
                        FullScreenImageFragment.this.iv_previous.callOnClick();
                        return;
                    }
                    return;
                }
            }
            if (message.what == 30) {
                LogUtil.d("onBluetoothPhoneCallStateChanged:status=" + message.arg1);
                if (message.arg1 == 7 && !FullScreenImageFragment.this.isPhoneDisconnected) {
                    LogUtil.d("onBluetoothPhoneCallStateChanged:onResume");
                    FullScreenImageFragment.this.onResume();
                    FullScreenImageFragment.this.isPhoneDisconnected = true;
                } else if (FullScreenImageFragment.this.isPhoneDisconnected) {
                    LogUtil.d("onBluetoothPhoneCallStateChanged:onStop");
                    FullScreenImageFragment.this.onStop();
                    FullScreenImageFragment.this.isPhoneDisconnected = false;
                }
            }
        }
    };
    private CarScreenManager.CarScreenCallback carScreenCallback = new CarScreenManager.CarScreenCallback() { // from class: com.yftech.libgallery.ui.FullScreenImageFragment.2
        public void onChangedDayNightMode(int i) {
            LogUtil.d("onChangedDayNightMode:mode=" + i);
        }

        public void onChangedScreenBrightness(boolean z, int i) {
            LogUtil.d("onChangedScreenBrightness:open=" + z + ", brightness=" + i + ", isOpen=" + FullScreenImageFragment.this.isOpen);
            if (FullScreenImageFragment.this.isOpen == z) {
                return;
            }
            if (z) {
                FullScreenImageFragment.this.onResume();
            } else {
                FullScreenImageFragment.this.onStop();
            }
            FullScreenImageFragment.this.isOpen = z;
        }
    };
    private CarPlayMusicManager.CarPlayMusicListener carPlayMusicListener = new CarPlayMusicManager.CarPlayMusicListener() { // from class: com.yftech.libgallery.ui.FullScreenImageFragment.3
        @Override // com.yftech.libmusic.utils.CarPlayMusicManager.CarPlayMusicListener
        public void onCarplayConnectChanged(int i) {
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
            LogUtil.d("onResourceStateChanged:isDisplaying=" + z);
            if (FullScreenImageFragment.this.getActivity() instanceof BaseMvpActivity) {
                BaseMvpActivity baseMvpActivity = (BaseMvpActivity) FullScreenImageFragment.this.getActivity();
                LogUtil.d("onResourceStateChanged:isPageDisplaying=" + baseMvpActivity.isDisplaying);
                if (baseMvpActivity.isDisplaying) {
                    if (z) {
                        FullScreenImageFragment.this.onStop();
                    } else {
                        FullScreenImageFragment.this.onResume();
                    }
                }
            }
        }
    };
    private BroadcastReceiver receiver = new BroadcastReceiver() { // from class: com.yftech.libgallery.ui.FullScreenImageFragment.4
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("com.yftech.avmservice.SHOW_STATE".equals(intent.getAction())) {
                boolean booleanExtra = intent.getBooleanExtra("isShow", false);
                LogUtil.d("com.yftech.avmservice.SHOW_STATE:isShow=" + booleanExtra);
                if (!booleanExtra) {
                    FullScreenImageFragment.this.onResume();
                } else {
                    FullScreenImageFragment.this.isReturnByUser = false;
                    FullScreenImageFragment.this.onStop();
                }
            }
        }
    };
    private CarBluetoothPhoneCallback callback = new CarBluetoothPhoneCallback() { // from class: com.yftech.libgallery.ui.FullScreenImageFragment.6
        public void onBluetoothPhoneCallStateChanged(List<PhoneCall> list) {
            if (list == null || list.isEmpty()) {
                return;
            }
            PhoneCall phoneCall = list.get(0);
            Message messageObtainMessage = FullScreenImageFragment.this.mHandler.obtainMessage(30);
            messageObtainMessage.arg1 = phoneCall.getCallState();
            FullScreenImageFragment.this.mHandler.sendMessage(messageObtainMessage);
        }
    };
    private CarInputManager.CarInputCallback carInputCallback = new CarInputManager.CarInputCallback() { // from class: com.yftech.libgallery.ui.FullScreenImageFragment.7
        public void onInputKey(CarKey carKey) {
            LogUtil.d("onInputKey:CarKey=" + carKey.toString());
            if ((!(FullScreenImageFragment.this.getActivity() instanceof BaseMvpActivity) || ((BaseMvpActivity) FullScreenImageFragment.this.getActivity()).isDisplaying) && carKey.mAction == 0) {
                Message messageObtainMessage = FullScreenImageFragment.this.mHandler.obtainMessage(20);
                messageObtainMessage.arg1 = carKey.mCode;
                FullScreenImageFragment.this.mHandler.sendMessage(messageObtainMessage);
            }
        }
    };

    static /* synthetic */ int access$410(FullScreenImageFragment fullScreenImageFragment) {
        int i = fullScreenImageFragment.position;
        fullScreenImageFragment.position = i - 1;
        return i;
    }

    private float changeDegreeIn360(float f) {
        while (f < 0.0f) {
            f += 360.0f;
        }
        while (f >= 360.0f) {
            f -= 360.0f;
        }
        return f;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void displayImage() {
        setResetRotate();
        if (this.paths == null || this.paths.size() <= 0) {
            return;
        }
        File file = new File(this.paths.get(this.position % this.paths.size()).getData());
        this.actionBar.setTitleText(file.getName());
        this.dv_image.setController(Fresco.newDraweeControllerBuilder().setUri(UriUtil.getUriForFile(file)).setAutoPlayAnimations(true).setControllerListener(new BaseControllerListener<ImageInfo>() { // from class: com.yftech.libgallery.ui.FullScreenImageFragment.10
            @Override // com.facebook.drawee.controller.BaseControllerListener, com.facebook.drawee.controller.ControllerListener
            public void onFailure(String str, Throwable th) {
                LogUtil.d("play error!!!!!!!!!!!");
                FullScreenImageFragment.this.rotation_index = 0;
                if (FullScreenImageFragment.this.forward > 0) {
                    FullScreenImageFragment.this.position = (FullScreenImageFragment.this.position + 1) % FullScreenImageFragment.this.paths.size();
                } else if (FullScreenImageFragment.this.forward < 0) {
                    if (FullScreenImageFragment.this.position > 0) {
                        FullScreenImageFragment.access$410(FullScreenImageFragment.this);
                    } else {
                        FullScreenImageFragment.this.position += FullScreenImageFragment.this.paths.size() - 1;
                    }
                }
                FullScreenImageFragment.this.displayImage();
            }

            @Override // com.facebook.drawee.controller.BaseControllerListener, com.facebook.drawee.controller.ControllerListener
            public void onFinalImageSet(String str, @Nullable ImageInfo imageInfo, @Nullable Animatable animatable) {
                LogUtil.d("onFinalImageSet:isPlaying=" + FullScreenImageFragment.this.isPlaying);
                if (FullScreenImageFragment.this.isPlaying) {
                    if (FullScreenImageFragment.this.mHandler.hasMessages(99)) {
                        FullScreenImageFragment.this.mHandler.removeMessages(99);
                    }
                    FullScreenImageFragment.this.mHandler.sendEmptyMessageDelayed(99, TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
                }
                if (imageInfo != null) {
                    LogUtil.d("onFinalImageSet:width=" + imageInfo.getWidth() + ", height=" + imageInfo.getHeight());
                    GenericDraweeHierarchy hierarchy = FullScreenImageFragment.this.dv_image.getHierarchy();
                    if (imageInfo.getWidth() > Constants.displayWidth || imageInfo.getHeight() > Constants.displayHeight) {
                        hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
                    } else {
                        hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.CENTER);
                    }
                }
            }

            @Override // com.facebook.drawee.controller.BaseControllerListener, com.facebook.drawee.controller.ControllerListener
            public void onSubmit(String str, Object obj) {
                LogUtil.d("onSubmit");
            }
        }).build());
        if (this.index == 0) {
            SharedPrefsUtil.getInstance(getContext()).save(SharedPrefsUtil.TAG_PHOTO_VIEWED_LAST_FOR_USB_1, this.paths.get(this.position % this.paths.size()).getData());
        } else {
            SharedPrefsUtil.getInstance(getContext()).save(SharedPrefsUtil.TAG_PHOTO_VIEWED_LAST_FOR_USB_2, this.paths.get(this.position % this.paths.size()).getData());
        }
        LogUtil.d("savePhotoLastViewed:path=" + this.paths.get(this.position % this.paths.size()).getData());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean getControlBarVisible() {
        return this.control_bar.getVisibility() == 0 || this.actionBar.getVisibility() == 0;
    }

    public static FullScreenImageFragment newInstance(String str, int i) {
        FullScreenImageFragment fullScreenImageFragment = new FullScreenImageFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM1, str);
        bundle.putInt(ARG_PARAM2, i);
        fullScreenImageFragment.setArguments(bundle);
        return fullScreenImageFragment;
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.yftech.avmservice.SHOW_STATE");
        if (getContext() != null) {
            getContext().registerReceiver(this.receiver, intentFilter);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setControlBarVisible(int i) {
        this.control_bar.setVisibility(i);
        this.actionBar.setVisibility(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setPlaying(boolean z) {
        LogUtil.d("setPlaying:isPlaying=" + z);
        this.iv_slide.setSelected(z);
        this.isPlaying = z;
        if (this.mHandler.hasMessages(99)) {
            this.mHandler.removeMessages(99);
        }
        if (z) {
            this.mHandler.sendEmptyMessageDelayed(99, TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
        }
    }

    private void setResetRotate() {
        if (this.rotation == 0) {
            return;
        }
        this.rotation = 0;
        this.dv_image.setRotation(changeDegreeIn360(this.rotation));
    }

    private void setRotate(boolean z) {
        if (z) {
            this.rotation += 90;
        } else {
            this.rotation -= 90;
        }
        this.dv_image.setRotation(changeDegreeIn360(this.rotation));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.yftech.libmvpmodule.mvp.BaseMvpFragment
    public FullScreenImagePresenter createPresenter() {
        return new FullScreenImagePresenter(getActivity());
    }

    @Override // com.yftech.libgallery.view.IFullScreenImageFragmentView
    public void displayImages(int i, List<MediaFile> list) {
        this.isLoading = false;
        this.position = i;
        this.paths = list;
        if (list == null || list.isEmpty()) {
            ToastUtils.showFailMessage(R.string.picture_browse_empty_error, 1);
            return;
        }
        int photoFileSize = CarMediaDataManager.getInstance().getPhotoFileSize(this.index);
        LogUtil.d("displayImages" + i + ",fileSize:" + photoFileSize);
        if (photoFileSize == 1) {
            this.iv_next.setAlpha(0.6f);
            this.iv_previous.setAlpha(0.6f);
            this.iv_next.setEnabled(false);
            this.iv_previous.setEnabled(false);
        }
        setControlBarVisible(8);
        displayImage();
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseMvpFragment
    protected void initView(View view) {
        this.actionBar = (MyActionBar) view.findViewById(R.id.actionBar);
        this.actionBar.setOnBackPressClickListner(new View.OnClickListener() { // from class: com.yftech.libgallery.ui.FullScreenImageFragment.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                if (FullScreenImageFragment.this.getActivity() != null) {
                    FullScreenImageFragment.this.getActivity().onBackPressed();
                }
                if (FullScreenImageFragment.this.mHandler.hasMessages(99)) {
                    FullScreenImageFragment.this.mHandler.removeMessages(99);
                }
            }
        });
        this.control_bar = (FrameLayout) view.findViewById(R.id.control_bar);
        this.dv_image = (ZoomableDraweeView) view.findViewById(R.id.dv_image);
        this.dv_image.setIsLongpressEnabled(false);
        DoubleTapGestureListener doubleTapGestureListener = new DoubleTapGestureListener(this.dv_image);
        doubleTapGestureListener.setOnSingleClick(new DoubleTapGestureListener.OnSingleClickListener() { // from class: com.yftech.libgallery.ui.FullScreenImageFragment.9
            @Override // com.media.common.ui.widget.zoomable.DoubleTapGestureListener.OnSingleClickListener
            public void onDoubleClickDown() {
                FullScreenImageFragment.this.setPlaying(!FullScreenImageFragment.this.isPlaying);
                AudioSettingUtil.playSystemClickSoundEffect((Context) Objects.requireNonNull(FullScreenImageFragment.this.getContext()));
                new Handler(Looper.myLooper()).postDelayed(new Runnable() { // from class: com.yftech.libgallery.ui.FullScreenImageFragment.9.1
                    @Override // java.lang.Runnable
                    public void run() {
                        AudioSettingUtil.playSystemClickSoundEffect(FullScreenImageFragment.this.getContext());
                    }
                }, 130L);
            }

            @Override // com.media.common.ui.widget.zoomable.DoubleTapGestureListener.OnSingleClickListener
            public void onDoubleTapMoving() {
                LogUtil.d("onDoubleTapMoving");
            }

            @Override // com.media.common.ui.widget.zoomable.DoubleTapGestureListener.OnSingleClickListener
            public void onNext() {
                FullScreenImageFragment.this.iv_next.callOnClick();
            }

            @Override // com.media.common.ui.widget.zoomable.DoubleTapGestureListener.OnSingleClickListener
            public void onPrev() {
                FullScreenImageFragment.this.iv_previous.callOnClick();
            }

            @Override // com.media.common.ui.widget.zoomable.DoubleTapGestureListener.OnSingleClickListener
            public void onSingleClick() {
                LogUtil.d("image on single click");
                AudioSettingUtil.playSystemClickSoundEffect((Context) Objects.requireNonNull(FullScreenImageFragment.this.getContext()));
                if (FullScreenImageFragment.this.getControlBarVisible()) {
                    FullScreenImageFragment.this.setControlBarVisible(8);
                    if (FullScreenImageFragment.this.mHandler.hasMessages(10)) {
                        FullScreenImageFragment.this.mHandler.removeMessages(10);
                        return;
                    }
                    return;
                }
                FullScreenImageFragment.this.setControlBarVisible(0);
                if (FullScreenImageFragment.this.mHandler.hasMessages(10)) {
                    FullScreenImageFragment.this.mHandler.removeMessages(10);
                }
                FullScreenImageFragment.this.mHandler.sendEmptyMessageDelayed(10, TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
            }
        });
        this.dv_image.setTapListener(doubleTapGestureListener);
        view.findViewById(R.id.iv_wall_paper).setVisibility(HostConfig.isWallpaperPresent() ? 0 : 8);
        view.findViewById(R.id.iv_wall_paper).setOnClickListener(this);
        view.findViewById(R.id.iv_rotate).setOnClickListener(this);
        this.iv_previous = (ImageView) view.findViewById(R.id.iv_previous);
        this.iv_previous.setOnClickListener(this);
        this.iv_next = (ImageView) view.findViewById(R.id.iv_next);
        this.iv_next.setOnClickListener(this);
        this.iv_slide = (ImageView) view.findViewById(R.id.iv_slide);
        this.iv_slide.setImageResource(R.drawable.picture_play_state_selector);
        this.iv_slide.setOnClickListener(this);
        this.isLoading = true;
        getPresenter().getImagesByIndex(this.index, this.imagePath);
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseMvpFragment
    protected View onBindView(LayoutInflater layoutInflater, @androidx.annotation.Nullable ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.fragment_full_screen_image, viewGroup, false);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (this.isLoading) {
            ToastUtils.showNormalMessage(getString(R.string.data_loading), 1);
            return;
        }
        if (this.mHandler.hasMessages(10)) {
            this.mHandler.removeMessages(10);
        }
        this.mHandler.sendEmptyMessageDelayed(10, TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
        if (view.getId() == R.id.iv_rotate) {
            setRotate(false);
            LogUtil.d("onClick:rotate");
            setPlaying(false);
            return;
        }
        if (view.getId() == R.id.iv_wall_paper) {
            WallPaperSettingFragment wallPaperSettingFragmentNewInstance = WallPaperSettingFragment.newInstance(this.paths.get(this.position % this.paths.size()).getData());
            MyFragmentManger.getInstance().pushFragment(this.index, wallPaperSettingFragmentNewInstance);
            MyFragmentManger.getInstance().switchContent(wallPaperSettingFragmentNewInstance, MyFragmentManger.getInstance().getPhotoLayoutId());
            onStop();
            return;
        }
        if (view.getId() == R.id.iv_previous) {
            this.rotation_index = 0;
            this.forward = -1;
            if (this.position > 0) {
                this.position--;
            } else {
                this.position += this.paths.size() - 1;
            }
            displayImage();
            setPlaying(false);
            return;
        }
        if (view.getId() == R.id.iv_next) {
            this.rotation_index = 0;
            this.forward = 1;
            this.position = (this.position + 1) % this.paths.size();
            displayImage();
            setPlaying(false);
            return;
        }
        if (view.getId() == R.id.iv_slide) {
            setPlaying(!this.isPlaying);
            return;
        }
        if (view.getId() == R.id.dv_image) {
            LogUtil.d("onClick from image");
            if (getControlBarVisible()) {
                setControlBarVisible(8);
                if (this.mHandler.hasMessages(10)) {
                    this.mHandler.removeMessages(10);
                    return;
                }
                return;
            }
            setControlBarVisible(0);
            if (this.mHandler.hasMessages(10)) {
                this.mHandler.removeMessages(10);
            }
            this.mHandler.sendEmptyMessageDelayed(10, TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
        }
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseMvpFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() != null) {
            this.imagePath = getArguments().getString(ARG_PARAM1);
            this.index = getArguments().getInt(ARG_PARAM2);
        }
        this.isSplitMode = true;
        this.isFullScreen = true;
        CarPlayMusicManager.init(getContext());
        registerReceiver();
        CarScreenManager.getInstance().registerCarScreenCallback(this.carScreenCallback, (Handler) null);
        CarPlayMusicManager.getInstance(getActivity()).registerCarPlayMusicListener(this.carPlayMusicListener);
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseMvpFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d("onDestroy");
        if (this.mHandler.hasMessages(20)) {
            this.mHandler.removeMessages(20);
        }
        if (this.mHandler.hasMessages(30)) {
            this.mHandler.removeMessages(30);
        }
        if (this.mHandler.hasMessages(10)) {
            this.mHandler.removeMessages(10);
        }
        if (this.mHandler.hasMessages(99)) {
            this.mHandler.removeMessages(99);
        }
        if (getContext() != null) {
            getContext().unregisterReceiver(this.receiver);
        }
        CarScreenManager.getInstance().unregisterCarScreenCallback(this.carScreenCallback);
        CarPlayMusicManager.getInstance(getActivity()).unregisterCarPlayMusicListener(this.carPlayMusicListener);
        MyPhotoDataManager.getInstance().setPhotoPlayingBeforeSwitchPage(true);
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseMvpFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        LogUtil.d("onPause");
        if (this.isRegisterCallback) {
            CarBluetoothManager.getInstance().unregisterBluetoothPhoneCallback(this.callback);
            this.isRegisterCallback = false;
        }
        new Handler(Looper.myLooper()).postDelayed(new Runnable() { // from class: com.yftech.libgallery.ui.FullScreenImageFragment.5
            @Override // java.lang.Runnable
            public void run() {
                boolean z = SystemProperties.getBoolean("sys.atc.carplay.app.show_ui", false);
                LogUtil.d("onPause:isCarPlayDisplaying=" + z);
                if (z) {
                    MyPhotoDataManager.getInstance().setPhotoPlayingBeforeSwitchPage(FullScreenImageFragment.this.isPlaying);
                    FullScreenImageFragment.this.setPlaying(false);
                }
            }
        }, 1200L);
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseMvpFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        LogUtil.d("onResume->id=" + getId());
        if (!this.isRegisterCallback) {
            CarBluetoothManager.getInstance().registerBluetoothPhoneCallback(this.callback, (Handler) null);
            this.isRegisterCallback = true;
        }
        LogUtil.d("onResume");
        List phoneCalls = CarBluetoothManager.getInstance().getPhoneCalls();
        if (phoneCalls == null || phoneCalls.isEmpty()) {
            this.isPhoneDisconnected = true;
        } else {
            PhoneCall phoneCall = (PhoneCall) phoneCalls.get(0);
            LogUtil.d("onResume:callState=" + phoneCall.getCallState());
            this.isPhoneDisconnected = phoneCall.getCallState() == 7;
            if (phoneCall.getCallState() != 7) {
                return;
            }
        }
        setPlaying(MyPhotoDataManager.getInstance().isPhotoPlayingBeforeSwitchPage());
    }

    @Override // androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        LogUtil.d("onStop");
        if (this.isReturnByUser) {
            return;
        }
        MyPhotoDataManager.getInstance().setPhotoPlayingBeforeSwitchPage(this.isPlaying);
        setPlaying(false);
    }
}
