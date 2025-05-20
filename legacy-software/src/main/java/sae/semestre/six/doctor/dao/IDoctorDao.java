package sae.semestre.six.doctor.dao;

import sae.semestre.six.utils.dao.GenericDao;
import sae.semestre.six.doctor.entity.Doctor;

import java.util.List;

public interface IDoctorDao extends GenericDao<Doctor, Long> {
    Doctor findByDoctorNumber(String doctorNumber);
    List<Doctor> findBySpecialization(String specialization);
    List<Doctor> findByDepartment(String department);
} 