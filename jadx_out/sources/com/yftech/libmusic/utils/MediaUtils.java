package com.yftech.libmusic.utils;

import android.provider.SettingsStringUtil;
import com.media.common.util.LogUtil;
import com.yftech.libmusic.bean.LyricObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes4.dex */
public class MediaUtils {
    public static final Charset CHARSET_GBK = Charset.forName("GBK");
    private static final String FILE_CERTIFICATE_PATH = "/data/media/0/certificate";

    public static boolean isCarplayCertificate() {
        boolean zExists = new File(FILE_CERTIFICATE_PATH).exists();
        LogUtil.i("isCarplayCertificate carplayCertificate:" + zExists);
        return zExists;
    }

    public static boolean isGBK(String str) {
        int iLastIndexOf;
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(str));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8));
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    return false;
                }
                int length = line.length();
                if (length > 3 && (iLastIndexOf = line.lastIndexOf("]")) > 0 && iLastIndexOf < length) {
                    int i = 0;
                    while (iLastIndexOf < length) {
                        if (line.charAt(iLastIndexOf) == 65533) {
                            i++;
                        }
                        iLastIndexOf++;
                    }
                    if (i > length / 3) {
                        fileInputStream.close();
                        bufferedReader.close();
                        return true;
                    }
                }
            }
        } catch (Exception unused) {
            return false;
        }
    }

    public static List<LyricObject> read(String str) {
        LyricObject lyricObject;
        TreeMap treeMap = new TreeMap();
        LyricObject lyricObject2 = null;
        try {
            File file = new File(str);
            if (!file.isFile()) {
                return null;
            }
            Charset charset = isGBK(str) ? CHARSET_GBK : StandardCharsets.UTF_8;
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, charset));
            Pattern patternCompile = Pattern.compile("\\d{2}");
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                String strReplace = line.replace("[", "").replace("]", "@");
                String[] strArrSplit = strReplace.split("@");
                if (strReplace.endsWith("@")) {
                    for (String str2 : strArrSplit) {
                        String[] strArrSplit2 = str2.replace(SettingsStringUtil.DELIMITER, ".").replace(".", "@").split("@");
                        Matcher matcher = patternCompile.matcher(strArrSplit2[0]);
                        if (strArrSplit2.length == 3 && matcher.matches()) {
                            int i = (((Integer.parseInt(strArrSplit2[0]) * 60) + Integer.parseInt(strArrSplit2[1])) * 1000) + (Integer.parseInt(strArrSplit2[2]) * 10);
                            LyricObject lyricObject3 = new LyricObject();
                            lyricObject3.begintime = i;
                            lyricObject3.lrc = "";
                            treeMap.put(Integer.valueOf(i), lyricObject3);
                        }
                    }
                } else {
                    String str3 = strArrSplit[strArrSplit.length - 1];
                    for (int i2 = 0; i2 < strArrSplit.length - 1; i2++) {
                        String[] strArrSplit3 = strArrSplit[i2].replace(SettingsStringUtil.DELIMITER, ".").replace(".", "@").split("@");
                        Matcher matcher2 = patternCompile.matcher(strArrSplit3[0]);
                        if (strArrSplit3.length == 3 && matcher2.matches()) {
                            int i3 = (((Integer.parseInt(strArrSplit3[0]) * 60) + Integer.parseInt(strArrSplit3[1])) * 1000) + (Integer.parseInt(strArrSplit3[2]) * 10);
                            LyricObject lyricObject4 = new LyricObject();
                            lyricObject4.begintime = i3;
                            lyricObject4.lrc = str3;
                            treeMap.put(Integer.valueOf(i3), lyricObject4);
                        }
                    }
                }
            }
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList arrayList = new ArrayList();
        Iterator it = treeMap.keySet().iterator();
        while (it.hasNext() && (lyricObject = (LyricObject) treeMap.get(it.next())) != null) {
            if (lyricObject2 != null) {
                lyricObject2.timeline = lyricObject.begintime - lyricObject2.begintime;
                arrayList.add(lyricObject2);
            }
            if (!it.hasNext()) {
                arrayList.add(lyricObject);
            }
            lyricObject2 = lyricObject;
        }
        return arrayList;
    }
}
