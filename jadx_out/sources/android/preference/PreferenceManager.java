package android.preference;

import android.annotation.SystemApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.provider.SettingsStringUtil;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;

/* JADX INFO: loaded from: classes2.dex */
public class PreferenceManager {
    public static final String KEY_HAS_SET_DEFAULT_VALUES = "_has_set_default_values";
    public static final String METADATA_KEY_PREFERENCES = "android.preference";
    private static final int STORAGE_CREDENTIAL_PROTECTED = 2;
    private static final int STORAGE_DEFAULT = 0;
    private static final int STORAGE_DEVICE_PROTECTED = 1;
    private static final String TAG = "PreferenceManager";
    private Activity mActivity;
    private List<OnActivityDestroyListener> mActivityDestroyListeners;
    private List<OnActivityResultListener> mActivityResultListeners;
    private List<OnActivityStopListener> mActivityStopListeners;
    private Context mContext;
    private SharedPreferences.Editor mEditor;
    private PreferenceFragment mFragment;
    private int mNextRequestCode;
    private boolean mNoCommit;
    private OnPreferenceTreeClickListener mOnPreferenceTreeClickListener;
    private PreferenceDataStore mPreferenceDataStore;
    private PreferenceScreen mPreferenceScreen;
    private List<DialogInterface> mPreferencesScreens;
    private SharedPreferences mSharedPreferences;
    private int mSharedPreferencesMode;
    private String mSharedPreferencesName;
    private long mNextId = 0;
    private int mStorage = 0;

    public interface OnActivityDestroyListener {
        void onActivityDestroy();
    }

    public interface OnActivityResultListener {
        boolean onActivityResult(int i, int i2, Intent intent);
    }

    public interface OnActivityStopListener {
        void onActivityStop();
    }

    public interface OnPreferenceTreeClickListener {
        boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference);
    }

    public PreferenceManager(Activity activity, int i) {
        this.mActivity = activity;
        this.mNextRequestCode = i;
        init(activity);
    }

    PreferenceManager(Context context) {
        init(context);
    }

    private void dismissAllScreens() {
        synchronized (this) {
            if (this.mPreferencesScreens == null) {
                return;
            }
            ArrayList arrayList = new ArrayList(this.mPreferencesScreens);
            this.mPreferencesScreens.clear();
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                ((DialogInterface) arrayList.get(size)).dismiss();
            }
        }
    }

    public static SharedPreferences getDefaultSharedPreferences(Context context) {
        return context.getSharedPreferences(getDefaultSharedPreferencesName(context), getDefaultSharedPreferencesMode());
    }

    private static int getDefaultSharedPreferencesMode() {
        return 0;
    }

    public static String getDefaultSharedPreferencesName(Context context) {
        return context.getPackageName() + "_preferences";
    }

    private void init(Context context) {
        this.mContext = context;
        setSharedPreferencesName(getDefaultSharedPreferencesName(context));
    }

    private List<ResolveInfo> queryIntentActivities(Intent intent) {
        return this.mContext.getPackageManager().queryIntentActivities(intent, 128);
    }

    public static void setDefaultValues(Context context, int i, boolean z) {
        setDefaultValues(context, getDefaultSharedPreferencesName(context), getDefaultSharedPreferencesMode(), i, z);
    }

    public static void setDefaultValues(Context context, String str, int i, int i2, boolean z) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_HAS_SET_DEFAULT_VALUES, 0);
        if (z || !sharedPreferences.getBoolean(KEY_HAS_SET_DEFAULT_VALUES, false)) {
            PreferenceManager preferenceManager = new PreferenceManager(context);
            preferenceManager.setSharedPreferencesName(str);
            preferenceManager.setSharedPreferencesMode(i);
            preferenceManager.inflateFromResource(context, i2, null);
            SharedPreferences.Editor editorPutBoolean = sharedPreferences.edit().putBoolean(KEY_HAS_SET_DEFAULT_VALUES, true);
            try {
                editorPutBoolean.apply();
            } catch (AbstractMethodError unused) {
                editorPutBoolean.commit();
            }
        }
    }

    private void setNoCommit(boolean z) {
        if (!z && this.mEditor != null) {
            try {
                this.mEditor.apply();
            } catch (AbstractMethodError unused) {
                this.mEditor.commit();
            }
        }
        this.mNoCommit = z;
    }

    void addPreferencesScreen(DialogInterface dialogInterface) {
        synchronized (this) {
            if (this.mPreferencesScreens == null) {
                this.mPreferencesScreens = new ArrayList();
            }
            this.mPreferencesScreens.add(dialogInterface);
        }
    }

    public PreferenceScreen createPreferenceScreen(Context context) {
        PreferenceScreen preferenceScreen = new PreferenceScreen(context, null);
        preferenceScreen.onAttachedToHierarchy(this);
        return preferenceScreen;
    }

    void dispatchActivityDestroy() {
        ArrayList arrayList;
        synchronized (this) {
            arrayList = this.mActivityDestroyListeners != null ? new ArrayList(this.mActivityDestroyListeners) : null;
        }
        if (arrayList != null) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                ((OnActivityDestroyListener) arrayList.get(i)).onActivityDestroy();
            }
        }
        dismissAllScreens();
    }

    void dispatchActivityResult(int i, int i2, Intent intent) {
        synchronized (this) {
            if (this.mActivityResultListeners == null) {
                return;
            }
            ArrayList arrayList = new ArrayList(this.mActivityResultListeners);
            int size = arrayList.size();
            for (int i3 = 0; i3 < size && !((OnActivityResultListener) arrayList.get(i3)).onActivityResult(i, i2, intent); i3++) {
            }
        }
    }

    void dispatchActivityStop() {
        synchronized (this) {
            if (this.mActivityStopListeners == null) {
                return;
            }
            ArrayList arrayList = new ArrayList(this.mActivityStopListeners);
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                ((OnActivityStopListener) arrayList.get(i)).onActivityStop();
            }
        }
    }

    void dispatchNewIntent(Intent intent) {
        dismissAllScreens();
    }

    public Preference findPreference(CharSequence charSequence) {
        if (this.mPreferenceScreen == null) {
            return null;
        }
        return this.mPreferenceScreen.findPreference(charSequence);
    }

    Activity getActivity() {
        return this.mActivity;
    }

    Context getContext() {
        return this.mContext;
    }

    SharedPreferences.Editor getEditor() {
        if (this.mPreferenceDataStore != null) {
            return null;
        }
        if (!this.mNoCommit) {
            return getSharedPreferences().edit();
        }
        if (this.mEditor == null) {
            this.mEditor = getSharedPreferences().edit();
        }
        return this.mEditor;
    }

    PreferenceFragment getFragment() {
        return this.mFragment;
    }

    long getNextId() {
        long j;
        synchronized (this) {
            j = this.mNextId;
            this.mNextId = 1 + j;
        }
        return j;
    }

    int getNextRequestCode() {
        int i;
        synchronized (this) {
            i = this.mNextRequestCode;
            this.mNextRequestCode = i + 1;
        }
        return i;
    }

    OnPreferenceTreeClickListener getOnPreferenceTreeClickListener() {
        return this.mOnPreferenceTreeClickListener;
    }

    public PreferenceDataStore getPreferenceDataStore() {
        return this.mPreferenceDataStore;
    }

    PreferenceScreen getPreferenceScreen() {
        return this.mPreferenceScreen;
    }

    public SharedPreferences getSharedPreferences() {
        Context contextCreateDeviceProtectedStorageContext;
        if (this.mPreferenceDataStore != null) {
            return null;
        }
        if (this.mSharedPreferences == null) {
            switch (this.mStorage) {
                case 1:
                    contextCreateDeviceProtectedStorageContext = this.mContext.createDeviceProtectedStorageContext();
                    break;
                case 2:
                    contextCreateDeviceProtectedStorageContext = this.mContext.createCredentialProtectedStorageContext();
                    break;
                default:
                    contextCreateDeviceProtectedStorageContext = this.mContext;
                    break;
            }
            this.mSharedPreferences = contextCreateDeviceProtectedStorageContext.getSharedPreferences(this.mSharedPreferencesName, this.mSharedPreferencesMode);
        }
        return this.mSharedPreferences;
    }

    public int getSharedPreferencesMode() {
        return this.mSharedPreferencesMode;
    }

    public String getSharedPreferencesName() {
        return this.mSharedPreferencesName;
    }

    PreferenceScreen inflateFromIntent(Intent intent, PreferenceScreen preferenceScreen) {
        List<ResolveInfo> listQueryIntentActivities = queryIntentActivities(intent);
        HashSet hashSet = new HashSet();
        for (int size = listQueryIntentActivities.size() - 1; size >= 0; size--) {
            ActivityInfo activityInfo = listQueryIntentActivities.get(size).activityInfo;
            Bundle bundle = activityInfo.metaData;
            if (bundle != null && bundle.containsKey(METADATA_KEY_PREFERENCES)) {
                String str = activityInfo.packageName + SettingsStringUtil.DELIMITER + activityInfo.metaData.getInt(METADATA_KEY_PREFERENCES);
                if (!hashSet.contains(str)) {
                    hashSet.add(str);
                    try {
                        Context contextCreatePackageContext = this.mContext.createPackageContext(activityInfo.packageName, 0);
                        PreferenceInflater preferenceInflater = new PreferenceInflater(contextCreatePackageContext, this);
                        XmlResourceParser xmlResourceParserLoadXmlMetaData = activityInfo.loadXmlMetaData(contextCreatePackageContext.getPackageManager(), METADATA_KEY_PREFERENCES);
                        preferenceScreen = (PreferenceScreen) preferenceInflater.inflate((XmlPullParser) xmlResourceParserLoadXmlMetaData, preferenceScreen, true);
                        xmlResourceParserLoadXmlMetaData.close();
                    } catch (PackageManager.NameNotFoundException e) {
                        Log.w(TAG, "Could not create context for " + activityInfo.packageName + ": " + Log.getStackTraceString(e));
                    }
                }
            }
        }
        preferenceScreen.onAttachedToHierarchy(this);
        return preferenceScreen;
    }

    public PreferenceScreen inflateFromResource(Context context, int i, PreferenceScreen preferenceScreen) {
        setNoCommit(true);
        PreferenceScreen preferenceScreen2 = (PreferenceScreen) new PreferenceInflater(context, this).inflate(i, preferenceScreen, true);
        preferenceScreen2.onAttachedToHierarchy(this);
        setNoCommit(false);
        return preferenceScreen2;
    }

    @SystemApi
    public boolean isStorageCredentialProtected() {
        return this.mStorage == 2;
    }

    public boolean isStorageDefault() {
        return this.mStorage == 0;
    }

    public boolean isStorageDeviceProtected() {
        return this.mStorage == 1;
    }

    void registerOnActivityDestroyListener(OnActivityDestroyListener onActivityDestroyListener) {
        synchronized (this) {
            if (this.mActivityDestroyListeners == null) {
                this.mActivityDestroyListeners = new ArrayList();
            }
            if (!this.mActivityDestroyListeners.contains(onActivityDestroyListener)) {
                this.mActivityDestroyListeners.add(onActivityDestroyListener);
            }
        }
    }

    void registerOnActivityResultListener(OnActivityResultListener onActivityResultListener) {
        synchronized (this) {
            if (this.mActivityResultListeners == null) {
                this.mActivityResultListeners = new ArrayList();
            }
            if (!this.mActivityResultListeners.contains(onActivityResultListener)) {
                this.mActivityResultListeners.add(onActivityResultListener);
            }
        }
    }

    public void registerOnActivityStopListener(OnActivityStopListener onActivityStopListener) {
        synchronized (this) {
            if (this.mActivityStopListeners == null) {
                this.mActivityStopListeners = new ArrayList();
            }
            if (!this.mActivityStopListeners.contains(onActivityStopListener)) {
                this.mActivityStopListeners.add(onActivityStopListener);
            }
        }
    }

    void removePreferencesScreen(DialogInterface dialogInterface) {
        synchronized (this) {
            if (this.mPreferencesScreens == null) {
                return;
            }
            this.mPreferencesScreens.remove(dialogInterface);
        }
    }

    void setFragment(PreferenceFragment preferenceFragment) {
        this.mFragment = preferenceFragment;
    }

    void setOnPreferenceTreeClickListener(OnPreferenceTreeClickListener onPreferenceTreeClickListener) {
        this.mOnPreferenceTreeClickListener = onPreferenceTreeClickListener;
    }

    public void setPreferenceDataStore(PreferenceDataStore preferenceDataStore) {
        this.mPreferenceDataStore = preferenceDataStore;
    }

    boolean setPreferences(PreferenceScreen preferenceScreen) {
        if (preferenceScreen == this.mPreferenceScreen) {
            return false;
        }
        this.mPreferenceScreen = preferenceScreen;
        return true;
    }

    public void setSharedPreferencesMode(int i) {
        this.mSharedPreferencesMode = i;
        this.mSharedPreferences = null;
    }

    public void setSharedPreferencesName(String str) {
        this.mSharedPreferencesName = str;
        this.mSharedPreferences = null;
    }

    @SystemApi
    public void setStorageCredentialProtected() {
        this.mStorage = 2;
        this.mSharedPreferences = null;
    }

    public void setStorageDefault() {
        this.mStorage = 0;
        this.mSharedPreferences = null;
    }

    public void setStorageDeviceProtected() {
        this.mStorage = 1;
        this.mSharedPreferences = null;
    }

    boolean shouldCommit() {
        return !this.mNoCommit;
    }

    void unregisterOnActivityDestroyListener(OnActivityDestroyListener onActivityDestroyListener) {
        synchronized (this) {
            if (this.mActivityDestroyListeners != null) {
                this.mActivityDestroyListeners.remove(onActivityDestroyListener);
            }
        }
    }

    void unregisterOnActivityResultListener(OnActivityResultListener onActivityResultListener) {
        synchronized (this) {
            if (this.mActivityResultListeners != null) {
                this.mActivityResultListeners.remove(onActivityResultListener);
            }
        }
    }

    public void unregisterOnActivityStopListener(OnActivityStopListener onActivityStopListener) {
        synchronized (this) {
            if (this.mActivityStopListeners != null) {
                this.mActivityStopListeners.remove(onActivityStopListener);
            }
        }
    }
}
