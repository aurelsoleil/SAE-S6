package sae.semestre.six.room.dao;

import sae.semestre.six.room.entity.Room;
import sae.semestre.six.utils.dao.AbstractHibernateDao;
import org.springframework.stereotype.Repository;

@Repository
public class RoomDao extends AbstractHibernateDao<Room, Long> implements IRoomDao {
    
    @Override
    public Room findByRoomNumber(String roomNumber) {
        return (Room) getEntityManager()
                .createQuery("FROM Room WHERE roomNumber = :roomNumber")
                .setParameter("roomNumber", roomNumber)
                .getSingleResult();
    }
} 