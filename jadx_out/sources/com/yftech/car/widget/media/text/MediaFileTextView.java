package com.yftech.car.widget.media.text;

import android.content.Context;
import android.content.res.TypedArray;
import android.provider.MediaStore;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import com.yftech.car.media.data.entity.MediaFile;
import com.yftech.car.media.music.CarMusicManager;
import com.yftech.car.widget.media.R;
import com.yftech.car.widget.media.utils.FileUtil;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes3.dex */
public class MediaFileTextView extends AppCompatTextView {
    private static final int ALBUM = 3;
    private static final int ARTIST = 2;
    private static final int DIRECTORY_FILE_SIZE = 13;
    private static final int DIRECTORY_NAME = 14;
    private static final int DURATION = 8;
    private static final int FILE_NAME = 5;
    private static final int FILE_SIZE = 10;
    private static final int FOLDER = 6;
    private static final int FOLDER_PATH = 11;
    private static final int FOLDER_SIZE = 12;
    private static final long GB = 1073741824;
    private static final int GENRE = 4;
    private static final int HOUR = 3600000;
    private static final int KB = 1024;
    private static final int LAST_MODIFIED = 9;
    private static final int MB = 1048576;
    private static final int MINUTE = 60000;
    private static final int NUMBER = 7;
    private static final String TAG = "MediaFileTextView";
    private static final long TB = 1099511627776L;
    private static final int TITLE = 1;
    private AbsoluteSizeSpan mAbsoluteSearchSizeSpan;
    private final Date mDate;
    private int mDecimal;
    private SimpleDateFormat mDurationSimpleDateFormat;
    private SimpleDateFormat mDurationSimpleDateFormat3;
    private boolean mFolderAddFile;
    private SimpleDateFormat mLastModifiedSimpleDateFormat;
    private final List<MediaFileInfoItem> mList;
    private MediaFile mMediaFile;
    private String mNumberSplitContent;
    private int mSearchColor;
    private ForegroundColorSpan mSearchColorSpan;
    private SpannableStringBuilder mSpannable;

    /* JADX INFO: Access modifiers changed from: private */
    class MediaFileInfoItem {
        private AbsoluteSizeSpan mAbsoluteSizeSelectedSpan;
        private AbsoluteSizeSpan mAbsoluteSizeSpan;
        private ForegroundColorSpan mColorSelectedSpan;
        private ForegroundColorSpan mColorSpan;
        private String mEndContent;
        private final int mInfoType;
        private String mLastModifiedDateToday;
        private String mLastModifiedDateYesterday;
        private String mLastModifiedDateYesterdayBefore;
        private int mLastModifiedPatternMode;
        private boolean mLineFeed;
        private String mPattern;
        private String mStartContent;
        private String mUnknown;
        private int position;

        private MediaFileInfoItem(int i) {
            this.mInfoType = i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setAbsoluteSizeSelectedSpan(int i) {
            if (i > 0) {
                this.mAbsoluteSizeSelectedSpan = new AbsoluteSizeSpan(i);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setAbsoluteSizeSpan(int i) {
            this.mAbsoluteSizeSpan = new AbsoluteSizeSpan(i);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setColorSelectedSpan(int i) {
            if (i != 0) {
                this.mColorSelectedSpan = new ForegroundColorSpan(i);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setColorSpan(int i) {
            this.mColorSpan = new ForegroundColorSpan(i);
        }

        public void setUnknown(String str) {
            this.mUnknown = str;
        }
    }

    public MediaFileTextView(Context context) {
        super(context);
        this.mList = new ArrayList();
        this.mDate = new Date();
    }

    public MediaFileTextView(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mList = new ArrayList();
        this.mDate = new Date();
        init(context, attributeSet);
    }

    public MediaFileTextView(Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mList = new ArrayList();
        this.mDate = new Date();
        init(context, attributeSet);
    }

    private String getMediaFileInfo(String str, String str2) {
        if (str2 == null) {
            return str;
        }
        return (str == null || MediaStore.UNKNOWN_STRING.equals(str)) ? str2 : str;
    }

    private String handleLastModified(MediaFile mediaFile, MediaFileInfoItem mediaFileInfoItem) {
        if (mediaFileInfoItem.mLastModifiedPatternMode != 2) {
            if (mediaFileInfoItem.mLastModifiedPatternMode == 1) {
                mediaFileInfoItem.mPattern = "EEEE";
            }
            if (TextUtils.isEmpty(mediaFileInfoItem.mPattern)) {
                mediaFileInfoItem.mPattern = "yyyy-MM-dd";
            }
            if (this.mLastModifiedSimpleDateFormat == null) {
                this.mLastModifiedSimpleDateFormat = new SimpleDateFormat(mediaFileInfoItem.mPattern, Locale.getDefault(Locale.Category.FORMAT));
                this.mLastModifiedSimpleDateFormat.setTimeZone(TimeZone.getDefault());
            }
            this.mDate.setTime(mediaFile.getLastModified());
            return this.mLastModifiedSimpleDateFormat.format(this.mDate);
        }
        int iCurrentTimeMillis = (int) ((((System.currentTimeMillis() / 1000.0d) / 60.0d) / 60.0d) / 24.0d);
        int lastModified = (int) ((((mediaFile.getLastModified() / 1000.0d) / 60.0d) / 60.0d) / 24.0d);
        if (iCurrentTimeMillis == lastModified) {
            return TextUtils.isEmpty(mediaFileInfoItem.mLastModifiedDateToday) ? "今天" : mediaFileInfoItem.mLastModifiedDateToday;
        }
        if (lastModified == iCurrentTimeMillis - 1) {
            return TextUtils.isEmpty(mediaFileInfoItem.mLastModifiedDateYesterday) ? "昨天" : mediaFileInfoItem.mLastModifiedDateYesterday;
        }
        if (lastModified == iCurrentTimeMillis - 2) {
            return TextUtils.isEmpty(mediaFileInfoItem.mLastModifiedDateYesterdayBefore) ? "前天" : mediaFileInfoItem.mLastModifiedDateYesterdayBefore;
        }
        if (TextUtils.isEmpty(mediaFileInfoItem.mPattern)) {
            mediaFileInfoItem.mPattern = "yyyy-MM-dd";
        }
        if (this.mLastModifiedSimpleDateFormat == null) {
            this.mLastModifiedSimpleDateFormat = new SimpleDateFormat(mediaFileInfoItem.mPattern, Locale.getDefault(Locale.Category.FORMAT));
            this.mLastModifiedSimpleDateFormat.setTimeZone(TimeZone.getDefault());
        }
        this.mDate.setTime(mediaFile.getLastModified());
        return this.mLastModifiedSimpleDateFormat.format(this.mDate);
    }

    private void init(Context context, AttributeSet attributeSet) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.MediaTextView);
        int defaultColor = getTextColors().getDefaultColor();
        int textSize = (int) getTextSize();
        this.mList.clear();
        int i = typedArrayObtainStyledAttributes.getInt(R.styleable.MediaTextView_title_position, -1);
        if (i != -1) {
            MediaFileInfoItem mediaFileInfoItem = new MediaFileInfoItem(1);
            mediaFileInfoItem.position = i;
            mediaFileInfoItem.setColorSpan(typedArrayObtainStyledAttributes.getColor(R.styleable.MediaTextView_title_text_color, defaultColor));
            mediaFileInfoItem.setAbsoluteSizeSpan(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MediaTextView_title_text_size, textSize));
            mediaFileInfoItem.setColorSelectedSpan(typedArrayObtainStyledAttributes.getColor(R.styleable.MediaTextView_title_text_selected_color, 0));
            mediaFileInfoItem.setAbsoluteSizeSelectedSpan(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MediaTextView_title_text_selected_size, 0));
            mediaFileInfoItem.mLineFeed = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MediaTextView_title_line_feed, false);
            mediaFileInfoItem.setUnknown(typedArrayObtainStyledAttributes.getString(R.styleable.MediaTextView_title_text_unknown));
            mediaFileInfoItem.mStartContent = typedArrayObtainStyledAttributes.getString(R.styleable.MediaTextView_title_start_content);
            mediaFileInfoItem.mEndContent = typedArrayObtainStyledAttributes.getString(R.styleable.MediaTextView_title_end_content);
            this.mList.add(mediaFileInfoItem);
        }
        int i2 = typedArrayObtainStyledAttributes.getInt(R.styleable.MediaTextView_artist_position, -1);
        if (i2 != -1) {
            MediaFileInfoItem mediaFileInfoItem2 = new MediaFileInfoItem(2);
            mediaFileInfoItem2.position = i2;
            mediaFileInfoItem2.mLineFeed = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MediaTextView_artist_line_feed, false);
            mediaFileInfoItem2.setColorSpan(typedArrayObtainStyledAttributes.getColor(R.styleable.MediaTextView_artist_text_color, defaultColor));
            mediaFileInfoItem2.setAbsoluteSizeSpan(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MediaTextView_artist_text_size, textSize));
            mediaFileInfoItem2.setColorSelectedSpan(typedArrayObtainStyledAttributes.getColor(R.styleable.MediaTextView_artist_text_selected_color, 0));
            mediaFileInfoItem2.setAbsoluteSizeSelectedSpan(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MediaTextView_artist_text_selected_size, 0));
            mediaFileInfoItem2.setUnknown(typedArrayObtainStyledAttributes.getString(R.styleable.MediaTextView_artist_text_unknown));
            mediaFileInfoItem2.mStartContent = typedArrayObtainStyledAttributes.getString(R.styleable.MediaTextView_artist_start_content);
            mediaFileInfoItem2.mEndContent = typedArrayObtainStyledAttributes.getString(R.styleable.MediaTextView_artist_end_content);
            this.mList.add(mediaFileInfoItem2);
        }
        int i3 = typedArrayObtainStyledAttributes.getInt(R.styleable.MediaTextView_album_position, -1);
        if (i3 != -1) {
            MediaFileInfoItem mediaFileInfoItem3 = new MediaFileInfoItem(3);
            mediaFileInfoItem3.position = i3;
            mediaFileInfoItem3.mLineFeed = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MediaTextView_album_line_feed, false);
            mediaFileInfoItem3.setColorSpan(typedArrayObtainStyledAttributes.getColor(R.styleable.MediaTextView_album_text_color, defaultColor));
            mediaFileInfoItem3.setAbsoluteSizeSpan(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MediaTextView_album_text_size, textSize));
            mediaFileInfoItem3.setColorSelectedSpan(typedArrayObtainStyledAttributes.getColor(R.styleable.MediaTextView_album_text_selected_color, 0));
            mediaFileInfoItem3.setAbsoluteSizeSelectedSpan(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MediaTextView_album_text_selected_size, 0));
            mediaFileInfoItem3.setUnknown(typedArrayObtainStyledAttributes.getString(R.styleable.MediaTextView_album_text_unknown));
            mediaFileInfoItem3.mStartContent = typedArrayObtainStyledAttributes.getString(R.styleable.MediaTextView_album_start_content);
            mediaFileInfoItem3.mEndContent = typedArrayObtainStyledAttributes.getString(R.styleable.MediaTextView_album_end_content);
            this.mList.add(mediaFileInfoItem3);
        }
        int i4 = typedArrayObtainStyledAttributes.getInt(R.styleable.MediaTextView_genre_position, -1);
        if (i4 != -1) {
            MediaFileInfoItem mediaFileInfoItem4 = new MediaFileInfoItem(4);
            mediaFileInfoItem4.position = i4;
            mediaFileInfoItem4.mLineFeed = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MediaTextView_genre_line_feed, false);
            mediaFileInfoItem4.setColorSpan(typedArrayObtainStyledAttributes.getColor(R.styleable.MediaTextView_genre_text_color, defaultColor));
            mediaFileInfoItem4.setAbsoluteSizeSpan(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MediaTextView_genre_text_size, textSize));
            mediaFileInfoItem4.setColorSelectedSpan(typedArrayObtainStyledAttributes.getColor(R.styleable.MediaTextView_genre_text_selected_color, 0));
            mediaFileInfoItem4.setAbsoluteSizeSelectedSpan(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MediaTextView_genre_text_selected_size, 0));
            mediaFileInfoItem4.setUnknown(typedArrayObtainStyledAttributes.getString(R.styleable.MediaTextView_genre_text_unknown));
            mediaFileInfoItem4.mStartContent = typedArrayObtainStyledAttributes.getString(R.styleable.MediaTextView_genre_start_content);
            mediaFileInfoItem4.mEndContent = typedArrayObtainStyledAttributes.getString(R.styleable.MediaTextView_genre_end_content);
            this.mList.add(mediaFileInfoItem4);
        }
        int i5 = typedArrayObtainStyledAttributes.getInt(R.styleable.MediaTextView_fileName_position, -1);
        if (i5 != -1) {
            MediaFileInfoItem mediaFileInfoItem5 = new MediaFileInfoItem(5);
            mediaFileInfoItem5.position = i5;
            mediaFileInfoItem5.mLineFeed = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MediaTextView_fileName_line_feed, false);
            mediaFileInfoItem5.setColorSpan(typedArrayObtainStyledAttributes.getColor(R.styleable.MediaTextView_fileName_text_color, defaultColor));
            mediaFileInfoItem5.setAbsoluteSizeSpan(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MediaTextView_fileName_text_size, textSize));
            mediaFileInfoItem5.setColorSelectedSpan(typedArrayObtainStyledAttributes.getColor(R.styleable.MediaTextView_fileName_text_selected_color, 0));
            mediaFileInfoItem5.setAbsoluteSizeSelectedSpan(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MediaTextView_fileName_text_selected_size, 0));
            mediaFileInfoItem5.mStartContent = typedArrayObtainStyledAttributes.getString(R.styleable.MediaTextView_fileName_start_content);
            mediaFileInfoItem5.mEndContent = typedArrayObtainStyledAttributes.getString(R.styleable.MediaTextView_fileName_end_content);
            this.mList.add(mediaFileInfoItem5);
        }
        int i6 = typedArrayObtainStyledAttributes.getInt(R.styleable.MediaTextView_folder_position, -1);
        if (i6 != -1) {
            MediaFileInfoItem mediaFileInfoItem6 = new MediaFileInfoItem(6);
            mediaFileInfoItem6.position = i6;
            mediaFileInfoItem6.mLineFeed = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MediaTextView_folder_line_feed, false);
            mediaFileInfoItem6.setColorSpan(typedArrayObtainStyledAttributes.getColor(R.styleable.MediaTextView_folder_text_color, defaultColor));
            mediaFileInfoItem6.setAbsoluteSizeSpan(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MediaTextView_folder_text_size, textSize));
            mediaFileInfoItem6.setColorSelectedSpan(typedArrayObtainStyledAttributes.getColor(R.styleable.MediaTextView_folder_text_selected_color, 0));
            mediaFileInfoItem6.setAbsoluteSizeSelectedSpan(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MediaTextView_folder_text_selected_size, 0));
            mediaFileInfoItem6.mStartContent = typedArrayObtainStyledAttributes.getString(R.styleable.MediaTextView_folder_start_content);
            mediaFileInfoItem6.mEndContent = typedArrayObtainStyledAttributes.getString(R.styleable.MediaTextView_folder_end_content);
            this.mList.add(mediaFileInfoItem6);
        }
        int i7 = typedArrayObtainStyledAttributes.getInt(R.styleable.MediaTextView_folder_directory_position, -1);
        if (i7 != -1) {
            MediaFileInfoItem mediaFileInfoItem7 = new MediaFileInfoItem(11);
            mediaFileInfoItem7.position = i7;
            mediaFileInfoItem7.mLineFeed = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MediaTextView_folder_directory_line_feed, false);
            mediaFileInfoItem7.setColorSpan(typedArrayObtainStyledAttributes.getColor(R.styleable.MediaTextView_folder_directory_text_color, defaultColor));
            mediaFileInfoItem7.setAbsoluteSizeSpan(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MediaTextView_folder_directory_text_size, textSize));
            mediaFileInfoItem7.setColorSelectedSpan(typedArrayObtainStyledAttributes.getColor(R.styleable.MediaTextView_folder_directory_text_selected_color, 0));
            mediaFileInfoItem7.setAbsoluteSizeSelectedSpan(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MediaTextView_folder_directory_text_selected_size, 0));
            mediaFileInfoItem7.mStartContent = typedArrayObtainStyledAttributes.getString(R.styleable.MediaTextView_folder_directory_start_content);
            mediaFileInfoItem7.mEndContent = typedArrayObtainStyledAttributes.getString(R.styleable.MediaTextView_folder_directory_end_content);
            this.mList.add(mediaFileInfoItem7);
        }
        int i8 = typedArrayObtainStyledAttributes.getInt(R.styleable.MediaTextView_number_position, -1);
        if (i8 != -1) {
            MediaFileInfoItem mediaFileInfoItem8 = new MediaFileInfoItem(7);
            mediaFileInfoItem8.position = i8;
            mediaFileInfoItem8.mLineFeed = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MediaTextView_number_line_feed, false);
            mediaFileInfoItem8.setColorSpan(typedArrayObtainStyledAttributes.getColor(R.styleable.MediaTextView_number_text_color, defaultColor));
            mediaFileInfoItem8.setAbsoluteSizeSpan(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MediaTextView_number_text_size, textSize));
            mediaFileInfoItem8.setColorSelectedSpan(typedArrayObtainStyledAttributes.getColor(R.styleable.MediaTextView_number_text_selected_color, 0));
            mediaFileInfoItem8.setAbsoluteSizeSelectedSpan(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MediaTextView_number_text_selected_size, 0));
            mediaFileInfoItem8.mStartContent = typedArrayObtainStyledAttributes.getString(R.styleable.MediaTextView_number_start_content);
            mediaFileInfoItem8.mEndContent = typedArrayObtainStyledAttributes.getString(R.styleable.MediaTextView_number_end_content);
            this.mNumberSplitContent = typedArrayObtainStyledAttributes.getString(R.styleable.MediaTextView_number_split_content);
            this.mList.add(mediaFileInfoItem8);
        }
        int i9 = typedArrayObtainStyledAttributes.getInt(R.styleable.MediaTextView_duration_file_position, -1);
        if (i9 != -1) {
            MediaFileInfoItem mediaFileInfoItem9 = new MediaFileInfoItem(8);
            mediaFileInfoItem9.position = i9;
            mediaFileInfoItem9.mLineFeed = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MediaTextView_duration_file_line_feed, false);
            mediaFileInfoItem9.setColorSpan(typedArrayObtainStyledAttributes.getColor(R.styleable.MediaTextView_duration_file_text_color, defaultColor));
            mediaFileInfoItem9.setAbsoluteSizeSpan(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MediaTextView_duration_file_text_size, textSize));
            mediaFileInfoItem9.setColorSelectedSpan(typedArrayObtainStyledAttributes.getColor(R.styleable.MediaTextView_duration_file_text_selected_color, 0));
            mediaFileInfoItem9.setAbsoluteSizeSelectedSpan(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MediaTextView_duration_file_text_selected_size, 0));
            mediaFileInfoItem9.mStartContent = typedArrayObtainStyledAttributes.getString(R.styleable.MediaTextView_duration_file_start_content);
            mediaFileInfoItem9.mEndContent = typedArrayObtainStyledAttributes.getString(R.styleable.MediaTextView_duration_file_end_content);
            mediaFileInfoItem9.mPattern = typedArrayObtainStyledAttributes.getString(R.styleable.MediaTextView_duration_file_SimpleDateFormat_pattern);
            this.mList.add(mediaFileInfoItem9);
        }
        int i10 = typedArrayObtainStyledAttributes.getInt(R.styleable.MediaTextView_lastModified_position, -1);
        if (i10 != -1) {
            MediaFileInfoItem mediaFileInfoItem10 = new MediaFileInfoItem(9);
            mediaFileInfoItem10.position = i10;
            mediaFileInfoItem10.mLineFeed = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MediaTextView_lastModified_line_feed, false);
            mediaFileInfoItem10.setColorSpan(typedArrayObtainStyledAttributes.getColor(R.styleable.MediaTextView_lastModified_text_color, defaultColor));
            mediaFileInfoItem10.setAbsoluteSizeSpan(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MediaTextView_lastModified_text_size, textSize));
            mediaFileInfoItem10.setColorSelectedSpan(typedArrayObtainStyledAttributes.getColor(R.styleable.MediaTextView_lastModified_text_selected_color, 0));
            mediaFileInfoItem10.setAbsoluteSizeSelectedSpan(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MediaTextView_lastModified_text_selected_size, 0));
            mediaFileInfoItem10.mStartContent = typedArrayObtainStyledAttributes.getString(R.styleable.MediaTextView_lastModified_start_content);
            mediaFileInfoItem10.mEndContent = typedArrayObtainStyledAttributes.getString(R.styleable.MediaTextView_lastModified_end_content);
            mediaFileInfoItem10.mLastModifiedPatternMode = typedArrayObtainStyledAttributes.getInt(R.styleable.MediaTextView_lastModified_pattern_mode, 0);
            mediaFileInfoItem10.mPattern = typedArrayObtainStyledAttributes.getString(R.styleable.MediaTextView_lastModified_SimpleDateFormat_pattern);
            mediaFileInfoItem10.mLastModifiedDateToday = typedArrayObtainStyledAttributes.getString(R.styleable.MediaTextView_lastModified_date_today);
            mediaFileInfoItem10.mLastModifiedDateYesterday = typedArrayObtainStyledAttributes.getString(R.styleable.MediaTextView_lastModified_date_yesterday);
            mediaFileInfoItem10.mLastModifiedDateYesterdayBefore = typedArrayObtainStyledAttributes.getString(R.styleable.MediaTextView_lastModified_date_yesterday_before);
            this.mList.add(mediaFileInfoItem10);
        }
        int i11 = typedArrayObtainStyledAttributes.getInt(R.styleable.MediaTextView_size_position, -1);
        if (i11 != -1) {
            MediaFileInfoItem mediaFileInfoItem11 = new MediaFileInfoItem(10);
            mediaFileInfoItem11.position = i11;
            mediaFileInfoItem11.mLineFeed = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MediaTextView_size_line_feed, false);
            this.mDecimal = typedArrayObtainStyledAttributes.getInt(R.styleable.MediaTextView_size_decimal, 0);
            mediaFileInfoItem11.setColorSpan(typedArrayObtainStyledAttributes.getColor(R.styleable.MediaTextView_size_text_color, defaultColor));
            mediaFileInfoItem11.setAbsoluteSizeSpan(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MediaTextView_size_text_size, textSize));
            mediaFileInfoItem11.setColorSelectedSpan(typedArrayObtainStyledAttributes.getColor(R.styleable.MediaTextView_size_text_selected_color, 0));
            mediaFileInfoItem11.setAbsoluteSizeSelectedSpan(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MediaTextView_size_text_selected_size, 0));
            mediaFileInfoItem11.mStartContent = typedArrayObtainStyledAttributes.getString(R.styleable.MediaTextView_size_start_content);
            mediaFileInfoItem11.mEndContent = typedArrayObtainStyledAttributes.getString(R.styleable.MediaTextView_size_end_content);
            this.mList.add(mediaFileInfoItem11);
        }
        int i12 = typedArrayObtainStyledAttributes.getInt(R.styleable.MediaTextView_folder_size_position, -1);
        if (i12 != -1) {
            MediaFileInfoItem mediaFileInfoItem12 = new MediaFileInfoItem(12);
            mediaFileInfoItem12.position = i12;
            this.mFolderAddFile = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MediaTextView_folder_size_add_file, false);
            mediaFileInfoItem12.mLineFeed = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MediaTextView_folder_size_line_feed, false);
            mediaFileInfoItem12.setColorSpan(typedArrayObtainStyledAttributes.getColor(R.styleable.MediaTextView_folder_size_text_color, defaultColor));
            mediaFileInfoItem12.setAbsoluteSizeSpan(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MediaTextView_folder_size_text_size, textSize));
            mediaFileInfoItem12.setColorSelectedSpan(typedArrayObtainStyledAttributes.getColor(R.styleable.MediaTextView_folder_size_text_selected_color, 0));
            mediaFileInfoItem12.setAbsoluteSizeSelectedSpan(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MediaTextView_folder_size_text_selected_size, 0));
            mediaFileInfoItem12.mStartContent = typedArrayObtainStyledAttributes.getString(R.styleable.MediaTextView_folder_size_start_content);
            mediaFileInfoItem12.mEndContent = typedArrayObtainStyledAttributes.getString(R.styleable.MediaTextView_folder_size_end_content);
            this.mList.add(mediaFileInfoItem12);
        }
        int i13 = typedArrayObtainStyledAttributes.getInt(R.styleable.MediaTextView_directory_file_size_position, -1);
        if (i13 != -1) {
            MediaFileInfoItem mediaFileInfoItem13 = new MediaFileInfoItem(13);
            mediaFileInfoItem13.position = i13;
            mediaFileInfoItem13.mLineFeed = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MediaTextView_directory_file_size_line_feed, false);
            mediaFileInfoItem13.setColorSpan(typedArrayObtainStyledAttributes.getColor(R.styleable.MediaTextView_directory_file_size_text_color, defaultColor));
            mediaFileInfoItem13.setAbsoluteSizeSpan(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MediaTextView_directory_file_size_text_size, textSize));
            mediaFileInfoItem13.setColorSelectedSpan(typedArrayObtainStyledAttributes.getColor(R.styleable.MediaTextView_directory_file_size_text_selected_color, 0));
            mediaFileInfoItem13.setAbsoluteSizeSelectedSpan(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MediaTextView_directory_file_size_text_selected_size, 0));
            mediaFileInfoItem13.mStartContent = typedArrayObtainStyledAttributes.getString(R.styleable.MediaTextView_directory_file_size_start_content);
            mediaFileInfoItem13.mEndContent = typedArrayObtainStyledAttributes.getString(R.styleable.MediaTextView_directory_file_size_end_content);
            this.mList.add(mediaFileInfoItem13);
        }
        int i14 = typedArrayObtainStyledAttributes.getInt(R.styleable.MediaTextView_directory_name_position, -1);
        if (i14 != -1) {
            MediaFileInfoItem mediaFileInfoItem14 = new MediaFileInfoItem(14);
            mediaFileInfoItem14.position = i14;
            mediaFileInfoItem14.mLineFeed = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MediaTextView_directory_name_line_feed, false);
            mediaFileInfoItem14.setColorSpan(typedArrayObtainStyledAttributes.getColor(R.styleable.MediaTextView_directory_name_text_color, defaultColor));
            mediaFileInfoItem14.setAbsoluteSizeSpan(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MediaTextView_directory_name_text_size, textSize));
            mediaFileInfoItem14.setColorSelectedSpan(typedArrayObtainStyledAttributes.getColor(R.styleable.MediaTextView_directory_name_text_selected_color, 0));
            mediaFileInfoItem14.setAbsoluteSizeSelectedSpan(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MediaTextView_directory_name_text_selected_size, 0));
            mediaFileInfoItem14.mStartContent = typedArrayObtainStyledAttributes.getString(R.styleable.MediaTextView_directory_name_start_content);
            mediaFileInfoItem14.mEndContent = typedArrayObtainStyledAttributes.getString(R.styleable.MediaTextView_directory_name_end_content);
            this.mList.add(mediaFileInfoItem14);
        }
        this.mSearchColor = typedArrayObtainStyledAttributes.getColor(R.styleable.MediaTextView_search_size_text_color, 0);
        int dimensionPixelSize = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MediaTextView_search_size_text_size, textSize);
        if (dimensionPixelSize > 0) {
            this.mAbsoluteSearchSizeSpan = new AbsoluteSizeSpan(dimensionPixelSize);
        }
        this.mList.sort(new Comparator() { // from class: com.yftech.car.widget.media.text.-$$Lambda$MediaFileTextView$hur1sIXB_8LO5ZfAt6Y9IRMlQuY
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return MediaFileTextView.lambda$init$0((MediaFileTextView.MediaFileInfoItem) obj, (MediaFileTextView.MediaFileInfoItem) obj2);
            }
        });
    }

    static /* synthetic */ int lambda$init$0(MediaFileInfoItem mediaFileInfoItem, MediaFileInfoItem mediaFileInfoItem2) {
        return mediaFileInfoItem.position - mediaFileInfoItem2.position;
    }

    public MediaFile getPlayMediaFile() {
        return this.mMediaFile;
    }

    public boolean isMediaFileInfo() {
        return this.mSpannable == null || this.mSpannable.length() > 0;
    }

    @Override // android.widget.TextView, android.view.View
    public void setSelected(boolean z) {
        super.setSelected(z);
    }

    public void updateMediaFile(MediaFile mediaFile) {
        updateMediaFile(mediaFile, CarMusicManager.getInstance().getPlayPosition(), CarMusicManager.getInstance().getPlayListSize());
    }

    public void updateMediaFile(MediaFile mediaFile, int i, int i2) {
        updateMediaFile(mediaFile, i, i2, null);
    }

    public void updateMediaFile(MediaFile mediaFile, int i, int i2, String str) {
        this.mMediaFile = mediaFile;
        if (mediaFile == null) {
            Log.e(TAG, "updateMediaFile: file is null");
            return;
        }
        if (this.mSpannable == null) {
            this.mSpannable = new SpannableStringBuilder();
        } else {
            this.mSpannable.clear();
        }
        for (MediaFileInfoItem mediaFileInfoItem : this.mList) {
            String mediaFileInfo = null;
            if (mediaFile.getType() != 4) {
                switch (mediaFileInfoItem.mInfoType) {
                    case 1:
                        mediaFileInfo = getMediaFileInfo(mediaFile.getTitle(), mediaFileInfoItem.mUnknown);
                        break;
                    case 2:
                        mediaFileInfo = getMediaFileInfo(mediaFile.getArtist(), mediaFileInfoItem.mUnknown);
                        break;
                    case 3:
                        mediaFileInfo = getMediaFileInfo(mediaFile.getAlbum(), mediaFileInfoItem.mUnknown);
                        break;
                    case 4:
                        mediaFileInfo = getMediaFileInfo(mediaFile.getGenre(), mediaFileInfoItem.mUnknown);
                        break;
                    case 5:
                        mediaFileInfo = mediaFile.getFileName();
                        break;
                    case 6:
                        mediaFileInfo = mediaFile.getFolder();
                        break;
                    case 7:
                        if (i2 == -1) {
                            mediaFileInfo = (i + 1) + "";
                        } else {
                            if (this.mNumberSplitContent == null) {
                                this.mNumberSplitContent = "/";
                            }
                            mediaFileInfo = (i + 1) + this.mNumberSplitContent + i2;
                        }
                        break;
                    case 8:
                        if (TextUtils.isEmpty(mediaFileInfoItem.mPattern)) {
                            mediaFileInfoItem.mPattern = "HH:mm:ss";
                        }
                        long duration = mediaFile.getDuration();
                        this.mDate.setTime(duration);
                        if (duration < 3600000) {
                            if (this.mDurationSimpleDateFormat == null) {
                                this.mDurationSimpleDateFormat = new SimpleDateFormat(mediaFileInfoItem.mPattern, Locale.getDefault(Locale.Category.FORMAT));
                                this.mDurationSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
                            }
                            mediaFileInfo = this.mDurationSimpleDateFormat.format(this.mDate);
                        } else if (mediaFileInfoItem.mPattern.contains("H")) {
                            if (this.mDurationSimpleDateFormat == null) {
                                this.mDurationSimpleDateFormat = new SimpleDateFormat(mediaFileInfoItem.mPattern, Locale.getDefault(Locale.Category.FORMAT));
                                this.mDurationSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
                            }
                            mediaFileInfo = this.mDurationSimpleDateFormat.format(this.mDate);
                        } else {
                            if (this.mDurationSimpleDateFormat3 == null) {
                                if (mediaFileInfoItem.mPattern.contains("mm")) {
                                    this.mDurationSimpleDateFormat3 = new SimpleDateFormat("HH:mm:ss", Locale.getDefault(Locale.Category.FORMAT));
                                } else {
                                    this.mDurationSimpleDateFormat3 = new SimpleDateFormat("H:mm:ss", Locale.getDefault(Locale.Category.FORMAT));
                                }
                                this.mDurationSimpleDateFormat3.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
                            }
                            mediaFileInfo = this.mDurationSimpleDateFormat3.format(this.mDate);
                        }
                        break;
                    case 9:
                        mediaFileInfo = handleLastModified(mediaFile, mediaFileInfoItem);
                        break;
                    case 10:
                        mediaFileInfo = FileUtil.fileSize(mediaFile.getSize(), this.mDecimal);
                        break;
                    case 11:
                    case 12:
                    case 13:
                    case 14:
                        break;
                    default:
                        Log.e(TAG, "updateMediaFile: error info type == " + mediaFileInfoItem.mInfoType);
                        break;
                }
            } else {
                switch (mediaFileInfoItem.mInfoType) {
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 8:
                    case 9:
                    case 10:
                        break;
                    case 7:
                        mediaFileInfo = (i + 1) + "";
                        break;
                    case 11:
                        mediaFileInfo = mediaFile.getFolderPath();
                        break;
                    case 12:
                        mediaFileInfo = this.mFolderAddFile ? (mediaFile.getSize() + mediaFile.getLastModified()) + "" : mediaFile.getLastModified() + "";
                        break;
                    case 13:
                        mediaFileInfo = mediaFile.getSize() + "";
                        break;
                    case 14:
                        mediaFileInfo = mediaFile.getFolder();
                        break;
                    default:
                        Log.e(TAG, "updateMediaFile: directory error info type == " + mediaFileInfoItem.mInfoType);
                        break;
                }
            }
            if (mediaFileInfo != null) {
                if (TextUtils.isEmpty(str)) {
                    int length = this.mSpannable.length();
                    if (mediaFileInfoItem.mStartContent != null) {
                        mediaFileInfo = mediaFileInfoItem.mStartContent + mediaFileInfo;
                    }
                    if (mediaFileInfoItem.mEndContent != null) {
                        mediaFileInfo = mediaFileInfo + mediaFileInfoItem.mEndContent;
                    }
                    SpannableStringBuilder spannableStringBuilder = this.mSpannable;
                    if (mediaFileInfoItem.mLineFeed) {
                        mediaFileInfo = "\n" + mediaFileInfo;
                    }
                    spannableStringBuilder.append((CharSequence) mediaFileInfo);
                    int length2 = this.mSpannable.length();
                    if (!isSelected() || mediaFileInfoItem.mColorSelectedSpan == null) {
                        this.mSpannable.setSpan(mediaFileInfoItem.mColorSpan, length, length2, 33);
                    } else {
                        this.mSpannable.setSpan(mediaFileInfoItem.mColorSelectedSpan, length, length2, 33);
                    }
                    if (!isSelected() || mediaFileInfoItem.mAbsoluteSizeSelectedSpan == null) {
                        this.mSpannable.setSpan(mediaFileInfoItem.mAbsoluteSizeSpan, length, length2, 33);
                    } else {
                        this.mSpannable.setSpan(mediaFileInfoItem.mAbsoluteSizeSelectedSpan, length, length2, 33);
                    }
                } else {
                    int length3 = this.mSpannable.length();
                    int iIndexOf = mediaFileInfo.indexOf(str, -1);
                    if (iIndexOf != -1) {
                        iIndexOf += length3;
                    }
                    if (mediaFileInfoItem.mStartContent != null) {
                        mediaFileInfo = mediaFileInfoItem.mStartContent + mediaFileInfo;
                        if (iIndexOf != -1) {
                            iIndexOf += mediaFileInfoItem.mStartContent.length();
                        }
                    }
                    if (mediaFileInfoItem.mEndContent != null) {
                        mediaFileInfo = mediaFileInfo + mediaFileInfoItem.mEndContent;
                    }
                    SpannableStringBuilder spannableStringBuilder2 = this.mSpannable;
                    if (mediaFileInfoItem.mLineFeed) {
                        mediaFileInfo = "\n" + mediaFileInfo;
                    }
                    spannableStringBuilder2.append((CharSequence) mediaFileInfo);
                    int length4 = this.mSpannable.length();
                    if (iIndexOf != -1 && this.mSearchColor != 0) {
                        if (mediaFileInfoItem.mLineFeed) {
                            iIndexOf++;
                        }
                        int length5 = str.length() + iIndexOf;
                        if (!isSelected() || mediaFileInfoItem.mColorSelectedSpan == null) {
                            this.mSpannable.setSpan(mediaFileInfoItem.mColorSpan, length3, iIndexOf, 33);
                        } else {
                            this.mSpannable.setSpan(mediaFileInfoItem.mColorSelectedSpan, length3, iIndexOf, 33);
                        }
                        this.mSpannable.setSpan(new ForegroundColorSpan(this.mSearchColor), iIndexOf, length5, 33);
                        this.mSpannable.setSpan((!isSelected() || mediaFileInfoItem.mColorSelectedSpan == null) ? new ForegroundColorSpan(mediaFileInfoItem.mColorSpan.getForegroundColor()) : new ForegroundColorSpan(mediaFileInfoItem.mColorSelectedSpan.getForegroundColor()), length5, length4, 33);
                    } else if (!isSelected() || mediaFileInfoItem.mColorSelectedSpan == null) {
                        this.mSpannable.setSpan(mediaFileInfoItem.mColorSpan, length3, length4, 33);
                    } else {
                        this.mSpannable.setSpan(mediaFileInfoItem.mColorSelectedSpan, length3, length4, 33);
                    }
                    if (!isSelected() || mediaFileInfoItem.mAbsoluteSizeSelectedSpan == null) {
                        this.mSpannable.setSpan(mediaFileInfoItem.mAbsoluteSizeSpan, length3, length4, 33);
                    } else {
                        this.mSpannable.setSpan(mediaFileInfoItem.mAbsoluteSizeSelectedSpan, length3, length4, 33);
                    }
                }
            }
        }
        if (this.mSpannable.length() > 0) {
            setText(this.mSpannable);
        } else {
            setText("");
        }
    }
}
