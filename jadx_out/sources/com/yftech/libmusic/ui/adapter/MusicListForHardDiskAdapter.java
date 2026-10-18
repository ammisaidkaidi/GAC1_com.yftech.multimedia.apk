package com.yftech.libmusic.ui.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.media.common.ui.widget.MarqueeView;
import com.media.common.ui.widget.SyncImageView;
import com.media.common.util.LogUtil;
import com.yftech.libmusic.R;
import com.yftech.libmusic.bean.MusicListItem;
import java.io.File;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public class MusicListForHardDiskAdapter extends BaseQuickAdapter<MusicListItem, BaseViewHolder> {
    private Context context;
    private boolean mIsPlaying;
    private List<MusicListItem> parentList;
    private String playingFloderPath;
    private String playingPath;
    private int position;

    private class mOnMediaDataCallbackListener implements SyncImageView.OnMediaInformationCallback {
        private int position;

        public mOnMediaDataCallbackListener(int i) {
            this.position = i;
        }

        @Override // com.media.common.ui.widget.SyncImageView.OnMediaInformationCallback
        public void onMediaDurationCallback(int i) {
        }

        @Override // com.media.common.ui.widget.SyncImageView.OnMediaInformationCallback
        public void onMediaImageCallback(String str, byte[] bArr, int i, int i2) {
            MusicListItem item = MusicListForHardDiskAdapter.this.getItem(this.position);
            if (item != null) {
                item.setThumbData(bArr);
            }
            MusicListForHardDiskAdapter.this.notifyItemChanged(this.position, bArr);
        }
    }

    public MusicListForHardDiskAdapter(Context context) {
        super(R.layout.item_music);
        this.position = -1;
        this.mIsPlaying = false;
        this.parentList = null;
        this.context = context;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.chad.library.adapter.base.BaseQuickAdapter
    public void convert(BaseViewHolder baseViewHolder, MusicListItem musicListItem) {
        SyncImageView syncImageView = (SyncImageView) baseViewHolder.getView(R.id.iv_cover);
        MarqueeView marqueeView = (MarqueeView) baseViewHolder.getView(R.id.tv_name);
        MarqueeView marqueeView2 = (MarqueeView) baseViewHolder.getView(R.id.tv_artist);
        MarqueeView marqueeView3 = (MarqueeView) baseViewHolder.getView(R.id.tv_album);
        ImageView imageView = (ImageView) baseViewHolder.getView(R.id.iv_isPlaying);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        if (musicListItem.isDir()) {
            syncImageView.setImageResource(R.mipmap.music_pic_folder);
            marqueeView3.setVisibility(4);
            marqueeView2.setVisibility(4);
        } else {
            byte[] thumbData = musicListItem.getThumbData();
            if (thumbData == null) {
                syncImageView.setMediaFilePath(0, musicListItem.getFilePath(), 76, 76);
            } else {
                Glide.with(this.context).load(thumbData).error(R.mipmap.music_search_pic_cover).into(syncImageView);
            }
            syncImageView.setOnMediaInformationCallback(new mOnMediaDataCallbackListener(baseViewHolder.getLayoutPosition()));
            marqueeView3.setVisibility(0);
            marqueeView2.setVisibility(0);
        }
        marqueeView.setContent(musicListItem.getTitle());
        marqueeView2.setContent(musicListItem.isDir() ? "" : musicListItem.getArtist());
        marqueeView3.setContent(musicListItem.isDir() ? "" : musicListItem.getAlbum());
        if (musicListItem.isDir() && musicListItem.getTitle().equals(this.playingFloderPath)) {
            if (!this.mIsPlaying) {
                animationDrawable.stop();
            } else if (!animationDrawable.isRunning()) {
                animationDrawable.start();
            }
            imageView.setVisibility(0);
            marqueeView.setTextColor(ContextCompat.getColor(this.mContext, R.color.colorF3AE3D));
            marqueeView.setRepetType(1);
            baseViewHolder.itemView.setBackgroundResource(R.mipmap.music_pic_sel_88px);
        } else if (TextUtils.isEmpty(this.playingPath) || !this.playingPath.equals(musicListItem.getFilePath())) {
            animationDrawable.stop();
            imageView.setVisibility(8);
            marqueeView.setTextColor(ContextCompat.getColor(this.mContext, R.color.colorFFFFFF));
            marqueeView2.setTextColor(ContextCompat.getColor(this.mContext, R.color.colorFFFFFF));
            marqueeView3.setTextColor(ContextCompat.getColor(this.mContext, R.color.colorFFFFFF));
            marqueeView.setRepetType(-1);
            marqueeView2.setRepetType(-1);
            marqueeView3.setRepetType(-1);
            baseViewHolder.itemView.setBackgroundResource(17170445);
        } else {
            if (this.mIsPlaying) {
                animationDrawable.start();
            } else {
                animationDrawable.stop();
            }
            imageView.setVisibility(0);
            marqueeView.setTextColor(ContextCompat.getColor(this.mContext, R.color.colorF3AE3D));
            marqueeView2.setTextColor(ContextCompat.getColor(this.mContext, R.color.colorF3AE3D));
            marqueeView3.setTextColor(ContextCompat.getColor(this.mContext, R.color.colorF3AE3D));
            marqueeView.setRepetType(1);
            marqueeView2.setRepetType(1);
            marqueeView3.setRepetType(1);
            this.position = baseViewHolder.getLayoutPosition();
            baseViewHolder.itemView.setBackgroundResource(R.mipmap.music_pic_sel_88px);
        }
        baseViewHolder.addOnClickListener(R.id.tv_name, R.id.tv_artist, R.id.tv_album);
    }

    public List<MusicListItem> getParentList() {
        return this.parentList;
    }

    public String getPlayingPath() {
        return this.playingPath;
    }

    public int getPosition() {
        return this.position;
    }

    public int getPositionByPath(String str) {
        for (int i = 0; i < getData().size(); i++) {
            MusicListItem musicListItem = getData().get(i);
            if (musicListItem != null && !TextUtils.isEmpty(musicListItem.getFilePath()) && musicListItem.getFilePath().equals(str)) {
                return i;
            }
        }
        return -1;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public /* bridge */ /* synthetic */ void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i, @NonNull List list) {
        onBindViewHolder((BaseViewHolder) viewHolder, i, (List<Object>) list);
    }

    public void onBindViewHolder(@NonNull BaseViewHolder baseViewHolder, int i, @NonNull List<Object> list) {
        LogUtil.d("onBindViewHolder:payloadSize=" + list.size());
        if (list.isEmpty()) {
            onBindViewHolder(baseViewHolder, i);
            return;
        }
        Object obj = list.get(0);
        if (!(obj instanceof String)) {
            if (obj instanceof byte[]) {
                byte[] bArr = (byte[]) obj;
                SyncImageView syncImageView = (SyncImageView) baseViewHolder.getView(R.id.iv_cover);
                if (bArr.length == 10) {
                    syncImageView.setImageResource(R.mipmap.music_search_pic_cover);
                    return;
                } else {
                    Glide.with(this.context).load(bArr).error(R.mipmap.music_search_pic_cover).into(syncImageView);
                    return;
                }
            }
            return;
        }
        String string = obj.toString();
        LogUtil.d("onBindViewHolder:position=" + i + ", path=" + string + ", playingPath=" + this.playingPath);
        ImageView imageView = (ImageView) baseViewHolder.getView(R.id.iv_isPlaying);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        MarqueeView marqueeView = (MarqueeView) baseViewHolder.getView(R.id.tv_name);
        MarqueeView marqueeView2 = (MarqueeView) baseViewHolder.getView(R.id.tv_artist);
        MarqueeView marqueeView3 = (MarqueeView) baseViewHolder.getView(R.id.tv_album);
        if (TextUtils.isEmpty(this.playingPath) || !this.playingPath.equals(string)) {
            imageView.setVisibility(8);
            marqueeView.setTextColor(ContextCompat.getColor(this.mContext, R.color.colorFFFFFF));
            marqueeView2.setTextColor(ContextCompat.getColor(this.mContext, R.color.colorFFFFFF));
            marqueeView3.setTextColor(ContextCompat.getColor(this.mContext, R.color.colorFFFFFF));
            marqueeView.setRepetType(-1);
            marqueeView2.setRepetType(-1);
            marqueeView3.setRepetType(-1);
            baseViewHolder.itemView.setBackgroundResource(17170445);
            return;
        }
        if (this.mIsPlaying) {
            animationDrawable.start();
        } else {
            animationDrawable.stop();
        }
        imageView.setVisibility(0);
        marqueeView.setTextColor(ContextCompat.getColor(this.mContext, R.color.colorF3AE3D));
        marqueeView2.setTextColor(ContextCompat.getColor(this.mContext, R.color.colorF3AE3D));
        marqueeView3.setTextColor(ContextCompat.getColor(this.mContext, R.color.colorF3AE3D));
        marqueeView.setRepetType(1);
        marqueeView2.setRepetType(1);
        marqueeView3.setRepetType(1);
        baseViewHolder.itemView.setBackgroundResource(R.mipmap.music_pic_sel_88px);
    }

    public void setParentList(List<MusicListItem> list) {
        this.parentList = list;
    }

    public void setPlayStatus(boolean z) {
        this.mIsPlaying = z;
    }

    public void setPlaying(String str, boolean z) throws Throwable {
        int positionByPath = getPositionByPath(str);
        String parent = new File(str).getParent();
        this.playingFloderPath = parent.substring(parent.lastIndexOf("/") + 1);
        LogUtil.e(" update playingFloderPath = " + this.playingFloderPath + "  position = " + positionByPath + "  notifyChanged = " + z);
        if (!z) {
            this.playingPath = str;
            this.position = positionByPath;
            return;
        }
        if (this.position >= 0) {
            notifyItemChanged(this.position, "");
        }
        this.playingPath = str;
        this.position = positionByPath;
        if (this.position >= 0) {
            notifyItemChanged(this.position, str);
        } else {
            notifyDataSetChanged();
        }
    }
}
