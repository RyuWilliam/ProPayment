package co.edu.uptc.propayment.domain.service;


import co.edu.uptc.propayment.domain.model.Card;
import co.edu.uptc.propayment.domain.model.Transaction;
import co.edu.uptc.propayment.persistence.entities.Company;
import co.edu.uptc.propayment.persistence.entities.Payment;
import co.edu.uptc.propayment.persistence.enums.CardType;
import co.edu.uptc.propayment.persistence.enums.PaymentStatus;
import co.edu.uptc.propayment.persistence.repository.PaymentJpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentJpaRepository paymentRepository;
    private final CompanyService companyService;


    public PaymentService(PaymentJpaRepository paymentRepository, CompanyService companyService) {
        this.paymentRepository = paymentRepository;
        this.companyService = companyService;
    }

    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }

    public Payment build(String apiKey, Transaction transaction) {

        Company company = companyService.findByApiKey(apiKey);

        boolean authorized = mockAuthorize(transaction.getUserEmail());

        Payment payment = new Payment();
        payment.setCompany(company);
        payment.setUserEmail(transaction.getUserEmail());
        payment.setAmount(transaction.getAmount());
        SimplifiedCard simplifiedCard = new SimplifiedCard(transaction.getCard());
        payment.setCardType(simplifiedCard.getCardType());
        payment.setCardLast4(simplifiedCard.getCardLast4());
        payment.setCardHolderName(simplifiedCard.getCardHolderName());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus(authorized ? PaymentStatus.APPROVED : PaymentStatus.REJECTED);
        return payment;
    }

    private boolean mockAuthorize(String userEmail) {
        List<String> approvedEmails = List.of(
                "test@approved.com",
                "demo@visa.com",
                "pagador@ok.com"
        );
        return approvedEmails.contains(userEmail);
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
