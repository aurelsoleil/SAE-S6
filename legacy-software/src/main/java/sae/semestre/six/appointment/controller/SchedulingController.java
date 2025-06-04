package sae.semestre.six.appointment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sae.semestre.six.appointment.entity.Appointment;
import sae.semestre.six.appointment.service.IAppointmentService;
import sae.semestre.six.doctor.service.IDoctorService;
import sae.semestre.six.utils.email.SMTPHelper;

import java.util.*;

@RestController
@RequestMapping("/scheduling")
public class SchedulingController {

    @Autowired
    private IAppointmentService appointmentService;

    @Autowired
    private IDoctorService doctorService;

    private final SMTPHelper emailService = SMTPHelper.getInstance();

    @PostMapping("/appointment")
    public ResponseEntity<?> scheduleAppointment(
            @RequestParam Long doctorId,
            @RequestParam Long patientId,
            @RequestParam String appointmentDate,
            @RequestParam(required = false) Long insuranceId
    ) {

        if (doctorId == null) {
            return new ResponseEntity<>("Doctor not found", HttpStatus.NOT_FOUND);
        }
        if (patientId == null) {
            return new ResponseEntity<>("Patient not found", HttpStatus.NOT_FOUND);
        }
        if (appointmentDate == null || appointmentDate.isEmpty()) {
            return new ResponseEntity<>("Appointment date is required", HttpStatus.BAD_REQUEST);
        }


        Appointment appointment = appointmentService.createAppointment(doctorId, patientId, appointmentDate, insuranceId);

        return new ResponseEntity<>(appointment, HttpStatus.CREATED);
    }

    @PostMapping("/available")
    public ResponseEntity<?> availableAppointments(
            @RequestParam Long doctorId,
            @RequestParam Long roomId,
            @RequestParam Integer duration) {
        if (doctorId == null) {
            return new ResponseEntity<>("Doctor not found", HttpStatus.NOT_FOUND);
        }
        if (roomId == null) {
            return new ResponseEntity<>("Room not found", HttpStatus.NOT_FOUND);
        }
        if (duration == null || duration <= 0) {
            return new ResponseEntity<>("Duration is required", HttpStatus.BAD_REQUEST);
        }

        // check when both doctor and room are available
        List<Appointment> availableAppointments = appointmentService.findAvailable(doctorId, roomId, duration);

        return new ResponseEntity<>(availableAppointments, HttpStatus.OK);

    }
}