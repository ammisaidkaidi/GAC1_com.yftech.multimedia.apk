package com.yftech.car.widget.media.music.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import com.yftech.car.widget.media.entity.MediaMetadataReturn;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/* JADX INFO: loaded from: classes3.dex */
public class MusicUtils {
    private static final String TAG = "MusicUtils";

    public static Bitmap compress(int i, int i2, Bitmap bitmap) {
        if (i == 0 || i2 == 0) {
            i = 261;
            i2 = 261;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        try {
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);
        int i3 = options.outWidth;
        int i4 = options.outHeight;
        double d = (((double) i3) * 1.0d) / ((double) i);
        options.inSampleSize = (d > 1.0d || (((double) i4) * 1.0d) / ((double) i2) > 1.0d) ? (int) Math.ceil(Math.max(d, (((double) i4) * 1.0d) / ((double) i2))) : 1;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);
    }

    public static Bitmap getEmbeddedPictureAlbum(String str, int i, int i2) {
        int i3;
        int i4 = 261;
        if (i == 0 || i2 == 0) {
            Log.e(TAG, "getEmbeddedPictureAlbum: width == 0 || height == 0");
            i3 = 261;
        } else {
            i4 = i + 30;
            i3 = i2 + 30;
        }
        Log.d(TAG, "getEmbeddedPictureAlbum: width==" + i4 + ",height==" + i3 + ",path==" + str);
        MediaMetadataReturn embeddedPictureAlbum = getEmbeddedPictureAlbum(str);
        if (embeddedPictureAlbum.mBitmap == null) {
            return null;
        }
        return compress(i4, i3, embeddedPictureAlbum.mBitmap);
    }

    public static MediaMetadataReturn getEmbeddedPictureAlbum(String str) {
        Exception e;
        byte[] embeddedPicture;
        Bitmap bitmapCopy;
        MediaMetadataReturn mediaMetadataReturn = new MediaMetadataReturn();
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        try {
            mediaMetadataRetriever.setDataSource(str);
            embeddedPicture = mediaMetadataRetriever.getEmbeddedPicture();
            try {
                String strExtractMetadata = mediaMetadataRetriever.extractMetadata(9);
                if (strExtractMetadata != null) {
                    mediaMetadataReturn.mDuration = Integer.parseInt(strExtractMetadata);
                }
            } catch (Exception e2) {
                e = e2;
                e.printStackTrace();
            }
        } catch (Exception e3) {
            e = e3;
            embeddedPicture = null;
        }
        if (embeddedPicture == null) {
            Log.d(TAG, "get cur EmbeddedPicture  is null");
        } else {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inSampleSize = 1;
            BitmapFactory.decodeByteArray(embeddedPicture, 0, embeddedPicture.length, options);
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            mediaMetadataReturn.mBitmap = BitmapFactory.decodeByteArray(embeddedPicture, 0, embeddedPicture.length, options);
            if (mediaMetadataReturn.mBitmap != null && mediaMetadataReturn.mBitmap.getConfig() == null && (bitmapCopy = mediaMetadataReturn.mBitmap.copy(Bitmap.Config.RGB_565, false)) != null && bitmapCopy != mediaMetadataReturn.mBitmap) {
                mediaMetadataReturn.mBitmap.recycle();
                mediaMetadataReturn.mBitmap = bitmapCopy;
            }
        }
        try {
            mediaMetadataRetriever.release();
        } catch (Exception e4) {
            e4.printStackTrace();
        }
        return mediaMetadataReturn;
    }
}
