package com.facebook.imagepipeline.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import androidx.annotation.RequiresApi;
import com.facebook.common.internal.Preconditions;

/* JADX INFO: loaded from: classes3.dex */
public abstract class RenderScriptBlurFilter {
    public static final int BLUR_MAX_RADIUS = 25;

    @RequiresApi(17)
    public static void blurBitmap(Bitmap bitmap, Bitmap bitmap2, Context context, int i) throws Throwable {
        RenderScript renderScriptCreate;
        Preconditions.checkNotNull(bitmap);
        Preconditions.checkNotNull(bitmap2);
        Preconditions.checkNotNull(context);
        Preconditions.checkArgument(Boolean.valueOf(i > 0 && i <= 25));
        try {
            renderScriptCreate = RenderScript.create(context);
            try {
                ScriptIntrinsicBlur scriptIntrinsicBlurCreate = ScriptIntrinsicBlur.create(renderScriptCreate, Element.U8_4(renderScriptCreate));
                Allocation allocationCreateFromBitmap = Allocation.createFromBitmap(renderScriptCreate, bitmap2);
                Allocation allocationCreateFromBitmap2 = Allocation.createFromBitmap(renderScriptCreate, bitmap);
                scriptIntrinsicBlurCreate.setRadius(i);
                scriptIntrinsicBlurCreate.setInput(allocationCreateFromBitmap);
                scriptIntrinsicBlurCreate.forEach(allocationCreateFromBitmap2);
                allocationCreateFromBitmap2.copyTo(bitmap);
                scriptIntrinsicBlurCreate.destroy();
                allocationCreateFromBitmap.destroy();
                allocationCreateFromBitmap2.destroy();
                if (renderScriptCreate != null) {
                    renderScriptCreate.destroy();
                }
            } catch (Throwable th) {
                th = th;
                if (renderScriptCreate != null) {
                    renderScriptCreate.destroy();
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            renderScriptCreate = null;
        }
    }

    public static boolean canUseRenderScript() {
        return Build.VERSION.SDK_INT >= 17;
    }
}
