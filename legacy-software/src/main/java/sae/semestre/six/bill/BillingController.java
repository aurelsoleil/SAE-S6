package sae.semestre.six.bill;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import sae.semestre.six.doctor.DoctorDao;
import sae.semestre.six.patient.PatientDao;
import sae.semestre.six.doctor.Doctor;
import sae.semestre.six.patient.Patient;
import sae.semestre.six.email.EmailService;

import java.io.FileWriter;
import java.util.*;

import org.hibernate.Hibernate;

@RestController
@RequestMapping("/billing")
public class BillingController {
    
    private static volatile BillingController instance;
    private Map<BillingType, Double> priceList = new HashMap<>();
    private double totalRevenue = 0.0;
    private List<String> pendingBills = new ArrayList<>();
    
    @Autowired
    private BillDao billDao;
    
    @Autowired
    private PatientDao patientDao;
    
    @Autowired
    private DoctorDao doctorDao;

    @Autowired
    private BillingService billingService;
    
    private final EmailService emailService = EmailService.getInstance();
    
    private BillingController() {
        priceList.put(BillingType.CONSULTATION, 50.0);
        priceList.put(BillingType.XRAY, 150.0);
        priceList.put(BillingType.SURGERY, 1000.0);
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
    
    @PostMapping("/process")
    public String processBill(
            @RequestParam String patientId,
            @RequestParam String doctorId,
            @RequestParam BillingType[] treatments) {
        try {
            Patient patient = patientDao.findById(Long.parseLong(patientId));
            Doctor doctor = doctorDao.findById(Long.parseLong(doctorId));
            
            Hibernate.initialize(doctor.getAppointments());
            
            Bill bill = new Bill();
            bill.setBillNumber("BILL" + System.currentTimeMillis());
            bill.setPatient(patient);
            bill.setDoctor(doctor);
            
            Hibernate.initialize(bill.getBillDetails());
            
            double total = 0.0;
            Set<BillDetail> details = new HashSet<>();
            
            for (BillingType treatment : treatments) {
                double price = priceList.get(treatment);
                total += price;
                
                BillDetail detail = new BillDetail();
                detail.setBill(bill);
                detail.setTreatmentName(treatment);
                detail.setUnitPrice(price);
                details.add(detail);
                
                Hibernate.initialize(detail);
            }
            
            if (total > 500) {
                total = total * 0.9;
            }
            
            bill.setTotalAmount(total);
            bill.setBillDetails(details);
            
            BillingFile.write(bill.getBillNumber() + ": $" + total + "\n");

            totalRevenue += total;
            billDao.save(bill);
            
            emailService.sendEmail(
                "admin@hospital.com",
                "New Bill Generated",
                "Bill Number: " + bill.getBillNumber() + "\nTotal: $" + total
            );
            
            return "Bill processed successfully";
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    @PutMapping("/price")
    public String updatePrice(
            @RequestParam BillingType treatment,
            @RequestParam double price) {
        priceList.put(treatment, price);
        recalculateAllPendingBills();
        return "Price updated";
    }
    
    private void recalculateAllPendingBills() {
        for (String billId : pendingBills) {
            processBill(billId, "RECALC", new BillingType[]{BillingType.CONSULTATION});
        }
    }
    
    @GetMapping("/prices")
    public Map<BillingType, Double> getPrices() {
        return priceList;
    }
    
    @GetMapping("/insurance")
    public double calculateInsurance(@RequestParam double amount) {
        double result = billingService.calculateInsurance(amount);

        return result;
    }
    
    @GetMapping("/revenue")
    public String getTotalRevenue() {
        return "Total Revenue: $" + totalRevenue;
    }
    
    @GetMapping("/pending")
    public List<String> getPendingBills() {
        return pendingBills;
    }
} 