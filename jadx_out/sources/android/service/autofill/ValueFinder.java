package android.service.autofill;

import android.view.autofill.AutofillId;
import android.view.autofill.AutofillValue;

/* JADX INFO: loaded from: classes2.dex */
public interface ValueFinder {
    default String findByAutofillId(AutofillId autofillId) {
        AutofillValue autofillValueFindRawValueByAutofillId = findRawValueByAutofillId(autofillId);
        if (autofillValueFindRawValueByAutofillId == null || !autofillValueFindRawValueByAutofillId.isText()) {
            return null;
        }
        return autofillValueFindRawValueByAutofillId.getTextValue().toString();
    }

    AutofillValue findRawValueByAutofillId(AutofillId autofillId);
}
