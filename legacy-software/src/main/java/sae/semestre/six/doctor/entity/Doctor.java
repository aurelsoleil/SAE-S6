package sae.semestre.six.doctor.entity;

import jakarta.persistence.*;
import sae.semestre.six.appointment.entity.Appointment;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "doctor_number", unique = true, nullable = false)
    private String doctorNumber;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "specialization")
    private String specialization;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "department")
    private String department;

    @OneToMany(mappedBy = "doctor", fetch = FetchType.LAZY)
    private Set<Appointment> appointments;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDoctorNumber() {
        return doctorNumber;
    }

    public void setDoctorNumber(String doctorNumber) {
        this.doctorNumber = doctorNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Set<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(Set<Appointment> appointments) {
        this.appointments = appointments;
    }


    public void checkAppointmentAvailability(Appointment appointment) {
        LocalDateTime appointmentDate = appointment.getAppointmentDate()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        for (Appointment existing : appointments) {
            Date existingDate = existing.getAppointmentDate();
            ZoneId zoneId = ZoneId.systemDefault();
            LocalDateTime existingDateTime = existingDate.toInstant()
                    .atZone(zoneId)
                    .toLocalDateTime();

            if (existingDateTime.isEqual(appointmentDate) ||
                (existingDateTime.isBefore(appointmentDate) && existingDateTime.plusHours(1).isAfter(appointmentDate)) ||
                (appointmentDate.isBefore(existingDateTime) && appointmentDate.plusHours(1).isAfter(existingDateTime))) {
                throw new IllegalArgumentException("Doctor is not available at this time");
            }
        }

        int hour = appointmentDate.getHour();
        if (hour < 9 || hour > 17) {
            throw new IllegalArgumentException("Appointment time is outside of working hours");
        }
    }
} 