package com.szymonfluder.shop.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.szymonfluder.shop.controller.ProductController;
import com.szymonfluder.shop.dto.ProductCreateDTO;
import com.szymonfluder.shop.dto.ProductDTO;
import com.szymonfluder.shop.entity.Product;
import com.szymonfluder.shop.security.JWTService;
import com.szymonfluder.shop.security.RateLimitService;
import com.szymonfluder.shop.security.SecurityConfig;
import com.szymonfluder.shop.security.UserDetailsServiceImpl;
import com.szymonfluder.shop.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import(SecurityConfig.class)
public class ProductControllerTests extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private JWTService jwtService;

    @MockitoBean
    private RateLimitService rateLimitService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        setupJwtMocksWithTokenExtraction(jwtService, userDetailsService);
        setupRateLimitMocks(rateLimitService);
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    void getAllProducts_shouldReturnAllProducts() throws Exception {
        List<ProductDTO> products = List.of(new ProductDTO(1, "Product 1", "Description 1", 19.99, 50));
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/products")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].productId").value(1))
                .andExpect(jsonPath("$[0].name").value("Product 1"));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void getAllProducts_shouldReturnEmptyList() throws Exception {
        when(productService.getAllProducts()).thenReturn(List.of());

        mockMvc.perform(get("/products")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void getProductById_shouldReturnProduct() throws Exception {
        ProductDTO productDTO = new ProductDTO(1, "Test Product", "Test Description", 29.99, 100);
        when(productService.getProductById(1)).thenReturn(productDTO);

        mockMvc.perform(get("/products/1")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"));

        verify(productService, times(1)).getProductById(1);
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void addProduct_shouldReturnCreatedProduct() throws Exception {
        Product product = new Product(1, "Test Product", "Test Description", 29.99, 100);
        ProductCreateDTO productCreateDTO = new ProductCreateDTO("Test Product", "Test Description", 29.99, 100);
        when(productService.addProduct(any(ProductCreateDTO.class))).thenReturn(product);

        mockMvc.perform(post("/products")
                .header("Authorization", AUTH_HEADER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productCreateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"));

        verify(productService, times(1)).addProduct(any(ProductCreateDTO.class));
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void deleteProductById_shouldDeleteProduct() throws Exception {
        doNothing().when(productService).deleteProductById(1);

        mockMvc.perform(delete("/products/1")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk());

        verify(productService, times(1)).deleteProductById(1);
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void updateProduct_shouldReturnUpdatedProduct() throws Exception {
        Product updatedProduct = new Product(1, "Updated Product", "Updated Description", 39.99, 150);
        when(productService.updateProduct(any(Product.class))).thenReturn(updatedProduct);

        mockMvc.perform(put("/products")
                .header("Authorization", AUTH_HEADER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.name").value("Updated Product"));

        verify(productService, times(1)).updateProduct(any(Product.class));
    }

    @Test
    void getProductById_shouldHandleInvalidIdFormat() throws Exception {
        mockMvc.perform(get("/products/invalid")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteProductById_shouldHandleInvalidIdFormat() throws Exception {
        mockMvc.perform(delete("/products/invalid")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isBadRequest());
    }
}