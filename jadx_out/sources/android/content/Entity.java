package android.content;

import android.net.Uri;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public final class Entity {
    private final ArrayList<NamedContentValues> mSubValues = new ArrayList<>();
    private final ContentValues mValues;

    public static class NamedContentValues {
        public final Uri uri;
        public final ContentValues values;

        public NamedContentValues(Uri uri, ContentValues contentValues) {
            this.uri = uri;
            this.values = contentValues;
        }
    }

    public Entity(ContentValues contentValues) {
        this.mValues = contentValues;
    }

    public void addSubValue(Uri uri, ContentValues contentValues) {
        this.mSubValues.add(new NamedContentValues(uri, contentValues));
    }

    public ContentValues getEntityValues() {
        return this.mValues;
    }

    public ArrayList<NamedContentValues> getSubValues() {
        return this.mSubValues;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Entity: ");
        sb.append(getEntityValues());
        for (NamedContentValues namedContentValues : getSubValues()) {
            sb.append("\n  ");
            sb.append(namedContentValues.uri);
            sb.append("\n  -> ");
            sb.append(namedContentValues.values);
        }
        return sb.toString();
    }
}
