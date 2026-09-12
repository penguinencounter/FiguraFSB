package org.figuramc.fsb2.api.utils;

import java.util.function.Consumer;
import java.util.function.Function;

public final class Result<Ok, Err> {
    public static class UnwrapException extends RuntimeException {
        public final Object what;

        private UnwrapException(Object subject, String message) {
            super(message);
            this.what = subject;
        }

        private UnwrapException(String message, RuntimeException cause) {
            super(message, cause);
            this.what = null;
        }

        public static UnwrapException expectOk(Object what) {
            if (what instanceof RuntimeException) return new UnwrapException("got an Err variant", (RuntimeException) what);
            return new UnwrapException(what, "got an Err variant: " + what);
        }

        public static UnwrapException expectErr(Object what) {
            return new UnwrapException(what, "got an Ok variant when an Err was expected: " + what);
        }
    }

    private final boolean isOk;
    private final Ok ok;
    private final Err err;

    private Result(boolean isOk, Ok ok, Err err) {
        this.isOk = isOk;
        this.ok = ok;
        this.err = err;
    }

    public static <Ok, Err> Result<Ok, Err> ok(Ok value) {
        return new Result<>(true, value, null);
    }

    public static <Ok, Err> Result<Ok, Err> err(Err value) {
        return new Result<>(false, null, value);
    }

    /**
     * Asserting this is an Ok variant, adapt the Err generic parameter to a different type.
     */
    public <T> Result<Ok, T> adaptOk() {
        return ok(unwrap());
    }

    /**
     * Asserting this is an Err variant, adapt the Ok generic parameter to a different type.
     */
    public <T> Result<T, Err> adaptErr() {
        if (isOk) throw UnwrapException.expectErr(ok);
        return err(err);
    }

    public boolean isOk() {
        return isOk;
    }

    public boolean isErr() {
        return !isOk;
    }

    public Ok unwrap() {
        if (isOk) return ok;
        throw UnwrapException.expectOk(err);
    }

    public Ok expect(String reason) {
        if (isOk) return ok;
        throw new IllegalStateException(reason);
    }

    public Ok expect(Function<Err, String> reasonProvider) {
        if (isOk) return ok;
        throw new IllegalStateException(reasonProvider.apply(err));
    }

    public Result<Ok, Err> fallback(Ok alternative) {
        if (isOk) return this;
        return ok(alternative);
    }

    public Ok unwrapOr(Ok alternative) {
        return isOk ? ok : alternative;
    }

    public <T> Result<T, Err> map(Function<Ok, T> fn) {
        if (isOk) return ok(fn.apply(ok));
        return adaptErr();
    }

    public <T> Result<Ok, T> mapErr(Function<Err, T> fn) {
        if (isOk) return adaptOk();
        return err(fn.apply(err));
    }

    public <NewOk, NewErr> Result<NewOk, NewErr> mapOrElse(Function<Ok, NewOk> handleOk, Function<Err, NewErr> handleErr) {
        if (isOk) return ok(handleOk.apply(ok));
        return err(handleErr.apply(err));
    }

    public void then(Consumer<Ok> handleOk) {
        if (isOk) handleOk.accept(ok);
    }

    public void then(Consumer<Ok> handleOk, Consumer<Err> handleErr) {
        if (isOk) handleOk.accept(ok);
        else handleErr.accept(err);
    }
}
