package sae.semestre.six.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sae.semestre.six.bill.BillDao;
import sae.semestre.six.bill.BillingController;
import sae.semestre.six.doctor.DoctorDao;
import sae.semestre.six.patient.PatientDao;
import sae.semestre.six.doctor.Doctor;

import java.io.File;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BillingControllerTest {

    @InjectMocks
    private BillingController billingController;

    @Mock
    private File billingFile;

    @Mock
    private PatientDao patientDao;

    @Mock
    private DoctorDao doctorDao;

    @Mock
    private BillDao billDao;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testProcessBill() {
        long initialFileSize = billingFile.length();
        when(billingFile.length()).thenReturn(100L);

        Doctor mockDoctor = new Doctor();
        mockDoctor.setId(1L);
        mockDoctor.setLastName("Dr. Smith");
        mockDoctor.setAppointments(new HashSet<>());

        when(doctorDao.findById(anyLong())).thenReturn(mockDoctor);

        String result = billingController.processBill(
            "001",
            "001",
            new String[]{"CONSULTATION"}
        );

        assertTrue(result.contains("successfully"));
        assertTrue(billingFile.length() > initialFileSize);
    }

    @Test
    public void testCalculateInsurance() {
        double result = Double.parseDouble(
            billingController.calculateInsurance(1000.0)
                .replace("Insurance coverage: $", "")
        );

        assertEquals(700.0, result, 0.01);
    }

    @Test
    public void testUpdatePrice() {
        billingController.updatePrice("CONSULTATION", 75.0);
        assertEquals(75.0, billingController.getPrices().get("CONSULTATION"), 0.01);
    }
}