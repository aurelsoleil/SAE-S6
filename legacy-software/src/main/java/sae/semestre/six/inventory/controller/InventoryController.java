package sae.semestre.six.inventory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sae.semestre.six.inventory.entity.Inventory;
import sae.semestre.six.inventory.entity.StockMovement;
import sae.semestre.six.inventory.dao.InventoryDao;
import sae.semestre.six.inventory.dao.StockMovementDao;
import sae.semestre.six.supplier.SupplierInvoice;
import sae.semestre.six.supplier.SupplierInvoiceDetail;
import sae.semestre.six.utils.email.SMTPHelper;
import java.util.*;
import java.util.stream.Collectors;
import java.io.FileWriter;
import java.io.IOException;
import sae.semestre.six.inventory.entity.PriceHistory;
import sae.semestre.six.inventory.dao.PriceHistoryDao;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@RestController
@RequestMapping("/inventory")
public class InventoryController {
    
    @Autowired
    private InventoryDao inventoryDao;

    @Autowired
    private StockMovementDao stockMovementDao;
    
    @Autowired
    private PriceHistoryDao priceHistoryDao;

    private final SMTPHelper emailService = SMTPHelper.getInstance();
    
    
    @PostMapping("/supplier-invoice")
    public String processSupplierInvoice(@RequestBody SupplierInvoice invoice) {
        try {
            for (SupplierInvoiceDetail detail : invoice.getDetails()) {
                Inventory inventory = detail.getInventory();

                inventory.setQuantity(inventory.getQuantity() + detail.getQuantity());
                inventory.setUnitPrice(detail.getUnitPrice());
                inventory.setLastRestocked(new Date());
                inventoryDao.update(inventory);

                // Enregistrer le mouvement d'entrée
                StockMovement movement = new StockMovement();
                movement.setInventory(inventory);
                movement.setMovementType("IN");
                movement.setQuantity(detail.getQuantity());
                movement.setMovementDate(new Date());
                movement.setComment("Réception fournisseur");
                stockMovementDao.save(movement);
            }
            return "Supplier invoice processed successfully";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    
    @GetMapping("/low-stock")
    public List<Inventory> getLowStockItems() {
        return inventoryDao.findAll().stream()
            .filter(Inventory::needsRestock)
            .collect(Collectors.toList());
    }
    
    
    @PostMapping("/reorder")
    public String reorderItems() {
        List<Inventory> lowStockItems = inventoryDao.findNeedingRestock();
        
        for (Inventory item : lowStockItems) {
            
            int reorderQuantity = item.getReorderLevel() * 2;
            
            
            try (FileWriter fw = new FileWriter("C:\\hospital\\orders.txt", true)) {
                fw.write("REORDER: " + item.getItemCode() + ", Quantity: " + reorderQuantity + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            
            emailService.sendEmail(
                "supplier@example.com",
                "Reorder Request",
                "Please restock " + item.getName() + " (Quantity: " + reorderQuantity + ")"
            );
        }
        
        return "Reorder requests sent for " + lowStockItems.size() + " items";
    }
    
    @GetMapping("/{id}/quantity")
    public Map<String, Object> getProductQuantity(@PathVariable Long id) {
        Inventory inventory = inventoryDao.findById(id);
        if (inventory == null) {
            throw new NoSuchElementException("Produit non trouvé avec l'id : " + id);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("id", inventory.getId());
        result.put("name", inventory.getName());
        result.put("quantity", inventory.getQuantity());
        return result;
    }

    @GetMapping("/{id}/price-history")
    public List<Map<String, Object>> getPriceHistory(
            @PathVariable Long id,
            @RequestParam(required = false) Date startDate,
            @RequestParam(required = false) Date endDate) {
        List<PriceHistory> history = priceHistoryDao.findByInventoryIdAndDateRange(id, startDate, endDate);
        List<Map<String, Object>> result = new ArrayList<>();
        for (PriceHistory ph : history) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("date", ph.getChangeDate());
            entry.put("oldPrice", ph.getOldPrice());
            entry.put("newPrice", ph.getNewPrice());
            entry.put("variation", ph.getPriceIncrease());
            entry.put("variationPercent", ph.getPercentageChange());
            entry.put("supplier", ph.getSupplierName());
            result.add(entry);
        }
        return result;
    }

    @GetMapping("/{id}/price-history/export")
    public void exportPriceHistory(
            @PathVariable Long id,
            @RequestParam(required = false) Date startDate,
            @RequestParam(required = false) Date endDate,
            HttpServletResponse response) throws IOException {
        List<PriceHistory> history = priceHistoryDao.findByInventoryIdAndDateRange(id, startDate, endDate);

        response.setContentType("application/json");
        response.setHeader("Content-Disposition", "attachment; filename=\"price_history.json\"");

        List<Map<String, Object>> result = new ArrayList<>();
        for (PriceHistory ph : history) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("date", ph.getChangeDate());
            entry.put("oldPrice", ph.getOldPrice());
            entry.put("newPrice", ph.getNewPrice());
            entry.put("variation", ph.getPriceIncrease());
            entry.put("variationPercent", ph.getPercentageChange());
            entry.put("supplier", ph.getSupplierName());
            result.add(entry);
        }

        PrintWriter writer = response.getWriter();
        writer.print(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(result));
        writer.flush();
    }
}