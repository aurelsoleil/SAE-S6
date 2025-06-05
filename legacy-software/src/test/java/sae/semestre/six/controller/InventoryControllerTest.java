package sae.semestre.six.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import sae.semestre.six.inventory.controller.InventoryController;
import sae.semestre.six.inventory.dao.InventoryDao;
import sae.semestre.six.inventory.dao.StockMovementDao;
import sae.semestre.six.inventory.entity.Inventory;
import sae.semestre.six.inventory.entity.StockMovement;
import sae.semestre.six.supplier.SupplierInvoice;
import sae.semestre.six.supplier.SupplierInvoiceDetail;
import sae.semestre.six.utils.email.SMTPHelper;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InventoryControllerTest {

    @Mock
    private InventoryDao inventoryDao;

    @Mock
    private StockMovementDao stockMovementDao;

    @Mock
    private SMTPHelper emailService;

    @InjectMocks
    private InventoryController inventoryController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testProcessSupplierInvoice_createsStockMovement() {
        Inventory inventory = new Inventory();
        inventory.setReorderLevel(5);
        inventory.setQuantity(10);
        inventory.setName("Test Product");
        inventory.setItemCode("TEST001");

        SupplierInvoiceDetail detail = new SupplierInvoiceDetail();
        detail.setInventory(inventory);
        detail.setQuantity(5);
        detail.setUnitPrice(2.0);

        SupplierInvoice invoice = new SupplierInvoice();
        invoice.setDetails(Collections.singleton(detail));

        String result = inventoryController.processSupplierInvoice(invoice);

        assertEquals("Supplier invoice processed successfully", result);

        ArgumentCaptor<StockMovement> captor = ArgumentCaptor.forClass(StockMovement.class);
        verify(stockMovementDao, times(1)).save(captor.capture());
        StockMovement movement = captor.getValue();
        assertEquals("IN", movement.getMovementType());
        assertEquals(5, movement.getQuantity());
        assertEquals(inventory, movement.getInventory());
        assertEquals("Réception fournisseur", movement.getComment());
        assertNotNull(movement.getMovementDate());
    }

    @Test
    public void testDecrementStock_createsStockMovementOut() {
        Inventory inventory = new Inventory();
        inventory.setId(1L);
        inventory.setReorderLevel(3);
        inventory.setQuantity(10);
        inventory.setName("Test Product");

        when(inventoryDao.findById(1L)).thenReturn(inventory);

        // Simuler une sortie de stock
        int qte = 3;
        inventory.setQuantity(inventory.getQuantity() - qte);
        inventoryDao.update(inventory);

        StockMovement movement = new StockMovement();
        movement.setInventory(inventory);
        movement.setMovementType("OUT");
        movement.setQuantity(qte);
        movement.setMovementDate(new Date());
        movement.setComment("Sortie de stock (utilisation)");
        stockMovementDao.save(movement);

        ArgumentCaptor<StockMovement> captor = ArgumentCaptor.forClass(StockMovement.class);
        verify(stockMovementDao, times(1)).save(captor.capture());
        StockMovement savedMovement = captor.getValue();
        assertEquals("OUT", savedMovement.getMovementType());
        assertEquals(qte, savedMovement.getQuantity());
        assertEquals(inventory, savedMovement.getInventory());
    }
}