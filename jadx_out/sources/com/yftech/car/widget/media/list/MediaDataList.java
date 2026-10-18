package com.yftech.car.widget.media.list;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Looper;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yftech.car.media.data.entity.MediaFile;
import com.yftech.car.media.data.entity.MediaFolder;
import com.yftech.car.utils.Loger;
import com.yftech.car.widget.media.R;
import com.yftech.car.widget.media.flexbox.FlexboxLayoutManager;
import com.yftech.car.widget.media.list.adapter.ListOneLayoutAdapter;
import com.yftech.car.widget.media.list.adapter.MediaFileAdapter;
import com.yftech.car.widget.media.list.adapter.MediaFolderAdapter;
import com.yftech.car.widget.media.list.holder.MediaDataDirectoryHolder;
import com.yftech.car.widget.media.manager.MediaDataManager;
import com.yftech.car.widget.media.manager.MusicManager;
import com.yftech.car.widget.media.photo.util.PhotoDataPlayManager;
import com.yftech.car.widget.media.recyclerview.widget.GridLayoutManager;
import com.yftech.car.widget.media.recyclerview.widget.LinearLayoutManager;
import com.yftech.car.widget.media.recyclerview.widget.RecyclerView;
import com.yftech.car.widget.media.text.MediaDataListText;
import com.yftech.car.widget.media.text.MediaDataModifyBtn;
import com.yftech.car.widget.media.text.MediaListFolderPath;
import com.yftech.car.widget.media.video.VideoDataPlayManager;
import com.yftech.car.widget.media.view.SideTextRetrievalView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public class MediaDataList extends RecyclerView {
    public static final int DIRECTORY = 6;
    private static final String MEDIA_LIST_CLICK_MEMORY = "media_list_click_memory";
    public static final int PLAY_LIST = 100;
    public static final int SEARCH_LIST = 101;
    private static final String TAG = "MediaDataList";
    private boolean isEmptyAdapter;
    private boolean isListEmpty;
    private String mBaseRootDirectoryPath;
    private Date mDate;
    private String mDirectoryFolderPath;
    private final LinearLayoutManager mEmptyLayoutManager;
    private final MediaFileAdapter mFileAdapter;
    private final MediaFolderAdapter mFolderAdapter;
    private LinearLayoutManager mFolderFileLayoutManager;
    private final View.OnClickListener mFolderPathClick;
    private final View.OnTouchListener mFolderPathTouchListener;
    private boolean mInitAdapter;
    private RecyclerView.ItemDecoration mItemDecoration;
    private MediaFileAdapter.OnItemFileListener mItemFileListener;
    private LatelyList mLatelyFolderFileList;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<MediaDataDirectoryList> mMediaDataDirectoryList;
    private List<MediaDataListText> mMediaDataListTextList;
    private final View.OnClickListener mMediaDataModifyBtnClick;
    private List<MediaDataModifyBtn> mMediaDataModifyBtnList;
    private int mMediaDefaultList;
    private int mMediaFileType;
    private boolean mMediaListClickMemoryDirectory;
    private final List<MediaListFolderPath> mMediaListFolderPathList;
    private boolean mMediaListScrollToPosition;
    private final boolean mMediaMemoryList;
    private int mMediaSearchBackList;
    private ListOneLayoutAdapter mMediaSearchLoadAdapter;
    private boolean mMediaUsbDisconnectAutoSwitchIndex;
    private String mMemoryKey;
    private OnDirectoryListener mOnDirectoryListener;
    private OnItemChangeListener mOnItemChangeListener;
    private final MediaDataDirectoryHolder.OnItemClickPathListener mOnItemClickPathListener;
    private final MediaFileAdapter.OnItemFileListener mOnItemFileListener;
    private final RecyclerView.OnScrollListener mOnScrollListener;
    private final SideTextRetrievalView.OnSideTextRetrievalListener mOnSideTextRetrievalListener;
    private final Runnable mRunnable;
    private final int mScaledTouchSlop;
    private final int mScrollPositionWithOffset;
    private int mSearchFlags;
    private SideTextRetrievalView mSideTextRetrievalView;
    private SimpleDateFormat mSimpleDateFormat;
    private final SharedPreferences mSp;
    private final int mSpanCount;
    private final String mStartActivity;
    private Runnable mUpdateDataRunnable;
    private ListOneLayoutAdapter mUsbDisconnectAdapter;
    private int mUsbIndex;
    private ListOneLayoutAdapter mUsbLoadingAdapter;

    /* JADX INFO: renamed from: com.yftech.car.widget.media.list.MediaDataList$3, reason: invalid class name */
    class AnonymousClass3 extends MediaFileAdapter.OnItemFileListener {
        final /* synthetic */ Context val$context;

        AnonymousClass3(Context context) {
            this.val$context = context;
        }

        public static /* synthetic */ void lambda$onCheckBoxStatus$0(AnonymousClass3 anonymousClass3, int i, boolean z) {
            if (MediaDataList.this.mItemFileListener != null) {
                MediaDataList.this.mItemFileListener.onCheckBoxStatus(i, z);
            }
            if (MediaDataList.this.mOnItemChangeListener != null) {
                MediaDataList.this.mOnItemChangeListener.onCheckBoxStatus(i, z);
            }
            if (MediaDataList.this.mFileAdapter.getDataListSize() > 0) {
                MediaDataList.this.setCheckBoxStatus(i, z);
            }
        }

        public static /* synthetic */ void lambda$onListSizeChange$1(AnonymousClass3 anonymousClass3, int i) {
            if (MediaDataList.this.mItemFileListener != null) {
                MediaDataList.this.mItemFileListener.onListSizeChange(i);
            }
            if (MediaDataList.this.mOnItemChangeListener != null) {
                MediaDataList.this.mOnItemChangeListener.onListSizeChange(i);
            }
            MediaDataList.this.mFileAdapter.notifyDataSetChangedList();
            if (i > 0) {
                MediaDataList.this.setCheckBoxStatus((int) MediaDataList.this.mFileAdapter.getSelectedSize(true), MediaDataList.this.mFileAdapter.isCheckBox());
                return;
            }
            Iterator it = MediaDataList.this.mMediaDataModifyBtnList.iterator();
            while (it.hasNext()) {
                ((MediaDataModifyBtn) it.next()).setVisibility(8);
            }
            for (MediaDataListText mediaDataListText : MediaDataList.this.mMediaDataListTextList) {
                Loger.d(MediaDataList.TAG, " onListSizeChange: MediaDataListText GONE");
                mediaDataListText.setVisibility(8);
            }
        }

        @Override // com.yftech.car.widget.media.list.adapter.MediaFileAdapter.OnItemFileListener
        public int getMediaListType() {
            return MediaDataList.this.mMediaDefaultList;
        }

        @Override // com.yftech.car.widget.media.list.adapter.MediaFileAdapter.OnItemFileListener
        public int getSearchFlags() {
            return MediaDataList.this.mSearchFlags;
        }

        @Override // com.yftech.car.widget.media.list.adapter.MediaFileAdapter.OnItemFileListener
        public void onCheckBoxStatus(final int i, final boolean z) {
            MediaDataList.this.postOnAnimation(new Runnable() { // from class: com.yftech.car.widget.media.list.-$$Lambda$MediaDataList$3$chWa3rhFMzOtrU-mUtfxVx1gd84
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataList.AnonymousClass3.lambda$onCheckBoxStatus$0(this.f$0, i, z);
                }
            });
        }

        @Override // com.yftech.car.widget.media.list.adapter.MediaFileAdapter.OnItemFileListener
        public void onItemFile(MediaFile mediaFile) {
            if (mediaFile.getType() == 0) {
                if (MediaDataList.this.mMediaDefaultList == 100) {
                    int musicType = MusicManager.getInstance().getMusicType();
                    MusicManager musicManager = MusicManager.getInstance();
                    if (musicType == 0 || musicType == 6) {
                        musicType = 7;
                    }
                    musicManager.start(mediaFile, musicType);
                } else if (MediaDataList.this.mMediaDefaultList == 6) {
                    MusicManager.getInstance().start(mediaFile, 1);
                } else if (MediaDataList.this.mMediaDefaultList == 101) {
                    MusicManager.getInstance().start(mediaFile, 2);
                } else {
                    MusicManager.getInstance().start(mediaFile, MediaDataList.this.mMediaDefaultList);
                }
                MediaDataList.this.startPlayActivity(this.val$context);
            } else if (mediaFile.getType() == 1) {
                MediaDataList.this.startPlayActivity(this.val$context);
            } else if (mediaFile.getType() == 4) {
                MediaDataList.this.directoryFolderPath(mediaFile.getFolderPath());
            } else if (mediaFile.getType() == 2) {
                MediaDataList.this.startPlayActivity(this.val$context);
            }
            if (mediaFile.getType() != 4) {
                if (MediaDataList.this.mOnItemChangeListener != null) {
                    MediaDataList.this.mOnItemChangeListener.onItemFile(mediaFile);
                }
                if (MediaDataList.this.mItemFileListener != null) {
                    MediaDataList.this.mItemFileListener.onItemFile(mediaFile);
                }
            }
        }

        @Override // com.yftech.car.widget.media.list.adapter.MediaFileAdapter.OnItemFileListener
        public void onListFile(List<MediaFile> list, int i) {
            if (MediaDataList.this.mItemFileListener != null) {
                MediaDataList.this.mItemFileListener.onListFile(list, i);
            }
            if (MediaDataList.this.mOnItemChangeListener != null) {
                MediaDataList.this.mOnItemChangeListener.onListFile(list, i);
            }
        }

        @Override // com.yftech.car.widget.media.list.adapter.MediaFileAdapter.OnItemFileListener
        public void onListSizeChange(final int i) {
            MediaDataList.this.postOnAnimation(new Runnable() { // from class: com.yftech.car.widget.media.list.-$$Lambda$MediaDataList$3$ypRBDwFLiBIJIqOImmIOllwHmmM
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataList.AnonymousClass3.lambda$onListSizeChange$1(this.f$0, i);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    enum LatelyList {
        NOT_LIST,
        FILE_LIST,
        FOLDER_LIST,
        FOLDER_FILE_LIST,
        DIRECTORY,
        SEARCH_LOADING,
        SEARCH,
        USB_DISCONNECT,
        USB_LOADING
    }

    public interface OnDirectoryListener {
        void onDirectoryPathChange(String str);
    }

    public static abstract class OnItemChangeListener {
        public void onCheckBoxStatus(int i, boolean z) {
        }

        public void onItemFile(MediaFile mediaFile) {
        }

        public void onItemFolder(MediaFolder mediaFolder) {
        }

        public void onListFile(List<MediaFile> list, int i) {
        }

        public void onListFolder(List<MediaFolder> list, int i) {
        }

        public void onListSizeChange(int i) {
        }
    }

    private MediaDataList(@NonNull Context context) {
        this(context, null, 0);
    }

    public MediaDataList(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.recyclerViewStyle);
    }

    public MediaDataList(@NonNull Context context, @Nullable AttributeSet attributeSet, int i) {
        int resourceId;
        int resourceId2;
        int resourceId3;
        super(context, attributeSet, i);
        int i2 = 0;
        this.mUsbIndex = 0;
        this.isEmptyAdapter = false;
        this.isListEmpty = false;
        this.mMediaUsbDisconnectAutoSwitchIndex = false;
        int i3 = 1;
        this.mMediaListScrollToPosition = true;
        this.mMediaListClickMemoryDirectory = false;
        this.mMediaFileType = 0;
        this.mDirectoryFolderPath = null;
        this.mBaseRootDirectoryPath = null;
        this.mLatelyFolderFileList = LatelyList.NOT_LIST;
        this.mMediaListFolderPathList = new ArrayList();
        this.mSimpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        this.mDate = new Date();
        this.mFolderPathTouchListener = new View.OnTouchListener() { // from class: com.yftech.car.widget.media.list.MediaDataList.4
            private int mDownX;
            private int mDownY;
            private final TextPaint mTextPaint = new TextPaint();
            private boolean isClick = false;

            /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case 0:
                        this.mDownX = (int) motionEvent.getX();
                        this.mDownY = (int) motionEvent.getY();
                        return true;
                    case 1:
                    case 3:
                        if (this.isClick) {
                            this.isClick = false;
                            MediaListFolderPath mediaListFolderPath = (MediaListFolderPath) view;
                            this.mTextPaint.setTextAlign(Paint.Align.LEFT);
                            this.mTextPaint.setColor(mediaListFolderPath.getSelectedColor());
                            this.mTextPaint.setTextSize(mediaListFolderPath.getTextSize());
                            float fMeasureText = this.mTextPaint.measureText(mediaListFolderPath.getStartContent());
                            String currentPathText = mediaListFolderPath.getCurrentPathText();
                            int iLastIndexOf = currentPathText.lastIndexOf("/");
                            if (iLastIndexOf > 0) {
                                String strSubstring = currentPathText.substring(0, iLastIndexOf);
                                float fMeasureText2 = this.mTextPaint.measureText(strSubstring);
                                int i4 = (int) (fMeasureText + fMeasureText2);
                                Log.d(MediaDataList.TAG, "onTouch: startWidth==" + fMeasureText + ",mDownX==" + this.mDownX + ",pathWidth==" + fMeasureText2 + ",width==" + i4 + ",startText==" + strSubstring);
                                if (this.mDownX > fMeasureText && this.mDownX < i4) {
                                    String strSubstring2 = MediaDataList.this.mDirectoryFolderPath.substring(0, MediaDataList.this.mDirectoryFolderPath.lastIndexOf("/"));
                                    Log.d(MediaDataList.TAG, "onTouch: -------------------ok  ,folderPath==" + strSubstring2);
                                    this.mTextPaint.setTextSize(mediaListFolderPath.getTextSize());
                                    while (i4 > 0) {
                                        int iLastIndexOf2 = strSubstring2.lastIndexOf("/");
                                        String strSubstring3 = strSubstring2.substring(iLastIndexOf2);
                                        float fMeasureText3 = this.mTextPaint.measureText(strSubstring3);
                                        i4 = (int) (i4 - fMeasureText3);
                                        Log.d(MediaDataList.TAG, "onTouch: width==" + i4 + ",folderWidth==" + fMeasureText3 + ",folder==" + strSubstring3);
                                        if (this.mDownX <= i4 || this.mDownX >= i4 + fMeasureText3) {
                                            strSubstring2 = strSubstring2.substring(0, iLastIndexOf2);
                                        } else {
                                            Loger.d(MediaDataList.TAG, " onTouch: folder==" + strSubstring3 + ",folderPath==" + strSubstring2);
                                            MediaDataList.this.directoryFolderPath(strSubstring2);
                                            view.playSoundEffect(0);
                                        }
                                    }
                                }
                            } else {
                                Log.e(MediaDataList.TAG, "onTouch: endIndex==" + iLastIndexOf);
                            }
                        }
                        return true;
                    case 2:
                        int x = ((int) motionEvent.getX()) - this.mDownX;
                        int y = ((int) motionEvent.getY()) - this.mDownY;
                        if (this.mDownX != -1) {
                            if (Math.abs(x) >= MediaDataList.this.mScaledTouchSlop || Math.abs(y) >= MediaDataList.this.mScaledTouchSlop) {
                                this.isClick = false;
                                this.mDownX = -1;
                            } else {
                                this.isClick = true;
                            }
                        }
                        return true;
                    default:
                        return true;
                }
            }
        };
        this.mFolderPathClick = new View.OnClickListener() { // from class: com.yftech.car.widget.media.list.MediaDataList.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MediaDataList.this.backDirectoryFolder();
            }
        };
        this.mOnItemClickPathListener = new MediaDataDirectoryHolder.OnItemClickPathListener() { // from class: com.yftech.car.widget.media.list.MediaDataList.6
            @Override // com.yftech.car.widget.media.list.holder.MediaDataDirectoryHolder.OnItemClickPathListener
            public void onItemPath(String str) {
                MediaDataList.this.directoryFolderPath(str);
            }
        };
        this.mMediaDataModifyBtnClick = new View.OnClickListener() { // from class: com.yftech.car.widget.media.list.MediaDataList.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MediaDataModifyBtn mediaDataModifyBtn = (MediaDataModifyBtn) view;
                switch (mediaDataModifyBtn.getClickModify()) {
                    case 0:
                        if (MediaDataList.this.mFileAdapter != null) {
                            List<MediaFile> listDeleteDataList = MediaDataList.this.mFileAdapter.deleteDataList();
                            mediaDataModifyBtn.notifyDeleteList(listDeleteDataList);
                            if (listDeleteDataList.size() == MediaDataList.this.mFileAdapter.getDataListSize() && MediaDataList.this.mMediaDefaultList == 6) {
                                MediaDataList.this.directoryBack();
                                break;
                            }
                        }
                        break;
                    case 3:
                        MediaDataList.this.mFileAdapter.setAllSelectedStatus(true);
                        break;
                    case 4:
                        MediaDataList.this.mFileAdapter.setCheckBox(true);
                        break;
                    case 5:
                        MediaDataList.this.mFileAdapter.setCheckBox(false);
                        break;
                    case 6:
                        MediaDataList.this.mFileAdapter.setAllSelectedStatus(false);
                        break;
                }
            }
        };
        this.mOnSideTextRetrievalListener = new SideTextRetrievalView.OnSideTextRetrievalListener() { // from class: com.yftech.car.widget.media.list.MediaDataList.8
            @Override // com.yftech.car.widget.media.view.SideTextRetrievalView.OnSideTextRetrievalListener
            public void onTouchText(SideTextRetrievalView sideTextRetrievalView, char c, int i4) {
                if (MediaDataList.this.mSpanCount > 0) {
                    RecyclerView.Adapter adapter = MediaDataList.this.getAdapter();
                    if (adapter instanceof MediaFileAdapter) {
                        MediaFileAdapter mediaFileAdapter = (MediaFileAdapter) adapter;
                        List<MediaFile> dataList = mediaFileAdapter.getDataList();
                        if (c == '#') {
                            MediaDataList.this.scrollToPositionWithOffset(mediaFileAdapter.getItemCount() - 1, 0);
                            return;
                        }
                        int i5 = 0;
                        while (i5 < dataList.size()) {
                            MediaFile mediaFile = dataList.get(i5);
                            if (mediaFile != null && mediaFile.getSortKey() == c) {
                                MediaDataList mediaDataList = MediaDataList.this;
                                if (!mediaFileAdapter.isEmptyHeadItemLayoutId()) {
                                    i5++;
                                }
                                mediaDataList.scrollToPositionWithOffset(i5, 0);
                                return;
                            }
                            i5++;
                        }
                        return;
                    }
                    if (adapter instanceof MediaFolderAdapter) {
                        MediaFolderAdapter mediaFolderAdapter = (MediaFolderAdapter) adapter;
                        List<MediaFolder> folderList = mediaFolderAdapter.getFolderList();
                        if (c == '#') {
                            MediaDataList.this.scrollToPositionWithOffset(mediaFolderAdapter.getItemCount() - 1, 0);
                            return;
                        }
                        int i6 = 0;
                        while (i6 < folderList.size()) {
                            MediaFolder mediaFolder = folderList.get(i6);
                            if (mediaFolder != null && mediaFolder.getSortKey() == c) {
                                MediaDataList mediaDataList2 = MediaDataList.this;
                                if (!mediaFolderAdapter.isEmptyHeadItemLayoutId()) {
                                    i6++;
                                }
                                mediaDataList2.scrollToPositionWithOffset(i6, 0);
                                return;
                            }
                            i6++;
                        }
                    }
                }
            }
        };
        this.mOnScrollListener = new RecyclerView.OnScrollListener() { // from class: com.yftech.car.widget.media.list.MediaDataList.9
            private int mFirstItemPosition = -1;

            @Override // com.yftech.car.widget.media.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int i4) {
            }

            @Override // com.yftech.car.widget.media.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(@NonNull RecyclerView recyclerView, int i4, int i5) {
                if (MediaDataList.this.mSideTextRetrievalView == null || !(MediaDataList.this.mLayoutManager instanceof LinearLayoutManager)) {
                    return;
                }
                int iFindFirstVisibleItemPosition = ((LinearLayoutManager) MediaDataList.this.mLayoutManager).findFirstVisibleItemPosition();
                if (iFindFirstVisibleItemPosition < 1) {
                    iFindFirstVisibleItemPosition = 0;
                }
                if (this.mFirstItemPosition == iFindFirstVisibleItemPosition) {
                    return;
                }
                this.mFirstItemPosition = iFindFirstVisibleItemPosition;
                RecyclerView.Adapter adapter = MediaDataList.this.getAdapter();
                if (adapter instanceof MediaFileAdapter) {
                    MediaFileAdapter mediaFileAdapter = (MediaFileAdapter) adapter;
                    List<MediaFile> dataList = mediaFileAdapter.getDataList();
                    if (!mediaFileAdapter.isEmptyHeadItemLayoutId()) {
                        iFindFirstVisibleItemPosition--;
                    }
                    if (iFindFirstVisibleItemPosition < 0) {
                        iFindFirstVisibleItemPosition = 0;
                    }
                    if (iFindFirstVisibleItemPosition < dataList.size()) {
                        MediaDataList.this.mSideTextRetrievalView.setCurrent((char) dataList.get(iFindFirstVisibleItemPosition).getSortKey());
                        return;
                    }
                    return;
                }
                if (adapter instanceof MediaFolderAdapter) {
                    MediaFolderAdapter mediaFolderAdapter = (MediaFolderAdapter) adapter;
                    List<MediaFolder> folderList = mediaFolderAdapter.getFolderList();
                    if (!mediaFolderAdapter.isEmptyHeadItemLayoutId()) {
                        iFindFirstVisibleItemPosition--;
                    }
                    if (iFindFirstVisibleItemPosition < 0) {
                        iFindFirstVisibleItemPosition = 0;
                    }
                    if (iFindFirstVisibleItemPosition < folderList.size()) {
                        MediaDataList.this.mSideTextRetrievalView.setCurrent((char) folderList.get(iFindFirstVisibleItemPosition).getSortKey());
                    }
                }
            }
        };
        this.mRunnable = new Runnable() { // from class: com.yftech.car.widget.media.list.MediaDataList.10
            @Override // java.lang.Runnable
            public void run() {
                int scrollState = MediaDataList.this.getScrollState();
                if (scrollState != 0) {
                    Log.e(MediaDataList.TAG, "updateListPosition: scrollState==" + scrollState);
                    return;
                }
                if (MediaDataList.this.mMediaFileType == 0) {
                    MediaFile playMusic = MusicManager.getInstance().getPlayMusic();
                    if (MediaDataList.this.mScrollPositionWithOffset != -1) {
                        MediaDataList.this.scrollToPositionWithOffset(MediaDataList.this.mFileAdapter.getSelectedPosition(playMusic), MediaDataList.this.mScrollPositionWithOffset);
                        return;
                    } else {
                        MediaDataList.this.updatePosition(MediaDataList.this.mFileAdapter.getSelectedPosition(playMusic));
                        return;
                    }
                }
                if (MediaDataList.this.mMediaFileType == 1) {
                    if (MediaDataList.this.mScrollPositionWithOffset != -1) {
                        MediaDataList.this.scrollToPositionWithOffset(MediaDataList.this.mFileAdapter.getSelectedPosition(VideoDataPlayManager.getInstance().getPlayVideoId()), MediaDataList.this.mScrollPositionWithOffset);
                        return;
                    } else {
                        MediaDataList.this.updatePosition(MediaDataList.this.mFileAdapter.getSelectedPosition(VideoDataPlayManager.getInstance().getPlayVideoId()));
                        return;
                    }
                }
                if (MediaDataList.this.mMediaFileType != 2) {
                    MediaDataList.this.scrollToPositionWithOffset(0, 0);
                } else if (MediaDataList.this.mScrollPositionWithOffset != -1) {
                    MediaDataList.this.scrollToPositionWithOffset(MediaDataList.this.mFileAdapter.getSelectedPosition(PhotoDataPlayManager.getInstance().getPlayId()), MediaDataList.this.mScrollPositionWithOffset);
                } else {
                    MediaDataList.this.updatePosition(MediaDataList.this.mFileAdapter.getSelectedPosition(PhotoDataPlayManager.getInstance().getPlayId()));
                }
            }
        };
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.media_list_layout);
        this.mSpanCount = typedArrayObtainStyledAttributes.getInt(R.styleable.media_list_layout_media_span_count, 1);
        int i4 = typedArrayObtainStyledAttributes.getInt(R.styleable.media_list_layout_media_folder_file_span_count, 0);
        int i5 = typedArrayObtainStyledAttributes.getInt(R.styleable.media_list_layout_media_orientation, 1);
        this.mMediaDefaultList = typedArrayObtainStyledAttributes.getInt(R.styleable.media_list_layout_media_default_list, 2);
        this.mMediaMemoryList = typedArrayObtainStyledAttributes.getBoolean(R.styleable.media_list_layout_media_memory_list, false);
        this.mMediaUsbDisconnectAutoSwitchIndex = typedArrayObtainStyledAttributes.getBoolean(R.styleable.media_list_layout_media_usb_disconnect_auto_switch_index, false);
        this.mMediaListScrollToPosition = typedArrayObtainStyledAttributes.getBoolean(R.styleable.media_list_layout_media_list_scrollTo_position, true);
        this.mMediaListClickMemoryDirectory = typedArrayObtainStyledAttributes.getBoolean(R.styleable.media_list_layout_media_list_click_memory_directory, false);
        this.mUsbIndex = typedArrayObtainStyledAttributes.getInt(R.styleable.media_list_layout_media_usb_index, -1);
        this.mScrollPositionWithOffset = typedArrayObtainStyledAttributes.getInt(R.styleable.media_list_layout_media_scroll_position_with_offset, -1);
        this.mMemoryKey = typedArrayObtainStyledAttributes.getString(R.styleable.media_list_layout_media_list_click_memory_key);
        this.mStartActivity = typedArrayObtainStyledAttributes.getString(R.styleable.media_list_layout_media_list_start_activity_class);
        if (TextUtils.isEmpty(this.mMemoryKey)) {
            this.mMemoryKey = "media_list_click_memory_key";
        }
        if (this.mSpanCount <= 0) {
            final float dimension = typedArrayObtainStyledAttributes.getDimension(R.styleable.media_list_layout_media_auto_item_left, 0.0f);
            final float dimension2 = typedArrayObtainStyledAttributes.getDimension(R.styleable.media_list_layout_media_auto_item_top, 0.0f);
            final float dimension3 = typedArrayObtainStyledAttributes.getDimension(R.styleable.media_list_layout_media_auto_item_right, 0.0f);
            final float dimension4 = typedArrayObtainStyledAttributes.getDimension(R.styleable.media_list_layout_media_auto_item_bottom, 0.0f);
            this.mItemDecoration = new RecyclerView.ItemDecoration() { // from class: com.yftech.car.widget.media.list.MediaDataList.1
                @Override // com.yftech.car.widget.media.recyclerview.widget.RecyclerView.ItemDecoration
                public void getItemOffsets(@NonNull Rect rect, @NonNull View view, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.State state) {
                    super.getItemOffsets(rect, view, recyclerView, state);
                    rect.left = (int) dimension;
                    rect.top = (int) dimension2;
                    rect.right = (int) dimension3;
                    rect.bottom = (int) dimension4;
                }
            };
            this.mLayoutManager = new FlexboxLayoutManager(context, i2, i3) { // from class: com.yftech.car.widget.media.list.MediaDataList.2
                @Override // com.yftech.car.widget.media.flexbox.FlexboxLayoutManager, com.yftech.car.widget.media.recyclerview.widget.RecyclerView.LayoutManager
                public boolean canScrollVertically() {
                    return true;
                }
            };
        } else if (this.mSpanCount > 1) {
            this.mLayoutManager = new GridLayoutManager(context, this.mSpanCount);
        } else {
            this.mLayoutManager = new LinearLayoutManager(context);
        }
        if (i4 > 1) {
            this.mFolderFileLayoutManager = new GridLayoutManager(context, i4);
        } else if (i4 == 1) {
            this.mFolderFileLayoutManager = new LinearLayoutManager(context);
        }
        if (this.mLayoutManager instanceof LinearLayoutManager) {
            ((LinearLayoutManager) this.mLayoutManager).setOrientation(i5);
        }
        if (this.mFolderFileLayoutManager != null) {
            this.mFolderFileLayoutManager.setOrientation(i5);
        }
        setLayoutManager(this.mLayoutManager);
        setAddItemDecoration();
        this.mEmptyLayoutManager = new LinearLayoutManager(context);
        this.mEmptyLayoutManager.setOrientation(i5);
        if (this.mUsbDisconnectAdapter == null && (resourceId3 = typedArrayObtainStyledAttributes.getResourceId(R.styleable.media_list_layout_media_usb_disconnect_layout_id, -1)) != -1) {
            this.mUsbDisconnectAdapter = new ListOneLayoutAdapter(resourceId3);
        }
        if (this.mUsbLoadingAdapter == null && (resourceId2 = typedArrayObtainStyledAttributes.getResourceId(R.styleable.media_list_layout_media_usb_loading_layout_id, -1)) != -1) {
            this.mUsbLoadingAdapter = new ListOneLayoutAdapter(resourceId2);
        }
        if (this.mMediaSearchLoadAdapter == null && (resourceId = typedArrayObtainStyledAttributes.getResourceId(R.styleable.media_list_layout_media_search_loading_layout_id, -1)) != -1) {
            this.mMediaSearchLoadAdapter = new ListOneLayoutAdapter(resourceId);
        }
        this.mFileAdapter = new MediaFileAdapter(typedArrayObtainStyledAttributes);
        this.mOnItemFileListener = getOnItemFileListener(context);
        this.mFileAdapter.setOnItemFileListener(this.mOnItemFileListener);
        this.mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.mFolderAdapter = new MediaFolderAdapter(typedArrayObtainStyledAttributes);
        this.mFolderAdapter.setOnItemFolderListener(getOnItemFolderListener());
        this.mSp = context.getSharedPreferences(MEDIA_LIST_CLICK_MEMORY, 0);
        if (this.mMediaDefaultList != 101) {
            this.mMediaSearchBackList = this.mMediaDefaultList;
        }
        addOnScrollListener(this.mOnScrollListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void directoryBack() {
        if (this.mDirectoryFolderPath == null) {
            directoryFolderPath(null);
            return;
        }
        String strSubstring = this.mDirectoryFolderPath.substring(0, this.mDirectoryFolderPath.lastIndexOf("/"));
        Log.d(TAG, "directoryBack: folderPath==" + strSubstring + ",mBaseRootDirectoryPath==" + this.mBaseRootDirectoryPath);
        if (strSubstring.length() >= this.mBaseRootDirectoryPath.length()) {
            directoryFolderPath(strSubstring);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void directoryFolderPath(String str) {
        this.mDirectoryFolderPath = str;
        updateDirectoryList();
    }

    private MediaFileAdapter.OnItemFileListener getOnItemFileListener(@NonNull Context context) {
        return new AnonymousClass3(context);
    }

    private MediaFolderAdapter.OnItemFolderListener getOnItemFolderListener() {
        return new MediaFolderAdapter.OnItemFolderListener() { // from class: com.yftech.car.widget.media.list.-$$Lambda$MediaDataList$AVF7vbRpqsSeczol29V9BO2o7p0
            @Override // com.yftech.car.widget.media.list.adapter.MediaFolderAdapter.OnItemFolderListener
            public final void onItemFolder(MediaFolder mediaFolder, int i) {
                MediaDataList.lambda$getOnItemFolderListener$0(this.f$0, mediaFolder, i);
            }
        };
    }

    public static /* synthetic */ void lambda$getOnItemFolderListener$0(MediaDataList mediaDataList, MediaFolder mediaFolder, int i) {
        if (mediaDataList.mFileAdapter.isEmptyItemFileLayoutId()) {
            throw new RuntimeException("MediaDataList: media_item_file_layout_id 没有设置布局，无法加载适配器item子类界面,请设置item布局再试");
        }
        if (mediaDataList.mOnItemChangeListener != null) {
            mediaDataList.mOnItemChangeListener.onItemFolder(mediaFolder);
            mediaDataList.mOnItemChangeListener.onListFolder(mediaDataList.mFolderAdapter.getFolderList(), i);
        }
        mediaDataList.mFileAdapter.updateList(mediaFolder.getData(), true);
        if (mediaDataList.mLatelyFolderFileList != LatelyList.FILE_LIST && mediaDataList.mLatelyFolderFileList != LatelyList.FOLDER_FILE_LIST) {
            if (mediaDataList.mFolderFileLayoutManager != null) {
                mediaDataList.setLayoutManager(mediaDataList.mFolderFileLayoutManager);
            }
            mediaDataList.setAdapter(mediaDataList.mFileAdapter);
        }
        mediaDataList.mLatelyFolderFileList = LatelyList.FOLDER_FILE_LIST;
        for (MediaListFolderPath mediaListFolderPath : mediaDataList.mMediaListFolderPathList) {
            if (mediaListFolderPath.getVisibility() != 0) {
                if (!mediaListFolderPath.isDirectoryVisible()) {
                    mediaListFolderPath.setVisibility(0);
                }
            } else if (mediaListFolderPath.isDirectoryVisible()) {
                mediaListFolderPath.setVisibility(4);
            }
        }
        for (MediaDataDirectoryList mediaDataDirectoryList : mediaDataList.mMediaDataDirectoryList) {
            if (mediaDataDirectoryList.getVisibility() != 0) {
                if (!mediaDataDirectoryList.isDirectoryVisible()) {
                    mediaDataDirectoryList.setVisibility(0);
                }
            } else if (mediaDataDirectoryList.isDirectoryVisible()) {
                mediaDataDirectoryList.setVisibility(4);
            }
        }
    }

    public static /* synthetic */ void lambda$updateDirectoryList$1(MediaDataList mediaDataList) {
        mediaDataList.mFileAdapter.notifyDataSetChangedList();
        mediaDataList.setDirectoryList(mediaDataList.mFileAdapter.getDataList());
    }

    private synchronized int saveValue(int i) {
        SharedPreferences.Editor editorEdit = this.mSp.edit();
        editorEdit.putInt(this.mMemoryKey, i);
        editorEdit.apply();
        editorEdit.commit();
        return i;
    }

    private void setAddItemDecoration() {
        if (this.mItemDecoration != null) {
            boolean z = false;
            int i = 0;
            while (true) {
                if (i >= getItemDecorationCount()) {
                    z = true;
                    break;
                } else if (getItemDecorationAt(i) == this.mItemDecoration) {
                    break;
                } else {
                    i++;
                }
            }
            if (z) {
                addItemDecoration(this.mItemDecoration);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCheckBoxStatus(int i, boolean z) {
        int i2;
        int i3;
        int size;
        int lastModified;
        boolean z2 = z && this.mFileAdapter.isAllSelected();
        long selectedSize = this.mFileAdapter.getSelectedSize(false);
        Log.d(TAG, "setCheckBoxStatus: checkSize==" + i + ",isCheckBox==" + z + ",allSelected==" + z2 + ",fileSize==" + selectedSize);
        Iterator<MediaDataModifyBtn> it = this.mMediaDataModifyBtnList.iterator();
        while (true) {
            i2 = 5;
            i3 = 4;
            if (!it.hasNext()) {
                break;
            }
            MediaDataModifyBtn next = it.next();
            if (next.getModifyMediaDataListId() == -1 || next.getModifyMediaDataListId() == getId()) {
                next.setSelectedSize(i);
                next.setFileSize(selectedSize);
                if (z) {
                    if (i > 0) {
                        if (next.isClickModify(0) || next.isClickModify(1) || next.isClickModify(2)) {
                            next.setVisibility(0);
                        }
                        if (z2) {
                            if (next.isClickModify(6)) {
                                next.setVisibility(0);
                            }
                            if (next.isClickModify(3)) {
                                next.setVisibility(8);
                            }
                        } else {
                            if (next.isClickModify(3)) {
                                next.setVisibility(0);
                            }
                            if (next.isClickModify(6)) {
                                next.setVisibility(8);
                            }
                        }
                    } else {
                        if (next.isClickModify(3)) {
                            next.setVisibility(0);
                        }
                        if (next.isClickModify(6)) {
                            next.setVisibility(8);
                        }
                        if (next.isClickModify(0) || next.isClickModify(1) || next.isClickModify(2)) {
                            next.setVisibility(8);
                        }
                    }
                    if (next.isClickModify(4)) {
                        next.setVisibility(8);
                    }
                    if (next.isClickModify(5)) {
                        next.setVisibility(0);
                    }
                } else if (next.isClickModify(4)) {
                    next.setVisibility(0);
                } else {
                    next.setVisibility(8);
                }
            }
        }
        for (MediaDataListText mediaDataListText : this.mMediaDataListTextList) {
            if (mediaDataListText.isMediaDataListId(getId())) {
                if (mediaDataListText.isSelectedFunction()) {
                    if (!z) {
                        mediaDataListText.setVisibility(8);
                    } else if (i > 0) {
                        if (mediaDataListText.isSelectedFunction(i3)) {
                            mediaDataListText.setVisibility(8);
                        }
                        if (mediaDataListText.isSelectedFunction(i2)) {
                            mediaDataListText.setVisibility(0);
                            mediaDataListText.updateListSize(i, selectedSize);
                        }
                    } else {
                        if (mediaDataListText.isSelectedFunction(i3)) {
                            mediaDataListText.setVisibility(0);
                        }
                        if (mediaDataListText.isSelectedFunction(i2)) {
                            mediaDataListText.setVisibility(8);
                        }
                    }
                } else if (mediaDataListText.isFolderFileSize()) {
                    List<MediaFile> dataList = this.mFileAdapter.getDataList();
                    if (dataList != null) {
                        size = 0;
                        lastModified = 0;
                        for (MediaFile mediaFile : dataList) {
                            if (mediaFile.getType() == i3) {
                                size = (int) (((long) size) + mediaFile.getSize());
                                lastModified = (int) (((long) (lastModified + 1)) + mediaFile.getLastModified());
                            } else {
                                size++;
                            }
                            i3 = 4;
                        }
                    } else {
                        size = 0;
                        lastModified = 0;
                    }
                    mediaDataListText.setVisibility(0);
                    mediaDataListText.updateListSize(size, lastModified);
                }
            }
            i2 = 5;
            i3 = 4;
        }
    }

    private void setDirectoryList(List<MediaFile> list) {
        for (MediaListFolderPath mediaListFolderPath : this.mMediaListFolderPathList) {
            if (mediaListFolderPath != null) {
                mediaListFolderPath.setFolderPathText(this.mBaseRootDirectoryPath, this.mDirectoryFolderPath);
            }
        }
        for (MediaDataDirectoryList mediaDataDirectoryList : this.mMediaDataDirectoryList) {
            if (mediaDataDirectoryList.isMediaDataListId(getId())) {
                mediaDataDirectoryList.setDirectoryPath(this.mBaseRootDirectoryPath, this.mDirectoryFolderPath);
            }
        }
        if (list == null) {
            Log.e(TAG, "updateDirectoryList: list is null");
            return;
        }
        Log.d(TAG, "updateDirectoryList: mDirectoryFolderPath ==" + this.mDirectoryFolderPath + ",list == " + list.size());
        if (this.mDirectoryFolderPath.length() > this.mBaseRootDirectoryPath.length()) {
            for (MediaListFolderPath mediaListFolderPath2 : this.mMediaListFolderPathList) {
                if (mediaListFolderPath2.getVisibility() != 0) {
                    if (!mediaListFolderPath2.isDirectoryVisible()) {
                        mediaListFolderPath2.setVisibility(0);
                    }
                } else if (mediaListFolderPath2.isDirectoryVisible()) {
                    mediaListFolderPath2.setVisibility(4);
                }
            }
            for (MediaDataDirectoryList mediaDataDirectoryList2 : this.mMediaDataDirectoryList) {
                if (mediaDataDirectoryList2.getVisibility() != 0) {
                    if (!mediaDataDirectoryList2.isDirectoryVisible()) {
                        mediaDataDirectoryList2.setVisibility(0);
                    }
                } else if (mediaDataDirectoryList2.isDirectoryVisible()) {
                    mediaDataDirectoryList2.setVisibility(4);
                }
            }
        }
        if (list.isEmpty()) {
            this.isListEmpty = true;
        } else {
            if (this.isListEmpty) {
                this.isListEmpty = false;
                this.isEmptyAdapter = true;
                if (this.mLatelyFolderFileList != LatelyList.FOLDER_LIST) {
                    setAdapter(null);
                }
            }
            if (getLayoutManager() != this.mLayoutManager) {
                setLayoutManager(this.mLayoutManager);
            }
        }
        if (this.mLatelyFolderFileList != LatelyList.DIRECTORY || this.isEmptyAdapter || this.isListEmpty) {
            setAdapter(this.mFileAdapter);
        }
        removeCallbacks(this.mRunnable);
        postOnAnimationDelayed(this.mRunnable, 100L);
        this.isEmptyAdapter = false;
        this.mLatelyFolderFileList = LatelyList.DIRECTORY;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFileList(List<MediaFile> list, boolean z) {
        for (MediaListFolderPath mediaListFolderPath : this.mMediaListFolderPathList) {
            if (mediaListFolderPath.isDirectoryInvisible()) {
                mediaListFolderPath.setVisibility(4);
            }
        }
        for (MediaDataDirectoryList mediaDataDirectoryList : this.mMediaDataDirectoryList) {
            if (mediaDataDirectoryList.isDirectoryInvisible()) {
                mediaDataDirectoryList.setVisibility(4);
            }
        }
        if (list == null || list.isEmpty()) {
            this.isListEmpty = true;
            if (1000 != this.mMediaDefaultList && !MediaDataManager.getInstance().isUsbExists(this.mUsbIndex) && setUsbDisconnect()) {
                return;
            }
            if (getLayoutManager() != this.mEmptyLayoutManager) {
                setLayoutManager(this.mEmptyLayoutManager);
            }
        } else {
            if (this.isListEmpty) {
                this.isListEmpty = false;
                this.isEmptyAdapter = true;
                if (this.mLatelyFolderFileList != LatelyList.FOLDER_LIST) {
                    setAdapter(null);
                }
            }
            if (getLayoutManager() != this.mLayoutManager) {
                setLayoutManager(this.mLayoutManager);
            }
        }
        if ((this.mLatelyFolderFileList != LatelyList.FILE_LIST && this.mLatelyFolderFileList != LatelyList.FOLDER_FILE_LIST) || this.isEmptyAdapter || this.isListEmpty) {
            setAdapter(this.mFileAdapter);
        }
        if (z) {
            removeCallbacks(this.mRunnable);
            postOnAnimationDelayed(this.mRunnable, 100L);
        }
        this.isEmptyAdapter = false;
        this.mLatelyFolderFileList = LatelyList.FILE_LIST;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFolderList(List<MediaFolder> list) {
        for (MediaListFolderPath mediaListFolderPath : this.mMediaListFolderPathList) {
            if (mediaListFolderPath.isDirectoryInvisible()) {
                mediaListFolderPath.setVisibility(4);
            }
        }
        for (MediaDataDirectoryList mediaDataDirectoryList : this.mMediaDataDirectoryList) {
            if (mediaDataDirectoryList.isDirectoryInvisible()) {
                mediaDataDirectoryList.setVisibility(4);
            }
        }
        if (list == null || list.isEmpty()) {
            this.isListEmpty = true;
            if (!MediaDataManager.getInstance().isUsbExists(this.mUsbIndex) && setUsbDisconnect()) {
                return;
            }
            if (getLayoutManager() != this.mEmptyLayoutManager) {
                setLayoutManager(this.mEmptyLayoutManager);
            }
        } else {
            if (this.isListEmpty) {
                this.isListEmpty = false;
                this.isEmptyAdapter = true;
                if (this.mLatelyFolderFileList != LatelyList.FILE_LIST) {
                    setAdapter(null);
                }
            }
            setLayoutManager(this.mLayoutManager);
        }
        if (this.mLatelyFolderFileList != LatelyList.FOLDER_LIST || this.isEmptyAdapter || this.isListEmpty) {
            setAdapter(this.mFolderAdapter);
        }
        if (this.mItemFileListener != null) {
            this.mItemFileListener.onListSizeChange(list != null ? list.size() : 0);
        }
        if (this.mOnItemChangeListener != null) {
            this.mOnItemChangeListener.onListSizeChange(list != null ? list.size() : 0);
        }
        this.mLatelyFolderFileList = LatelyList.FOLDER_LIST;
    }

    private boolean setUsbDisconnect() {
        if (this.mUsbDisconnectAdapter == null || !this.mUsbDisconnectAdapter.isExistsLayoutId()) {
            return false;
        }
        if (this.mLatelyFolderFileList == LatelyList.USB_DISCONNECT) {
            return true;
        }
        if (getLayoutManager() != this.mEmptyLayoutManager) {
            setLayoutManager(this.mEmptyLayoutManager);
        }
        setAdapter(this.mUsbDisconnectAdapter);
        this.mLatelyFolderFileList = LatelyList.USB_DISCONNECT;
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startPlayActivity(@NonNull Context context) {
        if (TextUtils.isEmpty(this.mStartActivity)) {
            return;
        }
        Loger.i(TAG, "startPlayActivity->" + this.mStartActivity.getClass().getSimpleName());
        Intent intent = new Intent();
        intent.setClassName(context, this.mStartActivity);
        intent.addFlags(268435456);
        context.startActivity(intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePosition(int i) {
        if (this.mMediaListScrollToPosition && (this.mLayoutManager instanceof LinearLayoutManager)) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) this.mLayoutManager;
            int iFindFirstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
            Loger.d(TAG, " updatePosition: position==" + i + ",firstItemPosition==" + iFindFirstVisibleItemPosition);
            if (i >= iFindFirstVisibleItemPosition + 1 && i <= linearLayoutManager.findLastVisibleItemPosition() - 1) {
                Log.d(TAG, "updatePosition no update adapter firstItemPosition ==" + iFindFirstVisibleItemPosition);
                return;
            }
            if (i <= iFindFirstVisibleItemPosition) {
                scrollToPositionWithOffset(i, 0);
                return;
            }
            View childAt = this.mLayoutManager.getChildAt(0);
            if (childAt == null) {
                Log.e(TAG, "updatePosition: childAt is null");
                scrollToPositionWithOffset(i, 0);
                return;
            }
            int height = getHeight();
            int height2 = childAt.getHeight();
            int i2 = (((height / height2) - 1) * height2) + (height % height2);
            linearLayoutManager.scrollToPositionWithOffset(i, i2);
            Log.d(TAG, "updatePosition: offset==" + i2);
        }
    }

    public boolean backDirectoryFolder() {
        if (this.mMediaDefaultList == 1 || this.mMediaDefaultList == 3 || this.mMediaDefaultList == 4 || this.mMediaDefaultList == 5) {
            if (this.mLatelyFolderFileList == LatelyList.FOLDER_FILE_LIST) {
                for (MediaListFolderPath mediaListFolderPath : this.mMediaListFolderPathList) {
                    if (mediaListFolderPath.isDirectoryInvisible()) {
                        mediaListFolderPath.setVisibility(4);
                    }
                }
                for (MediaDataDirectoryList mediaDataDirectoryList : this.mMediaDataDirectoryList) {
                    if (mediaDataDirectoryList.isMediaDataListId(getId()) && mediaDataDirectoryList.isDirectoryInvisible()) {
                        mediaDataDirectoryList.setVisibility(4);
                    }
                }
                if (getLayoutManager() != this.mLayoutManager) {
                    setLayoutManager(this.mLayoutManager);
                }
                setAdapter(this.mFolderAdapter);
                this.mLatelyFolderFileList = LatelyList.FOLDER_LIST;
                return true;
            }
        } else if (this.mMediaDefaultList == 6 && this.mDirectoryFolderPath != null && this.mDirectoryFolderPath.length() > this.mBaseRootDirectoryPath.length()) {
            directoryBack();
            return true;
        }
        return false;
    }

    public void clearList() {
        Log.e(TAG, "clearList: -----------------------------clearList");
        if (getLayoutManager() != this.mEmptyLayoutManager) {
            setLayoutManager(this.mEmptyLayoutManager);
        }
        this.mDirectoryFolderPath = null;
        this.isListEmpty = true;
        boolean usbDisconnect = setUsbDisconnect();
        if (!usbDisconnect) {
            setAdapter(null);
        }
        this.mFileAdapter.updateList(null, true);
        this.mFolderAdapter.updateList(null, true);
        if (!usbDisconnect) {
            setAdapter(this.mLatelyFolderFileList == LatelyList.FOLDER_LIST ? this.mFolderAdapter : this.mFileAdapter);
        }
        for (MediaListFolderPath mediaListFolderPath : this.mMediaListFolderPathList) {
            if (mediaListFolderPath.isDirectoryInvisible()) {
                mediaListFolderPath.setVisibility(4);
            }
        }
        for (MediaDataDirectoryList mediaDataDirectoryList : this.mMediaDataDirectoryList) {
            if (mediaDataDirectoryList.isDirectoryInvisible()) {
                mediaDataDirectoryList.setVisibility(4);
            }
        }
    }

    public boolean closeListCheckBox() {
        return this.mFileAdapter.setCheckBox(false);
    }

    public boolean equalsFileList(List<MediaFile> list) {
        boolean zEqualsFileList = this.mFileAdapter.equalsFileList(list);
        Loger.d(TAG, "equalsFileList: isFileList==" + zEqualsFileList);
        return zEqualsFileList;
    }

    public String getDirectoryFolderPath() {
        return this.mDirectoryFolderPath;
    }

    public int getMediaDefaultList() {
        if (isMediaMemoryList()) {
            this.mMediaDefaultList = this.mSp.getInt(this.mMemoryKey, this.mMediaDefaultList);
        }
        return this.mMediaDefaultList;
    }

    public int getMediaSearchBackList() {
        return this.mMediaSearchBackList;
    }

    public int getUsbIndex() {
        return this.mUsbIndex;
    }

    public boolean isEmptyList() {
        if (this.mLatelyFolderFileList == LatelyList.FOLDER_LIST) {
            return this.mFolderAdapter.getDataListSize() == 0;
        }
        return this.mFileAdapter.getDataListSize() == 0;
    }

    public boolean isLatelyList(LatelyList latelyList) {
        return this.mLatelyFolderFileList == latelyList;
    }

    public boolean isMediaListClickMemoryDirectory() {
        return this.mMediaListClickMemoryDirectory;
    }

    public boolean isMediaMemoryList() {
        return this.mMediaMemoryList;
    }

    public boolean isMediaUsbDisconnectAutoSwitchIndex() {
        return this.mMediaUsbDisconnectAutoSwitchIndex;
    }

    public void notifyDataSetChanged() {
        if (this.mLatelyFolderFileList == LatelyList.FOLDER_LIST) {
            this.mFolderAdapter.notifyDataSetChanged();
        }
        if (this.mLatelyFolderFileList == LatelyList.FILE_LIST || this.mLatelyFolderFileList == LatelyList.FOLDER_FILE_LIST || this.mLatelyFolderFileList == LatelyList.DIRECTORY || this.mLatelyFolderFileList == LatelyList.SEARCH) {
            this.mFileAdapter.notifyDataSetChangedList();
        }
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void release() {
        this.mMediaListFolderPathList.clear();
        if (this.mItemDecoration != null) {
            for (int i = 0; i < getItemDecorationCount(); i++) {
                if (getItemDecorationAt(i) == this.mItemDecoration) {
                    removeItemDecoration(this.mItemDecoration);
                    break;
                }
            }
        }
        if (this.mFileAdapter != null) {
            this.mFileAdapter.release();
        }
        if (this.mFolderAdapter != null) {
            this.mFolderAdapter.release();
        }
        removeOnScrollListener(this.mOnScrollListener);
    }

    public void scrollToPositionWithOffset(int i, int i2) {
        Log.e(TAG, "scrollToPositionWithOffset: position==" + i + ",offset==" + i2);
        if (this.mLayoutManager instanceof LinearLayoutManager) {
            ((LinearLayoutManager) this.mLayoutManager).scrollToPositionWithOffset(i, i2);
        }
    }

    public void setCustomLayoutManager(RecyclerView.LayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
    }

    public void setLinkedList(LinkedList<ViewGroup> linkedList) {
        View headLayout = this.mFileAdapter.getHeadLayout(this);
        if (headLayout instanceof ViewGroup) {
            linkedList.add((ViewGroup) headLayout);
        }
        View endingLayout = this.mFileAdapter.getEndingLayout(this);
        if (endingLayout instanceof ViewGroup) {
            linkedList.add((ViewGroup) endingLayout);
        }
        View headLayout2 = this.mFolderAdapter.getHeadLayout(this);
        if (headLayout2 instanceof ViewGroup) {
            linkedList.add((ViewGroup) headLayout2);
        }
        View endingLayout2 = this.mFolderAdapter.getEndingLayout(this);
        if (endingLayout2 instanceof ViewGroup) {
            linkedList.add((ViewGroup) endingLayout2);
        }
    }

    public void setMediaDataDirectoryList(List<MediaDataDirectoryList> list) {
        this.mMediaDataDirectoryList = list;
        for (MediaDataDirectoryList mediaDataDirectoryList : this.mMediaDataDirectoryList) {
            if (mediaDataDirectoryList.isMediaDataListId(getId())) {
                mediaDataDirectoryList.setOnItemPathListener(this.mOnItemClickPathListener);
            }
        }
    }

    public void setMediaDataListTextList(List<MediaDataListText> list) {
        this.mMediaDataListTextList = list;
        setAddItemDecoration();
    }

    public void setMediaDataModifyBtnList(List<MediaDataModifyBtn> list) {
        this.mMediaDataModifyBtnList = list;
        for (MediaDataModifyBtn mediaDataModifyBtn : list) {
            if (mediaDataModifyBtn.getModifyMediaDataListId() == -1 || mediaDataModifyBtn.getModifyMediaDataListId() == getId()) {
                mediaDataModifyBtn.setVisibility(8);
                mediaDataModifyBtn.setOnClickListener(this.mMediaDataModifyBtnClick);
            }
        }
    }

    public void setMediaDefaultList(int i) {
        this.mMediaDefaultList = i;
        if (i != 101) {
            this.mMediaSearchBackList = i;
        }
        if (isMediaMemoryList()) {
            saveValue(i);
        }
    }

    public void setMediaListFolderPath(MediaListFolderPath mediaListFolderPath) {
        if (mediaListFolderPath.getListId() == -1 || mediaListFolderPath.getListId() == getId()) {
            this.mMediaListFolderPathList.add(mediaListFolderPath);
            if (mediaListFolderPath.isClickBack()) {
                mediaListFolderPath.setOnClickListener(this.mFolderPathClick);
            } else if (mediaListFolderPath.isClickFolder()) {
                mediaListFolderPath.setOnTouchListener(this.mFolderPathTouchListener);
            }
        }
    }

    public void setMediaType(int i, Runnable runnable) {
        byte[] musicExistsDataIndex;
        this.mMediaFileType = i;
        this.mUpdateDataRunnable = runnable;
        if (this.mUsbIndex == -1) {
            switch (this.mMediaFileType) {
                case 0:
                    musicExistsDataIndex = MediaDataManager.getInstance().getMusicExistsDataIndex();
                    break;
                case 1:
                    musicExistsDataIndex = MediaDataManager.getInstance().getVideoExistsDataIndex();
                    break;
                default:
                    musicExistsDataIndex = MediaDataManager.getInstance().getPhotoExistsDataIndex();
                    break;
            }
            if (musicExistsDataIndex == null || musicExistsDataIndex.length <= 0) {
                List<String> existUdiskList = MediaDataManager.getInstance().getExistUdiskList();
                if (existUdiskList == null || existUdiskList.size() <= 0) {
                    this.mUsbIndex = 0;
                    if (setUsbDisconnect()) {
                        return;
                    }
                } else {
                    this.mUsbIndex = MediaDataManager.getInstance().getUsbIndex(existUdiskList.get(0));
                    if (MediaDataManager.getInstance().isScanner(this.mUsbIndex) && setUsbLoadingScan()) {
                        return;
                    }
                }
            } else {
                this.mUsbIndex = musicExistsDataIndex[0];
            }
        } else if (MediaDataManager.getInstance().isUsbExists(this.mUsbIndex)) {
            if (MediaDataManager.getInstance().isScanner(this.mUsbIndex) && setUsbLoadingScan()) {
                return;
            }
        } else if (setUsbDisconnect()) {
            return;
        }
        long jCurrentTimeMillis = System.currentTimeMillis();
        this.mUpdateDataRunnable.run();
        Log.d(TAG, "setMediaType: loading time ==" + (System.currentTimeMillis() - jCurrentTimeMillis));
    }

    public void setOnDirectoryListener(OnDirectoryListener onDirectoryListener) {
        this.mOnDirectoryListener = onDirectoryListener;
    }

    public void setOnItemChangeListener(OnItemChangeListener onItemChangeListener) {
        this.mOnItemChangeListener = onItemChangeListener;
    }

    @Deprecated
    public void setOnItemFileListener(MediaFileAdapter.OnItemFileListener onItemFileListener) {
        this.mItemFileListener = onItemFileListener;
    }

    public void setSearchFlags(int i) {
        this.mSearchFlags = i;
    }

    public void setSearchLoading() {
        if (this.mMediaSearchLoadAdapter == null || !this.mMediaSearchLoadAdapter.isExistsLayoutId()) {
            return;
        }
        this.isListEmpty = true;
        setLayoutManager(this.mEmptyLayoutManager);
        setAdapter(this.mMediaSearchLoadAdapter);
        this.mLatelyFolderFileList = LatelyList.SEARCH_LOADING;
    }

    public void setSideTextRetrievalViewList(List<SideTextRetrievalView> list) {
        for (SideTextRetrievalView sideTextRetrievalView : list) {
            if (sideTextRetrievalView.isMediaDataListId(getId())) {
                this.mSideTextRetrievalView = sideTextRetrievalView;
                this.mSideTextRetrievalView.setOnSideTextRetrievalListener(this.mOnSideTextRetrievalListener);
                return;
            }
        }
    }

    public void setSpanCount(int i) {
        if (this.mLayoutManager instanceof GridLayoutManager) {
            ((GridLayoutManager) this.mLayoutManager).setSpanCount(i);
        }
    }

    public void setUsbIndex(int i) {
        setUsbIndex(i, false);
    }

    public void setUsbIndex(int i, boolean z) {
        this.mUsbIndex = i;
        if (!z || this.mUpdateDataRunnable == null) {
            return;
        }
        this.mUpdateDataRunnable.run();
    }

    public boolean setUsbLoadingScan() {
        if (this.mUsbLoadingAdapter == null || !this.mUsbLoadingAdapter.isExistsLayoutId() || this.mMediaDefaultList == 101 || this.mMediaDefaultList == 0) {
            return false;
        }
        if (this.mLatelyFolderFileList == LatelyList.USB_LOADING) {
            return true;
        }
        setAdapter(this.mUsbLoadingAdapter);
        this.mLatelyFolderFileList = LatelyList.USB_LOADING;
        return true;
    }

    public void updateDirectoryList() {
        if (this.mDirectoryFolderPath == null) {
            String[] udiskList = MediaDataManager.getInstance().getUdiskList();
            if (udiskList != null && udiskList.length > this.mUsbIndex) {
                for (String str : MediaDataManager.getInstance().getExistUdiskList()) {
                    if (str.startsWith(udiskList[this.mUsbIndex])) {
                        this.mDirectoryFolderPath = str;
                        break;
                    }
                }
            } else {
                Loger.e(TAG, " updateDirectoryList: udiskList is null");
            }
        }
        if (this.mDirectoryFolderPath == null) {
            Log.e(TAG, "updateDirectoryList: mDirectoryFolderPath is null");
            return;
        }
        List<MediaFile> musicDirectory = null;
        if (this.mBaseRootDirectoryPath == null) {
            this.mBaseRootDirectoryPath = MediaDataManager.getInstance().getUdiskList()[this.mUsbIndex];
        }
        boolean z = this.mDirectoryFolderPath.length() <= this.mBaseRootDirectoryPath.length();
        if (z) {
            for (MediaListFolderPath mediaListFolderPath : this.mMediaListFolderPathList) {
                if (mediaListFolderPath.isDirectoryInvisible()) {
                    mediaListFolderPath.setVisibility(4);
                } else if (mediaListFolderPath.isDirectoryVisible()) {
                    mediaListFolderPath.setVisibility(0);
                }
            }
        }
        if (z) {
            for (MediaDataDirectoryList mediaDataDirectoryList : this.mMediaDataDirectoryList) {
                if (mediaDataDirectoryList.isDirectoryInvisible()) {
                    mediaDataDirectoryList.setVisibility(4);
                } else if (mediaDataDirectoryList.isDirectoryVisible()) {
                    mediaDataDirectoryList.setVisibility(0);
                }
            }
        }
        if (this.mOnDirectoryListener != null) {
            this.mOnDirectoryListener.onDirectoryPathChange(this.mDirectoryFolderPath);
        }
        switch (this.mMediaFileType) {
            case 0:
                musicDirectory = MediaDataManager.getInstance().getMusicDirectory(this.mDirectoryFolderPath);
                break;
            case 1:
                musicDirectory = MediaDataManager.getInstance().getVideoDirectory(this.mDirectoryFolderPath);
                break;
            case 2:
                musicDirectory = MediaDataManager.getInstance().getPhotoDirectory(this.mDirectoryFolderPath);
                break;
            default:
                Log.e(TAG, "onItemFile: mMediaFileType==" + this.mMediaFileType);
                break;
        }
        this.mFileAdapter.updateDirectoryList(musicDirectory);
        if (Looper.myLooper() == Looper.getMainLooper()) {
            setDirectoryList(musicDirectory);
        } else {
            postOnAnimation(new Runnable() { // from class: com.yftech.car.widget.media.list.-$$Lambda$MediaDataList$7XHoePbgYf0dGw8AkFdP5WyfvvM
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataList.lambda$updateDirectoryList$1(this.f$0);
                }
            });
        }
    }

    public void updateDirectoryList(List<MediaFile> list) {
        int size;
        int lastModified;
        if (list != null) {
            Loger.d(TAG, " updateDirectoryList: list==" + list.size());
        } else {
            Loger.e(TAG, " updateDirectoryList: list is null");
        }
        if (this.mMediaDataListTextList != null && this.mMediaDataListTextList.size() > 0) {
            if (list != null) {
                size = 0;
                lastModified = 0;
                for (MediaFile mediaFile : list) {
                    if (mediaFile.getType() == 4) {
                        size = (int) (((long) size) + mediaFile.getSize());
                        lastModified = (int) (((long) (lastModified + 1)) + mediaFile.getLastModified());
                    } else {
                        size++;
                    }
                }
            } else {
                size = 0;
                lastModified = 0;
            }
            for (MediaDataListText mediaDataListText : this.mMediaDataListTextList) {
                if (mediaDataListText.isMediaDataListId(getId())) {
                    if (!mediaDataListText.isSelectedFunction()) {
                        if (mediaDataListText.getVisibility() != 0) {
                            mediaDataListText.setVisibility(0);
                        }
                        mediaDataListText.updateListSize(size, lastModified);
                    }
                    long selectedSize = this.mFileAdapter.getSelectedSize(false);
                    if (!this.mFileAdapter.isCheckBox()) {
                        if (mediaDataListText.isSelectedFunction(4)) {
                            mediaDataListText.setVisibility(8);
                        }
                        if (mediaDataListText.isSelectedFunction(5)) {
                            mediaDataListText.setVisibility(8);
                            mediaDataListText.updateListSize((int) this.mFileAdapter.getSelectedSize(true), selectedSize);
                        }
                    } else if (selectedSize > 0) {
                        if (mediaDataListText.isSelectedFunction(4)) {
                            mediaDataListText.setVisibility(8);
                        }
                        if (mediaDataListText.isSelectedFunction(5)) {
                            mediaDataListText.setVisibility(0);
                            mediaDataListText.updateListSize((int) this.mFileAdapter.getSelectedSize(true), selectedSize);
                        }
                    } else {
                        if (mediaDataListText.isSelectedFunction(4)) {
                            mediaDataListText.setVisibility(0);
                        }
                        if (mediaDataListText.isSelectedFunction(5)) {
                            mediaDataListText.setVisibility(8);
                            mediaDataListText.updateListSize((int) this.mFileAdapter.getSelectedSize(true), selectedSize);
                        }
                    }
                }
            }
        }
        String[] udiskList = MediaDataManager.getInstance().getUdiskList();
        if (udiskList != null) {
            this.mBaseRootDirectoryPath = udiskList[this.mUsbIndex];
            List<String> existUdiskList = MediaDataManager.getInstance().getExistUdiskList();
            if (existUdiskList != null) {
                int i = 0;
                for (String str : existUdiskList) {
                    if (str.startsWith(this.mBaseRootDirectoryPath)) {
                        if (i > 0) {
                            this.mBaseRootDirectoryPath = str.substring(0, str.lastIndexOf("/"));
                            break;
                        } else {
                            this.mBaseRootDirectoryPath = str;
                            i++;
                        }
                    }
                }
            } else {
                Log.e(TAG, "updateDirectoryList: is getExistUdiskList null");
            }
            this.mDirectoryFolderPath = this.mBaseRootDirectoryPath;
            Loger.d(TAG, " updateDirectoryList: mBaseRootDirectoryPath==" + this.mBaseRootDirectoryPath);
        } else {
            Loger.e(TAG, " updateDirectoryList: getUdiskList is null");
        }
        if (this.mFileAdapter.isEmptyItemDirectoryLayoutId()) {
            throw new RuntimeException("MediaDataList: media_item_directory_layout_id 没有设置布局，无法加载适配器item子类界面,请设置item布局再试");
        }
        this.mFileAdapter.updateDirectoryList(list);
        if (this.mOnDirectoryListener != null) {
            this.mOnDirectoryListener.onDirectoryPathChange(this.mDirectoryFolderPath);
        }
        for (MediaListFolderPath mediaListFolderPath : this.mMediaListFolderPathList) {
            if (this.mDirectoryFolderPath != null) {
                mediaListFolderPath.setFolderPathText(this.mBaseRootDirectoryPath, this.mDirectoryFolderPath);
            }
            if (mediaListFolderPath.isDirectoryInvisible()) {
                mediaListFolderPath.setVisibility(4);
            } else if (mediaListFolderPath.isDirectoryVisible()) {
                mediaListFolderPath.setVisibility(0);
            }
        }
        for (MediaDataDirectoryList mediaDataDirectoryList : this.mMediaDataDirectoryList) {
            if (this.mDirectoryFolderPath != null) {
                mediaDataDirectoryList.setDirectoryPath(this.mBaseRootDirectoryPath, this.mDirectoryFolderPath);
            }
            if (mediaDataDirectoryList.isDirectoryInvisible()) {
                mediaDataDirectoryList.setVisibility(4);
            } else if (mediaDataDirectoryList.isDirectoryVisible()) {
                mediaDataDirectoryList.setVisibility(0);
            }
        }
        if (list == null || list.isEmpty()) {
            this.isListEmpty = true;
            if (!MediaDataManager.getInstance().isUsbExists(this.mUsbIndex) && setUsbDisconnect()) {
                return;
            }
            if (getLayoutManager() != this.mEmptyLayoutManager) {
                setLayoutManager(this.mEmptyLayoutManager);
            }
        } else {
            if (this.isListEmpty) {
                this.isListEmpty = false;
                this.isEmptyAdapter = true;
                if (this.mLatelyFolderFileList != LatelyList.FOLDER_LIST) {
                    setAdapter(null);
                }
            }
            if (getLayoutManager() != this.mLayoutManager) {
                setLayoutManager(this.mLayoutManager);
            }
        }
        if (this.mLatelyFolderFileList != LatelyList.DIRECTORY || this.isEmptyAdapter || this.isListEmpty) {
            setAdapter(this.mFileAdapter);
        }
        removeCallbacks(this.mRunnable);
        postOnAnimationDelayed(this.mRunnable, 100L);
        this.isEmptyAdapter = false;
        this.mLatelyFolderFileList = LatelyList.DIRECTORY;
    }

    public void updateFileList(List<MediaFile> list, boolean z) {
        updateFileList(list, z, true);
    }

    public void updateFileList(final List<MediaFile> list, boolean z, final boolean z2) {
        if (list != null) {
            Loger.d(TAG, " updateFileList: list==" + list.size());
        } else {
            Loger.e(TAG, " updateFileList: list is null");
        }
        if (this.mMediaFileType != 0 && !this.mFileAdapter.isEmptyItemDateLayoutId() && this.mMediaDefaultList == 2) {
            if (list != null) {
                final ArrayList arrayList = new ArrayList();
                while (!list.isEmpty()) {
                    int i = 0;
                    long lastModified = 0;
                    for (int i2 = 0; i2 < list.size(); i2++) {
                        MediaFile mediaFile = list.get(i2);
                        if (mediaFile.getLastModified() > lastModified) {
                            lastModified = mediaFile.getLastModified();
                            i = i2;
                        }
                    }
                    MediaFile mediaFile2 = list.get(i);
                    if (arrayList.size() == 0) {
                        MediaFile mediaFile3 = new MediaFile(-11);
                        mediaFile3.setLastModified(mediaFile2.getLastModified());
                        arrayList.add(mediaFile3);
                    }
                    if (arrayList.size() > 1) {
                        MediaFile mediaFile4 = arrayList.get(arrayList.size() - 1);
                        this.mDate.setTime(mediaFile4.getLastModified());
                        int i3 = Integer.parseInt(this.mSimpleDateFormat.format(this.mDate));
                        this.mDate.setTime(mediaFile2.getLastModified());
                        if (i3 != Integer.parseInt(this.mSimpleDateFormat.format(this.mDate)) && mediaFile4.getType() != -11) {
                            MediaFile mediaFile5 = new MediaFile(-11);
                            mediaFile5.setLastModified(mediaFile2.getLastModified());
                            arrayList.add(mediaFile5);
                        }
                    }
                    arrayList.add(mediaFile2);
                    list.remove(i);
                }
                this.mFileAdapter.updateList(arrayList, z);
                if (Looper.myLooper() == Looper.getMainLooper()) {
                    setFileList(arrayList, z2);
                    return;
                } else {
                    postOnAnimation(new Runnable() { // from class: com.yftech.car.widget.media.list.-$$Lambda$MediaDataList$LGPlg--OJaqpnB4ypV27XSK5qt4
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.setFileList(arrayList, z2);
                        }
                    });
                    return;
                }
            }
            if (this.mOnItemFileListener != null) {
                this.mOnItemFileListener.onListSizeChange(0);
            }
            if (this.mOnItemChangeListener != null) {
                this.mOnItemChangeListener.onListSizeChange(0);
            }
        }
        if (this.mFileAdapter.isEmptyItemFileLayoutId()) {
            throw new RuntimeException("MediaDataList: media_item_file_layout_id 没有设置布局，无法加载适配器item子类界面,请设置item布局再试");
        }
        this.mFileAdapter.updateList(list, z);
        if (Looper.myLooper() == Looper.getMainLooper()) {
            setFileList(list, z2);
        } else {
            postOnAnimation(new Runnable() { // from class: com.yftech.car.widget.media.list.-$$Lambda$MediaDataList$exQ9t0kHMT0m7WcCdoR7LG3ti5Y
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.setFileList(list, z2);
                }
            });
        }
    }

    public void updateFolderList(final List<MediaFolder> list, boolean z) {
        if (this.mFolderAdapter.isEmptyItemFolderLayoutId()) {
            throw new RuntimeException("MediaDataList: media_item_folder_layout_id 没有设置布局，无法加载适配器item子类界面,请设置item布局再试");
        }
        this.mFolderAdapter.updateList(list, z);
        if (Looper.myLooper() == Looper.getMainLooper()) {
            setFolderList(list);
        } else {
            postOnAnimation(new Runnable() { // from class: com.yftech.car.widget.media.list.-$$Lambda$MediaDataList$2p2g-lyH1pKx3Nz1XzgkWt7nTWU
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.setFolderList(list);
                }
            });
        }
    }

    public void updateListPosition() {
        int scrollState = getScrollState();
        if (scrollState == 0) {
            removeCallbacks(this.mRunnable);
            postOnAnimationDelayed(this.mRunnable, 100L);
        } else {
            Log.e(TAG, "updateListPosition: scrollState==" + scrollState);
        }
    }

    public void updateSearchList(List<MediaFile> list) {
        if (list != null) {
            Loger.d(TAG, " updateFileList: list==" + list.size());
        } else {
            Loger.e(TAG, " updateFileList: list is null");
        }
        if (this.mFileAdapter.isEmptyItemFileLayoutId()) {
            throw new RuntimeException("MediaDataList: media_item_file_layout_id 没有设置布局，无法加载适配器item子类界面,请设置item布局再试");
        }
        this.mFileAdapter.updateList(list, true);
        for (MediaListFolderPath mediaListFolderPath : this.mMediaListFolderPathList) {
            if (mediaListFolderPath.isDirectoryInvisible()) {
                mediaListFolderPath.setVisibility(4);
            }
        }
        for (MediaDataDirectoryList mediaDataDirectoryList : this.mMediaDataDirectoryList) {
            if (mediaDataDirectoryList.isDirectoryInvisible()) {
                mediaDataDirectoryList.setVisibility(4);
            }
        }
        if (list == null || list.isEmpty()) {
            this.isListEmpty = true;
            if (getLayoutManager() != this.mEmptyLayoutManager) {
                setLayoutManager(this.mEmptyLayoutManager);
            }
        } else {
            if (this.isListEmpty) {
                this.isListEmpty = false;
                this.isEmptyAdapter = true;
                if (this.mLatelyFolderFileList != LatelyList.FOLDER_LIST) {
                    setAdapter(null);
                }
            }
            if (getLayoutManager() != this.mLayoutManager) {
                setLayoutManager(this.mLayoutManager);
            }
        }
        if (this.mLatelyFolderFileList != LatelyList.SEARCH || this.isEmptyAdapter || this.isListEmpty) {
            setAdapter(this.mFileAdapter);
        }
        removeCallbacks(this.mRunnable);
        postOnAnimationDelayed(this.mRunnable, 100L);
        this.isEmptyAdapter = false;
        this.mLatelyFolderFileList = LatelyList.SEARCH;
    }

    public void updateSearchText(String str) {
        if (this.mFileAdapter != null) {
            this.mFileAdapter.setSearchText(str);
        }
    }
}
