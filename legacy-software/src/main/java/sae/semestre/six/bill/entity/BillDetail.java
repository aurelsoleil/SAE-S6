package sae.semestre.six.bill.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "bill_details")
public class BillDetail {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "bill_id", nullable = false)
    private Bill bill;
    
    @Column(name = "treatment_name")
    private String treatmentName;
    
    @Column(name = "quantity")
    private Integer quantity = 1;
    
    @Column(name = "unit_price")
    private Double unitPrice = 0.0;
    
    @Column(name = "line_total")
    private Double lineTotal = 0.0;
    
    // Montant pris en charge par l'assurance pour ce matériel
    private Double priseEnChargeAssurance = 0.0;

    // Montant restant à charge du patient pour ce matériel
    private Double resteACharge = 0.0;

    public void calculateLineTotal() {
        this.lineTotal = this.quantity * this.unitPrice;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Bill getBill() { return bill; }
    public void setBill(Bill bill) { this.bill = bill; }
    
    public String getTreatmentName() { return treatmentName; }
    public void setTreatmentName(String treatmentName) { this.treatmentName = treatmentName; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { 
        this.quantity = quantity;
        calculateLineTotal(); 
    }
    
    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { 
        this.unitPrice = unitPrice;
        calculateLineTotal(); 
    }
    
    public Double getLineTotal() { return lineTotal; }
    public void setLineTotal(Double lineTotal) { this.lineTotal = lineTotal; }

    public Double getPriseEnChargeAssurance() {
        return priseEnChargeAssurance;
    }

    public void setPriseEnChargeAssurance(Double priseEnChargeAssurance) {
        this.priseEnChargeAssurance = priseEnChargeAssurance;
    }

    public Double getResteACharge() {
        return resteACharge;
    }

    public void setResteACharge(Double resteACharge) {
        this.resteACharge = resteACharge;
    }
}