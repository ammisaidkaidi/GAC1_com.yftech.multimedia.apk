package android.animation;

/* JADX INFO: loaded from: classes.dex */
public abstract class TypeConverter<T, V> {
    private Class<T> mFromClass;
    private Class<V> mToClass;

    public TypeConverter(Class<T> cls, Class<V> cls2) {
        this.mFromClass = cls;
        this.mToClass = cls2;
    }

    public abstract V convert(T t);

    Class<T> getSourceType() {
        return this.mFromClass;
    }

    Class<V> getTargetType() {
        return this.mToClass;
    }
}
