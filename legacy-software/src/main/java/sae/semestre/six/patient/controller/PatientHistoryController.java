package sae.semestre.six.patient.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sae.semestre.six.patient.dao.IPatientHistoryDao;
import sae.semestre.six.patient.entity.Patient;
import sae.semestre.six.patient.entity.PatientHistory;

import java.util.*;

@RestController
@RequestMapping("/patient-history")
public class PatientHistoryController {
    
    @Autowired
    private IPatientHistoryDao patientHistoryDao;
    
    
    @GetMapping("/search")
    public List<PatientHistory> searchHistory(
            @RequestParam String keyword,
            @RequestParam Date startDate,
            @RequestParam Date endDate) {
        
        
        List<PatientHistory> results = patientHistoryDao.searchByMultipleCriteria(
            keyword, startDate, endDate);
            
        return results;
    }
    
    
    @GetMapping("/patient/{patientId}/summary")
    public Map<String, Object> getPatientSummary(@PathVariable Long patientId) {
        List<PatientHistory> histories = patientHistoryDao.findCompleteHistoryByPatientId(patientId);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("visitCount", histories.size());
        
        
        double totalBilled = histories.stream()
            .mapToDouble(PatientHistory::getTotalBilledAmount)
            .sum();
            
        summary.put("totalBilled", totalBilled);

        Patient patient = histories.isEmpty() ? null : histories.get(0).getPatient();
        if (patient != null) {
            summary.put("insurances", patient.getInsurances());
        }
        return summary;
    }
}