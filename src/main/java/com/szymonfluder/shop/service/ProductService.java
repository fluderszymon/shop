package com.szymonfluder.shop.service;

import com.szymonfluder.shop.dto.ProductDTO;
import com.szymonfluder.shop.entity.Product;

import java.util.List;

public interface ProductService {

    List<ProductDTO> getAllProducts();
    ProductDTO getProductById(int productId);
    Product addProduct(ProductDTO productDTO);
    void deleteProductById(int productId);
    Product updateProduct(Product product);

}
