package android.app.slice;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class SliceManager {
    public static final String ACTION_REQUEST_SLICE_PERMISSION = "com.android.intent.action.REQUEST_SLICE_PERMISSION";
    public static final String CATEGORY_SLICE = "android.app.slice.category.SLICE";
    public static final String SLICE_METADATA_KEY = "android.metadata.SLICE_URI";
    private static final String TAG = "SliceManager";
    private final Context mContext;
    private final IBinder mToken = new Binder();
    private final ISliceManager mService = ISliceManager.Stub.asInterface(ServiceManager.getServiceOrThrow("slice"));

    private static /* synthetic */ void $closeResource(Throwable th, AutoCloseable autoCloseable) throws Exception {
        if (th == null) {
            autoCloseable.close();
            return;
        }
        try {
            autoCloseable.close();
        } catch (Throwable th2) {
            th.addSuppressed(th2);
        }
    }

    public SliceManager(Context context, Handler handler) throws ServiceManager.ServiceNotFoundException {
        this.mContext = context;
    }

    private String getAuthority(Intent intent) {
        Intent intent2 = new Intent(intent);
        if (!intent2.hasCategory(CATEGORY_SLICE)) {
            intent2.addCategory(CATEGORY_SLICE);
        }
        List<ResolveInfo> listQueryIntentContentProviders = this.mContext.getPackageManager().queryIntentContentProviders(intent2, 0);
        if (listQueryIntentContentProviders == null || listQueryIntentContentProviders.isEmpty()) {
            return null;
        }
        return listQueryIntentContentProviders.get(0).providerInfo.authority;
    }

    private Uri resolveStatic(Intent intent, ContentResolver contentResolver) {
        Preconditions.checkNotNull(intent, "intent");
        Preconditions.checkArgument((intent.getComponent() == null && intent.getPackage() == null && intent.getData() == null) ? false : true, "Slice intent must be explicit %s", intent);
        Uri data = intent.getData();
        if (data != null && SliceProvider.SLICE_TYPE.equals(contentResolver.getType(data))) {
            return data;
        }
        ResolveInfo resolveInfoResolveActivity = this.mContext.getPackageManager().resolveActivity(intent, 128);
        if (resolveInfoResolveActivity == null || resolveInfoResolveActivity.activityInfo == null || resolveInfoResolveActivity.activityInfo.metaData == null || !resolveInfoResolveActivity.activityInfo.metaData.containsKey(SLICE_METADATA_KEY)) {
            return null;
        }
        return Uri.parse(resolveInfoResolveActivity.activityInfo.metaData.getString(SLICE_METADATA_KEY));
    }

    @Deprecated
    public Slice bindSlice(Intent intent, List<SliceSpec> list) {
        return bindSlice(intent, new ArraySet(list));
    }

    /* JADX WARN: Code duplicated, block: B:42:0x00a2 A[Catch: RemoteException -> 0x00a6, TRY_ENTER, TryCatch #3 {RemoteException -> 0x00a6, blocks: (B:19:0x0052, B:23:0x0069, B:32:0x0088, B:36:0x0097, B:42:0x00a2, B:43:0x00a5), top: B:47:0x0052 }] */
    /* JADX WARN: Code duplicated, block: B:49:? A[Catch: RemoteException -> 0x00a6, SYNTHETIC, TRY_LEAVE, TryCatch #3 {RemoteException -> 0x00a6, blocks: (B:19:0x0052, B:23:0x0069, B:32:0x0088, B:36:0x0097, B:42:0x00a2, B:43:0x00a5), top: B:47:0x0052 }] */
    public Slice bindSlice(Intent intent, Set<SliceSpec> set) throws Exception {
        Throwable th;
        Preconditions.checkNotNull(intent, "intent");
        Preconditions.checkArgument((intent.getComponent() == null && intent.getPackage() == null && intent.getData() == null) ? false : true, "Slice intent must be explicit %s", intent);
        ContentResolver contentResolver = this.mContext.getContentResolver();
        Uri uriResolveStatic = resolveStatic(intent, contentResolver);
        if (uriResolveStatic != null) {
            return bindSlice(uriResolveStatic, set);
        }
        String authority = getAuthority(intent);
        if (authority == null) {
            return null;
        }
        Uri uriBuild = new Uri.Builder().scheme("content").authority(authority).build();
        try {
            ContentProviderClient contentProviderClientAcquireUnstableContentProviderClient = contentResolver.acquireUnstableContentProviderClient(uriBuild);
            try {
                if (contentProviderClientAcquireUnstableContentProviderClient == null) {
                    Log.w(TAG, String.format("Unknown URI: %s", uriBuild));
                    if (contentProviderClientAcquireUnstableContentProviderClient != null) {
                        $closeResource(null, contentProviderClientAcquireUnstableContentProviderClient);
                    }
                    return null;
                }
                Bundle bundle = new Bundle();
                bundle.putParcelable(SliceProvider.EXTRA_INTENT, intent);
                Bundle bundleCall = contentProviderClientAcquireUnstableContentProviderClient.call(SliceProvider.METHOD_MAP_INTENT, null, bundle);
                if (bundleCall == null) {
                    if (contentProviderClientAcquireUnstableContentProviderClient != null) {
                        $closeResource(null, contentProviderClientAcquireUnstableContentProviderClient);
                    }
                    return null;
                }
                Slice slice = (Slice) bundleCall.getParcelable("slice");
                if (contentProviderClientAcquireUnstableContentProviderClient != null) {
                    $closeResource(null, contentProviderClientAcquireUnstableContentProviderClient);
                }
                return slice;
            } catch (Throwable th2) {
                th = th2;
                th = null;
                if (contentProviderClientAcquireUnstableContentProviderClient != null) {
                    throw th;
                }
                $closeResource(th, contentProviderClientAcquireUnstableContentProviderClient);
                throw th;
            }
        } catch (RemoteException unused) {
            return null;
        }
    }

    @Deprecated
    public Slice bindSlice(Uri uri, List<SliceSpec> list) {
        return bindSlice(uri, new ArraySet(list));
    }

    /* JADX WARN: Code duplicated, block: B:27:0x006c A[Catch: RemoteException -> 0x0070, TRY_ENTER, TryCatch #0 {RemoteException -> 0x0070, blocks: (B:3:0x000d, B:8:0x0026, B:17:0x0052, B:21:0x0061, B:27:0x006c, B:28:0x006f), top: B:30:0x000d }] */
    /* JADX WARN: Code duplicated, block: B:34:? A[Catch: RemoteException -> 0x0070, SYNTHETIC, TRY_LEAVE, TryCatch #0 {RemoteException -> 0x0070, blocks: (B:3:0x000d, B:8:0x0026, B:17:0x0052, B:21:0x0061, B:27:0x006c, B:28:0x006f), top: B:30:0x000d }] */
    public Slice bindSlice(Uri uri, Set<SliceSpec> set) throws Exception {
        Throwable th;
        Preconditions.checkNotNull(uri, "uri");
        try {
            ContentProviderClient contentProviderClientAcquireUnstableContentProviderClient = this.mContext.getContentResolver().acquireUnstableContentProviderClient(uri);
            try {
                if (contentProviderClientAcquireUnstableContentProviderClient == null) {
                    Log.w(TAG, String.format("Unknown URI: %s", uri));
                    if (contentProviderClientAcquireUnstableContentProviderClient != null) {
                        $closeResource(null, contentProviderClientAcquireUnstableContentProviderClient);
                    }
                    return null;
                }
                Bundle bundle = new Bundle();
                bundle.putParcelable(SliceProvider.EXTRA_BIND_URI, uri);
                bundle.putParcelableArrayList(SliceProvider.EXTRA_SUPPORTED_SPECS, new ArrayList<>(set));
                Bundle bundleCall = contentProviderClientAcquireUnstableContentProviderClient.call(SliceProvider.METHOD_SLICE, null, bundle);
                Bundle.setDefusable(bundleCall, true);
                if (bundleCall == null) {
                    if (contentProviderClientAcquireUnstableContentProviderClient != null) {
                        $closeResource(null, contentProviderClientAcquireUnstableContentProviderClient);
                    }
                    return null;
                }
                Slice slice = (Slice) bundleCall.getParcelable("slice");
                if (contentProviderClientAcquireUnstableContentProviderClient != null) {
                    $closeResource(null, contentProviderClientAcquireUnstableContentProviderClient);
                }
                return slice;
            } catch (Throwable th2) {
                th = th2;
                th = null;
                if (contentProviderClientAcquireUnstableContentProviderClient != null) {
                    throw th;
                }
                $closeResource(th, contentProviderClientAcquireUnstableContentProviderClient);
                throw th;
            }
        } catch (RemoteException unused) {
            return null;
        }
    }

    public int checkSlicePermission(Uri uri, int i, int i2) {
        try {
            return this.mService.checkSlicePermission(uri, null, i, i2, null);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void enforceSlicePermission(Uri uri, String str, int i, int i2, String[] strArr) {
        try {
            if (UserHandle.isSameApp(i2, Process.myUid())) {
                return;
            }
            if (str == null) {
                throw new SecurityException("No pkg specified");
            }
            if (this.mService.checkSlicePermission(uri, str, i, i2, strArr) != -1) {
                return;
            }
            throw new SecurityException("User " + i2 + " does not have slice permission for " + uri + ".");
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public List<Uri> getPinnedSlices() {
        try {
            return Arrays.asList(this.mService.getPinnedSlices(this.mContext.getPackageName()));
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public Set<SliceSpec> getPinnedSpecs(Uri uri) {
        try {
            return new ArraySet(Arrays.asList(this.mService.getPinnedSpecs(uri, this.mContext.getPackageName())));
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public Collection<Uri> getSliceDescendants(Uri uri) throws Exception {
        try {
            ContentProviderClient contentProviderClientAcquireUnstableContentProviderClient = this.mContext.getContentResolver().acquireUnstableContentProviderClient(uri);
            Throwable th = null;
            try {
                try {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(SliceProvider.EXTRA_BIND_URI, uri);
                    ArrayList parcelableArrayList = contentProviderClientAcquireUnstableContentProviderClient.call(SliceProvider.METHOD_GET_DESCENDANTS, null, bundle).getParcelableArrayList(SliceProvider.EXTRA_SLICE_DESCENDANTS);
                    if (contentProviderClientAcquireUnstableContentProviderClient != null) {
                        $closeResource(null, contentProviderClientAcquireUnstableContentProviderClient);
                    }
                    return parcelableArrayList;
                } catch (Throwable th2) {
                    th = th2;
                    throw th;
                }
            } catch (Throwable th3) {
                if (contentProviderClientAcquireUnstableContentProviderClient != null) {
                    $closeResource(th, contentProviderClientAcquireUnstableContentProviderClient);
                }
                throw th3;
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Unable to get slice descendants", e);
            return Collections.emptyList();
        }
    }

    public void grantPermissionFromUser(Uri uri, String str, boolean z) {
        try {
            this.mService.grantPermissionFromUser(uri, str, this.mContext.getPackageName(), z);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void grantSlicePermission(String str, Uri uri) {
        try {
            this.mService.grantSlicePermission(this.mContext.getPackageName(), str, uri);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean hasSliceAccess() {
        try {
            return this.mService.hasSliceAccess(this.mContext.getPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* JADX WARN: Code duplicated, block: B:32:0x007a A[Catch: RemoteException -> 0x007e, TRY_ENTER, TryCatch #2 {RemoteException -> 0x007e, blocks: (B:9:0x0028, B:13:0x0041, B:22:0x0060, B:26:0x006f, B:32:0x007a, B:33:0x007d), top: B:37:0x0028 }] */
    /* JADX WARN: Code duplicated, block: B:39:? A[Catch: RemoteException -> 0x007e, SYNTHETIC, TRY_LEAVE, TryCatch #2 {RemoteException -> 0x007e, blocks: (B:9:0x0028, B:13:0x0041, B:22:0x0060, B:26:0x006f, B:32:0x007a, B:33:0x007d), top: B:37:0x0028 }] */
    public Uri mapIntentToUri(Intent intent) throws Exception {
        Throwable th;
        Throwable th2;
        ContentResolver contentResolver = this.mContext.getContentResolver();
        Uri uriResolveStatic = resolveStatic(intent, contentResolver);
        if (uriResolveStatic != null) {
            return uriResolveStatic;
        }
        String authority = getAuthority(intent);
        if (authority == null) {
            return null;
        }
        Uri uriBuild = new Uri.Builder().scheme("content").authority(authority).build();
        try {
            ContentProviderClient contentProviderClientAcquireUnstableContentProviderClient = contentResolver.acquireUnstableContentProviderClient(uriBuild);
            try {
                if (contentProviderClientAcquireUnstableContentProviderClient == null) {
                    Log.w(TAG, String.format("Unknown URI: %s", uriBuild));
                    if (contentProviderClientAcquireUnstableContentProviderClient != null) {
                        $closeResource(null, contentProviderClientAcquireUnstableContentProviderClient);
                    }
                    return null;
                }
                Bundle bundle = new Bundle();
                bundle.putParcelable(SliceProvider.EXTRA_INTENT, intent);
                Bundle bundleCall = contentProviderClientAcquireUnstableContentProviderClient.call(SliceProvider.METHOD_MAP_ONLY_INTENT, null, bundle);
                if (bundleCall == null) {
                    if (contentProviderClientAcquireUnstableContentProviderClient != null) {
                        $closeResource(null, contentProviderClientAcquireUnstableContentProviderClient);
                    }
                    return null;
                }
                Uri uri = (Uri) bundleCall.getParcelable("slice");
                if (contentProviderClientAcquireUnstableContentProviderClient != null) {
                    $closeResource(null, contentProviderClientAcquireUnstableContentProviderClient);
                }
                return uri;
            } catch (Throwable th3) {
                try {
                    throw th3;
                } catch (Throwable th4) {
                    th = th3;
                    th2 = th4;
                    if (contentProviderClientAcquireUnstableContentProviderClient != null) {
                        throw th2;
                    }
                    $closeResource(th, contentProviderClientAcquireUnstableContentProviderClient);
                    throw th2;
                }
            }
        } catch (RemoteException unused) {
            return null;
        }
    }

    @Deprecated
    public void pinSlice(Uri uri, List<SliceSpec> list) {
        pinSlice(uri, new ArraySet(list));
    }

    public void pinSlice(Uri uri, Set<SliceSpec> set) {
        try {
            this.mService.pinSlice(this.mContext.getPackageName(), uri, (SliceSpec[]) set.toArray(new SliceSpec[set.size()]), this.mToken);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void revokeSlicePermission(String str, Uri uri) {
        try {
            this.mService.revokeSlicePermission(this.mContext.getPackageName(), str, uri);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void unpinSlice(Uri uri) {
        try {
            this.mService.unpinSlice(this.mContext.getPackageName(), uri, this.mToken);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }
}
