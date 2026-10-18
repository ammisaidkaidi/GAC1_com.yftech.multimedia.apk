package android.net.lowpan;

/* JADX INFO: loaded from: classes.dex */
public class OperationCanceledException extends LowpanException {
    public OperationCanceledException() {
    }

    protected OperationCanceledException(Exception exc) {
        super(exc);
    }

    public OperationCanceledException(String str) {
        super(str);
    }

    public OperationCanceledException(String str, Throwable th) {
        super(str, th);
    }
}
