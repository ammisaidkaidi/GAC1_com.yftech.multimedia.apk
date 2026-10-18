package android.app.admin;

import com.android.server.LocalServices;

/* JADX INFO: loaded from: classes.dex */
public abstract class DevicePolicyCache {

    private static class EmptyDevicePolicyCache extends DevicePolicyCache {
        private static final EmptyDevicePolicyCache INSTANCE = new EmptyDevicePolicyCache();

        private EmptyDevicePolicyCache() {
        }

        @Override // android.app.admin.DevicePolicyCache
        public boolean getScreenCaptureDisabled(int i) {
            return false;
        }
    }

    protected DevicePolicyCache() {
    }

    public static DevicePolicyCache getInstance() {
        DevicePolicyManagerInternal devicePolicyManagerInternal = (DevicePolicyManagerInternal) LocalServices.getService(DevicePolicyManagerInternal.class);
        return devicePolicyManagerInternal != null ? devicePolicyManagerInternal.getDevicePolicyCache() : EmptyDevicePolicyCache.INSTANCE;
    }

    public abstract boolean getScreenCaptureDisabled(int i);
}
