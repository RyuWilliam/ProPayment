package co.edu.uptc.propayment.domain.model;

import co.edu.uptc.propayment.persistence.enums.PaymentStatus;

public class PaymentResponse {
    private PaymentStatus status;
    private String reason;

    public PaymentResponse(PaymentStatus status, String reason) {
        this.status = status;
        this.reason = reason;
    }


    public PaymentStatus getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
}
