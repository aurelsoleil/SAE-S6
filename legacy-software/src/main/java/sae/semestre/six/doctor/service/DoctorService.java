package sae.semestre.six.doctor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sae.semestre.six.doctor.entity.Doctor;
import sae.semestre.six.doctor.dao.IDoctorDao;

@Service
public class DoctorService implements IDoctorService {

    @Autowired
    private IDoctorDao doctorDao;

    @Override
    public Doctor findDoctorById(Long id) {
        return doctorDao.findById(id);
    }
}
