package com.android.internal.telephony;

import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.text.Emoji;
import java.text.BreakIterator;
import java.util.Arrays;

/* JADX INFO: loaded from: classes3.dex */
public abstract class SmsMessageBase {
    protected String mEmailBody;
    protected String mEmailFrom;
    protected boolean mIsEmail;
    protected boolean mIsMwi;
    protected String mMessageBody;
    public int mMessageRef;
    protected boolean mMwiDontStore;
    protected boolean mMwiSense;
    protected SmsAddress mOriginatingAddress;
    protected byte[] mPdu;
    protected String mPseudoSubject;
    protected String mScAddress;
    protected long mScTimeMillis;
    protected byte[] mUserData;
    protected SmsHeader mUserDataHeader;
    protected int mStatusOnIcc = -1;
    protected int mIndexOnIcc = -1;

    public static abstract class SubmitPduBase {
        public byte[] encodedMessage;
        public byte[] encodedScAddress;

        public String toString() {
            return "SubmitPdu: encodedScAddress = " + Arrays.toString(this.encodedScAddress) + ", encodedMessage = " + Arrays.toString(this.encodedMessage);
        }
    }

    public static GsmAlphabet.TextEncodingDetails calcUnicodeEncodingDetails(CharSequence charSequence) {
        GsmAlphabet.TextEncodingDetails textEncodingDetails = new GsmAlphabet.TextEncodingDetails();
        int length = charSequence.length() * 2;
        textEncodingDetails.codeUnitSize = 3;
        textEncodingDetails.codeUnitCount = charSequence.length();
        if (length > 140) {
            int i = 134;
            if (!SmsMessage.hasEmsSupport() && length <= 1188) {
                i = 132;
            }
            int i2 = 0;
            int i3 = 0;
            while (i2 < charSequence.length()) {
                int iFindNextUnicodePosition = findNextUnicodePosition(i2, i, charSequence);
                if (iFindNextUnicodePosition == charSequence.length()) {
                    textEncodingDetails.codeUnitsRemaining = (i2 + (i / 2)) - charSequence.length();
                }
                i3++;
                i2 = iFindNextUnicodePosition;
            }
            textEncodingDetails.msgCount = i3;
        } else {
            textEncodingDetails.msgCount = 1;
            textEncodingDetails.codeUnitsRemaining = (140 - length) / 2;
        }
        return textEncodingDetails;
    }

    public static int findNextUnicodePosition(int i, int i2, CharSequence charSequence) {
        int iMin = Math.min((i2 / 2) + i, charSequence.length());
        if (iMin >= charSequence.length()) {
            return iMin;
        }
        BreakIterator characterInstance = BreakIterator.getCharacterInstance();
        characterInstance.setText(charSequence.toString());
        if (characterInstance.isBoundary(iMin)) {
            return iMin;
        }
        int iPreceding = characterInstance.preceding(iMin);
        while (true) {
            int i3 = iPreceding + 4;
            if (i3 > iMin || !Emoji.isRegionalIndicatorSymbol(Character.codePointAt(charSequence, iPreceding)) || !Emoji.isRegionalIndicatorSymbol(Character.codePointAt(charSequence, iPreceding + 2))) {
                break;
            }
            iPreceding = i3;
        }
        if (iPreceding > i) {
            return iPreceding;
        }
        return Character.isHighSurrogate(charSequence.charAt(iMin + (-1))) ? iMin - 1 : iMin;
    }

    protected void extractEmailAddressFromMessageBody() {
        String[] strArrSplit = this.mMessageBody.split("( /)|( )", 2);
        if (strArrSplit.length < 2) {
            return;
        }
        this.mEmailFrom = strArrSplit[0];
        this.mEmailBody = strArrSplit[1];
        this.mIsEmail = Telephony.Mms.isEmailAddress(this.mEmailFrom);
    }

    public String getDisplayMessageBody() {
        return this.mIsEmail ? this.mEmailBody : getMessageBody();
    }

    public String getDisplayOriginatingAddress() {
        return this.mIsEmail ? this.mEmailFrom : getOriginatingAddress();
    }

    public String getEmailBody() {
        return this.mEmailBody;
    }

    public String getEmailFrom() {
        return this.mEmailFrom;
    }

    public int getIndexOnIcc() {
        return this.mIndexOnIcc;
    }

    public String getMessageBody() {
        return this.mMessageBody;
    }

    public abstract SmsConstants.MessageClass getMessageClass();

    public String getOriginatingAddress() {
        if (this.mOriginatingAddress == null) {
            return null;
        }
        return this.mOriginatingAddress.getAddressString();
    }

    public byte[] getPdu() {
        return this.mPdu;
    }

    public abstract int getProtocolIdentifier();

    public String getPseudoSubject() {
        return this.mPseudoSubject == null ? "" : this.mPseudoSubject;
    }

    public String getServiceCenterAddress() {
        return this.mScAddress;
    }

    public abstract int getStatus();

    public int getStatusOnIcc() {
        return this.mStatusOnIcc;
    }

    public long getTimestampMillis() {
        return this.mScTimeMillis;
    }

    public byte[] getUserData() {
        return this.mUserData;
    }

    public SmsHeader getUserDataHeader() {
        return this.mUserDataHeader;
    }

    public abstract boolean isCphsMwiMessage();

    public boolean isEmail() {
        return this.mIsEmail;
    }

    public abstract boolean isMWIClearMessage();

    public abstract boolean isMWISetMessage();

    public abstract boolean isMwiDontStore();

    public abstract boolean isReplace();

    public abstract boolean isReplyPathPresent();

    public abstract boolean isStatusReportMessage();

    protected void parseMessageBody() {
        if (this.mOriginatingAddress == null || !this.mOriginatingAddress.couldBeEmailGateway()) {
            return;
        }
        extractEmailAddressFromMessageBody();
    }
}
