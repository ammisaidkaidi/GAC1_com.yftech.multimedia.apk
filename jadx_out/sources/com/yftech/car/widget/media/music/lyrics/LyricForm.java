package com.yftech.car.widget.media.music.lyrics;

import android.os.Handler;
import android.util.Log;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public class LyricForm {
    private static final String KRC = ".krc";
    private static final String LRC = ".lrc";
    private static final String TAG = "LyricForm";
    private boolean isReadFile;
    private boolean mAddKrc;
    private Handler mHandler;
    private Lyric mLyric;
    private final int[] miarry = {64, 71, 97, 119, 94, 50, 116, 71, 81, 54, 49, 45, 206, 210, 110, 105};
    private final Charset CHARSET_GBK = Charset.forName("GBK");
    private boolean isValid = true;

    public LyricForm(Handler handler, boolean z) {
        this.mHandler = handler;
        this.mAddKrc = z;
    }

    private byte[] getKrcText(File file) {
        FileInputStream fileInputStream;
        byte[] bArr = new byte[(int) file.length()];
        try {
            fileInputStream = new FileInputStream(file);
            try {
                fileInputStream.read(new byte[4]);
                fileInputStream.read(bArr);
            } catch (IOException e) {
                e = e;
                e.printStackTrace();
            }
        } catch (IOException e2) {
            e = e2;
            fileInputStream = null;
        }
        int length = bArr.length;
        for (int i = 0; i < length; i++) {
            bArr[i] = (byte) (this.miarry[i % 16] ^ bArr[i]);
        }
        if (fileInputStream != null) {
            try {
                fileInputStream.close();
            } catch (IOException e3) {
                e3.printStackTrace();
            }
        }
        return ZLibUtils.decompress(bArr);
    }

    private boolean isFileName(File file, String str) {
        return file.getName().toLowerCase().equals(str.toLowerCase());
    }

    private boolean isFindNameKrc(String str, String str2) {
        return str2.startsWith(str) && str2.toLowerCase().endsWith(KRC);
    }

    private boolean isFindNameLrc(String str, String str2) {
        return str2.startsWith(str) && str2.toLowerCase().endsWith(LRC);
    }

    private Lyric loadLyric(String str) {
        File file = new File(str);
        if (this.mLyric != null) {
            this.mLyric = null;
        }
        if (file.exists()) {
            Log.d(TAG, "loadLyric: lrc exists");
            if (!str.endsWith(KRC)) {
                loadingLrc(file, StandardCharsets.UTF_8);
                return this.mLyric;
            }
            this.mLyric = loadingKrc(file);
        } else if (this.mAddKrc) {
            boolean z = true;
            if (str.endsWith(LRC)) {
                String strSubstring = str.substring(0, str.lastIndexOf("."));
                String str2 = strSubstring + KRC;
                Log.d(TAG, "loadingLyric: krcPath==" + str2);
                File file2 = new File(str2);
                if (file2.exists()) {
                    Log.d(TAG, "loadLyric: krc exists");
                    this.mLyric = loadingKrc(file2);
                } else {
                    File[] fileArrListFiles = new File(str.substring(0, str.lastIndexOf("/"))).listFiles();
                    if (fileArrListFiles != null) {
                        for (File file3 : fileArrListFiles) {
                            if (!this.isValid) {
                                Log.e(TAG, "readFileLyric: isValid is false");
                                break;
                            }
                            if (file3.isFile()) {
                                String path = file3.getPath();
                                if (isFindNameKrc(strSubstring, path)) {
                                    this.mLyric = loadLyric(path);
                                    z = false;
                                    break;
                                }
                            }
                        }
                    }
                    Log.d(TAG, "loadLyric: isFind==" + z);
                    if (z) {
                        this.mLyric = loadLyric(str2);
                    }
                }
            } else {
                Log.d(TAG, "loadingLyric: path ==" + str);
                this.isReadFile = false;
                if (!str.equals("usb/null")) {
                    if (str.length() > 9) {
                        int iIndexOf = str.substring(9).indexOf("/");
                        if (iIndexOf != -1) {
                            int i = iIndexOf + 9;
                            if (str.length() > i) {
                                String strSubstring2 = str.substring(0, i);
                                Log.e(TAG, "loadingLyric: 歌词文件未找到，正在全盘扫描歌词... scan == " + strSubstring2);
                                File[] fileArrListFiles2 = new File(strSubstring2).listFiles();
                                if (fileArrListFiles2 != null) {
                                    readFileLyric(fileArrListFiles2, str.substring(str.lastIndexOf("/") + 1));
                                }
                            } else {
                                Log.e(TAG, "loadLyric: indexOf error end==" + i);
                            }
                        } else {
                            Log.e(TAG, "loadLyric: path error,endIndex==" + iIndexOf);
                        }
                    } else {
                        Log.e(TAG, "loadLyric: path error");
                    }
                }
                if (!this.isReadFile) {
                    Log.e(TAG, "loadLyric: 未找到歌词文件");
                }
            }
        }
        return this.mLyric != null ? this.mLyric : Lyric.EMPTY;
    }

    private Lyric loadingKrc(File file) {
        byte[] krcText = getKrcText(file);
        Log.d(TAG, "loadingKrc: krc data ---ok");
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(krcText);
        InputStreamReader inputStreamReader = new InputStreamReader(byteArrayInputStream, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder sb = new StringBuilder();
        Lyric.Builder builder = new Lyric.Builder();
        while (true) {
            try {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                if (!this.isValid) {
                    Log.e(TAG, "loadingKrc: isValid is false");
                    return null;
                }
                if (sb.length() > 0) {
                    sb.delete(0, sb.length());
                }
                int iIndexOf = line.indexOf("]") + 1;
                String strSubstring = line.substring(iIndexOf);
                ArrayList arrayList = new ArrayList();
                loadingLyricDate(arrayList, strSubstring, sb);
                LrcParser.parseLine(builder, line.substring(0, iIndexOf) + sb.toString(), true);
                builder.setKrcList(arrayList);
            } catch (IOException e) {
                e.printStackTrace();
                builder = null;
                break;
            }
        }
        try {
            byteArrayInputStream.close();
            inputStreamReader.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        return builder != null ? builder.build() : Lyric.EMPTY;
    }

    private void loadingLrc(File file, Charset charset) {
        Lyric.Builder builder;
        int length;
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, charset);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            builder = new Lyric.Builder();
            while (true) {
                try {
                    String line = bufferedReader.readLine();
                    if (line == null) {
                        fileInputStream.close();
                        inputStreamReader.close();
                        break;
                    }
                    if (!this.isValid) {
                        Log.e(TAG, "loadingLrc: isValid is false");
                        return;
                    }
                    if (charset != this.CHARSET_GBK && (length = line.length()) > 3) {
                        int i = 0;
                        int iLastIndexOf = line.lastIndexOf("]");
                        if (iLastIndexOf > 0 && iLastIndexOf < length) {
                            while (iLastIndexOf < length) {
                                if (line.charAt(iLastIndexOf) == 65533) {
                                    i++;
                                }
                                iLastIndexOf++;
                            }
                            if (i > length / 3) {
                                loadingLrc(file, this.CHARSET_GBK);
                                fileInputStream.close();
                                inputStreamReader.close();
                                return;
                            }
                        }
                    }
                    LrcParser.parseLine(builder, line);
                } catch (IOException e) {
                    e = e;
                    e.printStackTrace();
                }
            }
        } catch (IOException e2) {
            e = e2;
            builder = null;
        }
        this.mLyric = builder != null ? builder.build() : Lyric.EMPTY;
    }

    private void loadingLyricDate(List<KrcPlayEntity> list, String str, StringBuilder sb) {
        int iIndexOf;
        if (str.startsWith("<")) {
            int iIndexOf2 = str.indexOf(">") + 1;
            String strSubstring = str.substring(1, iIndexOf2 - 1);
            KrcPlayEntity krcPlayEntity = new KrcPlayEntity();
            String[] strArrSplit = strSubstring.split(",");
            int[] iArr = new int[strArrSplit.length];
            for (int i = 0; i < iArr.length; i++) {
                iArr[i] = Integer.parseInt(strArrSplit[i]);
            }
            krcPlayEntity.setPlayTime(iArr);
            if (str.length() > iIndexOf2) {
                String strSubstring2 = str.substring(iIndexOf2);
                int iIndexOf3 = strSubstring2.indexOf("<", -1);
                if (iIndexOf3 == -1) {
                    krcPlayEntity.setLyrics(strSubstring2);
                } else {
                    krcPlayEntity.setLyrics(strSubstring2.substring(0, iIndexOf3));
                }
                sb.append(krcPlayEntity.getLyrics());
            } else {
                krcPlayEntity.setLyrics("");
            }
            list.add(krcPlayEntity);
            String strSubstring3 = str.substring(iIndexOf2);
            if (strSubstring3.length() <= 0 || (iIndexOf = strSubstring3.indexOf("<", -1)) == -1) {
                return;
            }
            loadingLyricDate(list, strSubstring3.substring(iIndexOf), sb);
        }
    }

    private void readFileLyric(File[] fileArr, String str) {
        if (!this.isValid) {
            Log.e(TAG, "readFileLyric: isValid is false");
            return;
        }
        for (int i = 0; fileArr != null && i < fileArr.length; i++) {
            if (!this.isValid) {
                Log.e(TAG, "readFileLyric: isValid is false");
                return;
            }
            File file = fileArr[i];
            boolean z = true;
            if (file.isFile()) {
                if (isFindNameLrc(str.substring(0, str.lastIndexOf(".")), file.getName())) {
                    Log.i(TAG, "readFile: music content lrc：" + file.getPath());
                    this.isReadFile = true;
                    loadingLrc(file, StandardCharsets.UTF_8);
                } else if (isFindNameKrc(str.substring(0, str.lastIndexOf(".")), file.getName())) {
                    Log.i(TAG, "readFile: music content krc：" + file.getPath());
                    this.isReadFile = true;
                    this.mLyric = loadingKrc(file);
                }
                if (this.isReadFile) {
                    return;
                }
            } else if (file.isDirectory()) {
                String absolutePath = file.getAbsolutePath();
                int i2 = 0;
                for (int i3 = 0; i3 < absolutePath.length(); i3++) {
                    if (absolutePath.charAt(i3) == '/') {
                        if (i2 > 50) {
                            z = false;
                            break;
                        }
                        i2++;
                    }
                }
                if (z) {
                    readFileLyric(new File(absolutePath).listFiles(), str);
                }
            }
        }
    }

    public void parseLyric(final LyricView lyricView, String str) {
        int iLastIndexOf;
        String str2 = "usb/null";
        if (str != null && (iLastIndexOf = str.lastIndexOf(".")) > 1) {
            str2 = str.substring(0, iLastIndexOf) + LRC;
        }
        Log.d(TAG, "handleMessage: path==" + str2);
        loadLyric(str2);
        Log.d(TAG, "parseLyric: isValid==" + this.isValid);
        if (this.isValid) {
            lyricView.setLyricMessageNoLyric();
            this.mHandler.post(new Runnable() { // from class: com.yftech.car.widget.media.music.lyrics.LyricForm.1
                @Override // java.lang.Runnable
                public void run() {
                    lyricView.setLyric(LyricForm.this.mLyric);
                }
            });
        }
    }

    public void setValid(boolean z) {
        this.isValid = z;
        this.mHandler = null;
        this.mLyric = null;
    }
}
