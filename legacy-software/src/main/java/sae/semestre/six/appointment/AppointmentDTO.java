package sae.semestre.six.appointment;

import sae.semestre.six.appointment.entity.AppointmentStatus;
import sae.semestre.six.doctor.entity.Doctor;
import sae.semestre.six.patient.Patient;
import sae.semestre.six.patient.PatientHistory;
import sae.semestre.six.room.entity.Room;

import java.util.Date;

public class AppointmentDTO {

    private Long id;

    private String appointmentNumber;

    private Patient patient;

    private Doctor doctor;

    private Room room;

    private PatientHistory patientHistory;

    private Date appointmentDate;

    private AppointmentStatus status;

    private String description;

    private String roomNumber;

}
