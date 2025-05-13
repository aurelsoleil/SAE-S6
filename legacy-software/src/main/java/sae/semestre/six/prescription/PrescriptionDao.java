package sae.semestre.six.prescription;

import sae.semestre.six.dao.GenericDao;

import java.util.List;

public interface PrescriptionDao extends GenericDao<Prescription, Long> {
    List<Prescription> findByPatientId(Long patientId);
} 