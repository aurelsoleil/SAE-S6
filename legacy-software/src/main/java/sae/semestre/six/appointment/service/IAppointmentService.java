package sae.semestre.six.appointment.service;

import sae.semestre.six.appointment.entity.Appointment;

import java.util.Date;
import java.util.List;

public interface IAppointmentService {

    Appointment findById(Long id);

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByDoctorId(Long doctorId);

    List<Appointment> findByDateRange(Date startDate, Date endDate);

    Appointment createAppointment(Long doctorId, Long patientId, String appointmentDate);

    List<Appointment> findAvailable(Long doctorId, Long roomId, Integer duration);
}
