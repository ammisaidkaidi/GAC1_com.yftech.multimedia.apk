package android.filterfw.core;

import android.util.Log;
import dalvik.system.PathClassLoader;
import java.util.HashSet;

/* JADX INFO: loaded from: classes.dex */
public class FilterFactory {
    private static FilterFactory mSharedFactory;
    private HashSet<String> mPackages = new HashSet<>();
    private static ClassLoader mCurrentClassLoader = Thread.currentThread().getContextClassLoader();
    private static HashSet<String> mLibraries = new HashSet<>();
    private static Object mClassLoaderGuard = new Object();
    private static final String TAG = "FilterFactory";
    private static boolean mLogVerbose = Log.isLoggable(TAG, 2);

    public static void addFilterLibrary(String str) {
        if (mLogVerbose) {
            Log.v(TAG, "Adding filter library " + str);
        }
        synchronized (mClassLoaderGuard) {
            if (mLibraries.contains(str)) {
                if (mLogVerbose) {
                    Log.v(TAG, "Library already added");
                }
            } else {
                mLibraries.add(str);
                mCurrentClassLoader = new PathClassLoader(str, mCurrentClassLoader);
            }
        }
    }

    public static FilterFactory sharedFactory() {
        if (mSharedFactory == null) {
            mSharedFactory = new FilterFactory();
        }
        return mSharedFactory;
    }

    public void addPackage(String str) {
        if (mLogVerbose) {
            Log.v(TAG, "Adding package " + str);
        }
        this.mPackages.add(str);
    }

    public Filter createFilterByClass(Class cls, String str) {
        Filter filter;
        try {
            cls.asSubclass(Filter.class);
            try {
                try {
                    filter = (Filter) cls.getConstructor(String.class).newInstance(str);
                } catch (Throwable unused) {
                    filter = null;
                }
                if (filter != null) {
                    return filter;
                }
                throw new IllegalArgumentException("Could not construct the filter '" + str + "'!");
            } catch (NoSuchMethodException unused2) {
                throw new IllegalArgumentException("The filter class '" + cls + "' does not have a constructor of the form <init>(String name)!");
            }
        } catch (ClassCastException unused3) {
            throw new IllegalArgumentException("Attempting to allocate class '" + cls + "' which is not a subclass of Filter!");
        }
    }

    /* JADX WARN: Bottom block not found for handler: all -> 0x0078 */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x0079, code lost:
    
        r2 = th;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public Filter createFilterByClassName(String str, String str2) throws Throwable {
        if (mLogVerbose) {
            Log.v(TAG, "Looking up class " + str);
        }
        Class<?> cls = null;
        for (String str3 : this.mPackages) {
            try {
                if (mLogVerbose) {
                    Log.v(TAG, "Trying " + str3 + "." + str);
                }
                synchronized (mClassLoaderGuard) {
                    Class<?> clsLoadClass = mCurrentClassLoader.loadClass(str3 + "." + str);
                    try {
                        if (clsLoadClass != null) {
                            cls = clsLoadClass;
                            break;
                        }
                        cls = clsLoadClass;
                    } catch (Throwable th) {
                        th = th;
                        cls = clsLoadClass;
                        while (true) {
                            throw th;
                        }
                    }
                }
            } catch (ClassNotFoundException unused) {
            }
        }
        if (cls != null) {
            return createFilterByClass(cls, str2);
        }
        throw new IllegalArgumentException("Unknown filter class '" + str + "'!");
    }
}
