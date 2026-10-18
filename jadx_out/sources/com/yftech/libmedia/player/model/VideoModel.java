package com.yftech.libmedia.player.model;

import android.app.Activity;
import android.app.Application;
import android.text.TextUtils;
import com.media.common.entity.MediaFileInfo;
import com.media.common.util.BaseApplication;
import com.media.common.util.LogUtil;
import com.yftech.car.media.data.CarMediaDataManager;
import com.yftech.car.media.data.entity.MediaFile;
import com.yftech.libmedia.player.bean.MediaFileForDataItem;
import com.yftech.libmedia.player.bean.MediaFileForDateItem;
import com.yftech.libmedia.player.model.interfaces.IVideoModel;
import com.yftech.libmedia.player.utils.MyVideoDataManager;
import com.yftech.libmvpmodule.BasePresenter;
import com.yftech.libmvpmodule.mvp.IMvpView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/* JADX INFO: loaded from: classes4.dex */
public class VideoModel implements IVideoModel {
    @Override // com.yftech.libmedia.player.model.interfaces.IVideoModel
    public void getVideo(final int i, BasePresenter<? extends IMvpView> basePresenter, final IVideoModel.OnVideoLoadListener onVideoLoadListener) {
        Activity activity = basePresenter.getActivity();
        if (activity != null) {
            Application application = activity.getApplication();
            if (application instanceof BaseApplication) {
                ((BaseApplication) application).getDataThreadPool().execute(new Runnable() { // from class: com.yftech.libmedia.player.model.VideoModel.1
                    @Override // java.lang.Runnable
                    public void run() {
                        List<MediaFile> fileVideo = CarMediaDataManager.getInstance().getFileVideo(i);
                        TreeMap treeMap = new TreeMap(new Comparator<String>() { // from class: com.yftech.libmedia.player.model.VideoModel.1.1
                            @Override // java.util.Comparator
                            public int compare(String str, String str2) {
                                return str2.compareTo(str);
                            }
                        });
                        if (fileVideo != null && !fileVideo.isEmpty()) {
                            for (MediaFile mediaFile : fileVideo) {
                                String strValueOf = String.valueOf(mediaFile.getCurrentTime());
                                MediaFileInfo mediaFileInfo = new MediaFileInfo(mediaFile);
                                List list = (List) treeMap.get(strValueOf);
                                if (list == null) {
                                    ArrayList arrayList = new ArrayList();
                                    arrayList.add(mediaFileInfo);
                                    treeMap.put(strValueOf, arrayList);
                                } else {
                                    list.add(mediaFileInfo);
                                }
                            }
                        }
                        ArrayList arrayList2 = new ArrayList();
                        MyVideoDataManager.getInstance().resetData(i);
                        for (Map.Entry entry : treeMap.entrySet()) {
                            List list2 = (List) entry.getValue();
                            if (list2 != null && !list2.isEmpty()) {
                                arrayList2.add(new MediaFileForDateItem(String.valueOf(((MediaFileInfo) list2.get(0)).getLastModified())));
                            }
                            Collections.sort((List) entry.getValue(), new Comparator<MediaFileInfo>() { // from class: com.yftech.libmedia.player.model.VideoModel.1.2
                                @Override // java.util.Comparator
                                public int compare(MediaFileInfo mediaFileInfo2, MediaFileInfo mediaFileInfo3) {
                                    if (mediaFileInfo3.getLastModified() == mediaFileInfo2.getLastModified()) {
                                        return 0;
                                    }
                                    return mediaFileInfo3.getLastModified() > mediaFileInfo2.getLastModified() ? 1 : -1;
                                }
                            });
                            for (MediaFileInfo mediaFileInfo2 : (List) entry.getValue()) {
                                MediaFileForDataItem mediaFileForDataItem = new MediaFileForDataItem(mediaFileInfo2);
                                if (!TextUtils.isEmpty(mediaFileForDataItem.getFileName()) && mediaFileForDataItem.getFileName().lastIndexOf(".") != -1) {
                                    mediaFileForDataItem.setFileName(mediaFileForDataItem.getFileName().substring(0, mediaFileForDataItem.getFileName().lastIndexOf(".")));
                                }
                                arrayList2.add(mediaFileForDataItem);
                                MyVideoDataManager.getInstance().setNewData(i, mediaFileInfo2);
                            }
                        }
                        LogUtil.d("getVideo:index=" + i + ",DisplayFileVideoSize=" + MyVideoDataManager.getInstance().getData(i).size());
                        onVideoLoadListener.onComplete(arrayList2);
                    }
                });
            }
        }
    }

    @Override // com.yftech.libmedia.player.model.interfaces.IVideoModel
    public void getVideoPlayList(int i, String str, BasePresenter<? extends IMvpView> basePresenter, IVideoModel.OnVideoPlayListLoadListener onVideoPlayListLoadListener) {
        int i2;
        List<MediaFile> data = MyVideoDataManager.getInstance().getData(i);
        if (TextUtils.isEmpty(str) || data == null || data.isEmpty()) {
            i2 = -1;
        } else {
            LogUtil.d("Target Video:" + str);
            i2 = 0;
            while (i2 < data.size()) {
                if (!str.equals(data.get(i2).getData())) {
                    i2++;
                }
            }
            i2 = -1;
        }
        onVideoPlayListLoadListener.onComplete(i2, data);
    }
}
