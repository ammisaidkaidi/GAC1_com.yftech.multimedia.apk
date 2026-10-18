package android.webkit;

import android.annotation.SystemApi;

/* JADX INFO: loaded from: classes2.dex */
public class JsResult {
    private final ResultReceiver mReceiver;
    private boolean mResult;

    @SystemApi
    public interface ResultReceiver {
        void onJsResultComplete(JsResult jsResult);
    }

    @SystemApi
    public JsResult(ResultReceiver resultReceiver) {
        this.mReceiver = resultReceiver;
    }

    private final void wakeUp() {
        this.mReceiver.onJsResultComplete(this);
    }

    public final void cancel() {
        this.mResult = false;
        wakeUp();
    }

    public final void confirm() {
        this.mResult = true;
        wakeUp();
    }

    @SystemApi
    public final boolean getResult() {
        return this.mResult;
    }
}
