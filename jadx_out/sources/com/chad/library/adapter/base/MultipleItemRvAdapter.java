package com.chad.library.adapter.base;

import android.util.SparseArray;
import android.view.View;
import androidx.annotation.Nullable;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.chad.library.adapter.base.util.MultiTypeDelegate;
import com.chad.library.adapter.base.util.ProviderDelegate;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public abstract class MultipleItemRvAdapter<T, V extends BaseViewHolder> extends BaseQuickAdapter<T, V> {
    private SparseArray<BaseItemProvider> mItemProviders;
    protected ProviderDelegate mProviderDelegate;

    public MultipleItemRvAdapter(@Nullable List<T> list) {
        super(list);
    }

    private void bindClick(final V v, final T t, final int i, final BaseItemProvider baseItemProvider) {
        BaseQuickAdapter.OnItemClickListener onItemClickListener = getOnItemClickListener();
        BaseQuickAdapter.OnItemLongClickListener onItemLongClickListener = getOnItemLongClickListener();
        if (onItemClickListener == null || onItemLongClickListener == null) {
            View view = v.itemView;
            if (onItemClickListener == null) {
                view.setOnClickListener(new View.OnClickListener() { // from class: com.chad.library.adapter.base.MultipleItemRvAdapter.2
                    /* JADX WARN: Multi-variable type inference failed */
                    /* JADX WARN: Type inference fix 'apply assigned field type' failed
                    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$PrimitiveArg
                    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
                    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
                    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
                    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
                    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
                    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
                    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
                     */
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view2) {
                        baseItemProvider.onClick(v, t, i);
                    }
                });
            }
            if (onItemLongClickListener == null) {
                view.setOnLongClickListener(new View.OnLongClickListener() { // from class: com.chad.library.adapter.base.MultipleItemRvAdapter.3
                    /* JADX WARN: Multi-variable type inference failed */
                    /* JADX WARN: Type inference fix 'apply assigned field type' failed
                    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$PrimitiveArg
                    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
                    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
                    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
                    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
                    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
                    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
                    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
                     */
                    @Override // android.view.View.OnLongClickListener
                    public boolean onLongClick(View view2) {
                        return baseItemProvider.onLongClick(v, t, i);
                    }
                });
            }
        }
    }

    @Override // com.chad.library.adapter.base.BaseQuickAdapter
    protected void convert(V v, T t) {
        BaseItemProvider baseItemProvider = this.mItemProviders.get(v.getItemViewType());
        baseItemProvider.mContext = v.itemView.getContext();
        int layoutPosition = v.getLayoutPosition() - getHeaderLayoutCount();
        baseItemProvider.convert(v, t, layoutPosition);
        bindClick(v, t, layoutPosition, baseItemProvider);
    }

    public void finishInitialize() {
        this.mProviderDelegate = new ProviderDelegate();
        setMultiTypeDelegate(new MultiTypeDelegate<T>() { // from class: com.chad.library.adapter.base.MultipleItemRvAdapter.1
            @Override // com.chad.library.adapter.base.util.MultiTypeDelegate
            protected int getItemType(T t) {
                return MultipleItemRvAdapter.this.getViewType(t);
            }
        });
        registerItemProvider();
        this.mItemProviders = this.mProviderDelegate.getItemProviders();
        for (int i = 0; i < this.mItemProviders.size(); i++) {
            int iKeyAt = this.mItemProviders.keyAt(i);
            BaseItemProvider baseItemProvider = this.mItemProviders.get(iKeyAt);
            baseItemProvider.mData = this.mData;
            getMultiTypeDelegate().registerItemType(iKeyAt, baseItemProvider.layout());
        }
    }

    protected abstract int getViewType(T t);

    public abstract void registerItemProvider();
}
