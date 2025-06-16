package com.szymonfluder.shop.service.impl;

import com.szymonfluder.shop.dto.ProductCreateDTO;
import com.szymonfluder.shop.dto.ProductDTO;
import com.szymonfluder.shop.entity.Product;
import com.szymonfluder.shop.mapper.ProductMapper;
import com.szymonfluder.shop.repository.ProductRepository;
import com.szymonfluder.shop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::productToProductDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO getProductById(int productId) {
        Product foundProduct = productRepository.findById(productId).orElse(new Product());
        return productMapper.productToProductDTO(foundProduct);
    }

    @Override
    public Product addProduct(ProductCreateDTO productCreateDTO) {
        Product product = productMapper.productCreateDTOToProduct(productCreateDTO);
        return productRepository.save(product);
    }

    @Override
    public void deleteProductById(int productId) {
        productRepository.deleteById(productId);
    }

    @Override
    public Product updateProduct(Product product) {
        Optional<Product> tempProduct = productRepository.findById(product.getProductId());
        Product updatedProduct = new Product();
        if (tempProduct.isPresent()) {
            updatedProduct.setProductId(product.getProductId());
            updatedProduct.setName(product.getName());
            updatedProduct.setDescription(product.getDescription());
            updatedProduct.setPrice(product.getPrice());
            updatedProduct.setStock(product.getStock());
        }
        return productRepository.save(updatedProduct);
    }

    public boolean isEnough(int productId, int orderedQuantity) {
        ProductDTO productDTO = getProductById(productId);
        return productDTO.getQuantity() >= orderedQuantity;
    }
}
