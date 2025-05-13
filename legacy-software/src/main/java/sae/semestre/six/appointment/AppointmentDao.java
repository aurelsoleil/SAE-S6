package sae.semestre.six.appointment;

import sae.semestre.six.appointment.Appointment;
import sae.semestre.six.dao.GenericDao;

import java.util.Date;
import java.util.List;

public interface AppointmentDao extends GenericDao<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByDoctorId(Long doctorId);
    List<Appointment> findByDateRange(Date startDate, Date endDate);
} 