package android.renderscript;

import com.bumptech.glide.load.Key;
import dalvik.system.CloseGuard;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/* JADX INFO: loaded from: classes2.dex */
public class BaseObj {
    final CloseGuard guard = CloseGuard.get();
    private boolean mDestroyed;
    private long mID;
    private String mName;
    RenderScript mRS;

    BaseObj(long j, RenderScript renderScript) {
        renderScript.validate();
        this.mRS = renderScript;
        this.mID = j;
        this.mDestroyed = false;
    }

    private void helpDestroy() {
        boolean z;
        synchronized (this) {
            z = true;
            if (this.mDestroyed) {
                z = false;
            } else {
                this.mDestroyed = true;
            }
        }
        if (z) {
            this.guard.close();
            ReentrantReadWriteLock.ReadLock lock = this.mRS.mRWLock.readLock();
            lock.lock();
            if (this.mRS.isAlive() && this.mID != 0) {
                this.mRS.nObjDestroy(this.mID);
            }
            lock.unlock();
            this.mRS = null;
            this.mID = 0L;
        }
    }

    void checkValid() {
        if (this.mID == 0) {
            throw new RSIllegalArgumentException("Invalid object.");
        }
    }

    public void destroy() {
        if (this.mDestroyed) {
            throw new RSInvalidStateException("Object already destroyed.");
        }
        helpDestroy();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return obj != null && getClass() == obj.getClass() && this.mID == ((BaseObj) obj).mID;
    }

    protected void finalize() throws Throwable {
        try {
            if (this.guard != null) {
                this.guard.warnIfOpen();
            }
            helpDestroy();
        } finally {
            super.finalize();
        }
    }

    long getID(RenderScript renderScript) {
        this.mRS.validate();
        if (this.mDestroyed) {
            throw new RSInvalidStateException("using a destroyed object.");
        }
        if (this.mID == 0) {
            throw new RSRuntimeException("Internal error: Object id 0.");
        }
        if (renderScript == null || renderScript == this.mRS) {
            return this.mID;
        }
        throw new RSInvalidStateException("using object with mismatched context.");
    }

    public String getName() {
        return this.mName;
    }

    public int hashCode() {
        return (int) ((this.mID & 268435455) ^ (this.mID >> 32));
    }

    void setID(long j) {
        if (this.mID != 0) {
            throw new RSRuntimeException("Internal Error, reset of object ID.");
        }
        this.mID = j;
    }

    public void setName(String str) {
        if (str == null) {
            throw new RSIllegalArgumentException("setName requires a string of non-zero length.");
        }
        if (str.length() < 1) {
            throw new RSIllegalArgumentException("setName does not accept a zero length string.");
        }
        if (this.mName != null) {
            throw new RSIllegalArgumentException("setName object already has a name.");
        }
        try {
            this.mRS.nAssignName(this.mID, str.getBytes(Key.STRING_CHARSET_NAME));
            this.mName = str;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    void updateFromNative() {
        this.mRS.validate();
        this.mName = this.mRS.nGetName(getID(this.mRS));
    }
}
