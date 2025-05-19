package sae.semestre.six.doctor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DoctorService implements IDoctorService {

    @Autowired
    private DoctorDao doctorDao;

    @Override
    public Doctor findDoctorById(Long id) {
        return doctorDao.findById(id);
    }
}
