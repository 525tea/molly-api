package org.example.mollyapi.product.service;

import org.example.mollyapi.product.dto.BrandSummaryDto;
import org.example.mollyapi.product.dto.ProductFilterCondition;
import org.example.mollyapi.product.dto.response.ProductResDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface ProductReadService {
    Slice<ProductResDto> getAllProducts(ProductFilterCondition condition, Pageable pageable);
    Slice<BrandSummaryDto> getPopularBrand(Pageable pageable);
    Optional<ProductResDto> getProductById(Long id);
}
