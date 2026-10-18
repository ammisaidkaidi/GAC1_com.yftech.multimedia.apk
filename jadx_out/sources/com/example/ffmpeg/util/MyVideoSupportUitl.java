package com.example.ffmpeg.util;

import android.media.MediaCodecList;
import android.media.MediaFormat;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes3.dex */
public class MyVideoSupportUitl {
    private static Map<String, String> codecMap = new HashMap();

    static {
        codecMap.put("h264", MediaFormat.MIMETYPE_VIDEO_AVC);
    }

    public static String findVideoCodecName(String str) {
        return codecMap.containsKey(str) ? codecMap.get(str) : "";
    }

    public static boolean isSupportCodec(String str) {
        int codecCount = MediaCodecList.getCodecCount();
        boolean z = false;
        for (int i = 0; i < codecCount; i++) {
            for (String str2 : MediaCodecList.getCodecInfoAt(i).getSupportedTypes()) {
                if (str2.equals(findVideoCodecName(str))) {
                    z = true;
                    break;
                }
            }
            if (z) {
                break;
            }
        }
        return z;
    }
}
