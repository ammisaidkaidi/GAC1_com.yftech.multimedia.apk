package android.os;

import android.util.proto.ProtoOutputStream;
import java.util.Arrays;

/* JADX INFO: loaded from: classes2.dex */
public class PatternMatcher implements Parcelable {
    private static final int MAX_PATTERN_STORAGE = 2048;
    private static final int NO_MATCH = -1;
    private static final int PARSED_MODIFIER_ONE_OR_MORE = -8;
    private static final int PARSED_MODIFIER_RANGE_START = -5;
    private static final int PARSED_MODIFIER_RANGE_STOP = -6;
    private static final int PARSED_MODIFIER_ZERO_OR_MORE = -7;
    private static final int PARSED_TOKEN_CHAR_ANY = -4;
    private static final int PARSED_TOKEN_CHAR_SET_INVERSE_START = -2;
    private static final int PARSED_TOKEN_CHAR_SET_START = -1;
    private static final int PARSED_TOKEN_CHAR_SET_STOP = -3;
    public static final int PATTERN_ADVANCED_GLOB = 3;
    public static final int PATTERN_LITERAL = 0;
    public static final int PATTERN_PREFIX = 1;
    public static final int PATTERN_SIMPLE_GLOB = 2;
    private static final String TAG = "PatternMatcher";
    private static final int TOKEN_TYPE_ANY = 1;
    private static final int TOKEN_TYPE_INVERSE_SET = 3;
    private static final int TOKEN_TYPE_LITERAL = 0;
    private static final int TOKEN_TYPE_SET = 2;
    private final int[] mParsedPattern;
    private final String mPattern;
    private final int mType;
    private static final int[] sParsedPatternScratch = new int[2048];
    public static final Parcelable.Creator<PatternMatcher> CREATOR = new Parcelable.Creator<PatternMatcher>() { // from class: android.os.PatternMatcher.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PatternMatcher createFromParcel(Parcel parcel) {
            return new PatternMatcher(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PatternMatcher[] newArray(int i) {
            return new PatternMatcher[i];
        }
    };

    public PatternMatcher(Parcel parcel) {
        this.mPattern = parcel.readString();
        this.mType = parcel.readInt();
        this.mParsedPattern = parcel.createIntArray();
    }

    public PatternMatcher(String str, int i) {
        this.mPattern = str;
        this.mType = i;
        if (this.mType == 3) {
            this.mParsedPattern = parseAndVerifyAdvancedPattern(str);
        } else {
            this.mParsedPattern = null;
        }
    }

    private static boolean isParsedModifier(int i) {
        return i == -8 || i == -7 || i == -6 || i == -5;
    }

    /* JADX WARN: Code duplicated, block: B:22:0x0045  */
    static boolean matchAdvancedPattern(int[] iArr, String str) {
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        int iMatchChars;
        int length = iArr.length;
        int length2 = str.length();
        int i8 = 0;
        int i9 = 0;
        int i10 = 0;
        int i11 = 0;
        while (i8 < length) {
            int i12 = iArr[i8];
            if (i12 != -4) {
                switch (i12) {
                    case -2:
                    case -1:
                        int i13 = i12 == -1 ? 2 : 3;
                        int i14 = i8 + 1;
                        do {
                            i8++;
                            if (i8 < length) {
                            }
                            int i15 = i8 - 1;
                            i = i8 + 1;
                            i2 = i14;
                            i3 = i15;
                            i4 = i13;
                            break;
                        } while (iArr[i8] != -3);
                        int i16 = i8 - 1;
                        i = i8 + 1;
                        i2 = i14;
                        i3 = i16;
                        i4 = i13;
                        break;
                    default:
                        i2 = i8;
                        i = i8 + 1;
                        i3 = i10;
                        i4 = 0;
                        break;
                }
            } else {
                i = i8 + 1;
                i2 = i9;
                i3 = i10;
                i4 = 1;
            }
            if (i < length) {
                int i17 = iArr[i];
                if (i17 != -5) {
                    switch (i17) {
                        case -8:
                            i5 = i + 1;
                            i7 = Integer.MAX_VALUE;
                            i6 = 1;
                            break;
                        case -7:
                            i5 = i + 1;
                            i7 = Integer.MAX_VALUE;
                            i6 = 0;
                            break;
                        default:
                            i5 = i;
                            i6 = 1;
                            i7 = 1;
                            break;
                    }
                } else {
                    int i18 = i + 1;
                    int i19 = iArr[i18];
                    int i20 = i18 + 1;
                    i5 = i20 + 2;
                    i6 = i19;
                    i7 = iArr[i20];
                }
            } else {
                i5 = i;
                i6 = 1;
                i7 = 1;
            }
            if (i6 > i7 || (iMatchChars = matchChars(str, i11, length2, i4, i6, i7, iArr, i2, i3)) == -1) {
                return false;
            }
            i11 += iMatchChars;
            i9 = i2;
            i10 = i3;
            i8 = i5;
        }
        return i8 >= length && i11 >= length2;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private static boolean matchChar(String str, int i, int i2, int i3, int[] iArr, int i4, int i5) {
        if (i >= i2) {
            return false;
        }
        switch (i3) {
            case 0:
                return str.charAt(i) == iArr[i4];
            case 1:
                return true;
            case 2:
                while (i4 < i5) {
                    char cCharAt = str.charAt(i);
                    if (cCharAt >= iArr[i4] && cCharAt <= iArr[i4 + 1]) {
                        return true;
                    }
                    i4 += 2;
                }
                return false;
            case 3:
                while (i4 < i5) {
                    char cCharAt2 = str.charAt(i);
                    if (cCharAt2 >= iArr[i4] && cCharAt2 <= iArr[i4 + 1]) {
                        return false;
                    }
                    i4 += 2;
                }
                return true;
            default:
                return false;
        }
    }

    private static int matchChars(String str, int i, int i2, int i3, int i4, int i5, int[] iArr, int i6, int i7) {
        int i8 = 0;
        while (i8 < i5 && matchChar(str, i + i8, i2, i3, iArr, i6, i7)) {
            i8++;
        }
        if (i8 < i4) {
            return -1;
        }
        return i8;
    }

    static boolean matchGlobPattern(String str, String str2) {
        int length = str.length();
        if (length <= 0) {
            return str2.length() <= 0;
        }
        int length2 = str2.length();
        int i = 0;
        char cCharAt = str.charAt(0);
        int i2 = 0;
        while (i2 < length && i < length2) {
            i2++;
            char cCharAt2 = i2 < length ? str.charAt(i2) : (char) 0;
            boolean z = cCharAt == '\\';
            if (z) {
                i2++;
                cCharAt = i2 < length ? str.charAt(i2) : (char) 0;
            } else {
                char c = cCharAt2;
                cCharAt2 = cCharAt;
                cCharAt = c;
            }
            if (cCharAt == '*') {
                if (z || cCharAt2 != '.') {
                    while (str2.charAt(i) == cCharAt2 && (i = i + 1) < length2) {
                    }
                    i2++;
                    cCharAt = i2 < length ? str.charAt(i2) : (char) 0;
                } else {
                    if (i2 >= length - 1) {
                        return true;
                    }
                    int i3 = i2 + 1;
                    char cCharAt3 = str.charAt(i3);
                    if (cCharAt3 == '\\') {
                        i3++;
                        cCharAt3 = i3 < length ? str.charAt(i3) : (char) 0;
                    }
                    while (str2.charAt(i) != cCharAt3 && (i = i + 1) < length2) {
                    }
                    if (i == length2) {
                        return false;
                    }
                    i2 = i3 + 1;
                    cCharAt = i2 < length ? str.charAt(i2) : (char) 0;
                    i++;
                }
            } else {
                if (cCharAt2 != '.' && str2.charAt(i) != cCharAt2) {
                    return false;
                }
                i++;
            }
        }
        if (i2 < length || i < length2) {
            return i2 == length + (-2) && str.charAt(i2) == '.' && str.charAt(i2 + 1) == '*';
        }
        return true;
    }

    static boolean matchPattern(String str, String str2, int[] iArr, int i) {
        if (str == null) {
            return false;
        }
        if (i == 0) {
            return str2.equals(str);
        }
        if (i == 1) {
            return str.startsWith(str2);
        }
        if (i == 2) {
            return matchGlobPattern(str2, str);
        }
        if (i == 3) {
            return matchAdvancedPattern(iArr, str);
        }
        return false;
    }

    /* JADX WARN: Code duplicated, block: B:111:0x0151 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:126:0x013f A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:127:0x0102 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:128:0x01a0 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:129:0x0198 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:130:0x0188 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:67:0x0104 A[Catch: all -> 0x01c8, TryCatch #1 {, blocks: (B:4:0x0003, B:8:0x0014, B:13:0x0024, B:15:0x0029, B:67:0x0104, B:99:0x01ab, B:68:0x010e, B:70:0x0112, B:72:0x011c, B:74:0x0124, B:75:0x012e, B:77:0x013f, B:79:0x0145, B:81:0x0151, B:89:0x0178, B:90:0x0188, B:91:0x018f, B:84:0x0159, B:87:0x016c, B:92:0x0190, B:93:0x0197, B:94:0x0198, B:95:0x019f, B:97:0x01a2, B:19:0x0033, B:22:0x003d, B:23:0x004a, B:24:0x0051, B:25:0x0052, B:27:0x0056, B:28:0x005b, B:29:0x0062, B:32:0x0066, B:34:0x0070, B:36:0x007e, B:35:0x0078, B:39:0x0086, B:41:0x0092, B:42:0x009b, B:43:0x00a2, B:46:0x00a7, B:48:0x00b3, B:49:0x00bb, B:50:0x00c2, B:52:0x00c5, B:56:0x00d4, B:58:0x00e0, B:59:0x00ec, B:60:0x00f3, B:62:0x00f6, B:100:0x01ae, B:101:0x01b5, B:103:0x01b8, B:106:0x01c0, B:107:0x01c7), top: B:113:0x0003, inners: #0 }] */
    /* JADX WARN: Code duplicated, block: B:68:0x010e A[Catch: all -> 0x01c8, TryCatch #1 {, blocks: (B:4:0x0003, B:8:0x0014, B:13:0x0024, B:15:0x0029, B:67:0x0104, B:99:0x01ab, B:68:0x010e, B:70:0x0112, B:72:0x011c, B:74:0x0124, B:75:0x012e, B:77:0x013f, B:79:0x0145, B:81:0x0151, B:89:0x0178, B:90:0x0188, B:91:0x018f, B:84:0x0159, B:87:0x016c, B:92:0x0190, B:93:0x0197, B:94:0x0198, B:95:0x019f, B:97:0x01a2, B:19:0x0033, B:22:0x003d, B:23:0x004a, B:24:0x0051, B:25:0x0052, B:27:0x0056, B:28:0x005b, B:29:0x0062, B:32:0x0066, B:34:0x0070, B:36:0x007e, B:35:0x0078, B:39:0x0086, B:41:0x0092, B:42:0x009b, B:43:0x00a2, B:46:0x00a7, B:48:0x00b3, B:49:0x00bb, B:50:0x00c2, B:52:0x00c5, B:56:0x00d4, B:58:0x00e0, B:59:0x00ec, B:60:0x00f3, B:62:0x00f6, B:100:0x01ae, B:101:0x01b5, B:103:0x01b8, B:106:0x01c0, B:107:0x01c7), top: B:113:0x0003, inners: #0 }] */
    /* JADX WARN: Code duplicated, block: B:70:0x0112 A[Catch: all -> 0x01c8, TryCatch #1 {, blocks: (B:4:0x0003, B:8:0x0014, B:13:0x0024, B:15:0x0029, B:67:0x0104, B:99:0x01ab, B:68:0x010e, B:70:0x0112, B:72:0x011c, B:74:0x0124, B:75:0x012e, B:77:0x013f, B:79:0x0145, B:81:0x0151, B:89:0x0178, B:90:0x0188, B:91:0x018f, B:84:0x0159, B:87:0x016c, B:92:0x0190, B:93:0x0197, B:94:0x0198, B:95:0x019f, B:97:0x01a2, B:19:0x0033, B:22:0x003d, B:23:0x004a, B:24:0x0051, B:25:0x0052, B:27:0x0056, B:28:0x005b, B:29:0x0062, B:32:0x0066, B:34:0x0070, B:36:0x007e, B:35:0x0078, B:39:0x0086, B:41:0x0092, B:42:0x009b, B:43:0x00a2, B:46:0x00a7, B:48:0x00b3, B:49:0x00bb, B:50:0x00c2, B:52:0x00c5, B:56:0x00d4, B:58:0x00e0, B:59:0x00ec, B:60:0x00f3, B:62:0x00f6, B:100:0x01ae, B:101:0x01b5, B:103:0x01b8, B:106:0x01c0, B:107:0x01c7), top: B:113:0x0003, inners: #0 }] */
    /* JADX WARN: Code duplicated, block: B:72:0x011c A[Catch: all -> 0x01c8, TryCatch #1 {, blocks: (B:4:0x0003, B:8:0x0014, B:13:0x0024, B:15:0x0029, B:67:0x0104, B:99:0x01ab, B:68:0x010e, B:70:0x0112, B:72:0x011c, B:74:0x0124, B:75:0x012e, B:77:0x013f, B:79:0x0145, B:81:0x0151, B:89:0x0178, B:90:0x0188, B:91:0x018f, B:84:0x0159, B:87:0x016c, B:92:0x0190, B:93:0x0197, B:94:0x0198, B:95:0x019f, B:97:0x01a2, B:19:0x0033, B:22:0x003d, B:23:0x004a, B:24:0x0051, B:25:0x0052, B:27:0x0056, B:28:0x005b, B:29:0x0062, B:32:0x0066, B:34:0x0070, B:36:0x007e, B:35:0x0078, B:39:0x0086, B:41:0x0092, B:42:0x009b, B:43:0x00a2, B:46:0x00a7, B:48:0x00b3, B:49:0x00bb, B:50:0x00c2, B:52:0x00c5, B:56:0x00d4, B:58:0x00e0, B:59:0x00ec, B:60:0x00f3, B:62:0x00f6, B:100:0x01ae, B:101:0x01b5, B:103:0x01b8, B:106:0x01c0, B:107:0x01c7), top: B:113:0x0003, inners: #0 }] */
    /* JADX WARN: Code duplicated, block: B:75:0x012e A[Catch: all -> 0x01c8, TryCatch #1 {, blocks: (B:4:0x0003, B:8:0x0014, B:13:0x0024, B:15:0x0029, B:67:0x0104, B:99:0x01ab, B:68:0x010e, B:70:0x0112, B:72:0x011c, B:74:0x0124, B:75:0x012e, B:77:0x013f, B:79:0x0145, B:81:0x0151, B:89:0x0178, B:90:0x0188, B:91:0x018f, B:84:0x0159, B:87:0x016c, B:92:0x0190, B:93:0x0197, B:94:0x0198, B:95:0x019f, B:97:0x01a2, B:19:0x0033, B:22:0x003d, B:23:0x004a, B:24:0x0051, B:25:0x0052, B:27:0x0056, B:28:0x005b, B:29:0x0062, B:32:0x0066, B:34:0x0070, B:36:0x007e, B:35:0x0078, B:39:0x0086, B:41:0x0092, B:42:0x009b, B:43:0x00a2, B:46:0x00a7, B:48:0x00b3, B:49:0x00bb, B:50:0x00c2, B:52:0x00c5, B:56:0x00d4, B:58:0x00e0, B:59:0x00ec, B:60:0x00f3, B:62:0x00f6, B:100:0x01ae, B:101:0x01b5, B:103:0x01b8, B:106:0x01c0, B:107:0x01c7), top: B:113:0x0003, inners: #0 }] */
    /* JADX WARN: Code duplicated, block: B:76:0x013d A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:79:0x0145 A[Catch: all -> 0x01c8, TRY_LEAVE, TryCatch #1 {, blocks: (B:4:0x0003, B:8:0x0014, B:13:0x0024, B:15:0x0029, B:67:0x0104, B:99:0x01ab, B:68:0x010e, B:70:0x0112, B:72:0x011c, B:74:0x0124, B:75:0x012e, B:77:0x013f, B:79:0x0145, B:81:0x0151, B:89:0x0178, B:90:0x0188, B:91:0x018f, B:84:0x0159, B:87:0x016c, B:92:0x0190, B:93:0x0197, B:94:0x0198, B:95:0x019f, B:97:0x01a2, B:19:0x0033, B:22:0x003d, B:23:0x004a, B:24:0x0051, B:25:0x0052, B:27:0x0056, B:28:0x005b, B:29:0x0062, B:32:0x0066, B:34:0x0070, B:36:0x007e, B:35:0x0078, B:39:0x0086, B:41:0x0092, B:42:0x009b, B:43:0x00a2, B:46:0x00a7, B:48:0x00b3, B:49:0x00bb, B:50:0x00c2, B:52:0x00c5, B:56:0x00d4, B:58:0x00e0, B:59:0x00ec, B:60:0x00f3, B:62:0x00f6, B:100:0x01ae, B:101:0x01b5, B:103:0x01b8, B:106:0x01c0, B:107:0x01c7), top: B:113:0x0003, inners: #0 }] */
    /* JADX WARN: Code duplicated, block: B:84:0x0159 A[Catch: NumberFormatException -> 0x0157, all -> 0x01c8, TryCatch #0 {NumberFormatException -> 0x0157, blocks: (B:81:0x0151, B:89:0x0178, B:90:0x0188, B:91:0x018f, B:84:0x0159, B:87:0x016c), top: B:111:0x0151, outer: #1 }] */
    /* JADX WARN: Code duplicated, block: B:86:0x0168  */
    /* JADX WARN: Code duplicated, block: B:87:0x016c A[Catch: NumberFormatException -> 0x0157, all -> 0x01c8, TryCatch #0 {NumberFormatException -> 0x0157, blocks: (B:81:0x0151, B:89:0x0178, B:90:0x0188, B:91:0x018f, B:84:0x0159, B:87:0x016c), top: B:111:0x0151, outer: #1 }] */
    /* JADX WARN: Code duplicated, block: B:89:0x0178 A[Catch: NumberFormatException -> 0x0157, all -> 0x01c8, TryCatch #0 {NumberFormatException -> 0x0157, blocks: (B:81:0x0151, B:89:0x0178, B:90:0x0188, B:91:0x018f, B:84:0x0159, B:87:0x016c), top: B:111:0x0151, outer: #1 }] */
    /* JADX WARN: Code duplicated, block: B:97:0x01a2 A[Catch: all -> 0x01c8, TryCatch #1 {, blocks: (B:4:0x0003, B:8:0x0014, B:13:0x0024, B:15:0x0029, B:67:0x0104, B:99:0x01ab, B:68:0x010e, B:70:0x0112, B:72:0x011c, B:74:0x0124, B:75:0x012e, B:77:0x013f, B:79:0x0145, B:81:0x0151, B:89:0x0178, B:90:0x0188, B:91:0x018f, B:84:0x0159, B:87:0x016c, B:92:0x0190, B:93:0x0197, B:94:0x0198, B:95:0x019f, B:97:0x01a2, B:19:0x0033, B:22:0x003d, B:23:0x004a, B:24:0x0051, B:25:0x0052, B:27:0x0056, B:28:0x005b, B:29:0x0062, B:32:0x0066, B:34:0x0070, B:36:0x007e, B:35:0x0078, B:39:0x0086, B:41:0x0092, B:42:0x009b, B:43:0x00a2, B:46:0x00a7, B:48:0x00b3, B:49:0x00bb, B:50:0x00c2, B:52:0x00c5, B:56:0x00d4, B:58:0x00e0, B:59:0x00ec, B:60:0x00f3, B:62:0x00f6, B:100:0x01ae, B:101:0x01b5, B:103:0x01b8, B:106:0x01c0, B:107:0x01c7), top: B:113:0x0003, inners: #0 }] */
    /* JADX WARN: Code duplicated, block: B:98:0x01aa  */
    static synchronized int[] parseAndVerifyAdvancedPattern(String str) {
        int i;
        int i2;
        boolean z;
        int i3;
        int i4;
        int iIndexOf;
        String strSubstring;
        int iIndexOf2;
        int i5;
        int i6;
        int i7;
        int i8;
        int length = str.length();
        int i9 = 0;
        i = 0;
        boolean z2 = false;
        boolean z3 = false;
        boolean z4 = false;
        while (i9 < length) {
            if (i > 2045) {
                throw new IllegalArgumentException("Pattern is too large!");
            }
            char cCharAt = str.charAt(i9);
            if (cCharAt == '.') {
                if (z2) {
                    i2 = i;
                } else {
                    i2 = i + 1;
                    sParsedPatternScratch[i] = -4;
                }
                z = false;
                if (z2) {
                    if (z4) {
                        sParsedPatternScratch[i2] = cCharAt;
                        i = i2 + 1;
                        z4 = false;
                    } else {
                        i3 = i9 + 2;
                        if (i3 < length) {
                            i4 = i9 + 1;
                            if (str.charAt(i4) == '-') {
                                int i10 = i2 + 1;
                                sParsedPatternScratch[i2] = cCharAt;
                                sParsedPatternScratch[i10] = cCharAt;
                                i = i10 + 1;
                            } else {
                                int i11 = i2 + 1;
                                sParsedPatternScratch[i2] = cCharAt;
                                sParsedPatternScratch[i11] = cCharAt;
                                i = i11 + 1;
                            }
                        } else {
                            int i12 = i2 + 1;
                            sParsedPatternScratch[i2] = cCharAt;
                            sParsedPatternScratch[i12] = cCharAt;
                            i = i12 + 1;
                        }
                    }
                } else if (z3) {
                    iIndexOf = str.indexOf(125, i9);
                    if (iIndexOf < 0) {
                        throw new IllegalArgumentException("Range not ended with '}'");
                    }
                    strSubstring = str.substring(i9, iIndexOf);
                    iIndexOf2 = strSubstring.indexOf(44);
                    if (iIndexOf2 < 0) {
                        i5 = Integer.parseInt(strSubstring);
                        i6 = i5;
                    } else {
                        i6 = Integer.parseInt(strSubstring.substring(0, iIndexOf2));
                        if (iIndexOf2 == strSubstring.length() - 1) {
                            i5 = Integer.MAX_VALUE;
                        } else {
                            i5 = Integer.parseInt(strSubstring.substring(iIndexOf2 + 1));
                        }
                    }
                    if (i6 > i5) {
                        throw new IllegalArgumentException("Range quantifier minimum is greater than maximum");
                    }
                    int i13 = i2 + 1;
                    sParsedPatternScratch[i2] = i6;
                    sParsedPatternScratch[i13] = i5;
                    i9 = iIndexOf;
                    i = i13 + 1;
                } else if (z) {
                    sParsedPatternScratch[i2] = cCharAt;
                    i = i2 + 1;
                } else {
                    i = i2;
                }
                i9++;
            } else if (cCharAt != '{') {
                if (cCharAt != '}') {
                    switch (cCharAt) {
                        case '*':
                            if (!z2) {
                                if (i == 0 || isParsedModifier(sParsedPatternScratch[i - 1])) {
                                    throw new IllegalArgumentException("Modifier must follow a token.");
                                }
                                i2 = i + 1;
                                sParsedPatternScratch[i] = -7;
                            }
                            z = false;
                            if (z2) {
                                if (z4) {
                                    sParsedPatternScratch[i2] = cCharAt;
                                    i = i2 + 1;
                                    z4 = false;
                                } else {
                                    i3 = i9 + 2;
                                    if (i3 < length) {
                                        i4 = i9 + 1;
                                        if (str.charAt(i4) == '-' || str.charAt(i3) == ']') {
                                            int i14 = i2 + 1;
                                            sParsedPatternScratch[i2] = cCharAt;
                                            sParsedPatternScratch[i14] = cCharAt;
                                            i = i14 + 1;
                                        } else {
                                            i = i2 + 1;
                                            sParsedPatternScratch[i2] = cCharAt;
                                            z4 = true;
                                            i9 = i4;
                                        }
                                    } else {
                                        int i15 = i2 + 1;
                                        sParsedPatternScratch[i2] = cCharAt;
                                        sParsedPatternScratch[i15] = cCharAt;
                                        i = i15 + 1;
                                    }
                                }
                            } else if (z3) {
                                iIndexOf = str.indexOf(125, i9);
                                if (iIndexOf < 0) {
                                    throw new IllegalArgumentException("Range not ended with '}'");
                                }
                                strSubstring = str.substring(i9, iIndexOf);
                                iIndexOf2 = strSubstring.indexOf(44);
                                if (iIndexOf2 < 0) {
                                    try {
                                        i5 = Integer.parseInt(strSubstring);
                                        i6 = i5;
                                    } catch (NumberFormatException e) {
                                        throw new IllegalArgumentException("Range number format incorrect", e);
                                    }
                                } else {
                                    i6 = Integer.parseInt(strSubstring.substring(0, iIndexOf2));
                                    if (iIndexOf2 == strSubstring.length() - 1) {
                                        i5 = Integer.MAX_VALUE;
                                    } else {
                                        i5 = Integer.parseInt(strSubstring.substring(iIndexOf2 + 1));
                                    }
                                }
                                if (i6 > i5) {
                                    throw new IllegalArgumentException("Range quantifier minimum is greater than maximum");
                                }
                                int i16 = i2 + 1;
                                sParsedPatternScratch[i2] = i6;
                                sParsedPatternScratch[i16] = i5;
                                i9 = iIndexOf;
                                i = i16 + 1;
                                break;
                            } else if (z) {
                                sParsedPatternScratch[i2] = cCharAt;
                                i = i2 + 1;
                            } else {
                                i = i2;
                            }
                            i9++;
                            break;
                        case '+':
                            if (!z2) {
                                if (i == 0 || isParsedModifier(sParsedPatternScratch[i - 1])) {
                                    throw new IllegalArgumentException("Modifier must follow a token.");
                                }
                                i2 = i + 1;
                                sParsedPatternScratch[i] = -8;
                            }
                            z = false;
                            if (z2) {
                                if (z4) {
                                    sParsedPatternScratch[i2] = cCharAt;
                                    i = i2 + 1;
                                    z4 = false;
                                } else {
                                    i3 = i9 + 2;
                                    if (i3 < length) {
                                        i4 = i9 + 1;
                                        if (str.charAt(i4) == '-') {
                                            int i17 = i2 + 1;
                                            sParsedPatternScratch[i2] = cCharAt;
                                            sParsedPatternScratch[i17] = cCharAt;
                                            i = i17 + 1;
                                        } else {
                                            int i18 = i2 + 1;
                                            sParsedPatternScratch[i2] = cCharAt;
                                            sParsedPatternScratch[i18] = cCharAt;
                                            i = i18 + 1;
                                        }
                                    } else {
                                        int i19 = i2 + 1;
                                        sParsedPatternScratch[i2] = cCharAt;
                                        sParsedPatternScratch[i19] = cCharAt;
                                        i = i19 + 1;
                                    }
                                }
                            } else if (z3) {
                                iIndexOf = str.indexOf(125, i9);
                                if (iIndexOf < 0) {
                                    throw new IllegalArgumentException("Range not ended with '}'");
                                }
                                strSubstring = str.substring(i9, iIndexOf);
                                iIndexOf2 = strSubstring.indexOf(44);
                                if (iIndexOf2 < 0) {
                                    i5 = Integer.parseInt(strSubstring);
                                    i6 = i5;
                                } else {
                                    i6 = Integer.parseInt(strSubstring.substring(0, iIndexOf2));
                                    if (iIndexOf2 == strSubstring.length() - 1) {
                                        i5 = Integer.MAX_VALUE;
                                    } else {
                                        i5 = Integer.parseInt(strSubstring.substring(iIndexOf2 + 1));
                                    }
                                }
                                if (i6 > i5) {
                                    throw new IllegalArgumentException("Range quantifier minimum is greater than maximum");
                                }
                                int i110 = i2 + 1;
                                sParsedPatternScratch[i2] = i6;
                                sParsedPatternScratch[i110] = i5;
                                i9 = iIndexOf;
                                i = i110 + 1;
                                break;
                            } else if (z) {
                                sParsedPatternScratch[i2] = cCharAt;
                                i = i2 + 1;
                            } else {
                                i = i2;
                            }
                            i9++;
                            break;
                        default:
                            switch (cCharAt) {
                                case '[':
                                    if (!z2) {
                                        int i20 = i9 + 1;
                                        if (str.charAt(i20) == '^') {
                                            i8 = i + 1;
                                            sParsedPatternScratch[i] = -2;
                                            i9 = i20;
                                        } else {
                                            i8 = i + 1;
                                            sParsedPatternScratch[i] = -1;
                                        }
                                        i = i8;
                                        i9++;
                                        z2 = true;
                                    } else {
                                        i2 = i;
                                        z = true;
                                        if (z2) {
                                            if (z4) {
                                                sParsedPatternScratch[i2] = cCharAt;
                                                i = i2 + 1;
                                                z4 = false;
                                            } else {
                                                i3 = i9 + 2;
                                                if (i3 < length) {
                                                    i4 = i9 + 1;
                                                    if (str.charAt(i4) == '-') {
                                                        int i111 = i2 + 1;
                                                        sParsedPatternScratch[i2] = cCharAt;
                                                        sParsedPatternScratch[i111] = cCharAt;
                                                        i = i111 + 1;
                                                    } else {
                                                        int i112 = i2 + 1;
                                                        sParsedPatternScratch[i2] = cCharAt;
                                                        sParsedPatternScratch[i112] = cCharAt;
                                                        i = i112 + 1;
                                                    }
                                                } else {
                                                    int i113 = i2 + 1;
                                                    sParsedPatternScratch[i2] = cCharAt;
                                                    sParsedPatternScratch[i113] = cCharAt;
                                                    i = i113 + 1;
                                                }
                                            }
                                        } else if (z3) {
                                            iIndexOf = str.indexOf(125, i9);
                                            if (iIndexOf < 0) {
                                                throw new IllegalArgumentException("Range not ended with '}'");
                                            }
                                            strSubstring = str.substring(i9, iIndexOf);
                                            iIndexOf2 = strSubstring.indexOf(44);
                                            if (iIndexOf2 < 0) {
                                                i5 = Integer.parseInt(strSubstring);
                                                i6 = i5;
                                            } else {
                                                i6 = Integer.parseInt(strSubstring.substring(0, iIndexOf2));
                                                if (iIndexOf2 == strSubstring.length() - 1) {
                                                    i5 = Integer.MAX_VALUE;
                                                } else {
                                                    i5 = Integer.parseInt(strSubstring.substring(iIndexOf2 + 1));
                                                }
                                            }
                                            if (i6 > i5) {
                                                throw new IllegalArgumentException("Range quantifier minimum is greater than maximum");
                                            }
                                            int i114 = i2 + 1;
                                            sParsedPatternScratch[i2] = i6;
                                            sParsedPatternScratch[i114] = i5;
                                            i9 = iIndexOf;
                                            i = i114 + 1;
                                            break;
                                        } else if (z) {
                                            sParsedPatternScratch[i2] = cCharAt;
                                            i = i2 + 1;
                                        } else {
                                            i = i2;
                                        }
                                        i9++;
                                    }
                                    break;
                                case '\\':
                                    i9++;
                                    if (i9 >= length) {
                                        throw new IllegalArgumentException("Escape found at end of pattern!");
                                    }
                                    cCharAt = str.charAt(i9);
                                    i2 = i;
                                    z = true;
                                    if (z2) {
                                        if (z4) {
                                            sParsedPatternScratch[i2] = cCharAt;
                                            i = i2 + 1;
                                            z4 = false;
                                        } else {
                                            i3 = i9 + 2;
                                            if (i3 < length) {
                                                i4 = i9 + 1;
                                                if (str.charAt(i4) == '-') {
                                                    int i115 = i2 + 1;
                                                    sParsedPatternScratch[i2] = cCharAt;
                                                    sParsedPatternScratch[i115] = cCharAt;
                                                    i = i115 + 1;
                                                } else {
                                                    int i116 = i2 + 1;
                                                    sParsedPatternScratch[i2] = cCharAt;
                                                    sParsedPatternScratch[i116] = cCharAt;
                                                    i = i116 + 1;
                                                }
                                            } else {
                                                int i117 = i2 + 1;
                                                sParsedPatternScratch[i2] = cCharAt;
                                                sParsedPatternScratch[i117] = cCharAt;
                                                i = i117 + 1;
                                            }
                                        }
                                    } else if (z3) {
                                        iIndexOf = str.indexOf(125, i9);
                                        if (iIndexOf < 0) {
                                            throw new IllegalArgumentException("Range not ended with '}'");
                                        }
                                        strSubstring = str.substring(i9, iIndexOf);
                                        iIndexOf2 = strSubstring.indexOf(44);
                                        if (iIndexOf2 < 0) {
                                            i5 = Integer.parseInt(strSubstring);
                                            i6 = i5;
                                        } else {
                                            i6 = Integer.parseInt(strSubstring.substring(0, iIndexOf2));
                                            if (iIndexOf2 == strSubstring.length() - 1) {
                                                i5 = Integer.MAX_VALUE;
                                            } else {
                                                i5 = Integer.parseInt(strSubstring.substring(iIndexOf2 + 1));
                                            }
                                        }
                                        if (i6 > i5) {
                                            throw new IllegalArgumentException("Range quantifier minimum is greater than maximum");
                                        }
                                        int i118 = i2 + 1;
                                        sParsedPatternScratch[i2] = i6;
                                        sParsedPatternScratch[i118] = i5;
                                        i9 = iIndexOf;
                                        i = i118 + 1;
                                        break;
                                    } else if (z) {
                                        sParsedPatternScratch[i2] = cCharAt;
                                        i = i2 + 1;
                                    } else {
                                        i = i2;
                                    }
                                    i9++;
                                    break;
                                    break;
                                case ']':
                                    if (z2) {
                                        int i21 = sParsedPatternScratch[i - 1];
                                        if (i21 == -1 || i21 == -2) {
                                            throw new IllegalArgumentException("You must define characters in a set.");
                                        }
                                        int i22 = i + 1;
                                        sParsedPatternScratch[i] = -3;
                                        z = false;
                                        z2 = false;
                                        i2 = i22;
                                        z4 = false;
                                    } else {
                                        i2 = i;
                                        z = true;
                                    }
                                    if (z2) {
                                        if (z4) {
                                            sParsedPatternScratch[i2] = cCharAt;
                                            i = i2 + 1;
                                            z4 = false;
                                        } else {
                                            i3 = i9 + 2;
                                            if (i3 < length) {
                                                i4 = i9 + 1;
                                                if (str.charAt(i4) == '-') {
                                                    int i119 = i2 + 1;
                                                    sParsedPatternScratch[i2] = cCharAt;
                                                    sParsedPatternScratch[i119] = cCharAt;
                                                    i = i119 + 1;
                                                } else {
                                                    int i1110 = i2 + 1;
                                                    sParsedPatternScratch[i2] = cCharAt;
                                                    sParsedPatternScratch[i1110] = cCharAt;
                                                    i = i1110 + 1;
                                                }
                                            } else {
                                                int i1111 = i2 + 1;
                                                sParsedPatternScratch[i2] = cCharAt;
                                                sParsedPatternScratch[i1111] = cCharAt;
                                                i = i1111 + 1;
                                            }
                                        }
                                    } else if (z3) {
                                        iIndexOf = str.indexOf(125, i9);
                                        if (iIndexOf < 0) {
                                            throw new IllegalArgumentException("Range not ended with '}'");
                                        }
                                        strSubstring = str.substring(i9, iIndexOf);
                                        iIndexOf2 = strSubstring.indexOf(44);
                                        if (iIndexOf2 < 0) {
                                            i5 = Integer.parseInt(strSubstring);
                                            i6 = i5;
                                        } else {
                                            i6 = Integer.parseInt(strSubstring.substring(0, iIndexOf2));
                                            if (iIndexOf2 == strSubstring.length() - 1) {
                                                i5 = Integer.MAX_VALUE;
                                            } else {
                                                i5 = Integer.parseInt(strSubstring.substring(iIndexOf2 + 1));
                                            }
                                        }
                                        if (i6 > i5) {
                                            throw new IllegalArgumentException("Range quantifier minimum is greater than maximum");
                                        }
                                        int i1112 = i2 + 1;
                                        sParsedPatternScratch[i2] = i6;
                                        sParsedPatternScratch[i1112] = i5;
                                        i9 = iIndexOf;
                                        i = i1112 + 1;
                                        break;
                                    } else if (z) {
                                        sParsedPatternScratch[i2] = cCharAt;
                                        i = i2 + 1;
                                    } else {
                                        i = i2;
                                    }
                                    i9++;
                                    break;
                                default:
                                    i2 = i;
                                    z = true;
                                    if (z2) {
                                        if (z4) {
                                            sParsedPatternScratch[i2] = cCharAt;
                                            i = i2 + 1;
                                            z4 = false;
                                        } else {
                                            i3 = i9 + 2;
                                            if (i3 < length) {
                                                i4 = i9 + 1;
                                                if (str.charAt(i4) == '-') {
                                                    int i1113 = i2 + 1;
                                                    sParsedPatternScratch[i2] = cCharAt;
                                                    sParsedPatternScratch[i1113] = cCharAt;
                                                    i = i1113 + 1;
                                                } else {
                                                    int i1114 = i2 + 1;
                                                    sParsedPatternScratch[i2] = cCharAt;
                                                    sParsedPatternScratch[i1114] = cCharAt;
                                                    i = i1114 + 1;
                                                }
                                            } else {
                                                int i1115 = i2 + 1;
                                                sParsedPatternScratch[i2] = cCharAt;
                                                sParsedPatternScratch[i1115] = cCharAt;
                                                i = i1115 + 1;
                                            }
                                        }
                                    } else if (z3) {
                                        iIndexOf = str.indexOf(125, i9);
                                        if (iIndexOf < 0) {
                                            throw new IllegalArgumentException("Range not ended with '}'");
                                        }
                                        strSubstring = str.substring(i9, iIndexOf);
                                        iIndexOf2 = strSubstring.indexOf(44);
                                        if (iIndexOf2 < 0) {
                                            i5 = Integer.parseInt(strSubstring);
                                            i6 = i5;
                                        } else {
                                            i6 = Integer.parseInt(strSubstring.substring(0, iIndexOf2));
                                            if (iIndexOf2 == strSubstring.length() - 1) {
                                                i5 = Integer.MAX_VALUE;
                                            } else {
                                                i5 = Integer.parseInt(strSubstring.substring(iIndexOf2 + 1));
                                            }
                                        }
                                        if (i6 > i5) {
                                            throw new IllegalArgumentException("Range quantifier minimum is greater than maximum");
                                        }
                                        int i1116 = i2 + 1;
                                        sParsedPatternScratch[i2] = i6;
                                        sParsedPatternScratch[i1116] = i5;
                                        i9 = iIndexOf;
                                        i = i1116 + 1;
                                        break;
                                    } else if (z) {
                                        sParsedPatternScratch[i2] = cCharAt;
                                        i = i2 + 1;
                                    } else {
                                        i = i2;
                                    }
                                    i9++;
                                    break;
                            }
                            break;
                    }
                } else if (z3) {
                    i7 = i + 1;
                    sParsedPatternScratch[i] = -6;
                    z = false;
                    z3 = false;
                    i2 = i7;
                    if (z2) {
                        if (z4) {
                            sParsedPatternScratch[i2] = cCharAt;
                            i = i2 + 1;
                            z4 = false;
                        } else {
                            i3 = i9 + 2;
                            if (i3 < length) {
                                i4 = i9 + 1;
                                if (str.charAt(i4) == '-') {
                                    int i1117 = i2 + 1;
                                    sParsedPatternScratch[i2] = cCharAt;
                                    sParsedPatternScratch[i1117] = cCharAt;
                                    i = i1117 + 1;
                                } else {
                                    int i1118 = i2 + 1;
                                    sParsedPatternScratch[i2] = cCharAt;
                                    sParsedPatternScratch[i1118] = cCharAt;
                                    i = i1118 + 1;
                                }
                            } else {
                                int i1119 = i2 + 1;
                                sParsedPatternScratch[i2] = cCharAt;
                                sParsedPatternScratch[i1119] = cCharAt;
                                i = i1119 + 1;
                            }
                        }
                    } else if (z3) {
                        iIndexOf = str.indexOf(125, i9);
                        if (iIndexOf < 0) {
                            throw new IllegalArgumentException("Range not ended with '}'");
                        }
                        strSubstring = str.substring(i9, iIndexOf);
                        iIndexOf2 = strSubstring.indexOf(44);
                        if (iIndexOf2 < 0) {
                            i5 = Integer.parseInt(strSubstring);
                            i6 = i5;
                        } else {
                            i6 = Integer.parseInt(strSubstring.substring(0, iIndexOf2));
                            if (iIndexOf2 == strSubstring.length() - 1) {
                                i5 = Integer.MAX_VALUE;
                            } else {
                                i5 = Integer.parseInt(strSubstring.substring(iIndexOf2 + 1));
                            }
                        }
                        if (i6 > i5) {
                            throw new IllegalArgumentException("Range quantifier minimum is greater than maximum");
                        }
                        int i11110 = i2 + 1;
                        sParsedPatternScratch[i2] = i6;
                        sParsedPatternScratch[i11110] = i5;
                        i9 = iIndexOf;
                        i = i11110 + 1;
                    } else if (z) {
                        sParsedPatternScratch[i2] = cCharAt;
                        i = i2 + 1;
                    } else {
                        i = i2;
                    }
                    i9++;
                }
                i2 = i;
                z = false;
                if (z2) {
                    if (z4) {
                        sParsedPatternScratch[i2] = cCharAt;
                        i = i2 + 1;
                        z4 = false;
                    } else {
                        i3 = i9 + 2;
                        if (i3 < length) {
                            i4 = i9 + 1;
                            if (str.charAt(i4) == '-') {
                                int i11111 = i2 + 1;
                                sParsedPatternScratch[i2] = cCharAt;
                                sParsedPatternScratch[i11111] = cCharAt;
                                i = i11111 + 1;
                            } else {
                                int i11112 = i2 + 1;
                                sParsedPatternScratch[i2] = cCharAt;
                                sParsedPatternScratch[i11112] = cCharAt;
                                i = i11112 + 1;
                            }
                        } else {
                            int i11113 = i2 + 1;
                            sParsedPatternScratch[i2] = cCharAt;
                            sParsedPatternScratch[i11113] = cCharAt;
                            i = i11113 + 1;
                        }
                    }
                } else if (z3) {
                    iIndexOf = str.indexOf(125, i9);
                    if (iIndexOf < 0) {
                        throw new IllegalArgumentException("Range not ended with '}'");
                    }
                    strSubstring = str.substring(i9, iIndexOf);
                    iIndexOf2 = strSubstring.indexOf(44);
                    if (iIndexOf2 < 0) {
                        i5 = Integer.parseInt(strSubstring);
                        i6 = i5;
                    } else {
                        i6 = Integer.parseInt(strSubstring.substring(0, iIndexOf2));
                        if (iIndexOf2 == strSubstring.length() - 1) {
                            i5 = Integer.MAX_VALUE;
                        } else {
                            i5 = Integer.parseInt(strSubstring.substring(iIndexOf2 + 1));
                        }
                    }
                    if (i6 > i5) {
                        throw new IllegalArgumentException("Range quantifier minimum is greater than maximum");
                    }
                    int i11114 = i2 + 1;
                    sParsedPatternScratch[i2] = i6;
                    sParsedPatternScratch[i11114] = i5;
                    i9 = iIndexOf;
                    i = i11114 + 1;
                } else if (z) {
                    sParsedPatternScratch[i2] = cCharAt;
                    i = i2 + 1;
                } else {
                    i = i2;
                }
                i9++;
            } else {
                if (!z2) {
                    if (i == 0 || isParsedModifier(sParsedPatternScratch[i - 1])) {
                        throw new IllegalArgumentException("Modifier must follow a token.");
                    }
                    i7 = i + 1;
                    sParsedPatternScratch[i] = -5;
                    i9++;
                    z = false;
                    z3 = true;
                    i2 = i7;
                    if (z2) {
                        if (z4) {
                            sParsedPatternScratch[i2] = cCharAt;
                            i = i2 + 1;
                            z4 = false;
                        } else {
                            i3 = i9 + 2;
                            if (i3 < length) {
                                i4 = i9 + 1;
                                if (str.charAt(i4) == '-') {
                                    int i11115 = i2 + 1;
                                    sParsedPatternScratch[i2] = cCharAt;
                                    sParsedPatternScratch[i11115] = cCharAt;
                                    i = i11115 + 1;
                                } else {
                                    int i11116 = i2 + 1;
                                    sParsedPatternScratch[i2] = cCharAt;
                                    sParsedPatternScratch[i11116] = cCharAt;
                                    i = i11116 + 1;
                                }
                            } else {
                                int i11117 = i2 + 1;
                                sParsedPatternScratch[i2] = cCharAt;
                                sParsedPatternScratch[i11117] = cCharAt;
                                i = i11117 + 1;
                            }
                        }
                    } else if (z3) {
                        iIndexOf = str.indexOf(125, i9);
                        if (iIndexOf < 0) {
                            throw new IllegalArgumentException("Range not ended with '}'");
                        }
                        strSubstring = str.substring(i9, iIndexOf);
                        iIndexOf2 = strSubstring.indexOf(44);
                        if (iIndexOf2 < 0) {
                            i5 = Integer.parseInt(strSubstring);
                            i6 = i5;
                        } else {
                            i6 = Integer.parseInt(strSubstring.substring(0, iIndexOf2));
                            if (iIndexOf2 == strSubstring.length() - 1) {
                                i5 = Integer.MAX_VALUE;
                            } else {
                                i5 = Integer.parseInt(strSubstring.substring(iIndexOf2 + 1));
                            }
                        }
                        if (i6 > i5) {
                            throw new IllegalArgumentException("Range quantifier minimum is greater than maximum");
                        }
                        int i11118 = i2 + 1;
                        sParsedPatternScratch[i2] = i6;
                        sParsedPatternScratch[i11118] = i5;
                        i9 = iIndexOf;
                        i = i11118 + 1;
                    } else if (z) {
                        sParsedPatternScratch[i2] = cCharAt;
                        i = i2 + 1;
                    } else {
                        i = i2;
                    }
                    i9++;
                }
                i2 = i;
                z = false;
                if (z2) {
                    if (z4) {
                        sParsedPatternScratch[i2] = cCharAt;
                        i = i2 + 1;
                        z4 = false;
                    } else {
                        i3 = i9 + 2;
                        if (i3 < length) {
                            i4 = i9 + 1;
                            if (str.charAt(i4) == '-') {
                                int i11119 = i2 + 1;
                                sParsedPatternScratch[i2] = cCharAt;
                                sParsedPatternScratch[i11119] = cCharAt;
                                i = i11119 + 1;
                            } else {
                                int i111110 = i2 + 1;
                                sParsedPatternScratch[i2] = cCharAt;
                                sParsedPatternScratch[i111110] = cCharAt;
                                i = i111110 + 1;
                            }
                        } else {
                            int i111111 = i2 + 1;
                            sParsedPatternScratch[i2] = cCharAt;
                            sParsedPatternScratch[i111111] = cCharAt;
                            i = i111111 + 1;
                        }
                    }
                } else if (z3) {
                    iIndexOf = str.indexOf(125, i9);
                    if (iIndexOf < 0) {
                        throw new IllegalArgumentException("Range not ended with '}'");
                    }
                    strSubstring = str.substring(i9, iIndexOf);
                    iIndexOf2 = strSubstring.indexOf(44);
                    if (iIndexOf2 < 0) {
                        i5 = Integer.parseInt(strSubstring);
                        i6 = i5;
                    } else {
                        i6 = Integer.parseInt(strSubstring.substring(0, iIndexOf2));
                        if (iIndexOf2 == strSubstring.length() - 1) {
                            i5 = Integer.MAX_VALUE;
                        } else {
                            i5 = Integer.parseInt(strSubstring.substring(iIndexOf2 + 1));
                        }
                    }
                    if (i6 > i5) {
                        throw new IllegalArgumentException("Range quantifier minimum is greater than maximum");
                    }
                    int i111112 = i2 + 1;
                    sParsedPatternScratch[i2] = i6;
                    sParsedPatternScratch[i111112] = i5;
                    i9 = iIndexOf;
                    i = i111112 + 1;
                } else if (z) {
                    sParsedPatternScratch[i2] = cCharAt;
                    i = i2 + 1;
                } else {
                    i = i2;
                }
                i9++;
            }
        }
        if (z2) {
            throw new IllegalArgumentException("Set was not terminated!");
        }
        return Arrays.copyOf(sParsedPatternScratch, i);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public final String getPath() {
        return this.mPattern;
    }

    public final int getType() {
        return this.mType;
    }

    public boolean match(String str) {
        return matchPattern(str, this.mPattern, this.mParsedPattern, this.mType);
    }

    public String toString() {
        String str = "? ";
        switch (this.mType) {
            case 0:
                str = "LITERAL: ";
                break;
            case 1:
                str = "PREFIX: ";
                break;
            case 2:
                str = "GLOB: ";
                break;
            case 3:
                str = "ADVANCED: ";
                break;
        }
        return "PatternMatcher{" + str + this.mPattern + "}";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mPattern);
        parcel.writeInt(this.mType);
        parcel.writeIntArray(this.mParsedPattern);
    }

    public void writeToProto(ProtoOutputStream protoOutputStream, long j) {
        long jStart = protoOutputStream.start(j);
        protoOutputStream.write(1138166333441L, this.mPattern);
        protoOutputStream.write(1159641169922L, this.mType);
        protoOutputStream.end(jStart);
    }
}
