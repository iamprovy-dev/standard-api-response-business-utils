package zw.saas.validation.responses.exception;


public class DoesNotExistException extends RuntimeException{
    public DoesNotExistException(String message){
        super(message);
    }
}
