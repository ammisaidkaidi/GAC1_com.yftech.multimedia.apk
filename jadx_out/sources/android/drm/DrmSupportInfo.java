package android.drm;

import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class DrmSupportInfo {
    private final ArrayList<String> mFileSuffixList = new ArrayList<>();
    private final ArrayList<String> mMimeTypeList = new ArrayList<>();
    private String mDescription = "";

    public void addFileSuffix(String str) {
        if (str == "") {
            throw new IllegalArgumentException("fileSuffix is an empty string");
        }
        this.mFileSuffixList.add(str);
    }

    public void addMimeType(String str) {
        if (str == null) {
            throw new IllegalArgumentException("mimeType is null");
        }
        if (str == "") {
            throw new IllegalArgumentException("mimeType is an empty string");
        }
        this.mMimeTypeList.add(str);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof DrmSupportInfo)) {
            return false;
        }
        DrmSupportInfo drmSupportInfo = (DrmSupportInfo) obj;
        return this.mFileSuffixList.equals(drmSupportInfo.mFileSuffixList) && this.mMimeTypeList.equals(drmSupportInfo.mMimeTypeList) && this.mDescription.equals(drmSupportInfo.mDescription);
    }

    public String getDescriprition() {
        return this.mDescription;
    }

    public String getDescription() {
        return this.mDescription;
    }

    public Iterator<String> getFileSuffixIterator() {
        return this.mFileSuffixList.iterator();
    }

    public Iterator<String> getMimeTypeIterator() {
        return this.mMimeTypeList.iterator();
    }

    public int hashCode() {
        return this.mFileSuffixList.hashCode() + this.mMimeTypeList.hashCode() + this.mDescription.hashCode();
    }

    boolean isSupportedFileSuffix(String str) {
        return this.mFileSuffixList.contains(str);
    }

    boolean isSupportedMimeType(String str) {
        if (str != null && !str.equals("")) {
            for (int i = 0; i < this.mMimeTypeList.size(); i++) {
                if (this.mMimeTypeList.get(i).startsWith(str)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setDescription(String str) {
        if (str == null) {
            throw new IllegalArgumentException("description is null");
        }
        if (str == "") {
            throw new IllegalArgumentException("description is an empty string");
        }
        this.mDescription = str;
    }
}
