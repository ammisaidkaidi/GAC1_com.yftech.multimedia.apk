package android.media;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.net.Uri;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class MediaInserter {
    private static final boolean ATC_AOSP_ENHANCEMENT = SystemProperties.getBoolean("ro.atc.aosp_enhancement", false);
    private static final String TAG = "MediaInserter";
    private final int mBufferSizePerUri;
    private final ContentProviderClient mProvider;
    private boolean mDebug = SystemProperties.getBoolean("persist.mediascanner.log.on", false);
    private final HashMap<Uri, List<ContentValues>> mRowMap = new HashMap<>();
    private final HashMap<Uri, List<ContentValues>> mPriorityRowMap = new HashMap<>();
    private final Object mInsertLock = new Object();

    public MediaInserter(ContentProviderClient contentProviderClient, int i) {
        this.mProvider = contentProviderClient;
        this.mBufferSizePerUri = i;
    }

    private void flush(Uri uri, List<ContentValues> list) throws RemoteException {
        if (list.isEmpty()) {
            return;
        }
        if (!ATC_AOSP_ENHANCEMENT) {
            this.mProvider.bulkInsert(uri, (ContentValues[]) list.toArray(new ContentValues[list.size()]));
            list.clear();
        } else {
            synchronized (this.mInsertLock) {
                this.mProvider.bulkInsert(uri, (ContentValues[]) list.toArray(new ContentValues[list.size()]));
                list.clear();
            }
        }
    }

    private void flushAllPriority() throws RemoteException {
        int size = 0;
        for (Uri uri : this.mPriorityRowMap.keySet()) {
            List<ContentValues> list = this.mPriorityRowMap.get(uri);
            if (ATC_AOSP_ENHANCEMENT && this.mDebug) {
                size += list.size();
            }
            flush(uri, list);
        }
        this.mPriorityRowMap.clear();
        if (ATC_AOSP_ENHANCEMENT && this.mDebug) {
            System.currentTimeMillis();
            Log.d(TAG, "flushAllPriority, size:" + size);
        }
    }

    private void insert(Uri uri, ContentValues contentValues, boolean z) throws RemoteException {
        HashMap<Uri, List<ContentValues>> map = z ? this.mPriorityRowMap : this.mRowMap;
        List<ContentValues> arrayList = map.get(uri);
        if (arrayList == null) {
            arrayList = new ArrayList<>();
            map.put(uri, arrayList);
        }
        arrayList.add(new ContentValues(contentValues));
        if (arrayList.size() >= this.mBufferSizePerUri) {
            flushAllPriority();
            flush(uri, arrayList);
        }
    }

    public void flushAll() throws RemoteException {
        List<ContentValues> list;
        long jCurrentTimeMillis = System.currentTimeMillis();
        flushAllPriority();
        int size = 0;
        for (Uri uri : this.mRowMap.keySet()) {
            if (ATC_AOSP_ENHANCEMENT) {
                synchronized (this.mInsertLock) {
                    list = this.mRowMap.get(uri);
                }
            } else {
                list = this.mRowMap.get(uri);
            }
            if (ATC_AOSP_ENHANCEMENT && this.mDebug) {
                size += list.size();
            }
            flush(uri, list);
        }
        this.mRowMap.clear();
        if (ATC_AOSP_ENHANCEMENT && this.mDebug) {
            Log.d(TAG, "flushAll finished, size:" + size + ", cost:" + (System.currentTimeMillis() - jCurrentTimeMillis) + " ms \n");
        }
    }

    public void insert(Uri uri, ContentValues contentValues) throws RemoteException {
        insert(uri, contentValues, false);
    }

    public void insertwithPriority(Uri uri, ContentValues contentValues) throws RemoteException {
        insert(uri, contentValues, true);
    }
}
