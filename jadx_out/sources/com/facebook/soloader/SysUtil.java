package com.facebook.soloader;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.backup.FullBackup;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Parcel;
import android.os.Process;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.util.Log;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

/* JADX INFO: loaded from: classes3.dex */
public final class SysUtil {
    private static final byte APK_SIGNATURE_VERSION = 1;
    private static final String TAG = "SysUtil";

    @DoNotOptimize
    @TargetApi(21)
    private static final class LollipopSysdeps {
        private LollipopSysdeps() {
        }

        @DoNotOptimize
        public static void fallocateIfSupported(FileDescriptor fileDescriptor, long j) throws IOException {
            try {
                Os.posix_fallocate(fileDescriptor, 0L, j);
            } catch (ErrnoException e) {
                if (e.errno != OsConstants.EOPNOTSUPP && e.errno != OsConstants.ENOSYS && e.errno != OsConstants.EINVAL) {
                    throw new IOException(e.toString(), e);
                }
            }
        }

        @DoNotOptimize
        public static String[] getSupportedAbis() {
            String[] strArr = Build.SUPPORTED_ABIS;
            TreeSet treeSet = new TreeSet();
            try {
                if (is64Bit()) {
                    treeSet.add(MinElf.ISA.AARCH64.toString());
                    treeSet.add(MinElf.ISA.X86_64.toString());
                } else {
                    treeSet.add(MinElf.ISA.ARM.toString());
                    treeSet.add(MinElf.ISA.X86.toString());
                }
                ArrayList arrayList = new ArrayList();
                for (String str : strArr) {
                    if (treeSet.contains(str)) {
                        arrayList.add(str);
                    }
                }
                return (String[]) arrayList.toArray(new String[arrayList.size()]);
            } catch (ErrnoException e) {
                Log.e(SysUtil.TAG, String.format("Could not read /proc/self/exe. Falling back to default ABI list: %s. errno: %d Err msg: %s", Arrays.toString(strArr), Integer.valueOf(e.errno), e.getMessage()));
                return Build.SUPPORTED_ABIS;
            }
        }

        @DoNotOptimize
        public static boolean is64Bit() throws ErrnoException {
            return Os.readlink("/proc/self/exe").contains("64");
        }
    }

    @DoNotOptimize
    @TargetApi(23)
    private static final class MarshmallowSysdeps {
        private MarshmallowSysdeps() {
        }

        @DoNotOptimize
        public static String[] getSupportedAbis() {
            String[] strArr = Build.SUPPORTED_ABIS;
            TreeSet treeSet = new TreeSet();
            if (is64Bit()) {
                treeSet.add(MinElf.ISA.AARCH64.toString());
                treeSet.add(MinElf.ISA.X86_64.toString());
            } else {
                treeSet.add(MinElf.ISA.ARM.toString());
                treeSet.add(MinElf.ISA.X86.toString());
            }
            ArrayList arrayList = new ArrayList();
            for (String str : strArr) {
                if (treeSet.contains(str)) {
                    arrayList.add(str);
                }
            }
            return (String[]) arrayList.toArray(new String[arrayList.size()]);
        }

        @DoNotOptimize
        public static boolean is64Bit() {
            return Process.is64Bit();
        }
    }

    static int copyBytes(RandomAccessFile randomAccessFile, InputStream inputStream, int i, byte[] bArr) throws IOException {
        int i2 = 0;
        while (i2 < i) {
            int i3 = inputStream.read(bArr, 0, Math.min(bArr.length, i - i2));
            if (i3 == -1) {
                break;
            }
            randomAccessFile.write(bArr, 0, i3);
            i2 += i3;
        }
        return i2;
    }

    public static void deleteOrThrow(File file) throws IOException {
        if (file.delete()) {
            return;
        }
        throw new IOException("could not delete file " + file);
    }

    public static void dumbDeleteRecursive(File file) throws IOException {
        if (file.isDirectory()) {
            File[] fileArrListFiles = file.listFiles();
            if (fileArrListFiles == null) {
                return;
            }
            for (File file2 : fileArrListFiles) {
                dumbDeleteRecursive(file2);
            }
        }
        if (file.delete() || !file.exists()) {
            return;
        }
        throw new IOException("could not delete: " + file);
    }

    public static void fallocateIfSupported(FileDescriptor fileDescriptor, long j) throws IOException {
        if (Build.VERSION.SDK_INT >= 21) {
            LollipopSysdeps.fallocateIfSupported(fileDescriptor, j);
        }
    }

    public static int findAbiScore(String[] strArr, String str) {
        for (int i = 0; i < strArr.length; i++) {
            if (strArr[i] != null && str.equals(strArr[i])) {
                return i;
            }
        }
        return -1;
    }

    static void fsyncRecursive(File file) throws IOException {
        if (file.isDirectory()) {
            File[] fileArrListFiles = file.listFiles();
            if (fileArrListFiles == null) {
                throw new IOException("cannot list directory " + file);
            }
            for (File file2 : fileArrListFiles) {
                fsyncRecursive(file2);
            }
            return;
        }
        if (file.getPath().endsWith("_lock")) {
            return;
        }
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, FullBackup.ROOT_TREE_TOKEN);
        Throwable th = null;
        try {
            randomAccessFile.getFD().sync();
            randomAccessFile.close();
        } catch (Throwable th2) {
            if (0 != 0) {
                try {
                    randomAccessFile.close();
                } catch (Throwable th3) {
                    th.addSuppressed(th3);
                }
            } else {
                randomAccessFile.close();
            }
            throw th2;
        }
    }

    public static int getAppVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        if (packageManager != null) {
            try {
                return packageManager.getPackageInfo(context.getPackageName(), 0).versionCode;
            } catch (PackageManager.NameNotFoundException | RuntimeException unused) {
            }
        }
        return 0;
    }

    public static String[] getSupportedAbis() {
        if (Build.VERSION.SDK_INT >= 23) {
            return MarshmallowSysdeps.getSupportedAbis();
        }
        return Build.VERSION.SDK_INT >= 21 ? LollipopSysdeps.getSupportedAbis() : new String[]{Build.CPU_ABI, Build.CPU_ABI2};
    }

    @SuppressLint({"CatchGeneralException"})
    public static boolean is64Bit() {
        if (Build.VERSION.SDK_INT >= 23) {
            return MarshmallowSysdeps.is64Bit();
        }
        if (Build.VERSION.SDK_INT < 21) {
            return false;
        }
        try {
            return LollipopSysdeps.is64Bit();
        } catch (Exception e) {
            Log.e(TAG, String.format("Could not read /proc/self/exe. Err msg: %s", e.getMessage()));
            return false;
        }
    }

    public static byte[] makeApkDepBlock(File file, Context context) throws IOException {
        File canonicalFile = file.getCanonicalFile();
        Parcel parcelObtain = Parcel.obtain();
        try {
            parcelObtain.writeByte((byte) 1);
            parcelObtain.writeString(canonicalFile.getPath());
            parcelObtain.writeLong(canonicalFile.lastModified());
            parcelObtain.writeInt(getAppVersionCode(context));
            return parcelObtain.marshall();
        } finally {
            parcelObtain.recycle();
        }
    }

    static void mkdirOrThrow(File file) throws IOException {
        if (file.mkdirs() || file.isDirectory()) {
            return;
        }
        throw new IOException("cannot mkdir: " + file);
    }
}
