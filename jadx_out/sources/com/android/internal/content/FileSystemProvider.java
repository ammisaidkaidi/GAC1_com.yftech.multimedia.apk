package com.android.internal.content;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.FileObserver;
import android.os.FileUtils;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.DocumentsProvider;
import android.provider.MediaStore;
import android.provider.MetadataReader;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.webkit.MimeTypeMap;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.widget.MessagingMessage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import libcore.io.IoUtils;

/* JADX INFO: loaded from: classes3.dex */
public abstract class FileSystemProvider extends DocumentsProvider {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final boolean LOG_INOTIFY = false;
    private static final String MIMETYPE_JPEG = "image/jpeg";
    private static final String MIMETYPE_JPG = "image/jpg";
    private static final String MIMETYPE_OCTET_STREAM = "application/octet-stream";
    private static final String TAG = "FileSystemProvider";
    private String[] mDefaultProjection;
    private Handler mHandler;

    @GuardedBy("mObservers")
    private final ArrayMap<File, DirectoryObserver> mObservers = new ArrayMap<>();

    private class DirectoryCursor extends MatrixCursor {
        private final File mFile;

        public DirectoryCursor(String[] strArr, String str, File file) {
            super(strArr);
            Uri uriBuildNotificationUri = FileSystemProvider.this.buildNotificationUri(str);
            setNotificationUri(FileSystemProvider.this.getContext().getContentResolver(), uriBuildNotificationUri);
            this.mFile = file;
            FileSystemProvider.this.startObserving(this.mFile, uriBuildNotificationUri);
        }

        @Override // android.database.AbstractCursor, android.database.Cursor, java.io.Closeable, java.lang.AutoCloseable
        public void close() {
            super.close();
            FileSystemProvider.this.stopObserving(this.mFile);
        }
    }

    private static class DirectoryObserver extends FileObserver {
        private static final int NOTIFY_EVENTS = 4044;
        private final File mFile;
        private final Uri mNotifyUri;
        private int mRefCount;
        private final ContentResolver mResolver;

        public DirectoryObserver(File file, ContentResolver contentResolver, Uri uri) {
            super(file.getAbsolutePath(), NOTIFY_EVENTS);
            this.mRefCount = 0;
            this.mFile = file;
            this.mResolver = contentResolver;
            this.mNotifyUri = uri;
        }

        static /* synthetic */ int access$008(DirectoryObserver directoryObserver) {
            int i = directoryObserver.mRefCount;
            directoryObserver.mRefCount = i + 1;
            return i;
        }

        static /* synthetic */ int access$010(DirectoryObserver directoryObserver) {
            int i = directoryObserver.mRefCount;
            directoryObserver.mRefCount = i - 1;
            return i;
        }

        @Override // android.os.FileObserver
        public void onEvent(int i, String str) {
            if ((i & NOTIFY_EVENTS) != 0) {
                this.mResolver.notifyChange(this.mNotifyUri, (ContentObserver) null, false);
            }
        }

        public String toString() {
            return "DirectoryObserver{file=" + this.mFile.getAbsolutePath() + ", ref=" + this.mRefCount + "}";
        }
    }

    private void addFolderToMediaStore(File file) {
        if (file != null) {
            long jClearCallingIdentity = Binder.clearCallingIdentity();
            try {
                ContentResolver contentResolver = getContext().getContentResolver();
                Uri directoryUri = MediaStore.Files.getDirectoryUri("external");
                ContentValues contentValues = new ContentValues();
                contentValues.put("_data", file.getAbsolutePath());
                contentResolver.insert(directoryUri, contentValues);
            } finally {
                Binder.restoreCallingIdentity(jClearCallingIdentity);
            }
        }
    }

    private static String getTypeForFile(File file) {
        return file.isDirectory() ? DocumentsContract.Document.MIME_TYPE_DIR : getTypeForName(file.getName());
    }

    private static String getTypeForName(String str) {
        int iLastIndexOf = str.lastIndexOf(46);
        if (iLastIndexOf < 0) {
            return MIMETYPE_OCTET_STREAM;
        }
        String mimeTypeFromExtension = MimeTypeMap.getSingleton().getMimeTypeFromExtension(str.substring(iLastIndexOf + 1).toLowerCase());
        return mimeTypeFromExtension != null ? mimeTypeFromExtension : MIMETYPE_OCTET_STREAM;
    }

    public static /* synthetic */ void lambda$openDocument$0(FileSystemProvider fileSystemProvider, String str, File file, IOException iOException) {
        fileSystemProvider.onDocIdChanged(str);
        fileSystemProvider.scanFile(file);
    }

    private void moveInMediaStore(File file, File file2) {
        if (file == null || file2 == null) {
            return;
        }
        long jClearCallingIdentity = Binder.clearCallingIdentity();
        try {
            ContentResolver contentResolver = getContext().getContentResolver();
            Uri directoryUri = file2.isDirectory() ? MediaStore.Files.getDirectoryUri("external") : MediaStore.Files.getContentUri("external");
            ContentValues contentValues = new ContentValues();
            contentValues.put("_data", file2.getAbsolutePath());
            String absolutePath = file.getAbsolutePath();
            contentResolver.update(directoryUri, contentValues, "_data LIKE ? AND lower(_data)=lower(?)", new String[]{absolutePath, absolutePath});
        } finally {
            Binder.restoreCallingIdentity(jClearCallingIdentity);
        }
    }

    private void removeFromMediaStore(File file, boolean z) throws FileNotFoundException {
        if (file != null) {
            long jClearCallingIdentity = Binder.clearCallingIdentity();
            try {
                ContentResolver contentResolver = getContext().getContentResolver();
                Uri contentUri = MediaStore.Files.getContentUri("external");
                if (z) {
                    String str = file.getAbsolutePath() + "/";
                    contentResolver.delete(contentUri, "_data LIKE ?1 AND lower(substr(_data,1,?2))=lower(?3)", new String[]{str + "%", Integer.toString(str.length()), str});
                }
                String absolutePath = file.getAbsolutePath();
                contentResolver.delete(contentUri, "_data LIKE ?1 AND lower(_data)=lower(?2)", new String[]{absolutePath, absolutePath});
            } finally {
                Binder.restoreCallingIdentity(jClearCallingIdentity);
            }
        }
    }

    private String[] resolveProjection(String[] strArr) {
        return strArr == null ? this.mDefaultProjection : strArr;
    }

    private void scanFile(File file) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        getContext().sendBroadcast(intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startObserving(File file, Uri uri) {
        synchronized (this.mObservers) {
            DirectoryObserver directoryObserver = this.mObservers.get(file);
            if (directoryObserver == null) {
                directoryObserver = new DirectoryObserver(file, getContext().getContentResolver(), uri);
                directoryObserver.startWatching();
                this.mObservers.put(file, directoryObserver);
            }
            DirectoryObserver.access$008(directoryObserver);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopObserving(File file) {
        synchronized (this.mObservers) {
            DirectoryObserver directoryObserver = this.mObservers.get(file);
            if (directoryObserver == null) {
                return;
            }
            DirectoryObserver.access$010(directoryObserver);
            if (directoryObserver.mRefCount == 0) {
                this.mObservers.remove(file);
                directoryObserver.stopWatching();
            }
        }
    }

    protected abstract Uri buildNotificationUri(String str);

    @Override // android.provider.DocumentsProvider
    public String createDocument(String str, String str2, String str3) throws FileNotFoundException {
        String strBuildValidFatFilename = FileUtils.buildValidFatFilename(str3);
        File fileForDocId = getFileForDocId(str);
        if (!fileForDocId.isDirectory()) {
            throw new IllegalArgumentException("Parent document isn't a directory");
        }
        File fileBuildUniqueFile = FileUtils.buildUniqueFile(fileForDocId, str2, strBuildValidFatFilename);
        if (DocumentsContract.Document.MIME_TYPE_DIR.equals(str2)) {
            if (fileBuildUniqueFile.mkdir()) {
                String docIdForFile = getDocIdForFile(fileBuildUniqueFile);
                onDocIdChanged(docIdForFile);
                addFolderToMediaStore(getFileForDocId(docIdForFile, true));
                return docIdForFile;
            }
            throw new IllegalStateException("Failed to mkdir " + fileBuildUniqueFile);
        }
        try {
            if (fileBuildUniqueFile.createNewFile()) {
                String docIdForFile2 = getDocIdForFile(fileBuildUniqueFile);
                onDocIdChanged(docIdForFile2);
                return docIdForFile2;
            }
            throw new IllegalStateException("Failed to touch " + fileBuildUniqueFile);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to touch " + fileBuildUniqueFile + ": " + e);
        }
    }

    @Override // android.provider.DocumentsProvider
    public void deleteDocument(String str) throws FileNotFoundException {
        File fileForDocId = getFileForDocId(str);
        File fileForDocId2 = getFileForDocId(str, true);
        boolean zIsDirectory = fileForDocId.isDirectory();
        if (zIsDirectory) {
            FileUtils.deleteContents(fileForDocId);
        }
        if (fileForDocId.delete()) {
            onDocIdChanged(str);
            removeFromMediaStore(fileForDocId2, zIsDirectory);
        } else {
            throw new IllegalStateException("Failed to delete " + fileForDocId);
        }
    }

    protected final List<String> findDocumentPath(File file, File file2) throws FileNotFoundException {
        if (!file2.exists()) {
            throw new FileNotFoundException(file2 + " is not found.");
        }
        if (!FileUtils.contains(file, file2)) {
            throw new FileNotFoundException(file2 + " is not found under " + file);
        }
        LinkedList linkedList = new LinkedList();
        while (file2 != null && FileUtils.contains(file, file2)) {
            linkedList.addFirst(getDocIdForFile(file2));
            file2 = file2.getParentFile();
        }
        return linkedList;
    }

    protected abstract String getDocIdForFile(File file) throws FileNotFoundException;

    @Override // android.provider.DocumentsProvider
    public Bundle getDocumentMetadata(String str) throws Throwable {
        FileInputStream fileInputStream;
        File fileForDocId = getFileForDocId(str);
        if (!fileForDocId.exists()) {
            throw new FileNotFoundException("Can't find the file for documentId: " + str);
        }
        FileInputStream fileInputStream2 = null;
        if (!fileForDocId.isFile()) {
            Log.w(TAG, "Can't stream non-regular file. Returning empty metadata.");
            return null;
        }
        if (!fileForDocId.canRead()) {
            Log.w(TAG, "Can't stream non-readable file. Returning empty metadata.");
            return null;
        }
        String typeForFile = getTypeForFile(fileForDocId);
        if (!MetadataReader.isSupportedMimeType(typeForFile)) {
            return null;
        }
        try {
            Bundle bundle = new Bundle();
            fileInputStream = new FileInputStream(fileForDocId.getAbsolutePath());
            try {
                try {
                    MetadataReader.getMetadata(bundle, fileInputStream, typeForFile, null);
                    IoUtils.closeQuietly(fileInputStream);
                    return bundle;
                } catch (IOException e) {
                    e = e;
                    Log.e(TAG, "An error occurred retrieving the metadata", e);
                    IoUtils.closeQuietly(fileInputStream);
                    return null;
                }
            } catch (Throwable th) {
                th = th;
                fileInputStream2 = fileInputStream;
                IoUtils.closeQuietly(fileInputStream2);
                throw th;
            }
        } catch (IOException e2) {
            e = e2;
            fileInputStream = null;
        } catch (Throwable th2) {
            th = th2;
            IoUtils.closeQuietly(fileInputStream2);
            throw th;
        }
    }

    @Override // android.provider.DocumentsProvider
    public String getDocumentType(String str) throws FileNotFoundException {
        return getTypeForFile(getFileForDocId(str));
    }

    protected final File getFileForDocId(String str) throws FileNotFoundException {
        return getFileForDocId(str, false);
    }

    protected abstract File getFileForDocId(String str, boolean z) throws FileNotFoundException;

    protected MatrixCursor.RowBuilder includeFile(MatrixCursor matrixCursor, String str, File file) throws FileNotFoundException {
        if (str == null) {
            str = getDocIdForFile(file);
        } else {
            file = getFileForDocId(str);
        }
        int i = 0;
        if (file.canWrite()) {
            i = file.isDirectory() ? 332 : 326;
        }
        String typeForFile = getTypeForFile(file);
        String name = file.getName();
        if (typeForFile.startsWith(MessagingMessage.IMAGE_MIME_TYPE_PREFIX)) {
            i |= 1;
        }
        if (typeSupportsMetadata(typeForFile)) {
            i |= 131072;
        }
        MatrixCursor.RowBuilder rowBuilderNewRow = matrixCursor.newRow();
        rowBuilderNewRow.add("document_id", str);
        rowBuilderNewRow.add("_display_name", name);
        rowBuilderNewRow.add("_size", Long.valueOf(file.length()));
        rowBuilderNewRow.add("mime_type", typeForFile);
        rowBuilderNewRow.add("flags", Integer.valueOf(i));
        long jLastModified = file.lastModified();
        if (jLastModified > 31536000000L) {
            rowBuilderNewRow.add("last_modified", Long.valueOf(jLastModified));
        }
        return rowBuilderNewRow;
    }

    @Override // android.provider.DocumentsProvider
    public boolean isChildDocument(String str, String str2) {
        try {
            return FileUtils.contains(getFileForDocId(str).getCanonicalFile(), getFileForDocId(str2).getCanonicalFile());
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to determine if " + str2 + " is child of " + str + ": " + e);
        }
    }

    @Override // android.provider.DocumentsProvider
    public String moveDocument(String str, String str2, String str3) throws FileNotFoundException {
        File fileForDocId = getFileForDocId(str);
        File file = new File(getFileForDocId(str3), fileForDocId.getName());
        File fileForDocId2 = getFileForDocId(str, true);
        if (file.exists()) {
            throw new IllegalStateException("Already exists " + file);
        }
        if (!fileForDocId.renameTo(file)) {
            throw new IllegalStateException("Failed to move to " + file);
        }
        String docIdForFile = getDocIdForFile(file);
        onDocIdChanged(str);
        onDocIdChanged(docIdForFile);
        moveInMediaStore(fileForDocId2, getFileForDocId(docIdForFile, true));
        return docIdForFile;
    }

    protected void onCreate(String[] strArr) {
        this.mHandler = new Handler();
        this.mDefaultProjection = strArr;
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        throw new UnsupportedOperationException("Subclass should override this and call onCreate(defaultDocumentProjection)");
    }

    protected void onDocIdChanged(String str) {
    }

    @Override // android.provider.DocumentsProvider
    public ParcelFileDescriptor openDocument(final String str, String str2, CancellationSignal cancellationSignal) throws FileNotFoundException {
        File fileForDocId = getFileForDocId(str);
        final File fileForDocId2 = getFileForDocId(str, true);
        int mode = ParcelFileDescriptor.parseMode(str2);
        if (mode == 268435456 || fileForDocId2 == null) {
            return ParcelFileDescriptor.open(fileForDocId, mode);
        }
        try {
            return ParcelFileDescriptor.open(fileForDocId, mode, this.mHandler, new ParcelFileDescriptor.OnCloseListener() { // from class: com.android.internal.content.-$$Lambda$FileSystemProvider$y9rjeYFpkvVjwD2Whw-ujCM-C7Y
                @Override // android.os.ParcelFileDescriptor.OnCloseListener
                public final void onClose(IOException iOException) {
                    FileSystemProvider.lambda$openDocument$0(this.f$0, str, fileForDocId2, iOException);
                }
            });
        } catch (IOException e) {
            throw new FileNotFoundException("Failed to open for writing: " + e);
        }
    }

    @Override // android.provider.DocumentsProvider
    public AssetFileDescriptor openDocumentThumbnail(String str, Point point, CancellationSignal cancellationSignal) throws FileNotFoundException {
        return DocumentsContract.openImageThumbnail(getFileForDocId(str));
    }

    @Override // android.provider.DocumentsProvider
    public Cursor queryChildDocuments(String str, String[] strArr, String str2) throws FileNotFoundException {
        File fileForDocId = getFileForDocId(str);
        DirectoryCursor directoryCursor = new DirectoryCursor(resolveProjection(strArr), str, fileForDocId);
        File[] fileArrListFiles = fileForDocId.listFiles();
        for (File file : fileArrListFiles) {
            includeFile(directoryCursor, null, file);
        }
        return directoryCursor;
    }

    @Override // android.provider.DocumentsProvider
    public Cursor queryDocument(String str, String[] strArr) throws FileNotFoundException {
        MatrixCursor matrixCursor = new MatrixCursor(resolveProjection(strArr));
        includeFile(matrixCursor, str, null);
        return matrixCursor;
    }

    protected final Cursor querySearchDocuments(File file, String str, String[] strArr, Set<String> set) throws FileNotFoundException {
        String lowerCase = str.toLowerCase();
        MatrixCursor matrixCursor = new MatrixCursor(resolveProjection(strArr));
        LinkedList linkedList = new LinkedList();
        linkedList.add(file);
        while (!linkedList.isEmpty() && matrixCursor.getCount() < 24) {
            File file2 = (File) linkedList.removeFirst();
            if (file2.isDirectory()) {
                for (File file3 : file2.listFiles()) {
                    linkedList.add(file3);
                }
            }
            if (file2.getName().toLowerCase().contains(lowerCase) && !set.contains(file2.getAbsolutePath())) {
                includeFile(matrixCursor, null, file2);
            }
        }
        return matrixCursor;
    }

    @Override // android.provider.DocumentsProvider
    public String renameDocument(String str, String str2) throws FileNotFoundException {
        String strBuildValidFatFilename = FileUtils.buildValidFatFilename(str2);
        File fileForDocId = getFileForDocId(str);
        File fileBuildUniqueFile = FileUtils.buildUniqueFile(fileForDocId.getParentFile(), strBuildValidFatFilename);
        File fileForDocId2 = getFileForDocId(str, true);
        if (!fileForDocId.renameTo(fileBuildUniqueFile)) {
            throw new IllegalStateException("Failed to rename to " + fileBuildUniqueFile);
        }
        String docIdForFile = getDocIdForFile(fileBuildUniqueFile);
        onDocIdChanged(str);
        onDocIdChanged(docIdForFile);
        File fileForDocId3 = getFileForDocId(docIdForFile, true);
        moveInMediaStore(fileForDocId2, fileForDocId3);
        if (TextUtils.equals(str, docIdForFile)) {
            return null;
        }
        scanFile(fileForDocId3);
        return docIdForFile;
    }

    protected boolean typeSupportsMetadata(String str) {
        return MetadataReader.isSupportedMimeType(str);
    }
}
