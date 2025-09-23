package com.szymonfluder.shop.integration.service;

import com.szymonfluder.shop.dto.CartItemDTO;
import com.szymonfluder.shop.dto.ProductDTO;
import com.szymonfluder.shop.entity.Product;
import com.szymonfluder.shop.exception.EntityNotFoundException;
import com.szymonfluder.shop.integration.config.TestConfig;
import com.szymonfluder.shop.mapper.ProductMapperImpl;
import com.szymonfluder.shop.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import({ProductServiceImpl.class, ProductMapperImpl.class, TestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProductServiceImplTests extends AbstractServiceTest {

    @Autowired
    private ProductMapperImpl productMapper;

    private Product getProductMock() {
        return new Product(PRODUCT_ID, PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, DEFAULT_STOCK);
    }

    private ProductDTO getProductDTOMock() {
        return new ProductDTO(PRODUCT_ID, PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, DEFAULT_STOCK);
    }

    @Test
    void getAllProducts_shouldReturnEmptyList_whenNoProductsExist() {
        List<ProductDTO> actualProductDTOList = productService.getAllProducts();
        assertThat(actualProductDTOList.isEmpty()).isTrue();
    }

    @Test
    void getAllProducts_shouldReturnAllProductDTOs_whenProductsExist() {
        addProductToDatabase();
        List<ProductDTO> actualProductDTOList = productService.getAllProducts();
        List<ProductDTO> expectedProductDTOList = List.of(getProductDTOMock());

        assertThat(actualProductDTOList).isEqualTo(expectedProductDTOList);
    }

    @Test
    void getProductsByIdList_shouldReturnEmptyList_whenNoProductsWithGivenIdsExist() {
        addProductToDatabase();
        List<Integer> notExistingIdList = List.of(NON_EXISTING_ID);
        List<ProductDTO> actualProductDTOList = productService.getProductsByIdList(notExistingIdList);

        assertThat(actualProductDTOList.isEmpty()).isTrue();
    }

    @Test
    void getProductsByIdList_shouldReturnProductDTOs_whenProductsWithGivenIdsExist() {
        addProductToDatabase();
        List<Integer> existingIdList = List.of(PRODUCT_ID);
        List<ProductDTO> actualProductDTOList = productService.getProductsByIdList(existingIdList);
        List<ProductDTO> expectedProductDTOList = List.of(getProductDTOMock());

        assertThat(actualProductDTOList).isEqualTo(expectedProductDTOList);
    }

    @Test
    void getProductById_shouldReturnProductDTO_whenProductExists() {
        addProductToDatabase();
        ProductDTO actualProductDTO = productService.getProductById(PRODUCT_ID);
        ProductDTO expectedProductDTO = getProductDTOMock();

        assertThat(actualProductDTO).isEqualTo(expectedProductDTO);
    }

    @Test
    void getProductById_shouldThrowEntityNotFoundException_whenProductNotFound() {
        assertThrows(EntityNotFoundException.class, () -> productService.getProductById(NON_EXISTING_ID));
    }

    @Test
    void addProduct_shouldReturnAddedProduct_whenProductIsValid() {
        Product addedProduct = addProductToDatabase();
        Product expectedProduct = getProductMock();

        assertThat(addedProduct).isEqualTo(expectedProduct);
    }

    @Test
    void deleteProductById_shouldDeleteProduct_whenProductExists() {
        addProductToDatabase();
        assertThat(productService.getProductById(PRODUCT_ID)).isNotNull();

        productService.deleteProductById(PRODUCT_ID);
        assertThrows(EntityNotFoundException.class, () -> productService.getProductById(PRODUCT_ID));
    }

    @Test
    void updateProduct_shouldReturnUpdatedProduct_whenProductExists() {
        addProductToDatabase();
        String UPDATED_PRODUCT_NAME = "Updated Product";
        String UPDATED_PRODUCT_DESCRIPTION = "Updated Description";
        BigDecimal UPDATED_PRODUCT_PRICE = BigDecimal.valueOf(50.00).setScale(2, RoundingMode.HALF_UP);
        int UPDATED_STOCK = 200;
        Product productPassedToUpdateMethod
            = new Product(PRODUCT_ID, UPDATED_PRODUCT_NAME, UPDATED_PRODUCT_DESCRIPTION, UPDATED_PRODUCT_PRICE, UPDATED_STOCK);

        Product updatedProduct = productService.updateProduct(productPassedToUpdateMethod);
        assertThat(updatedProduct).isEqualTo(productPassedToUpdateMethod);
    }

    @Test
    void updateProduct_shouldThrowEntityNotFoundException_whenProductNotFound() {
        addProductToDatabase();
        Product productWithNonExistingProductId = new Product(NON_EXISTING_ID, PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, DEFAULT_STOCK);

        assertThrows(EntityNotFoundException.class, () -> productService.updateProduct(productWithNonExistingProductId));
    }

    @Test
    void isEnough_shouldReturnTrue_whenStockIsSufficient() {
        Product addedProduct = addProductToDatabase();

        boolean result = productService.isEnough(PRODUCT_ID, addedProduct.getStock());
        assertThat(result).isTrue();
    }

    @Test
    void isEnough_shouldReturnFalse_whenStockIsInsufficient() {
        Product addedProduct = addProductToDatabase();

        boolean result = productService.isEnough(PRODUCT_ID, addedProduct.getStock() + 1);
        assertThat(result).isFalse();
    }

    @Test
    void updateProductsStock_shouldUpdateProductsStock_whenValidDataProvided() {
        Product addedProduct = addProductToDatabase();
        ProductDTO addedProductDTO = productMapper.productToProductDTO(addedProduct);
        CartItemDTO cartItemDTO = getCartItemDTOMock();

        Map<ProductDTO, CartItemDTO> productDTOCartItemDTOMap = new HashMap<>();
        productDTOCartItemDTOMap.put(addedProductDTO, cartItemDTO);
        productService.updateProductsStock(productDTOCartItemDTOMap);

        ProductDTO updatedProductDTO = productService.getProductById(PRODUCT_ID);

        int actualStock = updatedProductDTO.getStock();
        int expectedStock = addedProductDTO.getStock() - cartItemDTO.getQuantity();
        assertThat(actualStock).isEqualTo(expectedStock);
    }
}