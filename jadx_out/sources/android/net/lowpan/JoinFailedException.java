package android.net.lowpan;

/* JADX INFO: loaded from: classes.dex */
public class JoinFailedException extends LowpanException {
    public JoinFailedException() {
    }

    protected JoinFailedException(Exception exc) {
        super(exc);
    }

    public JoinFailedException(String str) {
        super(str);
    }

    public JoinFailedException(String str, Throwable th) {
        super(str, th);
    }
}
