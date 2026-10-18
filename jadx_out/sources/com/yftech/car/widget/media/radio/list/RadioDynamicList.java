package com.yftech.car.widget.media.radio.list;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yftech.car.media.radio.entity.DataOfRadio;
import com.yftech.car.widget.media.R;
import com.yftech.car.widget.media.manager.RadioManager;
import com.yftech.car.widget.media.radio.list.adapter.DynamicAdapter;
import com.yftech.car.widget.media.recyclerview.widget.GridLayoutManager;
import com.yftech.car.widget.media.recyclerview.widget.LinearLayoutManager;
import com.yftech.car.widget.media.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public class RadioDynamicList extends RecyclerView {
    public static final int FAVOR_LIST = 2;
    public static final int SCAN_SEARCH_LIST = 1;
    private static final String TAG = "RadioDynamicList";
    private DynamicAdapter mAdapter;
    private boolean mInitAdapter;
    private LinearLayoutManager mLayoutManager;
    private final List<DataOfRadio> mList;
    private int mListType;

    private RadioDynamicList(@NonNull Context context) {
        super(context);
        this.mListType = 1;
        this.mList = new ArrayList();
    }

    public RadioDynamicList(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.recyclerViewStyle);
    }

    public RadioDynamicList(@NonNull Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mListType = 1;
        this.mList = new ArrayList();
        this.mAdapter = new DynamicAdapter(this.mList, context, attributeSet);
        this.mListType = context.obtainStyledAttributes(attributeSet, R.styleable.RadioDynamicList).getInt(R.styleable.RadioDynamicList_list_type, 1);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.radio_list);
        int i2 = typedArrayObtainStyledAttributes.getInt(R.styleable.radio_list_span_count, 1);
        int i3 = typedArrayObtainStyledAttributes.getInt(R.styleable.radio_list_list_orientation, 0);
        if (i2 > 1) {
            this.mLayoutManager = new GridLayoutManager(context, i2);
        } else {
            this.mLayoutManager = new LinearLayoutManager(context);
        }
        this.mLayoutManager.setOrientation(i3);
    }

    private void updatePosition(DataOfRadio dataOfRadio) {
        for (int i = 0; i < this.mList.size(); i++) {
            if (dataOfRadio.getFreq() == this.mList.get(i).getFreq()) {
                int iFindFirstVisibleItemPosition = this.mLayoutManager.findFirstVisibleItemPosition();
                if (i >= iFindFirstVisibleItemPosition + 1 && i <= this.mLayoutManager.findLastVisibleItemPosition() - 1) {
                    Log.d(TAG, "no update adapter firstItemPosition ==" + iFindFirstVisibleItemPosition);
                    return;
                }
                if (i <= iFindFirstVisibleItemPosition) {
                    this.mLayoutManager.scrollToPositionWithOffset(i, 0);
                    return;
                }
                View childAt = this.mLayoutManager.getChildAt(iFindFirstVisibleItemPosition);
                if (childAt == null) {
                    this.mLayoutManager.scrollToPositionWithOffset(i, 0);
                    return;
                }
                int height = getHeight();
                int height2 = childAt.getHeight();
                this.mLayoutManager.scrollToPositionWithOffset(i, (((height / height2) - 1) * height2) + (height % height2));
                return;
            }
        }
    }

    public boolean isType(int i) {
        return this.mListType == i;
    }

    @Override // com.yftech.car.widget.media.recyclerview.widget.RecyclerView
    public void onChildDetachedFromWindow(@NonNull View view) {
        super.onChildDetachedFromWindow(view);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.yftech.car.widget.media.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        setLayoutManager(this.mLayoutManager);
        updateList(RadioManager.getInstance().getFreq());
        setAdapter(this.mAdapter);
        this.mInitAdapter = true;
    }

    public void updateList(DataOfRadio dataOfRadio) {
        if (dataOfRadio != null) {
            this.mList.clear();
            List<DataOfRadio> searchList = this.mListType == 1 ? RadioManager.getInstance().getSearchList(dataOfRadio.getType()) : RadioManager.getInstance().getFavorsList(dataOfRadio.getType());
            if (searchList != null && searchList.size() > 0) {
                this.mList.addAll(searchList);
            }
        }
        if (this.mInitAdapter) {
            this.mAdapter.notifyDataSetChanged();
        }
        if (dataOfRadio != null) {
            updatePosition(dataOfRadio);
        }
    }
}
