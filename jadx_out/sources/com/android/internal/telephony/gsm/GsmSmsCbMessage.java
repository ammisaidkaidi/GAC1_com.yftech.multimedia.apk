package com.android.internal.telephony.gsm;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.telephony.SmsCbLocation;
import android.telephony.SmsCbMessage;
import android.util.Pair;
import com.android.internal.R;
import com.android.internal.telephony.GsmAlphabet;
import java.io.UnsupportedEncodingException;

/* JADX INFO: loaded from: classes3.dex */
public class GsmSmsCbMessage {
    private static final char CARRIAGE_RETURN = '\r';
    private static final String[] LANGUAGE_CODES_GROUP_0 = {"de", "en", "it", "fr", "es", "nl", "sv", "da", "pt", "fi", "no", "el", "tr", "hu", "pl", null};
    private static final String[] LANGUAGE_CODES_GROUP_2 = {"cs", "he", "ar", "ru", "is", null, null, null, null, null, null, null, null, null, null, null};
    private static final int PDU_BODY_PAGE_LENGTH = 82;

    private GsmSmsCbMessage() {
    }

    public static SmsCbMessage createSmsCbMessage(Context context, SmsCbHeader smsCbHeader, SmsCbLocation smsCbLocation, byte[][] bArr) throws IllegalArgumentException {
        if (smsCbHeader.isEtwsPrimaryNotification()) {
            return new SmsCbMessage(1, smsCbHeader.getGeographicalScope(), smsCbHeader.getSerialNumber(), smsCbLocation, smsCbHeader.getServiceCategory(), null, getEtwsPrimaryMessage(context, smsCbHeader.getEtwsInfo().getWarningType()), 3, smsCbHeader.getEtwsInfo(), smsCbHeader.getCmasInfo());
        }
        StringBuilder sb = new StringBuilder();
        String str = null;
        for (byte[] bArr2 : bArr) {
            Pair<String, String> body = parseBody(smsCbHeader, bArr2);
            str = body.first;
            sb.append(body.second);
        }
        return new SmsCbMessage(1, smsCbHeader.getGeographicalScope(), smsCbHeader.getSerialNumber(), smsCbLocation, smsCbHeader.getServiceCategory(), str, sb.toString(), smsCbHeader.isEmergencyMessage() ? 3 : 0, smsCbHeader.getEtwsInfo(), smsCbHeader.getCmasInfo());
    }

    private static String getEtwsPrimaryMessage(Context context, int i) {
        Resources resources = context.getResources();
        switch (i) {
            case 0:
                return resources.getString(R.string.etws_primary_default_message_earthquake);
            case 1:
                return resources.getString(R.string.etws_primary_default_message_tsunami);
            case 2:
                return resources.getString(R.string.etws_primary_default_message_earthquake_and_tsunami);
            case 3:
                return resources.getString(R.string.etws_primary_default_message_test);
            case 4:
                return resources.getString(R.string.etws_primary_default_message_others);
            default:
                return "";
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:16:0x0029  */
    /* JADX WARN: Code duplicated, block: B:25:0x004b  */
    /* JADX WARN: Code duplicated, block: B:27:0x0054  */
    /* JADX WARN: Code duplicated, block: B:29:0x005b  */
    /* JADX WARN: Code duplicated, block: B:31:0x0067 A[LOOP:0: B:28:0x0059->B:31:0x0067, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:36:0x00a6  */
    /* JADX WARN: Code duplicated, block: B:38:0x00cb  */
    /* JADX WARN: Code duplicated, block: B:43:0x007d A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:7:0x0017 A[FALL_THROUGH, PHI: r5 r6
      0x0017: PHI (r5v3 int) = (r5v0 int), (r5v0 int), (r5v0 int), (r5v0 int), (r5v2 int), (r5v0 int), (r5v0 int) binds: [B:5:0x0011, B:22:0x003d, B:17:0x002c, B:13:0x0023, B:15:0x0027, B:6:0x0014, B:10:0x001d] A[DONT_GENERATE, DONT_INLINE]
      0x0017: PHI (r6v3 java.lang.String) = 
      (r6v0 java.lang.String)
      (r6v1 java.lang.String)
      (r6v2 java.lang.String)
      (r6v0 java.lang.String)
      (r6v0 java.lang.String)
      (r6v0 java.lang.String)
      (r6v0 java.lang.String)
     binds: [B:5:0x0011, B:22:0x003d, B:17:0x002c, B:13:0x0023, B:15:0x0027, B:6:0x0014, B:10:0x001d] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Instruction removed from duplicated block: B:36:0x00a6, please report this as an issue */
    private static Pair<String, String> parseBody(SmsCbHeader smsCbHeader, byte[] bArr) {
        boolean z;
        String str;
        byte b;
        StringBuilder sb;
        int i;
        byte b2;
        int dataCodingScheme = smsCbHeader.getDataCodingScheme();
        int i2 = (dataCodingScheme & 240) >> 4;
        if (i2 != 9) {
            int i3 = 1;
            String str2 = null;
            switch (i2) {
                case 0:
                    str2 = LANGUAGE_CODES_GROUP_0[dataCodingScheme & 15];
                    z = false;
                    str = str2;
                    if (!smsCbHeader.isUmtsFormat()) {
                        return unpackBody(bArr, i3, 6, bArr.length - 6, z, str);
                    }
                    b = bArr[6];
                    if (bArr.length < (b * 83) + 7) {
                        throw new IllegalArgumentException("Pdu length " + bArr.length + " does not match " + ((int) b) + " pages");
                    }
                    sb = new StringBuilder();
                    for (int i4 = 0; i4 < b; i4++) {
                        i = (i4 * 83) + 7;
                        b2 = bArr[i + 82];
                        if (b2 > 82) {
                            throw new IllegalArgumentException("Page length " + ((int) b2) + " exceeds maximum value 82");
                        }
                        Pair<String, String> pairUnpackBody = unpackBody(bArr, i3, i, b2, z, str);
                        str = pairUnpackBody.first;
                        sb.append(pairUnpackBody.second);
                    }
                    return new Pair<>(str, sb.toString());
                case 1:
                    if ((dataCodingScheme & 15) == 1) {
                        z = true;
                        str = null;
                        i3 = 3;
                    } else {
                        z = true;
                        str = str2;
                    }
                    if (!smsCbHeader.isUmtsFormat()) {
                        return unpackBody(bArr, i3, 6, bArr.length - 6, z, str);
                    }
                    b = bArr[6];
                    if (bArr.length < (b * 83) + 7) {
                        throw new IllegalArgumentException("Pdu length " + bArr.length + " does not match " + ((int) b) + " pages");
                    }
                    sb = new StringBuilder();
                    while (i4 < b) {
                        i = (i4 * 83) + 7;
                        b2 = bArr[i + 82];
                        if (b2 > 82) {
                            throw new IllegalArgumentException("Page length " + ((int) b2) + " exceeds maximum value 82");
                        }
                        Pair<String, String> pairUnpackBody2 = unpackBody(bArr, i3, i, b2, z, str);
                        str = pairUnpackBody2.first;
                        sb.append(pairUnpackBody2.second);
                    }
                    return new Pair<>(str, sb.toString());
                case 2:
                    str2 = LANGUAGE_CODES_GROUP_2[dataCodingScheme & 15];
                    z = false;
                    str = str2;
                    if (!smsCbHeader.isUmtsFormat()) {
                        return unpackBody(bArr, i3, 6, bArr.length - 6, z, str);
                    }
                    b = bArr[6];
                    if (bArr.length < (b * 83) + 7) {
                        throw new IllegalArgumentException("Pdu length " + bArr.length + " does not match " + ((int) b) + " pages");
                    }
                    sb = new StringBuilder();
                    while (i4 < b) {
                        i = (i4 * 83) + 7;
                        b2 = bArr[i + 82];
                        if (b2 > 82) {
                            throw new IllegalArgumentException("Page length " + ((int) b2) + " exceeds maximum value 82");
                        }
                        Pair<String, String> pairUnpackBody3 = unpackBody(bArr, i3, i, b2, z, str);
                        str = pairUnpackBody3.first;
                        sb.append(pairUnpackBody3.second);
                    }
                    return new Pair<>(str, sb.toString());
                case 3:
                    z = false;
                    str = str2;
                    if (!smsCbHeader.isUmtsFormat()) {
                        return unpackBody(bArr, i3, 6, bArr.length - 6, z, str);
                    }
                    b = bArr[6];
                    if (bArr.length < (b * 83) + 7) {
                        throw new IllegalArgumentException("Pdu length " + bArr.length + " does not match " + ((int) b) + " pages");
                    }
                    sb = new StringBuilder();
                    while (i4 < b) {
                        i = (i4 * 83) + 7;
                        b2 = bArr[i + 82];
                        if (b2 > 82) {
                            throw new IllegalArgumentException("Page length " + ((int) b2) + " exceeds maximum value 82");
                        }
                        Pair<String, String> pairUnpackBody4 = unpackBody(bArr, i3, i, b2, z, str);
                        str = pairUnpackBody4.first;
                        sb.append(pairUnpackBody4.second);
                    }
                    return new Pair<>(str, sb.toString());
                case 4:
                case 5:
                    switch ((dataCodingScheme & 12) >> 2) {
                        case 1:
                            z = false;
                            i3 = 2;
                            break;
                        case 2:
                            i3 = 3;
                        default:
                            z = false;
                            break;
                    }
                    str = str2;
                    if (!smsCbHeader.isUmtsFormat()) {
                        return unpackBody(bArr, i3, 6, bArr.length - 6, z, str);
                    }
                    b = bArr[6];
                    if (bArr.length < (b * 83) + 7) {
                        throw new IllegalArgumentException("Pdu length " + bArr.length + " does not match " + ((int) b) + " pages");
                    }
                    sb = new StringBuilder();
                    while (i4 < b) {
                        i = (i4 * 83) + 7;
                        b2 = bArr[i + 82];
                        if (b2 > 82) {
                            throw new IllegalArgumentException("Page length " + ((int) b2) + " exceeds maximum value 82");
                        }
                        Pair<String, String> pairUnpackBody5 = unpackBody(bArr, i3, i, b2, z, str);
                        str = pairUnpackBody5.first;
                        sb.append(pairUnpackBody5.second);
                    }
                    return new Pair<>(str, sb.toString());
                case 6:
                case 7:
                    break;
                default:
                    switch (i2) {
                        case 14:
                            break;
                        case 15:
                            if (((dataCodingScheme & 4) >> 2) == 1) {
                                z = false;
                                i3 = 2;
                            }
                            str = str2;
                            if (!smsCbHeader.isUmtsFormat()) {
                                return unpackBody(bArr, i3, 6, bArr.length - 6, z, str);
                            }
                            b = bArr[6];
                            if (bArr.length < (b * 83) + 7) {
                                throw new IllegalArgumentException("Pdu length " + bArr.length + " does not match " + ((int) b) + " pages");
                            }
                            sb = new StringBuilder();
                            while (i4 < b) {
                                i = (i4 * 83) + 7;
                                b2 = bArr[i + 82];
                                if (b2 > 82) {
                                    throw new IllegalArgumentException("Page length " + ((int) b2) + " exceeds maximum value 82");
                                }
                                Pair<String, String> pairUnpackBody6 = unpackBody(bArr, i3, i, b2, z, str);
                                str = pairUnpackBody6.first;
                                sb.append(pairUnpackBody6.second);
                            }
                            return new Pair<>(str, sb.toString());
                        default:
                            z = false;
                            str = str2;
                            if (!smsCbHeader.isUmtsFormat()) {
                                return unpackBody(bArr, i3, 6, bArr.length - 6, z, str);
                            }
                            b = bArr[6];
                            if (bArr.length < (b * 83) + 7) {
                                throw new IllegalArgumentException("Pdu length " + bArr.length + " does not match " + ((int) b) + " pages");
                            }
                            sb = new StringBuilder();
                            while (i4 < b) {
                                i = (i4 * 83) + 7;
                                b2 = bArr[i + 82];
                                if (b2 > 82) {
                                    throw new IllegalArgumentException("Page length " + ((int) b2) + " exceeds maximum value 82");
                                }
                                Pair<String, String> pairUnpackBody7 = unpackBody(bArr, i3, i, b2, z, str);
                                str = pairUnpackBody7.first;
                                sb.append(pairUnpackBody7.second);
                            }
                            return new Pair<>(str, sb.toString());
                    }
                    break;
            }
        }
        throw new IllegalArgumentException("Unsupported GSM dataCodingScheme " + dataCodingScheme);
    }

    private static Pair<String, String> unpackBody(byte[] bArr, int i, int i2, int i3, boolean z, String str) {
        String strGsm7BitPackedToString;
        int i4;
        if (i == 1) {
            strGsm7BitPackedToString = GsmAlphabet.gsm7BitPackedToString(bArr, i2, (i3 * 8) / 7);
            if (z && strGsm7BitPackedToString != null && strGsm7BitPackedToString.length() > 2) {
                str = strGsm7BitPackedToString.substring(0, 2);
                strGsm7BitPackedToString = strGsm7BitPackedToString.substring(3);
            }
        } else if (i != 3) {
            strGsm7BitPackedToString = null;
        } else {
            if (z && bArr.length >= (i4 = i2 + 2)) {
                i3 -= 2;
                str = GsmAlphabet.gsm7BitPackedToString(bArr, i2, 2);
                i2 = i4;
            }
            try {
                strGsm7BitPackedToString = new String(bArr, i2, i3 & Configuration.DENSITY_DPI_ANY, "utf-16");
            } catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException("Error decoding UTF-16 message", e);
            }
        }
        if (strGsm7BitPackedToString != null) {
            for (int length = strGsm7BitPackedToString.length() - 1; length >= 0; length--) {
                if (strGsm7BitPackedToString.charAt(length) != '\r') {
                    strGsm7BitPackedToString = strGsm7BitPackedToString.substring(0, length + 1);
                    break;
                }
            }
        } else {
            strGsm7BitPackedToString = "";
        }
        return new Pair<>(str, strGsm7BitPackedToString);
    }
}
