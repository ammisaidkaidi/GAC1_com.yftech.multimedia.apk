package android.os;

import android.content.Context;
import android.util.Log;

/* JADX INFO: loaded from: classes2.dex */
public class UpdateLock {
    private static final boolean DEBUG = false;
    public static final String NOW_IS_CONVENIENT = "nowisconvenient";
    private static final String TAG = "UpdateLock";
    public static final String TIMESTAMP = "timestamp";
    public static final String UPDATE_LOCK_CHANGED = "android.os.UpdateLock.UPDATE_LOCK_CHANGED";
    private static IUpdateLock sService;
    final String mTag;
    int mCount = 0;
    boolean mRefCounted = true;
    boolean mHeld = false;
    IBinder mToken = new Binder();

    public UpdateLock(String str) {
        this.mTag = str;
    }

    private void acquireLocked() {
        if (this.mRefCounted) {
            int i = this.mCount;
            this.mCount = i + 1;
            if (i != 0) {
                return;
            }
        }
        if (sService != null) {
            try {
                sService.acquireUpdateLock(this.mToken, this.mTag);
            } catch (RemoteException unused) {
                Log.e(TAG, "Unable to contact service to acquire");
            }
        }
        this.mHeld = true;
    }

    private static void checkService() {
        if (sService == null) {
            sService = IUpdateLock.Stub.asInterface(ServiceManager.getService(Context.UPDATE_LOCK_SERVICE));
        }
    }

    /* JADX WARN: Code duplicated, block: B:17:0x0010 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:6:0x000c  */
    private void releaseLocked() {
        if (this.mRefCounted) {
            int i = this.mCount - 1;
            this.mCount = i;
            if (i == 0) {
                if (sService != null) {
                    try {
                        sService.releaseUpdateLock(this.mToken);
                    } catch (RemoteException unused) {
                        Log.e(TAG, "Unable to contact service to release");
                    }
                }
                this.mHeld = false;
            }
        } else {
            if (sService != null) {
                sService.releaseUpdateLock(this.mToken);
            }
            this.mHeld = false;
        }
        if (this.mCount < 0) {
            throw new RuntimeException("UpdateLock under-locked");
        }
    }

    public void acquire() {
        checkService();
        synchronized (this.mToken) {
            acquireLocked();
        }
    }

    protected void finalize() throws Throwable {
        synchronized (this.mToken) {
            if (this.mHeld) {
                Log.wtf(TAG, "UpdateLock finalized while still held");
                try {
                    sService.releaseUpdateLock(this.mToken);
                } catch (RemoteException unused) {
                    Log.e(TAG, "Unable to contact service to release");
                }
            }
        }
    }

    public boolean isHeld() {
        boolean z;
        synchronized (this.mToken) {
            z = this.mHeld;
        }
        return z;
    }

    public void release() {
        checkService();
        synchronized (this.mToken) {
            releaseLocked();
        }
    }

    public void setReferenceCounted(boolean z) {
        this.mRefCounted = z;
    }
}
