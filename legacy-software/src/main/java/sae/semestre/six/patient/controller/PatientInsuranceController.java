package sae.semestre.six.patient.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sae.semestre.six.patient.entity.Patient;
import sae.semestre.six.insurance.Insurance;
import sae.semestre.six.insurance.entity.InsuranceHistory;
import sae.semestre.six.patient.service.IPatientService;
import sae.semestre.six.insurance.service.IInsuranceService;
import sae.semestre.six.insurance.repository.InsuranceHistoryRepository;

import java.util.Date;
import java.util.Set;

@RestController
@RequestMapping("/patients/{patientId}/insurances")
public class PatientInsuranceController {

    @Autowired
    private IPatientService patientService;
    @Autowired
    private IInsuranceService insuranceService;
    @Autowired
    private InsuranceHistoryRepository insuranceHistoryRepository;

    @GetMapping
    public Set<Insurance> list(@PathVariable Long patientId) {
        Patient patient = patientService.findById(patientId);
        return patient.getInsurances();
    }

    @PostMapping
    public Insurance add(@PathVariable Long patientId, @RequestBody Insurance insurance) {
        Patient patient = patientService.findById(patientId);
        insurance.setPatient(patient);
        insuranceService.save(insurance);
        patient.getInsurances().add(insurance);
        patientService.save(patient);

        InsuranceHistory history = new InsuranceHistory();
        history.setInsuranceId(insurance.getId());
        history.setAction("CREATED");
        history.setDate(new Date());
        history.setDetails("Ajout de l'assurance : " + insurance.getProvider());
        insuranceHistoryRepository.save(history);

        return insurance;
    }

    @PutMapping("/{insuranceId}")
    public Insurance update(@PathVariable Long patientId, @PathVariable Long insuranceId, @RequestBody Insurance updated) {
        Insurance insurance = insuranceService.findById(insuranceId);
        insurance.setProvider(updated.getProvider());
        insurance.setPolicyNumber(updated.getPolicyNumber());
        insurance.setCoveragePercentage(updated.getCoveragePercentage());
        insurance.setMaxCoverage(updated.getMaxCoverage());
        insurance.setStartDate(updated.getStartDate());
        insurance.setEndDate(updated.getEndDate());
        insuranceService.save(insurance);

        InsuranceHistory history = new InsuranceHistory();
        history.setInsuranceId(insurance.getId());
        history.setAction("UPDATED");
        history.setDate(new Date());
        history.setDetails("Mise à jour de l'assurance : " + insurance.getProvider());
        insuranceHistoryRepository.save(history);

        return insurance;
    }
}