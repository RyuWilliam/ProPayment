package co.edu.uptc.propayment.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class NuBankClient {

    private static final Logger log = LoggerFactory.getLogger(NuBankClient.class);
    private static final int TOKEN = 2000;

    @Value("${bank.nubank.url}")
    private String nuBankUrl;

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
            log.info("Validando tarjeta Nu en: {}", nuBankUrl);
            ResponseEntity<String> response = restTemplate.exchange(
                    nuBankUrl, HttpMethod.POST, entity, String.class);

            boolean isValid = "VALID".equalsIgnoreCase(
                    response.getBody() != null ? response.getBody().trim() : "");

            log.info("Respuesta Nu: {}", isValid ? "VÁLIDA" : "INVÁLIDA");
            return isValid;
        } catch (Exception e) {
            log.error("Error conectando a NuBank: {}", e.getMessage());
            return false;
        }
    }
}