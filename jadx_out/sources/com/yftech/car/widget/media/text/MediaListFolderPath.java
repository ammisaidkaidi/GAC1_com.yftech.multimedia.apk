package com.yftech.car.widget.media.text;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import com.yftech.car.widget.media.R;

/* JADX INFO: loaded from: classes3.dex */
public class MediaListFolderPath extends AppCompatTextView {
    private static final String TAG = "MediaListFolderPath";
    private AbsoluteSizeSpan mAbsoluteSizeSelectedSpan;
    private AbsoluteSizeSpan mAbsoluteSizeSpan;
    private boolean mClickBack;
    private boolean mClickFolder;
    private ForegroundColorSpan mColorSelectedSpan;
    private ForegroundColorSpan mColorSpan;
    private String mCurrentPathText;
    private boolean mDirectoryInvisible;
    private int mDirectoryInvisibleValue;
    private boolean mDirectoryVisible;
    private String mEndContent;
    private boolean mListFolderPathShowFolderPath;
    private int mListId;
    private String mRootDirectoryName;
    private int mSelectedColor;
    private int mSelectedSize;
    private SpannableStringBuilder mSpannable;
    private String mStartContent;

    public MediaListFolderPath(Context context) {
        super(context);
    }

    public MediaListFolderPath(Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public MediaListFolderPath(Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        int defaultColor = getTextColors().getDefaultColor();
        int textSize = (int) getTextSize();
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.MediaListFolderPath);
        this.mSelectedColor = typedArrayObtainStyledAttributes.getColor(R.styleable.MediaListFolderPath_ListFolderPath_selected_color, defaultColor);
        this.mSelectedSize = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MediaListFolderPath_ListFolderPath_selected_size, textSize);
        this.mClickBack = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MediaListFolderPath_ListFolderPath_click_back, false);
        this.mClickFolder = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MediaListFolderPath_ListFolderPath_click_folder, false);
        this.mDirectoryInvisible = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MediaListFolderPath_ListFolderPath_directory_invisible, false);
        this.mDirectoryInvisibleValue = typedArrayObtainStyledAttributes.getInt(R.styleable.MediaListFolderPath_ListFolderPath_directory_root_value, -1);
        this.mDirectoryVisible = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MediaListFolderPath_ListFolderPath_directory_visible, false);
        this.mListFolderPathShowFolderPath = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MediaListFolderPath_ListFolderPath_show_folder_path, true);
        this.mListId = typedArrayObtainStyledAttributes.getResourceId(R.styleable.MediaListFolderPath_ListFolderPath_list_id, -1);
        this.mRootDirectoryName = typedArrayObtainStyledAttributes.getString(R.styleable.MediaListFolderPath_ListFolderPath_root_directory_name);
        if (TextUtils.isEmpty(this.mRootDirectoryName)) {
            this.mRootDirectoryName = "";
        } else {
            this.mRootDirectoryName += "/";
        }
        this.mStartContent = typedArrayObtainStyledAttributes.getString(R.styleable.MediaListFolderPath_ListFolderPath_start_content);
        this.mEndContent = typedArrayObtainStyledAttributes.getString(R.styleable.MediaListFolderPath_ListFolderPath_end_content);
        this.mColorSpan = new ForegroundColorSpan(defaultColor);
        this.mAbsoluteSizeSpan = new AbsoluteSizeSpan(textSize);
        this.mColorSelectedSpan = new ForegroundColorSpan(this.mSelectedColor);
        this.mAbsoluteSizeSelectedSpan = new AbsoluteSizeSpan(this.mSelectedSize);
    }

    public String getCurrentPathText() {
        return this.mCurrentPathText;
    }

    public String getEndContent() {
        return this.mEndContent;
    }

    public int getListId() {
        return this.mListId;
    }

    public String getRootDirectoryName() {
        return this.mRootDirectoryName;
    }

    public int getSelectedColor() {
        return this.mSelectedColor;
    }

    public int getSelectedSize() {
        return this.mSelectedSize;
    }

    public String getStartContent() {
        return this.mStartContent;
    }

    public boolean isClickBack() {
        return this.mClickBack;
    }

    public boolean isClickFolder() {
        return this.mClickFolder;
    }

    public boolean isDirectoryInvisible() {
        if (this.mDirectoryInvisibleValue == -1) {
            return this.mDirectoryInvisible;
        }
        setVisibility(this.mDirectoryInvisibleValue);
        return false;
    }

    public boolean isDirectoryVisible() {
        return this.mDirectoryVisible;
    }

    public void setFolderPathText(String str, String str2) {
        String strSubstring;
        int length;
        if (this.mListFolderPathShowFolderPath) {
            if (this.mSpannable == null) {
                this.mSpannable = new SpannableStringBuilder();
            } else {
                this.mSpannable.clear();
            }
            if (TextUtils.isEmpty(str2)) {
                setText("");
                return;
            }
            int length2 = str.length();
            int i = str2.length() > length2 ? length2 + 1 : 0;
            if (i == 0) {
                strSubstring = this.mRootDirectoryName.length() > 0 ? this.mRootDirectoryName.substring(0, this.mRootDirectoryName.length() - 1) : "";
            } else {
                strSubstring = this.mRootDirectoryName + str2.substring(i);
            }
            this.mCurrentPathText = strSubstring;
            int iLastIndexOf = strSubstring.lastIndexOf("/");
            Log.d(TAG, "setFolderPathText: text==" + strSubstring + ",endIndex==" + iLastIndexOf);
            if (TextUtils.isEmpty(this.mStartContent)) {
                length = 0;
            } else {
                this.mSpannable.append((CharSequence) this.mStartContent);
                length = this.mStartContent.length();
                this.mSpannable.setSpan(this.mColorSpan, 0, length, 33);
                this.mSpannable.setSpan(this.mAbsoluteSizeSpan, 0, length, 33);
            }
            if (iLastIndexOf == -1) {
                this.mSpannable.append((CharSequence) strSubstring);
                this.mSpannable.setSpan(this.mColorSelectedSpan, length, strSubstring.length() + length, 33);
                this.mSpannable.setSpan(this.mAbsoluteSizeSelectedSpan, length, strSubstring.length() + length, 33);
            } else {
                this.mSpannable.append((CharSequence) strSubstring.substring(0, iLastIndexOf));
                int i2 = iLastIndexOf + length;
                this.mSpannable.setSpan(this.mColorSpan, length, i2, 33);
                this.mSpannable.setSpan(this.mAbsoluteSizeSpan, length, i2, 33);
                String strSubstring2 = strSubstring.substring(iLastIndexOf);
                this.mSpannable.append((CharSequence) strSubstring2);
                this.mSpannable.setSpan(this.mColorSelectedSpan, i2, strSubstring2.length() + iLastIndexOf + length, 33);
                this.mSpannable.setSpan(this.mAbsoluteSizeSelectedSpan, i2, iLastIndexOf + strSubstring2.length() + length, 33);
            }
            if (!TextUtils.isEmpty(this.mEndContent)) {
                int length3 = length + strSubstring.length();
                this.mSpannable.append((CharSequence) this.mEndContent);
                this.mSpannable.setSpan(this.mColorSpan, length3, this.mEndContent.length() + length3, 33);
                this.mSpannable.setSpan(this.mAbsoluteSizeSpan, length3, this.mEndContent.length() + length3, 33);
            }
            setText(this.mSpannable);
        }
    }

    @Override // android.view.View
    public void setVisibility(int i) {
        super.setVisibility(i);
    }
}
