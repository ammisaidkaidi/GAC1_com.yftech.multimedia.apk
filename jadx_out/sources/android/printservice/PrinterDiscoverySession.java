package android.printservice;

import android.content.pm.ParceledListSlice;
import android.os.CancellationSignal;
import android.os.RemoteException;
import android.print.PrinterId;
import android.print.PrinterInfo;
import android.util.ArrayMap;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public abstract class PrinterDiscoverySession {
    private static final String LOG_TAG = "PrinterDiscoverySession";
    private static int sIdCounter;
    private final int mId;
    private boolean mIsDestroyed;
    private boolean mIsDiscoveryStarted;
    private ArrayMap<PrinterId, PrinterInfo> mLastSentPrinters;
    private IPrintServiceClient mObserver;
    private final ArrayMap<PrinterId, PrinterInfo> mPrinters = new ArrayMap<>();
    private final List<PrinterId> mTrackedPrinters = new ArrayList();

    public PrinterDiscoverySession() {
        int i = sIdCounter;
        sIdCounter = i + 1;
        this.mId = i;
    }

    private void sendOutOfDiscoveryPeriodPrinterChanges() {
        if (this.mLastSentPrinters == null || this.mLastSentPrinters.isEmpty()) {
            this.mLastSentPrinters = null;
            return;
        }
        ArrayList arrayList = null;
        for (PrinterInfo printerInfo : this.mPrinters.values()) {
            PrinterInfo printerInfo2 = this.mLastSentPrinters.get(printerInfo.getId());
            if (printerInfo2 == null || !printerInfo2.equals(printerInfo)) {
                if (arrayList == null) {
                    arrayList = new ArrayList();
                }
                arrayList.add(printerInfo);
            }
        }
        if (arrayList != null) {
            try {
                this.mObserver.onPrintersAdded(new ParceledListSlice(arrayList));
            } catch (RemoteException e) {
                Log.e(LOG_TAG, "Error sending added printers", e);
            }
        }
        ArrayList arrayList2 = null;
        for (PrinterInfo printerInfo3 : this.mLastSentPrinters.values()) {
            if (!this.mPrinters.containsKey(printerInfo3.getId())) {
                if (arrayList2 == null) {
                    arrayList2 = new ArrayList();
                }
                arrayList2.add(printerInfo3.getId());
            }
        }
        if (arrayList2 != null) {
            try {
                this.mObserver.onPrintersRemoved(new ParceledListSlice(arrayList2));
            } catch (RemoteException e2) {
                Log.e(LOG_TAG, "Error sending removed printers", e2);
            }
        }
        this.mLastSentPrinters = null;
    }

    public final void addPrinters(List<PrinterInfo> list) {
        PrintService.throwIfNotCalledOnMainThread();
        if (this.mIsDestroyed) {
            Log.w(LOG_TAG, "Not adding printers - session destroyed.");
            return;
        }
        int i = 0;
        if (!this.mIsDiscoveryStarted) {
            if (this.mLastSentPrinters == null) {
                this.mLastSentPrinters = new ArrayMap<>(this.mPrinters);
            }
            int size = list.size();
            while (i < size) {
                PrinterInfo printerInfo = list.get(i);
                if (this.mPrinters.get(printerInfo.getId()) == null) {
                    this.mPrinters.put(printerInfo.getId(), printerInfo);
                }
                i++;
            }
            return;
        }
        ArrayList arrayList = null;
        int size2 = list.size();
        while (i < size2) {
            PrinterInfo printerInfo2 = list.get(i);
            PrinterInfo printerInfoPut = this.mPrinters.put(printerInfo2.getId(), printerInfo2);
            if (printerInfoPut == null || !printerInfoPut.equals(printerInfo2)) {
                if (arrayList == null) {
                    arrayList = new ArrayList();
                }
                arrayList.add(printerInfo2);
            }
            i++;
        }
        if (arrayList != null) {
            try {
                this.mObserver.onPrintersAdded(new ParceledListSlice(arrayList));
            } catch (RemoteException e) {
                Log.e(LOG_TAG, "Error sending added printers", e);
            }
        }
    }

    void destroy() {
        if (this.mIsDestroyed) {
            return;
        }
        this.mIsDestroyed = true;
        this.mIsDiscoveryStarted = false;
        this.mPrinters.clear();
        this.mLastSentPrinters = null;
        this.mObserver = null;
        onDestroy();
    }

    int getId() {
        return this.mId;
    }

    public final List<PrinterInfo> getPrinters() {
        PrintService.throwIfNotCalledOnMainThread();
        return this.mIsDestroyed ? Collections.emptyList() : new ArrayList(this.mPrinters.values());
    }

    public final List<PrinterId> getTrackedPrinters() {
        PrintService.throwIfNotCalledOnMainThread();
        return this.mIsDestroyed ? Collections.emptyList() : new ArrayList(this.mTrackedPrinters);
    }

    public final boolean isDestroyed() {
        PrintService.throwIfNotCalledOnMainThread();
        return this.mIsDestroyed;
    }

    public final boolean isPrinterDiscoveryStarted() {
        PrintService.throwIfNotCalledOnMainThread();
        return this.mIsDiscoveryStarted;
    }

    public abstract void onDestroy();

    public void onRequestCustomPrinterIcon(PrinterId printerId, CancellationSignal cancellationSignal, CustomPrinterIconCallback customPrinterIconCallback) {
    }

    public abstract void onStartPrinterDiscovery(List<PrinterId> list);

    public abstract void onStartPrinterStateTracking(PrinterId printerId);

    public abstract void onStopPrinterDiscovery();

    public abstract void onStopPrinterStateTracking(PrinterId printerId);

    public abstract void onValidatePrinters(List<PrinterId> list);

    public final void removePrinters(List<PrinterId> list) {
        PrintService.throwIfNotCalledOnMainThread();
        if (this.mIsDestroyed) {
            Log.w(LOG_TAG, "Not removing printers - session destroyed.");
            return;
        }
        int i = 0;
        if (!this.mIsDiscoveryStarted) {
            if (this.mLastSentPrinters == null) {
                this.mLastSentPrinters = new ArrayMap<>(this.mPrinters);
            }
            int size = list.size();
            while (i < size) {
                this.mPrinters.remove(list.get(i));
                i++;
            }
            return;
        }
        ArrayList arrayList = new ArrayList();
        int size2 = list.size();
        while (i < size2) {
            PrinterId printerId = list.get(i);
            if (this.mPrinters.remove(printerId) != null) {
                arrayList.add(printerId);
            }
            i++;
        }
        if (arrayList.isEmpty()) {
            return;
        }
        try {
            this.mObserver.onPrintersRemoved(new ParceledListSlice(arrayList));
        } catch (RemoteException e) {
            Log.e(LOG_TAG, "Error sending removed printers", e);
        }
    }

    void requestCustomPrinterIcon(PrinterId printerId) {
        if (this.mIsDestroyed || this.mObserver == null) {
            return;
        }
        onRequestCustomPrinterIcon(printerId, new CancellationSignal(), new CustomPrinterIconCallback(printerId, this.mObserver));
    }

    void setObserver(IPrintServiceClient iPrintServiceClient) {
        this.mObserver = iPrintServiceClient;
        if (this.mPrinters.isEmpty()) {
            return;
        }
        try {
            this.mObserver.onPrintersAdded(new ParceledListSlice(getPrinters()));
        } catch (RemoteException e) {
            Log.e(LOG_TAG, "Error sending added printers", e);
        }
    }

    void startPrinterDiscovery(List<PrinterId> list) {
        if (this.mIsDestroyed) {
            return;
        }
        this.mIsDiscoveryStarted = true;
        sendOutOfDiscoveryPeriodPrinterChanges();
        if (list == null) {
            list = Collections.emptyList();
        }
        onStartPrinterDiscovery(list);
    }

    void startPrinterStateTracking(PrinterId printerId) {
        if (this.mIsDestroyed || this.mObserver == null || this.mTrackedPrinters.contains(printerId)) {
            return;
        }
        this.mTrackedPrinters.add(printerId);
        onStartPrinterStateTracking(printerId);
    }

    void stopPrinterDiscovery() {
        if (this.mIsDestroyed) {
            return;
        }
        this.mIsDiscoveryStarted = false;
        onStopPrinterDiscovery();
    }

    void stopPrinterStateTracking(PrinterId printerId) {
        if (this.mIsDestroyed || this.mObserver == null || !this.mTrackedPrinters.remove(printerId)) {
            return;
        }
        onStopPrinterStateTracking(printerId);
    }

    void validatePrinters(List<PrinterId> list) {
        if (this.mIsDestroyed || this.mObserver == null) {
            return;
        }
        onValidatePrinters(list);
    }
}
