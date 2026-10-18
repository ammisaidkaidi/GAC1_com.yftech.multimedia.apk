package com.yftech.libradio.dabui.epg;

import android.os.AsyncTask;
import com.media.common.util.LogUtil;
import com.yftech.car.media.radio.dab.CarDabRadioManager;
import com.yftech.car.media.radio.dab.entity.DabPlayInfo;
import com.yftech.car.media.radio.dab.entity.epg.Programme;
import com.yftech.car.media.radio.dab.entity.epg.Schedule;
import com.yftech.libradio.DabDataManager;
import com.yftech.libradio.R;
import com.yftech.libradio.bean.DabChannelInfo;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/* JADX INFO: loaded from: classes4.dex */
public class EpgAllProgramFragment extends EpgBaseFragment {
    private boolean isStopRequst;
    private CarDabRadioManager.CarDabRadioControlCallback mCallback = new CarDabRadioManager.CarDabRadioControlCallback() { // from class: com.yftech.libradio.dabui.epg.EpgAllProgramFragment.1
        public void onPlayStatusChange(DabPlayInfo dabPlayInfo) {
            super.onPlayStatusChange(dabPlayInfo);
            EpgAllProgramFragment.this.saveDabPlayInfo(dabPlayInfo);
        }
    };
    private DabPlayInfo mDabPlayInfo;

    private class UpdateAllProgrammeData extends AsyncTask<Object, Void, Object> {
        private Calendar mTime;

        UpdateAllProgrammeData(Calendar calendar) {
            this.mTime = calendar;
        }

        private List<Programme> getProgrammesByFileName(String str) {
            if (str == null) {
                LogUtil.w("fileName is request ---");
                return null;
            }
            List data = CarDabRadioManager.getInstance().getNameEpgData(str).getData();
            if (data == null || data.isEmpty()) {
                LogUtil.d("scheduleList is empty ---");
                return null;
            }
            ArrayList arrayList = new ArrayList();
            Iterator it = data.iterator();
            while (it.hasNext()) {
                List programmes = ((Schedule) it.next()).getProgrammes();
                if (programmes == null || programmes.isEmpty()) {
                    LogUtil.d("programmes is empty ---");
                } else {
                    Iterator it2 = programmes.iterator();
                    while (it2.hasNext()) {
                        arrayList.add((Programme) it2.next());
                    }
                }
            }
            return arrayList;
        }

        @Override // android.os.AsyncTask
        protected final Object doInBackground(Object[] objArr) {
            try {
                if (this.mTime == null) {
                    LogUtil.w("time parameter request---");
                    return null;
                }
                List<DabChannelInfo> dataDabChannelInfoList = DabDataManager.getInstance().getDataDabChannelInfoList();
                if (dataDabChannelInfoList != null && !dataDabChannelInfoList.isEmpty()) {
                    for (DabChannelInfo dabChannelInfo : dataDabChannelInfoList) {
                        DabPlayInfo playInfo = CarDabRadioManager.getInstance().getPlayInfo();
                        if (dabChannelInfo.isEpg()) {
                            if (playInfo == null || playInfo.getFrequency() != dabChannelInfo.getFrequency() || playInfo.getEnsembleId() != dabChannelInfo.getEnsembleId() || playInfo.getServiceId() != dabChannelInfo.getServiceId()) {
                                LogUtil.d("set setFreq to epg, Frequency: " + dabChannelInfo.getFrequency() + " ,serviceId: " + dabChannelInfo.getServiceId());
                                CarDabRadioManager.getInstance().setFreq(dabChannelInfo.getFrequency(), dabChannelInfo.getEnsembleId(), dabChannelInfo.getServiceId());
                                break;
                            }
                            break;
                        }
                    }
                } else {
                    LogUtil.w("there are none dab channels ----");
                }
                int i = 30;
                List<String> epgDataNameList = CarDabRadioManager.getInstance().getEpgDataNameList();
                while (true) {
                    if ((epgDataNameList != null && !epgDataNameList.isEmpty()) || i <= 0 || EpgAllProgramFragment.this.isStopRequst) {
                        break;
                        break;
                        break;
                    }
                    i--;
                    Thread.sleep(2000L);
                    epgDataNameList = CarDabRadioManager.getInstance().getEpgDataNameList();
                }
                if (epgDataNameList != null && !epgDataNameList.isEmpty()) {
                    String str = new SimpleDateFormat("MMdd", Locale.ENGLISH).format(this.mTime.getTime());
                    ArrayList arrayList = new ArrayList();
                    for (String str2 : epgDataNameList) {
                        if (str2.contains(str)) {
                            arrayList.addAll(getProgrammesByFileName(str2));
                        }
                    }
                    if (arrayList.isEmpty()) {
                        return null;
                    }
                    return EpgUtils.sortProgramList(EpgAllProgramFragment.this.currentDate, arrayList);
                }
                LogUtil.w("epgNameList is empty---");
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.i("error ---");
                return null;
            }
        }

        @Override // android.os.AsyncTask
        protected void onPostExecute(Object obj) {
            super.onPostExecute(obj);
            if (obj == null) {
                EpgAllProgramFragment.this.onProgrameGot(null);
            } else {
                EpgAllProgramFragment.this.onProgrameGot((List) obj);
            }
            EpgAllProgramFragment.this.recoverDabPlay();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void recoverDabPlay() {
        if (this.mDabPlayInfo == null) {
            LogUtil.w("mDabPlayInfo is null ----");
            return;
        }
        DabPlayInfo playInfo = CarDabRadioManager.getInstance().getPlayInfo();
        if (playInfo != null && playInfo.getServiceId() == this.mDabPlayInfo.getServiceId() && playInfo.getEnsembleId() == this.mDabPlayInfo.getEnsembleId() && playInfo.getFrequency() == this.mDabPlayInfo.getFrequency()) {
            return;
        }
        CarDabRadioManager.getInstance().setFreq(this.mDabPlayInfo.getFrequency(), this.mDabPlayInfo.getEnsembleId(), this.mDabPlayInfo.getServiceId());
        LogUtil.d("mDabPlayInfo: " + this.mDabPlayInfo.toString());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveDabPlayInfo(DabPlayInfo dabPlayInfo) {
        if (dabPlayInfo == null) {
            LogUtil.w("dabPlayInfo is null ----");
            return;
        }
        if (this.mDabPlayInfo != null && dabPlayInfo.getFrequency() == this.mDabPlayInfo.getFrequency() && dabPlayInfo.getEnsembleId() == this.mDabPlayInfo.getEnsembleId() && dabPlayInfo.getServiceId() == this.mDabPlayInfo.getServiceId()) {
            return;
        }
        List<DabChannelInfo> dabChannelInfoList = DabDataManager.getInstance().getDabChannelInfoList();
        if (dabChannelInfoList == null || dabChannelInfoList.isEmpty()) {
            LogUtil.w("dabChannelInfoList is empty ----");
            return;
        }
        for (DabChannelInfo dabChannelInfo : dabChannelInfoList) {
            if (dabPlayInfo.getFrequency() == dabChannelInfo.getFrequency() && dabPlayInfo.getEnsembleId() == dabChannelInfo.getEnsembleId() && dabPlayInfo.getServiceId() == dabChannelInfo.getServiceId()) {
                this.mDabPlayInfo = dabPlayInfo;
                LogUtil.d("save success , dabPlayInfo: " + dabPlayInfo);
                return;
            }
        }
        LogUtil.w("none dabChannelInfo is matched ----,dabPlayInfo: " + dabPlayInfo);
    }

    @Override // com.yftech.libradio.dabui.epg.EpgBaseFragment
    void getProgrammeList(Calendar calendar) {
        saveDabPlayInfo(CarDabRadioManager.getInstance().getPlayInfo());
        this.isStopRequst = false;
        new UpdateAllProgrammeData(calendar).execute(new Object[0]);
    }

    @Override // com.yftech.libradio.dabui.epg.EpgBaseFragment
    String getTipsForEmptyContent() {
        return getString(R.string.dab_epg_none_of_list_tips);
    }

    @Override // com.yftech.libradio.dabui.epg.EpgBaseFragment, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        CarDabRadioManager.getInstance().registerRadioControlCallback(this.mCallback);
    }

    @Override // com.yftech.libradio.dabui.epg.EpgBaseFragment, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        this.isStopRequst = true;
        CarDabRadioManager.getInstance().unregisterRadioControlCallback(this.mCallback);
        recoverDabPlay();
    }
}
