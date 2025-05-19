package sae.semestre.six.patient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientService implements IPatientService {

    @Autowired
    private PatientDao patientDao;

    @Override
    public Patient findById(Long id) {
        return patientDao.findById(id);
    }
}
