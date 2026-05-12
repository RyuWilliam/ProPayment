package co.edu.uptc.propayment.web.controller;


import co.edu.uptc.propayment.domain.service.CompanyService;
import co.edu.uptc.propayment.persistence.entities.Company;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping()
    public Company findByApyKey(@RequestHeader("API-Key") String apiKey){
        return companyService.findByApiKey(apiKey);
    }
}
