package sae.semestre.six.room.dao;

import sae.semestre.six.room.entity.Room;
import sae.semestre.six.utils.dao.GenericDao;

public interface IRoomDao extends GenericDao<Room, Long> {
    Room findByRoomNumber(String roomNumber);
} 