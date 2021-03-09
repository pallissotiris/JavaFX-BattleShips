package application;

public class InvalidCountException extends Exception{
    public InvalidCountException(String errorMessage) {
        super(errorMessage);
    }
}
