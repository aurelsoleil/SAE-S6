package sae.semestre.six.doctor;

import sae.semestre.six.dao.GenericDao;
import sae.semestre.six.doctor.Doctor;
import java.util.List;

public interface DoctorDao extends GenericDao<Doctor, Long> {
    Doctor findByDoctorNumber(String doctorNumber);
    List<Doctor> findBySpecialization(String specialization);
    List<Doctor> findByDepartment(String department);
} 