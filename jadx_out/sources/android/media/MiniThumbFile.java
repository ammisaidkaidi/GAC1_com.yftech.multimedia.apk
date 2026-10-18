package android.media;

import android.app.backup.FullBackup;
import android.app.job.JobInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import com.android.internal.content.NativeLibraryHelper;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Hashtable;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class MiniThumbFile {
    public static final int BYTES_PER_MINTHUMB = 10000;
    private static final int HEADER_SIZE = 13;
    private static final int MINI_THUMB_DATA_FILE_VERSION = 4;
    private static final String TAG = "MiniThumbFile";
    private static final Hashtable<String, MiniThumbFile> sThumbFiles = new Hashtable<>();
    private FileChannel mChannel;
    private RandomAccessFile mMiniThumbFile;
    private Uri mUri;
    private ByteBuffer mBuffer = ByteBuffer.allocateDirect(10000);
    private ByteBuffer mEmptyBuffer = ByteBuffer.allocateDirect(10000);

    private MiniThumbFile(Uri uri) {
        this.mUri = uri;
    }

    public static synchronized MiniThumbFile instance(Uri uri) {
        MiniThumbFile miniThumbFile;
        String str = uri.getPathSegments().get(1);
        miniThumbFile = sThumbFiles.get(str);
        if (miniThumbFile == null) {
            miniThumbFile = new MiniThumbFile(Uri.parse("content://media/external/" + str + "/media"));
            sThumbFiles.put(str, miniThumbFile);
        }
        return miniThumbFile;
    }

    private RandomAccessFile miniThumbDataFile() {
        if (this.mMiniThumbFile == null) {
            removeOldFile();
            String strRandomAccessFilePath = randomAccessFilePath(4);
            File parentFile = new File(strRandomAccessFilePath).getParentFile();
            if (!parentFile.isDirectory() && !parentFile.mkdirs()) {
                Log.e(TAG, "Unable to create .thumbnails directory " + parentFile.toString());
            }
            File file = new File(strRandomAccessFilePath);
            try {
                try {
                    this.mMiniThumbFile = new RandomAccessFile(file, "rw");
                } catch (IOException unused) {
                }
            } catch (IOException unused2) {
                this.mMiniThumbFile = new RandomAccessFile(file, FullBackup.ROOT_TREE_TOKEN);
            }
            if (this.mMiniThumbFile != null) {
                this.mChannel = this.mMiniThumbFile.getChannel();
            }
        }
        return this.mMiniThumbFile;
    }

    private String randomAccessFilePath(int i) {
        return (Environment.getExternalStorageDirectory().toString() + "/DCIM/.thumbnails") + "/.thumbdata" + i + NativeLibraryHelper.CLEAR_ABI_OVERRIDE + this.mUri.hashCode();
    }

    private void removeOldFile() {
        File file = new File(randomAccessFilePath(3));
        if (file.exists()) {
            try {
                file.delete();
            } catch (SecurityException unused) {
            }
        }
    }

    public static synchronized void reset() {
        Iterator<MiniThumbFile> it = sThumbFiles.values().iterator();
        while (it.hasNext()) {
            it.next().deactivate();
        }
        sThumbFiles.clear();
    }

    public synchronized void deactivate() {
        if (this.mMiniThumbFile != null) {
            try {
                this.mMiniThumbFile.close();
                this.mMiniThumbFile = null;
            } catch (IOException unused) {
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:32:0x009f, code lost:
    
        if (r8 != null) goto L33;
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x00a1, code lost:
    
        r8.release();
     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x00ad, code lost:
    
        if (r8 != null) goto L33;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public synchronized void eraseMiniThumb(long j) {
        if (miniThumbDataFile() != null) {
            long j2 = JobInfo.MIN_BACKOFF_MILLIS * j;
            FileLock fileLock = null;
            try {
                try {
                    try {
                        this.mBuffer.clear();
                        this.mBuffer.limit(9);
                        FileLock fileLockLock = this.mChannel.lock(j2, JobInfo.MIN_BACKOFF_MILLIS, false);
                        try {
                            if (this.mChannel.read(this.mBuffer, j2) == 9) {
                                this.mBuffer.position(0);
                                if (this.mBuffer.get() == 1) {
                                    if (this.mBuffer.getLong() == 0) {
                                        Log.i(TAG, "no thumbnail for id " + j);
                                        if (fileLockLock != null) {
                                            try {
                                                fileLockLock.release();
                                            } catch (IOException unused) {
                                            }
                                        }
                                        return;
                                    }
                                    this.mChannel.write(this.mEmptyBuffer, j2);
                                }
                            }
                            if (fileLockLock != null) {
                                fileLockLock.release();
                            }
                        } catch (IOException e) {
                            e = e;
                            fileLock = fileLockLock;
                            Log.v(TAG, "Got exception checking file magic: ", e);
                        } catch (RuntimeException e2) {
                            e = e2;
                            fileLock = fileLockLock;
                            Log.e(TAG, "Got exception when reading magic, id = " + j + ", disk full or mount read-only? " + e.getClass());
                        } catch (Throwable th) {
                            th = th;
                            fileLock = fileLockLock;
                            if (fileLock != null) {
                                try {
                                    fileLock.release();
                                } catch (IOException unused2) {
                                }
                            }
                            throw th;
                        }
                    } catch (IOException unused3) {
                    }
                } catch (IOException e3) {
                    e = e3;
                } catch (RuntimeException e4) {
                    e = e4;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:30:0x007d A[Catch: IOException -> 0x0092, all -> 0x0096, PHI: r8
      0x007d: PHI (r8v4 java.nio.channels.FileLock) = (r8v3 java.nio.channels.FileLock), (r8v5 java.nio.channels.FileLock) binds: [B:29:0x007b, B:34:0x0089] A[DONT_GENERATE, DONT_INLINE], TRY_ENTER, TRY_LEAVE, TryCatch #4 {, blocks: (B:3:0x0001, B:13:0x0042, B:17:0x0049, B:37:0x008e, B:38:0x0091, B:30:0x007d), top: B:53:0x0001 }] */
    public synchronized long getMagic(long j) {
        if (miniThumbDataFile() != null) {
            long j2 = JobInfo.MIN_BACKOFF_MILLIS * j;
            FileLock fileLock = null;
            try {
                try {
                    try {
                        this.mBuffer.clear();
                        this.mBuffer.limit(9);
                        FileLock fileLockLock = this.mChannel.lock(j2, 9L, true);
                        try {
                            if (this.mChannel.read(this.mBuffer, j2) == 9) {
                                this.mBuffer.position(0);
                                if (this.mBuffer.get() == 1) {
                                    long j3 = this.mBuffer.getLong();
                                    if (fileLockLock != null) {
                                        try {
                                            fileLockLock.release();
                                        } catch (IOException unused) {
                                        }
                                    }
                                    return j3;
                                }
                            }
                            if (fileLockLock != null) {
                                fileLockLock.release();
                            }
                        } catch (IOException e) {
                            e = e;
                            fileLock = fileLockLock;
                            Log.v(TAG, "Got exception checking file magic: ", e);
                            if (fileLock != null) {
                                fileLock.release();
                            }
                        } catch (RuntimeException e2) {
                            e = e2;
                            fileLock = fileLockLock;
                            Log.e(TAG, "Got exception when reading magic, id = " + j + ", disk full or mount read-only? " + e.getClass());
                            if (fileLock != null) {
                                fileLock.release();
                            }
                        } catch (Throwable th) {
                            th = th;
                            fileLock = fileLockLock;
                            if (fileLock != null) {
                                try {
                                    fileLock.release();
                                } catch (IOException unused2) {
                                }
                            }
                            throw th;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                    }
                } catch (IOException e3) {
                    e = e3;
                } catch (RuntimeException e4) {
                    e = e4;
                }
            } catch (IOException unused3) {
            }
        }
        return 0L;
    }

    /* JADX WARN: Code duplicated, block: B:57:0x0060 A[EXC_TOP_SPLITTER, PHI: r0
      0x0060: PHI (r0v7 java.nio.channels.FileLock) = (r0v5 java.nio.channels.FileLock), (r0v6 java.nio.channels.FileLock), (r0v9 java.nio.channels.FileLock) binds: [B:39:0x0091, B:43:0x00b2, B:26:0x005e] A[DONT_GENERATE, DONT_INLINE], SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:60:0x00ba A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v0, types: [java.io.RandomAccessFile] */
    /* JADX WARN: Type inference failed for: r0v1 */
    public synchronized byte[] getMiniThumbFromFile(long j, byte[] bArr) {
        FileLock fileLockLock;
        FileLock fileLockMiniThumbDataFile = miniThumbDataFile();
        if (fileLockMiniThumbDataFile == 0) {
            return null;
        }
        long j2 = JobInfo.MIN_BACKOFF_MILLIS * j;
        try {
            try {
                this.mBuffer.clear();
                fileLockLock = this.mChannel.lock(j2, JobInfo.MIN_BACKOFF_MILLIS, true);
                try {
                    int i = this.mChannel.read(this.mBuffer, j2);
                    if (i > 13) {
                        this.mBuffer.position(0);
                        byte b = this.mBuffer.get();
                        long j3 = this.mBuffer.getLong();
                        int i2 = this.mBuffer.getInt();
                        if (i >= i2 + 13 && i2 != 0 && j3 != 0 && b == 1 && bArr.length >= i2) {
                            this.mBuffer.get(bArr, 0, i2);
                            if (fileLockLock != null) {
                                try {
                                    fileLockLock.release();
                                } catch (IOException unused) {
                                }
                            }
                            return bArr;
                        }
                    }
                    if (fileLockLock != null) {
                        try {
                            fileLockLock.release();
                        } catch (IOException unused2) {
                        }
                    }
                } catch (IOException e) {
                    e = e;
                    Log.w(TAG, "got exception when reading thumbnail id=" + j + ", exception: " + e);
                    if (fileLockLock != null) {
                        fileLockLock.release();
                    }
                } catch (RuntimeException e2) {
                    e = e2;
                    Log.e(TAG, "Got exception when reading thumbnail, id = " + j + ", disk full or mount read-only? " + e.getClass());
                    if (fileLockLock != null) {
                        fileLockLock.release();
                    }
                }
            } catch (Throwable th) {
                th = th;
                if (fileLockMiniThumbDataFile != 0) {
                    try {
                        fileLockMiniThumbDataFile.release();
                    } catch (IOException unused3) {
                    }
                }
                throw th;
            }
        } catch (IOException e3) {
            e = e3;
            fileLockLock = null;
        } catch (RuntimeException e4) {
            e = e4;
            fileLockLock = null;
        } catch (Throwable th2) {
            th = th2;
            fileLockMiniThumbDataFile = 0;
            if (fileLockMiniThumbDataFile != 0) {
                fileLockMiniThumbDataFile.release();
            }
            throw th;
        }
        return null;
    }

    public synchronized void saveMiniThumbToFile(byte[] bArr, long j, long j2) throws IOException {
        FileLock fileLockLock;
        if (miniThumbDataFile() == null) {
            return;
        }
        long j3 = JobInfo.MIN_BACKOFF_MILLIS * j;
        FileLock fileLock = null;
        try {
            try {
                if (bArr != null) {
                    try {
                        if (bArr.length > 9987) {
                            return;
                        }
                        this.mBuffer.clear();
                        this.mBuffer.put((byte) 1);
                        this.mBuffer.putLong(j2);
                        this.mBuffer.putInt(bArr.length);
                        this.mBuffer.put(bArr);
                        this.mBuffer.flip();
                        fileLockLock = this.mChannel.lock(j3, JobInfo.MIN_BACKOFF_MILLIS, false);
                        try {
                            this.mChannel.write(this.mBuffer, j3);
                        } catch (IOException e) {
                            e = e;
                        } catch (RuntimeException e2) {
                            e = e2;
                            fileLock = fileLockLock;
                            Log.e(TAG, "couldn't save mini thumbnail data for " + j + "; disk full or mount read-only? " + e.getClass());
                            if (fileLock != null) {
                                fileLock.release();
                            }
                        } catch (Throwable th) {
                            th = th;
                            fileLock = fileLockLock;
                            if (fileLock != null) {
                                try {
                                    fileLock.release();
                                } catch (IOException unused) {
                                }
                            }
                            throw th;
                        }
                    } catch (IOException e3) {
                        e = e3;
                    } catch (RuntimeException e4) {
                        e = e4;
                    }
                    Log.e(TAG, "couldn't save mini thumbnail data for " + j + "; ", e);
                    throw e;
                }
                fileLockLock = null;
                if (fileLockLock != null) {
                    fileLockLock.release();
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (IOException unused2) {
        }
    }
}
