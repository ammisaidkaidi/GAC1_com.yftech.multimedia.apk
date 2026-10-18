package android.net;

import android.system.ErrnoException;
import android.system.Int32Ref;
import android.system.Os;
import android.system.OsConstants;
import android.system.StructLinger;
import android.system.StructTimeval;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* JADX INFO: loaded from: classes.dex */
class LocalSocketImpl {
    private FileDescriptor fd;
    private SocketInputStream fis;
    private SocketOutputStream fos;
    FileDescriptor[] inboundFileDescriptors;
    private boolean mFdCreatedInternally;
    FileDescriptor[] outboundFileDescriptors;
    private Object readMonitor = new Object();
    private Object writeMonitor = new Object();

    class SocketInputStream extends InputStream {
        SocketInputStream() {
        }

        @Override // java.io.InputStream
        public int available() throws IOException {
            FileDescriptor fileDescriptor = LocalSocketImpl.this.fd;
            if (fileDescriptor == null) {
                throw new IOException("socket closed");
            }
            Int32Ref int32Ref = new Int32Ref(0);
            try {
                Os.ioctlInt(fileDescriptor, OsConstants.FIONREAD, int32Ref);
                return int32Ref.value;
            } catch (ErrnoException e) {
                throw e.rethrowAsIOException();
            }
        }

        @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            LocalSocketImpl.this.close();
        }

        @Override // java.io.InputStream
        public int read() throws IOException {
            int i;
            synchronized (LocalSocketImpl.this.readMonitor) {
                FileDescriptor fileDescriptor = LocalSocketImpl.this.fd;
                if (fileDescriptor == null) {
                    throw new IOException("socket closed");
                }
                i = LocalSocketImpl.this.read_native(fileDescriptor);
            }
            return i;
        }

        @Override // java.io.InputStream
        public int read(byte[] bArr) throws IOException {
            return read(bArr, 0, bArr.length);
        }

        @Override // java.io.InputStream
        public int read(byte[] bArr, int i, int i2) throws IOException {
            int i3;
            synchronized (LocalSocketImpl.this.readMonitor) {
                FileDescriptor fileDescriptor = LocalSocketImpl.this.fd;
                if (fileDescriptor == null) {
                    throw new IOException("socket closed");
                }
                if (i < 0 || i2 < 0 || i + i2 > bArr.length) {
                    throw new ArrayIndexOutOfBoundsException();
                }
                i3 = LocalSocketImpl.this.readba_native(bArr, i, i2, fileDescriptor);
            }
            return i3;
        }
    }

    class SocketOutputStream extends OutputStream {
        SocketOutputStream() {
        }

        @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            LocalSocketImpl.this.close();
        }

        @Override // java.io.OutputStream, java.io.Flushable
        public void flush() throws IOException {
            FileDescriptor fileDescriptor = LocalSocketImpl.this.fd;
            if (fileDescriptor == null) {
                throw new IOException("socket closed");
            }
            Int32Ref int32Ref = new Int32Ref(0);
            while (true) {
                try {
                    Os.ioctlInt(fileDescriptor, OsConstants.TIOCOUTQ, int32Ref);
                    if (int32Ref.value <= 0) {
                        return;
                    }
                    try {
                        Thread.sleep(10L);
                    } catch (InterruptedException unused) {
                        return;
                    }
                } catch (ErrnoException e) {
                    throw e.rethrowAsIOException();
                }
            }
        }

        @Override // java.io.OutputStream
        public void write(int i) throws IOException {
            synchronized (LocalSocketImpl.this.writeMonitor) {
                FileDescriptor fileDescriptor = LocalSocketImpl.this.fd;
                if (fileDescriptor == null) {
                    throw new IOException("socket closed");
                }
                LocalSocketImpl.this.write_native(i, fileDescriptor);
            }
        }

        @Override // java.io.OutputStream
        public void write(byte[] bArr) throws IOException {
            write(bArr, 0, bArr.length);
        }

        @Override // java.io.OutputStream
        public void write(byte[] bArr, int i, int i2) throws IOException {
            synchronized (LocalSocketImpl.this.writeMonitor) {
                FileDescriptor fileDescriptor = LocalSocketImpl.this.fd;
                if (fileDescriptor == null) {
                    throw new IOException("socket closed");
                }
                if (i < 0 || i2 < 0 || i + i2 > bArr.length) {
                    throw new ArrayIndexOutOfBoundsException();
                }
                LocalSocketImpl.this.writeba_native(bArr, i, i2, fileDescriptor);
            }
        }
    }

    LocalSocketImpl() {
    }

    LocalSocketImpl(FileDescriptor fileDescriptor) {
        this.fd = fileDescriptor;
    }

    private native void bindLocal(FileDescriptor fileDescriptor, String str, int i) throws IOException;

    private native void connectLocal(FileDescriptor fileDescriptor, String str, int i) throws IOException;

    private native Credentials getPeerCredentials_native(FileDescriptor fileDescriptor) throws IOException;

    private static int javaSoToOsOpt(int i) {
        if (i == 4) {
            return OsConstants.SO_REUSEADDR;
        }
        switch (i) {
            case 4097:
                return OsConstants.SO_SNDBUF;
            case 4098:
                return OsConstants.SO_RCVBUF;
            default:
                throw new UnsupportedOperationException("Unknown option: " + i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public native int read_native(FileDescriptor fileDescriptor) throws IOException;

    /* JADX INFO: Access modifiers changed from: private */
    public native int readba_native(byte[] bArr, int i, int i2, FileDescriptor fileDescriptor) throws IOException;

    /* JADX INFO: Access modifiers changed from: private */
    public native void write_native(int i, FileDescriptor fileDescriptor) throws IOException;

    /* JADX INFO: Access modifiers changed from: private */
    public native void writeba_native(byte[] bArr, int i, int i2, FileDescriptor fileDescriptor) throws IOException;

    protected void accept(LocalSocketImpl localSocketImpl) throws IOException {
        if (this.fd == null) {
            throw new IOException("socket not created");
        }
        try {
            localSocketImpl.fd = Os.accept(this.fd, null);
            localSocketImpl.mFdCreatedInternally = true;
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    protected int available() throws IOException {
        return getInputStream().available();
    }

    public void bind(LocalSocketAddress localSocketAddress) throws IOException {
        if (this.fd == null) {
            throw new IOException("socket not created");
        }
        bindLocal(this.fd, localSocketAddress.getName(), localSocketAddress.getNamespace().getId());
    }

    public void close() throws IOException {
        synchronized (this) {
            if (this.fd == null || !this.mFdCreatedInternally) {
                this.fd = null;
                return;
            }
            try {
                Os.close(this.fd);
            } catch (ErrnoException e) {
                e.rethrowAsIOException();
            }
            this.fd = null;
        }
    }

    protected void connect(LocalSocketAddress localSocketAddress, int i) throws IOException {
        if (this.fd == null) {
            throw new IOException("socket not created");
        }
        connectLocal(this.fd, localSocketAddress.getName(), localSocketAddress.getNamespace().getId());
    }

    public void create(int i) throws IOException {
        int i2;
        if (this.fd != null) {
            throw new IOException("LocalSocketImpl already has an fd");
        }
        switch (i) {
            case 1:
                i2 = OsConstants.SOCK_DGRAM;
                break;
            case 2:
                i2 = OsConstants.SOCK_STREAM;
                break;
            case 3:
                i2 = OsConstants.SOCK_SEQPACKET;
                break;
            default:
                throw new IllegalStateException("unknown sockType");
        }
        try {
            this.fd = Os.socket(OsConstants.AF_UNIX, i2, 0);
            this.mFdCreatedInternally = true;
        } catch (ErrnoException e) {
            e.rethrowAsIOException();
        }
    }

    protected void finalize() throws IOException {
        close();
    }

    public FileDescriptor[] getAncillaryFileDescriptors() throws IOException {
        FileDescriptor[] fileDescriptorArr;
        synchronized (this.readMonitor) {
            fileDescriptorArr = this.inboundFileDescriptors;
            this.inboundFileDescriptors = null;
        }
        return fileDescriptorArr;
    }

    protected FileDescriptor getFileDescriptor() {
        return this.fd;
    }

    protected InputStream getInputStream() throws IOException {
        SocketInputStream socketInputStream;
        if (this.fd == null) {
            throw new IOException("socket not created");
        }
        synchronized (this) {
            if (this.fis == null) {
                this.fis = new SocketInputStream();
            }
            socketInputStream = this.fis;
        }
        return socketInputStream;
    }

    public Object getOption(int i) throws IOException {
        if (this.fd == null) {
            throw new IOException("socket not created");
        }
        try {
            if (i == 1) {
                return Integer.valueOf(Os.getsockoptInt(this.fd, OsConstants.IPPROTO_TCP, OsConstants.TCP_NODELAY));
            }
            if (i != 4) {
                if (i == 128) {
                    StructLinger structLinger = Os.getsockoptLinger(this.fd, OsConstants.SOL_SOCKET, OsConstants.SO_LINGER);
                    if (structLinger.isOn()) {
                        return Integer.valueOf(structLinger.l_linger);
                    }
                    return -1;
                }
                if (i == 4102) {
                    return Integer.valueOf((int) Os.getsockoptTimeval(this.fd, OsConstants.SOL_SOCKET, OsConstants.SO_SNDTIMEO).toMillis());
                }
                switch (i) {
                    case 4097:
                    case 4098:
                        break;
                    default:
                        throw new IOException("Unknown option: " + i);
                }
            }
            return Integer.valueOf(Os.getsockoptInt(this.fd, OsConstants.SOL_SOCKET, javaSoToOsOpt(i)));
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    protected OutputStream getOutputStream() throws IOException {
        SocketOutputStream socketOutputStream;
        if (this.fd == null) {
            throw new IOException("socket not created");
        }
        synchronized (this) {
            if (this.fos == null) {
                this.fos = new SocketOutputStream();
            }
            socketOutputStream = this.fos;
        }
        return socketOutputStream;
    }

    public Credentials getPeerCredentials() throws IOException {
        return getPeerCredentials_native(this.fd);
    }

    public LocalSocketAddress getSockAddress() throws IOException {
        return null;
    }

    protected void listen(int i) throws IOException {
        if (this.fd == null) {
            throw new IOException("socket not created");
        }
        try {
            Os.listen(this.fd, i);
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    protected void sendUrgentData(int i) throws IOException {
        throw new RuntimeException("not impled");
    }

    public void setFileDescriptorsForSend(FileDescriptor[] fileDescriptorArr) {
        synchronized (this.writeMonitor) {
            this.outboundFileDescriptors = fileDescriptorArr;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v1 */
    /* JADX WARN: Type inference failed for: r0v5, types: [int] */
    /* JADX WARN: Type inference failed for: r0v9 */
    public void setOption(int i, Object obj) throws IOException {
        if (this.fd == null) {
            throw new IOException("socket not created");
        }
        ?? BooleanValue = -1;
        int iIntValue = 0;
        if (obj instanceof Integer) {
            iIntValue = ((Integer) obj).intValue();
        } else {
            if (!(obj instanceof Boolean)) {
                throw new IOException("bad value: " + obj);
            }
            BooleanValue = ((Boolean) obj).booleanValue();
        }
        try {
            if (i == 1) {
                Os.setsockoptInt(this.fd, OsConstants.IPPROTO_TCP, OsConstants.TCP_NODELAY, iIntValue);
                return;
            }
            if (i != 4) {
                if (i == 128) {
                    Os.setsockoptLinger(this.fd, OsConstants.SOL_SOCKET, OsConstants.SO_LINGER, new StructLinger((int) BooleanValue, iIntValue));
                    return;
                } else if (i == 4102) {
                    StructTimeval structTimevalFromMillis = StructTimeval.fromMillis(iIntValue);
                    Os.setsockoptTimeval(this.fd, OsConstants.SOL_SOCKET, OsConstants.SO_RCVTIMEO, structTimevalFromMillis);
                    Os.setsockoptTimeval(this.fd, OsConstants.SOL_SOCKET, OsConstants.SO_SNDTIMEO, structTimevalFromMillis);
                    return;
                } else {
                    switch (i) {
                        case 4097:
                        case 4098:
                            break;
                        default:
                            throw new IOException("Unknown option: " + i);
                    }
                }
            }
            Os.setsockoptInt(this.fd, OsConstants.SOL_SOCKET, javaSoToOsOpt(i), iIntValue);
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    protected void shutdownInput() throws IOException {
        if (this.fd == null) {
            throw new IOException("socket not created");
        }
        try {
            Os.shutdown(this.fd, OsConstants.SHUT_RD);
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    protected void shutdownOutput() throws IOException {
        if (this.fd == null) {
            throw new IOException("socket not created");
        }
        try {
            Os.shutdown(this.fd, OsConstants.SHUT_WR);
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    protected boolean supportsUrgentData() {
        return false;
    }

    public String toString() {
        return super.toString() + " fd:" + this.fd;
    }
}
