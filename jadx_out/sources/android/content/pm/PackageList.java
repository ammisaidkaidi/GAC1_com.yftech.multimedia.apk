package android.content.pm;

import com.android.server.LocalServices;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class PackageList implements PackageManagerInternal.PackageListObserver, AutoCloseable {
    private final List<String> mPackageNames;
    private final PackageManagerInternal.PackageListObserver mWrappedObserver;

    public PackageList(List<String> list, PackageManagerInternal.PackageListObserver packageListObserver) {
        this.mPackageNames = list;
        this.mWrappedObserver = packageListObserver;
    }

    @Override // java.lang.AutoCloseable
    public void close() throws Exception {
        ((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).removePackageListObserver(this);
    }

    public List<String> getPackageNames() {
        return this.mPackageNames;
    }

    @Override // android.content.pm.PackageManagerInternal.PackageListObserver
    public void onPackageAdded(String str) {
        if (this.mWrappedObserver != null) {
            this.mWrappedObserver.onPackageAdded(str);
        }
    }

    @Override // android.content.pm.PackageManagerInternal.PackageListObserver
    public void onPackageRemoved(String str) {
        if (this.mWrappedObserver != null) {
            this.mWrappedObserver.onPackageRemoved(str);
        }
    }
}
