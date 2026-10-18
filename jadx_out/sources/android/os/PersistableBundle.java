package android.os;

import android.util.ArrayMap;
import android.util.proto.ProtoOutputStream;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/* JADX INFO: loaded from: classes2.dex */
public final class PersistableBundle extends BaseBundle implements Cloneable, Parcelable, XmlUtils.WriteMapCallback {
    public static final Parcelable.Creator<PersistableBundle> CREATOR;
    public static final PersistableBundle EMPTY = new PersistableBundle();
    private static final String TAG_PERSISTABLEMAP = "pbundle_as_map";

    static class MyReadMapCallback implements XmlUtils.ReadMapCallback {
        MyReadMapCallback() {
        }

        @Override // com.android.internal.util.XmlUtils.ReadMapCallback
        public Object readThisUnknownObjectXml(XmlPullParser xmlPullParser, String str) throws XmlPullParserException, IOException {
            if (PersistableBundle.TAG_PERSISTABLEMAP.equals(str)) {
                return PersistableBundle.restoreFromXml(xmlPullParser);
            }
            throw new XmlPullParserException("Unknown tag=" + str);
        }
    }

    static {
        EMPTY.mMap = ArrayMap.EMPTY;
        CREATOR = new Parcelable.Creator<PersistableBundle>() { // from class: android.os.PersistableBundle.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public PersistableBundle createFromParcel(Parcel parcel) {
                return parcel.readPersistableBundle();
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public PersistableBundle[] newArray(int i) {
                return new PersistableBundle[i];
            }
        };
    }

    public PersistableBundle() {
        this.mFlags = 1;
    }

    public PersistableBundle(int i) {
        super(i);
        this.mFlags = 1;
    }

    public PersistableBundle(Bundle bundle) {
        this(bundle.getMap());
    }

    PersistableBundle(Parcel parcel, int i) {
        super(parcel, i);
        this.mFlags = 1;
    }

    public PersistableBundle(PersistableBundle persistableBundle) {
        super(persistableBundle);
        this.mFlags = persistableBundle.mFlags;
    }

    private PersistableBundle(ArrayMap<String, Object> arrayMap) {
        this.mFlags = 1;
        putAll(arrayMap);
        int size = this.mMap.size();
        for (int i = 0; i < size; i++) {
            Object objValueAt = this.mMap.valueAt(i);
            if (objValueAt instanceof ArrayMap) {
                this.mMap.setValueAt(i, new PersistableBundle((ArrayMap<String, Object>) objValueAt));
            } else if (objValueAt instanceof Bundle) {
                this.mMap.setValueAt(i, new PersistableBundle((Bundle) objValueAt));
            } else if (!isValidType(objValueAt)) {
                throw new IllegalArgumentException("Bad value in PersistableBundle key=" + this.mMap.keyAt(i) + " value=" + objValueAt);
            }
        }
    }

    PersistableBundle(boolean z) {
        super(z);
    }

    public static PersistableBundle forPair(String str, String str2) {
        PersistableBundle persistableBundle = new PersistableBundle(1);
        persistableBundle.putString(str, str2);
        return persistableBundle;
    }

    public static boolean isValidType(Object obj) {
        return (obj instanceof Integer) || (obj instanceof Long) || (obj instanceof Double) || (obj instanceof String) || (obj instanceof int[]) || (obj instanceof long[]) || (obj instanceof double[]) || (obj instanceof String[]) || (obj instanceof PersistableBundle) || obj == null || (obj instanceof Boolean) || (obj instanceof boolean[]);
    }

    public static PersistableBundle restoreFromXml(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        int next;
        int depth = xmlPullParser.getDepth();
        String name = xmlPullParser.getName();
        String[] strArr = new String[1];
        do {
            next = xmlPullParser.next();
            if (next == 1 || (next == 3 && xmlPullParser.getDepth() >= depth)) {
                return EMPTY;
            }
        } while (next != 2);
        return new PersistableBundle(XmlUtils.readThisArrayMapXml(xmlPullParser, name, strArr, new MyReadMapCallback()));
    }

    public Object clone() {
        return new PersistableBundle(this);
    }

    public PersistableBundle deepCopy() {
        PersistableBundle persistableBundle = new PersistableBundle(false);
        persistableBundle.copyInternal(this, true);
        return persistableBundle;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public PersistableBundle getPersistableBundle(String str) {
        unparcel();
        Object obj = this.mMap.get(str);
        if (obj == null) {
            return null;
        }
        try {
            return (PersistableBundle) obj;
        } catch (ClassCastException e) {
            typeWarning(str, obj, "Bundle", e);
            return null;
        }
    }

    public void putPersistableBundle(String str, PersistableBundle persistableBundle) {
        unparcel();
        this.mMap.put(str, persistableBundle);
    }

    public void saveToXml(XmlSerializer xmlSerializer) throws XmlPullParserException, IOException {
        unparcel();
        XmlUtils.writeMapXml(this.mMap, xmlSerializer, this);
    }

    public synchronized String toShortString() {
        if (this.mParcelledData == null) {
            return this.mMap.toString();
        }
        if (isEmptyParcel()) {
            return "EMPTY_PARCEL";
        }
        return "mParcelledData.dataSize=" + this.mParcelledData.dataSize();
    }

    public synchronized String toString() {
        if (this.mParcelledData == null) {
            return "PersistableBundle[" + this.mMap.toString() + "]";
        }
        if (isEmptyParcel()) {
            return "PersistableBundle[EMPTY_PARCEL]";
        }
        return "PersistableBundle[mParcelledData.dataSize=" + this.mParcelledData.dataSize() + "]";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        boolean zPushAllowFds = parcel.pushAllowFds(false);
        try {
            writeToParcelInner(parcel, i);
        } finally {
            parcel.restoreAllowFds(zPushAllowFds);
        }
    }

    public void writeToProto(ProtoOutputStream protoOutputStream, long j) {
        long jStart = protoOutputStream.start(j);
        if (this.mParcelledData == null) {
            protoOutputStream.write(1138166333442L, this.mMap.toString());
        } else if (isEmptyParcel()) {
            protoOutputStream.write(1120986464257L, 0);
        } else {
            protoOutputStream.write(1120986464257L, this.mParcelledData.dataSize());
        }
        protoOutputStream.end(jStart);
    }

    @Override // com.android.internal.util.XmlUtils.WriteMapCallback
    public void writeUnknownObject(Object obj, String str, XmlSerializer xmlSerializer) throws XmlPullParserException, IOException {
        if (!(obj instanceof PersistableBundle)) {
            throw new XmlPullParserException("Unknown Object o=" + obj);
        }
        xmlSerializer.startTag(null, TAG_PERSISTABLEMAP);
        xmlSerializer.attribute(null, "name", str);
        ((PersistableBundle) obj).saveToXml(xmlSerializer);
        xmlSerializer.endTag(null, TAG_PERSISTABLEMAP);
    }
}
