package com.yftech.libmusic.event;

import androidx.annotation.NonNull;

/* JADX INFO: loaded from: classes4.dex */
public class MusicSourceChangedEvent {
    public String music_source;

    public MusicSourceChangedEvent(String str) {
        this.music_source = str;
    }

    @NonNull
    public String toString() {
        return "music_source=" + this.music_source;
    }
}
