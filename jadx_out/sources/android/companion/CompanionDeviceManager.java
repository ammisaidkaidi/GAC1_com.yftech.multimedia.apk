package android.companion;

import android.app.Activity;
import android.app.Application;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import com.android.internal.util.Preconditions;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

/* JADX INFO: loaded from: classes.dex */
public final class CompanionDeviceManager {
    public static final String COMPANION_DEVICE_DISCOVERY_PACKAGE_NAME = "com.android.companiondevicemanager";
    private static final boolean DEBUG = false;
    public static final String EXTRA_DEVICE = "android.companion.extra.DEVICE";
    private static final String LOG_TAG = "CompanionDeviceManager";
    private final Context mContext;
    private final ICompanionDeviceManager mService;

    public static abstract class Callback {
        public abstract void onDeviceFound(IntentSender intentSender);

        public abstract void onFailure(CharSequence charSequence);
    }

    /* JADX INFO: Access modifiers changed from: private */
    class CallbackProxy extends IFindDeviceCallback.Stub implements Application.ActivityLifecycleCallbacks {
        private Callback mCallback;
        private Handler mHandler;
        final Object mLock;
        private AssociationRequest mRequest;

        private CallbackProxy(AssociationRequest associationRequest, Callback callback, Handler handler) {
            this.mLock = new Object();
            this.mCallback = callback;
            this.mHandler = handler;
            this.mRequest = associationRequest;
            CompanionDeviceManager.this.getActivity().getApplication().registerActivityLifecycleCallbacks(this);
        }

        public static /* synthetic */ void lambda$lockAndPost$0(CallbackProxy callbackProxy, BiConsumer biConsumer, Object obj) {
            Callback callback;
            synchronized (callbackProxy.mLock) {
                callback = callbackProxy.mCallback;
            }
            if (callback != null) {
                biConsumer.accept(callback, obj);
            }
        }

        <T> void lockAndPost(final BiConsumer<Callback, T> biConsumer, final T t) {
            synchronized (this.mLock) {
                if (this.mHandler != null) {
                    this.mHandler.post(new Runnable() { // from class: android.companion.-$$Lambda$CompanionDeviceManager$CallbackProxy$gkUVA3m3QgEEk8G84_kcBFARHvo
                        @Override // java.lang.Runnable
                        public final void run() {
                            CompanionDeviceManager.CallbackProxy.lambda$lockAndPost$0(this.f$0, biConsumer, t);
                        }
                    });
                }
            }
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityCreated(Activity activity, Bundle bundle) {
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityDestroyed(Activity activity) {
            synchronized (this.mLock) {
                if (activity != CompanionDeviceManager.this.getActivity()) {
                    return;
                }
                try {
                    CompanionDeviceManager.this.mService.stopScan(this.mRequest, this, CompanionDeviceManager.this.getCallingPackage());
                } catch (RemoteException e) {
                    e.rethrowFromSystemServer();
                }
                CompanionDeviceManager.this.getActivity().getApplication().unregisterActivityLifecycleCallbacks(this);
                this.mCallback = null;
                this.mHandler = null;
                this.mRequest = null;
            }
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityPaused(Activity activity) {
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityResumed(Activity activity) {
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityStarted(Activity activity) {
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityStopped(Activity activity) {
        }

        @Override // android.companion.IFindDeviceCallback
        public void onFailure(CharSequence charSequence) {
            lockAndPost(new BiConsumer() { // from class: android.companion.-$$Lambda$ZUPGnRMz08ZrG1ogNO-2O5Hso3I
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    ((CompanionDeviceManager.Callback) obj).onFailure((CharSequence) obj2);
                }
            }, charSequence);
        }

        @Override // android.companion.IFindDeviceCallback
        public void onSuccess(PendingIntent pendingIntent) {
            lockAndPost(new BiConsumer() { // from class: android.companion.-$$Lambda$OThxsns9MAD5QsKURFQAFbt-3qc
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    ((CompanionDeviceManager.Callback) obj).onDeviceFound((IntentSender) obj2);
                }
            }, pendingIntent.getIntentSender());
        }
    }

    public CompanionDeviceManager(ICompanionDeviceManager iCompanionDeviceManager, Context context) {
        this.mService = iCompanionDeviceManager;
        this.mContext = context;
    }

    private boolean checkFeaturePresent() {
        return this.mService != null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Activity getActivity() {
        return (Activity) this.mContext;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getCallingPackage() {
        return this.mContext.getPackageName();
    }

    public void associate(AssociationRequest associationRequest, Callback callback, Handler handler) {
        if (checkFeaturePresent()) {
            Preconditions.checkNotNull(associationRequest, "Request cannot be null");
            Preconditions.checkNotNull(callback, "Callback cannot be null");
            try {
                this.mService.associate(associationRequest, new CallbackProxy(associationRequest, callback, Handler.mainIfNull(handler)), getCallingPackage());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void disassociate(String str) {
        if (checkFeaturePresent()) {
            try {
                this.mService.disassociate(str, getCallingPackage());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public List<String> getAssociations() {
        if (!checkFeaturePresent()) {
            return Collections.emptyList();
        }
        try {
            return this.mService.getAssociations(getCallingPackage(), this.mContext.getUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean hasNotificationAccess(ComponentName componentName) {
        if (!checkFeaturePresent()) {
            return false;
        }
        try {
            return this.mService.hasNotificationAccess(componentName);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void requestNotificationAccess(ComponentName componentName) {
        if (checkFeaturePresent()) {
            try {
                this.mContext.startIntentSender(this.mService.requestNotificationAccess(componentName).getIntentSender(), null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                throw new RuntimeException(e);
            } catch (RemoteException e2) {
                throw e2.rethrowFromSystemServer();
            }
        }
    }
}
