package android.print;

import android.content.Context;
import android.content.Loader;
import android.os.Handler;
import android.os.Message;
import android.printservice.PrintServiceInfo;
import com.android.internal.util.Preconditions;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class PrintServicesLoader extends Loader<List<PrintServiceInfo>> {
    private final Handler mHandler;
    private PrintManager.PrintServicesChangeListener mListener;
    private final PrintManager mPrintManager;
    private final int mSelectionFlags;

    private class MyHandler extends Handler {
        public MyHandler() {
            super(PrintServicesLoader.this.getContext().getMainLooper());
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (PrintServicesLoader.this.isStarted()) {
                PrintServicesLoader.this.deliverResult((List) message.obj);
            }
        }
    }

    public PrintServicesLoader(PrintManager printManager, Context context, int i) {
        super((Context) Preconditions.checkNotNull(context));
        this.mHandler = new MyHandler();
        this.mPrintManager = (PrintManager) Preconditions.checkNotNull(printManager);
        this.mSelectionFlags = Preconditions.checkFlagsArgument(i, 3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void queueNewResult() {
        Message messageObtainMessage = this.mHandler.obtainMessage(0);
        messageObtainMessage.obj = this.mPrintManager.getPrintServices(this.mSelectionFlags);
        this.mHandler.sendMessage(messageObtainMessage);
    }

    @Override // android.content.Loader
    protected void onForceLoad() {
        queueNewResult();
    }

    @Override // android.content.Loader
    protected void onReset() {
        onStopLoading();
    }

    @Override // android.content.Loader
    protected void onStartLoading() {
        this.mListener = new PrintManager.PrintServicesChangeListener() { // from class: android.print.PrintServicesLoader.1
            @Override // android.print.PrintManager.PrintServicesChangeListener
            public void onPrintServicesChanged() {
                PrintServicesLoader.this.queueNewResult();
            }
        };
        this.mPrintManager.addPrintServicesChangeListener(this.mListener, null);
        deliverResult(this.mPrintManager.getPrintServices(this.mSelectionFlags));
    }

    @Override // android.content.Loader
    protected void onStopLoading() {
        if (this.mListener != null) {
            this.mPrintManager.removePrintServicesChangeListener(this.mListener);
            this.mListener = null;
        }
        this.mHandler.removeMessages(0);
    }
}
