package org.example.mollyapi.search.dto;

public record SearchItemResDto(
        Long productId,
        String url,
        String brandName,
        String productName,
        Long price
){

}
