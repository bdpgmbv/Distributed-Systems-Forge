package com.vyshaliprabananthlal.ecommerce.catalog.controller;

/**
 * 3/1/26 - 15:36
 *
 * @author Vyshali Prabananth Lal
 */

import com.vyshaliprabananthlal.ecommerce.catalog.domain.Product;
import com.vyshaliprabananthlal.ecommerce.catalog.repository.CatalogRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    private final CatalogRepository catalogRepository;

    public CatalogController(CatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    @PostMapping("/products")
    public ResponseEntity<String> createProduct(@RequestBody Product request) {
        // Generate a unique ID for the new product
        Product newProduct = new Product(UUID.randomUUID().toString(), request.name(), request.price());

        // Call our transactional method
        catalogRepository.saveProductAndEvent(newProduct);

        return ResponseEntity.ok("Product created and event saved to outbox!");
    }
}
