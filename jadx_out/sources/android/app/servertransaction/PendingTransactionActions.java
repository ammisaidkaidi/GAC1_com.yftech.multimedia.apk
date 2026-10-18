package android.app.servertransaction;

import android.app.ActivityManager;
import android.app.ActivityThread;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.os.TransactionTooLargeException;
import android.util.Log;
import android.util.LogWriter;
import com.android.internal.util.IndentingPrintWriter;

/* JADX INFO: loaded from: classes.dex */
public class PendingTransactionActions {
    private boolean mCallOnPostCreate;
    private Bundle mOldState;
    private boolean mReportRelaunchToWM;
    private boolean mRestoreInstanceState;
    private StopInfo mStopInfo;

    public static class StopInfo implements Runnable {
        private static final String TAG = "ActivityStopInfo";
        private ActivityThread.ActivityClientRecord mActivity;
        private CharSequence mDescription;
        private PersistableBundle mPersistentState;
        private Bundle mState;

        @Override // java.lang.Runnable
        public void run() {
            try {
                ActivityManager.getService().activityStopped(this.mActivity.token, this.mState, this.mPersistentState, this.mDescription);
            } catch (RemoteException e) {
                IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(new LogWriter(5, TAG), "  ");
                indentingPrintWriter.println("Bundle stats:");
                Bundle.dumpStats(indentingPrintWriter, this.mState);
                indentingPrintWriter.println("PersistableBundle stats:");
                Bundle.dumpStats(indentingPrintWriter, this.mPersistentState);
                if (!(e instanceof TransactionTooLargeException) || this.mActivity.packageInfo.getTargetSdkVersion() >= 24) {
                    throw e.rethrowFromSystemServer();
                }
                Log.e(TAG, "App sent too much data in instance state, so it was ignored", e);
            }
        }

        public void setActivity(ActivityThread.ActivityClientRecord activityClientRecord) {
            this.mActivity = activityClientRecord;
        }

        public void setDescription(CharSequence charSequence) {
            this.mDescription = charSequence;
        }

        public void setPersistentState(PersistableBundle persistableBundle) {
            this.mPersistentState = persistableBundle;
        }

        public void setState(Bundle bundle) {
            this.mState = bundle;
        }
    }

    public PendingTransactionActions() {
        clear();
    }

    public void clear() {
        this.mRestoreInstanceState = false;
        this.mCallOnPostCreate = false;
        this.mOldState = null;
        this.mStopInfo = null;
    }

    public Bundle getOldState() {
        return this.mOldState;
    }

    public StopInfo getStopInfo() {
        return this.mStopInfo;
    }

    public void setCallOnPostCreate(boolean z) {
        this.mCallOnPostCreate = z;
    }

    public void setOldState(Bundle bundle) {
        this.mOldState = bundle;
    }

    public void setReportRelaunchToWindowManager(boolean z) {
        this.mReportRelaunchToWM = z;
    }

    public void setRestoreInstanceState(boolean z) {
        this.mRestoreInstanceState = z;
    }

    public void setStopInfo(StopInfo stopInfo) {
        this.mStopInfo = stopInfo;
    }

    public boolean shouldCallOnPostCreate() {
        return this.mCallOnPostCreate;
    }

    public boolean shouldReportRelaunchToWindowManager() {
        return this.mReportRelaunchToWM;
    }

    public boolean shouldRestoreInstanceState() {
        return this.mRestoreInstanceState;
    }
}
