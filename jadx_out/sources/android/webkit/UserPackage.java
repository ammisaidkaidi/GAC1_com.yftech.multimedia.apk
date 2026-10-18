package android.webkit;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.os.UserManager;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class UserPackage {
    public static final int MINIMUM_SUPPORTED_SDK = 28;
    private final PackageInfo mPackageInfo;
    private final UserInfo mUserInfo;

    public UserPackage(UserInfo userInfo, PackageInfo packageInfo) {
        this.mUserInfo = userInfo;
        this.mPackageInfo = packageInfo;
    }

    private static List<UserInfo> getAllUsers(Context context) {
        return ((UserManager) context.getSystemService("user")).getUsers(false);
    }

    public static List<UserPackage> getPackageInfosAllUsers(Context context, String str, int i) throws PackageManager.NameNotFoundException {
        List<UserInfo> allUsers = getAllUsers(context);
        ArrayList arrayList = new ArrayList(allUsers.size());
        for (UserInfo userInfo : allUsers) {
            PackageInfo packageInfoAsUser = null;
            try {
                packageInfoAsUser = context.getPackageManager().getPackageInfoAsUser(str, i, userInfo.id);
            } catch (PackageManager.NameNotFoundException unused) {
            }
            arrayList.add(new UserPackage(userInfo, packageInfoAsUser));
        }
        return arrayList;
    }

    public static boolean hasCorrectTargetSdkVersion(PackageInfo packageInfo) {
        return packageInfo.applicationInfo.targetSdkVersion >= 28;
    }

    public PackageInfo getPackageInfo() {
        return this.mPackageInfo;
    }

    public UserInfo getUserInfo() {
        return this.mUserInfo;
    }

    public boolean isEnabledPackage() {
        if (this.mPackageInfo == null) {
            return false;
        }
        return this.mPackageInfo.applicationInfo.enabled;
    }

    public boolean isInstalledPackage() {
        return (this.mPackageInfo == null || (this.mPackageInfo.applicationInfo.flags & 8388608) == 0 || (this.mPackageInfo.applicationInfo.privateFlags & 1) != 0) ? false : true;
    }
}
