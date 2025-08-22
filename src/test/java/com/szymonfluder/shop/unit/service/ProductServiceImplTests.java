package com.szymonfluder.shop.unit.service;

import com.szymonfluder.shop.dto.ProductDTO;
import com.szymonfluder.shop.entity.Product;
import com.szymonfluder.shop.mapper.ProductMapper;
import com.szymonfluder.shop.repository.ProductRepository;
import com.szymonfluder.shop.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    private static final int PRODUCT_ID = 1;
    private static final String PRODUCT_NAME = "Test Product";
    private static final String PRODUCT_DESCRIPTION = "Test Description";
    private static final double PRODUCT_PRICE = 25.0;
    private static final int PRODUCT_STOCK = 10;
    private static final int ORDERED_QUANTITY = 5;

    @Mock private ProductRepository productRepository;
    @Mock private ProductMapper productMapper;

    @InjectMocks private ProductServiceImpl productService;

    private Product product;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        product = new Product(PRODUCT_ID, PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, PRODUCT_STOCK);
        productDTO = new ProductDTO(PRODUCT_ID, PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, PRODUCT_STOCK);
    }

    private void mockProductServiceImplementation() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(productMapper.productToProductDTO(product)).thenReturn(productDTO);
    }

    @Test
    void isEnough_shouldReturnTrueWhenStockIsSufficient() {
        mockProductServiceImplementation();

        boolean result = productService.isEnough(PRODUCT_ID, ORDERED_QUANTITY);
        assertTrue(result);
    }

    @Test
    void isEnough_shouldReturnFalseWhenStockIsInsufficient() {
        int largeQuantity = PRODUCT_STOCK + 1;
        mockProductServiceImplementation();

        boolean result = productService.isEnough(PRODUCT_ID, largeQuantity);
        assertFalse(result);
    }

    @Test
    void isEnough_shouldReturnTrueWhenStockEqualsOrderedQuantity() {
        mockProductServiceImplementation();

        boolean result = productService.isEnough(PRODUCT_ID, PRODUCT_STOCK);
        assertTrue(result);
    }
}