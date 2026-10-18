package com.yftech.car.widget.media.text;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import com.yftech.car.widget.media.R;

/* JADX INFO: loaded from: classes3.dex */
public class PlayStatus extends AppCompatTextView {
    public static final int MUSIC_ITEM = 2;
    public static final int PAUSE = 2;
    public static final int PLAY = 1;
    public static final int RADIO_ITEM = 1;
    private int mPlayStatusClickFunction;
    private final Drawable[] mPlayStatusDrawable;
    private final String[] mPlayStatusText;
    private int mType;

    private PlayStatus(@NonNull Context context) {
        super(context);
        this.mType = 1;
        this.mPlayStatusText = new String[2];
        this.mPlayStatusDrawable = new Drawable[2];
    }

    public PlayStatus(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mType = 1;
        this.mPlayStatusText = new String[2];
        this.mPlayStatusDrawable = new Drawable[2];
        init(context, attributeSet);
    }

    public PlayStatus(@NonNull Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mType = 1;
        this.mPlayStatusText = new String[2];
        this.mPlayStatusDrawable = new Drawable[2];
        init(context, attributeSet);
    }

    private void init(Context context, AttributeSet attributeSet) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.PlayStatus);
        this.mPlayStatusText[0] = typedArrayObtainStyledAttributes.getString(R.styleable.PlayStatus_playStatus_pause_text);
        this.mPlayStatusText[1] = typedArrayObtainStyledAttributes.getString(R.styleable.PlayStatus_playStatus_play_text);
        this.mPlayStatusDrawable[0] = typedArrayObtainStyledAttributes.getDrawable(R.styleable.PlayStatus_playStatus_pause_drawable);
        this.mPlayStatusDrawable[1] = typedArrayObtainStyledAttributes.getDrawable(R.styleable.PlayStatus_playStatus_play_drawable);
        this.mPlayStatusClickFunction = typedArrayObtainStyledAttributes.getInt(R.styleable.PlayStatus_playStatus_click_function, 0);
    }

    public int getClickFunction() {
        return this.mPlayStatusClickFunction;
    }

    public boolean isClickFunction(int i) {
        return (this.mPlayStatusClickFunction & i) > 0;
    }

    @Override // android.widget.TextView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (2 == this.mType || 1 == this.mType) {
            setVisibility(0);
        } else {
            setVisibility(8);
        }
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
    }

    public void setPlayStatus(boolean z) {
        String str = this.mPlayStatusText[z ? 1 : 0];
        if (str != null) {
            setText(str);
        }
        setBackground(this.mPlayStatusDrawable[z ? 1 : 0]);
    }

    public void setType(int i) {
        this.mType = i;
        if (2 == this.mType || 1 == this.mType) {
            setVisibility(0);
        } else {
            setVisibility(8);
        }
    }
}
