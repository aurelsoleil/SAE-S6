package sae.semestre.six.controller;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import sae.semestre.six.bill.dao.IBillDao;
import sae.semestre.six.bill.entity.Bill;
import sae.semestre.six.bill.controller.BillingController;
import sae.semestre.six.patient.dao.IPatientDao;
import sae.semestre.six.doctor.dao.IDoctorDao;
import sae.semestre.six.patient.entity.Patient;
import sae.semestre.six.doctor.entity.Doctor;

import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BillingIntegrityTest {

    @Autowired
    private IBillDao billDao;
    @Autowired
    private IPatientDao patientDao;
    @Autowired
    private IDoctorDao doctorDao;
    @Autowired
    private BillingController billingController;

    private Patient patient;
    private Doctor doctor;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patientDao.save(patient);

        doctor = new Doctor();
        doctor.setFirstName("Alice");
        doctor.setLastName("Smith");
        doctorDao.save(doctor);
    }

    @Test
    public void testBillCreationAndIntegrityChain() {
        // Création de 3 factures en chaîne
        String[] treatments = {"CONSULTATION", "XRAY"};
        for (int i = 0; i < 3; i++) {
            billingController.processBill(patient.getId().toString(), doctor.getId().toString(), treatments);
        }

        // Vérification du rapport d'intégrité
        List<Map<String, Object>> report = billingController.getIntegrityReport();
        assertEquals(3, report.size());
        
        // Toutes les factures doivent être valides
        report.forEach(entry -> assertTrue((Boolean) entry.get("integrityOk")));
    }

    @Test
    public void testMiddleBillTamperingInvalidatesSubsequentBills() {
        // Création de 3 factures
        String[] treatments = {"CONSULTATION"};
        for (int i = 0; i < 3; i++) {
            billingController.processBill(patient.getId().toString(), doctor.getId().toString(), treatments);
        }

        // Modification frauduleuse de la deuxième facture
        List<Bill> bills = billDao.findAllOrderByCreatedDateAsc();
        Bill middleBill = bills.get(1);
        middleBill.setTotalAmount(9999.0);
        billDao.save(middleBill);

        // Vérification du rapport
        List<Map<String, Object>> report = billingController.getIntegrityReport();
        
        // La première facture doit être valide
        assertTrue((Boolean) report.get(0).get("integrityOk"));
        // La deuxième facture (modifiée) doit être invalide
        assertFalse((Boolean) report.get(1).get("integrityOk"));
        // La troisième facture doit aussi être invalide car son hash cumulatif est basé sur la deuxième
        assertFalse((Boolean) report.get(2).get("integrityOk"));
    }

    @Test
    public void testConcurrentBillCreation() throws InterruptedException {
        int threadCount = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        String[] treatments = {"CONSULTATION"};
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    billingController.processBill(
                        patient.getId().toString(),
                        doctor.getId().toString(),
                        treatments
                    );
                } finally {
                    latch.countDown();
                }
            });
        }

        // Attendre que tous les threads finissent
        latch.await(10, TimeUnit.SECONDS);
        executorService.shutdown();

        // Vérification
        List<Map<String, Object>> report = billingController.getIntegrityReport();
        assertEquals(threadCount, report.size());
        // Toutes les factures doivent être valides malgré la concurrence
        report.forEach(entry -> assertTrue((Boolean) entry.get("integrityOk")));
    }
}
