package android.media.projection;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IMediaProjectionWatcherCallback extends IInterface {

    public static abstract class Stub extends Binder implements IMediaProjectionWatcherCallback {
        private static final String DESCRIPTOR = "android.media.projection.IMediaProjectionWatcherCallback";
        static final int TRANSACTION_onStart = 1;
        static final int TRANSACTION_onStop = 2;

        private static class Proxy implements IMediaProjectionWatcherCallback {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.media.projection.IMediaProjectionWatcherCallback
            public void onStart(MediaProjectionInfo mediaProjectionInfo) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (mediaProjectionInfo != null) {
                        parcelObtain.writeInt(1);
                        mediaProjectionInfo.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(1, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.media.projection.IMediaProjectionWatcherCallback
            public void onStop(MediaProjectionInfo mediaProjectionInfo) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (mediaProjectionInfo != null) {
                        parcelObtain.writeInt(1);
                        mediaProjectionInfo.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(2, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMediaProjectionWatcherCallback asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof IMediaProjectionWatcherCallback)) ? new Proxy(iBinder) : (IMediaProjectionWatcherCallback) iInterfaceQueryLocalInterface;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1598968902) {
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
            switch (i) {
                case 1:
                    parcel.enforceInterface(DESCRIPTOR);
                    onStart(parcel.readInt() != 0 ? MediaProjectionInfo.CREATOR.createFromParcel(parcel) : null);
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    onStop(parcel.readInt() != 0 ? MediaProjectionInfo.CREATOR.createFromParcel(parcel) : null);
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    void onStart(MediaProjectionInfo mediaProjectionInfo) throws RemoteException;

    void onStop(MediaProjectionInfo mediaProjectionInfo) throws RemoteException;
}
