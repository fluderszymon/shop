package com.szymonfluder.shop.integration.service;

import com.szymonfluder.shop.dto.CartItemDTO;
import com.szymonfluder.shop.dto.ProductDTO;
import com.szymonfluder.shop.entity.Product;
import com.szymonfluder.shop.integration.config.TestConfig;
import com.szymonfluder.shop.mapper.ProductMapperImpl;
import com.szymonfluder.shop.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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
    void getAllProducts_shouldReturnEmptyList() {
        List<ProductDTO> actualProductDTOList = productService.getAllProducts();
        assertThat(actualProductDTOList.isEmpty()).isTrue();
    }

    @Test
    void getAllProducts_shouldReturnAllProductDTOs() {
        addProductToDatabase();
        List<ProductDTO> actualProductDTOList = productService.getAllProducts();
        List<ProductDTO> expectedProductDTOList = List.of(getProductDTOMock());

        assertThat(actualProductDTOList).isEqualTo(expectedProductDTOList);
    }

    @Test
    void getProductsByIdList_shouldReturnEmptyListWhenThereIsNoProductWithIdInGivenList() {
        addProductToDatabase();
        List<Integer> notExistingIdList = List.of(NON_EXISTING_ID);
        List<ProductDTO> actualProductDTOList = productService.getProductsByIdList(notExistingIdList);

        assertThat(actualProductDTOList.isEmpty()).isTrue();
    }

    @Test
    void getProductsByIdList_shouldReturnProductDTOsHavingIdsInGivenList() {
        addProductToDatabase();
        List<Integer> existingIdList = List.of(PRODUCT_ID);
        List<ProductDTO> actualProductDTOList = productService.getProductsByIdList(existingIdList);
        List<ProductDTO> expectedProductDTOList = List.of(getProductDTOMock());

        assertThat(actualProductDTOList).isEqualTo(expectedProductDTOList);
    }

    @Test
    void getProductsByIdList_shouldReturnEmptyListWhenIdListIsEmpty() {
        addProductToDatabase();
        List<ProductDTO> actualProductDTOList = productService.getProductsByIdList(List.of());

        assertThat(actualProductDTOList.isEmpty()).isTrue();
    }

    @Test
    void getProductById_shouldReturnProductDTO() {
        addProductToDatabase();
        ProductDTO actualProductDTO = productService.getProductById(PRODUCT_ID);
        ProductDTO expectedProductDTO = getProductDTOMock();

        assertThat(actualProductDTO).isEqualTo(expectedProductDTO);
    }

    @Test
    void getProductById_shouldThrowExceptionWhenProductNotFound() {
        assertRuntimeExceptionWithMessage(() -> productService.getProductById(NON_EXISTING_ID), 
                "Product with id: " + NON_EXISTING_ID + " does not exist");
    }

    @Test
    void addProduct_shouldReturnAddedProduct() {
        Product addedProduct = addProductToDatabase();
        Product expectedProduct = getProductMock();

        assertThat(addedProduct).isEqualTo(expectedProduct);
    }

    @Test
    void deleteProductById_shouldDeleteProduct() {
        addProductToDatabase();
        assertThat(productService.getProductById(PRODUCT_ID)).isNotNull();

        productService.deleteProductById(PRODUCT_ID);
        assertRuntimeExceptionWithMessage(() -> productService.getProductById(PRODUCT_ID), 
                "Product with id: " + PRODUCT_ID + " does not exist");
    }

    @Test
    void updateProduct_shouldReturnUpdatedProduct() {
        addProductToDatabase();
        String UPDATED_PRODUCT_NAME = "Updated Product";
        String UPDATED_PRODUCT_DESCRIPTION = "Updated Description";
        double UPDATED_PRODUCT_PRICE = 50.00;
        int UPDATED_STOCK = 200;
        Product productPassedToUpdateMethod
            = new Product(PRODUCT_ID, UPDATED_PRODUCT_NAME, UPDATED_PRODUCT_DESCRIPTION, UPDATED_PRODUCT_PRICE, UPDATED_STOCK);

        Product updatedProduct = productService.updateProduct(productPassedToUpdateMethod);
        assertThat(updatedProduct).isEqualTo(productPassedToUpdateMethod);
    }

    @Test
    void updateProduct_shouldThrowExceptionWhenThereIsNoProductToUpdateWithGivenId() {
        addProductToDatabase();
        Product productWithNonExistingProductId = new Product(NON_EXISTING_ID, PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, DEFAULT_STOCK);

        assertRuntimeExceptionWithMessage(() -> productService.updateProduct(productWithNonExistingProductId), 
                "Product with id: " + NON_EXISTING_ID + " does not exist");
    }

    @Test
    void isEnough_shouldReturnTrueWhenStockIsEnough() {
        Product addedProduct = addProductToDatabase();

        boolean result = productService.isEnough(PRODUCT_ID, addedProduct.getStock());
        assertThat(result).isTrue();
    }

    @Test
    void isEnough_shouldReturnFalseWhenStockIsNotEnough() {
        Product addedProduct = addProductToDatabase();

        boolean result = productService.isEnough(PRODUCT_ID, addedProduct.getStock() + 1);
        assertThat(result).isFalse();
    }

    @Test
    void updateProductsStock_shouldUpdateProductsStock() {
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