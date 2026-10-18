package android.security;

import java.security.KeyPair;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public final class AttestedKeyPair {
    private final Certificate[] mAttestationRecord;
    private final KeyPair mKeyPair;

    public AttestedKeyPair(KeyPair keyPair, Certificate[] certificateArr) {
        this.mKeyPair = keyPair;
        this.mAttestationRecord = certificateArr;
    }

    public List<Certificate> getAttestationRecord() {
        return this.mAttestationRecord == null ? new ArrayList() : Arrays.asList(this.mAttestationRecord);
    }

    public KeyPair getKeyPair() {
        return this.mKeyPair;
    }
}
