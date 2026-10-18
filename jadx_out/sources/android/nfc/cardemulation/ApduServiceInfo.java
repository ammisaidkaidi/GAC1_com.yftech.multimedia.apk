package android.nfc.cardemulation;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import com.android.internal.R;
import com.android.internal.telephony.PhoneConstants;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public final class ApduServiceInfo implements Parcelable {
    public static final Parcelable.Creator<ApduServiceInfo> CREATOR = new Parcelable.Creator<ApduServiceInfo>() { // from class: android.nfc.cardemulation.ApduServiceInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ApduServiceInfo createFromParcel(Parcel parcel) {
            ResolveInfo resolveInfoCreateFromParcel = ResolveInfo.CREATOR.createFromParcel(parcel);
            String string = parcel.readString();
            boolean z = parcel.readInt() != 0;
            ArrayList arrayList = new ArrayList();
            if (parcel.readInt() > 0) {
                parcel.readTypedList(arrayList, AidGroup.CREATOR);
            }
            ArrayList arrayList2 = new ArrayList();
            if (parcel.readInt() > 0) {
                parcel.readTypedList(arrayList2, AidGroup.CREATOR);
            }
            return new ApduServiceInfo(resolveInfoCreateFromParcel, z, string, arrayList, arrayList2, parcel.readInt() != 0, parcel.readInt(), parcel.readInt(), parcel.readString());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ApduServiceInfo[] newArray(int i) {
            return new ApduServiceInfo[i];
        }
    };
    static final String TAG = "ApduServiceInfo";
    final int mBannerResourceId;
    final String mDescription;
    final HashMap<String, AidGroup> mDynamicAidGroups;
    final boolean mOnHost;
    final boolean mRequiresDeviceUnlock;
    final ResolveInfo mService;
    final String mSettingsActivityName;
    final HashMap<String, AidGroup> mStaticAidGroups;
    final int mUid;

    /* JADX WARN: Code duplicated, block: B:116:0x0290  */
    /* JADX WARN: Not initialized variable reg: 6, insn: 0x0023: MOVE (r5 I:??[OBJECT, ARRAY]) = (r6 I:??[OBJECT, ARRAY]), block:B:11:0x0023 */
    public ApduServiceInfo(PackageManager packageManager, ResolveInfo resolveInfo, boolean z) throws Throwable {
        XmlResourceParser xmlResourceParser;
        XmlResourceParser xmlResourceParser2;
        XmlResourceParser xmlResourceParser3;
        XmlResourceParser xmlResourceParserLoadXmlMetaData;
        ServiceInfo serviceInfo = resolveInfo.serviceInfo;
        try {
            try {
                if (z) {
                    xmlResourceParserLoadXmlMetaData = serviceInfo.loadXmlMetaData(packageManager, HostApduService.SERVICE_META_DATA);
                    if (xmlResourceParserLoadXmlMetaData == null) {
                        throw new XmlPullParserException("No android.nfc.cardemulation.host_apdu_service meta-data");
                    }
                } else {
                    xmlResourceParserLoadXmlMetaData = serviceInfo.loadXmlMetaData(packageManager, OffHostApduService.SERVICE_META_DATA);
                    if (xmlResourceParserLoadXmlMetaData == null) {
                        throw new XmlPullParserException("No android.nfc.cardemulation.off_host_apdu_service meta-data");
                    }
                }
                for (int eventType = xmlResourceParserLoadXmlMetaData.getEventType(); eventType != 2 && eventType != 1; eventType = xmlResourceParserLoadXmlMetaData.next()) {
                }
                String name = xmlResourceParserLoadXmlMetaData.getName();
                if (z && !"host-apdu-service".equals(name)) {
                    throw new XmlPullParserException("Meta-data does not start with <host-apdu-service> tag");
                }
                if (!z && !"offhost-apdu-service".equals(name)) {
                    throw new XmlPullParserException("Meta-data does not start with <offhost-apdu-service> tag");
                }
                Resources resourcesForApplication = packageManager.getResourcesForApplication(serviceInfo.applicationInfo);
                AttributeSet attributeSetAsAttributeSet = Xml.asAttributeSet(xmlResourceParserLoadXmlMetaData);
                if (z) {
                    TypedArray typedArrayObtainAttributes = resourcesForApplication.obtainAttributes(attributeSetAsAttributeSet, R.styleable.HostApduService);
                    this.mService = resolveInfo;
                    this.mDescription = typedArrayObtainAttributes.getString(0);
                    this.mRequiresDeviceUnlock = typedArrayObtainAttributes.getBoolean(2, false);
                    this.mBannerResourceId = typedArrayObtainAttributes.getResourceId(3, -1);
                    this.mSettingsActivityName = typedArrayObtainAttributes.getString(1);
                    typedArrayObtainAttributes.recycle();
                } else {
                    TypedArray typedArrayObtainAttributes2 = resourcesForApplication.obtainAttributes(attributeSetAsAttributeSet, R.styleable.OffHostApduService);
                    this.mService = resolveInfo;
                    this.mDescription = typedArrayObtainAttributes2.getString(0);
                    this.mRequiresDeviceUnlock = false;
                    this.mBannerResourceId = typedArrayObtainAttributes2.getResourceId(2, -1);
                    this.mSettingsActivityName = typedArrayObtainAttributes2.getString(1);
                    typedArrayObtainAttributes2.recycle();
                }
                this.mStaticAidGroups = new HashMap<>();
                this.mDynamicAidGroups = new HashMap<>();
                this.mOnHost = z;
                int depth = xmlResourceParserLoadXmlMetaData.getDepth();
                loop1: while (true) {
                    AidGroup aidGroup = null;
                    while (true) {
                        int next = xmlResourceParserLoadXmlMetaData.next();
                        if ((next == 3 && xmlResourceParserLoadXmlMetaData.getDepth() <= depth) || next == 1) {
                            break loop1;
                        }
                        String name2 = xmlResourceParserLoadXmlMetaData.getName();
                        if (next == 2 && "aid-group".equals(name2) && aidGroup == null) {
                            TypedArray typedArrayObtainAttributes3 = resourcesForApplication.obtainAttributes(attributeSetAsAttributeSet, R.styleable.AidGroup);
                            String string = typedArrayObtainAttributes3.getString(1);
                            String string2 = typedArrayObtainAttributes3.getString(0);
                            string = CardEmulation.CATEGORY_PAYMENT.equals(string) ? string : "other";
                            AidGroup aidGroup2 = this.mStaticAidGroups.get(string);
                            if (aidGroup2 == null) {
                                aidGroup2 = new AidGroup(string, string2);
                            } else if (!"other".equals(string)) {
                                Log.e(TAG, "Not allowing multiple aid-groups in the " + string + " category");
                                aidGroup2 = null;
                            }
                            typedArrayObtainAttributes3.recycle();
                            aidGroup = aidGroup2;
                        } else if (next != 3 || !"aid-group".equals(name2) || aidGroup == null) {
                            if (next == 2 && "aid-filter".equals(name2) && aidGroup != null) {
                                TypedArray typedArrayObtainAttributes4 = resourcesForApplication.obtainAttributes(attributeSetAsAttributeSet, R.styleable.AidFilter);
                                String upperCase = typedArrayObtainAttributes4.getString(0).toUpperCase();
                                if (!CardEmulation.isValidAid(upperCase) || aidGroup.aids.contains(upperCase)) {
                                    Log.e(TAG, "Ignoring invalid or duplicate aid: " + upperCase);
                                } else {
                                    aidGroup.aids.add(upperCase);
                                }
                                typedArrayObtainAttributes4.recycle();
                            } else if (next == 2 && "aid-prefix-filter".equals(name2) && aidGroup != null) {
                                TypedArray typedArrayObtainAttributes5 = resourcesForApplication.obtainAttributes(attributeSetAsAttributeSet, R.styleable.AidFilter);
                                String strConcat = typedArrayObtainAttributes5.getString(0).toUpperCase().concat(PhoneConstants.APN_TYPE_ALL);
                                if (!CardEmulation.isValidAid(strConcat) || aidGroup.aids.contains(strConcat)) {
                                    Log.e(TAG, "Ignoring invalid or duplicate aid: " + strConcat);
                                } else {
                                    aidGroup.aids.add(strConcat);
                                }
                                typedArrayObtainAttributes5.recycle();
                            } else if (next == 2 && name2.equals("aid-suffix-filter") && aidGroup != null) {
                                TypedArray typedArrayObtainAttributes6 = resourcesForApplication.obtainAttributes(attributeSetAsAttributeSet, R.styleable.AidFilter);
                                String strConcat2 = typedArrayObtainAttributes6.getString(0).toUpperCase().concat("#");
                                if (!CardEmulation.isValidAid(strConcat2) || aidGroup.aids.contains(strConcat2)) {
                                    Log.e(TAG, "Ignoring invalid or duplicate aid: " + strConcat2);
                                } else {
                                    aidGroup.aids.add(strConcat2);
                                }
                                typedArrayObtainAttributes6.recycle();
                            }
                        }
                    }
                    if (aidGroup.aids.size() <= 0) {
                        Log.e(TAG, "Not adding <aid-group> with empty or invalid AIDs");
                    } else if (!this.mStaticAidGroups.containsKey(aidGroup.category)) {
                        this.mStaticAidGroups.put(aidGroup.category, aidGroup);
                    }
                }
                if (xmlResourceParserLoadXmlMetaData != null) {
                    xmlResourceParserLoadXmlMetaData.close();
                }
                this.mUid = serviceInfo.applicationInfo.uid;
            } catch (PackageManager.NameNotFoundException unused) {
                xmlResourceParser2 = xmlResourceParser3;
                try {
                    throw new XmlPullParserException("Unable to create context for: " + serviceInfo.packageName);
                } catch (Throwable th) {
                    th = th;
                    xmlResourceParser = xmlResourceParser2;
                    if (xmlResourceParser != null) {
                        xmlResourceParser.close();
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                if (xmlResourceParser != null) {
                    xmlResourceParser.close();
                }
                throw th;
            }
        } catch (PackageManager.NameNotFoundException unused2) {
            xmlResourceParser2 = null;
            throw new XmlPullParserException("Unable to create context for: " + serviceInfo.packageName);
        } catch (Throwable th3) {
            th = th3;
            xmlResourceParser = null;
            if (xmlResourceParser != null) {
                xmlResourceParser.close();
            }
            throw th;
        }
    }

    public ApduServiceInfo(ResolveInfo resolveInfo, boolean z, String str, ArrayList<AidGroup> arrayList, ArrayList<AidGroup> arrayList2, boolean z2, int i, int i2, String str2) {
        this.mService = resolveInfo;
        this.mDescription = str;
        this.mStaticAidGroups = new HashMap<>();
        this.mDynamicAidGroups = new HashMap<>();
        this.mOnHost = z;
        this.mRequiresDeviceUnlock = z2;
        for (AidGroup aidGroup : arrayList) {
            this.mStaticAidGroups.put(aidGroup.category, aidGroup);
        }
        for (AidGroup aidGroup2 : arrayList2) {
            this.mDynamicAidGroups.put(aidGroup2.category, aidGroup2);
        }
        this.mBannerResourceId = i;
        this.mUid = i2;
        this.mSettingsActivityName = str2;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("    " + getComponent() + " (Description: " + getDescription() + ")");
        printWriter.println("    Static AID groups:");
        for (AidGroup aidGroup : this.mStaticAidGroups.values()) {
            printWriter.println("        Category: " + aidGroup.category);
            Iterator<String> it = aidGroup.aids.iterator();
            while (it.hasNext()) {
                printWriter.println("            AID: " + it.next());
            }
        }
        printWriter.println("    Dynamic AID groups:");
        for (AidGroup aidGroup2 : this.mDynamicAidGroups.values()) {
            printWriter.println("        Category: " + aidGroup2.category);
            Iterator<String> it2 = aidGroup2.aids.iterator();
            while (it2.hasNext()) {
                printWriter.println("            AID: " + it2.next());
            }
        }
        printWriter.println("    Settings Activity: " + this.mSettingsActivityName);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ApduServiceInfo) {
            return ((ApduServiceInfo) obj).getComponent().equals(getComponent());
        }
        return false;
    }

    public ArrayList<AidGroup> getAidGroups() {
        ArrayList<AidGroup> arrayList = new ArrayList<>();
        Iterator<Map.Entry<String, AidGroup>> it = this.mDynamicAidGroups.entrySet().iterator();
        while (it.hasNext()) {
            arrayList.add(it.next().getValue());
        }
        for (Map.Entry<String, AidGroup> entry : this.mStaticAidGroups.entrySet()) {
            if (!this.mDynamicAidGroups.containsKey(entry.getKey())) {
                arrayList.add(entry.getValue());
            }
        }
        return arrayList;
    }

    public List<String> getAids() {
        ArrayList arrayList = new ArrayList();
        Iterator<AidGroup> it = getAidGroups().iterator();
        while (it.hasNext()) {
            arrayList.addAll(it.next().aids);
        }
        return arrayList;
    }

    public String getCategoryForAid(String str) {
        for (AidGroup aidGroup : getAidGroups()) {
            if (aidGroup.aids.contains(str.toUpperCase())) {
                return aidGroup.category;
            }
        }
        return null;
    }

    public ComponentName getComponent() {
        return new ComponentName(this.mService.serviceInfo.packageName, this.mService.serviceInfo.name);
    }

    public String getDescription() {
        return this.mDescription;
    }

    public AidGroup getDynamicAidGroupForCategory(String str) {
        return this.mDynamicAidGroups.get(str);
    }

    public List<String> getPrefixAids() {
        ArrayList arrayList = new ArrayList();
        Iterator<AidGroup> it = getAidGroups().iterator();
        while (it.hasNext()) {
            for (String str : it.next().aids) {
                if (str.endsWith(PhoneConstants.APN_TYPE_ALL)) {
                    arrayList.add(str);
                }
            }
        }
        return arrayList;
    }

    public String getSettingsActivityName() {
        return this.mSettingsActivityName;
    }

    public List<String> getSubsetAids() {
        ArrayList arrayList = new ArrayList();
        Iterator<AidGroup> it = getAidGroups().iterator();
        while (it.hasNext()) {
            for (String str : it.next().aids) {
                if (str.endsWith("#")) {
                    arrayList.add(str);
                }
            }
        }
        return arrayList;
    }

    public int getUid() {
        return this.mUid;
    }

    public boolean hasCategory(String str) {
        return this.mStaticAidGroups.containsKey(str) || this.mDynamicAidGroups.containsKey(str);
    }

    public int hashCode() {
        return getComponent().hashCode();
    }

    public boolean isOnHost() {
        return this.mOnHost;
    }

    public CharSequence loadAppLabel(PackageManager packageManager) {
        try {
            return packageManager.getApplicationLabel(packageManager.getApplicationInfo(this.mService.resolvePackageName, 128));
        } catch (PackageManager.NameNotFoundException unused) {
            return null;
        }
    }

    public Drawable loadBanner(PackageManager packageManager) {
        try {
            return packageManager.getResourcesForApplication(this.mService.serviceInfo.packageName).getDrawable(this.mBannerResourceId);
        } catch (PackageManager.NameNotFoundException unused) {
            Log.e(TAG, "Could not load banner.");
            return null;
        } catch (Resources.NotFoundException unused2) {
            Log.e(TAG, "Could not load banner.");
            return null;
        }
    }

    public Drawable loadIcon(PackageManager packageManager) {
        return this.mService.loadIcon(packageManager);
    }

    public CharSequence loadLabel(PackageManager packageManager) {
        return this.mService.loadLabel(packageManager);
    }

    public boolean removeDynamicAidGroupForCategory(String str) {
        return this.mDynamicAidGroups.remove(str) != null;
    }

    public boolean requiresUnlock() {
        return this.mRequiresDeviceUnlock;
    }

    public void setOrReplaceDynamicAidGroup(AidGroup aidGroup) {
        this.mDynamicAidGroups.put(aidGroup.getCategory(), aidGroup);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ApduService: ");
        sb.append(getComponent());
        sb.append(", description: " + this.mDescription);
        sb.append(", Static AID Groups: ");
        Iterator<AidGroup> it = this.mStaticAidGroups.values().iterator();
        while (it.hasNext()) {
            sb.append(it.next().toString());
        }
        sb.append(", Dynamic AID Groups: ");
        Iterator<AidGroup> it2 = this.mDynamicAidGroups.values().iterator();
        while (it2.hasNext()) {
            sb.append(it2.next().toString());
        }
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        this.mService.writeToParcel(parcel, i);
        parcel.writeString(this.mDescription);
        parcel.writeInt(this.mOnHost ? 1 : 0);
        parcel.writeInt(this.mStaticAidGroups.size());
        if (this.mStaticAidGroups.size() > 0) {
            parcel.writeTypedList(new ArrayList(this.mStaticAidGroups.values()));
        }
        parcel.writeInt(this.mDynamicAidGroups.size());
        if (this.mDynamicAidGroups.size() > 0) {
            parcel.writeTypedList(new ArrayList(this.mDynamicAidGroups.values()));
        }
        parcel.writeInt(this.mRequiresDeviceUnlock ? 1 : 0);
        parcel.writeInt(this.mBannerResourceId);
        parcel.writeInt(this.mUid);
        parcel.writeString(this.mSettingsActivityName);
    }
}
