package com.media.common.util;

import android.text.Html;
import android.text.Spanned;
import androidx.annotation.NonNull;
import java.util.Locale;

/* JADX INFO: loaded from: classes3.dex */
public class StringUtil {
    public static Spanned getHighLightByKeyword(@NonNull String str, @NonNull String str2) {
        StringBuilder sb = new StringBuilder();
        int iIndexOf = str.indexOf(str2);
        if (iIndexOf != -1) {
            sb.append(str.substring(0, iIndexOf));
            sb.append(String.format(Locale.getDefault(), "<font color='#ff8800'>%s</font>", str.substring(iIndexOf, str2.length() + iIndexOf)));
            if (str2.length() + iIndexOf < str.length()) {
                sb.append(str.substring(iIndexOf + str2.length()));
            }
        }
        return Html.fromHtml(sb.toString());
    }
}
