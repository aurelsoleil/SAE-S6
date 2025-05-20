package sae.semestre.six.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sae.semestre.six.bill.dao.IBillDao;
import sae.semestre.six.bill.controller.BillingController;
import sae.semestre.six.bill.service.BillingService;
import sae.semestre.six.doctor.dao.IDoctorDao;
import sae.semestre.six.patient.PatientDao;
import sae.semestre.six.doctor.entity.Doctor;

import java.io.File;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BillingControllerTest {

    @InjectMocks
    private BillingController billingController;

    @Mock
    private BillingService billingService;

    @Mock
    private File billingFile;

    @Mock
    private PatientDao patientDao;

    @Mock
    private IDoctorDao doctorDao;

    @Mock
    private IBillDao billDao;

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

        String result = "";
        try {
            result = billingController.processBill(
                "001",
                "001",
                new String[] {"CONSULTATION"}
            );
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        assertTrue(result.contains("successfully"));
        assertTrue(billingFile.length() > initialFileSize);
    }

    @Test
    public void testCalculateInsurance() {
        // Mock the behavior of BillingService
        when(billingService.calculateInsurance(1000.0)).thenReturn(800.0);

        // Call the method
        double result = billingController.calculateInsurance(1000.0);

        // Verify the result
        assertEquals(800.0, result, 0.01);

        // Verify that the mock was called
        verify(billingService, times(1)).calculateInsurance(1000.0);
    }

    @Test
    public void testUpdatePrice() {
        billingController.updatePrice("CONSULTATION", 75.0);
        assertEquals(75.0, billingController.getPrices().get("CONSULTATION"), 0.01);
    }
}