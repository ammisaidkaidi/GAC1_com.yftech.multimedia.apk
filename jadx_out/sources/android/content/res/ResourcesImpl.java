package android.content.res;

import android.animation.Animator;
import android.animation.StateListAnimator;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.icu.text.PluralRules;
import android.net.wifi.WifiEnterpriseConfig;
import android.os.Build;
import android.os.LocaleList;
import android.os.Process;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.TypedValue;
import android.util.Xml;
import android.view.DisplayAdjustments;
import com.android.internal.util.GrowingArrayUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Supplier;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public class ResourcesImpl {
    private static final boolean DEBUG_CONFIG = false;
    private static final boolean DEBUG_LOAD = false;
    private static final int ID_OTHER = 16777220;
    static final String TAG = "Resources";
    static final String TAG_PRELOAD = "Resources.preload";
    private static final boolean TRACE_FOR_MISS_PRELOAD = false;
    private static final boolean TRACE_FOR_PRELOAD = false;
    private static final int XML_BLOCK_CACHE_SIZE = 4;
    private static int sPreloadTracingNumLoadedDrawables;
    private static boolean sPreloaded;
    final AssetManager mAssets;
    private final DisplayAdjustments mDisplayAdjustments;
    private PluralRules mPluralRule;
    private long mPreloadTracingPreloadStartTime;
    private long mPreloadTracingStartBitmapCount;
    private long mPreloadTracingStartBitmapSize;
    private boolean mPreloading;
    public static final boolean TRACE_FOR_DETAILED_PRELOAD = SystemProperties.getBoolean("debug.trace_resource_preload", false);
    private static final Object sSync = new Object();
    private static final LongSparseArray<Drawable.ConstantState> sPreloadedColorDrawables = new LongSparseArray<>();
    private static final LongSparseArray<ConstantState<ComplexColor>> sPreloadedComplexColors = new LongSparseArray<>();
    private static final LongSparseArray<Drawable.ConstantState>[] sPreloadedDrawables = new LongSparseArray[2];
    private final Object mAccessLock = new Object();
    private final Configuration mTmpConfig = new Configuration();
    private final DrawableCache mDrawableCache = new DrawableCache();
    private final DrawableCache mColorDrawableCache = new DrawableCache();
    private final ConfigurationBoundResourceCache<ComplexColor> mComplexColorCache = new ConfigurationBoundResourceCache<>();
    private final ConfigurationBoundResourceCache<Animator> mAnimatorCache = new ConfigurationBoundResourceCache<>();
    private final ConfigurationBoundResourceCache<StateListAnimator> mStateListAnimatorCache = new ConfigurationBoundResourceCache<>();
    private final ThreadLocal<LookupStack> mLookupStack = ThreadLocal.withInitial(new Supplier() { // from class: android.content.res.-$$Lambda$ResourcesImpl$h3PTRX185BeQl8SVC2_w9arp5Og
        @Override // java.util.function.Supplier
        public final Object get() {
            return ResourcesImpl.lambda$new$0();
        }
    });
    private int mLastCachedXmlBlockIndex = -1;
    private final int[] mCachedXmlBlockCookies = new int[4];
    private final String[] mCachedXmlBlockFiles = new String[4];
    private final XmlBlock[] mCachedXmlBlocks = new XmlBlock[4];
    private final DisplayMetrics mMetrics = new DisplayMetrics();
    private final Configuration mConfiguration = new Configuration();

    /* JADX INFO: Access modifiers changed from: private */
    static class LookupStack {
        private int[] mIds;
        private int mSize;

        private LookupStack() {
            this.mIds = new int[4];
            this.mSize = 0;
        }

        public boolean contains(int i) {
            for (int i2 = 0; i2 < this.mSize; i2++) {
                if (this.mIds[i2] == i) {
                    return true;
                }
            }
            return false;
        }

        public void pop() {
            this.mSize--;
        }

        public void push(int i) {
            this.mIds = GrowingArrayUtils.append(this.mIds, this.mSize, i);
            this.mSize++;
        }
    }

    public class ThemeImpl {
        private final AssetManager mAssets;
        private final long mTheme;
        private final Resources.ThemeKey mKey = new Resources.ThemeKey();
        private int mThemeResId = 0;

        ThemeImpl() {
            this.mAssets = ResourcesImpl.this.mAssets;
            this.mTheme = this.mAssets.createTheme();
        }

        void applyStyle(int i, boolean z) {
            synchronized (this.mKey) {
                this.mAssets.applyStyleToTheme(this.mTheme, i, z);
                this.mThemeResId = i;
                this.mKey.append(i, z);
            }
        }

        public void dump(int i, String str, String str2) {
            synchronized (this.mKey) {
                this.mAssets.dumpTheme(this.mTheme, i, str, str2);
            }
        }

        protected void finalize() throws Throwable {
            super.finalize();
            this.mAssets.releaseTheme(this.mTheme);
        }

        int[] getAllAttributes() {
            return this.mAssets.getStyleAttributes(getAppliedStyleResId());
        }

        int getAppliedStyleResId() {
            return this.mThemeResId;
        }

        int getChangingConfigurations() {
            int iActivityInfoConfigNativeToJava;
            synchronized (this.mKey) {
                iActivityInfoConfigNativeToJava = ActivityInfo.activityInfoConfigNativeToJava(AssetManager.nativeThemeGetChangingConfigurations(this.mTheme));
            }
            return iActivityInfoConfigNativeToJava;
        }

        Resources.ThemeKey getKey() {
            return this.mKey;
        }

        long getNativeTheme() {
            return this.mTheme;
        }

        String[] getTheme() {
            String[] strArr;
            synchronized (this.mKey) {
                int i = this.mKey.mCount;
                strArr = new String[i * 2];
                int i2 = 0;
                int i3 = i - 1;
                while (i2 < strArr.length) {
                    int i4 = this.mKey.mResId[i3];
                    boolean z = this.mKey.mForce[i3];
                    try {
                        strArr[i2] = ResourcesImpl.this.getResourceName(i4);
                    } catch (Resources.NotFoundException unused) {
                        strArr[i2] = Integer.toHexString(i2);
                    }
                    strArr[i2 + 1] = z ? "forced" : "not forced";
                    i2 += 2;
                    i3--;
                }
            }
            return strArr;
        }

        TypedArray obtainStyledAttributes(Resources.Theme theme, AttributeSet attributeSet, int[] iArr, int i, int i2) {
            TypedArray typedArrayObtain;
            synchronized (this.mKey) {
                typedArrayObtain = TypedArray.obtain(theme.getResources(), iArr.length);
                XmlBlock.Parser parser = (XmlBlock.Parser) attributeSet;
                this.mAssets.applyStyle(this.mTheme, i, i2, parser, iArr, typedArrayObtain.mDataAddress, typedArrayObtain.mIndicesAddress);
                typedArrayObtain.mTheme = theme;
                typedArrayObtain.mXml = parser;
            }
            return typedArrayObtain;
        }

        void rebase() {
            synchronized (this.mKey) {
                AssetManager.nativeThemeClear(this.mTheme);
                for (int i = 0; i < this.mKey.mCount; i++) {
                    this.mAssets.applyStyleToTheme(this.mTheme, this.mKey.mResId[i], this.mKey.mForce[i]);
                }
            }
        }

        boolean resolveAttribute(int i, TypedValue typedValue, boolean z) {
            boolean themeValue;
            synchronized (this.mKey) {
                themeValue = this.mAssets.getThemeValue(this.mTheme, i, typedValue, z);
            }
            return themeValue;
        }

        TypedArray resolveAttributes(Resources.Theme theme, int[] iArr, int[] iArr2) {
            TypedArray typedArrayObtain;
            synchronized (this.mKey) {
                int length = iArr2.length;
                if (iArr == null || length != iArr.length) {
                    throw new IllegalArgumentException("Base attribute values must the same length as attrs");
                }
                typedArrayObtain = TypedArray.obtain(theme.getResources(), length);
                this.mAssets.resolveAttrs(this.mTheme, 0, 0, iArr, iArr2, typedArrayObtain.mData, typedArrayObtain.mIndices);
                typedArrayObtain.mTheme = theme;
                typedArrayObtain.mXml = null;
            }
            return typedArrayObtain;
        }

        void setTo(ThemeImpl themeImpl) {
            synchronized (this.mKey) {
                synchronized (themeImpl.mKey) {
                    AssetManager.nativeThemeCopy(this.mTheme, themeImpl.mTheme);
                    this.mThemeResId = themeImpl.mThemeResId;
                    this.mKey.setTo(themeImpl.getKey());
                }
            }
        }
    }

    static {
        sPreloadedDrawables[0] = new LongSparseArray<>();
        sPreloadedDrawables[1] = new LongSparseArray<>();
    }

    public ResourcesImpl(AssetManager assetManager, DisplayMetrics displayMetrics, Configuration configuration, DisplayAdjustments displayAdjustments) {
        this.mAssets = assetManager;
        this.mMetrics.setToDefaults();
        this.mDisplayAdjustments = displayAdjustments;
        this.mConfiguration.setToDefaults();
        updateConfiguration(configuration, displayMetrics, displayAdjustments.getCompatibilityInfo());
    }

    private static String adjustLanguageTag(String str) {
        String strSubstring;
        int iIndexOf = str.indexOf(45);
        if (iIndexOf == -1) {
            strSubstring = "";
        } else {
            String strSubstring2 = str.substring(0, iIndexOf);
            strSubstring = str.substring(iIndexOf);
            str = strSubstring2;
        }
        return Locale.adjustLanguageCode(str) + strSubstring;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:20:0x003e  */
    private static int attrForQuantityCode(String str) {
        switch (str) {
            case "zero":
                return 16777221;
            case "one":
                return 16777222;
            case "two":
                return 16777223;
            case "few":
                return 16777224;
            case "many":
                return 16777225;
            default:
                return ID_OTHER;
        }
    }

    private void cacheDrawable(TypedValue typedValue, boolean z, DrawableCache drawableCache, Resources.Theme theme, boolean z2, long j, Drawable drawable) {
        Drawable.ConstantState constantState = drawable.getConstantState();
        if (constantState == null) {
            return;
        }
        if (!this.mPreloading) {
            synchronized (this.mAccessLock) {
                drawableCache.put(j, theme, constantState, z2);
            }
            return;
        }
        int changingConfigurations = constantState.getChangingConfigurations();
        if (z) {
            if (verifyPreloadConfig(changingConfigurations, 0, typedValue.resourceId, "drawable")) {
                sPreloadedColorDrawables.put(j, constantState);
            }
        } else if (verifyPreloadConfig(changingConfigurations, 8192, typedValue.resourceId, "drawable")) {
            if ((changingConfigurations & 8192) != 0) {
                sPreloadedDrawables[this.mConfiguration.getLayoutDirection()].put(j, constantState);
            } else {
                sPreloadedDrawables[0].put(j, constantState);
                sPreloadedDrawables[1].put(j, constantState);
            }
        }
    }

    private Drawable decodeImageDrawable(AssetManager.AssetInputStream assetInputStream, Resources resources, TypedValue typedValue) {
        try {
            return ImageDecoder.decodeDrawable(new ImageDecoder.AssetInputStreamSource(assetInputStream, resources, typedValue), new ImageDecoder.OnHeaderDecodedListener() { // from class: android.content.res.-$$Lambda$ResourcesImpl$99dm2ENnzo9b0SIUjUj2Kl3pi90
                @Override // android.graphics.ImageDecoder.OnHeaderDecodedListener
                public final void onHeaderDecoded(ImageDecoder imageDecoder, ImageDecoder.ImageInfo imageInfo, ImageDecoder.Source source) {
                    imageDecoder.setAllocator(1);
                }
            });
        } catch (IOException unused) {
            return null;
        }
    }

    private ColorStateList getColorStateListFromInt(TypedValue typedValue, long j) {
        ConstantState<ComplexColor> constantState = sPreloadedComplexColors.get(j);
        if (constantState != null) {
            return (ColorStateList) constantState.newInstance2();
        }
        ColorStateList colorStateListValueOf = ColorStateList.valueOf(typedValue.data);
        if (this.mPreloading && verifyPreloadConfig(typedValue.changingConfigurations, 0, typedValue.resourceId, "color")) {
            sPreloadedComplexColors.put(j, colorStateListValueOf.getConstantState());
        }
        return colorStateListValueOf;
    }

    private PluralRules getPluralRule() {
        PluralRules pluralRules;
        synchronized (sSync) {
            if (this.mPluralRule == null) {
                this.mPluralRule = PluralRules.forLocale(this.mConfiguration.getLocales().get(0));
            }
            pluralRules = this.mPluralRule;
        }
        return pluralRules;
    }

    static /* synthetic */ LookupStack lambda$new$0() {
        return new LookupStack();
    }

    private ComplexColor loadComplexColorForCookie(Resources resources, TypedValue typedValue, int i, Resources.Theme theme) {
        int next;
        if (typedValue.string == null) {
            throw new UnsupportedOperationException("Can't convert to ComplexColor: type=0x" + typedValue.type);
        }
        String string = typedValue.string.toString();
        ComplexColor complexColorCreateFromXmlInner = null;
        Trace.traceBegin(8192L, string);
        if (!string.endsWith(".xml")) {
            Trace.traceEnd(8192L);
            throw new Resources.NotFoundException("File " + string + " from drawable resource ID #0x" + Integer.toHexString(i) + ": .xml extension required");
        }
        try {
            XmlResourceParser xmlResourceParserLoadXmlResourceParser = loadXmlResourceParser(string, i, typedValue.assetCookie, "ComplexColor");
            AttributeSet attributeSetAsAttributeSet = Xml.asAttributeSet(xmlResourceParserLoadXmlResourceParser);
            do {
                next = xmlResourceParserLoadXmlResourceParser.next();
                if (next == 2) {
                    break;
                }
            } while (next != 1);
            if (next != 2) {
                throw new XmlPullParserException("No start tag found");
            }
            String name = xmlResourceParserLoadXmlResourceParser.getName();
            if (name.equals("gradient")) {
                complexColorCreateFromXmlInner = GradientColor.createFromXmlInner(resources, xmlResourceParserLoadXmlResourceParser, attributeSetAsAttributeSet, theme);
            } else if (name.equals("selector")) {
                complexColorCreateFromXmlInner = ColorStateList.createFromXmlInner(resources, xmlResourceParserLoadXmlResourceParser, attributeSetAsAttributeSet, theme);
            }
            xmlResourceParserLoadXmlResourceParser.close();
            Trace.traceEnd(8192L);
            return complexColorCreateFromXmlInner;
        } catch (Exception e) {
            Trace.traceEnd(8192L);
            Resources.NotFoundException notFoundException = new Resources.NotFoundException("File " + string + " from ComplexColor resource ID #0x" + Integer.toHexString(i));
            notFoundException.initCause(e);
            throw notFoundException;
        }
    }

    private ComplexColor loadComplexColorFromName(Resources resources, Resources.Theme theme, TypedValue typedValue, int i) {
        long j = (((long) typedValue.assetCookie) << 32) | ((long) typedValue.data);
        ConfigurationBoundResourceCache<ComplexColor> configurationBoundResourceCache = this.mComplexColorCache;
        ComplexColor configurationBoundResourceCache2 = configurationBoundResourceCache.getInstance(j, resources, theme);
        if (configurationBoundResourceCache2 != null) {
            return configurationBoundResourceCache2;
        }
        ConstantState<ComplexColor> constantState = sPreloadedComplexColors.get(j);
        if (constantState != null) {
            configurationBoundResourceCache2 = constantState.newInstance2(resources, theme);
        }
        if (configurationBoundResourceCache2 == null) {
            configurationBoundResourceCache2 = loadComplexColorForCookie(resources, typedValue, i, theme);
        }
        if (configurationBoundResourceCache2 != null) {
            configurationBoundResourceCache2.setBaseChangingConfigurations(typedValue.changingConfigurations);
            if (!this.mPreloading) {
                configurationBoundResourceCache.put(j, theme, configurationBoundResourceCache2.getConstantState());
            } else if (verifyPreloadConfig(configurationBoundResourceCache2.getChangingConfigurations(), 0, typedValue.resourceId, "color")) {
                sPreloadedComplexColors.put(j, configurationBoundResourceCache2.getConstantState());
            }
        }
        return configurationBoundResourceCache2;
    }

    private Drawable loadDrawableForCookie(Resources resources, TypedValue typedValue, int i, int i2) {
        long j;
        int i3;
        int i4;
        long j2;
        Drawable drawableDecodeImageDrawable;
        String resourceName;
        if (typedValue.string == null) {
            throw new Resources.NotFoundException("Resource \"" + getResourceName(i) + "\" (" + Integer.toHexString(i) + ") is not a Drawable (color or path): " + typedValue);
        }
        String string = typedValue.string.toString();
        long jNanoTime = 0;
        if (TRACE_FOR_DETAILED_PRELOAD) {
            jNanoTime = System.nanoTime();
            i3 = Bitmap.sPreloadTracingNumInstantiatedBitmaps;
            j = Bitmap.sPreloadTracingTotalBitmapsSize;
            i4 = sPreloadTracingNumLoadedDrawables;
        } else {
            j = 0;
            i3 = 0;
            i4 = 0;
        }
        Trace.traceBegin(8192L, string);
        LookupStack lookupStack = this.mLookupStack.get();
        try {
            try {
                if (lookupStack.contains(i)) {
                    throw new Exception("Recursive reference in drawable");
                }
                lookupStack.push(i);
                try {
                    if (string.endsWith(".xml")) {
                        XmlResourceParser xmlResourceParserLoadXmlResourceParser = loadXmlResourceParser(string, i, typedValue.assetCookie, "drawable");
                        drawableDecodeImageDrawable = Drawable.createFromXmlForDensity(resources, xmlResourceParserLoadXmlResourceParser, i2, null);
                        xmlResourceParserLoadXmlResourceParser.close();
                    } else {
                        drawableDecodeImageDrawable = decodeImageDrawable((AssetManager.AssetInputStream) this.mAssets.openNonAsset(typedValue.assetCookie, string, 2), resources, typedValue);
                    }
                    lookupStack.pop();
                    Trace.traceEnd(8192L);
                    if (TRACE_FOR_DETAILED_PRELOAD && (i >>> 24) == 1 && (resourceName = getResourceName(i)) != null) {
                        long jNanoTime2 = System.nanoTime() - jNanoTime;
                        int i5 = Bitmap.sPreloadTracingNumInstantiatedBitmaps - i3;
                        long j3 = Bitmap.sPreloadTracingTotalBitmapsSize - j;
                        int i6 = sPreloadTracingNumLoadedDrawables - i4;
                        sPreloadTracingNumLoadedDrawables++;
                        boolean z = Process.myUid() == 0;
                        StringBuilder sb = new StringBuilder();
                        sb.append(z ? "Preloaded FW drawable #" : "Loaded non-preloaded FW drawable #");
                        sb.append(Integer.toHexString(i));
                        sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                        sb.append(resourceName);
                        sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                        sb.append(string);
                        sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                        sb.append(drawableDecodeImageDrawable.getClass().getCanonicalName());
                        sb.append(" #nested_drawables= ");
                        sb.append(i6);
                        sb.append(" #bitmaps= ");
                        sb.append(i5);
                        sb.append(" total_bitmap_size= ");
                        sb.append(j3);
                        sb.append(" in[us] ");
                        sb.append(jNanoTime2 / 1000);
                        Log.d(TAG_PRELOAD, sb.toString());
                    }
                    return drawableDecodeImageDrawable;
                } catch (Throwable th) {
                    lookupStack.pop();
                    throw th;
                }
            } catch (Exception | StackOverflowError e) {
                e = e;
                j2 = 8192;
            }
        } catch (Exception | StackOverflowError e2) {
            e = e2;
            j2 = 8192;
        }
        Trace.traceEnd(j2);
        Resources.NotFoundException notFoundException = new Resources.NotFoundException("File " + string + " from drawable resource ID #0x" + Integer.toHexString(i));
        notFoundException.initCause(e);
        throw notFoundException;
    }

    private boolean verifyPreloadConfig(int i, int i2, int i3, String str) {
        String resourceName;
        if ((i & (-1073745921) & (~i2)) == 0) {
            return true;
        }
        try {
            resourceName = getResourceName(i3);
        } catch (Resources.NotFoundException unused) {
            resourceName = "?";
        }
        Log.w(TAG, "Preloaded " + str + " resource #0x" + Integer.toHexString(i3) + " (" + resourceName + ") that varies with configuration!!");
        return false;
    }

    public int calcConfigChanges(Configuration configuration) {
        if (configuration == null) {
            return -1;
        }
        this.mTmpConfig.setTo(configuration);
        int i = configuration.densityDpi;
        if (i == 0) {
            i = this.mMetrics.noncompatDensityDpi;
        }
        this.mDisplayAdjustments.getCompatibilityInfo().applyToConfiguration(i, this.mTmpConfig);
        if (this.mTmpConfig.getLocales().isEmpty()) {
            this.mTmpConfig.setLocales(LocaleList.getDefault());
        }
        return this.mConfiguration.updateFrom(this.mTmpConfig);
    }

    void finishPreloading() {
        if (this.mPreloading) {
            if (TRACE_FOR_DETAILED_PRELOAD) {
                long jUptimeMillis = SystemClock.uptimeMillis() - this.mPreloadTracingPreloadStartTime;
                long j = Bitmap.sPreloadTracingTotalBitmapsSize - this.mPreloadTracingStartBitmapSize;
                Log.d(TAG_PRELOAD, "Preload finished, " + (((long) Bitmap.sPreloadTracingNumInstantiatedBitmaps) - this.mPreloadTracingStartBitmapCount) + " bitmaps of " + j + " bytes in " + jUptimeMillis + " ms");
            }
            this.mPreloading = false;
            flushLayoutCache();
        }
    }

    public void flushLayoutCache() {
        synchronized (this.mCachedXmlBlocks) {
            Arrays.fill(this.mCachedXmlBlockCookies, 0);
            Arrays.fill(this.mCachedXmlBlockFiles, (Object) null);
            XmlBlock[] xmlBlockArr = this.mCachedXmlBlocks;
            for (int i = 0; i < 4; i++) {
                XmlBlock xmlBlock = xmlBlockArr[i];
                if (xmlBlock != null) {
                    xmlBlock.close();
                }
            }
            Arrays.fill(xmlBlockArr, (Object) null);
        }
    }

    ConfigurationBoundResourceCache<Animator> getAnimatorCache() {
        return this.mAnimatorCache;
    }

    public AssetManager getAssets() {
        return this.mAssets;
    }

    CompatibilityInfo getCompatibilityInfo() {
        return this.mDisplayAdjustments.getCompatibilityInfo();
    }

    Configuration getConfiguration() {
        return this.mConfiguration;
    }

    public DisplayAdjustments getDisplayAdjustments() {
        return this.mDisplayAdjustments;
    }

    DisplayMetrics getDisplayMetrics() {
        return this.mMetrics;
    }

    int getIdentifier(String str, String str2, String str3) {
        if (str == null) {
            throw new NullPointerException("name is null");
        }
        try {
            return Integer.parseInt(str);
        } catch (Exception unused) {
            return this.mAssets.getResourceIdentifier(str, str2, str3);
        }
    }

    LongSparseArray<Drawable.ConstantState> getPreloadedDrawables() {
        return sPreloadedDrawables[0];
    }

    CharSequence getQuantityText(int i, int i2) throws Resources.NotFoundException {
        PluralRules pluralRule = getPluralRule();
        double d = i2;
        CharSequence resourceBagText = this.mAssets.getResourceBagText(i, attrForQuantityCode(pluralRule.select(d)));
        if (resourceBagText != null) {
            return resourceBagText;
        }
        CharSequence resourceBagText2 = this.mAssets.getResourceBagText(i, ID_OTHER);
        if (resourceBagText2 != null) {
            return resourceBagText2;
        }
        throw new Resources.NotFoundException("Plural resource ID #0x" + Integer.toHexString(i) + " quantity=" + i2 + " item=" + pluralRule.select(d));
    }

    String getResourceEntryName(int i) throws Resources.NotFoundException {
        String resourceEntryName = this.mAssets.getResourceEntryName(i);
        if (resourceEntryName != null) {
            return resourceEntryName;
        }
        throw new Resources.NotFoundException("Unable to find resource ID #0x" + Integer.toHexString(i));
    }

    String getResourceName(int i) throws Resources.NotFoundException {
        String resourceName = this.mAssets.getResourceName(i);
        if (resourceName != null) {
            return resourceName;
        }
        throw new Resources.NotFoundException("Unable to find resource ID #0x" + Integer.toHexString(i));
    }

    String getResourcePackageName(int i) throws Resources.NotFoundException {
        String resourcePackageName = this.mAssets.getResourcePackageName(i);
        if (resourcePackageName != null) {
            return resourcePackageName;
        }
        throw new Resources.NotFoundException("Unable to find resource ID #0x" + Integer.toHexString(i));
    }

    String getResourceTypeName(int i) throws Resources.NotFoundException {
        String resourceTypeName = this.mAssets.getResourceTypeName(i);
        if (resourceTypeName != null) {
            return resourceTypeName;
        }
        throw new Resources.NotFoundException("Unable to find resource ID #0x" + Integer.toHexString(i));
    }

    Configuration[] getSizeConfigurations() {
        return this.mAssets.getSizeConfigurations();
    }

    ConfigurationBoundResourceCache<StateListAnimator> getStateListAnimatorCache() {
        return this.mStateListAnimatorCache;
    }

    void getValue(int i, TypedValue typedValue, boolean z) throws Resources.NotFoundException {
        if (this.mAssets.getResourceValue(i, 0, typedValue, z)) {
            return;
        }
        throw new Resources.NotFoundException("Resource ID #0x" + Integer.toHexString(i));
    }

    void getValue(String str, TypedValue typedValue, boolean z) throws Resources.NotFoundException {
        int identifier = getIdentifier(str, "string", null);
        if (identifier != 0) {
            getValue(identifier, typedValue, z);
            return;
        }
        throw new Resources.NotFoundException("String resource name " + str);
    }

    void getValueForDensity(int i, int i2, TypedValue typedValue, boolean z) throws Resources.NotFoundException {
        if (this.mAssets.getResourceValue(i, i2, typedValue, z)) {
            return;
        }
        throw new Resources.NotFoundException("Resource ID #0x" + Integer.toHexString(i));
    }

    ColorStateList loadColorStateList(Resources resources, TypedValue typedValue, int i, Resources.Theme theme) throws Resources.NotFoundException {
        long j = (((long) typedValue.assetCookie) << 32) | ((long) typedValue.data);
        if (typedValue.type >= 28 && typedValue.type <= 31) {
            return getColorStateListFromInt(typedValue, j);
        }
        ComplexColor complexColorLoadComplexColorFromName = loadComplexColorFromName(resources, theme, typedValue, i);
        if (complexColorLoadComplexColorFromName != null && (complexColorLoadComplexColorFromName instanceof ColorStateList)) {
            return (ColorStateList) complexColorLoadComplexColorFromName;
        }
        throw new Resources.NotFoundException("Can't find ColorStateList from drawable resource ID #0x" + Integer.toHexString(i));
    }

    ComplexColor loadComplexColor(Resources resources, TypedValue typedValue, int i, Resources.Theme theme) {
        long j = (((long) typedValue.assetCookie) << 32) | ((long) typedValue.data);
        if (typedValue.type >= 28 && typedValue.type <= 31) {
            return getColorStateListFromInt(typedValue, j);
        }
        String string = typedValue.string.toString();
        if (!string.endsWith(".xml")) {
            throw new Resources.NotFoundException("File " + string + " from drawable resource ID #0x" + Integer.toHexString(i) + ": .xml extension required");
        }
        try {
            return loadComplexColorFromName(resources, theme, typedValue, i);
        } catch (Exception e) {
            Resources.NotFoundException notFoundException = new Resources.NotFoundException("File " + string + " from complex color resource ID #0x" + Integer.toHexString(i));
            notFoundException.initCause(e);
            throw notFoundException;
        }
    }

    Drawable loadDrawable(Resources resources, TypedValue typedValue, int i, int i2, Resources.Theme theme) throws Resources.NotFoundException {
        String resourceName;
        DrawableCache drawableCache;
        long j;
        boolean z;
        Drawable colorDrawable;
        Drawable.ConstantState constantState;
        String resourceName2;
        Drawable drawableCache2;
        boolean z2 = i2 == 0 || typedValue.density == this.mMetrics.densityDpi;
        if (i2 > 0 && typedValue.density > 0 && typedValue.density != 65535) {
            if (typedValue.density == i2) {
                typedValue.density = this.mMetrics.densityDpi;
            } else {
                typedValue.density = (typedValue.density * this.mMetrics.densityDpi) / i2;
            }
        }
        try {
            if (typedValue.type < 28 || typedValue.type > 31) {
                drawableCache = this.mDrawableCache;
                j = (((long) typedValue.assetCookie) << 32) | ((long) typedValue.data);
                z = false;
            } else {
                drawableCache = this.mColorDrawableCache;
                j = typedValue.data;
                z = true;
            }
            if (!this.mPreloading && z2 && (drawableCache2 = drawableCache.getInstance(j, resources, theme)) != null) {
                drawableCache2.setChangingConfigurations(typedValue.changingConfigurations);
                return drawableCache2;
            }
            Drawable.ConstantState constantState2 = z ? sPreloadedColorDrawables.get(j) : sPreloadedDrawables[this.mConfiguration.getLayoutDirection()].get(j);
            if (constantState2 != null) {
                if (TRACE_FOR_DETAILED_PRELOAD && (i >>> 24) == 1 && Process.myUid() != 0 && (resourceName2 = getResourceName(i)) != null) {
                    Log.d(TAG_PRELOAD, "Hit preloaded FW drawable #" + Integer.toHexString(i) + WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + resourceName2);
                }
                colorDrawable = constantState2.newDrawable(resources);
            } else {
                colorDrawable = z ? new ColorDrawable(typedValue.data) : loadDrawableForCookie(resources, typedValue, i, i2);
            }
            boolean z3 = colorDrawable instanceof DrawableContainer;
            boolean z4 = colorDrawable != null && colorDrawable.canApplyTheme();
            if (z4 && theme != null) {
                colorDrawable = colorDrawable.mutate();
                colorDrawable.applyTheme(theme);
                colorDrawable.clearMutated();
            }
            Drawable drawable = colorDrawable;
            if (drawable == null) {
                return drawable;
            }
            drawable.setChangingConfigurations(typedValue.changingConfigurations);
            if (!z2) {
                return drawable;
            }
            cacheDrawable(typedValue, z, drawableCache, theme, z4, j, drawable);
            return (!z3 || (constantState = drawable.getConstantState()) == null) ? drawable : constantState.newDrawable(resources);
        } catch (Exception e) {
            try {
                resourceName = getResourceName(i);
            } catch (Resources.NotFoundException unused) {
                resourceName = "(missing name)";
            }
            Resources.NotFoundException notFoundException = new Resources.NotFoundException("Drawable " + resourceName + " with resource ID #0x" + Integer.toHexString(i), e);
            notFoundException.setStackTrace(new StackTraceElement[0]);
            throw notFoundException;
        }
    }

    public Typeface loadFont(Resources resources, TypedValue typedValue, int i) {
        if (typedValue.string == null) {
            throw new Resources.NotFoundException("Resource \"" + getResourceName(i) + "\" (" + Integer.toHexString(i) + ") is not a Font: " + typedValue);
        }
        String string = typedValue.string.toString();
        if (!string.startsWith("res/")) {
            return null;
        }
        Typeface typefaceFindFromCache = Typeface.findFromCache(this.mAssets, string);
        if (typefaceFindFromCache != null) {
            return typefaceFindFromCache;
        }
        Trace.traceBegin(8192L, string);
        try {
            if (!string.endsWith("xml")) {
                return Typeface.createFromResources(this.mAssets, string, typedValue.assetCookie);
            }
            FontResourcesParser.FamilyResourceEntry familyResourceEntry = FontResourcesParser.parse(loadXmlResourceParser(string, i, typedValue.assetCookie, "font"), resources);
            if (familyResourceEntry == null) {
                return null;
            }
            return Typeface.createFromResources(familyResourceEntry, this.mAssets, string);
        } catch (IOException e) {
            Log.e(TAG, "Failed to read xml resource " + string, e);
            return null;
        } catch (XmlPullParserException e2) {
            Log.e(TAG, "Failed to parse xml resource " + string, e2);
            return null;
        } finally {
            Trace.traceEnd(8192L);
        }
    }

    XmlResourceParser loadXmlResourceParser(String str, int i, int i2, String str2) throws Resources.NotFoundException {
        if (i != 0) {
            try {
                synchronized (this.mCachedXmlBlocks) {
                    int[] iArr = this.mCachedXmlBlockCookies;
                    String[] strArr = this.mCachedXmlBlockFiles;
                    XmlBlock[] xmlBlockArr = this.mCachedXmlBlocks;
                    int length = strArr.length;
                    for (int i3 = 0; i3 < length; i3++) {
                        if (iArr[i3] == i2 && strArr[i3] != null && strArr[i3].equals(str)) {
                            return xmlBlockArr[i3].newParser();
                        }
                    }
                    XmlBlock xmlBlockOpenXmlBlockAsset = this.mAssets.openXmlBlockAsset(i2, str);
                    if (xmlBlockOpenXmlBlockAsset != null) {
                        int i4 = (this.mLastCachedXmlBlockIndex + 1) % length;
                        this.mLastCachedXmlBlockIndex = i4;
                        XmlBlock xmlBlock = xmlBlockArr[i4];
                        if (xmlBlock != null) {
                            xmlBlock.close();
                        }
                        iArr[i4] = i2;
                        strArr[i4] = str;
                        xmlBlockArr[i4] = xmlBlockOpenXmlBlockAsset;
                        return xmlBlockOpenXmlBlockAsset.newParser();
                    }
                }
            } catch (Exception e) {
                Resources.NotFoundException notFoundException = new Resources.NotFoundException("File " + str + " from xml type " + str2 + " resource ID #0x" + Integer.toHexString(i));
                notFoundException.initCause(e);
                throw notFoundException;
            }
        }
        throw new Resources.NotFoundException("File " + str + " from xml type " + str2 + " resource ID #0x" + Integer.toHexString(i));
    }

    ThemeImpl newThemeImpl() {
        return new ThemeImpl();
    }

    ThemeImpl newThemeImpl(Resources.ThemeKey themeKey) {
        ThemeImpl themeImpl = new ThemeImpl();
        themeImpl.mKey.setTo(themeKey);
        themeImpl.rebase();
        return themeImpl;
    }

    InputStream openRawResource(int i, TypedValue typedValue) throws Resources.NotFoundException {
        getValue(i, typedValue, true);
        try {
            return this.mAssets.openNonAsset(typedValue.assetCookie, typedValue.string.toString(), 2);
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            sb.append("File ");
            sb.append(typedValue.string == null ? "(null)" : typedValue.string.toString());
            sb.append(" from drawable resource ID #0x");
            sb.append(Integer.toHexString(i));
            Resources.NotFoundException notFoundException = new Resources.NotFoundException(sb.toString());
            notFoundException.initCause(e);
            throw notFoundException;
        }
    }

    AssetFileDescriptor openRawResourceFd(int i, TypedValue typedValue) throws Resources.NotFoundException {
        getValue(i, typedValue, true);
        try {
            return this.mAssets.openNonAssetFd(typedValue.assetCookie, typedValue.string.toString());
        } catch (Exception e) {
            throw new Resources.NotFoundException("File " + typedValue.string.toString() + " from drawable resource ID #0x" + Integer.toHexString(i), e);
        }
    }

    public final void startPreloading() {
        synchronized (sSync) {
            if (sPreloaded) {
                throw new IllegalStateException("Resources already preloaded");
            }
            sPreloaded = true;
            this.mPreloading = true;
            this.mConfiguration.densityDpi = DisplayMetrics.DENSITY_DEVICE;
            updateConfiguration(null, null, null);
            if (TRACE_FOR_DETAILED_PRELOAD) {
                this.mPreloadTracingPreloadStartTime = SystemClock.uptimeMillis();
                this.mPreloadTracingStartBitmapSize = Bitmap.sPreloadTracingTotalBitmapsSize;
                this.mPreloadTracingStartBitmapCount = Bitmap.sPreloadTracingNumInstantiatedBitmaps;
                Log.d(TAG_PRELOAD, "Preload starting");
            }
        }
    }

    public void updateConfiguration(Configuration configuration, DisplayMetrics displayMetrics, CompatibilityInfo compatibilityInfo) {
        long j;
        int i;
        int i2;
        Locale firstMatchWithEnglishSupported;
        Trace.traceBegin(8192L, "ResourcesImpl#updateConfiguration");
        try {
            synchronized (this.mAccessLock) {
                try {
                    if (compatibilityInfo != null) {
                        try {
                            this.mDisplayAdjustments.setCompatibilityInfo(compatibilityInfo);
                        } catch (Throwable th) {
                            throw th;
                        }
                    }
                    if (displayMetrics != null) {
                        this.mMetrics.setTo(displayMetrics);
                    }
                    this.mDisplayAdjustments.getCompatibilityInfo().applyToDisplayMetrics(this.mMetrics);
                    int iCalcConfigChanges = calcConfigChanges(configuration);
                    LocaleList locales = this.mConfiguration.getLocales();
                    if (locales.isEmpty()) {
                        locales = LocaleList.getDefault();
                        this.mConfiguration.setLocales(locales);
                    }
                    if ((iCalcConfigChanges & 4) != 0 && locales.size() > 1) {
                        String[] nonSystemLocales = this.mAssets.getNonSystemLocales();
                        if (LocaleList.isPseudoLocalesOnly(nonSystemLocales)) {
                            nonSystemLocales = this.mAssets.getLocales();
                            if (LocaleList.isPseudoLocalesOnly(nonSystemLocales)) {
                                nonSystemLocales = null;
                            }
                        }
                        if (nonSystemLocales != null && (firstMatchWithEnglishSupported = locales.getFirstMatchWithEnglishSupported(nonSystemLocales)) != null && firstMatchWithEnglishSupported != locales.get(0)) {
                            this.mConfiguration.setLocales(new LocaleList(firstMatchWithEnglishSupported, locales));
                        }
                    }
                    if (this.mConfiguration.densityDpi != 0) {
                        this.mMetrics.densityDpi = this.mConfiguration.densityDpi;
                        this.mMetrics.density = this.mConfiguration.densityDpi * 0.00625f;
                    }
                    this.mMetrics.scaledDensity = this.mMetrics.density * (this.mConfiguration.fontScale != 0.0f ? this.mConfiguration.fontScale : 1.0f);
                    if (this.mMetrics.widthPixels >= this.mMetrics.heightPixels) {
                        i = this.mMetrics.widthPixels;
                        i2 = this.mMetrics.heightPixels;
                    } else {
                        i = this.mMetrics.heightPixels;
                        i2 = this.mMetrics.widthPixels;
                    }
                    this.mAssets.setConfiguration(this.mConfiguration.mcc, this.mConfiguration.mnc, adjustLanguageTag(this.mConfiguration.getLocales().get(0).toLanguageTag()), this.mConfiguration.orientation, this.mConfiguration.touchscreen, this.mConfiguration.densityDpi, this.mConfiguration.keyboard, (this.mConfiguration.keyboardHidden == 1 && this.mConfiguration.hardKeyboardHidden == 2) ? 3 : this.mConfiguration.keyboardHidden, this.mConfiguration.navigation, i, i2, this.mConfiguration.smallestScreenWidthDp, this.mConfiguration.screenWidthDp, this.mConfiguration.screenHeightDp, this.mConfiguration.screenLayout, this.mConfiguration.uiMode, this.mConfiguration.colorMode, Build.VERSION.RESOURCES_SDK_INT);
                    this.mDrawableCache.onConfigurationChange(iCalcConfigChanges);
                    this.mColorDrawableCache.onConfigurationChange(iCalcConfigChanges);
                    this.mComplexColorCache.onConfigurationChange(iCalcConfigChanges);
                    this.mAnimatorCache.onConfigurationChange(iCalcConfigChanges);
                    this.mStateListAnimatorCache.onConfigurationChange(iCalcConfigChanges);
                    flushLayoutCache();
                } catch (Throwable th2) {
                    th = th2;
                    j = 8192;
                    Trace.traceEnd(j);
                    throw th;
                }
            }
            synchronized (sSync) {
                if (this.mPluralRule != null) {
                    this.mPluralRule = PluralRules.forLocale(this.mConfiguration.getLocales().get(0));
                }
            }
            Trace.traceEnd(8192L);
        } catch (Throwable th3) {
            th = th3;
            j = 8192;
        }
    }
}
