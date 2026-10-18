package com.yftech.car.widget.media.radio.list;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yftech.car.media.radio.entity.DataOfRadio;
import com.yftech.car.widget.media.R;
import com.yftech.car.widget.media.manager.RadioManager;
import com.yftech.car.widget.media.radio.list.adapter.FixedFavorAdapter;
import com.yftech.car.widget.media.recyclerview.widget.GridLayoutManager;
import com.yftech.car.widget.media.recyclerview.widget.LinearLayoutManager;
import com.yftech.car.widget.media.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public class RadioFixedFavorList extends RecyclerView {
    private static final String TAG = "RadioFixedFavorList";
    private FixedFavorAdapter mAdapter;
    private int mAmListSize;
    private int mFmListSize;
    private boolean mInitAdapter;
    private LinearLayoutManager mLayoutManager;
    private final List<DataOfRadio> mList;
    private final RecyclerView.OnScrollListener mOnScrollListener;

    private RadioFixedFavorList(@NonNull Context context) {
        super(context);
        this.mList = new ArrayList();
        this.mOnScrollListener = new RecyclerView.OnScrollListener() { // from class: com.yftech.car.widget.media.radio.list.RadioFixedFavorList.1
            @Override // com.yftech.car.widget.media.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int i) {
                super.onScrollStateChanged(recyclerView, i);
            }

            @Override // com.yftech.car.widget.media.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(@NonNull RecyclerView recyclerView, int i, int i2) {
                super.onScrolled(recyclerView, i, i2);
            }
        };
    }

    public RadioFixedFavorList(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.recyclerViewStyle);
    }

    public RadioFixedFavorList(@NonNull Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mList = new ArrayList();
        this.mOnScrollListener = new RecyclerView.OnScrollListener() { // from class: com.yftech.car.widget.media.radio.list.RadioFixedFavorList.1
            @Override // com.yftech.car.widget.media.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int i2) {
                super.onScrollStateChanged(recyclerView, i2);
            }

            @Override // com.yftech.car.widget.media.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(@NonNull RecyclerView recyclerView, int i2, int i3) {
                super.onScrolled(recyclerView, i2, i3);
            }
        };
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.RadioFixedFavorList);
        this.mFmListSize = typedArrayObtainStyledAttributes.getInt(R.styleable.RadioFixedFavorList_fm_list_size, 15);
        this.mAmListSize = typedArrayObtainStyledAttributes.getInt(R.styleable.RadioFixedFavorList_am_list_size, 10);
        TypedArray typedArrayObtainStyledAttributes2 = context.obtainStyledAttributes(attributeSet, R.styleable.radio_list);
        int i2 = typedArrayObtainStyledAttributes2.getInt(R.styleable.radio_list_span_count, 1);
        int i3 = typedArrayObtainStyledAttributes2.getInt(R.styleable.radio_list_list_orientation, 0);
        if (i2 > 1) {
            this.mLayoutManager = new GridLayoutManager(context, i2);
        } else {
            this.mLayoutManager = new LinearLayoutManager(context);
        }
        this.mLayoutManager.setOrientation(i3);
        this.mAdapter = new FixedFavorAdapter(this.mList, typedArrayObtainStyledAttributes2);
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
        updateFavorsList(RadioManager.getInstance().getFreq());
        setLayoutManager(this.mLayoutManager);
        setAdapter(this.mAdapter);
        this.mInitAdapter = true;
    }

    public void updateFavorsList(DataOfRadio dataOfRadio) {
        if (dataOfRadio == null) {
            if (this.mInitAdapter) {
                this.mAdapter.notifyDataSetChanged();
                return;
            }
            return;
        }
        if (dataOfRadio.getType() == -1) {
            Log.e(TAG, "onFinishInflate: radio service no connect");
            return;
        }
        List<DataOfRadio> favorsList = RadioManager.getInstance().getFavorsList(dataOfRadio.getType());
        Log.d(TAG, "updateFavorsList: favorsList==" + favorsList);
        this.mList.clear();
        if (favorsList == null || favorsList.size() <= 0) {
            int i = dataOfRadio.getType() == 2 ? this.mAmListSize : this.mFmListSize;
            for (int i2 = 0; i2 < i; i2++) {
                this.mList.add(new DataOfRadio("", 0, 0));
            }
            RadioManager.getInstance().updateFavorsList(this.mList, dataOfRadio.getType());
        } else {
            int i3 = dataOfRadio.getType() == 2 ? this.mAmListSize : this.mFmListSize;
            if (i3 == favorsList.size()) {
                this.mList.addAll(favorsList);
            } else {
                for (int i4 = 0; i4 < i3; i4++) {
                    if (i4 < favorsList.size()) {
                        DataOfRadio dataOfRadio2 = favorsList.get(i4);
                        if (dataOfRadio2 != null) {
                            this.mList.add(dataOfRadio2);
                        } else {
                            this.mList.add(new DataOfRadio("", 0, 0));
                        }
                    } else {
                        this.mList.add(new DataOfRadio("", 0, 0));
                    }
                }
                RadioManager.getInstance().updateFavorsList(this.mList, dataOfRadio.getType());
            }
        }
        if (this.mInitAdapter) {
            this.mAdapter.notifyDataSetChanged();
        }
    }
}
