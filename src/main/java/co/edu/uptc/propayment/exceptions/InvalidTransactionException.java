package co.edu.uptc.propayment.exceptions;

public class InvalidTransactionException extends RuntimeException {
    public InvalidTransactionException(String field) {
        super("required field is blank or null: " + field);
    }
}