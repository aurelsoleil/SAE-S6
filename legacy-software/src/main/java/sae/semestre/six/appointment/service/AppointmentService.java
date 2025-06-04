package sae.semestre.six.appointment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sae.semestre.six.appointment.dao.IAppointmentDao;
import sae.semestre.six.appointment.entity.Appointment;
import sae.semestre.six.doctor.entity.Doctor;
import sae.semestre.six.doctor.service.IDoctorService;
import sae.semestre.six.patient.entity.Patient;
import sae.semestre.six.patient.service.IPatientService;
import sae.semestre.six.room.service.IRoomService;
import sae.semestre.six.utils.email.SMTPHelper;
import sae.semestre.six.insurance.entity.Insurance;
import sae.semestre.six.insurance.service.IInsuranceService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Service
public class AppointmentService implements IAppointmentService {

    @Autowired
    private IAppointmentDao appointmentDao;

    @Autowired
    private IDoctorService doctorService;

    @Autowired
    private IPatientService patientService;

    @Autowired
    private IRoomService roomService;

    @Autowired
    private IInsuranceService insuranceService;

    private final SMTPHelper smtpHelper = SMTPHelper.getInstance();

    public Appointment findById(Long id) {
        return appointmentDao.findById(id);
    }

    public List<Appointment> findByPatientId(Long patientId) {
        return appointmentDao.findByPatientId(patientId);
    }

    public List<Appointment> findByDoctorId(Long doctorId) {
        return appointmentDao.findByDoctorId(doctorId);
    }

    public List<Appointment> findByDateRange(Date startDate, Date endDate) {
        return appointmentDao.findByDateRange(startDate, endDate);
    }

    public Appointment createAppointment(Long doctorId, Long patientId, String appointmentDate, Long insuranceId) {
        Doctor doctor = doctorService.findDoctorById(doctorId);
        Patient patient = patientService.findById(patientId);
        if (doctor == null) throw new IllegalArgumentException("Doctor not found");
        if (patient == null) throw new IllegalArgumentException("Patient not found");

        LocalDateTime appointmentDateTime = LocalDateTime.parse(appointmentDate);
        Date date = Date.from(appointmentDateTime.atZone(ZoneId.systemDefault()).toInstant());

        Appointment appointment = new Appointment(doctor, patient, date);

        // Vérification et association de l'assurance
        if (insuranceId != null) {
            Insurance insurance = insuranceService.findById(insuranceId);
            if (insurance == null) throw new IllegalArgumentException("Insurance not found");
            if (!insurance.isValidForDate(date)) throw new IllegalArgumentException("Insurance is not valid for the appointment date");
            appointment.setInsurance(insurance);
        }

        doctor.checkAppointmentAvailability(appointment);

        smtpHelper.sendEmail(
                doctor.getEmail(),
                "New Appointment Scheduled",
                "You have a new appointment on " + appointmentDateTime
        );
        
        appointmentDao.save(appointment);
        return appointment;
    }

    /**
     * return a list of appointments when both doctor and room are available at the same time
     * @param doctorId
     * @param roomId
     * @return
     */
    public List<Appointment> findAvailable(Long doctorId, Long roomId, Integer duration) {
        if (duration == null || duration <= 0) {
            throw new IllegalArgumentException("Duration must be greater than 0");
        }

        // Récupérer les rendez-vous pour le docteur et la salle
        List<Appointment> roomAndDoctorAppointments = appointmentDao.findByRoomNumberAndDoctorId(roomId, doctorId);

        List<Appointment> availableAppointments = new ArrayList<>();
        Date now = new Date();

        // Trier les rendez-vous par date
        roomAndDoctorAppointments.sort(Comparator.comparing(Appointment::getAppointmentDate));

        // Vérifier les créneaux disponibles avant le premier rendez-vous
        if (!roomAndDoctorAppointments.isEmpty()) {
            Appointment firstAppointment = roomAndDoctorAppointments.get(0);
            Date startTime = now;
            Date endTime = firstAppointment.getAppointmentDate();

            if (isDurationAvailable(startTime, endTime, duration)) {
                availableAppointments.add(createAvailableSlot(startTime));
            }
        }

        // Vérifier les créneaux entre les rendez-vous
        for (int i = 0; i < roomAndDoctorAppointments.size() - 1; i++) {
            Appointment current = roomAndDoctorAppointments.get(i);
            Appointment next = roomAndDoctorAppointments.get(i + 1);

            Date startTime = addDuration(current.getAppointmentDate(), duration);
            Date endTime = next.getAppointmentDate();

            if (isDurationAvailable(startTime, endTime, duration)) {
                availableAppointments.add(createAvailableSlot(startTime));
            }
        }

        // Vérifier les créneaux après le dernier rendez-vous
        if (!roomAndDoctorAppointments.isEmpty()) {
            Appointment lastAppointment = roomAndDoctorAppointments.get(roomAndDoctorAppointments.size() - 1);
            Date startTime = addDuration(lastAppointment.getAppointmentDate(), duration);

            if (startTime.after(now)) {
                availableAppointments.add(createAvailableSlot(startTime));
            }
        }

        if (availableAppointments.isEmpty()) {
            throw new IllegalArgumentException("No available appointments for the given duration");
        }

        return availableAppointments;
    }

    private boolean isDurationAvailable(Date startTime, Date endTime, Integer duration) {
        long availableTime = endTime.getTime() - startTime.getTime();
        return availableTime >= duration * 60 * 1000; // Convertir la durée en millisecondes
    }

    private Appointment createAvailableSlot(Date startTime) {
        Appointment availableSlot = new Appointment();
        availableSlot.setAppointmentDate(startTime);
        return availableSlot;
    }

    private Date addDuration(Date date, Integer duration) {
        return new Date(date.getTime() + duration * 60 * 1000); // Ajouter la durée en millisecondes
    }
}
