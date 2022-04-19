package com.microservices.revision.productservice.controller;

import com.microservices.revision.productservice.model.Product;
import com.microservices.revision.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductRepository productRepository;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Product> findAll() {
        log.info("Getting a list of all the products");
        return productRepository.findAll();
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteAll() {
        log.info("Deleting all products");
        productRepository.deleteAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@RequestBody Product product) {
        log.info("Creating product : {}", product);
        productRepository.save(product);
        log.info("Product {} created", product);
    }

    @GetMapping("/{productName}")
    @ResponseStatus(HttpStatus.OK)
    public String getProduct(@PathVariable("productName") String productName) {
        log.info("Getting product {} ", productName);
        return "Product name = "+ productName;
    }
}
