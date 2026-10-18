package com.facebook.common.internal;

import com.facebook.infer.annotation.Nullsafe;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.STRICT)
public class Files {
    private Files() {
    }

    static byte[] readFile(InputStream inputStream, long j) throws IOException {
        if (j <= 2147483647L) {
            return j == 0 ? ByteStreams.toByteArray(inputStream) : ByteStreams.toByteArray(inputStream, (int) j);
        }
        throw new OutOfMemoryError("file is too large to fit in a byte array: " + j + " bytes");
    }

    public static byte[] toByteArray(File file) throws Throwable {
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(file);
            try {
                byte[] file2 = readFile(fileInputStream, fileInputStream.getChannel().size());
                fileInputStream.close();
                return file2;
            } catch (Throwable th) {
                th = th;
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            fileInputStream = null;
        }
    }
}
