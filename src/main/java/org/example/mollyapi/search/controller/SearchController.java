package org.example.mollyapi.search.controller;


import lombok.RequiredArgsConstructor;
import org.example.mollyapi.search.dto.SearchItemResDto;
import org.example.mollyapi.search.service.SearchService;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<?> search(@RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) Long cursorId,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastCreatedAt,
                                    Pageable pageable) {
        List<SearchItemResDto> result = searchService.searchItem(keyword, cursorId, lastCreatedAt, pageable);

        return ResponseEntity.ok(result);
    }
}
