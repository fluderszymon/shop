package com.szymonfluder.shop.mapper;

import com.szymonfluder.shop.dto.ProductCreateDTO;
import com.szymonfluder.shop.dto.ProductDTO;
import com.szymonfluder.shop.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel="spring")
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    ProductDTO productToProductDTO(Product product);
    Product productDTOToProduct(ProductDTO productDTO);

    ProductCreateDTO productToProductCreateDTO(Product product);
    Product productCreateDTOToProduct(ProductCreateDTO productCreateDTO);

}
