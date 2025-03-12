package org.example.mollyapi.product.dto.request;

import lombok.Builder;
import lombok.Getter;
import org.example.mollyapi.product.entity.Product;
import org.example.mollyapi.product.entity.ProductItem;

@Getter
public class BulkProductItemReqDto {

    private Long productId;

    private String color;

    private String colorCode;

    private Long quantity;

    private String size;

    @Builder
    public BulkProductItemReqDto(Long productId, String color, String colorCode, Long quantity, String size) {
        this.productId = productId;
        this.color = color;
        this.colorCode = colorCode;
        this.quantity = quantity;
        this.size = size;
    }

    public ProductItem toProductItem(Product product) {

        return ProductItem.builder()
                .id(this.getProductId())
                .product(product)
                .color(this.color)
                .colorCode(this.colorCode)
                .quantity(this.quantity)
                .size(this.size)
                .build();
    }


}
