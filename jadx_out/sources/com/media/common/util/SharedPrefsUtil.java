package com.media.common.util;

import android.content.Context;
import android.content.SharedPreferences;

/* JADX INFO: loaded from: classes3.dex */
public class SharedPrefsUtil {
    public static final String TAG_MUSIC_SOURCE_PLAYING = "TAG_MUSIC_SOURCE_PLAYING";
    public static final String TAG_PHOTO_VIEWED_LAST_FOR_USB_1 = "TAG_PHOTO_VIEWED_LAST_FOR_USB_1";
    public static final String TAG_PHOTO_VIEWED_LAST_FOR_USB_2 = "TAG_PHOTO_VIEWED_LAST_FOR_USB_2";
    public static final String TAG_SOUND_EFFECT_DTS_TYPE = "TAG_SOUND_EFFECT_DTS_TYPE";
    public static final String TAG_SOUND_EFFECT_EQ_TYPE = "TAG_SOUND_EFFECT_EQ_TYPE";
    public static final String TAG_SOUND_EFFECT_HIGH_VALUE = "TAG_SOUND_EFFECT_HIGH_VALUE";
    public static final String TAG_SOUND_EFFECT_LOW_VALUE = "TAG_SOUND_EFFECT_LOW_VALUE";
    public static final String TAG_SOUND_EFFECT_MIDDLE_VALUE = "TAG_SOUND_EFFECT_MIDDLE_VALUE";
    public static final String TAG_VIDEO_ON_BACK_PRESS_BY_USER_BEFORE_SWITCH_PAGE = "TAG_VIDEO_ON_BACK_PRESS_BY_USER_BEFORE_SWITCH_PAGE";
    public static final String TAG_VIDEO_VECHICLE_OVERFLOW_HOLDING = "TAG_VIDEO_VECHICLE_OVERFLOW_HOLDING";
    public static final String TAG_VIDEO_VIEWED_LAST_FOR_USB_1 = "TAG_VIDEO_VIEWED_LAST_FOR_USB_1";
    public static final String TAG_VIDEO_VIEWED_LAST_FOR_USB_2 = "TAG_VIDEO_VIEWED_LAST_FOR_USB_2";
    public static final String TAG_VOLUME_BEFORE_MUTE = "TAG_VOLUME_BEFORE_MUTE";
    private static SharedPrefsUtil instance = new SharedPrefsUtil();
    private static SharedPreferences sp;

    private SharedPrefsUtil() {
    }

    public static SharedPrefsUtil getInstance(Context context) {
        if (sp == null) {
            sp = context.getSharedPreferences("first_time", 0);
        }
        return instance;
    }

    public boolean getBoolean(String str, Boolean bool) {
        return sp.getBoolean(str, bool.booleanValue());
    }

    public double getFloat(String str, Float f) {
        return sp.getFloat(str, f.floatValue());
    }

    public int getInt(String str, Integer num) {
        return sp.getInt(str, num.intValue());
    }

    public String getString(String str, String str2) {
        return sp.getString(str, str2);
    }

    public void remove(String str) {
        sp.edit().remove(str).apply();
    }

    public void save(String str, Object obj) {
        SharedPreferences.Editor editorEdit = sp.edit();
        if (obj instanceof String) {
            editorEdit.putString(str, (String) obj);
        } else if (obj instanceof Integer) {
            editorEdit.putInt(str, ((Integer) obj).intValue());
        } else if (obj instanceof Boolean) {
            editorEdit.putBoolean(str, ((Boolean) obj).booleanValue());
        } else if (obj instanceof Float) {
            editorEdit.putFloat(str, ((Float) obj).floatValue());
        }
        editorEdit.apply();
    }
}
