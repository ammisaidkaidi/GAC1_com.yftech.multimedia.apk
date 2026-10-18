package android.print;

import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;

/* JADX INFO: loaded from: classes2.dex */
public final class PrintJobInfo implements Parcelable {
    public static final Parcelable.Creator<PrintJobInfo> CREATOR = new Parcelable.Creator<PrintJobInfo>() { // from class: android.print.PrintJobInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PrintJobInfo createFromParcel(Parcel parcel) {
            return new PrintJobInfo(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PrintJobInfo[] newArray(int i) {
            return new PrintJobInfo[i];
        }
    };
    public static final int STATE_ANY = -1;
    public static final int STATE_ANY_ACTIVE = -3;
    public static final int STATE_ANY_SCHEDULED = -4;
    public static final int STATE_ANY_VISIBLE_TO_CLIENTS = -2;
    public static final int STATE_BLOCKED = 4;
    public static final int STATE_CANCELED = 7;
    public static final int STATE_COMPLETED = 5;
    public static final int STATE_CREATED = 1;
    public static final int STATE_FAILED = 6;
    public static final int STATE_QUEUED = 2;
    public static final int STATE_STARTED = 3;
    private Bundle mAdvancedOptions;
    private int mAppId;
    private PrintAttributes mAttributes;
    private boolean mCanceling;
    private int mCopies;
    private long mCreationTime;
    private PrintDocumentInfo mDocumentInfo;
    private PrintJobId mId;
    private String mLabel;
    private PageRange[] mPageRanges;
    private PrinterId mPrinterId;
    private String mPrinterName;
    private float mProgress;
    private int mState;
    private CharSequence mStatus;
    private int mStatusRes;
    private CharSequence mStatusResAppPackageName;
    private String mTag;

    public static final class Builder {
        private final PrintJobInfo mPrototype;

        public Builder(PrintJobInfo printJobInfo) {
            this.mPrototype = printJobInfo != null ? new PrintJobInfo(printJobInfo) : new PrintJobInfo();
        }

        public PrintJobInfo build() {
            return this.mPrototype;
        }

        public void putAdvancedOption(String str, int i) {
            if (this.mPrototype.mAdvancedOptions == null) {
                this.mPrototype.mAdvancedOptions = new Bundle();
            }
            this.mPrototype.mAdvancedOptions.putInt(str, i);
        }

        public void putAdvancedOption(String str, String str2) {
            Preconditions.checkNotNull(str, "key cannot be null");
            if (this.mPrototype.mAdvancedOptions == null) {
                this.mPrototype.mAdvancedOptions = new Bundle();
            }
            this.mPrototype.mAdvancedOptions.putString(str, str2);
        }

        public void setAttributes(PrintAttributes printAttributes) {
            this.mPrototype.mAttributes = printAttributes;
        }

        public void setCopies(int i) {
            this.mPrototype.mCopies = i;
        }

        public void setPages(PageRange[] pageRangeArr) {
            this.mPrototype.mPageRanges = pageRangeArr;
        }

        public void setProgress(float f) {
            Preconditions.checkArgumentInRange(f, 0.0f, 1.0f, "progress");
            this.mPrototype.mProgress = f;
        }

        public void setStatus(CharSequence charSequence) {
            this.mPrototype.mStatus = charSequence;
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
    }

    public PrintJobInfo() {
        this.mProgress = -1.0f;
    }

    private PrintJobInfo(Parcel parcel) {
        this.mId = (PrintJobId) parcel.readParcelable(null);
        this.mLabel = parcel.readString();
        this.mPrinterId = (PrinterId) parcel.readParcelable(null);
        this.mPrinterName = parcel.readString();
        this.mState = parcel.readInt();
        this.mAppId = parcel.readInt();
        this.mTag = parcel.readString();
        this.mCreationTime = parcel.readLong();
        this.mCopies = parcel.readInt();
        Parcelable[] parcelableArray = parcel.readParcelableArray(null);
        if (parcelableArray != null) {
            this.mPageRanges = new PageRange[parcelableArray.length];
            for (int i = 0; i < parcelableArray.length; i++) {
                this.mPageRanges[i] = (PageRange) parcelableArray[i];
            }
        }
        this.mAttributes = (PrintAttributes) parcel.readParcelable(null);
        this.mDocumentInfo = (PrintDocumentInfo) parcel.readParcelable(null);
        this.mProgress = parcel.readFloat();
        this.mStatus = parcel.readCharSequence();
        this.mStatusRes = parcel.readInt();
        this.mStatusResAppPackageName = parcel.readCharSequence();
        this.mCanceling = parcel.readInt() == 1;
        this.mAdvancedOptions = parcel.readBundle();
        if (this.mAdvancedOptions != null) {
            Preconditions.checkArgument(!this.mAdvancedOptions.containsKey(null));
        }
    }

    public PrintJobInfo(PrintJobInfo printJobInfo) {
        this.mId = printJobInfo.mId;
        this.mLabel = printJobInfo.mLabel;
        this.mPrinterId = printJobInfo.mPrinterId;
        this.mPrinterName = printJobInfo.mPrinterName;
        this.mState = printJobInfo.mState;
        this.mAppId = printJobInfo.mAppId;
        this.mTag = printJobInfo.mTag;
        this.mCreationTime = printJobInfo.mCreationTime;
        this.mCopies = printJobInfo.mCopies;
        this.mPageRanges = printJobInfo.mPageRanges;
        this.mAttributes = printJobInfo.mAttributes;
        this.mDocumentInfo = printJobInfo.mDocumentInfo;
        this.mProgress = printJobInfo.mProgress;
        this.mStatus = printJobInfo.mStatus;
        this.mStatusRes = printJobInfo.mStatusRes;
        this.mStatusResAppPackageName = printJobInfo.mStatusResAppPackageName;
        this.mCanceling = printJobInfo.mCanceling;
        this.mAdvancedOptions = printJobInfo.mAdvancedOptions;
    }

    public static String stateToString(int i) {
        switch (i) {
            case 1:
                return "STATE_CREATED";
            case 2:
                return "STATE_QUEUED";
            case 3:
                return "STATE_STARTED";
            case 4:
                return "STATE_BLOCKED";
            case 5:
                return "STATE_COMPLETED";
            case 6:
                return "STATE_FAILED";
            case 7:
                return "STATE_CANCELED";
            default:
                return "STATE_UNKNOWN";
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public int getAdvancedIntOption(String str) {
        if (this.mAdvancedOptions != null) {
            return this.mAdvancedOptions.getInt(str);
        }
        return 0;
    }

    public Bundle getAdvancedOptions() {
        return this.mAdvancedOptions;
    }

    public String getAdvancedStringOption(String str) {
        if (this.mAdvancedOptions != null) {
            return this.mAdvancedOptions.getString(str);
        }
        return null;
    }

    public int getAppId() {
        return this.mAppId;
    }

    public PrintAttributes getAttributes() {
        return this.mAttributes;
    }

    public int getCopies() {
        return this.mCopies;
    }

    public long getCreationTime() {
        return this.mCreationTime;
    }

    public PrintDocumentInfo getDocumentInfo() {
        return this.mDocumentInfo;
    }

    public PrintJobId getId() {
        return this.mId;
    }

    public String getLabel() {
        return this.mLabel;
    }

    public PageRange[] getPages() {
        return this.mPageRanges;
    }

    public PrinterId getPrinterId() {
        return this.mPrinterId;
    }

    public String getPrinterName() {
        return this.mPrinterName;
    }

    public float getProgress() {
        return this.mProgress;
    }

    public int getState() {
        return this.mState;
    }

    public CharSequence getStatus(PackageManager packageManager) {
        if (this.mStatusRes == 0) {
            return this.mStatus;
        }
        try {
            return packageManager.getResourcesForApplication(this.mStatusResAppPackageName.toString()).getString(this.mStatusRes);
        } catch (PackageManager.NameNotFoundException | Resources.NotFoundException unused) {
            return null;
        }
    }

    public String getTag() {
        return this.mTag;
    }

    public boolean hasAdvancedOption(String str) {
        return this.mAdvancedOptions != null && this.mAdvancedOptions.containsKey(str);
    }

    public boolean isCancelling() {
        return this.mCanceling;
    }

    public void setAdvancedOptions(Bundle bundle) {
        this.mAdvancedOptions = bundle;
    }

    public void setAppId(int i) {
        this.mAppId = i;
    }

    public void setAttributes(PrintAttributes printAttributes) {
        this.mAttributes = printAttributes;
    }

    public void setCancelling(boolean z) {
        this.mCanceling = z;
    }

    public void setCopies(int i) {
        if (i < 1) {
            throw new IllegalArgumentException("Copies must be more than one.");
        }
        this.mCopies = i;
    }

    public void setCreationTime(long j) {
        if (j < 0) {
            throw new IllegalArgumentException("creationTime must be non-negative.");
        }
        this.mCreationTime = j;
    }

    public void setDocumentInfo(PrintDocumentInfo printDocumentInfo) {
        this.mDocumentInfo = printDocumentInfo;
    }

    public void setId(PrintJobId printJobId) {
        this.mId = printJobId;
    }

    public void setLabel(String str) {
        this.mLabel = str;
    }

    public void setPages(PageRange[] pageRangeArr) {
        this.mPageRanges = pageRangeArr;
    }

    public void setPrinterId(PrinterId printerId) {
        this.mPrinterId = printerId;
    }

    public void setPrinterName(String str) {
        this.mPrinterName = str;
    }

    public void setProgress(float f) {
        Preconditions.checkArgumentInRange(f, 0.0f, 1.0f, "progress");
        this.mProgress = f;
    }

    public void setState(int i) {
        this.mState = i;
    }

    public void setStatus(int i, CharSequence charSequence) {
        this.mStatus = null;
        this.mStatusRes = i;
        this.mStatusResAppPackageName = charSequence;
    }

    public void setStatus(CharSequence charSequence) {
        this.mStatusRes = 0;
        this.mStatusResAppPackageName = null;
        this.mStatus = charSequence;
    }

    public void setTag(String str) {
        this.mTag = str;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PrintJobInfo{");
        sb.append("label: ");
        sb.append(this.mLabel);
        sb.append(", id: ");
        sb.append(this.mId);
        sb.append(", state: ");
        sb.append(stateToString(this.mState));
        sb.append(", printer: " + this.mPrinterId);
        sb.append(", tag: ");
        sb.append(this.mTag);
        sb.append(", creationTime: " + this.mCreationTime);
        sb.append(", copies: ");
        sb.append(this.mCopies);
        StringBuilder sb2 = new StringBuilder();
        sb2.append(", attributes: ");
        sb2.append(this.mAttributes != null ? this.mAttributes.toString() : null);
        sb.append(sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        sb3.append(", documentInfo: ");
        sb3.append(this.mDocumentInfo != null ? this.mDocumentInfo.toString() : null);
        sb.append(sb3.toString());
        sb.append(", cancelling: " + this.mCanceling);
        StringBuilder sb4 = new StringBuilder();
        sb4.append(", pages: ");
        sb4.append(this.mPageRanges != null ? Arrays.toString(this.mPageRanges) : null);
        sb.append(sb4.toString());
        StringBuilder sb5 = new StringBuilder();
        sb5.append(", hasAdvancedOptions: ");
        sb5.append(this.mAdvancedOptions != null);
        sb.append(sb5.toString());
        sb.append(", progress: " + this.mProgress);
        StringBuilder sb6 = new StringBuilder();
        sb6.append(", status: ");
        sb6.append(this.mStatus != null ? this.mStatus.toString() : null);
        sb.append(sb6.toString());
        sb.append(", statusRes: " + this.mStatusRes);
        StringBuilder sb7 = new StringBuilder();
        sb7.append(", statusResAppPackageName: ");
        sb7.append(this.mStatusResAppPackageName != null ? this.mStatusResAppPackageName.toString() : null);
        sb.append(sb7.toString());
        sb.append("}");
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(this.mId, i);
        parcel.writeString(this.mLabel);
        parcel.writeParcelable(this.mPrinterId, i);
        parcel.writeString(this.mPrinterName);
        parcel.writeInt(this.mState);
        parcel.writeInt(this.mAppId);
        parcel.writeString(this.mTag);
        parcel.writeLong(this.mCreationTime);
        parcel.writeInt(this.mCopies);
        parcel.writeParcelableArray(this.mPageRanges, i);
        parcel.writeParcelable(this.mAttributes, i);
        parcel.writeParcelable(this.mDocumentInfo, 0);
        parcel.writeFloat(this.mProgress);
        parcel.writeCharSequence(this.mStatus);
        parcel.writeInt(this.mStatusRes);
        parcel.writeCharSequence(this.mStatusResAppPackageName);
        parcel.writeInt(this.mCanceling ? 1 : 0);
        parcel.writeBundle(this.mAdvancedOptions);
    }
}
