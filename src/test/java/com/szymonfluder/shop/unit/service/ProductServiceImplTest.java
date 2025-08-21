package com.szymonfluder.shop.unit.service;

import com.szymonfluder.shop.dto.CartItemDTO;
import com.szymonfluder.shop.dto.ProductCreateDTO;
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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    private ProductCreateDTO productCreateDTO;

    @BeforeEach
    void setUp() {
        product = new Product(PRODUCT_ID, PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, PRODUCT_STOCK);
        productDTO = new ProductDTO(PRODUCT_ID, PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, PRODUCT_STOCK);
        productCreateDTO = new ProductCreateDTO(PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, PRODUCT_STOCK);
    }

    @Test
    void getAllProducts_shouldReturnAllProductDTOs() {
        List<Product> products = List.of(product);
        List<ProductDTO> expectedProductDTOs = List.of(productDTO);

        when(productRepository.findAll()).thenReturn(products);
        when(productMapper.productToProductDTO(product)).thenReturn(productDTO);

        List<ProductDTO> result = productService.getAllProducts();

        assertEquals(expectedProductDTOs, result);
        verify(productRepository).findAll();
        verify(productMapper).productToProductDTO(product);
    }

    @Test
    void getAllProducts_shouldReturnEmptyListWhenNoProductsExist() {
        when(productRepository.findAll()).thenReturn(List.of());

        List<ProductDTO> result = productService.getAllProducts();

        assertTrue(result.isEmpty());
        verify(productRepository).findAll();
        verify(productMapper, never()).productToProductDTO(any());
    }

    @Test
    void getProductsByIdList_shouldReturnProductDTOsByIdList() {
        List<Integer> idList = List.of(PRODUCT_ID);
        List<Product> products = List.of(product);
        List<ProductDTO> expectedProductDTOs = List.of(productDTO);

        when(productRepository.findAllById(idList)).thenReturn(products);
        when(productMapper.productToProductDTO(product)).thenReturn(productDTO);

        List<ProductDTO> result = productService.getProductsByIdList(idList);

        assertEquals(expectedProductDTOs, result);
        verify(productRepository).findAllById(idList);
        verify(productMapper).productToProductDTO(product);
    }

    @Test
    void getProductsByIdList_shouldReturnEmptyListWhenNoProductsFoundByIdList() {
        List<Integer> idList = List.of(PRODUCT_ID);

        when(productRepository.findAllById(idList)).thenReturn(List.of());

        List<ProductDTO> result = productService.getProductsByIdList(idList);

        assertTrue(result.isEmpty());
        verify(productRepository).findAllById(idList);
        verify(productMapper, never()).productToProductDTO(any());
    }

    @Test
    void getProductById_shouldReturnProductDTO() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(productMapper.productToProductDTO(product)).thenReturn(productDTO);

        ProductDTO result = productService.getProductById(PRODUCT_ID);

        assertEquals(productDTO, result);
        verify(productRepository).findById(PRODUCT_ID);
        verify(productMapper).productToProductDTO(product);
    }

    @Test
    void getProductById_shouldThrowExceptionWhenProductNotFound() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> productService.getProductById(PRODUCT_ID));
        
        assertEquals("Product with id: " + PRODUCT_ID + " does not exist", exception.getMessage());
        verify(productRepository).findById(PRODUCT_ID);
    }

    @Test
    void addProduct_shouldAddProduct() {
        Product productToSave = new Product(PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, PRODUCT_STOCK);
        Product savedProduct = new Product(PRODUCT_ID, PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, PRODUCT_STOCK);

        when(productMapper.productCreateDTOToProduct(productCreateDTO)).thenReturn(productToSave);
        when(productRepository.save(productToSave)).thenReturn(savedProduct);

        Product result = productService.addProduct(productCreateDTO);

        assertNotNull(result);
        assertEquals(PRODUCT_ID, result.getProductId());
        assertEquals(PRODUCT_NAME, result.getName());
        assertEquals(PRODUCT_DESCRIPTION, result.getDescription());
        assertEquals(PRODUCT_PRICE, result.getPrice());
        assertEquals(PRODUCT_STOCK, result.getStock());
        verify(productMapper).productCreateDTOToProduct(productCreateDTO);
        verify(productRepository).save(productToSave);
    }

    @Test
    void deleteProductById_shouldDeleteProduct() {
        doNothing().when(productRepository).deleteById(PRODUCT_ID);

        productService.deleteProductById(PRODUCT_ID);

        verify(productRepository).deleteById(PRODUCT_ID);
    }

    @Test
    void updateProduct_shouldUpdateProduct() {
        Product updatedProduct = new Product(PRODUCT_ID, "Updated Product", "Updated Description", 30.0, 15);
        Product existingProduct = new Product(PRODUCT_ID, PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, PRODUCT_STOCK);

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        Product result = productService.updateProduct(updatedProduct);

        assertNotNull(result);
        assertEquals(PRODUCT_ID, result.getProductId());
        assertEquals("Updated Product", result.getName());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(30.0, result.getPrice());
        assertEquals(15, result.getStock());
        verify(productRepository).findById(PRODUCT_ID);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updateProduct_shouldThrowExceptionWhenUpdatingNonExistentProduct() {
        Product updatedProduct = new Product(PRODUCT_ID, "Updated Product", "Updated Description", 30.0, 15);

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> productService.updateProduct(updatedProduct));
        
        assertEquals("Product with id: " + PRODUCT_ID + " does not exist", exception.getMessage());
        verify(productRepository).findById(PRODUCT_ID);
        verify(productRepository, never()).save(any());
    }

    @Test
    void isEnough_shouldReturnTrueWhenStockIsSufficient() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(productMapper.productToProductDTO(product)).thenReturn(productDTO);

        boolean result = productService.isEnough(PRODUCT_ID, ORDERED_QUANTITY);

        assertTrue(result);
        verify(productRepository).findById(PRODUCT_ID);
        verify(productMapper).productToProductDTO(product);
    }

    @Test
    void isEnough_shouldReturnFalseWhenStockIsInsufficient() {
        int largeQuantity = PRODUCT_STOCK + 1;
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(productMapper.productToProductDTO(product)).thenReturn(productDTO);

        boolean result = productService.isEnough(PRODUCT_ID, largeQuantity);

        assertFalse(result);
        verify(productRepository).findById(PRODUCT_ID);
        verify(productMapper).productToProductDTO(product);
    }

    @Test
    void isEnough_shouldReturnTrueWhenStockEqualsOrderedQuantity() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(productMapper.productToProductDTO(product)).thenReturn(productDTO);

        boolean result = productService.isEnough(PRODUCT_ID, PRODUCT_STOCK);

        assertTrue(result);
        verify(productRepository).findById(PRODUCT_ID);
        verify(productMapper).productToProductDTO(product);
    }

    @Test
    void updateProductsStock_shouldUpdateProductsStock() {
        CartItemDTO cartItem = new CartItemDTO(1, 1, PRODUCT_ID, ORDERED_QUANTITY);
        ProductDTO productWithUpdatedStock = new ProductDTO(PRODUCT_ID, PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, PRODUCT_STOCK - ORDERED_QUANTITY);
        Map<ProductDTO, CartItemDTO> productCartItemMap = Map.of(productWithUpdatedStock, cartItem);

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        productService.updateProductsStock(productCartItemMap);

        verify(productRepository, times(2)).findById(PRODUCT_ID);
        verify(productRepository, times(1)).save(any(Product.class));
    }
}