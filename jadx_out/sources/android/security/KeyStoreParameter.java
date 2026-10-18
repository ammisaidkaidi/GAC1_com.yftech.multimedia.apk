package android.security;

import android.content.Context;

/* JADX INFO: loaded from: classes2.dex */
@Deprecated
public final class KeyStoreParameter implements java.security.KeyStore.ProtectionParameter {
    private final int mFlags;

    @Deprecated
    public static final class Builder {
        private int mFlags;

        public Builder(Context context) {
            if (context == null) {
                throw new NullPointerException("context == null");
            }
        }

        public KeyStoreParameter build() {
            return new KeyStoreParameter(this.mFlags);
        }

        public Builder setEncryptionRequired(boolean z) {
            if (z) {
                this.mFlags |= 1;
            } else {
                this.mFlags &= -2;
            }
            return this;
        }
    }

    private KeyStoreParameter(int i) {
        this.mFlags = i;
    }

    public int getFlags() {
        return this.mFlags;
    }

    public boolean isEncryptionRequired() {
        return (this.mFlags & 1) != 0;
    }
}
