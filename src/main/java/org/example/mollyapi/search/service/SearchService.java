package org.example.mollyapi.search.service;


import lombok.RequiredArgsConstructor;
import org.example.mollyapi.product.repository.ProductRepository;
import org.example.mollyapi.search.dto.SearchItemResDto;
import org.example.mollyapi.search.entity.Search;
import org.example.mollyapi.search.repository.SearchRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final ProductRepository productRepository;
    private final SearchRepository searchRepository;

    public List<SearchItemResDto> searchItem(String keyword,
                                             Long cursorId,
                                             LocalDateTime lastCreatedAt,
                                             Pageable pageable){

        Search search = searchRepository.findByKeyword(keyword)
                .orElse(
                        Search.builder()
                                .keyword(keyword)
                                .count(0)
                                .build()
                );

        search.increaseCount();

        searchRepository.save(search);

        return productRepository.search(keyword, cursorId, lastCreatedAt, pageable);
    }
}
