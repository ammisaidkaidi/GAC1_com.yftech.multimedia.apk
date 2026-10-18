package com.android.internal.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityThread;
import android.app.VoiceInteractor;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.os.RemoteException;
import android.os.StrictMode;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.SettingsStringUtil;
import android.text.TextUtils;
import android.util.IconDrawableFactory;
import android.util.Log;
import android.util.Slog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.internal.R;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.content.PackageMonitor;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.widget.ResolverDrawerLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/* JADX INFO: loaded from: classes3.dex */
public class ResolverActivity extends Activity {
    private static final boolean DEBUG = false;
    private static final String TAG = "ResolverActivity";
    protected ResolveListAdapter mAdapter;
    private AbsListView mAdapterView;
    private Button mAlwaysButton;
    private int mDefaultTitleResId;
    private int mIconDpi;
    IconDrawableFactory mIconFactory;
    protected int mLaunchedFromUid;
    private int mLayoutId;
    private Button mOnceButton;
    private PickTargetOptionRequest mPickOptionRequest;
    protected PackageManager mPm;
    private Runnable mPostListReadyRunnable;
    private View mProfileView;
    private String mReferrerPackage;
    private boolean mRegistered;
    protected ResolverDrawerLayout mResolverDrawerLayout;
    private boolean mRetainInOnStop;
    private boolean mSafeForwardingMode;
    private boolean mSupportsAlwaysUseOption;
    private CharSequence mTitle;
    private int mLastSelected = -1;
    private boolean mResolvingHome = false;
    private int mProfileSwitchMessageId = -1;
    private final ArrayList<Intent> mIntents = new ArrayList<>();
    private final PackageMonitor mPackageMonitor = new PackageMonitor() { // from class: com.android.internal.app.ResolverActivity.1
        @Override // com.android.internal.content.PackageMonitor
        public boolean onPackageChanged(String str, int i, String[] strArr) {
            return true;
        }

        @Override // com.android.internal.content.PackageMonitor
        public void onSomePackagesChanged() {
            ResolverActivity.this.mAdapter.handlePackagesChanged();
            if (ResolverActivity.this.mProfileView != null) {
                ResolverActivity.this.bindProfileView();
            }
        }
    };

    private enum ActionTitle {
        VIEW("android.intent.action.VIEW", R.string.whichViewApplication, R.string.whichViewApplicationNamed, R.string.whichViewApplicationLabel),
        EDIT(Intent.ACTION_EDIT, R.string.whichEditApplication, R.string.whichEditApplicationNamed, R.string.whichEditApplicationLabel),
        SEND(Intent.ACTION_SEND, R.string.whichSendApplication, R.string.whichSendApplicationNamed, R.string.whichSendApplicationLabel),
        SENDTO(Intent.ACTION_SENDTO, R.string.whichSendToApplication, R.string.whichSendToApplicationNamed, R.string.whichSendToApplicationLabel),
        SEND_MULTIPLE(Intent.ACTION_SEND_MULTIPLE, R.string.whichSendApplication, R.string.whichSendApplicationNamed, R.string.whichSendApplicationLabel),
        CAPTURE_IMAGE(MediaStore.ACTION_IMAGE_CAPTURE, R.string.whichImageCaptureApplication, R.string.whichImageCaptureApplicationNamed, R.string.whichImageCaptureApplicationLabel),
        DEFAULT(null, R.string.whichApplication, R.string.whichApplicationNamed, R.string.whichApplicationLabel),
        HOME(Intent.ACTION_MAIN, R.string.whichHomeApplication, R.string.whichHomeApplicationNamed, R.string.whichHomeApplicationLabel);

        public final String action;
        public final int labelRes;
        public final int namedTitleRes;
        public final int titleRes;

        ActionTitle(String str, int i, int i2, int i3) {
            this.action = str;
            this.titleRes = i;
            this.namedTitleRes = i2;
            this.labelRes = i3;
        }

        public static ActionTitle forAction(String str) {
            for (ActionTitle actionTitle : values()) {
                if (actionTitle != HOME && str != null && str.equals(actionTitle.action)) {
                    return actionTitle;
                }
            }
            return DEFAULT;
        }
    }

    public final class DisplayResolveInfo implements TargetInfo {
        private Drawable mBadge;
        private Drawable mDisplayIcon;
        private final CharSequence mDisplayLabel;
        private final CharSequence mExtendedInfo;
        private boolean mPinned;
        private final ResolveInfo mResolveInfo;
        private final Intent mResolvedIntent;
        private final List<Intent> mSourceIntents = new ArrayList();

        public DisplayResolveInfo(Intent intent, ResolveInfo resolveInfo, CharSequence charSequence, CharSequence charSequence2, Intent intent2) {
            this.mSourceIntents.add(intent);
            this.mResolveInfo = resolveInfo;
            this.mDisplayLabel = charSequence;
            this.mExtendedInfo = charSequence2;
            Intent intent3 = new Intent(intent2 == null ? ResolverActivity.this.getReplacementIntent(resolveInfo.activityInfo, ResolverActivity.this.getTargetIntent()) : intent2);
            intent3.addFlags(View.SCROLLBARS_OUTSIDE_INSET);
            ActivityInfo activityInfo = this.mResolveInfo.activityInfo;
            intent3.setComponent(new ComponentName(activityInfo.applicationInfo.packageName, activityInfo.name));
            this.mResolvedIntent = intent3;
        }

        private DisplayResolveInfo(DisplayResolveInfo displayResolveInfo, Intent intent, int i) {
            this.mSourceIntents.addAll(displayResolveInfo.getAllSourceIntents());
            this.mResolveInfo = displayResolveInfo.mResolveInfo;
            this.mDisplayLabel = displayResolveInfo.mDisplayLabel;
            this.mDisplayIcon = displayResolveInfo.mDisplayIcon;
            this.mExtendedInfo = displayResolveInfo.mExtendedInfo;
            this.mResolvedIntent = new Intent(displayResolveInfo.mResolvedIntent);
            this.mResolvedIntent.fillIn(intent, i);
            this.mPinned = displayResolveInfo.mPinned;
        }

        public void addAlternateSourceIntent(Intent intent) {
            this.mSourceIntents.add(intent);
        }

        @Override // com.android.internal.app.ResolverActivity.TargetInfo
        public TargetInfo cloneFilledIn(Intent intent, int i) {
            return ResolverActivity.this.new DisplayResolveInfo(this, intent, i);
        }

        @Override // com.android.internal.app.ResolverActivity.TargetInfo
        public List<Intent> getAllSourceIntents() {
            return this.mSourceIntents;
        }

        @Override // com.android.internal.app.ResolverActivity.TargetInfo
        public CharSequence getBadgeContentDescription() {
            return null;
        }

        @Override // com.android.internal.app.ResolverActivity.TargetInfo
        public Drawable getBadgeIcon() {
            if (TextUtils.isEmpty(getExtendedInfo())) {
                return null;
            }
            if (this.mBadge == null && this.mResolveInfo != null && this.mResolveInfo.activityInfo != null && this.mResolveInfo.activityInfo.applicationInfo != null) {
                if (this.mResolveInfo.activityInfo.icon == 0 || this.mResolveInfo.activityInfo.icon == this.mResolveInfo.activityInfo.applicationInfo.icon) {
                    return null;
                }
                this.mBadge = this.mResolveInfo.activityInfo.applicationInfo.loadIcon(ResolverActivity.this.mPm);
            }
            return this.mBadge;
        }

        @Override // com.android.internal.app.ResolverActivity.TargetInfo
        public Drawable getDisplayIcon() {
            return this.mDisplayIcon;
        }

        @Override // com.android.internal.app.ResolverActivity.TargetInfo
        public CharSequence getDisplayLabel() {
            return this.mDisplayLabel;
        }

        @Override // com.android.internal.app.ResolverActivity.TargetInfo
        public CharSequence getExtendedInfo() {
            return this.mExtendedInfo;
        }

        @Override // com.android.internal.app.ResolverActivity.TargetInfo
        public ResolveInfo getResolveInfo() {
            return this.mResolveInfo;
        }

        @Override // com.android.internal.app.ResolverActivity.TargetInfo
        public ComponentName getResolvedComponentName() {
            return new ComponentName(this.mResolveInfo.activityInfo.packageName, this.mResolveInfo.activityInfo.name);
        }

        @Override // com.android.internal.app.ResolverActivity.TargetInfo
        public Intent getResolvedIntent() {
            return this.mResolvedIntent;
        }

        public boolean hasDisplayIcon() {
            return this.mDisplayIcon != null;
        }

        @Override // com.android.internal.app.ResolverActivity.TargetInfo
        public boolean isPinned() {
            return this.mPinned;
        }

        public void setDisplayIcon(Drawable drawable) {
            this.mDisplayIcon = drawable;
        }

        public void setPinned(boolean z) {
            this.mPinned = z;
        }

        @Override // com.android.internal.app.ResolverActivity.TargetInfo
        public boolean start(Activity activity, Bundle bundle) {
            activity.startActivity(this.mResolvedIntent, bundle);
            return true;
        }

        @Override // com.android.internal.app.ResolverActivity.TargetInfo
        public boolean startAsCaller(Activity activity, Bundle bundle, int i) {
            activity.startActivityAsCaller(this.mResolvedIntent, bundle, false, i);
            return true;
        }

        @Override // com.android.internal.app.ResolverActivity.TargetInfo
        public boolean startAsUser(Activity activity, Bundle bundle, UserHandle userHandle) {
            activity.startActivityAsUser(this.mResolvedIntent, bundle, userHandle);
            return false;
        }
    }

    class ItemClickListener implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
        ItemClickListener() {
        }

        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            ListView listView = adapterView instanceof ListView ? (ListView) adapterView : null;
            if (listView != null) {
                i -= listView.getHeaderViewsCount();
            }
            if (i >= 0 && ResolverActivity.this.mAdapter.resolveInfoForPosition(i, true) != null) {
                int checkedItemPosition = ResolverActivity.this.mAdapterView.getCheckedItemPosition();
                boolean z = checkedItemPosition != -1;
                if (ResolverActivity.this.useLayoutWithDefault() || ((z && ResolverActivity.this.mLastSelected == checkedItemPosition) || ResolverActivity.this.mAlwaysButton == null)) {
                    ResolverActivity.this.startSelected(i, false, true);
                    return;
                }
                ResolverActivity.this.setAlwaysButtonEnabled(z, checkedItemPosition, true);
                ResolverActivity.this.mOnceButton.setEnabled(z);
                if (z) {
                    ResolverActivity.this.mAdapterView.smoothScrollToPosition(checkedItemPosition);
                }
                ResolverActivity.this.mLastSelected = checkedItemPosition;
            }
        }

        @Override // android.widget.AdapterView.OnItemLongClickListener
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
            ListView listView = adapterView instanceof ListView ? (ListView) adapterView : null;
            if (listView != null) {
                i -= listView.getHeaderViewsCount();
            }
            if (i < 0) {
                return false;
            }
            ResolverActivity.this.showTargetDetails(ResolverActivity.this.mAdapter.resolveInfoForPosition(i, true));
            return true;
        }
    }

    class LoadAdapterIconTask extends LoadIconTask {
        public LoadAdapterIconTask(DisplayResolveInfo displayResolveInfo) {
            super(displayResolveInfo);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.android.internal.app.ResolverActivity.LoadIconTask, android.os.AsyncTask
        public void onPostExecute(Drawable drawable) {
            super.onPostExecute(drawable);
            if (ResolverActivity.this.mProfileView != null && ResolverActivity.this.mAdapter.getOtherProfile() == this.mDisplayResolveInfo) {
                ResolverActivity.this.bindProfileView();
            }
            ResolverActivity.this.mAdapter.notifyDataSetChanged();
        }
    }

    class LoadIconIntoViewTask extends LoadIconTask {
        private final ImageView mTargetView;

        public LoadIconIntoViewTask(DisplayResolveInfo displayResolveInfo, ImageView imageView) {
            super(displayResolveInfo);
            this.mTargetView = imageView;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.android.internal.app.ResolverActivity.LoadIconTask, android.os.AsyncTask
        public void onPostExecute(Drawable drawable) {
            super.onPostExecute(drawable);
            this.mTargetView.setImageDrawable(drawable);
        }
    }

    abstract class LoadIconTask extends AsyncTask<Void, Void, Drawable> {
        protected final DisplayResolveInfo mDisplayResolveInfo;
        private final ResolveInfo mResolveInfo;

        public LoadIconTask(DisplayResolveInfo displayResolveInfo) {
            this.mDisplayResolveInfo = displayResolveInfo;
            this.mResolveInfo = displayResolveInfo.getResolveInfo();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Drawable doInBackground(Void... voidArr) {
            return ResolverActivity.this.loadIconForResolveInfo(this.mResolveInfo);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.AsyncTask
        public void onPostExecute(Drawable drawable) {
            this.mDisplayResolveInfo.setDisplayIcon(drawable);
        }
    }

    static class PickTargetOptionRequest extends VoiceInteractor.PickOptionRequest {
        public PickTargetOptionRequest(VoiceInteractor.Prompt prompt, VoiceInteractor.PickOptionRequest.Option[] optionArr, Bundle bundle) {
            super(prompt, optionArr, bundle);
        }

        @Override // android.app.VoiceInteractor.Request
        public void onCancel() {
            super.onCancel();
            ResolverActivity resolverActivity = (ResolverActivity) getActivity();
            if (resolverActivity != null) {
                resolverActivity.mPickOptionRequest = null;
                resolverActivity.finish();
            }
        }

        @Override // android.app.VoiceInteractor.PickOptionRequest
        public void onPickOptionResult(boolean z, VoiceInteractor.PickOptionRequest.Option[] optionArr, Bundle bundle) {
            ResolverActivity resolverActivity;
            super.onPickOptionResult(z, optionArr, bundle);
            if (optionArr.length == 1 && (resolverActivity = (ResolverActivity) getActivity()) != null && resolverActivity.onTargetSelected(resolverActivity.mAdapter.getItem(optionArr[0].getIndex()), false)) {
                resolverActivity.mPickOptionRequest = null;
                resolverActivity.finish();
            }
        }
    }

    public class ResolveListAdapter extends BaseAdapter {
        private final List<ResolveInfo> mBaseResolveList;
        List<DisplayResolveInfo> mDisplayList;
        private boolean mFilterLastUsed;
        private boolean mHasExtendedInfo;
        protected final LayoutInflater mInflater;
        private final Intent[] mInitialIntents;
        private final List<Intent> mIntents;
        protected ResolveInfo mLastChosen;
        private int mLastChosenPosition = -1;
        private DisplayResolveInfo mOtherProfile;
        private int mPlaceholderCount;
        private ResolverListController mResolverListController;
        List<ResolvedComponentInfo> mUnfilteredResolveList;

        public ResolveListAdapter(Context context, List<Intent> list, Intent[] intentArr, List<ResolveInfo> list2, int i, boolean z, ResolverListController resolverListController) {
            this.mIntents = list;
            this.mInitialIntents = intentArr;
            this.mBaseResolveList = list2;
            ResolverActivity.this.mLaunchedFromUid = i;
            this.mInflater = LayoutInflater.from(context);
            this.mDisplayList = new ArrayList();
            this.mFilterLastUsed = z;
            this.mResolverListController = resolverListController;
        }

        private void addResolveInfo(DisplayResolveInfo displayResolveInfo) {
            if (displayResolveInfo == null || displayResolveInfo.mResolveInfo == null || displayResolveInfo.mResolveInfo.targetUserId != -2) {
                return;
            }
            Iterator<DisplayResolveInfo> it = this.mDisplayList.iterator();
            while (it.hasNext()) {
                if (ResolverActivity.resolveInfoMatch(displayResolveInfo.mResolveInfo, it.next().mResolveInfo)) {
                    return;
                }
            }
            this.mDisplayList.add(displayResolveInfo);
        }

        private void addResolveInfoWithAlternates(ResolvedComponentInfo resolvedComponentInfo, CharSequence charSequence, CharSequence charSequence2) {
            int count = resolvedComponentInfo.getCount();
            Intent intentAt = resolvedComponentInfo.getIntentAt(0);
            ResolveInfo resolveInfoAt = resolvedComponentInfo.getResolveInfoAt(0);
            Intent replacementIntent = ResolverActivity.this.getReplacementIntent(resolveInfoAt.activityInfo, intentAt);
            DisplayResolveInfo displayResolveInfo = ResolverActivity.this.new DisplayResolveInfo(intentAt, resolveInfoAt, charSequence2, charSequence, replacementIntent);
            displayResolveInfo.setPinned(resolvedComponentInfo.isPinned());
            addResolveInfo(displayResolveInfo);
            if (replacementIntent == intentAt) {
                for (int i = 1; i < count; i++) {
                    displayResolveInfo.addAlternateSourceIntent(resolvedComponentInfo.getIntentAt(i));
                }
            }
            updateLastChosenPosition(resolveInfoAt);
        }

        private void onBindView(View view, TargetInfo targetInfo) {
            ViewHolder viewHolder = (ViewHolder) view.getTag();
            if (targetInfo == null) {
                viewHolder.icon.setImageDrawable(ResolverActivity.this.getDrawable(R.drawable.resolver_icon_placeholder));
                return;
            }
            if (!TextUtils.equals(viewHolder.text.getText(), targetInfo.getDisplayLabel())) {
                viewHolder.text.setText(targetInfo.getDisplayLabel());
            }
            if (showsExtendedInfo(targetInfo)) {
                viewHolder.text2.setVisibility(0);
                viewHolder.text2.setText(targetInfo.getExtendedInfo());
            } else {
                viewHolder.text2.setVisibility(8);
            }
            if (targetInfo instanceof DisplayResolveInfo) {
                DisplayResolveInfo displayResolveInfo = (DisplayResolveInfo) targetInfo;
                if (!displayResolveInfo.hasDisplayIcon()) {
                    ResolverActivity.this.new LoadAdapterIconTask(displayResolveInfo).execute(new Void[0]);
                }
            }
            viewHolder.icon.setImageDrawable(targetInfo.getDisplayIcon());
            if (viewHolder.badge != null) {
                Drawable badgeIcon = targetInfo.getBadgeIcon();
                if (badgeIcon == null) {
                    viewHolder.badge.setVisibility(8);
                    return;
                }
                viewHolder.badge.setImageDrawable(badgeIcon);
                viewHolder.badge.setContentDescription(targetInfo.getBadgeContentDescription());
                viewHolder.badge.setVisibility(0);
            }
        }

        private void postListReadyRunnable() {
            if (ResolverActivity.this.mPostListReadyRunnable == null) {
                ResolverActivity.this.mPostListReadyRunnable = new Runnable() { // from class: com.android.internal.app.ResolverActivity.ResolveListAdapter.2
                    @Override // java.lang.Runnable
                    public void run() throws PackageManager.NameNotFoundException {
                        ResolverActivity.this.setTitleAndIcon();
                        ResolverActivity.this.resetAlwaysOrOnceButtonBar();
                        ResolveListAdapter.this.onListRebuilt();
                        ResolverActivity.this.mPostListReadyRunnable = null;
                    }
                };
                ResolverActivity.this.getMainThreadHandler().post(ResolverActivity.this.mPostListReadyRunnable);
            }
        }

        private void processGroup(List<ResolvedComponentInfo> list, int i, int i2, ResolvedComponentInfo resolvedComponentInfo, CharSequence charSequence) {
            if ((i2 - i) + 1 == 1) {
                addResolveInfoWithAlternates(resolvedComponentInfo, null, charSequence);
                return;
            }
            this.mHasExtendedInfo = true;
            CharSequence charSequenceLoadLabel = resolvedComponentInfo.getResolveInfoAt(0).activityInfo.applicationInfo.loadLabel(ResolverActivity.this.mPm);
            boolean z = charSequenceLoadLabel == null;
            if (!z) {
                HashSet hashSet = new HashSet();
                hashSet.add(charSequenceLoadLabel);
                for (int i3 = i + 1; i3 <= i2; i3++) {
                    CharSequence charSequenceLoadLabel2 = list.get(i3).getResolveInfoAt(0).activityInfo.applicationInfo.loadLabel(ResolverActivity.this.mPm);
                    if (charSequenceLoadLabel2 == null || hashSet.contains(charSequenceLoadLabel2)) {
                        z = true;
                        break;
                    }
                    hashSet.add(charSequenceLoadLabel2);
                }
                hashSet.clear();
            }
            while (i <= i2) {
                ResolvedComponentInfo resolvedComponentInfo2 = list.get(i);
                ResolveInfo resolveInfoAt = resolvedComponentInfo2.getResolveInfoAt(0);
                addResolveInfoWithAlternates(resolvedComponentInfo2, z ? resolveInfoAt.activityInfo.packageName : resolveInfoAt.activityInfo.applicationInfo.loadLabel(ResolverActivity.this.mPm), charSequence);
                i++;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void processSortedList(List<ResolvedComponentInfo> list) {
            int size;
            if (list != null && (size = list.size()) != 0) {
                if (this.mInitialIntents != null) {
                    for (int i = 0; i < this.mInitialIntents.length; i++) {
                        Intent intent = this.mInitialIntents[i];
                        if (intent != null) {
                            ActivityInfo activityInfoResolveActivityInfo = intent.resolveActivityInfo(ResolverActivity.this.getPackageManager(), 0);
                            if (activityInfoResolveActivityInfo == null) {
                                Log.w(ResolverActivity.TAG, "No activity found for " + intent);
                            } else {
                                ResolveInfo resolveInfo = new ResolveInfo();
                                resolveInfo.activityInfo = activityInfoResolveActivityInfo;
                                UserManager userManager = (UserManager) ResolverActivity.this.getSystemService("user");
                                if (intent instanceof LabeledIntent) {
                                    LabeledIntent labeledIntent = (LabeledIntent) intent;
                                    resolveInfo.resolvePackageName = labeledIntent.getSourcePackage();
                                    resolveInfo.labelRes = labeledIntent.getLabelResource();
                                    resolveInfo.nonLocalizedLabel = labeledIntent.getNonLocalizedLabel();
                                    resolveInfo.icon = labeledIntent.getIconResource();
                                    resolveInfo.iconResourceId = resolveInfo.icon;
                                }
                                if (userManager.isManagedProfile()) {
                                    resolveInfo.noResourceId = true;
                                    resolveInfo.icon = 0;
                                }
                                addResolveInfo(ResolverActivity.this.new DisplayResolveInfo(intent, resolveInfo, resolveInfo.loadLabel(ResolverActivity.this.getPackageManager()), null, intent));
                            }
                        }
                    }
                }
                ResolvedComponentInfo resolvedComponentInfo = list.get(0);
                ResolveInfo resolveInfoAt = resolvedComponentInfo.getResolveInfoAt(0);
                CharSequence charSequenceLoadLabel = resolveInfoAt.loadLabel(ResolverActivity.this.mPm);
                this.mHasExtendedInfo = false;
                ResolvedComponentInfo resolvedComponentInfo2 = resolvedComponentInfo;
                CharSequence charSequence = charSequenceLoadLabel;
                int i2 = 0;
                for (int i3 = 1; i3 < size; i3++) {
                    if (charSequence == null) {
                        charSequence = resolveInfoAt.activityInfo.packageName;
                    }
                    ResolvedComponentInfo resolvedComponentInfo3 = list.get(i3);
                    ResolveInfo resolveInfoAt2 = resolvedComponentInfo3.getResolveInfoAt(0);
                    CharSequence charSequenceLoadLabel2 = resolveInfoAt2.loadLabel(ResolverActivity.this.mPm);
                    if (charSequenceLoadLabel2 == null) {
                        charSequenceLoadLabel2 = resolveInfoAt2.activityInfo.packageName;
                    }
                    CharSequence charSequence2 = charSequenceLoadLabel2;
                    if (!charSequence2.equals(charSequence)) {
                        processGroup(list, i2, i3 - 1, resolvedComponentInfo2, charSequence);
                        i2 = i3;
                        resolvedComponentInfo2 = resolvedComponentInfo3;
                        resolveInfoAt = resolveInfoAt2;
                        charSequence = charSequence2;
                    }
                }
                processGroup(list, i2, size - 1, resolvedComponentInfo2, charSequence);
            }
            postListReadyRunnable();
        }

        private void updateLastChosenPosition(ResolveInfo resolveInfo) {
            if (this.mOtherProfile != null) {
                this.mLastChosenPosition = -1;
            } else if (this.mLastChosen != null && this.mLastChosen.activityInfo.packageName.equals(resolveInfo.activityInfo.packageName) && this.mLastChosen.activityInfo.name.equals(resolveInfo.activityInfo.name)) {
                this.mLastChosenPosition = this.mDisplayList.size() - 1;
            }
        }

        public final void bindView(int i, View view) {
            onBindView(view, getItem(i));
        }

        public final View createView(ViewGroup viewGroup) {
            View viewOnCreateView = onCreateView(viewGroup);
            viewOnCreateView.setTag(new ViewHolder(viewOnCreateView));
            return viewOnCreateView;
        }

        @Override // android.widget.Adapter
        public int getCount() {
            int size = (this.mDisplayList == null || this.mDisplayList.isEmpty()) ? this.mPlaceholderCount : this.mDisplayList.size();
            return (!this.mFilterLastUsed || this.mLastChosenPosition < 0) ? size : size - 1;
        }

        public DisplayResolveInfo getDisplayInfoAt(int i) {
            return this.mDisplayList.get(i);
        }

        public int getDisplayInfoCount() {
            return this.mDisplayList.size();
        }

        public DisplayResolveInfo getDisplayResolveInfo(int i) {
            return this.mDisplayList.get(i);
        }

        public int getDisplayResolveInfoCount() {
            return this.mDisplayList.size();
        }

        public DisplayResolveInfo getFilteredItem() {
            if (!this.mFilterLastUsed || this.mLastChosenPosition < 0) {
                return null;
            }
            return this.mDisplayList.get(this.mLastChosenPosition);
        }

        public int getFilteredPosition() {
            if (!this.mFilterLastUsed || this.mLastChosenPosition < 0) {
                return -1;
            }
            return this.mLastChosenPosition;
        }

        @Override // android.widget.Adapter
        public TargetInfo getItem(int i) {
            if (this.mFilterLastUsed && this.mLastChosenPosition >= 0 && i >= this.mLastChosenPosition) {
                i++;
            }
            if (this.mDisplayList.size() > i) {
                return this.mDisplayList.get(i);
            }
            return null;
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return i;
        }

        public DisplayResolveInfo getOtherProfile() {
            return this.mOtherProfile;
        }

        public int getPlaceholderCount() {
            return this.mPlaceholderCount;
        }

        public float getScore(DisplayResolveInfo displayResolveInfo) {
            return this.mResolverListController.getScore(displayResolveInfo);
        }

        public int getUnfilteredCount() {
            return this.mDisplayList.size();
        }

        @Override // android.widget.Adapter
        public final View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = createView(viewGroup);
            }
            onBindView(view, getItem(i));
            return view;
        }

        public void handlePackagesChanged() {
            rebuildList();
            if (getCount() == 0) {
                ResolverActivity.this.finish();
            }
        }

        public boolean hasExtendedInfo() {
            return this.mHasExtendedInfo;
        }

        public boolean hasFilteredItem() {
            return this.mFilterLastUsed && this.mLastChosen != null;
        }

        public boolean hasResolvedTarget(ResolveInfo resolveInfo) {
            int size = this.mDisplayList.size();
            for (int i = 0; i < size; i++) {
                if (ResolverActivity.resolveInfoMatch(resolveInfo, this.mDisplayList.get(i).getResolveInfo())) {
                    return true;
                }
            }
            return false;
        }

        public boolean isComponentPinned(ComponentName componentName) {
            return false;
        }

        public View onCreateView(ViewGroup viewGroup) {
            return this.mInflater.inflate(R.layout.resolve_list_item, viewGroup, false);
        }

        public void onListRebuilt() {
            if (getUnfilteredCount() == 1 && getOtherProfile() == null) {
                TargetInfo targetInfoTargetInfoForPosition = targetInfoForPosition(0, false);
                if (ResolverActivity.this.shouldAutoLaunchSingleChoice(targetInfoTargetInfoForPosition)) {
                    ResolverActivity.this.safelyStartActivity(targetInfoTargetInfoForPosition);
                    ResolverActivity.this.finish();
                }
            }
        }

        protected boolean rebuildList() {
            List<ResolvedComponentInfo> resolversForIntent;
            this.mOtherProfile = null;
            this.mLastChosen = null;
            this.mLastChosenPosition = -1;
            this.mDisplayList.clear();
            if (this.mBaseResolveList != null) {
                resolversForIntent = new ArrayList<>();
                this.mUnfilteredResolveList = resolversForIntent;
                this.mResolverListController.addResolveListDedupe(resolversForIntent, ResolverActivity.this.getTargetIntent(), this.mBaseResolveList);
            } else {
                resolversForIntent = this.mResolverListController.getResolversForIntent(shouldGetResolvedFilter(), ResolverActivity.this.shouldGetActivityMetadata(), this.mIntents);
                this.mUnfilteredResolveList = resolversForIntent;
                if (resolversForIntent == null) {
                    processSortedList(resolversForIntent);
                    return true;
                }
                ArrayList<ResolvedComponentInfo> arrayListFilterIneligibleActivities = this.mResolverListController.filterIneligibleActivities(resolversForIntent, true);
                if (arrayListFilterIneligibleActivities != null) {
                    this.mUnfilteredResolveList = arrayListFilterIneligibleActivities;
                }
            }
            for (ResolvedComponentInfo resolvedComponentInfo : resolversForIntent) {
                if (resolvedComponentInfo.getResolveInfoAt(0).targetUserId != -2) {
                    this.mOtherProfile = ResolverActivity.this.new DisplayResolveInfo(resolvedComponentInfo.getIntentAt(0), resolvedComponentInfo.getResolveInfoAt(0), resolvedComponentInfo.getResolveInfoAt(0).loadLabel(ResolverActivity.this.mPm), resolvedComponentInfo.getResolveInfoAt(0).loadLabel(ResolverActivity.this.mPm), ResolverActivity.this.getReplacementIntent(resolvedComponentInfo.getResolveInfoAt(0).activityInfo, resolvedComponentInfo.getIntentAt(0)));
                    resolversForIntent.remove(resolvedComponentInfo);
                    break;
                }
            }
            if (this.mOtherProfile == null) {
                try {
                    this.mLastChosen = this.mResolverListController.getLastChosen();
                } catch (RemoteException e) {
                    Log.d(ResolverActivity.TAG, "Error calling getLastChosenActivity\n" + e);
                }
            }
            if (resolversForIntent == null || resolversForIntent.size() <= 0) {
                processSortedList(resolversForIntent);
                return true;
            }
            ArrayList<ResolvedComponentInfo> arrayListFilterLowPriority = this.mResolverListController.filterLowPriority(resolversForIntent, this.mUnfilteredResolveList == resolversForIntent);
            if (arrayListFilterLowPriority != null) {
                this.mUnfilteredResolveList = arrayListFilterLowPriority;
            }
            if (resolversForIntent.size() <= 1) {
                processSortedList(resolversForIntent);
                return true;
            }
            int size = resolversForIntent.size();
            if (ResolverActivity.this.useLayoutWithDefault()) {
                size--;
            }
            setPlaceholderCount(size);
            new AsyncTask<List<ResolvedComponentInfo>, Void, List<ResolvedComponentInfo>>() { // from class: com.android.internal.app.ResolverActivity.ResolveListAdapter.1
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public List<ResolvedComponentInfo> doInBackground(List<ResolvedComponentInfo>... listArr) {
                    ResolveListAdapter.this.mResolverListController.sort(listArr[0]);
                    return listArr[0];
                }

                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public void onPostExecute(List<ResolvedComponentInfo> list) {
                    ResolveListAdapter.this.processSortedList(list);
                    if (ResolverActivity.this.mProfileView != null) {
                        ResolverActivity.this.bindProfileView();
                    }
                    ResolveListAdapter.this.notifyDataSetChanged();
                }
            }.execute(resolversForIntent);
            postListReadyRunnable();
            return false;
        }

        public ResolveInfo resolveInfoForPosition(int i, boolean z) {
            TargetInfo targetInfoTargetInfoForPosition = targetInfoForPosition(i, z);
            if (targetInfoTargetInfoForPosition != null) {
                return targetInfoTargetInfoForPosition.getResolveInfo();
            }
            return null;
        }

        public void setPlaceholderCount(int i) {
            this.mPlaceholderCount = i;
        }

        public boolean shouldGetResolvedFilter() {
            return this.mFilterLastUsed;
        }

        public boolean showsExtendedInfo(TargetInfo targetInfo) {
            return !TextUtils.isEmpty(targetInfo.getExtendedInfo());
        }

        public TargetInfo targetInfoForPosition(int i, boolean z) {
            if (z) {
                return getItem(i);
            }
            if (this.mDisplayList.size() > i) {
                return this.mDisplayList.get(i);
            }
            return null;
        }

        public void updateChooserCounts(String str, int i, String str2) {
            this.mResolverListController.updateChooserCounts(str, i, str2);
        }

        public void updateModel(ComponentName componentName) {
            this.mResolverListController.updateModel(componentName);
        }
    }

    @VisibleForTesting
    public static final class ResolvedComponentInfo {
        private boolean mPinned;
        public final ComponentName name;
        private final List<Intent> mIntents = new ArrayList();
        private final List<ResolveInfo> mResolveInfos = new ArrayList();

        public ResolvedComponentInfo(ComponentName componentName, Intent intent, ResolveInfo resolveInfo) {
            this.name = componentName;
            add(intent, resolveInfo);
        }

        public void add(Intent intent, ResolveInfo resolveInfo) {
            this.mIntents.add(intent);
            this.mResolveInfos.add(resolveInfo);
        }

        public int findIntent(Intent intent) {
            int size = this.mIntents.size();
            for (int i = 0; i < size; i++) {
                if (intent.equals(this.mIntents.get(i))) {
                    return i;
                }
            }
            return -1;
        }

        public int findResolveInfo(ResolveInfo resolveInfo) {
            int size = this.mResolveInfos.size();
            for (int i = 0; i < size; i++) {
                if (resolveInfo.equals(this.mResolveInfos.get(i))) {
                    return i;
                }
            }
            return -1;
        }

        public int getCount() {
            return this.mIntents.size();
        }

        public Intent getIntentAt(int i) {
            if (i >= 0) {
                return this.mIntents.get(i);
            }
            return null;
        }

        public ResolveInfo getResolveInfoAt(int i) {
            if (i >= 0) {
                return this.mResolveInfos.get(i);
            }
            return null;
        }

        public boolean isPinned() {
            return this.mPinned;
        }

        public void setPinned(boolean z) {
            this.mPinned = z;
        }
    }

    public interface TargetInfo {
        TargetInfo cloneFilledIn(Intent intent, int i);

        List<Intent> getAllSourceIntents();

        CharSequence getBadgeContentDescription();

        Drawable getBadgeIcon();

        Drawable getDisplayIcon();

        CharSequence getDisplayLabel();

        CharSequence getExtendedInfo();

        ResolveInfo getResolveInfo();

        ComponentName getResolvedComponentName();

        Intent getResolvedIntent();

        boolean isPinned();

        boolean start(Activity activity, Bundle bundle);

        boolean startAsCaller(Activity activity, Bundle bundle, int i);

        boolean startAsUser(Activity activity, Bundle bundle, UserHandle userHandle);
    }

    static class ViewHolder {
        public ImageView badge;
        public ImageView icon;
        public TextView text;
        public TextView text2;

        public ViewHolder(View view) {
            this.text = (TextView) view.findViewById(16908308);
            this.text2 = (TextView) view.findViewById(16908309);
            this.icon = (ImageView) view.findViewById(16908294);
            this.badge = (ImageView) view.findViewById(R.id.target_badge);
        }
    }

    public static int getLabelRes(String str) {
        return ActionTitle.forAction(str).labelRes;
    }

    private boolean hasManagedProfile() {
        UserManager userManager = (UserManager) getSystemService("user");
        if (userManager == null) {
            return false;
        }
        try {
            for (UserInfo userInfo : userManager.getProfiles(getUserId())) {
                if (userInfo != null && userInfo.isManagedProfile()) {
                    return true;
                }
            }
            return false;
        } catch (SecurityException unused) {
            return false;
        }
    }

    static final boolean isSpecificUriMatch(int i) {
        int i2 = i & IntentFilter.MATCH_CATEGORY_MASK;
        return i2 >= 3145728 && i2 <= 5242880;
    }

    private Intent makeMyIntent() {
        Intent intent = new Intent(getIntent());
        intent.setComponent(null);
        intent.setFlags(intent.getFlags() & (-8388609));
        return intent;
    }

    static boolean resolveInfoMatch(ResolveInfo resolveInfo, ResolveInfo resolveInfo2) {
        if (resolveInfo == null) {
            if (resolveInfo2 != null) {
                return false;
            }
        } else if (resolveInfo.activityInfo == null) {
            if (resolveInfo2.activityInfo != null) {
                return false;
            }
        } else if (!Objects.equals(resolveInfo.activityInfo.name, resolveInfo2.activityInfo.name) || !Objects.equals(resolveInfo.activityInfo.packageName, resolveInfo2.activityInfo.packageName)) {
            return false;
        }
        return true;
    }

    private void safelyStartActivityInternal(TargetInfo targetInfo) {
        String launchedFromPackage;
        if (this.mProfileSwitchMessageId != -1) {
            Toast.makeText(this, getString(this.mProfileSwitchMessageId), 1).show();
        }
        if (!this.mSafeForwardingMode) {
            if (targetInfo.start(this, null)) {
                onActivityStarted(targetInfo);
                return;
            }
            return;
        }
        try {
            if (targetInfo.startAsCaller(this, null, -10000)) {
                onActivityStarted(targetInfo);
            }
        } catch (RuntimeException e) {
            try {
                launchedFromPackage = ActivityManager.getService().getLaunchedFromPackage(getActivityToken());
            } catch (RemoteException unused) {
                launchedFromPackage = "??";
            }
            Slog.wtf(TAG, "Unable to launch as uid " + this.mLaunchedFromUid + " package " + launchedFromPackage + ", while running in " + ActivityThread.currentProcessName(), e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setAlwaysButtonEnabled(boolean z, int i, boolean z2) {
        boolean z3;
        if (z) {
            ResolveInfo resolveInfoResolveInfoForPosition = this.mAdapter.resolveInfoForPosition(i, z2);
            if (resolveInfoResolveInfoForPosition == null) {
                Log.e(TAG, "Invalid position supplied to setAlwaysButtonEnabled");
                return;
            } else {
                if (resolveInfoResolveInfoForPosition.targetUserId != -2) {
                    Log.e(TAG, "Attempted to set selection to resolve info for another user");
                    return;
                }
                z3 = true;
            }
        } else {
            z3 = false;
        }
        this.mAlwaysButton.setEnabled(z3);
    }

    private void setProfileSwitchMessageId(int i) {
        if (i == -2 || i == UserHandle.myUserId()) {
            return;
        }
        UserManager userManager = (UserManager) getSystemService("user");
        UserInfo userInfo = userManager.getUserInfo(i);
        boolean zIsManagedProfile = userInfo != null ? userInfo.isManagedProfile() : false;
        boolean zIsManagedProfile2 = userManager.isManagedProfile();
        if (zIsManagedProfile && !zIsManagedProfile2) {
            this.mProfileSwitchMessageId = R.string.forward_intent_to_owner;
        } else {
            if (zIsManagedProfile || !zIsManagedProfile2) {
                return;
            }
            this.mProfileSwitchMessageId = R.string.forward_intent_to_work;
        }
    }

    private boolean supportsManagedProfiles(ResolveInfo resolveInfo) {
        try {
            return getPackageManager().getApplicationInfo(resolveInfo.activityInfo.packageName, 0).targetSdkVersion >= 21;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean useLayoutWithDefault() {
        return this.mSupportsAlwaysUseOption && this.mAdapter.hasFilteredItem();
    }

    void bindProfileView() {
        DisplayResolveInfo otherProfile = this.mAdapter.getOtherProfile();
        if (otherProfile == null) {
            this.mProfileView.setVisibility(8);
            return;
        }
        this.mProfileView.setVisibility(0);
        View viewFindViewById = this.mProfileView.findViewById(R.id.profile_button);
        if (!(viewFindViewById instanceof TextView)) {
            viewFindViewById = this.mProfileView.findViewById(16908308);
        }
        ((TextView) viewFindViewById).setText(otherProfile.getDisplayLabel());
    }

    public boolean configureContentView(List<Intent> list, Intent[] intentArr, List<ResolveInfo> list2) {
        this.mAdapter = createAdapter(this, list, intentArr, list2, this.mLaunchedFromUid, this.mSupportsAlwaysUseOption && !isVoiceInteraction());
        boolean zRebuildList = this.mAdapter.rebuildList();
        if (useLayoutWithDefault()) {
            this.mLayoutId = R.layout.resolver_list_with_default;
        } else {
            this.mLayoutId = getLayoutResource();
        }
        setContentView(this.mLayoutId);
        int unfilteredCount = this.mAdapter.getUnfilteredCount();
        if (zRebuildList && unfilteredCount == 1 && this.mAdapter.getOtherProfile() == null) {
            TargetInfo targetInfoTargetInfoForPosition = this.mAdapter.targetInfoForPosition(0, false);
            if (shouldAutoLaunchSingleChoice(targetInfoTargetInfoForPosition)) {
                safelyStartActivity(targetInfoTargetInfoForPosition);
                this.mPackageMonitor.unregister();
                this.mRegistered = false;
                finish();
                return true;
            }
        }
        this.mAdapterView = (AbsListView) findViewById(R.id.resolver_list);
        if (unfilteredCount == 0 && this.mAdapter.mPlaceholderCount == 0) {
            ((TextView) findViewById(16908292)).setVisibility(0);
            this.mAdapterView.setVisibility(8);
        } else {
            this.mAdapterView.setVisibility(0);
            onPrepareAdapterView(this.mAdapterView, this.mAdapter);
        }
        return false;
    }

    public ResolveListAdapter createAdapter(Context context, List<Intent> list, Intent[] intentArr, List<ResolveInfo> list2, int i, boolean z) {
        return new ResolveListAdapter(context, list, intentArr, list2, i, z, createListController());
    }

    @VisibleForTesting
    protected ResolverListController createListController() {
        return new ResolverListController(this, this.mPm, getTargetIntent(), getReferrerPackageName(), this.mLaunchedFromUid);
    }

    void dismiss() {
        if (isFinishing()) {
            return;
        }
        finish();
    }

    Drawable getIcon(Resources resources, int i) {
        try {
            return resources.getDrawableForDensity(i, this.mIconDpi);
        } catch (Resources.NotFoundException unused) {
            return null;
        }
    }

    public int getLayoutResource() {
        return R.layout.resolver_list;
    }

    protected String getReferrerPackageName() {
        Uri referrer = getReferrer();
        if (referrer == null || !"android-app".equals(referrer.getScheme())) {
            return null;
        }
        return referrer.getHost();
    }

    public Intent getReplacementIntent(ActivityInfo activityInfo, Intent intent) {
        return intent;
    }

    public Intent getTargetIntent() {
        if (this.mIntents.isEmpty()) {
            return null;
        }
        return this.mIntents.get(0);
    }

    protected CharSequence getTitleForAction(String str, int i) {
        ActionTitle actionTitleForAction = this.mResolvingHome ? ActionTitle.HOME : ActionTitle.forAction(str);
        boolean z = this.mAdapter.getFilteredPosition() >= 0;
        if (actionTitleForAction != ActionTitle.DEFAULT || i == 0) {
            return z ? getString(actionTitleForAction.namedTitleRes, this.mAdapter.getFilteredItem().getDisplayLabel()) : getString(actionTitleForAction.titleRes);
        }
        return getString(i);
    }

    Drawable loadIconForResolveInfo(ResolveInfo resolveInfo) {
        Drawable icon;
        Drawable icon2;
        try {
            if (resolveInfo.resolvePackageName != null && resolveInfo.icon != 0 && (icon2 = getIcon(this.mPm.getResourcesForApplication(resolveInfo.resolvePackageName), resolveInfo.icon)) != null) {
                return this.mIconFactory.getShadowedIcon(icon2);
            }
            int iconResource = resolveInfo.getIconResource();
            if (iconResource != 0 && (icon = getIcon(this.mPm.getResourcesForApplication(resolveInfo.activityInfo.packageName), iconResource)) != null) {
                return this.mIconFactory.getShadowedIcon(icon);
            }
            return this.mIconFactory.getBadgedIcon(resolveInfo.activityInfo.applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Couldn't find resources for package", e);
        }
    }

    public void onActivityStarted(TargetInfo targetInfo) {
    }

    public void onButtonClick(View view) {
        startSelected(this.mAdapter.hasFilteredItem() ? this.mAdapter.getFilteredPosition() : this.mAdapterView.getCheckedItemPosition(), view.getId() == 16908767, !this.mAdapter.hasFilteredItem());
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mAdapter.handlePackagesChanged();
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        Intent intentMakeMyIntent = makeMyIntent();
        Set<String> categories = intentMakeMyIntent.getCategories();
        if (Intent.ACTION_MAIN.equals(intentMakeMyIntent.getAction()) && categories != null && categories.size() == 1 && categories.contains(Intent.CATEGORY_HOME)) {
            this.mResolvingHome = true;
        }
        setSafeForwardingMode(true);
        onCreate(bundle, intentMakeMyIntent, null, 0, null, null, true);
    }

    protected void onCreate(Bundle bundle, Intent intent, CharSequence charSequence, int i, Intent[] intentArr, List<ResolveInfo> list, boolean z) {
        setTheme(R.style.Theme_DeviceDefault_Resolver);
        super.onCreate(bundle);
        setProfileSwitchMessageId(intent.getContentUserHint());
        try {
            this.mLaunchedFromUid = ActivityManager.getService().getLaunchedFromUid(getActivityToken());
        } catch (RemoteException unused) {
            this.mLaunchedFromUid = -1;
        }
        if (this.mLaunchedFromUid < 0 || UserHandle.isIsolated(this.mLaunchedFromUid)) {
            finish();
            return;
        }
        this.mPm = getPackageManager();
        this.mPackageMonitor.register(this, getMainLooper(), false);
        this.mRegistered = true;
        this.mReferrerPackage = getReferrerPackageName();
        this.mSupportsAlwaysUseOption = z;
        this.mIconDpi = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE)).getLauncherLargeIconDensity();
        this.mIntents.add(0, new Intent(intent));
        this.mTitle = charSequence;
        this.mDefaultTitleResId = i;
        if (configureContentView(this.mIntents, intentArr, list)) {
            return;
        }
        ResolverDrawerLayout resolverDrawerLayout = (ResolverDrawerLayout) findViewById(R.id.contentPanel);
        if (resolverDrawerLayout != null) {
            resolverDrawerLayout.setOnDismissedListener(new ResolverDrawerLayout.OnDismissedListener() { // from class: com.android.internal.app.ResolverActivity.2
                @Override // com.android.internal.widget.ResolverDrawerLayout.OnDismissedListener
                public void onDismissed() {
                    ResolverActivity.this.finish();
                }
            });
            if (isVoiceInteraction()) {
                resolverDrawerLayout.setCollapsed(false);
            }
            this.mResolverDrawerLayout = resolverDrawerLayout;
        }
        this.mProfileView = findViewById(R.id.profile_button);
        if (this.mProfileView != null) {
            this.mProfileView.setOnClickListener(new View.OnClickListener() { // from class: com.android.internal.app.ResolverActivity.3
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    DisplayResolveInfo otherProfile = ResolverActivity.this.mAdapter.getOtherProfile();
                    if (otherProfile == null) {
                        return;
                    }
                    ResolverActivity.this.mProfileSwitchMessageId = -1;
                    ResolverActivity.this.onTargetSelected(otherProfile, false);
                    ResolverActivity.this.finish();
                }
            });
            bindProfileView();
        }
        if (isVoiceInteraction()) {
            onSetupVoiceInteraction();
        }
        Set<String> categories = intent.getCategories();
        int i2 = this.mAdapter.hasFilteredItem() ? 451 : 453;
        StringBuilder sb = new StringBuilder();
        sb.append(intent.getAction());
        sb.append(SettingsStringUtil.DELIMITER);
        sb.append(intent.getType());
        sb.append(SettingsStringUtil.DELIMITER);
        sb.append(categories != null ? Arrays.toString(categories.toArray()) : "");
        MetricsLogger.action(this, i2, sb.toString());
        this.mIconFactory = IconDrawableFactory.newInstance(this, true);
    }

    protected void onCreate(Bundle bundle, Intent intent, CharSequence charSequence, Intent[] intentArr, List<ResolveInfo> list, boolean z) {
        onCreate(bundle, intent, charSequence, 0, intentArr, list, z);
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        if (!isChangingConfigurations() && this.mPickOptionRequest != null) {
            this.mPickOptionRequest.cancel();
        }
        if (this.mPostListReadyRunnable != null) {
            getMainThreadHandler().removeCallbacks(this.mPostListReadyRunnable);
            this.mPostListReadyRunnable = null;
        }
        if (this.mAdapter == null || this.mAdapter.mResolverListController == null) {
            return;
        }
        this.mAdapter.mResolverListController.destroy();
    }

    public void onPrepareAdapterView(AbsListView absListView, ResolveListAdapter resolveListAdapter) {
        boolean zHasFilteredItem = resolveListAdapter.hasFilteredItem();
        ListView listView = absListView instanceof ListView ? (ListView) absListView : null;
        absListView.setAdapter(this.mAdapter);
        ItemClickListener itemClickListener = new ItemClickListener();
        absListView.setOnItemClickListener(itemClickListener);
        absListView.setOnItemLongClickListener(itemClickListener);
        if (this.mSupportsAlwaysUseOption) {
            listView.setChoiceMode(1);
        }
        if (zHasFilteredItem && listView != null && listView.getHeaderViewsCount() == 0) {
            listView.addHeaderView(LayoutInflater.from(this).inflate(R.layout.resolver_different_item_header, (ViewGroup) listView, false));
        }
    }

    @Override // android.app.Activity
    protected void onRestart() {
        super.onRestart();
        if (!this.mRegistered) {
            this.mPackageMonitor.register(this, getMainLooper(), false);
            this.mRegistered = true;
        }
        this.mAdapter.handlePackagesChanged();
        if (this.mProfileView != null) {
            bindProfileView();
        }
    }

    @Override // android.app.Activity
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        resetAlwaysOrOnceButtonBar();
    }

    public void onSetupVoiceInteraction() {
        sendVoiceChoicesIfNeeded();
    }

    @Override // android.app.Activity
    protected void onStop() {
        super.onStop();
        if (this.mRegistered) {
            this.mPackageMonitor.unregister();
            this.mRegistered = false;
        }
        if ((getIntent().getFlags() & 268435456) == 0 || isVoiceInteraction() || this.mResolvingHome || this.mRetainInOnStop || isChangingConfigurations()) {
            return;
        }
        finish();
    }

    protected boolean onTargetSelected(TargetInfo targetInfo, boolean z) {
        String strResolveType;
        ResolveInfo resolveInfo = targetInfo.getResolveInfo();
        Intent resolvedIntent = targetInfo != null ? targetInfo.getResolvedIntent() : null;
        if (resolvedIntent != null && ((this.mSupportsAlwaysUseOption || this.mAdapter.hasFilteredItem()) && this.mAdapter.mUnfilteredResolveList != null)) {
            IntentFilter intentFilter = new IntentFilter();
            Intent selector = resolvedIntent.getSelector() != null ? resolvedIntent.getSelector() : resolvedIntent;
            String action = selector.getAction();
            if (action != null) {
                intentFilter.addAction(action);
            }
            Set<String> categories = selector.getCategories();
            if (categories != null) {
                Iterator<String> it = categories.iterator();
                while (it.hasNext()) {
                    intentFilter.addCategory(it.next());
                }
            }
            intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
            int i = resolveInfo.match & IntentFilter.MATCH_CATEGORY_MASK;
            Uri data = selector.getData();
            if (i == 6291456 && (strResolveType = selector.resolveType(this)) != null) {
                try {
                    intentFilter.addDataType(strResolveType);
                } catch (IntentFilter.MalformedMimeTypeException e) {
                    Log.w(TAG, e);
                    intentFilter = null;
                }
            }
            if (data != null && data.getScheme() != null && (i != 6291456 || (!"file".equals(data.getScheme()) && !"content".equals(data.getScheme())))) {
                intentFilter.addDataScheme(data.getScheme());
                Iterator<PatternMatcher> itSchemeSpecificPartsIterator = resolveInfo.filter.schemeSpecificPartsIterator();
                if (itSchemeSpecificPartsIterator != null) {
                    String schemeSpecificPart = data.getSchemeSpecificPart();
                    while (schemeSpecificPart != null && itSchemeSpecificPartsIterator.hasNext()) {
                        PatternMatcher next = itSchemeSpecificPartsIterator.next();
                        if (next.match(schemeSpecificPart)) {
                            intentFilter.addDataSchemeSpecificPart(next.getPath(), next.getType());
                            break;
                        }
                    }
                }
                Iterator<IntentFilter.AuthorityEntry> itAuthoritiesIterator = resolveInfo.filter.authoritiesIterator();
                if (itAuthoritiesIterator != null) {
                    while (itAuthoritiesIterator.hasNext()) {
                        IntentFilter.AuthorityEntry next2 = itAuthoritiesIterator.next();
                        if (next2.match(data) >= 0) {
                            int port = next2.getPort();
                            intentFilter.addDataAuthority(next2.getHost(), port >= 0 ? Integer.toString(port) : null);
                            break;
                        }
                    }
                }
                Iterator<PatternMatcher> itPathsIterator = resolveInfo.filter.pathsIterator();
                if (itPathsIterator != null) {
                    String path = data.getPath();
                    while (path != null && itPathsIterator.hasNext()) {
                        PatternMatcher next3 = itPathsIterator.next();
                        if (next3.match(path)) {
                            intentFilter.addDataPath(next3.getPath(), next3.getType());
                            break;
                        }
                    }
                }
            }
            if (intentFilter != null) {
                int size = this.mAdapter.mUnfilteredResolveList.size();
                boolean z2 = this.mAdapter.mOtherProfile != null;
                ComponentName[] componentNameArr = !z2 ? new ComponentName[size] : new ComponentName[size + 1];
                int i2 = 0;
                for (int i3 = 0; i3 < size; i3++) {
                    ResolveInfo resolveInfoAt = this.mAdapter.mUnfilteredResolveList.get(i3).getResolveInfoAt(0);
                    componentNameArr[i3] = new ComponentName(resolveInfoAt.activityInfo.packageName, resolveInfoAt.activityInfo.name);
                    if (resolveInfoAt.match > i2) {
                        i2 = resolveInfoAt.match;
                    }
                }
                if (z2) {
                    componentNameArr[size] = this.mAdapter.mOtherProfile.getResolvedComponentName();
                    int i4 = this.mAdapter.mOtherProfile.getResolveInfo().match;
                    if (i4 > i2) {
                        i2 = i4;
                    }
                }
                if (z) {
                    int userId = getUserId();
                    PackageManager packageManager = getPackageManager();
                    packageManager.addPreferredActivity(intentFilter, i2, componentNameArr, resolvedIntent.getComponent());
                    if (resolveInfo.handleAllWebDataURI && TextUtils.isEmpty(packageManager.getDefaultBrowserPackageNameAsUser(userId))) {
                        packageManager.setDefaultBrowserPackageNameAsUser(resolveInfo.activityInfo.packageName, userId);
                    }
                } else {
                    try {
                        this.mAdapter.mResolverListController.setLastChosen(resolvedIntent, intentFilter, i2);
                    } catch (RemoteException e2) {
                        Log.d(TAG, "Error calling setLastChosenActivity\n" + e2);
                    }
                }
            }
        }
        if (targetInfo != null) {
            safelyStartActivity(targetInfo);
        }
        return true;
    }

    VoiceInteractor.PickOptionRequest.Option optionForChooserTarget(TargetInfo targetInfo, int i) {
        return new VoiceInteractor.PickOptionRequest.Option(targetInfo.getDisplayLabel(), i);
    }

    public void resetAlwaysOrOnceButtonBar() {
        if (this.mSupportsAlwaysUseOption) {
            ViewGroup viewGroup = (ViewGroup) findViewById(R.id.button_bar);
            if (viewGroup != null) {
                viewGroup.setVisibility(0);
                this.mAlwaysButton = (Button) viewGroup.findViewById(R.id.button_always);
                this.mOnceButton = (Button) viewGroup.findViewById(R.id.button_once);
            } else {
                Log.e(TAG, "Layout unexpectedly does not have a button bar");
            }
        }
        if (useLayoutWithDefault() && this.mAdapter.getFilteredPosition() != -1) {
            setAlwaysButtonEnabled(true, this.mAdapter.getFilteredPosition(), false);
            this.mOnceButton.setEnabled(true);
        } else {
            if (this.mAdapterView == null || this.mAdapterView.getCheckedItemPosition() == -1) {
                return;
            }
            setAlwaysButtonEnabled(true, this.mAdapterView.getCheckedItemPosition(), true);
            this.mOnceButton.setEnabled(true);
        }
    }

    public void safelyStartActivity(TargetInfo targetInfo) {
        StrictMode.disableDeathOnFileUriExposure();
        try {
            safelyStartActivityInternal(targetInfo);
        } finally {
            StrictMode.enableDeathOnFileUriExposure();
        }
    }

    public void sendVoiceChoicesIfNeeded() {
        if (isVoiceInteraction()) {
            VoiceInteractor.PickOptionRequest.Option[] optionArr = new VoiceInteractor.PickOptionRequest.Option[this.mAdapter.getCount()];
            int length = optionArr.length;
            for (int i = 0; i < length; i++) {
                optionArr[i] = optionForChooserTarget(this.mAdapter.getItem(i), i);
            }
            this.mPickOptionRequest = new PickTargetOptionRequest(new VoiceInteractor.Prompt(getTitle()), optionArr, null);
            getVoiceInteractor().submitRequest(this.mPickOptionRequest);
        }
    }

    protected final void setAdditionalTargets(Intent[] intentArr) {
        if (intentArr != null) {
            for (Intent intent : intentArr) {
                this.mIntents.add(intent);
            }
        }
    }

    protected void setRetainInOnStop(boolean z) {
        this.mRetainInOnStop = z;
    }

    public void setSafeForwardingMode(boolean z) {
        this.mSafeForwardingMode = z;
    }

    public void setTitleAndIcon() throws PackageManager.NameNotFoundException {
        TextView textView;
        if (this.mAdapter.getCount() == 0 && this.mAdapter.mPlaceholderCount == 0 && (textView = (TextView) findViewById(16908310)) != null) {
            textView.setVisibility(8);
        }
        CharSequence titleForAction = this.mTitle != null ? this.mTitle : getTitleForAction(getTargetIntent().getAction(), this.mDefaultTitleResId);
        if (!TextUtils.isEmpty(titleForAction)) {
            TextView textView2 = (TextView) findViewById(16908310);
            if (textView2 != null) {
                textView2.setText(titleForAction);
            }
            setTitle(titleForAction);
            ImageView imageView = (ImageView) findViewById(R.id.title_icon);
            if (imageView != null) {
                ApplicationInfo applicationInfo = null;
                try {
                    if (!TextUtils.isEmpty(this.mReferrerPackage)) {
                        applicationInfo = this.mPm.getApplicationInfo(this.mReferrerPackage, 0);
                    }
                } catch (PackageManager.NameNotFoundException unused) {
                    Log.e(TAG, "Could not find referrer package " + this.mReferrerPackage);
                }
                if (applicationInfo != null) {
                    imageView.setImageDrawable(applicationInfo.loadIcon(this.mPm));
                }
            }
        }
        ImageView imageView2 = (ImageView) findViewById(16908294);
        DisplayResolveInfo filteredItem = this.mAdapter.getFilteredItem();
        if (imageView2 == null || filteredItem == null) {
            return;
        }
        new LoadIconIntoViewTask(filteredItem, imageView2).execute(new Void[0]);
    }

    public boolean shouldAutoLaunchSingleChoice(TargetInfo targetInfo) {
        return true;
    }

    public boolean shouldGetActivityMetadata() {
        return false;
    }

    public void showTargetDetails(ResolveInfo resolveInfo) {
        startActivity(new Intent().setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.fromParts("package", resolveInfo.activityInfo.packageName, null)).addFlags(524288));
    }

    public void startSelected(int i, boolean z, boolean z2) {
        if (isFinishing()) {
            return;
        }
        ResolveInfo resolveInfoResolveInfoForPosition = this.mAdapter.resolveInfoForPosition(i, z2);
        if (this.mResolvingHome && hasManagedProfile() && !supportsManagedProfiles(resolveInfoResolveInfoForPosition)) {
            Toast.makeText(this, String.format(getResources().getString(R.string.activity_resolver_work_profiles_support), resolveInfoResolveInfoForPosition.activityInfo.loadLabel(getPackageManager()).toString()), 1).show();
            return;
        }
        TargetInfo targetInfoTargetInfoForPosition = this.mAdapter.targetInfoForPosition(i, z2);
        if (targetInfoTargetInfoForPosition != null && onTargetSelected(targetInfoTargetInfoForPosition, z)) {
            if (z && this.mSupportsAlwaysUseOption) {
                MetricsLogger.action(this, 455);
            } else if (this.mSupportsAlwaysUseOption) {
                MetricsLogger.action(this, 456);
            } else {
                MetricsLogger.action(this, 457);
            }
            MetricsLogger.action(this, this.mAdapter.hasFilteredItem() ? 452 : 454);
            finish();
        }
    }
}
