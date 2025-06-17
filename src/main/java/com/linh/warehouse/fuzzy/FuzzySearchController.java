package com.linh.warehouse.fuzzy;

import com.linh.warehouse.entity.Product;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("")
public class FuzzySearchController {

    private final FuzzySearchService fuzzySearchService;

    public FuzzySearchController(FuzzySearchService fuzzySearchService) {
        this.fuzzySearchService = fuzzySearchService;
    }

    @GetMapping("/fuzzy-search")
    public List<Product> search(@RequestParam String query) {
        return fuzzySearchService.search(query, 3);
    }
}
