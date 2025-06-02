package sae.semestre.six.prescription.dao;

import sae.semestre.six.prescription.entity.Prescription;
import sae.semestre.six.utils.dao.GenericDao;

import java.util.List;

public interface IPrescriptionDao extends GenericDao<Prescription, Long> {
    List<Prescription> findByPatientId(Long patientId);
} 