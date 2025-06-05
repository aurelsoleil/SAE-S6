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
    void testFindAvailable_WithGapBetweenAppointments() {
        // Arrange
        Long doctorId = 1L;
        Long roomId = 1L;
        Integer duration = 30;
        Date now = new Date();

        // Doctor has appointment at current time
        Appointment doctorAppointment = new Appointment();
        doctorAppointment.setAppointmentDate(now);

        // Room has appointment 1 hour later
        Appointment roomAppointment = new Appointment();
        roomAppointment.setAppointmentDate(new Date(now.getTime() + 60 * 60 * 1000));

        when(appointmentDao.findByDoctorId(doctorId))
                .thenReturn(Collections.singletonList(doctorAppointment));
        when(appointmentDao.findByRoomId(roomId))
                .thenReturn(Collections.singletonList(roomAppointment));

        // Act
        List<Appointment> availableSlots = appointmentService.findAvailable(doctorId, roomId, duration);

        // Assert
        assertFalse(availableSlots.isEmpty());
        verify(appointmentDao).findByDoctorId(doctorId);
        verify(appointmentDao).findByRoomId(roomId);
    }

    @Test
    void testFindAvailable_InvalidDuration() {
        // Arrange
        Long doctorId = 1L;
        Long roomId = 1L;
        Integer duration = -30;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                appointmentService.findAvailable(doctorId, roomId, duration));
        assertEquals("Duration must be greater than 0", exception.getMessage());

        // Verify no DAO calls were made
        verify(appointmentDao, never()).findByDoctorId(anyLong());
        verify(appointmentDao, never()).findByRoomId(anyLong());
    }

    @Test
    void testFindAvailable_CompletelyOverlappingAppointments() {
        // Arrange
        Long doctorId = 1L;
        Long roomId = 1L;
        Integer duration = 30;
        Date now = new Date();

        // Both doctor and room have appointments at the same time
        Appointment doctorAppointment = new Appointment();
        doctorAppointment.setAppointmentDate(now);

        Appointment roomAppointment = new Appointment();
        roomAppointment.setAppointmentDate(now);

        when(appointmentDao.findByDoctorId(doctorId))
                .thenReturn(Collections.singletonList(doctorAppointment));
        when(appointmentDao.findByRoomId(roomId))
                .thenReturn(Collections.singletonList(roomAppointment));

        // Act
        List<Appointment> availableSlots = appointmentService.findAvailable(doctorId, roomId, duration);

        // Assert
        assertFalse(availableSlots.isEmpty());
        assertTrue(availableSlots.get(0).getAppointmentDate().after(now));
        verify(appointmentDao).findByDoctorId(doctorId);
        verify(appointmentDao).findByRoomId(roomId);
    }
}