package zw.saas.validation.responses.exception;


public class NotAllowedException extends RuntimeException{
    public NotAllowedException(String message){
        super(message);
    }
}
