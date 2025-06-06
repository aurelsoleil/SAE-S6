package sae.semestre.six.appointment.dao;

import sae.semestre.six.appointment.entity.Appointment;
import sae.semestre.six.utils.dao.GenericDao;
import org.springframework.data.jpa.repository.Query;
import sae.semestre.six.utils.dao.GenericDao;

import java.util.Date;
import java.util.List;

public interface IAppointmentDao extends GenericDao<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByDoctorId(Long doctorId);
    List<Appointment> findByRoomId(Long roomId);
    List<Appointment> findByDateRange(Date startDate, Date endDate);

    List<Appointment> findByRoomNumberAndDoctorId(Long roomNumber, Long doctorId);
} 