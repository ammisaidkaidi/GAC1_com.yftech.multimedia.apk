package com.google.android.material.datepicker;

/* JADX INFO: loaded from: classes3.dex */
abstract class OnSelectionChangedListener<S> {
    OnSelectionChangedListener() {
    }

    void onIncompleteSelectionChanged() {
    }

    abstract void onSelectionChanged(S s);
}
