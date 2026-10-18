package android.mtp;

import android.content.ContentProviderClient;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
class MtpPropertyGroup {
    private static final String PATH_WHERE = "_data=?";
    private static final String TAG = "MtpPropertyGroup";
    private String[] mColumns;
    private final Property[] mProperties;
    private final ContentProviderClient mProvider;
    private final Uri mUri;
    private final String mVolumeName;

    private class Property {
        int code;
        int column;
        int type;

        Property(int i, int i2, int i3) {
            this.code = i;
            this.type = i2;
            this.column = i3;
        }
    }

    public MtpPropertyGroup(ContentProviderClient contentProviderClient, String str, int[] iArr) {
        this.mProvider = contentProviderClient;
        this.mVolumeName = str;
        this.mUri = MediaStore.Files.getMtpObjectsUri(str);
        int length = iArr.length;
        ArrayList<String> arrayList = new ArrayList<>(length);
        arrayList.add("_id");
        this.mProperties = new Property[length];
        for (int i = 0; i < length; i++) {
            this.mProperties[i] = createProperty(iArr[i], arrayList);
        }
        int size = arrayList.size();
        this.mColumns = new String[size];
        for (int i2 = 0; i2 < size; i2++) {
            this.mColumns[i2] = arrayList.get(i2);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private Property createProperty(int i, ArrayList<String> arrayList) {
        int i2 = 4;
        int i3 = 65535;
        String str = null;
        switch (i) {
            case MtpConstants.PROPERTY_STORAGE_ID /* 56321 */:
            case MtpConstants.PROPERTY_PARENT_OBJECT /* 56331 */:
            case MtpConstants.PROPERTY_SAMPLE_RATE /* 56979 */:
            case MtpConstants.PROPERTY_AUDIO_WAVE_CODEC /* 56985 */:
            case MtpConstants.PROPERTY_AUDIO_BITRATE /* 56986 */:
                i3 = 6;
                break;
            case MtpConstants.PROPERTY_OBJECT_FORMAT /* 56322 */:
            case MtpConstants.PROPERTY_PROTECTION_STATUS /* 56323 */:
            case MtpConstants.PROPERTY_BITRATE_TYPE /* 56978 */:
            case MtpConstants.PROPERTY_NUMBER_OF_CHANNELS /* 56980 */:
                i3 = i2;
                break;
            case MtpConstants.PROPERTY_OBJECT_SIZE /* 56324 */:
                i2 = 8;
                i3 = i2;
                break;
            case MtpConstants.PROPERTY_OBJECT_FILE_NAME /* 56327 */:
            case MtpConstants.PROPERTY_DATE_MODIFIED /* 56329 */:
            case MtpConstants.PROPERTY_NAME /* 56388 */:
            case MtpConstants.PROPERTY_ARTIST /* 56390 */:
            case MtpConstants.PROPERTY_DATE_ADDED /* 56398 */:
            case MtpConstants.PROPERTY_GENRE /* 56460 */:
            case MtpConstants.PROPERTY_ALBUM_NAME /* 56474 */:
            case MtpConstants.PROPERTY_DISPLAY_NAME /* 56544 */:
                break;
            case MtpConstants.PROPERTY_PERSISTENT_UID /* 56385 */:
                i2 = 10;
                i3 = i2;
                break;
            case MtpConstants.PROPERTY_DESCRIPTION /* 56392 */:
                str = "description";
                break;
            case MtpConstants.PROPERTY_DURATION /* 56457 */:
                str = "duration";
                i3 = 6;
                break;
            case MtpConstants.PROPERTY_TRACK /* 56459 */:
                str = MediaStore.Audio.AudioColumns.TRACK;
                i3 = i2;
                break;
            case MtpConstants.PROPERTY_COMPOSER /* 56470 */:
                str = MediaStore.Audio.AudioColumns.COMPOSER;
                break;
            case MtpConstants.PROPERTY_ORIGINAL_RELEASE_DATE /* 56473 */:
                str = MediaStore.Audio.AudioColumns.YEAR;
                break;
            case MtpConstants.PROPERTY_ALBUM_ARTIST /* 56475 */:
                str = MediaStore.Audio.AudioColumns.ALBUM_ARTIST;
                break;
            default:
                i2 = 0;
                Log.e(TAG, "unsupported property " + i);
                i3 = i2;
                break;
        }
        if (str == null) {
            return new Property(i, i3, -1);
        }
        arrayList.add(str);
        return new Property(i, i3, arrayList.size() - 1);
    }

    private native String format_date_time(long j);

    private String queryAudio(String str, String str2) throws Throwable {
        Cursor cursor = null;
        try {
            Cursor cursorQuery = this.mProvider.query(MediaStore.Audio.Media.getContentUri(this.mVolumeName), new String[]{str2}, PATH_WHERE, new String[]{str}, null, null);
            if (cursorQuery != null) {
                try {
                    if (cursorQuery.moveToNext()) {
                        String string = cursorQuery.getString(0);
                        if (cursorQuery != null) {
                            cursorQuery.close();
                        }
                        return string;
                    }
                } catch (Exception unused) {
                    cursor = cursorQuery;
                    if (cursor != null) {
                        cursor.close();
                    }
                    return "";
                } catch (Throwable th) {
                    th = th;
                    cursor = cursorQuery;
                    if (cursor != null) {
                        cursor.close();
                    }
                    throw th;
                }
            }
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            return "";
        } catch (Exception unused2) {
        } catch (Throwable th2) {
            th = th2;
        }
    }

    private String queryGenre(String str) throws Throwable {
        Cursor cursor = null;
        try {
            Cursor cursorQuery = this.mProvider.query(MediaStore.Audio.Genres.getContentUri(this.mVolumeName), new String[]{"name"}, PATH_WHERE, new String[]{str}, null, null);
            if (cursorQuery != null) {
                try {
                    if (cursorQuery.moveToNext()) {
                        String string = cursorQuery.getString(0);
                        if (cursorQuery != null) {
                            cursorQuery.close();
                        }
                        return string;
                    }
                } catch (Exception unused) {
                    cursor = cursorQuery;
                    if (cursor != null) {
                        cursor.close();
                    }
                    return "";
                } catch (Throwable th) {
                    th = th;
                    cursor = cursorQuery;
                    if (cursor != null) {
                        cursor.close();
                    }
                    throw th;
                }
            }
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            return "";
        } catch (Exception unused2) {
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public int getPropertyList(MtpStorageManager.MtpObject mtpObject, MtpPropertyList mtpPropertyList) throws Throwable {
        Cursor cursor;
        int id = mtpObject.getId();
        String string = mtpObject.getPath().toString();
        Property[] propertyArr = this.mProperties;
        int length = propertyArr.length;
        Cursor cursor2 = null;
        int i = 0;
        while (i < length) {
            Property property = propertyArr[i];
            if (property.column == -1 || cursor2 != null) {
                cursor = cursor2;
            } else {
                try {
                    Cursor cursorQuery = this.mProvider.query(this.mUri, this.mColumns, PATH_WHERE, new String[]{string}, null, null);
                    if (cursorQuery != null) {
                        try {
                            if (!cursorQuery.moveToNext()) {
                                cursorQuery.close();
                                cursorQuery = null;
                            }
                        } catch (RemoteException unused) {
                            cursor2 = cursorQuery;
                            Log.e(TAG, "Mediaprovider lookup failed");
                            cursor = cursor2;
                        }
                    }
                    cursor = cursorQuery;
                } catch (RemoteException unused2) {
                }
            }
            switch (property.code) {
                case MtpConstants.PROPERTY_STORAGE_ID /* 56321 */:
                    mtpPropertyList.append(id, property.code, property.type, mtpObject.getStorageId());
                    break;
                case MtpConstants.PROPERTY_OBJECT_FORMAT /* 56322 */:
                    mtpPropertyList.append(id, property.code, property.type, mtpObject.getFormat());
                    break;
                case MtpConstants.PROPERTY_PROTECTION_STATUS /* 56323 */:
                    mtpPropertyList.append(id, property.code, property.type, 0L);
                    break;
                case MtpConstants.PROPERTY_OBJECT_SIZE /* 56324 */:
                    mtpPropertyList.append(id, property.code, property.type, mtpObject.getSize());
                    break;
                case MtpConstants.PROPERTY_OBJECT_FILE_NAME /* 56327 */:
                case MtpConstants.PROPERTY_NAME /* 56388 */:
                case MtpConstants.PROPERTY_DISPLAY_NAME /* 56544 */:
                    mtpPropertyList.append(id, property.code, mtpObject.getName());
                    break;
                case MtpConstants.PROPERTY_DATE_MODIFIED /* 56329 */:
                case MtpConstants.PROPERTY_DATE_ADDED /* 56398 */:
                    mtpPropertyList.append(id, property.code, format_date_time(mtpObject.getModifiedTime()));
                    break;
                case MtpConstants.PROPERTY_PARENT_OBJECT /* 56331 */:
                    mtpPropertyList.append(id, property.code, property.type, mtpObject.getParent().isRoot() ? 0L : mtpObject.getParent().getId());
                    break;
                case MtpConstants.PROPERTY_PERSISTENT_UID /* 56385 */:
                    mtpPropertyList.append(id, property.code, property.type, mtpObject.getModifiedTime() + ((long) (mtpObject.getPath().toString().hashCode() << 32)));
                    break;
                case MtpConstants.PROPERTY_ARTIST /* 56390 */:
                    mtpPropertyList.append(id, property.code, queryAudio(string, "artist"));
                    break;
                case MtpConstants.PROPERTY_TRACK /* 56459 */:
                    mtpPropertyList.append(id, property.code, 4, (cursor != null ? cursor.getInt(property.column) : 0) % 1000);
                    break;
                case MtpConstants.PROPERTY_GENRE /* 56460 */:
                    String strQueryGenre = queryGenre(string);
                    if (strQueryGenre != null) {
                        mtpPropertyList.append(id, property.code, strQueryGenre);
                    }
                    break;
                case MtpConstants.PROPERTY_ORIGINAL_RELEASE_DATE /* 56473 */:
                    mtpPropertyList.append(id, property.code, Integer.toString(cursor != null ? cursor.getInt(property.column) : 0) + "0101T000000");
                    break;
                case MtpConstants.PROPERTY_ALBUM_NAME /* 56474 */:
                    mtpPropertyList.append(id, property.code, queryAudio(string, "album"));
                    break;
                case MtpConstants.PROPERTY_BITRATE_TYPE /* 56978 */:
                case MtpConstants.PROPERTY_NUMBER_OF_CHANNELS /* 56980 */:
                    mtpPropertyList.append(id, property.code, 4, 0L);
                    break;
                case MtpConstants.PROPERTY_SAMPLE_RATE /* 56979 */:
                case MtpConstants.PROPERTY_AUDIO_WAVE_CODEC /* 56985 */:
                case MtpConstants.PROPERTY_AUDIO_BITRATE /* 56986 */:
                    mtpPropertyList.append(id, property.code, 6, 0L);
                    break;
                default:
                    int i2 = property.type;
                    if (i2 == 0) {
                        mtpPropertyList.append(id, property.code, property.type, 0L);
                    } else if (i2 == 65535) {
                        mtpPropertyList.append(id, property.code, cursor != null ? cursor.getString(property.column) : "");
                    } else {
                        mtpPropertyList.append(id, property.code, property.type, cursor != null ? cursor.getLong(property.column) : 0L);
                    }
                    break;
            }
            i++;
            cursor2 = cursor;
        }
        if (cursor2 == null) {
            return MtpConstants.RESPONSE_OK;
        }
        cursor2.close();
        return MtpConstants.RESPONSE_OK;
    }
}
