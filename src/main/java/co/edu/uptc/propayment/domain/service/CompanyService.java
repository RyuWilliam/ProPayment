package co.edu.uptc.propayment.domain.service;


import co.edu.uptc.propayment.persistence.entities.Company;
import co.edu.uptc.propayment.persistence.repository.CompanyJpaRepository;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {

    private final CompanyJpaRepository companyRepository;

    public CompanyService(CompanyJpaRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company save(Company company) {
        return companyRepository.save(company);
    }
}
