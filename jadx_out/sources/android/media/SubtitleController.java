package android.media;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.accessibility.CaptioningManager;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

/* JADX INFO: loaded from: classes.dex */
public class SubtitleController {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final int WHAT_HIDE = 2;
    private static final int WHAT_SELECT_DEFAULT_TRACK = 4;
    private static final int WHAT_SELECT_TRACK = 3;
    private static final int WHAT_SHOW = 1;
    private Anchor mAnchor;
    private CaptioningManager mCaptioningManager;
    private Handler mHandler;
    private Listener mListener;
    private SubtitleTrack mSelectedTrack;
    private MediaTimeProvider mTimeProvider;
    private final Handler.Callback mCallback = new Handler.Callback() { // from class: android.media.SubtitleController.1
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    SubtitleController.this.doShow();
                    return true;
                case 2:
                    SubtitleController.this.doHide();
                    return true;
                case 3:
                    SubtitleController.this.doSelectTrack((SubtitleTrack) message.obj);
                    return true;
                case 4:
                    SubtitleController.this.doSelectDefaultTrack();
                    return true;
                default:
                    return false;
            }
        }
    };
    private CaptioningManager.CaptioningChangeListener mCaptioningChangeListener = new CaptioningManager.CaptioningChangeListener() { // from class: android.media.SubtitleController.2
        @Override // android.view.accessibility.CaptioningManager.CaptioningChangeListener
        public void onEnabledChanged(boolean z) {
            SubtitleController.this.selectDefaultTrack();
        }

        @Override // android.view.accessibility.CaptioningManager.CaptioningChangeListener
        public void onLocaleChanged(Locale locale) {
            SubtitleController.this.selectDefaultTrack();
        }
    };
    private boolean mTrackIsExplicit = false;
    private boolean mVisibilityIsExplicit = false;
    private Vector<Renderer> mRenderers = new Vector<>();
    private boolean mShowing = false;
    private Vector<SubtitleTrack> mTracks = new Vector<>();

    public interface Anchor {
        Looper getSubtitleLooper();

        void setSubtitleWidget(SubtitleTrack.RenderingWidget renderingWidget);
    }

    public interface Listener {
        void onSubtitleTrackSelected(SubtitleTrack subtitleTrack);
    }

    public static abstract class Renderer {
        public abstract SubtitleTrack createTrack(MediaFormat mediaFormat);

        public abstract boolean supports(MediaFormat mediaFormat);
    }

    public SubtitleController(Context context, MediaTimeProvider mediaTimeProvider, Listener listener) {
        this.mTimeProvider = mediaTimeProvider;
        this.mListener = listener;
        this.mCaptioningManager = (CaptioningManager) context.getSystemService(Context.CAPTIONING_SERVICE);
    }

    private void checkAnchorLooper() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doHide() {
        this.mVisibilityIsExplicit = true;
        if (this.mSelectedTrack != null) {
            this.mSelectedTrack.hide();
        }
        this.mShowing = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doSelectDefaultTrack() {
        if (!this.mTrackIsExplicit) {
            SubtitleTrack defaultTrack = getDefaultTrack();
            if (defaultTrack != null) {
                selectTrack(defaultTrack);
                this.mTrackIsExplicit = false;
                if (this.mVisibilityIsExplicit) {
                    return;
                }
                show();
                this.mVisibilityIsExplicit = false;
                return;
            }
            return;
        }
        if (this.mVisibilityIsExplicit) {
            return;
        }
        if (this.mCaptioningManager.isEnabled() || !(this.mSelectedTrack == null || this.mSelectedTrack.getFormat().getInteger(MediaFormat.KEY_IS_FORCED_SUBTITLE, 0) == 0)) {
            show();
        } else if (this.mSelectedTrack != null && this.mSelectedTrack.getTrackType() == 4) {
            hide();
        }
        this.mVisibilityIsExplicit = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doSelectTrack(SubtitleTrack subtitleTrack) {
        this.mTrackIsExplicit = true;
        if (this.mSelectedTrack == subtitleTrack) {
            return;
        }
        if (this.mSelectedTrack != null) {
            this.mSelectedTrack.hide();
            this.mSelectedTrack.setTimeProvider(null);
        }
        this.mSelectedTrack = subtitleTrack;
        if (this.mAnchor != null) {
            this.mAnchor.setSubtitleWidget(getRenderingWidget());
        }
        if (this.mSelectedTrack != null) {
            this.mSelectedTrack.setTimeProvider(this.mTimeProvider);
            this.mSelectedTrack.show();
        }
        if (this.mListener != null) {
            this.mListener.onSubtitleTrackSelected(subtitleTrack);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doShow() {
        this.mShowing = true;
        this.mVisibilityIsExplicit = true;
        if (this.mSelectedTrack != null) {
            this.mSelectedTrack.show();
        }
    }

    private SubtitleTrack.RenderingWidget getRenderingWidget() {
        if (this.mSelectedTrack == null) {
            return null;
        }
        return this.mSelectedTrack.getRenderingWidget();
    }

    private void processOnAnchor(Message message) {
        if (Looper.myLooper() == this.mHandler.getLooper()) {
            this.mHandler.dispatchMessage(message);
        } else {
            this.mHandler.sendMessage(message);
        }
    }

    public SubtitleTrack addTrack(MediaFormat mediaFormat) {
        SubtitleTrack subtitleTrackCreateTrack;
        synchronized (this.mRenderers) {
            try {
                for (Renderer renderer : this.mRenderers) {
                    if (renderer.supports(mediaFormat) && (subtitleTrackCreateTrack = renderer.createTrack(mediaFormat)) != null) {
                        synchronized (this.mTracks) {
                            if (this.mTracks.size() == 0) {
                                this.mCaptioningManager.addCaptioningChangeListener(this.mCaptioningChangeListener);
                            }
                            this.mTracks.add(subtitleTrackCreateTrack);
                        }
                        return subtitleTrackCreateTrack;
                    }
                }
                return null;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    protected void finalize() throws Throwable {
        this.mCaptioningManager.removeCaptioningChangeListener(this.mCaptioningChangeListener);
        super.finalize();
    }

    public SubtitleTrack getDefaultTrack() {
        SubtitleTrack subtitleTrack;
        Locale locale = this.mCaptioningManager.getLocale();
        Locale locale2 = locale == null ? Locale.getDefault() : locale;
        boolean z = !this.mCaptioningManager.isEnabled();
        synchronized (this.mTracks) {
            subtitleTrack = null;
            int i = -1;
            for (SubtitleTrack subtitleTrack2 : this.mTracks) {
                MediaFormat format = subtitleTrack2.getFormat();
                String string = format.getString("language");
                int i2 = 0;
                boolean z2 = format.getInteger(MediaFormat.KEY_IS_FORCED_SUBTITLE, 0) != 0;
                boolean z3 = format.getInteger(MediaFormat.KEY_IS_AUTOSELECT, 1) != 0;
                boolean z4 = format.getInteger(MediaFormat.KEY_IS_DEFAULT, 0) != 0;
                int i3 = (locale2 == null || locale2.getLanguage().equals("") || locale2.getISO3Language().equals(string) || locale2.getLanguage().equals(string)) ? 1 : 0;
                int i4 = (z2 ? 0 : 8) + ((locale == null && z4) ? 4 : 0);
                if (!z3) {
                    i2 = 2;
                }
                int i5 = i4 + i2 + i3;
                if (!z || z2) {
                    if ((locale == null && z4) || (i3 != 0 && (z3 || z2 || locale != null))) {
                        if (i5 > i) {
                            subtitleTrack = subtitleTrack2;
                            i = i5;
                        }
                    }
                }
            }
        }
        return subtitleTrack;
    }

    public SubtitleTrack getSelectedTrack() {
        return this.mSelectedTrack;
    }

    public SubtitleTrack[] getTracks() {
        SubtitleTrack[] subtitleTrackArr;
        synchronized (this.mTracks) {
            subtitleTrackArr = new SubtitleTrack[this.mTracks.size()];
            this.mTracks.toArray(subtitleTrackArr);
        }
        return subtitleTrackArr;
    }

    public boolean hasRendererFor(MediaFormat mediaFormat) {
        synchronized (this.mRenderers) {
            Iterator<Renderer> it = this.mRenderers.iterator();
            while (it.hasNext()) {
                if (it.next().supports(mediaFormat)) {
                    return true;
                }
            }
            return false;
        }
    }

    public void hide() {
        processOnAnchor(this.mHandler.obtainMessage(2));
    }

    public void registerRenderer(Renderer renderer) {
        synchronized (this.mRenderers) {
            if (!this.mRenderers.contains(renderer)) {
                this.mRenderers.add(renderer);
            }
        }
    }

    public void reset() {
        checkAnchorLooper();
        hide();
        selectTrack(null);
        this.mTracks.clear();
        this.mTrackIsExplicit = false;
        this.mVisibilityIsExplicit = false;
        this.mCaptioningManager.removeCaptioningChangeListener(this.mCaptioningChangeListener);
    }

    public void selectDefaultTrack() {
        processOnAnchor(this.mHandler.obtainMessage(4));
    }

    public boolean selectTrack(SubtitleTrack subtitleTrack) {
        if (subtitleTrack != null && !this.mTracks.contains(subtitleTrack)) {
            return false;
        }
        processOnAnchor(this.mHandler.obtainMessage(3, subtitleTrack));
        return true;
    }

    public void setAnchor(Anchor anchor) {
        if (this.mAnchor == anchor) {
            return;
        }
        if (this.mAnchor != null) {
            checkAnchorLooper();
            this.mAnchor.setSubtitleWidget(null);
        }
        this.mAnchor = anchor;
        this.mHandler = null;
        if (this.mAnchor != null) {
            this.mHandler = new Handler(this.mAnchor.getSubtitleLooper(), this.mCallback);
            checkAnchorLooper();
            this.mAnchor.setSubtitleWidget(getRenderingWidget());
        }
    }

    public void show() {
        processOnAnchor(this.mHandler.obtainMessage(1));
    }
}
