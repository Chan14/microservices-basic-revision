package com.microservices.revision.inventory.service.controller;

import com.microservices.revision.inventory.service.entity.Inventory;
import com.microservices.revision.inventory.service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

    private final InventoryRepository inventoryRepository;

    @GetMapping("/{skuCode}")
    Boolean isInStock(@PathVariable String skuCode) {
        log.info("Checking inventory for the skuCode {} ", skuCode);
        Inventory inventory = inventoryRepository.findBySkuCode(skuCode)
                .orElseThrow(() -> {
                    log.info("skucode {} not found in inventory", skuCode);
                    throw new RuntimeException("Cannot find Product by sku Code " + skuCode);
                });
        log.info("Inventory {} for the skuCode {} ", inventory, skuCode);
        return inventory.getStock() > 0;
    }

    @GetMapping
    public List<Inventory> getStockOfAllProducts() {
        return inventoryRepository.findAll();
    }
}
