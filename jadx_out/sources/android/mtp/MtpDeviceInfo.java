package android.mtp;

/* JADX INFO: loaded from: classes.dex */
public class MtpDeviceInfo {
    private int[] mEventsSupported;
    private String mManufacturer;
    private String mModel;
    private int[] mOperationsSupported;
    private String mSerialNumber;
    private String mVersion;

    private MtpDeviceInfo() {
    }

    private static boolean isSupported(int[] iArr, int i) {
        for (int i2 : iArr) {
            if (i2 == i) {
                return true;
            }
        }
        return false;
    }

    public final int[] getEventsSupported() {
        return this.mEventsSupported;
    }

    public final String getManufacturer() {
        return this.mManufacturer;
    }

    public final String getModel() {
        return this.mModel;
    }

    public final int[] getOperationsSupported() {
        return this.mOperationsSupported;
    }

    public final String getSerialNumber() {
        return this.mSerialNumber;
    }

    public final String getVersion() {
        return this.mVersion;
    }

    public boolean isEventSupported(int i) {
        return isSupported(this.mEventsSupported, i);
    }

    public boolean isOperationSupported(int i) {
        return isSupported(this.mOperationsSupported, i);
    }
}
