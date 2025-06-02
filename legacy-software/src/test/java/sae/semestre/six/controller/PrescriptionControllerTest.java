package sae.semestre.six.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sae.semestre.six.patient.dao.IPatientDao;
import sae.semestre.six.prescription.controller.PrescriptionController;
import sae.semestre.six.prescription.dao.IPrescriptionDao;
import sae.semestre.six.bill.service.BillingService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PrescriptionControllerTest {

    @InjectMocks
    private PrescriptionController prescriptionController;

    @Mock
    private IPatientDao patientDao;

    @Mock
    private IPrescriptionDao prescriptionDao;

    @Mock
    private BillingService billingService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddAndRetrievePrescription() {
        String id = "001";
        String result = prescriptionController.addPrescription(
            id,
            new String[]{"PARACETAMOL"},
            "Test notes"
        );

        assertTrue(result.contains("created"));

        List<String> prescriptions = prescriptionController.getPatientPrescriptions(id);
        assertFalse(prescriptions.isEmpty());
        assertTrue(prescriptions.get(0).startsWith("RX"));
    }

    @Test
    public void testInventory() {
        prescriptionController.refillMedicine("PARACETAMOL", 10);
        assertEquals(9, (int) prescriptionController.getInventory().get("PARACETAMOL"));
//        assertEquals(10, (int) prescriptionController.getInventory().get("PARACETAMOL"));
    }

    @Test
    public void testClearData() {
        prescriptionController.clearAllData();
        assertTrue(prescriptionController.getInventory().isEmpty());
    }
}