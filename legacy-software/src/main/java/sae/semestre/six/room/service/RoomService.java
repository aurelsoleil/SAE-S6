package sae.semestre.six.room.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sae.semestre.six.room.dao.RoomDao;
import sae.semestre.six.room.entity.Room;

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
