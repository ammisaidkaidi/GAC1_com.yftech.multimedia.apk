package android.hardware.camera2.legacy;

import android.hardware.Camera;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.impl.CameraMetadataNative;
import android.hardware.camera2.utils.ParamsUtils;
import android.util.Log;
import com.android.internal.util.Preconditions;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class LegacyFocusStateMapper {
    private static final boolean DEBUG = false;
    private static String TAG = "LegacyFocusStateMapper";
    private final Camera mCamera;
    private int mAfStatePrevious = 0;
    private String mAfModePrevious = null;
    private final Object mLock = new Object();
    private int mAfRun = 0;
    private int mAfState = 0;

    public LegacyFocusStateMapper(Camera camera) {
        this.mCamera = (Camera) Preconditions.checkNotNull(camera, "camera must not be null");
    }

    private static String afStateToString(int i) {
        switch (i) {
            case 0:
                return "INACTIVE";
            case 1:
                return "PASSIVE_SCAN";
            case 2:
                return "PASSIVE_FOCUSED";
            case 3:
                return "ACTIVE_SCAN";
            case 4:
                return "FOCUSED_LOCKED";
            case 5:
                return "NOT_FOCUSED_LOCKED";
            case 6:
                return "PASSIVE_UNFOCUSED";
            default:
                return "UNKNOWN(" + i + ")";
        }
    }

    public void mapResultTriggers(CameraMetadataNative cameraMetadataNative) {
        int i;
        Preconditions.checkNotNull(cameraMetadataNative, "result must not be null");
        synchronized (this.mLock) {
            i = this.mAfState;
        }
        cameraMetadataNative.set(CaptureResult.CONTROL_AF_STATE, Integer.valueOf(i));
        this.mAfStatePrevious = i;
    }

    /* JADX WARN: Code duplicated, block: B:34:0x0089  */
    /* JADX WARN: Code duplicated, block: B:73:0x0100  */
    public void processRequestTriggers(CaptureRequest captureRequest, Camera.Parameters parameters) {
        final int i;
        final int i2;
        Preconditions.checkNotNull(captureRequest, "captureRequest must not be null");
        int i3 = 0;
        int iIntValue = ((Integer) ParamsUtils.getOrDefault(captureRequest, CaptureRequest.CONTROL_AF_TRIGGER, 0)).intValue();
        final String focusMode = parameters.getFocusMode();
        if (!Objects.equals(this.mAfModePrevious, focusMode)) {
            synchronized (this.mLock) {
                this.mAfRun++;
                this.mAfState = 0;
            }
            this.mCamera.cancelAutoFocus();
        }
        this.mAfModePrevious = focusMode;
        synchronized (this.mLock) {
            i = this.mAfRun;
        }
        Camera.AutoFocusMoveCallback autoFocusMoveCallback = new Camera.AutoFocusMoveCallback() { // from class: android.hardware.camera2.legacy.LegacyFocusStateMapper.1
            /* JADX WARN: Code duplicated, block: B:24:0x0059  */
            @Override // android.hardware.Camera.AutoFocusMoveCallback
            public void onAutoFocusMoving(boolean z, Camera camera) {
                synchronized (LegacyFocusStateMapper.this.mLock) {
                    if (i != LegacyFocusStateMapper.this.mAfRun) {
                        Log.d(LegacyFocusStateMapper.TAG, "onAutoFocusMoving - ignoring move callbacks from old af run" + i);
                        return;
                    }
                    int i4 = z ? 1 : 2;
                    switch (focusMode) {
                        case "continuous-picture":
                        case "continuous-video":
                            break;
                        default:
                            Log.w(LegacyFocusStateMapper.TAG, "onAutoFocus - got unexpected onAutoFocus in mode " + focusMode);
                            break;
                    }
                    LegacyFocusStateMapper.this.mAfState = i4;
                }
            }
        };
        switch (focusMode) {
            case "auto":
            case "macro":
            case "continuous-picture":
            case "continuous-video":
                this.mCamera.setAutoFocusMoveCallback(autoFocusMoveCallback);
                break;
        }
        switch (iIntValue) {
            case 0:
                return;
            case 1:
                switch (focusMode) {
                    case "auto":
                    case "macro":
                        i3 = 3;
                        break;
                    case "continuous-picture":
                    case "continuous-video":
                        i3 = 1;
                        break;
                }
                synchronized (this.mLock) {
                    i2 = this.mAfRun + 1;
                    this.mAfRun = i2;
                    this.mAfState = i3;
                    break;
                }
                if (i3 == 0) {
                    return;
                }
                this.mCamera.autoFocus(new Camera.AutoFocusCallback() { // from class: android.hardware.camera2.legacy.LegacyFocusStateMapper.2
                    /* JADX WARN: Code duplicated, block: B:33:0x007d  */
                    @Override // android.hardware.Camera.AutoFocusCallback
                    public void onAutoFocus(boolean z, Camera camera) {
                        synchronized (LegacyFocusStateMapper.this.mLock) {
                            int i4 = LegacyFocusStateMapper.this.mAfRun;
                            if (i4 != i2) {
                                Log.d(LegacyFocusStateMapper.TAG, String.format("onAutoFocus - ignoring AF callback (old run %d, new run %d)", Integer.valueOf(i2), Integer.valueOf(i4)));
                                return;
                            }
                            int i5 = z ? 4 : 5;
                            switch (focusMode) {
                                case "auto":
                                case "continuous-picture":
                                case "continuous-video":
                                case "macro":
                                    break;
                                default:
                                    Log.w(LegacyFocusStateMapper.TAG, "onAutoFocus - got unexpected onAutoFocus in mode " + focusMode);
                                    break;
                            }
                            LegacyFocusStateMapper.this.mAfState = i5;
                        }
                    }
                });
                return;
            case 2:
                synchronized (this.mLock) {
                    synchronized (this.mLock) {
                        this.mAfRun++;
                        this.mAfState = 0;
                        break;
                    }
                    this.mCamera.cancelAutoFocus();
                }
                return;
            default:
                Log.w(TAG, "processRequestTriggers - ignoring unknown control.afTrigger = " + iIntValue);
                return;
        }
    }
}
