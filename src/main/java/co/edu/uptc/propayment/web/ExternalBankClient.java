package co.edu.uptc.propayment.web;

import co.edu.uptc.propayment.web.dto.BankResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ExternalBankClient {

    private final RestTemplate restTemplate;

    @Value("${bank.visa.url}")
    private String visaUrl;

    @Value("${bank.mastercard.url}")
    private String mastercardUrl;

    public ExternalBankClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public BankResponse callVisa(String cardNumber, String holderName) {
        return call(visaUrl, cardNumber, holderName);
    }

    public BankResponse callMastercard(String cardNumber, String holderName) {
        return call(mastercardUrl, cardNumber, holderName);
    }

    private BankResponse call(String url, String cardNumber, String holderName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = String.format(
                "{\"cardNumber\":\"%s\",\"cardHolderName\":\"%s\"}",
                cardNumber, holderName);

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<BankResponse> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, BankResponse.class);

        return response.getBody();
    }
}
