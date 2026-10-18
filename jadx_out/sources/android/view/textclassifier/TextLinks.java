package android.view.textclassifier;

import android.os.LocaleList;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Spannable;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

/* JADX INFO: loaded from: classes2.dex */
public final class TextLinks implements Parcelable {
    public static final int APPLY_STRATEGY_IGNORE = 0;
    public static final int APPLY_STRATEGY_REPLACE = 1;
    public static final Parcelable.Creator<TextLinks> CREATOR = new Parcelable.Creator<TextLinks>() { // from class: android.view.textclassifier.TextLinks.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public TextLinks createFromParcel(Parcel parcel) {
            return new TextLinks(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public TextLinks[] newArray(int i) {
            return new TextLinks[i];
        }
    };
    public static final int STATUS_DIFFERENT_TEXT = 3;
    public static final int STATUS_LINKS_APPLIED = 0;
    public static final int STATUS_NO_LINKS_APPLIED = 2;
    public static final int STATUS_NO_LINKS_FOUND = 1;
    private final String mFullText;
    private final List<TextLink> mLinks;

    @Retention(RetentionPolicy.SOURCE)
    public @interface ApplyStrategy {
    }

    public static final class Builder {
        private final String mFullText;
        private final ArrayList<TextLink> mLinks = new ArrayList<>();

        public Builder(String str) {
            this.mFullText = (String) Preconditions.checkNotNull(str);
        }

        public Builder addLink(int i, int i2, Map<String, Float> map) {
            this.mLinks.add(new TextLink(i, i2, map, null));
            return this;
        }

        Builder addLink(int i, int i2, Map<String, Float> map, URLSpan uRLSpan) {
            this.mLinks.add(new TextLink(i, i2, map, uRLSpan));
            return this;
        }

        public TextLinks build() {
            return new TextLinks(this.mFullText, this.mLinks);
        }

        public Builder clearTextLinks() {
            this.mLinks.clear();
            return this;
        }
    }

    public static final class Options {
        private int mApplyStrategy;
        private String mCallingPackageName;
        private LocaleList mDefaultLocales;
        private TextClassifier.EntityConfig mEntityConfig;
        private boolean mLegacyFallback;
        private final Request mRequest;
        private final TextClassificationSessionId mSessionId;
        private Function<TextLink, TextLinkSpan> mSpanFactory;

        public Options() {
            this(null, null);
        }

        private Options(TextClassificationSessionId textClassificationSessionId, Request request) {
            this.mSessionId = textClassificationSessionId;
            this.mRequest = request;
        }

        private static void checkValidApplyStrategy(int i) {
            if (i != 0 && i != 1) {
                throw new IllegalArgumentException("Invalid apply strategy. See TextLinks.ApplyStrategy for options.");
            }
        }

        public static Options from(TextClassificationSessionId textClassificationSessionId, Request request) {
            Options options = new Options(textClassificationSessionId, request);
            options.setDefaultLocales(request.getDefaultLocales());
            options.setEntityConfig(request.getEntityConfig());
            return options;
        }

        public static Options fromLinkMask(int i) {
            ArrayList arrayList = new ArrayList();
            if ((i & 1) != 0) {
                arrayList.add("url");
            }
            if ((i & 2) != 0) {
                arrayList.add("email");
            }
            if ((i & 4) != 0) {
                arrayList.add("phone");
            }
            if ((i & 8) != 0) {
                arrayList.add("address");
            }
            return new Options().setEntityConfig(TextClassifier.EntityConfig.createWithEntityList(arrayList));
        }

        public int getApplyStrategy() {
            return this.mApplyStrategy;
        }

        public LocaleList getDefaultLocales() {
            return this.mDefaultLocales;
        }

        public TextClassifier.EntityConfig getEntityConfig() {
            return this.mEntityConfig;
        }

        public Request getRequest() {
            return this.mRequest;
        }

        public TextClassificationSessionId getSessionId() {
            return this.mSessionId;
        }

        public Function<TextLink, TextLinkSpan> getSpanFactory() {
            return this.mSpanFactory;
        }

        public Options setApplyStrategy(int i) {
            checkValidApplyStrategy(i);
            this.mApplyStrategy = i;
            return this;
        }

        public Options setDefaultLocales(LocaleList localeList) {
            this.mDefaultLocales = localeList;
            return this;
        }

        public Options setEntityConfig(TextClassifier.EntityConfig entityConfig) {
            this.mEntityConfig = entityConfig;
            return this;
        }

        public Options setSpanFactory(Function<TextLink, TextLinkSpan> function) {
            this.mSpanFactory = function;
            return this;
        }
    }

    public static final class Request implements Parcelable {
        public static final Parcelable.Creator<Request> CREATOR = new Parcelable.Creator<Request>() { // from class: android.view.textclassifier.TextLinks.Request.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public Request createFromParcel(Parcel parcel) {
                return new Request(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public Request[] newArray(int i) {
                return new Request[i];
            }
        };
        private String mCallingPackageName;
        private final LocaleList mDefaultLocales;
        private final TextClassifier.EntityConfig mEntityConfig;
        private final boolean mLegacyFallback;
        private final CharSequence mText;

        public static final class Builder {
            private String mCallingPackageName;
            private LocaleList mDefaultLocales;
            private TextClassifier.EntityConfig mEntityConfig;
            private boolean mLegacyFallback = true;
            private final CharSequence mText;

            public Builder(CharSequence charSequence) {
                this.mText = (CharSequence) Preconditions.checkNotNull(charSequence);
            }

            public Request build() {
                return new Request(this.mText, this.mDefaultLocales, this.mEntityConfig, this.mLegacyFallback, this.mCallingPackageName);
            }

            public Builder setCallingPackageName(String str) {
                this.mCallingPackageName = str;
                return this;
            }

            public Builder setDefaultLocales(LocaleList localeList) {
                this.mDefaultLocales = localeList;
                return this;
            }

            public Builder setEntityConfig(TextClassifier.EntityConfig entityConfig) {
                this.mEntityConfig = entityConfig;
                return this;
            }

            public Builder setLegacyFallback(boolean z) {
                this.mLegacyFallback = z;
                return this;
            }
        }

        private Request(Parcel parcel) {
            this.mText = parcel.readString();
            this.mDefaultLocales = parcel.readInt() == 0 ? null : LocaleList.CREATOR.createFromParcel(parcel);
            this.mEntityConfig = parcel.readInt() != 0 ? TextClassifier.EntityConfig.CREATOR.createFromParcel(parcel) : null;
            this.mLegacyFallback = true;
            this.mCallingPackageName = parcel.readString();
        }

        private Request(CharSequence charSequence, LocaleList localeList, TextClassifier.EntityConfig entityConfig, boolean z, String str) {
            this.mText = charSequence;
            this.mDefaultLocales = localeList;
            this.mEntityConfig = entityConfig;
            this.mLegacyFallback = z;
            this.mCallingPackageName = str;
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public String getCallingPackageName() {
            return this.mCallingPackageName;
        }

        public LocaleList getDefaultLocales() {
            return this.mDefaultLocales;
        }

        public TextClassifier.EntityConfig getEntityConfig() {
            return this.mEntityConfig;
        }

        public CharSequence getText() {
            return this.mText;
        }

        public boolean isLegacyFallback() {
            return this.mLegacyFallback;
        }

        void setCallingPackageName(String str) {
            this.mCallingPackageName = str;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(this.mText.toString());
            parcel.writeInt(this.mDefaultLocales != null ? 1 : 0);
            if (this.mDefaultLocales != null) {
                this.mDefaultLocales.writeToParcel(parcel, i);
            }
            parcel.writeInt(this.mEntityConfig != null ? 1 : 0);
            if (this.mEntityConfig != null) {
                this.mEntityConfig.writeToParcel(parcel, i);
            }
            parcel.writeString(this.mCallingPackageName);
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface Status {
    }

    public static final class TextLink implements Parcelable {
        public static final Parcelable.Creator<TextLink> CREATOR = new Parcelable.Creator<TextLink>() { // from class: android.view.textclassifier.TextLinks.TextLink.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public TextLink createFromParcel(Parcel parcel) {
                return new TextLink(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public TextLink[] newArray(int i) {
                return new TextLink[i];
            }
        };
        private final int mEnd;
        private final EntityConfidence mEntityScores;
        private final int mStart;
        final URLSpan mUrlSpan;

        TextLink(int i, int i2, Map<String, Float> map, URLSpan uRLSpan) {
            Preconditions.checkNotNull(map);
            Preconditions.checkArgument(!map.isEmpty());
            Preconditions.checkArgument(i <= i2);
            this.mStart = i;
            this.mEnd = i2;
            this.mEntityScores = new EntityConfidence(map);
            this.mUrlSpan = uRLSpan;
        }

        private TextLink(Parcel parcel) {
            this.mEntityScores = EntityConfidence.CREATOR.createFromParcel(parcel);
            this.mStart = parcel.readInt();
            this.mEnd = parcel.readInt();
            this.mUrlSpan = null;
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public float getConfidenceScore(String str) {
            return this.mEntityScores.getConfidenceScore(str);
        }

        public int getEnd() {
            return this.mEnd;
        }

        public String getEntity(int i) {
            return this.mEntityScores.getEntities().get(i);
        }

        public int getEntityCount() {
            return this.mEntityScores.getEntities().size();
        }

        public int getStart() {
            return this.mStart;
        }

        public String toString() {
            return String.format(Locale.US, "TextLink{start=%s, end=%s, entityScores=%s, urlSpan=%s}", Integer.valueOf(this.mStart), Integer.valueOf(this.mEnd), this.mEntityScores, this.mUrlSpan);
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            this.mEntityScores.writeToParcel(parcel, i);
            parcel.writeInt(this.mStart);
            parcel.writeInt(this.mEnd);
        }
    }

    public static class TextLinkSpan extends ClickableSpan {
        public static final int INVOCATION_METHOD_KEYBOARD = 1;
        public static final int INVOCATION_METHOD_TOUCH = 0;
        public static final int INVOCATION_METHOD_UNSPECIFIED = -1;
        private final TextLink mTextLink;

        @Retention(RetentionPolicy.SOURCE)
        public @interface InvocationMethod {
        }

        public TextLinkSpan(TextLink textLink) {
            this.mTextLink = textLink;
        }

        public final TextLink getTextLink() {
            return this.mTextLink;
        }

        @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
        public final String getUrl() {
            if (this.mTextLink.mUrlSpan != null) {
                return this.mTextLink.mUrlSpan.getURL();
            }
            return null;
        }

        @Override // android.text.style.ClickableSpan
        public void onClick(View view) {
            onClick(view, -1);
        }

        public final void onClick(View view, int i) {
            if (view instanceof TextView) {
                TextView textView = (TextView) view;
                if (TextClassificationManager.getSettings(textView.getContext()).isSmartLinkifyEnabled()) {
                    if (i != 0) {
                        textView.handleClick(this);
                        return;
                    } else {
                        textView.requestActionMode(this);
                        return;
                    }
                }
                if (this.mTextLink.mUrlSpan != null) {
                    this.mTextLink.mUrlSpan.onClick(textView);
                } else {
                    textView.handleClick(this);
                }
            }
        }
    }

    private TextLinks(Parcel parcel) {
        this.mFullText = parcel.readString();
        this.mLinks = parcel.createTypedArrayList(TextLink.CREATOR);
    }

    private TextLinks(String str, ArrayList<TextLink> arrayList) {
        this.mFullText = str;
        this.mLinks = Collections.unmodifiableList(arrayList);
    }

    public int apply(Spannable spannable, int i, Function<TextLink, TextLinkSpan> function) {
        Preconditions.checkNotNull(spannable);
        return new TextLinksParams.Builder().setApplyStrategy(i).setSpanFactory(function).build().apply(spannable, this);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public Collection<TextLink> getLinks() {
        return this.mLinks;
    }

    public String getText() {
        return this.mFullText;
    }

    public String toString() {
        return String.format(Locale.US, "TextLinks{fullText=%s, links=%s}", this.mFullText, this.mLinks);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mFullText);
        parcel.writeTypedList(this.mLinks);
    }
}
