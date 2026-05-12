package co.edu.uptc.propayment.web.controller;


import co.edu.uptc.propayment.domain.model.Transaction;
import co.edu.uptc.propayment.domain.service.PaymentService;
import co.edu.uptc.propayment.persistence.entities.Payment;
import co.edu.uptc.propayment.persistence.enums.PaymentStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }


    @PostMapping("/process")
        public boolean processPayment(@RequestHeader("API-Key") String apiKey,
                                      @RequestBody Transaction transaction)  {
            Payment payment = paymentService.build(apiKey, transaction);
            Payment saved = paymentService.save(payment);
            return saved.getStatus() == PaymentStatus.APPROVED;
        }
}
