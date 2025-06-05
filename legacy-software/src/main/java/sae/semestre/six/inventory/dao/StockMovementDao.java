package sae.semestre.six.inventory.dao;

import org.springframework.stereotype.Repository;
import sae.semestre.six.inventory.entity.StockMovement;
import sae.semestre.six.utils.dao.AbstractHibernateDao;

import java.util.List;

@Repository
public class StockMovementDao extends AbstractHibernateDao<StockMovement, Long> {

    public List<StockMovement> findByInventoryId(Long inventoryId) {
        return getEntityManager()
            .createQuery("FROM StockMovement WHERE inventory.id = :inventoryId ORDER BY movementDate DESC", StockMovement.class)
            .setParameter("inventoryId", inventoryId)
            .getResultList();
    }

    public List<StockMovement> findAllOrderByDateDesc() {
        return getEntityManager()
            .createQuery("FROM StockMovement ORDER BY movementDate DESC", StockMovement.class)
            .getResultList();
    }
}