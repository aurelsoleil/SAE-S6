package sae.semestre.six.room;

import sae.semestre.six.appointment.AppointmentDTO;

import java.util.HashSet;
import java.util.Set;

public class RoomDTO {

    private Long id;

    private String roomNumber;

    private Integer floor;

    private String type;

    private Integer capacity;

    private Boolean isOccupied = false;

    private Set<AppointmentDTO> appointments = new HashSet<>();

    private Integer currentPatientCount = 0;


    public static RoomDTO from(Room room) {
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(room.getId());
        roomDTO.setRoomNumber(room.getRoomNumber());
        roomDTO.setFloor(room.getFloor());
        roomDTO.setType(room.getType());
        roomDTO.setCapacity(room.getCapacity());
        roomDTO.setOccupied(room.getIsOccupied());
        roomDTO.setCurrentPatientCount(room.getCurrentPatientCount());
        return roomDTO;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Boolean getOccupied() {
        return isOccupied;
    }

    public void setOccupied(Boolean occupied) {
        isOccupied = occupied;
    }

    public Set<AppointmentDTO> getAppointments() {
        return appointments;
    }

    public void setAppointments(Set<AppointmentDTO> appointments) {
        this.appointments = appointments;
    }

    public Integer getCurrentPatientCount() {
        return currentPatientCount;
    }

    public void setCurrentPatientCount(Integer currentPatientCount) {
        this.currentPatientCount = currentPatientCount;
    }
}
