package android.content.res;

import android.content.pm.ActivityInfo;
import android.net.wifi.WifiEnterpriseConfig;
import android.os.ParcelFileDescriptor;
import android.util.ArraySet;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.Preconditions;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import libcore.io.IoUtils;

/* JADX INFO: loaded from: classes.dex */
public final class AssetManager implements AutoCloseable {
    public static final int ACCESS_BUFFER = 3;
    public static final int ACCESS_RANDOM = 1;
    public static final int ACCESS_STREAMING = 2;
    public static final int ACCESS_UNKNOWN = 0;
    private static final boolean DEBUG_REFS = false;
    private static final String FRAMEWORK_APK_PATH = "/system/framework/framework-res.apk";
    private static final String TAG = "AssetManager";

    @GuardedBy("sSync")
    private static ArraySet<ApkAssets> sSystemApkAssetsSet;

    @GuardedBy("this")
    private ApkAssets[] mApkAssets;

    @GuardedBy("this")
    private int mNumRefs;

    @GuardedBy("this")
    private long mObject;

    @GuardedBy("this")
    private final long[] mOffsets;

    @GuardedBy("this")
    private boolean mOpen;

    @GuardedBy("this")
    private HashMap<Long, RuntimeException> mRefStacks;

    @GuardedBy("this")
    private final TypedValue mValue;
    private static final Object sSync = new Object();
    private static final ApkAssets[] sEmptyApkAssets = new ApkAssets[0];

    @GuardedBy("sSync")
    static AssetManager sSystem = null;

    @GuardedBy("sSync")
    private static ApkAssets[] sSystemApkAssets = new ApkAssets[0];

    public final class AssetInputStream extends InputStream {
        private long mAssetNativePtr;
        private long mLength;
        private long mMarkPos;

        private AssetInputStream(long j) {
            this.mAssetNativePtr = j;
            this.mLength = AssetManager.nativeAssetGetLength(j);
        }

        private void ensureOpen() {
            if (this.mAssetNativePtr == 0) {
                throw new IllegalStateException("AssetInputStream is closed");
            }
        }

        @Override // java.io.InputStream
        public final int available() throws IOException {
            ensureOpen();
            long jNativeAssetGetRemainingLength = AssetManager.nativeAssetGetRemainingLength(this.mAssetNativePtr);
            if (jNativeAssetGetRemainingLength > 2147483647L) {
                return Integer.MAX_VALUE;
            }
            return (int) jNativeAssetGetRemainingLength;
        }

        @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
        public final void close() throws IOException {
            if (this.mAssetNativePtr != 0) {
                AssetManager.nativeAssetDestroy(this.mAssetNativePtr);
                this.mAssetNativePtr = 0L;
                synchronized (AssetManager.this) {
                    AssetManager.this.decRefsLocked(hashCode());
                }
            }
        }

        protected void finalize() throws Throwable {
            close();
        }

        public final int getAssetInt() {
            throw new UnsupportedOperationException();
        }

        public final long getNativeAsset() {
            return this.mAssetNativePtr;
        }

        @Override // java.io.InputStream
        public final void mark(int i) {
            ensureOpen();
            this.mMarkPos = AssetManager.nativeAssetSeek(this.mAssetNativePtr, 0L, 0);
        }

        @Override // java.io.InputStream
        public final boolean markSupported() {
            return true;
        }

        @Override // java.io.InputStream
        public final int read() throws IOException {
            ensureOpen();
            return AssetManager.nativeAssetReadChar(this.mAssetNativePtr);
        }

        @Override // java.io.InputStream
        public final int read(byte[] bArr) throws IOException {
            ensureOpen();
            Preconditions.checkNotNull(bArr, "b");
            return AssetManager.nativeAssetRead(this.mAssetNativePtr, bArr, 0, bArr.length);
        }

        @Override // java.io.InputStream
        public final int read(byte[] bArr, int i, int i2) throws IOException {
            ensureOpen();
            Preconditions.checkNotNull(bArr, "b");
            return AssetManager.nativeAssetRead(this.mAssetNativePtr, bArr, i, i2);
        }

        @Override // java.io.InputStream
        public final void reset() throws IOException {
            ensureOpen();
            AssetManager.nativeAssetSeek(this.mAssetNativePtr, this.mMarkPos, -1);
        }

        @Override // java.io.InputStream
        public final long skip(long j) throws IOException {
            ensureOpen();
            long jNativeAssetSeek = AssetManager.nativeAssetSeek(this.mAssetNativePtr, 0L, 0);
            if (jNativeAssetSeek + j > this.mLength) {
                j = this.mLength - jNativeAssetSeek;
            }
            if (j > 0) {
                AssetManager.nativeAssetSeek(this.mAssetNativePtr, j, 0);
            }
            return j;
        }
    }

    public static class Builder {
        private ArrayList<ApkAssets> mUserApkAssets = new ArrayList<>();

        public Builder addApkAssets(ApkAssets apkAssets) {
            this.mUserApkAssets.add(apkAssets);
            return this;
        }

        public AssetManager build() {
            ApkAssets[] apkAssets = AssetManager.getSystem().getApkAssets();
            ApkAssets[] apkAssetsArr = new ApkAssets[apkAssets.length + this.mUserApkAssets.size()];
            boolean z = false;
            System.arraycopy(apkAssets, 0, apkAssetsArr, 0, apkAssets.length);
            int size = this.mUserApkAssets.size();
            for (int i = 0; i < size; i++) {
                apkAssetsArr[apkAssets.length + i] = this.mUserApkAssets.get(i);
            }
            AssetManager assetManager = new AssetManager(z);
            assetManager.mApkAssets = apkAssetsArr;
            AssetManager.nativeSetApkAssets(assetManager.mObject, apkAssetsArr, false);
            return assetManager;
        }
    }

    private static /* synthetic */ void $closeResource(Throwable th, AutoCloseable autoCloseable) throws Exception {
        if (th == null) {
            autoCloseable.close();
            return;
        }
        try {
            autoCloseable.close();
        } catch (Throwable th2) {
            th.addSuppressed(th2);
        }
    }

    public AssetManager() {
        ApkAssets[] apkAssetsArr;
        this.mValue = new TypedValue();
        this.mOffsets = new long[2];
        this.mOpen = true;
        this.mNumRefs = 1;
        synchronized (sSync) {
            createSystemAssetsInZygoteLocked();
            apkAssetsArr = sSystemApkAssets;
        }
        this.mObject = nativeCreate();
        setApkAssets(apkAssetsArr, false);
    }

    private AssetManager(boolean z) {
        this.mValue = new TypedValue();
        this.mOffsets = new long[2];
        this.mOpen = true;
        this.mNumRefs = 1;
        this.mObject = nativeCreate();
    }

    private int addAssetPathInternal(String str, boolean z, boolean z2) {
        Preconditions.checkNotNull(str, "path");
        synchronized (this) {
            ensureOpenLocked();
            int length = this.mApkAssets.length;
            for (int i = 0; i < length; i++) {
                if (this.mApkAssets[i].getAssetPath().equals(str)) {
                    return i + 1;
                }
            }
            try {
                ApkAssets apkAssetsLoadOverlayFromPath = z ? ApkAssets.loadOverlayFromPath("/data/resource-cache/" + str.substring(1).replace('/', '@') + "@idmap", false) : ApkAssets.loadFromPath(str, false, z2);
                int i2 = length + 1;
                this.mApkAssets = (ApkAssets[]) Arrays.copyOf(this.mApkAssets, i2);
                this.mApkAssets[length] = apkAssetsLoadOverlayFromPath;
                nativeSetApkAssets(this.mObject, this.mApkAssets, true);
                invalidateCachesLocked(-1);
                return i2;
            } catch (IOException unused) {
                return 0;
            }
        }
    }

    @GuardedBy("sSync")
    private static void createSystemAssetsInZygoteLocked() {
        if (sSystem != null) {
            return;
        }
        nativeVerifySystemIdmaps();
        try {
            ArrayList arrayList = new ArrayList();
            arrayList.add(ApkAssets.loadFromPath(FRAMEWORK_APK_PATH, true));
            loadStaticRuntimeOverlays(arrayList);
            sSystemApkAssetsSet = new ArraySet<>(arrayList);
            sSystemApkAssets = (ApkAssets[]) arrayList.toArray(new ApkAssets[arrayList.size()]);
            sSystem = new AssetManager(true);
            sSystem.setApkAssets(sSystemApkAssets, false);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create system AssetManager", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @GuardedBy("this")
    public void decRefsLocked(long j) {
        this.mNumRefs--;
        if (this.mNumRefs != 0 || this.mObject == 0) {
            return;
        }
        nativeDestroy(this.mObject);
        this.mObject = 0L;
        this.mApkAssets = sEmptyApkAssets;
    }

    @GuardedBy("this")
    private void ensureOpenLocked() {
        if (!this.mOpen) {
            throw new RuntimeException("AssetManager has been closed");
        }
    }

    @GuardedBy("this")
    private void ensureValidLocked() {
        if (this.mObject == 0) {
            throw new RuntimeException("AssetManager has been destroyed");
        }
    }

    public static native String getAssetAllocations();

    public static native int getGlobalAssetCount();

    public static native int getGlobalAssetManagerCount();

    public static AssetManager getSystem() {
        AssetManager assetManager;
        synchronized (sSync) {
            createSystemAssetsInZygoteLocked();
            assetManager = sSystem;
        }
        return assetManager;
    }

    @GuardedBy("this")
    private void incRefsLocked(long j) {
        this.mNumRefs++;
    }

    private void invalidateCachesLocked(int i) {
    }

    /* JADX WARN: Code duplicated, block: B:22:0x0050 A[Catch: all -> 0x0054, Throwable -> 0x0056, TRY_ENTER, TryCatch #4 {, blocks: (B:5:0x0012, B:11:0x003b, B:22:0x0050, B:23:0x0053), top: B:36:0x0012, outer: #0 }] */
    /* JADX WARN: Code duplicated, block: B:46:? A[Catch: all -> 0x0054, Throwable -> 0x0056, SYNTHETIC, TRY_LEAVE, TryCatch #4 {, blocks: (B:5:0x0012, B:11:0x003b, B:22:0x0050, B:23:0x0053), top: B:36:0x0012, outer: #0 }] */
    private static void loadStaticRuntimeOverlays(ArrayList<ApkAssets> arrayList) throws IOException {
        Throwable th;
        Throwable th2;
        try {
            FileInputStream fileInputStream = new FileInputStream("/data/resource-cache/overlays.list");
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                try {
                    FileLock fileLockLock = fileInputStream.getChannel().lock(0L, Long.MAX_VALUE, true);
                    while (true) {
                        try {
                            String line = bufferedReader.readLine();
                            if (line == null) {
                                break;
                            } else {
                                arrayList.add(ApkAssets.loadOverlayFromPath(line.split(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER)[1], true));
                            }
                        } catch (Throwable th3) {
                            try {
                                throw th3;
                            } catch (Throwable th4) {
                                th = th3;
                                th2 = th4;
                                if (fileLockLock != null) {
                                    throw th2;
                                }
                                $closeResource(th, fileLockLock);
                                throw th2;
                            }
                        }
                    }
                    if (fileLockLock != null) {
                        $closeResource(null, fileLockLock);
                    }
                    $closeResource(null, bufferedReader);
                    IoUtils.closeQuietly(fileInputStream);
                } catch (Throwable th5) {
                    $closeResource(null, bufferedReader);
                    throw th5;
                }
            } catch (Throwable th6) {
                IoUtils.closeQuietly(fileInputStream);
                throw th6;
            }
        } catch (FileNotFoundException unused) {
            Log.i(TAG, "no overlays.list file found");
        }
    }

    private static native void nativeApplyStyle(long j, long j2, int i, int i2, long j3, int[] iArr, long j4, long j5);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeAssetDestroy(long j);

    /* JADX INFO: Access modifiers changed from: private */
    public static native long nativeAssetGetLength(long j);

    /* JADX INFO: Access modifiers changed from: private */
    public static native long nativeAssetGetRemainingLength(long j);

    /* JADX INFO: Access modifiers changed from: private */
    public static native int nativeAssetRead(long j, byte[] bArr, int i, int i2);

    /* JADX INFO: Access modifiers changed from: private */
    public static native int nativeAssetReadChar(long j);

    /* JADX INFO: Access modifiers changed from: private */
    public static native long nativeAssetSeek(long j, long j2, int i);

    private static native long nativeCreate();

    private static native void nativeDestroy(long j);

    private static native SparseArray<String> nativeGetAssignedPackageIdentifiers(long j);

    private static native String[] nativeGetLocales(long j, boolean z);

    private static native int nativeGetResourceArray(long j, int i, int[] iArr);

    private static native int nativeGetResourceArraySize(long j, int i);

    private static native int nativeGetResourceBagValue(long j, int i, int i2, TypedValue typedValue);

    private static native String nativeGetResourceEntryName(long j, int i);

    private static native int nativeGetResourceIdentifier(long j, String str, String str2, String str3);

    private static native int[] nativeGetResourceIntArray(long j, int i);

    private static native String nativeGetResourceName(long j, int i);

    private static native String nativeGetResourcePackageName(long j, int i);

    private static native String[] nativeGetResourceStringArray(long j, int i);

    private static native int[] nativeGetResourceStringArrayInfo(long j, int i);

    private static native String nativeGetResourceTypeName(long j, int i);

    private static native int nativeGetResourceValue(long j, int i, short s, TypedValue typedValue, boolean z);

    private static native Configuration[] nativeGetSizeConfigurations(long j);

    private static native int[] nativeGetStyleAttributes(long j, int i);

    private static native String[] nativeList(long j, String str) throws IOException;

    private static native long nativeOpenAsset(long j, String str, int i);

    private static native ParcelFileDescriptor nativeOpenAssetFd(long j, String str, long[] jArr) throws IOException;

    private static native long nativeOpenNonAsset(long j, int i, String str, int i2);

    private static native ParcelFileDescriptor nativeOpenNonAssetFd(long j, int i, String str, long[] jArr) throws IOException;

    private static native long nativeOpenXmlAsset(long j, int i, String str);

    private static native boolean nativeResolveAttrs(long j, long j2, int i, int i2, int[] iArr, int[] iArr2, int[] iArr3, int[] iArr4);

    private static native boolean nativeRetrieveAttributes(long j, long j2, int[] iArr, int[] iArr2, int[] iArr3);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeSetApkAssets(long j, ApkAssets[] apkAssetsArr, boolean z);

    private static native void nativeSetConfiguration(long j, int i, int i2, String str, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, int i13, int i14, int i15, int i16, int i17);

    private static native void nativeThemeApplyStyle(long j, long j2, int i, boolean z);

    static native void nativeThemeClear(long j);

    static native void nativeThemeCopy(long j, long j2);

    private static native long nativeThemeCreate(long j);

    private static native void nativeThemeDestroy(long j);

    private static native void nativeThemeDump(long j, long j2, int i, String str, String str2);

    private static native int nativeThemeGetAttributeValue(long j, long j2, int i, TypedValue typedValue, boolean z);

    static native int nativeThemeGetChangingConfigurations(long j);

    private static native void nativeVerifySystemIdmaps();

    @Deprecated
    public int addAssetPath(String str) {
        return addAssetPathInternal(str, false, false);
    }

    @Deprecated
    public int addAssetPathAsSharedLibrary(String str) {
        return addAssetPathInternal(str, false, true);
    }

    @Deprecated
    public int addOverlayPath(String str) {
        return addAssetPathInternal(str, true, false);
    }

    void applyStyle(long j, int i, int i2, XmlBlock.Parser parser, int[] iArr, long j2, long j3) {
        Preconditions.checkNotNull(iArr, "inAttrs");
        synchronized (this) {
            ensureValidLocked();
            nativeApplyStyle(this.mObject, j, i, i2, parser != null ? parser.mParseState : 0L, iArr, j2, j3);
        }
    }

    void applyStyleToTheme(long j, int i, boolean z) {
        synchronized (this) {
            ensureValidLocked();
            nativeThemeApplyStyle(this.mObject, j, i, z);
        }
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        synchronized (this) {
            if (this.mOpen) {
                this.mOpen = false;
                decRefsLocked(hashCode());
            }
        }
    }

    long createTheme() {
        long jNativeThemeCreate;
        synchronized (this) {
            ensureValidLocked();
            jNativeThemeCreate = nativeThemeCreate(this.mObject);
            incRefsLocked(jNativeThemeCreate);
        }
        return jNativeThemeCreate;
    }

    void dumpTheme(long j, int i, String str, String str2) {
        synchronized (this) {
            ensureValidLocked();
            nativeThemeDump(this.mObject, j, i, str, str2);
        }
    }

    protected void finalize() throws Throwable {
        synchronized (this) {
            if (this.mObject != 0) {
                nativeDestroy(this.mObject);
                this.mObject = 0L;
            }
        }
    }

    public int findCookieForPath(String str) {
        Preconditions.checkNotNull(str, "path");
        synchronized (this) {
            ensureValidLocked();
            int length = this.mApkAssets.length;
            for (int i = 0; i < length; i++) {
                if (str.equals(this.mApkAssets[i].getAssetPath())) {
                    return i + 1;
                }
            }
            return 0;
        }
    }

    public ApkAssets[] getApkAssets() {
        synchronized (this) {
            if (!this.mOpen) {
                return sEmptyApkAssets;
            }
            return this.mApkAssets;
        }
    }

    public SparseArray<String> getAssignedPackageIdentifiers() {
        SparseArray<String> sparseArrayNativeGetAssignedPackageIdentifiers;
        synchronized (this) {
            ensureValidLocked();
            sparseArrayNativeGetAssignedPackageIdentifiers = nativeGetAssignedPackageIdentifiers(this.mObject);
        }
        return sparseArrayNativeGetAssignedPackageIdentifiers;
    }

    public String[] getLocales() {
        String[] strArrNativeGetLocales;
        synchronized (this) {
            ensureValidLocked();
            strArrNativeGetLocales = nativeGetLocales(this.mObject, false);
        }
        return strArrNativeGetLocales;
    }

    public String[] getNonSystemLocales() {
        String[] strArrNativeGetLocales;
        synchronized (this) {
            ensureValidLocked();
            strArrNativeGetLocales = nativeGetLocales(this.mObject, true);
        }
        return strArrNativeGetLocales;
    }

    CharSequence getPooledStringForCookie(int i, int i2) {
        return getApkAssets()[i - 1].getStringFromPool(i2);
    }

    int getResourceArray(int i, int[] iArr) {
        int iNativeGetResourceArray;
        Preconditions.checkNotNull(iArr, "outData");
        synchronized (this) {
            ensureValidLocked();
            iNativeGetResourceArray = nativeGetResourceArray(this.mObject, i, iArr);
        }
        return iNativeGetResourceArray;
    }

    int getResourceArraySize(int i) {
        int iNativeGetResourceArraySize;
        synchronized (this) {
            ensureValidLocked();
            iNativeGetResourceArraySize = nativeGetResourceArraySize(this.mObject, i);
        }
        return iNativeGetResourceArraySize;
    }

    CharSequence getResourceBagText(int i, int i2) {
        synchronized (this) {
            ensureValidLocked();
            TypedValue typedValue = this.mValue;
            int iNativeGetResourceBagValue = nativeGetResourceBagValue(this.mObject, i, i2, typedValue);
            if (iNativeGetResourceBagValue <= 0) {
                return null;
            }
            typedValue.changingConfigurations = ActivityInfo.activityInfoConfigNativeToJava(typedValue.changingConfigurations);
            if (typedValue.type == 3) {
                return this.mApkAssets[iNativeGetResourceBagValue - 1].getStringFromPool(typedValue.data);
            }
            return typedValue.coerceToString();
        }
    }

    String getResourceEntryName(int i) {
        String strNativeGetResourceEntryName;
        synchronized (this) {
            ensureValidLocked();
            strNativeGetResourceEntryName = nativeGetResourceEntryName(this.mObject, i);
        }
        return strNativeGetResourceEntryName;
    }

    int getResourceIdentifier(String str, String str2, String str3) {
        int iNativeGetResourceIdentifier;
        synchronized (this) {
            ensureValidLocked();
            iNativeGetResourceIdentifier = nativeGetResourceIdentifier(this.mObject, str, str2, str3);
        }
        return iNativeGetResourceIdentifier;
    }

    int[] getResourceIntArray(int i) {
        int[] iArrNativeGetResourceIntArray;
        synchronized (this) {
            ensureValidLocked();
            iArrNativeGetResourceIntArray = nativeGetResourceIntArray(this.mObject, i);
        }
        return iArrNativeGetResourceIntArray;
    }

    String getResourceName(int i) {
        String strNativeGetResourceName;
        synchronized (this) {
            ensureValidLocked();
            strNativeGetResourceName = nativeGetResourceName(this.mObject, i);
        }
        return strNativeGetResourceName;
    }

    String getResourcePackageName(int i) {
        String strNativeGetResourcePackageName;
        synchronized (this) {
            ensureValidLocked();
            strNativeGetResourcePackageName = nativeGetResourcePackageName(this.mObject, i);
        }
        return strNativeGetResourcePackageName;
    }

    String[] getResourceStringArray(int i) {
        String[] strArrNativeGetResourceStringArray;
        synchronized (this) {
            ensureValidLocked();
            strArrNativeGetResourceStringArray = nativeGetResourceStringArray(this.mObject, i);
        }
        return strArrNativeGetResourceStringArray;
    }

    CharSequence getResourceText(int i) {
        synchronized (this) {
            TypedValue typedValue = this.mValue;
            if (!getResourceValue(i, 0, typedValue, true)) {
                return null;
            }
            return typedValue.coerceToString();
        }
    }

    CharSequence[] getResourceTextArray(int i) {
        synchronized (this) {
            ensureValidLocked();
            int[] iArrNativeGetResourceStringArrayInfo = nativeGetResourceStringArrayInfo(this.mObject, i);
            if (iArrNativeGetResourceStringArrayInfo == null) {
                return null;
            }
            int length = iArrNativeGetResourceStringArrayInfo.length;
            CharSequence[] charSequenceArr = new CharSequence[length / 2];
            int i2 = 0;
            int i3 = 0;
            while (i2 < length) {
                int i4 = iArrNativeGetResourceStringArrayInfo[i2];
                int i5 = iArrNativeGetResourceStringArrayInfo[i2 + 1];
                charSequenceArr[i3] = (i5 < 0 || i4 <= 0) ? null : this.mApkAssets[i4 - 1].getStringFromPool(i5);
                i2 += 2;
                i3++;
            }
            return charSequenceArr;
        }
    }

    String getResourceTypeName(int i) {
        String strNativeGetResourceTypeName;
        synchronized (this) {
            ensureValidLocked();
            strNativeGetResourceTypeName = nativeGetResourceTypeName(this.mObject, i);
        }
        return strNativeGetResourceTypeName;
    }

    boolean getResourceValue(int i, int i2, TypedValue typedValue, boolean z) {
        Preconditions.checkNotNull(typedValue, "outValue");
        synchronized (this) {
            ensureValidLocked();
            int iNativeGetResourceValue = nativeGetResourceValue(this.mObject, i, (short) i2, typedValue, z);
            if (iNativeGetResourceValue <= 0) {
                return false;
            }
            typedValue.changingConfigurations = ActivityInfo.activityInfoConfigNativeToJava(typedValue.changingConfigurations);
            if (typedValue.type == 3) {
                typedValue.string = this.mApkAssets[iNativeGetResourceValue - 1].getStringFromPool(typedValue.data);
            }
            return true;
        }
    }

    Configuration[] getSizeConfigurations() {
        Configuration[] configurationArrNativeGetSizeConfigurations;
        synchronized (this) {
            ensureValidLocked();
            configurationArrNativeGetSizeConfigurations = nativeGetSizeConfigurations(this.mObject);
        }
        return configurationArrNativeGetSizeConfigurations;
    }

    int[] getStyleAttributes(int i) {
        int[] iArrNativeGetStyleAttributes;
        synchronized (this) {
            ensureValidLocked();
            iArrNativeGetStyleAttributes = nativeGetStyleAttributes(this.mObject, i);
        }
        return iArrNativeGetStyleAttributes;
    }

    boolean getThemeValue(long j, int i, TypedValue typedValue, boolean z) {
        Preconditions.checkNotNull(typedValue, "outValue");
        synchronized (this) {
            ensureValidLocked();
            int iNativeThemeGetAttributeValue = nativeThemeGetAttributeValue(this.mObject, j, i, typedValue, z);
            if (iNativeThemeGetAttributeValue <= 0) {
                return false;
            }
            typedValue.changingConfigurations = ActivityInfo.activityInfoConfigNativeToJava(typedValue.changingConfigurations);
            if (typedValue.type == 3) {
                typedValue.string = this.mApkAssets[iNativeThemeGetAttributeValue - 1].getStringFromPool(typedValue.data);
            }
            return true;
        }
    }

    public boolean isUpToDate() {
        for (ApkAssets apkAssets : getApkAssets()) {
            if (!apkAssets.isUpToDate()) {
                return false;
            }
        }
        return true;
    }

    public String[] list(String str) throws IOException {
        String[] strArrNativeList;
        Preconditions.checkNotNull(str, "path");
        synchronized (this) {
            ensureValidLocked();
            strArrNativeList = nativeList(this.mObject, str);
        }
        return strArrNativeList;
    }

    public InputStream open(String str) throws IOException {
        return open(str, 2);
    }

    public InputStream open(String str, int i) throws IOException {
        AssetInputStream assetInputStream;
        Preconditions.checkNotNull(str, "fileName");
        synchronized (this) {
            ensureOpenLocked();
            long jNativeOpenAsset = nativeOpenAsset(this.mObject, str, i);
            if (jNativeOpenAsset == 0) {
                throw new FileNotFoundException("Asset file: " + str);
            }
            assetInputStream = new AssetInputStream(jNativeOpenAsset);
            incRefsLocked(assetInputStream.hashCode());
        }
        return assetInputStream;
    }

    public AssetFileDescriptor openFd(String str) throws IOException {
        AssetFileDescriptor assetFileDescriptor;
        Preconditions.checkNotNull(str, "fileName");
        synchronized (this) {
            ensureOpenLocked();
            ParcelFileDescriptor parcelFileDescriptorNativeOpenAssetFd = nativeOpenAssetFd(this.mObject, str, this.mOffsets);
            if (parcelFileDescriptorNativeOpenAssetFd == null) {
                throw new FileNotFoundException("Asset file: " + str);
            }
            assetFileDescriptor = new AssetFileDescriptor(parcelFileDescriptorNativeOpenAssetFd, this.mOffsets[0], this.mOffsets[1]);
        }
        return assetFileDescriptor;
    }

    public InputStream openNonAsset(int i, String str) throws IOException {
        return openNonAsset(i, str, 2);
    }

    public InputStream openNonAsset(int i, String str, int i2) throws IOException {
        AssetInputStream assetInputStream;
        Preconditions.checkNotNull(str, "fileName");
        synchronized (this) {
            ensureOpenLocked();
            long jNativeOpenNonAsset = nativeOpenNonAsset(this.mObject, i, str, i2);
            if (jNativeOpenNonAsset == 0) {
                throw new FileNotFoundException("Asset absolute file: " + str);
            }
            assetInputStream = new AssetInputStream(jNativeOpenNonAsset);
            incRefsLocked(assetInputStream.hashCode());
        }
        return assetInputStream;
    }

    public InputStream openNonAsset(String str) throws IOException {
        return openNonAsset(0, str, 2);
    }

    public InputStream openNonAsset(String str, int i) throws IOException {
        return openNonAsset(0, str, i);
    }

    public AssetFileDescriptor openNonAssetFd(int i, String str) throws IOException {
        AssetFileDescriptor assetFileDescriptor;
        Preconditions.checkNotNull(str, "fileName");
        synchronized (this) {
            ensureOpenLocked();
            ParcelFileDescriptor parcelFileDescriptorNativeOpenNonAssetFd = nativeOpenNonAssetFd(this.mObject, i, str, this.mOffsets);
            if (parcelFileDescriptorNativeOpenNonAssetFd == null) {
                throw new FileNotFoundException("Asset absolute file: " + str);
            }
            assetFileDescriptor = new AssetFileDescriptor(parcelFileDescriptorNativeOpenNonAssetFd, this.mOffsets[0], this.mOffsets[1]);
        }
        return assetFileDescriptor;
    }

    public AssetFileDescriptor openNonAssetFd(String str) throws IOException {
        return openNonAssetFd(0, str);
    }

    XmlBlock openXmlBlockAsset(int i, String str) throws IOException {
        XmlBlock xmlBlock;
        Preconditions.checkNotNull(str, "fileName");
        synchronized (this) {
            ensureOpenLocked();
            long jNativeOpenXmlAsset = nativeOpenXmlAsset(this.mObject, i, str);
            if (jNativeOpenXmlAsset == 0) {
                throw new FileNotFoundException("Asset XML file: " + str);
            }
            xmlBlock = new XmlBlock(this, jNativeOpenXmlAsset);
            incRefsLocked(xmlBlock.hashCode());
        }
        return xmlBlock;
    }

    XmlBlock openXmlBlockAsset(String str) throws IOException {
        return openXmlBlockAsset(0, str);
    }

    /* JADX WARN: Code duplicated, block: B:15:0x001f  */
    public XmlResourceParser openXmlResourceParser(int i, String str) throws Exception {
        XmlBlock xmlBlockOpenXmlBlockAsset = openXmlBlockAsset(i, str);
        Throwable th = null;
        try {
            XmlResourceParser xmlResourceParserNewParser = xmlBlockOpenXmlBlockAsset.newParser();
            if (xmlResourceParserNewParser == null) {
                throw new AssertionError("block.newParser() returned a null parser");
            }
            if (xmlBlockOpenXmlBlockAsset != null) {
                $closeResource(null, xmlBlockOpenXmlBlockAsset);
            }
            return xmlResourceParserNewParser;
        } catch (Throwable th2) {
            if (xmlBlockOpenXmlBlockAsset != null) {
                $closeResource(th, xmlBlockOpenXmlBlockAsset);
            }
            throw th2;
        }
        if (xmlBlockOpenXmlBlockAsset != null) {
            $closeResource(th, xmlBlockOpenXmlBlockAsset);
        }
        throw th2;
    }

    public XmlResourceParser openXmlResourceParser(String str) throws IOException {
        return openXmlResourceParser(0, str);
    }

    void releaseTheme(long j) {
        synchronized (this) {
            nativeThemeDestroy(j);
            decRefsLocked(j);
        }
    }

    boolean resolveAttrs(long j, int i, int i2, int[] iArr, int[] iArr2, int[] iArr3, int[] iArr4) {
        boolean zNativeResolveAttrs;
        Preconditions.checkNotNull(iArr2, "inAttrs");
        Preconditions.checkNotNull(iArr3, "outValues");
        Preconditions.checkNotNull(iArr4, "outIndices");
        synchronized (this) {
            ensureValidLocked();
            zNativeResolveAttrs = nativeResolveAttrs(this.mObject, j, i, i2, iArr, iArr2, iArr3, iArr4);
        }
        return zNativeResolveAttrs;
    }

    boolean retrieveAttributes(XmlBlock.Parser parser, int[] iArr, int[] iArr2, int[] iArr3) {
        boolean zNativeRetrieveAttributes;
        Preconditions.checkNotNull(parser, "parser");
        Preconditions.checkNotNull(iArr, "inAttrs");
        Preconditions.checkNotNull(iArr2, "outValues");
        Preconditions.checkNotNull(iArr3, "outIndices");
        synchronized (this) {
            ensureValidLocked();
            zNativeRetrieveAttributes = nativeRetrieveAttributes(this.mObject, parser.mParseState, iArr, iArr2, iArr3);
        }
        return zNativeRetrieveAttributes;
    }

    public void setApkAssets(ApkAssets[] apkAssetsArr, boolean z) {
        Preconditions.checkNotNull(apkAssetsArr, "apkAssets");
        ApkAssets[] apkAssetsArr2 = new ApkAssets[sSystemApkAssets.length + apkAssetsArr.length];
        System.arraycopy(sSystemApkAssets, 0, apkAssetsArr2, 0, sSystemApkAssets.length);
        int length = sSystemApkAssets.length;
        for (ApkAssets apkAssets : apkAssetsArr) {
            if (!sSystemApkAssetsSet.contains(apkAssets)) {
                apkAssetsArr2[length] = apkAssets;
                length++;
            }
        }
        if (length != apkAssetsArr2.length) {
            apkAssetsArr2 = (ApkAssets[]) Arrays.copyOf(apkAssetsArr2, length);
        }
        synchronized (this) {
            ensureOpenLocked();
            this.mApkAssets = apkAssetsArr2;
            nativeSetApkAssets(this.mObject, this.mApkAssets, z);
            if (z) {
                invalidateCachesLocked(-1);
            }
        }
    }

    public void setConfiguration(int i, int i2, String str, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, int i13, int i14, int i15, int i16, int i17) {
        synchronized (this) {
            ensureValidLocked();
            nativeSetConfiguration(this.mObject, i, i2, str, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14, i15, i16, i17);
        }
    }

    void xmlBlockGone(int i) {
        synchronized (this) {
            decRefsLocked(i);
        }
    }
}
