package android.view.textclassifier;

import com.android.internal.util.Preconditions;

/* JADX INFO: loaded from: classes2.dex */
final class TextClassificationSession implements TextClassifier {
    static final boolean DEBUG_LOG_ENABLED = true;
    private static final String LOG_TAG = "TextClassificationSession";
    private final TextClassificationContext mClassificationContext;
    private final TextClassifier mDelegate;
    private boolean mDestroyed;
    private final SelectionEventHelper mEventHelper;
    private final TextClassificationSessionId mSessionId = new TextClassificationSessionId();

    private static final class SelectionEventHelper {
        private final TextClassificationContext mContext;
        private int mInvocationMethod = 0;
        private SelectionEvent mPrevEvent;
        private final TextClassificationSessionId mSessionId;
        private SelectionEvent mSmartEvent;
        private SelectionEvent mStartEvent;

        SelectionEventHelper(TextClassificationSessionId textClassificationSessionId, TextClassificationContext textClassificationContext) {
            this.mSessionId = (TextClassificationSessionId) Preconditions.checkNotNull(textClassificationSessionId);
            this.mContext = (TextClassificationContext) Preconditions.checkNotNull(textClassificationContext);
        }

        private static boolean isPlatformLocalTextClassifierSmartSelection(String str) {
            return TextClassifier.DEFAULT_LOG_TAG.equals(SelectionSessionLogger.SignatureParser.getClassifierId(str));
        }

        private void modifyAutoSelectionEventType(SelectionEvent selectionEvent) {
            switch (selectionEvent.getEventType()) {
                case 3:
                case 4:
                case 5:
                    if (!isPlatformLocalTextClassifierSmartSelection(selectionEvent.getResultId())) {
                        selectionEvent.setEventType(5);
                    } else if (selectionEvent.getAbsoluteEnd() - selectionEvent.getAbsoluteStart() <= 1) {
                        selectionEvent.setEventType(3);
                    } else {
                        selectionEvent.setEventType(4);
                    }
                    break;
            }
        }

        private void updateInvocationMethod(SelectionEvent selectionEvent) {
            selectionEvent.setTextClassificationSessionContext(this.mContext);
            if (selectionEvent.getInvocationMethod() == 0) {
                selectionEvent.setInvocationMethod(this.mInvocationMethod);
            } else {
                this.mInvocationMethod = selectionEvent.getInvocationMethod();
            }
        }

        void endSession() {
            this.mPrevEvent = null;
            this.mSmartEvent = null;
            this.mStartEvent = null;
        }

        boolean sanitizeEvent(SelectionEvent selectionEvent) {
            updateInvocationMethod(selectionEvent);
            modifyAutoSelectionEventType(selectionEvent);
            if (selectionEvent.getEventType() != 1 && this.mStartEvent == null) {
                Log.d(TextClassificationSession.LOG_TAG, "Selection session not yet started. Ignoring event");
                return false;
            }
            long jCurrentTimeMillis = System.currentTimeMillis();
            switch (selectionEvent.getEventType()) {
                case 1:
                    Preconditions.checkArgument(selectionEvent.getAbsoluteEnd() == selectionEvent.getAbsoluteStart() + 1);
                    selectionEvent.setSessionId(this.mSessionId);
                    this.mStartEvent = selectionEvent;
                    break;
                case 2:
                case 5:
                    if (this.mPrevEvent != null && this.mPrevEvent.getAbsoluteStart() == selectionEvent.getAbsoluteStart() && this.mPrevEvent.getAbsoluteEnd() == selectionEvent.getAbsoluteEnd()) {
                        return false;
                    }
                    break;
                case 3:
                case 4:
                    this.mSmartEvent = selectionEvent;
                    break;
            }
            selectionEvent.setEventTime(jCurrentTimeMillis);
            if (this.mStartEvent != null) {
                selectionEvent.setSessionId(this.mStartEvent.getSessionId()).setDurationSinceSessionStart(jCurrentTimeMillis - this.mStartEvent.getEventTime()).setStart(selectionEvent.getAbsoluteStart() - this.mStartEvent.getAbsoluteStart()).setEnd(selectionEvent.getAbsoluteEnd() - this.mStartEvent.getAbsoluteStart());
            }
            if (this.mSmartEvent != null) {
                selectionEvent.setResultId(this.mSmartEvent.getResultId()).setSmartStart(this.mSmartEvent.getAbsoluteStart() - this.mStartEvent.getAbsoluteStart()).setSmartEnd(this.mSmartEvent.getAbsoluteEnd() - this.mStartEvent.getAbsoluteStart());
            }
            if (this.mPrevEvent != null) {
                selectionEvent.setDurationSincePreviousEvent(jCurrentTimeMillis - this.mPrevEvent.getEventTime()).setEventIndex(this.mPrevEvent.getEventIndex() + 1);
            }
            this.mPrevEvent = selectionEvent;
            return true;
        }
    }

    TextClassificationSession(TextClassificationContext textClassificationContext, TextClassifier textClassifier) {
        this.mClassificationContext = (TextClassificationContext) Preconditions.checkNotNull(textClassificationContext);
        this.mDelegate = (TextClassifier) Preconditions.checkNotNull(textClassifier);
        this.mEventHelper = new SelectionEventHelper(this.mSessionId, this.mClassificationContext);
        initializeRemoteSession();
    }

    private void checkDestroyed() {
        if (this.mDestroyed) {
            throw new IllegalStateException("This TextClassification session has been destroyed");
        }
    }

    private void initializeRemoteSession() {
        if (this.mDelegate instanceof SystemTextClassifier) {
            ((SystemTextClassifier) this.mDelegate).initializeRemoteSession(this.mClassificationContext, this.mSessionId);
        }
    }

    @Override // android.view.textclassifier.TextClassifier
    public TextClassification classifyText(TextClassification.Request request) {
        checkDestroyed();
        return this.mDelegate.classifyText(request);
    }

    @Override // android.view.textclassifier.TextClassifier
    public void destroy() {
        this.mEventHelper.endSession();
        this.mDelegate.destroy();
        this.mDestroyed = true;
    }

    @Override // android.view.textclassifier.TextClassifier
    public TextLinks generateLinks(TextLinks.Request request) {
        checkDestroyed();
        return this.mDelegate.generateLinks(request);
    }

    @Override // android.view.textclassifier.TextClassifier
    public boolean isDestroyed() {
        return this.mDestroyed;
    }

    @Override // android.view.textclassifier.TextClassifier
    public void onSelectionEvent(SelectionEvent selectionEvent) {
        checkDestroyed();
        Preconditions.checkNotNull(selectionEvent);
        if (this.mEventHelper.sanitizeEvent(selectionEvent)) {
            this.mDelegate.onSelectionEvent(selectionEvent);
        }
    }

    @Override // android.view.textclassifier.TextClassifier
    public TextSelection suggestSelection(TextSelection.Request request) {
        checkDestroyed();
        return this.mDelegate.suggestSelection(request);
    }
}
