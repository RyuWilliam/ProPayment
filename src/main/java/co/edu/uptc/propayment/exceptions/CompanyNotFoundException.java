package co.edu.uptc.propayment.exceptions;

public class CompanyNotFoundException extends RuntimeException {
    public CompanyNotFoundException(String apiKey) {
        super( "the company with apiKey "+ apiKey + " was not found");
    }
}
