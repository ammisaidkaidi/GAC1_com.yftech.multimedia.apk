package android.provider;

import android.app.backup.FullBackup;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriPermission;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MiniThumbFile;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import libcore.io.IoUtils;

/* JADX INFO: loaded from: classes2.dex */
public final class MediaStore {
    public static final String ACTION_IMAGE_CAPTURE = "android.media.action.IMAGE_CAPTURE";
    public static final String ACTION_IMAGE_CAPTURE_SECURE = "android.media.action.IMAGE_CAPTURE_SECURE";
    public static final String ACTION_VIDEO_CAPTURE = "android.media.action.VIDEO_CAPTURE";
    public static final String AUTHORITY = "media";
    private static final String CONTENT_AUTHORITY_SLASH = "content://media/";
    public static final String EXTRA_DURATION_LIMIT = "android.intent.extra.durationLimit";
    public static final String EXTRA_FINISH_ON_COMPLETION = "android.intent.extra.finishOnCompletion";
    public static final String EXTRA_FULL_SCREEN = "android.intent.extra.fullScreen";
    public static final String EXTRA_MEDIA_ALBUM = "android.intent.extra.album";
    public static final String EXTRA_MEDIA_ARTIST = "android.intent.extra.artist";
    public static final String EXTRA_MEDIA_FOCUS = "android.intent.extra.focus";
    public static final String EXTRA_MEDIA_GENRE = "android.intent.extra.genre";
    public static final String EXTRA_MEDIA_PLAYLIST = "android.intent.extra.playlist";
    public static final String EXTRA_MEDIA_RADIO_CHANNEL = "android.intent.extra.radio_channel";
    public static final String EXTRA_MEDIA_TITLE = "android.intent.extra.title";
    public static final String EXTRA_OUTPUT = "output";
    public static final String EXTRA_SCREEN_ORIENTATION = "android.intent.extra.screenOrientation";
    public static final String EXTRA_SHOW_ACTION_ICONS = "android.intent.extra.showActionIcons";
    public static final String EXTRA_SIZE_LIMIT = "android.intent.extra.sizeLimit";
    public static final String EXTRA_VIDEO_QUALITY = "android.intent.extra.videoQuality";
    public static final String INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH = "android.media.action.MEDIA_PLAY_FROM_SEARCH";
    public static final String INTENT_ACTION_MEDIA_SEARCH = "android.intent.action.MEDIA_SEARCH";

    @Deprecated
    public static final String INTENT_ACTION_MUSIC_PLAYER = "android.intent.action.MUSIC_PLAYER";
    public static final String INTENT_ACTION_STILL_IMAGE_CAMERA = "android.media.action.STILL_IMAGE_CAMERA";
    public static final String INTENT_ACTION_STILL_IMAGE_CAMERA_SECURE = "android.media.action.STILL_IMAGE_CAMERA_SECURE";
    public static final String INTENT_ACTION_TEXT_OPEN_FROM_SEARCH = "android.media.action.TEXT_OPEN_FROM_SEARCH";
    public static final String INTENT_ACTION_VIDEO_CAMERA = "android.media.action.VIDEO_CAMERA";
    public static final String INTENT_ACTION_VIDEO_PLAY_FROM_SEARCH = "android.media.action.VIDEO_PLAY_FROM_SEARCH";
    public static final String MEDIA_IGNORE_FILENAME = ".nomedia";
    public static final String MEDIA_SCANNER_VOLUME = "volume";
    public static final String META_DATA_STILL_IMAGE_CAMERA_PREWARM_SERVICE = "android.media.still_image_camera_preview_service";
    public static final String PARAM_DELETE_DATA = "deletedata";
    public static final String RETRANSLATE_CALL = "update_titles";
    private static final String TAG = "MediaStore";
    public static final String UNHIDE_CALL = "unhide";
    public static final String UNKNOWN_STRING = "<unknown>";

    public static final class Audio {

        public interface AlbumColumns {
            public static final String ALBUM = "album";
            public static final String ALBUM_ART = "album_art";
            public static final String ALBUM_ID = "album_id";
            public static final String ALBUM_KEY = "album_key";
            public static final String ARTIST = "artist";
            public static final String FIRST_YEAR = "minyear";
            public static final String LAST_YEAR = "maxyear";
            public static final String NUMBER_OF_SONGS = "numsongs";
            public static final String NUMBER_OF_SONGS_FOR_ARTIST = "numsongs_by_artist";
        }

        public static final class Albums implements BaseColumns, AlbumColumns {
            public static final String CONTENT_TYPE = "vnd.android.cursor.dir/albums";
            public static final String DEFAULT_SORT_ORDER = "album_key";
            public static final String ENTRY_CONTENT_TYPE = "vnd.android.cursor.item/album";
            public static final Uri INTERNAL_CONTENT_URI = getContentUri("internal");
            public static final Uri EXTERNAL_CONTENT_URI = getContentUri("external");

            public static Uri getContentUri(String str) {
                return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + str + "/audio/albums");
            }
        }

        public interface ArtistColumns {
            public static final String ARTIST = "artist";
            public static final String ARTIST_KEY = "artist_key";
            public static final String NUMBER_OF_ALBUMS = "number_of_albums";
            public static final String NUMBER_OF_TRACKS = "number_of_tracks";
        }

        public static final class Artists implements BaseColumns, ArtistColumns {
            public static final String CONTENT_TYPE = "vnd.android.cursor.dir/artists";
            public static final String DEFAULT_SORT_ORDER = "artist_key";
            public static final String ENTRY_CONTENT_TYPE = "vnd.android.cursor.item/artist";
            public static final Uri INTERNAL_CONTENT_URI = getContentUri("internal");
            public static final Uri EXTERNAL_CONTENT_URI = getContentUri("external");

            public static final class Albums implements AlbumColumns {
                public static final Uri getContentUri(String str, long j) {
                    return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + str + "/audio/artists/" + j + "/albums");
                }
            }

            public static Uri getContentUri(String str) {
                return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + str + "/audio/artists");
            }
        }

        public interface AudioColumns extends MediaColumns {
            public static final String ALBUM = "album";
            public static final String ALBUM_ARTIST = "album_artist";
            public static final String ALBUM_ID = "album_id";
            public static final String ALBUM_KEY = "album_key";
            public static final String ARTIST = "artist";
            public static final String ARTIST_ID = "artist_id";
            public static final String ARTIST_KEY = "artist_key";
            public static final String BOOKMARK = "bookmark";
            public static final String COMPILATION = "compilation";
            public static final String COMPOSER = "composer";
            public static final String DURATION = "duration";
            public static final String GENRE = "genre";
            public static final String IS_ALARM = "is_alarm";
            public static final String IS_MUSIC = "is_music";
            public static final String IS_NOTIFICATION = "is_notification";
            public static final String IS_PODCAST = "is_podcast";
            public static final String IS_RINGTONE = "is_ringtone";
            public static final String TITLE_KEY = "title_key";
            public static final String TITLE_RESOURCE_URI = "title_resource_uri";
            public static final String TRACK = "track";
            public static final String YEAR = "year";
        }

        public static final class Genres implements BaseColumns, GenresColumns {
            public static final String CONTENT_TYPE = "vnd.android.cursor.dir/genre";
            public static final String DEFAULT_SORT_ORDER = "name";
            public static final String ENTRY_CONTENT_TYPE = "vnd.android.cursor.item/genre";
            public static final Uri INTERNAL_CONTENT_URI = getContentUri("internal");
            public static final Uri EXTERNAL_CONTENT_URI = getContentUri("external");

            public static final class Members implements AudioColumns {
                public static final String AUDIO_ID = "audio_id";
                public static final String CONTENT_DIRECTORY = "members";
                public static final String DEFAULT_SORT_ORDER = "title_key";
                public static final String GENRE_ID = "genre_id";

                public static final Uri getContentUri(String str, long j) {
                    return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + str + "/audio/genres/" + j + "/members");
                }
            }

            public static Uri getContentUri(String str) {
                return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + str + "/audio/genres");
            }

            public static Uri getContentUriForAudioId(String str, int i) {
                return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + str + "/audio/media/" + i + "/genres");
            }
        }

        public interface GenresColumns {
            public static final String NAME = "name";
        }

        public static final class Media implements AudioColumns {
            public static final String CONTENT_TYPE = "vnd.android.cursor.dir/audio";
            public static final String DEFAULT_SORT_ORDER = "title_key";
            public static final String ENTRY_CONTENT_TYPE = "vnd.android.cursor.item/audio";
            public static final Uri EXTERNAL_CONTENT_URI;
            private static final String[] EXTERNAL_PATHS;
            public static final String EXTRA_MAX_BYTES = "android.provider.MediaStore.extra.MAX_BYTES";
            public static final Uri INTERNAL_CONTENT_URI;
            public static final String RECORD_SOUND_ACTION = "android.provider.MediaStore.RECORD_SOUND";

            static {
                String str = System.getenv("SECONDARY_STORAGE");
                if (str != null) {
                    EXTERNAL_PATHS = str.split(SettingsStringUtil.DELIMITER);
                } else {
                    EXTERNAL_PATHS = new String[0];
                }
                INTERNAL_CONTENT_URI = getContentUri("internal");
                EXTERNAL_CONTENT_URI = getContentUri("external");
            }

            public static Uri getContentUri(String str) {
                return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + str + "/audio/media");
            }

            public static Uri getContentUriForPath(String str) {
                for (String str2 : EXTERNAL_PATHS) {
                    if (str.startsWith(str2)) {
                        return EXTERNAL_CONTENT_URI;
                    }
                }
                return str.startsWith(Environment.getExternalStorageDirectory().getPath()) ? EXTERNAL_CONTENT_URI : INTERNAL_CONTENT_URI;
            }
        }

        public static final class Playlists implements BaseColumns, PlaylistsColumns {
            public static final String CONTENT_TYPE = "vnd.android.cursor.dir/playlist";
            public static final String DEFAULT_SORT_ORDER = "name";
            public static final String ENTRY_CONTENT_TYPE = "vnd.android.cursor.item/playlist";
            public static final Uri INTERNAL_CONTENT_URI = getContentUri("internal");
            public static final Uri EXTERNAL_CONTENT_URI = getContentUri("external");

            public static final class Members implements AudioColumns {
                public static final String AUDIO_ID = "audio_id";
                public static final String CONTENT_DIRECTORY = "members";
                public static final String DEFAULT_SORT_ORDER = "play_order";
                public static final String PLAYLIST_ID = "playlist_id";
                public static final String PLAY_ORDER = "play_order";
                public static final String _ID = "_id";

                public static final Uri getContentUri(String str, long j) {
                    return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + str + "/audio/playlists/" + j + "/members");
                }

                public static final boolean moveItem(ContentResolver contentResolver, long j, int i, int i2) {
                    Uri uriBuild = getContentUri("external", j).buildUpon().appendEncodedPath(String.valueOf(i)).appendQueryParameter("move", "true").build();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("play_order", Integer.valueOf(i2));
                    return contentResolver.update(uriBuild, contentValues, null, null) != 0;
                }
            }

            public static Uri getContentUri(String str) {
                return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + str + "/audio/playlists");
            }
        }

        public interface PlaylistsColumns {
            public static final String DATA = "_data";
            public static final String DATE_ADDED = "date_added";
            public static final String DATE_MODIFIED = "date_modified";
            public static final String NAME = "name";
        }

        public static final class Radio {
            public static final String ENTRY_CONTENT_TYPE = "vnd.android.cursor.item/radio";

            private Radio() {
            }
        }

        public static String keyFor(String str) {
            if (str == null) {
                return null;
            }
            if (str.equals(MediaStore.UNKNOWN_STRING)) {
                return "\u0001";
            }
            boolean zStartsWith = str.startsWith("\u0001");
            String lowerCase = str.trim().toLowerCase();
            if (lowerCase.startsWith("the ")) {
                lowerCase = lowerCase.substring(4);
            }
            if (lowerCase.startsWith("an ")) {
                lowerCase = lowerCase.substring(3);
            }
            if (lowerCase.startsWith("a ")) {
                lowerCase = lowerCase.substring(2);
            }
            if (lowerCase.endsWith(", the") || lowerCase.endsWith(",the") || lowerCase.endsWith(", an") || lowerCase.endsWith(",an") || lowerCase.endsWith(", a") || lowerCase.endsWith(",a")) {
                lowerCase = lowerCase.substring(0, lowerCase.lastIndexOf(44));
            }
            String strTrim = lowerCase.replaceAll("[\\[\\]\\(\\)\"'.,?!]", "").trim();
            if (strTrim.length() <= 0) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            sb.append('.');
            int length = strTrim.length();
            for (int i = 0; i < length; i++) {
                sb.append(strTrim.charAt(i));
                sb.append('.');
            }
            String collationKey = DatabaseUtils.getCollationKey(sb.toString());
            if (!zStartsWith) {
                return collationKey;
            }
            return "\u0001" + collationKey;
        }
    }

    public static final class Files {

        public interface FileColumns extends MediaColumns {
            public static final String FORMAT = "format";
            public static final String MEDIA_TYPE = "media_type";
            public static final int MEDIA_TYPE_AUDIO = 2;
            public static final int MEDIA_TYPE_IMAGE = 1;
            public static final int MEDIA_TYPE_NONE = 0;
            public static final int MEDIA_TYPE_PLAYLIST = 4;
            public static final int MEDIA_TYPE_VIDEO = 3;
            public static final String MIME_TYPE = "mime_type";
            public static final String PARENT = "parent";
            public static final String STORAGE_ID = "storage_id";
            public static final String TITLE = "title";
        }

        public static Uri getContentUri(String str) {
            return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + str + "/file");
        }

        public static final Uri getContentUri(String str, long j) {
            return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + str + "/file/" + j);
        }

        public static final Uri getDirectoryUri(String str) {
            return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + str + "/dir");
        }

        public static Uri getMtpObjectsUri(String str) {
            return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + str + "/object");
        }

        public static final Uri getMtpObjectsUri(String str, long j) {
            return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + str + "/object/" + j);
        }

        public static final Uri getMtpReferencesUri(String str, long j) {
            return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + str + "/object/" + j + "/references");
        }
    }

    public static final class Images {

        public interface ImageColumns extends MediaColumns {
            public static final String BUCKET_DISPLAY_NAME = "bucket_display_name";
            public static final String BUCKET_ID = "bucket_id";
            public static final String DATE_TAKEN = "datetaken";
            public static final String DESCRIPTION = "description";
            public static final String IS_PRIVATE = "isprivate";
            public static final String LATITUDE = "latitude";
            public static final String LONGITUDE = "longitude";
            public static final String MINI_THUMB_MAGIC = "mini_thumb_magic";
            public static final String ORIENTATION = "orientation";
            public static final String PICASA_ID = "picasa_id";
        }

        public static final class Media implements ImageColumns {
            public static final String CONTENT_TYPE = "vnd.android.cursor.dir/image";
            public static final String DEFAULT_SORT_ORDER = "bucket_display_name";
            public static final Uri INTERNAL_CONTENT_URI = getContentUri("internal");
            public static final Uri EXTERNAL_CONTENT_URI = getContentUri("external");

            private static final Bitmap StoreThumbnail(ContentResolver contentResolver, Bitmap bitmap, long j, float f, float f2, int i) {
                Matrix matrix = new Matrix();
                matrix.setScale(f / bitmap.getWidth(), f2 / bitmap.getHeight());
                Bitmap bitmapCreateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                ContentValues contentValues = new ContentValues(4);
                contentValues.put("kind", Integer.valueOf(i));
                contentValues.put("image_id", Integer.valueOf((int) j));
                contentValues.put("height", Integer.valueOf(bitmapCreateBitmap.getHeight()));
                contentValues.put("width", Integer.valueOf(bitmapCreateBitmap.getWidth()));
                try {
                    OutputStream outputStreamOpenOutputStream = contentResolver.openOutputStream(contentResolver.insert(Thumbnails.EXTERNAL_CONTENT_URI, contentValues));
                    bitmapCreateBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStreamOpenOutputStream);
                    outputStreamOpenOutputStream.close();
                    return bitmapCreateBitmap;
                } catch (FileNotFoundException unused) {
                    return null;
                } catch (IOException unused2) {
                    return null;
                }
            }

            public static final Bitmap getBitmap(ContentResolver contentResolver, Uri uri) throws Throwable {
                InputStream inputStreamOpenInputStream = contentResolver.openInputStream(uri);
                Bitmap bitmapDecodeStream = BitmapFactory.decodeStream(inputStreamOpenInputStream);
                inputStreamOpenInputStream.close();
                return bitmapDecodeStream;
            }

            public static Uri getContentUri(String str) {
                return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + str + "/images/media");
            }

            public static final String insertImage(ContentResolver contentResolver, Bitmap bitmap, String str, String str2) {
                Uri uriInsert;
                ContentValues contentValues = new ContentValues();
                contentValues.put("title", str);
                contentValues.put("description", str2);
                contentValues.put("mime_type", "image/jpeg");
                try {
                    uriInsert = contentResolver.insert(EXTERNAL_CONTENT_URI, contentValues);
                    try {
                        if (bitmap != null) {
                            OutputStream outputStreamOpenOutputStream = contentResolver.openOutputStream(uriInsert);
                            try {
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStreamOpenOutputStream);
                                outputStreamOpenOutputStream.close();
                                long id = ContentUris.parseId(uriInsert);
                                StoreThumbnail(contentResolver, Thumbnails.getThumbnail(contentResolver, id, 1, null), id, 50.0f, 50.0f, 3);
                            } catch (Throwable th) {
                                outputStreamOpenOutputStream.close();
                                throw th;
                            }
                        } else {
                            Log.e(MediaStore.TAG, "Failed to create thumbnail, removing original");
                            contentResolver.delete(uriInsert, null, null);
                            uriInsert = null;
                        }
                    } catch (Exception e) {
                        e = e;
                        Log.e(MediaStore.TAG, "Failed to insert image", e);
                        if (uriInsert != null) {
                            contentResolver.delete(uriInsert, null, null);
                            uriInsert = null;
                        }
                    }
                } catch (Exception e2) {
                    e = e2;
                    uriInsert = null;
                }
                if (uriInsert != null) {
                    return uriInsert.toString();
                }
                return null;
            }

            public static final String insertImage(ContentResolver contentResolver, String str, String str2, String str3) throws FileNotFoundException {
                FileInputStream fileInputStream = new FileInputStream(str);
                try {
                    Bitmap bitmapDecodeFile = BitmapFactory.decodeFile(str);
                    String strInsertImage = insertImage(contentResolver, bitmapDecodeFile, str2, str3);
                    bitmapDecodeFile.recycle();
                    return strInsertImage;
                } finally {
                    try {
                        fileInputStream.close();
                    } catch (IOException unused) {
                    }
                }
            }

            public static final Cursor query(ContentResolver contentResolver, Uri uri, String[] strArr) {
                return contentResolver.query(uri, strArr, null, null, "bucket_display_name");
            }

            public static final Cursor query(ContentResolver contentResolver, Uri uri, String[] strArr, String str, String str2) {
                if (str2 == null) {
                    str2 = "bucket_display_name";
                }
                return contentResolver.query(uri, strArr, str, null, str2);
            }

            public static final Cursor query(ContentResolver contentResolver, Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
                if (str2 == null) {
                    str2 = "bucket_display_name";
                }
                return contentResolver.query(uri, strArr, str, strArr2, str2);
            }
        }

        public static class Thumbnails implements BaseColumns {
            public static final String DATA = "_data";
            public static final String DEFAULT_SORT_ORDER = "image_id ASC";
            public static final int FULL_SCREEN_KIND = 2;
            public static final String HEIGHT = "height";
            public static final String IMAGE_ID = "image_id";
            public static final String KIND = "kind";
            public static final int MICRO_KIND = 3;
            public static final int MINI_KIND = 1;
            public static final String THUMB_DATA = "thumb_data";
            public static final String WIDTH = "width";
            public static final Uri INTERNAL_CONTENT_URI = getContentUri("internal");
            public static final Uri EXTERNAL_CONTENT_URI = getContentUri("external");

            public static void cancelThumbnailRequest(ContentResolver contentResolver, long j) {
                InternalThumbnails.cancelThumbnailRequest(contentResolver, j, EXTERNAL_CONTENT_URI, 0L);
            }

            public static void cancelThumbnailRequest(ContentResolver contentResolver, long j, long j2) {
                InternalThumbnails.cancelThumbnailRequest(contentResolver, j, EXTERNAL_CONTENT_URI, j2);
            }

            public static Uri getContentUri(String str) {
                return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + str + "/images/thumbnails");
            }

            public static Bitmap getThumbnail(ContentResolver contentResolver, long j, int i, BitmapFactory.Options options) {
                return InternalThumbnails.getThumbnail(contentResolver, j, 0L, i, options, EXTERNAL_CONTENT_URI, false);
            }

            public static Bitmap getThumbnail(ContentResolver contentResolver, long j, long j2, int i, BitmapFactory.Options options) {
                return InternalThumbnails.getThumbnail(contentResolver, j, j2, i, options, EXTERNAL_CONTENT_URI, false);
            }

            public static final Cursor query(ContentResolver contentResolver, Uri uri, String[] strArr) {
                return contentResolver.query(uri, strArr, null, null, DEFAULT_SORT_ORDER);
            }

            public static final Cursor queryMiniThumbnail(ContentResolver contentResolver, long j, int i, String[] strArr) {
                return contentResolver.query(EXTERNAL_CONTENT_URI, strArr, "image_id = " + j + " AND kind = " + i, null, null);
            }

            public static final Cursor queryMiniThumbnails(ContentResolver contentResolver, Uri uri, int i, String[] strArr) {
                return contentResolver.query(uri, strArr, "kind = " + i, null, DEFAULT_SORT_ORDER);
            }
        }
    }

    private static class InternalThumbnails implements BaseColumns {
        static final int DEFAULT_GROUP_ID = 0;
        private static final int FULL_SCREEN_KIND = 2;
        private static final int MICRO_KIND = 3;
        private static final int MINI_KIND = 1;
        private static byte[] sThumbBuf;
        private static final String[] PROJECTION = {"_id", "_data"};
        private static final Object sThumbBufLock = new Object();

        private InternalThumbnails() {
        }

        static void cancelThumbnailRequest(ContentResolver contentResolver, long j, Uri uri, long j2) {
            Cursor cursorQuery = contentResolver.query(uri.buildUpon().appendQueryParameter("cancel", "1").appendQueryParameter("orig_id", String.valueOf(j)).appendQueryParameter(Contacts.GroupMembership.GROUP_ID, String.valueOf(j2)).build(), PROJECTION, null, null, null);
            if (cursorQuery != null) {
                cursorQuery.close();
            }
        }

        private static Bitmap getMiniThumbFromFile(Cursor cursor, Uri uri, ContentResolver contentResolver, BitmapFactory.Options options) {
            Uri uriWithAppendedId;
            Bitmap bitmapDecodeFileDescriptor;
            try {
                long j = cursor.getLong(0);
                cursor.getString(1);
                uriWithAppendedId = ContentUris.withAppendedId(uri, j);
                try {
                    ParcelFileDescriptor parcelFileDescriptorOpenFileDescriptor = contentResolver.openFileDescriptor(uriWithAppendedId, FullBackup.ROOT_TREE_TOKEN);
                    bitmapDecodeFileDescriptor = BitmapFactory.decodeFileDescriptor(parcelFileDescriptorOpenFileDescriptor.getFileDescriptor(), null, options);
                    try {
                        parcelFileDescriptorOpenFileDescriptor.close();
                    } catch (FileNotFoundException e) {
                        e = e;
                        Log.e(MediaStore.TAG, "couldn't open thumbnail " + uriWithAppendedId + "; " + e);
                    } catch (IOException e2) {
                        e = e2;
                        Log.e(MediaStore.TAG, "couldn't open thumbnail " + uriWithAppendedId + "; " + e);
                    } catch (OutOfMemoryError e3) {
                        e = e3;
                        Log.e(MediaStore.TAG, "failed to allocate memory for thumbnail " + uriWithAppendedId + "; " + e);
                    }
                } catch (FileNotFoundException e4) {
                    e = e4;
                    bitmapDecodeFileDescriptor = null;
                } catch (IOException e5) {
                    e = e5;
                    bitmapDecodeFileDescriptor = null;
                } catch (OutOfMemoryError e6) {
                    e = e6;
                    bitmapDecodeFileDescriptor = null;
                }
            } catch (FileNotFoundException e7) {
                e = e7;
                uriWithAppendedId = null;
                bitmapDecodeFileDescriptor = null;
            } catch (IOException e8) {
                e = e8;
                uriWithAppendedId = null;
                bitmapDecodeFileDescriptor = null;
            } catch (OutOfMemoryError e9) {
                e = e9;
                uriWithAppendedId = null;
                bitmapDecodeFileDescriptor = null;
            }
            return bitmapDecodeFileDescriptor;
        }

        /* JADX WARN: Bottom block not found for handler: all -> 0x013e */
        /* JADX WARN: Code duplicated, block: B:101:0x019e A[Catch: all -> 0x0140, SQLiteException -> 0x0144, TRY_ENTER, TRY_LEAVE, TryCatch #0 {all -> 0x0140, blocks: (B:64:0x00ff, B:65:0x0101, B:83:0x0139, B:99:0x0159, B:101:0x019e, B:102:0x01a1, B:94:0x014b, B:96:0x0151), top: B:153:0x00fd }] */
        /* JADX WARN: Code duplicated, block: B:108:0x01b6  */
        /* JADX WARN: Code duplicated, block: B:109:0x01b7 A[Catch: all -> 0x00aa, SQLiteException -> 0x01c9, TryCatch #6 {all -> 0x00aa, blocks: (B:38:0x0090, B:40:0x0096, B:106:0x01b0, B:109:0x01b7, B:112:0x01bf, B:113:0x01c4), top: B:160:0x0090 }] */
        /* JADX WARN: Code duplicated, block: B:111:0x01bd A[DONT_INVERT] */
        /* JADX WARN: Code duplicated, block: B:112:0x01bf A[Catch: all -> 0x00aa, SQLiteException -> 0x01c9, TryCatch #6 {all -> 0x00aa, blocks: (B:38:0x0090, B:40:0x0096, B:106:0x01b0, B:109:0x01b7, B:112:0x01bf, B:113:0x01c4), top: B:160:0x0090 }] */
        /* JADX WARN: Code duplicated, block: B:113:0x01c4 A[Catch: all -> 0x00aa, SQLiteException -> 0x01c9, TRY_LEAVE, TryCatch #6 {all -> 0x00aa, blocks: (B:38:0x0090, B:40:0x0096, B:106:0x01b0, B:109:0x01b7, B:112:0x01bf, B:113:0x01c4), top: B:160:0x0090 }] */
        /* JADX WARN: Code duplicated, block: B:118:0x01ce  */
        /* JADX WARN: Code duplicated, block: B:121:0x01d5  */
        /* JADX WARN: Code duplicated, block: B:124:0x01db  */
        /* JADX WARN: Code duplicated, block: B:129:0x01e6  */
        /* JADX WARN: Code duplicated, block: B:146:0x0217  */
        /* JADX WARN: Code duplicated, block: B:150:0x0222  */
        /* JADX WARN: Code duplicated, block: B:153:0x00fd A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Code duplicated, block: B:158:0x014b A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Code duplicated, block: B:162:0x0125 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Code duplicated, block: B:164:0x01b0 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Code duplicated, block: B:167:0x0102 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Code duplicated, block: B:52:0x00b4  */
        /* JADX WARN: Code duplicated, block: B:53:0x00b9  */
        /* JADX WARN: Code duplicated, block: B:56:0x00e4 A[Catch: all -> 0x0202, SQLiteException -> 0x0206, TryCatch #19 {SQLiteException -> 0x0206, all -> 0x0202, blocks: (B:54:0x00be, B:56:0x00e4, B:57:0x00e7), top: B:175:0x00be }] */
        /* JADX WARN: Code duplicated, block: B:59:0x00f4 A[DONT_INVERT] */
        /* JADX WARN: Code duplicated, block: B:60:0x00f6  */
        /* JADX WARN: Code duplicated, block: B:64:0x00ff A[Catch: all -> 0x0140, SQLiteException -> 0x0144, TRY_ENTER, TryCatch #0 {all -> 0x0140, blocks: (B:64:0x00ff, B:65:0x0101, B:83:0x0139, B:99:0x0159, B:101:0x019e, B:102:0x01a1, B:94:0x014b, B:96:0x0151), top: B:153:0x00fd }] */
        /* JADX WARN: Code duplicated, block: B:68:0x0106 A[Catch: all -> 0x0135, TryCatch #11 {all -> 0x0135, blocks: (B:66:0x0102, B:68:0x0106, B:69:0x010c, B:71:0x011a, B:78:0x0132), top: B:167:0x0102 }] */
        /* JADX WARN: Code duplicated, block: B:71:0x011a A[Catch: all -> 0x0135, TRY_LEAVE, TryCatch #11 {all -> 0x0135, blocks: (B:66:0x0102, B:68:0x0106, B:69:0x010c, B:71:0x011a, B:78:0x0132), top: B:167:0x0102 }] */
        /* JADX WARN: Code duplicated, block: B:92:0x0148  */
        /* JADX WARN: Code duplicated, block: B:96:0x0151 A[Catch: all -> 0x0140, SQLiteException -> 0x0144, TRY_ENTER, TRY_LEAVE, TryCatch #0 {all -> 0x0140, blocks: (B:64:0x00ff, B:65:0x0101, B:83:0x0139, B:99:0x0159, B:101:0x019e, B:102:0x01a1, B:94:0x014b, B:96:0x0151), top: B:153:0x00fd }] */
        /* JADX WARN: Code duplicated, block: B:99:0x0159 A[Catch: all -> 0x0140, SQLiteException -> 0x01e2, TRY_ENTER, TRY_LEAVE, TryCatch #0 {all -> 0x0140, blocks: (B:64:0x00ff, B:65:0x0101, B:83:0x0139, B:99:0x0159, B:101:0x019e, B:102:0x01a1, B:94:0x014b, B:96:0x0151), top: B:153:0x00fd }] */
        /* JADX WARN: Instruction removed from duplicated block: B:99:0x0159, please report this as an issue */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        static Bitmap getThumbnail(ContentResolver contentResolver, long j, long j2, int i, BitmapFactory.Options options, Uri uri, boolean z) throws Throwable {
            Bitmap miniThumbFromFile;
            Cursor cursor;
            int i2;
            Cursor cursor2;
            Cursor cursorQuery;
            Cursor cursorQuery2;
            int i3;
            Bitmap bitmapCreateImageThumbnail;
            String string;
            Bitmap bitmap;
            Bitmap bitmapDecodeByteArray;
            Bitmap bitmapDecodeByteArray2;
            MiniThumbFile miniThumbFileInstance = MiniThumbFile.instance(z ? Video.Media.EXTERNAL_CONTENT_URI : Images.Media.EXTERNAL_CONTENT_URI);
            Cursor cursor3 = null;
            try {
                try {
                    if (miniThumbFileInstance.getMagic(j) == 0) {
                        i2 = 3;
                        cursor2 = null;
                        miniThumbFromFile = null;
                        try {
                            Uri uriBuild = uri.buildUpon().appendQueryParameter("blocking", "1").appendQueryParameter("orig_id", String.valueOf(j)).appendQueryParameter(Contacts.GroupMembership.GROUP_ID, String.valueOf(j2)).build();
                            if (cursor2 != null) {
                                cursor2.close();
                            }
                            cursorQuery2 = contentResolver.query(uriBuild, PROJECTION, null, null, null);
                            if (cursorQuery2 == null) {
                                if (cursorQuery2 != null) {
                                    cursorQuery2.close();
                                }
                                miniThumbFileInstance.deactivate();
                                return null;
                            }
                            try {
                                try {
                                    try {
                                        if (i == i2) {
                                            synchronized (sThumbBufLock) {
                                                try {
                                                    if (sThumbBuf == null) {
                                                        sThumbBuf = new byte[10000];
                                                    }
                                                    Arrays.fill(sThumbBuf, (byte) 0);
                                                    if (miniThumbFileInstance.getMiniThumbFromFile(j, sThumbBuf) != null) {
                                                        bitmapDecodeByteArray = BitmapFactory.decodeByteArray(sThumbBuf, 0, sThumbBuf.length);
                                                        if (bitmapDecodeByteArray == null) {
                                                            try {
                                                                Log.w(MediaStore.TAG, "couldn't decode byte array.");
                                                            } catch (Throwable th) {
                                                                th = th;
                                                                bitmap = bitmapDecodeByteArray;
                                                                while (true) {
                                                                    break;
                                                                }
                                                                throw th;
                                                            }
                                                        }
                                                        miniThumbFromFile = bitmapDecodeByteArray;
                                                    }
                                                } catch (Throwable th2) {
                                                    th = th2;
                                                    bitmap = miniThumbFromFile;
                                                }
                                            }
                                            try {
                                                break;
                                                throw th;
                                            } catch (SQLiteException e) {
                                                e = e;
                                                miniThumbFromFile = bitmap;
                                                cursor3 = cursorQuery2;
                                                Log.w(MediaStore.TAG, e);
                                                if (cursor3 != null) {
                                                    cursor3.close();
                                                }
                                                miniThumbFileInstance.deactivate();
                                                return miniThumbFromFile;
                                            }
                                        }
                                        i3 = 1;
                                        if (i == 1) {
                                            throw new IllegalArgumentException("Unsupported kind: " + i);
                                        }
                                        try {
                                            if (cursorQuery2.moveToFirst()) {
                                                miniThumbFromFile = getMiniThumbFromFile(cursorQuery2, uri, contentResolver, options);
                                            }
                                        } catch (SQLiteException e2) {
                                            e = e2;
                                            cursor3 = cursorQuery2;
                                            Log.w(MediaStore.TAG, e);
                                            if (cursor3 != null) {
                                                cursor3.close();
                                            }
                                            miniThumbFileInstance.deactivate();
                                            return miniThumbFromFile;
                                        }
                                        if (miniThumbFromFile == null) {
                                            Log.v(MediaStore.TAG, "Create the thumbnail in memory: origId=" + j + ", kind=" + i + ", isVideo=" + z);
                                            Uri uri2 = Uri.parse(uri.buildUpon().appendPath(String.valueOf(j)).toString().replaceFirst("thumbnails", MediaStore.AUTHORITY));
                                            if (cursorQuery2 != null) {
                                                cursorQuery2.close();
                                            }
                                            cursorQuery = contentResolver.query(uri2, PROJECTION, null, null, null);
                                            if (cursorQuery != null) {
                                                try {
                                                    if (!cursorQuery.moveToFirst()) {
                                                        string = cursorQuery.getString(i3);
                                                        if (string != null) {
                                                            if (z) {
                                                                bitmapCreateImageThumbnail = ThumbnailUtils.createVideoThumbnail(string, i);
                                                            } else {
                                                                bitmapCreateImageThumbnail = ThumbnailUtils.createImageThumbnail(string, i);
                                                            }
                                                        }
                                                        if (cursorQuery != null) {
                                                            cursorQuery.close();
                                                        }
                                                        miniThumbFileInstance.deactivate();
                                                        return bitmapCreateImageThumbnail;
                                                    }
                                                } catch (SQLiteException e3) {
                                                    e = e3;
                                                    cursor3 = cursorQuery;
                                                    Log.w(MediaStore.TAG, e);
                                                    if (cursor3 != null) {
                                                        cursor3.close();
                                                    }
                                                    miniThumbFileInstance.deactivate();
                                                    return miniThumbFromFile;
                                                }
                                            }
                                            if (cursorQuery != null) {
                                                cursorQuery.close();
                                            }
                                            miniThumbFileInstance.deactivate();
                                            return null;
                                        }
                                        cursorQuery = cursorQuery2;
                                        bitmapCreateImageThumbnail = miniThumbFromFile;
                                        if (cursorQuery != null) {
                                            cursorQuery.close();
                                        }
                                        miniThumbFileInstance.deactivate();
                                        return bitmapCreateImageThumbnail;
                                    } catch (Throwable th3) {
                                        th = th3;
                                        cursor = cursorQuery2;
                                        if (cursor != null) {
                                            cursor.close();
                                        }
                                        miniThumbFileInstance.deactivate();
                                        throw th;
                                    }
                                } catch (SQLiteException e4) {
                                    e = e4;
                                }
                            } catch (SQLiteException e5) {
                                e = e5;
                            } catch (Throwable th4) {
                                th = th4;
                                if (cursor != null) {
                                    cursor.close();
                                }
                                miniThumbFileInstance.deactivate();
                                throw th;
                            }
                        } catch (SQLiteException e6) {
                            e = e6;
                            cursor3 = cursor2;
                        } catch (Throwable th5) {
                            th = th5;
                            cursor = cursor2;
                        }
                    } else {
                        if (i != 3) {
                            if (i == 1) {
                                String str = z ? "video_id=" : "image_id=";
                                i2 = 3;
                                cursorQuery = contentResolver.query(uri, PROJECTION, str + j, null, null);
                                if (cursorQuery != null) {
                                    try {
                                        try {
                                            if (cursorQuery.moveToFirst()) {
                                                Bitmap miniThumbFromFile2 = getMiniThumbFromFile(cursorQuery, uri, contentResolver, options);
                                                if (miniThumbFromFile2 != null) {
                                                    if (cursorQuery != null) {
                                                        cursorQuery.close();
                                                    }
                                                    miniThumbFileInstance.deactivate();
                                                    return miniThumbFromFile2;
                                                }
                                                cursor2 = cursorQuery;
                                                miniThumbFromFile = miniThumbFromFile2;
                                            } else {
                                                cursor2 = cursorQuery;
                                                miniThumbFromFile = null;
                                            }
                                        } catch (Throwable th6) {
                                            th = th6;
                                            cursor = cursorQuery;
                                            if (cursor != null) {
                                                cursor.close();
                                            }
                                            miniThumbFileInstance.deactivate();
                                            throw th;
                                        }
                                    } catch (SQLiteException e7) {
                                        e = e7;
                                        miniThumbFromFile = null;
                                        cursor3 = cursorQuery;
                                        Log.w(MediaStore.TAG, e);
                                        if (cursor3 != null) {
                                            cursor3.close();
                                        }
                                        miniThumbFileInstance.deactivate();
                                        return miniThumbFromFile;
                                    }
                                } else {
                                    cursor2 = cursorQuery;
                                    miniThumbFromFile = null;
                                }
                            } else {
                                i2 = 3;
                                cursor2 = null;
                                miniThumbFromFile = null;
                            }
                            Uri uriBuild2 = uri.buildUpon().appendQueryParameter("blocking", "1").appendQueryParameter("orig_id", String.valueOf(j)).appendQueryParameter(Contacts.GroupMembership.GROUP_ID, String.valueOf(j2)).build();
                            if (cursor2 != null) {
                                cursor2.close();
                            }
                            cursorQuery2 = contentResolver.query(uriBuild2, PROJECTION, null, null, null);
                            if (cursorQuery2 == null) {
                                if (cursorQuery2 != null) {
                                    cursorQuery2.close();
                                }
                                miniThumbFileInstance.deactivate();
                                return null;
                            }
                            if (i == i2) {
                                synchronized (sThumbBufLock) {
                                    if (sThumbBuf == null) {
                                        sThumbBuf = new byte[10000];
                                    }
                                    Arrays.fill(sThumbBuf, (byte) 0);
                                    if (miniThumbFileInstance.getMiniThumbFromFile(j, sThumbBuf) != null) {
                                        bitmapDecodeByteArray = BitmapFactory.decodeByteArray(sThumbBuf, 0, sThumbBuf.length);
                                        if (bitmapDecodeByteArray == null) {
                                            Log.w(MediaStore.TAG, "couldn't decode byte array.");
                                        }
                                        miniThumbFromFile = bitmapDecodeByteArray;
                                    }
                                    i3 = 1;
                                }
                            } else {
                                i3 = 1;
                                if (i == 1) {
                                    throw new IllegalArgumentException("Unsupported kind: " + i);
                                }
                                if (cursorQuery2.moveToFirst()) {
                                    miniThumbFromFile = getMiniThumbFromFile(cursorQuery2, uri, contentResolver, options);
                                }
                            }
                            if (miniThumbFromFile == null) {
                                Log.v(MediaStore.TAG, "Create the thumbnail in memory: origId=" + j + ", kind=" + i + ", isVideo=" + z);
                                Uri uri3 = Uri.parse(uri.buildUpon().appendPath(String.valueOf(j)).toString().replaceFirst("thumbnails", MediaStore.AUTHORITY));
                                if (cursorQuery2 != null) {
                                    cursorQuery2.close();
                                }
                                cursorQuery = contentResolver.query(uri3, PROJECTION, null, null, null);
                                if (cursorQuery != null) {
                                    if (!cursorQuery.moveToFirst()) {
                                        string = cursorQuery.getString(i3);
                                        if (string != null) {
                                            if (z) {
                                                bitmapCreateImageThumbnail = ThumbnailUtils.createVideoThumbnail(string, i);
                                            } else {
                                                bitmapCreateImageThumbnail = ThumbnailUtils.createImageThumbnail(string, i);
                                            }
                                        }
                                        if (cursorQuery != null) {
                                            cursorQuery.close();
                                        }
                                        miniThumbFileInstance.deactivate();
                                        return bitmapCreateImageThumbnail;
                                    }
                                }
                                if (cursorQuery != null) {
                                    cursorQuery.close();
                                }
                                miniThumbFileInstance.deactivate();
                                return null;
                            }
                            cursorQuery = cursorQuery2;
                            bitmapCreateImageThumbnail = miniThumbFromFile;
                            if (cursorQuery != null) {
                                cursorQuery.close();
                            }
                            miniThumbFileInstance.deactivate();
                            return bitmapCreateImageThumbnail;
                        }
                        try {
                            synchronized (sThumbBufLock) {
                                try {
                                    if (sThumbBuf == null) {
                                        sThumbBuf = new byte[10000];
                                    }
                                    if (miniThumbFileInstance.getMiniThumbFromFile(j, sThumbBuf) != null) {
                                        bitmapDecodeByteArray2 = BitmapFactory.decodeByteArray(sThumbBuf, 0, sThumbBuf.length);
                                        if (bitmapDecodeByteArray2 == null) {
                                            Log.w(MediaStore.TAG, "couldn't decode byte array.");
                                        }
                                    } else {
                                        bitmapDecodeByteArray2 = null;
                                    }
                                    miniThumbFileInstance.deactivate();
                                    return bitmapDecodeByteArray2;
                                } catch (Throwable th7) {
                                    th = th7;
                                }
                            }
                        } catch (Throwable th8) {
                            th = th8;
                        }
                        try {
                            throw th;
                        } catch (SQLiteException e8) {
                            e = e8;
                            miniThumbFromFile = null;
                        }
                    }
                } catch (Throwable th9) {
                    th = th9;
                    cursor = null;
                }
            } catch (SQLiteException e9) {
                e = e9;
                miniThumbFromFile = null;
            }
            Log.w(MediaStore.TAG, e);
            if (cursor3 != null) {
                cursor3.close();
            }
            miniThumbFileInstance.deactivate();
            return miniThumbFromFile;
        }
    }

    public interface MediaColumns extends BaseColumns {
        public static final String DATA = "_data";
        public static final String DATE_ADDED = "date_added";
        public static final String DATE_MODIFIED = "date_modified";
        public static final String DISPLAY_NAME = "_display_name";
        public static final String HEIGHT = "height";
        public static final String IS_DRM = "is_drm";
        public static final String MEDIA_SCANNER_NEW_OBJECT_ID = "media_scanner_new_object_id";
        public static final String MIME_TYPE = "mime_type";
        public static final String SIZE = "_size";
        public static final String TITLE = "title";
        public static final String WIDTH = "width";
    }

    public static final class Video {
        public static final String DEFAULT_SORT_ORDER = "_display_name";

        public static final class Media implements VideoColumns {
            public static final String CONTENT_TYPE = "vnd.android.cursor.dir/video";
            public static final String DEFAULT_SORT_ORDER = "title";
            public static final Uri INTERNAL_CONTENT_URI = getContentUri("internal");
            public static final Uri EXTERNAL_CONTENT_URI = getContentUri("external");

            public static Uri getContentUri(String str) {
                return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + str + "/video/media");
            }
        }

        public static class Thumbnails implements BaseColumns {
            public static final String DATA = "_data";
            public static final String DEFAULT_SORT_ORDER = "video_id ASC";
            public static final int FULL_SCREEN_KIND = 2;
            public static final String HEIGHT = "height";
            public static final String KIND = "kind";
            public static final int MICRO_KIND = 3;
            public static final int MINI_KIND = 1;
            public static final String VIDEO_ID = "video_id";
            public static final String WIDTH = "width";
            public static final Uri INTERNAL_CONTENT_URI = getContentUri("internal");
            public static final Uri EXTERNAL_CONTENT_URI = getContentUri("external");

            public static void cancelThumbnailRequest(ContentResolver contentResolver, long j) {
                InternalThumbnails.cancelThumbnailRequest(contentResolver, j, EXTERNAL_CONTENT_URI, 0L);
            }

            public static void cancelThumbnailRequest(ContentResolver contentResolver, long j, long j2) {
                InternalThumbnails.cancelThumbnailRequest(contentResolver, j, EXTERNAL_CONTENT_URI, j2);
            }

            public static Uri getContentUri(String str) {
                return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + str + "/video/thumbnails");
            }

            public static Bitmap getThumbnail(ContentResolver contentResolver, long j, int i, BitmapFactory.Options options) {
                return InternalThumbnails.getThumbnail(contentResolver, j, 0L, i, options, EXTERNAL_CONTENT_URI, true);
            }

            public static Bitmap getThumbnail(ContentResolver contentResolver, long j, long j2, int i, BitmapFactory.Options options) {
                return InternalThumbnails.getThumbnail(contentResolver, j, j2, i, options, EXTERNAL_CONTENT_URI, true);
            }
        }

        public interface VideoColumns extends MediaColumns {
            public static final String ALBUM = "album";
            public static final String ARTIST = "artist";
            public static final String BOOKMARK = "bookmark";
            public static final String BUCKET_DISPLAY_NAME = "bucket_display_name";
            public static final String BUCKET_ID = "bucket_id";
            public static final String CATEGORY = "category";
            public static final String DATE_TAKEN = "datetaken";
            public static final String DESCRIPTION = "description";
            public static final String DURATION = "duration";
            public static final String IS_PRIVATE = "isprivate";
            public static final String LANGUAGE = "language";
            public static final String LATITUDE = "latitude";
            public static final String LONGITUDE = "longitude";
            public static final String MINI_THUMB_MAGIC = "mini_thumb_magic";
            public static final String RESOLUTION = "resolution";
            public static final String TAGS = "tags";
        }

        public static final Cursor query(ContentResolver contentResolver, Uri uri, String[] strArr) {
            return contentResolver.query(uri, strArr, null, null, "_display_name");
        }
    }

    private static /* synthetic */ void $closeResource(Throwable th, AutoCloseable autoCloseable) throws Exception {
        if (th == null) {
            autoCloseable.close();
            return;
        }
        try {
            autoCloseable.close();
        } catch (Throwable th2) {
            th.addSuppressed(th2);
        }
    }

    private static Uri getDocumentUri(ContentResolver contentResolver, String str, List<UriPermission> list) throws Exception {
        ContentProviderClient contentProviderClientAcquireUnstableContentProviderClient = contentResolver.acquireUnstableContentProviderClient(DocumentsContract.EXTERNAL_STORAGE_PROVIDER_AUTHORITY);
        try {
            Bundle bundle = new Bundle();
            bundle.putParcelableList("com.android.externalstorage.documents.extra.uriPermissions", list);
            Uri uri = (Uri) contentProviderClientAcquireUnstableContentProviderClient.call("getDocumentId", str, bundle).getParcelable("uri");
            if (contentProviderClientAcquireUnstableContentProviderClient != null) {
                $closeResource(null, contentProviderClientAcquireUnstableContentProviderClient);
            }
            return uri;
        } catch (Throwable th) {
            if (contentProviderClientAcquireUnstableContentProviderClient != null) {
                $closeResource(null, contentProviderClientAcquireUnstableContentProviderClient);
            }
            throw th;
        }
    }

    public static Uri getDocumentUri(Context context, Uri uri) {
        try {
            ContentResolver contentResolver = context.getContentResolver();
            return getDocumentUri(contentResolver, getFilePath(contentResolver, uri), contentResolver.getPersistedUriPermissions());
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    private static String getFilePath(ContentResolver contentResolver, Uri uri) throws Exception {
        ContentProviderClient contentProviderClientAcquireUnstableContentProviderClient = contentResolver.acquireUnstableContentProviderClient(AUTHORITY);
        try {
            Cursor cursorQuery = contentProviderClientAcquireUnstableContentProviderClient.query(uri, new String[]{"_data"}, null, null, null);
            try {
                if (cursorQuery.getCount() == 0) {
                    throw new IllegalStateException("Not found media file under URI: " + uri);
                }
                if (!cursorQuery.moveToFirst()) {
                    throw new IllegalStateException("Failed to move cursor to the first item.");
                }
                String string = cursorQuery.getString(0);
                IoUtils.closeQuietly(cursorQuery);
                if (contentProviderClientAcquireUnstableContentProviderClient != null) {
                    $closeResource(null, contentProviderClientAcquireUnstableContentProviderClient);
                }
                return string;
            } catch (Throwable th) {
                IoUtils.closeQuietly(cursorQuery);
                throw th;
            }
        } catch (Throwable th2) {
            if (contentProviderClientAcquireUnstableContentProviderClient != null) {
                $closeResource(null, contentProviderClientAcquireUnstableContentProviderClient);
            }
            throw th2;
        }
    }

    public static Uri getMediaScannerUri() {
        return Uri.parse("content://media/none/media_scanner");
    }

    public static String getVersion(Context context) {
        Cursor cursorQuery = context.getContentResolver().query(Uri.parse("content://media/none/version"), null, null, null, null);
        if (cursorQuery == null) {
            return null;
        }
        try {
            if (cursorQuery.moveToFirst()) {
                return cursorQuery.getString(0);
            }
            return null;
        } finally {
            cursorQuery.close();
        }
    }
}
