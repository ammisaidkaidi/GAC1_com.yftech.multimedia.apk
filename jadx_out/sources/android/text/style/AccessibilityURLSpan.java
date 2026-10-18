package android.text.style;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;

/* JADX INFO: loaded from: classes2.dex */
public class AccessibilityURLSpan extends URLSpan implements Parcelable {
    final AccessibilityClickableSpan mAccessibilityClickableSpan;

    public AccessibilityURLSpan(Parcel parcel) {
        super(parcel);
        this.mAccessibilityClickableSpan = new AccessibilityClickableSpan(parcel);
    }

    public AccessibilityURLSpan(URLSpan uRLSpan) {
        super(uRLSpan.getURL());
        this.mAccessibilityClickableSpan = new AccessibilityClickableSpan(uRLSpan.getId());
    }

    public void copyConnectionDataFrom(AccessibilityNodeInfo accessibilityNodeInfo) {
        this.mAccessibilityClickableSpan.copyConnectionDataFrom(accessibilityNodeInfo);
    }

    @Override // android.text.style.URLSpan, android.text.ParcelableSpan
    public int getSpanTypeId() {
        return getSpanTypeIdInternal();
    }

    @Override // android.text.style.URLSpan, android.text.ParcelableSpan
    public int getSpanTypeIdInternal() {
        return 26;
    }

    @Override // android.text.style.URLSpan, android.text.style.ClickableSpan
    public void onClick(View view) {
        this.mAccessibilityClickableSpan.onClick(view);
    }

    @Override // android.text.style.URLSpan, android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        writeToParcelInternal(parcel, i);
    }

    @Override // android.text.style.URLSpan, android.text.ParcelableSpan
    public void writeToParcelInternal(Parcel parcel, int i) {
        super.writeToParcelInternal(parcel, i);
        this.mAccessibilityClickableSpan.writeToParcel(parcel, i);
    }
}
