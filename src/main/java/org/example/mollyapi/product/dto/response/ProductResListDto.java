package org.example.mollyapi.product.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.mollyapi.product.entity.Product;

import java.util.List;

public record ProductResListDto(@JsonProperty("data") List<ProductResDto> productDtoList) {
    static public ProductResListDto from(List<Product> products) {
        List<ProductResDto> responseData = products.stream()
                .map(ProductResDto::from)
                .toList();
        return new ProductResListDto(responseData);
    };
};
