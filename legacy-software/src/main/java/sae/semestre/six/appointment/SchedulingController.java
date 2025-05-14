package sae.semestre.six.appointment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sae.semestre.six.doctor.DoctorDao;
import sae.semestre.six.doctor.Doctor;
import sae.semestre.six.email.EmailService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@RestController
@RequestMapping("/scheduling")
public class SchedulingController {
    
    @Autowired
    private AppointmentDao appointmentDao;
    
    @Autowired
    private DoctorDao doctorDao;
    
    private final EmailService emailService = EmailService.getInstance();
    
    
    @PostMapping("/appointment")
    public String scheduleAppointment(
            @RequestParam Long doctorId,
            @RequestParam Long patientId,
            @RequestParam String appointmentDate) {
        try {
            Doctor doctor = doctorDao.findById(doctorId);

            List<Appointment> doctorAppointments = appointmentDao.findByDoctorId(doctorId);
            LocalDateTime appointmentDateTime = LocalDateTime.parse(appointmentDate);

            for (Appointment existing : doctorAppointments) {
                Date existingDate = existing.getAppointmentDate();
                ZoneId zoneId = ZoneId.systemDefault();
                LocalDateTime existingDateTime = existingDate.toInstant()
                        .atZone(zoneId)
                        .toLocalDateTime();

                if (existingDateTime.equals(appointmentDateTime)) {
                    return "Doctor is not available at this time";
                }
            }

            int hour = appointmentDateTime.getHour();
            if (hour < 9 || hour > 17) {
                return "Appointments only available between 9 AM and 5 PM";
            }

            emailService.sendEmail(
                doctor.getEmail(),
                "New Appointment Scheduled",
                "You have a new appointment on " + appointmentDateTime
            );

            return "Appointment scheduled successfully";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    

} 