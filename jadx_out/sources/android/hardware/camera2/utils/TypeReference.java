package android.hardware.camera2.utils;

import com.android.internal.util.Preconditions;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

/* JADX INFO: loaded from: classes.dex */
public abstract class TypeReference<T> {
    private final int mHash;
    private final Type mType;

    private static class SpecializedBaseTypeReference extends TypeReference {
        public SpecializedBaseTypeReference(Type type) {
            super(type);
        }
    }

    private static class SpecializedTypeReference<T> extends TypeReference<T> {
        public SpecializedTypeReference(Class<T> cls) {
            super(cls);
        }
    }

    protected TypeReference() {
        this.mType = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        if (containsTypeVariable(this.mType)) {
            throw new IllegalArgumentException("Including a type variable in a type reference is not allowed");
        }
        this.mHash = this.mType.hashCode();
    }

    private TypeReference(Type type) {
        this.mType = type;
        if (containsTypeVariable(this.mType)) {
            throw new IllegalArgumentException("Including a type variable in a type reference is not allowed");
        }
        this.mHash = this.mType.hashCode();
    }

    public static boolean containsTypeVariable(Type type) {
        if (type == null) {
            return false;
        }
        if (type instanceof TypeVariable) {
            return true;
        }
        if (type instanceof Class) {
            Class cls = (Class) type;
            if (cls.getTypeParameters().length != 0) {
                return true;
            }
            return containsTypeVariable(cls.getDeclaringClass());
        }
        if (!(type instanceof ParameterizedType)) {
            if (!(type instanceof WildcardType)) {
                return false;
            }
            WildcardType wildcardType = (WildcardType) type;
            return containsTypeVariable(wildcardType.getLowerBounds()) || containsTypeVariable(wildcardType.getUpperBounds());
        }
        for (Type type2 : ((ParameterizedType) type).getActualTypeArguments()) {
            if (containsTypeVariable(type2)) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsTypeVariable(Type[] typeArr) {
        if (typeArr == null) {
            return false;
        }
        for (Type type : typeArr) {
            if (containsTypeVariable(type)) {
                return true;
            }
        }
        return false;
    }

    public static <T> TypeReference<T> createSpecializedTypeReference(Class<T> cls) {
        return new SpecializedTypeReference(cls);
    }

    public static TypeReference<?> createSpecializedTypeReference(Type type) {
        return new SpecializedBaseTypeReference(type);
    }

    private static final Class<?> getArrayClass(Class<?> cls) {
        return Array.newInstance(cls, 0).getClass();
    }

    private static Type getComponentType(Type type) {
        Preconditions.checkNotNull(type, "type must not be null");
        if (type instanceof Class) {
            return ((Class) type).getComponentType();
        }
        if (type instanceof ParameterizedType) {
            return null;
        }
        if (type instanceof GenericArrayType) {
            return ((GenericArrayType) type).getGenericComponentType();
        }
        if (type instanceof WildcardType) {
            throw new UnsupportedOperationException("TODO: support wild card components");
        }
        if (type instanceof TypeVariable) {
            throw new AssertionError("Type variables are not allowed in type references");
        }
        throw new AssertionError("Unhandled branch to get component type for type " + type);
    }

    private static final Class<?> getRawType(Type type) {
        if (type == null) {
            throw new NullPointerException("type must not be null");
        }
        if (type instanceof Class) {
            return (Class) type;
        }
        if (type instanceof ParameterizedType) {
            return (Class) ((ParameterizedType) type).getRawType();
        }
        if (type instanceof GenericArrayType) {
            return getArrayClass(getRawType(((GenericArrayType) type).getGenericComponentType()));
        }
        if (type instanceof WildcardType) {
            return getRawType(((WildcardType) type).getUpperBounds());
        }
        if (type instanceof TypeVariable) {
            throw new AssertionError("Type variables are not allowed in type references");
        }
        throw new AssertionError("Unhandled branch to get raw type for type " + type);
    }

    private static final Class<?> getRawType(Type[] typeArr) {
        if (typeArr == null) {
            return null;
        }
        for (Type type : typeArr) {
            Class<?> rawType = getRawType(type);
            if (rawType != null) {
                return rawType;
            }
        }
        return null;
    }

    private static void toString(Type type, StringBuilder sb) {
        if (type == null) {
            return;
        }
        if (type instanceof TypeVariable) {
            sb.append(((TypeVariable) type).getName());
            return;
        }
        if (type instanceof Class) {
            Class cls = (Class) type;
            sb.append(cls.getName());
            toString(cls.getTypeParameters(), sb);
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            sb.append(((Class) parameterizedType.getRawType()).getName());
            toString(parameterizedType.getActualTypeArguments(), sb);
        } else if (!(type instanceof GenericArrayType)) {
            sb.append(type.toString());
        } else {
            toString(((GenericArrayType) type).getGenericComponentType(), sb);
            sb.append("[]");
        }
    }

    private static void toString(Type[] typeArr, StringBuilder sb) {
        if (typeArr == null || typeArr.length == 0) {
            return;
        }
        sb.append("<");
        for (int i = 0; i < typeArr.length; i++) {
            toString(typeArr[i], sb);
            if (i != typeArr.length - 1) {
                sb.append(", ");
            }
        }
        sb.append(">");
    }

    public boolean equals(Object obj) {
        return (obj instanceof TypeReference) && this.mType.equals(((TypeReference) obj).mType);
    }

    public TypeReference<?> getComponentType() {
        Type componentType = getComponentType(this.mType);
        if (componentType != null) {
            return createSpecializedTypeReference(componentType);
        }
        return null;
    }

    public final Class<? super T> getRawType() {
        return (Class<? super T>) getRawType(this.mType);
    }

    public Type getType() {
        return this.mType;
    }

    public int hashCode() {
        return this.mHash;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TypeReference<");
        toString(getType(), sb);
        sb.append(">");
        return sb.toString();
    }
}
