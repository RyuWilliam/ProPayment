package co.edu.uptc.propayment.persistence.repository;

import co.edu.uptc.propayment.persistence.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<Payment, Integer> {

}
