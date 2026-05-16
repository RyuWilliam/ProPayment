package co.edu.uptc.propayment.persistence.repository;

import co.edu.uptc.propayment.persistence.entities.Company;
import co.edu.uptc.propayment.persistence.entities.Payment;
import co.edu.uptc.propayment.persistence.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentJpaRepository extends JpaRepository<Payment, Integer> {

    // APPROVED y ya transferidos
    List<Payment> findByCompanyAndStatusAndTransferredToCompanyTrue(
            Company company, PaymentStatus status
    );

    // APPROVED y NO transferidos aún
    List<Payment> findByCompanyAndStatusAndTransferredToCompanyFalse(
            Company company, PaymentStatus status
    );

    // NO transferidos y que NO sean APPROVED (PENDING o REJECTED)
    List<Payment> findByCompanyAndTransferredToCompanyFalseAndStatusNot(
            Company company, PaymentStatus status
    );
}

