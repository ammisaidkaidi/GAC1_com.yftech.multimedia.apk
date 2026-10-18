package com.media.common.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.wifi.WifiEnterpriseConfig;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import java.util.List;
import java.util.Locale;

/* JADX INFO: loaded from: classes3.dex */
public class MarqueeView extends View implements Runnable {
    public static final int NOT_SCROLL = -1;
    public static final int REPET_CONTINUOUS = 2;
    public static final int REPET_INTERVAL = 1;
    public static final int REPET_ONCETIME = 0;
    private static final String TAG = "MarqueeView";
    private final int DEFAULT_SIZE;
    private String black_count;
    private String content;
    private int contentWidth;
    private boolean isClickStop;
    private boolean isResetLocation;
    private boolean isRightStart;
    private boolean isRoll;
    private String keyWords;
    private int keyWordsColor;
    private boolean mIsVisibe;
    private final int offSet;
    private float oneBlack_width;
    private TextPaint paint;
    private Rect rect;
    private int repetCount;
    private int repetType;
    private boolean resetInit;
    private float speed;
    private float startLocationDistance;
    private String string;
    private int textColor;
    private int textDistance1;
    private boolean textGravityCenterMode;
    private float textHeight;
    private float textSize;
    private boolean textStyle_isBlod;
    private int textdistance;
    private Thread thread;
    private int width;
    private float xLocation;

    public MarqueeView(Context context) {
        this(context, null);
    }

    public MarqueeView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public MarqueeView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.speed = 1.0f;
        this.textColor = -16777216;
        this.keyWordsColor = -65536;
        this.textSize = 12.0f;
        this.textDistance1 = 10;
        this.black_count = "";
        this.repetType = 1;
        this.startLocationDistance = 1.0f;
        this.DEFAULT_SIZE = 500;
        this.textStyle_isBlod = false;
        this.textGravityCenterMode = false;
        this.isClickStop = false;
        this.isResetLocation = true;
        this.xLocation = 0.0f;
        this.isRoll = false;
        this.repetCount = 0;
        this.resetInit = true;
        this.content = "";
        this.keyWords = "";
        this.mIsVisibe = false;
        this.offSet = 15;
        initattrs(context, attributeSet);
        initpaint();
        initClick();
        if (TextUtils.isEmpty(this.content)) {
            return;
        }
        setContent(this.content);
    }

    private float getBlacktWidth() {
        return getContentWidth("en en") - getContentWidth("enen");
    }

    private float getContentHeight() {
        Paint.FontMetrics fontMetrics = this.paint.getFontMetrics();
        return Math.abs(fontMetrics.bottom - fontMetrics.top) / 2.0f;
    }

    private float getContentWidth(String str) {
        if (str == null || str == "") {
            return 0.0f;
        }
        if (this.rect == null) {
            this.rect = new Rect();
        }
        this.paint.getTextBounds(str, 0, str.length(), this.rect);
        this.textHeight = getContentHeight();
        return this.rect.width() + 15;
    }

    private float getWordWidth(String str) {
        return this.paint.measureText(str);
    }

    private void initClick() {
        setOnClickListener(new View.OnClickListener() { // from class: com.media.common.ui.widget.MarqueeView.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (MarqueeView.this.isClickStop) {
                    if (MarqueeView.this.isRoll) {
                        MarqueeView.this.stopRoll();
                    } else {
                        MarqueeView.this.continueRoll();
                    }
                }
            }
        });
    }

    private void initattrs(Context context, AttributeSet attributeSet) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.MarqueeView);
        this.content = typedArrayObtainStyledAttributes.getString(R.styleable.MarqueeView_marqueeview_text) == null ? "" : typedArrayObtainStyledAttributes.getString(R.styleable.MarqueeView_marqueeview_text);
        this.keyWords = typedArrayObtainStyledAttributes.getString(R.styleable.MarqueeView_marqueeview_keywords) == null ? "" : typedArrayObtainStyledAttributes.getString(R.styleable.MarqueeView_marqueeview_keywords);
        this.textStyle_isBlod = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MarqueeView_marqueeview_textStyle_isBold, false);
        this.textColor = typedArrayObtainStyledAttributes.getColor(R.styleable.MarqueeView_marqueeview_text_color, this.textColor);
        this.keyWordsColor = typedArrayObtainStyledAttributes.getColor(R.styleable.MarqueeView_marqueeview_keywords_color, this.keyWordsColor);
        this.isClickStop = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MarqueeView_marqueeview_isclickalbe_stop, this.isClickStop);
        this.isResetLocation = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MarqueeView_marqueeview_is_resetLocation, this.isResetLocation);
        this.speed = typedArrayObtainStyledAttributes.getFloat(R.styleable.MarqueeView_marqueeview_text_speed, this.speed);
        this.textSize = typedArrayObtainStyledAttributes.getFloat(R.styleable.MarqueeView_marqueeview_text_size, this.textSize);
        this.textDistance1 = typedArrayObtainStyledAttributes.getInteger(R.styleable.MarqueeView_marqueeview_text_distance, this.textDistance1);
        this.startLocationDistance = typedArrayObtainStyledAttributes.getFloat(R.styleable.MarqueeView_marqueeview_text_startlocationdistance, this.startLocationDistance);
        this.repetType = typedArrayObtainStyledAttributes.getInt(R.styleable.MarqueeView_marqueeview_repet_type, this.repetType);
        this.textGravityCenterMode = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MarqueeView_marqueeview_textGravityCenterMode, this.textGravityCenterMode);
        typedArrayObtainStyledAttributes.recycle();
        Locale locale = Locale.getDefault();
        this.isRightStart = locale.getLanguage().equals("ar") || locale.getLanguage().equals("he");
    }

    private void initpaint() {
        this.rect = new Rect();
        this.paint = new TextPaint(1);
        this.paint.setStyle(Paint.Style.FILL);
        this.paint.setColor(this.textColor);
        this.paint.setTextSize(dp2px(this.textSize));
        this.paint.setFakeBoldText(this.textStyle_isBlod);
    }

    private void setClickStop(boolean z) {
        this.isClickStop = z;
    }

    private void setContinueble(int i) {
        this.repetType = i;
    }

    private void setResetLocation(boolean z) {
        this.isResetLocation = z;
    }

    public void appendContent(String str) {
    }

    public void continueRoll() {
        if (this.isRoll) {
            return;
        }
        if (this.thread != null) {
            this.thread.interrupt();
            this.thread = null;
        }
        this.isRoll = true;
        this.thread = new Thread(this);
        this.thread.start();
    }

    public int dp2px(float f) {
        return (int) ((f * getResources().getDisplayMetrics().density) + 0.5f);
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopRoll();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.contentWidth >= this.width && this.repetType != -1) {
            if (this.mIsVisibe) {
                continueRoll();
            }
            switch (this.repetType) {
                case 1:
                    if (this.contentWidth <= (-this.xLocation)) {
                        this.xLocation = getWidth();
                    }
                    break;
                case 2:
                    if (this.xLocation < 0.0f && ((int) ((-this.xLocation) / this.contentWidth)) >= this.repetCount) {
                        this.repetCount++;
                        this.string += this.content;
                    }
                    break;
                default:
                    if (this.contentWidth < (-this.xLocation)) {
                        stopRoll();
                    }
                    break;
            }
        } else {
            if (this.textGravityCenterMode) {
                this.xLocation = (this.width - getContentWidth(this.content)) / 2.0f;
            } else {
                this.xLocation = this.width * this.startLocationDistance;
            }
            stopRoll();
        }
        if (this.string == null) {
            return;
        }
        if (this.isRightStart) {
            float fMeasureText = this.paint.measureText(this.string);
            float width = getWidth();
            if (width > fMeasureText) {
                this.xLocation = width - fMeasureText;
            }
        }
        if (TextUtils.isEmpty(this.keyWords) || !this.string.contains(this.keyWords)) {
            canvas.drawText(this.string, this.xLocation, (getHeight() / 2.0f) + (this.textHeight / 2.0f), this.paint);
            return;
        }
        String strSubstring = this.string;
        float wordWidth = this.xLocation;
        while (true) {
            int iIndexOf = strSubstring.indexOf(this.keyWords);
            if (iIndexOf == -1) {
                if (strSubstring.length() > 0) {
                    this.paint.setColor(this.textColor);
                    canvas.drawText(strSubstring, wordWidth, (getHeight() / 2.0f) + (this.textHeight / 2.0f), this.paint);
                    return;
                }
                return;
            }
            if (iIndexOf != 0) {
                this.paint.setColor(this.textColor);
                canvas.drawText(strSubstring.substring(0, iIndexOf), wordWidth, (getHeight() / 2.0f) + (this.textHeight / 2.0f), this.paint);
                wordWidth += getWordWidth(strSubstring.substring(0, iIndexOf));
            }
            this.paint.setColor(this.keyWordsColor);
            canvas.drawText(this.keyWords, wordWidth, (getHeight() / 2.0f) + (this.textHeight / 2.0f), this.paint);
            wordWidth += getWordWidth(strSubstring.substring(iIndexOf, this.keyWords.length() + iIndexOf));
            if (this.keyWords.length() + iIndexOf + 1 < strSubstring.length()) {
                strSubstring = strSubstring.substring(iIndexOf + this.keyWords.length());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int mode = View.MeasureSpec.getMode(i);
        int size = View.MeasureSpec.getSize(i);
        Log.d(TAG, "WidthMode=" + mode + ", WidthSize=" + size);
        if (mode == Integer.MIN_VALUE) {
            this.width = Math.min(500, size);
        } else if (mode != 1073741824) {
            this.width = 500;
        } else {
            this.width = size;
        }
        setTextDistance(this.textDistance1);
        if (this.startLocationDistance < 0.0f) {
            this.startLocationDistance = 0.0f;
        } else if (this.startLocationDistance > 1.0f) {
            this.startLocationDistance = 1.0f;
        }
        this.xLocation = this.width * this.startLocationDistance;
        this.resetInit = false;
    }

    @Override // android.view.View
    protected void onVisibilityChanged(@NonNull View view, int i) {
        super.onVisibilityChanged(view, i);
        this.mIsVisibe = i == 0;
        if (this.mIsVisibe) {
            invalidate();
        } else {
            stopRoll();
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        while (this.isRoll && !TextUtils.isEmpty(this.content) && this.mIsVisibe) {
            try {
                Thread.sleep(10L);
                this.xLocation -= this.speed;
                postInvalidate();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setContent(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        if (this.thread != null) {
            this.thread.interrupt();
            this.thread = null;
        }
        if (this.isResetLocation) {
            this.xLocation = this.width * this.startLocationDistance;
        }
        if (!str.endsWith(this.black_count)) {
            str = str + this.black_count;
        }
        this.content = str;
        float contentWidth = getContentWidth(this.content);
        if (contentWidth < this.width) {
            this.contentWidth = (int) contentWidth;
            this.string = str;
        } else if (this.repetType == 2) {
            this.contentWidth = (int) (getContentWidth(this.content) + this.textdistance);
            this.repetCount = 0;
            int i = (this.width / this.contentWidth) + 2;
            this.string = "";
            for (int i2 = 0; i2 <= i; i2++) {
                this.string += this.content;
            }
        } else {
            if (this.xLocation < 0.0f && this.repetType == 0 && (-this.xLocation) > this.contentWidth) {
                this.xLocation = getWidth() * this.startLocationDistance;
            }
            this.contentWidth = (int) getContentWidth(this.content);
            this.string = str;
        }
        invalidate();
    }

    public void setContent(List<String> list) {
        setTextDistance(this.textDistance1);
        String str = "";
        if (list != null && list.size() != 0) {
            for (int i = 0; i < list.size(); i++) {
                str = str + list.get(i) + this.black_count;
            }
        }
        setContent(str);
    }

    public void setKeyWords(String str) {
        this.keyWords = str;
        invalidate();
    }

    public void setRepetType(int i) {
        this.repetType = i;
        this.resetInit = true;
        setContent(this.content);
    }

    public void setTextColor(int i) {
        if (i != 0) {
            this.textColor = i;
            this.paint.setColor(i);
        }
    }

    public void setTextDistance(int i) {
        this.oneBlack_width = getBlacktWidth();
        int iDp2px = (int) (dp2px(i) / this.oneBlack_width);
        if (iDp2px == 0) {
            iDp2px = 1;
        }
        this.textdistance = (int) (this.oneBlack_width * iDp2px);
        this.black_count = "";
        for (int i2 = 0; i2 <= iDp2px; i2++) {
            this.black_count += WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER;
        }
        setContent(this.content);
    }

    public void setTextSize(float f) {
        if (f > 0.0f) {
            this.textSize = f;
            this.paint.setTextSize(dp2px(f));
            this.contentWidth = (int) (getContentWidth(this.content) + this.textdistance);
        }
    }

    public void setTextSpeed(float f) {
        this.speed = f;
    }

    public void stopRoll() {
        this.isRoll = false;
        if (this.thread != null) {
            this.thread.interrupt();
            this.thread = null;
        }
    }
}
