package survivalblock.train_across_time.common.remap;

public class EndClassVisitException extends RuntimeException {
    public EndClassVisitException() {
    }

    public EndClassVisitException(String message) {
        super(message);
    }

    public EndClassVisitException(String message, Throwable cause) {
        super(message, cause);
    }

    public EndClassVisitException(Throwable cause) {
        super(cause);
    }

    public EndClassVisitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
