package sae.semestre.six.insurance.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
public class InsuranceHistory {
    @Id
    @GeneratedValue
    private Long id;

    private Long insuranceId;
    private String action; // "CREATED", "UPDATED"
    private Date date;
    @Lob
    private String details; // JSON ou description des changements

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getInsuranceId() { return insuranceId; }
    public void setInsuranceId(Long insuranceId) { this.insuranceId = insuranceId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}