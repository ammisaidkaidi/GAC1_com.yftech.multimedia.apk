package android.hardware.camera2.legacy;

import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.impl.CameraMetadataNative;
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.utils.ParamsUtils;
import android.location.Location;
import android.util.Log;
import android.util.Size;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class LegacyResultMapper {
    private static final boolean DEBUG = false;
    private static final String TAG = "LegacyResultMapper";
    private LegacyRequest mCachedRequest = null;
    private CameraMetadataNative mCachedResult = null;

    private static int convertLegacyAfMode(String str) {
        if (str == null) {
            Log.w(TAG, "convertLegacyAfMode - no AF mode, default to OFF");
            return 0;
        }
        switch (str) {
            case "auto":
                return 1;
            case "continuous-picture":
                return 4;
            case "continuous-video":
                return 3;
            case "edof":
                return 5;
            case "macro":
                return 2;
            case "fixed":
                return 0;
            case "infinity":
                return 0;
            default:
                Log.w(TAG, "convertLegacyAfMode - unknown mode " + str + " , ignoring");
                return 0;
        }
    }

    private static int convertLegacyAwbMode(String str) {
        if (str == null) {
            return 1;
        }
        switch (str) {
            case "auto":
                return 1;
            case "incandescent":
                return 2;
            case "fluorescent":
                return 3;
            case "warm-fluorescent":
                return 4;
            case "daylight":
                return 5;
            case "cloudy-daylight":
                return 6;
            case "twilight":
                return 7;
            case "shade":
                return 8;
            default:
                Log.w(TAG, "convertAwbMode - unrecognized WB mode " + str);
                return 1;
        }
    }

    private static CameraMetadataNative convertResultMetadata(LegacyRequest legacyRequest) {
        CameraCharacteristics cameraCharacteristics = legacyRequest.characteristics;
        CaptureRequest captureRequest = legacyRequest.captureRequest;
        Size size = legacyRequest.previewSize;
        Camera.Parameters parameters = legacyRequest.parameters;
        CameraMetadataNative cameraMetadataNative = new CameraMetadataNative();
        Rect rect = (Rect) cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
        ParameterUtils.ZoomData zoomDataConvertScalerCropRegion = ParameterUtils.convertScalerCropRegion(rect, (Rect) captureRequest.get(CaptureRequest.SCALER_CROP_REGION), size, parameters);
        cameraMetadataNative.set(CaptureResult.COLOR_CORRECTION_ABERRATION_MODE, (Integer) captureRequest.get(CaptureRequest.COLOR_CORRECTION_ABERRATION_MODE));
        mapAe(cameraMetadataNative, cameraCharacteristics, captureRequest, rect, zoomDataConvertScalerCropRegion, parameters);
        mapAf(cameraMetadataNative, rect, zoomDataConvertScalerCropRegion, parameters);
        mapAwb(cameraMetadataNative, parameters);
        cameraMetadataNative.set(CaptureResult.CONTROL_CAPTURE_INTENT, Integer.valueOf(LegacyRequestMapper.filterSupportedCaptureIntent(((Integer) ParamsUtils.getOrDefault(captureRequest, CaptureRequest.CONTROL_CAPTURE_INTENT, 1)).intValue())));
        if (((Integer) ParamsUtils.getOrDefault(captureRequest, CaptureRequest.CONTROL_MODE, 1)).intValue() == 2) {
            cameraMetadataNative.set((CaptureResult.Key<int>) CaptureResult.CONTROL_MODE, 2);
        } else {
            cameraMetadataNative.set((CaptureResult.Key<int>) CaptureResult.CONTROL_MODE, 1);
        }
        String sceneMode = parameters.getSceneMode();
        int iConvertSceneModeFromLegacy = LegacyMetadataMapper.convertSceneModeFromLegacy(sceneMode);
        if (iConvertSceneModeFromLegacy != -1) {
            cameraMetadataNative.set(CaptureResult.CONTROL_SCENE_MODE, Integer.valueOf(iConvertSceneModeFromLegacy));
        } else {
            Log.w(TAG, "Unknown scene mode " + sceneMode + " returned by camera HAL, setting to disabled.");
            cameraMetadataNative.set((CaptureResult.Key<int>) CaptureResult.CONTROL_SCENE_MODE, 0);
        }
        String colorEffect = parameters.getColorEffect();
        int iConvertEffectModeFromLegacy = LegacyMetadataMapper.convertEffectModeFromLegacy(colorEffect);
        if (iConvertEffectModeFromLegacy != -1) {
            cameraMetadataNative.set(CaptureResult.CONTROL_EFFECT_MODE, Integer.valueOf(iConvertEffectModeFromLegacy));
        } else {
            Log.w(TAG, "Unknown effect mode " + colorEffect + " returned by camera HAL, setting to off.");
            cameraMetadataNative.set((CaptureResult.Key<int>) CaptureResult.CONTROL_EFFECT_MODE, 0);
        }
        cameraMetadataNative.set(CaptureResult.CONTROL_VIDEO_STABILIZATION_MODE, Integer.valueOf((parameters.isVideoStabilizationSupported() && parameters.getVideoStabilization()) ? 1 : 0));
        if (Camera.Parameters.FOCUS_MODE_INFINITY.equals(parameters.getFocusMode())) {
            cameraMetadataNative.set(CaptureResult.LENS_FOCUS_DISTANCE, Float.valueOf(0.0f));
        }
        cameraMetadataNative.set(CaptureResult.LENS_FOCAL_LENGTH, Float.valueOf(parameters.getFocalLength()));
        cameraMetadataNative.set(CaptureResult.REQUEST_PIPELINE_DEPTH, (Byte) cameraCharacteristics.get(CameraCharacteristics.REQUEST_PIPELINE_MAX_DEPTH));
        mapScaler(cameraMetadataNative, zoomDataConvertScalerCropRegion, parameters);
        cameraMetadataNative.set((CaptureResult.Key<int>) CaptureResult.SENSOR_TEST_PATTERN_MODE, 0);
        cameraMetadataNative.set(CaptureResult.JPEG_GPS_LOCATION, (Location) captureRequest.get(CaptureRequest.JPEG_GPS_LOCATION));
        cameraMetadataNative.set(CaptureResult.JPEG_ORIENTATION, (Integer) captureRequest.get(CaptureRequest.JPEG_ORIENTATION));
        cameraMetadataNative.set(CaptureResult.JPEG_QUALITY, Byte.valueOf((byte) parameters.getJpegQuality()));
        cameraMetadataNative.set(CaptureResult.JPEG_THUMBNAIL_QUALITY, Byte.valueOf((byte) parameters.getJpegThumbnailQuality()));
        Camera.Size jpegThumbnailSize = parameters.getJpegThumbnailSize();
        if (jpegThumbnailSize != null) {
            cameraMetadataNative.set(CaptureResult.JPEG_THUMBNAIL_SIZE, ParameterUtils.convertSize(jpegThumbnailSize));
        } else {
            Log.w(TAG, "Null thumbnail size received from parameters.");
        }
        cameraMetadataNative.set(CaptureResult.NOISE_REDUCTION_MODE, (Integer) captureRequest.get(CaptureRequest.NOISE_REDUCTION_MODE));
        return cameraMetadataNative;
    }

    private static MeteringRectangle[] getMeteringRectangles(Rect rect, ParameterUtils.ZoomData zoomData, List<Camera.Area> list, String str) {
        ArrayList arrayList = new ArrayList();
        if (list != null) {
            Iterator<Camera.Area> it = list.iterator();
            while (it.hasNext()) {
                arrayList.add(ParameterUtils.convertCameraAreaToActiveArrayRectangle(rect, zoomData, it.next()).toMetering());
            }
        }
        return (MeteringRectangle[]) arrayList.toArray(new MeteringRectangle[0]);
    }

    private static void mapAe(CameraMetadataNative cameraMetadataNative, CameraCharacteristics cameraCharacteristics, CaptureRequest captureRequest, Rect rect, ParameterUtils.ZoomData zoomData, Camera.Parameters parameters) {
        cameraMetadataNative.set(CaptureResult.CONTROL_AE_ANTIBANDING_MODE, Integer.valueOf(LegacyMetadataMapper.convertAntiBandingModeOrDefault(parameters.getAntibanding())));
        cameraMetadataNative.set(CaptureResult.CONTROL_AE_EXPOSURE_COMPENSATION, Integer.valueOf(parameters.getExposureCompensation()));
        boolean autoExposureLock = parameters.isAutoExposureLockSupported() ? parameters.getAutoExposureLock() : false;
        cameraMetadataNative.set(CaptureResult.CONTROL_AE_LOCK, Boolean.valueOf(autoExposureLock));
        Boolean bool = (Boolean) captureRequest.get(CaptureRequest.CONTROL_AE_LOCK);
        if (bool != null && bool.booleanValue() != autoExposureLock) {
            Log.w(TAG, "mapAe - android.control.aeLock was requested to " + bool + " but resulted in " + autoExposureLock);
        }
        mapAeAndFlashMode(cameraMetadataNative, cameraCharacteristics, parameters);
        if (parameters.getMaxNumMeteringAreas() > 0) {
            cameraMetadataNative.set(CaptureResult.CONTROL_AE_REGIONS, getMeteringRectangles(rect, zoomData, parameters.getMeteringAreas(), "AE"));
        }
    }

    private static void mapAeAndFlashMode(CameraMetadataNative cameraMetadataNative, CameraCharacteristics cameraCharacteristics, Camera.Parameters parameters) {
        int i = 0;
        Integer num = ((Boolean) cameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)).booleanValue() ? null : 0;
        String flashMode = parameters.getFlashMode();
        int i2 = 1;
        if (flashMode != null) {
            switch (flashMode) {
                case "off":
                    break;
                case "auto":
                    i2 = 2;
                    break;
                case "on":
                    num = 3;
                    i = 1;
                    i2 = 3;
                    break;
                case "red-eye":
                    i2 = 4;
                    break;
                case "torch":
                    num = 3;
                    i = 2;
                    break;
                default:
                    Log.w(TAG, "mapAeAndFlashMode - Ignoring unknown flash mode " + parameters.getFlashMode());
                    break;
            }
        }
        cameraMetadataNative.set(CaptureResult.FLASH_STATE, num);
        cameraMetadataNative.set(CaptureResult.FLASH_MODE, Integer.valueOf(i));
        cameraMetadataNative.set(CaptureResult.CONTROL_AE_MODE, Integer.valueOf(i2));
    }

    private static void mapAf(CameraMetadataNative cameraMetadataNative, Rect rect, ParameterUtils.ZoomData zoomData, Camera.Parameters parameters) {
        cameraMetadataNative.set(CaptureResult.CONTROL_AF_MODE, Integer.valueOf(convertLegacyAfMode(parameters.getFocusMode())));
        if (parameters.getMaxNumFocusAreas() > 0) {
            cameraMetadataNative.set(CaptureResult.CONTROL_AF_REGIONS, getMeteringRectangles(rect, zoomData, parameters.getFocusAreas(), "AF"));
        }
    }

    private static void mapAwb(CameraMetadataNative cameraMetadataNative, Camera.Parameters parameters) {
        cameraMetadataNative.set(CaptureResult.CONTROL_AWB_LOCK, Boolean.valueOf(parameters.isAutoWhiteBalanceLockSupported() ? parameters.getAutoWhiteBalanceLock() : false));
        cameraMetadataNative.set(CaptureResult.CONTROL_AWB_MODE, Integer.valueOf(convertLegacyAwbMode(parameters.getWhiteBalance())));
    }

    private static void mapScaler(CameraMetadataNative cameraMetadataNative, ParameterUtils.ZoomData zoomData, Camera.Parameters parameters) {
        cameraMetadataNative.set(CaptureResult.SCALER_CROP_REGION, zoomData.reportedCrop);
    }

    public CameraMetadataNative cachedConvertResultMetadata(LegacyRequest legacyRequest, long j) {
        CameraMetadataNative cameraMetadataNative;
        if (this.mCachedRequest != null && legacyRequest.parameters.same(this.mCachedRequest.parameters) && legacyRequest.captureRequest.equals((Object) this.mCachedRequest.captureRequest)) {
            cameraMetadataNative = new CameraMetadataNative(this.mCachedResult);
        } else {
            CameraMetadataNative cameraMetadataNativeConvertResultMetadata = convertResultMetadata(legacyRequest);
            this.mCachedRequest = legacyRequest;
            this.mCachedResult = new CameraMetadataNative(cameraMetadataNativeConvertResultMetadata);
            cameraMetadataNative = cameraMetadataNativeConvertResultMetadata;
        }
        cameraMetadataNative.set(CaptureResult.SENSOR_TIMESTAMP, Long.valueOf(j));
        return cameraMetadataNative;
    }
}
