package com.facebook.drawee.backends.pipeline.info.internal;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.Supplier;
import com.facebook.common.time.MonotonicClock;
import com.facebook.drawee.backends.pipeline.info.ImagePerfNotifier;
import com.facebook.drawee.backends.pipeline.info.ImagePerfState;
import com.facebook.fresco.ui.common.BaseControllerListener2;
import com.facebook.fresco.ui.common.ControllerListener2;
import com.facebook.fresco.ui.common.DimensionsInfo;
import com.facebook.fresco.ui.common.OnDrawControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.infer.annotation.Nullsafe;
import javax.annotation.Nullable;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.LOCAL)
public class ImagePerfControllerListener2 extends BaseControllerListener2<ImageInfo> implements OnDrawControllerListener<ImageInfo> {
    private static final String TAG = "ImagePerfControllerListener2";
    private static final int WHAT_STATUS = 1;
    private static final int WHAT_VISIBILITY = 2;
    private final Supplier<Boolean> mAsyncLogging;
    private final MonotonicClock mClock;

    @Nullable
    private Handler mHandler;
    private final ImagePerfNotifier mImagePerfNotifier;
    private final ImagePerfState mImagePerfState;
    private final Supplier<Boolean> mUseNewState;

    static class LogHandler extends Handler {
        private final ImagePerfNotifier mNotifier;

        public LogHandler(@NonNull Looper looper, @NonNull ImagePerfNotifier imagePerfNotifier) {
            super(looper);
            this.mNotifier = imagePerfNotifier;
        }

        @Override // android.os.Handler
        public void handleMessage(@NonNull Message message) {
            ImagePerfState imagePerfState = (ImagePerfState) Preconditions.checkNotNull(message.obj);
            switch (message.what) {
                case 1:
                    this.mNotifier.notifyStatusUpdated(imagePerfState, message.arg1);
                    break;
                case 2:
                    this.mNotifier.notifyListenersOfVisibilityStateUpdate(imagePerfState, message.arg1);
                    break;
            }
        }
    }

    public ImagePerfControllerListener2(MonotonicClock monotonicClock, ImagePerfState imagePerfState, ImagePerfNotifier imagePerfNotifier, Supplier<Boolean> supplier, Supplier<Boolean> supplier2) {
        this.mClock = monotonicClock;
        this.mImagePerfState = imagePerfState;
        this.mImagePerfNotifier = imagePerfNotifier;
        this.mAsyncLogging = supplier;
        this.mUseNewState = supplier2;
    }

    private synchronized void initHandler() {
        if (this.mHandler != null) {
            return;
        }
        HandlerThread handlerThread = new HandlerThread("ImagePerfControllerListener2Thread");
        handlerThread.start();
        this.mHandler = new LogHandler((Looper) Preconditions.checkNotNull(handlerThread.getLooper()), this.mImagePerfNotifier);
    }

    private ImagePerfState obtainState() {
        return this.mUseNewState.get().booleanValue() ? new ImagePerfState() : this.mImagePerfState;
    }

    @VisibleForTesting
    private void reportViewInvisible(ImagePerfState imagePerfState, long j) {
        imagePerfState.setVisible(false);
        imagePerfState.setInvisibilityEventTimeMs(j);
        updateVisibility(imagePerfState, 2);
    }

    private boolean shouldDispatchAsync() {
        boolean zBooleanValue = this.mAsyncLogging.get().booleanValue();
        if (zBooleanValue && this.mHandler == null) {
            initHandler();
        }
        return zBooleanValue;
    }

    private void updateStatus(ImagePerfState imagePerfState, int i) {
        if (!shouldDispatchAsync()) {
            this.mImagePerfNotifier.notifyStatusUpdated(imagePerfState, i);
            return;
        }
        Message messageObtainMessage = ((Handler) Preconditions.checkNotNull(this.mHandler)).obtainMessage();
        messageObtainMessage.what = 1;
        messageObtainMessage.arg1 = i;
        messageObtainMessage.obj = imagePerfState;
        this.mHandler.sendMessage(messageObtainMessage);
    }

    private void updateVisibility(ImagePerfState imagePerfState, int i) {
        if (!shouldDispatchAsync()) {
            this.mImagePerfNotifier.notifyListenersOfVisibilityStateUpdate(imagePerfState, i);
            return;
        }
        Message messageObtainMessage = ((Handler) Preconditions.checkNotNull(this.mHandler)).obtainMessage();
        messageObtainMessage.what = 2;
        messageObtainMessage.arg1 = i;
        messageObtainMessage.obj = imagePerfState;
        this.mHandler.sendMessage(messageObtainMessage);
    }

    @Override // com.facebook.fresco.ui.common.BaseControllerListener2, com.facebook.fresco.ui.common.ControllerListener2
    public void onFailure(String str, Throwable th, @Nullable ControllerListener2.Extras extras) {
        long jNow = this.mClock.now();
        ImagePerfState imagePerfStateObtainState = obtainState();
        imagePerfStateObtainState.setExtraData(extras);
        imagePerfStateObtainState.setControllerFailureTimeMs(jNow);
        imagePerfStateObtainState.setControllerId(str);
        imagePerfStateObtainState.setErrorThrowable(th);
        updateStatus(imagePerfStateObtainState, 5);
        reportViewInvisible(imagePerfStateObtainState, jNow);
    }

    @Override // com.facebook.fresco.ui.common.BaseControllerListener2, com.facebook.fresco.ui.common.ControllerListener2
    public void onFinalImageSet(String str, @Nullable ImageInfo imageInfo, @Nullable ControllerListener2.Extras extras) {
        long jNow = this.mClock.now();
        ImagePerfState imagePerfStateObtainState = obtainState();
        imagePerfStateObtainState.setExtraData(extras);
        imagePerfStateObtainState.setControllerFinalImageSetTimeMs(jNow);
        imagePerfStateObtainState.setImageRequestEndTimeMs(jNow);
        imagePerfStateObtainState.setControllerId(str);
        imagePerfStateObtainState.setImageInfo(imageInfo);
        updateStatus(imagePerfStateObtainState, 3);
    }

    @Override // com.facebook.fresco.ui.common.OnDrawControllerListener
    public void onImageDrawn(String str, ImageInfo imageInfo, DimensionsInfo dimensionsInfo) {
        ImagePerfState imagePerfStateObtainState = obtainState();
        imagePerfStateObtainState.setControllerId(str);
        imagePerfStateObtainState.setImageDrawTimeMs(this.mClock.now());
        imagePerfStateObtainState.setDimensionsInfo(dimensionsInfo);
        updateStatus(imagePerfStateObtainState, 6);
    }

    @Override // com.facebook.fresco.ui.common.BaseControllerListener2, com.facebook.fresco.ui.common.ControllerListener2
    public void onIntermediateImageSet(String str, @Nullable ImageInfo imageInfo) {
        long jNow = this.mClock.now();
        ImagePerfState imagePerfStateObtainState = obtainState();
        imagePerfStateObtainState.setControllerIntermediateImageSetTimeMs(jNow);
        imagePerfStateObtainState.setControllerId(str);
        imagePerfStateObtainState.setImageInfo(imageInfo);
        updateStatus(imagePerfStateObtainState, 2);
    }

    @Override // com.facebook.fresco.ui.common.BaseControllerListener2, com.facebook.fresco.ui.common.ControllerListener2
    public void onRelease(String str, @Nullable ControllerListener2.Extras extras) {
        long jNow = this.mClock.now();
        ImagePerfState imagePerfStateObtainState = obtainState();
        imagePerfStateObtainState.setExtraData(extras);
        imagePerfStateObtainState.setControllerId(str);
        int imageLoadStatus = imagePerfStateObtainState.getImageLoadStatus();
        if (imageLoadStatus != 3 && imageLoadStatus != 5 && imageLoadStatus != 6) {
            imagePerfStateObtainState.setControllerCancelTimeMs(jNow);
            updateStatus(imagePerfStateObtainState, 4);
        }
        reportViewInvisible(imagePerfStateObtainState, jNow);
    }

    @Override // com.facebook.fresco.ui.common.BaseControllerListener2, com.facebook.fresco.ui.common.ControllerListener2
    public void onSubmit(String str, @Nullable Object obj, @Nullable ControllerListener2.Extras extras) {
        long jNow = this.mClock.now();
        ImagePerfState imagePerfStateObtainState = obtainState();
        imagePerfStateObtainState.resetPointsTimestamps();
        imagePerfStateObtainState.setControllerSubmitTimeMs(jNow);
        imagePerfStateObtainState.setControllerId(str);
        imagePerfStateObtainState.setCallerContext(obj);
        imagePerfStateObtainState.setExtraData(extras);
        updateStatus(imagePerfStateObtainState, 0);
        reportViewVisible(imagePerfStateObtainState, jNow);
    }

    @VisibleForTesting
    public void reportViewVisible(ImagePerfState imagePerfState, long j) {
        imagePerfState.setVisible(true);
        imagePerfState.setVisibilityEventTimeMs(j);
        updateVisibility(imagePerfState, 1);
    }

    public void resetState() {
        obtainState().reset();
    }
}
