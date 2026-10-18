package com.android.internal.util;

import android.os.RemoteException;
import android.util.ExceptionUtils;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes3.dex */
public class FunctionalUtils {

    @FunctionalInterface
    public interface RemoteExceptionIgnoringConsumer<T> extends Consumer<T> {
        @Override // java.util.function.Consumer
        default void accept(T t) {
            try {
                acceptOrThrow(t);
            } catch (RemoteException unused) {
            }
        }

        void acceptOrThrow(T t) throws RemoteException;
    }

    @FunctionalInterface
    public interface ThrowingConsumer<T> extends Consumer<T> {
        @Override // java.util.function.Consumer
        default void accept(T t) {
            try {
                acceptOrThrow(t);
            } catch (Exception e) {
                throw ExceptionUtils.propagate(e);
            }
        }

        void acceptOrThrow(T t) throws Exception;
    }

    @FunctionalInterface
    public interface ThrowingRunnable extends Runnable {
        @Override // java.lang.Runnable
        default void run() {
            try {
                runOrThrow();
            } catch (Exception e) {
                throw ExceptionUtils.propagate(e);
            }
        }

        void runOrThrow() throws Exception;
    }

    @FunctionalInterface
    public interface ThrowingSupplier<T> {
        T getOrThrow() throws Exception;
    }

    private FunctionalUtils() {
    }

    public static Runnable handleExceptions(final ThrowingRunnable throwingRunnable, final Consumer<Throwable> consumer) {
        return new Runnable() { // from class: com.android.internal.util.-$$Lambda$FunctionalUtils$koCSI8D7Nu5vOJTVTEj0m3leo_U
            @Override // java.lang.Runnable
            public final void run() {
                FunctionalUtils.lambda$handleExceptions$0(throwingRunnable, consumer);
            }
        };
    }

    public static <T> Consumer<T> ignoreRemoteException(RemoteExceptionIgnoringConsumer<T> remoteExceptionIgnoringConsumer) {
        return remoteExceptionIgnoringConsumer;
    }

    static /* synthetic */ void lambda$handleExceptions$0(ThrowingRunnable throwingRunnable, Consumer consumer) {
        try {
            throwingRunnable.run();
        } catch (Throwable th) {
            consumer.accept(th);
        }
    }

    public static <T> Consumer<T> uncheckExceptions(ThrowingConsumer<T> throwingConsumer) {
        return throwingConsumer;
    }
}
