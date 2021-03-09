package application;

public class GameNotStartedException extends Exception{
    public GameNotStartedException(String errorMessage) {
        super(errorMessage);
    }
}
