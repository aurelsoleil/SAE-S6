package sae.semestre.six.appointment.dao;

import org.springframework.stereotype.Repository;
import sae.semestre.six.appointment.entity.Appointment;
import sae.semestre.six.utils.dao.AbstractHibernateDao;

import java.util.Date;
import java.util.List;

@Repository
public class AppointmentDao extends AbstractHibernateDao<Appointment, Long> implements IAppointmentDao {

    @Override
    @SuppressWarnings("unchecked")
    public List<Appointment> findByPatientId(Long patientId) {
        return getEntityManager()
                .createQuery("FROM Appointment WHERE patient.id = :patientId")
                .setParameter("patientId", patientId)
                .getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Appointment> findByDoctorId(Long doctorId) {
        return getEntityManager()
                .createQuery("FROM Appointment WHERE doctor.id = :doctorId")
                .setParameter("doctorId", doctorId)
                .getResultList();
    }

    @Override
    public List<Appointment> findByRoomId(Long roomId) {
        return getEntityManager()
                .createQuery("FROM Appointment WHERE room.id = :roomId")
                .setParameter("roomId", roomId)
                .getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Appointment> findByDateRange(Date startDate, Date endDate) {
        return getEntityManager()
                .createQuery("FROM Appointment WHERE appointmentDate BETWEEN :startDate AND :endDate")
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
    }

    @Override
    public List<Appointment> findByRoomNumberAndDoctorId(Long roomNumber, Long doctorId) {
        return getEntityManager()
                .createQuery("FROM Appointment WHERE room.roomNumber = :roomNumber AND doctor.id = :doctorId")
                .setParameter("roomNumber", roomNumber)
                .setParameter("doctorId", doctorId)
                .getResultList();
    }

}