package sae.semestre.six.patient;

import sae.semestre.six.dao.GenericDao;
import sae.semestre.six.patient.PatientHistory;
import java.util.List;
import java.util.Date;

public interface PatientHistoryDao extends GenericDao<PatientHistory, Long> {
    List<PatientHistory> findCompleteHistoryByPatientId(Long patientId);
    List<PatientHistory> searchByMultipleCriteria(String keyword, Date startDate, Date endDate);
} 