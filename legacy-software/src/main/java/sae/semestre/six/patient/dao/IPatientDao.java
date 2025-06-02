package sae.semestre.six.patient.dao;

import sae.semestre.six.patient.entity.Patient;
import sae.semestre.six.utils.dao.GenericDao;

import java.util.List;

public interface IPatientDao extends GenericDao<Patient, Long> {
    Patient findByPatientNumber(String patientNumber);
    List<Patient> findByLastName(String lastName);
} 