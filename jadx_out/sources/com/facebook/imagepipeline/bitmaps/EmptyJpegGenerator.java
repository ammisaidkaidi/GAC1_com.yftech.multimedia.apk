package com.facebook.imagepipeline.bitmaps;

import android.net.wifi.WifiScanner;
import com.android.internal.midi.MidiConstants;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.memory.PooledByteBufferFactory;
import com.facebook.common.memory.PooledByteBufferOutputStream;
import com.facebook.common.references.CloseableReference;
import com.facebook.infer.annotation.Nullsafe;
import com.yftech.vehicle.internal.adapter.ICarDashBoard;
import com.yftech.vehicle.internal.project.Ihu12DashBoard;
import java.io.IOException;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.LOCAL)
public class EmptyJpegGenerator {
    private static final byte[] EMPTY_JPEG_PREFIX = {-1, -40, -1, -37, 0, 67, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -64, 0, 17, 8};
    private static final byte[] EMPTY_JPEG_SUFFIX = {3, 1, 34, 0, 2, 17, 0, 3, 17, 0, -1, -60, 0, 31, 0, 0, 1, 5, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, Ihu12DashBoard.INFO_TYPE_NAVI_GUIDE_NEXT_PATH_CALCULATION, -1, -60, 0, -75, WifiScanner.PnoSettings.PnoNetwork.FLAG_SAME_NETWORK, 0, 2, 1, 3, 3, 2, 4, 3, 5, 5, 4, 4, 0, 0, 1, 125, 1, 2, 3, 0, 4, 17, 5, 18, 33, 49, 65, 6, 19, 81, 97, 7, 34, 113, 20, 50, -127, -111, -95, 8, 35, 66, -79, -63, 21, 82, -47, -16, 36, 51, 98, 114, -126, 9, 10, 22, 23, 24, 25, 26, 37, ICarDashBoard.DashBoardMsgAudio.AUDIO_STATE_UNPLAYABLE_FILE, 39, 40, ICarDashBoard.DashBoardMsgAudio.AUDIO_STATE_NO_AUDIO_FILE, 42, 52, 53, 54, ICarDashBoard.DashBoardMsgAudio.AUDIO_STATE_LOADING_AUDIO_FILE_LIST, 56, 57, ICarDashBoard.DashBoardMsgAudio.AUDIO_STATE_NO_DEVICES_CONNECTED, 67, 68, 69, 70, 71, 72, 73, 74, 83, 84, 85, 86, 87, 88, 89, 90, 99, 100, 101, 102, 103, 104, 105, 106, 115, 116, 117, 118, 119, 120, 121, 122, -125, -124, -123, -122, -121, -120, -119, -118, -110, -109, -108, -107, -106, -105, -104, -103, -102, -94, -93, -92, -91, -90, -89, -88, -87, -86, -78, -77, -76, -75, -74, -73, -72, -71, -70, -62, -61, -60, -59, -58, -57, -56, -55, -54, -46, -45, -44, -43, -42, -41, -40, -39, -38, -31, -30, -29, -28, -27, -26, -25, -24, -23, -22, MidiConstants.STATUS_MIDI_TIME_CODE, MidiConstants.STATUS_SONG_POSITION, MidiConstants.STATUS_SONG_SELECT, -12, -11, -10, -9, -8, -7, -6, -1, -60, 0, 31, 1, 0, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, Ihu12DashBoard.INFO_TYPE_NAVI_GUIDE_NEXT_PATH_CALCULATION, -1, -60, 0, -75, 17, 0, 2, 1, 2, 4, 4, 3, 4, 7, 5, 4, 4, 0, 1, 2, 119, 0, 1, 2, 3, 17, 4, 5, 33, 49, 6, 18, 65, 81, 7, 97, 113, 19, 34, 50, -127, 8, 20, 66, -111, -95, -79, -63, 9, 35, 51, 82, -16, 21, 98, 114, -47, 10, 22, 36, 52, -31, 37, MidiConstants.STATUS_MIDI_TIME_CODE, 23, 24, 25, 26, ICarDashBoard.DashBoardMsgAudio.AUDIO_STATE_UNPLAYABLE_FILE, 39, 40, ICarDashBoard.DashBoardMsgAudio.AUDIO_STATE_NO_AUDIO_FILE, 42, 53, 54, ICarDashBoard.DashBoardMsgAudio.AUDIO_STATE_LOADING_AUDIO_FILE_LIST, 56, 57, ICarDashBoard.DashBoardMsgAudio.AUDIO_STATE_NO_DEVICES_CONNECTED, 67, 68, 69, 70, 71, 72, 73, 74, 83, 84, 85, 86, 87, 88, 89, 90, 99, 100, 101, 102, 103, 104, 105, 106, 115, 116, 117, 118, 119, 120, 121, 122, -126, -125, -124, -123, -122, -121, -120, -119, -118, -110, -109, -108, -107, -106, -105, -104, -103, -102, -94, -93, -92, -91, -90, -89, -88, -87, -86, -78, -77, -76, -75, -74, -73, -72, -71, -70, -62, -61, -60, -59, -58, -57, -56, -55, -54, -46, -45, -44, -43, -42, -41, -40, -39, -38, -30, -29, -28, -27, -26, -25, -24, -23, -22, MidiConstants.STATUS_SONG_POSITION, MidiConstants.STATUS_SONG_SELECT, -12, -11, -10, -9, -8, -7, -6, -1, -38, 0, 12, 3, 1, 0, 2, 17, 3, 17, 0, 63, 0, -114, -118, 40, MidiConstants.STATUS_POLYPHONIC_AFTERTOUCH, MidiConstants.STATUS_CHANNEL_MASK, -1, -39};
    private final PooledByteBufferFactory mPooledByteBufferFactory;

    public EmptyJpegGenerator(PooledByteBufferFactory pooledByteBufferFactory) {
        this.mPooledByteBufferFactory = pooledByteBufferFactory;
    }

    public CloseableReference<PooledByteBuffer> generate(short s, short s2) throws Throwable {
        PooledByteBufferOutputStream pooledByteBufferOutputStreamNewOutputStream;
        PooledByteBufferOutputStream pooledByteBufferOutputStream = null;
        try {
            try {
                pooledByteBufferOutputStreamNewOutputStream = this.mPooledByteBufferFactory.newOutputStream(EMPTY_JPEG_PREFIX.length + EMPTY_JPEG_SUFFIX.length + 4);
                try {
                    pooledByteBufferOutputStreamNewOutputStream.write(EMPTY_JPEG_PREFIX);
                    pooledByteBufferOutputStreamNewOutputStream.write((byte) (s2 >> 8));
                    pooledByteBufferOutputStreamNewOutputStream.write((byte) (s2 & 255));
                    pooledByteBufferOutputStreamNewOutputStream.write((byte) (s >> 8));
                    pooledByteBufferOutputStreamNewOutputStream.write((byte) (s & 255));
                    pooledByteBufferOutputStreamNewOutputStream.write(EMPTY_JPEG_SUFFIX);
                    CloseableReference<PooledByteBuffer> closeableReferenceOf = CloseableReference.of(pooledByteBufferOutputStreamNewOutputStream.toByteBuffer());
                    if (pooledByteBufferOutputStreamNewOutputStream != null) {
                        pooledByteBufferOutputStreamNewOutputStream.close();
                    }
                    return closeableReferenceOf;
                } catch (IOException e) {
                    e = e;
                    pooledByteBufferOutputStream = pooledByteBufferOutputStreamNewOutputStream;
                    throw new RuntimeException(e);
                } catch (Throwable th) {
                    th = th;
                    if (pooledByteBufferOutputStreamNewOutputStream != null) {
                        pooledByteBufferOutputStreamNewOutputStream.close();
                    }
                    throw th;
                }
            } catch (IOException e2) {
                e = e2;
            }
        } catch (Throwable th2) {
            th = th2;
            pooledByteBufferOutputStreamNewOutputStream = pooledByteBufferOutputStream;
        }
    }
}
