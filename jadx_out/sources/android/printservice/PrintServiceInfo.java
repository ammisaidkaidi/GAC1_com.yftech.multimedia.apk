package android.printservice;

import android.annotation.SystemApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.Xml;
import com.android.internal.R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes2.dex */
@SystemApi
public final class PrintServiceInfo implements Parcelable {
    public static final Parcelable.Creator<PrintServiceInfo> CREATOR = new Parcelable.Creator<PrintServiceInfo>() { // from class: android.printservice.PrintServiceInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PrintServiceInfo createFromParcel(Parcel parcel) {
            return new PrintServiceInfo(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PrintServiceInfo[] newArray(int i) {
            return new PrintServiceInfo[i];
        }
    };
    private static final String LOG_TAG = "PrintServiceInfo";
    private static final String TAG_PRINT_SERVICE = "print-service";
    private final String mAddPrintersActivityName;
    private final String mAdvancedPrintOptionsActivityName;
    private final String mId;
    private boolean mIsEnabled;
    private final ResolveInfo mResolveInfo;
    private final String mSettingsActivityName;

    public PrintServiceInfo(ResolveInfo resolveInfo, String str, String str2, String str3) {
        this.mId = new ComponentName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name).flattenToString();
        this.mResolveInfo = resolveInfo;
        this.mSettingsActivityName = str;
        this.mAddPrintersActivityName = str2;
        this.mAdvancedPrintOptionsActivityName = str3;
    }

    public PrintServiceInfo(Parcel parcel) {
        this.mId = parcel.readString();
        this.mIsEnabled = parcel.readByte() != 0;
        this.mResolveInfo = (ResolveInfo) parcel.readParcelable(null);
        this.mSettingsActivityName = parcel.readString();
        this.mAddPrintersActivityName = parcel.readString();
        this.mAdvancedPrintOptionsActivityName = parcel.readString();
    }

    /* JADX WARN: Code duplicated, block: B:20:0x0058 A[PHI: r1 r3 r4
      0x0058: PHI (r1v10 java.lang.String) = (r1v7 java.lang.String), (r1v8 java.lang.String), (r1v9 java.lang.String), (r1v11 java.lang.String) binds: [B:19:0x0056, B:48:0x00b3, B:52:0x00cd, B:43:0x0097] A[DONT_GENERATE, DONT_INLINE]
      0x0058: PHI (r3v20 java.lang.String) = (r3v16 java.lang.String), (r3v18 java.lang.String), (r3v19 java.lang.String), (r3v21 java.lang.String) binds: [B:19:0x0056, B:48:0x00b3, B:52:0x00cd, B:43:0x0097] A[DONT_GENERATE, DONT_INLINE]
      0x0058: PHI (r4v18 java.lang.String) = (r4v14 java.lang.String), (r4v16 java.lang.String), (r4v17 java.lang.String), (r4v19 java.lang.String) binds: [B:19:0x0056, B:48:0x00b3, B:52:0x00cd, B:43:0x0097] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:43:0x0097  */
    /* JADX WARN: Code duplicated, block: B:48:0x00b3  */
    public static PrintServiceInfo create(Context context, ResolveInfo resolveInfo) {
        String string;
        String string2;
        PackageManager packageManager = context.getPackageManager();
        XmlResourceParser xmlResourceParserLoadXmlMetaData = resolveInfo.serviceInfo.loadXmlMetaData(packageManager, PrintService.SERVICE_META_DATA);
        String str = null;
        if (xmlResourceParserLoadXmlMetaData != null) {
            for (int next = 0; next != 1 && next != 2; next = xmlResourceParserLoadXmlMetaData.next()) {
                try {
                    try {
                    } catch (Throwable th) {
                        if (xmlResourceParserLoadXmlMetaData != null) {
                            xmlResourceParserLoadXmlMetaData.close();
                        }
                        throw th;
                    }
                } catch (PackageManager.NameNotFoundException unused) {
                    string = null;
                    string2 = null;
                    Log.e(LOG_TAG, "Unable to load resources for: " + resolveInfo.serviceInfo.packageName);
                    if (xmlResourceParserLoadXmlMetaData != null) {
                        xmlResourceParserLoadXmlMetaData.close();
                    }
                    return new PrintServiceInfo(resolveInfo, str, string, string2);
                } catch (IOException e) {
                    e = e;
                    string = null;
                    string2 = null;
                    Log.w(LOG_TAG, "Error reading meta-data:" + e);
                    if (xmlResourceParserLoadXmlMetaData != null) {
                        xmlResourceParserLoadXmlMetaData.close();
                    }
                    return new PrintServiceInfo(resolveInfo, str, string, string2);
                } catch (XmlPullParserException e2) {
                    e = e2;
                    string = null;
                    string2 = null;
                    Log.w(LOG_TAG, "Error reading meta-data:" + e);
                    if (xmlResourceParserLoadXmlMetaData != null) {
                        xmlResourceParserLoadXmlMetaData.close();
                    }
                    return new PrintServiceInfo(resolveInfo, str, string, string2);
                }
            }
            if (TAG_PRINT_SERVICE.equals(xmlResourceParserLoadXmlMetaData.getName())) {
                TypedArray typedArrayObtainAttributes = packageManager.getResourcesForApplication(resolveInfo.serviceInfo.applicationInfo).obtainAttributes(Xml.asAttributeSet(xmlResourceParserLoadXmlMetaData), R.styleable.PrintService);
                String string3 = typedArrayObtainAttributes.getString(0);
                try {
                    string = typedArrayObtainAttributes.getString(1);
                    try {
                        string2 = typedArrayObtainAttributes.getString(3);
                        try {
                            typedArrayObtainAttributes.recycle();
                            str = string3;
                        } catch (PackageManager.NameNotFoundException unused2) {
                            str = string3;
                            Log.e(LOG_TAG, "Unable to load resources for: " + resolveInfo.serviceInfo.packageName);
                            if (xmlResourceParserLoadXmlMetaData != null) {
                            }
                            return new PrintServiceInfo(resolveInfo, str, string, string2);
                        } catch (IOException e3) {
                            e = e3;
                            str = string3;
                            Log.w(LOG_TAG, "Error reading meta-data:" + e);
                            if (xmlResourceParserLoadXmlMetaData != null) {
                            }
                            return new PrintServiceInfo(resolveInfo, str, string, string2);
                        } catch (XmlPullParserException e4) {
                            e = e4;
                            str = string3;
                            Log.w(LOG_TAG, "Error reading meta-data:" + e);
                            if (xmlResourceParserLoadXmlMetaData != null) {
                            }
                            return new PrintServiceInfo(resolveInfo, str, string, string2);
                        }
                    } catch (PackageManager.NameNotFoundException unused3) {
                        string2 = null;
                    } catch (IOException e5) {
                        e = e5;
                        string2 = null;
                    } catch (XmlPullParserException e6) {
                        e = e6;
                        string2 = null;
                    }
                } catch (PackageManager.NameNotFoundException unused4) {
                    string = null;
                    string2 = null;
                } catch (IOException e7) {
                    e = e7;
                    string = null;
                    string2 = null;
                } catch (XmlPullParserException e8) {
                    e = e8;
                    string = null;
                    string2 = null;
                }
            } else {
                Log.e(LOG_TAG, "Ignoring meta-data that does not start with print-service tag");
                string = null;
                string2 = null;
            }
            if (xmlResourceParserLoadXmlMetaData != null) {
                xmlResourceParserLoadXmlMetaData.close();
            }
        } else {
            string = null;
            string2 = null;
        }
        return new PrintServiceInfo(resolveInfo, str, string, string2);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PrintServiceInfo printServiceInfo = (PrintServiceInfo) obj;
        if (this.mId == null) {
            if (printServiceInfo.mId != null) {
                return false;
            }
        } else if (!this.mId.equals(printServiceInfo.mId)) {
            return false;
        }
        return true;
    }

    public String getAddPrintersActivityName() {
        return this.mAddPrintersActivityName;
    }

    public String getAdvancedOptionsActivityName() {
        return this.mAdvancedPrintOptionsActivityName;
    }

    public ComponentName getComponentName() {
        return new ComponentName(this.mResolveInfo.serviceInfo.packageName, this.mResolveInfo.serviceInfo.name);
    }

    public String getId() {
        return this.mId;
    }

    public ResolveInfo getResolveInfo() {
        return this.mResolveInfo;
    }

    public String getSettingsActivityName() {
        return this.mSettingsActivityName;
    }

    public int hashCode() {
        return (this.mId == null ? 0 : this.mId.hashCode()) + 31;
    }

    public boolean isEnabled() {
        return this.mIsEnabled;
    }

    public void setIsEnabled(boolean z) {
        this.mIsEnabled = z;
    }

    public String toString() {
        return "PrintServiceInfo{id=" + this.mId + "isEnabled=" + this.mIsEnabled + ", resolveInfo=" + this.mResolveInfo + ", settingsActivityName=" + this.mSettingsActivityName + ", addPrintersActivityName=" + this.mAddPrintersActivityName + ", advancedPrintOptionsActivityName=" + this.mAdvancedPrintOptionsActivityName + "}";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mId);
        parcel.writeByte(this.mIsEnabled ? (byte) 1 : (byte) 0);
        parcel.writeParcelable(this.mResolveInfo, 0);
        parcel.writeString(this.mSettingsActivityName);
        parcel.writeString(this.mAddPrintersActivityName);
        parcel.writeString(this.mAdvancedPrintOptionsActivityName);
    }
}
