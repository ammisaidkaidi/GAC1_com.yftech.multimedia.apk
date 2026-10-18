package com.yftech.car.widget.media.list.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.yftech.car.widget.media.list.holder.MediaDataDirectoryHolder;
import com.yftech.car.widget.media.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public class MediaDataDirectoryAdapter extends RecyclerView.Adapter<MediaDataDirectoryHolder> {
    private String mDirectoryPath;
    private final int mLayoutItemId;
    private MediaDataDirectoryHolder.OnItemClickPathListener mOnItemPathLister;
    private String mRootDirectory;
    private final List<String> mList = new ArrayList();
    private String mBaseRootDirectoryPath = null;
    private final MediaDataDirectoryHolder.OnItemClickPathListener mOnItemClickPathListener = new MediaDataDirectoryHolder.OnItemClickPathListener() { // from class: com.yftech.car.widget.media.list.adapter.MediaDataDirectoryAdapter.1
        @Override // com.yftech.car.widget.media.list.holder.MediaDataDirectoryHolder.OnItemClickPathListener
        public void onItemPath(String str) {
            if (MediaDataDirectoryAdapter.this.mOnItemPathLister != null) {
                MediaDataDirectoryAdapter.this.mOnItemPathLister.onItemPath(str);
            }
        }
    };

    public MediaDataDirectoryAdapter(int i, String str) {
        this.mLayoutItemId = i;
        this.mRootDirectory = str;
        if (TextUtils.isEmpty(this.mRootDirectory)) {
            this.mRootDirectory = "根目录";
        }
        if (i == -1) {
            throw new RuntimeException("MediaDataDirectoryList: directory_item_layout_id 没有设置布局，无法加载适配器item子类界面,请设置item布局再试");
        }
    }

    public String getBaseRootDirectoryPath() {
        return this.mBaseRootDirectoryPath;
    }

    public String getCurrentDirectoryPath() {
        return this.mDirectoryPath;
    }

    @Override // com.yftech.car.widget.media.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.mList.size();
    }

    public String getRootDirectory() {
        return this.mRootDirectory;
    }

    @Override // com.yftech.car.widget.media.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(@NonNull MediaDataDirectoryHolder mediaDataDirectoryHolder, int i) {
        mediaDataDirectoryHolder.setDirectoryText(this.mList.get(i), i);
    }

    @Override // com.yftech.car.widget.media.recyclerview.widget.RecyclerView.Adapter
    @NonNull
    public MediaDataDirectoryHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        MediaDataDirectoryHolder mediaDataDirectoryHolder = new MediaDataDirectoryHolder(LayoutInflater.from(viewGroup.getContext()).inflate(this.mLayoutItemId, viewGroup, false), this);
        mediaDataDirectoryHolder.setOnItemClickPathListener(this.mOnItemClickPathListener);
        return mediaDataDirectoryHolder;
    }

    public void setOnItemPathListener(MediaDataDirectoryHolder.OnItemClickPathListener onItemClickPathListener) {
        this.mOnItemPathLister = onItemClickPathListener;
    }

    public int updateDirectoryList(String str, String str2) {
        this.mBaseRootDirectoryPath = str;
        this.mDirectoryPath = str2;
        if (str2 != null) {
            this.mList.clear();
            this.mList.add(this.mDirectoryPath);
            if (str2.length() > str.length()) {
                while (str2.lastIndexOf("/") > 0) {
                    str2 = str2.substring(0, str2.lastIndexOf("/"));
                    this.mList.add(0, str2);
                    if (str2.length() <= str.length()) {
                        break;
                    }
                }
            }
            notifyDataSetChanged();
        }
        return Math.max(this.mList.size() - 1, 0);
    }

    public void updateList(List<String> list) {
        this.mList.clear();
        if (list != null && list.size() > 0) {
            this.mList.addAll(list);
        }
        notifyDataSetChanged();
    }
}
