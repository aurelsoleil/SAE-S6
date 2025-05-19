package sae.semestre.six.patient;


import org.springframework.stereotype.Service;

public interface IPatientService {

    Patient findById(Long id);

}
