package sae.semestre.six.room.controller;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sae.semestre.six.appointment.dao.IAppointmentDao;
import sae.semestre.six.appointment.entity.Appointment;
import sae.semestre.six.room.dao.IRoomDao;
import sae.semestre.six.room.entity.Room;

import java.util.*;

@RestController
@RequestMapping("/rooms")
public class RoomController {
    
    @Autowired
    private IRoomDao roomDao;
    
    @Autowired
    private IAppointmentDao appointmentDao;
    
    
    @PostMapping("/assign")
    @Transactional
    public String assignRoom(@RequestParam Long appointmentId, @RequestParam String roomNumber) {
        try {
            Room room = roomDao.findByRoomNumber(roomNumber);
            Appointment appointment = appointmentDao.findById(appointmentId);

            if (room.getType().equals("SURGERY") &&
                !appointment.getDoctor().getSpecialization().equals("SURGEON")) {
                return "Error: Only surgeons can use surgery rooms";
            }

            if (room.getCurrentPatientCount() >= room.getCapacity()) {
                return "Error: Room is at full capacity";
            }

            room.setCurrentPatientCount(room.getCurrentPatientCount() + 1);
            appointment.setRoomNumber(roomNumber);

            roomDao.update(room);
//            appointmentDao.update(appointment);

            return "Room assigned successfully";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    
    @GetMapping("/availability")
    public Map<String, Object> getRoomAvailability(@RequestParam String roomNumber) {
        Room room = roomDao.findByRoomNumber(roomNumber);
        Map<String, Object> result = new HashMap<>();
        
        result.put("roomNumber", room.getRoomNumber());
        result.put("capacity", room.getCapacity());
        result.put("currentPatients", room.getCurrentPatientCount());
        result.put("available", room.canAcceptPatient());
        
        return result;
    }
} 