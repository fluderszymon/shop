package com.szymonfluder.shop.unit.mapper;

import com.szymonfluder.shop.dto.ProductCreateDTO;
import com.szymonfluder.shop.dto.ProductDTO;
import com.szymonfluder.shop.entity.Product;
import com.szymonfluder.shop.mapper.ProductMapper;
import com.szymonfluder.shop.mapper.ProductMapperImpl;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ProductMapperTests {

    private final String PRODUCT_NAME = "Test Product";
    private final String PRODUCT_DESCRIPTION = "Test Description";
    private final BigDecimal PRODUCT_PRICE = BigDecimal.valueOf(10.00).setScale(2, RoundingMode.HALF_UP);
    private final int PRODUCT_STOCK = 100;
    
    private final ProductMapper productMapper = new ProductMapperImpl();

    @Test
    void productToProductDTO_shouldMapProductToProductDTO_whenValidDataProvided() {
        Product givenProduct = new Product(1, PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, PRODUCT_STOCK);
        ProductDTO expectedProductDTO = new ProductDTO(1, PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, PRODUCT_STOCK);
        ProductDTO mappedProductDTO = productMapper.productToProductDTO(givenProduct);

        assertThat(mappedProductDTO).isEqualTo(expectedProductDTO);
    }

    @Test
    void productDTOToProduct_shouldMapProductDTOToProduct_whenValidDataProvided() {
        ProductDTO givenProductDTO = new ProductDTO(1, PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, PRODUCT_STOCK);
        Product expectedProduct = new Product(1, PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, PRODUCT_STOCK);
        Product mappedProduct = productMapper.productDTOToProduct(givenProductDTO);

        assertThat(mappedProduct).isEqualTo(expectedProduct);
    }

    @Test
    void productCreateDTOToProduct_shouldMapProductCreateDTOToProduct_whenValidDataProvided() {
        ProductCreateDTO givenProductCreateDTO = new ProductCreateDTO(PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, PRODUCT_STOCK);
        Product expectedProduct = new Product(0, PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, PRODUCT_STOCK);
        Product mappedProduct = productMapper.productCreateDTOToProduct(givenProductCreateDTO);

        assertThat(mappedProduct).isEqualTo(expectedProduct);
    }
}