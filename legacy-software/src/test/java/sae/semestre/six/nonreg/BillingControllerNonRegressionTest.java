package sae.semestre.six.nonreg;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sae.semestre.six.bill.BillDao;
import sae.semestre.six.bill.BillingController;
import sae.semestre.six.bill.BillingType;
import sae.semestre.six.doctor.DoctorDao;
import sae.semestre.six.patient.PatientDao;
import sae.semestre.six.doctor.Doctor;
import sae.semestre.six.patient.Patient;

import java.util.HashSet;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BillingControllerNonRegressionTest {

    @InjectMocks
    private BillingController billingController;

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
    public void testProcessBillNonRegression() {
        // Mocking dependencies
        Patient mockPatient = new Patient();
        mockPatient.setId(1L);
        mockPatient.setFirstName("John");
        mockPatient.setLastName("Doe");

        Doctor mockDoctor = new Doctor();
        mockDoctor.setId(1L);
        mockDoctor.setLastName("Smith");
        mockDoctor.setAppointments(new HashSet<>());

        when(patientDao.findById(1L)).thenReturn(mockPatient);
        when(doctorDao.findById(1L)).thenReturn(mockDoctor);

        // Execute the method
        String result = billingController.processBill(
            "1",
            "1",
            new String[] {"CONSULTATION"}
        );

        // Verify the result
        assertTrue(result.contains("successfully"));
        verify(billDao, times(1)).save(any());
    }

    @Test
    public void testCalculateInsuranceNonRegression() {
        // Execute the method
        double result = billingController.calculateInsurance(1000.0);

        // Verify the result
        assertEquals(800.0, result, 0.01);
    }

    @Test
    public void testUpdatePriceNonRegression() {
        // Update the price
        billingController.updatePrice("CONSULTATION", 75.0);

        // Verify the updated price
        Map<String, Double> prices = billingController.getPrices();
        assertEquals(75.0, prices.get("CONSULTATION"), 0.01);
    }

    @Test
    public void testGetTotalRevenueNonRegression() {
        // Verify the initial total revenue
        String revenue = billingController.getTotalRevenue();
        assertTrue(revenue.contains("Total Revenue: $"));
    }

    @Test
    public void testGetPendingBillsNonRegression() {
        // Verify the pending bills list is initially empty
        assertTrue(billingController.getPendingBills().isEmpty());
    }
}