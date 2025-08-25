package com.szymonfluder.shop.integration.service;

import com.szymonfluder.shop.dto.CartItemDTO;
import com.szymonfluder.shop.dto.ProductCreateDTO;
import com.szymonfluder.shop.dto.ProductDTO;
import com.szymonfluder.shop.entity.Product;
import com.szymonfluder.shop.mapper.ProductMapperImpl;
import com.szymonfluder.shop.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import({ProductServiceImpl.class, ProductMapperImpl.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProductServiceImplTests {

    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private ProductMapperImpl productMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Product addProductToDatabase() {
        ProductCreateDTO productCreateDTO = new ProductCreateDTO(
                "Product", "Product Description", 15.50, 100);
        return productService.addProduct(productCreateDTO);
    }

    private Product getProduct() {
        return new Product(1, "Product", "Product Description", 15.50, 100);
    }

    private ProductDTO getProductDTO() {
        return new ProductDTO(1, "Product", "Product Description", 15.50, 100);
    }

    @Test
    void getAllProducts_shouldReturnEmptyList() {
        List<ProductDTO> actualProductDTOList = productService.getAllProducts();
        assertThat(actualProductDTOList.isEmpty()).isTrue();
    }

    @Test
    void getAllProducts_shouldReturnAllProductDTOs() {
        addProductToDatabase();
        List<ProductDTO> actualProductDTOList = productService.getAllProducts();
        ProductDTO expectedProductDTO = getProductDTO();

        assertThat(actualProductDTOList.contains(expectedProductDTO)).isTrue();
    }

    @Test
    void getProductsByIdList_shouldReturnEmptyListWhenThereIsNoProductWithIdInGivenList() {
        addProductToDatabase();
        List<Integer> notExistingIdList = List.of(55);
        List<ProductDTO> actualProductDTOList = productService.getProductsByIdList(notExistingIdList);

        assertThat(actualProductDTOList.isEmpty()).isTrue();
    }

    @Test
    void getProductsByIdList_shouldReturnProductDTOsHavingIdsInGivenList() {
        Product addedProduct = addProductToDatabase();
        List<Integer> existingIdList = List.of(addedProduct.getProductId());

        List<ProductDTO> actualProductDTOList = productService.getProductsByIdList(existingIdList);
        ProductDTO expectedProductDTO = getProductDTO();

        assertThat(actualProductDTOList.contains(expectedProductDTO)).isTrue();
    }

    @Test
    void getProductsByIdList_shouldReturnEmptyListWhenIdListIsEmpty() {
        addProductToDatabase();
        List<ProductDTO> actualProductDTOList = productService.getProductsByIdList(List.of());
        assertThat(actualProductDTOList.isEmpty()).isTrue();
    }

    @Test
    void getProductById_shouldReturnProductDTO() {
        Product addedProduct = addProductToDatabase();
        ProductDTO actualProductDTO = productService.getProductById(addedProduct.getProductId());
        ProductDTO expectedProductDTO = getProductDTO();

        assertThat(actualProductDTO).isEqualTo(expectedProductDTO);
    }

    @Test
    void getProductById_shouldThrowExceptionWhenProductNotFound() {
        int productId = 1;

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> productService.getProductById(productId));

        assertThat(exception.getMessage()).isEqualTo("Product with id: " + productId + " does not exist");
    }

    @Test
    void addProduct_shouldReturnAddedProduct() {
        Product addedProduct = addProductToDatabase();
        Product expectedProduct = getProduct();

        assertThat(addedProduct).isEqualTo(expectedProduct);
    }

    @Test
    void deleteProductById_shouldDeleteProduct() {
        Product addedProduct = addProductToDatabase();
        int productId = addedProduct.getProductId();
        assertThat(productService.getProductById(productId)).isNotNull();

        productService.deleteProductById(productId);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> productService.getProductById(productId));
        assertThat(exception.getMessage()).isEqualTo("Product with id: " + productId + " does not exist");
    }

    @Test
    void updateProduct_shouldReturnUpdatedProduct() {
        Product addedProduct = addProductToDatabase();
        int productId = addedProduct.getProductId();

        Product productPassedToUpdateMethod 
            = new Product(productId, "Updated Product", "Updated Description", 100.50, 500);

        Product updatedProduct = productService.updateProduct(productPassedToUpdateMethod);
        assertThat(updatedProduct).isEqualTo(productPassedToUpdateMethod);
    }

    @Test
    void updateProduct_shouldThrowWhenThereIsNoProductToUpdateWithGivenId() {
        Product addedProduct = addProductToDatabase();
        int productId = addedProduct.getProductId();
        Product productWithNonExistingProductId = new Product((productId + 1), "Non Existing Product", "Non Existing Product Description", 14.22, 200);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> productService.updateProduct(productWithNonExistingProductId));
        assertThat(exception.getMessage()).isEqualTo("Product with id: " + (productId + 1) + " does not exist");
    }

    @Test
    void isEnough_shouldReturnTrueWhenStockIsEnough() {
        Product addedProduct = addProductToDatabase();
        int productId = addedProduct.getProductId();

        boolean result = productService.isEnough(productId, addedProduct.getStock());
        assertThat(result).isTrue();
    }

    @Test
    void isEnough_shouldReturnFalseWhenStockIsNotEnough() {
        Product addedProduct = addProductToDatabase();
        int productId = addedProduct.getProductId();
        boolean result = productService.isEnough(productId, addedProduct.getStock() + 1);

        assertThat(result).isFalse();
    }

    @Test
    void updateProductsStock_shouldUpdateProductsStock() {
        Product addedProduct = addProductToDatabase();
        ProductDTO addedProductDTO = productMapper.productToProductDTO(addedProduct);
        CartItemDTO cartItemDTO = new CartItemDTO(1, 1, addedProduct.getProductId(), 50);

        Map<ProductDTO, CartItemDTO> productDTOCartItemDTOMap = new HashMap<>();
        productDTOCartItemDTOMap.put(addedProductDTO, cartItemDTO);
        productService.updateProductsStock(productDTOCartItemDTOMap);

        ProductDTO updatedProductDTO = productService.getProductById(addedProduct.getProductId());

        int actualStock = updatedProductDTO.getStock();
        int expectedStock = addedProductDTO.getStock() - cartItemDTO.getQuantity();
        assertThat(actualStock).isEqualTo(expectedStock);
    }
}