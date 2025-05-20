package sae.semestre.six.record.dao;

import sae.semestre.six.record.entity.MedicalRecord;
import sae.semestre.six.utils.dao.GenericDao;

import java.util.Date;
import java.util.List;

public interface IMedicalRecordDao extends GenericDao<MedicalRecord, Long> {
    MedicalRecord findByRecordNumber(String recordNumber);
    List<MedicalRecord> findByPatientId(Long patientId);
    List<MedicalRecord> findByDoctorId(Long doctorId);
    List<MedicalRecord> findByDateRange(Date startDate, Date endDate);
    List<MedicalRecord> findByDiagnosis(String diagnosis);
} 