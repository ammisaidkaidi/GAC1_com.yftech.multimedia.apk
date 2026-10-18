package android.os;

import android.net.wifi.WifiEnterpriseConfig;
import android.util.Log;
import android.util.Printer;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;

/* JADX INFO: loaded from: classes2.dex */
public final class Looper {
    private static final String TAG = "Looper";
    private static Looper sMainLooper;
    static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<>();
    private Printer mLogging;
    final MessageQueue mQueue;
    private long mSlowDeliveryThresholdMs;
    private long mSlowDispatchThresholdMs;
    final Thread mThread = Thread.currentThread();
    private long mTraceTag;

    private Looper(boolean z) {
        this.mQueue = new MessageQueue(z);
    }

    public static Looper getMainLooper() {
        Looper looper;
        synchronized (Looper.class) {
            looper = sMainLooper;
        }
        return looper;
    }

    /* JADX WARN: Code duplicated, block: B:24:0x009d  */
    /* JADX WARN: Code duplicated, block: B:25:0x00a0  */
    /* JADX WARN: Code duplicated, block: B:30:0x00a8  */
    /* JADX WARN: Code duplicated, block: B:37:0x00bf  */
    /* JADX WARN: Code duplicated, block: B:38:0x00c6  */
    /* JADX WARN: Code duplicated, block: B:41:0x00cf A[Catch: all -> 0x0196, TRY_LEAVE, TryCatch #0 {all -> 0x0196, blocks: (B:39:0x00c8, B:41:0x00cf), top: B:70:0x00c8 }] */
    /* JADX WARN: Code duplicated, block: B:43:0x00d6  */
    /* JADX WARN: Code duplicated, block: B:45:0x00da  */
    /* JADX WARN: Code duplicated, block: B:47:0x00df A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:48:0x00e1  */
    /* JADX WARN: Code duplicated, block: B:50:0x00eb  */
    /* JADX WARN: Code duplicated, block: B:51:0x00f6  */
    /* JADX WARN: Code duplicated, block: B:54:0x0107  */
    /* JADX WARN: Code duplicated, block: B:55:0x0108 A[PHI: r5
      0x0108: PHI (r5v10 android.util.Printer) = (r5v9 android.util.Printer), (r5v24 android.util.Printer) binds: [B:54:0x0107, B:52:0x0104] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:57:0x010c  */
    /* JADX WARN: Code duplicated, block: B:59:0x011a  */
    /* JADX WARN: Code duplicated, block: B:62:0x0142  */
    /* JADX WARN: Code duplicated, block: B:74:0x018e A[SYNTHETIC] */
    /* JADX WARN: Instruction removed from duplicated block: B:59:0x011a, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:62:0x0142, please report this as an issue */
    public static void loop() {
        long j;
        long j2;
        boolean z;
        boolean z2;
        boolean z3;
        boolean z4;
        long jUptimeMillis;
        long jUptimeMillis2;
        Printer printer;
        long jClearCallingIdentity;
        Looper looperMyLooper = myLooper();
        if (looperMyLooper == null) {
            throw new RuntimeException("No Looper; Looper.prepare() wasn't called on this thread.");
        }
        MessageQueue messageQueue = looperMyLooper.mQueue;
        Binder.clearCallingIdentity();
        long jClearCallingIdentity2 = Binder.clearCallingIdentity();
        int i = SystemProperties.getInt("log.looper." + Process.myUid() + "." + Thread.currentThread().getName() + ".slow", 0);
        boolean z5 = false;
        while (true) {
            Message next = messageQueue.next();
            if (next == null) {
                return;
            }
            Printer printer2 = looperMyLooper.mLogging;
            if (printer2 != null) {
                printer2.println(">>>>> Dispatching to " + next.target + WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + next.callback + ": " + next.what);
            }
            long j3 = looperMyLooper.mTraceTag;
            long j4 = looperMyLooper.mSlowDispatchThresholdMs;
            long j5 = looperMyLooper.mSlowDeliveryThresholdMs;
            if (i > 0) {
                j2 = i;
                j = j2;
            } else {
                j = j4;
                j2 = j5;
            }
            boolean z6 = true;
            try {
                if (j2 > 0) {
                    z = z5;
                    z2 = next.when > 0;
                    if (j > 0) {
                        z3 = true;
                    } else {
                        z3 = false;
                    }
                    if (!z2 || z3) {
                        z4 = true;
                    } else {
                        z4 = false;
                    }
                    if (j3 != 0 && Trace.isTagEnabled(j3)) {
                        Trace.traceBegin(j3, next.target.getTraceName(next));
                    }
                    if (z4) {
                        jUptimeMillis = SystemClock.uptimeMillis();
                    } else {
                        jUptimeMillis = 0;
                    }
                    next.target.dispatchMessage(next);
                    if (z3) {
                        jUptimeMillis2 = SystemClock.uptimeMillis();
                    } else {
                        jUptimeMillis2 = 0;
                    }
                    if (j3 != 0) {
                        Trace.traceEnd(j3);
                    }
                    if (!z2) {
                        printer = printer2;
                        z6 = z;
                    } else if (z) {
                        printer = printer2;
                        if (!showSlowLog(j2, next.when, jUptimeMillis, "delivery", next)) {
                            z6 = z;
                        }
                    } else if (jUptimeMillis - next.when <= 10) {
                        Slog.w(TAG, "Drained");
                        printer = printer2;
                        z6 = false;
                    } else {
                        printer = printer2;
                        z6 = z;
                    }
                    if (z3) {
                        showSlowLog(j, jUptimeMillis, jUptimeMillis2, "dispatch", next);
                    }
                    if (printer != null) {
                        printer.println("<<<<< Finished to " + next.target + WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + next.callback);
                    }
                    jClearCallingIdentity = Binder.clearCallingIdentity();
                    if (jClearCallingIdentity2 != jClearCallingIdentity) {
                        Log.wtf(TAG, "Thread identity changed from 0x" + Long.toHexString(jClearCallingIdentity2) + " to 0x" + Long.toHexString(jClearCallingIdentity) + " while dispatching to " + next.target.getClass().getName() + WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + next.callback + " what=" + next.what);
                    }
                    next.recycleUnchecked();
                    z5 = z6;
                } else {
                    z = z5;
                }
                next.target.dispatchMessage(next);
                if (z3) {
                    jUptimeMillis2 = SystemClock.uptimeMillis();
                } else {
                    jUptimeMillis2 = 0;
                }
                if (j3 != 0) {
                    Trace.traceEnd(j3);
                }
                if (!z2) {
                    printer = printer2;
                    z6 = z;
                } else if (z) {
                    printer = printer2;
                    if (!showSlowLog(j2, next.when, jUptimeMillis, "delivery", next)) {
                        z6 = z;
                    }
                } else if (jUptimeMillis - next.when <= 10) {
                    Slog.w(TAG, "Drained");
                    printer = printer2;
                    z6 = false;
                } else {
                    printer = printer2;
                    z6 = z;
                }
                if (z3) {
                    showSlowLog(j, jUptimeMillis, jUptimeMillis2, "dispatch", next);
                }
                if (printer != null) {
                    printer.println("<<<<< Finished to " + next.target + WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + next.callback);
                }
                jClearCallingIdentity = Binder.clearCallingIdentity();
                if (jClearCallingIdentity2 != jClearCallingIdentity) {
                    Log.wtf(TAG, "Thread identity changed from 0x" + Long.toHexString(jClearCallingIdentity2) + " to 0x" + Long.toHexString(jClearCallingIdentity) + " while dispatching to " + next.target.getClass().getName() + WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + next.callback + " what=" + next.what);
                }
                next.recycleUnchecked();
                z5 = z6;
            } catch (Throwable th) {
                if (j3 != 0) {
                    Trace.traceEnd(j3);
                }
                throw th;
            }
            if (j > 0) {
                z3 = true;
            } else {
                z3 = false;
            }
            if (z2) {
                z4 = true;
            } else {
                z4 = true;
            }
            if (j3 != 0) {
                Trace.traceBegin(j3, next.target.getTraceName(next));
            }
            if (z4) {
                jUptimeMillis = SystemClock.uptimeMillis();
            } else {
                jUptimeMillis = 0;
            }
        }
    }

    public static Looper myLooper() {
        return sThreadLocal.get();
    }

    public static MessageQueue myQueue() {
        return myLooper().mQueue;
    }

    public static void prepare() {
        prepare(true);
    }

    private static void prepare(boolean z) {
        if (sThreadLocal.get() != null) {
            throw new RuntimeException("Only one Looper may be created per thread");
        }
        sThreadLocal.set(new Looper(z));
    }

    public static void prepareMainLooper() {
        prepare(false);
        synchronized (Looper.class) {
            if (sMainLooper != null) {
                throw new IllegalStateException("The main Looper has already been prepared.");
            }
            sMainLooper = myLooper();
        }
    }

    private static boolean showSlowLog(long j, long j2, long j3, String str, Message message) {
        long j4 = j3 - j2;
        if (j4 < j) {
            return false;
        }
        Slog.w(TAG, "Slow " + str + " took " + j4 + "ms " + Thread.currentThread().getName() + " h=" + message.target.getClass().getName() + " c=" + message.callback + " m=" + message.what);
        return true;
    }

    public void dump(Printer printer, String str) {
        printer.println(str + toString());
        this.mQueue.dump(printer, str + "  ", null);
    }

    public void dump(Printer printer, String str, Handler handler) {
        printer.println(str + toString());
        this.mQueue.dump(printer, str + "  ", handler);
    }

    public MessageQueue getQueue() {
        return this.mQueue;
    }

    public Thread getThread() {
        return this.mThread;
    }

    public boolean isCurrentThread() {
        return Thread.currentThread() == this.mThread;
    }

    public void quit() {
        this.mQueue.quit(false);
    }

    public void quitSafely() {
        this.mQueue.quit(true);
    }

    public void setMessageLogging(Printer printer) {
        this.mLogging = printer;
    }

    public void setSlowLogThresholdMs(long j, long j2) {
        this.mSlowDispatchThresholdMs = j;
        this.mSlowDeliveryThresholdMs = j2;
    }

    public void setTraceTag(long j) {
        this.mTraceTag = j;
    }

    public String toString() {
        return "Looper (" + this.mThread.getName() + ", tid " + this.mThread.getId() + ") {" + Integer.toHexString(System.identityHashCode(this)) + "}";
    }

    public void writeToProto(ProtoOutputStream protoOutputStream, long j) {
        long jStart = protoOutputStream.start(j);
        protoOutputStream.write(1138166333441L, this.mThread.getName());
        protoOutputStream.write(1112396529666L, this.mThread.getId());
        this.mQueue.writeToProto(protoOutputStream, 1146756268035L);
        protoOutputStream.end(jStart);
    }
}
