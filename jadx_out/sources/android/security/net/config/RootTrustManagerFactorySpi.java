package android.security.net.config;

import com.android.internal.annotations.VisibleForTesting;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactorySpi;

/* JADX INFO: loaded from: classes2.dex */
public class RootTrustManagerFactorySpi extends TrustManagerFactorySpi {
    private ApplicationConfig mApplicationConfig;
    private NetworkSecurityConfig mConfig;

    @VisibleForTesting
    public static final class ApplicationConfigParameters implements ManagerFactoryParameters {
        public final ApplicationConfig config;

        public ApplicationConfigParameters(ApplicationConfig applicationConfig) {
            this.config = applicationConfig;
        }
    }

    @Override // javax.net.ssl.TrustManagerFactorySpi
    public TrustManager[] engineGetTrustManagers() {
        if (this.mApplicationConfig != null) {
            return new TrustManager[]{this.mApplicationConfig.getTrustManager()};
        }
        throw new IllegalStateException("TrustManagerFactory not initialized");
    }

    @Override // javax.net.ssl.TrustManagerFactorySpi
    public void engineInit(KeyStore keyStore) throws KeyStoreException {
        if (keyStore != null) {
            this.mApplicationConfig = new ApplicationConfig(new KeyStoreConfigSource(keyStore));
        } else {
            this.mApplicationConfig = ApplicationConfig.getDefaultInstance();
        }
    }

    @Override // javax.net.ssl.TrustManagerFactorySpi
    public void engineInit(ManagerFactoryParameters managerFactoryParameters) throws InvalidAlgorithmParameterException {
        if (managerFactoryParameters instanceof ApplicationConfigParameters) {
            this.mApplicationConfig = ((ApplicationConfigParameters) managerFactoryParameters).config;
            return;
        }
        throw new InvalidAlgorithmParameterException("Unsupported spec: " + managerFactoryParameters + ". Only " + ApplicationConfigParameters.class.getName() + " supported");
    }
}
