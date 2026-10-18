package android.security.net.config;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;
import android.util.Pair;
import java.util.Set;

/* JADX INFO: loaded from: classes2.dex */
public class ManifestConfigSource implements ConfigSource {
    private static final boolean DBG = true;
    private static final String LOG_TAG = "NetworkSecurityConfig";
    private final ApplicationInfo mApplicationInfo;
    private ConfigSource mConfigSource;
    private final Context mContext;
    private final Object mLock = new Object();

    private static final class DefaultConfigSource implements ConfigSource {
        private final NetworkSecurityConfig mDefaultConfig;

        DefaultConfigSource(boolean z, ApplicationInfo applicationInfo) {
            this.mDefaultConfig = NetworkSecurityConfig.getDefaultBuilder(applicationInfo).setCleartextTrafficPermitted(z).build();
        }

        @Override // android.security.net.config.ConfigSource
        public NetworkSecurityConfig getDefaultConfig() {
            return this.mDefaultConfig;
        }

        @Override // android.security.net.config.ConfigSource
        public Set<Pair<Domain, NetworkSecurityConfig>> getPerDomainConfigs() {
            return null;
        }
    }

    public ManifestConfigSource(Context context) {
        this.mContext = context;
        this.mApplicationInfo = new ApplicationInfo(context.getApplicationInfo());
    }

    private ConfigSource getConfigSource() {
        ConfigSource defaultConfigSource;
        synchronized (this.mLock) {
            if (this.mConfigSource != null) {
                return this.mConfigSource;
            }
            int i = this.mApplicationInfo.networkSecurityConfigRes;
            boolean z = false;
            if (i != 0) {
                Log.d(LOG_TAG, "Using Network Security Config from resource " + this.mContext.getResources().getResourceEntryName(i) + " debugBuild: " + ((2 & this.mApplicationInfo.flags) != 0));
                defaultConfigSource = new XmlConfigSource(this.mContext, i, this.mApplicationInfo);
            } else {
                Log.d(LOG_TAG, "No Network Security Config specified, using platform default");
                if ((this.mApplicationInfo.flags & 134217728) != 0 && this.mApplicationInfo.targetSandboxVersion < 2) {
                    z = true;
                }
                defaultConfigSource = new DefaultConfigSource(z, this.mApplicationInfo);
            }
            this.mConfigSource = defaultConfigSource;
            return this.mConfigSource;
        }
    }

    @Override // android.security.net.config.ConfigSource
    public NetworkSecurityConfig getDefaultConfig() {
        return getConfigSource().getDefaultConfig();
    }

    @Override // android.security.net.config.ConfigSource
    public Set<Pair<Domain, NetworkSecurityConfig>> getPerDomainConfigs() {
        return getConfigSource().getPerDomainConfigs();
    }
}
