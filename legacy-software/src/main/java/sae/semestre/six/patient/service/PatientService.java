package sae.semestre.six.patient.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sae.semestre.six.patient.entity.Patient;
import sae.semestre.six.patient.dao.IPatientDao;

@Service
public class PatientService implements IPatientService {

    @Autowired
    private IPatientDao patientDao;

    @Override
    public Patient findById(Long id) {
        return patientDao.findById(id);
    }
}
