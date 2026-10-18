package android.net;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.HexDump;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public final class IpSecAlgorithm implements Parcelable {
    public static final String AUTH_CRYPT_AES_GCM = "rfc4106(gcm(aes))";
    public static final String AUTH_HMAC_MD5 = "hmac(md5)";
    public static final String AUTH_HMAC_SHA1 = "hmac(sha1)";
    public static final String AUTH_HMAC_SHA256 = "hmac(sha256)";
    public static final String AUTH_HMAC_SHA384 = "hmac(sha384)";
    public static final String AUTH_HMAC_SHA512 = "hmac(sha512)";
    public static final Parcelable.Creator<IpSecAlgorithm> CREATOR = new Parcelable.Creator<IpSecAlgorithm>() { // from class: android.net.IpSecAlgorithm.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public IpSecAlgorithm createFromParcel(Parcel parcel) {
            return new IpSecAlgorithm(parcel.readString(), parcel.createByteArray(), parcel.readInt());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public IpSecAlgorithm[] newArray(int i) {
            return new IpSecAlgorithm[i];
        }
    };
    public static final String CRYPT_AES_CBC = "cbc(aes)";
    public static final String CRYPT_NULL = "ecb(cipher_null)";
    private static final String TAG = "IpSecAlgorithm";
    private final byte[] mKey;
    private final String mName;
    private final int mTruncLenBits;

    @Retention(RetentionPolicy.SOURCE)
    public @interface AlgorithmName {
    }

    public IpSecAlgorithm(String str, byte[] bArr) {
        this(str, bArr, 0);
    }

    public IpSecAlgorithm(String str, byte[] bArr, int i) {
        this.mName = str;
        this.mKey = (byte[]) bArr.clone();
        this.mTruncLenBits = i;
        checkValidOrThrow(this.mName, this.mKey.length * 8, this.mTruncLenBits);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:26:0x0051  */
    /* JADX WARN: Code duplicated, block: B:44:0x008e A[PHI: r8
      0x008e: PHI (r8v18 boolean) = (r8v4 boolean), (r8v4 boolean), (r8v7 boolean), (r8v7 boolean), (r8v10 boolean), (r8v10 boolean), (r8v20 boolean) binds: [B:76:0x00c5, B:77:0x00c7, B:70:0x00bb, B:71:0x00bd, B:64:0x00b1, B:65:0x00b3, B:42:0x008a] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:59:0x00a9 A[PHI: r0
      0x00a9: PHI (r0v24 boolean) = (r0v22 boolean), (r0v22 boolean), (r0v27 boolean), (r0v27 boolean) binds: [B:56:0x00a4, B:57:0x00a6, B:49:0x0098, B:50:0x009a] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:86:0x00d6 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:87:0x00d8 A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:88:0x00d9  */
    /* JADX WARN: Code duplicated, block: B:90:0x00f0  */
    /* JADX WARN: Instruction removed from duplicated block: B:88:0x00d9, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:90:0x00f0, please report this as an issue */
    private static void checkValidOrThrow(String str, int i, int i2) {
        boolean z;
        boolean z2;
        boolean z3 = true;
        switch (str) {
            case "cbc(aes)":
                z = i == 128 || i == 192 || i == 256;
                if (!z) {
                    throw new IllegalArgumentException("Invalid key material keyLength: " + i);
                }
                if (z3) {
                    return;
                }
                throw new IllegalArgumentException("Invalid truncation keyLength: " + i2);
            case "hmac(md5)":
                z = i == 128;
                if (i2 < 96 || i2 > 128) {
                    z3 = false;
                }
                if (!z) {
                    throw new IllegalArgumentException("Invalid key material keyLength: " + i);
                }
                if (z3) {
                    return;
                }
                throw new IllegalArgumentException("Invalid truncation keyLength: " + i2);
            case "hmac(sha1)":
                z = i == 160;
                if (i2 < 96 || i2 > 160) {
                    z3 = false;
                }
                if (!z) {
                    throw new IllegalArgumentException("Invalid key material keyLength: " + i);
                }
                if (z3) {
                    return;
                }
                throw new IllegalArgumentException("Invalid truncation keyLength: " + i2);
            case "hmac(sha256)":
                z = i == 256;
                if (i2 < 96 || i2 > 256) {
                    z3 = false;
                }
                if (!z) {
                    throw new IllegalArgumentException("Invalid key material keyLength: " + i);
                }
                if (z3) {
                    return;
                }
                throw new IllegalArgumentException("Invalid truncation keyLength: " + i2);
            case "hmac(sha384)":
                z2 = i == 384;
                if (i2 < 192 || i2 > 384) {
                    z3 = false;
                }
                z = z2;
                if (!z) {
                    throw new IllegalArgumentException("Invalid key material keyLength: " + i);
                }
                if (z3) {
                    return;
                }
                throw new IllegalArgumentException("Invalid truncation keyLength: " + i2);
            case "hmac(sha512)":
                z2 = i == 512;
                if (i2 < 256 || i2 > 512) {
                    z3 = false;
                }
                z = z2;
                if (!z) {
                    throw new IllegalArgumentException("Invalid key material keyLength: " + i);
                }
                if (z3) {
                    return;
                }
                throw new IllegalArgumentException("Invalid truncation keyLength: " + i2);
            case "rfc4106(gcm(aes))":
                z = i == 160 || i == 224 || i == 288;
                if (i2 != 64 && i2 != 96 && i2 != 128) {
                    z3 = false;
                }
                if (!z) {
                    throw new IllegalArgumentException("Invalid key material keyLength: " + i);
                }
                if (z3) {
                    return;
                }
                throw new IllegalArgumentException("Invalid truncation keyLength: " + i2);
            default:
                throw new IllegalArgumentException("Couldn't find an algorithm: " + str);
        }
    }

    @VisibleForTesting
    public static boolean equals(IpSecAlgorithm ipSecAlgorithm, IpSecAlgorithm ipSecAlgorithm2) {
        if (ipSecAlgorithm == null || ipSecAlgorithm2 == null) {
            return ipSecAlgorithm == ipSecAlgorithm2;
        }
        return ipSecAlgorithm.mName.equals(ipSecAlgorithm2.mName) && Arrays.equals(ipSecAlgorithm.mKey, ipSecAlgorithm2.mKey) && ipSecAlgorithm.mTruncLenBits == ipSecAlgorithm2.mTruncLenBits;
    }

    private static boolean isUnsafeBuild() {
        return Build.IS_DEBUGGABLE && Build.IS_ENG;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public byte[] getKey() {
        return (byte[]) this.mKey.clone();
    }

    public String getName() {
        return this.mName;
    }

    public int getTruncationLengthBits() {
        return this.mTruncLenBits;
    }

    public boolean isAead() {
        return getName().equals(AUTH_CRYPT_AES_GCM);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:20:0x0040  */
    public boolean isAuthentication() {
        switch (getName()) {
            case "hmac(md5)":
            case "hmac(sha1)":
            case "hmac(sha256)":
            case "hmac(sha384)":
            case "hmac(sha512)":
                return true;
            default:
                return false;
        }
    }

    public boolean isEncryption() {
        return getName().equals(CRYPT_AES_CBC);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{mName=");
        sb.append(this.mName);
        sb.append(", mKey=");
        sb.append(isUnsafeBuild() ? HexDump.toHexString(this.mKey) : "<hidden>");
        sb.append(", mTruncLenBits=");
        sb.append(this.mTruncLenBits);
        sb.append("}");
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mName);
        parcel.writeByteArray(this.mKey);
        parcel.writeInt(this.mTruncLenBits);
    }
}
