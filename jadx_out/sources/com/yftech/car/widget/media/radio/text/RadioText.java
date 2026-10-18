package com.yftech.car.widget.media.radio.text;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import androidx.appcompat.widget.AppCompatTextView;
import com.yftech.car.media.radio.entity.DataOfRadio;
import com.yftech.car.widget.media.R;
import com.yftech.car.widget.media.manager.RadioManager;

/* JADX INFO: loaded from: classes3.dex */
public class RadioText extends AppCompatTextView implements View.OnClickListener {
    private static final int BAND = 2;
    private static final int FREQ = 1;
    private static final int HERTZ = 4;
    private static final int NAME = 8;
    private static final String TAG = "RadioText";
    private boolean isTwoDecimalPlaces;
    private final Drawable[] mBandDrawable;
    private Drawable[] mFreqAmDrawable;
    private Drawable[] mFreqFmDrawable;
    private String mHertzAm;
    private final Drawable[] mHertzDrawable;
    private String mHertzFm;
    private String mHertzFm1;
    private String mRadioName;
    private int mTextFunctionEnum;

    public RadioText(Context context) {
        super(context);
        this.mBandDrawable = new Drawable[3];
        this.mHertzDrawable = new Drawable[3];
        this.mFreqFmDrawable = new Drawable[4];
        this.mFreqAmDrawable = new Drawable[4];
    }

    public RadioText(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 16842884);
    }

    public RadioText(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mBandDrawable = new Drawable[3];
        this.mHertzDrawable = new Drawable[3];
        this.mFreqFmDrawable = new Drawable[4];
        this.mFreqAmDrawable = new Drawable[4];
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.RadioTextView);
        this.mTextFunctionEnum = typedArrayObtainStyledAttributes.getInt(R.styleable.RadioTextView_radio_text_function, 1);
        boolean z = typedArrayObtainStyledAttributes.getBoolean(R.styleable.RadioTextView_click_switch_band, false);
        this.mBandDrawable[0] = typedArrayObtainStyledAttributes.getDrawable(R.styleable.RadioTextView_fm_image);
        this.mBandDrawable[1] = typedArrayObtainStyledAttributes.getDrawable(R.styleable.RadioTextView_fm1_image);
        this.mBandDrawable[2] = typedArrayObtainStyledAttributes.getDrawable(R.styleable.RadioTextView_am_image);
        this.mFreqFmDrawable[0] = typedArrayObtainStyledAttributes.getDrawable(R.styleable.RadioTextView_freq_image);
        this.mFreqFmDrawable[1] = typedArrayObtainStyledAttributes.getDrawable(R.styleable.RadioTextView_freq_image1);
        this.mFreqFmDrawable[2] = typedArrayObtainStyledAttributes.getDrawable(R.styleable.RadioTextView_freq_image2);
        this.mFreqFmDrawable[3] = typedArrayObtainStyledAttributes.getDrawable(R.styleable.RadioTextView_freq_image3);
        this.mFreqAmDrawable[0] = typedArrayObtainStyledAttributes.getDrawable(R.styleable.RadioTextView_freq_am_image);
        this.mFreqAmDrawable[1] = typedArrayObtainStyledAttributes.getDrawable(R.styleable.RadioTextView_freq_am_image1);
        this.mFreqAmDrawable[2] = typedArrayObtainStyledAttributes.getDrawable(R.styleable.RadioTextView_freq_am_image2);
        this.mFreqAmDrawable[3] = typedArrayObtainStyledAttributes.getDrawable(R.styleable.RadioTextView_freq_am_image3);
        this.mHertzDrawable[0] = typedArrayObtainStyledAttributes.getDrawable(R.styleable.RadioTextView_fm_hertz_image);
        this.mHertzDrawable[1] = typedArrayObtainStyledAttributes.getDrawable(R.styleable.RadioTextView_fm1_hertz_image);
        this.mHertzDrawable[2] = typedArrayObtainStyledAttributes.getDrawable(R.styleable.RadioTextView_am_hertz_image);
        if (z) {
            setOnClickListener(this);
        }
        this.mHertzFm = typedArrayObtainStyledAttributes.getString(R.styleable.RadioTextView_hertz_fm_text);
        if (this.mHertzFm == null) {
            this.mHertzFm = context.getString(R.string.hertz_fm);
        }
        this.mHertzFm1 = typedArrayObtainStyledAttributes.getString(R.styleable.RadioTextView_hertz_fm1_text);
        if (this.mHertzFm1 == null) {
            this.mHertzFm1 = context.getString(R.string.hertz_fm1);
        }
        this.mHertzAm = typedArrayObtainStyledAttributes.getString(R.styleable.RadioTextView_hertz_am_text);
        if (this.mHertzAm == null) {
            this.mHertzAm = context.getString(R.string.hertz_am);
        }
        this.mRadioName = typedArrayObtainStyledAttributes.getString(R.styleable.RadioTextView_radio_unknown_name);
        if (this.mRadioName == null) {
            this.mRadioName = context.getString(R.string.radio_unknown_name);
        }
        this.isTwoDecimalPlaces = RadioManager.getInstance().getStep(0) % 100 != 0;
    }

    private String getFmFreqValue(int i) {
        int i2 = i / 100;
        StringBuilder sb = new StringBuilder();
        sb.append(i / 1000.0f);
        sb.append((this.isTwoDecimalPlaces && i2 * 100 == i) ? "0" : "");
        return sb.toString();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (RadioManager.getInstance().getBand() == 0) {
            RadioManager.getInstance().setBand(2);
        } else {
            RadioManager.getInstance().setBand(0);
        }
    }

    public void setRadioText(DataOfRadio dataOfRadio) {
        int type = dataOfRadio.getType();
        int freq = dataOfRadio.getFreq();
        String name = "";
        if (type >= 0 && type < this.mBandDrawable.length) {
            int i = this.mTextFunctionEnum;
            if (i != 4) {
                if (i != 8) {
                    switch (i) {
                        case 1:
                            name = type == 2 ? String.valueOf(freq) : getFmFreqValue(freq);
                            break;
                        case 2:
                            if (this.mBandDrawable[type] != null) {
                                setBackground(this.mBandDrawable[type]);
                            } else if (type == 0) {
                                name = "FM";
                            } else if (type == 2) {
                                name = "AM";
                            } else {
                                name = "FM1";
                            }
                            break;
                        default:
                            Log.e(TAG, "setRadioText: no mTextFunctionEnum==" + this.mTextFunctionEnum);
                            break;
                    }
                } else {
                    name = dataOfRadio.getName();
                    if (TextUtils.isEmpty(name)) {
                        name = this.mRadioName;
                    }
                }
            } else if (this.mHertzDrawable[type] != null) {
                setBackground(this.mHertzDrawable[type]);
            } else if (type != 0) {
                name = type != 2 ? this.mHertzFm1 : this.mHertzAm;
            } else {
                name = this.mHertzFm;
            }
        }
        setText(name);
    }
}
