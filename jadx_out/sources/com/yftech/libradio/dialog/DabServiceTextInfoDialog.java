package com.yftech.libradio.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.android.internal.logging.nano.MetricsProto;
import com.media.common.util.LogUtil;
import com.yftech.libmvpmodule.mvp.BaseMvpDialogFragment;
import com.yftech.libmvpmodule.mvp.BaseMvpPresenter;
import com.yftech.libradio.R;

/* JADX INFO: loaded from: classes4.dex */
public class DabServiceTextInfoDialog extends BaseMvpDialogFragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "content";
    private static final String ARG_PARAM2 = "title";
    private String content;
    private String title;
    private TextView tvBack;
    private TextView tvContent;
    private TextView tvTitle;

    public static DabServiceTextInfoDialog newInstance(String str, String str2) {
        DabServiceTextInfoDialog dabServiceTextInfoDialog = new DabServiceTextInfoDialog();
        Bundle bundle = new Bundle();
        bundle.putString("content", str);
        bundle.putString("title", str2);
        dabServiceTextInfoDialog.setArguments(bundle);
        return dabServiceTextInfoDialog;
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseMvpDialogFragment
    protected BaseMvpPresenter createPresenter() {
        return null;
    }

    @Override // androidx.fragment.app.DialogFragment
    public void dismiss() {
        super.dismiss();
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseMvpDialogFragment
    protected void initView(View view) {
        this.tvTitle = (TextView) view.findViewById(R.id.tv_title);
        this.tvContent = (TextView) view.findViewById(R.id.tv_content);
        this.tvBack = (TextView) view.findViewById(R.id.tv_control);
        this.tvBack.setOnClickListener(this);
        this.tvContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        if (this.content != null) {
            this.tvContent.setText(this.content);
        }
        if (this.title != null) {
            this.tvTitle.setText(this.title);
        }
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseMvpDialogFragment
    protected View onBindView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.dialog_dab_service_txt_info, viewGroup, false);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view.getId() == R.id.tv_control) {
            LogUtil.d("click back dismiss");
            dismiss();
        }
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() != null) {
            this.content = getArguments().getString("content");
            this.title = getArguments().getString("title");
        }
        LogUtil.d("onCreate() content:" + this.content);
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseDialogFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d("onDestroy()");
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        LogUtil.d("onStart()");
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            Window window = dialog.getWindow();
            if (window != null) {
                window.setBackgroundDrawableResource(17170445);
                WindowManager.LayoutParams attributes = window.getAttributes();
                attributes.windowAnimations = R.style.DialogFadeAnimation;
                attributes.width = 1078;
                attributes.height = MetricsProto.MetricsEvent.ACTION_PERMISSION_REQUEST_READ_CALL_LOG;
                attributes.gravity = 17;
                window.setAttributes(attributes);
            }
        }
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        LogUtil.d("onStop()");
    }
}
