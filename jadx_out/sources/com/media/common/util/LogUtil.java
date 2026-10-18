package com.media.common.util;

import android.net.wifi.WifiEnterpriseConfig;
import android.provider.SettingsStringUtil;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.content.NativeLibraryHelper;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

/* JADX INFO: loaded from: classes3.dex */
public class LogUtil {
    public static final String LOG_PATH = "/mnt/udisk";
    public static CustomLogger customLogger = null;
    public static String customTagPrefix = "YFMediaPlayer";
    public static boolean isSaveLog = false;
    private static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE);
    public static boolean allowD = true;
    public static boolean allowE = true;
    public static boolean allowI = true;
    public static boolean allowV = true;
    public static boolean allowW = true;
    public static boolean allowWtf = true;
    private static final ThreadLocal<ReusableFormatter> thread_local_formatter = new ThreadLocal<ReusableFormatter>() { // from class: com.media.common.util.LogUtil.1
        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.lang.ThreadLocal
        public ReusableFormatter initialValue() {
            return new ReusableFormatter();
        }
    };

    public interface CustomLogger {
        void d(String str, String str2);

        void d(String str, String str2, Throwable th);

        void e(String str, String str2);

        void e(String str, String str2, Throwable th);

        void i(String str, String str2);

        void i(String str, String str2, Throwable th);

        void v(String str, String str2);

        void v(String str, String str2, Throwable th);

        void w(String str, String str2);

        void w(String str, String str2, Throwable th);

        void w(String str, Throwable th);

        void wtf(String str, String str2);

        void wtf(String str, String str2, Throwable th);

        void wtf(String str, Throwable th);
    }

    private static class ReusableFormatter {
        private StringBuilder builder = new StringBuilder();
        private Formatter formatter = new Formatter(this.builder);

        public String format(String str, Object... objArr) {
            this.formatter.format(str, objArr);
            String string = this.builder.toString();
            this.builder.setLength(0);
            return string;
        }
    }

    private LogUtil() {
    }

    public static void createDipPath(String str) {
        String strSubstring = str.substring(0, str.lastIndexOf("/"));
        File file = new File(str);
        File file2 = new File(strSubstring);
        if (file.exists()) {
            return;
        }
        file2.mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void d(String str) {
        if (allowD) {
            String strGenerateTag = generateTag(getCallerStackTraceElement());
            if (customLogger != null) {
                customLogger.d(strGenerateTag, str);
            } else {
                Log.d(strGenerateTag, str);
            }
        }
    }

    public static void d(String str, Throwable th) {
        if (allowD) {
            String strGenerateTag = generateTag(getCallerStackTraceElement());
            if (customLogger != null) {
                customLogger.d(strGenerateTag, str, th);
            } else {
                Log.d(strGenerateTag, str, th);
            }
        }
    }

    public static void e(String str) throws Throwable {
        if (allowE) {
            String strGenerateTag = generateTag(getCallerStackTraceElement());
            if (customLogger != null) {
                customLogger.e(strGenerateTag, str);
            } else {
                Log.e(strGenerateTag, str);
            }
            if (isSaveLog) {
                point(LOG_PATH, strGenerateTag, str);
            }
        }
    }

    public static void e(String str, Throwable th) throws Throwable {
        if (allowE) {
            String strGenerateTag = generateTag(getCallerStackTraceElement());
            if (customLogger != null) {
                customLogger.e(strGenerateTag, str, th);
            } else {
                Log.e(strGenerateTag, str, th);
            }
            if (isSaveLog) {
                point(LOG_PATH, strGenerateTag, th.getMessage());
            }
        }
    }

    public static void e(Throwable th) throws Throwable {
        if (allowE) {
            String strGenerateTag = generateTag(getCallerStackTraceElement());
            if (customLogger != null) {
                customLogger.e(strGenerateTag, "error", th);
            } else {
                Log.e(strGenerateTag, th.getMessage(), th);
            }
            if (isSaveLog) {
                point(LOG_PATH, strGenerateTag, th.getMessage());
            }
        }
    }

    public static String format(String str, Object... objArr) {
        return thread_local_formatter.get().format(str, objArr);
    }

    private static String generateTag(StackTraceElement stackTraceElement) {
        String className = stackTraceElement.getClassName();
        String str = String.format(Locale.getDefault(), "%s.%s(Line:%d)", className.substring(className.lastIndexOf(".") + 1), stackTraceElement.getMethodName(), Integer.valueOf(stackTraceElement.getLineNumber()));
        if (TextUtils.isEmpty(customTagPrefix)) {
            return str;
        }
        return customTagPrefix + SettingsStringUtil.DELIMITER + str;
    }

    private static StackTraceElement getCallerStackTraceElement() {
        return Thread.currentThread().getStackTrace()[4];
    }

    public static void i(String str) {
        if (allowI) {
            String strGenerateTag = generateTag(getCallerStackTraceElement());
            if (customLogger != null) {
                customLogger.i(strGenerateTag, str);
            } else {
                Log.i(strGenerateTag, str);
            }
        }
    }

    public static void i(String str, Throwable th) {
        if (allowI) {
            String strGenerateTag = generateTag(getCallerStackTraceElement());
            if (customLogger != null) {
                customLogger.i(strGenerateTag, str, th);
            } else {
                Log.i(strGenerateTag, str, th);
            }
        }
    }

    private static boolean isSDAva() {
        return new File(LOG_PATH).exists();
    }

    /* JADX WARN: Code duplicated, block: B:38:0x0097 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    public static void point(String str, String str2, String str3) throws Throwable {
        BufferedWriter bufferedWriter;
        Throwable th;
        Exception e;
        if (isSDAva()) {
            long jCurrentTimeMillis = System.currentTimeMillis();
            String str4 = formatter.format(new Date());
            String str5 = str + "/brucelogs/naviservice-log-" + str4 + NativeLibraryHelper.CLEAR_ABI_OVERRIDE + jCurrentTimeMillis + ".log";
            File file = new File(str5);
            if (!file.exists()) {
                createDipPath(str5);
            }
            try {
                try {
                    bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
                    try {
                        try {
                            bufferedWriter.write(str4 + WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + str2 + WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + str3 + "\r\n");
                            bufferedWriter.close();
                        } catch (Exception e2) {
                            e = e2;
                            e.printStackTrace();
                            if (bufferedWriter == null) {
                            } else {
                                bufferedWriter.close();
                            }
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        if (bufferedWriter != null) {
                            try {
                                bufferedWriter.close();
                            } catch (IOException e3) {
                                e3.printStackTrace();
                            }
                        }
                        throw th;
                    }
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
            } catch (Exception e5) {
                bufferedWriter = null;
                e = e5;
            } catch (Throwable th3) {
                bufferedWriter = null;
                th = th3;
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
                throw th;
            }
        }
    }

    public static void v(String str) {
        if (allowV) {
            String strGenerateTag = generateTag(getCallerStackTraceElement());
            if (customLogger != null) {
                customLogger.v(strGenerateTag, str);
            } else {
                Log.v(strGenerateTag, str);
            }
        }
    }

    public static void v(String str, Throwable th) {
        if (allowV) {
            String strGenerateTag = generateTag(getCallerStackTraceElement());
            if (customLogger != null) {
                customLogger.v(strGenerateTag, str, th);
            } else {
                Log.v(strGenerateTag, str, th);
            }
        }
    }

    public static void w(String str) {
        if (allowW) {
            String strGenerateTag = generateTag(getCallerStackTraceElement());
            if (customLogger != null) {
                customLogger.w(strGenerateTag, str);
            } else {
                Log.w(strGenerateTag, str);
            }
        }
    }

    public static void w(String str, Throwable th) {
        if (allowW) {
            String strGenerateTag = generateTag(getCallerStackTraceElement());
            if (customLogger != null) {
                customLogger.w(strGenerateTag, str, th);
            } else {
                Log.w(strGenerateTag, str, th);
            }
        }
    }

    public static void w(Throwable th) {
        if (allowW) {
            String strGenerateTag = generateTag(getCallerStackTraceElement());
            if (customLogger != null) {
                customLogger.w(strGenerateTag, th);
            } else {
                Log.w(strGenerateTag, th);
            }
        }
    }

    public static void wtf(String str) {
        if (allowWtf) {
            String strGenerateTag = generateTag(getCallerStackTraceElement());
            if (customLogger != null) {
                customLogger.wtf(strGenerateTag, str);
            } else {
                Log.wtf(strGenerateTag, str);
            }
        }
    }

    public static void wtf(String str, Throwable th) {
        if (allowWtf) {
            String strGenerateTag = generateTag(getCallerStackTraceElement());
            if (customLogger != null) {
                customLogger.wtf(strGenerateTag, str, th);
            } else {
                Log.wtf(strGenerateTag, str, th);
            }
        }
    }

    public static void wtf(Throwable th) {
        if (allowWtf) {
            String strGenerateTag = generateTag(getCallerStackTraceElement());
            if (customLogger != null) {
                customLogger.wtf(strGenerateTag, th);
            } else {
                Log.wtf(strGenerateTag, th);
            }
        }
    }
}
