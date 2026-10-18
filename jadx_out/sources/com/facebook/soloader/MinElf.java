package com.facebook.soloader;

import android.hardware.contexthub.V1_0.HostEndPoint;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

/* JADX INFO: loaded from: classes3.dex */
public final class MinElf {
    public static final int DT_NEEDED = 1;
    public static final int DT_NULL = 0;
    public static final int DT_STRTAB = 5;
    public static final int ELF_MAGIC = 1179403647;
    public static final int PN_XNUM = 65535;
    public static final int PT_DYNAMIC = 2;
    public static final int PT_LOAD = 1;

    private static class ElfError extends RuntimeException {
        ElfError(String str) {
            super(str);
        }
    }

    public enum ISA {
        NOT_SO("not_so"),
        X86("x86"),
        ARM("armeabi-v7a"),
        X86_64("x86_64"),
        AARCH64("arm64-v8a"),
        OTHERS("others");

        private final String value;

        ISA(String str) {
            this.value = str;
        }

        @Override // java.lang.Enum
        public String toString() {
            return this.value;
        }
    }

    public static String[] extract_DT_NEEDED(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        try {
            return extract_DT_NEEDED(fileInputStream.getChannel());
        } finally {
            fileInputStream.close();
        }
    }

    public static String[] extract_DT_NEEDED(FileChannel fileChannel) throws IOException {
        long j;
        long j2;
        ByteBuffer byteBufferAllocate = ByteBuffer.allocate(8);
        byteBufferAllocate.order(ByteOrder.LITTLE_ENDIAN);
        if (getu32(fileChannel, byteBufferAllocate, 0L) != 1179403647) {
            throw new ElfError("file is not ELF");
        }
        boolean z = getu8(fileChannel, byteBufferAllocate, 4L) == 1;
        if (getu8(fileChannel, byteBufferAllocate, 5L) == 2) {
            byteBufferAllocate.order(ByteOrder.BIG_ENDIAN);
        }
        long j3 = z ? getu32(fileChannel, byteBufferAllocate, 28L) : get64(fileChannel, byteBufferAllocate, 32L);
        long j4 = z ? getu16(fileChannel, byteBufferAllocate, 44L) : getu16(fileChannel, byteBufferAllocate, 56L);
        int i = getu16(fileChannel, byteBufferAllocate, z ? 42L : 54L);
        if (j4 == 65535) {
            long j5 = z ? getu32(fileChannel, byteBufferAllocate, 32L) : get64(fileChannel, byteBufferAllocate, 40L);
            j4 = z ? getu32(fileChannel, byteBufferAllocate, j5 + 28) : getu32(fileChannel, byteBufferAllocate, j5 + 44);
        }
        long j6 = j3;
        long j7 = 0;
        while (true) {
            if (j7 >= j4) {
                j = 0;
                break;
            }
            if ((z ? getu32(fileChannel, byteBufferAllocate, j6 + 0) : getu32(fileChannel, byteBufferAllocate, j6 + 0)) == 2) {
                if (!z) {
                    j = get64(fileChannel, byteBufferAllocate, j6 + 8);
                    break;
                }
                j = getu32(fileChannel, byteBufferAllocate, j6 + 4);
                break;
            }
            j6 += (long) i;
            j7++;
        }
        long j8 = 0;
        if (j == 0) {
            throw new ElfError("ELF file does not contain dynamic linking information");
        }
        long j9 = j;
        long j10 = 0;
        int i2 = 0;
        while (true) {
            long j11 = z ? getu32(fileChannel, byteBufferAllocate, j9 + j8) : get64(fileChannel, byteBufferAllocate, j9 + j8);
            long j12 = j;
            if (j11 == 1) {
                if (i2 == Integer.MAX_VALUE) {
                    throw new ElfError("malformed DT_NEEDED section");
                }
                i2++;
            } else if (j11 == 5) {
                j10 = z != 0 ? getu32(fileChannel, byteBufferAllocate, j9 + 4) : get64(fileChannel, byteBufferAllocate, j9 + 8);
            }
            long j13 = 16;
            j9 += z != 0 ? 8L : 16L;
            j8 = 0;
            if (j11 != 0) {
                z = z;
                j = j12;
            } else {
                if (j10 == 0) {
                    throw new ElfError("Dynamic section string-table not found");
                }
                int i3 = 0;
                while (true) {
                    if (i3 >= j4) {
                        j2 = 0;
                        break;
                    }
                    if ((z != 0 ? getu32(fileChannel, byteBufferAllocate, j3 + j8) : getu32(fileChannel, byteBufferAllocate, j3 + j8)) == 1) {
                        long j14 = z != 0 ? getu32(fileChannel, byteBufferAllocate, j3 + 8) : get64(fileChannel, byteBufferAllocate, j3 + j13);
                        long j15 = z != 0 ? getu32(fileChannel, byteBufferAllocate, j3 + 20) : get64(fileChannel, byteBufferAllocate, j3 + 40);
                        if (j14 <= j10 && j10 < j15 + j14) {
                            j2 = (z != 0 ? getu32(fileChannel, byteBufferAllocate, j3 + 4) : get64(fileChannel, byteBufferAllocate, j3 + 8)) + (j10 - j14);
                            break;
                        }
                    } else {
                        j4 = j4;
                    }
                    j3 += (long) i;
                    i3++;
                    j4 = j4;
                    j13 = 16;
                    j8 = 0;
                }
                long j16 = 0;
                if (j2 == 0) {
                    throw new ElfError("did not find file offset of DT_STRTAB table");
                }
                String[] strArr = new String[i2];
                int i4 = 0;
                while (true) {
                    long j17 = z != 0 ? getu32(fileChannel, byteBufferAllocate, j12 + j16) : get64(fileChannel, byteBufferAllocate, j12 + j16);
                    if (j17 == 1) {
                        strArr[i4] = getSz(fileChannel, byteBufferAllocate, (z != 0 ? getu32(fileChannel, byteBufferAllocate, j12 + 4) : get64(fileChannel, byteBufferAllocate, j12 + 8)) + j2);
                        if (i4 == Integer.MAX_VALUE) {
                            throw new ElfError("malformed DT_NEEDED section");
                        }
                        i4++;
                    }
                    j12 += z != 0 ? 8L : 16L;
                    if (j17 == 0) {
                        if (i4 == strArr.length) {
                            return strArr;
                        }
                        throw new ElfError("malformed DT_NEEDED section");
                    }
                    j16 = 0;
                }
            }
        }
    }

    private static long get64(FileChannel fileChannel, ByteBuffer byteBuffer, long j) throws IOException {
        read(fileChannel, byteBuffer, 8, j);
        return byteBuffer.getLong();
    }

    private static String getSz(FileChannel fileChannel, ByteBuffer byteBuffer, long j) throws IOException {
        StringBuilder sb = new StringBuilder();
        while (true) {
            long j2 = 1 + j;
            short u8Var = getu8(fileChannel, byteBuffer, j);
            if (u8Var == 0) {
                return sb.toString();
            }
            sb.append((char) u8Var);
            j = j2;
        }
    }

    private static int getu16(FileChannel fileChannel, ByteBuffer byteBuffer, long j) throws IOException {
        read(fileChannel, byteBuffer, 2, j);
        return byteBuffer.getShort() & HostEndPoint.BROADCAST;
    }

    private static long getu32(FileChannel fileChannel, ByteBuffer byteBuffer, long j) throws IOException {
        read(fileChannel, byteBuffer, 4, j);
        return ((long) byteBuffer.getInt()) & 4294967295L;
    }

    private static short getu8(FileChannel fileChannel, ByteBuffer byteBuffer, long j) throws IOException {
        read(fileChannel, byteBuffer, 1, j);
        return (short) (byteBuffer.get() & 255);
    }

    private static void read(FileChannel fileChannel, ByteBuffer byteBuffer, int i, long j) throws IOException {
        int i2;
        byteBuffer.position(0);
        byteBuffer.limit(i);
        while (byteBuffer.remaining() > 0 && (i2 = fileChannel.read(byteBuffer, j)) != -1) {
            j += (long) i2;
        }
        if (byteBuffer.remaining() > 0) {
            throw new ElfError("ELF file truncated");
        }
        byteBuffer.position(0);
    }
}
