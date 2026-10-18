package android.net.lowpan;

/* JADX INFO: loaded from: classes.dex */
public class JoinFailedAtAuthException extends JoinFailedException {
    public JoinFailedAtAuthException() {
    }

    public JoinFailedAtAuthException(Exception exc) {
        super(exc);
    }

    public JoinFailedAtAuthException(String str) {
        super(str);
    }

    public JoinFailedAtAuthException(String str, Throwable th) {
        super(str, th);
    }
}
