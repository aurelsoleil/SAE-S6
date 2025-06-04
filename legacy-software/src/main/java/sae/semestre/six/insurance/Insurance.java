package sae.semestre.six.insurance;

import jakarta.persistence.*;
import sae.semestre.six.patient.entity.Patient;

import java.util.Date;

@Entity
@Table(name = "insurance")
public class Insurance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "policy_number", unique = true)
    private String policyNumber;
    
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;
    
    @Column(name = "provider")
    private String provider;
    
    @Column(name = "coverage_percentage")
    private Double coveragePercentage;
    
    @Column(name = "max_coverage")
    private Double maxCoverage;
    
    @Column(name = "expiry_date")
    @Temporal(TemporalType.DATE)
    private Date expiryDate;
    
    @Column(name = "start_date")
    @Temporal(TemporalType.DATE)
    private Date startDate;
    
    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    private Date endDate;
    
    
    public Double calculateCoverage(Double billAmount) {
        Double coverage = billAmount * (coveragePercentage / 100);
        return coverage > maxCoverage ? maxCoverage : coverage;
    }
    
    
    public boolean isValid() {
        return new Date().before(expiryDate);
    }
    
    public boolean isValidForDate(Date date) {
        return (date.after(this.startDate) || date.equals(this.startDate))
            && (date.before(this.endDate) || date.equals(this.endDate));
    }
}