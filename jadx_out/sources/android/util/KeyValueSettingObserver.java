package android.util;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

/* JADX INFO: loaded from: classes2.dex */
public abstract class KeyValueSettingObserver {
    private static final String TAG = "KeyValueSettingObserver";
    private final ContentObserver mObserver;
    private final KeyValueListParser mParser = new KeyValueListParser(',');
    private final ContentResolver mResolver;
    private final Uri mSettingUri;

    private class SettingObserver extends ContentObserver {
        private SettingObserver(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            KeyValueSettingObserver.this.setParserValue();
            KeyValueSettingObserver.this.update(KeyValueSettingObserver.this.mParser);
        }
    }

    public KeyValueSettingObserver(Handler handler, ContentResolver contentResolver, Uri uri) {
        this.mObserver = new SettingObserver(handler);
        this.mResolver = contentResolver;
        this.mSettingUri = uri;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setParserValue() {
        String settingValue = getSettingValue(this.mResolver);
        try {
            this.mParser.setString(settingValue);
        } catch (IllegalArgumentException unused) {
            Slog.e(TAG, "Malformed setting: " + settingValue);
        }
    }

    public abstract String getSettingValue(ContentResolver contentResolver);

    public void start() {
        this.mResolver.registerContentObserver(this.mSettingUri, false, this.mObserver);
        setParserValue();
        update(this.mParser);
    }

    public void stop() {
        this.mResolver.unregisterContentObserver(this.mObserver);
    }

    public abstract void update(KeyValueListParser keyValueListParser);
}
