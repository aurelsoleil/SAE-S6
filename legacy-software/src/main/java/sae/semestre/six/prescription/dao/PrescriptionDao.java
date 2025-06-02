package sae.semestre.six.prescription.dao;

import sae.semestre.six.prescription.entity.Prescription;
import sae.semestre.six.utils.dao.AbstractHibernateDao;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class PrescriptionDao extends AbstractHibernateDao<Prescription, Long> implements IPrescriptionDao {
    
    @Override
    @SuppressWarnings("unchecked")
    public List<Prescription> findByPatientId(Long patientId) {
        return getEntityManager()
                .createQuery("FROM Prescription WHERE patient.id = :patientId")
                .setParameter("patientId", patientId)
                .getResultList();
    }
} 