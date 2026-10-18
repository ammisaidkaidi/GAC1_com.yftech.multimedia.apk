package android.net.lowpan;

/* JADX INFO: loaded from: classes.dex */
public class NetworkAlreadyExistsException extends LowpanException {
    public NetworkAlreadyExistsException() {
    }

    public NetworkAlreadyExistsException(Exception exc) {
        super(exc);
    }

    public NetworkAlreadyExistsException(String str) {
        super(str, null);
    }

    public NetworkAlreadyExistsException(String str, Throwable th) {
        super(str, th);
    }
}
