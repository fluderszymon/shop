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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import({ProductServiceImpl.class, ProductMapperImpl.class})
public class ProductServiceImplTests {

    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private ProductMapperImpl productMapper;

    private Product addProductToDatabase() {
        ProductCreateDTO productCreateDTO = new ProductCreateDTO(
                "First Product", "First Description", 15.50, 100);
        return productService.addProduct(productCreateDTO);
    }

    @Test
    void getAllProducts_shouldReturnEmptyList() {
        List<ProductDTO> products = productService.getAllProducts();
        assertThat(products.isEmpty()).isTrue();
    }

    @Test
    void getAllProducts_shouldReturnAllProductDTOs() {
        Product addedProduct = addProductToDatabase();
        List<ProductDTO> productDTOList = productService.getAllProducts();
        ProductDTO expectedProductDTO = productMapper.productToProductDTO(addedProduct);

        assertThat(productDTOList.size()).isEqualTo(1);
        assertThat(productDTOList.contains(expectedProductDTO)).isTrue();
    }

    @Test
    void getProductsByIdList_shouldReturnEmptyListWhenThereIsNoProductWithIdInGivenList() {
        addProductToDatabase();
        List<Integer> notExistingIdList = List.of(55);
        List<ProductDTO> productDTOList = productService.getProductsByIdList(notExistingIdList);

        assertThat(productDTOList.isEmpty()).isTrue();
    }

    @Test
    void getProductsByIdList_shouldReturnProductDTOsHavingIdsInGivenList() {
        Product addedProduct = addProductToDatabase();
        List<Integer> existingIdList = List.of(addedProduct.getProductId());

        ProductDTO expectedProductDTO = productMapper.productToProductDTO(addedProduct);
        List<ProductDTO> productDTOList = productService.getProductsByIdList(existingIdList);

        assertThat(productDTOList.size()).isEqualTo(1);
        assertThat(productDTOList.contains(expectedProductDTO)).isTrue();
    }

    @Test
    void getProductsByIdList_shouldReturnEmptyListWhenIdListIsEmpty() {
        addProductToDatabase();
        List<ProductDTO> products = productService.getProductsByIdList(List.of());
        assertThat(products.isEmpty()).isTrue();
    }

    @Test
    void getProductById_shouldReturnProductDTO() {
        Product addedProduct = addProductToDatabase();
        ProductDTO productDTO = productService.getProductById(addedProduct.getProductId());
        ProductDTO expectedProductDTO = productMapper.productToProductDTO(addedProduct);

        assertEquals(expectedProductDTO, productDTO);
    }

    @Test
    void getProductById_shouldThrowExceptionWhenProductWithGivenIdIsNotPresent() {
        int productId = 1;

        RuntimeException exception = assertThrows(
                RuntimeException.class, () -> productService.getProductById(productId));

        assertEquals("Product with id: " + productId + " does not exist", exception.getMessage());
    }

    @Test
    void addProduct_shouldReturnAddedProduct() {
        Product addedProduct = addProductToDatabase();
        ProductDTO result = productService.getProductById(addedProduct.getProductId());

        assertEquals(addedProduct, productMapper.productDTOToProduct(result));
    }

    @Test
    void deleteProductById_shouldDeleteProduct() {
        Product addedProduct = addProductToDatabase();
        int productId = addedProduct.getProductId();
        assertThat(productService.getProductById(productId)).isNotNull();

        productService.deleteProductById(productId);

        RuntimeException exception = assertThrows(
                RuntimeException.class, () -> productService.getProductById(productId));
        assertEquals("Product with id: " + productId + " does not exist", exception.getMessage());
    }

    @Test
    void updateProduct_shouldReturnUpdatedProduct() {
        Product addedProduct = addProductToDatabase();
        int productId = addedProduct.getProductId();

        Product productPassedToUpdateMethod 
            = new Product(productId, "Updated Product", "Updated Description", 100.50, 500);
        ProductDTO expectedProductDTO = productMapper.productToProductDTO(productPassedToUpdateMethod);

        Product updatedProduct = productService.updateProduct(productPassedToUpdateMethod);

        assertThat(expectedProductDTO).isEqualTo(productMapper.productToProductDTO(updatedProduct));
    }

    @Test
    void updateProduct_shouldThrowWhenThereIsNoProductToUpdateWithGivenId() {
        Product addedProduct = addProductToDatabase();
        int productId = addedProduct.getProductId();
        Product productWithNonExistingProductId = new Product(productId + 1, "Non Existing Product", "Non Existing Product Description", 14.22, 200);

        RuntimeException exception = assertThrows(
                RuntimeException.class, () -> productService.updateProduct(productWithNonExistingProductId));

        assertEquals("Product with id: " + (productId + 1) + " does not exist", exception.getMessage());
    }

    @Test
    void isEnough_shouldReturnTrueWhenStockIsEnough() {
        Product addedProduct = addProductToDatabase();
        int productId = addedProduct.getProductId();

        assertThat(productService.isEnough(productId, addedProduct.getStock())).isTrue();
    }

    @Test
    void isEnough_shouldReturnFalseWhenStockIsNotEnough() {
        Product addedProduct = addProductToDatabase();
        int productId = addedProduct.getProductId();

        assertThat(productService.isEnough(productId, addedProduct.getStock() + 1)).isFalse();
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

        assertThat(updatedProductDTO.getStock()).isEqualTo(addedProductDTO.getStock() - cartItemDTO.getQuantity());
    }
}