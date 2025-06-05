package sae.semestre.six.bill.controller;

import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import sae.semestre.six.bill.service.BillPdfGenerator;
import sae.semestre.six.bill.BillingFile;
import sae.semestre.six.bill.service.BillingService;
import sae.semestre.six.bill.dao.IBillDao;
import sae.semestre.six.bill.entity.Bill;
import sae.semestre.six.doctor.dao.IDoctorDao;
import sae.semestre.six.patient.dao.IPatientDao;
import sae.semestre.six.doctor.entity.Doctor;
import sae.semestre.six.patient.entity.Patient;
import sae.semestre.six.utils.email.SMTPHelper;

import java.util.*;

@RestController
@RequestMapping("/billing")
public class BillingController {
    
    private static volatile BillingController instance;
    private Map<String, Double> priceList = new HashMap<>();
    private List<String> pendingBills = new ArrayList<>();
    
    @Autowired
    private IBillDao billDao;
    
    @Autowired
    private IPatientDao patientDao;
    
    @Autowired
    private IDoctorDao doctorDao;

    @Autowired
    private BillingService billingService;

    private final SMTPHelper emailService = SMTPHelper.getInstance();
    
    public BillingController() {
        priceList.put("CONSULTATION", 50.0);
        priceList.put("XRAY", 150.0);
        priceList.put("SURGERY", 1000.0);
    }
    
    public static BillingController getInstance() {
        if (instance == null) {
            synchronized (BillingController.class) {
                if (instance == null) {
                    instance = new BillingController();
                }
            }
        }
        return instance;
    }

    public static Double getTreatmentPrice(String treatment) {
        return getInstance().priceList.get(treatment);
    }
    
    @PostMapping("/process")
    public String processBill(
            @RequestParam String patientId,
            @RequestParam String doctorId,
            @RequestParam String[] treatments) {
        try {
            Patient patient = patientDao.findById(Long.parseLong(patientId));
            Doctor doctor = doctorDao.findById(Long.parseLong(doctorId));

            Bill bill = new Bill();
            bill.setBillNumber("BILL" + System.currentTimeMillis());
            bill.setPatient(patient);
            bill.setDoctor(doctor);
            bill.addBillDetails(treatments);

            // Calcul du hash incluant l'historique
            bill.setHash(bill.computeHash());
            bill.setStatus("ISSUED");

            BillingFile.write(bill.getBillNumber() + ": $" + bill.getTotalAmount() + "\n");
            billDao.save(bill);

            // Envoi de l'email de confirmation
            emailService.sendEmail(
                "admin@hospital.com",
                "New Bill Generated",
                "Bill Number: " + bill.getBillNumber() + "\nTotal: $" + bill.getTotalAmount()
            );

            // Envoi de la facture par email au patient
            try {
                byte[] pdf = BillPdfGenerator.generatePdf(bill);
                String body = String.format(
                    "Dear %s %s,\n\nYour bill of %s is available in attachment.\nTotal amount : %.2f $\nBill number : %s\n\nThanks for choosing us.",
                    bill.getPatient().getFirstName(),
                    bill.getPatient().getLastName(),
                    bill.getBillDate(),
                    bill.getTotalAmount(),
                    bill.getBillNumber()
                );
                emailService.sendEmailWithAttachment(
                    patient.getEmail(),
                    "Your medical bill",
                    body,
                    pdf,
                    "Bill-" + bill.getBillNumber() + ".pdf"
                );
            } catch (Exception e) {
                System.err.println("Erreur lors de la génération ou l'envoi du PDF : " + e.getMessage());
            }

            return "Bill processed successfully";
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Vérifie l'intégrité de toutes les factures.
     */
    @GetMapping("/integrity-report")
    public List<Map<String, Object>> getIntegrityReport() {
        List<Bill> bills = billDao.findAllOrderByCreatedDateAsc();
        List<Map<String, Object>> report = new ArrayList<>();

        for (Bill bill : bills) {
            String expectedHash = bill.computeHash();
            boolean integrityOk = expectedHash.equals(bill.getHash());

            Map<String, Object> entry = new HashMap<>();
            entry.put("billNumber", bill.getBillNumber());
            entry.put("date", bill.getBillDate());
            entry.put("status", bill.getStatus());
            entry.put("integrityOk", integrityOk);
            report.add(entry);
        }
        return report;
    }
    
    @PutMapping("/price")
    public String updatePrice(
            @RequestParam String treatment,
            @RequestParam double price) {
        priceList.put(treatment, price);
        recalculateAllPendingBills();
        return "Price updated";
    }
    
    private void recalculateAllPendingBills() {
        for (String billId : pendingBills) {
            processBill(billId, "RECALC", new String[]{"CONSULTATION"});
        }
    }
    
    @GetMapping("/prices")
    public Map<String, Double> getPrices() {
        return priceList;
    }
    
    @GetMapping("/insurance")
    public double calculateInsurance(@RequestParam double amount) {
        return billingService.calculateInsurance(amount);
    }
    
    @GetMapping("/revenue")
    public String getTotalRevenue() {
        return "Total Revenue: $" + billDao.calculateTotalRevenue();
    }
    
    @GetMapping("/pending")
    public List<String> getPendingBills() {
        return pendingBills;
    }
}