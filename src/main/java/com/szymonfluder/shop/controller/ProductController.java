package com.szymonfluder.shop.controller;

import com.szymonfluder.shop.dto.ProductCreateDTO;
import com.szymonfluder.shop.dto.ProductDTO;
import com.szymonfluder.shop.entity.Product;
import com.szymonfluder.shop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USER')")
    public List<ProductDTO> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{productId}")
    @PreAuthorize("hasAuthority('USER')")
    public ProductDTO getProductById(@PathVariable int productId) {
        return productService.getProductById(productId);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public Product addProduct(@RequestBody ProductCreateDTO productCreateDTO) {
        return productService.addProduct(productCreateDTO);
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteProductById(@PathVariable int productId) {
        productService.deleteProductById(productId);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public Product updateProduct(@RequestBody Product product) {
        return productService.updateProduct(product);
    }
}