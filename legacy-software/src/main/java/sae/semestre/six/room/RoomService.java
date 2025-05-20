package sae.semestre.six.room;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService implements IRoomService {

    @Autowired
    private RoomDao roomDao;

    public Room findById(Long id) {
        return roomDao.findById(id);
    }

    public List<Room> findAllRooms() {
        return roomDao.findAll();
    }

    public List<Room> findAvailableRooms(String date) {
        return findAllRooms();
    }
}
