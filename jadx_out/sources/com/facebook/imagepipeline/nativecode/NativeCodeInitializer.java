package com.facebook.imagepipeline.nativecode;

import android.content.Context;
import com.facebook.common.internal.DoNotStrip;
import com.facebook.infer.annotation.Nullsafe;
import com.facebook.soloader.SoLoader;
import java.io.IOException;

/* JADX INFO: loaded from: classes3.dex */
@DoNotStrip
@Nullsafe(Nullsafe.Mode.LOCAL)
public class NativeCodeInitializer {
    @DoNotStrip
    public static void init(Context context) throws IOException {
        SoLoader.init(context, 0);
    }
}
