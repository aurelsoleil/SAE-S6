package sae.semestre.six.appointment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sae.semestre.six.appointment.dao.IAppointmentDao;
import sae.semestre.six.appointment.entity.Appointment;

import java.util.Date;
import java.util.List;

@Service
public class AppointmentService implements IAppointmentService {

    @Autowired
    private IAppointmentDao appointmentDao;

    public Appointment findById(Long id) {
        return appointmentDao.findById(id);
    }

    public List<Appointment> findByPatientId(Long patientId) {
        return appointmentDao.findByPatientId(patientId);
    }

    public List<Appointment> findByDoctorId(Long doctorId) {
        return appointmentDao.findByDoctorId(doctorId);
    }

    public List<Appointment> findByDateRange(Date startDate, Date endDate) {
        return appointmentDao.findByDateRange(startDate, endDate);
    }
}
