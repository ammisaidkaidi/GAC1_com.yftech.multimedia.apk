package com.yftech.libradio.dabui.epg;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.media.common.util.AppConstants;
import com.media.common.util.LogUtil;
import com.yftech.libradio.R;
import com.yftech.libradio.Utils;

/* JADX INFO: loaded from: classes4.dex */
public class EpgActivity extends AppCompatActivity {
    private static final String KEY_SP_TAB_INDEX = "yftech.epg.tab";
    private static final int TAB_TYPE_EPG_LIST = 20006;
    private static final int TAB_TYPE_EPG_SUBSCRIBE = 20007;
    private EpgAllProgramFragment allEgpList;
    private RadioButton rbAllList;
    private RadioButton rbSubscribeList;
    private RadioGroup rgEpg;
    private EpgSubsCribedProgramFragment subsCribedFragment;

    private int getSharedTabIndex(String str, int i) {
        return getSharedPreferences(Utils.SHARED_RADIO_DATA_NAME, 0).getInt(str, i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveSharedTabIndex(String str, int i) {
        LogUtil.i("saveSharedRadioData() key:" + str + " value:" + i);
        getSharedPreferences(Utils.SHARED_RADIO_DATA_NAME, 0).edit().putInt(str, i).commit();
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_egp, fragment).commit();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showTypeView(int i) {
        if (TAB_TYPE_EPG_LIST == i) {
            this.rbAllList.setTypeface(Typeface.create("sourcehan-sans-regular", 0));
            this.rbAllList.setTextColor(getColor(R.color.colorFFFFFF));
            this.rbSubscribeList.setTypeface(Typeface.create(AppConstants.SourceHanSansCN_Medium, 0));
            this.rbSubscribeList.setTextColor(getColor(R.color.colorB3FFFFFF));
            showFragment(this.allEgpList);
            return;
        }
        if (TAB_TYPE_EPG_SUBSCRIBE != i) {
            LogUtil.w("tab switch showTypeView error---");
            return;
        }
        this.rbAllList.setTypeface(Typeface.create(AppConstants.SourceHanSansCN_Medium, 0));
        this.rbAllList.setTextColor(getColor(R.color.colorB3FFFFFF));
        this.rbSubscribeList.setTypeface(Typeface.create("sourcehan-sans-regular", 0));
        this.rbSubscribeList.setTextColor(getColor(R.color.colorFFFFFF));
        showFragment(this.subsCribedFragment);
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_epg);
        this.rbAllList = (RadioButton) findViewById(R.id.rb_epg_all_list);
        this.rbSubscribeList = (RadioButton) findViewById(R.id.rb_epg_subscribed_list);
        this.rgEpg = (RadioGroup) findViewById(R.id.tab_epg);
        this.allEgpList = new EpgAllProgramFragment();
        this.subsCribedFragment = new EpgSubsCribedProgramFragment();
        this.rgEpg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { // from class: com.yftech.libradio.dabui.epg.EpgActivity.1
            @Override // android.widget.RadioGroup.OnCheckedChangeListener
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rb_epg_all_list) {
                    EpgActivity.this.showTypeView(EpgActivity.TAB_TYPE_EPG_LIST);
                    EpgActivity.this.saveSharedTabIndex(EpgActivity.KEY_SP_TAB_INDEX, EpgActivity.TAB_TYPE_EPG_LIST);
                } else if (i == R.id.rb_epg_subscribed_list) {
                    EpgActivity.this.showTypeView(EpgActivity.TAB_TYPE_EPG_SUBSCRIBE);
                    EpgActivity.this.saveSharedTabIndex(EpgActivity.KEY_SP_TAB_INDEX, EpgActivity.TAB_TYPE_EPG_SUBSCRIBE);
                }
            }
        });
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onStart() {
        super.onStart();
        if (getSharedTabIndex(KEY_SP_TAB_INDEX, TAB_TYPE_EPG_LIST) == TAB_TYPE_EPG_SUBSCRIBE) {
            this.rbSubscribeList.setChecked(true);
            this.rbAllList.setChecked(false);
        } else {
            this.rbAllList.setChecked(true);
            this.rbSubscribeList.setChecked(false);
        }
    }
}
