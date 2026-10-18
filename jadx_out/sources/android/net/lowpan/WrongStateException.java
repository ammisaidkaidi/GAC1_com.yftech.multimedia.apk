package android.net.lowpan;

/* JADX INFO: loaded from: classes.dex */
public class WrongStateException extends LowpanException {
    public WrongStateException() {
    }

    protected WrongStateException(Exception exc) {
        super(exc);
    }

    public WrongStateException(String str) {
        super(str);
    }

    public WrongStateException(String str, Throwable th) {
        super(str, th);
    }
}
