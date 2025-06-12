package com.szymonfluder.shop.controller;

import com.szymonfluder.shop.dto.ProductDTO;
import com.szymonfluder.shop.entity.Product;
import com.szymonfluder.shop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<ProductDTO> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{productId}")
    public ProductDTO getProductById(@PathVariable int productId) {
        return productService.getProductById(productId);
    }

    @PostMapping
    public Product addProduct(@RequestBody ProductDTO productDTO) {
        return productService.addProduct(productDTO);
    }

    @DeleteMapping("/{productId}")
    public void deleteProductById(@PathVariable int productId) {
        productService.deleteProductById(productId);
    }

    @PutMapping
    public Product updateProduct(@RequestBody Product product) {
        return productService.updateProduct(product);
    }
}
