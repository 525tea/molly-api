package org.example.mollyapi.search.service;


import lombok.RequiredArgsConstructor;
import org.example.mollyapi.product.repository.ProductRepository;
import org.example.mollyapi.search.dto.SearchItemResDto;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final ProductRepository productRepository;

    public List<SearchItemResDto> searchItem(String keyword,
                                             Long cursorId,
                                             LocalDateTime lastCreatedAt,
                                             Pageable pageable){

        return productRepository.search(keyword, cursorId, lastCreatedAt, pageable);
    }
}
