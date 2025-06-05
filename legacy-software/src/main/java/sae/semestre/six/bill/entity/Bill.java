package sae.semestre.six.bill.entity;

import jakarta.persistence.*;
import sae.semestre.six.bill.controller.BillingController;
import sae.semestre.six.doctor.entity.Doctor;
import sae.semestre.six.patient.entity.Patient;
import sae.semestre.six.patient.entity.PatientHistory;

import java.util.Date;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bills")
public class Bill {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "bill_number", unique = true)
    private String billNumber;
    
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;
    
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;
    
    @Column(name = "bill_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date billDate = new Date();
    
    @Column(name = "total_amount")
    private Double totalAmount = 0.0;
    
    @Column(name = "status")
    private String status = "PENDING";
    
    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<BillDetail> billDetails = new HashSet<>();
    
    
    @Column(name = "created_date")
    private Date createdDate = new Date();
    
    @Column(name = "last_modified")
    private Date lastModified = new Date();

    @ManyToOne
    private PatientHistory patientHistory;

    @Column(name = "hash", length = 128, updatable = false)
    private String hash;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getBillNumber() {
        return billNumber;
    }
    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }
    
    public Patient getPatient() {
        return patient;
    }
    public void setPatient(Patient patient) {
        this.patient = patient;
    }
    
    public Doctor getDoctor() {
        return doctor;
    }
    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }
    
    public Date getBillDate() {
        return billDate;
    }
    public void setBillDate(Date billDate) {
        this.billDate = billDate;
    }
    
    public Double getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) { 
        // Empêcher la modification si la facture est déjà émise ou archivée
        if (this.status != null && (this.status.equals("ISSUED") || this.status.equals("ARCHIVED"))) {
            throw new IllegalStateException("Impossible de modifier une facture émise ou archivée.");
        }
        this.status = status;
        this.lastModified = new java.util.Date(); 
    }
    
    public Set<BillDetail> getBillDetails() {
        return billDetails;
    }
    public void setBillDetails(Set<BillDetail> billDetails) {
        this.billDetails = billDetails;
    }

    public void addBillDetails(String[] treatments) {
        double total = 0.0;
        Set<BillDetail> details = new HashSet<>();

        for (String treatment : treatments) {
            double price = BillingController.getTreatmentPrice(treatment);

            total += price;

            BillDetail detail = new BillDetail();
            detail.setBill(this);
            detail.setTreatmentName(treatment);
            detail.setUnitPrice(price);
            details.add(detail);

        }

        if (total > 500) {
            total = total * 0.9;
        }

        setTotalAmount(total);
        setBillDetails(details);
    }

    public String getHash() { return hash; }
    public void setHash(String hash) { this.hash = hash; }

    /**
     * Calcule un hash cryptographique (SHA-256) pour la facture, en gardant le sel.
     * Le hash ne dépend que du contenu de la facture (pas de chaînage),
     * ce qui permet de détecter exactement quelle facture est altérée.
     */
    public String computeHash() {
        StringBuilder sb = new StringBuilder();
        sb.append(createdDate.toString())
          .append(lastModified.toString())
          .append(billDate.toString());

        sb.append(billNumber)
          .append(totalAmount)
          .append(patient != null ? patient.getId() : "")
          .append(doctor != null ? doctor.getId() : "");

        List<BillDetail> sortedDetails = new ArrayList<>(billDetails);
        sortedDetails.sort((a, b) -> a.getTreatmentName().compareTo(b.getTreatmentName()));
        for (BillDetail detail : sortedDetails) {
            sb.append(detail.getTreatmentName())
              .append(detail.getUnitPrice());
        }
        
        String secretSalt = System.getenv("BILL_HASH_SECRET");
        if (secretSalt == null) {
            secretSalt = "default-secret";
        }
        sb.append(secretSalt);

        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du calcul du hash", e);
        }
    }
}