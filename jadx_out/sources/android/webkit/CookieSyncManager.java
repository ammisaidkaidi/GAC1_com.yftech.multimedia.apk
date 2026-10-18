package android.webkit;

import android.content.Context;

/* JADX INFO: loaded from: classes2.dex */
@Deprecated
public final class CookieSyncManager extends WebSyncManager {
    private static boolean sGetInstanceAllowed = false;
    private static final Object sLock = new Object();
    private static CookieSyncManager sRef;

    private CookieSyncManager() {
        super(null, null);
    }

    private static void checkInstanceIsAllowed() {
        if (!sGetInstanceAllowed) {
            throw new IllegalStateException("CookieSyncManager::createInstance() needs to be called before CookieSyncManager::getInstance()");
        }
    }

    public static CookieSyncManager createInstance(Context context) {
        CookieSyncManager cookieSyncManager;
        synchronized (sLock) {
            try {
                if (context == null) {
                    throw new IllegalArgumentException("Invalid context argument");
                }
                setGetInstanceIsAllowed();
                cookieSyncManager = getInstance();
            } catch (Throwable th) {
                throw th;
            }
        }
        return cookieSyncManager;
    }

    public static CookieSyncManager getInstance() {
        CookieSyncManager cookieSyncManager;
        synchronized (sLock) {
            checkInstanceIsAllowed();
            if (sRef == null) {
                sRef = new CookieSyncManager();
            }
            cookieSyncManager = sRef;
        }
        return cookieSyncManager;
    }

    static void setGetInstanceIsAllowed() {
        sGetInstanceAllowed = true;
    }

    @Override // android.webkit.WebSyncManager
    @Deprecated
    public void resetSync() {
    }

    @Override // android.webkit.WebSyncManager, java.lang.Runnable
    public /* bridge */ /* synthetic */ void run() {
        super.run();
    }

    @Override // android.webkit.WebSyncManager
    @Deprecated
    public void startSync() {
    }

    @Override // android.webkit.WebSyncManager
    @Deprecated
    public void stopSync() {
    }

    @Override // android.webkit.WebSyncManager
    @Deprecated
    public void sync() {
        CookieManager.getInstance().flush();
    }

    @Override // android.webkit.WebSyncManager
    @Deprecated
    protected void syncFromRamToFlash() {
        CookieManager.getInstance().flush();
    }
}
