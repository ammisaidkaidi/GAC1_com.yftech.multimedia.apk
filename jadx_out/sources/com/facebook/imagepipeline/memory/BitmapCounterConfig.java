package com.facebook.imagepipeline.memory;

import com.facebook.infer.annotation.Nullsafe;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.STRICT)
public class BitmapCounterConfig {
    public static final int DEFAULT_MAX_BITMAP_COUNT = 384;
    private int mMaxBitmapCount;

    public static class Builder {
        private int mMaxBitmapCount;

        private Builder() {
            this.mMaxBitmapCount = 384;
        }

        public BitmapCounterConfig build() {
            return new BitmapCounterConfig(this);
        }

        public int getMaxBitmapCount() {
            return this.mMaxBitmapCount;
        }

        public Builder setMaxBitmapCount(int i) {
            this.mMaxBitmapCount = i;
            return this;
        }
    }

    public BitmapCounterConfig(Builder builder) {
        this.mMaxBitmapCount = 384;
        this.mMaxBitmapCount = builder.getMaxBitmapCount();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public int getMaxBitmapCount() {
        return this.mMaxBitmapCount;
    }

    public void setMaxBitmapCount(int i) {
        this.mMaxBitmapCount = i;
    }
}
