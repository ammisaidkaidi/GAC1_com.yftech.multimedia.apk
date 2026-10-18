package com.facebook.common.memory;

import com.facebook.common.internal.Throwables;
import com.facebook.infer.annotation.Nullsafe;
import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.LOCAL)
public abstract class PooledByteBufferOutputStream extends OutputStream {
    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws Throwable {
        try {
            super.close();
        } catch (IOException e) {
            Throwables.propagate(e);
        }
    }

    public abstract int size();

    public abstract PooledByteBuffer toByteBuffer();
}
