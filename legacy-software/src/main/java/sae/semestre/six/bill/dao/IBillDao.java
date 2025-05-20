package sae.semestre.six.bill.dao;

import sae.semestre.six.bill.entity.Bill;
import sae.semestre.six.utils.dao.GenericDao;

import java.util.Date;
import java.util.List;

public interface IBillDao extends GenericDao<Bill, Long> {
    Bill findByBillNumber(String billNumber);
    List<Bill> findByPatientId(Long patientId);
    List<Bill> findByDoctorId(Long doctorId);
    List<Bill> findByDateRange(Date startDate, Date endDate);
    List<Bill> findByStatus(String status);
} 