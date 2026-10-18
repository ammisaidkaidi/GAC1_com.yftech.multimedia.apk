package android.os;

import android.app.backup.FullBackup;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.system.StructStat;
import android.util.Log;
import dalvik.system.CloseGuard;
import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramSocket;
import java.net.Socket;
import java.nio.ByteOrder;
import libcore.io.IoUtils;
import libcore.io.Memory;

/* JADX INFO: loaded from: classes2.dex */
public class ParcelFileDescriptor implements Parcelable, Closeable {
    public static final Parcelable.Creator<ParcelFileDescriptor> CREATOR = new Parcelable.Creator<ParcelFileDescriptor>() { // from class: android.os.ParcelFileDescriptor.2
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ParcelFileDescriptor createFromParcel(Parcel parcel) {
            return new ParcelFileDescriptor(parcel.readRawFileDescriptor(), parcel.readInt() != 0 ? parcel.readRawFileDescriptor() : null);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ParcelFileDescriptor[] newArray(int i) {
            return new ParcelFileDescriptor[i];
        }
    };
    private static final int MAX_STATUS = 1024;
    public static final int MODE_APPEND = 33554432;
    public static final int MODE_CREATE = 134217728;
    public static final int MODE_READ_ONLY = 268435456;
    public static final int MODE_READ_WRITE = 805306368;
    public static final int MODE_TRUNCATE = 67108864;

    @Deprecated
    public static final int MODE_WORLD_READABLE = 1;

    @Deprecated
    public static final int MODE_WORLD_WRITEABLE = 2;
    public static final int MODE_WRITE_ONLY = 536870912;
    private static final String TAG = "ParcelFileDescriptor";
    private volatile boolean mClosed;
    private FileDescriptor mCommFd;
    private final FileDescriptor mFd;
    private final CloseGuard mGuard;
    private Status mStatus;
    private byte[] mStatusBuf;
    private final ParcelFileDescriptor mWrapped;

    public static class AutoCloseInputStream extends FileInputStream {
        private final ParcelFileDescriptor mPfd;

        public AutoCloseInputStream(ParcelFileDescriptor parcelFileDescriptor) {
            super(parcelFileDescriptor.getFileDescriptor());
            this.mPfd = parcelFileDescriptor;
        }

        @Override // java.io.FileInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            try {
                this.mPfd.close();
            } finally {
                super.close();
            }
        }

        @Override // java.io.FileInputStream, java.io.InputStream
        public int read() throws IOException {
            int i = super.read();
            if (i == -1 && this.mPfd.canDetectErrors()) {
                this.mPfd.checkError();
            }
            return i;
        }

        @Override // java.io.FileInputStream, java.io.InputStream
        public int read(byte[] bArr) throws IOException {
            int i = super.read(bArr);
            if (i == -1 && this.mPfd.canDetectErrors()) {
                this.mPfd.checkError();
            }
            return i;
        }

        @Override // java.io.FileInputStream, java.io.InputStream
        public int read(byte[] bArr, int i, int i2) throws IOException {
            int i3 = super.read(bArr, i, i2);
            if (i3 == -1 && this.mPfd.canDetectErrors()) {
                this.mPfd.checkError();
            }
            return i3;
        }
    }

    public static class AutoCloseOutputStream extends FileOutputStream {
        private final ParcelFileDescriptor mPfd;

        public AutoCloseOutputStream(ParcelFileDescriptor parcelFileDescriptor) {
            super(parcelFileDescriptor.getFileDescriptor());
            this.mPfd = parcelFileDescriptor;
        }

        @Override // java.io.FileOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            try {
                this.mPfd.close();
            } finally {
                super.close();
            }
        }
    }

    public static class FileDescriptorDetachedException extends IOException {
        private static final long serialVersionUID = 955542466045L;

        public FileDescriptorDetachedException() {
            super("Remote side is detached");
        }
    }

    public interface OnCloseListener {
        void onClose(IOException iOException);
    }

    private static class Status {
        public static final int DEAD = -2;
        public static final int DETACHED = 2;
        public static final int ERROR = 1;
        public static final int LEAKED = 3;
        public static final int OK = 0;
        public static final int SILENCE = -1;
        public final String msg;
        public final int status;

        public Status(int i) {
            this(i, null);
        }

        public Status(int i, String str) {
            this.status = i;
            this.msg = str;
        }

        public IOException asIOException() {
            int i = this.status;
            if (i == -2) {
                return new IOException("Remote side is dead");
            }
            switch (i) {
                case 0:
                    return null;
                case 1:
                    return new IOException("Remote error: " + this.msg);
                case 2:
                    return new FileDescriptorDetachedException();
                case 3:
                    return new IOException("Remote side was leaked");
                default:
                    return new IOException("Unknown status: " + this.status);
            }
        }

        public String toString() {
            return "{" + this.status + ": " + this.msg + "}";
        }
    }

    public ParcelFileDescriptor(ParcelFileDescriptor parcelFileDescriptor) {
        this.mGuard = CloseGuard.get();
        this.mWrapped = parcelFileDescriptor;
        this.mFd = null;
        this.mCommFd = null;
        this.mClosed = true;
    }

    public ParcelFileDescriptor(FileDescriptor fileDescriptor) {
        this(fileDescriptor, null);
    }

    public ParcelFileDescriptor(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2) {
        this.mGuard = CloseGuard.get();
        if (fileDescriptor == null) {
            throw new NullPointerException("FileDescriptor must not be null");
        }
        this.mWrapped = null;
        this.mFd = fileDescriptor;
        this.mCommFd = fileDescriptor2;
        this.mGuard.open("close");
    }

    public static ParcelFileDescriptor adoptFd(int i) {
        FileDescriptor fileDescriptor = new FileDescriptor();
        fileDescriptor.setInt$(i);
        return new ParcelFileDescriptor(fileDescriptor);
    }

    private void closeWithStatus(int i, String str) {
        if (this.mClosed) {
            return;
        }
        this.mClosed = true;
        if (this.mGuard != null) {
            this.mGuard.close();
        }
        writeCommStatusAndClose(i, str);
        IoUtils.closeQuietly(this.mFd);
        releaseResources();
    }

    private static FileDescriptor[] createCommSocketPair() throws IOException {
        try {
            FileDescriptor fileDescriptor = new FileDescriptor();
            FileDescriptor fileDescriptor2 = new FileDescriptor();
            Os.socketpair(OsConstants.AF_UNIX, OsConstants.SOCK_SEQPACKET, 0, fileDescriptor, fileDescriptor2);
            IoUtils.setBlocking(fileDescriptor, false);
            IoUtils.setBlocking(fileDescriptor2, false);
            return new FileDescriptor[]{fileDescriptor, fileDescriptor2};
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    public static ParcelFileDescriptor[] createPipe() throws IOException {
        try {
            FileDescriptor[] fileDescriptorArrPipe = Os.pipe();
            return new ParcelFileDescriptor[]{new ParcelFileDescriptor(fileDescriptorArrPipe[0]), new ParcelFileDescriptor(fileDescriptorArrPipe[1])};
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    public static ParcelFileDescriptor[] createReliablePipe() throws IOException {
        try {
            FileDescriptor[] fileDescriptorArrCreateCommSocketPair = createCommSocketPair();
            FileDescriptor[] fileDescriptorArrPipe = Os.pipe();
            return new ParcelFileDescriptor[]{new ParcelFileDescriptor(fileDescriptorArrPipe[0], fileDescriptorArrCreateCommSocketPair[0]), new ParcelFileDescriptor(fileDescriptorArrPipe[1], fileDescriptorArrCreateCommSocketPair[1])};
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    public static ParcelFileDescriptor[] createReliableSocketPair() throws IOException {
        return createReliableSocketPair(OsConstants.SOCK_STREAM);
    }

    public static ParcelFileDescriptor[] createReliableSocketPair(int i) throws IOException {
        try {
            FileDescriptor[] fileDescriptorArrCreateCommSocketPair = createCommSocketPair();
            FileDescriptor fileDescriptor = new FileDescriptor();
            FileDescriptor fileDescriptor2 = new FileDescriptor();
            Os.socketpair(OsConstants.AF_UNIX, i, 0, fileDescriptor, fileDescriptor2);
            return new ParcelFileDescriptor[]{new ParcelFileDescriptor(fileDescriptor, fileDescriptorArrCreateCommSocketPair[0]), new ParcelFileDescriptor(fileDescriptor2, fileDescriptorArrCreateCommSocketPair[1])};
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    public static ParcelFileDescriptor[] createSocketPair() throws IOException {
        return createSocketPair(OsConstants.SOCK_STREAM);
    }

    public static ParcelFileDescriptor[] createSocketPair(int i) throws IOException {
        try {
            FileDescriptor fileDescriptor = new FileDescriptor();
            FileDescriptor fileDescriptor2 = new FileDescriptor();
            Os.socketpair(OsConstants.AF_UNIX, i, 0, fileDescriptor, fileDescriptor2);
            return new ParcelFileDescriptor[]{new ParcelFileDescriptor(fileDescriptor), new ParcelFileDescriptor(fileDescriptor2)};
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    public static ParcelFileDescriptor dup(FileDescriptor fileDescriptor) throws IOException {
        try {
            return new ParcelFileDescriptor(Os.dup(fileDescriptor));
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    @Deprecated
    public static ParcelFileDescriptor fromData(byte[] bArr, String str) throws IOException {
        if (bArr == null) {
            return null;
        }
        MemoryFile memoryFile = new MemoryFile(str, bArr.length);
        if (bArr.length > 0) {
            memoryFile.writeBytes(bArr, 0, 0, bArr.length);
        }
        memoryFile.deactivate();
        FileDescriptor fileDescriptor = memoryFile.getFileDescriptor();
        if (fileDescriptor != null) {
            return new ParcelFileDescriptor(fileDescriptor);
        }
        return null;
    }

    public static ParcelFileDescriptor fromDatagramSocket(DatagramSocket datagramSocket) {
        FileDescriptor fileDescriptor$ = datagramSocket.getFileDescriptor$();
        if (fileDescriptor$ != null) {
            return new ParcelFileDescriptor(fileDescriptor$);
        }
        return null;
    }

    public static ParcelFileDescriptor fromFd(int i) throws IOException {
        FileDescriptor fileDescriptor = new FileDescriptor();
        fileDescriptor.setInt$(i);
        try {
            return new ParcelFileDescriptor(Os.dup(fileDescriptor));
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    public static ParcelFileDescriptor fromFd(FileDescriptor fileDescriptor, Handler handler, final OnCloseListener onCloseListener) throws IOException {
        if (handler == null) {
            throw new IllegalArgumentException("Handler must not be null");
        }
        if (onCloseListener == null) {
            throw new IllegalArgumentException("Listener must not be null");
        }
        FileDescriptor[] fileDescriptorArrCreateCommSocketPair = createCommSocketPair();
        ParcelFileDescriptor parcelFileDescriptor = new ParcelFileDescriptor(fileDescriptor, fileDescriptorArrCreateCommSocketPair[0]);
        final MessageQueue queue = handler.getLooper().getQueue();
        queue.addOnFileDescriptorEventListener(fileDescriptorArrCreateCommSocketPair[1], 1, new MessageQueue.OnFileDescriptorEventListener() { // from class: android.os.ParcelFileDescriptor.1
            @Override // android.os.MessageQueue.OnFileDescriptorEventListener
            public int onFileDescriptorEvents(FileDescriptor fileDescriptor2, int i) {
                Status status;
                if ((i & 1) != 0) {
                    status = ParcelFileDescriptor.readCommStatus(fileDescriptor2, new byte[1024]);
                } else {
                    status = (i & 4) != 0 ? new Status(-2) : null;
                }
                if (status == null) {
                    return 1;
                }
                queue.removeOnFileDescriptorEventListener(fileDescriptor2);
                IoUtils.closeQuietly(fileDescriptor2);
                onCloseListener.onClose(status.asIOException());
                return 0;
            }
        });
        return parcelFileDescriptor;
    }

    public static ParcelFileDescriptor fromSocket(Socket socket) {
        FileDescriptor fileDescriptor$ = socket.getFileDescriptor$();
        if (fileDescriptor$ != null) {
            return new ParcelFileDescriptor(fileDescriptor$);
        }
        return null;
    }

    public static File getFile(FileDescriptor fileDescriptor) throws IOException {
        try {
            String str = Os.readlink("/proc/self/fd/" + fileDescriptor.getInt$());
            if (OsConstants.S_ISREG(Os.stat(str).st_mode)) {
                return new File(str);
            }
            throw new IOException("Not a regular file: " + str);
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    private byte[] getOrCreateStatusBuffer() {
        if (this.mStatusBuf == null) {
            this.mStatusBuf = new byte[1024];
        }
        return this.mStatusBuf;
    }

    public static ParcelFileDescriptor open(File file, int i) throws FileNotFoundException {
        FileDescriptor fileDescriptorOpenInternal = openInternal(file, i);
        if (fileDescriptorOpenInternal == null) {
            return null;
        }
        return new ParcelFileDescriptor(fileDescriptorOpenInternal);
    }

    public static ParcelFileDescriptor open(File file, int i, Handler handler, OnCloseListener onCloseListener) throws IOException {
        if (handler == null) {
            throw new IllegalArgumentException("Handler must not be null");
        }
        if (onCloseListener == null) {
            throw new IllegalArgumentException("Listener must not be null");
        }
        FileDescriptor fileDescriptorOpenInternal = openInternal(file, i);
        if (fileDescriptorOpenInternal == null) {
            return null;
        }
        return fromFd(fileDescriptorOpenInternal, handler, onCloseListener);
    }

    private static FileDescriptor openInternal(File file, int i) throws FileNotFoundException {
        int i2 = i & 805306368;
        if (i2 == 0) {
            throw new IllegalArgumentException("Must specify MODE_READ_ONLY, MODE_WRITE_ONLY, or MODE_READ_WRITE");
        }
        int i3 = 0;
        if (i2 == 0 || i2 == 268435456) {
            i3 = OsConstants.O_RDONLY;
        } else if (i2 == 536870912) {
            i3 = OsConstants.O_WRONLY;
        } else if (i2 == 805306368) {
            i3 = OsConstants.O_RDWR;
        }
        if ((134217728 & i) != 0) {
            i3 |= OsConstants.O_CREAT;
        }
        if ((67108864 & i) != 0) {
            i3 |= OsConstants.O_TRUNC;
        }
        if ((33554432 & i) != 0) {
            i3 |= OsConstants.O_APPEND;
        }
        int i4 = OsConstants.S_IRWXU | OsConstants.S_IRWXG;
        if ((i & 1) != 0) {
            i4 |= OsConstants.S_IROTH;
        }
        if ((i & 2) != 0) {
            i4 |= OsConstants.S_IWOTH;
        }
        try {
            return Os.open(file.getPath(), i3, i4);
        } catch (ErrnoException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    public static int parseMode(String str) {
        if (FullBackup.ROOT_TREE_TOKEN.equals(str)) {
            return 268435456;
        }
        if ("w".equals(str) || "wt".equals(str)) {
            return 738197504;
        }
        if ("wa".equals(str)) {
            return 704643072;
        }
        if ("rw".equals(str)) {
            return 939524096;
        }
        if ("rwt".equals(str)) {
            return 1006632960;
        }
        throw new IllegalArgumentException("Bad mode '" + str + "'");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Status readCommStatus(FileDescriptor fileDescriptor, byte[] bArr) {
        try {
            int i = Os.read(fileDescriptor, bArr, 0, bArr.length);
            if (i == 0) {
                return new Status(-2);
            }
            int iPeekInt = Memory.peekInt(bArr, 0, ByteOrder.BIG_ENDIAN);
            return iPeekInt == 1 ? new Status(iPeekInt, new String(bArr, 4, i - 4)) : new Status(iPeekInt);
        } catch (ErrnoException e) {
            if (e.errno == OsConstants.EAGAIN) {
                return null;
            }
            Log.d(TAG, "Failed to read status; assuming dead: " + e);
            return new Status(-2);
        } catch (InterruptedIOException e2) {
            Log.d(TAG, "Failed to read status; assuming dead: " + e2);
            return new Status(-2);
        }
    }

    private void writeCommStatusAndClose(int i, String str) {
        if (this.mCommFd == null) {
            if (str != null) {
                Log.w(TAG, "Unable to inform peer: " + str);
                return;
            }
            return;
        }
        if (i == 2) {
            Log.w(TAG, "Peer expected signal when closed; unable to deliver after detach");
        }
        if (i == -1) {
            IoUtils.closeQuietly(this.mCommFd);
            this.mCommFd = null;
            return;
        }
        try {
            this.mStatus = readCommStatus(this.mCommFd, getOrCreateStatusBuffer());
            if (this.mStatus != null) {
                return;
            }
            try {
                byte[] orCreateStatusBuffer = getOrCreateStatusBuffer();
                Memory.pokeInt(orCreateStatusBuffer, 0, i, ByteOrder.BIG_ENDIAN);
                int i2 = 4;
                if (str != null) {
                    byte[] bytes = str.getBytes();
                    int iMin = Math.min(bytes.length, orCreateStatusBuffer.length - 4);
                    System.arraycopy(bytes, 0, orCreateStatusBuffer, 4, iMin);
                    i2 = 4 + iMin;
                }
                Os.write(this.mCommFd, orCreateStatusBuffer, 0, i2);
            } catch (ErrnoException e) {
                Log.w(TAG, "Failed to report status: " + e);
            } catch (InterruptedIOException e2) {
                Log.w(TAG, "Failed to report status: " + e2);
            }
        } finally {
            IoUtils.closeQuietly(this.mCommFd);
            this.mCommFd = null;
        }
    }

    public boolean canDetectErrors() {
        if (this.mWrapped != null) {
            return this.mWrapped.canDetectErrors();
        }
        return this.mCommFd != null;
    }

    public void checkError() throws IOException {
        if (this.mWrapped != null) {
            this.mWrapped.checkError();
            return;
        }
        if (this.mStatus == null) {
            if (this.mCommFd == null) {
                Log.w(TAG, "Peer didn't provide a comm channel; unable to check for errors");
                return;
            }
            this.mStatus = readCommStatus(this.mCommFd, getOrCreateStatusBuffer());
        }
        if (this.mStatus != null && this.mStatus.status != 0) {
            throw this.mStatus.asIOException();
        }
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.mWrapped == null) {
            closeWithStatus(0, null);
            return;
        }
        try {
            this.mWrapped.close();
        } finally {
            releaseResources();
        }
    }

    public void closeWithError(String str) throws IOException {
        if (this.mWrapped != null) {
            try {
                this.mWrapped.closeWithError(str);
            } finally {
                releaseResources();
            }
        } else {
            if (str == null) {
                throw new IllegalArgumentException("Message must not be null");
            }
            closeWithStatus(1, str);
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        if (this.mWrapped != null) {
            return this.mWrapped.describeContents();
        }
        return 1;
    }

    public int detachFd() {
        if (this.mWrapped != null) {
            return this.mWrapped.detachFd();
        }
        if (this.mClosed) {
            throw new IllegalStateException("Already closed");
        }
        int fd = getFd();
        this.mFd.setInt$(-1);
        writeCommStatusAndClose(2, null);
        this.mClosed = true;
        this.mGuard.close();
        releaseResources();
        return fd;
    }

    public ParcelFileDescriptor dup() throws IOException {
        return this.mWrapped != null ? this.mWrapped.dup() : dup(getFileDescriptor());
    }

    protected void finalize() throws Throwable {
        if (this.mWrapped != null) {
            releaseResources();
        }
        if (this.mGuard != null) {
            this.mGuard.warnIfOpen();
        }
        try {
            if (!this.mClosed) {
                closeWithStatus(3, null);
            }
        } finally {
            super.finalize();
        }
    }

    public int getFd() {
        if (this.mWrapped != null) {
            return this.mWrapped.getFd();
        }
        if (this.mClosed) {
            throw new IllegalStateException("Already closed");
        }
        return this.mFd.getInt$();
    }

    public FileDescriptor getFileDescriptor() {
        return this.mWrapped != null ? this.mWrapped.getFileDescriptor() : this.mFd;
    }

    public long getStatSize() {
        if (this.mWrapped != null) {
            return this.mWrapped.getStatSize();
        }
        try {
            StructStat structStatFstat = Os.fstat(this.mFd);
            if (!OsConstants.S_ISREG(structStatFstat.st_mode) && !OsConstants.S_ISLNK(structStatFstat.st_mode)) {
                return -1L;
            }
            return structStatFstat.st_size;
        } catch (ErrnoException e) {
            Log.w(TAG, "fstat() failed: " + e);
            return -1L;
        }
    }

    public void releaseResources() {
    }

    public long seekTo(long j) throws IOException {
        if (this.mWrapped != null) {
            return this.mWrapped.seekTo(j);
        }
        try {
            return Os.lseek(this.mFd, j, OsConstants.SEEK_SET);
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    public String toString() {
        if (this.mWrapped != null) {
            return this.mWrapped.toString();
        }
        return "{ParcelFileDescriptor: " + this.mFd + "}";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        if (this.mWrapped != null) {
            try {
                this.mWrapped.writeToParcel(parcel, i);
                return;
            } finally {
                releaseResources();
            }
        }
        if (this.mCommFd != null) {
            parcel.writeInt(1);
            parcel.writeFileDescriptor(this.mFd);
            parcel.writeFileDescriptor(this.mCommFd);
        } else {
            parcel.writeInt(0);
            parcel.writeFileDescriptor(this.mFd);
        }
        if ((i & 1) == 0 || this.mClosed) {
            return;
        }
        closeWithStatus(-1, null);
    }
}
