package android.preference;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.SeekBar;
import com.android.internal.R;

/* JADX INFO: loaded from: classes2.dex */
public class VolumePreference extends SeekBarDialogPreference implements PreferenceManager.OnActivityStopListener, View.OnKeyListener, SeekBarVolumizer.Callback {
    private SeekBarVolumizer mSeekBarVolumizer;
    private int mStreamType;

    private static class SavedState extends Preference.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: android.preference.VolumePreference.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        VolumeStore mVolumeStore;

        public SavedState(Parcel parcel) {
            super(parcel);
            this.mVolumeStore = new VolumeStore();
            this.mVolumeStore.volume = parcel.readInt();
            this.mVolumeStore.originalVolume = parcel.readInt();
        }

        public SavedState(Parcelable parcelable) {
            super(parcelable);
            this.mVolumeStore = new VolumeStore();
        }

        VolumeStore getVolumeStore() {
            return this.mVolumeStore;
        }

        @Override // android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.mVolumeStore.volume);
            parcel.writeInt(this.mVolumeStore.originalVolume);
        }
    }

    public static class VolumeStore {
        public int volume = -1;
        public int originalVolume = -1;
    }

    public VolumePreference(Context context) {
        this(context, null);
    }

    public VolumePreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.seekBarDialogPreferenceStyle);
    }

    public VolumePreference(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public VolumePreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.VolumePreference, i, i2);
        this.mStreamType = typedArrayObtainStyledAttributes.getInt(0, 0);
        typedArrayObtainStyledAttributes.recycle();
    }

    private void cleanup() {
        getPreferenceManager().unregisterOnActivityStopListener(this);
        if (this.mSeekBarVolumizer != null) {
            Dialog dialog = getDialog();
            if (dialog != null && dialog.isShowing()) {
                View viewFindViewById = dialog.getWindow().getDecorView().findViewById(R.id.seekbar);
                if (viewFindViewById != null) {
                    viewFindViewById.setOnKeyListener(null);
                }
                this.mSeekBarVolumizer.revertVolume();
            }
            this.mSeekBarVolumizer.stop();
            this.mSeekBarVolumizer = null;
        }
    }

    @Override // android.preference.PreferenceManager.OnActivityStopListener
    public void onActivityStop() {
        if (this.mSeekBarVolumizer != null) {
            this.mSeekBarVolumizer.stopSample();
        }
    }

    @Override // android.preference.SeekBarDialogPreference, android.preference.DialogPreference
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekbar);
        this.mSeekBarVolumizer = new SeekBarVolumizer(getContext(), this.mStreamType, null, this);
        this.mSeekBarVolumizer.start();
        this.mSeekBarVolumizer.setSeekBar(seekBar);
        getPreferenceManager().registerOnActivityStopListener(this);
        view.setOnKeyListener(this);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
    }

    @Override // android.preference.DialogPreference
    protected void onDialogClosed(boolean z) {
        super.onDialogClosed(z);
        if (!z && this.mSeekBarVolumizer != null) {
            this.mSeekBarVolumizer.revertVolume();
        }
        cleanup();
    }

    @Override // android.preference.Preference
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (this.mSeekBarVolumizer == null) {
            return true;
        }
        boolean z = keyEvent.getAction() == 0;
        if (i == 164) {
            if (z) {
                this.mSeekBarVolumizer.muteVolume();
            }
            return true;
        }
        switch (i) {
            case 24:
                if (z) {
                    this.mSeekBarVolumizer.changeVolumeBy(1);
                }
                return true;
            case 25:
                if (z) {
                    this.mSeekBarVolumizer.changeVolumeBy(-1);
                }
                return true;
            default:
                return false;
        }
    }

    @Override // android.preference.SeekBarVolumizer.Callback
    public void onMuted(boolean z, boolean z2) {
    }

    @Override // android.preference.SeekBarVolumizer.Callback
    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
    }

    @Override // android.preference.DialogPreference, android.preference.Preference
    protected void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable == null || !parcelable.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        if (this.mSeekBarVolumizer != null) {
            this.mSeekBarVolumizer.onRestoreInstanceState(savedState.getVolumeStore());
        }
    }

    @Override // android.preference.SeekBarVolumizer.Callback
    public void onSampleStarting(SeekBarVolumizer seekBarVolumizer) {
        if (this.mSeekBarVolumizer == null || seekBarVolumizer == this.mSeekBarVolumizer) {
            return;
        }
        this.mSeekBarVolumizer.stopSample();
    }

    @Override // android.preference.DialogPreference, android.preference.Preference
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelableOnSaveInstanceState = super.onSaveInstanceState();
        if (isPersistent()) {
            return parcelableOnSaveInstanceState;
        }
        SavedState savedState = new SavedState(parcelableOnSaveInstanceState);
        if (this.mSeekBarVolumizer != null) {
            this.mSeekBarVolumizer.onSaveInstanceState(savedState.getVolumeStore());
        }
        return savedState;
    }

    public void setStreamType(int i) {
        this.mStreamType = i;
    }
}
