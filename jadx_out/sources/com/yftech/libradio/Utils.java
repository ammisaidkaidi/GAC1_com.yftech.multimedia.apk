package com.yftech.libradio;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.provider.Settings;
import android.telecom.Logging.Session;
import android.text.TextUtils;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.media.common.util.LogUtil;
import com.yftech.car.audio.CarAudioManager;
import com.yftech.car.media.radio.CarRadioManager;
import com.yftech.car.media.radio.dab.CarDabRadioManager;
import com.yftech.car.media.radio.dab.entity.DabEnsemble;
import com.yftech.car.media.radio.dab.entity.DabImageInfo;
import com.yftech.car.media.radio.dab.entity.DabLabel;
import com.yftech.car.media.radio.dab.entity.DabProperties;
import com.yftech.libradio.bean.DabChannelInfo;
import com.yftech.vehicle.internal.adapter.IHostConfig;
import com.yftech.vehicle.sdk.HostConfig;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.zip.GZIPInputStream;

/* JADX INFO: loaded from: classes4.dex */
public class Utils {
    public static final String LISTENER_DAB = "com.yftech.media.radio.dab.manager.RadioDabAudioManager";
    private static final int MIN_CLICK_DELAY_TIME = 500;
    public static final String SHARED_KEY_AM_MAX = "am_max";
    public static final int SHARED_KEY_AM_MAX_DEFAULT = 1629;
    public static final String SHARED_KEY_AM_MIN = "am_min";
    public static final int SHARED_KEY_AM_MIN_DEFAULT = 531;
    public static final String SHARED_KEY_AM_STEP = "am_step";
    public static final int SHARED_KEY_AM_STEP_DEFAULT = 9;
    public static final String SHARED_KEY_FM_MAX = "fm_max";
    public static final int SHARED_KEY_FM_MAX_DEFAULT = 108000;
    public static final String SHARED_KEY_FM_MIN = "fm_min";
    public static final int SHARED_KEY_FM_MIN_DEFAULT = 87500;
    public static final String SHARED_KEY_FM_STEP = "fm_step";
    public static final int SHARED_KEY_FM_STEP_DEFAULT = 100;
    public static final String SHARED_RADIO_DATA_NAME = "yf_radio_app_data";
    private static long lastClickTime;
    private static Utils sInstance;

    private Utils() {
    }

    public static void convertDabInfos(DabEnsemble[] dabEnsembleArr, List<DabChannelInfo> list, List<DabChannelInfo> list2) throws Throwable {
        if (list == null) {
            LogUtil.e("convertDabInfos,channelInfoList can 't be null");
            return;
        }
        if (dabEnsembleArr == null || dabEnsembleArr.length == 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("convertDabInfos,dabEnsembles is Empty, length: ");
            sb.append(dabEnsembleArr == null ? "null" : Integer.valueOf(dabEnsembleArr.length));
            LogUtil.w(sb.toString());
            return;
        }
        for (DabEnsemble dabEnsemble : dabEnsembleArr) {
            DabProperties[] list3 = dabEnsemble.getList();
            if (list3 == null || list3.length <= 0) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("convertDabInfos,properties is Empty, length: ");
                sb2.append(list3 == null ? "null" : Integer.valueOf(list3.length));
                LogUtil.w(sb2.toString());
            } else {
                for (DabProperties dabProperties : list3) {
                    DabChannelInfo dabChannelInfo = new DabChannelInfo(dabEnsemble.getFrequency(), dabEnsemble.getEnsembleId(), dabEnsemble.getLabel(), dabProperties, dabEnsemble.getTab());
                    if (dabProperties.getPdFlag() != 0) {
                        list.add(dabChannelInfo);
                    } else if (list2 != null) {
                        list2.add(dabChannelInfo);
                    }
                }
            }
        }
    }

    public static String convertStationName(String str) {
        if (str.length() <= 16) {
            return str;
        }
        return str.substring(0, 16) + Session.TRUNCATE_STRING;
    }

    public static String getAudioSourceListener() {
        String currentMediaSource = CarAudioManager.getInstance().getCurrentMediaSource();
        if (currentMediaSource == null) {
            return "";
        }
        String[] strArrSplit = currentMediaSource.split(",");
        if (strArrSplit.length != 3) {
            return "";
        }
        String str = strArrSplit[2];
        return str.equals("null") ? "" : str;
    }

    public static DabEnsemble[] getDabData(int i) {
        DabEnsemble[] dabEnsembleArr = new DabEnsemble[i > 3 ? i % 3 == 0 ? i / 3 : (i / 3) + 1 : 1];
        for (int i2 = 0; i2 < dabEnsembleArr.length; i2++) {
            DabEnsemble dabEnsemble = new DabEnsemble();
            dabEnsemble.setEnsembleId(i2 + 20);
            dabEnsemble.setFrequency((i2 * 2) + 1025);
            dabEnsemble.setTab(i2 + 2);
            DabLabel dabLabel = new DabLabel();
            dabLabel.setLabelString("BBC Group_" + i2);
            dabEnsemble.setLabel(dabLabel);
            DabProperties[] dabPropertiesArr = new DabProperties[3];
            dabEnsemble.setList(dabPropertiesArr);
            int i3 = 0;
            while (i3 < dabPropertiesArr.length) {
                DabProperties dabProperties = new DabProperties();
                dabProperties.setCaApplied(true);
                dabProperties.setCaId(i3 + 2);
                int i4 = i3 + 1;
                dabProperties.setDynPty(i4);
                dabProperties.setServiceId(i3 + 5);
                DabLabel dabLabel2 = new DabLabel();
                dabLabel2.setLabelString("BBC National_" + i3);
                dabProperties.setLabel(dabLabel2);
                dabPropertiesArr[i3] = dabProperties;
                i3 = i4;
            }
            dabEnsembleArr[i2] = dabEnsemble;
        }
        return dabEnsembleArr;
    }

    public static byte[] getDabImageInfo() {
        DabImageInfo dabImageInfo = CarDabRadioManager.getInstance().getDabImageInfo();
        if (dabImageInfo == null) {
            LogUtil.w("dabImageInfo is null---");
            return null;
        }
        if (dabImageInfo.getCompress() != 1) {
            byte[] data = dabImageInfo.getData();
            if (data != null && data.length > 0) {
                return data;
            }
            LogUtil.d("data is Empty ----");
            return null;
        }
        LogUtil.d("data is compress ----");
        byte[] data2 = dabImageInfo.getData();
        if (data2 != null && data2.length > 0) {
            return uncompressGzip(data2);
        }
        LogUtil.d("data is Empty ----");
        return null;
    }

    public static String getDabLableString(DabLabel dabLabel, String str) {
        return (dabLabel == null || TextUtils.isEmpty(dabLabel.getLabelString())) ? str : dabLabel.getLabelString();
    }

    public static Utils getInstance() {
        if (sInstance == null) {
            synchronized (Utils.class) {
                if (sInstance == null) {
                    sInstance = new Utils();
                }
            }
        }
        return sInstance;
    }

    public static boolean isFMAndAMPresent() {
        IHostConfig.RadioConfiguration radioConfiguration = HostConfig.getRadioConfiguration();
        LogUtil.d("getFMAndAMSConfiguration-->config=" + radioConfiguration);
        boolean z = radioConfiguration == IHostConfig.RadioConfiguration.FM || radioConfiguration == IHostConfig.RadioConfiguration.AM || radioConfiguration == IHostConfig.RadioConfiguration.FM_PLUSAM;
        LogUtil.d("getFMAndAMSConfiguration--> " + z);
        return z;
    }

    public static synchronized boolean isQuickClick() {
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (Math.abs(jCurrentTimeMillis - lastClickTime) < 500) {
            return true;
        }
        lastClickTime = jCurrentTimeMillis;
        return false;
    }

    public static boolean isRadioDABPresent() {
        IHostConfig.RadioDABAndRDSConfig radioDABAndRDSConfiguration = HostConfig.getRadioDABAndRDSConfiguration();
        LogUtil.d("isRadioDABPresent-->config=" + radioDABAndRDSConfiguration);
        boolean z = radioDABAndRDSConfiguration == IHostConfig.RadioDABAndRDSConfig.DAB_RDS || radioDABAndRDSConfiguration == IHostConfig.RadioDABAndRDSConfig.DAB;
        LogUtil.d("isRadioDABPresent-->: " + z);
        return z;
    }

    public static boolean isRdsPresent() {
        IHostConfig.RadioDABAndRDSConfig radioDABAndRDSConfiguration = HostConfig.getRadioDABAndRDSConfiguration();
        LogUtil.d("getRadioDABAndRDSConfiguration-->config=" + radioDABAndRDSConfiguration);
        boolean z = radioDABAndRDSConfiguration == IHostConfig.RadioDABAndRDSConfig.DAB_RDS || radioDABAndRDSConfiguration == IHostConfig.RadioDABAndRDSConfig.RDS;
        LogUtil.d("getRadioDABAndRDSConfiguration--> " + z);
        return z;
    }

    public static void loadDabSliderShowData(ImageView imageView, Drawable drawable, int i, byte[] bArr) {
        if (imageView == null) {
            LogUtil.w("imageView is null---");
            return;
        }
        if (bArr == null || bArr.length <= 0) {
            LogUtil.w("none imamge data----");
            imageView.setImageDrawable(drawable);
        } else {
            if (i <= 0) {
                Glide.with(imageView.getContext()).load(bArr).placeholder(drawable).error(drawable).into(imageView);
                return;
            }
            new RequestOptions().placeholder(drawable);
            Glide.with(imageView.getContext()).applyDefaultRequestOptions(RequestOptions.bitmapTransform(new RoundedCorners(i))).load(bArr).placeholder(drawable).error(drawable).into(imageView);
        }
    }

    public static void loadDabSliderShowDataNoCache(final ImageView imageView, Drawable drawable, int i, byte[] bArr) {
        if (imageView == null) {
            LogUtil.w("imageView is null---");
            return;
        }
        if (bArr == null || bArr.length <= 0) {
            LogUtil.w("none imamge data----");
            imageView.setImageDrawable(drawable);
        } else {
            if (i <= 0) {
                Glide.with(imageView.getContext()).load(bArr).placeholder(drawable).error(drawable).addListener(new RequestListener<Drawable>() { // from class: com.yftech.libradio.Utils.2
                    @Override // com.bumptech.glide.request.RequestListener
                    public boolean onLoadFailed(GlideException glideException, Object obj, Target<Drawable> target, boolean z) {
                        return false;
                    }

                    @Override // com.bumptech.glide.request.RequestListener
                    public boolean onResourceReady(Drawable drawable2, Object obj, Target<Drawable> target, DataSource dataSource, boolean z) {
                        DabChannelInfo dabChannelInfo = (DabChannelInfo) imageView.getTag();
                        if (dabChannelInfo == null) {
                            return false;
                        }
                        return dabChannelInfo.getServiceId() != CarDabRadioManager.getInstance().getPlayInfo().getServiceId();
                    }
                }).into(imageView);
                return;
            }
            new RequestOptions().placeholder(drawable);
            Glide.with(imageView.getContext()).applyDefaultRequestOptions(RequestOptions.bitmapTransform(new RoundedCorners(i))).load(bArr).placeholder(drawable).error(drawable).addListener(new RequestListener<Drawable>() { // from class: com.yftech.libradio.Utils.1
                @Override // com.bumptech.glide.request.RequestListener
                public boolean onLoadFailed(GlideException glideException, Object obj, Target<Drawable> target, boolean z) {
                    return false;
                }

                @Override // com.bumptech.glide.request.RequestListener
                public boolean onResourceReady(Drawable drawable2, Object obj, Target<Drawable> target, DataSource dataSource, boolean z) {
                    DabChannelInfo dabChannelInfo = (DabChannelInfo) imageView.getTag();
                    if (dabChannelInfo == null) {
                        return false;
                    }
                    return dabChannelInfo.getServiceId() != CarDabRadioManager.getInstance().getPlayInfo().getServiceId();
                }
            }).into(imageView);
        }
    }

    public static synchronized void playKeySoundEffect(Context context) {
        boolean z = true;
        if (Settings.System.getInt(context.getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, 0) != 1) {
            z = false;
        }
        if (z) {
            ((AudioManager) context.getSystemService("audio")).playSoundEffect(0);
        }
    }

    private static byte[] uncompressGzip(byte[] bArr) {
        if (bArr == null || bArr.length == 0) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bArr);
        try {
            GZIPInputStream gZIPInputStream = new GZIPInputStream(byteArrayInputStream);
            byte[] bArr2 = new byte[bArr.length];
            while (true) {
                int i = gZIPInputStream.read(bArr2);
                if (i < 0) {
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    try {
                        return byteArray;
                    } catch (IOException e) {
                        return byteArray;
                    }
                }
                byteArrayOutputStream.write(bArr2, 0, i);
            }
        } catch (IOException e2) {
            LogUtil.e("gzip compress error." + e2.getMessage());
            return null;
        } finally {
            try {
                byteArrayInputStream.close();
                byteArrayOutputStream.close();
            } catch (IOException e3) {
                e3.printStackTrace();
            }
        }
    }

    public String formatFreq(int i, int i2) {
        if (i2 == 0) {
            return isFmKeepTwoDecima() ? String.format(Locale.ENGLISH, "%.2f", Float.valueOf(i / 1000.0f)) : String.valueOf(i / 1000.0f);
        }
        return String.valueOf(i);
    }

    public boolean isFmKeepTwoDecima() {
        int step = CarRadioManager.getInstance().getStep(0);
        boolean z = step % 100 != 0;
        LogUtil.i("isFmKeepTwoDecima->isFmKeepTwoDecima=" + z + ";step=" + step);
        return z;
    }
}
