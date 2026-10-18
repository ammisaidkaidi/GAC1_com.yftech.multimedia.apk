package com.yftech.car.widget.media.list.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.yftech.car.widget.media.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public class SearchHistoryHolder extends RecyclerView.ViewHolder {
    private Button mDeleteBtn;
    private ImageView mDeleteImage;
    private final List<TextView> mTextViewList;

    public SearchHistoryHolder(View view) {
        super(view);
        this.mTextViewList = new ArrayList();
        if (!(view instanceof ViewGroup)) {
            if (view instanceof Button) {
                this.mDeleteBtn = (Button) view;
                return;
            } else if (view instanceof TextView) {
                this.mTextViewList.add((TextView) view);
                return;
            } else {
                if (view instanceof ImageView) {
                    this.mDeleteImage = (ImageView) view;
                    return;
                }
                return;
            }
        }
        LinkedList linkedList = new LinkedList();
        linkedList.add((ViewGroup) view);
        while (!linkedList.isEmpty()) {
            ViewGroup viewGroup = (ViewGroup) linkedList.removeFirst();
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View childAt = viewGroup.getChildAt(i);
                if (childAt instanceof ViewGroup) {
                    linkedList.add((ViewGroup) childAt);
                } else if (childAt instanceof Button) {
                    this.mDeleteBtn = (Button) childAt;
                } else if (childAt instanceof TextView) {
                    this.mTextViewList.add((TextView) childAt);
                } else if (childAt instanceof ImageView) {
                    this.mDeleteImage = (ImageView) childAt;
                }
            }
        }
    }

    public void setDeleteOnClickListener(View.OnClickListener onClickListener) {
        if (this.mDeleteBtn != null) {
            this.mDeleteBtn.setOnClickListener(onClickListener);
        }
        if (this.mDeleteImage != null) {
            this.mDeleteImage.setOnClickListener(onClickListener);
        }
    }

    public void setDeleteVisibility(int i) {
        if (this.mDeleteBtn != null) {
            this.mDeleteBtn.setVisibility(i);
        }
        if (this.mDeleteImage != null) {
            this.mDeleteImage.setVisibility(i);
        }
    }

    public void setItemData(String str, int i) {
        this.itemView.setTag(str);
        if (this.mDeleteBtn != null) {
            this.mDeleteBtn.setTag(str);
        }
        if (this.mDeleteImage != null) {
            this.mDeleteImage.setTag(str);
        }
        if (this.mTextViewList.size() > 1) {
            this.mTextViewList.get(0).setText(String.valueOf(i + 1));
            this.mTextViewList.get(1).setText(str);
        } else {
            Iterator<TextView> it = this.mTextViewList.iterator();
            while (it.hasNext()) {
                it.next().setText(str);
            }
        }
    }
}
