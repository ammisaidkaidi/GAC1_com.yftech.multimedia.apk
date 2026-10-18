package androidx.core.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.ActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.util.Preconditions;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes3.dex */
public final class ShareCompat {
    public static final String EXTRA_CALLING_ACTIVITY = "androidx.core.app.EXTRA_CALLING_ACTIVITY";
    public static final String EXTRA_CALLING_ACTIVITY_INTEROP = "android.support.v4.app.EXTRA_CALLING_ACTIVITY";
    public static final String EXTRA_CALLING_PACKAGE = "androidx.core.app.EXTRA_CALLING_PACKAGE";
    public static final String EXTRA_CALLING_PACKAGE_INTEROP = "android.support.v4.app.EXTRA_CALLING_PACKAGE";
    private static final String HISTORY_FILENAME_PREFIX = ".sharecompat_";

    public static class IntentBuilder {

        @Nullable
        private ArrayList<String> mBccAddresses;

        @Nullable
        private ArrayList<String> mCcAddresses;

        @Nullable
        private CharSequence mChooserTitle;

        @NonNull
        private final Context mContext;

        @NonNull
        private final Intent mIntent = new Intent().setAction(Intent.ACTION_SEND);

        @Nullable
        private ArrayList<Uri> mStreams;

        @Nullable
        private ArrayList<String> mToAddresses;

        private IntentBuilder(@NonNull Context context, @Nullable ComponentName componentName) {
            this.mContext = (Context) Preconditions.checkNotNull(context);
            this.mIntent.putExtra(ShareCompat.EXTRA_CALLING_PACKAGE, context.getPackageName());
            this.mIntent.putExtra(ShareCompat.EXTRA_CALLING_PACKAGE_INTEROP, context.getPackageName());
            this.mIntent.putExtra(ShareCompat.EXTRA_CALLING_ACTIVITY, componentName);
            this.mIntent.putExtra(ShareCompat.EXTRA_CALLING_ACTIVITY_INTEROP, componentName);
            this.mIntent.addFlags(524288);
        }

        private void combineArrayExtra(String str, ArrayList<String> arrayList) {
            String[] stringArrayExtra = this.mIntent.getStringArrayExtra(str);
            int length = stringArrayExtra != null ? stringArrayExtra.length : 0;
            String[] strArr = new String[arrayList.size() + length];
            arrayList.toArray(strArr);
            if (stringArrayExtra != null) {
                System.arraycopy(stringArrayExtra, 0, strArr, arrayList.size(), length);
            }
            this.mIntent.putExtra(str, strArr);
        }

        private void combineArrayExtra(@Nullable String str, @NonNull String[] strArr) {
            Intent intent = getIntent();
            String[] stringArrayExtra = intent.getStringArrayExtra(str);
            int length = stringArrayExtra != null ? stringArrayExtra.length : 0;
            String[] strArr2 = new String[strArr.length + length];
            if (stringArrayExtra != null) {
                System.arraycopy(stringArrayExtra, 0, strArr2, 0, length);
            }
            System.arraycopy(strArr, 0, strArr2, length, strArr.length);
            intent.putExtra(str, strArr2);
        }

        @NonNull
        public static IntentBuilder from(@NonNull Activity activity) {
            return from((Context) Preconditions.checkNotNull(activity), activity.getComponentName());
        }

        @NonNull
        private static IntentBuilder from(@NonNull Context context, @Nullable ComponentName componentName) {
            return new IntentBuilder(context, componentName);
        }

        @NonNull
        public IntentBuilder addEmailBcc(@NonNull String str) {
            if (this.mBccAddresses == null) {
                this.mBccAddresses = new ArrayList<>();
            }
            this.mBccAddresses.add(str);
            return this;
        }

        @NonNull
        public IntentBuilder addEmailBcc(@NonNull String[] strArr) {
            combineArrayExtra(Intent.EXTRA_BCC, strArr);
            return this;
        }

        @NonNull
        public IntentBuilder addEmailCc(@NonNull String str) {
            if (this.mCcAddresses == null) {
                this.mCcAddresses = new ArrayList<>();
            }
            this.mCcAddresses.add(str);
            return this;
        }

        @NonNull
        public IntentBuilder addEmailCc(@NonNull String[] strArr) {
            combineArrayExtra(Intent.EXTRA_CC, strArr);
            return this;
        }

        @NonNull
        public IntentBuilder addEmailTo(@NonNull String str) {
            if (this.mToAddresses == null) {
                this.mToAddresses = new ArrayList<>();
            }
            this.mToAddresses.add(str);
            return this;
        }

        @NonNull
        public IntentBuilder addEmailTo(@NonNull String[] strArr) {
            combineArrayExtra(Intent.EXTRA_EMAIL, strArr);
            return this;
        }

        @NonNull
        public IntentBuilder addStream(@NonNull Uri uri) {
            Uri uri2 = (Uri) this.mIntent.getParcelableExtra(Intent.EXTRA_STREAM);
            if (this.mStreams == null && uri2 == null) {
                return setStream(uri);
            }
            if (this.mStreams == null) {
                this.mStreams = new ArrayList<>();
            }
            if (uri2 != null) {
                this.mIntent.removeExtra(Intent.EXTRA_STREAM);
                this.mStreams.add(uri2);
            }
            this.mStreams.add(uri);
            return this;
        }

        @NonNull
        public Intent createChooserIntent() {
            return Intent.createChooser(getIntent(), this.mChooserTitle);
        }

        @NonNull
        Context getContext() {
            return this.mContext;
        }

        @NonNull
        public Intent getIntent() {
            if (this.mToAddresses != null) {
                combineArrayExtra(Intent.EXTRA_EMAIL, this.mToAddresses);
                this.mToAddresses = null;
            }
            if (this.mCcAddresses != null) {
                combineArrayExtra(Intent.EXTRA_CC, this.mCcAddresses);
                this.mCcAddresses = null;
            }
            if (this.mBccAddresses != null) {
                combineArrayExtra(Intent.EXTRA_BCC, this.mBccAddresses);
                this.mBccAddresses = null;
            }
            boolean z = this.mStreams != null && this.mStreams.size() > 1;
            boolean zEquals = Intent.ACTION_SEND_MULTIPLE.equals(this.mIntent.getAction());
            if (!z && zEquals) {
                this.mIntent.setAction(Intent.ACTION_SEND);
                if (this.mStreams == null || this.mStreams.isEmpty()) {
                    this.mIntent.removeExtra(Intent.EXTRA_STREAM);
                } else {
                    this.mIntent.putExtra(Intent.EXTRA_STREAM, this.mStreams.get(0));
                }
                this.mStreams = null;
            }
            if (z && !zEquals) {
                this.mIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                if (this.mStreams == null || this.mStreams.isEmpty()) {
                    this.mIntent.removeExtra(Intent.EXTRA_STREAM);
                } else {
                    this.mIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, this.mStreams);
                }
            }
            return this.mIntent;
        }

        @NonNull
        public IntentBuilder setChooserTitle(@StringRes int i) {
            return setChooserTitle(this.mContext.getText(i));
        }

        @NonNull
        public IntentBuilder setChooserTitle(@Nullable CharSequence charSequence) {
            this.mChooserTitle = charSequence;
            return this;
        }

        @NonNull
        public IntentBuilder setEmailBcc(@Nullable String[] strArr) {
            this.mIntent.putExtra(Intent.EXTRA_BCC, strArr);
            return this;
        }

        @NonNull
        public IntentBuilder setEmailCc(@Nullable String[] strArr) {
            this.mIntent.putExtra(Intent.EXTRA_CC, strArr);
            return this;
        }

        @NonNull
        public IntentBuilder setEmailTo(@Nullable String[] strArr) {
            if (this.mToAddresses != null) {
                this.mToAddresses = null;
            }
            this.mIntent.putExtra(Intent.EXTRA_EMAIL, strArr);
            return this;
        }

        @NonNull
        public IntentBuilder setHtmlText(@Nullable String str) {
            this.mIntent.putExtra("android.intent.extra.HTML_TEXT", str);
            if (!this.mIntent.hasExtra(Intent.EXTRA_TEXT)) {
                setText(Html.fromHtml(str));
            }
            return this;
        }

        @NonNull
        public IntentBuilder setStream(@Nullable Uri uri) {
            if (!Intent.ACTION_SEND.equals(this.mIntent.getAction())) {
                this.mIntent.setAction(Intent.ACTION_SEND);
            }
            this.mStreams = null;
            this.mIntent.putExtra(Intent.EXTRA_STREAM, uri);
            return this;
        }

        @NonNull
        public IntentBuilder setSubject(@Nullable String str) {
            this.mIntent.putExtra(Intent.EXTRA_SUBJECT, str);
            return this;
        }

        @NonNull
        public IntentBuilder setText(@Nullable CharSequence charSequence) {
            this.mIntent.putExtra(Intent.EXTRA_TEXT, charSequence);
            return this;
        }

        @NonNull
        public IntentBuilder setType(@Nullable String str) {
            this.mIntent.setType(str);
            return this;
        }

        public void startChooser() {
            this.mContext.startActivity(createChooserIntent());
        }
    }

    public static class IntentReader {
        private static final String TAG = "IntentReader";

        @Nullable
        private final ComponentName mCallingActivity;

        @Nullable
        private final String mCallingPackage;

        @NonNull
        private final Context mContext;

        @NonNull
        private final Intent mIntent;

        @Nullable
        private ArrayList<Uri> mStreams;

        private IntentReader(@NonNull Context context, @NonNull Intent intent) {
            this.mContext = (Context) Preconditions.checkNotNull(context);
            this.mIntent = (Intent) Preconditions.checkNotNull(intent);
            this.mCallingPackage = ShareCompat.getCallingPackage(intent);
            this.mCallingActivity = ShareCompat.getCallingActivity(intent);
        }

        @NonNull
        public static IntentReader from(@NonNull Activity activity) {
            return from((Context) Preconditions.checkNotNull(activity), activity.getIntent());
        }

        @NonNull
        private static IntentReader from(@NonNull Context context, @NonNull Intent intent) {
            return new IntentReader(context, intent);
        }

        private static void withinStyle(StringBuilder sb, CharSequence charSequence, int i, int i2) {
            while (i < i2) {
                char cCharAt = charSequence.charAt(i);
                if (cCharAt == '<') {
                    sb.append("&lt;");
                } else if (cCharAt == '>') {
                    sb.append("&gt;");
                } else if (cCharAt == '&') {
                    sb.append("&amp;");
                } else if (cCharAt > '~' || cCharAt < ' ') {
                    sb.append("&#");
                    sb.append((int) cCharAt);
                    sb.append(";");
                } else if (cCharAt == ' ') {
                    while (true) {
                        int i3 = i + 1;
                        if (i3 >= i2 || charSequence.charAt(i3) != ' ') {
                            break;
                        }
                        sb.append("&nbsp;");
                        i = i3;
                    }
                    sb.append(' ');
                } else {
                    sb.append(cCharAt);
                }
                i++;
            }
        }

        @Nullable
        public ComponentName getCallingActivity() {
            return this.mCallingActivity;
        }

        @Nullable
        public Drawable getCallingActivityIcon() {
            if (this.mCallingActivity == null) {
                return null;
            }
            try {
                return this.mContext.getPackageManager().getActivityIcon(this.mCallingActivity);
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Could not retrieve icon for calling activity", e);
                return null;
            }
        }

        @Nullable
        public Drawable getCallingApplicationIcon() {
            if (this.mCallingPackage == null) {
                return null;
            }
            try {
                return this.mContext.getPackageManager().getApplicationIcon(this.mCallingPackage);
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Could not retrieve icon for calling application", e);
                return null;
            }
        }

        @Nullable
        public CharSequence getCallingApplicationLabel() {
            if (this.mCallingPackage == null) {
                return null;
            }
            PackageManager packageManager = this.mContext.getPackageManager();
            try {
                return packageManager.getApplicationLabel(packageManager.getApplicationInfo(this.mCallingPackage, 0));
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Could not retrieve label for calling application", e);
                return null;
            }
        }

        @Nullable
        public String getCallingPackage() {
            return this.mCallingPackage;
        }

        @Nullable
        public String[] getEmailBcc() {
            return this.mIntent.getStringArrayExtra(Intent.EXTRA_BCC);
        }

        @Nullable
        public String[] getEmailCc() {
            return this.mIntent.getStringArrayExtra(Intent.EXTRA_CC);
        }

        @Nullable
        public String[] getEmailTo() {
            return this.mIntent.getStringArrayExtra(Intent.EXTRA_EMAIL);
        }

        @Nullable
        public String getHtmlText() {
            String stringExtra = this.mIntent.getStringExtra("android.intent.extra.HTML_TEXT");
            if (stringExtra != null) {
                return stringExtra;
            }
            CharSequence text = getText();
            if (text instanceof Spanned) {
                return Html.toHtml((Spanned) text);
            }
            if (text == null) {
                return stringExtra;
            }
            if (Build.VERSION.SDK_INT >= 16) {
                return Html.escapeHtml(text);
            }
            StringBuilder sb = new StringBuilder();
            withinStyle(sb, text, 0, text.length());
            return sb.toString();
        }

        @Nullable
        public Uri getStream() {
            return (Uri) this.mIntent.getParcelableExtra(Intent.EXTRA_STREAM);
        }

        @Nullable
        public Uri getStream(int i) {
            if (this.mStreams == null && isMultipleShare()) {
                this.mStreams = this.mIntent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            }
            if (this.mStreams != null) {
                return this.mStreams.get(i);
            }
            if (i == 0) {
                return (Uri) this.mIntent.getParcelableExtra(Intent.EXTRA_STREAM);
            }
            throw new IndexOutOfBoundsException("Stream items available: " + getStreamCount() + " index requested: " + i);
        }

        public int getStreamCount() {
            if (this.mStreams == null && isMultipleShare()) {
                this.mStreams = this.mIntent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            }
            return this.mStreams != null ? this.mStreams.size() : this.mIntent.hasExtra(Intent.EXTRA_STREAM) ? 1 : 0;
        }

        @Nullable
        public String getSubject() {
            return this.mIntent.getStringExtra(Intent.EXTRA_SUBJECT);
        }

        @Nullable
        public CharSequence getText() {
            return this.mIntent.getCharSequenceExtra(Intent.EXTRA_TEXT);
        }

        @Nullable
        public String getType() {
            return this.mIntent.getType();
        }

        public boolean isMultipleShare() {
            return Intent.ACTION_SEND_MULTIPLE.equals(this.mIntent.getAction());
        }

        public boolean isShareIntent() {
            String action = this.mIntent.getAction();
            return Intent.ACTION_SEND.equals(action) || Intent.ACTION_SEND_MULTIPLE.equals(action);
        }

        public boolean isSingleShare() {
            return Intent.ACTION_SEND.equals(this.mIntent.getAction());
        }
    }

    private ShareCompat() {
    }

    public static void configureMenuItem(@NonNull Menu menu, @IdRes int i, @NonNull IntentBuilder intentBuilder) {
        MenuItem menuItemFindItem = menu.findItem(i);
        if (menuItemFindItem != null) {
            configureMenuItem(menuItemFindItem, intentBuilder);
            return;
        }
        throw new IllegalArgumentException("Could not find menu item with id " + i + " in the supplied menu");
    }

    public static void configureMenuItem(@NonNull MenuItem menuItem, @NonNull IntentBuilder intentBuilder) {
        ActionProvider actionProvider = menuItem.getActionProvider();
        ShareActionProvider shareActionProvider = !(actionProvider instanceof ShareActionProvider) ? new ShareActionProvider(intentBuilder.getContext()) : (ShareActionProvider) actionProvider;
        shareActionProvider.setShareHistoryFileName(HISTORY_FILENAME_PREFIX + intentBuilder.getContext().getClass().getName());
        shareActionProvider.setShareIntent(intentBuilder.getIntent());
        menuItem.setActionProvider(shareActionProvider);
        if (Build.VERSION.SDK_INT >= 16 || menuItem.hasSubMenu()) {
            return;
        }
        menuItem.setIntent(intentBuilder.createChooserIntent());
    }

    @Nullable
    public static ComponentName getCallingActivity(@NonNull Activity activity) {
        Intent intent = activity.getIntent();
        ComponentName callingActivity = activity.getCallingActivity();
        return callingActivity == null ? getCallingActivity(intent) : callingActivity;
    }

    @Nullable
    static ComponentName getCallingActivity(@NonNull Intent intent) {
        ComponentName componentName = (ComponentName) intent.getParcelableExtra(EXTRA_CALLING_ACTIVITY);
        return componentName == null ? (ComponentName) intent.getParcelableExtra(EXTRA_CALLING_ACTIVITY_INTEROP) : componentName;
    }

    @Nullable
    public static String getCallingPackage(@NonNull Activity activity) {
        Intent intent = activity.getIntent();
        String callingPackage = activity.getCallingPackage();
        return (callingPackage != null || intent == null) ? callingPackage : getCallingPackage(intent);
    }

    @Nullable
    static String getCallingPackage(@NonNull Intent intent) {
        String stringExtra = intent.getStringExtra(EXTRA_CALLING_PACKAGE);
        return stringExtra == null ? intent.getStringExtra(EXTRA_CALLING_PACKAGE_INTEROP) : stringExtra;
    }
}
