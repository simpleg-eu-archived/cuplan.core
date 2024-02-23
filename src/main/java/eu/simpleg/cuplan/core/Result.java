package eu.simpleg.cuplan.core;

/**
 * Rust result type. Useful to use with operations which can fail.
 *
 * @param <TOk>    Success value.
 * @param <TError> Error value.
 */
public class Result<TOk, TError> {
    private final TOk ok;
    private final TError error;
    private final boolean success;

    private Result(TOk ok, TError error, boolean success) {
        this.ok = ok;
        this.error = error;
        this.success = success;
    }

    public static <TOk, TError> Result<TOk, TError> ok(TOk ok) {
        return new Result<>(ok, null, true);
    }

    public static <TOk, TError> Result<TOk, TError> err(TError error) {
        return new Result<>(null, error, false);
    }

    public boolean isOk() {
        return success;
    }

    public TOk unwrap() {
        if (!success) {
            throw new IllegalStateException(String.format("Tried to unwrap an error value: %s", error));
        }

        return ok;
    }

    public TError unwrapErr() {
        if (success) {
            throw new IllegalStateException(String.format("Tried to unwrapErr an ok value: %s", ok));
        }

        return error;
    }

    @Override
    public int hashCode() {
        if (success) {
            return ok.hashCode();
        } else {
            return error.hashCode();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Result<?, ?>)) {
            return false;
        }

        if (isOk() != ((Result<?, ?>) obj).isOk()) {
            return false;
        }

        if (isOk()) {
            return ok.equals(((Result<?, ?>) obj).ok);
        }

        return error.equals(((Result<?, ?>) obj).error);
    }

    @Override
    public String toString() {
        if (isOk()) {
            return ok.toString();
        }

        return error.toString();
    }
}
