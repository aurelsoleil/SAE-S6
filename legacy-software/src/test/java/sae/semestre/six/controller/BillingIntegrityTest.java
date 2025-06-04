package sae.semestre.six.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sae.semestre.six.bill.controller.BillingController;
import sae.semestre.six.bill.dao.IBillDao;
import sae.semestre.six.bill.entity.Bill;
import sae.semestre.six.doctor.dao.IDoctorDao;
import sae.semestre.six.doctor.entity.Doctor;
import sae.semestre.six.patient.dao.IPatientDao;
import sae.semestre.six.patient.entity.Patient;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BillingIntegrityTest {
    @InjectMocks
    private BillingController billingController;
    @Mock
    private IBillDao billDao;
    @Mock
    private IPatientDao patientDao;
    @Mock
    private IDoctorDao doctorDao;

    private Patient patient;
    private Doctor doctor;
    private List<Bill> bills;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        BillingController.getInstance().getPrices().put("CONSULTATION", 50.0);
        BillingController.getInstance().getPrices().put("XRAY", 150.0);
        BillingController.getInstance().getPrices().put("SURGERY", 1000.0);
        System.setProperty("BILL_HASH_SECRET", "test-secret");
        patient = new Patient();
        patient.setId(1L);
        patient.setFirstName("John");
        patient.setLastName("Doe");
        doctor = new Doctor();
        doctor.setId(2L);
        doctor.setFirstName("Alice");
        doctor.setLastName("Smith");
        bills = new ArrayList<>();
        when(patientDao.findById(anyLong())).thenReturn(patient);
        when(doctorDao.findById(anyLong())).thenReturn(doctor);
        // Simule la sauvegarde en base
        doAnswer(invocation -> {
            Bill b = invocation.getArgument(0);
            bills.add(b);
            return null;
        }).when(billDao).save(any(Bill.class));
        // Simule la récupération ordonnée
        when(billDao.findAllOrderByCreatedDateAsc()).thenAnswer(invocation -> new ArrayList<>(bills));
        // Simule la récupération de la dernière facture
        when(billDao.findLastBill()).thenAnswer(invocation -> bills.isEmpty() ? null : bills.get(bills.size() - 1));
    }

    @Test
    public void testProcessBillAndIntegrityChain() {
        // Création de 3 factures via processBill
        for (int i = 0; i < 3; i++) {
            billingController.processBill("1", "2", new String[]{"CONSULTATION", "XRAY"});
        }
        List<Map<String, Object>> report = billingController.getIntegrityReport();
        assertEquals(3, report.size());
        report.forEach(entry -> assertTrue((Boolean) entry.get("integrityOk")));
    }

    @Test
    public void testIntegrityDetectsTampering() {
        // Création de 3 factures via processBill
        for (int i = 0; i < 3; i++) {
            billingController.processBill("1", "2", new String[]{"CONSULTATION"});
        }
        // Altération frauduleuse de la 2e facture
        Bill middleBill = bills.get(1);
        middleBill.setTotalAmount(9999.0); // modifie le montant sans recalculer le hash
        List<Map<String, Object>> report = billingController.getIntegrityReport();
        assertTrue((Boolean) report.get(0).get("integrityOk"));
        assertFalse((Boolean) report.get(1).get("integrityOk"));
        assertFalse((Boolean) report.get(2).get("integrityOk"));
    }

    @Test
    public void testIntegrityWithConcurrentBills() {
        // Création de 5 factures via processBill
        for (int i = 0; i < 5; i++) {
            billingController.processBill("1", "2", new String[]{"CONSULTATION"});
        }
        List<Map<String, Object>> report = billingController.getIntegrityReport();
        assertEquals(5, report.size());
        report.forEach(entry -> assertTrue((Boolean) entry.get("integrityOk")));
    }
}
