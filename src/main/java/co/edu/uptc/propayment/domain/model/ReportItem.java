package co.edu.uptc.propayment.domain.model;

import co.edu.uptc.propayment.persistence.enums.CardType;
import co.edu.uptc.propayment.persistence.enums.PaymentStatus;

import java.time.LocalDateTime;

public class ReportItem {
    private Double amount;
    private Integer paymentId;
    private CardType cardType;
    private PaymentStatus status;
    private String last4;
    private LocalDateTime paymentDate;

    public ReportItem(Double amount, CardType cardType, Integer paymentId , String last4, LocalDateTime paymentDate, PaymentStatus status) {
        this.amount = amount;
        this.cardType = cardType;
        this.last4 = last4;
        this.paymentDate = paymentDate;
        this.paymentId = paymentId;
        this.status = status;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public String getLast4() {
        return last4;
    }

    public void setLast4(String last4) {
        this.last4 = last4;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
}
