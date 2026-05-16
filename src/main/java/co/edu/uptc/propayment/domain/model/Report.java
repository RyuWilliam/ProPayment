package co.edu.uptc.propayment.domain.model;

import co.edu.uptc.propayment.persistence.enums.CardType;

import java.time.LocalDateTime;
import java.util.List;

public class Report {

    private String companyName;
    private List<ReportItem> items;
    private Double totalAmount;

    public Report(String companyName, List<ReportItem> items) {
        this.companyName = companyName;
        this.items = items;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public List<ReportItem> getItems() {
        return items;
    }

    public void setItems(List<ReportItem> items) {
        this.items = items;
    }

    public Double getTotalAmount() {
        Double total = 0.0;
        for (ReportItem item : items) {
            total += item.getAmount();
        }
        return total;
    }

}
