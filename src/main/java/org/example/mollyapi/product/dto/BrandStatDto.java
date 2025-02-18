package org.example.mollyapi.product.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class BrandStatDto {
    private String brandName;
    private Long viewCount;

    @QueryProjection
    public BrandStatDto(String brandName, Long viewCount) {
        this.brandName = brandName;
        this.viewCount = viewCount;
    }
}
