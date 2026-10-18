package android.util;

import android.os.FileUtils;
import android.os.SystemClock;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.function.Consumer;
import libcore.io.IoUtils;

/* JADX INFO: loaded from: classes2.dex */
public class AtomicFile {
    private final File mBackupName;
    private final File mBaseName;
    private final String mCommitTag;
    private long mStartTime;

    public AtomicFile(File file) {
        this(file, null);
    }

    public AtomicFile(File file, String str) {
        this.mBaseName = file;
        this.mBackupName = new File(file.getPath() + ".bak");
        this.mCommitTag = str;
    }

    public void delete() {
        this.mBaseName.delete();
        this.mBackupName.delete();
    }

    public boolean exists() {
        return this.mBaseName.exists() || this.mBackupName.exists();
    }

    public void failWrite(FileOutputStream fileOutputStream) {
        if (fileOutputStream != null) {
            FileUtils.sync(fileOutputStream);
            try {
                fileOutputStream.close();
                this.mBaseName.delete();
                this.mBackupName.renameTo(this.mBaseName);
            } catch (IOException e) {
                Log.w("AtomicFile", "failWrite: Got exception:", e);
            }
        }
    }

    public void finishWrite(FileOutputStream fileOutputStream) {
        if (fileOutputStream != null) {
            FileUtils.sync(fileOutputStream);
            try {
                fileOutputStream.close();
                this.mBackupName.delete();
            } catch (IOException e) {
                Log.w("AtomicFile", "finishWrite: Got exception:", e);
            }
            if (this.mCommitTag != null) {
                com.android.internal.logging.EventLogTags.writeCommitSysConfigFile(this.mCommitTag, SystemClock.uptimeMillis() - this.mStartTime);
            }
        }
    }

    public File getBaseFile() {
        return this.mBaseName;
    }

    public long getLastModifiedTime() {
        return this.mBackupName.exists() ? this.mBackupName.lastModified() : this.mBaseName.lastModified();
    }

    @Deprecated
    public FileOutputStream openAppend() throws IOException {
        try {
            return new FileOutputStream(this.mBaseName, true);
        } catch (FileNotFoundException unused) {
            throw new IOException("Couldn't append " + this.mBaseName);
        }
    }

    public FileInputStream openRead() throws FileNotFoundException {
        if (this.mBackupName.exists()) {
            this.mBaseName.delete();
            this.mBackupName.renameTo(this.mBaseName);
        }
        return new FileInputStream(this.mBaseName);
    }

    public byte[] readFully() throws IOException {
        FileInputStream fileInputStreamOpenRead = openRead();
        try {
            byte[] bArr = new byte[fileInputStreamOpenRead.available()];
            int i = 0;
            while (true) {
                int i2 = fileInputStreamOpenRead.read(bArr, i, bArr.length - i);
                if (i2 <= 0) {
                    fileInputStreamOpenRead.close();
                    return bArr;
                }
                i += i2;
                int iAvailable = fileInputStreamOpenRead.available();
                if (iAvailable > bArr.length - i) {
                    byte[] bArr2 = new byte[iAvailable + i];
                    System.arraycopy(bArr, 0, bArr2, 0, i);
                    bArr = bArr2;
                }
            }
        } catch (Throwable th) {
            fileInputStreamOpenRead.close();
            throw th;
        }
    }

    public FileOutputStream startWrite() throws IOException {
        return startWrite(this.mCommitTag != null ? SystemClock.uptimeMillis() : 0L);
    }

    public FileOutputStream startWrite(long j) throws IOException {
        this.mStartTime = j;
        if (this.mBaseName.exists()) {
            if (this.mBackupName.exists()) {
                this.mBaseName.delete();
            } else if (!this.mBaseName.renameTo(this.mBackupName)) {
                Log.w("AtomicFile", "Couldn't rename file " + this.mBaseName + " to backup file " + this.mBackupName);
            }
        }
        try {
            return new FileOutputStream(this.mBaseName);
        } catch (FileNotFoundException unused) {
            File parentFile = this.mBaseName.getParentFile();
            if (!parentFile.mkdirs()) {
                throw new IOException("Couldn't create directory " + this.mBaseName);
            }
            FileUtils.setPermissions(parentFile.getPath(), 505, -1, -1);
            try {
                return new FileOutputStream(this.mBaseName);
            } catch (FileNotFoundException unused2) {
                throw new IOException("Couldn't create " + this.mBaseName);
            }
        }
    }

    @Deprecated
    public void truncate() throws IOException {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(this.mBaseName);
            FileUtils.sync(fileOutputStream);
            fileOutputStream.close();
        } catch (FileNotFoundException unused) {
            throw new IOException("Couldn't append " + this.mBaseName);
        } catch (IOException unused2) {
        }
    }

    public void write(Consumer<FileOutputStream> consumer) throws Throwable {
        FileOutputStream fileOutputStream = null;
        try {
            try {
                FileOutputStream fileOutputStreamStartWrite = startWrite();
                try {
                    consumer.accept(fileOutputStreamStartWrite);
                    finishWrite(fileOutputStreamStartWrite);
                    IoUtils.closeQuietly(fileOutputStreamStartWrite);
                } catch (Throwable th) {
                    th = th;
                    fileOutputStream = fileOutputStreamStartWrite;
                    IoUtils.closeQuietly(fileOutputStream);
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Throwable th3) {
            th = th3;
        }
    }
}
