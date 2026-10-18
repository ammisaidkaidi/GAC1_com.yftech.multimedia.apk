package com.facebook.imagepipeline.producers;

import android.app.backup.FullBackup;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import com.facebook.common.memory.PooledByteBufferFactory;
import com.facebook.common.util.UriUtil;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.request.ImageRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;

/* JADX INFO: loaded from: classes3.dex */
public class LocalContentUriFetchProducer extends LocalFetchProducer {
    public static final String PRODUCER_NAME = "LocalContentUriFetchProducer";
    private static final String[] PROJECTION = {"_id", "_data"};
    private final ContentResolver mContentResolver;

    public LocalContentUriFetchProducer(Executor executor, PooledByteBufferFactory pooledByteBufferFactory, ContentResolver contentResolver) {
        super(executor, pooledByteBufferFactory);
        this.mContentResolver = contentResolver;
    }

    @Nullable
    private EncodedImage getCameraImage(Uri uri) throws IOException {
        Cursor cursorQuery = this.mContentResolver.query(uri, PROJECTION, null, null, null);
        if (cursorQuery == null) {
            return null;
        }
        try {
            if (cursorQuery.getCount() == 0) {
                return null;
            }
            cursorQuery.moveToFirst();
            String string = cursorQuery.getString(cursorQuery.getColumnIndex("_data"));
            if (string != null) {
                return getEncodedImage(new FileInputStream(this.mContentResolver.openFileDescriptor(uri, FullBackup.ROOT_TREE_TOKEN).getFileDescriptor()), getLength(string));
            }
            return null;
        } finally {
            cursorQuery.close();
        }
    }

    private static int getLength(String str) {
        if (str == null) {
            return -1;
        }
        return (int) new File(str).length();
    }

    @Override // com.facebook.imagepipeline.producers.LocalFetchProducer
    protected EncodedImage getEncodedImage(ImageRequest imageRequest) throws Throwable {
        EncodedImage cameraImage;
        InputStream inputStreamCreateInputStream;
        Uri sourceUri = imageRequest.getSourceUri();
        if (!UriUtil.isLocalContactUri(sourceUri)) {
            return (!UriUtil.isLocalCameraUri(sourceUri) || (cameraImage = getCameraImage(sourceUri)) == null) ? getEncodedImage(this.mContentResolver.openInputStream(sourceUri), -1) : cameraImage;
        }
        if (sourceUri.toString().endsWith("/photo")) {
            inputStreamCreateInputStream = this.mContentResolver.openInputStream(sourceUri);
        } else if (sourceUri.toString().endsWith("/display_photo")) {
            try {
                inputStreamCreateInputStream = this.mContentResolver.openAssetFileDescriptor(sourceUri, FullBackup.ROOT_TREE_TOKEN).createInputStream();
            } catch (IOException unused) {
                throw new IOException("Contact photo does not exist: " + sourceUri);
            }
        } else {
            inputStreamCreateInputStream = ContactsContract.Contacts.openContactPhotoInputStream(this.mContentResolver, sourceUri);
            if (inputStreamCreateInputStream == null) {
                throw new IOException("Contact photo does not exist: " + sourceUri);
            }
        }
        return getEncodedImage(inputStreamCreateInputStream, -1);
    }

    @Override // com.facebook.imagepipeline.producers.LocalFetchProducer
    protected String getProducerName() {
        return PRODUCER_NAME;
    }
}
