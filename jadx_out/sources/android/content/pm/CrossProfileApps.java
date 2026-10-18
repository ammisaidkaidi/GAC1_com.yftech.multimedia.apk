package android.content.pm;

import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import com.android.internal.R;
import com.android.internal.util.UserIcons;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class CrossProfileApps {
    private final Context mContext;
    private final Resources mResources;
    private final ICrossProfileApps mService;
    private final UserManager mUserManager;

    public CrossProfileApps(Context context, ICrossProfileApps iCrossProfileApps) {
        this.mContext = context;
        this.mService = iCrossProfileApps;
        this.mUserManager = (UserManager) context.getSystemService(UserManager.class);
        this.mResources = context.getResources();
    }

    private void verifyCanAccessUser(UserHandle userHandle) {
        if (getTargetUserProfiles().contains(userHandle)) {
            return;
        }
        throw new SecurityException("Not allowed to access " + userHandle);
    }

    public Drawable getProfileSwitchingIconDrawable(UserHandle userHandle) {
        verifyCanAccessUser(userHandle);
        return this.mUserManager.isManagedProfile(userHandle.getIdentifier()) ? this.mResources.getDrawable(R.drawable.ic_corp_badge, null) : UserIcons.getDefaultUserIcon(this.mResources, 0, true);
    }

    public CharSequence getProfileSwitchingLabel(UserHandle userHandle) {
        verifyCanAccessUser(userHandle);
        return this.mResources.getString(this.mUserManager.isManagedProfile(userHandle.getIdentifier()) ? R.string.managed_profile_label : R.string.user_owner_label);
    }

    public List<UserHandle> getTargetUserProfiles() {
        try {
            return this.mService.getTargetUserProfiles(this.mContext.getPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void startMainActivity(ComponentName componentName, UserHandle userHandle) {
        try {
            this.mService.startActivityAsUser(this.mContext.getIApplicationThread(), this.mContext.getPackageName(), componentName, userHandle);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }
}
