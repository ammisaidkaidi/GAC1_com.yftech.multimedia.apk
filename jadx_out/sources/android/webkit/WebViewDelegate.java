package android.webkit;

import android.annotation.SystemApi;
import android.app.ActivityThread;
import android.app.Application;
import android.app.ResourcesManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.Trace;
import android.util.SparseArray;
import android.view.DisplayListCanvas;
import android.view.View;
import android.view.ViewRootImpl;
import com.android.internal.util.ArrayUtils;

/* JADX INFO: loaded from: classes2.dex */
@SystemApi
public final class WebViewDelegate {

    public interface OnTraceEnabledChangeListener {
        void onTraceEnabledChange(boolean z);
    }

    WebViewDelegate() {
    }

    public void addWebViewAssetPath(Context context) {
        String str = WebViewFactory.getLoadedPackageInfo().applicationInfo.sourceDir;
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        String[] strArr = applicationInfo.sharedLibraryFiles;
        if (ArrayUtils.contains(strArr, str)) {
            return;
        }
        int length = (strArr != null ? strArr.length : 0) + 1;
        String[] strArr2 = new String[length];
        if (strArr != null) {
            System.arraycopy(strArr, 0, strArr2, 0, strArr.length);
        }
        strArr2[length - 1] = str;
        applicationInfo.sharedLibraryFiles = strArr2;
        ResourcesManager.getInstance().appendLibAssetForMainAssetPath(applicationInfo.getBaseResourcePath(), str);
    }

    public void callDrawGlFunction(Canvas canvas, long j) {
        if (canvas instanceof DisplayListCanvas) {
            ((DisplayListCanvas) canvas).drawGLFunctor2(j, null);
            return;
        }
        throw new IllegalArgumentException(canvas.getClass().getName() + " is not a DisplayList canvas");
    }

    public void callDrawGlFunction(Canvas canvas, long j, Runnable runnable) {
        if (canvas instanceof DisplayListCanvas) {
            ((DisplayListCanvas) canvas).drawGLFunctor2(j, runnable);
            return;
        }
        throw new IllegalArgumentException(canvas.getClass().getName() + " is not a DisplayList canvas");
    }

    public boolean canInvokeDrawGlFunctor(View view) {
        return true;
    }

    public void detachDrawGlFunctor(View view, long j) {
        ViewRootImpl viewRootImpl = view.getViewRootImpl();
        if (j == 0 || viewRootImpl == null) {
            return;
        }
        viewRootImpl.detachFunctor(j);
    }

    public Application getApplication() {
        return ActivityThread.currentApplication();
    }

    public String getDataDirectorySuffix() {
        return WebViewFactory.getDataDirectorySuffix();
    }

    public String getErrorString(Context context, int i) {
        return LegacyErrorStrings.getString(i, context);
    }

    public int getPackageId(Resources resources, String str) {
        SparseArray<String> assignedPackageIdentifiers = resources.getAssets().getAssignedPackageIdentifiers();
        for (int i = 0; i < assignedPackageIdentifiers.size(); i++) {
            if (str.equals(assignedPackageIdentifiers.valueAt(i))) {
                return assignedPackageIdentifiers.keyAt(i);
            }
        }
        throw new RuntimeException("Package not found: " + str);
    }

    public void invokeDrawGlFunctor(View view, long j, boolean z) {
        ViewRootImpl.invokeFunctor(j, z);
    }

    public boolean isMultiProcessEnabled() {
        try {
            return WebViewFactory.getUpdateService().isMultiProcessEnabled();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isTraceTagEnabled() {
        return Trace.isTagEnabled(16L);
    }

    public void setOnTraceEnabledChangeListener(final OnTraceEnabledChangeListener onTraceEnabledChangeListener) {
        SystemProperties.addChangeCallback(new Runnable() { // from class: android.webkit.WebViewDelegate.1
            @Override // java.lang.Runnable
            public void run() {
                onTraceEnabledChangeListener.onTraceEnabledChange(WebViewDelegate.this.isTraceTagEnabled());
            }
        });
    }
}
