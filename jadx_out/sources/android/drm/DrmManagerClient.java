package android.drm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import dalvik.system.CloseGuard;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/* JADX INFO: loaded from: classes.dex */
public class DrmManagerClient implements AutoCloseable {
    private static final int ACTION_PROCESS_DRM_INFO = 1002;
    private static final int ACTION_REMOVE_ALL_RIGHTS = 1001;
    public static final int ERROR_NONE = 0;
    public static final int ERROR_UNKNOWN = -2000;
    public static final int INVALID_SESSION = -1;
    private static final String TAG = "DrmManagerClient";
    private Context mContext;
    private EventHandler mEventHandler;
    HandlerThread mEventThread;
    private InfoHandler mInfoHandler;
    HandlerThread mInfoThread;
    private long mNativeContext;
    private OnErrorListener mOnErrorListener;
    private OnEventListener mOnEventListener;
    private OnInfoListener mOnInfoListener;
    private int mUniqueId;
    private final AtomicBoolean mClosed = new AtomicBoolean();
    private final CloseGuard mCloseGuard = CloseGuard.get();

    private class EventHandler extends Handler {
        public EventHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            DrmEvent drmEvent;
            HashMap map = new HashMap();
            DrmErrorEvent drmErrorEvent = null;
            switch (message.what) {
                case 1001:
                    if (DrmManagerClient.this._removeAllRights(DrmManagerClient.this.mUniqueId) != 0) {
                        drmErrorEvent = new DrmErrorEvent(DrmManagerClient.this.mUniqueId, 2007, null);
                        drmEvent = null;
                    } else {
                        drmEvent = new DrmEvent(DrmManagerClient.this.mUniqueId, 1001, null);
                    }
                    break;
                case 1002:
                    DrmInfo drmInfo = (DrmInfo) message.obj;
                    DrmInfoStatus drmInfoStatus_processDrmInfo = DrmManagerClient.this._processDrmInfo(DrmManagerClient.this.mUniqueId, drmInfo);
                    map.put(DrmEvent.DRM_INFO_STATUS_OBJECT, drmInfoStatus_processDrmInfo);
                    map.put(DrmEvent.DRM_INFO_OBJECT, drmInfo);
                    if (drmInfoStatus_processDrmInfo != null && 1 == drmInfoStatus_processDrmInfo.statusCode) {
                        drmEvent = new DrmEvent(DrmManagerClient.this.mUniqueId, DrmManagerClient.this.getEventType(drmInfoStatus_processDrmInfo.infoType), null, map);
                    } else {
                        DrmErrorEvent drmErrorEvent2 = new DrmErrorEvent(DrmManagerClient.this.mUniqueId, DrmManagerClient.this.getErrorType(drmInfoStatus_processDrmInfo != null ? drmInfoStatus_processDrmInfo.infoType : drmInfo.getInfoType()), null, map);
                        drmEvent = null;
                        drmErrorEvent = drmErrorEvent2;
                    }
                    break;
                default:
                    Log.e(DrmManagerClient.TAG, "Unknown message type " + message.what);
                    return;
            }
            if (DrmManagerClient.this.mOnEventListener != null && drmEvent != null) {
                DrmManagerClient.this.mOnEventListener.onEvent(DrmManagerClient.this, drmEvent);
            }
            if (DrmManagerClient.this.mOnErrorListener == null || drmErrorEvent == null) {
                return;
            }
            DrmManagerClient.this.mOnErrorListener.onError(DrmManagerClient.this, drmErrorEvent);
        }
    }

    private class InfoHandler extends Handler {
        public static final int INFO_EVENT_TYPE = 1;

        public InfoHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            DrmInfoEvent drmInfoEvent;
            if (message.what != 1) {
                Log.e(DrmManagerClient.TAG, "Unknown message type " + message.what);
                return;
            }
            int i = message.arg1;
            int i2 = message.arg2;
            String string = message.obj.toString();
            DrmErrorEvent drmErrorEvent = null;
            switch (i2) {
                case 1:
                case 3:
                case 4:
                case 5:
                case 6:
                    drmInfoEvent = new DrmInfoEvent(i, i2, string);
                    break;
                case 2:
                    try {
                        DrmUtils.removeFile(string);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    drmInfoEvent = new DrmInfoEvent(i, i2, string);
                    break;
                default:
                    drmInfoEvent = null;
                    drmErrorEvent = new DrmErrorEvent(i, i2, string);
                    break;
            }
            if (DrmManagerClient.this.mOnInfoListener != null && drmInfoEvent != null) {
                DrmManagerClient.this.mOnInfoListener.onInfo(DrmManagerClient.this, drmInfoEvent);
            }
            if (DrmManagerClient.this.mOnErrorListener == null || drmErrorEvent == null) {
                return;
            }
            DrmManagerClient.this.mOnErrorListener.onError(DrmManagerClient.this, drmErrorEvent);
        }
    }

    public interface OnErrorListener {
        void onError(DrmManagerClient drmManagerClient, DrmErrorEvent drmErrorEvent);
    }

    public interface OnEventListener {
        void onEvent(DrmManagerClient drmManagerClient, DrmEvent drmEvent);
    }

    public interface OnInfoListener {
        void onInfo(DrmManagerClient drmManagerClient, DrmInfoEvent drmInfoEvent);
    }

    static {
        System.loadLibrary("drmframework_jni");
    }

    public DrmManagerClient(Context context) {
        this.mContext = context;
        createEventThreads();
        this.mUniqueId = _initialize();
        this.mCloseGuard.open("release");
    }

    private native DrmInfo _acquireDrmInfo(int i, DrmInfoRequest drmInfoRequest);

    private native boolean _canHandle(int i, String str, String str2);

    private native int _checkRightsStatus(int i, String str, int i2);

    private native DrmConvertedStatus _closeConvertSession(int i, int i2);

    private native DrmConvertedStatus _convertData(int i, int i2, byte[] bArr);

    private native DrmSupportInfo[] _getAllSupportInfo(int i);

    private native ContentValues _getConstraints(int i, String str, int i2);

    private native int _getDrmObjectType(int i, String str, String str2);

    private native ContentValues _getMetadata(int i, String str);

    private native String _getOriginalMimeType(int i, String str, FileDescriptor fileDescriptor);

    private native int _initialize();

    private native void _installDrmEngine(int i, String str);

    private native int _openConvertSession(int i, String str);

    /* JADX INFO: Access modifiers changed from: private */
    public native DrmInfoStatus _processDrmInfo(int i, DrmInfo drmInfo);

    private native void _release(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public native int _removeAllRights(int i);

    private native int _removeRights(int i, String str);

    private native int _saveRights(int i, DrmRights drmRights, String str, String str2);

    private native void _setListeners(int i, Object obj);

    private String convertUriToPath(Uri uri) throws Throwable {
        Cursor cursorQuery;
        Cursor cursor = null;
        if (uri == null) {
            return null;
        }
        String scheme = uri.getScheme();
        if (scheme == null || scheme.equals("") || scheme.equals("file")) {
            return uri.getPath();
        }
        if (scheme.equals("http")) {
            return uri.toString();
        }
        if (!scheme.equals("content")) {
            throw new IllegalArgumentException("Given Uri scheme is not supported");
        }
        try {
            try {
                cursorQuery = this.mContext.getContentResolver().query(uri, new String[]{"_data"}, null, null, null);
                if (cursorQuery != null) {
                    try {
                        if (cursorQuery.getCount() != 0 && cursorQuery.moveToFirst()) {
                            String string = cursorQuery.getString(cursorQuery.getColumnIndexOrThrow("_data"));
                            if (cursorQuery == null) {
                                return string;
                            }
                            cursorQuery.close();
                            return string;
                        }
                    } catch (SQLiteException unused) {
                        cursor = cursorQuery;
                        throw new IllegalArgumentException("Given Uri is not formatted in a way so that it can be found in media store.");
                    } catch (Throwable th) {
                        th = th;
                        if (cursorQuery != null) {
                            cursorQuery.close();
                        }
                        throw th;
                    }
                }
                throw new IllegalArgumentException("Given Uri could not be found in media store");
            } catch (SQLiteException unused2) {
            }
        } catch (Throwable th2) {
            th = th2;
            cursorQuery = cursor;
        }
    }

    private void createEventThreads() {
        if (this.mEventHandler == null && this.mInfoHandler == null) {
            this.mInfoThread = new HandlerThread("DrmManagerClient.InfoHandler");
            this.mInfoThread.start();
            this.mInfoHandler = new InfoHandler(this.mInfoThread.getLooper());
            this.mEventThread = new HandlerThread("DrmManagerClient.EventHandler");
            this.mEventThread.start();
            this.mEventHandler = new EventHandler(this.mEventThread.getLooper());
        }
    }

    private void createListeners() {
        _setListeners(this.mUniqueId, new WeakReference(this));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getErrorType(int i) {
        switch (i) {
            case 1:
            case 2:
            case 3:
                return 2006;
            default:
                return -1;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getEventType(int i) {
        switch (i) {
            case 1:
            case 2:
            case 3:
                return 1002;
            default:
                return -1;
        }
    }

    public static void notify(Object obj, int i, int i2, String str) {
        DrmManagerClient drmManagerClient = (DrmManagerClient) ((WeakReference) obj).get();
        if (drmManagerClient == null || drmManagerClient.mInfoHandler == null) {
            return;
        }
        drmManagerClient.mInfoHandler.sendMessage(drmManagerClient.mInfoHandler.obtainMessage(1, i, i2, str));
    }

    public DrmInfo acquireDrmInfo(DrmInfoRequest drmInfoRequest) {
        if (drmInfoRequest == null || !drmInfoRequest.isValid()) {
            throw new IllegalArgumentException("Given drmInfoRequest is invalid/null");
        }
        return _acquireDrmInfo(this.mUniqueId, drmInfoRequest);
    }

    public int acquireRights(DrmInfoRequest drmInfoRequest) {
        DrmInfo drmInfoAcquireDrmInfo = acquireDrmInfo(drmInfoRequest);
        return drmInfoAcquireDrmInfo == null ? ERROR_UNKNOWN : processDrmInfo(drmInfoAcquireDrmInfo);
    }

    public boolean canHandle(Uri uri, String str) {
        if ((uri == null || Uri.EMPTY == uri) && (str == null || str.equals(""))) {
            throw new IllegalArgumentException("Uri or the mimetype should be non null");
        }
        return canHandle(convertUriToPath(uri), str);
    }

    public boolean canHandle(String str, String str2) {
        if ((str == null || str.equals("")) && (str2 == null || str2.equals(""))) {
            throw new IllegalArgumentException("Path or the mimetype should be non null");
        }
        return _canHandle(this.mUniqueId, str, str2);
    }

    public int checkRightsStatus(Uri uri) {
        if (uri == null || Uri.EMPTY == uri) {
            throw new IllegalArgumentException("Given uri is not valid");
        }
        return checkRightsStatus(convertUriToPath(uri));
    }

    public int checkRightsStatus(Uri uri, int i) {
        if (uri == null || Uri.EMPTY == uri) {
            throw new IllegalArgumentException("Given uri is not valid");
        }
        return checkRightsStatus(convertUriToPath(uri), i);
    }

    public int checkRightsStatus(String str) {
        return checkRightsStatus(str, 0);
    }

    public int checkRightsStatus(String str, int i) {
        if (str == null || str.equals("") || !DrmStore.Action.isValid(i)) {
            throw new IllegalArgumentException("Given path or action is not valid");
        }
        return _checkRightsStatus(this.mUniqueId, str, i);
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        this.mCloseGuard.close();
        if (this.mClosed.compareAndSet(false, true)) {
            if (this.mEventHandler != null) {
                this.mEventThread.quit();
                this.mEventThread = null;
            }
            if (this.mInfoHandler != null) {
                this.mInfoThread.quit();
                this.mInfoThread = null;
            }
            this.mEventHandler = null;
            this.mInfoHandler = null;
            this.mOnEventListener = null;
            this.mOnInfoListener = null;
            this.mOnErrorListener = null;
            _release(this.mUniqueId);
        }
    }

    public DrmConvertedStatus closeConvertSession(int i) {
        return _closeConvertSession(this.mUniqueId, i);
    }

    public DrmConvertedStatus convertData(int i, byte[] bArr) {
        if (bArr == null || bArr.length <= 0) {
            throw new IllegalArgumentException("Given inputData should be non null");
        }
        return _convertData(this.mUniqueId, i, bArr);
    }

    protected void finalize() throws Throwable {
        try {
            if (this.mCloseGuard != null) {
                this.mCloseGuard.warnIfOpen();
            }
            close();
        } finally {
            super.finalize();
        }
    }

    public String[] getAvailableDrmEngines() {
        DrmSupportInfo[] drmSupportInfoArr_getAllSupportInfo = _getAllSupportInfo(this.mUniqueId);
        ArrayList arrayList = new ArrayList();
        for (DrmSupportInfo drmSupportInfo : drmSupportInfoArr_getAllSupportInfo) {
            arrayList.add(drmSupportInfo.getDescriprition());
        }
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    public ContentValues getConstraints(Uri uri, int i) {
        if (uri == null || Uri.EMPTY == uri) {
            throw new IllegalArgumentException("Uri should be non null");
        }
        return getConstraints(convertUriToPath(uri), i);
    }

    public ContentValues getConstraints(String str, int i) {
        if (str == null || str.equals("") || !DrmStore.Action.isValid(i)) {
            throw new IllegalArgumentException("Given usage or path is invalid/null");
        }
        return _getConstraints(this.mUniqueId, str, i);
    }

    public int getDrmObjectType(Uri uri, String str) throws Throwable {
        String strConvertUriToPath;
        if ((uri == null || Uri.EMPTY == uri) && (str == null || str.equals(""))) {
            throw new IllegalArgumentException("Uri or the mimetype should be non null");
        }
        try {
            strConvertUriToPath = convertUriToPath(uri);
        } catch (Exception unused) {
            Log.w(TAG, "Given Uri could not be found in media store");
            strConvertUriToPath = "";
        }
        return getDrmObjectType(strConvertUriToPath, str);
    }

    public int getDrmObjectType(String str, String str2) {
        if ((str == null || str.equals("")) && (str2 == null || str2.equals(""))) {
            throw new IllegalArgumentException("Path or the mimetype should be non null");
        }
        return _getDrmObjectType(this.mUniqueId, str, str2);
    }

    public ContentValues getMetadata(Uri uri) {
        if (uri == null || Uri.EMPTY == uri) {
            throw new IllegalArgumentException("Uri should be non null");
        }
        return getMetadata(convertUriToPath(uri));
    }

    public ContentValues getMetadata(String str) {
        if (str == null || str.equals("")) {
            throw new IllegalArgumentException("Given path is invalid/null");
        }
        return _getMetadata(this.mUniqueId, str);
    }

    public String getOriginalMimeType(Uri uri) {
        if (uri == null || Uri.EMPTY == uri) {
            throw new IllegalArgumentException("Given uri is not valid");
        }
        return getOriginalMimeType(convertUriToPath(uri));
    }

    public String getOriginalMimeType(String str) {
        FileInputStream fileInputStream;
        FileDescriptor fd;
        if (str == null || str.equals("")) {
            throw new IllegalArgumentException("Given path should be non null");
        }
        FileInputStream fileInputStream2 = null;
        try {
            File file = new File(str);
            if (file.exists()) {
                fileInputStream = new FileInputStream(file);
                try {
                    fd = fileInputStream.getFD();
                } catch (IOException unused) {
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        } catch (IOException unused2) {
                        }
                    }
                    return null;
                } catch (Throwable th) {
                    th = th;
                    fileInputStream2 = fileInputStream;
                    if (fileInputStream2 != null) {
                        try {
                            fileInputStream2.close();
                        } catch (IOException unused3) {
                        }
                    }
                    throw th;
                }
            } else {
                fd = null;
                fileInputStream = null;
            }
            String str_getOriginalMimeType = _getOriginalMimeType(this.mUniqueId, str, fd);
            if (fileInputStream == null) {
                return str_getOriginalMimeType;
            }
            try {
                fileInputStream.close();
                return str_getOriginalMimeType;
            } catch (IOException unused4) {
                return str_getOriginalMimeType;
            }
        } catch (IOException unused5) {
            fileInputStream = null;
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public void installDrmEngine(String str) {
        if (str != null && !str.equals("")) {
            _installDrmEngine(this.mUniqueId, str);
            return;
        }
        throw new IllegalArgumentException("Given engineFilePath: " + str + "is not valid");
    }

    public int openConvertSession(String str) {
        if (str == null || str.equals("")) {
            throw new IllegalArgumentException("Path or the mimeType should be non null");
        }
        return _openConvertSession(this.mUniqueId, str);
    }

    public int processDrmInfo(DrmInfo drmInfo) {
        if (drmInfo == null || !drmInfo.isValid()) {
            throw new IllegalArgumentException("Given drmInfo is invalid/null");
        }
        if (this.mEventHandler == null) {
            return ERROR_UNKNOWN;
        }
        if (this.mEventHandler.sendMessage(this.mEventHandler.obtainMessage(1002, drmInfo))) {
            return 0;
        }
        return ERROR_UNKNOWN;
    }

    @Deprecated
    public void release() {
        close();
    }

    public int removeAllRights() {
        if (this.mEventHandler == null) {
            return ERROR_UNKNOWN;
        }
        if (this.mEventHandler.sendMessage(this.mEventHandler.obtainMessage(1001))) {
            return 0;
        }
        return ERROR_UNKNOWN;
    }

    public int removeRights(Uri uri) {
        if (uri == null || Uri.EMPTY == uri) {
            throw new IllegalArgumentException("Given uri is not valid");
        }
        return removeRights(convertUriToPath(uri));
    }

    public int removeRights(String str) {
        if (str == null || str.equals("")) {
            throw new IllegalArgumentException("Given path should be non null");
        }
        return _removeRights(this.mUniqueId, str);
    }

    public int saveRights(DrmRights drmRights, String str, String str2) throws Throwable {
        if (drmRights == null || !drmRights.isValid()) {
            throw new IllegalArgumentException("Given drmRights or contentPath is not valid");
        }
        if (str != null && !str.equals("")) {
            DrmUtils.writeToFile(str, drmRights.getData());
        }
        return _saveRights(this.mUniqueId, drmRights, str, str2);
    }

    public synchronized void setOnErrorListener(OnErrorListener onErrorListener) {
        this.mOnErrorListener = onErrorListener;
        if (onErrorListener != null) {
            createListeners();
        }
    }

    public synchronized void setOnEventListener(OnEventListener onEventListener) {
        this.mOnEventListener = onEventListener;
        if (onEventListener != null) {
            createListeners();
        }
    }

    public synchronized void setOnInfoListener(OnInfoListener onInfoListener) {
        this.mOnInfoListener = onInfoListener;
        if (onInfoListener != null) {
            createListeners();
        }
    }
}
