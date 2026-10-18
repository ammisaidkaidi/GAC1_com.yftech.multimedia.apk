package com.yftech.car.widget.media.music.lyrics;

import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes3.dex */
public class LrcParser {
    private static final String ALBUM_KEY = "al";
    private static final String ARTIST_KEY = "ar";
    private static final String BY_KEY = "by";
    private static final long NO_TS = Long.MIN_VALUE;
    private static final String OFFSET_KEY = "offset";
    private static final String TAG = "LrcParser";
    private static final String TITLE_KEY = "ti";
    private static int mLinearMax;

    LrcParser() {
    }

    private static void parseCommonLine(Lyric.Builder builder, String str) {
        int length = str.length();
        ArrayList arrayList = new ArrayList();
        int i = 0;
        do {
            int i2 = i + 1;
            int iIndexOf = str.indexOf(93, i2);
            if (iIndexOf == -1) {
                break;
            }
            long timeStamp = parseTimeStamp(str.substring(i2, iIndexOf));
            if (timeStamp == Long.MIN_VALUE) {
                break;
            }
            arrayList.add(Long.valueOf(timeStamp));
            i = iIndexOf + 1;
        } while (i != length);
        if (arrayList.isEmpty()) {
            return;
        }
        String strSubstring = str.substring(i);
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            builder.insertLine(((Long) it.next()).longValue(), strSubstring);
        }
    }

    private static void parseCommonLine(Lyric.Builder builder, String str, boolean z) {
        int iIndexOf = str.indexOf("[") + 1;
        int iIndexOf2 = str.indexOf(",");
        if (iIndexOf2 <= 0 || iIndexOf < 0 || iIndexOf >= iIndexOf2) {
            return;
        }
        builder.insertLine(Long.valueOf(str.substring(iIndexOf, iIndexOf2)).longValue(), str.substring(str.indexOf("]") + 1));
    }

    public static void parseLine(Lyric.Builder builder, String str) {
        if (str != null) {
            str = str.trim();
            if (str.length() != 0 && str.charAt(0) == '[') {
                char cCharAt = str.charAt(1);
                if ((cCharAt < 'a' || cCharAt > 'z') && (cCharAt < 'A' || cCharAt > 'Z')) {
                    parseCommonLine(builder, str);
                    return;
                } else {
                    parseMetaData(builder, str);
                    return;
                }
            }
        }
        if (mLinearMax != 1) {
            builder.addTranslate(str);
        }
    }

    public static void parseLine(Lyric.Builder builder, String str, boolean z) {
        if (str != null) {
            String strTrim = str.trim();
            if (strTrim.length() == 0 || strTrim.charAt(0) != '[') {
                return;
            }
            char cCharAt = strTrim.charAt(1);
            if ((cCharAt < 'a' || cCharAt > 'z') && (cCharAt < 'A' || cCharAt > 'Z')) {
                parseCommonLine(builder, strTrim, z);
            } else {
                parseMetaData(builder, strTrim);
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:20:0x002d A[Catch: IOException -> 0x0031, TryCatch #1 {IOException -> 0x0031, blocks: (B:3:0x0005, B:9:0x001a, B:16:0x0024, B:21:0x0030, B:20:0x002d, B:19:0x0029, B:5:0x0010, B:7:0x0016, B:14:0x0021), top: B:28:0x0005, inners: #2, #3 }] */
    /* JADX WARN: Code duplicated, block: B:30:0x0024 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    static Lyric parseLyric(InputStream inputStream, String str) {
        Lyric.Builder builder = new Lyric.Builder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, str));
            Throwable th = null;
            while (true) {
                try {
                    String line = bufferedReader.readLine();
                    if (line == null) {
                        break;
                    }
                    parseLine(builder, line);
                } catch (Throwable th2) {
                    if (th != null) {
                        try {
                            bufferedReader.close();
                        } catch (Throwable th3) {
                            th.addSuppressed(th3);
                        }
                    } else {
                        bufferedReader.close();
                    }
                    throw th2;
                }
                if (th != null) {
                    bufferedReader.close();
                } else {
                    bufferedReader.close();
                }
                throw th2;
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.build();
    }

    /* JADX WARN: Code duplicated, block: B:36:0x0081  */
    private static void parseMetaData(Lyric.Builder builder, String str) {
        int i;
        int iIndexOf;
        byte b = 2;
        int iIndexOf2 = str.indexOf(58, 2);
        if (iIndexOf2 == -1 || (iIndexOf = str.indexOf(93, (i = iIndexOf2 + 1))) == -1) {
            return;
        }
        String strTrim = str.substring(1, iIndexOf2).trim();
        String strTrim2 = str.substring(i, iIndexOf).trim();
        if (strTrim.length() == 0 || strTrim2.length() == 0) {
            return;
        }
        String lowerCase = strTrim.toLowerCase();
        int iHashCode = lowerCase.hashCode();
        if (iHashCode != -1019779949) {
            if (iHashCode != 3115) {
                if (iHashCode != 3121) {
                    if (iHashCode != 3159) {
                        if (iHashCode == 3701 && lowerCase.equals(TITLE_KEY)) {
                            b = 4;
                        } else {
                            b = -1;
                        }
                    } else if (lowerCase.equals(BY_KEY)) {
                        b = 3;
                    } else {
                        b = -1;
                    }
                } else if (!lowerCase.equals(ARTIST_KEY)) {
                    b = -1;
                }
            } else if (lowerCase.equals(ALBUM_KEY)) {
                b = 1;
            } else {
                b = -1;
            }
        } else if (lowerCase.equals("offset")) {
            b = 0;
        } else {
            b = -1;
        }
        switch (b) {
            case 0:
                try {
                    builder.setOffset(Long.parseLong(strTrim2));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                break;
            case 1:
                builder.setAlbum(strTrim2);
                break;
            case 2:
                builder.setArtist(strTrim2);
                break;
            case 3:
                builder.setBy(strTrim2);
                break;
            case 4:
                builder.setTitle(strTrim2);
                break;
            default:
                Log.d(TAG, "parseMetaData: ");
                break;
        }
    }

    static Lyric parseText(String str) {
        if (str == null || str.length() == 0) {
            return Lyric.EMPTY;
        }
        Lyric.Builder builder = new Lyric.Builder();
        for (String str2 : str.split("\n")) {
            parseLine(builder, str2);
        }
        return builder.build();
    }

    private static long parseTimeStamp(String str) {
        long j;
        int iIndexOf = str.indexOf(58);
        if (iIndexOf < 0) {
            return Long.MIN_VALUE;
        }
        try {
            long j2 = ((long) (Integer.parseInt(str.substring(0, iIndexOf)) * 60000)) + 0;
            int i = iIndexOf + 1;
            int iIndexOf2 = str.indexOf(46, i);
            if (iIndexOf2 < 0) {
                iIndexOf2 = str.indexOf(58, i);
            }
            try {
                if (iIndexOf2 < 0) {
                    j = j2 + ((long) (Integer.parseInt(str.substring(i)) * 1000));
                } else {
                    j = j2 + ((long) (Integer.parseInt(str.substring(i, iIndexOf2)) * 1000)) + ((long) (Integer.parseInt(str.substring(iIndexOf2 + 1)) * 10));
                }
                return j;
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return Long.MIN_VALUE;
            }
        } catch (NumberFormatException e2) {
            e2.printStackTrace();
            return Long.MIN_VALUE;
        }
    }

    public static void setLinearMax(int i) {
        mLinearMax = i;
    }
}
