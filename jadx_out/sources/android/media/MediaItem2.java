package android.media;

import android.media.update.ApiLoader;
import android.media.update.MediaItem2Provider;
import android.os.Bundle;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: classes.dex */
public class MediaItem2 {
    public static final int FLAG_BROWSABLE = 1;
    public static final int FLAG_PLAYABLE = 2;
    private final MediaItem2Provider mProvider;

    public static final class Builder {
        private final MediaItem2Provider.BuilderProvider mProvider;

        public Builder(int i) {
            this.mProvider = ApiLoader.getProvider().createMediaItem2Builder(this, i);
        }

        public MediaItem2 build() {
            return this.mProvider.build_impl();
        }

        public Builder setDataSourceDesc(DataSourceDesc dataSourceDesc) {
            return this.mProvider.setDataSourceDesc_impl(dataSourceDesc);
        }

        public Builder setMediaId(String str) {
            return this.mProvider.setMediaId_impl(str);
        }

        public Builder setMetadata(MediaMetadata2 mediaMetadata2) {
            return this.mProvider.setMetadata_impl(mediaMetadata2);
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface Flags {
    }

    public MediaItem2(MediaItem2Provider mediaItem2Provider) {
        this.mProvider = mediaItem2Provider;
    }

    public static MediaItem2 fromBundle(Bundle bundle) {
        return ApiLoader.getProvider().fromBundle_MediaItem2(bundle);
    }

    public boolean equals(Object obj) {
        return this.mProvider.equals_impl(obj);
    }

    public DataSourceDesc getDataSourceDesc() {
        return this.mProvider.getDataSourceDesc_impl();
    }

    public int getFlags() {
        return this.mProvider.getFlags_impl();
    }

    public String getMediaId() {
        return this.mProvider.getMediaId_impl();
    }

    public MediaMetadata2 getMetadata() {
        return this.mProvider.getMetadata_impl();
    }

    public MediaItem2Provider getProvider() {
        return this.mProvider;
    }

    public int hashCode() {
        return super.hashCode();
    }

    public boolean isBrowsable() {
        return this.mProvider.isBrowsable_impl();
    }

    public boolean isPlayable() {
        return this.mProvider.isPlayable_impl();
    }

    public void setMetadata(MediaMetadata2 mediaMetadata2) {
        this.mProvider.setMetadata_impl(mediaMetadata2);
    }

    public Bundle toBundle() {
        return this.mProvider.toBundle_impl();
    }

    public String toString() {
        return this.mProvider.toString_impl();
    }
}
