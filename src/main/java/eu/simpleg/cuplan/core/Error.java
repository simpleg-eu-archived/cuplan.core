package eu.simpleg.cuplan.core;

public class Error {
    private final String errorKind;
    private final String message;

    public Error(String errorKind, String message) {
        this.errorKind = errorKind;
        this.message = message;
    }

    public String getErrorKind() {
        return errorKind;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public int hashCode() {
        return errorKind.hashCode() + message.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Error)) {
            return false;
        }

        return errorKind.equals(((Error) obj).errorKind) && message.equals(((Error) obj).message);
    }

    @Override
    public String toString() {
        return String.format("%s: %s", errorKind, message);
    }
}
