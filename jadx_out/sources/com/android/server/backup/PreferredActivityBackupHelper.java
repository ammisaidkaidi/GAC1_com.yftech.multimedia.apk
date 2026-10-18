package com.android.server.backup;

import android.app.AppGlobals;
import android.app.backup.BlobBackupHelper;
import android.content.pm.IPackageManager;
import android.util.Slog;

/* JADX INFO: loaded from: classes3.dex */
public class PreferredActivityBackupHelper extends BlobBackupHelper {
    private static final boolean DEBUG = false;
    private static final String KEY_DEFAULT_APPS = "default-apps";
    private static final String KEY_INTENT_VERIFICATION = "intent-verification";
    private static final String KEY_PREFERRED = "preferred-activity";
    private static final int STATE_VERSION = 3;
    private static final String TAG = "PreferredBackup";

    public PreferredActivityBackupHelper() {
        super(3, KEY_PREFERRED, KEY_DEFAULT_APPS, KEY_INTENT_VERIFICATION);
    }

    @Override // android.app.backup.BlobBackupHelper
    protected void applyRestoredPayload(String str, byte[] bArr) {
        IPackageManager packageManager = AppGlobals.getPackageManager();
        try {
            switch (str) {
                case "preferred-activity":
                    packageManager.restorePreferredActivities(bArr, 0);
                    break;
                case "default-apps":
                    packageManager.restoreDefaultApps(bArr, 0);
                    break;
                case "intent-verification":
                    packageManager.restoreIntentFilterVerification(bArr, 0);
                    break;
                default:
                    Slog.w(TAG, "Unexpected restore key " + str);
                    break;
            }
        } catch (Exception unused) {
            Slog.w(TAG, "Unable to restore key " + str);
        }
    }

    @Override // android.app.backup.BlobBackupHelper
    protected byte[] getBackupPayload(String str) {
        IPackageManager packageManager = AppGlobals.getPackageManager();
        try {
            switch (str) {
                case "preferred-activity":
                    return packageManager.getPreferredActivityBackup(0);
                case "default-apps":
                    return packageManager.getDefaultAppsBackup(0);
                case "intent-verification":
                    return packageManager.getIntentFilterVerificationBackup(0);
                default:
                    Slog.w(TAG, "Unexpected backup key " + str);
                    return null;
            }
        } catch (Exception unused) {
            Slog.e(TAG, "Unable to store payload " + str);
            return null;
        }
    }
}
