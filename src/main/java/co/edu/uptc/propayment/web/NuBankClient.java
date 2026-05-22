package co.edu.uptc.propayment.web;

import org.springframework.stereotype.Component;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

@Component
public class NuBankClient {

    private static final String NU_URL = "https://nu-service.onrender.com/validate";
    private static final int TOKEN = 2000;
    private final RestTemplate restTemplate;

    public NuBankClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean validate(String cardNumber, String csv) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = String.format("{\"number\":\"%s\",\"csv\":\"%s\",\"token\":%d}",
                cardNumber, csv, TOKEN);

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    NU_URL, HttpMethod.POST, entity, String.class);
            return "VALID".equalsIgnoreCase(
                    response.getBody() != null ? response.getBody().trim() : "");
        } catch (Exception e) {
            return false;
        }
    }
}
