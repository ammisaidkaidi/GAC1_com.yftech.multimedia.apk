package com.yftech.car.widget.media.photo.button;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import com.yftech.car.widget.media.R;

/* JADX INFO: loaded from: classes3.dex */
public class PhotoButton extends AppCompatButton {
    public static final int ALBUM = 1004;
    public static final int ARTIST = 1003;
    public static final int COLLECT = 1000;
    public static final int DIRECTORY = 1006;
    public static final int FOLDER = 1001;
    public static final int GENRE = 1005;
    public static final int PLAY_LIST = 1100;
    public static final int SWITCH_USB1 = 100;
    public static final int SWITCH_USB2 = 101;
    public static final int SWITCH_USB3 = 102;
    public static final int SWITCH_USB4 = 103;
    public static final int SWITCH_USB5 = 104;
    public static final int TITLE = 1002;
    private int mClickFunction;
    private int mClickLongFunction;
    private Drawable mDrawable;
    private Drawable mDrawableSelected;
    private OnClickFunctionListener mFunctionListener;
    private int mMediaDataListId;
    private final View.OnClickListener mOnClick;
    private View.OnClickListener mOnClickListener;
    private final View.OnLongClickListener mOnLongClick;
    private View.OnLongClickListener mOnLongClickListener;
    private String mSelected;
    private String mText;

    public interface OnClickFunctionListener {
        void onClickFunction(int i, int i2);
    }

    public PhotoButton(Context context) {
        super(context);
        this.mClickLongFunction = -1;
        this.mText = "";
        this.mSelected = null;
        this.mOnClick = new View.OnClickListener() { // from class: com.yftech.car.widget.media.photo.button.PhotoButton.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (PhotoButton.this.mFunctionListener != null) {
                    PhotoButton.this.mFunctionListener.onClickFunction(PhotoButton.this.mMediaDataListId, PhotoButton.this.mClickFunction);
                }
                if (PhotoButton.this.mOnClickListener != null) {
                    PhotoButton.this.mOnClickListener.onClick(view);
                }
            }
        };
        this.mOnLongClick = new View.OnLongClickListener() { // from class: com.yftech.car.widget.media.photo.button.PhotoButton.2
            @Override // android.view.View.OnLongClickListener
            public boolean onLongClick(View view) {
                if (PhotoButton.this.mFunctionListener != null) {
                    PhotoButton.this.mFunctionListener.onClickFunction(PhotoButton.this.mMediaDataListId, PhotoButton.this.mClickLongFunction);
                }
                if (PhotoButton.this.mOnLongClickListener == null) {
                    return true;
                }
                PhotoButton.this.mOnLongClickListener.onLongClick(view);
                return true;
            }
        };
    }

    public PhotoButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mClickLongFunction = -1;
        this.mText = "";
        this.mSelected = null;
        this.mOnClick = new View.OnClickListener() { // from class: com.yftech.car.widget.media.photo.button.PhotoButton.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (PhotoButton.this.mFunctionListener != null) {
                    PhotoButton.this.mFunctionListener.onClickFunction(PhotoButton.this.mMediaDataListId, PhotoButton.this.mClickFunction);
                }
                if (PhotoButton.this.mOnClickListener != null) {
                    PhotoButton.this.mOnClickListener.onClick(view);
                }
            }
        };
        this.mOnLongClick = new View.OnLongClickListener() { // from class: com.yftech.car.widget.media.photo.button.PhotoButton.2
            @Override // android.view.View.OnLongClickListener
            public boolean onLongClick(View view) {
                if (PhotoButton.this.mFunctionListener != null) {
                    PhotoButton.this.mFunctionListener.onClickFunction(PhotoButton.this.mMediaDataListId, PhotoButton.this.mClickLongFunction);
                }
                if (PhotoButton.this.mOnLongClickListener == null) {
                    return true;
                }
                PhotoButton.this.mOnLongClickListener.onLongClick(view);
                return true;
            }
        };
        init(context, attributeSet);
    }

    public PhotoButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mClickLongFunction = -1;
        this.mText = "";
        this.mSelected = null;
        this.mOnClick = new View.OnClickListener() { // from class: com.yftech.car.widget.media.photo.button.PhotoButton.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (PhotoButton.this.mFunctionListener != null) {
                    PhotoButton.this.mFunctionListener.onClickFunction(PhotoButton.this.mMediaDataListId, PhotoButton.this.mClickFunction);
                }
                if (PhotoButton.this.mOnClickListener != null) {
                    PhotoButton.this.mOnClickListener.onClick(view);
                }
            }
        };
        this.mOnLongClick = new View.OnLongClickListener() { // from class: com.yftech.car.widget.media.photo.button.PhotoButton.2
            @Override // android.view.View.OnLongClickListener
            public boolean onLongClick(View view) {
                if (PhotoButton.this.mFunctionListener != null) {
                    PhotoButton.this.mFunctionListener.onClickFunction(PhotoButton.this.mMediaDataListId, PhotoButton.this.mClickLongFunction);
                }
                if (PhotoButton.this.mOnLongClickListener == null) {
                    return true;
                }
                PhotoButton.this.mOnLongClickListener.onLongClick(view);
                return true;
            }
        };
        init(context, attributeSet);
    }

    private void init(Context context, AttributeSet attributeSet) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.PhotoButton);
        this.mClickFunction = typedArrayObtainStyledAttributes.getInt(R.styleable.PhotoButton_photo_click_function, 1002);
        this.mClickLongFunction = typedArrayObtainStyledAttributes.getInt(R.styleable.PhotoButton_photo_long_click_function, -1);
        this.mMediaDataListId = typedArrayObtainStyledAttributes.getResourceId(R.styleable.PhotoButton_photo_media_data_list_id, -1);
        this.mDrawableSelected = typedArrayObtainStyledAttributes.getDrawable(R.styleable.PhotoButton_photo_background_activated);
        this.mSelected = typedArrayObtainStyledAttributes.getString(R.styleable.PhotoButton_photo_activated_text);
    }

    public boolean isFunction(int i) {
        return this.mClickFunction == i || this.mClickLongFunction == i;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        CharSequence text = getText();
        if (text != null) {
            this.mText = text.toString();
        }
        this.mDrawable = getBackground();
        setOnClickListener(this.mOnClick);
        if (this.mClickLongFunction != -1) {
            setOnLongClickListener(this.mOnLongClick);
        }
    }

    public void setFunctionListener(OnClickFunctionListener onClickFunctionListener) {
        this.mFunctionListener = onClickFunctionListener;
    }

    @Override // android.view.View
    public void setOnClickListener(@Nullable View.OnClickListener onClickListener) {
        if (onClickListener == this.mOnClick) {
            super.setOnClickListener(onClickListener);
        } else {
            this.mOnClickListener = onClickListener;
        }
    }

    @Override // android.view.View
    public void setOnLongClickListener(@Nullable View.OnLongClickListener onLongClickListener) {
        if (this.mOnLongClick == onLongClickListener) {
            super.setOnLongClickListener(onLongClickListener);
        } else {
            this.mOnLongClickListener = onLongClickListener;
        }
    }
}
