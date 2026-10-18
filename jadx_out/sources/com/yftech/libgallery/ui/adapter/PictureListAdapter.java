package com.yftech.libgallery.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.media.common.entity.QuickMultipleEntity;
import com.media.common.util.DateUtil;
import com.media.common.util.LogUtil;
import com.media.common.util.SharedPrefsUtil;
import com.media.common.util.ValueUtil;
import com.yftech.libgallery.R;
import com.yftech.libgallery.bean.MediaFileForDataItem;
import com.yftech.libgallery.bean.MediaFileForDateItem;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public class PictureListAdapter extends BaseQuickAdapter<QuickMultipleEntity, BaseViewHolder> {
    private HashSet<String> errorPhoto;
    private int index;
    private boolean isSelectorMode;
    private String lastViewedPath;
    private Context mContext;
    private OnItemClickListener onItemClickListener;
    private OnLastViewedCallback onLastViewedCallback;
    private SparseArray<QuickMultipleEntity> selected;

    public interface OnItemClickListener {
        void onItemClickListener(String str);
    }

    public interface OnLastViewedCallback {
        void onLoadLastViewedPosition(int i);
    }

    private class mOnItemClickListener implements View.OnClickListener {
        private int position;

        public mOnItemClickListener(int i) {
            this.position = i;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (!PictureListAdapter.this.isSelectorMode) {
                if (PictureListAdapter.this.onItemClickListener != null) {
                    QuickMultipleEntity quickMultipleEntity = PictureListAdapter.this.getData().get(this.position);
                    if (quickMultipleEntity.getItemType() == 1 && (quickMultipleEntity instanceof MediaFileForDataItem)) {
                        PictureListAdapter.this.onItemClickListener.onItemClickListener(((MediaFileForDataItem) quickMultipleEntity).getData());
                        return;
                    }
                    return;
                }
                return;
            }
            QuickMultipleEntity quickMultipleEntity2 = PictureListAdapter.this.getData().get(this.position);
            if (quickMultipleEntity2.getItemType() == 1) {
                StringBuilder sb = new StringBuilder();
                sb.append("picture is selected:");
                sb.append(PictureListAdapter.this.selected.indexOfKey(this.position) < 0);
                LogUtil.d(sb.toString());
                if (PictureListAdapter.this.selected.indexOfKey(this.position) < 0) {
                    PictureListAdapter.this.selected.append(this.position, quickMultipleEntity2);
                } else {
                    PictureListAdapter.this.selected.remove(this.position);
                }
                PictureListAdapter.this.notifyItemChanged(this.position, Boolean.valueOf(PictureListAdapter.this.selected.indexOfKey(this.position) >= 0));
            }
        }
    }

    private class mRequestListener implements RequestListener<Drawable> {
        int position;

        public mRequestListener(int i) {
            this.position = i;
        }

        @Override // com.bumptech.glide.request.RequestListener
        public boolean onLoadFailed(@Nullable GlideException glideException, Object obj, Target<Drawable> target, boolean z) {
            PictureListAdapter.this.notifyItemChanged(this.position, Integer.valueOf(this.position));
            return false;
        }

        @Override // com.bumptech.glide.request.RequestListener
        public boolean onResourceReady(Drawable drawable, Object obj, Target<Drawable> target, DataSource dataSource, boolean z) {
            return false;
        }
    }

    public PictureListAdapter(Context context, int i) {
        super(R.layout.item_list_mix);
        this.isSelectorMode = false;
        this.mContext = context;
        this.index = i;
        this.selected = new SparseArray<>();
        this.errorPhoto = new HashSet<>();
        if (i == 0) {
            this.lastViewedPath = SharedPrefsUtil.getInstance(context).getString(SharedPrefsUtil.TAG_PHOTO_VIEWED_LAST_FOR_USB_1, "");
        } else {
            this.lastViewedPath = SharedPrefsUtil.getInstance(context).getString(SharedPrefsUtil.TAG_PHOTO_VIEWED_LAST_FOR_USB_2, "");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.chad.library.adapter.base.BaseQuickAdapter
    public void convert(BaseViewHolder baseViewHolder, QuickMultipleEntity quickMultipleEntity) {
        Resources resources;
        int i;
        baseViewHolder.getView(R.id.v_isSelected).setSelected(this.selected.indexOfKey(baseViewHolder.getLayoutPosition()) >= 0);
        switch (quickMultipleEntity.getItemType()) {
            case 0:
                baseViewHolder.setVisible(R.id.v_isSelected, false);
                if (quickMultipleEntity instanceof MediaFileForDateItem) {
                    baseViewHolder.setGone(R.id.iv_pic, false).setGone(R.id.iv_cover, false).setGone(R.id.tv_item_date, true).setVisible(R.id.tv_tips, false).setText(R.id.tv_item_date, DateUtil.getSimpleTime(ValueUtil.parseLong(((MediaFileForDateItem) quickMultipleEntity).getDate())));
                    baseViewHolder.itemView.setOnClickListener(null);
                }
                break;
            case 1:
                baseViewHolder.setVisible(R.id.v_isSelected, this.isSelectorMode);
                if (quickMultipleEntity instanceof MediaFileForDataItem) {
                    baseViewHolder.setGone(R.id.iv_pic, true).setGone(R.id.iv_cover, true).setGone(R.id.tv_item_date, false);
                    ImageView imageView = (ImageView) baseViewHolder.getView(R.id.iv_pic);
                    MediaFileForDataItem mediaFileForDataItem = (MediaFileForDataItem) quickMultipleEntity;
                    if (this.errorPhoto.contains(mediaFileForDataItem.getData())) {
                        Glide.with(this.mContext).load(Integer.valueOf(R.mipmap.pv_pic_error2)).into(imageView);
                    } else {
                        Glide.with(this.mContext).load(mediaFileForDataItem.getData()).error(R.mipmap.pv_pic_error2).listener(new mRequestListener(baseViewHolder.getLayoutPosition())).into(imageView);
                        baseViewHolder.setVisible(R.id.tv_tips, false);
                    }
                    if (TextUtils.isEmpty(this.lastViewedPath) || !this.lastViewedPath.equals(mediaFileForDataItem.getData())) {
                        resources = this.mContext.getResources();
                        i = R.color.transparent;
                    } else {
                        resources = this.mContext.getResources();
                        i = R.mipmap.pv_pic_selected_4;
                    }
                    imageView.setForeground(resources.getDrawable(i));
                    ((ImageView) baseViewHolder.getView(R.id.iv_cover)).setOnClickListener(new mOnItemClickListener(baseViewHolder.getLayoutPosition()));
                }
                break;
        }
    }

    public List<QuickMultipleEntity> getImageSelector() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.selected.size(); i++) {
            arrayList.add(this.selected.get(this.selected.keyAt(i)));
        }
        return arrayList;
    }

    public boolean isSelectedPhoto() {
        return this.selected.size() > 0;
    }

    public boolean isSelectorMode() {
        return this.isSelectorMode;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public /* bridge */ /* synthetic */ void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i, @NonNull List list) {
        onBindViewHolder((BaseViewHolder) viewHolder, i, (List<Object>) list);
    }

    public void onBindViewHolder(@NonNull BaseViewHolder baseViewHolder, int i, @NonNull List<Object> list) {
        if (list.isEmpty()) {
            super.onBindViewHolder(baseViewHolder, i, list);
            return;
        }
        Object obj = list.get(0);
        if (!(obj instanceof Boolean)) {
            if (obj instanceof Integer) {
                LogUtil.d("onBindViewHolder:mPosition=" + ValueUtil.parseInt(obj.toString()));
                QuickMultipleEntity item = getItem(i);
                if (item instanceof MediaFileForDataItem) {
                    this.errorPhoto.add(((MediaFileForDataItem) item).getData());
                    return;
                }
                return;
            }
            return;
        }
        boolean z = ValueUtil.parseBoolean(obj.toString());
        LogUtil.d("onBindViewHolder:isSelectorMode_Payload=" + z);
        QuickMultipleEntity item2 = getItem(i);
        if (item2 == null || item2.getItemType() != 1) {
            return;
        }
        View view = baseViewHolder.getView(R.id.v_isSelected);
        view.setVisibility(z ? 0 : 4);
        view.setSelected(this.selected.indexOfKey(i) >= 0);
    }

    public void setLastViewedPath(String str) {
        this.lastViewedPath = str;
        notifyDataSetChanged();
        List<QuickMultipleEntity> data = getData();
        int i = 0;
        while (true) {
            if (i >= data.size()) {
                i = -1;
                break;
            }
            QuickMultipleEntity quickMultipleEntity = data.get(i);
            if (quickMultipleEntity.getItemType() == 1 && (quickMultipleEntity instanceof MediaFileForDataItem)) {
                MediaFileForDataItem mediaFileForDataItem = (MediaFileForDataItem) quickMultipleEntity;
                if (!TextUtils.isEmpty(mediaFileForDataItem.getData()) && mediaFileForDataItem.getData().equals(str)) {
                    break;
                }
            }
            i++;
        }
        if (this.onLastViewedCallback != null) {
            this.onLastViewedCallback.onLoadLastViewedPosition(i);
        }
    }

    @Override // com.chad.library.adapter.base.BaseQuickAdapter
    public void setNewData(@Nullable List<QuickMultipleEntity> list) {
        super.setNewData(list);
        if (list != null) {
            int i = -1;
            for (int i2 = 0; i2 < list.size(); i2++) {
                QuickMultipleEntity quickMultipleEntity = list.get(i2);
                if (quickMultipleEntity.getItemType() == 1 && (quickMultipleEntity instanceof MediaFileForDataItem)) {
                    MediaFileForDataItem mediaFileForDataItem = (MediaFileForDataItem) quickMultipleEntity;
                    if (!TextUtils.isEmpty(mediaFileForDataItem.getData()) && mediaFileForDataItem.getData().equals(this.lastViewedPath)) {
                        i = i2;
                        break;
                    }
                }
            }
            if (this.onLastViewedCallback != null) {
                this.onLastViewedCallback.onLoadLastViewedPosition(i);
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnLastViewedCallback(OnLastViewedCallback onLastViewedCallback) {
        this.onLastViewedCallback = onLastViewedCallback;
    }

    public void setSelectorMode(boolean z, boolean z2) {
        this.isSelectorMode = z;
        if (!this.isSelectorMode) {
            this.selected.clear();
        }
        if (z2) {
            notifyItemRangeChanged(0, getItemCount(), Boolean.valueOf(this.isSelectorMode));
        }
    }
}
