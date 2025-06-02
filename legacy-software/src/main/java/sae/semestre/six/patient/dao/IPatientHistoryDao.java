package sae.semestre.six.patient.dao;

import sae.semestre.six.patient.entity.PatientHistory;
import sae.semestre.six.utils.dao.GenericDao;

import java.util.List;
import java.util.Date;

public interface IPatientHistoryDao extends GenericDao<PatientHistory, Long> {
    List<PatientHistory> findCompleteHistoryByPatientId(Long patientId);
    List<PatientHistory> searchByMultipleCriteria(String keyword, Date startDate, Date endDate);
} 