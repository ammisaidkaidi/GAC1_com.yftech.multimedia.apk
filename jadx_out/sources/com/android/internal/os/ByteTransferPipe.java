package com.android.internal.os;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: classes3.dex */
public class ByteTransferPipe extends TransferPipe {
    static final String TAG = "ByteTransferPipe";
    private ByteArrayOutputStream mOutputStream;

    public ByteTransferPipe() throws IOException {
    }

    public ByteTransferPipe(String str) throws IOException {
        super(str, TAG);
    }

    public byte[] get() throws IOException {
        go(null);
        return this.mOutputStream.toByteArray();
    }

    @Override // com.android.internal.os.TransferPipe
    protected OutputStream getNewOutputStream() {
        this.mOutputStream = new ByteArrayOutputStream();
        return this.mOutputStream;
    }
}
