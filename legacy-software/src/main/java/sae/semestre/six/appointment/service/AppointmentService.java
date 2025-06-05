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

    public Appointment createAppointment(Long doctorId, Long patientId, String appointmentDate) {

        Doctor doctor = doctorService.findDoctorById(doctorId);
        Patient patient = patientService.findById(patientId);
        if (doctor == null) {
            throw new IllegalArgumentException("Doctor not found");
        }

        if (patient == null) {
            throw new IllegalArgumentException("Patient not found");
        }

        LocalDateTime appointmentDateTime = LocalDateTime.parse(appointmentDate);
        Date date = Date.from(appointmentDateTime.atZone(ZoneId.systemDefault()).toInstant());

        Appointment appointment = new Appointment(doctor, patient, date);

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

        // Get existing appointments for both doctor and room
        List<Appointment> doctorAppointments = appointmentDao.findByDoctorId(doctorId);
        List<Appointment> roomAppointments = appointmentDao.findByRoomId(roomId);

        // Get current time
        Date now = new Date();

        // Find slots where both doctor and room are available
        List<TimeSlot> doctorBusySlots = convertToTimeSlots(doctorAppointments, duration);
        List<TimeSlot> roomBusySlots = convertToTimeSlots(roomAppointments, duration);

        List<TimeSlot> availableSlots = findAvailableTimeSlots(doctorBusySlots, roomBusySlots, now, duration);

        if (availableSlots.isEmpty()) {
            throw new IllegalArgumentException("No available appointments for the given duration");
        }

        // Convert available slots to appointments
        return convertToAppointments(availableSlots);
    }

    private static class TimeSlot {
        private final Date start;
        private final Date end;

        public TimeSlot(Date start, Date end) {
            this.start = start;
            this.end = end;
        }
    }

    private List<TimeSlot> convertToTimeSlots(List<Appointment> appointments, Integer duration) {
        List<TimeSlot> slots = new ArrayList<>();
        for (Appointment appointment : appointments) {
            Date start = appointment.getAppointmentDate();
            Date end = addDuration(start, duration);
            slots.add(new TimeSlot(start, end));
        }
        return slots;
    }

    private List<TimeSlot> findAvailableTimeSlots(List<TimeSlot> doctorSlots, List<TimeSlot> roomSlots, Date now, Integer duration) {
        List<TimeSlot> availableSlots = new ArrayList<>();
        Date currentTime = now;

        // Combine and sort all busy slots
        List<TimeSlot> allBusySlots = new ArrayList<>();
        allBusySlots.addAll(doctorSlots);
        allBusySlots.addAll(roomSlots);
        allBusySlots.sort((a, b) -> a.start.compareTo(b.start));

        // Find gaps between busy slots
        for (TimeSlot busySlot : allBusySlots) {
            if (isDurationAvailable(currentTime, busySlot.start, duration)) {
                availableSlots.add(new TimeSlot(currentTime, busySlot.start));
            }
            currentTime = busySlot.end.after(currentTime) ? busySlot.end : currentTime;
        }

        // Add one more slot after the last busy slot
        if (currentTime.after(now)) {
            availableSlots.add(new TimeSlot(currentTime, addDuration(currentTime, duration)));
        }

        return availableSlots;
    }

    private List<Appointment> convertToAppointments(List<TimeSlot> slots) {
        List<Appointment> appointments = new ArrayList<>();
        for (TimeSlot slot : slots) {
            Appointment appointment = new Appointment();
            appointment.setAppointmentDate(slot.start);
            appointments.add(appointment);
        }
        return appointments;
    }

    private boolean isDurationAvailable(Date startTime, Date endTime, Integer duration) {
        long availableTime = endTime.getTime() - startTime.getTime();
        return availableTime >= duration * 60 * 1000;
    }

    private Date addDuration(Date date, Integer duration) {
        return new Date(date.getTime() + duration * 60 * 1000);
    }
}
