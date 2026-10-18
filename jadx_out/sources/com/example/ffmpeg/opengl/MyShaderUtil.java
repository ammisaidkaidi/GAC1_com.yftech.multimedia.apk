package com.example.ffmpeg.opengl;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/* JADX INFO: loaded from: classes3.dex */
public class MyShaderUtil {
    public static int createProgram(String str, String str2) {
        int iLoadShader;
        int iLoadShader2 = loadShader(GLES20.GL_VERTEX_SHADER, str);
        if (iLoadShader2 == 0 || (iLoadShader = loadShader(GLES20.GL_FRAGMENT_SHADER, str2)) == 0) {
            return 0;
        }
        int iGlCreateProgram = GLES20.glCreateProgram();
        if (iGlCreateProgram != 0) {
            GLES20.glAttachShader(iGlCreateProgram, iLoadShader2);
            GLES20.glAttachShader(iGlCreateProgram, iLoadShader);
            GLES20.glLinkProgram(iGlCreateProgram);
            int[] iArr = new int[1];
            GLES20.glGetProgramiv(iGlCreateProgram, GLES20.GL_LINK_STATUS, iArr, 0);
            if (iArr[0] != 1) {
                Log.d("ywl5320", "link program error");
                GLES20.glDeleteProgram(iGlCreateProgram);
                return 0;
            }
        }
        return iGlCreateProgram;
    }

    public static int loadShader(int i, String str) {
        int iGlCreateShader = GLES20.glCreateShader(i);
        if (iGlCreateShader == 0) {
            return iGlCreateShader;
        }
        GLES20.glShaderSource(iGlCreateShader, str);
        GLES20.glCompileShader(iGlCreateShader);
        int[] iArr = new int[1];
        GLES20.glGetShaderiv(iGlCreateShader, GLES20.GL_COMPILE_STATUS, iArr, 0);
        if (iArr[0] == 1) {
            return iGlCreateShader;
        }
        Log.d("ywl5320", "shader compile error");
        GLES20.glDeleteShader(iGlCreateShader);
        return 0;
    }

    public static String readRawTxt(Context context, int i) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(i)));
        StringBuffer stringBuffer = new StringBuffer();
        while (true) {
            try {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                stringBuffer.append(line);
                stringBuffer.append("\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        bufferedReader.close();
        return stringBuffer.toString();
    }
}
