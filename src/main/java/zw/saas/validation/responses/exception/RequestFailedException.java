package zw.saas.validation.responses.exception;

public class RequestFailedException extends RuntimeException {

    public RequestFailedException(String message) {
        super(message);
    }
}
