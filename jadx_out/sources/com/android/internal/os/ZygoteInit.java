package com.android.internal.os;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.icu.impl.CacheValue;
import android.icu.text.DecimalFormatSymbols;
import android.icu.util.ULocale;
import android.opengl.EGL14;
import android.os.Build;
import android.os.Environment;
import android.os.IInstalld;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.ServiceSpecificException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.UserHandle;
import android.os.ZygoteProcess;
import android.os.storage.StorageManager;
import android.provider.SettingsStringUtil;
import android.security.keystore.AndroidKeyStoreProvider;
import android.service.notification.ZenModeConfig;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.system.StructCapUserData;
import android.system.StructCapUserHeader;
import android.text.Hyphenator;
import android.util.EventLog;
import android.util.Log;
import android.util.TimingsTraceLog;
import android.webkit.WebViewFactory;
import android.widget.TextView;
import com.android.internal.R;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.telephony.PhoneConstants;
import com.android.internal.util.Preconditions;
import dalvik.system.DexFile;
import dalvik.system.VMRuntime;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.Provider;
import java.security.Security;
import libcore.io.IoUtils;

/* JADX INFO: loaded from: classes3.dex */
public class ZygoteInit {
    private static final String ABI_LIST_ARG = "--abi-list=";
    private static final int LOG_BOOT_PROGRESS_PRELOAD_END = 3030;
    private static final int LOG_BOOT_PROGRESS_PRELOAD_START = 3020;
    private static final String PRELOADED_CLASSES = "/system/etc/preloaded-classes";
    private static final String PRELOADED_SECOND_STAGE_CLASSES = "/system/etc/preloaded-classes-secondlist";
    private static final int PRELOAD_GC_THRESHOLD = 50000;
    public static final boolean PRELOAD_RESOURCES = true;
    private static final String PROPERTY_DISABLE_OPENGL_PRELOADING = "ro.zygote.disable_gl_preload";
    private static final String PROPERTY_GFX_DRIVER = "ro.gfx.driver.0";
    private static final int ROOT_GID = 0;
    private static final int ROOT_UID = 0;
    private static final String SOCKET_NAME_ARG = "--socket-name=";
    private static final String TAG = "Zygote";
    private static final int UNPRIVILEGED_GID = 9999;
    private static final int UNPRIVILEGED_UID = 9999;
    private static Resources mResources = null;
    private static boolean sMtprofDisable = false;
    private static boolean sPreloadComplete;

    private ZygoteInit() {
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v1 */
    /* JADX WARN: Type inference failed for: r0v14 */
    /* JADX WARN: Type inference failed for: r0v15 */
    /* JADX WARN: Type inference failed for: r0v16 */
    /* JADX WARN: Type inference failed for: r0v17 */
    /* JADX WARN: Type inference failed for: r0v18 */
    /* JADX WARN: Type inference failed for: r0v19 */
    /* JADX WARN: Type inference failed for: r0v3 */
    /* JADX WARN: Type inference failed for: r0v4, types: [java.io.FileOutputStream] */
    /* JADX WARN: Type inference failed for: r0v6 */
    /* JADX WARN: Type inference failed for: r0v9 */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:29:0x0043 -> B:37:0x004a). Please report as a decompilation issue!!! */
    private static void addBootEvent(String str) throws Throwable {
        if (sMtprofDisable) {
            return;
        }
        ?? r0 = 0;
        r0 = 0;
        FileOutputStream fileOutputStream = null;
        FileOutputStream fileOutputStream2 = null;
        try {
            try {
                try {
                    FileOutputStream fileOutputStream3 = new FileOutputStream("/proc/bootprof");
                    try {
                        fileOutputStream3.write(str.getBytes());
                        fileOutputStream3.flush();
                        fileOutputStream3.close();
                    } catch (FileNotFoundException e) {
                        e = e;
                        fileOutputStream = fileOutputStream3;
                        Log.e("BOOTPROF", "Failure open /proc/bootprof, not found!", e);
                        r0 = fileOutputStream;
                        if (fileOutputStream != null) {
                            fileOutputStream.close();
                            r0 = fileOutputStream;
                        }
                    } catch (IOException e2) {
                        e = e2;
                        fileOutputStream2 = fileOutputStream3;
                        Log.e("BOOTPROF", "Failure open /proc/bootprof entry", e);
                        r0 = fileOutputStream2;
                        if (fileOutputStream2 != null) {
                            fileOutputStream2.close();
                            r0 = fileOutputStream2;
                        }
                    } catch (Throwable th) {
                        th = th;
                        r0 = fileOutputStream3;
                        if (r0 != 0) {
                            try {
                                r0.close();
                            } catch (IOException e3) {
                                Log.e("BOOTPROF", "Failure close /proc/bootprof entry", e3);
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (FileNotFoundException e4) {
                e = e4;
            } catch (IOException e5) {
                e = e5;
            }
        } catch (IOException e6) {
            Log.e("BOOTPROF", "Failure close /proc/bootprof entry", e6);
            r0 = "BOOTPROF";
        }
    }

    private static void beginIcuCachePinning() {
        Log.i(TAG, "Installing ICU cache reference pinning...");
        CacheValue.setStrength(CacheValue.Strength.STRONG);
        Log.i(TAG, "Preloading ICU data...");
        for (ULocale uLocale : new ULocale[]{ULocale.ROOT, ULocale.US, ULocale.getDefault()}) {
            new DecimalFormatSymbols(uLocale);
        }
    }

    static final Runnable childZygoteInit(int i, String[] strArr, ClassLoader classLoader) {
        RuntimeInit.Arguments arguments = new RuntimeInit.Arguments(strArr);
        return RuntimeInit.findStaticMain(arguments.startClass, arguments.startArgs, classLoader);
    }

    static ClassLoader createPathClassLoader(String str, int i) {
        String property = System.getProperty("java.library.path");
        return ClassLoaderFactory.createClassLoader(str, property, property, ClassLoader.getSystemClassLoader(), i, true, null);
    }

    private static String encodeSystemServerClassPath(String str, String str2) {
        if (str == null || str.isEmpty()) {
            return str2;
        }
        return str + SettingsStringUtil.DELIMITER + str2;
    }

    private static void endIcuCachePinning() {
        CacheValue.setStrength(CacheValue.Strength.SOFT);
        Log.i(TAG, "Uninstalled ICU cache reference pinning...");
    }

    private static Runnable forkSystemServer(String str, String str2, ZygoteServer zygoteServer) {
        long jPosixCapabilitiesAsBits = posixCapabilitiesAsBits(OsConstants.CAP_IPC_LOCK, OsConstants.CAP_KILL, OsConstants.CAP_NET_ADMIN, OsConstants.CAP_NET_BIND_SERVICE, OsConstants.CAP_NET_BROADCAST, OsConstants.CAP_NET_RAW, OsConstants.CAP_SYS_MODULE, OsConstants.CAP_SYS_NICE, OsConstants.CAP_SYS_PTRACE, OsConstants.CAP_SYS_TIME, OsConstants.CAP_SYS_TTY_CONFIG, OsConstants.CAP_WAKE_ALARM, OsConstants.CAP_BLOCK_SUSPEND);
        try {
            StructCapUserData[] structCapUserDataArrCapget = Os.capget(new StructCapUserHeader(OsConstants._LINUX_CAPABILITY_VERSION_3, 0));
            long j = jPosixCapabilitiesAsBits & (((long) structCapUserDataArrCapget[0].effective) | (((long) structCapUserDataArrCapget[1].effective) << 32));
            try {
                ZygoteConnection.Arguments arguments = new ZygoteConnection.Arguments(new String[]{"--setuid=1000", "--setgid=1000", "--setgroups=1001,1002,1003,1004,1005,1006,1007,1008,1009,1010,1014,1018,1021,1023,1024,1032,1065,3001,3002,3003,3006,3007,3009,3010", "--capabilities=" + j + "," + j, "--nice-name=system_server", "--runtime-args", "--target-sdk-version=10000", "com.android.server.SystemServer"});
                ZygoteConnection.applyDebuggerSystemProperty(arguments);
                ZygoteConnection.applyInvokeWithSystemProperty(arguments);
                if (SystemProperties.getBoolean("dalvik.vm.profilesystemserver", false)) {
                    arguments.runtimeFlags |= 16384;
                }
                if (Zygote.forkSystemServer(arguments.uid, arguments.gid, arguments.gids, arguments.runtimeFlags, null, arguments.permittedCapabilities, arguments.effectiveCapabilities) != 0) {
                    return null;
                }
                zygoteServer.closeServerSocket();
                return handleSystemServerProcess(arguments);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            }
        } catch (ErrnoException e2) {
            throw new RuntimeException("Failed to capget()", e2);
        }
    }

    static void gcAndFinalize() {
        VMRuntime runtime = VMRuntime.getRuntime();
        System.gc();
        runtime.runFinalizationSync();
        System.gc();
    }

    private static String getSystemServerClassLoaderContext(String str) {
        if (str == null) {
            return "PCL[]";
        }
        return "PCL[" + str + "]";
    }

    private static Runnable handleSystemServerProcess(ZygoteConnection.Arguments arguments) {
        String[] strArr;
        Os.umask(OsConstants.S_IRWXG | OsConstants.S_IRWXO);
        if (arguments.niceName != null) {
            Process.setArgV0(arguments.niceName);
        }
        String str = Os.getenv("SYSTEMSERVERCLASSPATH");
        if (str != null) {
            performSystemServerDexOpt(str);
            if (SystemProperties.getBoolean("dalvik.vm.profilesystemserver", false) && (Build.IS_USERDEBUG || Build.IS_ENG)) {
                try {
                    prepareSystemServerProfile(str);
                } catch (Exception e) {
                    Log.wtf(TAG, "Failed to set up system server profile", e);
                }
            }
        }
        if (arguments.invokeWith == null) {
            ClassLoader classLoaderCreatePathClassLoader = null;
            if (str != null) {
                classLoaderCreatePathClassLoader = createPathClassLoader(str, arguments.targetSdkVersion);
                Thread.currentThread().setContextClassLoader(classLoaderCreatePathClassLoader);
            }
            return zygoteInit(arguments.targetSdkVersion, arguments.remainingArgs, classLoaderCreatePathClassLoader);
        }
        String[] strArr2 = arguments.remainingArgs;
        if (str != null) {
            String[] strArr3 = new String[strArr2.length + 2];
            strArr3[0] = "-cp";
            strArr3[1] = str;
            System.arraycopy(strArr2, 0, strArr3, 2, strArr2.length);
            strArr = strArr3;
        } else {
            strArr = strArr2;
        }
        WrapperInit.execApplication(arguments.invokeWith, arguments.niceName, arguments.targetSdkVersion, VMRuntime.getCurrentInstructionSet(), null, strArr);
        throw new IllegalStateException("Unexpected return from WrapperInit.execApplication");
    }

    private static boolean hasSecondZygote(String str) {
        return !SystemProperties.get("ro.product.cpu.abilist").equals(str);
    }

    static boolean isPreloadComplete() {
        return sPreloadComplete;
    }

    public static void lazyPreload() {
        Preconditions.checkState(!sPreloadComplete);
        Log.i(TAG, "Lazily preloading resources.");
        preload(new TimingsTraceLog("ZygoteInitTiming_lazy", 16384L));
    }

    public static void main(String[] strArr) {
        ZygoteServer zygoteServer = new ZygoteServer();
        try {
            try {
                Os.setpgid(0, 0);
                try {
                    String strSubstring = null;
                    if (!"1".equals(SystemProperties.get("sys.boot_completed"))) {
                        MetricsLogger.histogram(null, "boot_zygote_init", (int) SystemClock.elapsedRealtime());
                    }
                    TimingsTraceLog timingsTraceLog = new TimingsTraceLog(Process.is64Bit() ? "Zygote64Timing" : "Zygote32Timing", 16384L);
                    timingsTraceLog.traceBegin("ZygoteInit");
                    RuntimeInit.enableDdms();
                    boolean z = false;
                    boolean z2 = false;
                    String strSubstring2 = Process.ZYGOTE_SOCKET;
                    for (int i = 1; i < strArr.length; i++) {
                        if ("start-system-server".equals(strArr[i])) {
                            z2 = true;
                        } else if ("--enable-lazy-preload".equals(strArr[i])) {
                            z = true;
                        } else if (strArr[i].startsWith(ABI_LIST_ARG)) {
                            strSubstring = strArr[i].substring(ABI_LIST_ARG.length());
                        } else {
                            if (!strArr[i].startsWith(SOCKET_NAME_ARG)) {
                                throw new RuntimeException("Unknown command line argument: " + strArr[i]);
                            }
                            strSubstring2 = strArr[i].substring(SOCKET_NAME_ARG.length());
                        }
                    }
                    if (strSubstring == null) {
                        throw new RuntimeException("No ABI list supplied.");
                    }
                    zygoteServer.registerServerSocketFromEnv(strSubstring2);
                    if (z) {
                        Zygote.resetNicePriority();
                    } else {
                        timingsTraceLog.traceBegin("ZygotePreload");
                        EventLog.writeEvent(LOG_BOOT_PROGRESS_PRELOAD_START, SystemClock.uptimeMillis());
                        preload(timingsTraceLog);
                        new Thread(new Runnable() { // from class: com.android.internal.os.ZygoteInit.3
                            @Override // java.lang.Runnable
                            public void run() {
                                ZygoteInit.preloadSecondStageClasses();
                            }
                        }).start();
                        EventLog.writeEvent(LOG_BOOT_PROGRESS_PRELOAD_END, SystemClock.uptimeMillis());
                        timingsTraceLog.traceEnd();
                    }
                    timingsTraceLog.traceBegin("PostZygoteInitGC");
                    gcAndFinalize();
                    timingsTraceLog.traceEnd();
                    timingsTraceLog.traceEnd();
                    Trace.setTracingEnabled(false, 0);
                    Zygote.nativeSecurityInit();
                    Zygote.nativeUnmountStorageOnInit();
                    if (z2) {
                        addBootEvent("Zygote: start fork SystemServer");
                        Runnable runnableForkSystemServer = forkSystemServer(strSubstring, strSubstring2, zygoteServer);
                        if (runnableForkSystemServer != null) {
                            runnableForkSystemServer.run();
                            zygoteServer.closeServerSocket();
                            return;
                        }
                    }
                    Log.i(TAG, "Accepting command socket connections");
                    Runnable runnableRunSelectLoop = zygoteServer.runSelectLoop(strSubstring);
                    zygoteServer.closeServerSocket();
                    if (runnableRunSelectLoop != null) {
                        runnableRunSelectLoop.run();
                    }
                } catch (Throwable th) {
                    Log.e(TAG, "System zygote died with exception", th);
                    throw th;
                }
            } catch (ErrnoException e) {
                throw new RuntimeException("Failed to setpgid(0,0)", e);
            }
        } catch (Throwable th2) {
            zygoteServer.closeServerSocket();
            throw th2;
        }
    }

    private static native void nativePreloadAppProcessHALs();

    private static final native void nativeZygoteInit();

    /* JADX WARN: Unreachable blocks removed: 2, instructions: 5 */
    private static void performSystemServerDexOpt(String str) {
        int dexOptNeeded;
        String str2;
        String str3;
        String[] strArrSplit = str.split(SettingsStringUtil.DELIMITER);
        IInstalld iInstalldAsInterface = IInstalld.Stub.asInterface(ServiceManager.getService("installd"));
        String strVmInstructionSet = VMRuntime.getRuntime().vmInstructionSet();
        int length = strArrSplit.length;
        String strEncodeSystemServerClassPath = "";
        int i = 0;
        while (i < length) {
            String str4 = strArrSplit[i];
            String str5 = SystemProperties.get("dalvik.vm.systemservercompilerfilter", "speed");
            try {
                dexOptNeeded = DexFile.getDexOptNeeded(str4, strVmInstructionSet, str5, null, false, false);
            } catch (FileNotFoundException unused) {
                i = i;
                length = length;
                Log.w(TAG, "Missing classpath element for system server: " + str4);
                strEncodeSystemServerClassPath = strEncodeSystemServerClassPath;
            } catch (IOException e) {
                Log.w(TAG, "Error checking classpath element for system server: " + str4, e);
                dexOptNeeded = 0;
            }
            if (dexOptNeeded != 0) {
                String str6 = strEncodeSystemServerClassPath;
                try {
                    iInstalldAsInterface.dexopt(str4, 1000, PhoneConstants.APN_TYPE_ALL, strVmInstructionSet, dexOptNeeded, null, 0, str5, StorageManager.UUID_PRIVATE_INTERNAL, getSystemServerClassLoaderContext(strEncodeSystemServerClassPath), null, false, 0, null, null, "server-dexopt");
                    str2 = str4;
                } catch (RemoteException | ServiceSpecificException e2) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Failed compiling classpath element for system server: ");
                    str2 = str4;
                    sb.append(str2);
                    Log.w(TAG, sb.toString(), e2);
                }
                str3 = str6;
            } else {
                str2 = str4;
                str3 = strEncodeSystemServerClassPath;
            }
            strEncodeSystemServerClassPath = encodeSystemServerClassPath(str3, str2);
            i++;
            length = length;
        }
    }

    private static long posixCapabilitiesAsBits(int... iArr) {
        long j = 0;
        for (int i : iArr) {
            if (i < 0 || i > OsConstants.CAP_LAST_CAP) {
                throw new IllegalArgumentException(String.valueOf(i));
            }
            j |= 1 << i;
        }
        return j;
    }

    static void preload(TimingsTraceLog timingsTraceLog) {
        addBootEvent("Zygote:Preload Start...");
        Log.d(TAG, "begin preload");
        Thread thread = new Thread(new Runnable() { // from class: com.android.internal.os.ZygoteInit.1
            @Override // java.lang.Runnable
            public void run() throws Throwable {
                ZygoteInit.preloadResources();
            }
        });
        Thread thread2 = new Thread(new Runnable() { // from class: com.android.internal.os.ZygoteInit.2
            @Override // java.lang.Runnable
            public void run() throws Throwable {
                ZygoteInit.preloadClasses();
            }
        });
        thread.start();
        thread2.start();
        beginIcuCachePinning();
        nativePreloadAppProcessHALs();
        preloadOpenGL();
        preloadSharedLibraries();
        preloadTextResources();
        WebViewFactory.prepareWebViewInZygote();
        endIcuCachePinning();
        warmUpJcaProviders();
        try {
            thread.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "end preload");
        addBootEvent("Zygote:Preload End...");
        sPreloadComplete = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:91:0x01a4 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    public static void preloadClasses() throws Throwable {
        boolean z;
        int i;
        StringBuilder sb;
        VMRuntime runtime = VMRuntime.getRuntime();
        try {
            FileInputStream fileInputStream = new FileInputStream(PRELOADED_CLASSES);
            Log.i(TAG, "Preloading classes...");
            long jUptimeMillis = SystemClock.uptimeMillis();
            int i2 = Os.getuid();
            int i3 = Os.getgid();
            boolean z2 = true;
            if (i2 == 0 && i3 == 0) {
                try {
                    Os.setregid(0, 9999);
                    Os.setreuid(0, 9999);
                    z = true;
                } catch (ErrnoException e) {
                    throw new RuntimeException("Failed to drop root", e);
                }
            } else {
                z = false;
            }
            float targetHeapUtilization = runtime.getTargetHeapUtilization();
            runtime.setTargetHeapUtilization(0.8f);
            try {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream), 256);
                    i = 0;
                    while (true) {
                        try {
                            String line = bufferedReader.readLine();
                            if (line == null) {
                                break;
                            }
                            String strTrim = line.trim();
                            if (!strTrim.startsWith("#") && !strTrim.equals("")) {
                                Trace.traceBegin(16384L, strTrim);
                                try {
                                    Class.forName(strTrim, z2, null);
                                    i++;
                                } catch (ClassNotFoundException unused) {
                                    Log.w(TAG, "Class not found for preloading: " + strTrim);
                                } catch (UnsatisfiedLinkError e2) {
                                    Log.w(TAG, "Problem preloading " + strTrim + ": " + e2);
                                } catch (Throwable th) {
                                    Log.e(TAG, "Error preloading " + strTrim + ".", th);
                                    if (th instanceof Error) {
                                        throw ((Error) th);
                                    }
                                    if (!(th instanceof RuntimeException)) {
                                        throw new RuntimeException(th);
                                    }
                                    throw ((RuntimeException) th);
                                }
                                Trace.traceEnd(16384L);
                            }
                            z2 = true;
                        } catch (IOException e3) {
                            e = e3;
                            Log.e(TAG, "Error reading /system/etc/preloaded-classes.", e);
                            IoUtils.closeQuietly(fileInputStream);
                            runtime.setTargetHeapUtilization(targetHeapUtilization);
                            Trace.traceBegin(16384L, "PreloadDexCaches");
                            runtime.preloadDexCaches();
                            Trace.traceEnd(16384L);
                            if (z) {
                                try {
                                    Os.setreuid(0, 0);
                                    Os.setregid(0, 0);
                                } catch (ErrnoException e4) {
                                    throw new RuntimeException("Failed to restore root", e4);
                                }
                            }
                            sb = new StringBuilder();
                        }
                    }
                    Log.i(TAG, "...preloaded " + i + " classes in " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
                    IoUtils.closeQuietly(fileInputStream);
                    runtime.setTargetHeapUtilization(targetHeapUtilization);
                    Trace.traceBegin(16384L, "PreloadDexCaches");
                    runtime.preloadDexCaches();
                    Trace.traceEnd(16384L);
                    if (z) {
                        try {
                            Os.setreuid(0, 0);
                            Os.setregid(0, 0);
                        } catch (ErrnoException e5) {
                            throw new RuntimeException("Failed to restore root", e5);
                        }
                    }
                    sb = new StringBuilder();
                } catch (Throwable th2) {
                    th = th2;
                    IoUtils.closeQuietly(fileInputStream);
                    runtime.setTargetHeapUtilization(targetHeapUtilization);
                    Trace.traceBegin(16384L, "PreloadDexCaches");
                    runtime.preloadDexCaches();
                    Trace.traceEnd(16384L);
                    if (z) {
                        try {
                            Os.setreuid(0, 0);
                            Os.setregid(0, 0);
                        } catch (ErrnoException e6) {
                            throw new RuntimeException("Failed to restore root", e6);
                        }
                    }
                    addBootEvent("Zygote:Preload 0 classes in " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms");
                    throw th;
                }
            } catch (IOException e7) {
                e = e7;
                i = 0;
            } catch (Throwable th3) {
                th = th3;
                IoUtils.closeQuietly(fileInputStream);
                runtime.setTargetHeapUtilization(targetHeapUtilization);
                Trace.traceBegin(16384L, "PreloadDexCaches");
                runtime.preloadDexCaches();
                Trace.traceEnd(16384L);
                if (z) {
                    Os.setreuid(0, 0);
                    Os.setregid(0, 0);
                }
                addBootEvent("Zygote:Preload 0 classes in " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms");
                throw th;
            }
            sb.append("Zygote:Preload ");
            sb.append(i);
            sb.append(" classes in ");
            sb.append(SystemClock.uptimeMillis() - jUptimeMillis);
            sb.append("ms");
            addBootEvent(sb.toString());
        } catch (FileNotFoundException unused2) {
            Log.e(TAG, "Couldn't find /system/etc/preloaded-classes.");
        }
    }

    private static int preloadColorStateLists(TypedArray typedArray) {
        int length = typedArray.length();
        for (int i = 0; i < length; i++) {
            int resourceId = typedArray.getResourceId(i, 0);
            if (resourceId != 0 && mResources.getColorStateList(resourceId, null) == null) {
                throw new IllegalArgumentException("Unable to find preloaded color resource #0x" + Integer.toHexString(resourceId) + " (" + typedArray.getString(i) + ")");
            }
        }
        return length;
    }

    private static int preloadDrawables(TypedArray typedArray) {
        int length = typedArray.length();
        for (int i = 0; i < length; i++) {
            int resourceId = typedArray.getResourceId(i, 0);
            if (resourceId != 0 && mResources.getDrawable(resourceId, null) == null) {
                throw new IllegalArgumentException("Unable to find preloaded drawable resource #0x" + Integer.toHexString(resourceId) + " (" + typedArray.getString(i) + ")");
            }
        }
        return length;
    }

    private static void preloadOpenGL() {
        String str = SystemProperties.get(PROPERTY_GFX_DRIVER);
        if (SystemProperties.getBoolean(PROPERTY_DISABLE_OPENGL_PRELOADING, false)) {
            return;
        }
        if (str == null || str.isEmpty()) {
            EGL14.eglGetDisplay(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void preloadResources() throws Throwable {
        VMRuntime.getRuntime();
        try {
            mResources = Resources.getSystem();
            mResources.startPreloading();
            Log.i(TAG, "Preloading resources...");
            long jUptimeMillis = SystemClock.uptimeMillis();
            TypedArray typedArrayObtainTypedArray = mResources.obtainTypedArray(R.array.preloaded_drawables);
            int iPreloadDrawables = preloadDrawables(typedArrayObtainTypedArray);
            typedArrayObtainTypedArray.recycle();
            Log.i(TAG, "...preloaded " + iPreloadDrawables + " resources in " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
            addBootEvent("Zygote:Preload " + iPreloadDrawables + " obtain resources in " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms");
            long jUptimeMillis2 = SystemClock.uptimeMillis();
            TypedArray typedArrayObtainTypedArray2 = mResources.obtainTypedArray(R.array.preloaded_color_state_lists);
            int iPreloadColorStateLists = preloadColorStateLists(typedArrayObtainTypedArray2);
            typedArrayObtainTypedArray2.recycle();
            Log.i(TAG, "...preloaded " + iPreloadColorStateLists + " resources in " + (SystemClock.uptimeMillis() - jUptimeMillis2) + "ms.");
            if (mResources.getBoolean(R.bool.config_freeformWindowManagement)) {
                jUptimeMillis2 = SystemClock.uptimeMillis();
                TypedArray typedArrayObtainTypedArray3 = mResources.obtainTypedArray(R.array.preloaded_freeform_multi_window_drawables);
                iPreloadColorStateLists = preloadDrawables(typedArrayObtainTypedArray3);
                typedArrayObtainTypedArray3.recycle();
                Log.i(TAG, "...preloaded " + iPreloadColorStateLists + " resource in " + (SystemClock.uptimeMillis() - jUptimeMillis2) + "ms.");
            }
            addBootEvent("Zygote:Preload " + iPreloadColorStateLists + " resources in " + (SystemClock.uptimeMillis() - jUptimeMillis2) + "ms");
            mResources.finishPreloading();
        } catch (RuntimeException e) {
            Log.w(TAG, "Failure preloading resources", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void preloadSecondStageClasses() {
        try {
            FileInputStream fileInputStream = new FileInputStream(PRELOADED_SECOND_STAGE_CLASSES);
            Log.i(TAG, "Preloading Second Stage classes...");
            try {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream), 256);
                    while (true) {
                        String line = bufferedReader.readLine();
                        if (line == null) {
                            break;
                        }
                        String strTrim = line.trim();
                        if (!strTrim.startsWith("#") && !strTrim.equals("")) {
                            try {
                                Class.forName(strTrim, true, null);
                            } catch (ClassNotFoundException unused) {
                                Log.w(TAG, "Class not found for preloading: " + strTrim);
                            } catch (UnsatisfiedLinkError e) {
                                Log.w(TAG, "Problem preloading " + strTrim + ": " + e);
                            } catch (Throwable th) {
                                Log.e(TAG, "Error preloading " + strTrim + ".", th);
                                if (th instanceof Error) {
                                    throw ((Error) th);
                                }
                                if (!(th instanceof RuntimeException)) {
                                    throw new RuntimeException(th);
                                }
                                throw ((RuntimeException) th);
                            }
                        }
                    }
                } catch (IOException e2) {
                    Log.e(TAG, "Error reading /system/etc/preloaded-classes-secondlist.", e2);
                }
                IoUtils.closeQuietly(fileInputStream);
            } catch (Throwable th2) {
                IoUtils.closeQuietly(fileInputStream);
                throw th2;
            }
        } catch (FileNotFoundException unused2) {
            Log.e(TAG, "Couldn't find /system/etc/preloaded-classes-secondlist.");
        }
    }

    private static void preloadSharedLibraries() {
        Log.i(TAG, "Preloading shared libraries...");
        System.loadLibrary(ZenModeConfig.SYSTEM_AUTHORITY);
        System.loadLibrary("compiler_rt");
        System.loadLibrary("jnigraphics");
    }

    private static void preloadTextResources() {
        Hyphenator.init();
        TextView.preloadFontCache();
    }

    private static void prepareSystemServerProfile(String str) throws RemoteException {
        if (str.isEmpty()) {
            return;
        }
        String[] strArrSplit = str.split(SettingsStringUtil.DELIMITER);
        IInstalld.Stub.asInterface(ServiceManager.getService("installd")).prepareAppProfile(ZenModeConfig.SYSTEM_AUTHORITY, 0, UserHandle.getAppId(1000), "primary.prof", strArrSplit[0], null);
        VMRuntime.registerAppInfo(new File(Environment.getDataProfilesDePackageDirectory(0, ZenModeConfig.SYSTEM_AUTHORITY), "primary.prof").getAbsolutePath(), strArrSplit);
    }

    public static void setApiBlacklistExemptions(String[] strArr) {
        VMRuntime.getRuntime().setHiddenApiExemptions(strArr);
    }

    public static void setHiddenApiAccessLogSampleRate(int i) {
        VMRuntime.getRuntime().setHiddenApiAccessLogSamplingRate(i);
    }

    private static void waitForSecondaryZygote(String str) {
        ZygoteProcess.waitForConnectionToZygote(Process.ZYGOTE_SOCKET.equals(str) ? Process.SECONDARY_ZYGOTE_SOCKET : Process.ZYGOTE_SOCKET);
    }

    private static void warmUpJcaProviders() {
        long jUptimeMillis = SystemClock.uptimeMillis();
        Trace.traceBegin(16384L, "Starting installation of AndroidKeyStoreProvider");
        AndroidKeyStoreProvider.install();
        Log.i(TAG, "Installed AndroidKeyStoreProvider in " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
        Trace.traceEnd(16384L);
        long jUptimeMillis2 = SystemClock.uptimeMillis();
        Trace.traceBegin(16384L, "Starting warm up of JCA providers");
        for (Provider provider : Security.getProviders()) {
            provider.warmUpServiceProvision();
        }
        Log.i(TAG, "Warmed up JCA providers in " + (SystemClock.uptimeMillis() - jUptimeMillis2) + "ms.");
        Trace.traceEnd(16384L);
    }

    public static final Runnable zygoteInit(int i, String[] strArr, ClassLoader classLoader) {
        Trace.traceBegin(64L, "ZygoteInit");
        RuntimeInit.redirectLogStreams();
        RuntimeInit.commonInit();
        nativeZygoteInit();
        return RuntimeInit.applicationInit(i, strArr, classLoader);
    }
}
