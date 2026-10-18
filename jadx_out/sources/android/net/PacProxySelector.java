package android.net;

import android.net.wifi.WifiEnterpriseConfig;
import android.os.ServiceManager;
import android.provider.SettingsStringUtil;
import android.util.Log;
import com.android.net.IProxyService;
import com.google.android.collect.Lists;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class PacProxySelector extends ProxySelector {
    private static final String PROXY = "PROXY ";
    public static final String PROXY_SERVICE = "com.android.net.IProxyService";
    private static final String SOCKS = "SOCKS ";
    private static final String TAG = "PacProxySelector";
    private final List<java.net.Proxy> mDefaultList;
    private IProxyService mProxyService = IProxyService.Stub.asInterface(ServiceManager.getService(PROXY_SERVICE));

    public PacProxySelector() {
        if (this.mProxyService == null) {
            Log.e(TAG, "PacManager: no proxy service");
        }
        this.mDefaultList = Lists.newArrayList(java.net.Proxy.NO_PROXY);
    }

    private static List<java.net.Proxy> parseResponse(String str) {
        java.net.Proxy proxyProxyFromHostPort;
        String[] strArrSplit = str.split(";");
        ArrayList arrayListNewArrayList = Lists.newArrayList();
        for (String str2 : strArrSplit) {
            String strTrim = str2.trim();
            if (strTrim.equals("DIRECT")) {
                arrayListNewArrayList.add(java.net.Proxy.NO_PROXY);
            } else if (strTrim.startsWith(PROXY)) {
                java.net.Proxy proxyProxyFromHostPort2 = proxyFromHostPort(java.net.Proxy.Type.HTTP, strTrim.substring(PROXY.length()));
                if (proxyProxyFromHostPort2 != null) {
                    arrayListNewArrayList.add(proxyProxyFromHostPort2);
                }
            } else if (strTrim.startsWith(SOCKS) && (proxyProxyFromHostPort = proxyFromHostPort(java.net.Proxy.Type.SOCKS, strTrim.substring(SOCKS.length()))) != null) {
                arrayListNewArrayList.add(proxyProxyFromHostPort);
            }
        }
        if (arrayListNewArrayList.size() == 0) {
            arrayListNewArrayList.add(java.net.Proxy.NO_PROXY);
        }
        return arrayListNewArrayList;
    }

    private static java.net.Proxy proxyFromHostPort(java.net.Proxy.Type type, String str) {
        try {
            String[] strArrSplit = str.split(SettingsStringUtil.DELIMITER);
            return new java.net.Proxy(type, InetSocketAddress.createUnresolved(strArrSplit[0], Integer.parseInt(strArrSplit[1])));
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            Log.d(TAG, "Unable to parse proxy " + str + WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + e);
            return null;
        }
    }

    @Override // java.net.ProxySelector
    public void connectFailed(URI uri, SocketAddress socketAddress, IOException iOException) {
    }

    @Override // java.net.ProxySelector
    public List<java.net.Proxy> select(URI uri) {
        String host;
        String strResolvePacFile;
        if (this.mProxyService == null) {
            this.mProxyService = IProxyService.Stub.asInterface(ServiceManager.getService(PROXY_SERVICE));
        }
        if (this.mProxyService == null) {
            Log.e(TAG, "select: no proxy service return NO_PROXY");
            return Lists.newArrayList(java.net.Proxy.NO_PROXY);
        }
        try {
            if (!"http".equalsIgnoreCase(uri.getScheme())) {
                uri = new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), "/", null, null);
            }
            host = uri.toURL().toString();
        } catch (MalformedURLException unused) {
            host = uri.getHost();
        } catch (URISyntaxException unused2) {
            host = uri.getHost();
        }
        try {
            strResolvePacFile = this.mProxyService.resolvePacFile(uri.getHost(), host);
        } catch (Exception e) {
            Log.e(TAG, "Error resolving PAC File", e);
            strResolvePacFile = null;
        }
        return strResolvePacFile == null ? this.mDefaultList : parseResponse(strResolvePacFile);
    }
}
