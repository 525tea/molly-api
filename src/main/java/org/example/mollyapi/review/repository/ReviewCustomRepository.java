package org.example.mollyapi.review.repository;

import org.example.mollyapi.review.dto.response.MyReviewInfoDto;
import org.example.mollyapi.review.dto.response.ReviewInfoDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
public interface ReviewCustomRepository {
    Slice<ReviewInfoDto> getReviewInfo(Pageable pageable, Long productId, Long userId);
    List<String> getImageList(Long reviewId);
    Slice<MyReviewInfoDto> getMyReviewInfo(Pageable pageable, Long userId);
}
