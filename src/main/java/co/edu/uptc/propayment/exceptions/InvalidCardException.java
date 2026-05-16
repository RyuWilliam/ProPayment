package co.edu.uptc.propayment.exceptions;

public class InvalidCardException extends RuntimeException {
    public InvalidCardException(String reason) {
        super("Invalid Card" + reason);
    }
}
