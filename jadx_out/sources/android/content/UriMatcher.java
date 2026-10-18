package android.content;

import android.net.Uri;
import com.android.internal.telephony.PhoneConstants;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class UriMatcher {
    private static final int EXACT = 0;
    public static final int NO_MATCH = -1;
    private static final int NUMBER = 1;
    private static final int TEXT = 2;
    private ArrayList<UriMatcher> mChildren;
    private int mCode;
    private String mText;
    private int mWhich;

    private UriMatcher() {
        this.mCode = -1;
        this.mWhich = -1;
        this.mChildren = new ArrayList<>();
        this.mText = null;
    }

    public UriMatcher(int i) {
        this.mCode = i;
        this.mWhich = -1;
        this.mChildren = new ArrayList<>();
        this.mText = null;
    }

    public void addURI(String str, String str2, int i) {
        if (i < 0) {
            throw new IllegalArgumentException("code " + i + " is invalid: it must be positive");
        }
        String[] strArrSplit = null;
        if (str2 != null) {
            if (str2.length() > 1 && str2.charAt(0) == '/') {
                str2 = str2.substring(1);
            }
            strArrSplit = str2.split("/");
        }
        int length = strArrSplit != null ? strArrSplit.length : 0;
        int i2 = -1;
        while (i2 < length) {
            String str3 = i2 < 0 ? str : strArrSplit[i2];
            ArrayList<UriMatcher> arrayList = this.mChildren;
            int size = arrayList.size();
            int i3 = 0;
            while (i3 < size) {
                UriMatcher uriMatcher = arrayList.get(i3);
                if (str3.equals(uriMatcher.mText)) {
                    this = uriMatcher;
                    break;
                }
                i3++;
            }
            if (i3 == size) {
                UriMatcher uriMatcher2 = new UriMatcher();
                if (str3.equals("#")) {
                    uriMatcher2.mWhich = 1;
                } else if (str3.equals(PhoneConstants.APN_TYPE_ALL)) {
                    uriMatcher2.mWhich = 2;
                } else {
                    uriMatcher2.mWhich = 0;
                }
                uriMatcher2.mText = str3;
                this.mChildren.add(uriMatcher2);
                this = uriMatcher2;
            }
            i2++;
        }
        this.mCode = i;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:31:0x005f  */
    /* JADX WARN: Code duplicated, block: B:38:0x006a A[LOOP:0: B:9:0x0016->B:38:0x006a, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:43:0x0069 A[SYNTHETIC] */
    public int match(Uri uri) {
        List<String> pathSegments = uri.getPathSegments();
        int size = pathSegments.size();
        if (size == 0 && uri.getAuthority() == null) {
            return this.mCode;
        }
        UriMatcher uriMatcher = this;
        int i = -1;
        while (i < size) {
            String authority = i < 0 ? uri.getAuthority() : pathSegments.get(i);
            ArrayList<UriMatcher> arrayList = uriMatcher.mChildren;
            if (arrayList == null) {
                return uriMatcher.mCode;
            }
            int size2 = arrayList.size();
            UriMatcher uriMatcher2 = null;
            for (int i2 = 0; i2 < size2; i2++) {
                UriMatcher uriMatcher3 = arrayList.get(i2);
                switch (uriMatcher3.mWhich) {
                    case 0:
                        if (uriMatcher3.mText.equals(authority)) {
                            uriMatcher2 = uriMatcher3;
                        }
                        break;
                    case 1:
                        int length = authority.length();
                        int i3 = 0;
                        while (true) {
                            if (i3 >= length) {
                                uriMatcher2 = uriMatcher3;
                            } else {
                                char cCharAt = authority.charAt(i3);
                                if (cCharAt >= '0' && cCharAt <= '9') {
                                    i3++;
                                }
                            }
                            break;
                        }
                        break;
                    case 2:
                        uriMatcher2 = uriMatcher3;
                        break;
                }
                if (uriMatcher2 != null) {
                    uriMatcher = uriMatcher2;
                    if (uriMatcher == null) {
                        return -1;
                    }
                    i++;
                }
            }
            uriMatcher = uriMatcher2;
            if (uriMatcher == null) {
                return -1;
            }
            i++;
        }
        return uriMatcher.mCode;
    }
}
