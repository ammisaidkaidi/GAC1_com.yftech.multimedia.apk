package com.alibaba.android.vlayout;

/* JADX INFO: loaded from: classes3.dex */
public class Cantor {
    public static long getCantor(long j, long j2) {
        long j3 = j + j2;
        return ((j3 * (1 + j3)) / 2) + j2;
    }

    public static void reverseCantor(long j, long[] jArr) {
        if (jArr == null || jArr.length < 2) {
            jArr = new long[2];
        }
        int iFloor = (int) (Math.floor(Math.sqrt((8 * j) + 1) - 1.0d) / 2.0d);
        int i = (int) (j - ((long) (((iFloor * iFloor) + iFloor) / 2)));
        jArr[0] = iFloor - i;
        jArr[1] = i;
    }
}
