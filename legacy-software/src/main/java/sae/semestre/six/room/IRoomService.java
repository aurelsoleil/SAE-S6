package sae.semestre.six.room;

import java.util.List;

public interface IRoomService {

    Room findById(Long id);

    List<Room> findAllRooms();

    List<Room> findAvailableRooms(String date);

}
