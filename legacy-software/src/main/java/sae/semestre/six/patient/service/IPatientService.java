package sae.semestre.six.patient.service;


import sae.semestre.six.patient.entity.Patient;

public interface IPatientService {

    Patient findById(Long id);

}
