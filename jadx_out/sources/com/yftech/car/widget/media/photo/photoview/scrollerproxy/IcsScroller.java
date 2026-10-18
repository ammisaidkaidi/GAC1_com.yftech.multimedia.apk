package com.yftech.car.widget.media.photo.photoview.scrollerproxy;

import android.annotation.TargetApi;
import android.content.Context;

/* JADX INFO: loaded from: classes3.dex */
@TargetApi(14)
public class IcsScroller extends GingerScroller {
    public IcsScroller(Context context) {
        super(context);
    }

    @Override // com.yftech.car.widget.media.photo.photoview.scrollerproxy.GingerScroller, com.yftech.car.widget.media.photo.photoview.scrollerproxy.ScrollerProxy
    public boolean computeScrollOffset() {
        return this.mScroller.computeScrollOffset();
    }
}
