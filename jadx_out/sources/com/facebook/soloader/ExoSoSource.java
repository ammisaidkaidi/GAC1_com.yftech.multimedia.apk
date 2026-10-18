package com.facebook.soloader;

import android.content.Context;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;

/* JADX INFO: loaded from: classes3.dex */
public final class ExoSoSource extends UnpackingSoSource {

    private final class ExoUnpacker extends UnpackingSoSource.Unpacker {
        private final FileDso[] mDsos;

        private final class FileBackedInputDsoIterator extends UnpackingSoSource.InputDsoIterator {
            private int mCurrentDso;

            private FileBackedInputDsoIterator() {
            }

            @Override // com.facebook.soloader.UnpackingSoSource.InputDsoIterator
            public boolean hasNext() {
                return this.mCurrentDso < ExoUnpacker.this.mDsos.length;
            }

            @Override // com.facebook.soloader.UnpackingSoSource.InputDsoIterator
            public UnpackingSoSource.InputDso next() throws IOException {
                FileDso[] fileDsoArr = ExoUnpacker.this.mDsos;
                int i = this.mCurrentDso;
                this.mCurrentDso = i + 1;
                FileDso fileDso = fileDsoArr[i];
                FileInputStream fileInputStream = new FileInputStream(fileDso.backingFile);
                try {
                    return new UnpackingSoSource.InputDso(fileDso, fileInputStream);
                } catch (Throwable th) {
                    fileInputStream.close();
                    throw th;
                }
            }
        }

        /* JADX WARN: Code duplicated, block: B:47:0x0103 A[Catch: all -> 0x0107, Throwable -> 0x010b, TryCatch #0 {Throwable -> 0x010b, blocks: (B:11:0x0063, B:31:0x00e3, B:48:0x0106, B:47:0x0103, B:46:0x00ff), top: B:65:0x0063 }] */
        /* JADX WARN: Code duplicated, block: B:61:0x011c  */
        /* JADX WARN: Code duplicated, block: B:67:0x00f9 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Code duplicated, block: B:71:0x0112 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        ExoUnpacker(UnpackingSoSource unpackingSoSource) throws Throwable {
            Throwable th;
            Throwable th2;
            Throwable th3;
            Throwable th4;
            boolean z;
            File file = new File("/data/local/tmp/exopackage/" + ExoSoSource.this.mContext.getPackageName() + "/native-libs/");
            ArrayList arrayList = new ArrayList();
            LinkedHashSet linkedHashSet = new LinkedHashSet();
            String[] supportedAbis = SysUtil.getSupportedAbis();
            int length = supportedAbis.length;
            int i = 0;
            int i2 = 0;
            while (i2 < length) {
                String str = supportedAbis[i2];
                File file2 = new File(file, str);
                if (file2.isDirectory()) {
                    linkedHashSet.add(str);
                    File file3 = new File(file2, "metadata.txt");
                    if (file3.isFile()) {
                        FileReader fileReader = new FileReader(file3);
                        try {
                            try {
                                BufferedReader bufferedReader = new BufferedReader(fileReader);
                                while (true) {
                                    try {
                                        String line = bufferedReader.readLine();
                                        if (line == null) {
                                            bufferedReader.close();
                                            fileReader.close();
                                            break;
                                        }
                                        if (line.length() != 0) {
                                            int iIndexOf = line.indexOf(32);
                                            if (iIndexOf == -1) {
                                                throw new RuntimeException("illegal line in exopackage metadata: [" + line + "]");
                                            }
                                            String str2 = line.substring(i, iIndexOf) + ".so";
                                            int size = arrayList.size();
                                            while (true) {
                                                if (i >= size) {
                                                    z = false;
                                                    break;
                                                } else {
                                                    if (((FileDso) arrayList.get(i)).name.equals(str2)) {
                                                        z = true;
                                                        break;
                                                    }
                                                    i++;
                                                }
                                            }
                                            if (!z) {
                                                String strSubstring = line.substring(iIndexOf + 1);
                                                arrayList.add(new FileDso(str2, strSubstring, new File(file2, strSubstring)));
                                            }
                                            i = 0;
                                        }
                                    } catch (Throwable th5) {
                                        th3 = th5;
                                        th4 = null;
                                        if (th4 == null) {
                                            bufferedReader.close();
                                            throw th3;
                                        }
                                        bufferedReader.close();
                                        throw th3;
                                    }
                                }
                            } catch (Throwable th6) {
                                th2 = th6;
                                try {
                                    throw th2;
                                } catch (Throwable th7) {
                                    th = th7;
                                    if (th2 == null) {
                                        fileReader.close();
                                        throw th;
                                    }
                                    try {
                                        fileReader.close();
                                        throw th;
                                    } catch (Throwable th8) {
                                        th2.addSuppressed(th8);
                                        throw th;
                                    }
                                }
                            }
                        } catch (Throwable th9) {
                            th = th9;
                            th2 = null;
                            if (th2 == null) {
                                fileReader.close();
                                throw th;
                            }
                            fileReader.close();
                            throw th;
                        }
                    } else {
                        continue;
                    }
                }
                i2++;
                i = 0;
            }
            unpackingSoSource.setSoSourceAbis((String[]) linkedHashSet.toArray(new String[linkedHashSet.size()]));
            this.mDsos = (FileDso[]) arrayList.toArray(new FileDso[arrayList.size()]);
        }

        @Override // com.facebook.soloader.UnpackingSoSource.Unpacker
        protected UnpackingSoSource.DsoManifest getDsoManifest() throws IOException {
            return new UnpackingSoSource.DsoManifest(this.mDsos);
        }

        @Override // com.facebook.soloader.UnpackingSoSource.Unpacker
        protected UnpackingSoSource.InputDsoIterator openDsoIterator() throws IOException {
            return new FileBackedInputDsoIterator();
        }
    }

    private static final class FileDso extends UnpackingSoSource.Dso {
        final File backingFile;

        FileDso(String str, String str2, File file) {
            super(str, str2);
            this.backingFile = file;
        }
    }

    public ExoSoSource(Context context, String str) {
        super(context, str);
    }

    @Override // com.facebook.soloader.UnpackingSoSource
    protected UnpackingSoSource.Unpacker makeUnpacker() throws IOException {
        return new ExoUnpacker(this);
    }
}
