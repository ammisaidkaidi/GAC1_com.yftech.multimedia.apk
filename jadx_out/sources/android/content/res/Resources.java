package android.content.res;

import android.animation.Animator;
import android.animation.StateListAnimator;
import android.graphics.Movie;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableInflater;
import android.media.TtmlUtils;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.Pools;
import android.util.TypedValue;
import android.view.DisplayAdjustments;
import android.view.ViewDebug;
import android.view.ViewHierarchyEncoder;
import com.android.internal.R;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.GrowingArrayUtils;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.function.Predicate;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public class Resources {
    private static final int MIN_THEME_REFS_FLUSH_SIZE = 32;
    static final String TAG = "Resources";
    final ClassLoader mClassLoader;
    private DrawableInflater mDrawableInflater;
    private ResourcesImpl mResourcesImpl;
    private final ArrayList<WeakReference<Theme>> mThemeRefs;
    private int mThemeRefsNextFlushSize;
    private TypedValue mTmpValue;
    private final Object mTmpValueLock;
    final Pools.SynchronizedPool<TypedArray> mTypedArrayPool;
    private static final Object sSync = new Object();
    static Resources mSystem = null;

    public static class NotFoundException extends RuntimeException {
        public NotFoundException() {
        }

        public NotFoundException(String str) {
            super(str);
        }

        public NotFoundException(String str, Exception exc) {
            super(str, exc);
        }
    }

    public final class Theme {
        private ResourcesImpl.ThemeImpl mThemeImpl;

        private Theme() {
        }

        private String getResourceNameFromHexString(String str) {
            return Resources.this.getResourceName(Integer.parseInt(str, 16));
        }

        public void applyStyle(int i, boolean z) {
            this.mThemeImpl.applyStyle(i, z);
        }

        public void dump(int i, String str, String str2) {
            this.mThemeImpl.dump(i, str, str2);
        }

        public void encode(ViewHierarchyEncoder viewHierarchyEncoder) {
            viewHierarchyEncoder.beginObject(this);
            String[] theme = getTheme();
            for (int i = 0; i < theme.length; i += 2) {
                viewHierarchyEncoder.addProperty(theme[i], theme[i + 1]);
            }
            viewHierarchyEncoder.endObject();
        }

        public int[] getAllAttributes() {
            return this.mThemeImpl.getAllAttributes();
        }

        int getAppliedStyleResId() {
            return this.mThemeImpl.getAppliedStyleResId();
        }

        public int getChangingConfigurations() {
            return this.mThemeImpl.getChangingConfigurations();
        }

        public Drawable getDrawable(int i) throws NotFoundException {
            return Resources.this.getDrawable(i, this);
        }

        public ThemeKey getKey() {
            return this.mThemeImpl.getKey();
        }

        long getNativeTheme() {
            return this.mThemeImpl.getNativeTheme();
        }

        public Resources getResources() {
            return Resources.this;
        }

        @ViewDebug.ExportedProperty(category = "theme", hasAdjacentMapping = true)
        public String[] getTheme() {
            return this.mThemeImpl.getTheme();
        }

        public TypedArray obtainStyledAttributes(int i, int[] iArr) throws NotFoundException {
            return this.mThemeImpl.obtainStyledAttributes(this, null, iArr, 0, i);
        }

        public TypedArray obtainStyledAttributes(AttributeSet attributeSet, int[] iArr, int i, int i2) {
            return this.mThemeImpl.obtainStyledAttributes(this, attributeSet, iArr, i, i2);
        }

        public TypedArray obtainStyledAttributes(int[] iArr) {
            return this.mThemeImpl.obtainStyledAttributes(this, null, iArr, 0, 0);
        }

        public void rebase() {
            this.mThemeImpl.rebase();
        }

        public boolean resolveAttribute(int i, TypedValue typedValue, boolean z) {
            return this.mThemeImpl.resolveAttribute(i, typedValue, z);
        }

        public TypedArray resolveAttributes(int[] iArr, int[] iArr2) {
            return this.mThemeImpl.resolveAttributes(this, iArr, iArr2);
        }

        void setImpl(ResourcesImpl.ThemeImpl themeImpl) {
            this.mThemeImpl = themeImpl;
        }

        public void setTo(Theme theme) {
            this.mThemeImpl.setTo(theme.mThemeImpl);
        }
    }

    static class ThemeKey implements Cloneable {
        int mCount;
        boolean[] mForce;
        private int mHashCode = 0;
        int[] mResId;

        ThemeKey() {
        }

        public void append(int i, boolean z) {
            if (this.mResId == null) {
                this.mResId = new int[4];
            }
            if (this.mForce == null) {
                this.mForce = new boolean[4];
            }
            this.mResId = GrowingArrayUtils.append(this.mResId, this.mCount, i);
            this.mForce = GrowingArrayUtils.append(this.mForce, this.mCount, z);
            this.mCount++;
            this.mHashCode = (((this.mHashCode * 31) + i) * 31) + (z ? 1 : 0);
        }

        /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
        public ThemeKey m18clone() {
            ThemeKey themeKey = new ThemeKey();
            themeKey.mResId = this.mResId;
            themeKey.mForce = this.mForce;
            themeKey.mCount = this.mCount;
            themeKey.mHashCode = this.mHashCode;
            return themeKey;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass() || hashCode() != obj.hashCode()) {
                return false;
            }
            ThemeKey themeKey = (ThemeKey) obj;
            if (this.mCount != themeKey.mCount) {
                return false;
            }
            int i = this.mCount;
            for (int i2 = 0; i2 < i; i2++) {
                if (this.mResId[i2] != themeKey.mResId[i2] || this.mForce[i2] != themeKey.mForce[i2]) {
                    return false;
                }
            }
            return true;
        }

        public int hashCode() {
            return this.mHashCode;
        }

        public void setTo(ThemeKey themeKey) {
            this.mResId = themeKey.mResId == null ? null : (int[]) themeKey.mResId.clone();
            this.mForce = themeKey.mForce != null ? (boolean[]) themeKey.mForce.clone() : null;
            this.mCount = themeKey.mCount;
        }
    }

    private Resources() {
        this(null);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        displayMetrics.setToDefaults();
        Configuration configuration = new Configuration();
        configuration.setToDefaults();
        this.mResourcesImpl = new ResourcesImpl(AssetManager.getSystem(), displayMetrics, configuration, new DisplayAdjustments());
    }

    @Deprecated
    public Resources(AssetManager assetManager, DisplayMetrics displayMetrics, Configuration configuration) {
        this(null);
        this.mResourcesImpl = new ResourcesImpl(assetManager, displayMetrics, configuration, new DisplayAdjustments());
    }

    public Resources(ClassLoader classLoader) {
        this.mTypedArrayPool = new Pools.SynchronizedPool<>(5);
        this.mTmpValueLock = new Object();
        this.mTmpValue = new TypedValue();
        this.mThemeRefs = new ArrayList<>();
        this.mThemeRefsNextFlushSize = 32;
        this.mClassLoader = classLoader == null ? ClassLoader.getSystemClassLoader() : classLoader;
    }

    public static Resources getSystem() {
        Resources resources;
        synchronized (sSync) {
            resources = mSystem;
            if (resources == null) {
                resources = new Resources();
                mSystem = resources;
            }
        }
        return resources;
    }

    static /* synthetic */ boolean lambda$newTheme$0(WeakReference weakReference) {
        return weakReference.get() == null;
    }

    public static TypedArray obtainAttributes(Resources resources, Theme theme, AttributeSet attributeSet, int[] iArr) {
        return theme == null ? resources.obtainAttributes(attributeSet, iArr) : theme.obtainStyledAttributes(attributeSet, iArr, 0, 0);
    }

    private TypedValue obtainTempTypedValue() {
        TypedValue typedValue;
        synchronized (this.mTmpValueLock) {
            if (this.mTmpValue != null) {
                typedValue = this.mTmpValue;
                this.mTmpValue = null;
            } else {
                typedValue = null;
            }
        }
        return typedValue == null ? new TypedValue() : typedValue;
    }

    private void releaseTempTypedValue(TypedValue typedValue) {
        synchronized (this.mTmpValueLock) {
            if (this.mTmpValue == null) {
                this.mTmpValue = typedValue;
            }
        }
    }

    public static boolean resourceHasPackage(int i) {
        return (i >>> 24) != 0;
    }

    public static int selectDefaultTheme(int i, int i2) {
        return selectSystemTheme(i, i2, 16973829, 16973931, 16974120, 16974143);
    }

    public static int selectSystemTheme(int i, int i2, int i3, int i4, int i5, int i6) {
        if (i != 0) {
            return i;
        }
        if (i2 < 11) {
            return i3;
        }
        if (i2 < 14) {
            return i4;
        }
        return i2 < 24 ? i5 : i6;
    }

    public static void updateSystemConfiguration(Configuration configuration, DisplayMetrics displayMetrics, CompatibilityInfo compatibilityInfo) {
        if (mSystem != null) {
            mSystem.updateConfiguration(configuration, displayMetrics, compatibilityInfo);
        }
    }

    @VisibleForTesting
    public int calcConfigChanges(Configuration configuration) {
        return this.mResourcesImpl.calcConfigChanges(configuration);
    }

    public final void finishPreloading() {
        this.mResourcesImpl.finishPreloading();
    }

    public final void flushLayoutCache() {
        this.mResourcesImpl.flushLayoutCache();
    }

    public XmlResourceParser getAnimation(int i) throws NotFoundException {
        return loadXmlResourceParser(i, "anim");
    }

    public ConfigurationBoundResourceCache<Animator> getAnimatorCache() {
        return this.mResourcesImpl.getAnimatorCache();
    }

    public final AssetManager getAssets() {
        return this.mResourcesImpl.getAssets();
    }

    public boolean getBoolean(int i) throws NotFoundException {
        TypedValue typedValueObtainTempTypedValue = obtainTempTypedValue();
        try {
            this.mResourcesImpl.getValue(i, typedValueObtainTempTypedValue, true);
            if (typedValueObtainTempTypedValue.type >= 16 && typedValueObtainTempTypedValue.type <= 31) {
                boolean z = typedValueObtainTempTypedValue.data != 0;
                releaseTempTypedValue(typedValueObtainTempTypedValue);
                return z;
            }
            throw new NotFoundException("Resource ID #0x" + Integer.toHexString(i) + " type #0x" + Integer.toHexString(typedValueObtainTempTypedValue.type) + " is not valid");
        } catch (Throwable th) {
            releaseTempTypedValue(typedValueObtainTempTypedValue);
            throw th;
        }
    }

    public ClassLoader getClassLoader() {
        return this.mClassLoader;
    }

    @Deprecated
    public int getColor(int i) throws NotFoundException {
        return getColor(i, null);
    }

    public int getColor(int i, Theme theme) throws NotFoundException {
        TypedValue typedValueObtainTempTypedValue = obtainTempTypedValue();
        try {
            ResourcesImpl resourcesImpl = this.mResourcesImpl;
            resourcesImpl.getValue(i, typedValueObtainTempTypedValue, true);
            if (typedValueObtainTempTypedValue.type >= 16 && typedValueObtainTempTypedValue.type <= 31) {
                int i2 = typedValueObtainTempTypedValue.data;
                releaseTempTypedValue(typedValueObtainTempTypedValue);
                return i2;
            }
            if (typedValueObtainTempTypedValue.type == 3) {
                int defaultColor = resourcesImpl.loadColorStateList(this, typedValueObtainTempTypedValue, i, theme).getDefaultColor();
                releaseTempTypedValue(typedValueObtainTempTypedValue);
                return defaultColor;
            }
            throw new NotFoundException("Resource ID #0x" + Integer.toHexString(i) + " type #0x" + Integer.toHexString(typedValueObtainTempTypedValue.type) + " is not valid");
        } catch (Throwable th) {
            releaseTempTypedValue(typedValueObtainTempTypedValue);
            throw th;
        }
    }

    @Deprecated
    public ColorStateList getColorStateList(int i) throws NotFoundException {
        ColorStateList colorStateList = getColorStateList(i, null);
        if (colorStateList != null && colorStateList.canApplyTheme()) {
            Log.w(TAG, "ColorStateList " + getResourceName(i) + " has unresolved theme attributes! Consider using Resources.getColorStateList(int, Theme) or Context.getColorStateList(int).", new RuntimeException());
        }
        return colorStateList;
    }

    public ColorStateList getColorStateList(int i, Theme theme) throws NotFoundException {
        TypedValue typedValueObtainTempTypedValue = obtainTempTypedValue();
        try {
            ResourcesImpl resourcesImpl = this.mResourcesImpl;
            resourcesImpl.getValue(i, typedValueObtainTempTypedValue, true);
            return resourcesImpl.loadColorStateList(this, typedValueObtainTempTypedValue, i, theme);
        } finally {
            releaseTempTypedValue(typedValueObtainTempTypedValue);
        }
    }

    public CompatibilityInfo getCompatibilityInfo() {
        return this.mResourcesImpl.getCompatibilityInfo();
    }

    public Configuration getConfiguration() {
        return this.mResourcesImpl.getConfiguration();
    }

    public float getDimension(int i) throws NotFoundException {
        TypedValue typedValueObtainTempTypedValue = obtainTempTypedValue();
        try {
            ResourcesImpl resourcesImpl = this.mResourcesImpl;
            resourcesImpl.getValue(i, typedValueObtainTempTypedValue, true);
            if (typedValueObtainTempTypedValue.type == 5) {
                float fComplexToDimension = TypedValue.complexToDimension(typedValueObtainTempTypedValue.data, resourcesImpl.getDisplayMetrics());
                releaseTempTypedValue(typedValueObtainTempTypedValue);
                return fComplexToDimension;
            }
            throw new NotFoundException("Resource ID #0x" + Integer.toHexString(i) + " type #0x" + Integer.toHexString(typedValueObtainTempTypedValue.type) + " is not valid");
        } catch (Throwable th) {
            releaseTempTypedValue(typedValueObtainTempTypedValue);
            throw th;
        }
    }

    public int getDimensionPixelOffset(int i) throws NotFoundException {
        TypedValue typedValueObtainTempTypedValue = obtainTempTypedValue();
        try {
            ResourcesImpl resourcesImpl = this.mResourcesImpl;
            resourcesImpl.getValue(i, typedValueObtainTempTypedValue, true);
            if (typedValueObtainTempTypedValue.type == 5) {
                int iComplexToDimensionPixelOffset = TypedValue.complexToDimensionPixelOffset(typedValueObtainTempTypedValue.data, resourcesImpl.getDisplayMetrics());
                releaseTempTypedValue(typedValueObtainTempTypedValue);
                return iComplexToDimensionPixelOffset;
            }
            throw new NotFoundException("Resource ID #0x" + Integer.toHexString(i) + " type #0x" + Integer.toHexString(typedValueObtainTempTypedValue.type) + " is not valid");
        } catch (Throwable th) {
            releaseTempTypedValue(typedValueObtainTempTypedValue);
            throw th;
        }
    }

    public int getDimensionPixelSize(int i) throws NotFoundException {
        TypedValue typedValueObtainTempTypedValue = obtainTempTypedValue();
        try {
            ResourcesImpl resourcesImpl = this.mResourcesImpl;
            resourcesImpl.getValue(i, typedValueObtainTempTypedValue, true);
            if (typedValueObtainTempTypedValue.type == 5) {
                int iComplexToDimensionPixelSize = TypedValue.complexToDimensionPixelSize(typedValueObtainTempTypedValue.data, resourcesImpl.getDisplayMetrics());
                releaseTempTypedValue(typedValueObtainTempTypedValue);
                return iComplexToDimensionPixelSize;
            }
            throw new NotFoundException("Resource ID #0x" + Integer.toHexString(i) + " type #0x" + Integer.toHexString(typedValueObtainTempTypedValue.type) + " is not valid");
        } catch (Throwable th) {
            releaseTempTypedValue(typedValueObtainTempTypedValue);
            throw th;
        }
    }

    public DisplayAdjustments getDisplayAdjustments() {
        return this.mResourcesImpl.getDisplayAdjustments();
    }

    public DisplayMetrics getDisplayMetrics() {
        return this.mResourcesImpl.getDisplayMetrics();
    }

    @Deprecated
    public Drawable getDrawable(int i) throws NotFoundException {
        Drawable drawable = getDrawable(i, null);
        if (drawable != null && drawable.canApplyTheme()) {
            Log.w(TAG, "Drawable " + getResourceName(i) + " has unresolved theme attributes! Consider using Resources.getDrawable(int, Theme) or Context.getDrawable(int).", new RuntimeException());
        }
        return drawable;
    }

    public Drawable getDrawable(int i, Theme theme) throws NotFoundException {
        return getDrawableForDensity(i, 0, theme);
    }

    @Deprecated
    public Drawable getDrawableForDensity(int i, int i2) throws NotFoundException {
        return getDrawableForDensity(i, i2, null);
    }

    public Drawable getDrawableForDensity(int i, int i2, Theme theme) {
        TypedValue typedValueObtainTempTypedValue = obtainTempTypedValue();
        try {
            ResourcesImpl resourcesImpl = this.mResourcesImpl;
            resourcesImpl.getValueForDensity(i, i2, typedValueObtainTempTypedValue, true);
            return resourcesImpl.loadDrawable(this, typedValueObtainTempTypedValue, i, i2, theme);
        } finally {
            releaseTempTypedValue(typedValueObtainTempTypedValue);
        }
    }

    public final DrawableInflater getDrawableInflater() {
        if (this.mDrawableInflater == null) {
            this.mDrawableInflater = new DrawableInflater(this, this.mClassLoader);
        }
        return this.mDrawableInflater;
    }

    public float getFloat(int i) {
        TypedValue typedValueObtainTempTypedValue = obtainTempTypedValue();
        try {
            this.mResourcesImpl.getValue(i, typedValueObtainTempTypedValue, true);
            if (typedValueObtainTempTypedValue.type == 4) {
                float f = typedValueObtainTempTypedValue.getFloat();
                releaseTempTypedValue(typedValueObtainTempTypedValue);
                return f;
            }
            throw new NotFoundException("Resource ID #0x" + Integer.toHexString(i) + " type #0x" + Integer.toHexString(typedValueObtainTempTypedValue.type) + " is not valid");
        } catch (Throwable th) {
            releaseTempTypedValue(typedValueObtainTempTypedValue);
            throw th;
        }
    }

    public Typeface getFont(int i) throws NotFoundException {
        TypedValue typedValueObtainTempTypedValue = obtainTempTypedValue();
        try {
            ResourcesImpl resourcesImpl = this.mResourcesImpl;
            resourcesImpl.getValue(i, typedValueObtainTempTypedValue, true);
            Typeface typefaceLoadFont = resourcesImpl.loadFont(this, typedValueObtainTempTypedValue, i);
            if (typefaceLoadFont != null) {
                releaseTempTypedValue(typedValueObtainTempTypedValue);
                return typefaceLoadFont;
            }
            releaseTempTypedValue(typedValueObtainTempTypedValue);
            throw new NotFoundException("Font resource ID #0x" + Integer.toHexString(i));
        } catch (Throwable th) {
            releaseTempTypedValue(typedValueObtainTempTypedValue);
            throw th;
        }
    }

    Typeface getFont(TypedValue typedValue, int i) throws NotFoundException {
        return this.mResourcesImpl.loadFont(this, typedValue, i);
    }

    public float getFraction(int i, int i2, int i3) {
        TypedValue typedValueObtainTempTypedValue = obtainTempTypedValue();
        try {
            this.mResourcesImpl.getValue(i, typedValueObtainTempTypedValue, true);
            if (typedValueObtainTempTypedValue.type == 6) {
                float fComplexToFraction = TypedValue.complexToFraction(typedValueObtainTempTypedValue.data, i2, i3);
                releaseTempTypedValue(typedValueObtainTempTypedValue);
                return fComplexToFraction;
            }
            throw new NotFoundException("Resource ID #0x" + Integer.toHexString(i) + " type #0x" + Integer.toHexString(typedValueObtainTempTypedValue.type) + " is not valid");
        } catch (Throwable th) {
            releaseTempTypedValue(typedValueObtainTempTypedValue);
            throw th;
        }
    }

    public int getIdentifier(String str, String str2, String str3) {
        return this.mResourcesImpl.getIdentifier(str, str2, str3);
    }

    public ResourcesImpl getImpl() {
        return this.mResourcesImpl;
    }

    public int[] getIntArray(int i) throws NotFoundException {
        int[] resourceIntArray = this.mResourcesImpl.getAssets().getResourceIntArray(i);
        if (resourceIntArray != null) {
            return resourceIntArray;
        }
        throw new NotFoundException("Int array resource ID #0x" + Integer.toHexString(i));
    }

    public int getInteger(int i) throws NotFoundException {
        TypedValue typedValueObtainTempTypedValue = obtainTempTypedValue();
        try {
            this.mResourcesImpl.getValue(i, typedValueObtainTempTypedValue, true);
            if (typedValueObtainTempTypedValue.type >= 16 && typedValueObtainTempTypedValue.type <= 31) {
                int i2 = typedValueObtainTempTypedValue.data;
                releaseTempTypedValue(typedValueObtainTempTypedValue);
                return i2;
            }
            throw new NotFoundException("Resource ID #0x" + Integer.toHexString(i) + " type #0x" + Integer.toHexString(typedValueObtainTempTypedValue.type) + " is not valid");
        } catch (Throwable th) {
            releaseTempTypedValue(typedValueObtainTempTypedValue);
            throw th;
        }
    }

    public XmlResourceParser getLayout(int i) throws NotFoundException {
        return loadXmlResourceParser(i, TtmlUtils.TAG_LAYOUT);
    }

    public Movie getMovie(int i) throws NotFoundException {
        InputStream inputStreamOpenRawResource = openRawResource(i);
        Movie movieDecodeStream = Movie.decodeStream(inputStreamOpenRawResource);
        try {
            inputStreamOpenRawResource.close();
        } catch (IOException unused) {
        }
        return movieDecodeStream;
    }

    public LongSparseArray<Drawable.ConstantState> getPreloadedDrawables() {
        return this.mResourcesImpl.getPreloadedDrawables();
    }

    public String getQuantityString(int i, int i2) throws NotFoundException {
        return getQuantityText(i, i2).toString();
    }

    public String getQuantityString(int i, int i2, Object... objArr) throws NotFoundException {
        return String.format(this.mResourcesImpl.getConfiguration().getLocales().get(0), getQuantityText(i, i2).toString(), objArr);
    }

    public CharSequence getQuantityText(int i, int i2) throws NotFoundException {
        return this.mResourcesImpl.getQuantityText(i, i2);
    }

    public String getResourceEntryName(int i) throws NotFoundException {
        return this.mResourcesImpl.getResourceEntryName(i);
    }

    public String getResourceName(int i) throws NotFoundException {
        return this.mResourcesImpl.getResourceName(i);
    }

    public String getResourcePackageName(int i) throws NotFoundException {
        return this.mResourcesImpl.getResourcePackageName(i);
    }

    public String getResourceTypeName(int i) throws NotFoundException {
        return this.mResourcesImpl.getResourceTypeName(i);
    }

    public Configuration[] getSizeConfigurations() {
        return this.mResourcesImpl.getSizeConfigurations();
    }

    public ConfigurationBoundResourceCache<StateListAnimator> getStateListAnimatorCache() {
        return this.mResourcesImpl.getStateListAnimatorCache();
    }

    public String getString(int i) throws NotFoundException {
        return getText(i).toString();
    }

    public String getString(int i, Object... objArr) throws NotFoundException {
        return String.format(this.mResourcesImpl.getConfiguration().getLocales().get(0), getString(i), objArr);
    }

    public String[] getStringArray(int i) throws NotFoundException {
        String[] resourceStringArray = this.mResourcesImpl.getAssets().getResourceStringArray(i);
        if (resourceStringArray != null) {
            return resourceStringArray;
        }
        throw new NotFoundException("String array resource ID #0x" + Integer.toHexString(i));
    }

    public CharSequence getText(int i) throws NotFoundException {
        CharSequence resourceText = this.mResourcesImpl.getAssets().getResourceText(i);
        if (resourceText != null) {
            return resourceText;
        }
        throw new NotFoundException("String resource ID #0x" + Integer.toHexString(i));
    }

    public CharSequence getText(int i, CharSequence charSequence) {
        CharSequence resourceText = i != 0 ? this.mResourcesImpl.getAssets().getResourceText(i) : null;
        return resourceText != null ? resourceText : charSequence;
    }

    public CharSequence[] getTextArray(int i) throws NotFoundException {
        CharSequence[] resourceTextArray = this.mResourcesImpl.getAssets().getResourceTextArray(i);
        if (resourceTextArray != null) {
            return resourceTextArray;
        }
        throw new NotFoundException("Text array resource ID #0x" + Integer.toHexString(i));
    }

    public void getValue(int i, TypedValue typedValue, boolean z) throws NotFoundException {
        this.mResourcesImpl.getValue(i, typedValue, z);
    }

    public void getValue(String str, TypedValue typedValue, boolean z) throws NotFoundException {
        this.mResourcesImpl.getValue(str, typedValue, z);
    }

    public void getValueForDensity(int i, int i2, TypedValue typedValue, boolean z) throws NotFoundException {
        this.mResourcesImpl.getValueForDensity(i, i2, typedValue, z);
    }

    public XmlResourceParser getXml(int i) throws NotFoundException {
        return loadXmlResourceParser(i, "xml");
    }

    ColorStateList loadColorStateList(TypedValue typedValue, int i, Theme theme) throws NotFoundException {
        return this.mResourcesImpl.loadColorStateList(this, typedValue, i, theme);
    }

    public ComplexColor loadComplexColor(TypedValue typedValue, int i, Theme theme) {
        return this.mResourcesImpl.loadComplexColor(this, typedValue, i, theme);
    }

    Drawable loadDrawable(TypedValue typedValue, int i, int i2, Theme theme) throws NotFoundException {
        return this.mResourcesImpl.loadDrawable(this, typedValue, i, i2, theme);
    }

    XmlResourceParser loadXmlResourceParser(int i, String str) throws NotFoundException {
        TypedValue typedValueObtainTempTypedValue = obtainTempTypedValue();
        try {
            ResourcesImpl resourcesImpl = this.mResourcesImpl;
            resourcesImpl.getValue(i, typedValueObtainTempTypedValue, true);
            if (typedValueObtainTempTypedValue.type == 3) {
                XmlResourceParser xmlResourceParserLoadXmlResourceParser = resourcesImpl.loadXmlResourceParser(typedValueObtainTempTypedValue.string.toString(), i, typedValueObtainTempTypedValue.assetCookie, str);
                releaseTempTypedValue(typedValueObtainTempTypedValue);
                return xmlResourceParserLoadXmlResourceParser;
            }
            throw new NotFoundException("Resource ID #0x" + Integer.toHexString(i) + " type #0x" + Integer.toHexString(typedValueObtainTempTypedValue.type) + " is not valid");
        } catch (Throwable th) {
            releaseTempTypedValue(typedValueObtainTempTypedValue);
            throw th;
        }
    }

    XmlResourceParser loadXmlResourceParser(String str, int i, int i2, String str2) throws NotFoundException {
        return this.mResourcesImpl.loadXmlResourceParser(str, i, i2, str2);
    }

    public final Theme newTheme() {
        Theme theme = new Theme();
        theme.setImpl(this.mResourcesImpl.newThemeImpl());
        synchronized (this.mThemeRefs) {
            this.mThemeRefs.add(new WeakReference<>(theme));
            if (this.mThemeRefs.size() > this.mThemeRefsNextFlushSize) {
                this.mThemeRefs.removeIf(new Predicate() { // from class: android.content.res.-$$Lambda$Resources$4msWUw7LKsgLexLZjIfWa4oguq4
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        return Resources.lambda$newTheme$0((WeakReference) obj);
                    }
                });
                this.mThemeRefsNextFlushSize = Math.max(32, this.mThemeRefs.size() * 2);
            }
        }
        return theme;
    }

    public TypedArray obtainAttributes(AttributeSet attributeSet, int[] iArr) {
        TypedArray typedArrayObtain = TypedArray.obtain(this, iArr.length);
        XmlBlock.Parser parser = (XmlBlock.Parser) attributeSet;
        this.mResourcesImpl.getAssets().retrieveAttributes(parser, iArr, typedArrayObtain.mData, typedArrayObtain.mIndices);
        typedArrayObtain.mXml = parser;
        return typedArrayObtain;
    }

    public TypedArray obtainTypedArray(int i) throws NotFoundException {
        ResourcesImpl resourcesImpl = this.mResourcesImpl;
        int resourceArraySize = resourcesImpl.getAssets().getResourceArraySize(i);
        if (resourceArraySize >= 0) {
            TypedArray typedArrayObtain = TypedArray.obtain(this, resourceArraySize);
            typedArrayObtain.mLength = resourcesImpl.getAssets().getResourceArray(i, typedArrayObtain.mData);
            typedArrayObtain.mIndices[0] = 0;
            return typedArrayObtain;
        }
        throw new NotFoundException("Array resource ID #0x" + Integer.toHexString(i));
    }

    public InputStream openRawResource(int i) throws NotFoundException {
        TypedValue typedValueObtainTempTypedValue = obtainTempTypedValue();
        try {
            return openRawResource(i, typedValueObtainTempTypedValue);
        } finally {
            releaseTempTypedValue(typedValueObtainTempTypedValue);
        }
    }

    public InputStream openRawResource(int i, TypedValue typedValue) throws NotFoundException {
        return this.mResourcesImpl.openRawResource(i, typedValue);
    }

    public AssetFileDescriptor openRawResourceFd(int i) throws NotFoundException {
        TypedValue typedValueObtainTempTypedValue = obtainTempTypedValue();
        try {
            return this.mResourcesImpl.openRawResourceFd(i, typedValueObtainTempTypedValue);
        } finally {
            releaseTempTypedValue(typedValueObtainTempTypedValue);
        }
    }

    public void parseBundleExtra(String str, AttributeSet attributeSet, Bundle bundle) throws XmlPullParserException {
        TypedArray typedArrayObtainAttributes = obtainAttributes(attributeSet, R.styleable.Extra);
        String string = typedArrayObtainAttributes.getString(0);
        if (string == null) {
            typedArrayObtainAttributes.recycle();
            throw new XmlPullParserException("<" + str + "> requires an android:name attribute at " + attributeSet.getPositionDescription());
        }
        TypedValue typedValuePeekValue = typedArrayObtainAttributes.peekValue(1);
        if (typedValuePeekValue == null) {
            typedArrayObtainAttributes.recycle();
            throw new XmlPullParserException("<" + str + "> requires an android:value or android:resource attribute at " + attributeSet.getPositionDescription());
        }
        if (typedValuePeekValue.type == 3) {
            bundle.putCharSequence(string, typedValuePeekValue.coerceToString());
        } else if (typedValuePeekValue.type == 18) {
            bundle.putBoolean(string, typedValuePeekValue.data != 0);
        } else if (typedValuePeekValue.type >= 16 && typedValuePeekValue.type <= 31) {
            bundle.putInt(string, typedValuePeekValue.data);
        } else {
            if (typedValuePeekValue.type != 4) {
                typedArrayObtainAttributes.recycle();
                throw new XmlPullParserException("<" + str + "> only supports string, integer, float, color, and boolean at " + attributeSet.getPositionDescription());
            }
            bundle.putFloat(string, typedValuePeekValue.getFloat());
        }
        typedArrayObtainAttributes.recycle();
    }

    public void parseBundleExtras(XmlResourceParser xmlResourceParser, Bundle bundle) throws XmlPullParserException, IOException {
        int depth = xmlResourceParser.getDepth();
        while (true) {
            int next = xmlResourceParser.next();
            if (next == 1) {
                return;
            }
            if (next == 3 && xmlResourceParser.getDepth() <= depth) {
                return;
            }
            if (next != 3 && next != 4) {
                if (xmlResourceParser.getName().equals("extra")) {
                    parseBundleExtra("extra", xmlResourceParser, bundle);
                    XmlUtils.skipCurrentTag(xmlResourceParser);
                } else {
                    XmlUtils.skipCurrentTag(xmlResourceParser);
                }
            }
        }
    }

    public void preloadFonts(int i) {
        TypedArray typedArrayObtainTypedArray = obtainTypedArray(i);
        try {
            int length = typedArrayObtainTypedArray.length();
            for (int i2 = 0; i2 < length; i2++) {
                typedArrayObtainTypedArray.getFont(i2);
            }
            typedArrayObtainTypedArray.recycle();
        } catch (Throwable th) {
            typedArrayObtainTypedArray.recycle();
            throw th;
        }
    }

    @VisibleForTesting
    public void setCompatibilityInfo(CompatibilityInfo compatibilityInfo) {
        if (compatibilityInfo != null) {
            this.mResourcesImpl.updateConfiguration(null, null, compatibilityInfo);
        }
    }

    public void setImpl(ResourcesImpl resourcesImpl) {
        if (resourcesImpl == this.mResourcesImpl) {
            return;
        }
        this.mResourcesImpl = resourcesImpl;
        synchronized (this.mThemeRefs) {
            int size = this.mThemeRefs.size();
            for (int i = 0; i < size; i++) {
                WeakReference<Theme> weakReference = this.mThemeRefs.get(i);
                Theme theme = weakReference != null ? weakReference.get() : null;
                if (theme != null) {
                    theme.setImpl(this.mResourcesImpl.newThemeImpl(theme.getKey()));
                }
            }
        }
    }

    public final void startPreloading() {
        this.mResourcesImpl.startPreloading();
    }

    @Deprecated
    public void updateConfiguration(Configuration configuration, DisplayMetrics displayMetrics) {
        updateConfiguration(configuration, displayMetrics, null);
    }

    public void updateConfiguration(Configuration configuration, DisplayMetrics displayMetrics, CompatibilityInfo compatibilityInfo) {
        this.mResourcesImpl.updateConfiguration(configuration, displayMetrics, compatibilityInfo);
    }
}
