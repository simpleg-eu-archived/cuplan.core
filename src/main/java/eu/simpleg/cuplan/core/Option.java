package eu.simpleg.cuplan.core;

public class Option<T> {
    private final T value;
    private final boolean hasValue;

    private Option(T value, boolean hasValue) {
        this.value = value;
        this.hasValue = hasValue;
    }

    public static <T> Option<T> none() {
        return new Option<>(null, false);
    }

    public static <T> Option<T> some(T value) {
        return new Option<>(value, true);
    }

    public boolean isNone() {
        return !hasValue;
    }

    public boolean isSome() {
        return hasValue;
    }

    public T unwrap() {
        if (isNone()) {
            throw new IllegalStateException("Tried to unwrap a 'None' value.");
        }

        return value;
    }

    @Override
    public int hashCode() {
        if (isNone()) {
            return 0;
        }

        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Option<?>)) {
            return false;
        }

        return value.equals(((Option<?>) obj).value);
    }

    @Override
    public String toString() {
        if (isNone()) {
            return "None";
        }

        return value.toString();
    }
}
