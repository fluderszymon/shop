package com.szymonfluder.shop.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.szymonfluder.shop.controller.ProductController;
import com.szymonfluder.shop.dto.ProductCreateDTO;
import com.szymonfluder.shop.dto.ProductDTO;
import com.szymonfluder.shop.entity.Product;
import com.szymonfluder.shop.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllProducts_shouldReturnAllProducts() throws Exception {
        List<ProductDTO> products = Arrays.asList(
            new ProductDTO(1, "Product 1", "Description 1", 19.99, 50),
            new ProductDTO(2, "Product 2", "Description 2", 39.99, 75)
        );
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].productId").value(1))
                .andExpect(jsonPath("$[0].name").value("Product 1"))
                .andExpect(jsonPath("$[0].description").value("Description 1"))
                .andExpect(jsonPath("$[0].price").value(19.99))
                .andExpect(jsonPath("$[0].stock").value(50))
                .andExpect(jsonPath("$[1].productId").value(2))
                .andExpect(jsonPath("$[1].name").value("Product 2"))
                .andExpect(jsonPath("$[1].description").value("Description 2"))
                .andExpect(jsonPath("$[1].price").value(39.99))
                .andExpect(jsonPath("$[1].stock").value(75));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void getAllProducts_shouldReturnEmptyList() throws Exception {
        when(productService.getAllProducts()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void getProductById_shouldReturnProduct() throws Exception {
        ProductDTO productDTO = new ProductDTO(1, "Test Product", "Test Description", 29.99, 100);
        when(productService.getProductById(1)).thenReturn(productDTO);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.price").value(29.99))
                .andExpect(jsonPath("$.stock").value(100));

        verify(productService, times(1)).getProductById(1);
    }

    @Test
    void addProduct_shouldReturnCreatedProduct() throws Exception {
        Product product = new Product(1, "Test Product", "Test Description", 29.99, 100);
        ProductCreateDTO productCreateDTO = new ProductCreateDTO("Test Product", "Test Description", 29.99, 100);
        when(productService.addProduct(any(ProductCreateDTO.class))).thenReturn(product);

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productCreateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.price").value(29.99))
                .andExpect(jsonPath("$.stock").value(100));

        verify(productService, times(1)).addProduct(any(ProductCreateDTO.class));
    }

    @Test
    void deleteProductById_shouldDeleteProduct() throws Exception {
        doNothing().when(productService).deleteProductById(1);

        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isOk());

        verify(productService, times(1)).deleteProductById(1);
    }

    @Test
    void updateProduct_shouldReturnUpdatedProduct() throws Exception {
        Product updatedProduct = new Product(1, "Updated Product", "Updated Description", 39.99, 150);
        when(productService.updateProduct(any(Product.class))).thenReturn(updatedProduct);

        mockMvc.perform(put("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.price").value(39.99))
                .andExpect(jsonPath("$.stock").value(150));

        verify(productService, times(1)).updateProduct(any(Product.class));
    }

    @Test
    void getProductById_shouldHandleInvalidIdFormat() throws Exception {
        mockMvc.perform(get("/products/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteProductById_shouldHandleInvalidIdFormat() throws Exception {
        mockMvc.perform(delete("/products/invalid"))
                .andExpect(status().isBadRequest());
    }
}