package co.edu.uptc.propayment.web.controller;


import co.edu.uptc.propayment.domain.model.Report;
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
        public PaymentStatus processPayment(@RequestHeader("API-Key") String apiKey,
                                      @RequestBody Transaction transaction)  {
            Payment payment = paymentService.build(apiKey, transaction);
            return payment.getStatus();
        }

    @GetMapping("/approved-transferred")
    public Report approvedTransferred(@RequestHeader("API-Key") String apiKey) {
        return paymentService.generateApprovedTransferredReport(apiKey);
    }
    @GetMapping("/approved-pending")
    public Report approvedNotTransferred(@RequestHeader("API-Key") String apiKey) {
        return paymentService.generateApprovedNotTransferredReport(apiKey);
    }

    @GetMapping("/not-approved")
    public Report notApprovedNotTransferred(@RequestHeader("API-Key") String apiKey) {
        return paymentService.generateNotApprovedNotTransferredReport(apiKey);
    }

    @PostMapping("/checkout")
    public boolean transferPayments(@RequestHeader("API-Key") String apiKey){
        return paymentService.payToCompany(apiKey);
    }



}
