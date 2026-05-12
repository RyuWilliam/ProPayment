package co.edu.uptc.propayment.persistence.repository;

import co.edu.uptc.propayment.persistence.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyJpaRepository extends JpaRepository<Company, Integer> {

    Company findByApiKey(String apiKey);

}
