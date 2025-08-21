package com.szymonfluder.shop.unit.mapper;

import com.szymonfluder.shop.dto.ProductCreateDTO;
import com.szymonfluder.shop.dto.ProductDTO;
import com.szymonfluder.shop.entity.Product;
import com.szymonfluder.shop.mapper.ProductMapper;
import com.szymonfluder.shop.mapper.ProductMapperImpl;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ProductMapperTests {

    private final String PRODUCT_NAME = "Test Product";
    private final String PRODUCT_DESCRIPTION = "Test Description";
    private final double PRODUCT_PRICE = 10.00;
    private final int PRODUCT_STOCK = 100;
    
    private final ProductMapper productMapper = new ProductMapperImpl();

    @Test
    void productToProductDTO_shouldMapProductToProductDTO() {
        Product givenProduct = new Product(1, PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, PRODUCT_STOCK);
        ProductDTO result = new ProductDTO(1, PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, PRODUCT_STOCK);

        assertThat(productMapper.productToProductDTO(givenProduct)).isEqualTo(result);
    }

    @Test
    void productToProductDTO_shouldReturnNullIfProductIsNull() {
        ProductDTO productDTO = productMapper.productToProductDTO(null);

        assertThat(productDTO).isNull();
    }

    @Test
    void productDTOToProduct_shouldMapProductDTOToProduct() {
        ProductDTO givenProductDTO = new ProductDTO(1, PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, PRODUCT_STOCK);
        Product result = new Product(1, PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, PRODUCT_STOCK);

        assertThat(productMapper.productDTOToProduct(givenProductDTO)).isEqualTo(result);
    }

    @Test
    void productDTOToProduct_shouldReturnNullIfProductDTOIsNull() {
        Product product = productMapper.productDTOToProduct(null);

        assertThat(product).isNull();
    }

    @Test
    void productCreateDTOToProduct_shouldMapProductCreateDTOToProduct() {
        ProductCreateDTO productCreateDTO = new ProductCreateDTO(PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, PRODUCT_STOCK);
        Product result = new Product(0, PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, PRODUCT_STOCK);

        assertThat(productMapper.productCreateDTOToProduct(productCreateDTO)).isEqualTo(result);
    }

    @Test
    void productCreateDTOToProduct_shouldReturnNullIfProductCreateDTOIsNull() {
        Product product = productMapper.productCreateDTOToProduct(null);

        assertThat(product).isNull();
    }
}