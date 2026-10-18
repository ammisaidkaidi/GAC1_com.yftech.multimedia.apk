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
import com.yftech.car.media.music.CarMusicManager;
import com.yftech.libmusic.R;
import com.yftech.libmusic.bean.MediaFileItem;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public class MusicSearchResultAdapter extends BaseQuickAdapter<MediaFileItem, BaseViewHolder> {
    private String keyWords;
    private Context mContext;
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
            MediaFileItem item = MusicSearchResultAdapter.this.getItem(this.position);
            if (item != null) {
                item.setThumbData(bArr);
            }
            MusicSearchResultAdapter.this.notifyItemChanged(this.position, bArr);
        }
    }

    public MusicSearchResultAdapter(Context context) {
        super(R.layout.item_music_search_result);
        this.position = -1;
        this.keyWords = null;
        this.mContext = context;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.chad.library.adapter.base.BaseQuickAdapter
    public void convert(BaseViewHolder baseViewHolder, MediaFileItem mediaFileItem) {
        SyncImageView syncImageView = (SyncImageView) baseViewHolder.getView(R.id.iv_cover);
        MarqueeView marqueeView = (MarqueeView) baseViewHolder.getView(R.id.tv_name);
        MarqueeView marqueeView2 = (MarqueeView) baseViewHolder.getView(R.id.tv_artist);
        MarqueeView marqueeView3 = (MarqueeView) baseViewHolder.getView(R.id.tv_album);
        if (mediaFileItem.isDir()) {
            syncImageView.setImageResource(R.mipmap.music_pic_folder);
            marqueeView3.setVisibility(4);
            marqueeView2.setVisibility(4);
        } else {
            byte[] thumbData = mediaFileItem.getThumbData();
            if (thumbData == null) {
                syncImageView.setMediaFilePath(0, mediaFileItem.getData(), 88, 88);
            } else {
                Glide.with(this.mContext).load(thumbData).error(R.mipmap.music_search_pic_cover).into(syncImageView);
            }
            syncImageView.setOnMediaInformationCallback(new mOnMediaDataCallbackListener(baseViewHolder.getLayoutPosition()));
            marqueeView3.setVisibility(0);
            marqueeView2.setVisibility(0);
        }
        marqueeView.setContent(mediaFileItem.getTitle());
        marqueeView.setKeyWords(this.keyWords);
        marqueeView2.setContent(mediaFileItem.isDir() ? "" : mediaFileItem.getArtist());
        marqueeView2.setKeyWords(this.keyWords);
        marqueeView3.setContent(mediaFileItem.isDir() ? "" : mediaFileItem.getAlbum());
        marqueeView3.setKeyWords(this.keyWords);
        ImageView imageView = (ImageView) baseViewHolder.getView(R.id.iv_isPlaying);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        if (TextUtils.isEmpty(this.playingPath) || !this.playingPath.equals(mediaFileItem.getData())) {
            animationDrawable.stop();
            imageView.setVisibility(8);
            marqueeView.setTextColor(ContextCompat.getColor(this.mContext, R.color.colorFFFFFF));
            marqueeView2.setTextColor(ContextCompat.getColor(this.mContext, R.color.colorFFFFFF));
            marqueeView3.setTextColor(ContextCompat.getColor(this.mContext, R.color.colorFFFFFF));
            baseViewHolder.itemView.setBackgroundResource(17170445);
        } else {
            if (CarMusicManager.getInstance().isPlaying()) {
                animationDrawable.start();
            } else {
                animationDrawable.stop();
            }
            imageView.setVisibility(0);
            marqueeView.setTextColor(ContextCompat.getColor(this.mContext, R.color.colorF3AE3D));
            marqueeView2.setTextColor(ContextCompat.getColor(this.mContext, R.color.colorF3AE3D));
            marqueeView3.setTextColor(ContextCompat.getColor(this.mContext, R.color.colorF3AE3D));
            this.position = baseViewHolder.getLayoutPosition();
            baseViewHolder.itemView.setBackgroundResource(R.mipmap.music_pic_sel_88px);
        }
        baseViewHolder.addOnClickListener(R.id.tv_name, R.id.tv_artist, R.id.tv_album);
    }

    public String getPlayingPath() {
        return this.playingPath;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public /* bridge */ /* synthetic */ void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i, @NonNull List list) {
        onBindViewHolder((BaseViewHolder) viewHolder, i, (List<Object>) list);
    }

    public void onBindViewHolder(@NonNull BaseViewHolder baseViewHolder, int i, @NonNull List<Object> list) {
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
                    Glide.with(this.mContext).load(bArr).error(R.mipmap.music_search_pic_cover).into(syncImageView);
                    return;
                }
            }
            return;
        }
        String string = obj.toString();
        ImageView imageView = (ImageView) baseViewHolder.getView(R.id.iv_isPlaying);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        MarqueeView marqueeView = (MarqueeView) baseViewHolder.getView(R.id.tv_name);
        MarqueeView marqueeView2 = (MarqueeView) baseViewHolder.getView(R.id.tv_artist);
        MarqueeView marqueeView3 = (MarqueeView) baseViewHolder.getView(R.id.tv_album);
        if (TextUtils.isEmpty(this.playingPath) || !this.playingPath.equals(string)) {
            animationDrawable.stop();
            imageView.setVisibility(8);
            marqueeView.setTextColor(ContextCompat.getColor(this.mContext, R.color.colorFFFFFF));
            marqueeView2.setTextColor(ContextCompat.getColor(this.mContext, R.color.colorFFFFFF));
            marqueeView3.setTextColor(ContextCompat.getColor(this.mContext, R.color.colorFFFFFF));
            baseViewHolder.itemView.setBackgroundResource(17170445);
        } else {
            if (CarMusicManager.getInstance().isPlaying()) {
                animationDrawable.start();
            } else {
                animationDrawable.stop();
            }
            imageView.setVisibility(0);
            marqueeView.setTextColor(ContextCompat.getColor(this.mContext, R.color.colorF3AE3D));
            marqueeView2.setTextColor(ContextCompat.getColor(this.mContext, R.color.colorF3AE3D));
            marqueeView3.setTextColor(ContextCompat.getColor(this.mContext, R.color.colorF3AE3D));
            baseViewHolder.itemView.setBackgroundResource(R.mipmap.music_pic_sel_88px);
        }
        marqueeView.setRepetType(1);
        marqueeView2.setRepetType(1);
        marqueeView3.setRepetType(1);
    }

    public void setKeyWords(String str, boolean z) {
        this.keyWords = str;
        if (z) {
            notifyDataSetChanged();
        }
    }

    public void setPlaying(String str, boolean z) {
        int i = 0;
        while (true) {
            if (i >= getData().size()) {
                i = -1;
                break;
            }
            MediaFileItem mediaFileItem = getData().get(i);
            if (mediaFileItem != null && !TextUtils.isEmpty(mediaFileItem.getData()) && mediaFileItem.getData().equals(str)) {
                break;
            } else {
                i++;
            }
        }
        if (!z) {
            this.playingPath = str;
            this.position = i;
        } else {
            if (i == this.position) {
                notifyItemChanged(this.position, str);
                return;
            }
            if (this.position >= 0) {
                notifyItemChanged(this.position, "");
            }
            this.playingPath = str;
            this.position = i;
            if (this.position >= 0) {
                notifyItemChanged(this.position, str);
            }
        }
    }
}
