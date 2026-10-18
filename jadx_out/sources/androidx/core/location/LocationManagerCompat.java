package androidx.core.location;

import android.location.LocationManager;
import android.os.Build;
import androidx.annotation.NonNull;

/* JADX INFO: loaded from: classes3.dex */
public final class LocationManagerCompat {
    private LocationManagerCompat() {
    }

    public static boolean isLocationEnabled(@NonNull LocationManager locationManager) {
        if (Build.VERSION.SDK_INT >= 28) {
            return locationManager.isLocationEnabled();
        }
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
