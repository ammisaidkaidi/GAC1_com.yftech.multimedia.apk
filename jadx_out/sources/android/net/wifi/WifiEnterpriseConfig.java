package android.net.wifi;

import android.net.wifi.hotspot2.pps.Credential;
import android.os.Parcel;
import android.os.Parcelable;
import android.security.Credentials;
import android.telecom.Logging.Session;
import android.text.TextUtils;
import android.util.Log;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.conn.ssl.SSLSocketFactory;

/* JADX INFO: loaded from: classes.dex */
public class WifiEnterpriseConfig implements Parcelable {
    public static final String CA_CERT_ALIAS_DELIMITER = " ";
    public static final String CA_CERT_PREFIX = "keystore://CACERT_";
    public static final String CLIENT_CERT_PREFIX = "keystore://USRCERT_";
    public static final String EAP_KEY = "eap";
    public static final String EMPTY_VALUE = "NULL";
    public static final String ENGINE_DISABLE = "0";
    public static final String ENGINE_ENABLE = "1";
    public static final String ENGINE_ID_KEYSTORE = "keystore";
    public static final String ENGINE_KEY = "engine";
    public static final String KEYSTORES_URI = "keystores://";
    public static final String KEYSTORE_URI = "keystore://";
    public static final String PASSWORD_KEY = "password";
    public static final String PHASE2_KEY = "phase2";
    public static final String PLMN_KEY = "plmn";
    public static final String REALM_KEY = "realm";
    private static final String TAG = "WifiEnterpriseConfig";
    private X509Certificate[] mCaCerts;
    private X509Certificate[] mClientCertificateChain;
    private PrivateKey mClientPrivateKey;
    public static final String IDENTITY_KEY = "identity";
    public static final String ANON_IDENTITY_KEY = "anonymous_identity";
    public static final String CLIENT_CERT_KEY = "client_cert";
    public static final String CA_CERT_KEY = "ca_cert";
    public static final String SUBJECT_MATCH_KEY = "subject_match";
    public static final String ENGINE_ID_KEY = "engine_id";
    public static final String PRIVATE_KEY_ID_KEY = "key_id";
    public static final String ALTSUBJECT_MATCH_KEY = "altsubject_match";
    public static final String DOM_SUFFIX_MATCH_KEY = "domain_suffix_match";
    public static final String CA_PATH_KEY = "ca_path";
    private static final String[] SUPPLICANT_CONFIG_KEYS = {IDENTITY_KEY, ANON_IDENTITY_KEY, "password", CLIENT_CERT_KEY, CA_CERT_KEY, SUBJECT_MATCH_KEY, "engine", ENGINE_ID_KEY, PRIVATE_KEY_ID_KEY, ALTSUBJECT_MATCH_KEY, DOM_SUFFIX_MATCH_KEY, CA_PATH_KEY};
    public static final String OPP_KEY_CACHING = "proactive_key_caching";
    private static final List<String> UNQUOTED_KEYS = Arrays.asList("engine", OPP_KEY_CACHING);
    public static final Parcelable.Creator<WifiEnterpriseConfig> CREATOR = new Parcelable.Creator<WifiEnterpriseConfig>() { // from class: android.net.wifi.WifiEnterpriseConfig.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WifiEnterpriseConfig createFromParcel(Parcel parcel) {
            WifiEnterpriseConfig wifiEnterpriseConfig = new WifiEnterpriseConfig();
            int i = parcel.readInt();
            for (int i2 = 0; i2 < i; i2++) {
                wifiEnterpriseConfig.mFields.put(parcel.readString(), parcel.readString());
            }
            wifiEnterpriseConfig.mEapMethod = parcel.readInt();
            wifiEnterpriseConfig.mPhase2Method = parcel.readInt();
            wifiEnterpriseConfig.mCaCerts = ParcelUtil.readCertificates(parcel);
            wifiEnterpriseConfig.mClientPrivateKey = ParcelUtil.readPrivateKey(parcel);
            wifiEnterpriseConfig.mClientCertificateChain = ParcelUtil.readCertificates(parcel);
            return wifiEnterpriseConfig;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WifiEnterpriseConfig[] newArray(int i) {
            return new WifiEnterpriseConfig[i];
        }
    };
    private HashMap<String, String> mFields = new HashMap<>();
    private int mEapMethod = -1;
    private int mPhase2Method = 0;

    public static final class Eap {
        public static final int AKA = 5;
        public static final int AKA_PRIME = 6;
        public static final int NONE = -1;
        public static final int PEAP = 0;
        public static final int PWD = 3;
        public static final int SIM = 4;
        public static final int TLS = 1;
        public static final int TTLS = 2;
        public static final int UNAUTH_TLS = 7;
        public static final String[] strings = {"PEAP", SSLSocketFactory.TLS, "TTLS", "PWD", "SIM", "AKA", "AKA'", "WFA-UNAUTH-TLS"};

        private Eap() {
        }
    }

    public static final class Phase2 {
        public static final int AKA = 6;
        public static final int AKA_PRIME = 7;
        private static final String AUTHEAP_PREFIX = "autheap=";
        private static final String AUTH_PREFIX = "auth=";
        public static final int GTC = 4;
        public static final int MSCHAP = 2;
        public static final int MSCHAPV2 = 3;
        public static final int NONE = 0;
        public static final int PAP = 1;
        public static final int SIM = 5;
        public static final String[] strings = {WifiEnterpriseConfig.EMPTY_VALUE, Credential.UserCredential.AUTH_METHOD_PAP, "MSCHAP", "MSCHAPV2", "GTC", "SIM", "AKA", "AKA'"};

        private Phase2() {
        }
    }

    public interface SupplicantLoader {
        String loadValue(String str);
    }

    public interface SupplicantSaver {
        boolean saveValue(String str, String str2);
    }

    public WifiEnterpriseConfig() {
    }

    public WifiEnterpriseConfig(WifiEnterpriseConfig wifiEnterpriseConfig) {
        copyFrom(wifiEnterpriseConfig, false, "");
    }

    private String convertToQuotedString(String str) {
        return "\"" + str + "\"";
    }

    private void copyFrom(WifiEnterpriseConfig wifiEnterpriseConfig, boolean z, String str) {
        for (String str2 : wifiEnterpriseConfig.mFields.keySet()) {
            if (!z || !str2.equals("password") || !TextUtils.equals(wifiEnterpriseConfig.mFields.get(str2), str)) {
                this.mFields.put(str2, wifiEnterpriseConfig.mFields.get(str2));
            }
        }
        if (wifiEnterpriseConfig.mCaCerts != null) {
            this.mCaCerts = (X509Certificate[]) Arrays.copyOf(wifiEnterpriseConfig.mCaCerts, wifiEnterpriseConfig.mCaCerts.length);
        } else {
            this.mCaCerts = null;
        }
        this.mClientPrivateKey = wifiEnterpriseConfig.mClientPrivateKey;
        if (wifiEnterpriseConfig.mClientCertificateChain != null) {
            this.mClientCertificateChain = (X509Certificate[]) Arrays.copyOf(wifiEnterpriseConfig.mClientCertificateChain, wifiEnterpriseConfig.mClientCertificateChain.length);
        } else {
            this.mClientCertificateChain = null;
        }
        this.mEapMethod = wifiEnterpriseConfig.mEapMethod;
        this.mPhase2Method = wifiEnterpriseConfig.mPhase2Method;
    }

    public static String decodeCaCertificateAlias(String str) {
        byte[] bArr = new byte[str.length() >> 1];
        int i = 0;
        int i2 = 0;
        while (i < str.length()) {
            int i3 = i + 2;
            bArr[i2] = (byte) Integer.parseInt(str.substring(i, i3), 16);
            i2++;
            i = i3;
        }
        try {
            return new String(bArr, StandardCharsets.UTF_8);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return str;
        }
    }

    public static String encodeCaCertificateAlias(String str) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", Integer.valueOf(b & 255)));
        }
        return sb.toString();
    }

    private String getFieldValue(String str, String str2) {
        String str3 = this.mFields.get(str);
        if (TextUtils.isEmpty(str3) || EMPTY_VALUE.equals(str3)) {
            return "";
        }
        String strRemoveDoubleQuotes = removeDoubleQuotes(str3);
        return strRemoveDoubleQuotes.startsWith(str2) ? strRemoveDoubleQuotes.substring(str2.length()) : strRemoveDoubleQuotes;
    }

    private int getStringIndex(String[] strArr, String str, int i) {
        if (TextUtils.isEmpty(str)) {
            return i;
        }
        for (int i2 = 0; i2 < strArr.length; i2++) {
            if (str.equals(strArr[i2])) {
                return i2;
            }
        }
        return i;
    }

    private boolean isEapMethodValid() {
        if (this.mEapMethod == -1) {
            Log.e(TAG, "WiFi enterprise configuration is invalid as it supplies no EAP method.");
            return false;
        }
        if (this.mEapMethod < 0 || this.mEapMethod >= Eap.strings.length) {
            Log.e(TAG, "mEapMethod is invald for WiFi enterprise configuration: " + this.mEapMethod);
            return false;
        }
        if (this.mPhase2Method >= 0 && this.mPhase2Method < Phase2.strings.length) {
            return true;
        }
        Log.e(TAG, "mPhase2Method is invald for WiFi enterprise configuration: " + this.mPhase2Method);
        return false;
    }

    private String removeDoubleQuotes(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        int length = str.length();
        if (length > 1 && str.charAt(0) == '\"') {
            int i = length - 1;
            if (str.charAt(i) == '\"') {
                return str.substring(1, i);
            }
        }
        return str;
    }

    private void setFieldValue(String str, String str2, String str3) {
        String strConvertToQuotedString;
        if (TextUtils.isEmpty(str2)) {
            this.mFields.put(str, EMPTY_VALUE);
            return;
        }
        if (UNQUOTED_KEYS.contains(str)) {
            strConvertToQuotedString = str3 + str2;
        } else {
            strConvertToQuotedString = convertToQuotedString(str3 + str2);
        }
        this.mFields.put(str, strConvertToQuotedString);
    }

    public void copyFromExternal(WifiEnterpriseConfig wifiEnterpriseConfig, String str) {
        copyFrom(wifiEnterpriseConfig, true, convertToQuotedString(str));
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getAltSubjectMatch() {
        return getFieldValue(ALTSUBJECT_MATCH_KEY);
    }

    public String getAnonymousIdentity() {
        return getFieldValue(ANON_IDENTITY_KEY);
    }

    public X509Certificate getCaCertificate() {
        if (this.mCaCerts == null || this.mCaCerts.length <= 0) {
            return null;
        }
        return this.mCaCerts[0];
    }

    public String getCaCertificateAlias() {
        return getFieldValue(CA_CERT_KEY, CA_CERT_PREFIX);
    }

    public String[] getCaCertificateAliases() {
        String fieldValue = getFieldValue(CA_CERT_KEY);
        if (fieldValue.startsWith(CA_CERT_PREFIX)) {
            return new String[]{getFieldValue(CA_CERT_KEY, CA_CERT_PREFIX)};
        }
        if (!fieldValue.startsWith(KEYSTORES_URI)) {
            if (TextUtils.isEmpty(fieldValue)) {
                return null;
            }
            return new String[]{fieldValue};
        }
        String[] strArrSplit = TextUtils.split(fieldValue.substring(KEYSTORES_URI.length()), CA_CERT_ALIAS_DELIMITER);
        for (int i = 0; i < strArrSplit.length; i++) {
            strArrSplit[i] = decodeCaCertificateAlias(strArrSplit[i]);
            if (strArrSplit[i].startsWith(Credentials.CA_CERTIFICATE)) {
                strArrSplit[i] = strArrSplit[i].substring(Credentials.CA_CERTIFICATE.length());
            }
        }
        if (strArrSplit.length != 0) {
            return strArrSplit;
        }
        return null;
    }

    public X509Certificate[] getCaCertificates() {
        if (this.mCaCerts == null || this.mCaCerts.length <= 0) {
            return null;
        }
        return this.mCaCerts;
    }

    public String getCaPath() {
        return getFieldValue(CA_PATH_KEY);
    }

    public X509Certificate getClientCertificate() {
        if (this.mClientCertificateChain == null || this.mClientCertificateChain.length <= 0) {
            return null;
        }
        return this.mClientCertificateChain[0];
    }

    public String getClientCertificateAlias() {
        return getFieldValue(CLIENT_CERT_KEY, CLIENT_CERT_PREFIX);
    }

    public X509Certificate[] getClientCertificateChain() {
        if (this.mClientCertificateChain == null || this.mClientCertificateChain.length <= 0) {
            return null;
        }
        return this.mClientCertificateChain;
    }

    public PrivateKey getClientPrivateKey() {
        return this.mClientPrivateKey;
    }

    public String getDomainSuffixMatch() {
        return getFieldValue(DOM_SUFFIX_MATCH_KEY);
    }

    public int getEapMethod() {
        return this.mEapMethod;
    }

    public String getFieldValue(String str) {
        return getFieldValue(str, "");
    }

    public String getIdentity() {
        return getFieldValue(IDENTITY_KEY);
    }

    public String getKeyId(WifiEnterpriseConfig wifiEnterpriseConfig) {
        if (this.mEapMethod == -1) {
            return wifiEnterpriseConfig != null ? wifiEnterpriseConfig.getKeyId(null) : EMPTY_VALUE;
        }
        if (!isEapMethodValid()) {
            return EMPTY_VALUE;
        }
        return Eap.strings[this.mEapMethod] + Session.SESSION_SEPARATION_CHAR_CHILD + Phase2.strings[this.mPhase2Method];
    }

    public String getPassword() {
        return getFieldValue("password");
    }

    public int getPhase2Method() {
        return this.mPhase2Method;
    }

    public String getPlmn() {
        return getFieldValue("plmn");
    }

    public String getRealm() {
        return getFieldValue(REALM_KEY);
    }

    public String getSubjectMatch() {
        return getFieldValue(SUBJECT_MATCH_KEY);
    }

    public void loadFromSupplicant(SupplicantLoader supplicantLoader) {
        for (String str : SUPPLICANT_CONFIG_KEYS) {
            String strLoadValue = supplicantLoader.loadValue(str);
            if (strLoadValue == null) {
                this.mFields.put(str, EMPTY_VALUE);
            } else {
                this.mFields.put(str, strLoadValue);
            }
        }
        this.mEapMethod = getStringIndex(Eap.strings, supplicantLoader.loadValue(EAP_KEY), -1);
        String strRemoveDoubleQuotes = removeDoubleQuotes(supplicantLoader.loadValue(PHASE2_KEY));
        if (strRemoveDoubleQuotes.startsWith("auth=")) {
            strRemoveDoubleQuotes = strRemoveDoubleQuotes.substring("auth=".length());
        } else if (strRemoveDoubleQuotes.startsWith("autheap=")) {
            strRemoveDoubleQuotes = strRemoveDoubleQuotes.substring("autheap=".length());
        }
        this.mPhase2Method = getStringIndex(Phase2.strings, strRemoveDoubleQuotes, 0);
    }

    public void resetCaCertificate() {
        this.mCaCerts = null;
    }

    public void resetClientKeyEntry() {
        this.mClientPrivateKey = null;
        this.mClientCertificateChain = null;
    }

    public boolean saveToSupplicant(SupplicantSaver supplicantSaver) {
        boolean z = false;
        if (!isEapMethodValid()) {
            return false;
        }
        boolean z2 = this.mEapMethod == 4 || this.mEapMethod == 5 || this.mEapMethod == 6;
        for (String str : this.mFields.keySet()) {
            if (!z2 || !ANON_IDENTITY_KEY.equals(str)) {
                if (!supplicantSaver.saveValue(str, this.mFields.get(str))) {
                    return false;
                }
            }
        }
        if (!supplicantSaver.saveValue(EAP_KEY, Eap.strings[this.mEapMethod])) {
            return false;
        }
        if (this.mEapMethod == 1 || this.mPhase2Method == 0) {
            if (this.mPhase2Method == 0) {
                return supplicantSaver.saveValue(PHASE2_KEY, null);
            }
            Log.e(TAG, "WiFi enterprise configuration is invalid as it supplies a phase 2 method but the phase1 method does not support it.");
            return false;
        }
        if (this.mEapMethod == 2 && this.mPhase2Method == 4) {
            z = true;
        }
        return supplicantSaver.saveValue(PHASE2_KEY, convertToQuotedString((z ? "autheap=" : "auth=") + Phase2.strings[this.mPhase2Method]));
    }

    public void setAltSubjectMatch(String str) {
        setFieldValue(ALTSUBJECT_MATCH_KEY, str);
    }

    public void setAnonymousIdentity(String str) {
        setFieldValue(ANON_IDENTITY_KEY, str);
    }

    public void setCaCertificate(X509Certificate x509Certificate) {
        if (x509Certificate == null) {
            this.mCaCerts = null;
        } else {
            if (x509Certificate.getBasicConstraints() < 0) {
                throw new IllegalArgumentException("Not a CA certificate");
            }
            this.mCaCerts = new X509Certificate[]{x509Certificate};
        }
    }

    public void setCaCertificateAlias(String str) {
        setFieldValue(CA_CERT_KEY, str, CA_CERT_PREFIX);
    }

    public void setCaCertificateAliases(String[] strArr) {
        if (strArr == null) {
            setFieldValue(CA_CERT_KEY, null, CA_CERT_PREFIX);
            return;
        }
        if (strArr.length == 1) {
            setCaCertificateAlias(strArr[0]);
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strArr.length; i++) {
            if (i > 0) {
                sb.append(CA_CERT_ALIAS_DELIMITER);
            }
            sb.append(encodeCaCertificateAlias(Credentials.CA_CERTIFICATE + strArr[i]));
        }
        setFieldValue(CA_CERT_KEY, sb.toString(), KEYSTORES_URI);
    }

    public void setCaCertificates(X509Certificate[] x509CertificateArr) {
        if (x509CertificateArr == null) {
            this.mCaCerts = null;
            return;
        }
        X509Certificate[] x509CertificateArr2 = new X509Certificate[x509CertificateArr.length];
        for (int i = 0; i < x509CertificateArr.length; i++) {
            if (x509CertificateArr[i].getBasicConstraints() < 0) {
                throw new IllegalArgumentException("Not a CA certificate");
            }
            x509CertificateArr2[i] = x509CertificateArr[i];
        }
        this.mCaCerts = x509CertificateArr2;
    }

    public void setCaPath(String str) {
        setFieldValue(CA_PATH_KEY, str);
    }

    public void setClientCertificateAlias(String str) {
        setFieldValue(CLIENT_CERT_KEY, str, CLIENT_CERT_PREFIX);
        setFieldValue(PRIVATE_KEY_ID_KEY, str, Credentials.USER_PRIVATE_KEY);
        if (TextUtils.isEmpty(str)) {
            setFieldValue("engine", "0");
            setFieldValue(ENGINE_ID_KEY, "");
        } else {
            setFieldValue("engine", "1");
            setFieldValue(ENGINE_ID_KEY, ENGINE_ID_KEYSTORE);
        }
    }

    public void setClientKeyEntry(PrivateKey privateKey, X509Certificate x509Certificate) {
        setClientKeyEntryWithCertificateChain(privateKey, x509Certificate != null ? new X509Certificate[]{x509Certificate} : null);
    }

    public void setClientKeyEntryWithCertificateChain(PrivateKey privateKey, X509Certificate[] x509CertificateArr) {
        X509Certificate[] x509CertificateArr2;
        if (x509CertificateArr == null || x509CertificateArr.length <= 0) {
            x509CertificateArr2 = null;
        } else {
            if (x509CertificateArr[0].getBasicConstraints() != -1) {
                throw new IllegalArgumentException("First certificate in the chain must be a client end certificate");
            }
            for (int i = 1; i < x509CertificateArr.length; i++) {
                if (x509CertificateArr[i].getBasicConstraints() == -1) {
                    throw new IllegalArgumentException("All certificates following the first must be CA certificates");
                }
            }
            x509CertificateArr2 = (X509Certificate[]) Arrays.copyOf(x509CertificateArr, x509CertificateArr.length);
            if (privateKey == null) {
                throw new IllegalArgumentException("Client cert without a private key");
            }
            if (privateKey.getEncoded() == null) {
                throw new IllegalArgumentException("Private key cannot be encoded");
            }
        }
        this.mClientPrivateKey = privateKey;
        this.mClientCertificateChain = x509CertificateArr2;
    }

    public void setDomainSuffixMatch(String str) {
        setFieldValue(DOM_SUFFIX_MATCH_KEY, str);
    }

    public void setEapMethod(int i) {
        switch (i) {
            case 0:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                break;
            case 1:
            case 7:
                setPhase2Method(0);
                break;
            default:
                throw new IllegalArgumentException("Unknown EAP method");
        }
        this.mEapMethod = i;
        setFieldValue(OPP_KEY_CACHING, "1");
    }

    public void setFieldValue(String str, String str2) {
        setFieldValue(str, str2, "");
    }

    public void setIdentity(String str) {
        setFieldValue(IDENTITY_KEY, str, "");
    }

    public void setPassword(String str) {
        setFieldValue("password", str);
    }

    public void setPhase2Method(int i) {
        switch (i) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                this.mPhase2Method = i;
                return;
            default:
                throw new IllegalArgumentException("Unknown Phase 2 method");
        }
    }

    public void setPlmn(String str) {
        setFieldValue("plmn", str);
    }

    public void setRealm(String str) {
        setFieldValue(REALM_KEY, str);
    }

    public void setSubjectMatch(String str) {
        setFieldValue(SUBJECT_MATCH_KEY, str);
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        for (String str : this.mFields.keySet()) {
            String str2 = "password".equals(str) ? "<removed>" : this.mFields.get(str);
            stringBuffer.append(str);
            stringBuffer.append(CA_CERT_ALIAS_DELIMITER);
            stringBuffer.append(str2);
            stringBuffer.append("\n");
        }
        return stringBuffer.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mFields.size());
        for (Map.Entry<String, String> entry : this.mFields.entrySet()) {
            parcel.writeString(entry.getKey());
            parcel.writeString(entry.getValue());
        }
        parcel.writeInt(this.mEapMethod);
        parcel.writeInt(this.mPhase2Method);
        ParcelUtil.writeCertificates(parcel, this.mCaCerts);
        ParcelUtil.writePrivateKey(parcel, this.mClientPrivateKey);
        ParcelUtil.writeCertificates(parcel, this.mClientCertificateChain);
    }
}
