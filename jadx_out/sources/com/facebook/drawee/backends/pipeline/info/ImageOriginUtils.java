package com.facebook.drawee.backends.pipeline.info;

import android.bluetooth.BluetoothHidDevice;
import android.location.LocationManager;
import com.facebook.imagepipeline.producers.BitmapMemoryCacheGetProducer;
import com.facebook.imagepipeline.producers.BitmapMemoryCacheProducer;
import com.facebook.imagepipeline.producers.DataFetchProducer;
import com.facebook.imagepipeline.producers.DiskCacheReadProducer;
import com.facebook.imagepipeline.producers.EncodedMemoryCacheProducer;
import com.facebook.imagepipeline.producers.LocalAssetFetchProducer;
import com.facebook.imagepipeline.producers.LocalContentUriFetchProducer;
import com.facebook.imagepipeline.producers.LocalContentUriThumbnailFetchProducer;
import com.facebook.imagepipeline.producers.LocalFileFetchProducer;
import com.facebook.imagepipeline.producers.LocalResourceFetchProducer;
import com.facebook.imagepipeline.producers.LocalVideoThumbnailProducer;
import com.facebook.imagepipeline.producers.NetworkFetchProducer;
import com.facebook.imagepipeline.producers.PartialDiskCacheProducer;
import com.facebook.imagepipeline.producers.PostprocessedBitmapMemoryCacheProducer;
import com.facebook.imagepipeline.producers.QualifiedResourceFetchProducer;
import com.facebook.infer.annotation.Nullsafe;
import com.yftech.vehicle.internal.project.Ihu12DashBoard;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.STRICT)
public class ImageOriginUtils {
    private ImageOriginUtils() {
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:50:0x00b2  */
    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
    public static int mapProducerNameToImageOrigin(String str) {
        byte b;
        switch (str.hashCode()) {
            case -1917159454:
                if (!str.equals(QualifiedResourceFetchProducer.PRODUCER_NAME)) {
                    b = -1;
                } else {
                    b = BluetoothHidDevice.ERROR_RSP_UNKNOWN;
                }
                break;
            case -1914072202:
                if (!str.equals(BitmapMemoryCacheGetProducer.PRODUCER_NAME)) {
                    b = -1;
                } else {
                    b = 0;
                }
                break;
            case -1683996557:
                if (!str.equals(LocalResourceFetchProducer.PRODUCER_NAME)) {
                    b = -1;
                } else {
                    b = 12;
                }
                break;
            case -1579985851:
                if (!str.equals(LocalFileFetchProducer.PRODUCER_NAME)) {
                    b = -1;
                } else {
                    b = Ihu12DashBoard.INFO_TYPE_NAVI_GUIDE_NEXT_PATH_CALCULATION;
                }
                break;
            case -1307634203:
                if (!str.equals(EncodedMemoryCacheProducer.PRODUCER_NAME)) {
                    b = -1;
                } else {
                    b = 3;
                }
                break;
            case -1224383234:
                if (!str.equals(NetworkFetchProducer.PRODUCER_NAME)) {
                    b = -1;
                } else {
                    b = 6;
                }
                break;
            case 473552259:
                if (!str.equals(LocalVideoThumbnailProducer.PRODUCER_NAME)) {
                    b = -1;
                } else {
                    b = 13;
                }
                break;
            case 656304759:
                if (!str.equals(DiskCacheReadProducer.PRODUCER_NAME)) {
                    b = -1;
                } else {
                    b = 4;
                }
                break;
            case 957714404:
                if (!str.equals(BitmapMemoryCacheProducer.PRODUCER_NAME)) {
                    b = -1;
                } else {
                    b = 1;
                }
                break;
            case 1019542023:
                if (!str.equals(LocalAssetFetchProducer.PRODUCER_NAME)) {
                    b = -1;
                } else {
                    b = 8;
                }
                break;
            case 1023071510:
                if (!str.equals(PostprocessedBitmapMemoryCacheProducer.PRODUCER_NAME)) {
                    b = -1;
                } else {
                    b = 2;
                }
                break;
            case 1721672898:
                if (!str.equals(DataFetchProducer.PRODUCER_NAME)) {
                    b = -1;
                } else {
                    b = 7;
                }
                break;
            case 1793127518:
                if (!str.equals(LocalContentUriThumbnailFetchProducer.PRODUCER_NAME)) {
                    b = -1;
                } else {
                    b = 10;
                }
                break;
            case 2109593398:
                if (!str.equals(PartialDiskCacheProducer.PRODUCER_NAME)) {
                    b = -1;
                } else {
                    b = 5;
                }
                break;
            case 2113652014:
                if (!str.equals(LocalContentUriFetchProducer.PRODUCER_NAME)) {
                    b = -1;
                } else {
                    b = 9;
                }
                break;
            default:
                b = -1;
                break;
        }
        switch (b) {
            case 0:
            case 1:
            case 2:
                return 5;
            case 3:
                return 4;
            case 4:
            case 5:
                return 3;
            case 6:
                return 2;
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
                return 7;
            default:
                return 1;
        }
    }

    public static String toString(int i) {
        switch (i) {
            case 2:
                return LocationManager.NETWORK_PROVIDER;
            case 3:
                return "disk";
            case 4:
                return "memory_encoded";
            case 5:
                return "memory_bitmap";
            case 6:
                return "memory_bitmap_shortcut";
            case 7:
                return "local";
            default:
                return "unknown";
        }
    }
}
