package co.edu.uptc.propayment.web.controller;


import co.edu.uptc.propayment.domain.service.CompanyService;
import co.edu.uptc.propayment.persistence.entities.Company;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/companies")
public class CompanyController {


    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/save")
    public Company save(@RequestBody Company company) {
        return companyService.save(company);
    }
}
