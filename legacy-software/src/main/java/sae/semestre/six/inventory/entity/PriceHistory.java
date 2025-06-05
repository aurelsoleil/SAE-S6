package sae.semestre.six.inventory.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "price_history")
public class PriceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;
    
    @Column(name = "old_price")
    private Double oldPrice;
    
    @Column(name = "new_price")
    private Double newPrice;
    
    @Column(name = "change_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date changeDate = new Date();
    
    @Column(name = "supplier_name")
    private String supplierName;

    public Double getPriceIncrease() {
        return newPrice - oldPrice;
    }
    
    public Double getPercentageChange() {
        return (newPrice - oldPrice) / oldPrice * 100;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Double getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(Double oldPrice) {
        this.oldPrice = oldPrice;
    }

    public Double getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(Double newPrice) {
        this.newPrice = newPrice;
    }

    public Date getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public Long getInventoryId() {
        return inventory != null ? inventory.getId() : null;
    }
}