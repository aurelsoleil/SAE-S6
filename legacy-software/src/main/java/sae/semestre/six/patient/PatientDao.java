package sae.semestre.six.patient;

import sae.semestre.six.dao.GenericDao;
import sae.semestre.six.patient.Patient;
import java.util.List;

public interface PatientDao extends GenericDao<Patient, Long> {
    Patient findByPatientNumber(String patientNumber);
    List<Patient> findByLastName(String lastName);
} 