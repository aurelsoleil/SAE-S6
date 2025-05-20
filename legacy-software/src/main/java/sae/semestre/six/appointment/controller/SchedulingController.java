package sae.semestre.six.appointment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sae.semestre.six.appointment.entity.Appointment;
import sae.semestre.six.appointment.service.IAppointmentService;
import sae.semestre.six.doctor.entity.Doctor;
import sae.semestre.six.doctor.service.IDoctorService;
import sae.semestre.six.utils.email.SMTPHelper;
import sae.semestre.six.patient.IPatientService;
import sae.semestre.six.patient.Patient;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@RestController
@RequestMapping("/scheduling")
public class SchedulingController {

    @Autowired
    private IAppointmentService appointmentService;

    @Autowired
    private IDoctorService doctorService;

    @Autowired
    private IPatientService patientService;

    private final SMTPHelper emailService = SMTPHelper.getInstance();
    
    
    @PostMapping("/appointment")
    public ResponseEntity<?> scheduleAppointment(
            @RequestParam Long doctorId,
            @RequestParam Long patientId,
            @RequestParam String appointmentDate) {

        if (doctorId == null) {
            return new ResponseEntity<>("Doctor not found", HttpStatus.NOT_FOUND);
        }
        if (patientId == null) {
            return new ResponseEntity<>("Patient not found", HttpStatus.NOT_FOUND);
        }


        Doctor doctor = doctorService.findDoctorById(doctorId);
        Patient patient = patientService.findById(patientId);
        if (doctor == null) {
            return new ResponseEntity<>("Doctor not found", HttpStatus.NOT_FOUND);
        }

        if (patient == null) {
            return new ResponseEntity<>("Patient not found", HttpStatus.NOT_FOUND);
        }

        LocalDateTime appointmentDateTime = LocalDateTime.parse(appointmentDate);

        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setAppointmentDate(Date.from(appointmentDateTime.atZone(ZoneId.systemDefault()).toInstant()));

        List<Appointment> doctorAppointments = appointmentService.findByDoctorId(doctorId);

        for (Appointment existing : doctorAppointments) {
            Date existingDate = existing.getAppointmentDate();
            ZoneId zoneId = ZoneId.systemDefault();
            LocalDateTime existingDateTime = existingDate.toInstant()
                    .atZone(zoneId)
                    .toLocalDateTime();

            if (existingDateTime.equals(appointmentDateTime)) {
                return new ResponseEntity<>("Doctor is not available at this time", HttpStatus.CONFLICT);
            }
        }

        int hour = appointmentDateTime.getHour();
        if (hour < 9 || hour > 17) {
            return new ResponseEntity<>("Appointment time is outside of working hours", HttpStatus.BAD_REQUEST);
        }

        emailService.sendEmail(
                doctor.getEmail(),
                "New Appointment Scheduled",
                "You have a new appointment on " + appointmentDateTime
        );

        return new ResponseEntity<>(appointment, HttpStatus.CREATED);
    }
    
    

} 