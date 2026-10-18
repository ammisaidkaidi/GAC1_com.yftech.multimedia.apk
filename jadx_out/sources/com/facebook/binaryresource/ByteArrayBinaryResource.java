package com.facebook.binaryresource;

import com.facebook.common.internal.Preconditions;
import com.facebook.infer.annotation.Nullsafe;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.STRICT)
public class ByteArrayBinaryResource implements BinaryResource {
    private final byte[] mBytes;

    public ByteArrayBinaryResource(byte[] bArr) {
        this.mBytes = (byte[]) Preconditions.checkNotNull(bArr);
    }

    @Override // com.facebook.binaryresource.BinaryResource
    public InputStream openStream() throws IOException {
        return new ByteArrayInputStream(this.mBytes);
    }

    @Override // com.facebook.binaryresource.BinaryResource
    public byte[] read() {
        return this.mBytes;
    }

    @Override // com.facebook.binaryresource.BinaryResource
    public long size() {
        return this.mBytes.length;
    }
}
