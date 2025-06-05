package sae.semestre.six;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import sae.semestre.six.appointment.dao.IAppointmentDao;
import sae.semestre.six.appointment.entity.Appointment;
import sae.semestre.six.appointment.service.AppointmentService;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppointmentServiceTest {

    @InjectMocks
    private AppointmentService appointmentService;
    @Mock
    private IAppointmentDao appointmentDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAvailableSlots_ValidSlots() {
        // Arrange
        Long doctorId = 1L;
        Long roomId = 1L;
        Integer duration = 30; // 30 minutes

        Appointment appointment1 = new Appointment();
        appointment1.setAppointmentDate(new Date(System.currentTimeMillis() + 3600 * 1000)); // 1 hour from now

        Appointment appointment2 = new Appointment();
        appointment2.setAppointmentDate(new Date(System.currentTimeMillis() + 7200 * 1000)); // 2 hours from now

        when(appointmentDao.findByRoomNumberAndDoctorId(roomId, doctorId))
                .thenReturn(Arrays.asList(appointment1, appointment2));

        // Act
        List<Appointment> availableSlots = appointmentService.findAvailable(doctorId, roomId, duration);

        // Assert
        assertFalse(availableSlots.isEmpty());
        assertTrue(availableSlots.size() > 0);
    }

    @Test
    void testFindAvailableSlots_NoOverlap() {
        // Arrange
        Long doctorId = 1L;
        Long roomId = 1L;
        Integer duration = 30; // 30 minutes

        Appointment appointment1 = new Appointment();
        appointment1.setAppointmentDate(new Date(System.currentTimeMillis() + 3600 * 1000)); // 1 hour from now

        when(appointmentDao.findByRoomNumberAndDoctorId(roomId, doctorId))
                .thenReturn(Collections.singletonList(appointment1));

        // Act
        List<Appointment> availableSlots = appointmentService.findAvailable(doctorId, roomId, duration);

        // Assert
        assertFalse(availableSlots.isEmpty());
    }

    @Test
    void testFindAvailableSlots_NoAvailability() {
        // Arrange
        Long doctorId = 1L;
        Long roomId = 1L;
        Integer duration = 30; // 30 minutes

        // Create appointments with 15-minute gaps
        Date now = new Date();
        Appointment doctorAppointment1 = new Appointment();
        doctorAppointment1.setAppointmentDate(new Date(now.getTime() + 15 * 60 * 1000)); // 15 min from now

        Appointment doctorAppointment2 = new Appointment();
        doctorAppointment2.setAppointmentDate(new Date(now.getTime() + 45 * 60 * 1000)); // 45 min from now

        Appointment roomAppointment1 = new Appointment();
        roomAppointment1.setAppointmentDate(new Date(now.getTime() + 30 * 60 * 1000)); // 30 min from now

        // Mock the DAO calls
        when(appointmentDao.findByDoctorId(doctorId))
                .thenReturn(Arrays.asList(doctorAppointment1, doctorAppointment2));
        when(appointmentDao.findByRoomId(roomId))
                .thenReturn(Collections.singletonList(roomAppointment1));

        // Act & Assert
        assertDoesNotThrow(() -> appointmentService.findAvailable(doctorId, roomId, duration));

        // Verify the DAO methods were called
        verify(appointmentDao).findByDoctorId(doctorId);
        verify(appointmentDao).findByRoomId(roomId);
    }
}