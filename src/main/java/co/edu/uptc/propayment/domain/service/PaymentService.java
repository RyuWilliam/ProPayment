package co.edu.uptc.propayment.domain.service;


import co.edu.uptc.propayment.domain.model.*;
import co.edu.uptc.propayment.exceptions.CompanyNotFoundException;
import co.edu.uptc.propayment.exceptions.InvalidCardException;
import co.edu.uptc.propayment.exceptions.InvalidTransactionException;
import co.edu.uptc.propayment.persistence.entities.Company;
import co.edu.uptc.propayment.persistence.entities.Payment;
import co.edu.uptc.propayment.persistence.enums.CardType;
import co.edu.uptc.propayment.persistence.enums.PaymentStatus;
import co.edu.uptc.propayment.persistence.repository.PaymentJpaRepository;
import co.edu.uptc.propayment.web.ExternalBankClient;
import co.edu.uptc.propayment.web.NuBankClient;
import co.edu.uptc.propayment.web.dto.BankResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PaymentService {
    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final NuBankClient nuBankClient;
    private final ExternalBankClient externalBankClient;
    private final PaymentJpaRepository paymentRepository;
    private final CompanyService companyService;


    public PaymentService(NuBankClient nuBankClient, ExternalBankClient externalBankClient, PaymentJpaRepository paymentRepository, CompanyService companyService) {
        this.nuBankClient = nuBankClient;
        this.externalBankClient = externalBankClient;
        this.paymentRepository = paymentRepository;
        this.companyService = companyService;
    }

    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }

    public PaymentResponse processPayment(String apiKey, Transaction transaction) {

        log.info("=== Inicio de solicitud de pago ===");

        if (apiKey == null || apiKey.isBlank()) {
            log.error("API Key nula o vacía");
            throw new InvalidTransactionException("API-Key header");
        }

        if (transaction == null) {
            log.error("Transaction nula");
            throw new InvalidTransactionException("transaction body");
        }

        validateTransaction(transaction);
        Payment payment = initialBuild(transaction);
        log.info("Buscando empresa para API key...");
        Company company = companyService.findByApiKey(apiKey);

        if (company == null) {
            log.error("Empresa no encontrada para API el key");
            payment.setStatus(PaymentStatus.REJECTED);
            paymentRepository.save(payment);
            throw new CompanyNotFoundException(apiKey);
        }

        payment.setCompany(company);
        log.info("Empresa identificada: {}", company.getCompanyName());
        Payment persisted = paymentRepository.save(payment);
        log.info("Pago registrado con ID: {} — estado inicial: PENDING", persisted.getPaymentId());
        log.info("Envío de pago a autorización externa");

        PaymentResponse response = sendToBank(transaction.getCard());
        persisted.setStatus(response.getStatus());
        Payment updated = paymentRepository.save(persisted);

        log.info("Pago ID: {} — resultado final: {}", updated.getPaymentId(), updated.getStatus());
        log.info("=== Fin de solicitud de pago ===");

        return response;
    }

    public PaymentResponse sendToBank(Card card) {
        if (card.getCardType() == CardType.VISA) {
            return sendToVISABank(card.getCardNumber(), card.getCardHolderName());
        } else if (card.getCardType() == CardType.MASTERCARD) {
            return sendToMasterCardBank(card.getCardNumber(), card.getCardHolderName());
        } else if (card.getCardType() == CardType.NU) {
            return sendToNuBank(card.getCardNumber(), card.getCsv());
        } else {
            return new PaymentResponse(PaymentStatus.REJECTED, "Tipo de tarjeta no soportada");
        }
    }

    private PaymentResponse sendToMasterCardBank(String cardNumber, String cardHolderName) {
        try {
            BankResponse r = externalBankClient.callMastercard(cardNumber, cardHolderName);
            if (r == null) return new PaymentResponse(PaymentStatus.FAILED, "Sin respuesta de MasterCard");
            boolean approved = "APPROVED".equalsIgnoreCase(r.getStatus());
            return new PaymentResponse(
                    approved ? PaymentStatus.APPROVED : PaymentStatus.REJECTED,
                    r.getMessage());
        } catch (Exception e) {
            log.error("Serverless MasterCard no disponible: {}", e.getMessage());
            return new PaymentResponse(PaymentStatus.FAILED, "Servicio MasterCard no disponible");
        }
    }
    private PaymentResponse sendToNuBank(String cardNumber, String csv) {
        try {

            boolean approved = nuBankClient.validate(cardNumber, csv);
            String message = approved ? "Pago aprobado por Nu" : "Pago rechazado por Nu";
            return new PaymentResponse(approved ? PaymentStatus.APPROVED : PaymentStatus.REJECTED, message);
        }
        catch (Exception e) {
            log.error("Serverless NuBank no disponible: {}", e.getMessage());
            return new PaymentResponse(PaymentStatus.FAILED, "Servicio NuBank no disponible");
        }
    }

    private PaymentResponse sendToVISABank(String cardNumber, String cardHolderName) {
        try {
            BankResponse r = externalBankClient.callVisa(cardNumber, cardHolderName);
            if (r == null) return new PaymentResponse(PaymentStatus.FAILED, "Sin respuesta de VISA");
            boolean approved = "APPROVED".equalsIgnoreCase(r.getStatus());
            return new PaymentResponse(
                    approved ? PaymentStatus.APPROVED : PaymentStatus.REJECTED,
                    r.getMessage());
        } catch (Exception e) {
            log.error("Serverless VISA no disponible: {}", e.getMessage());
            return new PaymentResponse(PaymentStatus.FAILED, "Servicio VISA no disponible");
        }
    }

    private Payment initialBuild(Transaction transaction){
        Payment payment = new Payment();
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setUserEmail(transaction.getUserEmail());
        payment.setAmount(transaction.getAmount());
        payment.setTransferredToCompany(false);
        payment.setTransferDate(null);

        SimplifiedCard simplifiedCard = new SimplifiedCard(transaction.getCard());
        payment.setCardType(simplifiedCard.getCardType());
        payment.setCardLast4(simplifiedCard.getCardLast4());
        payment.setCardHolderName(simplifiedCard.getCardHolderName());
        return payment;
    }
    private void validateTransaction(Transaction transaction) {
        if (transaction.getUserEmail() == null || transaction.getUserEmail().isBlank()) {
            log.error("userEmail nulo o vacío");
            throw new InvalidTransactionException("userEmail");
        }
        if (!transaction.getUserEmail().contains("@")) {
            log.error("userEmail con formato inválido: {}", transaction.getUserEmail());
            throw new InvalidTransactionException("userEmail — formato inválido");
        }
        if (transaction.getAmount() == null) {
            log.error("amount nulo");
            throw new InvalidTransactionException("amount");
        }
        if (transaction.getAmount() <= 0) {
            log.error("amount inválido: {}", transaction.getAmount());
            throw new InvalidTransactionException("amount — debe ser mayor a 0");
        }
        if (transaction.getCard() == null) {
            log.error("card nula");
            throw new InvalidTransactionException("card");
        }

        validateCard(transaction.getCard());
    }




    private void validateCard(Card card) {
        if (card.getCardNumber() == null || card.getCardNumber().isBlank()) {
            log.error("cardNumber nulo");
            throw new InvalidCardException("cardNumber nulo o vacío");
        }
        if (card.getCardNumber().length() < 16) {
            log.error("cardNumber muy corto: {}", card.getCardNumber().length());
            throw new InvalidCardException("cardNumber debe tener al menos 16 dígitos");
        }
        if (!card.getCardNumber().matches("\\d+")) {
            log.error("cardNumber contiene caracteres no numéricos");
            throw new InvalidCardException("cardNumber solo debe contener dígitos");
        }
        if (card.getCardHolderName() == null || card.getCardHolderName().isBlank()) {
            log.error("cardHolderName nulo");
            throw new InvalidCardException("cardHolderName nulo o vacío");
        }
        if (card.getCardType() == null) {
            log.error("cardType nulo");
            throw new InvalidCardException("cardType nulo — debe ser VISA o MASTERCARD");
        }
        if (card.getCardType() == CardType.NU) {
            if (card.getCsv() == null || card.getCsv().isBlank()) {
                log.error("csv nulo para tarjeta Nu");
                throw new InvalidCardException("csv requerido para tarjetas Nu");
            }
        }
    }


    public boolean payToCompany(String apiKey) {
        Company company = companyService.findByApiKey(apiKey);
        if (company == null) throw new CompanyNotFoundException(apiKey);

        List<Payment> toTransfer = paymentRepository
                .findByCompanyAndStatusAndTransferredToCompanyFalse(
                        company, PaymentStatus.APPROVED
                );

        if (toTransfer.isEmpty()) return false;

        toTransfer.forEach(p -> {
            p.setTransferredToCompany(true);
            p.setTransferDate(LocalDateTime.now());
            paymentRepository.save(p);
        });

        log.info("Transferidos {} pagos a empresa '{}'", toTransfer.size(), company.getCompanyName());
        return true;
    }


    public Report generateApprovedTransferredReport(String apiKey) {
        Company company = companyService.findByApiKey(apiKey);
        if (company == null) throw new CompanyNotFoundException(apiKey);

        List<Payment> payments = paymentRepository
                .findByCompanyAndStatusAndTransferredToCompanyTrue(
                        company, PaymentStatus.APPROVED
                );

        log.info("Reporte 1 — empresa '{}': {} pagos aprobados y transferidos",
                company.getCompanyName(), payments.size());
        return new Report(company.getCompanyName(), convertToItems(payments));
    }
    public Report generateApprovedNotTransferredReport(String apiKey) {
        Company company = companyService.findByApiKey(apiKey);
        if (company == null) throw new CompanyNotFoundException(apiKey);

        List<Payment> payments = paymentRepository
                .findByCompanyAndStatusAndTransferredToCompanyFalse(
                        company, PaymentStatus.APPROVED
                );

        log.info("Reporte 2 — empresa '{}': {} pagos aprobados sin transferir",
                company.getCompanyName(), payments.size());
        return new Report(company.getCompanyName(), convertToItems(payments));
    }
    public Report generateNotApprovedNotTransferredReport(String apiKey) {
        Company company = companyService.findByApiKey(apiKey);
        if (company == null) throw new CompanyNotFoundException(apiKey);

        List<Payment> payments = paymentRepository
                .findByCompanyAndTransferredToCompanyFalseAndStatusNot(
                        company, PaymentStatus.APPROVED
                );

        log.info("Reporte 3 — empresa '{}': {} pagos no aprobados sin transferir",
                company.getCompanyName(), payments.size());
        return new Report(company.getCompanyName(), convertToItems(payments));
    }

    private List<ReportItem> convertToItems(List<Payment> payments) {
        return payments.stream()
                .map(payment -> new ReportItem(
                        payment.getAmount(),
                        payment.getCardType(),
                        payment.getPaymentId(),
                        payment.getCardLast4(),
                        payment.getPaymentDate(),
                        payment.getStatus()
                ))
                .toList();
    }


    private static class SimplifiedCard {
        private final String cardLast4;
        private String cardHolderName;
        private CardType cardType;

        public SimplifiedCard(Card card){
            this.cardLast4 = card.getCardNumber().substring(card.getCardNumber().length() - 4);
            this.cardHolderName = card.getCardHolderName();
            this.cardType = card.getCardType();
        }

        public String getCardLast4() {
            return cardLast4;
        }


        public String getCardHolderName() {
            return cardHolderName;
        }

        public void setCardHolderName(String cardHolderName) {
            this.cardHolderName = cardHolderName;
        }

        public CardType getCardType() {
            return cardType;
        }

    }



}
