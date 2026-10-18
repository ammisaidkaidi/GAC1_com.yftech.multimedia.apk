package android.os;

/* JADX INFO: loaded from: classes2.dex */
public final class Messenger implements Parcelable {
    public static final Parcelable.Creator<Messenger> CREATOR = new Parcelable.Creator<Messenger>() { // from class: android.os.Messenger.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Messenger createFromParcel(Parcel parcel) {
            IBinder strongBinder = parcel.readStrongBinder();
            if (strongBinder != null) {
                return new Messenger(strongBinder);
            }
            return null;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Messenger[] newArray(int i) {
            return new Messenger[i];
        }
    };
    private final IMessenger mTarget;

    public Messenger(Handler handler) {
        this.mTarget = handler.getIMessenger();
    }

    public Messenger(IBinder iBinder) {
        this.mTarget = IMessenger.Stub.asInterface(iBinder);
    }

    public static Messenger readMessengerOrNullFromParcel(Parcel parcel) {
        IBinder strongBinder = parcel.readStrongBinder();
        if (strongBinder != null) {
            return new Messenger(strongBinder);
        }
        return null;
    }

    public static void writeMessengerOrNullToParcel(Messenger messenger, Parcel parcel) {
        parcel.writeStrongBinder(messenger != null ? messenger.mTarget.asBinder() : null);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        try {
            return this.mTarget.asBinder().equals(((Messenger) obj).mTarget.asBinder());
        } catch (ClassCastException unused) {
            return false;
        }
    }

    public IBinder getBinder() {
        return this.mTarget.asBinder();
    }

    public int hashCode() {
        return this.mTarget.asBinder().hashCode();
    }

    public void send(Message message) throws RemoteException {
        this.mTarget.send(message);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStrongBinder(this.mTarget.asBinder());
    }
}
