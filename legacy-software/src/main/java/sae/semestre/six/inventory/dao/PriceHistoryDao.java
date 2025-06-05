package sae.semestre.six.inventory.dao;

import sae.semestre.six.inventory.entity.PriceHistory;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class PriceHistoryDao {

    private final List<PriceHistory> priceHistories = new ArrayList<>();

    public void save(PriceHistory priceHistory) {
        priceHistories.add(priceHistory);
    }

    public List<PriceHistory> findByInventoryId(Long inventoryId) {
        return priceHistories.stream()
            .filter(ph -> ph.getInventoryId().equals(inventoryId))
            .collect(Collectors.toList());
    }

    public List<PriceHistory> findByInventoryIdAndDateRange(Long inventoryId, Date startDate, Date endDate) {
        return priceHistories.stream()
            .filter(ph -> ph.getInventoryId().equals(inventoryId))
            .filter(ph -> {
                Date date = ph.getChangeDate();
                boolean afterStart = (startDate == null) || !date.before(startDate);
                boolean beforeEnd = (endDate == null) || !date.after(endDate);
                return afterStart && beforeEnd;
            })
            .collect(Collectors.toList());
    }

    public void clear() {
        priceHistories.clear();
    }
}