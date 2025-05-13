package sae.semestre.six.room;

import sae.semestre.six.dao.GenericDao;

public interface RoomDao extends GenericDao<Room, Long> {
    Room findByRoomNumber(String roomNumber);
} 