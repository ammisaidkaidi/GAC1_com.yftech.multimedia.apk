package android.net;

import android.annotation.SystemApi;
import android.provider.SettingsStringUtil;
import com.yftech.vehicle.internal.adapter.ICarInfo;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public class WebAddress {
    static final int MATCH_GROUP_AUTHORITY = 2;
    static final int MATCH_GROUP_HOST = 3;
    static final int MATCH_GROUP_PATH = 5;
    static final int MATCH_GROUP_PORT = 4;
    static final int MATCH_GROUP_SCHEME = 1;
    static Pattern sAddressPattern = Pattern.compile("(?:(http|https|file)\\:\\/\\/)?(?:([-A-Za-z0-9$_.+!*'(),;?&=]+(?:\\:[-A-Za-z0-9$_.+!*'(),;?&=]+)?)@)?([a-zA-Z0-9 -\ud7ff豈-﷏ﷰ-\uffef%_-][a-zA-Z0-9 -\ud7ff豈-﷏ﷰ-\uffef%_\\.-]*|\\[[0-9a-fA-F:\\.]+\\])?(?:\\:([0-9]*))?(\\/?[^#]*)?.*", 2);
    private String mAuthInfo;
    private String mHost;
    private String mPath;
    private int mPort;
    private String mScheme;

    public WebAddress(String str) throws ParseException {
        if (str == null) {
            throw new NullPointerException();
        }
        this.mScheme = "";
        this.mHost = "";
        this.mPort = -1;
        this.mPath = "/";
        this.mAuthInfo = "";
        Matcher matcher = sAddressPattern.matcher(str);
        if (!matcher.matches()) {
            throw new ParseException("Bad address");
        }
        String strGroup = matcher.group(1);
        if (strGroup != null) {
            this.mScheme = strGroup.toLowerCase(Locale.ROOT);
        }
        String strGroup2 = matcher.group(2);
        if (strGroup2 != null) {
            this.mAuthInfo = strGroup2;
        }
        String strGroup3 = matcher.group(3);
        if (strGroup3 != null) {
            this.mHost = strGroup3;
        }
        String strGroup4 = matcher.group(4);
        if (strGroup4 != null && strGroup4.length() > 0) {
            try {
                this.mPort = Integer.parseInt(strGroup4);
            } catch (NumberFormatException unused) {
                throw new ParseException("Bad port");
            }
        }
        String strGroup5 = matcher.group(5);
        if (strGroup5 != null && strGroup5.length() > 0) {
            if (strGroup5.charAt(0) == '/') {
                this.mPath = strGroup5;
            } else {
                this.mPath = "/" + strGroup5;
            }
        }
        if (this.mPort == 443 && this.mScheme.equals("")) {
            this.mScheme = "https";
        } else if (this.mPort == -1) {
            if (this.mScheme.equals("https")) {
                this.mPort = ICarInfo.ENGINE_IGN_ANGLE_STATE;
            } else {
                this.mPort = 80;
            }
        }
        if (this.mScheme.equals("")) {
            this.mScheme = "http";
        }
    }

    public String getAuthInfo() {
        return this.mAuthInfo;
    }

    public String getHost() {
        return this.mHost;
    }

    public String getPath() {
        return this.mPath;
    }

    public int getPort() {
        return this.mPort;
    }

    public String getScheme() {
        return this.mScheme;
    }

    public void setAuthInfo(String str) {
        this.mAuthInfo = str;
    }

    public void setHost(String str) {
        this.mHost = str;
    }

    public void setPath(String str) {
        this.mPath = str;
    }

    public void setPort(int i) {
        this.mPort = i;
    }

    public void setScheme(String str) {
        this.mScheme = str;
    }

    public String toString() {
        String str = "";
        if ((this.mPort != 443 && this.mScheme.equals("https")) || (this.mPort != 80 && this.mScheme.equals("http"))) {
            str = SettingsStringUtil.DELIMITER + Integer.toString(this.mPort);
        }
        String str2 = "";
        if (this.mAuthInfo.length() > 0) {
            str2 = this.mAuthInfo + "@";
        }
        return this.mScheme + "://" + str2 + this.mHost + str + this.mPath;
    }
}
