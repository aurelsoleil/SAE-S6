package sae.semestre.six.bill.dao;

import org.springframework.stereotype.Repository;
import sae.semestre.six.bill.entity.Bill;
import sae.semestre.six.utils.dao.AbstractHibernateDao;

import java.util.Date;
import java.util.List;

@Repository
public class BillDao extends AbstractHibernateDao<Bill, Long> implements IBillDao {
    
    @Override
    public Bill findByBillNumber(String billNumber) {
        return (Bill) getEntityManager()
                .createQuery("FROM Bill WHERE billNumber = :billNumber")
                .setParameter("billNumber", billNumber)
                .getSingleResult();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<Bill> findByPatientId(Long patientId) {
        return getEntityManager()
                .createQuery("FROM Bill WHERE patient.id = :patientId")
                .setParameter("patientId", patientId)
                .getResultList();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<Bill> findByDoctorId(Long doctorId) {
        return getEntityManager()
                .createQuery("FROM Bill WHERE doctor.id = :doctorId")
                .setParameter("doctorId", doctorId)
                .getResultList();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<Bill> findByDateRange(Date startDate, Date endDate) {
        return getEntityManager()
                .createQuery("FROM Bill WHERE billDate BETWEEN :startDate AND :endDate")
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<Bill> findByStatus(String status) {
        return getEntityManager()
                .createQuery("FROM Bill WHERE status = :status")
                .setParameter("status", status)
                .getResultList();
    }
} 