package com.yftech.libmusic.dialog.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.media.common.ui.widget.WordsLimitTextView;
import com.media.common.util.DateUtil;
import com.media.common.util.LogUtil;
import com.media.common.util.ValueUtil;
import com.yftech.libmusic.R;
import com.yftech.libmusic.bean.MediaFileItem;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public class MusicSongListAdapter extends BaseQuickAdapter<MediaFileItem, BaseViewHolder> {
    private boolean isPlay;
    private Context mContext;
    private int position;

    public MusicSongListAdapter(Context context) {
        super(R.layout.item_music_song);
        this.position = -1;
        this.isPlay = false;
        this.mContext = context;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.chad.library.adapter.base.BaseQuickAdapter
    public void convert(BaseViewHolder baseViewHolder, MediaFileItem mediaFileItem) {
        baseViewHolder.setText(R.id.tv_duration, DateUtil.getMusicTime(mediaFileItem.getDuration())).setText(R.id.tv_order, String.valueOf(baseViewHolder.getLayoutPosition() + 1));
        baseViewHolder.setVisible(R.id.tv_order, this.position != baseViewHolder.getLayoutPosition());
        WordsLimitTextView wordsLimitTextView = (WordsLimitTextView) baseViewHolder.getView(R.id.tv_name);
        WordsLimitTextView wordsLimitTextView2 = (WordsLimitTextView) baseViewHolder.getView(R.id.tv_artist);
        wordsLimitTextView.setContent(mediaFileItem.getTitle());
        wordsLimitTextView2.setContent(mediaFileItem.getArtist());
        if (this.position == baseViewHolder.getLayoutPosition()) {
            wordsLimitTextView.setTextColor(ContextCompat.getColor(this.mContext, R.color.colorDD9D09));
            wordsLimitTextView2.setTextColor(ContextCompat.getColor(this.mContext, R.color.colorDD9D09));
            baseViewHolder.setTextColor(R.id.tv_duration, ContextCompat.getColor(this.mContext, R.color.colorB3DD9D09));
        } else {
            wordsLimitTextView.setTextColor(ContextCompat.getColor(this.mContext, R.color.colorFFFFFF));
            wordsLimitTextView2.setTextColor(ContextCompat.getColor(this.mContext, R.color.color80FFFFFF));
            baseViewHolder.setTextColor(R.id.tv_duration, ContextCompat.getColor(this.mContext, R.color.color80FFFFFF));
        }
        ImageView imageView = (ImageView) baseViewHolder.getView(R.id.iv_playing);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        if (this.position == baseViewHolder.getLayoutPosition()) {
            if (this.isPlay) {
                animationDrawable.start();
            } else {
                animationDrawable.stop();
            }
            imageView.setVisibility(0);
        } else {
            animationDrawable.stop();
            imageView.setVisibility(4);
        }
        baseViewHolder.addOnClickListener(R.id.tv_name, R.id.tv_artist);
    }

    public int getPosition() {
        return this.position;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public /* bridge */ /* synthetic */ void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i, @NonNull List list) {
        onBindViewHolder((BaseViewHolder) viewHolder, i, (List<Object>) list);
    }

    public void onBindViewHolder(@NonNull BaseViewHolder baseViewHolder, int i, @NonNull List<Object> list) {
        if (list.isEmpty()) {
            super.onBindViewHolder(baseViewHolder, i);
            return;
        }
        LogUtil.d("onBindViewHolder:position=" + i);
        LogUtil.d("onBindViewHolder:isPlaying=" + ValueUtil.parseBoolean(list.get(0).toString()));
        baseViewHolder.setVisible(R.id.tv_order, this.position != i);
        WordsLimitTextView wordsLimitTextView = (WordsLimitTextView) baseViewHolder.getView(R.id.tv_name);
        WordsLimitTextView wordsLimitTextView2 = (WordsLimitTextView) baseViewHolder.getView(R.id.tv_artist);
        ImageView imageView = (ImageView) baseViewHolder.getView(R.id.iv_playing);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        if (this.position != i) {
            animationDrawable.stop();
            imageView.setVisibility(4);
            wordsLimitTextView.setTextColor(ContextCompat.getColor(this.mContext, R.color.colorFFFFFF));
            wordsLimitTextView2.setTextColor(ContextCompat.getColor(this.mContext, R.color.color80FFFFFF));
            baseViewHolder.setTextColor(R.id.tv_duration, ContextCompat.getColor(this.mContext, R.color.color80FFFFFF));
            return;
        }
        wordsLimitTextView.setTextColor(ContextCompat.getColor(this.mContext, R.color.colorDD9D09));
        wordsLimitTextView2.setTextColor(ContextCompat.getColor(this.mContext, R.color.colorDD9D09));
        baseViewHolder.setTextColor(R.id.tv_duration, ContextCompat.getColor(this.mContext, R.color.colorB3DD9D09));
        if (this.isPlay) {
            animationDrawable.start();
        } else {
            animationDrawable.stop();
        }
        imageView.setVisibility(0);
    }

    public void setPlayingIndex(int i, boolean z, boolean z2) {
        this.isPlay = z2;
        if (!z) {
            this.position = i;
            return;
        }
        int i2 = this.position;
        this.position = i;
        notifyItemChanged(i2, false);
        notifyItemChanged(this.position, true);
    }
}
