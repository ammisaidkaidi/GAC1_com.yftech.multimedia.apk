package com.yftech.car.widget.media.text;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import com.yftech.car.widget.media.R;

/* JADX INFO: loaded from: classes3.dex */
public class DateTextView extends AppCompatTextView {
    private String mPattern;
    private String mThisYearPattern;
    private String mToday;
    private String mYesterday;

    private DateTextView(@NonNull Context context) {
        super(context);
    }

    public DateTextView(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context, attributeSet);
    }

    public DateTextView(@NonNull Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context, attributeSet);
    }

    private void init(Context context, AttributeSet attributeSet) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.DateTextView);
        this.mPattern = typedArrayObtainStyledAttributes.getString(R.styleable.DateTextView_date_format_pattern);
        this.mThisYearPattern = typedArrayObtainStyledAttributes.getString(R.styleable.DateTextView_date_format_this_year_pattern);
        this.mToday = typedArrayObtainStyledAttributes.getString(R.styleable.DateTextView_date_today);
        this.mYesterday = typedArrayObtainStyledAttributes.getString(R.styleable.DateTextView_date_yesterday);
    }

    public String getFormatPattern() {
        return this.mPattern;
    }

    public String getThisYearPattern() {
        return this.mThisYearPattern;
    }

    public String getToday() {
        return this.mToday;
    }

    public String getYesterday() {
        return this.mYesterday;
    }
}
