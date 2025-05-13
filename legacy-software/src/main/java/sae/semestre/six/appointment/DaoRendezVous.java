package sae.semestre.six.appointment;

import sae.semestre.six.appointment.Appointment;
import sae.semestre.six.dao.DaoGenerique;

import java.util.Date;
import java.util.List;

public interface DaoRendezVous extends DaoGenerique<Appointment, Long> {
    List<Appointment> trouverParIdPatient(Long idPatient);
    List<Appointment> trouverParIdMedecin(Long idMedecin);
    List<Appointment> trouverParPlageDates(Date dateDebut, Date dateFin);
} 