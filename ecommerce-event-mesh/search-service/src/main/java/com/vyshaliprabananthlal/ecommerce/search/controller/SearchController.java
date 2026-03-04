package com.vyshaliprabananthlal.ecommerce.search.controller;

/**
 * 3/1/26 - 15:59
 *
 * @author Vyshali Prabananth Lal
 */

import com.vyshaliprabananthlal.ecommerce.search.domain.ProductEventDto;
import com.vyshaliprabananthlal.ecommerce.search.repository.SearchRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchRepository searchRepository;

    public SearchController(SearchRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductEventDto>> searchProducts() {
        // Blazing fast read directly from our optimized Read Model!
        return ResponseEntity.ok(searchRepository.getAllProducts());
    }
}
