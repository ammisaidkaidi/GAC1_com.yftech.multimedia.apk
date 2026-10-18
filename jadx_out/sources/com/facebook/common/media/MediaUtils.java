package com.facebook.common.media;

import com.android.internal.widget.MessagingMessage;
import com.facebook.common.internal.ImmutableMap;
import com.facebook.infer.annotation.Nullsafe;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.LOCAL)
public class MediaUtils {
    public static final Map<String, String> ADDITIONAL_ALLOWED_MIME_TYPES = ImmutableMap.of("mkv", "video/x-matroska", "glb", "model/gltf-binary");

    @Nullable
    private static String extractExtension(String str) {
        int iLastIndexOf = str.lastIndexOf(46);
        if (iLastIndexOf < 0 || iLastIndexOf == str.length() - 1) {
            return null;
        }
        return str.substring(iLastIndexOf + 1);
    }

    @Nullable
    public static String extractMime(String str) {
        String strExtractExtension = extractExtension(str);
        if (strExtractExtension == null) {
            return null;
        }
        String lowerCase = strExtractExtension.toLowerCase(Locale.US);
        String mimeTypeFromExtension = MimeTypeMapWrapper.getMimeTypeFromExtension(lowerCase);
        return mimeTypeFromExtension == null ? ADDITIONAL_ALLOWED_MIME_TYPES.get(lowerCase) : mimeTypeFromExtension;
    }

    public static boolean isNonNativeSupportedMimeType(String str) {
        return ADDITIONAL_ALLOWED_MIME_TYPES.containsValue(str);
    }

    public static boolean isPhoto(@Nullable String str) {
        return str != null && str.startsWith(MessagingMessage.IMAGE_MIME_TYPE_PREFIX);
    }

    public static boolean isThreeD(@Nullable String str) {
        return str != null && str.equals("model/gltf-binary");
    }

    public static boolean isVideo(@Nullable String str) {
        return str != null && str.startsWith("video/");
    }
}
