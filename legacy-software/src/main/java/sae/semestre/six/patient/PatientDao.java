package sae.semestre.six.patient;

import sae.semestre.six.utils.dao.GenericDao;

import java.util.List;

public interface PatientDao extends GenericDao<Patient, Long> {
    Patient findByPatientNumber(String patientNumber);
    List<Patient> findByLastName(String lastName);
} 