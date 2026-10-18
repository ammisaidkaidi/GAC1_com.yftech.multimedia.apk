package com.alibaba.android.vlayout;

import android.util.Pair;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.alibaba.android.vlayout.layout.SingleLayoutHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/* JADX INFO: loaded from: classes3.dex */
public class DelegateAdapter extends VirtualLayoutAdapter<RecyclerView.ViewHolder> {
    private long[] cantorReverse;

    @NonNull
    private final List<Pair<AdapterDataObserver, Adapter>> mAdapters;
    private final boolean mHasConsistItemType;
    private int mIndex;
    private final SparseArray<Pair<AdapterDataObserver, Adapter>> mIndexAry;

    @Nullable
    private AtomicInteger mIndexGen;
    private SparseArray<Adapter> mItemTypeAry;
    private int mTotal;

    public static abstract class Adapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
        protected void onBindViewHolderWithOffset(VH vh, int i, int i2) {
        }

        protected void onBindViewHolderWithOffset(VH vh, int i, int i2, List<Object> list) {
            onBindViewHolderWithOffset(vh, i, i2);
        }

        public abstract LayoutHelper onCreateLayoutHelper();
    }

    protected class AdapterDataObserver extends RecyclerView.AdapterDataObserver {
        int mIndex;
        int mStartPosition;

        public AdapterDataObserver(int i, int i2) {
            this.mIndex = -1;
            this.mStartPosition = i;
            this.mIndex = i2;
        }

        /* JADX WARN: Multi-variable type inference failed */
        private boolean updateLayoutHelper() {
            int iFindAdapterPositionByIndex;
            if (this.mIndex < 0 || (iFindAdapterPositionByIndex = DelegateAdapter.this.findAdapterPositionByIndex(this.mIndex)) < 0) {
                return false;
            }
            Pair pair = (Pair) DelegateAdapter.this.mAdapters.get(iFindAdapterPositionByIndex);
            LinkedList linkedList = new LinkedList(DelegateAdapter.this.getLayoutHelpers());
            LayoutHelper layoutHelper = (LayoutHelper) linkedList.get(iFindAdapterPositionByIndex);
            if (layoutHelper.getItemCount() != ((Adapter) pair.second).getItemCount()) {
                layoutHelper.setItemCount(((Adapter) pair.second).getItemCount());
                DelegateAdapter.this.mTotal = this.mStartPosition + ((Adapter) pair.second).getItemCount();
                for (int i = iFindAdapterPositionByIndex + 1; i < DelegateAdapter.this.mAdapters.size(); i++) {
                    Pair pair2 = (Pair) DelegateAdapter.this.mAdapters.get(i);
                    ((AdapterDataObserver) pair2.first).mStartPosition = DelegateAdapter.this.mTotal;
                    DelegateAdapter.this.mTotal += ((Adapter) pair2.second).getItemCount();
                }
                DelegateAdapter.super.setLayoutHelpers(linkedList);
            }
            return true;
        }

        public int getIndex() {
            return this.mIndex;
        }

        public int getStartPosition() {
            return this.mStartPosition;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
        public void onChanged() {
            if (updateLayoutHelper()) {
                DelegateAdapter.this.notifyDataSetChanged();
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeChanged(int i, int i2) {
            if (updateLayoutHelper()) {
                DelegateAdapter.this.notifyItemRangeChanged(this.mStartPosition + i, i2);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeChanged(int i, int i2, Object obj) {
            if (updateLayoutHelper()) {
                DelegateAdapter.this.notifyItemRangeChanged(this.mStartPosition + i, i2, obj);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeInserted(int i, int i2) {
            if (updateLayoutHelper()) {
                DelegateAdapter.this.notifyItemRangeInserted(this.mStartPosition + i, i2);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeMoved(int i, int i2, int i3) {
            if (updateLayoutHelper()) {
                DelegateAdapter.this.notifyItemMoved(this.mStartPosition + i, this.mStartPosition + i2);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeRemoved(int i, int i2) {
            if (updateLayoutHelper()) {
                DelegateAdapter.this.notifyItemRangeRemoved(this.mStartPosition + i, i2);
            }
        }

        public void updateStartPositionAndIndex(int i, int i2) {
            this.mStartPosition = i;
            this.mIndex = i2;
        }
    }

    static class SimpleViewAdapter extends Adapter<RecyclerView.ViewHolder> {
        private LayoutHelper mLayoutHelper;
        private View mView;

        public SimpleViewAdapter(@NonNull View view) {
            this(view, new SingleLayoutHelper());
        }

        public SimpleViewAdapter(@NonNull View view, @NonNull LayoutHelper layoutHelper) {
            this.mView = view;
            this.mLayoutHelper = layoutHelper;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return 1;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        }

        @Override // com.alibaba.android.vlayout.DelegateAdapter.Adapter
        public LayoutHelper onCreateLayoutHelper() {
            return new SingleLayoutHelper();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new SimpleViewHolder(this.mView);
        }
    }

    static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public SimpleViewHolder(View view) {
            super(view);
        }
    }

    public DelegateAdapter(VirtualLayoutManager virtualLayoutManager) {
        this(virtualLayoutManager, false, false);
    }

    public DelegateAdapter(VirtualLayoutManager virtualLayoutManager, boolean z) {
        this(virtualLayoutManager, z, false);
    }

    DelegateAdapter(VirtualLayoutManager virtualLayoutManager, boolean z, boolean z2) {
        super(virtualLayoutManager);
        this.mIndex = 0;
        this.mItemTypeAry = new SparseArray<>();
        this.mAdapters = new ArrayList();
        this.mTotal = 0;
        this.mIndexAry = new SparseArray<>();
        this.cantorReverse = new long[2];
        if (z2) {
            this.mIndexGen = new AtomicInteger(0);
        }
        this.mHasConsistItemType = z;
    }

    public static Adapter<? extends RecyclerView.ViewHolder> simpleAdapter(@NonNull View view) {
        return new SimpleViewAdapter(view);
    }

    public static Adapter<? extends RecyclerView.ViewHolder> simpleAdapter(@NonNull View view, @NonNull LayoutHelper layoutHelper) {
        return new SimpleViewAdapter(view, layoutHelper);
    }

    public void addAdapter(int i, @Nullable Adapter adapter) {
        addAdapters(i, Collections.singletonList(adapter));
    }

    public void addAdapter(@Nullable Adapter adapter) {
        addAdapters(Collections.singletonList(adapter));
    }

    public void addAdapters(int i, @Nullable List<Adapter> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        if (i < 0) {
            i = 0;
        }
        if (i > this.mAdapters.size()) {
            i = this.mAdapters.size();
        }
        ArrayList arrayList = new ArrayList();
        Iterator<Pair<AdapterDataObserver, Adapter>> it = this.mAdapters.iterator();
        while (it.hasNext()) {
            arrayList.add(it.next().second);
        }
        Iterator<Adapter> it2 = list.iterator();
        while (it2.hasNext()) {
            arrayList.add(i, it2.next());
            i++;
        }
        setAdapters(arrayList);
    }

    public void addAdapters(@Nullable List<Adapter> list) {
        addAdapters(this.mAdapters.size(), list);
    }

    public void clear() {
        this.mTotal = 0;
        this.mIndex = 0;
        if (this.mIndexGen != null) {
            this.mIndexGen.set(0);
        }
        this.mLayoutManager.setLayoutHelpers(null);
        for (Pair<AdapterDataObserver, Adapter> pair : this.mAdapters) {
            pair.second.unregisterAdapterDataObserver(pair.first);
        }
        this.mItemTypeAry.clear();
        this.mAdapters.clear();
        this.mIndexAry.clear();
    }

    public Adapter findAdapterByIndex(int i) {
        return this.mIndexAry.get(i).second;
    }

    @Nullable
    public Pair<AdapterDataObserver, Adapter> findAdapterByPosition(int i) {
        int size = this.mAdapters.size();
        if (size == 0) {
            return null;
        }
        int i2 = 0;
        int i3 = size - 1;
        while (i2 <= i3) {
            int i4 = (i2 + i3) / 2;
            Pair<AdapterDataObserver, Adapter> pair = this.mAdapters.get(i4);
            int itemCount = (pair.first.mStartPosition + pair.second.getItemCount()) - 1;
            if (pair.first.mStartPosition > i) {
                i3 = i4 - 1;
            } else if (itemCount < i) {
                i2 = i4 + 1;
            } else if (pair.first.mStartPosition <= i && itemCount >= i) {
                return pair;
            }
        }
        return null;
    }

    public int findAdapterPositionByIndex(int i) {
        Pair<AdapterDataObserver, Adapter> pair = this.mIndexAry.get(i);
        if (pair == null) {
            return -1;
        }
        return this.mAdapters.indexOf(pair);
    }

    public int findOffsetPosition(int i) {
        Pair<AdapterDataObserver, Adapter> pairFindAdapterByPosition = findAdapterByPosition(i);
        if (pairFindAdapterByPosition == null) {
            return -1;
        }
        return i - pairFindAdapterByPosition.first.mStartPosition;
    }

    public int getAdaptersCount() {
        if (this.mAdapters == null) {
            return 0;
        }
        return this.mAdapters.size();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.mTotal;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public long getItemId(int i) {
        Pair<AdapterDataObserver, Adapter> pairFindAdapterByPosition = findAdapterByPosition(i);
        if (pairFindAdapterByPosition == null) {
            return -1L;
        }
        long itemId = pairFindAdapterByPosition.second.getItemId(i - pairFindAdapterByPosition.first.mStartPosition);
        if (itemId < 0) {
            return -1L;
        }
        return Cantor.getCantor(pairFindAdapterByPosition.first.mIndex, itemId);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int i) {
        Pair<AdapterDataObserver, Adapter> pairFindAdapterByPosition = findAdapterByPosition(i);
        if (pairFindAdapterByPosition == null) {
            return -1;
        }
        int itemViewType = pairFindAdapterByPosition.second.getItemViewType(i - pairFindAdapterByPosition.first.mStartPosition);
        if (itemViewType < 0) {
            return itemViewType;
        }
        if (this.mHasConsistItemType) {
            this.mItemTypeAry.put(itemViewType, pairFindAdapterByPosition.second);
            return itemViewType;
        }
        return (int) Cantor.getCantor(itemViewType, pairFindAdapterByPosition.first.mIndex);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, List<Object> list) {
        super.onBindViewHolder(viewHolder, i, list);
        Pair<AdapterDataObserver, Adapter> pairFindAdapterByPosition = findAdapterByPosition(i);
        if (pairFindAdapterByPosition == null) {
            return;
        }
        pairFindAdapterByPosition.second.onBindViewHolder(viewHolder, i - pairFindAdapterByPosition.first.mStartPosition, list);
        pairFindAdapterByPosition.second.onBindViewHolderWithOffset(viewHolder, i - pairFindAdapterByPosition.first.mStartPosition, i, list);
    }

    /* JADX WARN: Type inference failed for: r4v2, types: [androidx.recyclerview.widget.RecyclerView$ViewHolder] */
    /* JADX WARN: Type inference failed for: r4v6, types: [androidx.recyclerview.widget.RecyclerView$ViewHolder] */
    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (this.mHasConsistItemType) {
            Adapter adapter = this.mItemTypeAry.get(i);
            if (adapter != null) {
                return adapter.onCreateViewHolder(viewGroup, i);
            }
            return null;
        }
        Cantor.reverseCantor(i, this.cantorReverse);
        int i2 = (int) this.cantorReverse[1];
        int i3 = (int) this.cantorReverse[0];
        Adapter adapterFindAdapterByIndex = findAdapterByIndex(i2);
        if (adapterFindAdapterByIndex == null) {
            return null;
        }
        return adapterFindAdapterByIndex.onCreateViewHolder(viewGroup, i3);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
        Pair<AdapterDataObserver, Adapter> pairFindAdapterByPosition;
        super.onViewAttachedToWindow(viewHolder);
        int position = viewHolder.getPosition();
        if (position <= 0 || (pairFindAdapterByPosition = findAdapterByPosition(position)) == null) {
            return;
        }
        pairFindAdapterByPosition.second.onViewAttachedToWindow(viewHolder);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder viewHolder) {
        Pair<AdapterDataObserver, Adapter> pairFindAdapterByPosition;
        super.onViewDetachedFromWindow(viewHolder);
        int position = viewHolder.getPosition();
        if (position <= 0 || (pairFindAdapterByPosition = findAdapterByPosition(position)) == null) {
            return;
        }
        pairFindAdapterByPosition.second.onViewDetachedFromWindow(viewHolder);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
        Pair<AdapterDataObserver, Adapter> pairFindAdapterByPosition;
        super.onViewRecycled(viewHolder);
        int position = viewHolder.getPosition();
        if (position <= 0 || (pairFindAdapterByPosition = findAdapterByPosition(position)) == null) {
            return;
        }
        pairFindAdapterByPosition.second.onViewRecycled(viewHolder);
    }

    public void removeAdapter(int i) {
        if (i < 0 || i >= this.mAdapters.size()) {
            return;
        }
        removeAdapter(this.mAdapters.get(i).second);
    }

    public void removeAdapter(@Nullable Adapter adapter) {
        if (adapter == null) {
            return;
        }
        removeAdapters(Collections.singletonList(adapter));
    }

    public void removeAdapters(@Nullable List<Adapter> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        LinkedList linkedList = new LinkedList(super.getLayoutHelpers());
        int size = list.size();
        for (int i = 0; i < size; i++) {
            Adapter adapter = list.get(i);
            Iterator<Pair<AdapterDataObserver, Adapter>> it = this.mAdapters.iterator();
            while (it.hasNext()) {
                Pair<AdapterDataObserver, Adapter> next = it.next();
                Adapter adapter2 = next.second;
                if (adapter2.equals(adapter)) {
                    adapter2.unregisterAdapterDataObserver(next.first);
                    int iFindAdapterPositionByIndex = findAdapterPositionByIndex(next.first.mIndex);
                    if (iFindAdapterPositionByIndex >= 0 && iFindAdapterPositionByIndex < linkedList.size()) {
                        linkedList.remove(iFindAdapterPositionByIndex);
                    }
                    it.remove();
                    break;
                }
            }
        }
        List<Adapter> arrayList = new ArrayList<>();
        Iterator<Pair<AdapterDataObserver, Adapter>> it2 = this.mAdapters.iterator();
        while (it2.hasNext()) {
            arrayList.add(it2.next().second);
        }
        setAdapters(arrayList);
    }

    public void removeFirstAdapter() {
        if (this.mAdapters == null || this.mAdapters.isEmpty()) {
            return;
        }
        removeAdapter(this.mAdapters.get(0).second);
    }

    public void removeLastAdapter() {
        if (this.mAdapters == null || this.mAdapters.isEmpty()) {
            return;
        }
        removeAdapter(this.mAdapters.get(this.mAdapters.size() - 1).second);
    }

    public void setAdapters(@Nullable List<Adapter> list) {
        int iIncrementAndGet;
        clear();
        if (list == null) {
            list = Collections.emptyList();
        }
        LinkedList linkedList = new LinkedList();
        this.mTotal = 0;
        boolean z = true;
        for (Adapter adapter : list) {
            int i = this.mTotal;
            if (this.mIndexGen == null) {
                iIncrementAndGet = this.mIndex;
                this.mIndex = iIncrementAndGet + 1;
            } else {
                iIncrementAndGet = this.mIndexGen.incrementAndGet();
            }
            AdapterDataObserver adapterDataObserver = new AdapterDataObserver(i, iIncrementAndGet);
            adapter.registerAdapterDataObserver(adapterDataObserver);
            z = z && adapter.hasStableIds();
            LayoutHelper layoutHelperOnCreateLayoutHelper = adapter.onCreateLayoutHelper();
            layoutHelperOnCreateLayoutHelper.setItemCount(adapter.getItemCount());
            this.mTotal += layoutHelperOnCreateLayoutHelper.getItemCount();
            linkedList.add(layoutHelperOnCreateLayoutHelper);
            Pair<AdapterDataObserver, Adapter> pairCreate = Pair.create(adapterDataObserver, adapter);
            this.mIndexAry.put(adapterDataObserver.mIndex, pairCreate);
            this.mAdapters.add(pairCreate);
        }
        if (!hasObservers()) {
            super.setHasStableIds(z);
        }
        super.setLayoutHelpers(linkedList);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void setHasStableIds(boolean z) {
    }

    @Override // com.alibaba.android.vlayout.VirtualLayoutAdapter
    @Deprecated
    public void setLayoutHelpers(List<LayoutHelper> list) {
        throw new UnsupportedOperationException("DelegateAdapter doesn't support setLayoutHelpers directly");
    }
}
