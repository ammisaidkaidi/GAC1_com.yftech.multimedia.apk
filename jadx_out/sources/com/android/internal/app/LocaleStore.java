package com.android.internal.app;

import android.content.Context;
import android.os.LocaleList;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import com.android.internal.content.NativeLibraryHelper;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IllformedLocaleException;
import java.util.Locale;
import java.util.Set;

/* JADX INFO: loaded from: classes3.dex */
public class LocaleStore {
    private static final HashMap<String, LocaleInfo> sLocaleCache = new HashMap<>();
    private static boolean sFullyInitialized = false;

    public static class LocaleInfo {
        private static final int SUGGESTION_TYPE_CFG = 2;
        private static final int SUGGESTION_TYPE_NONE = 0;
        private static final int SUGGESTION_TYPE_SIM = 1;
        private String mFullCountryNameNative;
        private String mFullNameNative;
        private final String mId;
        private boolean mIsChecked;
        private boolean mIsPseudo;
        private boolean mIsTranslated;
        private String mLangScriptKey;
        private final Locale mLocale;
        private final Locale mParent;
        private int mSuggestionFlags;

        private LocaleInfo(String str) {
            this(Locale.forLanguageTag(str));
        }

        private LocaleInfo(Locale locale) {
            this.mLocale = locale;
            this.mId = locale.toLanguageTag();
            this.mParent = getParent(locale);
            this.mIsChecked = false;
            this.mSuggestionFlags = 0;
            this.mIsTranslated = false;
            this.mIsPseudo = false;
        }

        static /* synthetic */ int access$076(LocaleInfo localeInfo, int i) {
            int i2 = i | localeInfo.mSuggestionFlags;
            localeInfo.mSuggestionFlags = i2;
            return i2;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public String getLangScriptKey() {
            if (this.mLangScriptKey == null) {
                Locale parent = getParent(LocaleHelper.addLikelySubtags(new Locale.Builder().setLocale(this.mLocale).setExtension('u', "").build()));
                this.mLangScriptKey = parent == null ? this.mLocale.toLanguageTag() : parent.toLanguageTag();
            }
            return this.mLangScriptKey;
        }

        private static Locale getParent(Locale locale) {
            if (locale.getCountry().isEmpty()) {
                return null;
            }
            return new Locale.Builder().setLocale(locale).setRegion("").setExtension('u', "").build();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isSuggestionOfType(int i) {
            return this.mIsTranslated && (this.mSuggestionFlags & i) == i;
        }

        public boolean getChecked() {
            return this.mIsChecked;
        }

        String getContentDescription(boolean z) {
            return z ? getFullCountryNameInUiLanguage() : getFullNameInUiLanguage();
        }

        String getFullCountryNameInUiLanguage() {
            return LocaleHelper.getDisplayCountry(this.mLocale);
        }

        String getFullCountryNameNative() {
            if (this.mFullCountryNameNative == null) {
                this.mFullCountryNameNative = LocaleHelper.getDisplayCountry(this.mLocale, this.mLocale);
            }
            return this.mFullCountryNameNative;
        }

        public String getFullNameInUiLanguage() {
            return LocaleHelper.getDisplayName(this.mLocale, true);
        }

        public String getFullNameNative() {
            if (this.mFullNameNative == null) {
                this.mFullNameNative = LocaleHelper.getDisplayName(this.mLocale, this.mLocale, true);
            }
            return this.mFullNameNative;
        }

        public String getId() {
            return this.mId;
        }

        String getLabel(boolean z) {
            return z ? getFullCountryNameNative() : getFullNameNative();
        }

        public Locale getLocale() {
            return this.mLocale;
        }

        public Locale getParent() {
            return this.mParent;
        }

        boolean isSuggested() {
            return this.mIsTranslated && this.mSuggestionFlags != 0;
        }

        public boolean isTranslated() {
            return this.mIsTranslated;
        }

        public void setChecked(boolean z) {
            this.mIsChecked = z;
        }

        public void setTranslated(boolean z) {
            this.mIsTranslated = z;
        }

        public String toString() {
            return this.mId;
        }
    }

    private static void addSuggestedLocalesForRegion(Locale locale) {
        if (locale == null) {
            return;
        }
        String country = locale.getCountry();
        if (country.isEmpty()) {
            return;
        }
        for (LocaleInfo localeInfo : sLocaleCache.values()) {
            if (country.equals(localeInfo.getLocale().getCountry())) {
                LocaleInfo.access$076(localeInfo, 1);
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:20:0x0053  */
    /* JADX WARN: Code duplicated, block: B:23:0x0065  */
    /* JADX WARN: Code duplicated, block: B:25:0x0071  */
    /* JADX WARN: Code duplicated, block: B:53:0x007b A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:54:0x007b A[SYNTHETIC] */
    public static void fillCache(Context context) {
        LocaleInfo localeInfo;
        Locale parent;
        String languageTag;
        if (sFullyInitialized) {
            return;
        }
        Set<String> simCountries = getSimCountries(context);
        boolean z = Settings.Global.getInt(context.getContentResolver(), "development_settings_enabled", 0) != 0;
        String[] supportedLocales = LocalePicker.getSupportedLocales(context);
        int length = supportedLocales.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                HashSet hashSet = new HashSet();
                for (String str : LocalePicker.getSystemAssetLocales()) {
                    LocaleInfo localeInfo2 = new LocaleInfo(str);
                    String country = localeInfo2.getLocale().getCountry();
                    if (!country.isEmpty()) {
                        if (sLocaleCache.containsKey(localeInfo2.getId())) {
                            localeInfo = sLocaleCache.get(localeInfo2.getId());
                        } else {
                            String str2 = localeInfo2.getLangScriptKey() + NativeLibraryHelper.CLEAR_ABI_OVERRIDE + country;
                            localeInfo = sLocaleCache.containsKey(str2) ? sLocaleCache.get(str2) : null;
                        }
                        if (localeInfo != null) {
                            LocaleInfo.access$076(localeInfo, 2);
                        }
                    }
                    hashSet.add(localeInfo2.getLangScriptKey());
                }
                for (LocaleInfo localeInfo3 : sLocaleCache.values()) {
                    localeInfo3.setTranslated(hashSet.contains(localeInfo3.getLangScriptKey()));
                }
                addSuggestedLocalesForRegion(Locale.getDefault());
                sFullyInitialized = true;
                return;
            }
            String str3 = supportedLocales[i];
            if (str3.isEmpty()) {
                throw new IllformedLocaleException("Bad locale entry in locale_config.xml");
            }
            LocaleInfo localeInfo4 = new LocaleInfo(str3);
            if (!LocaleList.isPseudoLocale(localeInfo4.getLocale())) {
                if (simCountries.contains(localeInfo4.getLocale().getCountry())) {
                    LocaleInfo.access$076(localeInfo4, 1);
                }
                sLocaleCache.put(localeInfo4.getId(), localeInfo4);
                parent = localeInfo4.getParent();
                if (parent != null) {
                    languageTag = parent.toLanguageTag();
                    if (!sLocaleCache.containsKey(languageTag)) {
                        sLocaleCache.put(languageTag, new LocaleInfo(parent));
                    }
                }
            } else if (z) {
                localeInfo4.setTranslated(true);
                localeInfo4.mIsPseudo = true;
                LocaleInfo.access$076(localeInfo4, 1);
                if (simCountries.contains(localeInfo4.getLocale().getCountry())) {
                    LocaleInfo.access$076(localeInfo4, 1);
                }
                sLocaleCache.put(localeInfo4.getId(), localeInfo4);
                parent = localeInfo4.getParent();
                if (parent != null) {
                    languageTag = parent.toLanguageTag();
                    if (!sLocaleCache.containsKey(languageTag)) {
                        sLocaleCache.put(languageTag, new LocaleInfo(parent));
                    }
                }
            }
            i++;
        }
    }

    private static int getLevel(Set<String> set, LocaleInfo localeInfo, boolean z) {
        if (set.contains(localeInfo.getId())) {
            return 0;
        }
        if (localeInfo.mIsPseudo) {
            return 2;
        }
        return ((!z || localeInfo.isTranslated()) && localeInfo.getParent() != null) ? 2 : 0;
    }

    public static Set<LocaleInfo> getLevelLocales(Context context, Set<String> set, LocaleInfo localeInfo, boolean z) {
        fillCache(context);
        String id = localeInfo == null ? null : localeInfo.getId();
        HashSet hashSet = new HashSet();
        for (LocaleInfo localeInfo2 : sLocaleCache.values()) {
            if (getLevel(set, localeInfo2, z) == 2) {
                if (localeInfo != null) {
                    if (id.equals(localeInfo2.getParent().toLanguageTag())) {
                        hashSet.add(localeInfo2);
                    }
                } else if (localeInfo2.isSuggestionOfType(1)) {
                    hashSet.add(localeInfo2);
                } else {
                    hashSet.add(getLocaleInfo(localeInfo2.getParent()));
                }
            }
        }
        return hashSet;
    }

    public static LocaleInfo getLocaleInfo(Locale locale) {
        String languageTag = locale.toLanguageTag();
        if (sLocaleCache.containsKey(languageTag)) {
            return sLocaleCache.get(languageTag);
        }
        LocaleInfo localeInfo = new LocaleInfo(locale);
        sLocaleCache.put(languageTag, localeInfo);
        return localeInfo;
    }

    private static Set<String> getSimCountries(Context context) {
        HashSet hashSet = new HashSet();
        TelephonyManager telephonyManagerFrom = TelephonyManager.from(context);
        if (telephonyManagerFrom != null) {
            String upperCase = telephonyManagerFrom.getSimCountryIso().toUpperCase(Locale.US);
            if (!upperCase.isEmpty()) {
                hashSet.add(upperCase);
            }
            String upperCase2 = telephonyManagerFrom.getNetworkCountryIso().toUpperCase(Locale.US);
            if (!upperCase2.isEmpty()) {
                hashSet.add(upperCase2);
            }
        }
        return hashSet;
    }

    public static void updateSimCountries(Context context) {
        Set<String> simCountries = getSimCountries(context);
        for (LocaleInfo localeInfo : sLocaleCache.values()) {
            if (simCountries.contains(localeInfo.getLocale().getCountry())) {
                LocaleInfo.access$076(localeInfo, 1);
            }
        }
    }
}
