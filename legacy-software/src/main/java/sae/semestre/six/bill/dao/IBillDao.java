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
    double calculateTotalRevenue();
    /**
     * Retourne toutes les factures triées par date de création croissante (pour chaînage).
     */
    List<Bill> findAllOrderByCreatedDateAsc();
    /**
     * Retourne la dernière facture créée (par date de création décroissante).
     */
    Bill findLastBill();
    /**
     * Retourne toutes les factures pour une assurance donnée, triées par date de création.
     */
    List<Bill> findByInsuranceIdOrderByCreatedDateAsc(Long insuranceId);
}