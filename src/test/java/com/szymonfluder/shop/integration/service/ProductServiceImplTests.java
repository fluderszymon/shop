package com.szymonfluder.shop.integration.service;

import com.szymonfluder.shop.dto.CartItemDTO;
import com.szymonfluder.shop.dto.ProductCreateDTO;
import com.szymonfluder.shop.dto.ProductDTO;
import com.szymonfluder.shop.entity.Product;
import com.szymonfluder.shop.mapper.ProductMapperImpl;
import com.szymonfluder.shop.repository.ProductRepository;
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
    private ProductRepository productRepository;

    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private ProductMapperImpl productMapperImpl;

    @Test
    void getAllProducts_shouldReturnEmptyList() {
        List<ProductDTO> products = productService.getAllProducts();
        assertThat(products.isEmpty()).isTrue();
    }

    private Product addProductToDatabase() {
        ProductCreateDTO productCreateDTO = new ProductCreateDTO(
                "First Product", "First Description", 15.50, 100);
        return productService.addProduct(productCreateDTO);
    }

    @Test
    void getAllProducts_shouldReturnAllProducts() {
        Product addedProduct = addProductToDatabase();
        List<ProductDTO> productDTOList = productService.getAllProducts();
        ProductDTO expectedProductDTO = productMapperImpl.productToProductDTO(addedProduct);

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
    void getProductsByIdList_shouldReturnProductsHavingIdsInGivenList() {
        Product addedProduct = addProductToDatabase();
        List<Integer> existingIdList = List.of(addedProduct.getProductId());

        ProductDTO expectedProductDTO = productMapperImpl.productToProductDTO(addedProduct);
        List<ProductDTO> productDTOList = productService.getProductsByIdList(existingIdList);

        assertThat(productDTOList.size()).isEqualTo(1);
        assertThat(productDTOList.contains(expectedProductDTO)).isTrue();
    }

    @Test
    void getProductsByIdList_shouldReturnEmptyListWhenIdListIsEmpty() {
        List<ProductDTO> products = productService.getProductsByIdList(List.of());
        assertThat(products.isEmpty()).isTrue();
    }

    @Test
    void getProductById_shouldReturnProductDTOFromDatabase() {
        Product addedProduct = addProductToDatabase();
        ProductDTO productDTO = productService.getProductById(addedProduct.getProductId());
        ProductDTO expectedProductDTO = productMapperImpl.productToProductDTO(addedProduct);

        assertEquals(expectedProductDTO, productDTO);
    }

    @Test
    void getProductById_shouldThrowWhenProductWithGivenIdIsNotPresent() {
        int productId = 1;

        RuntimeException exception = assertThrows(
                RuntimeException.class, () -> productService.getProductById(productId));

        assertEquals("Product with id: " + productId + " does not exist", exception.getMessage());
    }

    @Test
    void addProduct_shouldReturnAddedProduct() {
        Product addedProduct = addProductToDatabase();
        ProductDTO result = productService.getProductById(addedProduct.getProductId());

        assertEquals(addedProduct, productMapperImpl.productDTOToProduct(result));
    }

    @Test
    void deleteProductById_shouldDeleteProduct() {
        Product addedProduct = addProductToDatabase();
        int productId = addedProduct.getProductId();
        assertThat(productRepository.findById(productId)).isPresent();

        productService.deleteProductById(productId);
        assertThat(productRepository.findById(productId)).isEmpty();
    }

    @Test
    void updateProduct_shouldReturnUpdatedProduct() {
        Product addedProduct = addProductToDatabase();
        int productId = addedProduct.getProductId();

        Product productPassedToUpdateMethod 
            = new Product(productId, "Updated Product", "Updated Description", 100.50, 500);
        Product updatedProduct = productService.updateProduct(productPassedToUpdateMethod);

        ProductDTO productAfterUpdate = productService.getProductById(productId);

        assertThat(productAfterUpdate).isEqualTo(productMapperImpl.productToProductDTO(updatedProduct));
    }

    @Test
    void updateProduct_shouldThrowWhenThereIsNoProductToUpdateWithGivenId() {
        Product addedProduct = addProductToDatabase();
        int productId = addedProduct.getProductId();
        Product productWithExistingProductId = new Product(productId, "Second Product", "First Description", 14.22, 200);

        RuntimeException exception = assertThrows(
                RuntimeException.class, () -> productService.updateProduct(productWithExistingProductId));

        assertEquals("Product with id: " + productId + " does not exist", exception.getMessage());
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
        Product firstProduct = new Product("First Product", "First Description", 14.22, 200);
        Product secondProduct = new Product("Second Product", "Second Description", 44.12, 144);
        Product savedFirstProduct = productRepository.save(firstProduct);
        Product savedSecondProduct = productRepository.save(secondProduct);

        ProductDTO firstProductDTO = productService.getProductById(savedFirstProduct.getProductId());
        ProductDTO secondProductDTO = productService.getProductById(savedSecondProduct.getProductId());

        CartItemDTO firstCartItemDTO = new CartItemDTO(1, 1, savedFirstProduct.getProductId(), 50);
        CartItemDTO secondCartItemDTO = new CartItemDTO(2, 2, savedSecondProduct.getProductId(), 100);

        Map<ProductDTO, CartItemDTO> productDTOCartItemDTOMap = new HashMap<>();
        productDTOCartItemDTOMap.put(firstProductDTO, firstCartItemDTO);
        productDTOCartItemDTOMap.put(secondProductDTO, secondCartItemDTO);

       
        productService.updateProductsStock(productDTOCartItemDTOMap);

        Product updatedFirstProduct = productRepository.findById(savedFirstProduct.getProductId()).orElseThrow();
        Product updatedSecondProduct = productRepository.findById(savedSecondProduct.getProductId()).orElseThrow();

        assertThat(updatedFirstProduct.getStock()).isEqualTo(200 - 50);
        assertThat(updatedSecondProduct.getStock()).isEqualTo(144 - 100);
        
        assertThat(updatedFirstProduct.getName()).isEqualTo("First Product");
        assertThat(updatedFirstProduct.getDescription()).isEqualTo("First Description");
        assertThat(updatedFirstProduct.getPrice()).isEqualTo(14.22);
        
        assertThat(updatedSecondProduct.getName()).isEqualTo("Second Product");
        assertThat(updatedSecondProduct.getDescription()).isEqualTo("Second Description");
        assertThat(updatedSecondProduct.getPrice()).isEqualTo(44.12);
    }
}