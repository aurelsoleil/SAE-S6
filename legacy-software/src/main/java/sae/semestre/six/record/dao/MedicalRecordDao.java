package sae.semestre.six.record.dao;

import sae.semestre.six.record.entity.MedicalRecord;
import sae.semestre.six.utils.dao.AbstractHibernateDao;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

@Repository
public class MedicalRecordDao extends AbstractHibernateDao<MedicalRecord, Long> implements IMedicalRecordDao {
    
    @Override
    public MedicalRecord findByRecordNumber(String recordNumber) {
        return (MedicalRecord) getEntityManager()
                .createQuery("FROM MedicalRecord WHERE recordNumber = :recordNumber")
                .setParameter("recordNumber", recordNumber)
                .getSingleResult();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<MedicalRecord> findByPatientId(Long patientId) {
        return getEntityManager()
                .createQuery("FROM MedicalRecord WHERE patient.id = :patientId")
                .setParameter("patientId", patientId)
                .getResultList();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<MedicalRecord> findByDoctorId(Long doctorId) {
        return getEntityManager()
                .createQuery("FROM MedicalRecord WHERE doctor.id = :doctorId")
                .setParameter("doctorId", doctorId)
                .getResultList();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<MedicalRecord> findByDateRange(Date startDate, Date endDate) {
        return getEntityManager()
                .createQuery("FROM MedicalRecord WHERE recordDate BETWEEN :startDate AND :endDate")
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<MedicalRecord> findByDiagnosis(String diagnosis) {
        return getEntityManager()
                .createQuery("FROM MedicalRecord WHERE diagnosis LIKE :diagnosis")
                .setParameter("diagnosis", "%" + diagnosis + "%")
                .getResultList();
    }
} 