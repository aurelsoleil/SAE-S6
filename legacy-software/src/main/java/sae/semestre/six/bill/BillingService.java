package sae.semestre.six.bill;

import org.springframework.stereotype.Service;

@Service
public class BillingService {

    public static final double INSURANCE_RATE = 0.8;

    public void processBill(String patientId, String source, String[] items) {
        
    }

    public double calculateInsurance(double montant) {
        if (montant <= 0) {
            throw new IllegalArgumentException("Montant cannot be negative");
        }

        System.out.println("Calculating insurance for montant: " + montant);

        return montant * INSURANCE_RATE;
    }
} 