package com.facebook.soloader;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.provider.SettingsStringUtil;
import android.security.keystore.KeyProperties;
import android.text.TextUtils;
import android.util.Log;
import com.facebook.soloader.nativeloader.NativeLoader;
import dalvik.system.BaseDexClassLoader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;

/* JADX INFO: loaded from: classes3.dex */
@ThreadSafe
public class SoLoader {
    static final boolean DEBUG = false;
    public static final int SOLOADER_ALLOW_ASYNC_INIT = 2;
    public static final int SOLOADER_DISABLE_BACKUP_SOSOURCE = 8;
    public static final int SOLOADER_DONT_TREAT_AS_SYSTEMAPP = 32;
    public static final int SOLOADER_ENABLE_EXOPACKAGE = 1;
    public static final int SOLOADER_LOOK_IN_ZIP = 4;
    public static final int SOLOADER_SKIP_MERGED_JNI_ONLOAD = 16;
    private static final String SO_STORE_NAME_MAIN = "lib-main";
    private static final String SO_STORE_NAME_SPLIT = "lib-";
    static final boolean SYSTRACE_LIBRARY_LOADING;
    static final String TAG = "SoLoader";
    private static boolean isSystemApp;

    @GuardedBy("sSoSourcesLock")
    @Nullable
    private static ApplicationSoSource sApplicationSoSource;

    @GuardedBy("sSoSourcesLock")
    @Nullable
    private static UnpackingSoSource[] sBackupSoSources;

    @GuardedBy("sSoSourcesLock")
    private static int sFlags;

    @Nullable
    static SoFileLoader sSoFileLoader;
    private static final ReentrantReadWriteLock sSoSourcesLock = new ReentrantReadWriteLock();

    @GuardedBy("sSoSourcesLock")
    @Nullable
    private static SoSource[] sSoSources = null;

    @GuardedBy("sSoSourcesLock")
    private static volatile int sSoSourcesVersion = 0;

    @GuardedBy("SoLoader.class")
    private static final HashSet<String> sLoadedLibraries = new HashSet<>();

    @GuardedBy("SoLoader.class")
    private static final Map<String, Object> sLoadingLibraries = new HashMap();
    private static final Set<String> sLoadedAndMergedLibraries = Collections.newSetFromMap(new ConcurrentHashMap());

    @Nullable
    private static SystemLoadLibraryWrapper sSystemLoadLibraryWrapper = null;

    @DoNotOptimize
    @TargetApi(14)
    private static class Api14Utils {
        private Api14Utils() {
        }

        public static String getClassLoaderLdLoadLibrary() {
            ClassLoader classLoader = SoLoader.class.getClassLoader();
            if (classLoader == null || (classLoader instanceof BaseDexClassLoader)) {
                try {
                    return (String) BaseDexClassLoader.class.getMethod("getLdLibraryPath", new Class[0]).invoke((BaseDexClassLoader) classLoader, new Object[0]);
                } catch (Exception e) {
                    throw new RuntimeException("Cannot call getLdLibraryPath", e);
                }
            }
            throw new IllegalStateException("ClassLoader " + classLoader.getClass().getName() + " should be of type BaseDexClassLoader");
        }
    }

    @NotThreadSafe
    static class TestOnlyUtils {
        TestOnlyUtils() {
        }

        static void resetStatus() {
            synchronized (SoLoader.class) {
                SoLoader.sLoadedLibraries.clear();
                SoLoader.sLoadingLibraries.clear();
                SoLoader.sSoFileLoader = null;
            }
            setSoSources(null);
        }

        static void setSoFileLoader(SoFileLoader soFileLoader) {
            SoLoader.sSoFileLoader = soFileLoader;
        }

        static void setSoSources(SoSource[] soSourceArr) {
            SoLoader.sSoSourcesLock.writeLock().lock();
            try {
                SoSource[] unused = SoLoader.sSoSources = soSourceArr;
                SoLoader.access$208();
            } finally {
                SoLoader.sSoSourcesLock.writeLock().unlock();
            }
        }
    }

    public static final class WrongAbiError extends UnsatisfiedLinkError {
        WrongAbiError(Throwable th, String str) {
            super("APK was built for a different platform. Supported ABIs: " + Arrays.toString(SysUtil.getSupportedAbis()) + " error: " + str);
            initCause(th);
        }
    }

    static {
        boolean z = false;
        try {
            if (Build.VERSION.SDK_INT >= 18) {
                z = true;
            }
        } catch (NoClassDefFoundError | UnsatisfiedLinkError unused) {
        }
        SYSTRACE_LIBRARY_LOADING = z;
    }

    static /* synthetic */ int access$208() {
        int i = sSoSourcesVersion;
        sSoSourcesVersion = i + 1;
        return i;
    }

    public static boolean areSoSourcesAbisSupported() {
        sSoSourcesLock.readLock().lock();
        try {
            if (sSoSources == null) {
                return false;
            }
            String[] supportedAbis = SysUtil.getSupportedAbis();
            for (SoSource soSource : sSoSources) {
                for (String str : soSource.getSoSourceAbis()) {
                    boolean zEquals = false;
                    for (int i = 0; i < supportedAbis.length && !zEquals; i++) {
                        zEquals = str.equals(supportedAbis[i]);
                    }
                    if (!zEquals) {
                        Log.e(TAG, "abi not supported: " + str);
                        return false;
                    }
                }
            }
            return true;
        } finally {
            sSoSourcesLock.readLock().unlock();
        }
    }

    private static void assertInitialized() {
        if (!isInitialized()) {
            throw new RuntimeException("SoLoader.init() not yet called");
        }
    }

    private static boolean checkIfSystemApp(Context context, int i) {
        return ((i & 32) != 0 || context == null || (context.getApplicationInfo().flags & 129) == 0) ? false : true;
    }

    public static void deinitForTest() {
        TestOnlyUtils.setSoSources(null);
    }

    /* JADX WARN: Code duplicated, block: B:65:0x012e  */
    /* JADX WARN: Code duplicated, block: B:67:0x0133  */
    /* JADX WARN: Code duplicated, block: B:75:0x0157 A[LOOP:2: B:73:0x0152->B:75:0x0157, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:78:0x0176  */
    private static void doLoadLibraryBySoName(String str, int i, @Nullable StrictMode.ThreadPolicy threadPolicy) throws Throwable {
        boolean z;
        int iLoadLibrary;
        StringBuilder sb;
        sSoSourcesLock.readLock().lock();
        try {
            if (sSoSources == null) {
                Log.e(TAG, "Could not load: " + str + " because no SO source exists");
                throw new UnsatisfiedLinkError("couldn't find DSO to load: " + str);
            }
            sSoSourcesLock.readLock().unlock();
            int i2 = 0;
            if (threadPolicy == null) {
                threadPolicy = StrictMode.allowThreadDiskReads();
                z = true;
            } else {
                z = false;
            }
            if (SYSTRACE_LIBRARY_LOADING) {
                Api18TraceUtils.beginTraceSection("SoLoader.loadLibrary[", str, "]");
            }
            try {
                sSoSourcesLock.readLock().lock();
                int i3 = 0;
                int i4 = 0;
                while (i3 == 0) {
                    try {
                        if (i4 >= sSoSources.length) {
                            break;
                        }
                        iLoadLibrary = sSoSources[i4].loadLibrary(str, i, threadPolicy);
                        if (iLoadLibrary == 3) {
                            try {
                                if (sBackupSoSources != null) {
                                    Log.d(TAG, "Trying backup SoSource for " + str);
                                    UnpackingSoSource[] unpackingSoSourceArr = sBackupSoSources;
                                    int length = unpackingSoSourceArr.length;
                                    for (int i5 = 0; i5 < length; i5++) {
                                        UnpackingSoSource unpackingSoSource = unpackingSoSourceArr[i5];
                                        unpackingSoSource.prepare(str);
                                        int iLoadLibrary2 = unpackingSoSource.loadLibrary(str, i, threadPolicy);
                                        if (iLoadLibrary2 == 1) {
                                            iLoadLibrary = iLoadLibrary2;
                                            break;
                                        }
                                    }
                                    i3 = iLoadLibrary;
                                    break;
                                }
                            } catch (Throwable th) {
                                th = th;
                                try {
                                    sSoSourcesLock.readLock().unlock();
                                    throw th;
                                } catch (Throwable th2) {
                                    th = th2;
                                    if (SYSTRACE_LIBRARY_LOADING) {
                                        Api18TraceUtils.endSection();
                                    }
                                    if (z) {
                                        StrictMode.setThreadPolicy(threadPolicy);
                                    }
                                    if (iLoadLibrary == 0) {
                                    }
                                    sb = new StringBuilder();
                                    sb.append("couldn't find DSO to load: ");
                                    sb.append(str);
                                    sSoSourcesLock.readLock().lock();
                                    while (i2 < sSoSources.length) {
                                        sb.append("\n\tSoSource ");
                                        sb.append(i2);
                                        sb.append(": ");
                                        sb.append(sSoSources[i2].toString());
                                        i2++;
                                    }
                                    if (sApplicationSoSource != null) {
                                        File nativeLibDirFromContext = ApplicationSoSource.getNativeLibDirFromContext(sApplicationSoSource.getUpdatedContext());
                                        sb.append("\n\tNative lib dir: ");
                                        sb.append(nativeLibDirFromContext.getAbsolutePath());
                                        sb.append("\n");
                                    }
                                    sSoSourcesLock.readLock().unlock();
                                    sb.append(" result: ");
                                    sb.append(iLoadLibrary);
                                    String string = sb.toString();
                                    Log.e(TAG, string);
                                    throw new UnsatisfiedLinkError(string);
                                }
                            }
                            if (SYSTRACE_LIBRARY_LOADING) {
                                Api18TraceUtils.endSection();
                            }
                            if (z) {
                                StrictMode.setThreadPolicy(threadPolicy);
                            }
                            if (iLoadLibrary == 0 || iLoadLibrary == 3) {
                                StringBuilder sb2 = new StringBuilder();
                                sb2.append("couldn't find DSO to load: ");
                                sb2.append(str);
                                String message = th.getMessage();
                                if (message == null) {
                                    message = th.toString();
                                }
                                sb2.append(" caused by: ");
                                sb2.append(message);
                                th.printStackTrace();
                                sb2.append(" result: ");
                                sb2.append(iLoadLibrary);
                                String string2 = sb2.toString();
                                Log.e(TAG, string2);
                                throw new UnsatisfiedLinkError(string2);
                            }
                            return;
                        }
                        i4++;
                        i3 = iLoadLibrary;
                    } catch (Throwable th3) {
                        th = th3;
                        iLoadLibrary = i3;
                    }
                }
                try {
                    sSoSourcesLock.readLock().unlock();
                    if (SYSTRACE_LIBRARY_LOADING) {
                        Api18TraceUtils.endSection();
                    }
                    if (z) {
                        StrictMode.setThreadPolicy(threadPolicy);
                    }
                    if (i3 == 0 || i3 == 3) {
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append("couldn't find DSO to load: ");
                        sb3.append(str);
                        sSoSourcesLock.readLock().lock();
                        while (i2 < sSoSources.length) {
                            sb3.append("\n\tSoSource ");
                            sb3.append(i2);
                            sb3.append(": ");
                            sb3.append(sSoSources[i2].toString());
                            i2++;
                        }
                        if (sApplicationSoSource != null) {
                            File nativeLibDirFromContext2 = ApplicationSoSource.getNativeLibDirFromContext(sApplicationSoSource.getUpdatedContext());
                            sb3.append("\n\tNative lib dir: ");
                            sb3.append(nativeLibDirFromContext2.getAbsolutePath());
                            sb3.append("\n");
                        }
                        sSoSourcesLock.readLock().unlock();
                        sb3.append(" result: ");
                        sb3.append(i3);
                        String string3 = sb3.toString();
                        Log.e(TAG, string3);
                        throw new UnsatisfiedLinkError(string3);
                    }
                } catch (Throwable th4) {
                    th = th4;
                    iLoadLibrary = i3;
                }
            } catch (Throwable th5) {
                th = th5;
                iLoadLibrary = 0;
            }
        } catch (Throwable th6) {
            sSoSourcesLock.readLock().unlock();
            throw th6;
        }
    }

    @Nullable
    public static String[] getLibraryDependencies(String str) throws IOException {
        sSoSourcesLock.readLock().lock();
        try {
            String[] libraryDependencies = null;
            if (sSoSources != null) {
                for (int i = 0; libraryDependencies == null && i < sSoSources.length; i++) {
                    libraryDependencies = sSoSources[i].getLibraryDependencies(str);
                }
            }
            return libraryDependencies;
        } finally {
            sSoSourcesLock.readLock().unlock();
        }
    }

    @Nullable
    public static String getLibraryPath(String str) throws IOException {
        sSoSourcesLock.readLock().lock();
        try {
            String libraryPath = null;
            if (sSoSources != null) {
                for (int i = 0; libraryPath == null && i < sSoSources.length; i++) {
                    libraryPath = sSoSources[i].getLibraryPath(str);
                }
            }
            return libraryPath;
        } finally {
            sSoSourcesLock.readLock().unlock();
        }
    }

    @Nullable
    private static Method getNativeLoadRuntimeMethod() {
        if (Build.VERSION.SDK_INT < 23 || Build.VERSION.SDK_INT > 27) {
            return null;
        }
        try {
            Method declaredMethod = Runtime.class.getDeclaredMethod("nativeLoad", String.class, ClassLoader.class, String.class);
            declaredMethod.setAccessible(true);
            return declaredMethod;
        } catch (NoSuchMethodException | SecurityException e) {
            Log.w(TAG, "Cannot get nativeLoad method", e);
            return null;
        }
    }

    public static int getSoSourcesVersion() {
        return sSoSourcesVersion;
    }

    public static void init(Context context, int i) throws IOException {
        init(context, i, null);
    }

    public static void init(Context context, int i, @Nullable SoFileLoader soFileLoader) throws IOException {
        StrictMode.ThreadPolicy threadPolicyAllowThreadDiskWrites = StrictMode.allowThreadDiskWrites();
        try {
            isSystemApp = checkIfSystemApp(context, i);
            initSoLoader(soFileLoader);
            initSoSources(context, i, soFileLoader);
            if (!NativeLoader.isInitialized()) {
                NativeLoader.init(new NativeLoaderToSoLoaderDelegate());
            }
        } finally {
            StrictMode.setThreadPolicy(threadPolicyAllowThreadDiskWrites);
        }
    }

    public static void init(Context context, boolean z) {
        try {
            init(context, z ? 1 : 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static synchronized void initSoLoader(@Nullable SoFileLoader soFileLoader) {
        try {
            if (soFileLoader != null) {
                sSoFileLoader = soFileLoader;
                return;
            }
            final Runtime runtime = Runtime.getRuntime();
            final Method nativeLoadRuntimeMethod = getNativeLoadRuntimeMethod();
            final boolean z = nativeLoadRuntimeMethod != null;
            final String classLoaderLdLoadLibrary = z ? Api14Utils.getClassLoaderLdLoadLibrary() : null;
            final String strMakeNonZipPath = makeNonZipPath(classLoaderLdLoadLibrary);
            sSoFileLoader = new SoFileLoader() { // from class: com.facebook.soloader.SoLoader.1
                /* JADX WARN: Code duplicated, block: B:20:0x0047 A[Catch: NoSuchAlgorithmException -> 0x004b, SecurityException -> 0x0051, IOException -> 0x0057, TryCatch #5 {IOException -> 0x0057, SecurityException -> 0x0051, NoSuchAlgorithmException -> 0x004b, blocks: (B:2:0x0000, B:9:0x0034, B:16:0x003e, B:21:0x004a, B:20:0x0047, B:19:0x0043, B:4:0x0013, B:5:0x0015, B:7:0x001c, B:8:0x0020, B:14:0x003b), top: B:34:0x0000, inners: #3, #4 }] */
                /* JADX WARN: Code duplicated, block: B:32:0x003e A[EXC_TOP_SPLITTER, SYNTHETIC] */
                private String getLibHash(String str) {
                    try {
                        File file = new File(str);
                        MessageDigest messageDigest = MessageDigest.getInstance(KeyProperties.DIGEST_MD5);
                        FileInputStream fileInputStream = new FileInputStream(file);
                        Throwable th = null;
                        try {
                            try {
                                byte[] bArr = new byte[4096];
                                while (true) {
                                    int i = fileInputStream.read(bArr);
                                    if (i <= 0) {
                                        String str2 = String.format("%32x", new BigInteger(1, messageDigest.digest()));
                                        fileInputStream.close();
                                        return str2;
                                    }
                                    messageDigest.update(bArr, 0, i);
                                    if (th != null) {
                                        try {
                                            fileInputStream.close();
                                        } catch (Throwable th2) {
                                            th.addSuppressed(th2);
                                        }
                                    } else {
                                        fileInputStream.close();
                                    }
                                    throw th;
                                }
                            } catch (Throwable th3) {
                                th = th3;
                                throw th;
                            }
                        } catch (Throwable th4) {
                            if (th != null) {
                                fileInputStream.close();
                            } else {
                                fileInputStream.close();
                            }
                            throw th4;
                        }
                    } catch (IOException e) {
                        return e.toString();
                    } catch (SecurityException e2) {
                        return e2.toString();
                    } catch (NoSuchAlgorithmException e3) {
                        return e3.toString();
                    }
                }

                /* JADX WARN: Bottom block not found for handler: all -> 0x006d */
                /* JADX WARN: Code duplicated, block: B:35:0x008e  */
                /* JADX WARN: Code restructure failed: missing block: B:17:0x0035, code lost:
                
                    if (r1 == null) goto L49;
                 */
                /* JADX WARN: Code restructure failed: missing block: B:18:0x0037, code lost:
                
                    android.util.Log.e(com.facebook.soloader.SoLoader.TAG, "Error when loading lib: " + r1 + " lib hash: " + getLibHash(r9) + " search path is " + r10);
                 */
                /* JADX WARN: Code restructure failed: missing block: B:46:0x006e, code lost:
                
                    r1 = th;
                 */
                /* JADX WARN: Code restructure failed: missing block: B:49:?, code lost:
                
                    return;
                 */
                /* JADX WARN: Code restructure failed: missing block: B:50:?, code lost:
                
                    return;
                 */
                /* JADX WARN: Instruction removed from duplicated block: B:35:0x008e, please report this as an issue */
                @Override // com.facebook.soloader.SoFileLoader
                /*
                    Code decompiled incorrectly, please refer to instructions dump.
                */
                public void load(String str, int i) throws Throwable {
                    if (!z) {
                        System.load(str);
                        return;
                    }
                    String str2 = (i & 4) == 4 ? classLoaderLdLoadLibrary : strMakeNonZipPath;
                    String str3 = null;
                    try {
                        try {
                            synchronized (runtime) {
                                String str4 = (String) nativeLoadRuntimeMethod.invoke(runtime, str, SoLoader.class.getClassLoader(), str2);
                                try {
                                    if (str4 != null) {
                                        throw new UnsatisfiedLinkError(str4);
                                    }
                                } catch (Throwable th) {
                                    th = th;
                                    str3 = str4;
                                    while (true) {
                                        break;
                                    }
                                    throw th;
                                }
                            }
                            while (true) {
                                break;
                                break;
                            }
                            throw th;
                        } catch (Throwable th2) {
                            th = th2;
                            if (str3 != null) {
                                Log.e(SoLoader.TAG, "Error when loading lib: " + str3 + " lib hash: " + getLibHash(str) + " search path is " + str2);
                            }
                            throw th;
                        }
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        String str5 = "Error: Cannot load " + str;
                        try {
                            throw new RuntimeException(str5, e);
                        } catch (Throwable th3) {
                            th = th3;
                            str3 = str5;
                            if (str3 != null) {
                                Log.e(SoLoader.TAG, "Error when loading lib: " + str3 + " lib hash: " + getLibHash(str) + " search path is " + str2);
                            }
                            throw th;
                        }
                    }
                }
            };
        } catch (Throwable th) {
            throw th;
        }
    }

    private static void initSoSources(Context context, int i, @Nullable SoFileLoader soFileLoader) throws IOException {
        int i2;
        sSoSourcesLock.writeLock().lock();
        try {
            if (sSoSources == null) {
                Log.d(TAG, "init start");
                sFlags = i;
                ArrayList arrayList = new ArrayList();
                String str = System.getenv("LD_LIBRARY_PATH");
                if (str == null) {
                    str = SysUtil.is64Bit() ? "/vendor/lib64:/system/lib64" : "/vendor/lib:/system/lib";
                }
                for (String str2 : str.split(SettingsStringUtil.DELIMITER)) {
                    Log.d(TAG, "adding system library source: " + str2);
                    arrayList.add(new DirectorySoSource(new File(str2), 2));
                }
                if (context != null) {
                    if ((i & 1) != 0) {
                        sBackupSoSources = null;
                        Log.d(TAG, "adding exo package source: lib-main");
                        arrayList.add(0, new ExoSoSource(context, SO_STORE_NAME_MAIN));
                    } else {
                        if (isSystemApp) {
                            i2 = 0;
                        } else {
                            sApplicationSoSource = new ApplicationSoSource(context, Build.VERSION.SDK_INT <= 17 ? 1 : 0);
                            Log.d(TAG, "adding application source: " + sApplicationSoSource.toString());
                            arrayList.add(0, sApplicationSoSource);
                            i2 = 1;
                        }
                        if ((sFlags & 8) != 0) {
                            sBackupSoSources = null;
                        } else {
                            File file = new File(context.getApplicationInfo().sourceDir);
                            ArrayList arrayList2 = new ArrayList();
                            ApkSoSource apkSoSource = new ApkSoSource(context, file, SO_STORE_NAME_MAIN, i2);
                            arrayList2.add(apkSoSource);
                            Log.d(TAG, "adding backup source from : " + apkSoSource.toString());
                            if (Build.VERSION.SDK_INT >= 21 && context.getApplicationInfo().splitSourceDirs != null) {
                                Log.d(TAG, "adding backup sources from split apks");
                                String[] strArr = context.getApplicationInfo().splitSourceDirs;
                                int length = strArr.length;
                                int i3 = 0;
                                int i4 = 0;
                                while (i3 < length) {
                                    ApkSoSource apkSoSource2 = new ApkSoSource(context, new File(strArr[i3]), SO_STORE_NAME_SPLIT + i4, i2);
                                    Log.d(TAG, "adding backup source: " + apkSoSource2.toString());
                                    arrayList2.add(apkSoSource2);
                                    i3++;
                                    i4++;
                                }
                            }
                            sBackupSoSources = (UnpackingSoSource[]) arrayList2.toArray(new UnpackingSoSource[arrayList2.size()]);
                            arrayList.addAll(0, arrayList2);
                        }
                    }
                }
                SoSource[] soSourceArr = (SoSource[]) arrayList.toArray(new SoSource[arrayList.size()]);
                int iMakePrepareFlags = makePrepareFlags();
                int length2 = soSourceArr.length;
                while (true) {
                    int i5 = length2 - 1;
                    if (length2 <= 0) {
                        break;
                    }
                    Log.d(TAG, "Preparing SO source: " + soSourceArr[i5]);
                    soSourceArr[i5].prepare(iMakePrepareFlags);
                    length2 = i5;
                }
                sSoSources = soSourceArr;
                sSoSourcesVersion++;
                Log.d(TAG, "init finish: " + sSoSources.length + " SO sources prepared");
            }
        } finally {
            Log.d(TAG, "init exiting");
            sSoSourcesLock.writeLock().unlock();
        }
    }

    public static boolean isInitialized() {
        sSoSourcesLock.readLock().lock();
        try {
            return sSoSources != null;
        } finally {
            sSoSourcesLock.readLock().unlock();
        }
    }

    public static boolean loadLibrary(String str) {
        return loadLibrary(str, 0);
    }

    public static boolean loadLibrary(String str, int i) throws UnsatisfiedLinkError {
        boolean z;
        sSoSourcesLock.readLock().lock();
        try {
            if (sSoSources == null) {
                if (!"http://www.android.com/".equals(System.getProperty("java.vendor.url"))) {
                    synchronized (SoLoader.class) {
                        z = !sLoadedLibraries.contains(str);
                        if (z) {
                            if (sSystemLoadLibraryWrapper != null) {
                                sSystemLoadLibraryWrapper.loadLibrary(str);
                            } else {
                                System.loadLibrary(str);
                            }
                        }
                    }
                    sSoSourcesLock.readLock().unlock();
                    return z;
                }
                assertInitialized();
            }
            sSoSourcesLock.readLock().unlock();
            if (!isSystemApp || sSystemLoadLibraryWrapper == null) {
                String strMapLibName = MergedSoMapping.mapLibName(str);
                return loadLibraryBySoName(System.mapLibraryName(strMapLibName != null ? strMapLibName : str), str, strMapLibName, i, null);
            }
            sSystemLoadLibraryWrapper.loadLibrary(str);
            return true;
        } catch (Throwable th) {
            sSoSourcesLock.readLock().unlock();
            throw th;
        }
    }

    static void loadLibraryBySoName(String str, int i, StrictMode.ThreadPolicy threadPolicy) {
        loadLibraryBySoNameImpl(str, null, null, i, threadPolicy);
    }

    private static boolean loadLibraryBySoName(String str, @Nullable String str2, @Nullable String str3, int i, @Nullable StrictMode.ThreadPolicy threadPolicy) {
        boolean z;
        boolean zLoadLibraryBySoNameImpl = false;
        do {
            try {
                z = false;
                zLoadLibraryBySoNameImpl = loadLibraryBySoNameImpl(str, str2, str3, i, threadPolicy);
            } catch (UnsatisfiedLinkError e) {
                int i2 = sSoSourcesVersion;
                sSoSourcesLock.writeLock().lock();
                try {
                    try {
                        z = true;
                        if (sApplicationSoSource == null || !sApplicationSoSource.checkAndMaybeUpdate()) {
                            z = false;
                        } else {
                            Log.w(TAG, "sApplicationSoSource updated during load: " + str + ", attempting load again.");
                            sSoSourcesVersion = sSoSourcesVersion + 1;
                        }
                        sSoSourcesLock.writeLock().unlock();
                        if (sSoSourcesVersion == i2) {
                            throw e;
                        }
                    } catch (IOException e2) {
                        throw new RuntimeException(e2);
                    }
                } catch (Throwable th) {
                    sSoSourcesLock.writeLock().unlock();
                    throw th;
                }
            }
        } while (z);
        return zLoadLibraryBySoNameImpl;
    }

    private static boolean loadLibraryBySoNameImpl(String str, @Nullable String str2, @Nullable String str3, int i, @Nullable StrictMode.ThreadPolicy threadPolicy) {
        boolean z;
        Object obj;
        boolean z2 = false;
        if (!TextUtils.isEmpty(str2) && sLoadedAndMergedLibraries.contains(str2)) {
            return false;
        }
        synchronized (SoLoader.class) {
            if (!sLoadedLibraries.contains(str)) {
                z = false;
            } else {
                if (str3 == null) {
                    return false;
                }
                z = true;
            }
            if (sLoadingLibraries.containsKey(str)) {
                obj = sLoadingLibraries.get(str);
            } else {
                obj = new Object();
                sLoadingLibraries.put(str, obj);
            }
            synchronized (obj) {
                if (!z) {
                    try {
                        synchronized (SoLoader.class) {
                            if (sLoadedLibraries.contains(str)) {
                                if (str3 == null) {
                                    return false;
                                }
                                z = true;
                            }
                            if (!z) {
                                try {
                                    Log.d(TAG, "About to load: " + str);
                                    doLoadLibraryBySoName(str, i, threadPolicy);
                                    synchronized (SoLoader.class) {
                                        Log.d(TAG, "Loaded: " + str);
                                        sLoadedLibraries.add(str);
                                    }
                                } catch (UnsatisfiedLinkError e) {
                                    String message = e.getMessage();
                                    if (message == null || !message.contains("unexpected e_machine:")) {
                                        throw e;
                                    }
                                    throw new WrongAbiError(e, message.substring(message.lastIndexOf("unexpected e_machine:")));
                                }
                            }
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
                if ((i & 16) == 0) {
                    if (!TextUtils.isEmpty(str2) && sLoadedAndMergedLibraries.contains(str2)) {
                        z2 = true;
                    }
                    if (str3 != null && !z2) {
                        if (SYSTRACE_LIBRARY_LOADING) {
                            Api18TraceUtils.beginTraceSection("MergedSoMapping.invokeJniOnload[", str2, "]");
                        }
                        try {
                            try {
                                Log.d(TAG, "About to merge: " + str2 + " / " + str);
                                MergedSoMapping.invokeJniOnload(str2);
                                sLoadedAndMergedLibraries.add(str2);
                                if (SYSTRACE_LIBRARY_LOADING) {
                                    Api18TraceUtils.endSection();
                                }
                            } catch (UnsatisfiedLinkError e2) {
                                throw new RuntimeException("Failed to call JNI_OnLoad from '" + str2 + "', which has been merged into '" + str + "'.  See comment for details.", e2);
                            }
                        } catch (Throwable th2) {
                            if (SYSTRACE_LIBRARY_LOADING) {
                                Api18TraceUtils.endSection();
                            }
                            throw th2;
                        }
                    }
                }
                return !z;
            }
        }
    }

    public static String makeLdLibraryPath() {
        sSoSourcesLock.readLock().lock();
        try {
            assertInitialized();
            Log.d(TAG, "makeLdLibraryPath");
            ArrayList arrayList = new ArrayList();
            SoSource[] soSourceArr = sSoSources;
            if (soSourceArr != null) {
                for (SoSource soSource : soSourceArr) {
                    soSource.addToLdLibraryPath(arrayList);
                }
            }
            String strJoin = TextUtils.join(SettingsStringUtil.DELIMITER, arrayList);
            Log.d(TAG, "makeLdLibraryPath final path: " + strJoin);
            return strJoin;
        } finally {
            sSoSourcesLock.readLock().unlock();
        }
    }

    @Nullable
    public static String makeNonZipPath(String str) {
        if (str == null) {
            return null;
        }
        String[] strArrSplit = str.split(SettingsStringUtil.DELIMITER);
        ArrayList arrayList = new ArrayList(strArrSplit.length);
        for (String str2 : strArrSplit) {
            if (!str2.contains("!")) {
                arrayList.add(str2);
            }
        }
        return TextUtils.join(SettingsStringUtil.DELIMITER, arrayList);
    }

    private static int makePrepareFlags() {
        sSoSourcesLock.writeLock().lock();
        try {
            return (sFlags & 2) != 0 ? 1 : 0;
        } finally {
            sSoSourcesLock.writeLock().unlock();
        }
    }

    public static void prependSoSource(SoSource soSource) throws IOException {
        sSoSourcesLock.writeLock().lock();
        try {
            Log.d(TAG, "Prepending to SO sources: " + soSource);
            assertInitialized();
            soSource.prepare(makePrepareFlags());
            SoSource[] soSourceArr = new SoSource[sSoSources.length + 1];
            soSourceArr[0] = soSource;
            System.arraycopy(sSoSources, 0, soSourceArr, 1, sSoSources.length);
            sSoSources = soSourceArr;
            sSoSourcesVersion++;
            Log.d(TAG, "Prepended to SO sources: " + soSource);
        } finally {
            sSoSourcesLock.writeLock().unlock();
        }
    }

    public static void setInTestMode() {
        TestOnlyUtils.setSoSources(new SoSource[]{new NoopSoSource()});
    }

    public static void setSystemLoadLibraryWrapper(SystemLoadLibraryWrapper systemLoadLibraryWrapper) {
        sSystemLoadLibraryWrapper = systemLoadLibraryWrapper;
    }

    public static File unpackLibraryAndDependencies(String str) throws UnsatisfiedLinkError {
        assertInitialized();
        try {
            return unpackLibraryBySoName(System.mapLibraryName(str));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static File unpackLibraryBySoName(String str) throws IOException {
        sSoSourcesLock.readLock().lock();
        try {
            for (SoSource soSource : sSoSources) {
                File fileUnpackLibrary = soSource.unpackLibrary(str);
                if (fileUnpackLibrary != null) {
                    return fileUnpackLibrary;
                }
            }
            throw new FileNotFoundException(str);
        } finally {
            sSoSourcesLock.readLock().unlock();
        }
    }
}
