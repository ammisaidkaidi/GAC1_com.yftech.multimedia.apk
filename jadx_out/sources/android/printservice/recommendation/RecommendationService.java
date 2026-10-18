package android.printservice.recommendation;

import android.annotation.SystemApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
@SystemApi
public abstract class RecommendationService extends Service {
    private static final String LOG_TAG = "PrintServiceRecS";
    public static final String SERVICE_INTERFACE = "android.printservice.recommendation.RecommendationService";
    private IRecommendationServiceCallbacks mCallbacks;
    private Handler mHandler;

    private class MyHandler extends Handler {
        static final int MSG_CONNECT = 1;
        static final int MSG_DISCONNECT = 2;
        static final int MSG_UPDATE = 3;

        MyHandler() {
            super(Looper.getMainLooper());
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    RecommendationService.this.mCallbacks = (IRecommendationServiceCallbacks) message.obj;
                    RecommendationService.this.onConnected();
                    break;
                case 2:
                    RecommendationService.this.onDisconnected();
                    RecommendationService.this.mCallbacks = null;
                    break;
                case 3:
                    try {
                        RecommendationService.this.mCallbacks.onRecommendationsUpdated((List) message.obj);
                    } catch (RemoteException | NullPointerException e) {
                        Log.e(RecommendationService.LOG_TAG, "Could not update recommended services", e);
                        return;
                    }
                    break;
            }
        }
    }

    @Override // android.content.ContextWrapper
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        this.mHandler = new MyHandler();
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        return new IRecommendationService.Stub() { // from class: android.printservice.recommendation.RecommendationService.1
            @Override // android.printservice.recommendation.IRecommendationService
            public void registerCallbacks(IRecommendationServiceCallbacks iRecommendationServiceCallbacks) {
                if (iRecommendationServiceCallbacks != null) {
                    RecommendationService.this.mHandler.obtainMessage(1, iRecommendationServiceCallbacks).sendToTarget();
                } else {
                    RecommendationService.this.mHandler.obtainMessage(2).sendToTarget();
                }
            }
        };
    }

    public abstract void onConnected();

    public abstract void onDisconnected();

    public final void updateRecommendations(List<RecommendationInfo> list) {
        this.mHandler.obtainMessage(3, list).sendToTarget();
    }
}
