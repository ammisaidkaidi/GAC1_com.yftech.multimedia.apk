package com.media.common.ui.widget.zoomable;

import android.graphics.Matrix;
import com.facebook.infer.annotation.Nullsafe;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.STRICT)
public class MultiZoomableControllerListener implements ZoomableController.Listener {
    private final List<ZoomableController.Listener> mListeners = new ArrayList();

    public synchronized void addListener(ZoomableController.Listener listener) {
        this.mListeners.add(listener);
    }

    @Override // com.media.common.ui.widget.zoomable.ZoomableController.Listener
    public synchronized void onTransformBegin(Matrix matrix) {
        Iterator<ZoomableController.Listener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            it.next().onTransformBegin(matrix);
        }
    }

    @Override // com.media.common.ui.widget.zoomable.ZoomableController.Listener
    public synchronized void onTransformChanged(Matrix matrix) {
        Iterator<ZoomableController.Listener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            it.next().onTransformChanged(matrix);
        }
    }

    @Override // com.media.common.ui.widget.zoomable.ZoomableController.Listener
    public synchronized void onTransformEnd(Matrix matrix) {
        Iterator<ZoomableController.Listener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            it.next().onTransformEnd(matrix);
        }
    }

    public synchronized void removeListener(ZoomableController.Listener listener) {
        this.mListeners.remove(listener);
    }
}
