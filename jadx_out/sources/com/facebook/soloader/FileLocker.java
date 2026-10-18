package com.facebook.soloader;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import javax.annotation.Nullable;

/* JADX INFO: loaded from: classes3.dex */
public final class FileLocker implements Closeable {

    @Nullable
    private final FileLock mLock;
    private final FileOutputStream mLockFileOutputStream;

    private FileLocker(File file) throws IOException {
        this.mLockFileOutputStream = new FileOutputStream(file);
        try {
            FileLock fileLockLock = this.mLockFileOutputStream.getChannel().lock();
            if (fileLockLock == null) {
                this.mLockFileOutputStream.close();
            }
            this.mLock = fileLockLock;
        } catch (Throwable th) {
            this.mLockFileOutputStream.close();
            throw th;
        }
    }

    public static FileLocker lock(File file) throws IOException {
        return new FileLocker(file);
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        try {
            if (this.mLock != null) {
                this.mLock.release();
            }
        } finally {
            this.mLockFileOutputStream.close();
        }
    }
}
