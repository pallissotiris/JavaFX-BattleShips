package application;

public class CellAlreadyHitException extends  Exception{
    public CellAlreadyHitException(String errorMessage) {
        super(errorMessage);
    }
}
