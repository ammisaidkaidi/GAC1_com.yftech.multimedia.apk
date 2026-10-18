package android.media;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.mtp.MtpConstants;
import android.net.wifi.WifiScanner;
import android.opengl.GLES30;
import android.os.SystemProperties;
import android.security.keymaster.KeymasterDefs;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.text.format.Time;
import android.util.Log;
import android.util.Pair;
import com.android.internal.telephony.gsm.SmsCbConstants;
import com.facebook.cache.disk.DefaultDiskStorage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import libcore.io.IoUtils;
import libcore.io.Streams;

/* JADX INFO: loaded from: classes.dex */
public class ExifInterface {
    private static final short BYTE_ALIGN_II = 18761;
    private static final short BYTE_ALIGN_MM = 19789;
    private static final int DATA_DEFLATE_ZIP = 8;
    private static final int DATA_HUFFMAN_COMPRESSED = 2;
    private static final int DATA_JPEG = 6;
    private static final int DATA_JPEG_COMPRESSED = 7;
    private static final int DATA_LOSSY_JPEG = 34892;
    private static final int DATA_PACK_BITS_COMPRESSED = 32773;
    private static final int DATA_UNCOMPRESSED = 1;
    private static final boolean DEBUG = false;
    private static final ExifTag[] EXIF_POINTER_TAGS;
    private static final ExifTag[][] EXIF_TAGS;
    private static final ExifTag[] IFD_EXIF_TAGS;
    private static final int IFD_FORMAT_BYTE = 1;
    private static final int IFD_FORMAT_DOUBLE = 12;
    private static final int IFD_FORMAT_IFD = 13;
    private static final int IFD_FORMAT_SBYTE = 6;
    private static final int IFD_FORMAT_SINGLE = 11;
    private static final int IFD_FORMAT_SLONG = 9;
    private static final int IFD_FORMAT_SRATIONAL = 10;
    private static final int IFD_FORMAT_SSHORT = 8;
    private static final int IFD_FORMAT_STRING = 2;
    private static final int IFD_FORMAT_ULONG = 4;
    private static final int IFD_FORMAT_UNDEFINED = 7;
    private static final int IFD_FORMAT_URATIONAL = 5;
    private static final int IFD_FORMAT_USHORT = 3;
    private static final ExifTag[] IFD_GPS_TAGS;
    private static final ExifTag[] IFD_INTEROPERABILITY_TAGS;
    private static final int IFD_OFFSET = 8;
    private static final ExifTag[] IFD_THUMBNAIL_TAGS;
    private static final ExifTag[] IFD_TIFF_TAGS;
    private static final int IFD_TYPE_EXIF = 1;
    private static final int IFD_TYPE_GPS = 2;
    private static final int IFD_TYPE_INTEROPERABILITY = 3;
    private static final int IFD_TYPE_ORF_CAMERA_SETTINGS = 7;
    private static final int IFD_TYPE_ORF_IMAGE_PROCESSING = 8;
    private static final int IFD_TYPE_ORF_MAKER_NOTE = 6;
    private static final int IFD_TYPE_PEF = 9;
    private static final int IFD_TYPE_PREVIEW = 5;
    private static final int IFD_TYPE_PRIMARY = 0;
    private static final int IFD_TYPE_THUMBNAIL = 4;
    private static final int IMAGE_TYPE_ARW = 1;
    private static final int IMAGE_TYPE_CR2 = 2;
    private static final int IMAGE_TYPE_DNG = 3;
    private static final int IMAGE_TYPE_HEIF = 12;
    private static final int IMAGE_TYPE_JPEG = 4;
    private static final int IMAGE_TYPE_NEF = 5;
    private static final int IMAGE_TYPE_NRW = 6;
    private static final int IMAGE_TYPE_ORF = 7;
    private static final int IMAGE_TYPE_PEF = 8;
    private static final int IMAGE_TYPE_RAF = 9;
    private static final int IMAGE_TYPE_RW2 = 10;
    private static final int IMAGE_TYPE_SRW = 11;
    private static final int IMAGE_TYPE_UNKNOWN = 0;
    private static final ExifTag JPEG_INTERCHANGE_FORMAT_LENGTH_TAG;
    private static final ExifTag JPEG_INTERCHANGE_FORMAT_TAG;
    private static final byte MARKER = -1;
    private static final byte MARKER_APP1 = -31;
    private static final byte MARKER_COM = -2;
    private static final byte MARKER_EOI = -39;
    private static final byte MARKER_SOF0 = -64;
    private static final byte MARKER_SOF1 = -63;
    private static final byte MARKER_SOF10 = -54;
    private static final byte MARKER_SOF11 = -53;
    private static final byte MARKER_SOF13 = -51;
    private static final byte MARKER_SOF14 = -50;
    private static final byte MARKER_SOF15 = -49;
    private static final byte MARKER_SOF2 = -62;
    private static final byte MARKER_SOF3 = -61;
    private static final byte MARKER_SOF5 = -59;
    private static final byte MARKER_SOF6 = -58;
    private static final byte MARKER_SOF7 = -57;
    private static final byte MARKER_SOF9 = -55;
    private static final byte MARKER_SOS = -38;
    private static final int MAX_THUMBNAIL_SIZE = 512;
    private static final ExifTag[] ORF_CAMERA_SETTINGS_TAGS;
    private static final ExifTag[] ORF_IMAGE_PROCESSING_TAGS;
    private static final int ORF_MAKER_NOTE_HEADER_1_SIZE = 8;
    private static final int ORF_MAKER_NOTE_HEADER_2_SIZE = 12;
    private static final ExifTag[] ORF_MAKER_NOTE_TAGS;
    private static final short ORF_SIGNATURE_1 = 20306;
    private static final short ORF_SIGNATURE_2 = 21330;
    public static final int ORIENTATION_FLIP_HORIZONTAL = 2;
    public static final int ORIENTATION_FLIP_VERTICAL = 4;
    public static final int ORIENTATION_NORMAL = 1;
    public static final int ORIENTATION_ROTATE_180 = 3;
    public static final int ORIENTATION_ROTATE_270 = 8;
    public static final int ORIENTATION_ROTATE_90 = 6;
    public static final int ORIENTATION_TRANSPOSE = 5;
    public static final int ORIENTATION_TRANSVERSE = 7;
    public static final int ORIENTATION_UNDEFINED = 0;
    private static final int ORIGINAL_RESOLUTION_IMAGE = 0;
    private static final int PEF_MAKER_NOTE_SKIP_SIZE = 6;
    private static final String PEF_SIGNATURE = "PENTAX";
    private static final ExifTag[] PEF_TAGS;
    private static final int PHOTOMETRIC_INTERPRETATION_BLACK_IS_ZERO = 1;
    private static final int PHOTOMETRIC_INTERPRETATION_RGB = 2;
    private static final int PHOTOMETRIC_INTERPRETATION_WHITE_IS_ZERO = 0;
    private static final int PHOTOMETRIC_INTERPRETATION_YCBCR = 6;
    private static final int RAF_INFO_SIZE = 160;
    private static final int RAF_JPEG_LENGTH_VALUE_SIZE = 4;
    private static final int RAF_OFFSET_TO_JPEG_IMAGE_OFFSET = 84;
    private static final String RAF_SIGNATURE = "FUJIFILMCCD-RAW";
    private static final int REDUCED_RESOLUTION_IMAGE = 1;
    private static final short RW2_SIGNATURE = 85;
    private static final int SIGNATURE_CHECK_SIZE = 5000;
    private static final byte START_CODE = 42;
    private static final String TAG = "ExifInterface";

    @Deprecated
    public static final String TAG_APERTURE = "FNumber";
    public static final String TAG_APERTURE_VALUE = "ApertureValue";
    public static final String TAG_ARTIST = "Artist";
    public static final String TAG_BITS_PER_SAMPLE = "BitsPerSample";
    public static final String TAG_BRIGHTNESS_VALUE = "BrightnessValue";
    public static final String TAG_CFA_PATTERN = "CFAPattern";
    public static final String TAG_COLOR_SPACE = "ColorSpace";
    public static final String TAG_COMPONENTS_CONFIGURATION = "ComponentsConfiguration";
    public static final String TAG_COMPRESSED_BITS_PER_PIXEL = "CompressedBitsPerPixel";
    public static final String TAG_COMPRESSION = "Compression";
    public static final String TAG_CONTRAST = "Contrast";
    public static final String TAG_COPYRIGHT = "Copyright";
    public static final String TAG_CUSTOM_RENDERED = "CustomRendered";
    public static final String TAG_DATETIME = "DateTime";
    public static final String TAG_DATETIME_DIGITIZED = "DateTimeDigitized";
    public static final String TAG_DATETIME_ORIGINAL = "DateTimeOriginal";
    public static final String TAG_DEFAULT_CROP_SIZE = "DefaultCropSize";
    public static final String TAG_DEVICE_SETTING_DESCRIPTION = "DeviceSettingDescription";
    public static final String TAG_DIGITAL_ZOOM_RATIO = "DigitalZoomRatio";
    public static final String TAG_DNG_VERSION = "DNGVersion";
    private static final String TAG_EXIF_IFD_POINTER = "ExifIFDPointer";
    public static final String TAG_EXIF_VERSION = "ExifVersion";
    public static final String TAG_EXPOSURE_BIAS_VALUE = "ExposureBiasValue";
    public static final String TAG_EXPOSURE_INDEX = "ExposureIndex";
    public static final String TAG_EXPOSURE_MODE = "ExposureMode";
    public static final String TAG_EXPOSURE_PROGRAM = "ExposureProgram";
    public static final String TAG_EXPOSURE_TIME = "ExposureTime";
    public static final String TAG_FILE_SOURCE = "FileSource";
    public static final String TAG_FLASH = "Flash";
    public static final String TAG_FLASHPIX_VERSION = "FlashpixVersion";
    public static final String TAG_FLASH_ENERGY = "FlashEnergy";
    public static final String TAG_FOCAL_LENGTH = "FocalLength";
    public static final String TAG_FOCAL_LENGTH_IN_35MM_FILM = "FocalLengthIn35mmFilm";
    public static final String TAG_FOCAL_PLANE_RESOLUTION_UNIT = "FocalPlaneResolutionUnit";
    public static final String TAG_FOCAL_PLANE_X_RESOLUTION = "FocalPlaneXResolution";
    public static final String TAG_FOCAL_PLANE_Y_RESOLUTION = "FocalPlaneYResolution";
    public static final String TAG_F_NUMBER = "FNumber";
    public static final String TAG_GAIN_CONTROL = "GainControl";
    public static final String TAG_GPS_ALTITUDE = "GPSAltitude";
    public static final String TAG_GPS_ALTITUDE_REF = "GPSAltitudeRef";
    public static final String TAG_GPS_AREA_INFORMATION = "GPSAreaInformation";
    public static final String TAG_GPS_DATESTAMP = "GPSDateStamp";
    public static final String TAG_GPS_DEST_BEARING = "GPSDestBearing";
    public static final String TAG_GPS_DEST_BEARING_REF = "GPSDestBearingRef";
    public static final String TAG_GPS_DEST_DISTANCE = "GPSDestDistance";
    public static final String TAG_GPS_DEST_DISTANCE_REF = "GPSDestDistanceRef";
    public static final String TAG_GPS_DEST_LATITUDE = "GPSDestLatitude";
    public static final String TAG_GPS_DEST_LATITUDE_REF = "GPSDestLatitudeRef";
    public static final String TAG_GPS_DEST_LONGITUDE = "GPSDestLongitude";
    public static final String TAG_GPS_DEST_LONGITUDE_REF = "GPSDestLongitudeRef";
    public static final String TAG_GPS_DIFFERENTIAL = "GPSDifferential";
    public static final String TAG_GPS_DOP = "GPSDOP";
    public static final String TAG_GPS_IMG_DIRECTION = "GPSImgDirection";
    public static final String TAG_GPS_IMG_DIRECTION_REF = "GPSImgDirectionRef";
    private static final String TAG_GPS_INFO_IFD_POINTER = "GPSInfoIFDPointer";
    public static final String TAG_GPS_LATITUDE = "GPSLatitude";
    public static final String TAG_GPS_LATITUDE_REF = "GPSLatitudeRef";
    public static final String TAG_GPS_LONGITUDE = "GPSLongitude";
    public static final String TAG_GPS_LONGITUDE_REF = "GPSLongitudeRef";
    public static final String TAG_GPS_MAP_DATUM = "GPSMapDatum";
    public static final String TAG_GPS_MEASURE_MODE = "GPSMeasureMode";
    public static final String TAG_GPS_PROCESSING_METHOD = "GPSProcessingMethod";
    public static final String TAG_GPS_SATELLITES = "GPSSatellites";
    public static final String TAG_GPS_SPEED = "GPSSpeed";
    public static final String TAG_GPS_SPEED_REF = "GPSSpeedRef";
    public static final String TAG_GPS_STATUS = "GPSStatus";
    public static final String TAG_GPS_TIMESTAMP = "GPSTimeStamp";
    public static final String TAG_GPS_TRACK = "GPSTrack";
    public static final String TAG_GPS_TRACK_REF = "GPSTrackRef";
    public static final String TAG_GPS_VERSION_ID = "GPSVersionID";
    private static final String TAG_HAS_THUMBNAIL = "HasThumbnail";
    public static final String TAG_IMAGE_DESCRIPTION = "ImageDescription";
    public static final String TAG_IMAGE_LENGTH = "ImageLength";
    public static final String TAG_IMAGE_UNIQUE_ID = "ImageUniqueID";
    public static final String TAG_IMAGE_WIDTH = "ImageWidth";
    private static final String TAG_INTEROPERABILITY_IFD_POINTER = "InteroperabilityIFDPointer";
    public static final String TAG_INTEROPERABILITY_INDEX = "InteroperabilityIndex";

    @Deprecated
    public static final String TAG_ISO = "ISOSpeedRatings";
    public static final String TAG_ISO_SPEED_RATINGS = "ISOSpeedRatings";
    public static final String TAG_JPEG_INTERCHANGE_FORMAT = "JPEGInterchangeFormat";
    public static final String TAG_JPEG_INTERCHANGE_FORMAT_LENGTH = "JPEGInterchangeFormatLength";
    public static final String TAG_LIGHT_SOURCE = "LightSource";
    public static final String TAG_MAKE = "Make";
    public static final String TAG_MAKER_NOTE = "MakerNote";
    public static final String TAG_MAX_APERTURE_VALUE = "MaxApertureValue";
    public static final String TAG_METERING_MODE = "MeteringMode";
    public static final String TAG_MODEL = "Model";
    public static final String TAG_NEW_SUBFILE_TYPE = "NewSubfileType";
    public static final String TAG_OECF = "OECF";
    public static final String TAG_ORF_ASPECT_FRAME = "AspectFrame";
    private static final String TAG_ORF_CAMERA_SETTINGS_IFD_POINTER = "CameraSettingsIFDPointer";
    private static final String TAG_ORF_IMAGE_PROCESSING_IFD_POINTER = "ImageProcessingIFDPointer";
    public static final String TAG_ORF_PREVIEW_IMAGE_LENGTH = "PreviewImageLength";
    public static final String TAG_ORF_PREVIEW_IMAGE_START = "PreviewImageStart";
    public static final String TAG_ORF_THUMBNAIL_IMAGE = "ThumbnailImage";
    public static final String TAG_ORIENTATION = "Orientation";
    public static final String TAG_PHOTOMETRIC_INTERPRETATION = "PhotometricInterpretation";
    public static final String TAG_PIXEL_X_DIMENSION = "PixelXDimension";
    public static final String TAG_PIXEL_Y_DIMENSION = "PixelYDimension";
    public static final String TAG_PLANAR_CONFIGURATION = "PlanarConfiguration";
    public static final String TAG_PRIMARY_CHROMATICITIES = "PrimaryChromaticities";
    private static final ExifTag TAG_RAF_IMAGE_SIZE;
    public static final String TAG_REFERENCE_BLACK_WHITE = "ReferenceBlackWhite";
    public static final String TAG_RELATED_SOUND_FILE = "RelatedSoundFile";
    public static final String TAG_RESOLUTION_UNIT = "ResolutionUnit";
    public static final String TAG_ROWS_PER_STRIP = "RowsPerStrip";
    public static final String TAG_RW2_ISO = "ISO";
    public static final String TAG_RW2_JPG_FROM_RAW = "JpgFromRaw";
    public static final String TAG_RW2_SENSOR_BOTTOM_BORDER = "SensorBottomBorder";
    public static final String TAG_RW2_SENSOR_LEFT_BORDER = "SensorLeftBorder";
    public static final String TAG_RW2_SENSOR_RIGHT_BORDER = "SensorRightBorder";
    public static final String TAG_RW2_SENSOR_TOP_BORDER = "SensorTopBorder";
    public static final String TAG_SAMPLES_PER_PIXEL = "SamplesPerPixel";
    public static final String TAG_SATURATION = "Saturation";
    public static final String TAG_SCENE_CAPTURE_TYPE = "SceneCaptureType";
    public static final String TAG_SCENE_TYPE = "SceneType";
    public static final String TAG_SENSING_METHOD = "SensingMethod";
    public static final String TAG_SHARPNESS = "Sharpness";
    public static final String TAG_SHUTTER_SPEED_VALUE = "ShutterSpeedValue";
    public static final String TAG_SOFTWARE = "Software";
    public static final String TAG_SPATIAL_FREQUENCY_RESPONSE = "SpatialFrequencyResponse";
    public static final String TAG_SPECTRAL_SENSITIVITY = "SpectralSensitivity";
    public static final String TAG_STRIP_BYTE_COUNTS = "StripByteCounts";
    public static final String TAG_STRIP_OFFSETS = "StripOffsets";
    public static final String TAG_SUBFILE_TYPE = "SubfileType";
    public static final String TAG_SUBJECT_AREA = "SubjectArea";
    public static final String TAG_SUBJECT_DISTANCE = "SubjectDistance";
    public static final String TAG_SUBJECT_DISTANCE_RANGE = "SubjectDistanceRange";
    public static final String TAG_SUBJECT_LOCATION = "SubjectLocation";
    public static final String TAG_SUBSEC_TIME = "SubSecTime";
    public static final String TAG_SUBSEC_TIME_DIG = "SubSecTimeDigitized";
    public static final String TAG_SUBSEC_TIME_DIGITIZED = "SubSecTimeDigitized";
    public static final String TAG_SUBSEC_TIME_ORIG = "SubSecTimeOriginal";
    public static final String TAG_SUBSEC_TIME_ORIGINAL = "SubSecTimeOriginal";
    private static final String TAG_SUB_IFD_POINTER = "SubIFDPointer";
    private static final String TAG_THUMBNAIL_DATA = "ThumbnailData";
    public static final String TAG_THUMBNAIL_IMAGE_LENGTH = "ThumbnailImageLength";
    public static final String TAG_THUMBNAIL_IMAGE_WIDTH = "ThumbnailImageWidth";
    private static final String TAG_THUMBNAIL_LENGTH = "ThumbnailLength";
    private static final String TAG_THUMBNAIL_OFFSET = "ThumbnailOffset";
    public static final String TAG_TRANSFER_FUNCTION = "TransferFunction";
    public static final String TAG_USER_COMMENT = "UserComment";
    public static final String TAG_WHITE_BALANCE = "WhiteBalance";
    public static final String TAG_WHITE_POINT = "WhitePoint";
    public static final String TAG_X_RESOLUTION = "XResolution";
    public static final String TAG_Y_CB_CR_COEFFICIENTS = "YCbCrCoefficients";
    public static final String TAG_Y_CB_CR_POSITIONING = "YCbCrPositioning";
    public static final String TAG_Y_CB_CR_SUB_SAMPLING = "YCbCrSubSampling";
    public static final String TAG_Y_RESOLUTION = "YResolution";
    public static final int WHITEBALANCE_AUTO = 0;
    public static final int WHITEBALANCE_MANUAL = 1;
    private static final HashMap[] sExifTagMapsForReading;
    private static final HashMap[] sExifTagMapsForWriting;
    private static final Pattern sGpsTimestampPattern;
    private static final Pattern sNonZeroTimePattern;
    private final AssetManager.AssetInputStream mAssetInputStream;
    private final HashMap[] mAttributes;
    private Set<Integer> mAttributesOffsets;
    private ByteOrder mExifByteOrder;
    private int mExifOffset;
    private final String mFilename;
    private boolean mHasThumbnail;
    private final boolean mIsInputStream;
    private boolean mIsSupportedFile;
    private int mMimeType;
    private int mOrfMakerNoteOffset;
    private int mOrfThumbnailLength;
    private int mOrfThumbnailOffset;
    private int mRw2JpgFromRawOffset;
    private final FileDescriptor mSeekableFileDescriptor;
    private byte[] mThumbnailBytes;
    private int mThumbnailCompression;
    private int mThumbnailLength;
    private int mThumbnailOffset;
    private static boolean ATC_AOSP_ENHANCEMENT_AUDIO = SystemProperties.getBoolean("ro.atc.aosp_enhancement_audio", false);
    private static final byte MARKER_SOI = -40;
    private static final byte[] JPEG_SIGNATURE = {-1, MARKER_SOI, -1};
    private static final byte[] HEIF_TYPE_FTYP = {102, 116, 121, 112};
    private static final byte[] HEIF_BRAND_MIF1 = {109, 105, 102, 49};
    private static final byte[] HEIF_BRAND_HEIC = {104, 101, 105, 99};
    private static final byte[] ORF_MAKER_NOTE_HEADER_1 = {79, 76, 89, 77, 80, 0};
    private static final byte[] ORF_MAKER_NOTE_HEADER_2 = {79, 76, 89, 77, 80, 85, 83, 0, 73, 73};
    private static SimpleDateFormat sFormatter = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
    private static final String[] IFD_FORMAT_NAMES = {"", "BYTE", "STRING", "USHORT", "ULONG", "URATIONAL", "SBYTE", "UNDEFINED", "SSHORT", "SLONG", "SRATIONAL", "SINGLE", "DOUBLE"};
    private static final int[] IFD_FORMAT_BYTES_PER_FORMAT = {0, 1, 1, 2, 4, 8, 1, 1, 2, 4, 8, 4, 8, 1};
    private static final byte[] EXIF_ASCII_PREFIX = {65, 83, 67, 73, 73, 0, 0, 0};
    private static final int[] BITS_PER_SAMPLE_RGB = {8, 8, 8};
    private static final int[] BITS_PER_SAMPLE_GREYSCALE_1 = {4};
    private static final int[] BITS_PER_SAMPLE_GREYSCALE_2 = {8};
    private static final HashSet<String> sTagSetForCompatibility = new HashSet<>(Arrays.asList("FNumber", "DigitalZoomRatio", "ExposureTime", "SubjectDistance", "GPSTimeStamp"));
    private static final HashMap<Integer, Integer> sExifPointerTagMap = new HashMap<>();
    private static final Charset ASCII = Charset.forName("US-ASCII");
    private static final byte[] IDENTIFIER_EXIF_APP1 = "Exif\u0000\u0000".getBytes(ASCII);

    private static class ByteOrderedDataInputStream extends InputStream implements DataInput {
        private ByteOrder mByteOrder;
        private DataInputStream mDataInputStream;
        private InputStream mInputStream;
        private final int mLength;
        private int mPosition;
        private static final ByteOrder LITTLE_ENDIAN = ByteOrder.LITTLE_ENDIAN;
        private static final ByteOrder BIG_ENDIAN = ByteOrder.BIG_ENDIAN;

        public ByteOrderedDataInputStream(InputStream inputStream) throws IOException {
            this.mByteOrder = ByteOrder.BIG_ENDIAN;
            this.mInputStream = inputStream;
            this.mDataInputStream = new DataInputStream(inputStream);
            this.mLength = this.mDataInputStream.available();
            this.mPosition = 0;
            this.mDataInputStream.mark(this.mLength);
        }

        public ByteOrderedDataInputStream(byte[] bArr) throws IOException {
            this(new ByteArrayInputStream(bArr));
        }

        @Override // java.io.InputStream
        public int available() throws IOException {
            return this.mDataInputStream.available();
        }

        public int peek() {
            return this.mPosition;
        }

        @Override // java.io.InputStream
        public int read() throws IOException {
            this.mPosition++;
            return this.mDataInputStream.read();
        }

        @Override // java.io.DataInput
        public boolean readBoolean() throws IOException {
            this.mPosition++;
            return this.mDataInputStream.readBoolean();
        }

        @Override // java.io.DataInput
        public byte readByte() throws IOException {
            this.mPosition++;
            if (this.mPosition > this.mLength) {
                throw new EOFException();
            }
            int i = this.mDataInputStream.read();
            if (i >= 0) {
                return (byte) i;
            }
            throw new EOFException();
        }

        @Override // java.io.DataInput
        public char readChar() throws IOException {
            this.mPosition += 2;
            return this.mDataInputStream.readChar();
        }

        @Override // java.io.DataInput
        public double readDouble() throws IOException {
            return Double.longBitsToDouble(readLong());
        }

        @Override // java.io.DataInput
        public float readFloat() throws IOException {
            return Float.intBitsToFloat(readInt());
        }

        @Override // java.io.DataInput
        public void readFully(byte[] bArr) throws IOException {
            this.mPosition += bArr.length;
            if (this.mPosition > this.mLength) {
                throw new EOFException();
            }
            if (this.mDataInputStream.read(bArr, 0, bArr.length) != bArr.length) {
                throw new IOException("Couldn't read up to the length of buffer");
            }
        }

        @Override // java.io.DataInput
        public void readFully(byte[] bArr, int i, int i2) throws IOException {
            this.mPosition += i2;
            if (this.mPosition > this.mLength) {
                throw new EOFException();
            }
            if (this.mDataInputStream.read(bArr, i, i2) != i2) {
                throw new IOException("Couldn't read up to the length of buffer");
            }
        }

        @Override // java.io.DataInput
        public int readInt() throws IOException {
            this.mPosition += 4;
            if (this.mPosition > this.mLength) {
                throw new EOFException();
            }
            int i = this.mDataInputStream.read();
            int i2 = this.mDataInputStream.read();
            int i3 = this.mDataInputStream.read();
            int i4 = this.mDataInputStream.read();
            if ((i | i2 | i3 | i4) < 0) {
                throw new EOFException();
            }
            if (this.mByteOrder == LITTLE_ENDIAN) {
                return (i4 << 24) + (i3 << 16) + (i2 << 8) + i;
            }
            if (this.mByteOrder == BIG_ENDIAN) {
                return (i << 24) + (i2 << 16) + (i3 << 8) + i4;
            }
            throw new IOException("Invalid byte order: " + this.mByteOrder);
        }

        @Override // java.io.DataInput
        public String readLine() throws IOException {
            Log.d(ExifInterface.TAG, "Currently unsupported");
            return null;
        }

        @Override // java.io.DataInput
        public long readLong() throws IOException {
            this.mPosition += 8;
            if (this.mPosition > this.mLength) {
                throw new EOFException();
            }
            int i = this.mDataInputStream.read();
            int i2 = this.mDataInputStream.read();
            int i3 = this.mDataInputStream.read();
            int i4 = this.mDataInputStream.read();
            int i5 = this.mDataInputStream.read();
            int i6 = this.mDataInputStream.read();
            int i7 = this.mDataInputStream.read();
            int i8 = this.mDataInputStream.read();
            if ((i | i2 | i3 | i4 | i5 | i6 | i7 | i8) < 0) {
                throw new EOFException();
            }
            if (this.mByteOrder == LITTLE_ENDIAN) {
                return (((long) i8) << 56) + (((long) i7) << 48) + (((long) i6) << 40) + (((long) i5) << 32) + (((long) i4) << 24) + (((long) i3) << 16) + (((long) i2) << 8) + ((long) i);
            }
            if (this.mByteOrder == BIG_ENDIAN) {
                return (((long) i) << 56) + (((long) i2) << 48) + (((long) i3) << 40) + (((long) i4) << 32) + (((long) i5) << 24) + (((long) i6) << 16) + (((long) i7) << 8) + ((long) i8);
            }
            throw new IOException("Invalid byte order: " + this.mByteOrder);
        }

        @Override // java.io.DataInput
        public short readShort() throws IOException {
            this.mPosition += 2;
            if (this.mPosition > this.mLength) {
                throw new EOFException();
            }
            int i = this.mDataInputStream.read();
            int i2 = this.mDataInputStream.read();
            if ((i | i2) < 0) {
                throw new EOFException();
            }
            if (this.mByteOrder == LITTLE_ENDIAN) {
                return (short) ((i2 << 8) + i);
            }
            if (this.mByteOrder == BIG_ENDIAN) {
                return (short) ((i << 8) + i2);
            }
            throw new IOException("Invalid byte order: " + this.mByteOrder);
        }

        @Override // java.io.DataInput
        public String readUTF() throws IOException {
            this.mPosition += 2;
            return this.mDataInputStream.readUTF();
        }

        @Override // java.io.DataInput
        public int readUnsignedByte() throws IOException {
            this.mPosition++;
            return this.mDataInputStream.readUnsignedByte();
        }

        public long readUnsignedInt() throws IOException {
            return ((long) readInt()) & 4294967295L;
        }

        @Override // java.io.DataInput
        public int readUnsignedShort() throws IOException {
            this.mPosition += 2;
            if (this.mPosition > this.mLength) {
                throw new EOFException();
            }
            int i = this.mDataInputStream.read();
            int i2 = this.mDataInputStream.read();
            if ((i | i2) < 0) {
                throw new EOFException();
            }
            if (this.mByteOrder == LITTLE_ENDIAN) {
                return (i2 << 8) + i;
            }
            if (this.mByteOrder == BIG_ENDIAN) {
                return (i << 8) + i2;
            }
            throw new IOException("Invalid byte order: " + this.mByteOrder);
        }

        public void seek(long j) throws IOException {
            if (this.mPosition > j) {
                this.mPosition = 0;
                this.mDataInputStream.reset();
                this.mDataInputStream.mark(this.mLength);
            } else {
                j -= (long) this.mPosition;
            }
            int i = (int) j;
            if (skipBytes(i) != i) {
                throw new IOException("Couldn't seek up to the byteCount");
            }
        }

        public void setByteOrder(ByteOrder byteOrder) {
            this.mByteOrder = byteOrder;
        }

        @Override // java.io.DataInput
        public int skipBytes(int i) throws IOException {
            int iMin = Math.min(i, this.mLength - this.mPosition);
            int iSkipBytes = 0;
            while (iSkipBytes < iMin) {
                iSkipBytes += this.mDataInputStream.skipBytes(iMin - iSkipBytes);
            }
            this.mPosition += iSkipBytes;
            return iSkipBytes;
        }
    }

    private static class ByteOrderedDataOutputStream extends FilterOutputStream {
        private ByteOrder mByteOrder;
        private final OutputStream mOutputStream;

        public ByteOrderedDataOutputStream(OutputStream outputStream, ByteOrder byteOrder) {
            super(outputStream);
            this.mOutputStream = outputStream;
            this.mByteOrder = byteOrder;
        }

        public void setByteOrder(ByteOrder byteOrder) {
            this.mByteOrder = byteOrder;
        }

        @Override // java.io.FilterOutputStream, java.io.OutputStream
        public void write(byte[] bArr) throws IOException {
            this.mOutputStream.write(bArr);
        }

        @Override // java.io.FilterOutputStream, java.io.OutputStream
        public void write(byte[] bArr, int i, int i2) throws IOException {
            this.mOutputStream.write(bArr, i, i2);
        }

        public void writeByte(int i) throws IOException {
            this.mOutputStream.write(i);
        }

        public void writeInt(int i) throws IOException {
            if (this.mByteOrder == ByteOrder.LITTLE_ENDIAN) {
                this.mOutputStream.write((i >>> 0) & 255);
                this.mOutputStream.write((i >>> 8) & 255);
                this.mOutputStream.write((i >>> 16) & 255);
                this.mOutputStream.write((i >>> 24) & 255);
                return;
            }
            if (this.mByteOrder == ByteOrder.BIG_ENDIAN) {
                this.mOutputStream.write((i >>> 24) & 255);
                this.mOutputStream.write((i >>> 16) & 255);
                this.mOutputStream.write((i >>> 8) & 255);
                this.mOutputStream.write((i >>> 0) & 255);
            }
        }

        public void writeShort(short s) throws IOException {
            if (this.mByteOrder == ByteOrder.LITTLE_ENDIAN) {
                this.mOutputStream.write((s >>> 0) & 255);
                this.mOutputStream.write((s >>> 8) & 255);
            } else if (this.mByteOrder == ByteOrder.BIG_ENDIAN) {
                this.mOutputStream.write((s >>> 8) & 255);
                this.mOutputStream.write((s >>> 0) & 255);
            }
        }

        public void writeUnsignedInt(long j) throws IOException {
            writeInt((int) j);
        }

        public void writeUnsignedShort(int i) throws IOException {
            writeShort((short) i);
        }
    }

    private static class ExifAttribute {
        public final byte[] bytes;
        public final int format;
        public final int numberOfComponents;

        private ExifAttribute(int i, int i2, byte[] bArr) {
            this.format = i;
            this.numberOfComponents = i2;
            this.bytes = bArr;
        }

        public static ExifAttribute createByte(String str) {
            if (str.length() != 1 || str.charAt(0) < '0' || str.charAt(0) > '1') {
                byte[] bytes = str.getBytes(ExifInterface.ASCII);
                return new ExifAttribute(1, bytes.length, bytes);
            }
            byte[] bArr = {(byte) (str.charAt(0) - '0')};
            return new ExifAttribute(1, bArr.length, bArr);
        }

        public static ExifAttribute createDouble(double d, ByteOrder byteOrder) {
            return createDouble(new double[]{d}, byteOrder);
        }

        public static ExifAttribute createDouble(double[] dArr, ByteOrder byteOrder) {
            ByteBuffer byteBufferWrap = ByteBuffer.wrap(new byte[ExifInterface.IFD_FORMAT_BYTES_PER_FORMAT[12] * dArr.length]);
            byteBufferWrap.order(byteOrder);
            for (double d : dArr) {
                byteBufferWrap.putDouble(d);
            }
            return new ExifAttribute(12, dArr.length, byteBufferWrap.array());
        }

        public static ExifAttribute createSLong(int i, ByteOrder byteOrder) {
            return createSLong(new int[]{i}, byteOrder);
        }

        public static ExifAttribute createSLong(int[] iArr, ByteOrder byteOrder) {
            ByteBuffer byteBufferWrap = ByteBuffer.wrap(new byte[ExifInterface.IFD_FORMAT_BYTES_PER_FORMAT[9] * iArr.length]);
            byteBufferWrap.order(byteOrder);
            for (int i : iArr) {
                byteBufferWrap.putInt(i);
            }
            return new ExifAttribute(9, iArr.length, byteBufferWrap.array());
        }

        public static ExifAttribute createSRational(Rational rational, ByteOrder byteOrder) {
            return createSRational(new Rational[]{rational}, byteOrder);
        }

        public static ExifAttribute createSRational(Rational[] rationalArr, ByteOrder byteOrder) {
            ByteBuffer byteBufferWrap = ByteBuffer.wrap(new byte[ExifInterface.IFD_FORMAT_BYTES_PER_FORMAT[10] * rationalArr.length]);
            byteBufferWrap.order(byteOrder);
            for (Rational rational : rationalArr) {
                byteBufferWrap.putInt((int) rational.numerator);
                byteBufferWrap.putInt((int) rational.denominator);
            }
            return new ExifAttribute(10, rationalArr.length, byteBufferWrap.array());
        }

        public static ExifAttribute createString(String str) {
            byte[] bytes = (str + (char) 0).getBytes(ExifInterface.ASCII);
            return new ExifAttribute(2, bytes.length, bytes);
        }

        public static ExifAttribute createULong(long j, ByteOrder byteOrder) {
            return createULong(new long[]{j}, byteOrder);
        }

        public static ExifAttribute createULong(long[] jArr, ByteOrder byteOrder) {
            ByteBuffer byteBufferWrap = ByteBuffer.wrap(new byte[ExifInterface.IFD_FORMAT_BYTES_PER_FORMAT[4] * jArr.length]);
            byteBufferWrap.order(byteOrder);
            for (long j : jArr) {
                byteBufferWrap.putInt((int) j);
            }
            return new ExifAttribute(4, jArr.length, byteBufferWrap.array());
        }

        public static ExifAttribute createURational(Rational rational, ByteOrder byteOrder) {
            return createURational(new Rational[]{rational}, byteOrder);
        }

        public static ExifAttribute createURational(Rational[] rationalArr, ByteOrder byteOrder) {
            ByteBuffer byteBufferWrap = ByteBuffer.wrap(new byte[ExifInterface.IFD_FORMAT_BYTES_PER_FORMAT[5] * rationalArr.length]);
            byteBufferWrap.order(byteOrder);
            for (Rational rational : rationalArr) {
                byteBufferWrap.putInt((int) rational.numerator);
                byteBufferWrap.putInt((int) rational.denominator);
            }
            return new ExifAttribute(5, rationalArr.length, byteBufferWrap.array());
        }

        public static ExifAttribute createUShort(int i, ByteOrder byteOrder) {
            return createUShort(new int[]{i}, byteOrder);
        }

        public static ExifAttribute createUShort(int[] iArr, ByteOrder byteOrder) {
            ByteBuffer byteBufferWrap = ByteBuffer.wrap(new byte[ExifInterface.IFD_FORMAT_BYTES_PER_FORMAT[3] * iArr.length]);
            byteBufferWrap.order(byteOrder);
            for (int i : iArr) {
                byteBufferWrap.putShort((short) i);
            }
            return new ExifAttribute(3, iArr.length, byteBufferWrap.array());
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* JADX WARN: Code duplicated, block: B:171:0x01eb A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Code duplicated, block: B:97:0x0141 A[Catch: IOException -> 0x01cb, all -> 0x01e8, TryCatch #6 {all -> 0x01e8, blocks: (B:4:0x0008, B:5:0x000f, B:7:0x0014, B:8:0x0018, B:10:0x001c, B:17:0x0032, B:18:0x0036, B:20:0x003a, B:27:0x0051, B:28:0x0055, B:30:0x0059, B:37:0x007c, B:38:0x0080, B:40:0x0084, B:47:0x009a, B:48:0x009e, B:50:0x00a2, B:57:0x00b8, B:58:0x00bc, B:60:0x00c0, B:67:0x00e1, B:68:0x00e5, B:70:0x00e9, B:77:0x00ff, B:78:0x0103, B:80:0x0107, B:87:0x011d, B:90:0x0127, B:92:0x012e, B:95:0x013c, B:97:0x0141, B:98:0x0146, B:99:0x014b, B:101:0x014f, B:106:0x015a, B:108:0x0164, B:107:0x015f, B:109:0x0167, B:115:0x0178, B:117:0x017d, B:119:0x0183, B:121:0x0189, B:127:0x01a6, B:144:0x01d2), top: B:170:0x0001 }] */
        public Object getValue(ByteOrder byteOrder) throws Throwable {
            ByteOrderedDataInputStream byteOrderedDataInputStream;
            byte b;
            ByteOrderedDataInputStream byteOrderedDataInputStream2 = null;
            try {
                try {
                    byteOrderedDataInputStream = new ByteOrderedDataInputStream(this.bytes);
                    try {
                        byteOrderedDataInputStream.setByteOrder(byteOrder);
                        boolean z = true;
                        int length = 0;
                        switch (this.format) {
                            case 1:
                            case 6:
                                if (this.bytes.length != 1 || this.bytes[0] < 0 || this.bytes[0] > 1) {
                                    String str = new String(this.bytes, ExifInterface.ASCII);
                                    try {
                                        byteOrderedDataInputStream.close();
                                        break;
                                    } catch (IOException e) {
                                        Log.w(ExifInterface.TAG, "IOException occurred during close stream", e);
                                    }
                                    return str;
                                }
                                String str2 = new String(new char[]{(char) (this.bytes[0] + 48)});
                                try {
                                    byteOrderedDataInputStream.close();
                                    break;
                                } catch (IOException e2) {
                                    Log.w(ExifInterface.TAG, "IOException occurred during close stream", e2);
                                }
                                return str2;
                            case 2:
                            case 7:
                                if (this.numberOfComponents >= ExifInterface.EXIF_ASCII_PREFIX.length) {
                                    for (int i = 0; i < ExifInterface.EXIF_ASCII_PREFIX.length; i++) {
                                        if (this.bytes[i] != ExifInterface.EXIF_ASCII_PREFIX[i]) {
                                            z = false;
                                            if (z) {
                                                length = ExifInterface.EXIF_ASCII_PREFIX.length;
                                            }
                                        }
                                    }
                                    if (z) {
                                        length = ExifInterface.EXIF_ASCII_PREFIX.length;
                                    }
                                }
                                StringBuilder sb = new StringBuilder();
                                while (length < this.numberOfComponents && (b = this.bytes[length]) != 0) {
                                    if (b >= 32) {
                                        sb.append((char) b);
                                    } else {
                                        sb.append('?');
                                    }
                                    length++;
                                }
                                String string = sb.toString();
                                try {
                                    byteOrderedDataInputStream.close();
                                    break;
                                } catch (IOException e3) {
                                    Log.w(ExifInterface.TAG, "IOException occurred during close stream", e3);
                                }
                                return string;
                            case 3:
                                int[] iArr = new int[this.numberOfComponents];
                                while (length < this.numberOfComponents) {
                                    iArr[length] = byteOrderedDataInputStream.readUnsignedShort();
                                    length++;
                                }
                                try {
                                    byteOrderedDataInputStream.close();
                                    break;
                                } catch (IOException e4) {
                                    Log.w(ExifInterface.TAG, "IOException occurred during close stream", e4);
                                }
                                return iArr;
                            case 4:
                                long[] jArr = new long[this.numberOfComponents];
                                while (length < this.numberOfComponents) {
                                    jArr[length] = byteOrderedDataInputStream.readUnsignedInt();
                                    length++;
                                }
                                try {
                                    byteOrderedDataInputStream.close();
                                    break;
                                } catch (IOException e5) {
                                    Log.w(ExifInterface.TAG, "IOException occurred during close stream", e5);
                                }
                                return jArr;
                            case 5:
                                Rational[] rationalArr = new Rational[this.numberOfComponents];
                                while (length < this.numberOfComponents) {
                                    rationalArr[length] = new Rational(byteOrderedDataInputStream.readUnsignedInt(), byteOrderedDataInputStream.readUnsignedInt());
                                    length++;
                                }
                                try {
                                    byteOrderedDataInputStream.close();
                                    break;
                                } catch (IOException e6) {
                                    Log.w(ExifInterface.TAG, "IOException occurred during close stream", e6);
                                }
                                return rationalArr;
                            case 8:
                                int[] iArr2 = new int[this.numberOfComponents];
                                while (length < this.numberOfComponents) {
                                    iArr2[length] = byteOrderedDataInputStream.readShort();
                                    length++;
                                }
                                try {
                                    byteOrderedDataInputStream.close();
                                    break;
                                } catch (IOException e7) {
                                    Log.w(ExifInterface.TAG, "IOException occurred during close stream", e7);
                                }
                                return iArr2;
                            case 9:
                                int[] iArr3 = new int[this.numberOfComponents];
                                while (length < this.numberOfComponents) {
                                    iArr3[length] = byteOrderedDataInputStream.readInt();
                                    length++;
                                }
                                try {
                                    byteOrderedDataInputStream.close();
                                    break;
                                } catch (IOException e8) {
                                    Log.w(ExifInterface.TAG, "IOException occurred during close stream", e8);
                                }
                                return iArr3;
                            case 10:
                                Rational[] rationalArr2 = new Rational[this.numberOfComponents];
                                while (length < this.numberOfComponents) {
                                    rationalArr2[length] = new Rational(byteOrderedDataInputStream.readInt(), byteOrderedDataInputStream.readInt());
                                    length++;
                                }
                                try {
                                    byteOrderedDataInputStream.close();
                                    break;
                                } catch (IOException e9) {
                                    Log.w(ExifInterface.TAG, "IOException occurred during close stream", e9);
                                }
                                return rationalArr2;
                            case 11:
                                double[] dArr = new double[this.numberOfComponents];
                                while (length < this.numberOfComponents) {
                                    dArr[length] = byteOrderedDataInputStream.readFloat();
                                    length++;
                                }
                                try {
                                    byteOrderedDataInputStream.close();
                                    break;
                                } catch (IOException e10) {
                                    Log.w(ExifInterface.TAG, "IOException occurred during close stream", e10);
                                }
                                return dArr;
                            case 12:
                                double[] dArr2 = new double[this.numberOfComponents];
                                while (length < this.numberOfComponents) {
                                    dArr2[length] = byteOrderedDataInputStream.readDouble();
                                    length++;
                                }
                                try {
                                    byteOrderedDataInputStream.close();
                                    break;
                                } catch (IOException e11) {
                                    Log.w(ExifInterface.TAG, "IOException occurred during close stream", e11);
                                }
                                return dArr2;
                            default:
                                try {
                                    byteOrderedDataInputStream.close();
                                    break;
                                } catch (IOException e12) {
                                    Log.w(ExifInterface.TAG, "IOException occurred during close stream", e12);
                                }
                                return null;
                        }
                    } catch (IOException e13) {
                        e = e13;
                        Log.w(ExifInterface.TAG, "IOException occurred during reading a value", e);
                        if (byteOrderedDataInputStream != null) {
                            try {
                                byteOrderedDataInputStream.close();
                            } catch (IOException e14) {
                                Log.w(ExifInterface.TAG, "IOException occurred during close stream", e14);
                            }
                        }
                        return null;
                    }
                } catch (Throwable th) {
                    th = th;
                    if (0 != 0) {
                        try {
                            byteOrderedDataInputStream2.close();
                        } catch (IOException e15) {
                            Log.w(ExifInterface.TAG, "IOException occurred during close stream", e15);
                        }
                    }
                    throw th;
                }
            } catch (IOException e16) {
                e = e16;
                byteOrderedDataInputStream = null;
            } catch (Throwable th2) {
                th = th2;
                if (0 != 0) {
                    byteOrderedDataInputStream2.close();
                }
                throw th;
            }
        }

        public double getDoubleValue(ByteOrder byteOrder) throws Throwable {
            Object value = getValue(byteOrder);
            if (value == null) {
                throw new NumberFormatException("NULL can't be converted to a double value");
            }
            if (value instanceof String) {
                return Double.parseDouble((String) value);
            }
            if (value instanceof long[]) {
                long[] jArr = (long[]) value;
                if (jArr.length == 1) {
                    return jArr[0];
                }
                throw new NumberFormatException("There are more than one component");
            }
            if (value instanceof int[]) {
                int[] iArr = (int[]) value;
                if (iArr.length == 1) {
                    return iArr[0];
                }
                throw new NumberFormatException("There are more than one component");
            }
            if (value instanceof double[]) {
                double[] dArr = (double[]) value;
                if (dArr.length == 1) {
                    return dArr[0];
                }
                throw new NumberFormatException("There are more than one component");
            }
            if (!(value instanceof Rational[])) {
                throw new NumberFormatException("Couldn't find a double value");
            }
            Rational[] rationalArr = (Rational[]) value;
            if (rationalArr.length == 1) {
                return rationalArr[0].calculate();
            }
            throw new NumberFormatException("There are more than one component");
        }

        public int getIntValue(ByteOrder byteOrder) throws Throwable {
            Object value = getValue(byteOrder);
            if (value == null) {
                throw new NumberFormatException("NULL can't be converted to a integer value");
            }
            if (value instanceof String) {
                return Integer.parseInt((String) value);
            }
            if (value instanceof long[]) {
                long[] jArr = (long[]) value;
                if (jArr.length == 1) {
                    return (int) jArr[0];
                }
                throw new NumberFormatException("There are more than one component");
            }
            if (!(value instanceof int[])) {
                throw new NumberFormatException("Couldn't find a integer value");
            }
            int[] iArr = (int[]) value;
            if (iArr.length == 1) {
                return iArr[0];
            }
            throw new NumberFormatException("There are more than one component");
        }

        public String getStringValue(ByteOrder byteOrder) throws Throwable {
            Object value = getValue(byteOrder);
            if (value == null) {
                return null;
            }
            if (value instanceof String) {
                return (String) value;
            }
            StringBuilder sb = new StringBuilder();
            int i = 0;
            if (value instanceof long[]) {
                long[] jArr = (long[]) value;
                while (i < jArr.length) {
                    sb.append(jArr[i]);
                    i++;
                    if (i != jArr.length) {
                        sb.append(",");
                    }
                }
                return sb.toString();
            }
            if (value instanceof int[]) {
                int[] iArr = (int[]) value;
                while (i < iArr.length) {
                    sb.append(iArr[i]);
                    i++;
                    if (i != iArr.length) {
                        sb.append(",");
                    }
                }
                return sb.toString();
            }
            if (value instanceof double[]) {
                double[] dArr = (double[]) value;
                while (i < dArr.length) {
                    sb.append(dArr[i]);
                    i++;
                    if (i != dArr.length) {
                        sb.append(",");
                    }
                }
                return sb.toString();
            }
            if (!(value instanceof Rational[])) {
                return null;
            }
            Rational[] rationalArr = (Rational[]) value;
            while (i < rationalArr.length) {
                sb.append(rationalArr[i].numerator);
                sb.append('/');
                sb.append(rationalArr[i].denominator);
                i++;
                if (i != rationalArr.length) {
                    sb.append(",");
                }
            }
            return sb.toString();
        }

        public int size() {
            return ExifInterface.IFD_FORMAT_BYTES_PER_FORMAT[this.format] * this.numberOfComponents;
        }

        public String toString() {
            return "(" + ExifInterface.IFD_FORMAT_NAMES[this.format] + ", data length:" + this.bytes.length + ")";
        }
    }

    private static class ExifTag {
        public final String name;
        public final int number;
        public final int primaryFormat;
        public final int secondaryFormat;

        private ExifTag(String str, int i, int i2) {
            this.name = str;
            this.number = i;
            this.primaryFormat = i2;
            this.secondaryFormat = -1;
        }

        private ExifTag(String str, int i, int i2, int i3) {
            this.name = str;
            this.number = i;
            this.primaryFormat = i2;
            this.secondaryFormat = i3;
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface IfdType {
    }

    private static class Rational {
        public final long denominator;
        public final long numerator;

        private Rational(long j, long j2) {
            if (j2 == 0) {
                this.numerator = 0L;
                this.denominator = 1L;
            } else {
                this.numerator = j;
                this.denominator = j2;
            }
        }

        public double calculate() {
            return this.numerator / this.denominator;
        }

        public String toString() {
            return this.numerator + "/" + this.denominator;
        }
    }

    static {
        int i = 3;
        int i2 = 4;
        int i3 = 6;
        int i4 = 1;
        int i5 = 2;
        int i6 = 5;
        int i7 = 7;
        int i8 = 3;
        int i9 = 4;
        int i10 = 23;
        IFD_TIFF_TAGS = new ExifTag[]{new ExifTag("NewSubfileType", 254, i2), new ExifTag("SubfileType", 255, i2), new ExifTag("ImageWidth", 256, 3, 4), new ExifTag("ImageLength", 257, 3, 4), new ExifTag("BitsPerSample", 258, i), new ExifTag("Compression", 259, i), new ExifTag("PhotometricInterpretation", 262, i), new ExifTag("ImageDescription", 270, i5), new ExifTag("Make", 271, i5), new ExifTag("Model", 272, i5), new ExifTag("StripOffsets", 273, i8, i9), new ExifTag("Orientation", 274, i), new ExifTag("SamplesPerPixel", 277, i), new ExifTag("RowsPerStrip", 278, i8, i9), new ExifTag("StripByteCounts", 279, i8, i9), new ExifTag("XResolution", 282, i6), new ExifTag("YResolution", 283, i6), new ExifTag("PlanarConfiguration", 284, i), new ExifTag("ResolutionUnit", 296, i), new ExifTag("TransferFunction", 301, i), new ExifTag("Software", 305, i5), new ExifTag("DateTime", 306, i5), new ExifTag("Artist", 315, i5), new ExifTag("WhitePoint", 318, i6), new ExifTag("PrimaryChromaticities", 319, i6), new ExifTag(TAG_SUB_IFD_POINTER, 330, i2), new ExifTag("JPEGInterchangeFormat", 513, i2), new ExifTag("JPEGInterchangeFormatLength", 514, i2), new ExifTag("YCbCrCoefficients", 529, i6), new ExifTag("YCbCrSubSampling", 530, i), new ExifTag("YCbCrPositioning", 531, i), new ExifTag("ReferenceBlackWhite", 532, i6), new ExifTag("Copyright", 33432, i5), new ExifTag(TAG_EXIF_IFD_POINTER, 34665, i2), new ExifTag(TAG_GPS_INFO_IFD_POINTER, GLES30.GL_DRAW_BUFFER0, i2), new ExifTag("SensorTopBorder", i2, i2), new ExifTag("SensorLeftBorder", i6, i2), new ExifTag("SensorBottomBorder", i3, i2), new ExifTag("SensorRightBorder", i7, i2), new ExifTag("ISO", i10, i), new ExifTag("JpgFromRaw", 46, i7)};
        int i11 = 10;
        int i12 = 3;
        int i13 = 4;
        IFD_EXIF_TAGS = new ExifTag[]{new ExifTag("ExposureTime", 33434, i6), new ExifTag("FNumber", 33437, i6), new ExifTag("ExposureProgram", 34850, i), new ExifTag("SpectralSensitivity", GLES30.GL_MAX_DRAW_BUFFERS, i5), new ExifTag("ISOSpeedRatings", GLES30.GL_DRAW_BUFFER2, i), new ExifTag("OECF", GLES30.GL_DRAW_BUFFER3, i7), new ExifTag("ExifVersion", 36864, i5), new ExifTag("DateTimeOriginal", 36867, i5), new ExifTag("DateTimeDigitized", 36868, i5), new ExifTag("ComponentsConfiguration", 37121, i7), new ExifTag("CompressedBitsPerPixel", 37122, i6), new ExifTag("ShutterSpeedValue", 37377, i11), new ExifTag("ApertureValue", 37378, i6), new ExifTag("BrightnessValue", 37379, i11), new ExifTag("ExposureBiasValue", 37380, i11), new ExifTag("MaxApertureValue", 37381, i6), new ExifTag("SubjectDistance", 37382, i6), new ExifTag("MeteringMode", 37383, i), new ExifTag("LightSource", 37384, i), new ExifTag("Flash", 37385, i), new ExifTag("FocalLength", 37386, i6), new ExifTag("SubjectArea", 37396, i), new ExifTag("MakerNote", 37500, i7), new ExifTag("UserComment", 37510, i7), new ExifTag("SubSecTime", 37520, i5), new ExifTag("SubSecTimeOriginal", 37521, i5), new ExifTag("SubSecTimeDigitized", 37522, i5), new ExifTag("FlashpixVersion", 40960, i7), new ExifTag("ColorSpace", 40961, i), new ExifTag("PixelXDimension", 40962, i12, i13), new ExifTag("PixelYDimension", 40963, i12, i13), new ExifTag("RelatedSoundFile", 40964, i5), new ExifTag(TAG_INTEROPERABILITY_IFD_POINTER, 40965, i2), new ExifTag("FlashEnergy", 41483, i6), new ExifTag("SpatialFrequencyResponse", 41484, i7), new ExifTag("FocalPlaneXResolution", 41486, i6), new ExifTag("FocalPlaneYResolution", 41487, i6), new ExifTag("FocalPlaneResolutionUnit", 41488, i), new ExifTag("SubjectLocation", 41492, i), new ExifTag("ExposureIndex", 41493, i6), new ExifTag("SensingMethod", 41495, i), new ExifTag("FileSource", 41728, i7), new ExifTag("SceneType", 41729, i7), new ExifTag("CFAPattern", 41730, i7), new ExifTag("CustomRendered", 41985, i), new ExifTag("ExposureMode", 41986, i), new ExifTag("WhiteBalance", 41987, i), new ExifTag("DigitalZoomRatio", 41988, i6), new ExifTag("FocalLengthIn35mmFilm", 41989, i), new ExifTag("SceneCaptureType", 41990, i), new ExifTag("GainControl", 41991, i), new ExifTag("Contrast", 41992, i), new ExifTag("Saturation", 41993, i), new ExifTag("Sharpness", 41994, i), new ExifTag("DeviceSettingDescription", 41995, i7), new ExifTag("SubjectDistanceRange", 41996, i), new ExifTag("ImageUniqueID", 42016, i5), new ExifTag("DNGVersion", 50706, i4), new ExifTag("DefaultCropSize", 50720, i12, i13)};
        IFD_GPS_TAGS = new ExifTag[]{new ExifTag("GPSVersionID", 0, i4), new ExifTag("GPSLatitudeRef", i4, i5), new ExifTag("GPSLatitude", i5, i6), new ExifTag("GPSLongitudeRef", i, i5), new ExifTag("GPSLongitude", i2, i6), new ExifTag("GPSAltitudeRef", i6, i4), new ExifTag("GPSAltitude", i3, i6), new ExifTag("GPSTimeStamp", i7, i6), new ExifTag("GPSSatellites", 8, i5), new ExifTag("GPSStatus", 9, i5), new ExifTag("GPSMeasureMode", 10, i5), new ExifTag("GPSDOP", 11, i6), new ExifTag("GPSSpeedRef", 12, i5), new ExifTag("GPSSpeed", 13, i6), new ExifTag("GPSTrackRef", 14, i5), new ExifTag("GPSTrack", 15, i6), new ExifTag("GPSImgDirectionRef", 16, i5), new ExifTag("GPSImgDirection", 17, i6), new ExifTag("GPSMapDatum", 18, i5), new ExifTag("GPSDestLatitudeRef", 19, i5), new ExifTag("GPSDestLatitude", 20, i6), new ExifTag("GPSDestLongitudeRef", 21, i5), new ExifTag("GPSDestLongitude", 22, i6), new ExifTag("GPSDestBearingRef", i10, i5), new ExifTag("GPSDestBearing", 24, i6), new ExifTag("GPSDestDistanceRef", 25, i5), new ExifTag("GPSDestDistance", 26, i6), new ExifTag("GPSProcessingMethod", 27, i7), new ExifTag("GPSAreaInformation", 28, i7), new ExifTag("GPSDateStamp", 29, i5), new ExifTag("GPSDifferential", 30, i)};
        IFD_INTEROPERABILITY_TAGS = new ExifTag[]{new ExifTag("InteroperabilityIndex", i4, i5)};
        int i14 = 3;
        int i15 = 4;
        IFD_THUMBNAIL_TAGS = new ExifTag[]{new ExifTag("NewSubfileType", 254, i2), new ExifTag("SubfileType", 255, i2), new ExifTag("ThumbnailImageWidth", 256, 3, 4), new ExifTag("ThumbnailImageLength", 257, 3, 4), new ExifTag("BitsPerSample", 258, i), new ExifTag("Compression", 259, i), new ExifTag("PhotometricInterpretation", 262, i), new ExifTag("ImageDescription", 270, i5), new ExifTag("Make", 271, i5), new ExifTag("Model", 272, i5), new ExifTag("StripOffsets", 273, i14, i15), new ExifTag("Orientation", 274, i), new ExifTag("SamplesPerPixel", 277, i), new ExifTag("RowsPerStrip", 278, i14, i15), new ExifTag("StripByteCounts", 279, i14, i15), new ExifTag("XResolution", 282, i6), new ExifTag("YResolution", 283, i6), new ExifTag("PlanarConfiguration", 284, i), new ExifTag("ResolutionUnit", 296, i), new ExifTag("TransferFunction", 301, i), new ExifTag("Software", 305, i5), new ExifTag("DateTime", 306, i5), new ExifTag("Artist", 315, i5), new ExifTag("WhitePoint", 318, i6), new ExifTag("PrimaryChromaticities", 319, i6), new ExifTag(TAG_SUB_IFD_POINTER, 330, i2), new ExifTag("JPEGInterchangeFormat", 513, i2), new ExifTag("JPEGInterchangeFormatLength", 514, i2), new ExifTag("YCbCrCoefficients", 529, i6), new ExifTag("YCbCrSubSampling", 530, i), new ExifTag("YCbCrPositioning", 531, i), new ExifTag("ReferenceBlackWhite", 532, i6), new ExifTag("Copyright", 33432, i5), new ExifTag(TAG_EXIF_IFD_POINTER, 34665, i2), new ExifTag(TAG_GPS_INFO_IFD_POINTER, GLES30.GL_DRAW_BUFFER0, i2), new ExifTag("DNGVersion", 50706, i4), new ExifTag("DefaultCropSize", 50720, i14, i15)};
        TAG_RAF_IMAGE_SIZE = new ExifTag("StripOffsets", 273, i);
        ORF_MAKER_NOTE_TAGS = new ExifTag[]{new ExifTag("ThumbnailImage", 256, i7), new ExifTag(TAG_ORF_CAMERA_SETTINGS_IFD_POINTER, MtpConstants.RESPONSE_SPECIFICATION_OF_DESTINATION_UNSUPPORTED, i2), new ExifTag(TAG_ORF_IMAGE_PROCESSING_IFD_POINTER, 8256, i2)};
        ORF_CAMERA_SETTINGS_TAGS = new ExifTag[]{new ExifTag("PreviewImageStart", 257, i2), new ExifTag("PreviewImageLength", 258, i2)};
        ORF_IMAGE_PROCESSING_TAGS = new ExifTag[]{new ExifTag("AspectFrame", SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_IMMEDIATE_OBSERVED, i)};
        PEF_TAGS = new ExifTag[]{new ExifTag("ColorSpace", 55, i)};
        EXIF_TAGS = new ExifTag[][]{IFD_TIFF_TAGS, IFD_EXIF_TAGS, IFD_GPS_TAGS, IFD_INTEROPERABILITY_TAGS, IFD_THUMBNAIL_TAGS, IFD_TIFF_TAGS, ORF_MAKER_NOTE_TAGS, ORF_CAMERA_SETTINGS_TAGS, ORF_IMAGE_PROCESSING_TAGS, PEF_TAGS};
        EXIF_POINTER_TAGS = new ExifTag[]{new ExifTag(TAG_SUB_IFD_POINTER, 330, i2), new ExifTag(TAG_EXIF_IFD_POINTER, 34665, i2), new ExifTag(TAG_GPS_INFO_IFD_POINTER, GLES30.GL_DRAW_BUFFER0, i2), new ExifTag(TAG_INTEROPERABILITY_IFD_POINTER, 40965, i2), new ExifTag(TAG_ORF_CAMERA_SETTINGS_IFD_POINTER, MtpConstants.RESPONSE_SPECIFICATION_OF_DESTINATION_UNSUPPORTED, i4), new ExifTag(TAG_ORF_IMAGE_PROCESSING_IFD_POINTER, 8256, i4)};
        JPEG_INTERCHANGE_FORMAT_TAG = new ExifTag("JPEGInterchangeFormat", 513, i2);
        JPEG_INTERCHANGE_FORMAT_LENGTH_TAG = new ExifTag("JPEGInterchangeFormatLength", 514, i2);
        sExifTagMapsForReading = new HashMap[EXIF_TAGS.length];
        sExifTagMapsForWriting = new HashMap[EXIF_TAGS.length];
        sFormatter.setTimeZone(TimeZone.getTimeZone(Time.TIMEZONE_UTC));
        for (int i16 = 0; i16 < EXIF_TAGS.length; i16++) {
            sExifTagMapsForReading[i16] = new HashMap();
            sExifTagMapsForWriting[i16] = new HashMap();
            for (ExifTag exifTag : EXIF_TAGS[i16]) {
                sExifTagMapsForReading[i16].put(Integer.valueOf(exifTag.number), exifTag);
                sExifTagMapsForWriting[i16].put(exifTag.name, exifTag);
            }
        }
        sExifPointerTagMap.put(Integer.valueOf(EXIF_POINTER_TAGS[0].number), 5);
        sExifPointerTagMap.put(Integer.valueOf(EXIF_POINTER_TAGS[1].number), 1);
        sExifPointerTagMap.put(Integer.valueOf(EXIF_POINTER_TAGS[2].number), 2);
        sExifPointerTagMap.put(Integer.valueOf(EXIF_POINTER_TAGS[3].number), 3);
        sExifPointerTagMap.put(Integer.valueOf(EXIF_POINTER_TAGS[4].number), 7);
        sExifPointerTagMap.put(Integer.valueOf(EXIF_POINTER_TAGS[5].number), 8);
        sNonZeroTimePattern = Pattern.compile(".*[1-9].*");
        sGpsTimestampPattern = Pattern.compile("^([0-9][0-9]):([0-9][0-9]):([0-9][0-9])$");
    }

    public ExifInterface(FileDescriptor fileDescriptor) throws Throwable {
        this.mAttributes = new HashMap[EXIF_TAGS.length];
        this.mAttributesOffsets = new HashSet(EXIF_TAGS.length);
        this.mExifByteOrder = ByteOrder.BIG_ENDIAN;
        if (fileDescriptor == null) {
            throw new IllegalArgumentException("fileDescriptor cannot be null");
        }
        FileInputStream fileInputStream = null;
        this.mAssetInputStream = null;
        this.mFilename = null;
        if (isSeekableFD(fileDescriptor)) {
            this.mSeekableFileDescriptor = fileDescriptor;
            try {
                fileDescriptor = Os.dup(fileDescriptor);
            } catch (ErrnoException e) {
                throw e.rethrowAsIOException();
            }
        } else {
            this.mSeekableFileDescriptor = null;
        }
        this.mIsInputStream = false;
        try {
            FileInputStream fileInputStream2 = new FileInputStream(fileDescriptor);
            try {
                loadAttributes(fileInputStream2);
                IoUtils.closeQuietly(fileInputStream2);
            } catch (Throwable th) {
                th = th;
                fileInputStream = fileInputStream2;
                IoUtils.closeQuietly(fileInputStream);
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    /* JADX WARN: Code duplicated, block: B:12:0x0043  */
    public ExifInterface(InputStream inputStream) throws IOException {
        this.mAttributes = new HashMap[EXIF_TAGS.length];
        this.mAttributesOffsets = new HashSet(EXIF_TAGS.length);
        this.mExifByteOrder = ByteOrder.BIG_ENDIAN;
        if (inputStream == null) {
            throw new IllegalArgumentException("inputStream cannot be null");
        }
        this.mFilename = null;
        if (inputStream instanceof AssetManager.AssetInputStream) {
            this.mAssetInputStream = (AssetManager.AssetInputStream) inputStream;
            this.mSeekableFileDescriptor = null;
        } else if (inputStream instanceof FileInputStream) {
            FileInputStream fileInputStream = (FileInputStream) inputStream;
            if (isSeekableFD(fileInputStream.getFD())) {
                this.mAssetInputStream = null;
                this.mSeekableFileDescriptor = fileInputStream.getFD();
            } else {
                this.mAssetInputStream = null;
                this.mSeekableFileDescriptor = null;
            }
        } else {
            this.mAssetInputStream = null;
            this.mSeekableFileDescriptor = null;
        }
        this.mIsInputStream = true;
        loadAttributes(inputStream);
    }

    public ExifInterface(String str) throws Throwable {
        FileInputStream fileInputStream;
        this.mAttributes = new HashMap[EXIF_TAGS.length];
        this.mAttributesOffsets = new HashSet(EXIF_TAGS.length);
        this.mExifByteOrder = ByteOrder.BIG_ENDIAN;
        if (str == null) {
            throw new IllegalArgumentException("filename cannot be null");
        }
        this.mAssetInputStream = null;
        this.mFilename = str;
        this.mIsInputStream = false;
        try {
            fileInputStream = new FileInputStream(str);
            try {
                if (isSeekableFD(fileInputStream.getFD())) {
                    this.mSeekableFileDescriptor = fileInputStream.getFD();
                } else {
                    this.mSeekableFileDescriptor = null;
                }
                loadAttributes(fileInputStream);
                IoUtils.closeQuietly(fileInputStream);
            } catch (Throwable th) {
                th = th;
                IoUtils.closeQuietly(fileInputStream);
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            fileInputStream = null;
        }
    }

    private void addDefaultValuesForCompatibility() {
        String attribute = getAttribute("DateTimeOriginal");
        if (attribute != null && getAttribute("DateTime") == null) {
            this.mAttributes[0].put("DateTime", ExifAttribute.createString(attribute));
        }
        if (getAttribute("ImageWidth") == null) {
            this.mAttributes[0].put("ImageWidth", ExifAttribute.createULong(0L, this.mExifByteOrder));
        }
        if (getAttribute("ImageLength") == null) {
            this.mAttributes[0].put("ImageLength", ExifAttribute.createULong(0L, this.mExifByteOrder));
        }
        if (getAttribute("Orientation") == null) {
            this.mAttributes[0].put("Orientation", ExifAttribute.createUShort(0, this.mExifByteOrder));
        }
        if (getAttribute("LightSource") == null) {
            this.mAttributes[1].put("LightSource", ExifAttribute.createULong(0L, this.mExifByteOrder));
        }
    }

    private boolean containsMatch(byte[] bArr, byte[] bArr2) {
        for (int i = 0; i < bArr.length - bArr2.length; i++) {
            for (int i2 = 0; i2 < bArr2.length && bArr[i + i2] == bArr2[i2]; i2++) {
                if (i2 == bArr2.length - 1) {
                    return true;
                }
            }
        }
        return false;
    }

    public static float convertRationalLatLonToFloat(String str, String str2) {
        try {
            String[] strArrSplit = str.split(",");
            String[] strArrSplit2 = strArrSplit[0].split("/");
            double d = Double.parseDouble(strArrSplit2[0].trim()) / Double.parseDouble(strArrSplit2[1].trim());
            String[] strArrSplit3 = strArrSplit[1].split("/");
            double d2 = Double.parseDouble(strArrSplit3[0].trim()) / Double.parseDouble(strArrSplit3[1].trim());
            String[] strArrSplit4 = strArrSplit[2].split("/");
            double d3 = d + (d2 / 60.0d) + ((Double.parseDouble(strArrSplit4[0].trim()) / Double.parseDouble(strArrSplit4[1].trim())) / 3600.0d);
            return (str2.equals(androidx.exifinterface.media.ExifInterface.LATITUDE_SOUTH) || str2.equals(androidx.exifinterface.media.ExifInterface.LONGITUDE_WEST)) ? (float) (-d3) : (float) d3;
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException unused) {
            throw new IllegalArgumentException();
        }
    }

    private static long[] convertToLongArray(Object obj) {
        if (!(obj instanceof int[])) {
            if (obj instanceof long[]) {
                return (long[]) obj;
            }
            return null;
        }
        int[] iArr = (int[]) obj;
        long[] jArr = new long[iArr.length];
        for (int i = 0; i < iArr.length; i++) {
            jArr[i] = iArr[i];
        }
        return jArr;
    }

    private ExifAttribute getExifAttribute(String str) {
        for (int i = 0; i < EXIF_TAGS.length; i++) {
            Object obj = this.mAttributes[i].get(str);
            if (obj != null) {
                return (ExifAttribute) obj;
            }
        }
        return null;
    }

    private void getHeifAttributes(final ByteOrderedDataInputStream byteOrderedDataInputStream) throws IOException {
        String strExtractMetadata;
        String strExtractMetadata2;
        int i;
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        try {
            mediaMetadataRetriever.setDataSource(new MediaDataSource() { // from class: android.media.ExifInterface.1
                long mPosition;

                @Override // java.io.Closeable, java.lang.AutoCloseable
                public void close() throws IOException {
                }

                @Override // android.media.MediaDataSource
                public long getSize() throws IOException {
                    return -1L;
                }

                @Override // android.media.MediaDataSource
                public int readAt(long j, byte[] bArr, int i2, int i3) throws IOException {
                    if (i3 == 0) {
                        return 0;
                    }
                    if (j < 0) {
                        return -1;
                    }
                    try {
                        if (this.mPosition != j) {
                            byteOrderedDataInputStream.seek(j);
                            this.mPosition = j;
                        }
                        int i4 = byteOrderedDataInputStream.read(bArr, i2, i3);
                        if (i4 >= 0) {
                            this.mPosition += (long) i4;
                            return i4;
                        }
                    } catch (IOException unused) {
                    }
                    this.mPosition = -1L;
                    return -1;
                }
            });
            String strExtractMetadata3 = mediaMetadataRetriever.extractMetadata(33);
            String strExtractMetadata4 = mediaMetadataRetriever.extractMetadata(34);
            String strExtractMetadata5 = mediaMetadataRetriever.extractMetadata(26);
            String strExtractMetadata6 = mediaMetadataRetriever.extractMetadata(17);
            String strExtractMetadata7 = null;
            if ("yes".equals(strExtractMetadata5)) {
                strExtractMetadata7 = mediaMetadataRetriever.extractMetadata(29);
                strExtractMetadata = mediaMetadataRetriever.extractMetadata(30);
                strExtractMetadata2 = mediaMetadataRetriever.extractMetadata(31);
            } else if ("yes".equals(strExtractMetadata6)) {
                strExtractMetadata7 = mediaMetadataRetriever.extractMetadata(18);
                strExtractMetadata = mediaMetadataRetriever.extractMetadata(19);
                strExtractMetadata2 = mediaMetadataRetriever.extractMetadata(24);
            } else {
                strExtractMetadata = null;
                strExtractMetadata2 = null;
            }
            if (strExtractMetadata7 != null) {
                this.mAttributes[0].put("ImageWidth", ExifAttribute.createUShort(Integer.parseInt(strExtractMetadata7), this.mExifByteOrder));
            }
            if (strExtractMetadata != null) {
                this.mAttributes[0].put("ImageLength", ExifAttribute.createUShort(Integer.parseInt(strExtractMetadata), this.mExifByteOrder));
            }
            if (strExtractMetadata2 != null) {
                int i2 = Integer.parseInt(strExtractMetadata2);
                if (i2 == 90) {
                    i = 6;
                } else if (i2 != 180) {
                    i = i2 != 270 ? 1 : 8;
                } else {
                    i = 3;
                }
                this.mAttributes[0].put("Orientation", ExifAttribute.createUShort(i, this.mExifByteOrder));
            }
            if (strExtractMetadata3 != null && strExtractMetadata4 != null) {
                int i3 = Integer.parseInt(strExtractMetadata3);
                int i4 = Integer.parseInt(strExtractMetadata4);
                if (i4 <= 6) {
                    throw new IOException("Invalid exif length");
                }
                byteOrderedDataInputStream.seek(i3);
                byte[] bArr = new byte[6];
                if (byteOrderedDataInputStream.read(bArr) != 6) {
                    throw new IOException("Can't read identifier");
                }
                int i5 = i4 - 6;
                if (!Arrays.equals(bArr, IDENTIFIER_EXIF_APP1)) {
                    throw new IOException("Invalid identifier");
                }
                byte[] bArr2 = new byte[i5];
                if (byteOrderedDataInputStream.read(bArr2) != i5) {
                    throw new IOException("Can't read exif");
                }
                readExifSegment(bArr2, 0);
            }
            mediaMetadataRetriever.release();
        } catch (Throwable th) {
            mediaMetadataRetriever.release();
            throw th;
        }
    }

    /* JADX WARN: Code duplicated, block: B:25:0x0051 A[FALL_THROUGH] */
    /* JADX WARN: Code duplicated, block: B:27:0x0057  */
    /* JADX WARN: Code duplicated, block: B:78:0x0082 A[SYNTHETIC] */
    /*  JADX ERROR: UnsupportedOperationException in pass: RegionMakerVisitor
        java.lang.UnsupportedOperationException
        	at java.base/java.util.Collections$UnmodifiableCollection.add(Collections.java:1092)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker$1.leaveRegion(SwitchRegionMaker.java:419)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:91)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverse(DepthRegionTraversal.java:31)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.insertBreaksForCase(SwitchRegionMaker.java:399)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.insertBreaks(SwitchRegionMaker.java:89)
        	at jadx.core.dex.visitors.regions.PostProcessRegions.leaveRegion(PostProcessRegions.java:31)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:91)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverse(DepthRegionTraversal.java:27)
        	at jadx.core.dex.visitors.regions.PostProcessRegions.process(PostProcessRegions.java:21)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:31)
        */
    private void getJpegAttributes(android.media.ExifInterface.ByteOrderedDataInputStream r9, int r10, int r11) throws java.io.IOException {
        /*
            Method dump skipped, instruction units count: 420
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.ExifInterface.getJpegAttributes(android.media.ExifInterface$ByteOrderedDataInputStream, int, int):void");
    }

    private int getMimeType(BufferedInputStream bufferedInputStream) throws IOException {
        bufferedInputStream.mark(5000);
        byte[] bArr = new byte[5000];
        if (bufferedInputStream.read(bArr) <= 0) {
            return 0;
        }
        bufferedInputStream.reset();
        if (isJpegFormat(bArr)) {
            return 4;
        }
        if (isRafFormat(bArr)) {
            return 9;
        }
        if (isHeifFormat(bArr)) {
            return 12;
        }
        if (isOrfFormat(bArr)) {
            return 7;
        }
        return isRw2Format(bArr) ? 10 : 0;
    }

    private void getOrfAttributes(ByteOrderedDataInputStream byteOrderedDataInputStream) throws Throwable {
        getRawAttributes(byteOrderedDataInputStream);
        ExifAttribute exifAttribute = (ExifAttribute) this.mAttributes[1].get("MakerNote");
        if (exifAttribute != null) {
            ByteOrderedDataInputStream byteOrderedDataInputStream2 = new ByteOrderedDataInputStream(exifAttribute.bytes);
            byteOrderedDataInputStream2.setByteOrder(this.mExifByteOrder);
            byte[] bArr = new byte[ORF_MAKER_NOTE_HEADER_1.length];
            byteOrderedDataInputStream2.readFully(bArr);
            byteOrderedDataInputStream2.seek(0L);
            byte[] bArr2 = new byte[ORF_MAKER_NOTE_HEADER_2.length];
            byteOrderedDataInputStream2.readFully(bArr2);
            if (Arrays.equals(bArr, ORF_MAKER_NOTE_HEADER_1)) {
                byteOrderedDataInputStream2.seek(8L);
            } else if (Arrays.equals(bArr2, ORF_MAKER_NOTE_HEADER_2)) {
                byteOrderedDataInputStream2.seek(12L);
            }
            readImageFileDirectory(byteOrderedDataInputStream2, 6);
            ExifAttribute exifAttribute2 = (ExifAttribute) this.mAttributes[7].get("PreviewImageStart");
            ExifAttribute exifAttribute3 = (ExifAttribute) this.mAttributes[7].get("PreviewImageLength");
            if (exifAttribute2 != null && exifAttribute3 != null) {
                this.mAttributes[5].put("JPEGInterchangeFormat", exifAttribute2);
                this.mAttributes[5].put("JPEGInterchangeFormatLength", exifAttribute3);
            }
            ExifAttribute exifAttribute4 = (ExifAttribute) this.mAttributes[8].get("AspectFrame");
            if (exifAttribute4 != null) {
                int[] iArr = new int[4];
                int[] iArr2 = (int[]) exifAttribute4.getValue(this.mExifByteOrder);
                if (iArr2[2] <= iArr2[0] || iArr2[3] <= iArr2[1]) {
                    return;
                }
                int i = (iArr2[2] - iArr2[0]) + 1;
                int i2 = (iArr2[3] - iArr2[1]) + 1;
                if (i < i2) {
                    int i3 = i + i2;
                    i2 = i3 - i2;
                    i = i3 - i2;
                }
                ExifAttribute exifAttributeCreateUShort = ExifAttribute.createUShort(i, this.mExifByteOrder);
                ExifAttribute exifAttributeCreateUShort2 = ExifAttribute.createUShort(i2, this.mExifByteOrder);
                this.mAttributes[0].put("ImageWidth", exifAttributeCreateUShort);
                this.mAttributes[0].put("ImageLength", exifAttributeCreateUShort2);
            }
        }
    }

    private void getRafAttributes(ByteOrderedDataInputStream byteOrderedDataInputStream) throws IOException {
        byteOrderedDataInputStream.skipBytes(84);
        byte[] bArr = new byte[4];
        byte[] bArr2 = new byte[4];
        if (byteOrderedDataInputStream.read(bArr) <= 0) {
            return;
        }
        byteOrderedDataInputStream.skipBytes(4);
        if (byteOrderedDataInputStream.read(bArr2) <= 0) {
            return;
        }
        int i = ByteBuffer.wrap(bArr).getInt();
        int i2 = ByteBuffer.wrap(bArr2).getInt();
        getJpegAttributes(byteOrderedDataInputStream, i, 5);
        byteOrderedDataInputStream.seek(i2);
        byteOrderedDataInputStream.setByteOrder(ByteOrder.BIG_ENDIAN);
        int i3 = byteOrderedDataInputStream.readInt();
        for (int i4 = 0; i4 < i3; i4++) {
            int unsignedShort = byteOrderedDataInputStream.readUnsignedShort();
            int unsignedShort2 = byteOrderedDataInputStream.readUnsignedShort();
            if (unsignedShort == TAG_RAF_IMAGE_SIZE.number) {
                short s = byteOrderedDataInputStream.readShort();
                short s2 = byteOrderedDataInputStream.readShort();
                ExifAttribute exifAttributeCreateUShort = ExifAttribute.createUShort(s, this.mExifByteOrder);
                ExifAttribute exifAttributeCreateUShort2 = ExifAttribute.createUShort(s2, this.mExifByteOrder);
                this.mAttributes[0].put("ImageLength", exifAttributeCreateUShort);
                this.mAttributes[0].put("ImageWidth", exifAttributeCreateUShort2);
                return;
            }
            byteOrderedDataInputStream.skipBytes(unsignedShort2);
        }
    }

    private void getRawAttributes(ByteOrderedDataInputStream byteOrderedDataInputStream) throws Throwable {
        ExifAttribute exifAttribute;
        parseTiffHeaders(byteOrderedDataInputStream, byteOrderedDataInputStream.available());
        readImageFileDirectory(byteOrderedDataInputStream, 0);
        updateImageSizeValues(byteOrderedDataInputStream, 0);
        updateImageSizeValues(byteOrderedDataInputStream, 5);
        updateImageSizeValues(byteOrderedDataInputStream, 4);
        validateImages(byteOrderedDataInputStream);
        if (this.mMimeType != 8 || (exifAttribute = (ExifAttribute) this.mAttributes[1].get("MakerNote")) == null) {
            return;
        }
        ByteOrderedDataInputStream byteOrderedDataInputStream2 = new ByteOrderedDataInputStream(exifAttribute.bytes);
        byteOrderedDataInputStream2.setByteOrder(this.mExifByteOrder);
        byteOrderedDataInputStream2.seek(6L);
        readImageFileDirectory(byteOrderedDataInputStream2, 9);
        ExifAttribute exifAttribute2 = (ExifAttribute) this.mAttributes[9].get("ColorSpace");
        if (exifAttribute2 != null) {
            this.mAttributes[1].put("ColorSpace", exifAttribute2);
        }
    }

    private void getRw2Attributes(ByteOrderedDataInputStream byteOrderedDataInputStream) throws Throwable {
        getRawAttributes(byteOrderedDataInputStream);
        if (((ExifAttribute) this.mAttributes[0].get("JpgFromRaw")) != null) {
            getJpegAttributes(byteOrderedDataInputStream, this.mRw2JpgFromRawOffset, 5);
        }
        ExifAttribute exifAttribute = (ExifAttribute) this.mAttributes[0].get("ISO");
        ExifAttribute exifAttribute2 = (ExifAttribute) this.mAttributes[1].get("ISOSpeedRatings");
        if (exifAttribute == null || exifAttribute2 != null) {
            return;
        }
        this.mAttributes[1].put("ISOSpeedRatings", exifAttribute);
    }

    private static Pair<Integer, Integer> guessDataFormat(String str) {
        if (str.contains(",")) {
            String[] strArrSplit = str.split(",");
            Pair<Integer, Integer> pairGuessDataFormat = guessDataFormat(strArrSplit[0]);
            if (pairGuessDataFormat.first.intValue() == 2) {
                return pairGuessDataFormat;
            }
            for (int i = 1; i < strArrSplit.length; i++) {
                Pair<Integer, Integer> pairGuessDataFormat2 = guessDataFormat(strArrSplit[i]);
                int iIntValue = (pairGuessDataFormat2.first == pairGuessDataFormat.first || pairGuessDataFormat2.second == pairGuessDataFormat.first) ? pairGuessDataFormat.first.intValue() : -1;
                int iIntValue2 = (pairGuessDataFormat.second.intValue() == -1 || !(pairGuessDataFormat2.first == pairGuessDataFormat.second || pairGuessDataFormat2.second == pairGuessDataFormat.second)) ? -1 : pairGuessDataFormat.second.intValue();
                if (iIntValue == -1 && iIntValue2 == -1) {
                    return new Pair<>(2, -1);
                }
                if (iIntValue == -1) {
                    pairGuessDataFormat = new Pair<>(Integer.valueOf(iIntValue2), -1);
                } else if (iIntValue2 == -1) {
                    pairGuessDataFormat = new Pair<>(Integer.valueOf(iIntValue), -1);
                }
            }
            return pairGuessDataFormat;
        }
        if (!str.contains("/")) {
            try {
                try {
                    Long lValueOf = Long.valueOf(Long.parseLong(str));
                    if (lValueOf.longValue() < 0 || lValueOf.longValue() > 65535) {
                        return lValueOf.longValue() < 0 ? new Pair<>(9, -1) : new Pair<>(4, -1);
                    }
                    return new Pair<>(3, 4);
                } catch (NumberFormatException unused) {
                    return new Pair<>(2, -1);
                }
            } catch (NumberFormatException unused2) {
                Double.parseDouble(str);
                return new Pair<>(12, -1);
            }
        }
        String[] strArrSplit2 = str.split("/");
        if (strArrSplit2.length == 2) {
            try {
                long j = (long) Double.parseDouble(strArrSplit2[0]);
                long j2 = (long) Double.parseDouble(strArrSplit2[1]);
                if (j >= 0 && j2 >= 0) {
                    if (j <= 2147483647L && j2 <= 2147483647L) {
                        return new Pair<>(10, 5);
                    }
                    return new Pair<>(5, -1);
                }
                return new Pair<>(10, -1);
            } catch (NumberFormatException unused3) {
            }
        }
        return new Pair<>(2, -1);
    }

    private void handleThumbnailFromJfif(ByteOrderedDataInputStream byteOrderedDataInputStream, HashMap map) throws Throwable {
        ExifAttribute exifAttribute = (ExifAttribute) map.get("JPEGInterchangeFormat");
        ExifAttribute exifAttribute2 = (ExifAttribute) map.get("JPEGInterchangeFormatLength");
        if (exifAttribute == null || exifAttribute2 == null) {
            return;
        }
        int intValue = exifAttribute.getIntValue(this.mExifByteOrder);
        int iMin = Math.min(exifAttribute2.getIntValue(this.mExifByteOrder), byteOrderedDataInputStream.available() - intValue);
        if (this.mMimeType == 4 || this.mMimeType == 9 || this.mMimeType == 10) {
            intValue += this.mExifOffset;
        } else if (this.mMimeType == 7) {
            intValue += this.mOrfMakerNoteOffset;
        }
        if (intValue <= 0 || iMin <= 0) {
            return;
        }
        this.mHasThumbnail = true;
        this.mThumbnailOffset = intValue;
        this.mThumbnailLength = iMin;
        this.mThumbnailCompression = 6;
        if (this.mFilename == null && this.mAssetInputStream == null && this.mSeekableFileDescriptor == null) {
            byte[] bArr = new byte[iMin];
            byteOrderedDataInputStream.seek(intValue);
            byteOrderedDataInputStream.readFully(bArr);
            this.mThumbnailBytes = bArr;
        }
    }

    private void handleThumbnailFromStrips(ByteOrderedDataInputStream byteOrderedDataInputStream, HashMap map) throws IOException {
        ExifAttribute exifAttribute = (ExifAttribute) map.get("StripOffsets");
        ExifAttribute exifAttribute2 = (ExifAttribute) map.get("StripByteCounts");
        if (exifAttribute == null || exifAttribute2 == null) {
            return;
        }
        long[] jArrConvertToLongArray = convertToLongArray(exifAttribute.getValue(this.mExifByteOrder));
        long[] jArrConvertToLongArray2 = convertToLongArray(exifAttribute2.getValue(this.mExifByteOrder));
        if (jArrConvertToLongArray == null) {
            Log.w(TAG, "stripOffsets should not be null.");
            return;
        }
        if (jArrConvertToLongArray2 == null) {
            Log.w(TAG, "stripByteCounts should not be null.");
            return;
        }
        byte[] bArr = new byte[(int) Arrays.stream(jArrConvertToLongArray2).sum()];
        int i = 0;
        int length = 0;
        for (int i2 = 0; i2 < jArrConvertToLongArray.length; i2++) {
            int i3 = (int) jArrConvertToLongArray[i2];
            int i4 = (int) jArrConvertToLongArray2[i2];
            int i5 = i3 - i;
            if (i5 < 0) {
                Log.d(TAG, "Invalid strip offset value");
            }
            byteOrderedDataInputStream.seek(i5);
            i += i5;
            byte[] bArr2 = new byte[i4];
            if (byteOrderedDataInputStream.read(bArr2) > 0) {
                i += i4;
                System.arraycopy(bArr2, 0, bArr, length, bArr2.length);
                length += bArr2.length;
            }
        }
        this.mHasThumbnail = true;
        this.mThumbnailBytes = bArr;
        this.mThumbnailLength = bArr.length;
    }

    private boolean isHeifFormat(byte[] bArr) throws Throwable {
        ByteOrderedDataInputStream byteOrderedDataInputStream;
        try {
            byteOrderedDataInputStream = new ByteOrderedDataInputStream(bArr);
            try {
                byteOrderedDataInputStream.setByteOrder(ByteOrder.BIG_ENDIAN);
                long length = byteOrderedDataInputStream.readInt();
                byte[] bArr2 = new byte[4];
                if (byteOrderedDataInputStream.read(bArr2) <= 0) {
                    byteOrderedDataInputStream.close();
                    return false;
                }
                if (!Arrays.equals(bArr2, HEIF_TYPE_FTYP)) {
                    byteOrderedDataInputStream.close();
                    return false;
                }
                long j = 16;
                if (length == 1) {
                    length = byteOrderedDataInputStream.readLong();
                    if (length < 16) {
                        byteOrderedDataInputStream.close();
                        return false;
                    }
                } else {
                    j = 8;
                }
                if (length > bArr.length) {
                    length = bArr.length;
                }
                long j2 = length - j;
                if (j2 < 8) {
                    byteOrderedDataInputStream.close();
                    return false;
                }
                byte[] bArr3 = new byte[4];
                boolean z = false;
                boolean z2 = false;
                for (long j3 = 0; j3 < j2 / 4; j3++) {
                    if (byteOrderedDataInputStream.read(bArr3) != bArr3.length) {
                        byteOrderedDataInputStream.close();
                        return false;
                    }
                    if (j3 != 1) {
                        if (Arrays.equals(bArr3, HEIF_BRAND_MIF1)) {
                            z = true;
                        } else if (Arrays.equals(bArr3, HEIF_BRAND_HEIC)) {
                            z2 = true;
                        }
                        if (z && z2) {
                            byteOrderedDataInputStream.close();
                            return true;
                        }
                    }
                }
            } catch (Exception unused) {
                if (byteOrderedDataInputStream != null) {
                }
                return false;
            } catch (Throwable th) {
                th = th;
                if (byteOrderedDataInputStream != null) {
                    byteOrderedDataInputStream.close();
                }
                throw th;
            }
        } catch (Exception unused2) {
            byteOrderedDataInputStream = null;
        } catch (Throwable th2) {
            th = th2;
            byteOrderedDataInputStream = null;
        }
        byteOrderedDataInputStream.close();
        return false;
    }

    private static boolean isJpegFormat(byte[] bArr) throws IOException {
        for (int i = 0; i < JPEG_SIGNATURE.length; i++) {
            if (bArr[i] != JPEG_SIGNATURE[i]) {
                return false;
            }
        }
        return true;
    }

    private boolean isOrfFormat(byte[] bArr) throws IOException {
        ByteOrderedDataInputStream byteOrderedDataInputStream = new ByteOrderedDataInputStream(bArr);
        this.mExifByteOrder = readByteOrder(byteOrderedDataInputStream);
        byteOrderedDataInputStream.setByteOrder(this.mExifByteOrder);
        short s = byteOrderedDataInputStream.readShort();
        return s == 20306 || s == 21330;
    }

    private boolean isRafFormat(byte[] bArr) throws IOException {
        byte[] bytes = RAF_SIGNATURE.getBytes();
        for (int i = 0; i < bytes.length; i++) {
            if (bArr[i] != bytes[i]) {
                return false;
            }
        }
        return true;
    }

    private boolean isRw2Format(byte[] bArr) throws IOException {
        ByteOrderedDataInputStream byteOrderedDataInputStream = new ByteOrderedDataInputStream(bArr);
        this.mExifByteOrder = readByteOrder(byteOrderedDataInputStream);
        byteOrderedDataInputStream.setByteOrder(this.mExifByteOrder);
        return byteOrderedDataInputStream.readShort() == 85;
    }

    private static boolean isSeekableFD(FileDescriptor fileDescriptor) throws IOException {
        try {
            Os.lseek(fileDescriptor, 0L, OsConstants.SEEK_CUR);
            return true;
        } catch (ErrnoException unused) {
            return false;
        }
    }

    private boolean isSupportedDataType(HashMap map) throws Throwable {
        ExifAttribute exifAttribute;
        ExifAttribute exifAttribute2 = (ExifAttribute) map.get("BitsPerSample");
        if (exifAttribute2 == null) {
            return false;
        }
        int[] iArr = (int[]) exifAttribute2.getValue(this.mExifByteOrder);
        if (Arrays.equals(BITS_PER_SAMPLE_RGB, iArr)) {
            return true;
        }
        if (this.mMimeType != 3 || (exifAttribute = (ExifAttribute) map.get("PhotometricInterpretation")) == null) {
            return false;
        }
        int intValue = exifAttribute.getIntValue(this.mExifByteOrder);
        return (intValue == 1 && Arrays.equals(iArr, BITS_PER_SAMPLE_GREYSCALE_2)) || (intValue == 6 && Arrays.equals(iArr, BITS_PER_SAMPLE_RGB));
    }

    private boolean isThumbnail(HashMap map) throws IOException {
        ExifAttribute exifAttribute = (ExifAttribute) map.get("ImageLength");
        ExifAttribute exifAttribute2 = (ExifAttribute) map.get("ImageWidth");
        if (exifAttribute == null || exifAttribute2 == null) {
            return false;
        }
        return exifAttribute.getIntValue(this.mExifByteOrder) <= 512 && exifAttribute2.getIntValue(this.mExifByteOrder) <= 512;
    }

    private void loadAttributes(InputStream inputStream) throws IOException {
        for (int i = 0; i < EXIF_TAGS.length; i++) {
            try {
                try {
                    this.mAttributes[i] = new HashMap();
                } catch (IOException unused) {
                    this.mIsSupportedFile = false;
                }
            } catch (Throwable th) {
                addDefaultValuesForCompatibility();
                throw th;
            }
        }
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, 5000);
        this.mMimeType = getMimeType(bufferedInputStream);
        ByteOrderedDataInputStream byteOrderedDataInputStream = new ByteOrderedDataInputStream(bufferedInputStream);
        switch (this.mMimeType) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 5:
            case 6:
            case 8:
            case 11:
                getRawAttributes(byteOrderedDataInputStream);
                break;
            case 4:
                getJpegAttributes(byteOrderedDataInputStream, 0, 0);
                break;
            case 7:
                getOrfAttributes(byteOrderedDataInputStream);
                break;
            case 9:
                getRafAttributes(byteOrderedDataInputStream);
                break;
            case 10:
                getRw2Attributes(byteOrderedDataInputStream);
                break;
            case 12:
                getHeifAttributes(byteOrderedDataInputStream);
                break;
        }
        setThumbnailData(byteOrderedDataInputStream);
        this.mIsSupportedFile = true;
        addDefaultValuesForCompatibility();
    }

    private void parseTiffHeaders(ByteOrderedDataInputStream byteOrderedDataInputStream, int i) throws IOException {
        this.mExifByteOrder = readByteOrder(byteOrderedDataInputStream);
        byteOrderedDataInputStream.setByteOrder(this.mExifByteOrder);
        int unsignedShort = byteOrderedDataInputStream.readUnsignedShort();
        if (this.mMimeType != 7 && this.mMimeType != 10 && unsignedShort != 42) {
            throw new IOException("Invalid start code: " + Integer.toHexString(unsignedShort));
        }
        int i2 = byteOrderedDataInputStream.readInt();
        if (i2 < 8 || i2 >= i) {
            throw new IOException("Invalid first Ifd offset: " + i2);
        }
        int i3 = i2 - 8;
        if (i3 <= 0 || byteOrderedDataInputStream.skipBytes(i3) == i3) {
            return;
        }
        throw new IOException("Couldn't jump to first Ifd: " + i3);
    }

    private void printAttributes() {
        for (int i = 0; i < this.mAttributes.length; i++) {
            Log.d(TAG, "The size of tag group[" + i + "]: " + this.mAttributes[i].size());
            for (Map.Entry entry : this.mAttributes[i].entrySet()) {
                ExifAttribute exifAttribute = (ExifAttribute) entry.getValue();
                Log.d(TAG, "tagName: " + entry.getKey() + ", tagType: " + exifAttribute.toString() + ", tagValue: '" + exifAttribute.getStringValue(this.mExifByteOrder) + "'");
            }
        }
    }

    private ByteOrder readByteOrder(ByteOrderedDataInputStream byteOrderedDataInputStream) throws IOException {
        short s = byteOrderedDataInputStream.readShort();
        if (s == 18761) {
            return ByteOrder.LITTLE_ENDIAN;
        }
        if (s == 19789) {
            return ByteOrder.BIG_ENDIAN;
        }
        throw new IOException("Invalid byte order: " + Integer.toHexString(s));
    }

    private void readExifSegment(byte[] bArr, int i) throws IOException {
        ByteOrderedDataInputStream byteOrderedDataInputStream = new ByteOrderedDataInputStream(bArr);
        parseTiffHeaders(byteOrderedDataInputStream, bArr.length);
        readImageFileDirectory(byteOrderedDataInputStream, i);
    }

    /* JADX WARN: Code duplicated, block: B:113:0x0266 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:29:0x00bd  */
    /* JADX WARN: Code duplicated, block: B:30:0x00c4  */
    /* JADX WARN: Code duplicated, block: B:32:0x00ca  */
    /* JADX WARN: Code duplicated, block: B:34:0x00d3  */
    /* JADX WARN: Code duplicated, block: B:36:0x00d9  */
    /* JADX WARN: Code duplicated, block: B:38:0x00e0  */
    /* JADX WARN: Code duplicated, block: B:43:0x0127  */
    /* JADX WARN: Code duplicated, block: B:50:0x0147  */
    /* JADX WARN: Code duplicated, block: B:51:0x014b  */
    /* JADX WARN: Code duplicated, block: B:52:0x0166  */
    /* JADX WARN: Code duplicated, block: B:55:0x017a  */
    /* JADX WARN: Code duplicated, block: B:58:0x0182  */
    /* JADX WARN: Code duplicated, block: B:59:0x0188  */
    /* JADX WARN: Code duplicated, block: B:60:0x018e  */
    /* JADX WARN: Code duplicated, block: B:61:0x0193  */
    /* JADX WARN: Code duplicated, block: B:70:0x01ef  */
    /* JADX WARN: Code duplicated, block: B:72:0x0209  */
    /* JADX WARN: Code duplicated, block: B:74:0x0228  */
    /* JADX WARN: Code duplicated, block: B:88:0x0263  */
    /* JADX WARN: Instruction removed from duplicated block: B:51:0x014b, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:70:0x01ef, please report this as an issue */
    private void readImageFileDirectory(ByteOrderedDataInputStream byteOrderedDataInputStream, int i) throws IOException {
        long j;
        boolean z;
        short s;
        int i2;
        int i3;
        Integer num;
        long unsignedShort;
        int i4;
        long j2;
        if (byteOrderedDataInputStream.mPosition + 2 > byteOrderedDataInputStream.mLength) {
            return;
        }
        short s2 = byteOrderedDataInputStream.readShort();
        if (byteOrderedDataInputStream.mPosition + (s2 * 12) > byteOrderedDataInputStream.mLength || s2 <= 0) {
            return;
        }
        short s3 = 0;
        while (s3 < s2) {
            int unsignedShort2 = byteOrderedDataInputStream.readUnsignedShort();
            int unsignedShort3 = byteOrderedDataInputStream.readUnsignedShort();
            int i5 = byteOrderedDataInputStream.readInt();
            long jPeek = byteOrderedDataInputStream.peek() + 4;
            ExifTag exifTag = (ExifTag) sExifTagMapsForReading[i].get(Integer.valueOf(unsignedShort2));
            if (exifTag == null) {
                Log.w(TAG, "Skip the tag entry since tag number is not defined: " + unsignedShort2);
            } else {
                if (unsignedShort3 <= 0 || unsignedShort3 >= IFD_FORMAT_BYTES_PER_FORMAT.length) {
                    Log.w(TAG, "Skip the tag entry since data format is invalid: " + unsignedShort3);
                } else {
                    s2 = s2;
                    j = ((long) i5) * ((long) IFD_FORMAT_BYTES_PER_FORMAT[unsignedShort3]);
                    if (j < 0 || j > 2147483647L) {
                        Log.w(TAG, "Skip the tag entry since the number of components is invalid: " + i5);
                        z = false;
                    } else {
                        z = true;
                    }
                }
                if (z) {
                    if (j > 4) {
                        i4 = byteOrderedDataInputStream.readInt();
                        if (this.mMimeType == 7) {
                            if (exifTag.name == "MakerNote") {
                                this.mOrfMakerNoteOffset = i4;
                            } else if (i != 6 && exifTag.name == "ThumbnailImage") {
                                this.mOrfThumbnailOffset = i4;
                                this.mOrfThumbnailLength = i5;
                                ExifAttribute exifAttributeCreateUShort = ExifAttribute.createUShort(6, this.mExifByteOrder);
                                i2 = unsignedShort3;
                                i3 = i5;
                                ExifAttribute exifAttributeCreateULong = ExifAttribute.createULong(this.mOrfThumbnailOffset, this.mExifByteOrder);
                                ExifAttribute exifAttributeCreateULong2 = ExifAttribute.createULong(this.mOrfThumbnailLength, this.mExifByteOrder);
                                this.mAttributes[4].put("Compression", exifAttributeCreateUShort);
                                this.mAttributes[4].put("JPEGInterchangeFormat", exifAttributeCreateULong);
                                this.mAttributes[4].put("JPEGInterchangeFormatLength", exifAttributeCreateULong2);
                            }
                            i2 = unsignedShort3;
                            i3 = i5;
                        } else {
                            i2 = unsignedShort3;
                            i3 = i5;
                            if (this.mMimeType == 10 && exifTag.name == "JpgFromRaw") {
                                this.mRw2JpgFromRawOffset = i4;
                            }
                        }
                        j2 = i4;
                        s = s3;
                        if (j2 + j <= byteOrderedDataInputStream.mLength) {
                            byteOrderedDataInputStream.seek(j2);
                        } else {
                            Log.w(TAG, "Skip the tag entry since data offset is invalid: " + i4);
                            byteOrderedDataInputStream.seek(jPeek);
                        }
                    } else {
                        s = s3;
                        i2 = unsignedShort3;
                        i3 = i5;
                    }
                    num = sExifPointerTagMap.get(Integer.valueOf(unsignedShort2));
                    if (num != null) {
                        unsignedShort = -1;
                        switch (i2) {
                            case 3:
                                unsignedShort = byteOrderedDataInputStream.readUnsignedShort();
                                break;
                            case 4:
                                unsignedShort = byteOrderedDataInputStream.readUnsignedInt();
                                break;
                            case 8:
                                unsignedShort = byteOrderedDataInputStream.readShort();
                                break;
                            case 9:
                            case 13:
                                unsignedShort = byteOrderedDataInputStream.readInt();
                                break;
                        }
                        if (unsignedShort > 0 || unsignedShort >= byteOrderedDataInputStream.mLength) {
                            Log.w(TAG, "Skip jump into the IFD since its offset is invalid: " + unsignedShort);
                        } else if (this.mAttributesOffsets.contains(Integer.valueOf((int) unsignedShort))) {
                            Log.w(TAG, "Skip jump into the IFD since it has already been read: IfdType " + num + " (at " + unsignedShort + ")");
                        } else {
                            this.mAttributesOffsets.add(Integer.valueOf(byteOrderedDataInputStream.mPosition));
                            byteOrderedDataInputStream.seek(unsignedShort);
                            readImageFileDirectory(byteOrderedDataInputStream, num.intValue());
                        }
                        byteOrderedDataInputStream.seek(jPeek);
                    } else {
                        byte[] bArr = new byte[(int) j];
                        byteOrderedDataInputStream.readFully(bArr);
                        ExifAttribute exifAttribute = new ExifAttribute(i2, i3, bArr);
                        this.mAttributes[i].put(exifTag.name, exifAttribute);
                        if (exifTag.name == "DNGVersion") {
                            this.mMimeType = 3;
                        }
                        if (((exifTag.name != "Make" || exifTag.name == "Model") && exifAttribute.getStringValue(this.mExifByteOrder).contains(PEF_SIGNATURE)) || (exifTag.name == "Compression" && exifAttribute.getIntValue(this.mExifByteOrder) == 65535)) {
                            this.mMimeType = 8;
                        }
                        if (byteOrderedDataInputStream.peek() != jPeek) {
                            byteOrderedDataInputStream.seek(jPeek);
                        }
                    }
                } else {
                    byteOrderedDataInputStream.seek(jPeek);
                    s = s3;
                }
                s3 = (short) (s + 1);
                s2 = s2;
            }
            z = false;
            j = 0;
            if (z) {
                byteOrderedDataInputStream.seek(jPeek);
                s = s3;
            } else {
                if (j > 4) {
                    i4 = byteOrderedDataInputStream.readInt();
                    if (this.mMimeType == 7) {
                        if (exifTag.name == "MakerNote") {
                            this.mOrfMakerNoteOffset = i4;
                        } else if (i != 6) {
                        }
                        i2 = unsignedShort3;
                        i3 = i5;
                    } else {
                        i2 = unsignedShort3;
                        i3 = i5;
                        if (this.mMimeType == 10) {
                            this.mRw2JpgFromRawOffset = i4;
                        }
                    }
                    j2 = i4;
                    s = s3;
                    if (j2 + j <= byteOrderedDataInputStream.mLength) {
                        byteOrderedDataInputStream.seek(j2);
                    } else {
                        Log.w(TAG, "Skip the tag entry since data offset is invalid: " + i4);
                        byteOrderedDataInputStream.seek(jPeek);
                    }
                } else {
                    s = s3;
                    i2 = unsignedShort3;
                    i3 = i5;
                }
                num = sExifPointerTagMap.get(Integer.valueOf(unsignedShort2));
                if (num != null) {
                    unsignedShort = -1;
                    switch (i2) {
                        case 3:
                            unsignedShort = byteOrderedDataInputStream.readUnsignedShort();
                            break;
                        case 4:
                            unsignedShort = byteOrderedDataInputStream.readUnsignedInt();
                            break;
                        case 8:
                            unsignedShort = byteOrderedDataInputStream.readShort();
                            break;
                        case 9:
                        case 13:
                            unsignedShort = byteOrderedDataInputStream.readInt();
                            break;
                    }
                    if (unsignedShort > 0) {
                        Log.w(TAG, "Skip jump into the IFD since its offset is invalid: " + unsignedShort);
                    } else {
                        Log.w(TAG, "Skip jump into the IFD since its offset is invalid: " + unsignedShort);
                    }
                    byteOrderedDataInputStream.seek(jPeek);
                } else {
                    byte[] bArr2 = new byte[(int) j];
                    byteOrderedDataInputStream.readFully(bArr2);
                    ExifAttribute exifAttribute2 = new ExifAttribute(i2, i3, bArr2);
                    this.mAttributes[i].put(exifTag.name, exifAttribute2);
                    if (exifTag.name == "DNGVersion") {
                        this.mMimeType = 3;
                    }
                    if (exifTag.name != "Make") {
                    }
                    this.mMimeType = 8;
                    if (byteOrderedDataInputStream.peek() != jPeek) {
                        byteOrderedDataInputStream.seek(jPeek);
                    }
                }
            }
            s3 = (short) (s + 1);
            s2 = s2;
        }
        if (byteOrderedDataInputStream.peek() + 4 <= byteOrderedDataInputStream.mLength) {
            int i6 = byteOrderedDataInputStream.readInt();
            long j3 = i6;
            if (j3 <= 0 || i6 >= byteOrderedDataInputStream.mLength) {
                Log.w(TAG, "Stop reading file since a wrong offset may cause an infinite loop: " + i6);
                return;
            }
            if (this.mAttributesOffsets.contains(Integer.valueOf(i6))) {
                Log.w(TAG, "Stop reading file since re-reading an IFD may cause an infinite loop: " + i6);
                return;
            }
            this.mAttributesOffsets.add(Integer.valueOf(byteOrderedDataInputStream.mPosition));
            byteOrderedDataInputStream.seek(j3);
            if (this.mAttributes[4].isEmpty()) {
                readImageFileDirectory(byteOrderedDataInputStream, 4);
            } else if (this.mAttributes[5].isEmpty()) {
                readImageFileDirectory(byteOrderedDataInputStream, 5);
            }
        }
    }

    private void removeAttribute(String str) {
        for (int i = 0; i < EXIF_TAGS.length; i++) {
            this.mAttributes[i].remove(str);
        }
    }

    private void retrieveJpegImageSize(ByteOrderedDataInputStream byteOrderedDataInputStream, int i) throws IOException {
        ExifAttribute exifAttribute;
        ExifAttribute exifAttribute2 = (ExifAttribute) this.mAttributes[i].get("ImageLength");
        ExifAttribute exifAttribute3 = (ExifAttribute) this.mAttributes[i].get("ImageWidth");
        if ((exifAttribute2 == null || exifAttribute3 == null) && (exifAttribute = (ExifAttribute) this.mAttributes[i].get("JPEGInterchangeFormat")) != null) {
            getJpegAttributes(byteOrderedDataInputStream, exifAttribute.getIntValue(this.mExifByteOrder), i);
        }
    }

    private void saveJpegAttributes(InputStream inputStream, OutputStream outputStream) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        ByteOrderedDataOutputStream byteOrderedDataOutputStream = new ByteOrderedDataOutputStream(outputStream, ByteOrder.BIG_ENDIAN);
        if (dataInputStream.readByte() != -1) {
            throw new IOException("Invalid marker");
        }
        byteOrderedDataOutputStream.writeByte(-1);
        if (dataInputStream.readByte() != -40) {
            throw new IOException("Invalid marker");
        }
        byteOrderedDataOutputStream.writeByte(-40);
        byteOrderedDataOutputStream.writeByte(-1);
        byteOrderedDataOutputStream.writeByte(-31);
        writeExifSegment(byteOrderedDataOutputStream, 6);
        byte[] bArr = new byte[4096];
        while (dataInputStream.readByte() == -1) {
            byte b = dataInputStream.readByte();
            if (b != -31) {
                switch (b) {
                    case KeymasterDefs.KM_ERROR_UNSUPPORTED_TAG /* -39 */:
                    case -38:
                        byteOrderedDataOutputStream.writeByte(-1);
                        byteOrderedDataOutputStream.writeByte(b);
                        Streams.copy(dataInputStream, byteOrderedDataOutputStream);
                        return;
                    default:
                        byteOrderedDataOutputStream.writeByte(-1);
                        byteOrderedDataOutputStream.writeByte(b);
                        int unsignedShort = dataInputStream.readUnsignedShort();
                        byteOrderedDataOutputStream.writeUnsignedShort(unsignedShort);
                        int i = unsignedShort - 2;
                        if (i < 0) {
                            throw new IOException("Invalid length");
                        }
                        while (i > 0) {
                            int i2 = dataInputStream.read(bArr, 0, Math.min(i, bArr.length));
                            if (i2 < 0) {
                            }
                            byteOrderedDataOutputStream.write(bArr, 0, i2);
                            i -= i2;
                            break;
                        }
                        break;
                        break;
                }
            } else {
                int unsignedShort2 = dataInputStream.readUnsignedShort() - 2;
                if (unsignedShort2 < 0) {
                    throw new IOException("Invalid length");
                }
                byte[] bArr2 = new byte[6];
                if (unsignedShort2 >= 6) {
                    if (dataInputStream.read(bArr2) != 6) {
                        throw new IOException("Invalid exif");
                    }
                    if (Arrays.equals(bArr2, IDENTIFIER_EXIF_APP1)) {
                        int i3 = unsignedShort2 - 6;
                        if (dataInputStream.skipBytes(i3) != i3) {
                            throw new IOException("Invalid length");
                        }
                    }
                }
                byteOrderedDataOutputStream.writeByte(-1);
                byteOrderedDataOutputStream.writeByte(b);
                byteOrderedDataOutputStream.writeUnsignedShort(unsignedShort2 + 2);
                if (unsignedShort2 >= 6) {
                    unsignedShort2 -= 6;
                    byteOrderedDataOutputStream.write(bArr2);
                }
                while (unsignedShort2 > 0) {
                    int i4 = dataInputStream.read(bArr, 0, Math.min(unsignedShort2, bArr.length));
                    if (i4 < 0) {
                        break;
                    }
                    byteOrderedDataOutputStream.write(bArr, 0, i4);
                    unsignedShort2 -= i4;
                }
            }
        }
        throw new IOException("Invalid marker");
    }

    private void setThumbnailData(ByteOrderedDataInputStream byteOrderedDataInputStream) throws Throwable {
        HashMap map = this.mAttributes[4];
        ExifAttribute exifAttribute = (ExifAttribute) map.get("Compression");
        if (exifAttribute == null) {
            handleThumbnailFromJfif(byteOrderedDataInputStream, map);
            return;
        }
        this.mThumbnailCompression = exifAttribute.getIntValue(this.mExifByteOrder);
        int i = this.mThumbnailCompression;
        if (i != 1) {
            switch (i) {
                case 6:
                    handleThumbnailFromJfif(byteOrderedDataInputStream, map);
                    break;
            }
            return;
        }
        if (isSupportedDataType(map)) {
            handleThumbnailFromStrips(byteOrderedDataInputStream, map);
        }
    }

    private void swapBasedOnImageSize(int i, int i2) throws Throwable {
        if (this.mAttributes[i].isEmpty() || this.mAttributes[i2].isEmpty()) {
            return;
        }
        ExifAttribute exifAttribute = (ExifAttribute) this.mAttributes[i].get("ImageLength");
        ExifAttribute exifAttribute2 = (ExifAttribute) this.mAttributes[i].get("ImageWidth");
        ExifAttribute exifAttribute3 = (ExifAttribute) this.mAttributes[i2].get("ImageLength");
        ExifAttribute exifAttribute4 = (ExifAttribute) this.mAttributes[i2].get("ImageWidth");
        if (exifAttribute == null || exifAttribute2 == null || exifAttribute3 == null || exifAttribute4 == null) {
            return;
        }
        int intValue = exifAttribute.getIntValue(this.mExifByteOrder);
        int intValue2 = exifAttribute2.getIntValue(this.mExifByteOrder);
        int intValue3 = exifAttribute3.getIntValue(this.mExifByteOrder);
        int intValue4 = exifAttribute4.getIntValue(this.mExifByteOrder);
        if (intValue >= intValue3 || intValue2 >= intValue4) {
            return;
        }
        HashMap map = this.mAttributes[i];
        this.mAttributes[i] = this.mAttributes[i2];
        this.mAttributes[i2] = map;
    }

    private boolean updateAttribute(String str, ExifAttribute exifAttribute) {
        boolean z = false;
        for (int i = 0; i < EXIF_TAGS.length; i++) {
            if (this.mAttributes[i].containsKey(str)) {
                this.mAttributes[i].put(str, exifAttribute);
                z = true;
            }
        }
        return z;
    }

    private void updateImageSizeValues(ByteOrderedDataInputStream byteOrderedDataInputStream, int i) throws Throwable {
        ExifAttribute exifAttributeCreateUShort;
        ExifAttribute exifAttributeCreateUShort2;
        ExifAttribute exifAttribute = (ExifAttribute) this.mAttributes[i].get("DefaultCropSize");
        ExifAttribute exifAttribute2 = (ExifAttribute) this.mAttributes[i].get("SensorTopBorder");
        ExifAttribute exifAttribute3 = (ExifAttribute) this.mAttributes[i].get("SensorLeftBorder");
        ExifAttribute exifAttribute4 = (ExifAttribute) this.mAttributes[i].get("SensorBottomBorder");
        ExifAttribute exifAttribute5 = (ExifAttribute) this.mAttributes[i].get("SensorRightBorder");
        if (exifAttribute != null) {
            if (exifAttribute.format == 5) {
                Rational[] rationalArr = (Rational[]) exifAttribute.getValue(this.mExifByteOrder);
                exifAttributeCreateUShort = ExifAttribute.createURational(rationalArr[0], this.mExifByteOrder);
                exifAttributeCreateUShort2 = ExifAttribute.createURational(rationalArr[1], this.mExifByteOrder);
            } else {
                int[] iArr = (int[]) exifAttribute.getValue(this.mExifByteOrder);
                exifAttributeCreateUShort = ExifAttribute.createUShort(iArr[0], this.mExifByteOrder);
                exifAttributeCreateUShort2 = ExifAttribute.createUShort(iArr[1], this.mExifByteOrder);
            }
            this.mAttributes[i].put("ImageWidth", exifAttributeCreateUShort);
            this.mAttributes[i].put("ImageLength", exifAttributeCreateUShort2);
            return;
        }
        if (exifAttribute2 == null || exifAttribute3 == null || exifAttribute4 == null || exifAttribute5 == null) {
            retrieveJpegImageSize(byteOrderedDataInputStream, i);
            return;
        }
        int intValue = exifAttribute2.getIntValue(this.mExifByteOrder);
        int intValue2 = exifAttribute4.getIntValue(this.mExifByteOrder);
        int intValue3 = exifAttribute5.getIntValue(this.mExifByteOrder);
        int intValue4 = exifAttribute3.getIntValue(this.mExifByteOrder);
        if (intValue2 <= intValue || intValue3 <= intValue4) {
            return;
        }
        ExifAttribute exifAttributeCreateUShort3 = ExifAttribute.createUShort(intValue2 - intValue, this.mExifByteOrder);
        ExifAttribute exifAttributeCreateUShort4 = ExifAttribute.createUShort(intValue3 - intValue4, this.mExifByteOrder);
        this.mAttributes[i].put("ImageLength", exifAttributeCreateUShort3);
        this.mAttributes[i].put("ImageWidth", exifAttributeCreateUShort4);
    }

    private void validateImages(InputStream inputStream) throws Throwable {
        swapBasedOnImageSize(0, 5);
        swapBasedOnImageSize(0, 4);
        swapBasedOnImageSize(5, 4);
        ExifAttribute exifAttribute = (ExifAttribute) this.mAttributes[1].get("PixelXDimension");
        ExifAttribute exifAttribute2 = (ExifAttribute) this.mAttributes[1].get("PixelYDimension");
        if (exifAttribute != null && exifAttribute2 != null) {
            this.mAttributes[0].put("ImageWidth", exifAttribute);
            this.mAttributes[0].put("ImageLength", exifAttribute2);
        }
        if (this.mAttributes[4].isEmpty() && isThumbnail(this.mAttributes[5])) {
            this.mAttributes[4] = this.mAttributes[5];
            this.mAttributes[5] = new HashMap();
        }
        if (isThumbnail(this.mAttributes[4])) {
            return;
        }
        Log.d(TAG, "No image meets the size requirements of a thumbnail image.");
    }

    private int writeExifSegment(ByteOrderedDataOutputStream byteOrderedDataOutputStream, int i) throws IOException {
        int[] iArr = new int[EXIF_TAGS.length];
        int[] iArr2 = new int[EXIF_TAGS.length];
        for (ExifTag exifTag : EXIF_POINTER_TAGS) {
            removeAttribute(exifTag.name);
        }
        removeAttribute(JPEG_INTERCHANGE_FORMAT_TAG.name);
        removeAttribute(JPEG_INTERCHANGE_FORMAT_LENGTH_TAG.name);
        for (int i2 = 0; i2 < EXIF_TAGS.length; i2++) {
            for (Object obj : this.mAttributes[i2].entrySet().toArray()) {
                Map.Entry entry = (Map.Entry) obj;
                if (entry.getValue() == null) {
                    this.mAttributes[i2].remove(entry.getKey());
                }
            }
        }
        if (!this.mAttributes[1].isEmpty()) {
            this.mAttributes[0].put(EXIF_POINTER_TAGS[1].name, ExifAttribute.createULong(0L, this.mExifByteOrder));
        }
        if (!this.mAttributes[2].isEmpty()) {
            this.mAttributes[0].put(EXIF_POINTER_TAGS[2].name, ExifAttribute.createULong(0L, this.mExifByteOrder));
        }
        if (!this.mAttributes[3].isEmpty()) {
            this.mAttributes[1].put(EXIF_POINTER_TAGS[3].name, ExifAttribute.createULong(0L, this.mExifByteOrder));
        }
        if (this.mHasThumbnail) {
            this.mAttributes[4].put(JPEG_INTERCHANGE_FORMAT_TAG.name, ExifAttribute.createULong(0L, this.mExifByteOrder));
            this.mAttributes[4].put(JPEG_INTERCHANGE_FORMAT_LENGTH_TAG.name, ExifAttribute.createULong(this.mThumbnailLength, this.mExifByteOrder));
        }
        for (int i3 = 0; i3 < EXIF_TAGS.length; i3++) {
            Iterator it = this.mAttributes[i3].entrySet().iterator();
            int i4 = 0;
            while (it.hasNext()) {
                int size = ((ExifAttribute) ((Map.Entry) it.next()).getValue()).size();
                if (size > 4) {
                    i4 += size;
                }
            }
            iArr2[i3] = iArr2[i3] + i4;
        }
        int size2 = 8;
        for (int i5 = 0; i5 < EXIF_TAGS.length; i5++) {
            if (!this.mAttributes[i5].isEmpty()) {
                iArr[i5] = size2;
                size2 += (this.mAttributes[i5].size() * 12) + 2 + 4 + iArr2[i5];
            }
        }
        if (this.mHasThumbnail) {
            this.mAttributes[4].put(JPEG_INTERCHANGE_FORMAT_TAG.name, ExifAttribute.createULong(size2, this.mExifByteOrder));
            this.mThumbnailOffset = i + size2;
            size2 += this.mThumbnailLength;
        }
        int i6 = size2 + 8;
        if (!this.mAttributes[1].isEmpty()) {
            this.mAttributes[0].put(EXIF_POINTER_TAGS[1].name, ExifAttribute.createULong(iArr[1], this.mExifByteOrder));
        }
        if (!this.mAttributes[2].isEmpty()) {
            this.mAttributes[0].put(EXIF_POINTER_TAGS[2].name, ExifAttribute.createULong(iArr[2], this.mExifByteOrder));
        }
        if (!this.mAttributes[3].isEmpty()) {
            this.mAttributes[1].put(EXIF_POINTER_TAGS[3].name, ExifAttribute.createULong(iArr[3], this.mExifByteOrder));
        }
        byteOrderedDataOutputStream.writeUnsignedShort(i6);
        byteOrderedDataOutputStream.write(IDENTIFIER_EXIF_APP1);
        byteOrderedDataOutputStream.writeShort(this.mExifByteOrder == ByteOrder.BIG_ENDIAN ? BYTE_ALIGN_MM : BYTE_ALIGN_II);
        byteOrderedDataOutputStream.setByteOrder(this.mExifByteOrder);
        byteOrderedDataOutputStream.writeUnsignedShort(42);
        byteOrderedDataOutputStream.writeUnsignedInt(8L);
        for (int i7 = 0; i7 < EXIF_TAGS.length; i7++) {
            if (!this.mAttributes[i7].isEmpty()) {
                byteOrderedDataOutputStream.writeUnsignedShort(this.mAttributes[i7].size());
                int size3 = iArr[i7] + 2 + (this.mAttributes[i7].size() * 12) + 4;
                for (Map.Entry entry2 : this.mAttributes[i7].entrySet()) {
                    int i8 = ((ExifTag) sExifTagMapsForWriting[i7].get(entry2.getKey())).number;
                    ExifAttribute exifAttribute = (ExifAttribute) entry2.getValue();
                    int size4 = exifAttribute.size();
                    byteOrderedDataOutputStream.writeUnsignedShort(i8);
                    byteOrderedDataOutputStream.writeUnsignedShort(exifAttribute.format);
                    byteOrderedDataOutputStream.writeInt(exifAttribute.numberOfComponents);
                    if (size4 > 4) {
                        byteOrderedDataOutputStream.writeUnsignedInt(size3);
                        size3 += size4;
                    } else {
                        byteOrderedDataOutputStream.write(exifAttribute.bytes);
                        if (size4 < 4) {
                            while (size4 < 4) {
                                byteOrderedDataOutputStream.writeByte(0);
                                size4++;
                            }
                        }
                    }
                }
                if (i7 != 0 || this.mAttributes[4].isEmpty()) {
                    byteOrderedDataOutputStream.writeUnsignedInt(0L);
                } else {
                    byteOrderedDataOutputStream.writeUnsignedInt(iArr[4]);
                }
                Iterator it2 = this.mAttributes[i7].entrySet().iterator();
                while (it2.hasNext()) {
                    ExifAttribute exifAttribute2 = (ExifAttribute) ((Map.Entry) it2.next()).getValue();
                    if (exifAttribute2.bytes.length > 4) {
                        byteOrderedDataOutputStream.write(exifAttribute2.bytes, 0, exifAttribute2.bytes.length);
                    }
                }
            }
        }
        if (this.mHasThumbnail) {
            byteOrderedDataOutputStream.write(getThumbnailBytes());
        }
        byteOrderedDataOutputStream.setByteOrder(ByteOrder.BIG_ENDIAN);
        return i6;
    }

    public double getAltitude(double d) {
        double attributeDouble = getAttributeDouble("GPSAltitude", -1.0d);
        int attributeInt = getAttributeInt("GPSAltitudeRef", -1);
        if (attributeDouble < 0.0d || attributeInt < 0) {
            return d;
        }
        return attributeDouble * ((double) (attributeInt == 1 ? -1 : 1));
    }

    public String getAttribute(String str) {
        ExifAttribute exifAttribute = getExifAttribute(str);
        if (exifAttribute == null) {
            return null;
        }
        if (!sTagSetForCompatibility.contains(str)) {
            return exifAttribute.getStringValue(this.mExifByteOrder);
        }
        if (!str.equals("GPSTimeStamp")) {
            try {
                return Double.toString(exifAttribute.getDoubleValue(this.mExifByteOrder));
            } catch (NumberFormatException unused) {
                return null;
            }
        }
        if (exifAttribute.format != 5 && exifAttribute.format != 10) {
            return null;
        }
        Rational[] rationalArr = (Rational[]) exifAttribute.getValue(this.mExifByteOrder);
        if (rationalArr.length != 3) {
            return null;
        }
        return String.format("%02d:%02d:%02d", Integer.valueOf((int) (rationalArr[0].numerator / rationalArr[0].denominator)), Integer.valueOf((int) (rationalArr[1].numerator / rationalArr[1].denominator)), Integer.valueOf((int) (rationalArr[2].numerator / rationalArr[2].denominator)));
    }

    public double getAttributeDouble(String str, double d) {
        ExifAttribute exifAttribute = getExifAttribute(str);
        if (exifAttribute == null) {
            return d;
        }
        try {
            return exifAttribute.getDoubleValue(this.mExifByteOrder);
        } catch (NumberFormatException unused) {
            return d;
        }
    }

    public int getAttributeInt(String str, int i) {
        ExifAttribute exifAttribute = getExifAttribute(str);
        if (exifAttribute == null) {
            return i;
        }
        try {
            return exifAttribute.getIntValue(this.mExifByteOrder);
        } catch (NumberFormatException unused) {
            return i;
        }
    }

    public long getDateTime() {
        String attribute = getAttribute("DateTime");
        if (attribute == null || !sNonZeroTimePattern.matcher(attribute).matches()) {
            return -1L;
        }
        try {
            Date date = sFormatter.parse(attribute, new ParsePosition(0));
            if (date == null) {
                return -1L;
            }
            long time = date.getTime();
            String attribute2 = getAttribute("SubSecTime");
            if (attribute2 == null) {
                return time;
            }
            try {
                long j = Long.parseLong(attribute2);
                while (j > 1000) {
                    j /= 10;
                }
                return time + j;
            } catch (NumberFormatException unused) {
                return time;
            }
        } catch (IllegalArgumentException unused2) {
            return -1L;
        }
    }

    public long getGpsDateTime() {
        String attribute = getAttribute("GPSDateStamp");
        String attribute2 = getAttribute("GPSTimeStamp");
        if (attribute == null || attribute2 == null || !(sNonZeroTimePattern.matcher(attribute).matches() || sNonZeroTimePattern.matcher(attribute2).matches())) {
            return -1L;
        }
        try {
            Date date = sFormatter.parse(attribute + ' ' + attribute2, new ParsePosition(0));
            if (date == null) {
                return -1L;
            }
            return date.getTime();
        } catch (IllegalArgumentException unused) {
            return -1L;
        }
    }

    public boolean getLatLong(float[] fArr) {
        String attribute = getAttribute("GPSLatitude");
        String attribute2 = getAttribute("GPSLatitudeRef");
        String attribute3 = getAttribute("GPSLongitude");
        String attribute4 = getAttribute("GPSLongitudeRef");
        if (attribute != null && attribute2 != null && attribute3 != null && attribute4 != null) {
            try {
                fArr[0] = convertRationalLatLonToFloat(attribute, attribute2);
                fArr[1] = convertRationalLatLonToFloat(attribute3, attribute4);
                return true;
            } catch (IllegalArgumentException unused) {
            }
        }
        return false;
    }

    public byte[] getThumbnail() {
        if (this.mThumbnailCompression == 6 || this.mThumbnailCompression == 7) {
            return getThumbnailBytes();
        }
        return null;
    }

    public Bitmap getThumbnailBitmap() throws Throwable {
        if (!this.mHasThumbnail) {
            return null;
        }
        if (this.mThumbnailBytes == null) {
            this.mThumbnailBytes = getThumbnailBytes();
        }
        if (this.mThumbnailCompression == 6 || this.mThumbnailCompression == 7) {
            return BitmapFactory.decodeByteArray(this.mThumbnailBytes, 0, this.mThumbnailLength);
        }
        if (this.mThumbnailCompression == 1) {
            int[] iArr = new int[this.mThumbnailBytes.length / 3];
            for (int i = 0; i < iArr.length; i++) {
                int i2 = i * 3;
                iArr[i] = ((this.mThumbnailBytes[i2] << WifiScanner.PnoSettings.PnoNetwork.FLAG_SAME_NETWORK) + 255 + (this.mThumbnailBytes[i2 + 1] << 8) + this.mThumbnailBytes[i2 + 2]) & 0;
            }
            ExifAttribute exifAttribute = (ExifAttribute) this.mAttributes[4].get("ImageLength");
            ExifAttribute exifAttribute2 = (ExifAttribute) this.mAttributes[4].get("ImageWidth");
            if (exifAttribute != null && exifAttribute2 != null) {
                return Bitmap.createBitmap(iArr, exifAttribute2.getIntValue(this.mExifByteOrder), exifAttribute.getIntValue(this.mExifByteOrder), Bitmap.Config.ARGB_8888);
            }
        }
        return null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v1, types: [byte[]] */
    /* JADX WARN: Type inference failed for: r0v2, types: [java.lang.AutoCloseable] */
    /* JADX WARN: Type inference failed for: r0v3 */
    public byte[] getThumbnailBytes() throws Throwable {
        InputStream fileInputStream;
        if (!this.mHasThumbnail) {
            return null;
        }
        ?? r0 = this.mThumbnailBytes;
        try {
            if (r0 != 0) {
                return this.mThumbnailBytes;
            }
            try {
                try {
                    if (this.mAssetInputStream != null) {
                        fileInputStream = this.mAssetInputStream;
                        try {
                            if (!fileInputStream.markSupported()) {
                                Log.d(TAG, "Cannot read thumbnail from inputstream without mark/reset support");
                                IoUtils.closeQuietly(fileInputStream);
                                return null;
                            }
                            fileInputStream.reset();
                        } catch (ErrnoException | IOException e) {
                            e = e;
                            Log.d(TAG, "Encountered exception while getting thumbnail", e);
                            IoUtils.closeQuietly(fileInputStream);
                            return null;
                        }
                    } else if (this.mFilename != null) {
                        fileInputStream = new FileInputStream(this.mFilename);
                    } else if (this.mSeekableFileDescriptor != null) {
                        FileDescriptor fileDescriptorDup = Os.dup(this.mSeekableFileDescriptor);
                        Os.lseek(fileDescriptorDup, 0L, OsConstants.SEEK_SET);
                        fileInputStream = new FileInputStream(fileDescriptorDup);
                    } else {
                        fileInputStream = null;
                    }
                    if (fileInputStream == null) {
                        throw new FileNotFoundException();
                    }
                    if (fileInputStream.skip(this.mThumbnailOffset) != this.mThumbnailOffset) {
                        throw new IOException("Corrupted image");
                    }
                    byte[] bArr = new byte[this.mThumbnailLength];
                    if (fileInputStream.read(bArr) != this.mThumbnailLength) {
                        throw new IOException("Corrupted image");
                    }
                    this.mThumbnailBytes = bArr;
                    IoUtils.closeQuietly(fileInputStream);
                    return bArr;
                } catch (ErrnoException | IOException e2) {
                    e = e2;
                    fileInputStream = null;
                }
            } catch (Throwable th) {
                th = th;
                r0 = 0;
                IoUtils.closeQuietly((AutoCloseable) r0);
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public long[] getThumbnailRange() {
        if (this.mHasThumbnail) {
            return new long[]{this.mThumbnailOffset, this.mThumbnailLength};
        }
        return null;
    }

    public boolean hasThumbnail() {
        return this.mHasThumbnail;
    }

    public boolean isThumbnailCompressed() {
        if (this.mHasThumbnail) {
            return this.mThumbnailCompression == 6 || this.mThumbnailCompression == 7;
        }
        return false;
    }

    /* JADX WARN: Code duplicated, block: B:42:0x00a8 A[Catch: all -> 0x00d9, ErrnoException -> 0x00db, TryCatch #9 {ErrnoException -> 0x00db, all -> 0x00d9, blocks: (B:40:0x00a4, B:42:0x00a8, B:43:0x00b0, B:45:0x00b4), top: B:102:0x00a4 }] */
    /* JADX WARN: Code duplicated, block: B:43:0x00b0 A[Catch: all -> 0x00d9, ErrnoException -> 0x00db, TryCatch #9 {ErrnoException -> 0x00db, all -> 0x00d9, blocks: (B:40:0x00a4, B:42:0x00a8, B:43:0x00b0, B:45:0x00b4), top: B:102:0x00a4 }] */
    /* JADX WARN: Code duplicated, block: B:45:0x00b4 A[Catch: all -> 0x00d9, ErrnoException -> 0x00db, TRY_LEAVE, TryCatch #9 {ErrnoException -> 0x00db, all -> 0x00d9, blocks: (B:40:0x00a4, B:42:0x00a8, B:43:0x00b0, B:45:0x00b4), top: B:102:0x00a4 }] */
    /* JADX WARN: Code duplicated, block: B:47:0x00c3  */
    /* JADX WARN: Code duplicated, block: B:51:0x00cf  */
    /* JADX WARN: Code duplicated, block: B:74:0x00f4  */
    public void saveAttributes() throws Throwable {
        FileOutputStream fileOutputStream;
        File fileCreateTempFile;
        FileInputStream fileInputStream;
        AutoCloseable autoCloseable;
        FileInputStream fileInputStream2;
        FileOutputStream fileOutputStream2;
        if (!this.mIsSupportedFile || this.mMimeType != 4) {
            throw new IOException("ExifInterface only supports saving attributes on JPEG formats.");
        }
        if (this.mIsInputStream || (this.mSeekableFileDescriptor == null && this.mFilename == null)) {
            throw new IOException("ExifInterface does not support saving attributes for the current input.");
        }
        this.mThumbnailBytes = getThumbnail();
        FileInputStream fileInputStream3 = null;
        fileOutputStream = null;
        FileInputStream fileInputStream4 = null;
        FileOutputStream fileOutputStream3 = null;
        fileInputStream3 = null;
        try {
            try {
                try {
                    try {
                        if (this.mFilename == null) {
                            if (this.mSeekableFileDescriptor != null) {
                                fileCreateTempFile = File.createTempFile("temp", "jpg");
                                Os.lseek(this.mSeekableFileDescriptor, 0L, OsConstants.SEEK_SET);
                                fileInputStream = new FileInputStream(this.mSeekableFileDescriptor);
                                try {
                                    fileOutputStream = new FileOutputStream(fileCreateTempFile);
                                    try {
                                        Streams.copy(fileInputStream, fileOutputStream);
                                        autoCloseable = fileOutputStream;
                                    } catch (ErrnoException e) {
                                        e = e;
                                        fileInputStream3 = fileInputStream;
                                        fileOutputStream = fileOutputStream;
                                        try {
                                            throw e.rethrowAsIOException();
                                        } catch (Throwable th) {
                                            th = th;
                                            IoUtils.closeQuietly(fileInputStream3);
                                            IoUtils.closeQuietly(fileOutputStream);
                                            throw th;
                                        }
                                    } catch (Throwable th2) {
                                        th = th2;
                                        fileInputStream3 = fileInputStream;
                                        IoUtils.closeQuietly(fileInputStream3);
                                        IoUtils.closeQuietly(fileOutputStream);
                                        throw th;
                                    }
                                } catch (ErrnoException e2) {
                                    e = e2;
                                    fileOutputStream = null;
                                } catch (Throwable th3) {
                                    th = th3;
                                    fileOutputStream = null;
                                }
                            } else {
                                fileCreateTempFile = null;
                                fileInputStream = null;
                            }
                            IoUtils.closeQuietly(fileInputStream);
                            IoUtils.closeQuietly(autoCloseable);
                            fileInputStream2 = new FileInputStream(fileCreateTempFile);
                            if (this.mFilename != null) {
                                fileOutputStream2 = new FileOutputStream(this.mFilename);
                            } else if (this.mSeekableFileDescriptor != null) {
                                Os.lseek(this.mSeekableFileDescriptor, 0L, OsConstants.SEEK_SET);
                                fileOutputStream2 = new FileOutputStream(this.mSeekableFileDescriptor);
                            } else {
                                fileOutputStream2 = null;
                            }
                            saveJpegAttributes(fileInputStream2, fileOutputStream2);
                            IoUtils.closeQuietly(fileInputStream2);
                            IoUtils.closeQuietly(fileOutputStream2);
                            if (fileCreateTempFile != null) {
                                fileCreateTempFile.delete();
                            }
                            this.mThumbnailBytes = null;
                            return;
                        }
                        fileCreateTempFile = new File(this.mFilename + DefaultDiskStorage.FileType.TEMP);
                        if (!new File(this.mFilename).renameTo(fileCreateTempFile)) {
                            throw new IOException("Could'nt rename to " + fileCreateTempFile.getAbsolutePath());
                        }
                        fileInputStream = null;
                        saveJpegAttributes(fileInputStream2, fileOutputStream2);
                        IoUtils.closeQuietly(fileInputStream2);
                        IoUtils.closeQuietly(fileOutputStream2);
                        if (fileCreateTempFile != null) {
                            fileCreateTempFile.delete();
                        }
                        this.mThumbnailBytes = null;
                        return;
                    } catch (ErrnoException e3) {
                        e = e3;
                        fileInputStream4 = fileInputStream2;
                        try {
                            throw e.rethrowAsIOException();
                        } catch (Throwable th4) {
                            th = th4;
                            fileInputStream2 = fileInputStream4;
                            fileOutputStream3 = fileOutputStream2;
                            IoUtils.closeQuietly(fileInputStream2);
                            IoUtils.closeQuietly(fileOutputStream3);
                            if (fileCreateTempFile != null) {
                                fileCreateTempFile.delete();
                            }
                            throw th;
                        }
                    } catch (Throwable th5) {
                        th = th5;
                        fileOutputStream3 = fileOutputStream2;
                        IoUtils.closeQuietly(fileInputStream2);
                        IoUtils.closeQuietly(fileOutputStream3);
                        if (fileCreateTempFile != null) {
                            fileCreateTempFile.delete();
                        }
                        throw th;
                    }
                    if (this.mFilename != null) {
                        fileOutputStream2 = new FileOutputStream(this.mFilename);
                    } else if (this.mSeekableFileDescriptor != null) {
                        Os.lseek(this.mSeekableFileDescriptor, 0L, OsConstants.SEEK_SET);
                        fileOutputStream2 = new FileOutputStream(this.mSeekableFileDescriptor);
                    } else {
                        fileOutputStream2 = null;
                    }
                } catch (ErrnoException e4) {
                    e = e4;
                    fileOutputStream2 = null;
                } catch (Throwable th6) {
                    th = th6;
                    IoUtils.closeQuietly(fileInputStream2);
                    IoUtils.closeQuietly(fileOutputStream3);
                    if (fileCreateTempFile != null) {
                        fileCreateTempFile.delete();
                    }
                    throw th;
                }
                fileInputStream2 = new FileInputStream(fileCreateTempFile);
            } catch (ErrnoException e5) {
                e = e5;
                fileOutputStream2 = null;
            } catch (Throwable th7) {
                th = th7;
                fileInputStream2 = null;
            }
            autoCloseable = fileInputStream;
            IoUtils.closeQuietly(fileInputStream);
            IoUtils.closeQuietly(autoCloseable);
        } catch (ErrnoException e6) {
            e = e6;
            fileOutputStream = null;
        } catch (Throwable th8) {
            th = th8;
            fileOutputStream = null;
        }
    }

    public void setAttribute(String str, String str2) {
        Object obj;
        int i;
        String str3 = str2;
        if (str3 != null && sTagSetForCompatibility.contains(str)) {
            if (str.equals("GPSTimeStamp")) {
                Matcher matcher = sGpsTimestampPattern.matcher(str3);
                if (!matcher.find()) {
                    Log.w(TAG, "Invalid value for " + str + " : " + str3);
                    return;
                }
                str3 = Integer.parseInt(matcher.group(1)) + "/1," + Integer.parseInt(matcher.group(2)) + "/1," + Integer.parseInt(matcher.group(3)) + "/1";
            } else {
                try {
                    str3 = ((long) (Double.parseDouble(str2) * 10000.0d)) + "/10000";
                } catch (NumberFormatException unused) {
                    Log.w(TAG, "Invalid value for " + str + " : " + str3);
                    return;
                }
            }
        }
        for (int i2 = 0; i2 < EXIF_TAGS.length; i2++) {
            if ((i2 != 4 || this.mHasThumbnail) && (obj = sExifTagMapsForWriting[i2].get(str)) != null) {
                if (str3 != null) {
                    ExifTag exifTag = (ExifTag) obj;
                    Pair<Integer, Integer> pairGuessDataFormat = guessDataFormat(str3);
                    if (exifTag.primaryFormat == pairGuessDataFormat.first.intValue() || exifTag.primaryFormat == pairGuessDataFormat.second.intValue()) {
                        i = exifTag.primaryFormat;
                    } else if (exifTag.secondaryFormat != -1 && (exifTag.secondaryFormat == pairGuessDataFormat.first.intValue() || exifTag.secondaryFormat == pairGuessDataFormat.second.intValue())) {
                        i = exifTag.secondaryFormat;
                    } else if (exifTag.primaryFormat == 1 || exifTag.primaryFormat == 7 || exifTag.primaryFormat == 2) {
                        i = exifTag.primaryFormat;
                    } else {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Given tag (");
                        sb.append(str);
                        sb.append(") value didn't match with one of expected formats: ");
                        sb.append(IFD_FORMAT_NAMES[exifTag.primaryFormat]);
                        sb.append(exifTag.secondaryFormat == -1 ? "" : ", " + IFD_FORMAT_NAMES[exifTag.secondaryFormat]);
                        sb.append(" (guess: ");
                        sb.append(IFD_FORMAT_NAMES[pairGuessDataFormat.first.intValue()]);
                        sb.append(pairGuessDataFormat.second.intValue() == -1 ? "" : ", " + IFD_FORMAT_NAMES[pairGuessDataFormat.second.intValue()]);
                        sb.append(")");
                        Log.w(TAG, sb.toString());
                    }
                    switch (i) {
                        case 1:
                            this.mAttributes[i2].put(str, ExifAttribute.createByte(str3));
                            break;
                        case 2:
                        case 7:
                            this.mAttributes[i2].put(str, ExifAttribute.createString(str3));
                            break;
                        case 3:
                            String[] strArrSplit = str3.split(",");
                            int[] iArr = new int[strArrSplit.length];
                            for (int i3 = 0; i3 < strArrSplit.length; i3++) {
                                iArr[i3] = Integer.parseInt(strArrSplit[i3]);
                            }
                            this.mAttributes[i2].put(str, ExifAttribute.createUShort(iArr, this.mExifByteOrder));
                            break;
                        case 4:
                            String[] strArrSplit2 = str3.split(",");
                            long[] jArr = new long[strArrSplit2.length];
                            for (int i4 = 0; i4 < strArrSplit2.length; i4++) {
                                jArr[i4] = Long.parseLong(strArrSplit2[i4]);
                            }
                            this.mAttributes[i2].put(str, ExifAttribute.createULong(jArr, this.mExifByteOrder));
                            break;
                        case 5:
                            String[] strArrSplit3 = str3.split(",");
                            Rational[] rationalArr = new Rational[strArrSplit3.length];
                            for (int i5 = 0; i5 < strArrSplit3.length; i5++) {
                                String[] strArrSplit4 = strArrSplit3[i5].split("/");
                                rationalArr[i5] = new Rational((long) Double.parseDouble(strArrSplit4[0]), (long) Double.parseDouble(strArrSplit4[1]));
                            }
                            this.mAttributes[i2].put(str, ExifAttribute.createURational(rationalArr, this.mExifByteOrder));
                            break;
                        case 6:
                        case 8:
                        case 11:
                        default:
                            Log.w(TAG, "Data format isn't one of expected formats: " + i);
                            break;
                        case 9:
                            String[] strArrSplit5 = str3.split(",");
                            int[] iArr2 = new int[strArrSplit5.length];
                            for (int i6 = 0; i6 < strArrSplit5.length; i6++) {
                                iArr2[i6] = Integer.parseInt(strArrSplit5[i6]);
                            }
                            this.mAttributes[i2].put(str, ExifAttribute.createSLong(iArr2, this.mExifByteOrder));
                            break;
                        case 10:
                            String[] strArrSplit6 = str3.split(",");
                            Rational[] rationalArr2 = new Rational[strArrSplit6.length];
                            for (int i7 = 0; i7 < strArrSplit6.length; i7++) {
                                String[] strArrSplit7 = strArrSplit6[i7].split("/");
                                rationalArr2[i7] = new Rational((long) Double.parseDouble(strArrSplit7[0]), (long) Double.parseDouble(strArrSplit7[1]));
                            }
                            this.mAttributes[i2].put(str, ExifAttribute.createSRational(rationalArr2, this.mExifByteOrder));
                            break;
                        case 12:
                            String[] strArrSplit8 = str3.split(",");
                            double[] dArr = new double[strArrSplit8.length];
                            for (int i8 = 0; i8 < strArrSplit8.length; i8++) {
                                dArr[i8] = Double.parseDouble(strArrSplit8[i8]);
                            }
                            this.mAttributes[i2].put(str, ExifAttribute.createDouble(dArr, this.mExifByteOrder));
                            break;
                    }
                } else {
                    this.mAttributes[i2].remove(str);
                }
            }
        }
    }
}
