package com.yftech.libradio.dabui.epg;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.android.internal.logging.nano.MetricsProto;
import com.media.common.util.LogUtil;
import com.yftech.car.media.radio.dab.CarDabRadioManager;
import com.yftech.car.media.radio.dab.entity.epg.Programme;
import com.yftech.libradio.R;

/* JADX INFO: loaded from: classes4.dex */
public class EpgSubcribeConflictDialog extends Dialog implements View.OnClickListener {
    private EpgSubcribeConfirmCallBack confirmCallBack;
    private final CarDabRadioManager.CarDabRadioControlCallback mCarDabRadioControlCallback;
    private Programme mConflictPrograme;
    private Programme mTargetPrograme;
    private TextView tvCancle;
    private TextView tvReplace;
    private TextView tvTitle;
    private TextView tv_content;

    interface EpgSubcribeConfirmCallBack {
        void onCancle();

        void onSubcribeFaile();

        void onSubcribeSuccess();
    }

    public EpgSubcribeConflictDialog(@NonNull Context context) {
        super(context);
        this.mCarDabRadioControlCallback = new CarDabRadioManager.CarDabRadioControlCallback() { // from class: com.yftech.libradio.dabui.epg.EpgSubcribeConflictDialog.1
        };
    }

    public EpgSubcribeConflictDialog(@NonNull Context context, int i) {
        super(context, i);
        this.mCarDabRadioControlCallback = new CarDabRadioManager.CarDabRadioControlCallback() { // from class: com.yftech.libradio.dabui.epg.EpgSubcribeConflictDialog.1
        };
    }

    protected EpgSubcribeConflictDialog(@NonNull Context context, boolean z, @Nullable DialogInterface.OnCancelListener onCancelListener) {
        super(context, z, onCancelListener);
        this.mCarDabRadioControlCallback = new CarDabRadioManager.CarDabRadioControlCallback() { // from class: com.yftech.libradio.dabui.epg.EpgSubcribeConflictDialog.1
        };
    }

    @Override // android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        super.dismiss();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view.getId() != R.id.tv_litener) {
            if (view.getId() == R.id.tv_cancel) {
                dismiss();
                if (this.confirmCallBack != null) {
                    this.confirmCallBack.onCancle();
                    return;
                }
                return;
            }
            return;
        }
        dismiss();
        if (this.mTargetPrograme == null || this.mConflictPrograme == null) {
            return;
        }
        if (CarDabRadioManager.getInstance().removeEpgProgramme(this.mConflictPrograme) <= 0) {
            if (this.confirmCallBack != null) {
                this.confirmCallBack.onSubcribeFaile();
            }
        } else if (CarDabRadioManager.getInstance().addEpgProgramme(this.mTargetPrograme) > 0) {
            if (this.confirmCallBack != null) {
                this.confirmCallBack.onSubcribeSuccess();
            }
        } else if (this.confirmCallBack != null) {
            this.confirmCallBack.onSubcribeFaile();
        }
    }

    @Override // android.app.Dialog
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.dialog_epg_reminder);
        this.tvReplace = (TextView) findViewById(R.id.tv_litener);
        this.tvReplace.setOnClickListener(this);
        this.tvReplace.setText(getContext().getString(R.string.epg_reminder_confirm2));
        this.tvCancle = (TextView) findViewById(R.id.tv_cancel);
        this.tvCancle.setOnClickListener(this);
        this.tvTitle = (TextView) findViewById(R.id.tv_title);
        this.tvTitle.setText(getContext().getString(R.string.epg_reminder_title));
        this.tv_content = (TextView) findViewById(R.id.tv_content);
    }

    @Override // android.app.Dialog
    public void onStart() {
        super.onStart();
        LogUtil.d("onStart()");
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(17170445);
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.windowAnimations = R.style.DialogFadeAnimation;
            attributes.width = MetricsProto.MetricsEvent.WIFI_NETWORK_RECOMMENDATION_RECOMMENDED_NETWORK_EVALUATOR;
            attributes.height = 372;
            attributes.gravity = 17;
            window.setAttributes(attributes);
        }
        CarDabRadioManager.getInstance().registerRadioControlCallback(this.mCarDabRadioControlCallback);
        if (this.mConflictPrograme != null) {
            this.tv_content.setText(getContext().getString(R.string.epg_reminder_content2, this.mConflictPrograme.name));
        }
    }

    @Override // android.app.Dialog
    public void onStop() {
        super.onStop();
        LogUtil.d("onStop()");
        CarDabRadioManager.getInstance().unregisterRadioControlCallback(this.mCarDabRadioControlCallback);
    }

    public void setConfirmCallBack(EpgSubcribeConfirmCallBack epgSubcribeConfirmCallBack) {
        this.confirmCallBack = epgSubcribeConfirmCallBack;
    }

    public void setParams(Programme programme, Programme programme2) {
        this.mTargetPrograme = programme;
        this.mConflictPrograme = programme2;
    }
}
