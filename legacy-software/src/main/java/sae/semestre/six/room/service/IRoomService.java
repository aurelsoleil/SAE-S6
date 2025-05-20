package sae.semestre.six.room.service;

import sae.semestre.six.room.entity.Room;

import java.util.List;

public interface IRoomService {

    Room findById(Long id);

    List<Room> findAllRooms();

    List<Room> findAvailableRooms(String date);

}
