package sae.semestre.six.bill.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
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
    
    private BillingController() {
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

            BillingFile.write(bill.getBillNumber() + ": $" + bill.getTotalAmount() + "\n");
            billDao.save(bill);

//            emailService.sendEmail(
//                "admin@hospital.com",
//                "New Bill Generated",
//                "Bill Number: " + bill.getBillNumber() + "\nTotal: $" + total
//            );

            return "Bill processed successfully";
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
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